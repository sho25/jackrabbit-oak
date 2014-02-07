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
name|plugins
operator|.
name|document
operator|.
name|blob
package|;
end_package

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
name|rdb
operator|.
name|RDBBlobStore
import|;
end_import

begin_comment
comment|/**  * Tests the RDBBlobStore implementation.  */
end_comment

begin_class
specifier|public
class|class
name|RDBBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|private
name|RDBBlobStore
name|blobStore
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|blobStore
operator|=
operator|new
name|RDBBlobStore
argument_list|()
expr_stmt|;
name|blobStore
operator|.
name|setBlockSize
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|setBlockSizeMin
argument_list|(
literal|48
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|blobStore
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|blobStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

