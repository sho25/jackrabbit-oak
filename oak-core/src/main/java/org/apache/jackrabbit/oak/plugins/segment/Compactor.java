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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|Maps
operator|.
name|newHashMap
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|ArrayList
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
name|Map
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
name|Predicate
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
name|Predicates
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
name|commons
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
name|plugins
operator|.
name|memory
operator|.
name|BinaryPropertyState
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
name|EmptyNodeState
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
name|MultiBinaryPropertyState
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
name|PropertyStates
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
name|compaction
operator|.
name|CompactionStrategy
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
name|state
operator|.
name|ApplyDiff
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

begin_comment
comment|/**  * Tool for compacting segments.  */
end_comment

begin_class
specifier|public
class|class
name|Compactor
block|{
comment|/** Logger instance */
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
name|Compactor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Locks down the RecordId persistence structure      */
specifier|static
name|long
index|[]
name|recordAsKey
parameter_list|(
name|RecordId
name|r
parameter_list|)
block|{
return|return
operator|new
name|long
index|[]
block|{
name|r
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getMostSignificantBits
argument_list|()
block|,
name|r
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getLeastSignificantBits
argument_list|()
block|,
name|r
operator|.
name|getOffset
argument_list|()
block|}
return|;
block|}
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
specifier|final
name|PartialCompactionMap
name|map
decl_stmt|;
comment|/**      * Filters nodes that will be included in the compaction map, allowing for      * optimization in case of an offline compaction      */
specifier|private
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|includeInMap
init|=
name|Predicates
operator|.
name|alwaysTrue
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ProgressTracker
name|progress
init|=
operator|new
name|ProgressTracker
argument_list|()
decl_stmt|;
comment|/**      * Map from {@link #getBlobKey(Blob) blob keys} to matching compacted      * blob record identifiers. Used to de-duplicate copies of the same      * binary values.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RecordId
argument_list|>
argument_list|>
name|binaries
init|=
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**      * If the compactor should copy large binaries as streams or just copy the      * refs      */
specifier|private
specifier|final
name|boolean
name|cloneBinaries
decl_stmt|;
specifier|public
name|Compactor
parameter_list|(
name|SegmentWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|map
operator|=
operator|new
name|InMemoryCompactionMap
argument_list|(
name|writer
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cloneBinaries
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|Compactor
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|CompactionStrategy
name|compactionStrategy
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|store
operator|.
name|createSegmentWriter
argument_list|()
expr_stmt|;
if|if
condition|(
name|compactionStrategy
operator|.
name|getPersistCompactionMap
argument_list|()
condition|)
block|{
name|this
operator|.
name|map
operator|=
operator|new
name|PersistedCompactionMap
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|map
operator|=
operator|new
name|InMemoryCompactionMap
argument_list|(
name|writer
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|cloneBinaries
operator|=
name|compactionStrategy
operator|.
name|cloneBinaries
argument_list|()
expr_stmt|;
if|if
condition|(
name|compactionStrategy
operator|.
name|isOfflineCompaction
argument_list|()
condition|)
block|{
name|includeInMap
operator|=
operator|new
name|OfflineCompactionPredicate
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|SegmentNodeBuilder
name|process
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeState
name|onto
parameter_list|)
block|{
name|SegmentNodeBuilder
name|builder
init|=
operator|new
name|SegmentNodeBuilder
argument_list|(
name|writer
operator|.
name|writeNode
argument_list|(
name|onto
argument_list|)
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|CompactDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * Compact the differences between a {@code before} and a {@code after}      * on top of the {@code before} state.      *<p>      * Equivalent to {@code compact(before, after, before)}      *      * @param before  the before state      * @param after   the after state      * @return  the compacted state      */
specifier|public
name|SegmentNodeState
name|compact
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|progress
operator|.
name|start
argument_list|()
expr_stmt|;
name|SegmentNodeState
name|compacted
init|=
name|process
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|before
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|progress
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|compacted
return|;
block|}
comment|/**      * Compact the differences between a {@code before} and a {@code after}      * on top of an {@code onto} state.      * @param before  the before state      * @param after   the after state      * @param onto    the onto state      * @return  the compacted state      */
specifier|public
name|SegmentNodeState
name|compact
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeState
name|onto
parameter_list|)
block|{
name|progress
operator|.
name|start
argument_list|()
expr_stmt|;
name|SegmentNodeState
name|compacted
init|=
name|process
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|onto
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|progress
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|compacted
return|;
block|}
specifier|public
name|PartialCompactionMap
name|getCompactionMap
parameter_list|()
block|{
name|map
operator|.
name|compress
argument_list|()
expr_stmt|;
return|return
name|map
return|;
block|}
specifier|private
class|class
name|CompactDiff
extends|extends
name|ApplyDiff
block|{
comment|/**          * Current processed path, or null if the trace log is not enabled at          * the beginning of the compaction call. The null check will also be          * used to verify if a trace log will be needed or not          */
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
name|CompactDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|CompactDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|path
operator|=
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"propertyAdded {}/{}"
argument_list|,
name|path
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|progress
operator|.
name|onProperty
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|propertyAdded
argument_list|(
name|compact
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"propertyChanged {}/{}"
argument_list|,
name|path
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|progress
operator|.
name|onProperty
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|compact
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"childNodeAdded {}/{}"
argument_list|,
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|progress
operator|.
name|onNode
argument_list|()
expr_stmt|;
name|RecordId
name|id
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|after
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|id
operator|=
operator|(
operator|(
name|SegmentNodeState
operator|)
name|after
operator|)
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
name|RecordId
name|compactedId
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|compactedId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
operator|new
name|SegmentNodeState
argument_list|(
name|compactedId
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|NodeBuilder
name|child
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
name|EmptyNodeState
operator|.
name|compareAgainstEmptyState
argument_list|(
name|after
argument_list|,
operator|new
name|CompactDiff
argument_list|(
name|child
argument_list|,
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|SegmentNodeState
name|state
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|includeInMap
operator|.
name|apply
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|state
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"childNodeChanged {}/{}"
argument_list|,
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|progress
operator|.
name|onNode
argument_list|()
expr_stmt|;
name|RecordId
name|id
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|after
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|id
operator|=
operator|(
operator|(
name|SegmentNodeState
operator|)
name|after
operator|)
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
name|RecordId
name|compactedId
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|compactedId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
operator|new
name|SegmentNodeState
argument_list|(
name|compactedId
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
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
name|CompactDiff
argument_list|(
name|child
argument_list|,
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|RecordId
name|compactedId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|compactedId
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|success
return|;
block|}
block|}
specifier|private
name|PropertyState
name|compact
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
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
name|Blob
name|blob
init|=
name|compact
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
name|name
argument_list|,
name|blob
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
operator|new
name|ArrayList
argument_list|<
name|Blob
argument_list|>
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
name|blobs
operator|.
name|add
argument_list|(
name|compact
argument_list|(
name|blob
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiBinaryPropertyState
operator|.
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
name|Object
name|value
init|=
name|property
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
comment|/**      * Compacts (and de-duplicates) the given blob.      *      * @param blob blob to be compacted      * @return compacted blob      */
specifier|private
name|Blob
name|compact
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
name|SegmentBlob
name|sb
init|=
operator|(
name|SegmentBlob
operator|)
name|blob
decl_stmt|;
name|progress
operator|.
name|onBinary
argument_list|()
expr_stmt|;
try|try
block|{
comment|// else check if we've already cloned this specific record
name|RecordId
name|id
init|=
name|sb
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|RecordId
name|compactedId
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|compactedId
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SegmentBlob
argument_list|(
name|compactedId
argument_list|)
return|;
block|}
comment|// if the blob is inlined or external, just clone it
if|if
condition|(
name|sb
operator|.
name|isExternal
argument_list|()
operator|||
name|sb
operator|.
name|length
argument_list|()
operator|<
name|Segment
operator|.
name|MEDIUM_LIMIT
condition|)
block|{
name|SegmentBlob
name|clone
init|=
name|sb
operator|.
name|clone
argument_list|(
name|writer
argument_list|,
name|cloneBinaries
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|clone
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|// alternatively look if the exact same binary has been cloned
name|String
name|key
init|=
name|getBlobKey
argument_list|(
name|blob
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RecordId
argument_list|>
name|ids
init|=
name|binaries
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|RecordId
name|duplicateId
range|:
name|ids
control|)
block|{
if|if
condition|(
operator|new
name|SegmentBlob
argument_list|(
name|duplicateId
argument_list|)
operator|.
name|equals
argument_list|(
name|sb
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|duplicateId
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentBlob
argument_list|(
name|duplicateId
argument_list|)
return|;
block|}
block|}
block|}
comment|// if not, clone the blob and keep track of the result
name|sb
operator|=
name|sb
operator|.
name|clone
argument_list|(
name|writer
argument_list|,
name|cloneBinaries
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|sb
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|binaries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|sb
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to compact a blob"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// fall through
block|}
block|}
comment|// no way to compact this blob, so we'll just keep it as-is
return|return
name|blob
return|;
block|}
specifier|private
specifier|static
name|String
name|getBlobKey
parameter_list|(
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|SegmentWriter
operator|.
name|BLOCK_SIZE
index|]
decl_stmt|;
name|int
name|n
init|=
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
return|return
name|blob
operator|.
name|length
argument_list|()
operator|+
literal|":"
operator|+
name|Hashing
operator|.
name|sha1
argument_list|()
operator|.
name|hashBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|ProgressTracker
block|{
specifier|private
specifier|final
name|long
name|logAt
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"compaction-progress-log"
argument_list|,
literal|150000
argument_list|)
decl_stmt|;
specifier|private
name|long
name|start
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|nodes
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|properties
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|binaries
init|=
literal|0
decl_stmt|;
name|void
name|start
parameter_list|()
block|{
name|nodes
operator|=
literal|0
expr_stmt|;
name|properties
operator|=
literal|0
expr_stmt|;
name|binaries
operator|=
literal|0
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
name|void
name|onNode
parameter_list|()
block|{
if|if
condition|(
operator|++
name|nodes
operator|%
name|logAt
operator|==
literal|0
condition|)
block|{
name|logProgress
argument_list|(
name|start
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|onProperty
parameter_list|()
block|{
name|properties
operator|++
expr_stmt|;
block|}
name|void
name|onBinary
parameter_list|()
block|{
name|binaries
operator|++
expr_stmt|;
block|}
name|void
name|stop
parameter_list|()
block|{
name|logProgress
argument_list|(
name|start
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|logProgress
parameter_list|(
name|long
name|start
parameter_list|,
name|boolean
name|done
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Compacted {} nodes, {} properties, {} binaries in {} ms."
argument_list|,
name|nodes
argument_list|,
name|properties
argument_list|,
name|binaries
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|done
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Finished compaction: {} nodes, {} properties, {} binaries."
argument_list|,
name|nodes
argument_list|,
name|properties
argument_list|,
name|binaries
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|OfflineCompactionPredicate
implements|implements
name|Predicate
argument_list|<
name|NodeState
argument_list|>
block|{
comment|/**          * over 64K in size, node will be included in the compaction map          */
specifier|private
specifier|static
specifier|final
name|long
name|offlineThreshold
init|=
literal|65536
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|getChildNodeCount
argument_list|(
literal|2
argument_list|)
operator|>
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ps
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|long
name|size
init|=
name|ps
operator|.
name|size
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|count
operator|+=
name|size
expr_stmt|;
if|if
condition|(
name|size
operator|>=
name|offlineThreshold
operator|||
name|count
operator|>=
name|offlineThreshold
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

