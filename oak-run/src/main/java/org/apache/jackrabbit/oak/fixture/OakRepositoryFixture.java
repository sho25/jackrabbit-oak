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
name|plugins
operator|.
name|mongomk
operator|.
name|MongoMK
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
name|mongomk
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
name|plugins
operator|.
name|segment
operator|.
name|mongo
operator|.
name|MongoStore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
import|;
end_import

begin_class
specifier|public
specifier|abstract
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
specifier|final
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
literal|"Oak-Memory"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
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
name|Repository
index|[]
name|cluster
init|=
operator|new
name|Repository
index|[
name|n
index|]
decl_stmt|;
name|MicroKernel
name|kernel
init|=
operator|new
name|MicroKernelImpl
argument_list|()
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
init|=
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
decl_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
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
block|}
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getDefault
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
name|OakRepositoryFixture
argument_list|(
literal|"Oak-Default"
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
name|Repository
index|[]
name|cluster
init|=
operator|new
name|Repository
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
name|Oak
name|oak
init|=
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
decl_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
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
specifier|public
specifier|static
name|RepositoryFixture
name|getMongo
parameter_list|(
specifier|final
name|String
name|host
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
literal|"Oak-Mongo"
argument_list|)
block|{
specifier|private
name|MongoMK
index|[]
name|kernels
decl_stmt|;
annotation|@
name|Override
specifier|public
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
name|Repository
index|[]
name|cluster
init|=
operator|new
name|Repository
index|[
name|n
index|]
decl_stmt|;
name|kernels
operator|=
operator|new
name|MongoMK
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
name|unique
argument_list|)
decl_stmt|;
name|kernels
index|[
name|i
index|]
operator|=
operator|new
name|MongoMK
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
name|setClusterId
argument_list|(
name|i
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
name|Oak
name|oak
init|=
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
decl_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
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
name|tearDownCluster
parameter_list|()
block|{
for|for
control|(
name|MongoMK
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
name|unique
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
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getSegment
parameter_list|(
specifier|final
name|String
name|host
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
specifier|final
name|long
name|cacheSize
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
literal|"Oak-Segment"
argument_list|)
block|{
specifier|private
name|SegmentStore
index|[]
name|stores
decl_stmt|;
specifier|private
name|Mongo
name|mongo
decl_stmt|;
annotation|@
name|Override
specifier|public
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
name|Repository
index|[]
name|cluster
init|=
operator|new
name|Repository
index|[
name|n
index|]
decl_stmt|;
name|stores
operator|=
operator|new
name|SegmentStore
index|[
name|cluster
operator|.
name|length
index|]
expr_stmt|;
name|mongo
operator|=
operator|new
name|Mongo
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
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
name|MongoStore
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|(
name|unique
argument_list|)
argument_list|,
name|cacheSize
argument_list|)
expr_stmt|;
name|Oak
name|oak
init|=
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
decl_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
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
name|mongo
operator|.
name|getDB
argument_list|(
name|unique
argument_list|)
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|RepositoryFixture
name|getTar
parameter_list|(
specifier|final
name|File
name|base
parameter_list|,
specifier|final
name|int
name|maxFileSize
parameter_list|,
specifier|final
name|boolean
name|memoryMapping
parameter_list|)
block|{
return|return
operator|new
name|OakRepositoryFixture
argument_list|(
literal|"Oak-Tar"
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
name|Repository
index|[]
name|cluster
init|=
operator|new
name|Repository
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
name|maxFileSize
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
name|Oak
name|oak
init|=
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
decl_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
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
name|OakRepositoryFixture
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
comment|// nothing to do by default
block|}
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
block|}
end_class

end_unit

