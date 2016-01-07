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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|collect
operator|.
name|Maps
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
name|cache
operator|.
name|CacheValue
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
name|commons
operator|.
name|json
operator|.
name|JsopBuilder
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
name|commons
operator|.
name|json
operator|.
name|JsopReader
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
name|commons
operator|.
name|json
operator|.
name|JsopTokenizer
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

begin_comment
comment|/**  * A diff cache, which is pro-actively filled after a commit.  */
end_comment

begin_class
specifier|public
class|class
name|LocalDiffCache
extends|extends
name|DiffCache
block|{
comment|/**      * Limit is arbitrary for now i.e. 16 MB. Same as in MongoDiffCache      */
specifier|private
specifier|static
name|int
name|MAX_ENTRY_SIZE
init|=
literal|16
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|RevisionsKey
argument_list|,
name|Diff
argument_list|>
name|diffCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|diffCacheStats
decl_stmt|;
name|LocalDiffCache
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|diffCache
operator|=
name|builder
operator|.
name|buildLocalDiffCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|diffCacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|diffCache
argument_list|,
literal|"Document-LocalDiff"
argument_list|,
name|builder
operator|.
name|getWeigher
argument_list|()
argument_list|,
name|builder
operator|.
name|getLocalDiffCacheSize
argument_list|()
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
name|Nonnull
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|Nonnull
name|RevisionVector
name|to
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
block|{
name|RevisionsKey
name|key
init|=
operator|new
name|RevisionsKey
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|Diff
name|diff
init|=
name|diffCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|null
condition|)
block|{
name|String
name|result
init|=
name|diff
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|result
operator|!=
literal|null
condition|?
name|result
else|:
literal|""
return|;
block|}
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
return|return
name|loader
operator|.
name|call
argument_list|()
return|;
block|}
return|return
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
specifier|final
annotation|@
name|Nonnull
name|RevisionVector
name|from
parameter_list|,
specifier|final
annotation|@
name|Nonnull
name|RevisionVector
name|to
parameter_list|,
name|boolean
name|local
comment|/*ignored*/
parameter_list|)
block|{
return|return
operator|new
name|Entry
argument_list|()
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changesPerPath
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
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
if|if
condition|(
name|exceedsSize
argument_list|()
condition|)
block|{
return|return;
block|}
name|size
operator|+=
name|size
argument_list|(
name|path
argument_list|)
operator|+
name|size
argument_list|(
name|changes
argument_list|)
expr_stmt|;
name|changesPerPath
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|changes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|done
parameter_list|()
block|{
if|if
condition|(
name|exceedsSize
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|diffCache
operator|.
name|put
argument_list|(
operator|new
name|RevisionsKey
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|,
operator|new
name|Diff
argument_list|(
name|changesPerPath
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|exceedsSize
parameter_list|()
block|{
return|return
name|size
operator|>
name|MAX_ENTRY_SIZE
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
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
name|Collections
operator|.
name|singleton
argument_list|(
name|diffCacheStats
argument_list|)
return|;
block|}
comment|//-----------------------------< internal>---------------------------------
specifier|public
specifier|static
specifier|final
class|class
name|Diff
implements|implements
name|CacheValue
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changes
decl_stmt|;
specifier|private
name|int
name|memory
decl_stmt|;
specifier|public
name|Diff
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changes
parameter_list|,
name|int
name|memory
parameter_list|)
block|{
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
name|this
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
block|}
specifier|public
specifier|static
name|Diff
name|fromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|value
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
condition|)
block|{
break|break;
block|}
name|String
name|k
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
condition|)
block|{
break|break;
block|}
name|reader
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Diff
argument_list|(
name|map
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
name|String
name|asString
parameter_list|()
block|{
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|key
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getChanges
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|changes
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
if|if
condition|(
name|memory
operator|==
literal|0
condition|)
block|{
name|int
name|m
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|m
operator|+=
name|size
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|+
name|size
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|memory
operator|=
name|m
expr_stmt|;
block|}
return|return
name|memory
return|;
block|}
name|String
name|get
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|changes
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|asString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|Diff
condition|)
block|{
name|Diff
name|other
init|=
operator|(
name|Diff
operator|)
name|obj
decl_stmt|;
return|return
name|changes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|changes
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|int
name|size
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|StringValue
operator|.
name|getMemory
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

