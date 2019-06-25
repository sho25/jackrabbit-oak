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
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
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
name|fail
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
name|mongo
operator|.
name|MongoBlobStore
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
name|mongo
operator|.
name|MongoDocumentNodeStoreBuilder
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
name|MongoConnection
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
name|AbstractBlobStoreTest
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the {@link MongoBlobStore} implementation.  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|private
name|MongoConnection
name|mongoConnection
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|checkMongoDbAvailable
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|supportsStatsCollection
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
comment|// Some tests assume read from the primary
name|MongoDatabase
name|db
init|=
name|mongoConnection
operator|.
name|getDatabase
argument_list|()
operator|.
name|withReadPreference
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
decl_stmt|;
name|MongoBlobStore
name|blobStore
init|=
operator|new
name|MongoBlobStore
argument_list|(
name|db
argument_list|)
decl_stmt|;
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
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
name|mongoConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readOnly
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
name|MongoDocumentNodeStoreBuilder
name|mdnssb
init|=
name|MongoDocumentNodeStoreBuilder
operator|.
name|newMongoDocumentNodeStoreBuilder
argument_list|()
operator|.
name|setReadOnlyMode
argument_list|()
decl_stmt|;
name|MongoBlobStore
name|mbs
init|=
operator|new
name|MongoBlobStore
argument_list|(
name|mongoConnection
operator|.
name|getDatabase
argument_list|()
argument_list|,
literal|0
argument_list|,
name|mdnssb
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Read-only instantiation should fail when collection is missing, but got: "
operator|+
name|mbs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|expected
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

