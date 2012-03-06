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
name|mk
operator|.
name|blobs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
return|return
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
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
name|map
operator|.
name|put
argument_list|(
operator|new
name|BlockId
argument_list|(
name|digest
argument_list|,
literal|0
argument_list|)
argument_list|,
name|data
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
name|Exception
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
throws|throws
name|Exception
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
block|}
end_class

end_unit

