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
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|ChangeExtractor
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
name|ConflictHandler
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
name|ConflictHandler
operator|.
name|Resolution
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
name|CoreValue
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
name|Root
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
name|Tree
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
name|NodeStateBuilder
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
name|spi
operator|.
name|state
operator|.
name|NodeStoreBranch
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
name|ConflictHandler
operator|.
name|Resolution
operator|.
name|MERGED
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
name|ConflictHandler
operator|.
name|Resolution
operator|.
name|OURS
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
name|elements
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
name|getName
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
name|getParentPath
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
name|util
operator|.
name|Iterators
operator|.
name|toList
import|;
end_import

begin_class
specifier|public
class|class
name|RootImpl
implements|implements
name|Root
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RootImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Number of {@link #purge()} calls for which changes are kept in memory.      */
specifier|private
specifier|static
specifier|final
name|int
name|PURGE_LIMIT
init|=
literal|100
decl_stmt|;
comment|/** The underlying store to which this root belongs */
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
comment|/** Current branch this root operates on */
specifier|private
name|NodeStoreBranch
name|branch
decl_stmt|;
comment|/** Actual root element of the {@code Tree} */
specifier|private
name|TreeImpl
name|root
decl_stmt|;
comment|/**      * Number of {@link #purge()} occurred so since the lase      * purge.      */
specifier|private
name|int
name|modCount
decl_stmt|;
comment|/**      * Listeners which needs to be modified as soon as {@link #purgePendingChanges()}      * is called. Listeners are removed from this list after being called. If further      * notifications are required, they need to explicitly re-register.      *      * The {@link TreeImpl} instances us this mechanism to dispose of its associated      * {@link NodeStateBuilder} on purge. Keeping a reference on those {@code TreeImpl}      * instances {@code NodeStateBuilder} (i.e. those which are modified) prevents them      * from being prematurely garbage collected.      */
specifier|private
name|List
argument_list|<
name|PurgeListener
argument_list|>
name|purgePurgeListeners
init|=
operator|new
name|ArrayList
argument_list|<
name|PurgeListener
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Purge listener.      * @see #purgePurgeListeners      */
specifier|public
interface|interface
name|PurgeListener
block|{
name|void
name|purged
parameter_list|()
function_decl|;
block|}
comment|/**      * Reference to a {@code NodeState} of the revision up to which      * observation events are generated.      */
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
name|observationLimit
decl_stmt|;
comment|/**      * New instance bases on a given {@link NodeStore} and a workspace      * @param store  node store      * @param workspaceName  name of the workspace      * TODO: add support for multiple workspaces. See OAK-118      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedParameters"
argument_list|)
specifier|public
name|RootImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|workspaceName
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
name|observationLimit
operator|=
operator|new
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|branch
operator|=
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|root
operator|=
name|TreeImpl
operator|.
name|createRoot
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|TreeImpl
name|source
init|=
name|getChild
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TreeImpl
name|destParent
init|=
name|getChild
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|destName
init|=
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|destParent
operator|.
name|hasChild
argument_list|(
name|destName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|source
operator|.
name|moveTo
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
expr_stmt|;
return|return
name|branch
operator|.
name|move
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|purgePendingChanges
argument_list|()
expr_stmt|;
return|return
name|branch
operator|.
name|copy
argument_list|(
name|sourcePath
argument_list|,
name|destPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getChild
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|NodeState
name|base
init|=
name|getBaseState
argument_list|()
decl_stmt|;
name|NodeState
name|head
init|=
name|getCurrentRootState
argument_list|()
decl_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|base
argument_list|,
name|head
argument_list|,
name|root
argument_list|,
name|conflictHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
comment|// There is a small race here and we risk to get an "earlier" revision for the
comment|// observation limit than for the branch. This is not a problem though since
comment|// observation will catch up later on with the next call to ChangeExtractor.getChanges()
name|observationLimit
operator|.
name|set
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|branch
operator|=
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|root
operator|=
name|TreeImpl
operator|.
name|createRoot
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|rebase
argument_list|(
name|conflictHandler
argument_list|)
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
return|return
operator|!
name|getBaseState
argument_list|()
operator|.
name|equals
argument_list|(
name|getCurrentRootState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|ChangeExtractor
name|getChangeExtractor
parameter_list|()
block|{
return|return
operator|new
name|ChangeExtractor
argument_list|()
block|{
specifier|private
name|NodeState
name|baseLine
init|=
name|observationLimit
operator|.
name|get
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|getChanges
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|NodeState
name|head
init|=
name|observationLimit
operator|.
name|get
argument_list|()
decl_stmt|;
name|head
operator|.
name|compareAgainstBaseState
argument_list|(
name|baseLine
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|baseLine
operator|=
name|head
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**      * Add a {@code PurgeListener} to this instance. Listeners are automatically      * unregistered after having been called. If further notifications are required,      * they need to explicitly re-register.      * @param purgeListener  listener      */
specifier|public
name|void
name|addListener
parameter_list|(
name|PurgeListener
name|purgeListener
parameter_list|)
block|{
name|purgePurgeListeners
operator|.
name|add
argument_list|(
name|purgeListener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the current root node state      * @return root node state      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getCurrentRootState
parameter_list|()
block|{
return|return
name|root
operator|.
name|getNodeState
argument_list|()
return|;
block|}
comment|/**      * Returns the node state from which the current branch was created.      * @return base node state      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|branch
operator|.
name|getBase
argument_list|()
return|;
block|}
comment|/**      * Returns a builder for constructing a new or modified node state.      * The builder is initialized with all the properties and child nodes      * from the given base node state.      *      * @param nodeState  base node state, or {@code null} for building new nodes      * @return  builder instance      */
annotation|@
name|Nonnull
specifier|public
name|NodeStateBuilder
name|getBuilder
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
name|store
operator|.
name|getBuilder
argument_list|(
name|nodeState
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< internal>---
name|NodeStateBuilder
name|createRootBuilder
parameter_list|()
block|{
return|return
name|store
operator|.
name|getBuilder
argument_list|(
name|branch
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO better way to determine purge limit
name|void
name|purge
parameter_list|()
block|{
if|if
condition|(
operator|++
name|modCount
operator|>
name|PURGE_LIMIT
condition|)
block|{
name|modCount
operator|=
literal|0
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Purge all pending changes to the underlying {@link NodeStoreBranch}.      * All registered {@link PurgeListener}s are notified.      */
specifier|private
name|void
name|purgePendingChanges
parameter_list|()
block|{
if|if
condition|(
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|getCurrentRootState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|notifyListeners
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|notifyListeners
parameter_list|()
block|{
name|List
argument_list|<
name|PurgeListener
argument_list|>
name|purgeListeners
init|=
name|this
operator|.
name|purgePurgeListeners
decl_stmt|;
name|this
operator|.
name|purgePurgeListeners
operator|=
operator|new
name|ArrayList
argument_list|<
name|PurgeListener
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|PurgeListener
name|purgeListener
range|:
name|purgeListeners
control|)
block|{
name|purgeListener
operator|.
name|purged
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|TreeImpl
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
comment|/**      * Get a tree for the child identified by {@code path}      * @param path  the path to the child      * @return  a {@link Tree} instance for the child      *          at {@code path} or {@code null} if no such item exits.      */
specifier|private
name|TreeImpl
name|getChild
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeImpl
name|child
init|=
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|child
operator|=
name|child
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|child
return|;
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeState
name|fromState
parameter_list|,
name|NodeState
name|toState
parameter_list|,
specifier|final
name|TreeImpl
name|target
parameter_list|,
specifier|final
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
assert|assert
name|target
operator|!=
literal|null
assert|;
name|toState
operator|.
name|compareAgainstBaseState
argument_list|(
name|fromState
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
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
name|Resolution
name|resolution
decl_stmt|;
name|PropertyState
name|p
init|=
name|target
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
name|p
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|OURS
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|addExistingProperty
argument_list|(
name|target
argument_list|,
name|after
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
name|setProperty
argument_list|(
name|target
argument_list|,
name|after
argument_list|)
expr_stmt|;
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
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
assert|assert
name|before
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
assert|;
name|Resolution
name|resolution
decl_stmt|;
name|PropertyState
name|p
init|=
name|target
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
name|p
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|changeDeletedProperty
argument_list|(
name|target
argument_list|,
name|after
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
name|p
argument_list|)
condition|)
block|{
name|resolution
operator|=
name|OURS
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|changeChangedProperty
argument_list|(
name|target
argument_list|,
name|after
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
name|setProperty
argument_list|(
name|target
argument_list|,
name|after
argument_list|)
expr_stmt|;
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
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
name|Resolution
name|resolution
decl_stmt|;
name|PropertyState
name|p
init|=
name|target
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
name|before
operator|.
name|equals
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|resolution
operator|=
name|OURS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|deleteDeletedProperty
argument_list|(
name|target
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|deleteChangedProperty
argument_list|(
name|target
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
name|target
operator|.
name|removeProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
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
name|Resolution
name|resolution
decl_stmt|;
name|TreeImpl
name|n
init|=
name|target
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|OURS
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|addExistingNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|after
argument_list|,
name|n
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
name|addChild
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
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
name|Resolution
name|resolution
decl_stmt|;
name|TreeImpl
name|n
init|=
name|target
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|changeDeletedNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|merge
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|n
argument_list|,
name|conflictHandler
argument_list|)
expr_stmt|;
name|resolution
operator|=
name|MERGED
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
name|addChild
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
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
name|Resolution
name|resolution
decl_stmt|;
name|TreeImpl
name|n
init|=
name|target
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|deleteDeletedNode
argument_list|(
name|target
argument_list|,
name|name
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
name|n
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|resolution
operator|=
name|OURS
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|conflictHandler
operator|.
name|deleteChangedNode
argument_list|(
name|target
argument_list|,
name|name
argument_list|,
name|n
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|resolution
condition|)
block|{
case|case
name|OURS
case|:
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|THEIRS
case|:
case|case
name|MERGED
case|:
break|break;
block|}
block|}
specifier|private
name|void
name|addChild
parameter_list|(
name|Tree
name|target
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|Tree
name|child
init|=
name|target
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|setProperty
argument_list|(
name|child
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|addChild
argument_list|(
name|child
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setProperty
parameter_list|(
name|Tree
name|target
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|target
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|toList
argument_list|(
name|property
operator|.
name|getValues
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

