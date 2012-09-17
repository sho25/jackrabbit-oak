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
name|performance
operator|.
name|write
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
name|mongomk
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
name|perf
operator|.
name|BlobStoreFS
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
name|perf
operator|.
name|Config
import|;
end_import

begin_class
specifier|public
class|class
name|MultipleNodesTestBase
block|{
specifier|protected
specifier|static
name|MongoConnection
name|mongoConnection
decl_stmt|;
specifier|private
specifier|static
name|Config
name|config
decl_stmt|;
specifier|static
name|void
name|initMongo
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|config
operator|.
name|getMongoHost
argument_list|()
argument_list|,
name|config
operator|.
name|getMongoPort
argument_list|()
argument_list|,
name|config
operator|.
name|getMongoDatabase
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
name|MongoMicroKernel
name|initMicroKernel
parameter_list|()
throws|throws
name|Exception
block|{
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
name|BlobStoreFS
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoMicroKernel
argument_list|(
name|nodeStore
argument_list|,
name|blobStore
argument_list|)
return|;
block|}
specifier|static
name|void
name|readConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
name|MultipleNodesTestBase
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
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|config
operator|=
operator|new
name|Config
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

