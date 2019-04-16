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
name|composite
operator|.
name|CompositeNodeStore
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
name|composite
operator|.
name|InitialContentMigrator
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
name|composite
operator|.
name|checks
operator|.
name|MountedNodeStoreChecker
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
name|composite
operator|.
name|checks
operator|.
name|NamespacePrefixNodestoreChecker
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
name|composite
operator|.
name|checks
operator|.
name|NodeStoreChecksService
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
name|composite
operator|.
name|checks
operator|.
name|NodeTypeDefinitionNodeStoreChecker
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
name|composite
operator|.
name|checks
operator|.
name|NodeTypeMountedNodeStoreChecker
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
name|composite
operator|.
name|checks
operator|.
name|UniqueIndexNodeStoreChecker
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
name|DocumentNodeStore
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|state
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
name|oak
operator|.
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientURI
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
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
operator|.
name|newMongoDocumentNodeStoreBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
import|;
end_import

begin_class
specifier|abstract
class|class
name|CompositeStoreFixture
extends|extends
name|OakFixture
block|{
specifier|private
specifier|final
name|int
name|mounts
decl_stmt|;
specifier|private
specifier|final
name|int
name|pathsPerMount
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MountInfoProvider
name|MOUNT_INFO_PROVIDER
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"libs"
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/*$"
comment|// pathsSupportingFragments
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"/libs"
argument_list|,
comment|// mountedPaths
literal|"/apps"
argument_list|,
literal|"/jcr:system/rep:permissionStore/oak:mount-libs-crx.default"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
name|CompositeStoreFixture
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|mounts
parameter_list|,
name|int
name|pathsPerMount
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|mounts
operator|=
name|mounts
expr_stmt|;
name|this
operator|.
name|pathsPerMount
operator|=
name|pathsPerMount
expr_stmt|;
block|}
specifier|static
name|OakFixture
name|newCompositeMemoryFixture
parameter_list|(
name|String
name|name
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
name|CompositeStoreFixture
argument_list|(
name|name
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
operator|new
name|MemoryNodeStore
argument_list|()
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
specifier|static
name|OakFixture
name|newCompositeSegmentFixture
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
name|int
name|mounts
parameter_list|,
name|int
name|pathsPerMount
parameter_list|)
block|{
return|return
operator|new
name|CompositeStoreFixture
argument_list|(
name|name
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
block|{
specifier|private
name|FileStore
name|fileStore
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NodeStore
name|getNodeStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|FileStoreBuilder
name|fsBuilder
init|=
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|unique
argument_list|)
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
name|maxFileSizeMB
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
name|cacheSizeMB
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
decl_stmt|;
name|fileStore
operator|=
name|fsBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
return|;
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
name|fileStore
operator|!=
literal|null
condition|)
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|static
name|OakFixture
name|newCompositeMongoFixture
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
name|CompositeStoreFixture
argument_list|(
name|name
argument_list|,
name|mounts
argument_list|,
name|pathsPerMount
argument_list|)
block|{
specifier|private
name|String
name|database
init|=
operator|new
name|MongoClientURI
argument_list|(
name|uri
argument_list|)
operator|.
name|getDatabase
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
name|ns
operator|=
name|newMongoDocumentNodeStoreBuilder
argument_list|()
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|uri
argument_list|,
name|database
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|ns
return|;
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
name|ns
operator|!=
literal|null
condition|)
block|{
name|ns
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
name|MongoConnection
name|c
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|getDatabase
argument_list|(
name|database
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
specifier|protected
specifier|abstract
name|NodeStore
name|getNodeStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
function_decl|;
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
name|MemoryNodeStore
name|seed
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|Oak
name|oakSeed
init|=
operator|new
name|Oak
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|populateSeed
argument_list|(
name|oakSeed
argument_list|)
expr_stmt|;
name|NodeStore
name|global
init|=
name|getNodeStore
argument_list|()
decl_stmt|;
operator|new
name|InitialContentMigrator
argument_list|(
name|global
argument_list|,
name|seed
argument_list|,
name|MOUNT_INFO_PROVIDER
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|)
operator|.
name|migrate
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|MountedNodeStoreChecker
argument_list|<
name|?
argument_list|>
argument_list|>
name|checkerList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|checkerList
operator|.
name|add
argument_list|(
operator|new
name|NamespacePrefixNodestoreChecker
argument_list|()
argument_list|)
expr_stmt|;
name|checkerList
operator|.
name|add
argument_list|(
operator|new
name|NodeTypeDefinitionNodeStoreChecker
argument_list|()
argument_list|)
expr_stmt|;
name|checkerList
operator|.
name|add
argument_list|(
operator|new
name|NodeTypeMountedNodeStoreChecker
argument_list|()
argument_list|)
expr_stmt|;
name|checkerList
operator|.
name|add
argument_list|(
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
argument_list|)
expr_stmt|;
name|NodeStore
name|composite
init|=
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|MOUNT_INFO_PROVIDER
argument_list|,
name|global
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"libs"
argument_list|,
name|seed
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeStoreChecksService
argument_list|(
name|MOUNT_INFO_PROVIDER
argument_list|,
name|checkerList
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|(
name|composite
argument_list|)
return|;
block|}
comment|// this method allows to populate the /apps and /libs subtrees, which becomes
comment|// immutable after the composite node store is created
specifier|private
name|void
name|populateSeed
parameter_list|(
name|Oak
name|oakSeed
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|Oak
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|,
name|StatisticsProvider
name|statsProvider
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|n
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
return|return
operator|new
name|Oak
index|[]
block|{
name|getOak
argument_list|(
literal|1
argument_list|)
block|}
return|;
block|}
block|}
end_class

end_unit

