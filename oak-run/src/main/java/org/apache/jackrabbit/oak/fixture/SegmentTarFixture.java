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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_class
class|class
name|SegmentTarFixture
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
name|SegmentTarFixture
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
name|fileStoreBuilder
argument_list|(
name|base
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
name|maxFileSizeMB
argument_list|)
operator|.
name|withCacheSize
argument_list|(
name|cacheSizeMB
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|newOak
argument_list|(
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
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
name|FileStoreBuilder
name|builder
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
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|stores
index|[
name|i
index|]
operator|=
name|builder
operator|.
name|withMaxFileSize
argument_list|(
name|maxFileSizeMB
argument_list|)
operator|.
name|withCacheSize
argument_list|(
name|cacheSizeMB
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
index|[
name|i
index|]
operator|=
name|newOak
argument_list|(
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|stores
index|[
name|i
index|]
argument_list|)
operator|.
name|build
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
name|FileStore
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
end_class

end_unit

