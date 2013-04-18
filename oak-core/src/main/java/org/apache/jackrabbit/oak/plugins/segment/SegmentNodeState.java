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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|spi
operator|.
name|state
operator|.
name|AbstractNodeState
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

begin_class
class|class
name|SegmentNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|recordId
decl_stmt|;
specifier|private
name|RecordId
name|templateId
init|=
literal|null
decl_stmt|;
specifier|private
name|Template
name|template
init|=
literal|null
decl_stmt|;
name|SegmentNodeState
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|recordId
operator|=
name|checkNotNull
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|RecordId
name|getRecordId
parameter_list|()
block|{
return|return
name|recordId
return|;
block|}
name|RecordId
name|getTemplateId
parameter_list|()
block|{
name|getTemplate
argument_list|()
expr_stmt|;
comment|// force loading of the template
return|return
name|templateId
return|;
block|}
specifier|synchronized
name|Template
name|getTemplate
parameter_list|()
block|{
if|if
condition|(
name|template
operator|==
literal|null
condition|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|templateId
operator|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|template
operator|=
name|segment
operator|.
name|readTemplate
argument_list|(
name|templateId
argument_list|)
expr_stmt|;
block|}
return|return
name|template
return|;
block|}
name|MapRecord
name|getChildNodeMap
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|getTemplate
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|getTemplate
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getProperties
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeCount
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|checkNotNull
argument_list|(
name|name
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getTemplate
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// checkArgument(!checkNotNull(name).isEmpty()); // TODO
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeNames
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|base
operator|==
name|this
condition|)
block|{
return|return;
comment|// no changes
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|EMPTY_NODE
condition|)
block|{
name|EmptyNodeState
operator|.
name|compareAgainstEmptyState
argument_list|(
name|this
argument_list|,
name|diff
argument_list|)
expr_stmt|;
comment|// special case
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|SegmentNodeState
name|that
init|=
operator|(
name|SegmentNodeState
operator|)
name|base
decl_stmt|;
if|if
condition|(
operator|!
name|recordId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|recordId
argument_list|)
condition|)
block|{
name|getTemplate
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|,
name|that
operator|.
name|getTemplate
argument_list|()
argument_list|,
name|that
operator|.
name|recordId
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
comment|// fallback
block|}
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|NodeState
condition|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|SegmentNodeState
name|that
init|=
operator|(
name|SegmentNodeState
operator|)
name|object
decl_stmt|;
if|if
condition|(
name|recordId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|recordId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|getTemplate
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getTemplate
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|object
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

