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
name|mk
operator|.
name|blobs
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
name|mk
operator|.
name|util
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
name|mk
operator|.
name|util
operator|.
name|Cache
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
name|util
operator|.
name|StringUtils
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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|DigestOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import

begin_comment
comment|/**  * An abstract data store that splits the binaries in relatively small blocks,  * so that each block fits in memory.  *<p>  * Each data store id is a list of zero or more entries. Each entry is either  *<ul>  *<li>data (a number of bytes), or</li>  *<li>the hash code of the content of a number of bytes, or</li>  *<li>the hash code of the content of a data store id (indirect hash)</li>  *</ul>  * Thanks to the indirection, blocks can be kept relatively small, so that  * caching is simpler, and so that the storage backend doesn't need to support  * arbitrary size blobs (some storage backends buffer blobs in memory) and fast  * seeks (some storage backends re-read the whole blob when seeking).  *<p>  * The the format of a 'data' entry is: type (one byte; 0 for data), length  * (variable size int), data (bytes).  *<p>  * The format of a 'hash of content' entry is: type (one byte; 1 for hash),  * level (variable size int, 0 meaning not nested), size (variable size long),  * hash code length (variable size int), hash code.  *<p>  * The format of a 'hash of data store id' entry is: type (one byte; 1 for  * hash), level (variable size int, nesting level), total size (variable size  * long), size of data store id (variable size long), hash code length (variable  * size int), hash code.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractBlobStore
implements|implements
name|Closeable
implements|,
name|BlobStore
implements|,
name|Cache
operator|.
name|Backend
argument_list|<
name|AbstractBlobStore
operator|.
name|BlockId
argument_list|,
name|AbstractBlobStore
operator|.
name|Data
argument_list|>
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|HASH_ALGORITHM
init|=
literal|"SHA-1"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|TYPE_DATA
init|=
literal|0
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|TYPE_HASH
init|=
literal|1
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|TYPE_HASH_COMPRESSED
init|=
literal|2
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|WeakReference
argument_list|<
name|String
argument_list|>
argument_list|>
name|inUse
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|WeakReference
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * The minimum size of a block. Smaller blocks are inlined (the data store id      * is the data itself).      */
specifier|private
name|int
name|blockSizeMin
init|=
literal|256
decl_stmt|;
comment|/**      * The size of a block. 128 KB has been found to be as fast as larger      * values, and faster than smaller values. 2 MB results in less files.      */
specifier|private
name|int
name|blockSize
init|=
literal|2
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
name|Cache
argument_list|<
name|AbstractBlobStore
operator|.
name|BlockId
argument_list|,
name|Data
argument_list|>
name|cache
init|=
name|Cache
operator|.
name|newInstance
argument_list|(
name|this
argument_list|,
literal|8
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
specifier|public
name|void
name|setBlockSizeMin
parameter_list|(
name|int
name|x
parameter_list|)
block|{
name|this
operator|.
name|blockSizeMin
operator|=
name|x
expr_stmt|;
block|}
specifier|public
name|long
name|getBlockSizeMin
parameter_list|()
block|{
return|return
name|blockSizeMin
return|;
block|}
specifier|public
name|void
name|setBlockSize
parameter_list|(
name|int
name|x
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|x
expr_stmt|;
block|}
specifier|public
name|int
name|getBlockSize
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
specifier|public
name|String
name|addBlob
parameter_list|(
name|String
name|tempFilePath
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tempFilePath
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|ByteArrayOutputStream
name|idStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|convertBlobToId
argument_list|(
name|in
argument_list|,
name|idStream
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|byte
index|[]
name|id
init|=
name|idStream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
comment|// System.out.println("    write blob " +  StringUtils.convertBytesToHex(id));
name|String
name|blobId
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|usesBlobId
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
return|return
name|blobId
return|;
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
specifier|protected
name|void
name|usesBlobId
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
name|inUse
operator|.
name|put
argument_list|(
name|blobId
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|String
argument_list|>
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clearInUse
parameter_list|()
block|{
name|inUse
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|convertBlobToId
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|ByteArrayOutputStream
name|idStream
parameter_list|,
name|int
name|level
parameter_list|,
name|long
name|totalLength
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|block
init|=
operator|new
name|byte
index|[
name|blockSize
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|MessageDigest
name|messageDigest
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|HASH_ALGORITHM
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|buff
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DigestOutputStream
name|dout
init|=
operator|new
name|DigestOutputStream
argument_list|(
name|buff
argument_list|,
name|messageDigest
argument_list|)
decl_stmt|;
name|int
name|blockLen
init|=
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|block
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|length
argument_list|)
decl_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|blockLen
operator|==
literal|0
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|blockLen
operator|<
name|blockSizeMin
condition|)
block|{
name|idStream
operator|.
name|write
argument_list|(
name|TYPE_DATA
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|idStream
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|idStream
operator|.
name|write
argument_list|(
name|block
argument_list|,
literal|0
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
name|blockLen
expr_stmt|;
block|}
else|else
block|{
name|dout
operator|.
name|write
argument_list|(
name|block
argument_list|,
literal|0
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest
init|=
name|messageDigest
operator|.
name|digest
argument_list|()
decl_stmt|;
name|idStream
operator|.
name|write
argument_list|(
name|TYPE_HASH
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|idStream
argument_list|,
name|level
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|IOUtils
operator|.
name|writeVarLong
argument_list|(
name|idStream
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|writeVarLong
argument_list|(
name|idStream
argument_list|,
name|blockLen
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
name|blockLen
expr_stmt|;
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|idStream
argument_list|,
name|digest
operator|.
name|length
argument_list|)
expr_stmt|;
name|idStream
operator|.
name|write
argument_list|(
name|digest
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|buff
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|storeBlock
argument_list|(
name|digest
argument_list|,
name|level
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idStream
operator|.
name|size
argument_list|()
operator|>
name|blockSize
operator|/
literal|2
condition|)
block|{
comment|// convert large ids to a block, but ensure it can be stored as
comment|// one block (otherwise the indirection no longer works)
name|byte
index|[]
name|idBlock
init|=
name|idStream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|idStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|convertBlobToId
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|idBlock
argument_list|)
argument_list|,
name|idStream
argument_list|,
name|level
operator|+
literal|1
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
name|count
operator|=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|>
literal|0
operator|&&
name|idStream
operator|.
name|size
argument_list|()
operator|>
name|blockSizeMin
condition|)
block|{
comment|// at the very end, convert large ids to a block,
comment|// because large block ids are not handy
comment|// (specially if they are used to read data in small chunks)
name|byte
index|[]
name|idBlock
init|=
name|idStream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|idStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|convertBlobToId
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|idBlock
argument_list|)
argument_list|,
name|idStream
argument_list|,
name|level
operator|+
literal|1
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|void
name|startMark
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|int
name|sweep
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|isMarkEnabled
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|void
name|mark
parameter_list|(
name|BlockId
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
name|void
name|markInUse
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|id
range|:
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|inUse
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|mark
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Exception
block|{
if|if
condition|(
name|isMarkEnabled
argument_list|()
condition|)
block|{
name|mark
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|id
init|=
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|idStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|id
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|idStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|TYPE_DATA
condition|)
block|{
name|int
name|len
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|len
condition|)
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|idStream
argument_list|,
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
name|len
operator|-=
name|pos
expr_stmt|;
if|if
condition|(
name|length
operator|<
name|len
condition|)
block|{
name|len
operator|=
name|length
expr_stmt|;
block|}
name|IOUtils
operator|.
name|readFully
argument_list|(
name|idStream
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|idStream
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|-=
name|len
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|TYPE_HASH
condition|)
block|{
name|int
name|level
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
name|long
name|totalLength
init|=
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
comment|// block length (ignored)
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|digest
init|=
operator|new
name|byte
index|[
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|idStream
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|digest
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|totalLength
condition|)
block|{
name|pos
operator|-=
name|totalLength
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|block
init|=
name|readBlock
argument_list|(
name|digest
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|idStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|readPos
init|=
name|pos
operator|-
name|pos
operator|%
name|blockSize
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|readBlock
argument_list|(
name|digest
argument_list|,
name|readPos
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
name|pos
operator|-
name|readPos
argument_list|)
expr_stmt|;
return|return
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown blobs id type "
operator|+
name|type
operator|+
literal|" for blob "
operator|+
name|blobId
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|byte
index|[]
name|readBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|long
name|pos
parameter_list|)
throws|throws
name|Exception
block|{
name|BlockId
name|id
init|=
operator|new
name|BlockId
argument_list|(
name|digest
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|data
return|;
block|}
specifier|public
name|Data
name|load
parameter_list|(
name|BlockId
name|id
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|Data
argument_list|(
name|readBlockFromBackend
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
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
literal|"failed to read block from backend"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|abstract
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
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
if|if
condition|(
name|isMarkEnabled
argument_list|()
condition|)
block|{
name|mark
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|id
init|=
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|idStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|long
name|totalLength
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|idStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|type
operator|==
name|TYPE_DATA
condition|)
block|{
name|int
name|len
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|idStream
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
name|len
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|TYPE_HASH
condition|)
block|{
name|int
name|level
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
name|totalLength
operator|+=
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
comment|// block length (ignored)
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
block|}
name|int
name|digestLength
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|idStream
argument_list|,
name|digestLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Datastore id type "
operator|+
name|type
operator|+
literal|" for blob "
operator|+
name|blobId
argument_list|)
throw|;
block|}
block|}
return|return
name|totalLength
return|;
block|}
specifier|protected
name|void
name|mark
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|byte
index|[]
name|id
init|=
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|idStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|mark
argument_list|(
name|idStream
argument_list|)
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
name|IOException
argument_list|(
literal|"Mark failed for blob "
operator|+
name|blobId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|mark
parameter_list|(
name|ByteArrayInputStream
name|idStream
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|idStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|TYPE_DATA
condition|)
block|{
name|int
name|len
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|idStream
argument_list|,
operator|(
name|int
operator|)
name|len
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|TYPE_HASH
condition|)
block|{
name|int
name|level
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
decl_stmt|;
comment|// totalLength
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
comment|// block length (ignored)
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|digest
init|=
operator|new
name|byte
index|[
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|idStream
argument_list|)
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|idStream
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|digest
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|block
init|=
name|readBlock
argument_list|(
name|digest
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|idStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|mark
argument_list|(
name|idStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BlockId
name|id
init|=
operator|new
name|BlockId
argument_list|(
name|digest
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|mark
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown blobs id type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// ignore
block|}
comment|/**      * A block id. Blocks are small enough to fit in memory, so they can be      * cached.      */
specifier|public
specifier|static
class|class
name|BlockId
block|{
specifier|final
name|byte
index|[]
name|digest
decl_stmt|;
specifier|final
name|long
name|pos
decl_stmt|;
name|BlockId
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|long
name|pos
parameter_list|)
block|{
name|this
operator|.
name|digest
operator|=
name|digest
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|BlockId
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BlockId
name|o
init|=
operator|(
name|BlockId
operator|)
name|other
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|digest
argument_list|,
name|o
operator|.
name|digest
argument_list|)
operator|&&
name|pos
operator|==
name|o
operator|.
name|pos
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|digest
argument_list|)
operator|^
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
literal|32
argument_list|)
operator|^
operator|(
name|int
operator|)
name|pos
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
operator|+
literal|"@"
operator|+
name|pos
return|;
block|}
block|}
comment|/**      * The data for a block.      */
specifier|public
specifier|static
class|class
name|Data
implements|implements
name|Cache
operator|.
name|Value
block|{
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
name|Data
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|data
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|length
argument_list|()
operator|>
literal|100
condition|?
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
operator|+
literal|".. (len="
operator|+
name|data
operator|.
name|length
operator|+
literal|")"
else|:
name|s
return|;
block|}
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
name|data
operator|.
name|length
return|;
block|}
block|}
block|}
end_class

end_unit

