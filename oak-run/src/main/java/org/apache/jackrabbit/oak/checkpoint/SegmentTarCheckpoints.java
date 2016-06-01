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
name|checkpoint
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
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
name|collect
operator|.
name|Lists
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
name|io
operator|.
name|Closer
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

begin_class
class|class
name|SegmentTarCheckpoints
extends|extends
name|Checkpoints
block|{
specifier|static
name|Checkpoints
name|create
parameter_list|(
name|File
name|path
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTarCheckpoints
argument_list|(
name|closer
operator|.
name|register
argument_list|(
name|FileStore
operator|.
name|builder
argument_list|(
name|path
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
specifier|private
name|SegmentTarCheckpoints
parameter_list|(
name|FileStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CP
argument_list|>
name|list
parameter_list|()
block|{
name|List
argument_list|<
name|CP
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|NodeState
name|ns
init|=
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|ns
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|cneNs
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|CP
argument_list|(
name|cne
operator|.
name|getName
argument_list|()
argument_list|,
name|cneNs
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
argument_list|,
name|cneNs
operator|.
name|getLong
argument_list|(
literal|"timestamp"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|removeAll
parameter_list|()
block|{
name|SegmentNodeState
name|head
init|=
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cps
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
decl_stmt|;
name|long
name|cnt
init|=
name|cps
operator|.
name|getChildNodeCount
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|asSegmentNodeState
argument_list|(
name|builder
argument_list|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|cnt
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|removeUnreferenced
parameter_list|()
block|{
name|SegmentNodeState
name|head
init|=
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
decl_stmt|;
name|String
name|ref
init|=
name|getReferenceCheckpoint
argument_list|(
name|head
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cps
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
decl_stmt|;
name|long
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|cps
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|equals
argument_list|(
name|ref
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|cps
operator|.
name|getChildNode
argument_list|(
name|c
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|asSegmentNodeState
argument_list|(
name|builder
argument_list|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|cnt
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|remove
parameter_list|(
name|String
name|cp
parameter_list|)
block|{
name|SegmentNodeState
name|head
init|=
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|head
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cpn
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|cp
argument_list|)
decl_stmt|;
if|if
condition|(
name|cpn
operator|.
name|exists
argument_list|()
condition|)
block|{
name|cpn
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|head
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|asSegmentNodeState
argument_list|(
name|builder
argument_list|)
operator|.
name|getRecordId
argument_list|()
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
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
specifier|private
specifier|static
name|SegmentNodeState
name|asSegmentNodeState
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
operator|(
name|SegmentNodeState
operator|)
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

