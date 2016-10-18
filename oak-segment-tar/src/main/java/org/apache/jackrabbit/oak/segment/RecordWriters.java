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
name|Preconditions
operator|.
name|checkArgument
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
name|newArrayListWithCapacity
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
name|sort
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
name|singleton
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
name|MapRecord
operator|.
name|SIZE_BITS
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
name|RecordType
operator|.
name|BLOB_ID
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
name|RecordType
operator|.
name|BLOCK
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
name|RecordType
operator|.
name|BRANCH
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
name|RecordType
operator|.
name|BUCKET
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
name|RecordType
operator|.
name|LEAF
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
name|RecordType
operator|.
name|LIST
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
name|RecordType
operator|.
name|NODE
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
name|RecordType
operator|.
name|TEMPLATE
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
name|RecordType
operator|.
name|VALUE
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
name|RECORD_ID_BYTES
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
import|;
end_import

begin_class
specifier|final
class|class
name|RecordWriters
block|{
specifier|private
name|RecordWriters
parameter_list|()
block|{}
comment|/**      * Base class for all record writers      */
specifier|public
specifier|abstract
specifier|static
class|class
name|RecordWriter
block|{
specifier|private
specifier|final
name|RecordType
name|type
decl_stmt|;
specifier|protected
specifier|final
name|int
name|size
decl_stmt|;
specifier|protected
specifier|final
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
decl_stmt|;
specifier|protected
name|RecordWriter
parameter_list|(
name|RecordType
name|type
parameter_list|,
name|int
name|size
parameter_list|,
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|ids
operator|=
name|ids
expr_stmt|;
block|}
specifier|protected
name|RecordWriter
parameter_list|(
name|RecordType
name|type
parameter_list|,
name|int
name|size
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|size
argument_list|,
name|singleton
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|RecordWriter
parameter_list|(
name|RecordType
name|type
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|size
argument_list|,
name|Collections
operator|.
expr|<
name|RecordId
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|RecordId
name|write
parameter_list|(
name|SegmentBufferWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|RecordId
name|id
init|=
name|writer
operator|.
name|prepare
argument_list|(
name|type
argument_list|,
name|size
argument_list|,
name|ids
argument_list|)
decl_stmt|;
return|return
name|writeRecordContent
argument_list|(
name|id
argument_list|,
name|writer
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newMapLeafWriter
parameter_list|(
name|int
name|level
parameter_list|,
name|Collection
argument_list|<
name|MapEntry
argument_list|>
name|entries
parameter_list|)
block|{
return|return
operator|new
name|MapLeafWriter
argument_list|(
name|level
argument_list|,
name|entries
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newMapLeafWriter
parameter_list|()
block|{
return|return
operator|new
name|MapLeafWriter
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newMapBranchWriter
parameter_list|(
name|int
name|level
parameter_list|,
name|int
name|entryCount
parameter_list|,
name|int
name|bitmap
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
return|return
operator|new
name|MapBranchWriter
argument_list|(
name|level
argument_list|,
name|entryCount
argument_list|,
name|bitmap
argument_list|,
name|ids
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newMapBranchWriter
parameter_list|(
name|int
name|bitmap
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
return|return
operator|new
name|MapBranchWriter
argument_list|(
name|bitmap
argument_list|,
name|ids
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newListWriter
parameter_list|(
name|int
name|count
parameter_list|,
name|RecordId
name|lid
parameter_list|)
block|{
return|return
operator|new
name|ListWriter
argument_list|(
name|count
argument_list|,
name|lid
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newListWriter
parameter_list|()
block|{
return|return
operator|new
name|ListWriter
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newListBucketWriter
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
return|return
operator|new
name|ListBucketWriter
argument_list|(
name|ids
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newBlockWriter
parameter_list|(
name|byte
index|[]
name|bytes
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
name|BlockWriter
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newValueWriter
parameter_list|(
name|RecordId
name|rid
parameter_list|,
name|long
name|len
parameter_list|)
block|{
return|return
operator|new
name|SingleValueWriter
argument_list|(
name|rid
argument_list|,
name|len
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newValueWriter
parameter_list|(
name|int
name|length
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
operator|new
name|ArrayValueWriter
argument_list|(
name|length
argument_list|,
name|data
argument_list|)
return|;
block|}
comment|/**      * Write a large blob ID. A blob ID is considered large if the length of its      * binary representation is equal to or greater than {@code      * Segment.BLOB_ID_SMALL_LIMIT}.      */
specifier|public
specifier|static
name|RecordWriter
name|newBlobIdWriter
parameter_list|(
name|RecordId
name|rid
parameter_list|)
block|{
return|return
operator|new
name|LargeBlobIdWriter
argument_list|(
name|rid
argument_list|)
return|;
block|}
comment|/**      * Write a small blob ID. A blob ID is considered small if the length of its      * binary representation is less than {@code Segment.BLOB_ID_SMALL_LIMIT}.      */
specifier|public
specifier|static
name|RecordWriter
name|newBlobIdWriter
parameter_list|(
name|byte
index|[]
name|blobId
parameter_list|)
block|{
return|return
operator|new
name|SmallBlobIdWriter
argument_list|(
name|blobId
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newTemplateWriter
parameter_list|(
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|,
name|RecordId
index|[]
name|propertyNames
parameter_list|,
name|byte
index|[]
name|propertyTypes
parameter_list|,
name|int
name|head
parameter_list|,
name|RecordId
name|primaryId
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|mixinIds
parameter_list|,
name|RecordId
name|childNameId
parameter_list|,
name|RecordId
name|propNamesId
parameter_list|)
block|{
return|return
operator|new
name|TemplateWriter
argument_list|(
name|ids
argument_list|,
name|propertyNames
argument_list|,
name|propertyTypes
argument_list|,
name|head
argument_list|,
name|primaryId
argument_list|,
name|mixinIds
argument_list|,
name|childNameId
argument_list|,
name|propNamesId
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RecordWriter
name|newNodeStateWriter
parameter_list|(
name|RecordId
name|stableId
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
return|return
operator|new
name|NodeStateWriter
argument_list|(
name|stableId
argument_list|,
name|ids
argument_list|)
return|;
block|}
comment|/**      * Map Leaf record writer.      * @see RecordType#LEAF      */
specifier|private
specifier|static
class|class
name|MapLeafWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|int
name|level
decl_stmt|;
specifier|private
specifier|final
name|Collection
argument_list|<
name|MapEntry
argument_list|>
name|entries
decl_stmt|;
specifier|private
name|MapLeafWriter
parameter_list|()
block|{
name|super
argument_list|(
name|LEAF
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|this
operator|.
name|level
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|entries
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|MapLeafWriter
parameter_list|(
name|int
name|level
parameter_list|,
name|Collection
argument_list|<
name|MapEntry
argument_list|>
name|entries
parameter_list|)
block|{
name|super
argument_list|(
name|LEAF
argument_list|,
literal|4
operator|+
name|entries
operator|.
name|size
argument_list|()
operator|*
literal|4
argument_list|,
name|extractIds
argument_list|(
name|entries
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|RecordId
argument_list|>
name|extractIds
parameter_list|(
name|Collection
argument_list|<
name|MapEntry
argument_list|>
name|entries
parameter_list|)
block|{
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
init|=
name|newArrayListWithCapacity
argument_list|(
literal|2
operator|*
name|entries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|entries
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|entries
operator|!=
literal|null
condition|)
block|{
name|int
name|size
init|=
name|entries
operator|.
name|size
argument_list|()
decl_stmt|;
name|writer
operator|.
name|writeInt
argument_list|(
operator|(
name|level
operator|<<
name|SIZE_BITS
operator|)
operator||
name|size
argument_list|)
expr_stmt|;
comment|// copy the entries to an array so we can sort them before
comment|// writing
name|MapEntry
index|[]
name|array
init|=
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|MapEntry
index|[
name|size
index|]
argument_list|)
decl_stmt|;
name|sort
argument_list|(
name|array
argument_list|)
expr_stmt|;
for|for
control|(
name|MapEntry
name|entry
range|:
name|array
control|)
block|{
name|writer
operator|.
name|writeInt
argument_list|(
name|entry
operator|.
name|getHash
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|MapEntry
name|entry
range|:
name|array
control|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeRecordId
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|writer
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
comment|/**      * Map Branch record writer.      * @see RecordType#BRANCH      */
specifier|private
specifier|static
class|class
name|MapBranchWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|int
name|level
decl_stmt|;
specifier|private
specifier|final
name|int
name|entryCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|bitmap
decl_stmt|;
comment|/*          * Write a regular map branch          */
specifier|private
name|MapBranchWriter
parameter_list|(
name|int
name|level
parameter_list|,
name|int
name|entryCount
parameter_list|,
name|int
name|bitmap
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|super
argument_list|(
name|BRANCH
argument_list|,
literal|8
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|entryCount
operator|=
name|entryCount
expr_stmt|;
name|this
operator|.
name|bitmap
operator|=
name|bitmap
expr_stmt|;
block|}
comment|/*          * Write a diff map          */
specifier|private
name|MapBranchWriter
parameter_list|(
name|int
name|bitmap
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
comment|// level = 0 and and entryCount = -1 -> this is a map diff
name|this
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
name|bitmap
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
comment|// -1 to encode a map diff (if level == 0 and entryCount == -1)
name|writer
operator|.
name|writeInt
argument_list|(
operator|(
name|level
operator|<<
name|SIZE_BITS
operator|)
operator||
name|entryCount
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeInt
argument_list|(
name|bitmap
argument_list|)
expr_stmt|;
for|for
control|(
name|RecordId
name|mapId
range|:
name|ids
control|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|mapId
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
comment|/**      * List record writer.      * @see RecordType#LIST      */
specifier|private
specifier|static
class|class
name|ListWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|lid
decl_stmt|;
specifier|private
name|ListWriter
parameter_list|()
block|{
name|super
argument_list|(
name|LIST
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|lid
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|ListWriter
parameter_list|(
name|int
name|count
parameter_list|,
name|RecordId
name|lid
parameter_list|)
block|{
name|super
argument_list|(
name|LIST
argument_list|,
literal|4
argument_list|,
name|lid
argument_list|)
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|lid
operator|=
name|lid
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|lid
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|lid
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
comment|/**      * List Bucket record writer.      *      * @see RecordType#BUCKET      */
specifier|private
specifier|static
class|class
name|ListBucketWriter
extends|extends
name|RecordWriter
block|{
specifier|private
name|ListBucketWriter
parameter_list|(
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|super
argument_list|(
name|BUCKET
argument_list|,
literal|0
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
for|for
control|(
name|RecordId
name|bucketId
range|:
name|ids
control|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|bucketId
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
comment|/**      * Block record writer.      * @see SegmentWriter#writeBlock      * @see RecordType#BLOCK      */
specifier|private
specifier|static
class|class
name|BlockWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
specifier|private
name|BlockWriter
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|BLOCK
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
comment|/**      * Single RecordId record writer.      * @see RecordType#VALUE      */
specifier|private
specifier|static
class|class
name|SingleValueWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|RecordId
name|rid
decl_stmt|;
specifier|private
specifier|final
name|long
name|len
decl_stmt|;
specifier|private
name|SingleValueWriter
parameter_list|(
name|RecordId
name|rid
parameter_list|,
name|long
name|len
parameter_list|)
block|{
name|super
argument_list|(
name|VALUE
argument_list|,
literal|8
argument_list|,
name|rid
argument_list|)
expr_stmt|;
name|this
operator|.
name|rid
operator|=
name|rid
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|writeLong
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeRecordId
argument_list|(
name|rid
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
comment|/**      * Bye array record writer. Used as a special case for short binaries (up to      * about {@code Segment#MEDIUM_LIMIT}): store them directly as small or      * medium-sized value records.      * @see Segment#MEDIUM_LIMIT      * @see RecordType#VALUE      */
specifier|private
specifier|static
class|class
name|ArrayValueWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|ArrayValueWriter
parameter_list|(
name|int
name|length
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|VALUE
argument_list|,
name|length
operator|+
name|getSizeDelta
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isSmallSize
parameter_list|(
name|int
name|length
parameter_list|)
block|{
return|return
name|length
operator|<
name|SMALL_LIMIT
return|;
block|}
specifier|private
specifier|static
name|int
name|getSizeDelta
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|isSmallSize
argument_list|(
name|length
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|2
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|isSmallSize
argument_list|(
name|length
argument_list|)
condition|)
block|{
name|writer
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|writeShort
argument_list|(
call|(
name|short
call|)
argument_list|(
operator|(
name|length
operator|-
name|SMALL_LIMIT
operator|)
operator||
literal|0x8000
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
comment|/**      * Large Blob record writer. A blob ID is considered large if the length of      * its binary representation is equal to or greater than      * {@code Segment#BLOB_ID_SMALL_LIMIT}.      *      * @see Segment#BLOB_ID_SMALL_LIMIT      * @see RecordType#BLOB_ID      */
specifier|private
specifier|static
class|class
name|LargeBlobIdWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|RecordId
name|stringRecord
decl_stmt|;
specifier|private
name|LargeBlobIdWriter
parameter_list|(
name|RecordId
name|stringRecord
parameter_list|)
block|{
name|super
argument_list|(
name|BLOB_ID
argument_list|,
literal|1
argument_list|,
name|stringRecord
argument_list|)
expr_stmt|;
name|this
operator|.
name|stringRecord
operator|=
name|stringRecord
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
comment|// The length uses a fake "length" field that is always equal to
comment|// 0xF0.
comment|// This allows the code to take apart small from a large blob IDs.
name|writer
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0xF0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeRecordId
argument_list|(
name|stringRecord
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
comment|/**      * Small Blob record writer. A blob ID is considered small if the length of      * its binary representation is less than {@code Segment#BLOB_ID_SMALL_LIMIT}.       * @see Segment#BLOB_ID_SMALL_LIMIT      * @see RecordType#BLOB_ID      */
specifier|private
specifier|static
class|class
name|SmallBlobIdWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|byte
index|[]
name|blobId
decl_stmt|;
specifier|private
name|SmallBlobIdWriter
parameter_list|(
name|byte
index|[]
name|blobId
parameter_list|)
block|{
name|super
argument_list|(
name|BLOB_ID
argument_list|,
literal|2
operator|+
name|blobId
operator|.
name|length
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|blobId
operator|.
name|length
operator|<
name|Segment
operator|.
name|BLOB_ID_SMALL_LIMIT
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobId
operator|=
name|blobId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|int
name|length
init|=
name|blobId
operator|.
name|length
decl_stmt|;
name|writer
operator|.
name|writeShort
argument_list|(
call|(
name|short
call|)
argument_list|(
name|length
operator||
literal|0xE000
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeBytes
argument_list|(
name|blobId
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
comment|/**      * Template record writer.      * @see RecordType#TEMPLATE      */
specifier|private
specifier|static
class|class
name|TemplateWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|RecordId
index|[]
name|propertyNames
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|propertyTypes
decl_stmt|;
specifier|private
specifier|final
name|int
name|head
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|primaryId
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RecordId
argument_list|>
name|mixinIds
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|childNameId
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|propNamesId
decl_stmt|;
specifier|private
name|TemplateWriter
parameter_list|(
name|Collection
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|,
name|RecordId
index|[]
name|propertyNames
parameter_list|,
name|byte
index|[]
name|propertyTypes
parameter_list|,
name|int
name|head
parameter_list|,
name|RecordId
name|primaryId
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|mixinIds
parameter_list|,
name|RecordId
name|childNameId
parameter_list|,
name|RecordId
name|propNamesId
parameter_list|)
block|{
name|super
argument_list|(
name|TEMPLATE
argument_list|,
literal|4
operator|+
name|propertyTypes
operator|.
name|length
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|this
operator|.
name|propertyNames
operator|=
name|propertyNames
expr_stmt|;
name|this
operator|.
name|propertyTypes
operator|=
name|propertyTypes
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
name|this
operator|.
name|primaryId
operator|=
name|primaryId
expr_stmt|;
name|this
operator|.
name|mixinIds
operator|=
name|mixinIds
expr_stmt|;
name|this
operator|.
name|childNameId
operator|=
name|childNameId
expr_stmt|;
name|this
operator|.
name|propNamesId
operator|=
name|propNamesId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|writeInt
argument_list|(
name|head
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryId
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|primaryId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mixinIds
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|RecordId
name|mixinId
range|:
name|mixinIds
control|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|mixinId
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|childNameId
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|childNameId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propNamesId
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|propNamesId
argument_list|)
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
name|propertyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|writeByte
argument_list|(
name|propertyTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
comment|/**      * Node State record writer.      * @see RecordType#NODE      */
specifier|private
specifier|static
class|class
name|NodeStateWriter
extends|extends
name|RecordWriter
block|{
specifier|private
specifier|final
name|RecordId
name|stableId
decl_stmt|;
specifier|private
name|NodeStateWriter
parameter_list|(
name|RecordId
name|stableId
parameter_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
parameter_list|)
block|{
name|super
argument_list|(
name|NODE
argument_list|,
name|RECORD_ID_BYTES
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|this
operator|.
name|stableId
operator|=
name|stableId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RecordId
name|writeRecordContent
parameter_list|(
name|RecordId
name|id
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
comment|// Write the stable record ID. If no stable ID exists (in case of a
comment|// new node state), it is generated from the current record ID. In
comment|// this case, the generated stable ID is only a marker and is not a
comment|// reference to another record.
if|if
condition|(
name|stableId
operator|==
literal|null
condition|)
block|{
comment|// Write this node's record id to indicate that the stable id is not
comment|// explicitly stored.
name|writer
operator|.
name|writeRecordId
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|stableId
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RecordId
name|recordId
range|:
name|ids
control|)
block|{
name|writer
operator|.
name|writeRecordId
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
block|}
block|}
end_class

end_unit

