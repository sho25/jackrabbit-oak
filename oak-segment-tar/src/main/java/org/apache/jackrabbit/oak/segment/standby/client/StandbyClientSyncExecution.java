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
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Stopwatch
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
name|SegmentIdProvider
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
name|SegmentIdProvider
name|idProvider
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|running
decl_stmt|;
name|StandbyClientSyncExecution
parameter_list|(
name|FileStore
name|store
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
name|idProvider
operator|=
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
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
parameter_list|(
name|StandbyClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|RecordId
name|remoteHead
init|=
name|getHead
argument_list|(
name|client
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteHead
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to fetch remote head"
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|Stopwatch
name|stopwatch
init|=
name|Stopwatch
operator|.
name|createStarted
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
name|client
argument_list|,
name|current
argument_list|,
name|before
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|before
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|remoteHead
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Updated head state in {}"
argument_list|,
name|stopwatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|private
name|RecordId
name|getHead
parameter_list|(
name|StandbyClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|head
init|=
name|client
operator|.
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|RecordId
operator|.
name|fromString
argument_list|(
name|idProvider
argument_list|,
name|head
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
name|void
name|compareAgainstBaseState
parameter_list|(
name|StandbyClient
name|client
parameter_list|,
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
expr_stmt|;
return|return;
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
name|client
argument_list|,
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
name|StandbyClient
name|client
parameter_list|,
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
name|LinkedList
argument_list|<
name|UUID
argument_list|>
name|bulk
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|UUID
argument_list|>
name|data
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|queued
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|local
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|batch
operator|.
name|isEmpty
argument_list|()
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
literal|"Inspecting segment {}"
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
comment|// Add the current segment ID at the beginning of the respective
comment|// list, depending on its type. This allows to process those
comment|// segments in an optimal topological order later on. If the current
comment|// segment is a bulk segment, we can skip the rest of the loop,
comment|// since bulk segments don't reference any other segment.
if|if
condition|(
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
name|data
operator|.
name|addFirst
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bulk
operator|.
name|addFirst
argument_list|(
name|current
argument_list|)
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|String
name|s
range|:
name|readReferences
argument_list|(
name|client
argument_list|,
name|current
argument_list|)
control|)
block|{
name|UUID
name|referenced
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|s
argument_list|)
decl_stmt|;
comment|// Short circuit for the "backward reference". The segment graph
comment|// is not guaranteed to be acyclic, so there might be segments
comment|// pointing back to a previously visited (but locally
comment|// unavailable) segment.
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
comment|// Short circuit for the "diamond problem". Imagine that segment
comment|// S1 references S2 and S3 and both S2 and S3 reference S4.
comment|// These references form the shape of a diamond. If the segments
comment|// are processed in the order S1, S2, S3, then S4 is added twice
comment|// to the 'batch' queue. The following check prevents processing
comment|// S4 twice or more.
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
comment|// server. Moreover, if a segment was visited and persisted
comment|// during this synchronization process, it will end up in the
comment|// 'local' set as well.
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
name|batch
operator|.
name|add
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
name|queued
operator|.
name|add
argument_list|(
name|referenced
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|UUID
name|id
range|:
name|bulk
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Copying bulk segment {} from primary"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|copySegmentFromPrimary
argument_list|(
name|client
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|UUID
name|id
range|:
name|data
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Copying data segment {} from primary"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|copySegmentFromPrimary
argument_list|(
name|client
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|readReferences
parameter_list|(
name|StandbyClient
name|client
parameter_list|,
name|UUID
name|id
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|references
init|=
name|client
operator|.
name|getReferences
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|references
operator|==
literal|null
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
literal|"Unable to read references of segment %s from primary"
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|references
return|;
block|}
specifier|private
name|boolean
name|isLocal
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
return|return
name|store
operator|.
name|containsSegment
argument_list|(
name|idProvider
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
argument_list|)
return|;
block|}
specifier|private
name|void
name|copySegmentFromPrimary
parameter_list|(
name|StandbyClient
name|client
parameter_list|,
name|UUID
name|uuid
parameter_list|)
throws|throws
name|Exception
block|{
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
name|idProvider
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
block|}
block|}
end_class

end_unit

