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
operator|.
name|file
package|;
end_package

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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|GCType
operator|.
name|TAIL
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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|GCType
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
name|tar
operator|.
name|GCGeneration
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

begin_class
class|class
name|TailCompactionStrategy
extends|extends
name|AbstractCompactionStrategy
block|{
annotation|@
name|Override
name|GCType
name|getCompactionType
parameter_list|()
block|{
return|return
name|TAIL
return|;
block|}
annotation|@
name|Override
name|GCGeneration
name|nextGeneration
parameter_list|(
name|GCGeneration
name|current
parameter_list|)
block|{
return|return
name|current
operator|.
name|nextTail
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompactionResult
name|compact
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|NodeState
name|base
init|=
name|getBase
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|info
argument_list|(
literal|"no base state available, tail compaction is not applicable"
argument_list|)
expr_stmt|;
return|return
name|CompactionResult
operator|.
name|notApplicable
argument_list|(
name|context
operator|.
name|getGCCount
argument_list|()
argument_list|)
return|;
block|}
return|return
name|compact
argument_list|(
name|context
argument_list|,
name|base
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|getBase
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|RecordId
name|id
init|=
name|getLastCompactedRootId
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|RecordId
operator|.
name|NULL
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Nodes are read lazily. In order to force a read operation for the requested
comment|// node, the property count is computed. Computing the property count requires
comment|// access to the node template, whose ID is stored in the content of the node.
comment|// Accessing the content of the node forces a read operation for the segment
comment|// containing the node. If the following code completes without throwing a
comment|// SNFE, we can be sure that *at least* the root node can be accessed. This
comment|// doesn't say anything about the health of the full closure of the head
comment|// state.
try|try
block|{
name|NodeState
name|node
init|=
name|getLastCompactedRootNode
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|node
operator|.
name|getPropertyCount
argument_list|()
expr_stmt|;
return|return
name|node
return|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|error
argument_list|(
literal|"base state "
operator|+
name|id
operator|+
literal|" is not accessible"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getLastCompactedRoot
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getGCJournal
argument_list|()
operator|.
name|read
argument_list|()
operator|.
name|getRoot
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|RecordId
name|getLastCompactedRootId
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|RecordId
operator|.
name|fromString
argument_list|(
name|context
operator|.
name|getSegmentTracker
argument_list|()
argument_list|,
name|getLastCompactedRoot
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|getLastCompactedRootNode
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getSegmentReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|getLastCompactedRootId
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
