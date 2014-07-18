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
name|api
operator|.
name|Type
operator|.
name|BOOLEAN
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
name|concat
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
name|ASYNC_REINDEX_VALUE
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
name|REINDEX_ASYNC_PROPERTY_NAME
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
import|;
end_import

begin_class
specifier|public
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
name|String
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
comment|/** Parent updater, or {@code null} if this is the root updater. */
specifier|private
specifier|final
name|IndexUpdate
name|parent
decl_stmt|;
comment|/** Name of this node, or {@code null} for the root node. */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Path of this editor, built lazily in {@link #getPath()}. */
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Editors for indexes that will be normally updated.      */
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
comment|/**      * Editors for indexes that need to be re-indexed.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Editor
argument_list|>
name|reindex
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
comment|/**      * Callback for the update events of the indexing job      */
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|public
name|IndexUpdate
parameter_list|(
name|IndexEditorProvider
name|provider
parameter_list|,
name|String
name|async
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
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
name|this
operator|.
name|updateCallback
operator|=
name|checkNotNull
argument_list|(
name|updateCallback
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
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
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
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|parent
operator|.
name|updateCallback
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
name|collectIndexEditors
argument_list|(
name|builder
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
name|before
argument_list|)
expr_stmt|;
comment|// no-op when reindex is empty
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|reindex
operator|.
name|values
argument_list|()
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
specifier|private
name|boolean
name|shouldReindex
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
name|ps
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// reindex in the case this is a new node, even though the reindex flag
comment|// might be set to 'false' (possible via content import)
return|return
operator|!
name|before
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|void
name|collectIndexEditors
parameter_list|(
name|NodeBuilder
name|definitions
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|async
argument_list|,
name|definition
operator|.
name|getString
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|definition
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|boolean
name|shouldReindex
init|=
name|shouldReindex
argument_list|(
name|definition
argument_list|,
name|before
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|Editor
name|editor
init|=
name|provider
operator|.
name|getIndexEditor
argument_list|(
name|type
argument_list|,
name|definition
argument_list|,
name|root
argument_list|,
name|updateCallback
argument_list|)
decl_stmt|;
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
name|shouldReindex
condition|)
block|{
if|if
condition|(
name|definition
operator|.
name|getBoolean
argument_list|(
name|REINDEX_ASYNC_PROPERTY_NAME
argument_list|)
operator|&&
name|definition
operator|.
name|getString
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// switch index to an async update mode
name|definition
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|ASYNC_REINDEX_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
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
name|getChildNode
argument_list|(
name|rm
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|reindex
operator|.
name|put
argument_list|(
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|name
argument_list|)
argument_list|,
name|editor
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|editors
operator|.
name|add
argument_list|(
name|editor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Returns the path of this node, building it lazily when first requested.      */
specifier|private
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
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
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getReindexedDefinitions
parameter_list|()
block|{
return|return
name|reindex
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

