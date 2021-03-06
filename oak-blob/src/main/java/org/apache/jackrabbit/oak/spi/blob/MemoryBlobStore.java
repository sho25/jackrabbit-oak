begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AbstractIterator
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
name|commons
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * A memory blob store. Useful for testing.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryBlobStore
extends|extends
name|AbstractBlobStore
block|{
specifier|private
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
name|old
init|=
operator|new
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|Long
argument_list|>
name|timestamps
init|=
operator|new
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|mark
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|id
parameter_list|)
block|{
name|byte
index|[]
name|result
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|old
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|BlockId
name|id
init|=
operator|new
name|BlockId
argument_list|(
name|digest
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|timestamps
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startMark
parameter_list|()
throws|throws
name|IOException
block|{
name|mark
operator|=
literal|true
expr_stmt|;
name|old
operator|=
name|map
expr_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|markInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isMarkEnabled
parameter_list|()
block|{
return|return
name|mark
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|mark
parameter_list|(
name|BlockId
name|id
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|data
operator|=
name|old
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
block|{
name|int
name|count
init|=
name|old
operator|.
name|size
argument_list|()
decl_stmt|;
name|old
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mark
operator|=
literal|false
expr_stmt|;
return|return
name|count
return|;
block|}
comment|/**      * Ignores the maxlastModifiedTime      */
annotation|@
name|Override
specifier|public
name|long
name|countDeleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|chunkId
range|:
name|chunkIds
control|)
block|{
name|BlockId
name|id
init|=
operator|new
name|BlockId
argument_list|(
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|chunkId
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
if|if
condition|(
name|maxLastModifiedTime
operator|==
literal|0
operator|||
operator|(
name|maxLastModifiedTime
operator|>
literal|0
operator|&&
name|maxLastModifiedTime
operator|>
name|timestamps
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|timestamps
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|old
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|old
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * Ignores the maxlastModifiedTime      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllChunkIds
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|BlockId
argument_list|,
name|byte
index|[]
argument_list|>
name|combinedMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|combinedMap
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|combinedMap
operator|.
name|putAll
argument_list|(
name|old
argument_list|)
expr_stmt|;
specifier|final
name|Iterator
argument_list|<
name|BlockId
argument_list|>
name|iter
init|=
name|combinedMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BlockId
name|blockId
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockId
operator|!=
literal|null
condition|)
block|{
return|return
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
comment|// no cache
block|}
block|}
end_class

end_unit

