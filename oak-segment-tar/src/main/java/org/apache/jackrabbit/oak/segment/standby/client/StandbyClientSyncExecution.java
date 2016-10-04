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
name|client
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
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|segment
operator|.
name|RecordId
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
name|Segment
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
name|SegmentId
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
name|SegmentNodeBuilder
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
name|SegmentNodeState
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
name|SegmentNotFoundException
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
name|FileStore
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
comment|/**  * Encapsulates the algorithm for a single execution of the synchronization  * process between the primary and the standby instance. It also contains  * temporary state that is supposed to be used for the lifetime of a  * synchronization run.  */
end_comment

begin_class
class|class
name|StandbyClientSyncExecution
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
name|StandbyClientSyncExecution
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|StandbyClient
name|client
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|running
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|visited
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|queued
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|local
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
name|cache
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|StandbyClientSyncExecution
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|StandbyClient
name|client
parameter_list|,
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|running
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|running
operator|=
name|running
expr_stmt|;
block|}
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|RecordId
name|remoteHead
init|=
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteHead
operator|.
name|equals
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|before
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|current
init|=
name|newSegmentNodeState
argument_list|(
name|remoteHead
argument_list|)
decl_stmt|;
name|compareAgainstBaseState
argument_list|(
name|current
argument_list|,
name|before
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|boolean
name|ok
init|=
name|setHead
argument_list|(
name|before
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"updated head state successfully: {} in {}ms."
argument_list|,
name|ok
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RecordId
name|getHead
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|RecordId
operator|.
name|fromString
argument_list|(
name|store
argument_list|,
name|client
operator|.
name|getHead
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|SegmentNodeState
name|newSegmentNodeState
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
return|return
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|setHead
parameter_list|(
annotation|@
name|Nonnull
name|SegmentNodeState
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|SegmentNodeState
name|head
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|expected
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|SegmentNodeState
name|current
parameter_list|,
name|SegmentNodeState
name|before
parameter_list|,
name|SegmentNodeBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|current
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|StandbyDiff
argument_list|(
name|builder
argument_list|,
name|store
argument_list|,
name|client
argument_list|,
name|running
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Found missing segment {}"
argument_list|,
name|e
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
name|copySegmentHierarchyFromPrimary
argument_list|(
name|UUID
operator|.
name|fromString
argument_list|(
name|e
operator|.
name|getSegmentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|copySegmentHierarchyFromPrimary
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|UUID
argument_list|>
name|batch
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|batch
operator|.
name|offer
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
while|while
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|UUID
name|current
init|=
name|batch
operator|.
name|remove
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Loading segment {}"
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|copySegmentFromPrimary
argument_list|(
name|current
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Marking segment {} as loaded"
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|visited
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|SegmentId
operator|.
name|isDataSegmentId
argument_list|(
name|current
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Inspecting segment {} for references"
argument_list|,
name|current
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segment
operator|.
name|getReferencedSegmentIdCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|UUID
name|referenced
init|=
name|segment
operator|.
name|getReferencedSegmentId
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Short circuit for the "back reference problem". The segment
comment|// graph might or might not be acyclic. The following check
comment|// prevents processing segment that were already traversed.
if|if
condition|(
name|visited
operator|.
name|contains
argument_list|(
name|referenced
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Short circuit for the "diamond problem". Imagine that segment S1
comment|// references S2 and S3 and both S2 and S3 reference S4. These
comment|// references form the shape of a diamond. If the segments are
comment|// processed in the order S1, S2, S3, then S4 is added twice to the
comment|// 'batch' queue. The following check prevents processing S4 twice
comment|// or more.
if|if
condition|(
name|queued
operator|.
name|contains
argument_list|(
name|referenced
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Short circuit for the "sharing-is-caring problem". If many
comment|// new segments are sharing segments that are already locally
comment|// available, we should not issue a request for it to the
comment|// server.
if|if
condition|(
name|local
operator|.
name|contains
argument_list|(
name|referenced
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|isLocal
argument_list|(
name|referenced
argument_list|)
condition|)
block|{
name|local
operator|.
name|add
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// If we arrive at this point, the referenced segment is 1) not
comment|// present locally, 2) not already queued for retrieval and 3)
comment|// never visited before. We can safely add the reference to the
comment|// queue and transfer the segment later.
name|log
operator|.
name|debug
argument_list|(
literal|"Found reference from {} to {}"
argument_list|,
name|current
argument_list|,
name|referenced
argument_list|)
expr_stmt|;
if|if
condition|(
name|SegmentId
operator|.
name|isDataSegmentId
argument_list|(
name|referenced
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
condition|)
block|{
name|batch
operator|.
name|add
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|batch
operator|.
name|addFirst
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
block|}
name|queued
operator|.
name|add
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|isLocal
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
name|SegmentId
name|referencedId
init|=
name|store
operator|.
name|newSegmentId
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|persisted
init|=
literal|true
decl_stmt|;
try|try
block|{
name|referencedId
operator|.
name|getSegment
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|persisted
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|persisted
return|;
block|}
specifier|private
name|Segment
name|copySegmentFromPrimary
parameter_list|(
name|UUID
name|uuid
parameter_list|)
throws|throws
name|Exception
block|{
name|Segment
name|result
init|=
name|cache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Segment {} was found in the local cache"
argument_list|,
name|uuid
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
name|byte
index|[]
name|data
init|=
name|client
operator|.
name|getSegment
argument_list|(
name|uuid
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to read segment "
operator|+
name|uuid
argument_list|)
throw|;
block|}
name|long
name|msb
init|=
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|SegmentId
name|segmentId
init|=
name|store
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
decl_stmt|;
name|store
operator|.
name|writeSegment
argument_list|(
name|segmentId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|segmentId
operator|.
name|getSegment
argument_list|()
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

