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
name|INDEX_PATH
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
name|REINDEX_COUNT
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
import|import static
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
operator|.
name|compose
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
name|spi
operator|.
name|commit
operator|.
name|EditorDiff
operator|.
name|process
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
name|spi
operator|.
name|commit
operator|.
name|VisibleEditor
operator|.
name|wrap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|collect
operator|.
name|Maps
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
name|Sets
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
name|ProgressNotificationEditor
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
name|NodeStateUtils
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
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexUpdate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      *<p>      * The value of this flag determines the behavior of the IndexUpdate when      * dealing with {@code reindex} flags.      *</p>      *<p>      * If {@code false} (default value), the indexer will start reindexing      * immediately in the current thread, blocking a commit until this operation      * is done.      *</p>      *<p>      * If {@code true}, the indexer will ignore the flag, therefore ignoring any      * reindex requests.      *</p>      *<p>      * This is only provided as a support tool (see OAK-3505) so it should be      * used with extreme caution!      *</p>      */
specifier|private
specifier|static
specifier|final
name|boolean
name|IGNORE_REINDEX_FLAGS
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.indexUpdate.ignoreReindexFlags"
argument_list|)
decl_stmt|;
static|static
block|{
if|if
condition|(
name|IGNORE_REINDEX_FLAGS
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Reindexing is disabled by configuration. This value is configurable via the 'oak.indexUpdate.ignoreReindexFlags' system property."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|IndexUpdateRootState
name|rootState
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
specifier|private
name|MissingIndexProviderStrategy
name|missingProvider
init|=
operator|new
name|MissingIndexProviderStrategy
argument_list|()
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
name|rootState
operator|=
operator|new
name|IndexUpdateRootState
argument_list|(
name|provider
argument_list|,
name|async
argument_list|,
name|root
argument_list|,
name|updateCallback
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
name|rootState
operator|=
name|parent
operator|.
name|rootState
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
if|if
condition|(
operator|!
name|reindex
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reindexing will be performed for following indexes: {}"
argument_list|,
name|reindex
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|rootState
operator|.
name|reindexedIndexes
operator|.
name|addAll
argument_list|(
name|reindex
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// no-op when reindex is empty
name|CommitFailedException
name|exception
init|=
name|process
argument_list|(
name|wrap
argument_list|(
name|wrapProgress
argument_list|(
name|compose
argument_list|(
name|reindex
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"Reindexing"
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
specifier|public
name|boolean
name|isReindexingPerformed
parameter_list|()
block|{
return|return
operator|!
name|getReindexStats
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getReindexStats
parameter_list|()
block|{
return|return
name|rootState
operator|.
name|getReindexStats
argument_list|()
return|;
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
operator|!
name|IGNORE_REINDEX_FLAGS
return|;
block|}
comment|// reindex in the case this is a new node, even though the reindex flag
comment|// might be set to 'false' (possible via content import)
name|boolean
name|result
init|=
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
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Found a new index node [{}]. Reindexing is requested"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
name|rootState
operator|.
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
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
comment|// probably not an index def
continue|continue;
block|}
name|manageIndexPath
argument_list|(
name|definition
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
name|String
name|indexPath
init|=
name|getIndexPath
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|Editor
name|editor
init|=
name|rootState
operator|.
name|provider
operator|.
name|getIndexEditor
argument_list|(
name|type
argument_list|,
name|definition
argument_list|,
name|rootState
operator|.
name|root
argument_list|,
name|rootState
operator|.
name|newCallback
argument_list|(
name|indexPath
argument_list|,
name|shouldReindex
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|editor
operator|==
literal|null
condition|)
block|{
name|missingProvider
operator|.
name|onMissingIndex
argument_list|(
name|type
argument_list|,
name|definition
argument_list|,
name|indexPath
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
name|incrementReIndexCount
argument_list|(
name|definition
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
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|rm
argument_list|)
condition|)
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
specifier|private
name|void
name|manageIndexPath
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|path
init|=
name|definition
operator|.
name|getString
argument_list|(
name|INDEX_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|definition
operator|.
name|setProperty
argument_list|(
name|INDEX_PATH
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|incrementReIndexCount
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|)
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|hasProperty
argument_list|(
name|REINDEX_COUNT
argument_list|)
condition|)
block|{
name|count
operator|=
name|definition
operator|.
name|getProperty
argument_list|(
name|REINDEX_COUNT
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
name|definition
operator|.
name|setProperty
argument_list|(
name|REINDEX_COUNT
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rootState
operator|.
name|isReindexingPerformed
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|rootState
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|rootState
operator|.
name|somethingIndexed
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|rootState
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|private
specifier|static
name|String
name|getIndexPath
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|"/"
operator|+
name|INDEX_DEFINITIONS_NAME
operator|+
literal|"/"
operator|+
name|indexName
return|;
block|}
return|return
name|path
operator|+
literal|"/"
operator|+
name|INDEX_DEFINITIONS_NAME
operator|+
literal|"/"
operator|+
name|indexName
return|;
block|}
specifier|private
specifier|static
name|Editor
name|wrapProgress
parameter_list|(
name|Editor
name|editor
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
name|ProgressNotificationEditor
operator|.
name|wrap
argument_list|(
name|editor
argument_list|,
name|log
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
class|class
name|MissingIndexProviderStrategy
block|{
comment|/**          * The value of this flag determines the behavior of          * {@link #onMissingIndex(String, NodeBuilder, String)}. If          * {@code false} (default value), the method will set the          * {@code reindex} flag to true and log a warning. if {@code true}, the          * method will throw a {@link CommitFailedException} failing the commit.          */
specifier|private
name|boolean
name|failOnMissingIndexProvider
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.indexUpdate.failOnMissingIndexProvider"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ignore
init|=
name|newHashSet
argument_list|(
literal|"disabled"
argument_list|)
decl_stmt|;
specifier|public
name|void
name|onMissingIndex
parameter_list|(
name|String
name|type
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|indexPath
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isDisabled
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// trigger reindexing when an indexer becomes available
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
comment|// already true, skip the update
return|return;
block|}
if|if
condition|(
name|failOnMissingIndexProvider
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"IndexUpdate"
argument_list|,
literal|1
argument_list|,
literal|"Missing index provider detected for type ["
operator|+
name|type
operator|+
literal|"] on index ["
operator|+
name|indexPath
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Missing index provider of type [{}], requesting reindex on [{}]"
argument_list|,
name|type
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
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
block|}
name|boolean
name|isDisabled
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|ignore
operator|.
name|contains
argument_list|(
name|type
argument_list|)
return|;
block|}
name|void
name|setFailOnMissingIndexProvider
parameter_list|(
name|boolean
name|failOnMissingIndexProvider
parameter_list|)
block|{
name|this
operator|.
name|failOnMissingIndexProvider
operator|=
name|failOnMissingIndexProvider
expr_stmt|;
block|}
block|}
specifier|public
name|IndexUpdate
name|withMissingProviderStrategy
parameter_list|(
name|MissingIndexProviderStrategy
name|missingProvider
parameter_list|)
block|{
name|this
operator|.
name|missingProvider
operator|=
name|missingProvider
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|IndexUpdateRootState
block|{
specifier|final
name|IndexEditorProvider
name|provider
decl_stmt|;
specifier|final
name|String
name|async
decl_stmt|;
specifier|final
name|NodeState
name|root
decl_stmt|;
comment|/**          * Callback for the update events of the indexing job          */
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|reindexedIndexes
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CountingCallback
argument_list|>
name|callbacks
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|IndexUpdateRootState
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
name|IndexUpdateCallback
name|updateCallback
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
name|updateCallback
operator|=
name|checkNotNull
argument_list|(
name|updateCallback
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexUpdateCallback
name|newCallback
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|boolean
name|reindex
parameter_list|)
block|{
name|CountingCallback
name|cb
init|=
operator|new
name|CountingCallback
argument_list|(
name|indexPath
argument_list|,
name|reindex
argument_list|)
decl_stmt|;
name|callbacks
operator|.
name|put
argument_list|(
name|cb
operator|.
name|indexName
argument_list|,
name|cb
argument_list|)
expr_stmt|;
return|return
name|cb
return|;
block|}
specifier|public
name|String
name|getReport
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Indexing report"
argument_list|)
expr_stmt|;
for|for
control|(
name|CountingCallback
name|cb
range|:
name|callbacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|log
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
name|cb
operator|.
name|reindex
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|cb
operator|.
name|count
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    - %s%n"
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getReindexStats
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|stats
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CountingCallback
name|cb
range|:
name|callbacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|cb
operator|.
name|reindex
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
name|cb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|stats
return|;
block|}
specifier|public
name|boolean
name|somethingIndexed
parameter_list|()
block|{
for|for
control|(
name|CountingCallback
name|cb
range|:
name|callbacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|cb
operator|.
name|count
operator|>
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isReindexingPerformed
parameter_list|()
block|{
return|return
operator|!
name|reindexedIndexes
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|private
class|class
name|CountingCallback
implements|implements
name|IndexUpdateCallback
block|{
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|final
name|boolean
name|reindex
decl_stmt|;
specifier|final
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|int
name|count
decl_stmt|;
specifier|private
name|CountingCallback
parameter_list|(
name|String
name|indexName
parameter_list|,
name|boolean
name|reindex
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
name|this
operator|.
name|reindex
operator|=
name|reindex
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{} => Indexed {} nodes in {} ..."
argument_list|,
name|indexName
argument_list|,
name|count
argument_list|,
name|watch
argument_list|)
expr_stmt|;
name|watch
operator|.
name|reset
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|reindexMarker
init|=
name|reindex
condition|?
literal|"*"
else|:
literal|""
decl_stmt|;
return|return
name|indexName
operator|+
name|reindexMarker
operator|+
literal|"("
operator|+
name|count
operator|+
literal|")"
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

