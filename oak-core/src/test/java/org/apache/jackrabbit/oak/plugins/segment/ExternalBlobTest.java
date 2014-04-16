begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|segment
package|;
end_package

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
name|DataIdentifier
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
name|plugins
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
name|Test
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
name|Random
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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

begin_class
specifier|public
class|class
name|ExternalBlobTest
block|{
specifier|private
name|SegmentStore
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
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|fds
operator|.
name|getMinRecordLength
argument_list|()
operator|-
literal|2
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
name|Blob
name|b1
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
name|data
argument_list|)
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
specifier|public
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
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|getWorkDir
argument_list|()
argument_list|)
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
name|IOException
block|{
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|blobStore
argument_list|,
name|getWorkDir
argument_list|()
argument_list|,
literal|256
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|nodeStore
operator|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeStore
return|;
block|}
specifier|private
name|File
name|getWorkDir
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"ExternalBlobTest"
argument_list|)
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
name|File
operator|.
name|createTempFile
argument_list|(
literal|"blob"
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
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
name|String
name|blobId
parameter_list|)
block|{
return|return
name|blobId
return|;
block|}
block|}
block|}
end_class

end_unit

