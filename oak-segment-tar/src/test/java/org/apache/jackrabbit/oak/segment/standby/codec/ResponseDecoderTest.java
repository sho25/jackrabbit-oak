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
name|segment
operator|.
name|standby
operator|.
name|codec
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|elementsEqual
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|standby
operator|.
name|StandbyTestUtils
operator|.
name|createBlobChunkBuffer
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
name|standby
operator|.
name|StandbyTestUtils
operator|.
name|createMask
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
name|standby
operator|.
name|StandbyTestUtils
operator|.
name|hash
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
name|assertArrayEquals
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|Unpooled
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|embedded
operator|.
name|EmbeddedChannel
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|ResponseDecoderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|unrecognizedMessagesShouldBeDropped
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|channel
operator|.
name|readInbound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidOneChunkGetBlobResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|blobData
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|String
name|blobId
init|=
literal|"blobId"
decl_stmt|;
name|byte
name|mask
init|=
name|createMask
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|createBlobChunkBuffer
argument_list|(
name|Messages
operator|.
name|HEADER_BLOB
argument_list|,
literal|3L
argument_list|,
name|blobId
argument_list|,
name|blobData
argument_list|,
name|mask
argument_list|)
decl_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|GetBlobResponse
name|response
init|=
operator|(
name|GetBlobResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"blobId"
argument_list|,
name|response
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blobData
operator|.
name|length
argument_list|,
name|response
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|response
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|byte
index|[]
name|receivedData
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|blobData
argument_list|,
name|receivedData
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidTwoChunksGetBlobResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|blobData
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
decl_stmt|;
name|byte
index|[]
name|firstChunkData
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|}
decl_stmt|;
name|byte
index|[]
name|secondChunkbData
init|=
operator|new
name|byte
index|[]
block|{
literal|3
block|,
literal|4
block|}
decl_stmt|;
name|String
name|blobId
init|=
literal|"blobId"
decl_stmt|;
name|byte
name|firstMask
init|=
name|createMask
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ByteBuf
name|firstBuf
init|=
name|createBlobChunkBuffer
argument_list|(
name|Messages
operator|.
name|HEADER_BLOB
argument_list|,
literal|4L
argument_list|,
name|blobId
argument_list|,
name|firstChunkData
argument_list|,
name|firstMask
argument_list|)
decl_stmt|;
name|byte
name|secondMask
init|=
name|createMask
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ByteBuf
name|secondBuf
init|=
name|createBlobChunkBuffer
argument_list|(
name|Messages
operator|.
name|HEADER_BLOB
argument_list|,
literal|4L
argument_list|,
name|blobId
argument_list|,
name|secondChunkbData
argument_list|,
name|secondMask
argument_list|)
decl_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|firstBuf
argument_list|)
expr_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|secondBuf
argument_list|)
expr_stmt|;
name|GetBlobResponse
name|response
init|=
operator|(
name|GetBlobResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"blobId"
argument_list|,
name|response
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blobData
operator|.
name|length
argument_list|,
name|response
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|response
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|byte
index|[]
name|receivedData
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|blobData
argument_list|,
name|receivedData
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDropInvalidGetBlobResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|blobData
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|String
name|blobId
init|=
literal|"blobId"
decl_stmt|;
name|byte
index|[]
name|blobIdBytes
init|=
name|blobId
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
name|mask
init|=
name|createMask
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
literal|1
operator|+
literal|1
operator|+
literal|8
operator|+
literal|4
operator|+
name|blobIdBytes
operator|.
name|length
operator|+
literal|8
operator|+
name|blobData
operator|.
name|length
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_BLOB
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|mask
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|blobIdBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|blobIdBytes
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|hash
argument_list|(
name|mask
argument_list|,
literal|3L
argument_list|,
name|blobData
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|blobData
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|channel
operator|.
name|readInbound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidGetHeadResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|recordId
init|=
literal|"recordId"
decl_stmt|;
name|byte
index|[]
name|recordIdBytes
init|=
name|recordId
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ByteBuf
name|in
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|writeInt
argument_list|(
name|recordIdBytes
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|in
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_RECORD
argument_list|)
expr_stmt|;
name|in
operator|.
name|writeBytes
argument_list|(
name|recordIdBytes
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|GetHeadResponse
name|response
init|=
operator|(
name|GetHeadResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|recordId
argument_list|,
name|response
operator|.
name|getHeadRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidGetSegmentResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[]
block|{
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|25
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_SEGMENT
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|hash
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|GetSegmentResponse
name|response
init|=
operator|(
name|GetSegmentResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|UUID
operator|.
name|fromString
argument_list|(
name|response
operator|.
name|getSegmentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|response
operator|.
name|getSegmentData
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidGetReferencesResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
literal|"a:b,c"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_REFERENCES
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|GetReferencesResponse
name|response
init|=
operator|(
name|GetReferencesResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|response
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|elementsEqual
argument_list|(
name|asList
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|response
operator|.
name|getReferences
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDropGetReferencesResponsesWithoutDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
literal|"a"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_REFERENCES
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|channel
operator|.
name|readInbound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidSingleElementGetReferencesResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
literal|"a:b"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_REFERENCES
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|GetReferencesResponse
name|response
init|=
operator|(
name|GetReferencesResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|response
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|elementsEqual
argument_list|(
name|newArrayList
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|response
operator|.
name|getReferences
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDecodeValidZeroElementsGetReferencesResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
literal|"a:"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_REFERENCES
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|GetReferencesResponse
name|response
init|=
operator|(
name|GetReferencesResponse
operator|)
name|channel
operator|.
name|readInbound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|response
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|elementsEqual
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|response
operator|.
name|getReferences
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldDropInvalidGetSegmentResponses
parameter_list|()
throws|throws
name|Exception
block|{
name|UUID
name|uuid
init|=
operator|new
name|UUID
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[]
block|{
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
name|ByteBuf
name|buf
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
operator|+
literal|25
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_SEGMENT
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeLong
argument_list|(
name|hash
argument_list|(
name|data
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|ResponseDecoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|channel
operator|.
name|readInbound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

