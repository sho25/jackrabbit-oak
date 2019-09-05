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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
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
name|collect
operator|.
name|Iterables
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
name|RevisionsKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import static
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
name|Utils
operator|.
name|isLocalChange
import|;
end_import

begin_comment
comment|/**  * Implements a tiered diff cache which consists of a {@link LocalDiffCache} and  * a {@link MemoryDiffCache}.  */
end_comment

begin_class
class|class
name|TieredDiffCache
extends|extends
name|DiffCache
block|{
comment|/**      * A small cache of local diff cache misses to prevent repeated calls with      * the same revision vector range.      */
specifier|private
name|Cache
argument_list|<
name|RevisionsKey
argument_list|,
name|RevisionsKey
argument_list|>
name|localDiffMisses
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|128
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|clusterId
decl_stmt|;
specifier|private
specifier|final
name|DiffCache
name|localCache
decl_stmt|;
specifier|private
specifier|final
name|DiffCache
name|memoryCache
decl_stmt|;
name|TieredDiffCache
parameter_list|(
name|DocumentNodeStoreBuilder
argument_list|<
name|?
argument_list|>
name|builder
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|localCache
operator|=
operator|new
name|LocalDiffCache
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|memoryCache
operator|=
operator|new
name|MemoryDiffCache
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getChanges
parameter_list|(
annotation|@
name|NotNull
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|NotNull
name|RevisionVector
name|to
parameter_list|,
annotation|@
name|NotNull
name|Path
name|path
parameter_list|,
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
block|{
comment|// do not check local cache when changes are external
if|if
condition|(
name|isLocalChange
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|clusterId
argument_list|)
condition|)
block|{
comment|// do not read from the localCache when there was a previous miss
comment|// with the same key
name|RevisionsKey
name|k
init|=
operator|new
name|RevisionsKey
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|localDiffMisses
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// check local without loader and fallback to
comment|// memory cache when there is a cache miss
name|String
name|changes
init|=
name|localCache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|!=
literal|null
condition|)
block|{
return|return
name|changes
return|;
block|}
comment|// remember cache miss
name|localDiffMisses
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|memoryCache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
name|loader
argument_list|)
return|;
block|}
comment|/**      * Creates a new entry in the {@link LocalDiffCache} for local changes      * and {@link MemoryDiffCache} for external changes      *      * @param from the from revision.      * @param to the to revision.      * @return the new entry.      */
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Entry
name|newEntry
parameter_list|(
annotation|@
name|NotNull
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|NotNull
name|RevisionVector
name|to
parameter_list|,
name|boolean
name|local
parameter_list|)
block|{
if|if
condition|(
name|local
condition|)
block|{
return|return
name|localCache
operator|.
name|newEntry
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|memoryCache
operator|.
name|newEntry
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|localCache
operator|.
name|getStats
argument_list|()
argument_list|,
name|memoryCache
operator|.
name|getStats
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateAll
parameter_list|()
block|{
name|localCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|memoryCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

