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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|core
operator|.
name|data
operator|.
name|DataStore
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
name|core
operator|.
name|MicroKernelImpl
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
name|commons
operator|.
name|PropertiesUtil
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
name|KernelNodeStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|OAK_MEMORY_MK
init|=
literal|"Oak-MemoryMK"
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
name|OAK_MONGO_NS
init|=
literal|"Oak-MongoNS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_MONGO_MK
init|=
literal|"Oak-MongoMK"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_H2
init|=
literal|"Oak-H2"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_TAR
init|=
literal|"Oak-Tar"
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
expr_stmt|;
block|}
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
literal|false
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
literal|false
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMemoryMK
parameter_list|(
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMemory
argument_list|(
name|OAK_MEMORY_MK
argument_list|,
literal|true
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
name|boolean
name|useMk
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
if|if
condition|(
name|useMk
condition|)
block|{
name|MicroKernel
name|kernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
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
if|if
condition|(
name|useMk
condition|)
block|{
name|MicroKernel
name|kernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
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
literal|false
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
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getMongoMK
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
name|OAK_MONGO_MK
argument_list|,
literal|true
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
literal|false
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
name|boolean
name|useMk
parameter_list|,
specifier|final
name|String
name|host
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
specifier|final
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
name|String
name|dbName
init|=
name|database
operator|!=
literal|null
condition|?
name|database
else|:
name|unique
decl_stmt|;
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
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
try|try
block|{
name|String
name|className
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataStore"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
name|DataStore
name|ds
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|DataStore
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|ds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|getConfig
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
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
return|return
name|blobStore
return|;
block|}
comment|/**              * Taken from org.apache.jackrabbit.oak.plugins.document.blob.ds.DataStoreUtils              */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|e
range|:
name|Maps
operator|.
name|fromProperties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"ds."
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"bs."
argument_list|)
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|//length of bs.
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
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
name|host
argument_list|,
name|port
argument_list|,
name|dbName
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|()
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
name|Oak
name|oak
decl_stmt|;
if|if
condition|(
name|useMk
condition|)
block|{
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|dmk
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oak
operator|=
operator|new
name|Oak
argument_list|(
name|dmk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|host
argument_list|,
name|port
argument_list|,
name|dbName
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
name|getBlobStore
argument_list|()
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
name|Oak
name|oak
decl_stmt|;
if|if
condition|(
name|useMk
condition|)
block|{
name|oak
operator|=
operator|new
name|Oak
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernels
index|[
name|i
index|]
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oak
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
name|host
argument_list|,
name|port
argument_list|,
name|dbName
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
name|blobStore
operator|instanceof
name|CloudBlobStore
condition|)
block|{
operator|(
operator|(
name|CloudBlobStore
operator|)
name|blobStore
operator|)
operator|.
name|deleteBucket
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|blobStore
operator|instanceof
name|DataStoreBlobStore
condition|)
block|{
operator|(
operator|(
name|DataStoreBlobStore
operator|)
name|blobStore
operator|)
operator|.
name|clearInUse
argument_list|()
expr_stmt|;
operator|(
operator|(
name|DataStoreBlobStore
operator|)
name|blobStore
operator|)
operator|.
name|deleteAllOlderThan
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000000
argument_list|)
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
block|}
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getTar
parameter_list|(
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
parameter_list|)
block|{
return|return
operator|new
name|OakFixture
argument_list|(
name|OAK_TAR
argument_list|)
block|{
specifier|private
name|SegmentStore
index|[]
name|stores
decl_stmt|;
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
name|stores
index|[
name|i
index|]
operator|=
operator|new
name|FileStore
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|unique
argument_list|)
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
block|}
return|;
block|}
specifier|public
specifier|static
name|OakFixture
name|getH2MK
parameter_list|(
specifier|final
name|File
name|base
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
name|OAK_H2
argument_list|)
block|{
specifier|private
name|MicroKernelImpl
index|[]
name|kernels
decl_stmt|;
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
return|return
operator|new
name|Oak
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|(
name|base
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|cacheSize
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
name|kernels
operator|=
operator|new
name|MicroKernelImpl
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
name|kernels
index|[
name|i
index|]
operator|=
operator|new
name|MicroKernelImpl
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|unique
argument_list|)
operator|.
name|getPath
argument_list|()
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
name|KernelNodeStore
argument_list|(
name|kernels
index|[
name|i
index|]
argument_list|,
name|cacheSize
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
name|MicroKernelImpl
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
block|}
return|;
block|}
block|}
end_class

end_unit

