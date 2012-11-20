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
name|mk
operator|.
name|test
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|blobs
operator|.
name|BlobStore
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
name|mk
operator|.
name|test
operator|.
name|MicroKernelFixture
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
name|mongomk
operator|.
name|impl
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoMicroKernel
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
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
name|mongomk
operator|.
name|impl
operator|.
name|blob
operator|.
name|MongoBlobStore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|BaseMongoMicroKernelFixture
implements|implements
name|MicroKernelFixture
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|HOST
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|PORT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"mongo.port"
argument_list|,
literal|27017
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DB
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.db"
argument_list|,
literal|"mk-tf"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|MongoConnection
name|mongoConnection
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|MongoConnection
name|getMongoConnection
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mongoConnection
operator|==
literal|null
condition|)
block|{
name|mongoConnection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|HOST
argument_list|,
name|PORT
argument_list|,
name|DB
argument_list|)
expr_stmt|;
block|}
return|return
name|mongoConnection
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
try|try
block|{
name|MongoConnection
name|connection
init|=
name|BaseMongoMicroKernelFixture
operator|.
name|getMongoConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|getDB
argument_list|()
operator|.
name|command
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"ping"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUpCluster
parameter_list|(
name|MicroKernel
index|[]
name|cluster
parameter_list|)
throws|throws
name|Exception
block|{
name|MongoConnection
name|connection
init|=
name|getMongoConnection
argument_list|()
decl_stmt|;
name|DB
name|db
init|=
name|connection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|MongoNodeStore
name|nodeStore
init|=
operator|new
name|MongoNodeStore
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|MicroKernel
name|mk
init|=
operator|new
name|MongoMicroKernel
argument_list|(
name|connection
argument_list|,
name|nodeStore
argument_list|,
name|blobStore
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cluster
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cluster
index|[
name|i
index|]
operator|=
name|mk
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|syncMicroKernelCluster
parameter_list|(
name|MicroKernel
modifier|...
name|nodes
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|(
name|MicroKernel
index|[]
name|cluster
parameter_list|)
block|{
try|try
block|{
name|DB
name|db
init|=
name|getMongoConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|BlobStore
name|getBlobStore
parameter_list|(
name|DB
name|db
parameter_list|)
function_decl|;
specifier|private
name|void
name|dropCollections
parameter_list|(
name|DB
name|db
parameter_list|)
block|{
name|db
operator|.
name|getCollection
argument_list|(
name|MongoBlobStore
operator|.
name|COLLECTION_BLOBS
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
name|db
operator|.
name|getCollection
argument_list|(
name|MongoNodeStore
operator|.
name|COLLECTION_COMMITS
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
name|db
operator|.
name|getCollection
argument_list|(
name|MongoNodeStore
operator|.
name|COLLECTION_NODES
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
name|db
operator|.
name|getCollection
argument_list|(
name|MongoNodeStore
operator|.
name|COLLECTION_SYNC
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

