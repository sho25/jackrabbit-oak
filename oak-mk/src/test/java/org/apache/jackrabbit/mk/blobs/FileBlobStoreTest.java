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

begin_comment
comment|/**  * Tests the FileBlobStore implementation.  */
end_comment

begin_class
specifier|public
class|class
name|FileBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|FileBlobStore
name|store
init|=
operator|new
name|FileBlobStore
argument_list|(
literal|"target/temp"
argument_list|)
decl_stmt|;
name|store
operator|.
name|setBlockSize
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|store
operator|.
name|setBlockSizeMin
argument_list|(
literal|32
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
block|}
end_class

end_unit

