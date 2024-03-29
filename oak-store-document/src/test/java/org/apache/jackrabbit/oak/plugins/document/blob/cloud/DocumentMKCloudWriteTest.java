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
name|MongoUtils
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
name|blob
operator|.
name|DocumentMKWriteTest
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Tests for {@code DocumentMK#write(java.io.InputStream)} with  * {@link CloudBlobStore}  */
end_comment

begin_class
specifier|public
class|class
name|DocumentMKCloudWriteTest
extends|extends
name|DocumentMKWriteTest
block|{
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
name|Override
annotation|@
name|Before
specifier|public
name|void
name|setUpConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDBName
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongoConnection
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|mongoConnection
operator|.
name|getDBName
argument_list|()
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|CloudStoreUtils
operator|.
name|getBlobStore
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|public
name|void
name|tearDownConnection
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
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|connectionFactory
operator|.
name|getConnection
argument_list|()
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

