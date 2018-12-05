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
name|newLinkedHashMap
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
name|elements
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
name|getName
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
name|getParentPath
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|segment
operator|.
name|file
operator|.
name|cancel
operator|.
name|Canceller
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
name|spi
operator|.
name|persistence
operator|.
name|Buffer
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
name|gc
operator|.
name|GCMonitor
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
name|ChildNodeEntry
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * This compactor implementation is aware of the checkpoints in the repository.  * It uses this information to further optimise the compaction result by  *<ul>  *<li>Rebasing the checkpoints and subsequently the root on top of each other  *     in chronological order. This results minimises the deltas that need to be  *     processed and stored.</li>  *<li>Caching the compacted checkpoints and root states for deduplication should  *     the same checkpoint or root state occur again in a later compaction retry cycle.</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|CheckpointCompactor
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|GCMonitor
name|gcListener
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeState
argument_list|,
name|NodeState
argument_list|>
name|cpCache
init|=
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|Compactor
name|compactor
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|NodeWriter
name|nodeWriter
decl_stmt|;
specifier|private
interface|interface
name|NodeWriter
block|{
annotation|@
name|NotNull
name|SegmentNodeState
name|writeNode
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|node
parameter_list|,
annotation|@
name|Nullable
name|Buffer
name|stableId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**      * Create a new instance based on the passed arguments.      * @param reader     segment reader used to read from the segments      * @param writer     segment writer used to serialise to segments      * @param blobStore  the blob store or {@code null} if none      * @param compactionMonitor   notification call back for each compacted nodes,      *                            properties, and binaries      */
specifier|public
name|CheckpointCompactor
parameter_list|(
annotation|@
name|NotNull
name|GCMonitor
name|gcListener
parameter_list|,
annotation|@
name|NotNull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|NotNull
name|SegmentWriter
name|writer
parameter_list|,
annotation|@
name|Nullable
name|BlobStore
name|blobStore
parameter_list|,
annotation|@
name|NotNull
name|GCNodeWriteMonitor
name|compactionMonitor
parameter_list|)
block|{
name|this
operator|.
name|gcListener
operator|=
name|gcListener
expr_stmt|;
name|this
operator|.
name|compactor
operator|=
operator|new
name|Compactor
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|,
name|blobStore
argument_list|,
name|compactionMonitor
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeWriter
operator|=
parameter_list|(
name|node
parameter_list|,
name|stableId
parameter_list|)
lambda|->
block|{
name|RecordId
name|nodeId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|node
argument_list|,
name|stableId
argument_list|)
decl_stmt|;
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
expr_stmt|;
block|}
comment|/**      * Compact {@code uncompacted} on top of an optional {@code base}.      * @param base         the base state to compact against      * @param uncompacted  the uncompacted state to compact      * @param onto         the state onto which to compact the change between {@code base} and      *                     {@code uncompacted}      * @return  compacted clone of {@code uncompacted} or {@code null} if cancelled.      * @throws IOException      */
annotation|@
name|Nullable
specifier|public
name|SegmentNodeState
name|compact
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|base
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|uncompacted
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|onto
parameter_list|,
name|Canceller
name|canceller
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Collect a chronologically ordered list of roots for the uncompacted
comment|// state. This list consists of all checkpoints followed by the root.
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|uncompactedRoots
init|=
name|collectRoots
argument_list|(
name|uncompacted
argument_list|)
decl_stmt|;
comment|// Compact the list of uncompacted roots to a list of compacted roots.
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|compactedRoots
init|=
name|compact
argument_list|(
name|getRoot
argument_list|(
name|base
argument_list|)
argument_list|,
name|uncompactedRoots
argument_list|,
name|getRoot
argument_list|(
name|onto
argument_list|)
argument_list|,
name|canceller
argument_list|)
decl_stmt|;
if|if
condition|(
name|compactedRoots
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Build a compacted super root by replacing the uncompacted roots with
comment|// the compacted ones in the original node.
name|NodeBuilder
name|builder
init|=
name|uncompacted
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|compactedRoot
range|:
name|compactedRoots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|compactedRoot
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeState
name|state
init|=
name|compactedRoot
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childBuilder
init|=
name|getChild
argument_list|(
name|builder
argument_list|,
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|childBuilder
operator|.
name|setChildNode
argument_list|(
name|getName
argument_list|(
name|path
argument_list|)
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeWriter
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|getStableIdBytes
argument_list|(
name|uncompacted
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|private
specifier|static
name|Buffer
name|getStableIdBytes
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|node
parameter_list|)
block|{
return|return
name|node
operator|instanceof
name|SegmentNodeState
condition|?
operator|(
operator|(
name|SegmentNodeState
operator|)
name|node
operator|)
operator|.
name|getStableIdBytes
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|NodeState
name|getRoot
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|hasChildNode
argument_list|(
literal|"root"
argument_list|)
condition|?
name|node
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
else|:
name|EMPTY_NODE
return|;
block|}
comment|/**      * Compact a list of uncompacted roots on top of base roots of the same key or      * an empty node if none.      */
annotation|@
name|Nullable
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|compact
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|base
parameter_list|,
annotation|@
name|NotNull
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|uncompactedRoots
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|onto
parameter_list|,
name|Canceller
name|canceller
parameter_list|)
throws|throws
name|IOException
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|compactedRoots
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|uncompactedRoot
range|:
name|uncompactedRoots
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|uncompactedRoot
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeState
name|uncompacted
init|=
name|uncompactedRoot
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Result
name|result
init|=
name|compactWithCache
argument_list|(
name|base
argument_list|,
name|uncompacted
argument_list|,
name|onto
argument_list|,
name|path
argument_list|,
name|canceller
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|base
operator|=
name|result
operator|.
name|nextBefore
expr_stmt|;
name|onto
operator|=
name|result
operator|.
name|nextOnto
expr_stmt|;
name|compactedRoots
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|result
operator|.
name|compacted
argument_list|)
expr_stmt|;
block|}
return|return
name|compactedRoots
return|;
block|}
comment|/**      * Collect a chronologically ordered list of roots for the base and the uncompacted      * state from a {@code superRoot}. This list consists of all checkpoints followed by      * the root.      */
annotation|@
name|NotNull
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|collectRoots
parameter_list|(
annotation|@
name|Nullable
name|NodeState
name|superRoot
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|roots
init|=
name|newLinkedHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|superRoot
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|checkpoints
init|=
name|newArrayList
argument_list|(
name|superRoot
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|)
decl_stmt|;
name|checkpoints
operator|.
name|sort
argument_list|(
parameter_list|(
name|cne1
parameter_list|,
name|cne2
parameter_list|)
lambda|->
block|{
name|long
name|c1
init|=
name|cne1
operator|.
name|getNodeState
argument_list|()
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
name|long
name|c2
init|=
name|cne2
operator|.
name|getNodeState
argument_list|()
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|compare
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|checkpoint
range|:
name|checkpoints
control|)
block|{
name|String
name|name
init|=
name|checkpoint
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|node
init|=
name|checkpoint
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|gcListener
operator|.
name|info
argument_list|(
literal|"found checkpoint {} created at {}."
argument_list|,
name|name
argument_list|,
operator|new
name|Date
argument_list|(
name|node
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|roots
operator|.
name|put
argument_list|(
literal|"checkpoints/"
operator|+
name|name
operator|+
literal|"/root"
argument_list|,
name|node
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|roots
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
name|superRoot
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|roots
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|NodeBuilder
name|getChild
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
class|class
name|Result
block|{
specifier|final
name|NodeState
name|compacted
decl_stmt|;
specifier|final
name|NodeState
name|nextBefore
decl_stmt|;
specifier|final
name|NodeState
name|nextOnto
decl_stmt|;
name|Result
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|compacted
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nextBefore
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nextOnto
parameter_list|)
block|{
name|this
operator|.
name|compacted
operator|=
name|compacted
expr_stmt|;
name|this
operator|.
name|nextBefore
operator|=
name|nextBefore
expr_stmt|;
name|this
operator|.
name|nextOnto
operator|=
name|nextOnto
expr_stmt|;
block|}
block|}
comment|/**      * Compact {@code after} against {@code before} on top of {@code onto} unless      * {@code after} has been compacted before and is found in the cache. In this      * case the cached version of the previously compacted {@code before} is returned.      */
annotation|@
name|Nullable
specifier|private
name|Result
name|compactWithCache
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|before
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|after
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|onto
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
name|Canceller
name|canceller
parameter_list|)
throws|throws
name|IOException
block|{
name|gcListener
operator|.
name|info
argument_list|(
literal|"compacting {}."
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|NodeState
name|compacted
init|=
name|cpCache
operator|.
name|get
argument_list|(
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|compacted
operator|==
literal|null
condition|)
block|{
name|compacted
operator|=
name|compactor
operator|.
name|compact
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|onto
argument_list|,
name|canceller
argument_list|)
expr_stmt|;
if|if
condition|(
name|compacted
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|cpCache
operator|.
name|put
argument_list|(
name|after
argument_list|,
name|compacted
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|compacted
argument_list|,
name|after
argument_list|,
name|compacted
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|gcListener
operator|.
name|info
argument_list|(
literal|"found {} in cache."
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|compacted
argument_list|,
name|before
argument_list|,
name|onto
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

