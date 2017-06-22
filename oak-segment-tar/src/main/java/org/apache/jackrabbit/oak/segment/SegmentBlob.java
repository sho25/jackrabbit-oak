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
name|newHashSet
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
name|segment
operator|.
name|DefaultSegmentWriter
operator|.
name|BLOCK_SIZE
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
name|segment
operator|.
name|Segment
operator|.
name|SMALL_LIMIT
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|BlobStoreBlob
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
annotation|@
name|CheckForNull
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
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
annotation|@
name|Nullable
name|BlobStore
name|blobStore
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
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
return|return
operator|new
name|SegmentStream
argument_list|(
name|getRecordId
argument_list|()
argument_list|,
name|segment
operator|.
name|readBytes
argument_list|(
name|getRecordNumber
argument_list|()
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|,
name|length
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
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
argument_list|,
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
comment|// 1110 xxxx: external value, short blob ID
return|return
name|getNewStream
argument_list|(
name|readShortBlobId
argument_list|(
name|segment
argument_list|,
name|getRecordNumber
argument_list|()
argument_list|,
name|head
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
literal|0xf8
operator|)
operator|==
literal|0xf0
condition|)
block|{
comment|// 1111 0xxx: external value, long blob ID
return|return
name|getNewStream
argument_list|(
name|readLongBlobId
argument_list|(
name|segment
argument_list|,
name|getRecordNumber
argument_list|()
argument_list|)
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
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
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
comment|// 1110 xxxx: external value, short blob ID
return|return
name|getLength
argument_list|(
name|readShortBlobId
argument_list|(
name|segment
argument_list|,
name|getRecordNumber
argument_list|()
argument_list|,
name|head
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
literal|0xf8
operator|)
operator|==
literal|0xf0
condition|)
block|{
comment|// 1111 0xxx: external value, long blob ID
return|return
name|getLength
argument_list|(
name|readLongBlobId
argument_list|(
name|segment
argument_list|,
name|getRecordNumber
argument_list|()
argument_list|)
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
return|return
name|blobId
return|;
block|}
return|return
literal|null
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
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|getRecordNumber
argument_list|()
argument_list|)
decl_stmt|;
comment|// 1110 xxxx or 1111 0xxx: external value
return|return
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
operator|||
operator|(
name|head
operator|&
literal|0xf8
operator|)
operator|==
literal|0xf0
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getBlobId
parameter_list|()
block|{
return|return
name|readBlobId
argument_list|(
name|getSegment
argument_list|()
argument_list|,
name|getRecordNumber
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|readBlobId
parameter_list|(
annotation|@
name|Nonnull
name|Segment
name|segment
parameter_list|,
name|int
name|recordNumber
parameter_list|)
block|{
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|recordNumber
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
comment|// 1110 xxxx: external value, small blob ID
return|return
name|readShortBlobId
argument_list|(
name|segment
argument_list|,
name|recordNumber
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
literal|0xf8
operator|)
operator|==
literal|0xf0
condition|)
block|{
comment|// 1111 0xxx: external value, long blob ID
return|return
name|readLongBlobId
argument_list|(
name|segment
argument_list|,
name|recordNumber
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
name|Record
operator|.
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
name|length
argument_list|()
operator|!=
name|that
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|List
argument_list|<
name|RecordId
argument_list|>
name|bulkIds
init|=
name|this
operator|.
name|getBulkRecordIds
argument_list|()
decl_stmt|;
if|if
condition|(
name|bulkIds
operator|!=
literal|null
operator|&&
name|bulkIds
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getBulkRecordIds
argument_list|()
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
name|readShortBlobId
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|recordNumber
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
name|recordNumber
argument_list|,
literal|1
argument_list|)
operator|&
literal|0xff
operator|)
decl_stmt|;
return|return
name|UTF_8
operator|.
name|decode
argument_list|(
name|segment
operator|.
name|readBytes
argument_list|(
name|recordNumber
argument_list|,
literal|2
argument_list|,
name|length
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|readLongBlobId
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|recordNumber
parameter_list|)
block|{
name|RecordId
name|blobId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordNumber
argument_list|,
literal|1
argument_list|)
decl_stmt|;
return|return
name|blobId
operator|.
name|getSegment
argument_list|()
operator|.
name|readString
argument_list|(
name|blobId
operator|.
name|getRecordNumber
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RecordId
argument_list|>
name|getBulkRecordIds
parameter_list|()
block|{
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
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
name|getRecordNumber
argument_list|()
argument_list|,
literal|8
argument_list|)
argument_list|,
name|listSize
argument_list|)
decl_stmt|;
return|return
name|list
operator|.
name|getEntries
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|SegmentId
argument_list|>
name|getBulkSegmentIds
parameter_list|()
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|recordIds
init|=
name|getBulkRecordIds
argument_list|()
decl_stmt|;
if|if
condition|(
name|recordIds
operator|==
literal|null
condition|)
block|{
return|return
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|RecordId
name|id
range|:
name|recordIds
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
block|}
specifier|private
name|Blob
name|getBlob
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|BlobStoreBlob
argument_list|(
name|blobStore
argument_list|,
name|blobId
argument_list|)
return|;
block|}
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
specifier|private
name|InputStream
name|getNewStream
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
return|return
name|getBlob
argument_list|(
name|blobId
argument_list|)
operator|.
name|getNewStream
argument_list|()
return|;
block|}
specifier|private
name|long
name|getLength
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
name|long
name|length
init|=
name|getBlob
argument_list|(
name|blobId
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
name|String
operator|.
name|format
argument_list|(
literal|"Unknown length of external binary: %s"
argument_list|,
name|blobId
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|length
return|;
block|}
block|}
end_class

end_unit

