begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|checkState
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
name|BINARIES
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
name|memory
operator|.
name|BinaryPropertyState
operator|.
name|binaryProperty
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|MultiBinaryPropertyState
operator|.
name|binaryPropertyFromBlob
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|nio
operator|.
name|ByteBuffer
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|memory
operator|.
name|MemoryNodeBuilder
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
name|GCNodeWriteMonitor
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
name|spi
operator|.
name|state
operator|.
name|NodeStateDiff
import|;
end_import

begin_comment
comment|// FIXME OAK-6399 unify with Compactor (progress tracker? eager-flush, content based binary deduplication, unit tests, etc...)
end_comment

begin_comment
comment|/**  * Instances of this class can be used to compact a node state. I.e. to create a clone  * of a given node state without value sharing except for binaries. Binaries that are  * stored in a list of bulk segments will still value share the bulk segments (but not  * the list records).  * A node can either be compacted on its own or alternatively the difference between  * two nodes can be compacted on top of an already compacted node.  */
end_comment

begin_class
specifier|public
class|class
name|OnlineCompactor
block|{
comment|/**      * Number of content updates that need to happen before the updates      * are automatically purged to the underlying segments.      */
specifier|static
specifier|final
name|int
name|UPDATE_LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"compaction.update.limit"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nullable
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|cancel
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|GCNodeWriteMonitor
name|compactionMonitor
decl_stmt|;
comment|/**      * Create a new instance based on the passed arguments.      * @param reader     segment reader used to read from the segments      * @param writer     segment writer used to serialise to segments      * @param blobStore  the blob store or {@code null} if none      * @param cancel     a flag that can be used to cancel the compaction process      * @param compactionMonitor   notification call back for each compacted nodes,      *                            properties, and binaries      */
specifier|public
name|OnlineCompactor
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|SegmentWriter
name|writer
parameter_list|,
annotation|@
name|Nullable
name|BlobStore
name|blobStore
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|cancel
parameter_list|,
annotation|@
name|Nonnull
name|GCNodeWriteMonitor
name|compactionMonitor
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|checkNotNull
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|cancel
operator|=
name|checkNotNull
argument_list|(
name|cancel
argument_list|)
expr_stmt|;
name|this
operator|.
name|compactionMonitor
operator|=
name|checkNotNull
argument_list|(
name|compactionMonitor
argument_list|)
expr_stmt|;
block|}
comment|/**      * Compact a given {@code state}      * @param state  the node state to compact      * @return       the compacted node state or {@code null} if cancelled.      * @throws IOException      */
annotation|@
name|CheckForNull
specifier|public
name|SegmentNodeState
name|compact
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compact
argument_list|(
name|EMPTY_NODE
argument_list|,
name|state
argument_list|,
name|EMPTY_NODE
argument_list|)
return|;
block|}
comment|/**      * compact the differences between {@code after} and {@code before} on top of {@code ont}.      * @param before   the node state to diff against from {@code after}      * @param after    the node state diffed against {@code before}      * @param onto     the node state compacted onto      * @return         the compacted node state or {@code null} if cancelled.      * @throws IOException      */
annotation|@
name|CheckForNull
specifier|public
name|SegmentNodeState
name|compact
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|onto
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|onto
argument_list|)
expr_stmt|;
return|return
operator|new
name|CompactDiff
argument_list|(
name|onto
argument_list|)
operator|.
name|diff
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|ByteBuffer
name|getStableIdBytes
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentNodeState
operator|)
name|state
operator|)
operator|.
name|getStableIdBytes
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
class|class
name|CompactDiff
implements|implements
name|NodeStateDiff
block|{
annotation|@
name|Nonnull
specifier|private
name|MemoryNodeBuilder
name|builder
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
annotation|@
name|CheckForNull
specifier|private
name|IOException
name|exception
decl_stmt|;
specifier|private
name|long
name|modCount
decl_stmt|;
specifier|private
name|void
name|updated
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|modCount
operator|%
name|UPDATE_LIMIT
operator|==
literal|0
condition|)
block|{
name|RecordId
name|newBaseId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|newBase
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|,
name|blobStore
argument_list|,
name|newBaseId
argument_list|)
decl_stmt|;
name|builder
operator|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|newBase
argument_list|)
expr_stmt|;
block|}
block|}
name|CompactDiff
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|checkNotNull
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|CheckForNull
name|SegmentNodeState
name|diff
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|CancelableDiff
argument_list|(
name|this
argument_list|,
name|cancel
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|success
condition|)
block|{
name|NodeState
name|nodeState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
name|modCount
operator|==
literal|0
operator|||
operator|!
operator|(
name|nodeState
operator|instanceof
name|SegmentNodeState
operator|)
argument_list|)
expr_stmt|;
name|RecordId
name|nodeId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|nodeState
argument_list|,
name|getStableIdBytes
argument_list|(
name|after
argument_list|)
argument_list|)
decl_stmt|;
name|compactionMonitor
operator|.
name|onNode
argument_list|()
expr_stmt|;
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|,
name|blobStore
argument_list|,
name|nodeId
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
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|after
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|compact
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|after
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|compact
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
block|{
try|try
block|{
name|SegmentNodeState
name|compacted
init|=
name|compact
argument_list|(
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|compacted
operator|!=
literal|null
condition|)
block|{
name|updated
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
block|{
try|try
block|{
name|SegmentNodeState
name|compacted
init|=
name|compact
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|compacted
operator|!=
literal|null
condition|)
block|{
name|updated
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
try|try
block|{
name|updated
argument_list|()
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|PropertyState
name|compact
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
name|compactionMonitor
operator|.
name|onProperty
argument_list|()
expr_stmt|;
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|compactionMonitor
operator|.
name|onBinary
argument_list|()
expr_stmt|;
return|return
name|binaryProperty
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARIES
condition|)
block|{
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Blob
name|blob
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|compactionMonitor
operator|.
name|onBinary
argument_list|()
expr_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|blob
argument_list|)
expr_stmt|;
block|}
return|return
name|binaryPropertyFromBlob
argument_list|(
name|name
argument_list|,
name|blobs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

