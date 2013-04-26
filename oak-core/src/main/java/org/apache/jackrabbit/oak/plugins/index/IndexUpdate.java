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
name|index
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
name|checkNotNull
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
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|index
operator|.
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|index
operator|.
name|IndexUtils
operator|.
name|getBoolean
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
name|index
operator|.
name|IndexUtils
operator|.
name|getString
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
name|MISSING_NODE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|spi
operator|.
name|commit
operator|.
name|CompositeEditor
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
name|Editor
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
name|EditorDiff
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
name|VisibleEditor
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
name|IndexUpdate
implements|implements
name|Editor
block|{
specifier|private
specifier|final
name|IndexEditorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|async
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Editor
argument_list|>
name|editors
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|IndexUpdate
parameter_list|(
name|IndexEditorProvider
name|provider
parameter_list|,
name|boolean
name|async
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|checkNotNull
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|this
operator|.
name|async
operator|=
name|async
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexUpdate
parameter_list|(
name|IndexUpdate
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|parent
operator|.
name|provider
expr_stmt|;
name|this
operator|.
name|async
operator|=
name|parent
operator|.
name|async
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|parent
operator|.
name|root
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|parent
operator|.
name|builder
operator|.
name|child
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|List
argument_list|<
name|Editor
argument_list|>
name|reindex
init|=
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|builder
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Editor
argument_list|>
name|tempEditors
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Editor
argument_list|>
argument_list|()
decl_stmt|;
name|NodeBuilder
name|definitions
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|definitions
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|definition
init|=
name|definitions
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|async
operator|==
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|ASYNC_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|getString
argument_list|(
name|definition
argument_list|,
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|Editor
name|editor
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tempEditors
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|editor
operator|=
name|tempEditors
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|editor
operator|=
name|provider
operator|.
name|getIndexEditor
argument_list|(
name|type
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|tempEditors
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|editor
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|editor
operator|==
literal|null
condition|)
block|{
comment|// trigger reindexing when an indexer becomes available
name|definition
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getBoolean
argument_list|(
name|definition
argument_list|,
name|REINDEX_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|definition
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// as we don't know the index content node name
comment|// beforehand, we'll remove all child nodes
for|for
control|(
name|String
name|rm
range|:
name|definition
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|definition
operator|.
name|removeChildNode
argument_list|(
name|rm
argument_list|)
expr_stmt|;
block|}
name|reindex
operator|.
name|add
argument_list|(
name|editor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|editors
operator|.
name|add
argument_list|(
name|VisibleEditor
operator|.
name|wrap
argument_list|(
name|editor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// no-op when reindex is empty
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|VisibleEditor
operator|.
name|wrap
argument_list|(
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|reindex
argument_list|)
argument_list|)
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|editor
operator|.
name|enter
argument_list|(
name|before
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
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|editor
operator|.
name|leave
argument_list|(
name|before
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
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|editor
operator|.
name|propertyAdded
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
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|editor
operator|.
name|propertyChanged
argument_list|(
name|before
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
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|editor
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|List
argument_list|<
name|Editor
argument_list|>
name|children
init|=
name|newArrayListWithCapacity
argument_list|(
literal|1
operator|+
name|editors
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|new
name|IndexUpdate
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|Editor
name|child
init|=
name|editor
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|children
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Editor
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
throws|throws
name|CommitFailedException
block|{
name|List
argument_list|<
name|Editor
argument_list|>
name|children
init|=
name|newArrayListWithCapacity
argument_list|(
literal|1
operator|+
name|editors
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|new
name|IndexUpdate
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|Editor
name|child
init|=
name|editor
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|children
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|List
argument_list|<
name|Editor
argument_list|>
name|children
init|=
name|newArrayListWithCapacity
argument_list|(
name|editors
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|Editor
name|child
init|=
name|editor
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|children
argument_list|)
return|;
block|}
block|}
end_class

end_unit

