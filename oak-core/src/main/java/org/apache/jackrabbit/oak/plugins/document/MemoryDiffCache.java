begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|cache
operator|.
name|CacheStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|StringValue
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * An in-memory diff cache implementation.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryDiffCache
implements|implements
name|DiffCache
block|{
comment|/**      * Diff cache.      *      * Key: PathRev, value: StringValue      */
specifier|protected
specifier|final
name|Cache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|diffCache
decl_stmt|;
specifier|protected
specifier|final
name|CacheStats
name|diffCacheStats
decl_stmt|;
specifier|protected
name|MemoryDiffCache
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|diffCache
operator|=
name|builder
operator|.
name|buildDiffCache
argument_list|()
expr_stmt|;
name|diffCacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|diffCache
argument_list|,
literal|"Document-Diff"
argument_list|,
name|builder
operator|.
name|getWeigher
argument_list|()
argument_list|,
name|builder
operator|.
name|getDiffCacheSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|String
name|getChanges
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
specifier|final
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
block|{
name|PathRev
name|key
init|=
name|diffCacheKey
argument_list|(
name|path
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|StringValue
name|diff
decl_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|diff
operator|=
name|diffCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|diff
operator|=
name|diffCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
operator|new
name|Callable
argument_list|<
name|StringValue
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|StringValue
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|loader
operator|.
name|call
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// try again with loader directly
name|diff
operator|=
operator|new
name|StringValue
argument_list|(
name|loader
operator|.
name|call
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|diff
operator|!=
literal|null
condition|?
name|diff
operator|.
name|toString
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Entry
name|newEntry
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|)
block|{
return|return
operator|new
name|MemoryEntry
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
specifier|public
name|CacheStats
name|getDiffCacheStats
parameter_list|()
block|{
return|return
name|diffCacheStats
return|;
block|}
specifier|protected
class|class
name|MemoryEntry
implements|implements
name|Entry
block|{
specifier|private
specifier|final
name|Revision
name|from
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|to
decl_stmt|;
specifier|protected
name|MemoryEntry
parameter_list|(
name|Revision
name|from
parameter_list|,
name|Revision
name|to
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|checkNotNull
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|checkNotNull
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|String
name|changes
parameter_list|)
block|{
name|PathRev
name|key
init|=
name|diffCacheKey
argument_list|(
name|path
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|diffCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|StringValue
argument_list|(
name|changes
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|done
parameter_list|()
block|{         }
block|}
specifier|private
specifier|static
name|PathRev
name|diffCacheKey
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|)
block|{
return|return
operator|new
name|PathRev
argument_list|(
name|from
operator|+
name|path
argument_list|,
name|to
argument_list|)
return|;
block|}
block|}
end_class

end_unit

