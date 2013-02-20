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
name|security
operator|.
name|authorization
package|;
end_package

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
name|JcrConstants
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
name|core
operator|.
name|ReadOnlyRoot
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
name|core
operator|.
name|ReadOnlyTree
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
name|core
operator|.
name|TreeImpl
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
name|MemoryNodeState
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
name|MemoryPropertyBuilder
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeBits
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|PropertyBuilder
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
name|util
operator|.
name|TreeUtil
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
name|util
operator|.
name|Text
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_comment
comment|/**  * {@code CommitHook} implementation that processes any modification made to  * access control content and updates persisted permission caches associated  * with access control related data stored in the repository.  */
end_comment

begin_class
specifier|public
class|class
name|PermissionHook
implements|implements
name|CommitHook
implements|,
name|AccessControlConstants
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
name|PermissionHook
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
name|PermissionHook
parameter_list|(
name|String
name|workspaceName
parameter_list|)
block|{
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
specifier|final
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootAfter
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|permissionRoot
init|=
name|getPermissionRoot
argument_list|(
name|rootAfter
argument_list|,
name|workspaceName
argument_list|)
decl_stmt|;
name|ReadOnlyNodeTypeManager
name|ntMgr
init|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|PrivilegeBitsProvider
name|bitsProvider
init|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
operator|new
name|ReadOnlyRoot
argument_list|(
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
operator|new
name|BeforeNode
argument_list|(
name|before
argument_list|)
argument_list|,
operator|new
name|Node
argument_list|(
name|rootAfter
argument_list|)
argument_list|,
name|permissionRoot
argument_list|,
name|bitsProvider
argument_list|,
name|ntMgr
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rootAfter
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|NodeBuilder
name|getPermissionRoot
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|,
name|String
name|workspaceName
parameter_list|)
block|{
name|NodeBuilder
name|permissionStore
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|REP_PERMISSION_STORE
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissionStore
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
operator|==
literal|null
condition|)
block|{
name|permissionStore
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|permissionRoot
decl_stmt|;
if|if
condition|(
operator|!
name|permissionStore
operator|.
name|hasChildNode
argument_list|(
name|workspaceName
argument_list|)
condition|)
block|{
name|permissionRoot
operator|=
name|permissionStore
operator|.
name|child
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|permissionRoot
operator|=
name|permissionStore
operator|.
name|child
argument_list|(
name|workspaceName
argument_list|)
expr_stmt|;
block|}
return|return
name|permissionRoot
return|;
block|}
specifier|private
specifier|static
name|Tree
name|getTree
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
comment|// FIXME: this readonlytree is not properly connect to it's parent
return|return
operator|new
name|ReadOnlyTree
argument_list|(
literal|null
argument_list|,
name|name
argument_list|,
name|nodeState
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getAceIndex
parameter_list|(
name|BaseNode
name|aclNode
parameter_list|,
name|String
name|aceName
parameter_list|)
block|{
name|PropertyState
name|ordering
init|=
name|checkNotNull
argument_list|(
name|aclNode
operator|.
name|getNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|ordering
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
operator|.
name|indexOf
argument_list|(
name|aceName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|generateName
parameter_list|(
name|NodeBuilder
name|principalRoot
parameter_list|,
name|Entry
name|entry
parameter_list|)
block|{
name|StringBuilder
name|name
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|name
operator|.
name|append
argument_list|(
operator|(
name|entry
operator|.
name|isAllow
operator|)
condition|?
literal|'a'
else|:
literal|'d'
argument_list|)
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|principalRoot
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|name
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|Diff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|BeforeNode
name|parentBefore
decl_stmt|;
specifier|private
specifier|final
name|Node
name|parentAfter
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|permissionRoot
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|Diff
parameter_list|(
annotation|@
name|Nonnull
name|BeforeNode
name|parentBefore
parameter_list|,
annotation|@
name|Nonnull
name|Node
name|parentAfter
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|permissionRoot
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBitsProvider
name|bitsProvider
parameter_list|,
annotation|@
name|Nonnull
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
name|this
operator|.
name|parentBefore
operator|=
name|parentBefore
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|parentAfter
expr_stmt|;
name|this
operator|.
name|permissionRoot
operator|=
name|permissionRoot
expr_stmt|;
name|this
operator|.
name|bitsProvider
operator|=
name|bitsProvider
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
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
comment|// nothing to do
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
if|if
condition|(
name|isACL
argument_list|(
name|parentAfter
argument_list|)
operator|&&
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO: update if order has changed without child-node modifications
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
comment|// nothing to do
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
name|isACE
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|addEntry
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BeforeNode
name|before
init|=
operator|new
name|BeforeNode
argument_list|(
name|parentBefore
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
operator|new
name|Node
argument_list|(
name|parentAfter
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
operator|.
name|getNodeState
argument_list|()
argument_list|,
operator|new
name|Diff
argument_list|(
name|before
argument_list|,
name|node
argument_list|,
name|permissionRoot
argument_list|,
name|bitsProvider
argument_list|,
name|ntMgr
argument_list|)
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
specifier|final
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|isACE
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
operator|||
name|isACE
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|updateEntry
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_RESTRICTIONS
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|updateEntry
argument_list|(
name|parentAfter
operator|.
name|getName
argument_list|()
argument_list|,
name|parentBefore
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|parentAfter
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BeforeNode
name|nodeBefore
init|=
operator|new
name|BeforeNode
argument_list|(
name|parentBefore
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|Node
name|nodeAfter
init|=
operator|new
name|Node
argument_list|(
name|parentAfter
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|nodeBefore
argument_list|,
name|nodeAfter
argument_list|,
name|permissionRoot
argument_list|,
name|bitsProvider
argument_list|,
name|ntMgr
argument_list|)
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
name|isACE
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
condition|)
block|{
name|removeEntry
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BeforeNode
name|nodeBefore
init|=
operator|new
name|BeforeNode
argument_list|(
name|parentBefore
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|Node
name|after
init|=
operator|new
name|Node
argument_list|(
name|parentAfter
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|after
operator|.
name|getNodeState
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|nodeBefore
argument_list|,
name|after
argument_list|,
name|permissionRoot
argument_list|,
name|bitsProvider
argument_list|,
name|ntMgr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------< private>---
specifier|private
name|boolean
name|isACL
parameter_list|(
name|Node
name|parent
parameter_list|)
block|{
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|getTree
argument_list|(
name|parent
operator|.
name|getName
argument_list|()
argument_list|,
name|parent
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|,
name|NT_REP_POLICY
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isACE
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|getTree
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|)
argument_list|,
name|NT_REP_ACE
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getAccessControlledPath
parameter_list|(
name|BaseNode
name|aclNode
parameter_list|)
block|{
if|if
condition|(
name|REP_REPO_POLICY
operator|.
name|equals
argument_list|(
name|aclNode
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
else|else
block|{
return|return
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|aclNode
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|addEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ace
parameter_list|)
block|{
name|Entry
name|entry
init|=
name|createEntry
argument_list|(
name|name
argument_list|,
name|ace
argument_list|,
name|parentAfter
argument_list|)
decl_stmt|;
name|entry
operator|.
name|writeTo
argument_list|(
name|permissionRoot
operator|.
name|child
argument_list|(
name|entry
operator|.
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ace
parameter_list|)
block|{
name|Entry
name|entry
init|=
name|createEntry
argument_list|(
name|name
argument_list|,
name|ace
argument_list|,
name|parentBefore
argument_list|)
decl_stmt|;
name|String
name|permissionName
init|=
name|getPermissionNodeName
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissionName
operator|!=
literal|null
condition|)
block|{
name|permissionRoot
operator|.
name|child
argument_list|(
name|entry
operator|.
name|principalName
argument_list|)
operator|.
name|removeNode
argument_list|(
name|permissionName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|updateEntry
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
name|removeEntry
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|String
name|getPermissionNodeName
parameter_list|(
name|Entry
name|aceEntry
parameter_list|)
block|{
if|if
condition|(
name|permissionRoot
operator|.
name|hasChildNode
argument_list|(
name|aceEntry
operator|.
name|principalName
argument_list|)
condition|)
block|{
name|NodeBuilder
name|principalRoot
init|=
name|permissionRoot
operator|.
name|child
argument_list|(
name|aceEntry
operator|.
name|principalName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|principalRoot
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeState
name|state
init|=
name|principalRoot
operator|.
name|child
argument_list|(
name|childName
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|aceEntry
operator|.
name|isSame
argument_list|(
name|childName
argument_list|,
name|state
argument_list|)
condition|)
block|{
return|return
name|childName
return|;
block|}
block|}
name|log
operator|.
name|warn
argument_list|(
literal|"No entry node for "
operator|+
name|aceEntry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// inconsistency: removing an ACE that doesn't have a corresponding
comment|// entry in the permission store.
name|log
operator|.
name|warn
argument_list|(
literal|"Missing permission node for principal "
operator|+
name|aceEntry
operator|.
name|principalName
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Entry
name|createEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|ace
parameter_list|,
name|BaseNode
name|acl
parameter_list|)
block|{
name|Tree
name|aceTree
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|ace
argument_list|)
decl_stmt|;
name|String
name|principalName
init|=
name|checkNotNull
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|aceTree
argument_list|,
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|privilegeBits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|aceTree
argument_list|,
name|REP_PRIVILEGES
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|isAllow
init|=
name|NT_REP_GRANT_ACE
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|aceTree
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: respect restrictions
name|String
name|accessControlledPath
init|=
name|getAccessControlledPath
argument_list|(
name|acl
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|getAceIndex
argument_list|(
name|acl
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|Entry
argument_list|(
name|accessControlledPath
argument_list|,
name|index
argument_list|,
name|principalName
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|BaseNode
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|BaseNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|private
name|BaseNode
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
operator|new
name|String
index|[]
block|{
name|name
block|}
argument_list|)
expr_stmt|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|Text
operator|.
name|getName
argument_list|(
name|path
argument_list|)
return|;
block|}
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|abstract
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
block|}
specifier|private
specifier|static
class|class
name|BeforeNode
extends|extends
name|BaseNode
block|{
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
name|BeforeNode
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|super
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeState
operator|=
name|root
expr_stmt|;
block|}
name|BeforeNode
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
name|super
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
block|}
annotation|@
name|Override
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|nodeState
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Node
extends|extends
name|BaseNode
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
name|Node
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|)
block|{
name|super
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|rootBuilder
expr_stmt|;
block|}
specifier|private
name|Node
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|state
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Node
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
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
name|name
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Entry
block|{
specifier|private
specifier|final
name|String
name|accessControlledPath
decl_stmt|;
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
specifier|private
specifier|final
name|String
name|principalName
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAllow
decl_stmt|;
specifier|private
name|Entry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|accessControlledPath
parameter_list|,
name|int
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBits
name|privilegeBits
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
block|{
name|this
operator|.
name|accessControlledPath
operator|=
name|accessControlledPath
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|principalName
operator|=
name|principalName
expr_stmt|;
name|this
operator|.
name|privilegeBits
operator|=
name|privilegeBits
expr_stmt|;
name|this
operator|.
name|isAllow
operator|=
name|isAllow
expr_stmt|;
block|}
specifier|private
name|void
name|writeTo
parameter_list|(
name|NodeBuilder
name|principalRoot
parameter_list|)
block|{
name|String
name|entryName
init|=
name|generateName
argument_list|(
name|principalRoot
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|principalRoot
operator|.
name|child
argument_list|(
name|entryName
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"rep:accessControlledPath"
argument_list|,
name|accessControlledPath
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"rep:index"
argument_list|,
name|index
argument_list|)
operator|.
name|setProperty
argument_list|(
name|privilegeBits
operator|.
name|asPropertyState
argument_list|(
literal|"rep:privileges"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: append restrictions
name|PropertyState
name|ordering
init|=
name|principalRoot
operator|.
name|getProperty
argument_list|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordering
operator|==
literal|null
condition|)
block|{
name|principalRoot
operator|.
name|setProperty
argument_list|(
name|TreeImpl
operator|.
name|OAK_CHILD_ORDER
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|entryName
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PropertyBuilder
name|pb
init|=
name|MemoryPropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
name|ordering
argument_list|)
decl_stmt|;
comment|// TODO: determine ordering index
name|int
name|index
init|=
literal|0
decl_stmt|;
name|pb
operator|.
name|setValue
argument_list|(
name|entryName
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|principalRoot
operator|.
name|setProperty
argument_list|(
name|pb
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isSame
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
name|Tree
name|entry
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAllow
operator|==
operator|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'a'
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|privilegeBits
operator|.
name|equals
argument_list|(
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|REP_PRIVILEGES
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|principalName
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|entry
argument_list|,
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|index
operator|!=
name|entry
operator|.
name|getProperty
argument_list|(
literal|"rep:index"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|accessControlledPath
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|entry
argument_list|,
literal|"rep:accessControlledPath"
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// TODO: respect restrictions
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"entry: "
argument_list|)
operator|.
name|append
argument_list|(
name|accessControlledPath
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
operator|.
name|append
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
operator|.
name|append
argument_list|(
name|isAllow
condition|?
literal|"allow"
else|:
literal|"deny"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
operator|.
name|append
argument_list|(
name|privilegeBits
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

