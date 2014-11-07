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
name|plugins
operator|.
name|segment
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
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
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
name|Sets
operator|.
name|newIdentityHashSet
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
name|emptySet
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
name|segment
operator|.
name|Segment
operator|.
name|MEDIUM_LIMIT
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
name|segment
operator|.
name|Segment
operator|.
name|SMALL_LIMIT
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
name|segment
operator|.
name|SegmentWriter
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_comment
comment|/**  * A BLOB (stream of bytes). This is a record of type "VALUE".  */
end_comment

begin_class
specifier|public
class|class
name|SegmentBlob
extends|extends
name|Record
implements|implements
name|Blob
block|{
specifier|public
specifier|static
name|Iterable
argument_list|<
name|SegmentId
argument_list|>
name|getBulkSegmentIds
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
if|if
condition|(
name|blob
operator|instanceof
name|SegmentBlob
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentBlob
operator|)
name|blob
operator|)
operator|.
name|getBulkSegmentIds
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|emptySet
argument_list|()
return|;
block|}
block|}
name|SegmentBlob
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|private
name|InputStream
name|getInlineStream
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|byte
index|[]
name|inline
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|offset
argument_list|,
name|inline
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|inline
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0x80
operator|)
operator|==
literal|0x00
condition|)
block|{
comment|// 0xxx xxxx: small value
return|return
name|getInlineStream
argument_list|(
name|segment
argument_list|,
name|offset
operator|+
literal|1
argument_list|,
name|head
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xc0
operator|)
operator|==
literal|0x80
condition|)
block|{
comment|// 10xx xxxx: medium value
name|int
name|length
init|=
operator|(
name|segment
operator|.
name|readShort
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x3fff
operator|)
operator|+
name|SMALL_LIMIT
decl_stmt|;
return|return
name|getInlineStream
argument_list|(
name|segment
argument_list|,
name|offset
operator|+
literal|2
argument_list|,
name|length
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xe0
operator|)
operator|==
literal|0xc0
condition|)
block|{
comment|// 110x xxxx: long value
name|long
name|length
init|=
operator|(
name|segment
operator|.
name|readLong
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x1fffffffffffffffL
operator|)
operator|+
name|MEDIUM_LIMIT
decl_stmt|;
name|int
name|listSize
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|ListRecord
name|list
init|=
operator|new
name|ListRecord
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
argument_list|,
name|listSize
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|list
argument_list|,
name|length
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
condition|)
block|{
comment|// 1110 xxxx: external value
name|String
name|refererence
init|=
name|readReference
argument_list|(
name|segment
argument_list|,
name|offset
argument_list|,
name|head
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getTracker
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|readBlob
argument_list|(
name|refererence
argument_list|)
operator|.
name|getNewStream
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected value record type: %02x"
argument_list|,
name|head
operator|&
literal|0xff
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0x80
operator|)
operator|==
literal|0x00
condition|)
block|{
comment|// 0xxx xxxx: small value
return|return
name|head
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xc0
operator|)
operator|==
literal|0x80
condition|)
block|{
comment|// 10xx xxxx: medium value
return|return
operator|(
name|segment
operator|.
name|readShort
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x3fff
operator|)
operator|+
name|SMALL_LIMIT
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xe0
operator|)
operator|==
literal|0xc0
condition|)
block|{
comment|// 110x xxxx: long value
return|return
operator|(
name|segment
operator|.
name|readLong
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x1fffffffffffffffL
operator|)
operator|+
name|MEDIUM_LIMIT
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
condition|)
block|{
comment|// 1110 xxxx: external value
name|String
name|reference
init|=
name|readReference
argument_list|(
name|segment
argument_list|,
name|offset
argument_list|,
name|head
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|segment
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getTracker
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|readBlob
argument_list|(
name|reference
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown length of external binary: "
operator|+
name|reference
argument_list|)
throw|;
block|}
return|return
name|length
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected value record type: %02x"
argument_list|,
name|head
operator|&
literal|0xff
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getReference
parameter_list|()
block|{
name|String
name|blobId
init|=
name|getBlobId
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobId
operator|!=
literal|null
condition|)
block|{
name|BlobStore
name|blobStore
init|=
name|getSegment
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getTracker
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
return|return
name|blobStore
operator|.
name|getReference
argument_list|(
name|blobId
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Attempt to read external blob with blobId ["
operator|+
name|blobId
operator|+
literal|"] "
operator|+
literal|"without specifying BlobStore"
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|getRecordId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
comment|// 1110 xxxx: external value
return|return
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
return|;
block|}
specifier|public
name|String
name|getBlobId
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
condition|)
block|{
comment|// 1110 xxxx: external value
return|return
name|readReference
argument_list|(
name|segment
argument_list|,
name|offset
argument_list|,
name|head
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|SegmentBlob
name|clone
parameter_list|(
name|SegmentWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0x80
operator|)
operator|==
literal|0x00
condition|)
block|{
comment|// 0xxx xxxx: small value
return|return
name|writer
operator|.
name|writeStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|getNewStream
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xc0
operator|)
operator|==
literal|0x80
condition|)
block|{
comment|// 10xx xxxx: medium value
return|return
name|writer
operator|.
name|writeStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|getNewStream
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xe0
operator|)
operator|==
literal|0xc0
condition|)
block|{
comment|// 110x xxxx: long value
name|long
name|length
init|=
operator|(
name|segment
operator|.
name|readLong
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x1fffffffffffffffL
operator|)
operator|+
name|MEDIUM_LIMIT
decl_stmt|;
name|int
name|listSize
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|ListRecord
name|list
init|=
operator|new
name|ListRecord
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
argument_list|,
name|listSize
argument_list|)
decl_stmt|;
return|return
name|writer
operator|.
name|writeLargeBlob
argument_list|(
name|length
argument_list|,
name|list
operator|.
name|getEntries
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
condition|)
block|{
comment|// 1110 xxxx: external value
return|return
name|writer
operator|.
name|writeExternalBlob
argument_list|(
name|getBlobId
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected value record type: %02x"
argument_list|,
name|head
operator|&
literal|0xff
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
name|this
operator|||
name|fastEquals
argument_list|(
name|this
argument_list|,
name|object
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|SegmentBlob
condition|)
block|{
name|SegmentBlob
name|that
init|=
operator|(
name|SegmentBlob
operator|)
name|object
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|wasCompactedTo
argument_list|(
name|that
argument_list|)
operator|||
name|that
operator|.
name|wasCompactedTo
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|object
operator|instanceof
name|Blob
operator|&&
name|AbstractBlob
operator|.
name|equal
argument_list|(
name|this
argument_list|,
operator|(
name|Blob
operator|)
name|object
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|private
specifier|static
name|String
name|readReference
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|offset
parameter_list|,
name|byte
name|head
parameter_list|)
block|{
name|int
name|length
init|=
operator|(
name|head
operator|&
literal|0x0f
operator|)
operator|<<
literal|8
operator||
operator|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|+
literal|1
argument_list|)
operator|&
literal|0xff
operator|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|offset
operator|+
literal|2
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|SegmentId
argument_list|>
name|getBulkSegmentIds
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0xe0
operator|)
operator|==
literal|0xc0
condition|)
block|{
comment|// 110x xxxx: long value
name|long
name|length
init|=
operator|(
name|segment
operator|.
name|readLong
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x1fffffffffffffffL
operator|)
operator|+
name|MEDIUM_LIMIT
decl_stmt|;
name|int
name|listSize
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|ListRecord
name|list
init|=
operator|new
name|ListRecord
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
argument_list|,
name|listSize
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
name|newIdentityHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|RecordId
name|id
range|:
name|list
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
else|else
block|{
return|return
name|emptySet
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

