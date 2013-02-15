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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|ADD_EXISTING_NODE
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|ADD_EXISTING_PROPERTY
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|CHANGE_CHANGED_PROPERTY
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|CHANGE_DELETED_NODE
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|CHANGE_DELETED_PROPERTY
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|CONFLICT
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|DELETE_CHANGED_NODE
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|DELETE_CHANGED_PROPERTY
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|DELETE_DELETED_NODE
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
name|commit
operator|.
name|MergingNodeStateDiff
operator|.
name|DELETE_DELETED_PROPERTY
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
name|CommitFailedException
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
name|commons
operator|.
name|PathUtils
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
name|commit
operator|.
name|CommitHook
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
name|NodeStoreBranch
import|;
end_import

begin_class
class|class
name|SegmentNodeStoreBranch
implements|implements
name|NodeStoreBranch
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
name|RecordId
name|baseId
decl_stmt|;
specifier|private
name|RecordId
name|rootId
decl_stmt|;
name|SegmentNodeStoreBranch
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|SegmentReader
name|reader
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
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseId
operator|=
name|store
operator|.
name|getJournalHead
argument_list|()
expr_stmt|;
name|this
operator|.
name|rootId
operator|=
name|baseId
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|baseId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|rootId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|this
operator|.
name|rootId
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|// FIXME: Proper rebase needed
specifier|private
class|class
name|RebaseDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
name|RebaseDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|other
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|conflictMarker
argument_list|(
name|ADD_EXISTING_PROPERTY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|conflictMarker
argument_list|(
name|CHANGE_DELETED_PROPERTY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|other
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|conflictMarker
argument_list|(
name|CHANGE_CHANGED_PROPERTY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|conflictMarker
argument_list|(
name|DELETE_DELETED_PROPERTY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
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
block|}
else|else
block|{
name|conflictMarker
argument_list|(
name|DELETE_CHANGED_PROPERTY
argument_list|)
operator|.
name|setProperty
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
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
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|conflictMarker
argument_list|(
name|ADD_EXISTING_NODE
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
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
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|RebaseDiff
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conflictMarker
argument_list|(
name|CHANGE_DELETED_NODE
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|conflictMarker
argument_list|(
name|DELETE_DELETED_NODE
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conflictMarker
argument_list|(
name|DELETE_CHANGED_NODE
argument_list|)
operator|.
name|setNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|NodeBuilder
name|conflictMarker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|builder
operator|.
name|child
argument_list|(
name|CONFLICT
argument_list|)
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|rebase
parameter_list|()
block|{
name|RecordId
name|newBaseId
init|=
name|store
operator|.
name|getJournalHead
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|baseId
operator|.
name|equals
argument_list|(
name|newBaseId
argument_list|)
condition|)
block|{
name|NodeBuilder
name|builder
init|=
operator|new
name|MemoryNodeBuilder
argument_list|(
operator|new
name|SegmentNodeState
argument_list|(
name|reader
argument_list|,
name|newBaseId
argument_list|)
argument_list|)
decl_stmt|;
name|getRoot
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|getBase
argument_list|()
argument_list|,
operator|new
name|RebaseDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseId
operator|=
name|newBaseId
expr_stmt|;
name|this
operator|.
name|rootId
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
name|CommitHook
name|hook
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|RecordId
name|originalBaseId
init|=
name|baseId
decl_stmt|;
name|RecordId
name|originalRootId
init|=
name|rootId
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|rebase
argument_list|()
expr_stmt|;
name|RecordId
name|headId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|hook
operator|.
name|processCommit
argument_list|(
name|getBase
argument_list|()
argument_list|,
name|getRoot
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|.
name|setJournalHead
argument_list|(
name|headId
argument_list|,
name|baseId
argument_list|)
condition|)
block|{
name|baseId
operator|=
name|headId
expr_stmt|;
name|rootId
operator|=
name|headId
expr_stmt|;
return|return
name|getRoot
argument_list|()
return|;
block|}
name|baseId
operator|=
name|originalBaseId
expr_stmt|;
name|rootId
operator|=
name|originalRootId
expr_stmt|;
block|}
throw|throw
operator|new
name|CommitFailedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|source
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|NodeBuilder
name|builder
init|=
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|sourceBuilder
operator|.
name|removeNode
argument_list|(
name|sourceName
argument_list|)
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
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
name|copy
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|targetBuilder
init|=
name|builder
decl_stmt|;
name|String
name|targetParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|targetParent
argument_list|)
control|)
block|{
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|targetBuilder
operator|=
name|targetBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|targetName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetBuilder
operator|.
name|hasChildNode
argument_list|(
name|targetName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeBuilder
name|sourceBuilder
init|=
name|builder
decl_stmt|;
name|String
name|sourceParent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|sourceParent
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|sourceBuilder
operator|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|String
name|sourceName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceBuilder
operator|.
name|hasChildNode
argument_list|(
name|sourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|sourceState
init|=
name|sourceBuilder
operator|.
name|child
argument_list|(
name|sourceName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|targetBuilder
operator|.
name|setNode
argument_list|(
name|targetName
argument_list|,
name|sourceState
argument_list|)
expr_stmt|;
name|setRoot
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

