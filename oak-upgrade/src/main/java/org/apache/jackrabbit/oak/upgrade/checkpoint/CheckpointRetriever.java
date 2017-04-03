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
name|upgrade
operator|.
name|checkpoint
package|;
end_package

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
name|Function
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
name|Iterables
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
name|document
operator|.
name|DocumentCheckpointRetriever
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
name|document
operator|.
name|DocumentNodeStore
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
name|NodeStore
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
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|TarNodeStore
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
specifier|public
specifier|final
class|class
name|CheckpointRetriever
block|{
specifier|public
specifier|static
class|class
name|Checkpoint
implements|implements
name|Comparable
argument_list|<
name|Checkpoint
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|long
name|created
decl_stmt|;
specifier|private
specifier|final
name|long
name|expiryTime
decl_stmt|;
specifier|public
name|Checkpoint
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|created
parameter_list|,
name|long
name|expiryTime
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
name|this
operator|.
name|expiryTime
operator|=
name|expiryTime
expr_stmt|;
block|}
specifier|public
specifier|static
name|Checkpoint
name|createFromSegmentNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
return|return
operator|new
name|Checkpoint
argument_list|(
name|name
argument_list|,
name|node
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
argument_list|,
name|node
operator|.
name|getLong
argument_list|(
literal|"timestamp"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|long
name|getExpiryTime
parameter_list|()
block|{
return|return
name|expiryTime
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Checkpoint
name|o
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|this
operator|.
name|created
argument_list|,
name|o
operator|.
name|created
argument_list|)
return|;
block|}
block|}
specifier|private
name|CheckpointRetriever
parameter_list|()
block|{     }
specifier|public
specifier|static
name|List
argument_list|<
name|Checkpoint
argument_list|>
name|getCheckpoints
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|List
argument_list|<
name|Checkpoint
argument_list|>
name|result
decl_stmt|;
if|if
condition|(
name|nodeStore
operator|instanceof
name|TarNodeStore
condition|)
block|{
name|result
operator|=
name|getCheckpoints
argument_list|(
operator|(
name|TarNodeStore
operator|)
name|nodeStore
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeStore
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
name|result
operator|=
name|DocumentCheckpointRetriever
operator|.
name|getCheckpoints
argument_list|(
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Checkpoint
argument_list|>
name|getCheckpoints
parameter_list|(
name|TarNodeStore
name|nodeStore
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|nodeStore
operator|.
name|getSuperRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|Checkpoint
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Checkpoint
name|apply
parameter_list|(
annotation|@
name|Nullable
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
name|Checkpoint
operator|.
name|createFromSegmentNode
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|,
name|input
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
