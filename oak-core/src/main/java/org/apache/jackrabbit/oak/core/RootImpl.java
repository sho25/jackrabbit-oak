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
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|SessionQueryEngine
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
name|TreeLocation
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
name|commit
operator|.
name|DefaultConflictHandler
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
name|query
operator|.
name|SessionQueryEngineImpl
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
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
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
name|security
operator|.
name|authorization
operator|.
name|AccessControlProvider
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
name|security
operator|.
name|authorization
operator|.
name|CompiledPermissions
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
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
comment|/**      * The access control context provider.      */
specifier|private
specifier|final
name|AccessControlProvider
name|accProvider
decl_stmt|;
comment|/** Current branch this root operates on */
specifier|private
name|NodeStoreBranch
name|branch
decl_stmt|;
comment|/** Current root {@code Tree} */
specifier|private
name|TreeImpl
name|rootTree
decl_stmt|;
comment|/**      * Number of {@link #purge()} occurred so since the lase      * purge.      */
specifier|private
name|int
name|modCount
decl_stmt|;
comment|/**      * Listeners which needs to be notified as soon as {@link #purgePendingChanges()}      * is called. Listeners are removed from this list after being called. If further      * notifications are required, they need to explicitly re-register.      *      * The {@link TreeImpl} instances us this mechanism to dispose of its associated      * {@link NodeBuilder} on purge. Keeping a reference on those {@code TreeImpl}      * instances {@code NodeBuilder} (i.e. those which are modified) prevents them      * from being prematurely garbage collected.      */
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
specifier|private
specifier|volatile
name|ConflictHandler
name|conflictHandler
init|=
name|DefaultConflictHandler
operator|.
name|OURS
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
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
comment|/**      * New instance bases on a given {@link NodeStore} and a workspace      *      * @param store         node store      * @param workspaceName name of the workspace      * @param subject       the subject.      * @param accProvider   the access control context provider.      * @param indexProvider the query index provider.      */
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
parameter_list|,
name|Subject
name|subject
parameter_list|,
name|AccessControlProvider
name|accProvider
parameter_list|,
name|QueryIndexProvider
name|indexProvider
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
name|subject
operator|=
name|checkNotNull
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|this
operator|.
name|accProvider
operator|=
name|checkNotNull
argument_list|(
name|accProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setConflictHandler
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|this
operator|.
name|conflictHandler
operator|=
name|conflictHandler
expr_stmt|;
block|}
specifier|public
name|ConflictHandler
name|getConflictHandler
parameter_list|()
block|{
return|return
name|conflictHandler
return|;
block|}
comment|//---------------------------------------------------------------< Root>---
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
name|rootTree
operator|.
name|getTree
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
name|rootTree
operator|.
name|getTree
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
name|TreeImpl
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|rootTree
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getLocation
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rootTree
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
if|if
condition|(
operator|!
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|equals
argument_list|(
name|rootTree
operator|.
name|getBaseState
argument_list|()
argument_list|)
condition|)
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
name|rootTree
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|MergingNodeStateDiff
operator|.
name|merge
argument_list|(
name|base
argument_list|,
name|head
argument_list|,
name|rootTree
operator|.
name|getNodeBuilder
argument_list|()
argument_list|,
name|conflictHandler
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|refresh
parameter_list|()
block|{
name|branch
operator|=
name|store
operator|.
name|branch
argument_list|()
expr_stmt|;
name|rootTree
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
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|rebase
argument_list|()
expr_stmt|;
name|purgePendingChanges
argument_list|()
expr_stmt|;
name|CommitFailedException
name|exception
init|=
name|Subject
operator|.
name|doAs
argument_list|(
name|getCombinedSubject
argument_list|()
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|CommitFailedException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CommitFailedException
name|run
parameter_list|()
block|{
try|try
block|{
name|branch
operator|.
name|merge
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
return|return
name|e
return|;
block|}
block|}
block|}
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
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|// TODO: find a better solution for passing in additional principals
specifier|private
name|Subject
name|getCombinedSubject
parameter_list|()
block|{
name|Subject
name|accSubject
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|accSubject
operator|==
literal|null
condition|)
block|{
return|return
name|subject
return|;
block|}
else|else
block|{
name|Subject
name|combinedSubject
init|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|subject
operator|.
name|getPublicCredentials
argument_list|()
argument_list|,
name|subject
operator|.
name|getPrivateCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|combinedSubject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|addAll
argument_list|(
name|accSubject
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|combinedSubject
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|addAll
argument_list|(
name|accSubject
operator|.
name|getPrivateCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|combinedSubject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|addAll
argument_list|(
operator|(
name|accSubject
operator|.
name|getPublicCredentials
argument_list|()
operator|)
argument_list|)
expr_stmt|;
return|return
name|combinedSubject
return|;
block|}
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
name|rootTree
operator|.
name|getNodeState
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
name|store
operator|.
name|getRoot
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
name|store
operator|.
name|getRoot
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
annotation|@
name|Override
specifier|public
name|SessionQueryEngine
name|getQueryEngine
parameter_list|()
block|{
return|return
operator|new
name|SessionQueryEngineImpl
argument_list|(
name|store
argument_list|,
name|indexProvider
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Returns the node state from which the current branch was created.      * @return base node state      */
annotation|@
name|Nonnull
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
name|NodeBuilder
name|createRootBuilder
parameter_list|()
block|{
return|return
name|branch
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
return|;
block|}
comment|/**      * Add a {@code PurgeListener} to this instance. Listeners are automatically      * unregistered after having been called. If further notifications are required,      * they need to explicitly re-register.      * @param purgeListener  listener      */
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
comment|// TODO better way to determine purge limit. See OAK-175
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
name|CompiledPermissions
name|getPermissions
parameter_list|()
block|{
return|return
name|accProvider
operator|.
name|getAccessControlContext
argument_list|(
name|subject
argument_list|)
operator|.
name|getPermissions
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Purge all pending changes to the underlying {@link NodeStoreBranch}.      * All registered {@link PurgeListener}s are notified.      */
specifier|private
name|void
name|purgePendingChanges
parameter_list|()
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|rootTree
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

