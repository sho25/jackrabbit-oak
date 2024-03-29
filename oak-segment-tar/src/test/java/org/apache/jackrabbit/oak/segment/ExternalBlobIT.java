begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|segment
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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
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
name|Lists
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|FileDataStore
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
name|api
operator|.
name|Blob
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Type
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
name|memory
operator|.
name|AbstractBlob
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
name|compaction
operator|.
name|SegmentGCOptions
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
name|FileBlob
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
name|spi
operator|.
name|blob
operator|.
name|BlobOptions
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
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
name|NodeState
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
name|DefaultStatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalBlobIT
block|{
specifier|private
name|FileStore
name|store
decl_stmt|;
specifier|private
name|SegmentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|FileBlob
name|fileBlob
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"would need a FileBlobStore for this"
argument_list|)
specifier|public
name|void
name|testFileBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|nodeStore
operator|=
name|getNodeStore
argument_list|(
operator|new
name|TestBlobStore
argument_list|()
argument_list|)
expr_stmt|;
name|testCreateAndRead
argument_list|(
name|getFileBlob
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDataStoreBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|FileDataStore
name|fds
init|=
name|createFileDataStore
argument_list|()
decl_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
name|getNodeStore
argument_list|(
name|dbs
argument_list|)
expr_stmt|;
comment|//Test for Blob which get inlined
name|Blob
name|b1
init|=
name|testCreateAndRead
argument_list|(
name|createBlob
argument_list|(
name|fds
operator|.
name|getMinRecordLength
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|instanceof
name|SegmentBlob
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|SegmentBlob
operator|)
name|b1
operator|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
operator|(
name|SegmentBlob
operator|)
name|b1
operator|)
operator|.
name|isExternal
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|isInlined
argument_list|()
argument_list|)
expr_stmt|;
comment|//Test for Blob which need to be pushed to BlobStore
name|byte
index|[]
name|data2
init|=
operator|new
name|byte
index|[
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|+
literal|1
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data2
argument_list|)
expr_stmt|;
name|Blob
name|b2
init|=
name|testCreateAndRead
argument_list|(
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|instanceof
name|SegmentBlob
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|b2
operator|.
name|getReference
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b2
operator|.
name|getContentIdentity
argument_list|()
argument_list|,
operator|(
operator|(
name|SegmentBlob
operator|)
name|b2
operator|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|isInlined
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|dbs
operator|.
name|getInputStream
argument_list|(
operator|(
operator|(
name|SegmentBlob
operator|)
name|b2
operator|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data2
argument_list|)
argument_list|,
name|is
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullBlobId
parameter_list|()
throws|throws
name|Exception
block|{
name|FileDataStore
name|fds
init|=
name|createFileDataStore
argument_list|()
decl_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
name|getNodeStore
argument_list|(
name|dbs
argument_list|)
expr_stmt|;
name|NodeBuilder
name|nb
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cb
init|=
name|nb
operator|.
name|child
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|cb
operator|.
name|setProperty
argument_list|(
literal|"blob1"
argument_list|,
name|createBlob
argument_list|(
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|noOfBlobs
init|=
literal|4000
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
name|noOfBlobs
condition|;
name|i
operator|++
control|)
block|{
name|cb
operator|.
name|setProperty
argument_list|(
literal|"blob"
operator|+
name|i
argument_list|,
name|createBlob
argument_list|(
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cb
operator|.
name|setProperty
argument_list|(
literal|"anotherBlob2"
argument_list|,
name|createBlob
argument_list|(
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setProperty
argument_list|(
literal|"anotherBlob3"
argument_list|,
name|createBlob
argument_list|(
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|nb
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|references
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|store
operator|.
name|collectBlobReferences
argument_list|(
name|reference
lambda|->
block|{
name|assertNotNull
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|references
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|noOfBlobs
operator|+
literal|2
argument_list|,
name|references
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Blob
name|testCreateAndRead
parameter_list|(
name|Blob
name|blob
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeState
name|state
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|exists
argument_list|()
condition|)
block|{
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|state
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|blob
operator|=
name|state
operator|.
name|getProperty
argument_list|(
literal|"world"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Blob written and read must be equal"
argument_list|,
name|AbstractBlob
operator|.
name|equal
argument_list|(
name|blob
argument_list|,
name|blob
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|blob
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|nodeStore
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|SegmentNodeStore
name|getNodeStore
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
name|store
operator|=
name|fileStoreBuilder
argument_list|(
name|getWorkDir
argument_list|()
argument_list|)
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withStatisticsProvider
argument_list|(
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|nodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeStore
return|;
block|}
specifier|private
name|Blob
name|createBlob
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|FileDataStore
name|createFileDataStore
parameter_list|()
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
literal|4092
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|getWorkDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|fds
return|;
block|}
specifier|private
name|File
name|getWorkDir
parameter_list|()
block|{
return|return
name|folder
operator|.
name|getRoot
argument_list|()
return|;
block|}
specifier|private
name|FileBlob
name|getFileBlob
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileBlob
operator|==
literal|null
condition|)
block|{
name|File
name|file
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|2345
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeByteArrayToFile
argument_list|(
name|file
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|fileBlob
operator|=
operator|new
name|FileBlob
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fileBlob
return|;
block|}
specifier|private
class|class
name|TestBlobStore
implements|implements
name|BlobStore
block|{
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|BlobOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobId
operator|.
name|equals
argument_list|(
name|fileBlob
operator|.
name|getReference
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|fileBlob
operator|.
name|getNewStream
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBlobId
parameter_list|(
annotation|@
name|NotNull
name|String
name|reference
parameter_list|)
block|{
return|return
name|reference
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|(
annotation|@
name|NotNull
name|String
name|blobId
parameter_list|)
block|{
return|return
name|blobId
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSize
parameter_list|()
throws|throws
name|Exception
block|{
name|FileDataStore
name|fds
init|=
name|createFileDataStore
argument_list|()
decl_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
name|getNodeStore
argument_list|(
name|dbs
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|Segment
operator|.
name|MEDIUM_LIMIT
operator|+
literal|1
decl_stmt|;
name|byte
index|[]
name|data2
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data2
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data2
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"world"
argument_list|)
decl_stmt|;
comment|// world = {2318851547697882338 bytes}
name|assertEquals
argument_list|(
name|size
argument_list|,
name|ps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// assertEquals("{" + size + " bytes}", ps.toString());
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOfflineCompaction
parameter_list|()
throws|throws
name|Exception
block|{
name|FileDataStore
name|fds
init|=
name|createFileDataStore
argument_list|()
decl_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
name|getNodeStore
argument_list|(
name|dbs
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|2
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|byte
index|[]
name|data2
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data2
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data2
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// blob went to the external store
name|assertTrue
argument_list|(
name|store
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
literal|10
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
name|SegmentGCOptions
name|gcOptions
init|=
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
decl_stmt|;
name|store
operator|=
name|fileStoreBuilder
argument_list|(
name|getWorkDir
argument_list|()
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withGCOptions
argument_list|(
name|gcOptions
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
literal|10
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|store
operator|.
name|compactFull
argument_list|()
expr_stmt|;
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

