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
name|plugins
operator|.
name|observation
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
name|Objects
operator|.
name|toStringHelper
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
name|checkState
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
name|LONG
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
name|STRING
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|Objects
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
name|Queues
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

begin_comment
comment|/**  * A {@code ChangeDispatcher} instance records changes to a {@link NodeStore}  * and dispatches them to interested parties.  *<p>  * Actual changes are reported by calling {@link #beforeCommit(NodeState)},  * {@link #localCommit(NodeState)} and {@link #afterCommit(NodeState)} in that order:  *<pre>       NodeState root = store.getRoot();       branch.rebase();       changeDispatcher.beforeCommit(root);       try {           NodeState head = branch.getHead();           branch.merge();           changeDispatcher.localCommit(head);       } finally {           changeDispatcher.afterCommit(store.getRoot());       }  *</pre>  *<p>  * The {@link #newListener()} method registers a listener for receiving changes reported  * into a change dispatcher.  */
end_comment

begin_class
specifier|public
class|class
name|ChangeDispatcher
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|Listener
argument_list|>
name|listeners
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
name|NodeState
name|previousRoot
decl_stmt|;
comment|/**      * Create a new instance for recording changes to {@code store}.      * @param store  the node store to record changes for      */
specifier|public
name|ChangeDispatcher
parameter_list|(
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|previousRoot
operator|=
name|checkNotNull
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@link Listener} for receiving changes reported into      * this change dispatcher. Listeners need to be {@link Listener#dispose() disposed}      * when no longer needed.      * @return  a new {@code Listener} instance.      */
annotation|@
name|Nonnull
specifier|public
name|Listener
name|newListener
parameter_list|()
block|{
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|register
argument_list|(
name|listener
argument_list|)
expr_stmt|;
return|return
name|listener
return|;
block|}
specifier|private
specifier|final
name|AtomicLong
name|changeCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|inLocalCommit
parameter_list|()
block|{
return|return
name|changeCount
operator|.
name|get
argument_list|()
operator|%
literal|2
operator|==
literal|1
return|;
block|}
comment|/**      * Call with the latest persisted root node state right before persisting further changes.      * Calling this method marks this instance to be inside a local commit.      *<p>      * The differences from the root node state passed to the last call to      * {@link #afterCommit(NodeState)} to {@code root} are reported as cluster external      * changes to any listener.      *      * @param root  latest persisted root node state.      * @throws IllegalStateException  if inside a local commit      */
specifier|public
specifier|synchronized
name|void
name|beforeCommit
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|inLocalCommit
argument_list|()
argument_list|)
expr_stmt|;
name|changeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|externalChange
argument_list|(
name|checkNotNull
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Call right after changes have been successfully persisted passing the new root      * node state resulting from the persist operation.      *<p>      * The differences from the root node state passed to the last call to      * {@link #beforeCommit(NodeState)} to {@code root} are reported as cluster local      * changes to any listener.       * @param root  root node state just persisted      * @throws IllegalStateException  if not inside a local commit      */
specifier|public
specifier|synchronized
name|void
name|localCommit
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|)
block|{
name|checkState
argument_list|(
name|inLocalCommit
argument_list|()
argument_list|)
expr_stmt|;
name|internalChange
argument_list|(
name|checkNotNull
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Call to mark the end of a persist operation passing the latest persisted root node state.      * Calling this method marks this instance to not be inside a local commit.      *<p>      * The difference from the root node state passed to the las call to      * {@link #localCommit(NodeState)} to {@code root} are reported as cluster external      * changes to any listener.       * @param root  latest persisted root node state.      * @throws IllegalStateException  if not inside a local commit      */
specifier|public
specifier|synchronized
name|void
name|afterCommit
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|)
block|{
name|checkState
argument_list|(
name|inLocalCommit
argument_list|()
argument_list|)
expr_stmt|;
name|externalChange
argument_list|(
name|checkNotNull
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|changeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|externalChange
parameter_list|()
block|{
if|if
condition|(
operator|!
name|inLocalCommit
argument_list|()
condition|)
block|{
name|long
name|c
init|=
name|changeCount
operator|.
name|get
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// Need to get root outside sync. See OAK-959
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|c
operator|==
name|changeCount
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|inLocalCommit
argument_list|()
condition|)
block|{
name|externalChange
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|externalChange
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
operator|!
name|root
operator|.
name|equals
argument_list|(
name|previousRoot
argument_list|)
condition|)
block|{
name|add
argument_list|(
operator|new
name|ChangeSet
argument_list|(
name|previousRoot
argument_list|,
name|root
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|previousRoot
operator|=
name|root
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|internalChange
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|ChangeSet
argument_list|(
name|previousRoot
argument_list|,
name|root
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|previousRoot
operator|=
name|root
expr_stmt|;
block|}
specifier|private
name|void
name|register
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|unregister
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|add
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
for|for
control|(
name|Listener
name|l
range|:
name|getListeners
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|changeSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Listener
index|[]
name|getListeners
parameter_list|()
block|{
synchronized|synchronized
init|(
name|listeners
init|)
block|{
return|return
name|listeners
operator|.
name|toArray
argument_list|(
operator|new
name|Listener
index|[
name|listeners
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
comment|//------------------------------------------------------------< Listener>---
comment|/**      * Listener for receiving changes reported into a change dispatcher by any of its hooks.      */
specifier|public
class|class
name|Listener
block|{
specifier|private
specifier|final
name|Queue
argument_list|<
name|ChangeSet
argument_list|>
name|changeSets
init|=
name|Queues
operator|.
name|newLinkedBlockingQueue
argument_list|()
decl_stmt|;
comment|/**          * Free up any resources associated by this hook.          */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|unregister
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**          * Poll for changes reported to this listener.          * @return  {@code ChangeSet} of the changes or {@code null} if no changes occurred since          *          the last call to this method.          */
annotation|@
name|CheckForNull
specifier|public
name|ChangeSet
name|getChanges
parameter_list|()
block|{
if|if
condition|(
name|changeSets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|externalChange
argument_list|()
expr_stmt|;
block|}
return|return
name|changeSets
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|changeSets
operator|.
name|remove
argument_list|()
return|;
block|}
specifier|private
name|void
name|add
parameter_list|(
name|ChangeSet
name|changeSet
parameter_list|)
block|{
name|changeSets
operator|.
name|add
argument_list|(
name|changeSet
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< ChangeSet>---
comment|/**      * Instances of this class represent changes to a node store. In addition they      * record meta data associated with such changes like whether a change occurred      * on the local cluster node, the user causing the changes and the date the changes      * where persisted.      */
specifier|public
specifier|static
class|class
name|ChangeSet
block|{
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isExternal
decl_stmt|;
name|ChangeSet
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|boolean
name|isExternal
parameter_list|)
block|{
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|isExternal
operator|=
name|isExternal
expr_stmt|;
block|}
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
name|isExternal
return|;
block|}
specifier|public
name|boolean
name|isLocal
parameter_list|(
name|String
name|sessionId
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|getSessionId
argument_list|()
argument_list|,
name|sessionId
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getSessionId
parameter_list|()
block|{
return|return
name|getStringOrNull
argument_list|(
name|getCommitInfo
argument_list|(
name|after
argument_list|)
argument_list|,
name|CommitInfoEditorProvider
operator|.
name|SESSION_ID
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|getStringOrNull
argument_list|(
name|getCommitInfo
argument_list|(
name|after
argument_list|)
argument_list|,
name|CommitInfoEditorProvider
operator|.
name|USER_ID
argument_list|)
return|;
block|}
specifier|public
name|long
name|getDate
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|getCommitInfo
argument_list|(
name|after
argument_list|)
operator|.
name|getProperty
argument_list|(
name|CommitInfoEditorProvider
operator|.
name|TIME_STAMP
argument_list|)
decl_stmt|;
return|return
name|property
operator|==
literal|null
condition|?
literal|0
else|:
name|property
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
return|;
block|}
comment|/**          * State before the change          * @return  before state          */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBeforeState
parameter_list|()
block|{
return|return
name|before
return|;
block|}
comment|/**          * State after the change          * @return  after state          */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getAfterState
parameter_list|()
block|{
return|return
name|after
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"base"
argument_list|,
name|before
argument_list|)
operator|.
name|add
argument_list|(
literal|"head"
argument_list|,
name|after
argument_list|)
operator|.
name|add
argument_list|(
name|CommitInfoEditorProvider
operator|.
name|USER_ID
argument_list|,
name|getUserId
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|CommitInfoEditorProvider
operator|.
name|TIME_STAMP
argument_list|,
name|getDate
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|CommitInfoEditorProvider
operator|.
name|SESSION_ID
argument_list|,
name|getSessionId
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"external"
argument_list|,
name|isExternal
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ChangeSet
name|that
init|=
operator|(
name|ChangeSet
operator|)
name|other
decl_stmt|;
return|return
name|before
operator|.
name|equals
argument_list|(
name|that
operator|.
name|before
argument_list|)
operator|&&
name|after
operator|.
name|equals
argument_list|(
name|that
operator|.
name|after
argument_list|)
operator|&&
name|isExternal
operator|==
name|that
operator|.
name|isExternal
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|before
operator|.
name|hashCode
argument_list|()
operator|+
name|after
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|getStringOrNull
parameter_list|(
name|NodeState
name|commitInfo
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|commitInfo
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|property
operator|==
literal|null
condition|?
literal|null
else|:
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|getCommitInfo
parameter_list|(
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|after
operator|.
name|getChildNode
argument_list|(
name|CommitInfoEditorProvider
operator|.
name|COMMIT_INFO
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

