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
name|fixture
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|oak
operator|.
name|Oak
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
name|kernel
operator|.
name|NodeStoreKernel
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
name|rdb
operator|.
name|RDBBlobStore
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
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|rdb
operator|.
name|RDBDocumentStore
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
name|rdb
operator|.
name|RDBOptions
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
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
name|memory
operator|.
name|MemoryNodeStore
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|SegmentStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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

begin_class
specifier|public
specifier|abstract
class|class
name|OakFixture
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MEMORY
init|=
literal|"Oak-Memory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MEMORY_NS
init|=
literal|"Oak-MemoryNS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MONGO
init|=
literal|"Oak-Mongo"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MONGO_FDS
init|=
literal|"Oak-Mongo-FDS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MONGO_NS
init|=
literal|"Oak-MongoNS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_RDB
init|=
literal|"Oak-RDB"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_TAR
init|=
literal|"Oak-Tar"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_TAR_FDS
init|=
literal|"Oak-Tar-FDS"
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
specifier|final
name|String
name|unique
decl_stmt|;
specifier|protected
name|OakFixture
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|unique
operator|=
name|getUniqueDatabaseName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getUniqueDatabaseName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s-%d"
argument_list|,
name|name
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|MicroKernel
name|getMicroKernel
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|Oak
name|getOak
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|void
name|tearDownCluster
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMemory
parameter_list|(
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMemory
argument_list|(
name|OAK_MEMORY
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMemoryNS
parameter_list|(
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMemory
argument_list|(
name|OAK_MEMORY_NS
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMemory
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakFixture
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MicroKernel
name|getMicroKernel
parameter_list|()
block|{
return|return
operator|new
name|NodeStoreKernel
argument_list|(
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
name|getOak
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
name|oak
decl_stmt|;
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|oak
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
index|[]
name|cluster
init|=
operator|new
name|Oak
index|[
name|n
index|]
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
name|Oak
name|oak
decl_stmt|;
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
name|oak
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
comment|// nothing to do
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMongo
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|database
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMongo
argument_list|(
name|OAK_MONGO
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|database
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMongoNS
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|database
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMongo
argument_list|(
name|OAK_MONGO_NS
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|database
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMongo
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|String
name|host
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
name|String
name|database
parameter_list|,
specifier|final
name|boolean
name|dropDBAfterTest
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|,
specifier|final
name|boolean
name|useFileDataStore
parameter_list|,
specifier|final
name|File
name|base
parameter_list|,
specifier|final
name|int
name|fdsCacheInMB
parameter_list|)
block|{
if|if
condition|(
name|database
operator|==
literal|null
condition|)
block|{
name|database
operator|=
name|getUniqueDatabaseName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|String
name|uri
init|=
literal|"mongodb://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/"
operator|+
name|database
decl_stmt|;
return|return
name|getMongo
argument_list|(
name|name
argument_list|,
name|uri
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
name|useFileDataStore
argument_list|,
name|base
argument_list|,
name|fdsCacheInMB
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMongo
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|boolean
name|dropDBAfterTest
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|,
specifier|final
name|boolean
name|useFileDataStore
parameter_list|,
specifier|final
name|File
name|base
parameter_list|,
specifier|final
name|int
name|fdsCacheInMB
parameter_list|)
block|{
return|return
operator|new
name|OakFixture
argument_list|(
name|name
argument_list|)
block|{
specifier|private
name|DocumentMK
index|[]
name|kernels
decl_stmt|;
specifier|private
name|BlobStoreFixture
name|blobStoreFixture
decl_stmt|;
block|{
if|if
condition|(
name|useFileDataStore
condition|)
block|{
name|blobStoreFixture
operator|=
name|BlobStoreFixture
operator|.
name|getFileDataStore
argument_list|(
name|base
argument_list|,
name|fdsCacheInMB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blobStoreFixture
operator|=
name|BlobStoreFixture
operator|.
name|create
argument_list|(
name|base
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MicroKernel
name|getMicroKernel
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|setupBlobStore
argument_list|(
name|mkBuilder
argument_list|)
expr_stmt|;
return|return
name|mkBuilder
operator|.
name|open
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
name|getOak
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|setupBlobStore
argument_list|(
name|mkBuilder
argument_list|)
expr_stmt|;
name|DocumentMK
name|dmk
init|=
name|mkBuilder
operator|.
name|open
argument_list|()
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|(
name|dmk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
index|[]
name|cluster
init|=
operator|new
name|Oak
index|[
name|n
index|]
decl_stmt|;
name|kernels
operator|=
operator|new
name|DocumentMK
index|[
name|cluster
operator|.
name|length
index|]
expr_stmt|;
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
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|setupBlobStore
argument_list|(
name|mkBuilder
argument_list|)
expr_stmt|;
name|kernels
index|[
name|i
index|]
operator|=
name|mkBuilder
operator|.
name|open
argument_list|()
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Oak
argument_list|(
name|kernels
index|[
name|i
index|]
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
for|for
control|(
name|DocumentMK
name|kernel
range|:
name|kernels
control|)
block|{
name|kernel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dropDBAfterTest
condition|)
block|{
try|try
block|{
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|mongo
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|blobStoreFixture
operator|!=
literal|null
condition|)
block|{
name|blobStoreFixture
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
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
block|}
specifier|private
name|void
name|setupBlobStore
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
parameter_list|)
block|{
if|if
condition|(
name|blobStoreFixture
operator|!=
literal|null
condition|)
block|{
name|mkBuilder
operator|.
name|setBlobStore
argument_list|(
name|blobStoreFixture
operator|.
name|setUp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getRDB
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|jdbcuri
parameter_list|,
specifier|final
name|String
name|jdbcuser
parameter_list|,
specifier|final
name|String
name|jdbcpasswd
parameter_list|,
specifier|final
name|String
name|tablePrefix
parameter_list|,
specifier|final
name|boolean
name|dropDBAfterTest
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakFixture
argument_list|(
name|name
argument_list|)
block|{
specifier|private
name|DocumentMK
index|[]
name|kernels
decl_stmt|;
specifier|private
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
name|RDBOptions
name|getOptions
parameter_list|(
name|boolean
name|dropDBAFterTest
parameter_list|,
name|String
name|tablePrefix
parameter_list|)
block|{
return|return
operator|new
name|RDBOptions
argument_list|()
operator|.
name|dropTablesOnClose
argument_list|(
name|dropDBAfterTest
argument_list|)
operator|.
name|tablePrefix
argument_list|(
name|tablePrefix
argument_list|)
return|;
block|}
specifier|private
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
try|try
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|)
decl_stmt|;
name|blobStore
operator|=
operator|new
name|RDBBlobStore
argument_list|(
name|ds
argument_list|,
name|getOptions
argument_list|(
name|dropDBAfterTest
argument_list|,
name|tablePrefix
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
name|blobStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|MicroKernel
name|getMicroKernel
parameter_list|()
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|,
name|getOptions
argument_list|(
name|dropDBAfterTest
argument_list|,
name|tablePrefix
argument_list|)
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|mkBuilder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
return|return
name|mkBuilder
operator|.
name|open
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
name|getOak
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|,
name|getOptions
argument_list|(
name|dropDBAfterTest
argument_list|,
name|tablePrefix
argument_list|)
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|mkBuilder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|DocumentMK
name|dmk
init|=
name|mkBuilder
operator|.
name|open
argument_list|()
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|(
name|dmk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
index|[]
name|cluster
init|=
operator|new
name|Oak
index|[
name|n
index|]
decl_stmt|;
name|kernels
operator|=
operator|new
name|DocumentMK
index|[
name|cluster
operator|.
name|length
index|]
expr_stmt|;
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
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|()
decl_stmt|;
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|,
name|getOptions
argument_list|(
name|dropDBAfterTest
argument_list|,
name|tablePrefix
argument_list|)
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|i
argument_list|)
operator|.
name|setLogging
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|mkBuilder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|kernels
index|[
name|i
index|]
operator|=
name|mkBuilder
operator|.
name|open
argument_list|()
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Oak
argument_list|(
name|kernels
index|[
name|i
index|]
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
name|String
name|dropped
init|=
literal|""
decl_stmt|;
for|for
control|(
name|DocumentMK
name|kernel
range|:
name|kernels
control|)
block|{
name|kernel
operator|.
name|dispose
argument_list|()
expr_stmt|;
if|if
condition|(
name|kernel
operator|.
name|getDocumentStore
argument_list|()
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
name|dropped
operator|+=
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|kernel
operator|.
name|getDocumentStore
argument_list|()
operator|)
operator|.
name|getDroppedTables
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dropDBAfterTest
condition|)
block|{
if|if
condition|(
name|dropped
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"dropdb was set, but tables have not been dropped"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getTar
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|File
name|base
parameter_list|,
specifier|final
name|int
name|maxFileSizeMB
parameter_list|,
specifier|final
name|int
name|cacheSizeMB
parameter_list|,
specifier|final
name|boolean
name|memoryMapping
parameter_list|,
specifier|final
name|boolean
name|useBlobStore
parameter_list|)
block|{
return|return
operator|new
name|SegmentFixture
argument_list|(
name|name
argument_list|,
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
name|useBlobStore
argument_list|)
return|;
block|}
specifier|public
specifier|static
class|class
name|SegmentFixture
extends|extends
name|OakFixture
block|{
specifier|private
name|FileStore
index|[]
name|stores
decl_stmt|;
specifier|private
name|BlobStoreFixture
index|[]
name|blobStoreFixtures
init|=
operator|new
name|BlobStoreFixture
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|final
name|File
name|base
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxFileSizeMB
decl_stmt|;
specifier|private
specifier|final
name|int
name|cacheSizeMB
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|memoryMapping
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|useBlobStore
decl_stmt|;
specifier|public
name|SegmentFixture
parameter_list|(
name|String
name|name
parameter_list|,
name|File
name|base
parameter_list|,
name|int
name|maxFileSizeMB
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|,
name|boolean
name|memoryMapping
parameter_list|,
name|boolean
name|useBlobStore
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|maxFileSizeMB
operator|=
name|maxFileSizeMB
expr_stmt|;
name|this
operator|.
name|cacheSizeMB
operator|=
name|cacheSizeMB
expr_stmt|;
name|this
operator|.
name|memoryMapping
operator|=
name|memoryMapping
expr_stmt|;
name|this
operator|.
name|useBlobStore
operator|=
name|useBlobStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MicroKernel
name|getMicroKernel
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|fs
init|=
operator|new
name|FileStore
argument_list|(
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeStoreKernel
argument_list|(
operator|new
name|SegmentNodeStore
argument_list|(
name|fs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
name|getOak
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|FileStore
name|fs
init|=
operator|new
name|FileStore
argument_list|(
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|)
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|(
operator|new
name|SegmentNodeStore
argument_list|(
name|fs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
index|[]
name|cluster
init|=
operator|new
name|Oak
index|[
name|n
index|]
decl_stmt|;
name|stores
operator|=
operator|new
name|FileStore
index|[
name|cluster
operator|.
name|length
index|]
expr_stmt|;
if|if
condition|(
name|useBlobStore
condition|)
block|{
name|blobStoreFixtures
operator|=
operator|new
name|BlobStoreFixture
index|[
name|cluster
operator|.
name|length
index|]
expr_stmt|;
block|}
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
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useBlobStore
condition|)
block|{
name|blobStoreFixtures
index|[
name|i
index|]
operator|=
name|BlobStoreFixture
operator|.
name|create
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|blobStore
operator|=
name|blobStoreFixtures
index|[
name|i
index|]
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
name|stores
index|[
name|i
index|]
operator|=
operator|new
name|FileStore
argument_list|(
name|blobStore
argument_list|,
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|unique
argument_list|)
argument_list|,
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|SegmentNodeStore
argument_list|(
name|stores
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
for|for
control|(
name|SegmentStore
name|store
range|:
name|stores
control|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|BlobStoreFixture
name|blobStore
range|:
name|blobStoreFixtures
control|)
block|{
name|blobStore
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|unique
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BlobStoreFixture
index|[]
name|getBlobStoreFixtures
parameter_list|()
block|{
return|return
name|blobStoreFixtures
return|;
block|}
specifier|public
name|FileStore
index|[]
name|getStores
parameter_list|()
block|{
return|return
name|stores
return|;
block|}
block|}
block|}
end_class

end_unit

