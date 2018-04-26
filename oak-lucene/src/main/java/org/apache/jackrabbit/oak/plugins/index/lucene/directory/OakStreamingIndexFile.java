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
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|commons
operator|.
name|StringUtils
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
name|lucene
operator|.
name|store
operator|.
name|DataInput
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|io
operator|.
name|SequenceInputStream
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Preconditions
operator|.
name|checkPositionIndexes
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
name|JcrConstants
operator|.
name|JCR_DATA
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
name|JcrConstants
operator|.
name|JCR_LASTMODIFIED
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
name|api
operator|.
name|Type
operator|.
name|BINARY
import|;
end_import

begin_comment
comment|/**  * A file which streams blob directly off of storage.  */
end_comment

begin_class
class|class
name|OakStreamingIndexFile
implements|implements
name|OakIndexFile
implements|,
name|AutoCloseable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OakStreamingIndexFile
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * The file name.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * The node that contains the blob for this file.      */
specifier|private
specifier|final
name|NodeBuilder
name|file
decl_stmt|;
comment|/**      * The current position within the file (in streaming case, useful only for reading).      */
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
comment|/**      * The length of the file.      */
specifier|private
name|long
name|length
decl_stmt|;
comment|/**      * The blob which has been read for reading case.      * For writing case, it contains the blob that's pushed to repository      */
specifier|private
name|Blob
name|blob
decl_stmt|;
comment|/**      * Whether the blob was modified since it was last flushed. If yes, on a      * flush the metadata and the blob to the store.      */
specifier|private
name|boolean
name|blobModified
init|=
literal|false
decl_stmt|;
comment|/**      * The {@link InputStream} to read blob from blob.      */
specifier|private
name|InputStream
name|blobInputStream
decl_stmt|;
comment|/**      * The unique key that is used to make the content unique (to allow removing binaries from the blob store without      * risking to remove binaries that are still needed).      */
specifier|private
specifier|final
name|byte
index|[]
name|uniqueKey
decl_stmt|;
specifier|private
specifier|final
name|String
name|dirDetails
decl_stmt|;
specifier|private
specifier|final
name|BlobFactory
name|blobFactory
decl_stmt|;
name|OakStreamingIndexFile
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeBuilder
name|file
parameter_list|,
name|String
name|dirDetails
parameter_list|,
annotation|@
name|Nonnull
name|BlobFactory
name|blobFactory
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
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|dirDetails
operator|=
name|dirDetails
expr_stmt|;
name|this
operator|.
name|uniqueKey
operator|=
name|readUniqueKey
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobFactory
operator|=
name|checkNotNull
argument_list|(
name|blobFactory
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
name|file
operator|.
name|getProperty
argument_list|(
name|JCR_DATA
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
operator|==
name|BINARY
condition|)
block|{
name|this
operator|.
name|blob
operator|=
name|property
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't load blob for streaming for "
operator|+
name|name
operator|+
literal|" under "
operator|+
name|file
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|blob
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|length
operator|=
name|blob
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|uniqueKey
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|length
operator|-=
name|uniqueKey
operator|.
name|length
expr_stmt|;
block|}
block|}
name|this
operator|.
name|blobInputStream
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|OakStreamingIndexFile
parameter_list|(
name|OakStreamingIndexFile
name|that
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|that
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|that
operator|.
name|file
expr_stmt|;
name|this
operator|.
name|dirDetails
operator|=
name|that
operator|.
name|dirDetails
expr_stmt|;
name|this
operator|.
name|uniqueKey
operator|=
name|that
operator|.
name|uniqueKey
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|that
operator|.
name|position
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|that
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|blob
operator|=
name|that
operator|.
name|blob
expr_stmt|;
name|this
operator|.
name|blobModified
operator|=
name|that
operator|.
name|blobModified
expr_stmt|;
name|this
operator|.
name|blobFactory
operator|=
name|that
operator|.
name|blobFactory
expr_stmt|;
block|}
specifier|private
name|void
name|setupInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobInputStream
operator|==
literal|null
condition|)
block|{
name|blobInputStream
operator|=
name|blob
operator|.
name|getNewStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|position
operator|>
literal|0
condition|)
block|{
name|long
name|pos
init|=
name|position
decl_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|releaseInputStream
parameter_list|()
block|{
if|if
condition|(
name|blobInputStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blobInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
comment|//ignore
block|}
name|blobInputStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|OakIndexFile
name|clone
parameter_list|()
block|{
return|return
operator|new
name|OakStreamingIndexFile
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|position
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|blobInputStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|blob
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|blobInputStream
operator|==
literal|null
operator|&&
name|blob
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// seek() may be called with pos == length
comment|// see https://issues.apache.org/jira/browse/LUCENE-1196
if|if
condition|(
name|pos
argument_list|<
literal|0
operator|||
name|pos
argument_list|>
name|length
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid seek request for [%s][%s], "
operator|+
literal|"position: %d, file length: %d"
argument_list|,
name|dirDetails
argument_list|,
name|name
argument_list|,
name|pos
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|releaseInputStream
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|blobInputStream
operator|==
literal|null
condition|)
block|{
name|position
operator|=
name|pos
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|<
name|position
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Seeking back on streaming index file {}. Current position {}, requested position {}. "
operator|+
literal|"Please make sure that CopyOnRead and prefetch of index files are enabled."
argument_list|,
name|getName
argument_list|()
argument_list|,
name|position
argument_list|()
argument_list|,
name|pos
argument_list|)
expr_stmt|;
comment|// seeking back on input stream. Close current one
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|blobInputStream
argument_list|)
expr_stmt|;
name|blobInputStream
operator|=
literal|null
expr_stmt|;
name|position
operator|=
name|pos
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|position
operator|<
name|pos
condition|)
block|{
name|long
name|skipCnt
init|=
name|blobInputStream
operator|.
name|skip
argument_list|(
name|pos
operator|-
name|position
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipCnt
operator|<=
literal|0
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Seek request for [%s][%s], "
operator|+
literal|"position: %d, file length: %d failed. InputStream.skip returned %d"
argument_list|,
name|dirDetails
argument_list|,
name|name
argument_list|,
name|pos
argument_list|,
name|length
argument_list|,
name|skipCnt
argument_list|)
decl_stmt|;
name|releaseInputStream
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|position
operator|+=
name|skipCnt
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|len
argument_list|,
name|checkNotNull
argument_list|(
name|b
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
argument_list|<
literal|0
operator|||
name|position
operator|+
name|len
argument_list|>
name|length
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid byte range request for [%s][%s], "
operator|+
literal|"position: %d, file length: %d, len: %d"
argument_list|,
name|dirDetails
argument_list|,
name|name
argument_list|,
name|position
argument_list|,
name|length
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|releaseInputStream
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|setupInputStream
argument_list|()
expr_stmt|;
name|int
name|readCnt
init|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|blobInputStream
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|readCnt
operator|<
name|len
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Couldn't read byte range request for [%s][%s], "
operator|+
literal|"position: %d, file length: %d, len: %d. Actual read bytes %d"
argument_list|,
name|dirDetails
argument_list|,
name|name
argument_list|,
name|position
argument_list|,
name|length
argument_list|,
name|len
argument_list|,
name|readCnt
argument_list|)
decl_stmt|;
name|releaseInputStream
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|position
operator|+=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobModified
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't do piece wise upload with streaming access"
argument_list|)
throw|;
block|}
name|InputStream
name|in
init|=
operator|new
name|InputStream
argument_list|()
block|{
name|int
name|position
init|=
name|offset
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|offset
operator|+
name|len
operator|-
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
name|int
name|ret
init|=
name|b
index|[
name|position
operator|++
index|]
decl_stmt|;
return|return
name|ret
operator|<
literal|0
condition|?
literal|256
operator|+
name|ret
else|:
name|ret
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|target
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|read
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|len
argument_list|,
name|available
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|position
argument_list|,
name|target
argument_list|,
name|off
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|position
operator|+=
name|read
expr_stmt|;
return|return
name|read
return|;
block|}
block|}
decl_stmt|;
name|pushData
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsCopyFromDataInput
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyBytes
parameter_list|(
name|DataInput
name|input
parameter_list|,
specifier|final
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
operator|new
name|InputStream
argument_list|()
block|{
name|long
name|bytesLeftToRead
init|=
name|numBytes
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytesLeftToRead
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
name|bytesLeftToRead
operator|--
expr_stmt|;
name|int
name|ret
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
return|return
name|ret
operator|<
literal|0
condition|?
literal|256
operator|+
name|ret
else|:
name|ret
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytesLeftToRead
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|read
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|len
argument_list|,
name|bytesLeftToRead
argument_list|)
decl_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|bytesLeftToRead
operator|-=
name|read
expr_stmt|;
return|return
name|read
return|;
block|}
block|}
decl_stmt|;
name|pushData
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|pushData
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|uniqueKey
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
operator|new
name|SequenceInputStream
argument_list|(
name|in
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|uniqueKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|blob
operator|=
name|blobFactory
operator|.
name|createBlob
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blobModified
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|readUniqueKey
parameter_list|(
name|NodeBuilder
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|hasProperty
argument_list|(
name|OakDirectory
operator|.
name|PROP_UNIQUE_KEY
argument_list|)
condition|)
block|{
name|String
name|key
init|=
name|file
operator|.
name|getString
argument_list|(
name|OakDirectory
operator|.
name|PROP_UNIQUE_KEY
argument_list|)
decl_stmt|;
return|return
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobModified
condition|)
block|{
name|file
operator|.
name|setProperty
argument_list|(
name|JCR_LASTMODIFIED
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|setProperty
argument_list|(
name|JCR_DATA
argument_list|,
name|blob
argument_list|,
name|BINARY
argument_list|)
expr_stmt|;
name|blobModified
operator|=
literal|false
expr_stmt|;
block|}
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
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

