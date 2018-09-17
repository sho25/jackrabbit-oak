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
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|api
operator|.
name|JackrabbitRepository
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
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_class
specifier|public
class|class
name|OakRepositoryFixture
implements|implements
name|RepositoryFixture
block|{
specifier|public
specifier|static
name|RepositoryFixture
name|getMemoryNS
parameter_list|(
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMemory
argument_list|(
name|OakFixture
operator|.
name|OAK_MEMORY_NS
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RepositoryFixture
name|getMemory
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getMemory
argument_list|(
name|name
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
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
name|OakFixture
operator|.
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
name|RepositoryFixture
name|getMongo
parameter_list|(
name|String
name|uri
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
return|return
name|getMongoNS
argument_list|(
name|uri
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getMongoWithDS
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
parameter_list|,
specifier|final
name|File
name|base
parameter_list|,
name|int
name|fdsCacheInMB
parameter_list|)
block|{
return|return
name|getMongo
argument_list|(
name|OakFixture
operator|.
name|OAK_MONGO_DS
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
literal|true
argument_list|,
name|base
argument_list|,
name|fdsCacheInMB
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getMongoWithDS
parameter_list|(
name|String
name|uri
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|,
specifier|final
name|File
name|base
parameter_list|,
name|int
name|fdsCacheInMB
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getMongo
argument_list|(
name|OakFixture
operator|.
name|OAK_MONGO_DS
argument_list|,
name|uri
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
literal|true
argument_list|,
name|base
argument_list|,
name|fdsCacheInMB
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
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
name|OakFixture
operator|.
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
name|RepositoryFixture
name|getMongoNS
parameter_list|(
name|String
name|uri
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getMongo
argument_list|(
name|uri
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RepositoryFixture
name|getMongo
parameter_list|(
name|String
name|name
parameter_list|,
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
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getMongo
argument_list|(
name|name
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
name|useFileDataStore
argument_list|,
name|base
argument_list|,
name|fdsCacheInMB
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getRDB
parameter_list|(
name|String
name|jdbcuri
parameter_list|,
name|String
name|jdbcuser
parameter_list|,
name|String
name|jdbcpasswd
parameter_list|,
name|String
name|jdbctableprefix
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|,
name|int
name|vgcMaxAge
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getRDB
argument_list|(
name|OakFixture
operator|.
name|OAK_RDB
argument_list|,
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|,
name|jdbctableprefix
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
name|vgcMaxAge
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getRDBWithDS
parameter_list|(
name|String
name|jdbcuri
parameter_list|,
name|String
name|jdbcuser
parameter_list|,
name|String
name|jdbcpasswd
parameter_list|,
name|String
name|jdbctableprefix
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|long
name|cacheSize
parameter_list|,
name|File
name|base
parameter_list|,
name|int
name|fdsCacheInMB
parameter_list|,
name|int
name|vgcMaxAge
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getRDB
argument_list|(
name|OakFixture
operator|.
name|OAK_RDB_DS
argument_list|,
name|jdbcuri
argument_list|,
name|jdbcuser
argument_list|,
name|jdbcpasswd
argument_list|,
name|jdbctableprefix
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
literal|true
argument_list|,
name|base
argument_list|,
name|fdsCacheInMB
argument_list|,
name|vgcMaxAge
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getSegmentTar
parameter_list|(
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
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getVanillaSegmentTar
argument_list|(
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getSegmentTarWithDataStore
parameter_list|(
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
name|int
name|dsCacheInMB
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getSegmentTarWithDataStore
argument_list|(
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
name|dsCacheInMB
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getSegmentTarWithColdStandby
parameter_list|(
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
parameter_list|,
name|int
name|dsCacheInMB
parameter_list|,
name|int
name|syncInterval
parameter_list|,
name|boolean
name|shareBlobStore
parameter_list|,
name|boolean
name|secure
parameter_list|,
name|boolean
name|oneShotRun
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getSegmentTarWithColdStandby
argument_list|(
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
name|useBlobStore
argument_list|,
name|dsCacheInMB
argument_list|,
name|syncInterval
argument_list|,
name|shareBlobStore
argument_list|,
name|secure
argument_list|,
name|oneShotRun
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getSegmentTarWithAzureSegmentStore
parameter_list|(
specifier|final
name|File
name|base
parameter_list|,
specifier|final
name|String
name|azureConnectionString
parameter_list|,
specifier|final
name|String
name|azureContainerName
parameter_list|,
specifier|final
name|String
name|azureRootPath
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
name|useBlobStore
parameter_list|,
specifier|final
name|int
name|dsCacheInMB
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getSegmentTarWithAzureSegmentStore
argument_list|(
name|base
argument_list|,
name|azureConnectionString
argument_list|,
name|azureContainerName
argument_list|,
name|azureRootPath
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|useBlobStore
argument_list|,
name|dsCacheInMB
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getCompositeStore
parameter_list|(
name|File
name|base
parameter_list|,
name|int
name|maxFileSizeMB
parameter_list|,
name|int
name|cacheSizeMB
parameter_list|,
specifier|final
name|boolean
name|memoryMapping
parameter_list|,
name|int
name|mounts
parameter_list|,
name|int
name|pathsPerMount
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getCompositeStore
argument_list|(
name|OakFixture
operator|.
name|OAK_COMPOSITE_STORE
argument_list|,
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getCompositeMemoryStore
parameter_list|(
name|int
name|mounts
parameter_list|,
name|int
name|pathsPerMount
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getCompositeMemoryStore
argument_list|(
name|OakFixture
operator|.
name|OAK_COMPOSITE_MEMORY_STORE
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getCompositeMongoStore
parameter_list|(
name|String
name|uri
parameter_list|,
name|long
name|cacheSize
parameter_list|,
name|boolean
name|dropDBAfterTest
parameter_list|,
name|int
name|mounts
parameter_list|,
name|int
name|pathsPerMount
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
name|OakFixture
operator|.
name|getCompositeMongoStore
argument_list|(
name|OakFixture
operator|.
name|OAK_COMPOSITE_MONGO_STORE
argument_list|,
name|uri
argument_list|,
name|cacheSize
argument_list|,
name|dropDBAfterTest
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|OakFixture
name|oakFixture
decl_stmt|;
specifier|private
name|StatisticsProvider
name|statisticsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
specifier|private
name|Repository
index|[]
name|cluster
decl_stmt|;
specifier|protected
name|OakRepositoryFixture
parameter_list|(
name|OakFixture
name|oakFixture
parameter_list|)
block|{
name|this
operator|.
name|oakFixture
operator|=
name|oakFixture
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|Repository
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|setUpCluster
argument_list|(
name|n
argument_list|,
name|JcrCreator
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
specifier|public
name|Repository
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|,
name|JcrCreator
name|customizer
parameter_list|)
throws|throws
name|Exception
block|{
name|Oak
index|[]
name|oaks
init|=
name|oakFixture
operator|.
name|setUpCluster
argument_list|(
name|n
argument_list|,
name|statisticsProvider
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|Repository
index|[
name|oaks
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
name|oaks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|configureStatsProvider
argument_list|(
name|oaks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
name|customizer
operator|.
name|customize
argument_list|(
name|oaks
index|[
name|i
index|]
argument_list|)
operator|.
name|createRepository
argument_list|()
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
name|syncRepositoryCluster
parameter_list|(
name|Repository
modifier|...
name|nodes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Repository
name|repo
range|:
name|cluster
control|)
block|{
if|if
condition|(
name|repo
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repo
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|oakFixture
operator|.
name|tearDownCluster
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|oakFixture
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|OakFixture
name|getOakFixture
parameter_list|()
block|{
return|return
name|oakFixture
return|;
block|}
specifier|public
name|void
name|setStatisticsProvider
parameter_list|(
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
block|}
specifier|private
name|void
name|configureStatsProvider
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|oak
operator|.
name|getWhiteboard
argument_list|()
operator|.
name|register
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|statisticsProvider
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

