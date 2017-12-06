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
name|server
operator|.
name|FileStoreUtil
operator|.
name|roundDiv
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|hash
operator|.
name|Hashing
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
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|ByteToMessageDecoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ResponseDecoder
extends|extends
name|ByteToMessageDecoder
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ResponseDecoder
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
class|class
name|DeleteOnCloseFileInputStream
extends|extends
name|FileInputStream
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DeleteOnCloseFileInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
name|DeleteOnCloseFileInputStream
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|super
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"File {} was deleted"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not delete {}, not found"
argument_list|,
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
name|File
name|spoolFolder
decl_stmt|;
specifier|private
name|int
name|blobChunkSize
decl_stmt|;
specifier|public
name|ResponseDecoder
parameter_list|(
name|File
name|spoolFolder
parameter_list|)
block|{
name|this
operator|.
name|spoolFolder
operator|=
name|spoolFolder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|decode
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ByteBuf
name|in
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|in
operator|.
name|readByte
argument_list|()
condition|)
block|{
case|case
name|Messages
operator|.
name|HEADER_RECORD
case|:
name|log
operator|.
name|debug
argument_list|(
literal|"Decoding 'get head' response"
argument_list|)
expr_stmt|;
name|decodeGetHeadResponse
argument_list|(
name|length
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
break|break;
case|case
name|Messages
operator|.
name|HEADER_SEGMENT
case|:
name|log
operator|.
name|debug
argument_list|(
literal|"Decoding 'get segment' response"
argument_list|)
expr_stmt|;
name|decodeGetSegmentResponse
argument_list|(
name|length
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
break|break;
case|case
name|Messages
operator|.
name|HEADER_BLOB
case|:
name|log
operator|.
name|debug
argument_list|(
literal|"Decoding 'get blob' response"
argument_list|)
expr_stmt|;
name|decodeGetBlobResponse
argument_list|(
name|length
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
break|break;
case|case
name|Messages
operator|.
name|HEADER_REFERENCES
case|:
name|log
operator|.
name|debug
argument_list|(
literal|"Decoding 'get references' response"
argument_list|)
expr_stmt|;
name|decodeGetReferencesResponse
argument_list|(
name|length
argument_list|,
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
break|break;
default|default:
name|log
operator|.
name|debug
argument_list|(
literal|"Invalid type, dropping message"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|decodeGetHeadResponse
parameter_list|(
name|int
name|length
parameter_list|,
name|ByteBuf
name|in
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|String
name|recordId
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|out
operator|.
name|add
argument_list|(
operator|new
name|GetHeadResponse
argument_list|(
literal|null
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|decodeGetSegmentResponse
parameter_list|(
name|int
name|length
parameter_list|,
name|ByteBuf
name|in
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|)
block|{
name|long
name|msb
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|String
name|segmentId
init|=
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|long
name|hash
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
operator|-
literal|25
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
argument_list|(
name|data
argument_list|)
operator|!=
name|hash
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Invalid checksum, discarding segment {}"
argument_list|,
name|segmentId
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
operator|.
name|add
argument_list|(
operator|new
name|GetSegmentResponse
argument_list|(
literal|null
argument_list|,
name|segmentId
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|decodeGetBlobResponse
parameter_list|(
name|int
name|length
parameter_list|,
name|ByteBuf
name|in
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|mask
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|long
name|blobLength
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|int
name|blobIdLength
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|blobIdBytes
init|=
operator|new
name|byte
index|[
name|blobIdLength
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|blobIdBytes
argument_list|)
expr_stmt|;
name|String
name|blobId
init|=
operator|new
name|String
argument_list|(
name|blobIdBytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|spoolFolder
argument_list|,
name|blobId
operator|+
literal|".tmp"
argument_list|)
decl_stmt|;
comment|// START_CHUNK flag enabled
if|if
condition|(
operator|(
name|mask
operator|&
operator|(
literal|1
operator|<<
literal|0
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|blobChunkSize
operator|=
name|in
operator|.
name|readableBytes
argument_list|()
operator|-
literal|8
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|tempFile
operator|.
name|toPath
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Deleted temporary file for previous incomplete transfer of {}"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|hash
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Received chunk {}/{} of size {} from blob {}"
argument_list|,
name|roundDiv
argument_list|(
name|tempFile
operator|.
name|length
argument_list|()
operator|+
name|in
operator|.
name|readableBytes
argument_list|()
argument_list|,
name|blobChunkSize
argument_list|)
argument_list|,
name|roundDiv
argument_list|(
name|blobLength
argument_list|,
name|blobChunkSize
argument_list|)
argument_list|,
name|in
operator|.
name|readableBytes
argument_list|()
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
name|byte
index|[]
name|chunkData
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readableBytes
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|chunkData
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
argument_list|(
name|mask
argument_list|,
name|blobLength
argument_list|,
name|chunkData
argument_list|)
operator|!=
name|hash
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Invalid checksum, discarding current chunk from {}"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"All checks OK. Appending chunk to disk to {} "
argument_list|,
name|tempFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|outStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|outStream
operator|.
name|write
argument_list|(
name|chunkData
argument_list|)
expr_stmt|;
block|}
block|}
comment|// END_CHUNK flag enabled
if|if
condition|(
operator|(
name|mask
operator|&
operator|(
literal|1
operator|<<
literal|1
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Received entire blob {}"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
if|if
condition|(
name|blobLength
operator|==
name|tempFile
operator|.
name|length
argument_list|()
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|GetBlobResponse
argument_list|(
literal|null
argument_list|,
name|blobId
argument_list|,
operator|new
name|DeleteOnCloseFileInputStream
argument_list|(
name|tempFile
argument_list|)
argument_list|,
name|blobLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Blob {} discarded due to size mismatch. Expected size: {}, actual size: {} "
argument_list|,
name|blobId
argument_list|,
name|blobLength
argument_list|,
name|tempFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|decodeGetReferencesResponse
parameter_list|(
name|int
name|length
parameter_list|,
name|ByteBuf
name|in
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|out
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|String
name|body
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|colon
init|=
name|body
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|String
name|segmentId
init|=
name|body
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
decl_stmt|;
name|String
name|referencesList
init|=
name|body
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|references
decl_stmt|;
if|if
condition|(
name|referencesList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|references
operator|=
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|references
operator|=
name|asList
argument_list|(
name|referencesList
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|add
argument_list|(
operator|new
name|GetReferencesResponse
argument_list|(
literal|null
argument_list|,
name|segmentId
argument_list|,
name|references
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|hash
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|Hashing
operator|.
name|murmur3_32
argument_list|()
operator|.
name|newHasher
argument_list|()
operator|.
name|putBytes
argument_list|(
name|data
argument_list|)
operator|.
name|hash
argument_list|()
operator|.
name|padToLong
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|long
name|hash
parameter_list|(
name|byte
name|mask
parameter_list|,
name|long
name|blobLength
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|Hashing
operator|.
name|murmur3_32
argument_list|()
operator|.
name|newHasher
argument_list|()
operator|.
name|putByte
argument_list|(
name|mask
argument_list|)
operator|.
name|putLong
argument_list|(
name|blobLength
argument_list|)
operator|.
name|putBytes
argument_list|(
name|data
argument_list|)
operator|.
name|hash
argument_list|()
operator|.
name|padToLong
argument_list|()
return|;
block|}
block|}
end_class

end_unit

