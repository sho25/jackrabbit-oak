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
operator|.
name|blob
operator|.
name|cloud
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
name|blob
operator|.
name|cloud
operator|.
name|CloudBlobStore
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
name|DocumentMK
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
name|MongoBlobGCTest
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Test for MongoMK GC with {@link CloudBlobStore}  *  */
end_comment

begin_class
specifier|public
class|class
name|MongoCloudBlobGCTest
extends|extends
name|MongoBlobGCTest
block|{
specifier|private
name|BlobStore
name|blobStore
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
block|{
try|try
block|{
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|CloudStoreUtils
operator|.
name|getBlobStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUpBlobStore
parameter_list|()
throws|throws
name|Exception
block|{
name|blobStore
operator|=
name|CloudStoreUtils
operator|.
name|getBlobStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|deleteBucket
parameter_list|()
block|{
operator|(
operator|(
name|CloudBlobStore
operator|)
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|deleteBucket
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|addToBuilder
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|mk
parameter_list|)
block|{
return|return
name|super
operator|.
name|addToBuilder
argument_list|(
name|mk
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
return|;
block|}
block|}
end_class

end_unit

