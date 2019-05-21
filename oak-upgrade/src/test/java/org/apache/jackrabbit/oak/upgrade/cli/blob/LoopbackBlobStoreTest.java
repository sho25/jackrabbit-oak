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
name|upgrade
operator|.
name|cli
operator|.
name|blob
package|;
end_package

begin_import
import|import
name|junitparams
operator|.
name|JUnitParamsRunner
import|;
end_import

begin_import
import|import
name|junitparams
operator|.
name|Parameters
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
name|runner
operator|.
name|RunWith
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
name|assertNotNull
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedLabel"
argument_list|)
annotation|@
name|RunWith
argument_list|(
name|JUnitParamsRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|LoopbackBlobStoreTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|writingBinariesIsNotSupported
parameter_list|()
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
specifier|final
name|String
name|test
init|=
literal|"Test"
decl_stmt|;
name|blobStore
operator|.
name|writeBlob
argument_list|(
name|adaptToUtf8InputStream
argument_list|(
name|test
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|writingBinariesWithBlobOptsIsNotSupported
parameter_list|()
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|BlobOptions
name|blobOptions
init|=
operator|new
name|BlobOptions
argument_list|()
decl_stmt|;
name|when
label|:
block|{
name|blobStore
operator|.
name|writeBlob
argument_list|(
name|adaptToUtf8InputStream
argument_list|(
literal|"Test"
argument_list|)
argument_list|,
name|blobOptions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIds"
argument_list|)
specifier|public
name|void
name|getBlobIdShouldReturnTheSameValuePassedExceptOfNull
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|)
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|expect
label|:
block|{
name|assertEquals
argument_list|(
name|blobId
argument_list|,
name|blobStore
operator|.
name|getBlobId
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ConstantConditions"
argument_list|)
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getBlobIdShouldThrowAnExceptionWhenNullIsPassed
parameter_list|()
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
name|blobStore
operator|.
name|getBlobId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIds"
argument_list|)
specifier|public
name|void
name|getReferenceShouldReturnTheSameValuePassedExceptOfNull
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|)
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|where
label|:
block|{
name|expect
label|:
block|{
name|assertEquals
argument_list|(
name|blobId
argument_list|,
name|blobStore
operator|.
name|getReference
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ConstantConditions"
argument_list|)
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getReferenceShouldThrowAnExceptionWhenNullIsPassed
parameter_list|()
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
name|blobStore
operator|.
name|getReference
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIds"
argument_list|)
specifier|public
name|void
name|getBlobLengthShouldAlwaysReturnRealLengthOfBlobThatWillBeReturned
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|expect
label|:
block|{
name|assertEquals
argument_list|(
name|blobId
operator|.
name|getBytes
argument_list|()
operator|.
name|length
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getBlobLengthShouldAlwaysThrowAnExceptionWhenNullBlobIdIsPassed
parameter_list|()
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
name|store
operator|.
name|getBlobLength
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getInputStreamShouldAlwaysThrowAnExceptionWhenNullBlobIdIsPassed
parameter_list|()
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
name|store
operator|.
name|getInputStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIds"
argument_list|)
specifier|public
name|void
name|shouldAlwaysReturnStreamOfRequestedBlobIdUtf8BinRepresentation
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
name|when
label|:
block|{
specifier|final
name|InputStream
name|inputStream
init|=
name|store
operator|.
name|getInputStream
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|then
label|:
block|{
name|assertNotNull
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
name|and
label|:
block|{
specifier|final
name|String
name|actualInputStreamAsString
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|inputStream
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|then
label|:
block|{
name|assertEquals
argument_list|(
name|actualInputStreamAsString
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIdsReads"
argument_list|)
specifier|public
name|void
name|shouldAlwaysFillBufferWithRequestedBlobIdUtf8BinRepresentation
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|,
name|int
name|offsetToRead
parameter_list|,
name|int
name|bufSize
parameter_list|,
name|int
name|bufOffset
parameter_list|,
name|int
name|lengthToRead
parameter_list|,
specifier|final
name|String
name|expectedBufferContent
parameter_list|,
specifier|final
name|int
name|expectedNumberOfBytesRead
parameter_list|)
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|final
name|BlobStore
name|blobStore
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bufSize
index|]
decl_stmt|;
name|when
label|:
block|{
specifier|final
name|int
name|numberOfBytesRead
init|=
name|blobStore
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|offsetToRead
argument_list|,
name|buffer
argument_list|,
name|bufOffset
argument_list|,
name|lengthToRead
argument_list|)
decl_stmt|;
name|and
label|:
block|{
specifier|final
name|String
name|actualInputStreamAsString
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|buffer
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|then
label|:
block|{
name|assertEquals
argument_list|(
name|numberOfBytesRead
argument_list|,
name|expectedNumberOfBytesRead
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedBufferContent
argument_list|,
name|encodeBufferFreeSpace
argument_list|(
name|actualInputStreamAsString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIdsFailedBufferReadsCases"
argument_list|)
specifier|public
name|void
name|getInputStreamShouldAlwaysReturnExceptionIfBufferTooSmall
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|,
name|int
name|offsetToRead
parameter_list|,
name|int
name|bufSize
parameter_list|,
name|int
name|bufOffset
parameter_list|,
name|int
name|lengthToRead
parameter_list|)
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bufSize
index|]
decl_stmt|;
name|when
label|:
block|{
name|store
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|offsetToRead
argument_list|,
name|buffer
argument_list|,
name|bufOffset
argument_list|,
name|lengthToRead
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
annotation|@
name|Parameters
argument_list|(
name|method
operator|=
literal|"blobIdsFailedOffsetReadsCases"
argument_list|)
specifier|public
name|void
name|getInputStreamShouldAlwaysReturnExceptionIfBinaryOffsetIsBad
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|,
name|int
name|offsetToRead
parameter_list|,
name|int
name|bufSize
parameter_list|,
name|int
name|bufOffset
parameter_list|,
name|int
name|lengthToRead
parameter_list|)
throws|throws
name|IOException
block|{
name|given
label|:
block|{
specifier|final
name|BlobStore
name|store
init|=
operator|new
name|LoopbackBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bufSize
index|]
decl_stmt|;
name|when
label|:
block|{
name|store
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|offsetToRead
argument_list|,
name|buffer
argument_list|,
name|bufOffset
argument_list|,
name|lengthToRead
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Object
name|blobIdsReads
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
comment|//blobId, offsetToRead, bufSize, bufOffset, lengthToRead, expectedBufferContent, expectedNumOfBytesRead
operator|new
name|Object
index|[]
block|{
literal|""
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|""
block|,
literal|0
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|""
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|""
block|,
literal|0
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|0
block|,
literal|4
block|,
literal|0
block|,
literal|4
block|,
literal|"IDX1"
block|,
literal|4
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|4
block|,
literal|0
block|,
literal|0
block|,
literal|4
block|,
literal|""
block|,
literal|0
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|4
block|,
literal|4
block|,
literal|0
block|,
literal|4
block|,
literal|"####"
block|,
literal|0
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|0
block|,
literal|5
block|,
literal|0
block|,
literal|4
block|,
literal|"IDX1#"
block|,
literal|4
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|1
block|,
literal|4
block|,
literal|0
block|,
literal|3
block|,
literal|"DX1#"
block|,
literal|3
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|1
block|,
literal|4
block|,
literal|0
block|,
literal|4
block|,
literal|"DX1#"
block|,
literal|3
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZYZYYXYZYZYXYZQ"
block|,
literal|10
block|,
literal|20
block|,
literal|3
block|,
literal|10
block|,
literal|"###XXXXYYZYZY#######"
block|,
literal|10
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|20
block|,
literal|3
block|,
literal|10
block|,
literal|"###XXXXYYZY#########"
block|,
literal|8
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|20
block|,
literal|3
block|,
literal|10
block|,
literal|"###XXXXYYZY#########"
block|,
literal|8
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|11
block|,
literal|3
block|,
literal|10
block|,
literal|"###XXXXYYZY"
block|,
literal|8
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|11
block|,
literal|2
block|,
literal|10
block|,
literal|"##XXXXYYZY#"
block|,
literal|8
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|11
block|,
literal|1
block|,
literal|10
block|,
literal|"#XXXXYYZY##"
block|,
literal|8
block|}
block|,         }
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Object
name|blobIdsFailedBufferReadsCases
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
comment|//blobId, offsetToRead, bufferSize, bufferOffset, lengthToRead
operator|new
name|Object
index|[]
block|{
literal|" "
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|0
block|,
literal|3
block|,
literal|0
block|,
literal|4
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|1
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|1
block|,
literal|2
block|,
literal|0
block|,
literal|3
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|10
block|,
literal|0
block|,
literal|30
block|,
literal|10
block|}
block|,         }
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Object
name|blobIdsFailedOffsetReadsCases
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
comment|//blobId, offsetToRead, bufferSize, bufferOffset, lengthToRead
operator|new
name|Object
index|[]
block|{
literal|""
block|,
literal|1
block|,
literal|50
block|,
literal|0
block|,
literal|0
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|5
block|,
literal|50
block|,
literal|0
block|,
literal|3
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|,
literal|6
block|,
literal|50
block|,
literal|0
block|,
literal|4
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZY"
block|,
literal|30
block|,
literal|50
block|,
literal|1
block|,
literal|10
block|}
block|,         }
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|Object
name|blobIds
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
operator|new
name|Object
index|[]
block|{
literal|""
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"IDX1"
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ID2XXXXXXXXXXXYYZYZYYXYZYZYXYZQ"
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"ABCQ"
block|}
block|}
return|;
block|}
specifier|private
name|String
name|encodeBufferFreeSpace
parameter_list|(
specifier|final
name|String
name|actualInputStreamAsString
parameter_list|)
block|{
return|return
name|actualInputStreamAsString
operator|.
name|replace
argument_list|(
literal|'\0'
argument_list|,
literal|'#'
argument_list|)
return|;
block|}
specifier|private
name|InputStream
name|adaptToUtf8InputStream
parameter_list|(
specifier|final
name|String
name|string
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|string
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

