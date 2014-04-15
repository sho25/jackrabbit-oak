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
name|jcr
operator|.
name|Jcr
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
name|getMemory
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
literal|false
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getMemoryMK
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
name|OAK_MEMORY_MK
argument_list|,
literal|true
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
name|boolean
name|useMK
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
name|useMK
argument_list|,
name|cacheSize
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getH2MK
parameter_list|(
name|File
name|base
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
name|getH2MK
argument_list|(
name|base
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
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getMongoWithFDS
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
parameter_list|)
block|{
return|return
name|getMongo
argument_list|(
name|OakFixture
operator|.
name|OAK_MONGO_FDS
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
argument_list|,
literal|true
argument_list|,
name|base
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
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
name|OakFixture
operator|.
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
argument_list|,
literal|false
argument_list|,
literal|null
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
argument_list|,
literal|false
argument_list|,
literal|null
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
name|boolean
name|useMK
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
name|useMK
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
name|name
parameter_list|,
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
name|name
argument_list|,
name|uri
argument_list|,
literal|false
argument_list|,
name|dropDBAfterTest
argument_list|,
name|cacheSize
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getTar
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
name|getTar
argument_list|(
name|OakFixture
operator|.
name|OAK_TAR
argument_list|,
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getTarWithBlobStore
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
name|getTar
argument_list|(
name|OakFixture
operator|.
name|OAK_TAR_FDS
argument_list|,
name|base
argument_list|,
name|maxFileSizeMB
argument_list|,
name|cacheSizeMB
argument_list|,
name|memoryMapping
argument_list|,
literal|true
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
name|JcrCustomizer
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
name|JcrCustomizer
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
name|cluster
index|[
name|i
index|]
operator|=
name|customizer
operator|.
name|customize
argument_list|(
operator|new
name|Jcr
argument_list|(
name|oaks
index|[
name|i
index|]
argument_list|)
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
block|}
end_class

end_unit

