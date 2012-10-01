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
name|mongomk
operator|.
name|test
operator|.
name|it
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|api
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
name|mongomk
operator|.
name|api
operator|.
name|NodeStore
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
name|BlobStoreMongo
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
name|NodeStoreMongo
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
name|util
operator|.
name|MongoUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|MongoMicroKernelFixture
implements|implements
name|MicroKernelFixture
block|{
specifier|private
specifier|static
name|MongoConnection
name|mongoConnection
init|=
name|createMongoConnection
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|MongoConnection
name|createMongoConnection
parameter_list|()
block|{
try|try
block|{
name|InputStream
name|is
init|=
name|MongoMicroKernelFixture
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config.cfg"
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|String
name|host
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"mongo.host"
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"mongo.port"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|database
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"mongo.db"
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoConnection
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|database
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
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
block|{
try|try
block|{
name|MongoUtil
operator|.
name|initDatabase
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
name|NodeStore
name|nodeStore
init|=
operator|new
name|NodeStoreMongo
argument_list|(
name|mongoConnection
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
operator|new
name|BlobStoreMongo
argument_list|(
name|mongoConnection
argument_list|)
decl_stmt|;
name|MicroKernel
name|mk
init|=
operator|new
name|MongoMicroKernel
argument_list|(
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
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
name|MongoUtil
operator|.
name|clearDatabase
argument_list|(
name|mongoConnection
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
block|}
end_class

end_unit

