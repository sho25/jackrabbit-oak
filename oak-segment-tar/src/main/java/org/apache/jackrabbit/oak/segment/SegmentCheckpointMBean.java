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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
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
name|commons
operator|.
name|jmx
operator|.
name|AbstractCheckpointMBean
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
comment|/**  * {@code CheckpointMBean} implementation for the {@code SegmentNodeStore}.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentCheckpointMBean
extends|extends
name|AbstractCheckpointMBean
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|store
decl_stmt|;
specifier|public
name|SegmentCheckpointMBean
parameter_list|(
name|SegmentNodeStore
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
specifier|protected
name|void
name|collectCheckpoints
parameter_list|(
name|TabularDataSupport
name|tab
parameter_list|)
throws|throws
name|OpenDataException
block|{
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|cne
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|checkpoint
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|String
name|created
init|=
name|getDate
argument_list|(
name|checkpoint
argument_list|,
literal|"created"
argument_list|)
decl_stmt|;
name|String
name|expires
init|=
name|getDate
argument_list|(
name|checkpoint
argument_list|,
literal|"timestamp"
argument_list|)
decl_stmt|;
name|tab
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|toCompositeData
argument_list|(
name|id
argument_list|,
name|created
argument_list|,
name|expires
argument_list|,
name|store
operator|.
name|checkpointInfo
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getOldestCheckpointCreationTimestamp
parameter_list|()
block|{
name|long
name|minTimestamp
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|checkpoint
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|p
init|=
name|checkpoint
operator|.
name|getProperty
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|minTimestamp
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minTimestamp
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|minTimestamp
operator|==
name|Long
operator|.
name|MAX_VALUE
operator|)
condition|?
literal|0
else|:
name|minTimestamp
return|;
block|}
specifier|private
specifier|static
name|String
name|getDate
parameter_list|(
name|NodeState
name|checkpoint
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|p
init|=
name|checkpoint
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|"NA"
return|;
block|}
return|return
operator|new
name|Date
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|createCheckpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created checkpoint [{}] with lifetime {}"
argument_list|,
name|cp
argument_list|,
name|lifetime
argument_list|)
expr_stmt|;
return|return
name|cp
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|releaseCheckpoint
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Released checkpoint [{}]"
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|release
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
block|}
end_class

end_unit

