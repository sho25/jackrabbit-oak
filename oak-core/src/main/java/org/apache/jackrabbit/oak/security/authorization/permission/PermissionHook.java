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
operator|.
name|permission
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
name|core
operator|.
name|ImmutableRoot
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
name|ImmutableTree
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
name|TreeTypeProvider
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
name|spi
operator|.
name|commit
operator|.
name|PostValidationHook
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|permission
operator|.
name|PermissionConstants
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
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionProvider
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
name|spi
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
name|state
operator|.
name|DefaultNodeStateDiff
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
name|base
operator|.
name|Strings
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
name|JCR_SYSTEM
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

begin_comment
comment|/**  * {@code CommitHook} implementation that processes any modification made to  * access control content and updates persisted permission store associated  * with access control related data stored in the repository.  *<p>  * The access control entries are grouped by principal and store below the store root based on the hash value of the  * access controllable path. hash collisions are handled by adding subnodes accordingly.  *<pre>  *   /jcr:system/rep:permissionStore/crx.default  *      /everyone  *          /552423  [rep:PermissionStore]  *              /0     [rep:Permissions]  *              /1     [rep:Permissions]  *              /c0     [rep:PermissionStore]  *                  /0      [rep:Permissions]  *                  /1      [rep:Permissions]  *                  /2      [rep:Permissions]  *              /c1     [rep:PermissionStore]  *                  /0      [rep:Permissions]  *                  /1      [rep:Permissions]  *                  /2      [rep:Permissions]  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|PermissionHook
implements|implements
name|PostValidationHook
implements|,
name|AccessControlConstants
implements|,
name|PermissionConstants
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
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
name|NodeBuilder
name|permissionRoot
decl_stmt|;
specifier|private
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
name|modified
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
name|deleted
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|PermissionHook
parameter_list|(
name|String
name|workspaceName
parameter_list|,
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
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
name|permissionRoot
operator|=
name|getPermissionRoot
argument_list|(
name|rootAfter
argument_list|)
expr_stmt|;
name|ntMgr
operator|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
operator|new
name|ImmutableRoot
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|apply
argument_list|()
expr_stmt|;
return|return
name|rootAfter
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|void
name|apply
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
name|entry
range|:
name|deleted
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Acl
argument_list|>
name|entry
range|:
name|modified
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isACL
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|NT_REP_ACL
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isACE
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|NT_REP_ACE
argument_list|)
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
parameter_list|)
block|{
comment|// permission root has been created during workspace initialization
return|return
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|REP_PERMISSION_STORE
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|workspaceName
argument_list|)
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
return|return
operator|new
name|ImmutableTree
argument_list|(
name|ImmutableTree
operator|.
name|ParentProvider
operator|.
name|UNSUPPORTED
argument_list|,
name|name
argument_list|,
name|nodeState
argument_list|,
name|TreeTypeProvider
operator|.
name|EMPTY
argument_list|)
return|;
block|}
specifier|private
class|class
name|Diff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
name|Diff
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|parentPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|isACL
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|Acl
name|acl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|AfterNode
argument_list|(
name|path
argument_list|,
name|after
argument_list|)
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|acl
operator|.
name|accessControlledPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|Tree
name|beforeTree
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|Tree
name|afterTree
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|isACL
argument_list|(
name|beforeTree
argument_list|)
condition|)
block|{
if|if
condition|(
name|isACL
argument_list|(
name|afterTree
argument_list|)
condition|)
block|{
name|Acl
name|acl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|AfterNode
argument_list|(
name|path
argument_list|,
name|after
argument_list|)
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|acl
operator|.
name|accessControlledPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
comment|// also consider to remove the ACL from removed entries of other principals
name|Acl
name|beforeAcl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|BeforeNode
argument_list|(
name|path
argument_list|,
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|beforeAcl
operator|.
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|acl
operator|.
name|entries
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeAcl
operator|.
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleted
operator|.
name|put
argument_list|(
name|parentPath
argument_list|,
name|beforeAcl
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Acl
name|acl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|BeforeNode
argument_list|(
name|path
argument_list|,
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|put
argument_list|(
name|acl
operator|.
name|accessControlledPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isACL
argument_list|(
name|afterTree
argument_list|)
condition|)
block|{
name|Acl
name|acl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|AfterNode
argument_list|(
name|path
argument_list|,
name|after
argument_list|)
argument_list|)
decl_stmt|;
name|modified
operator|.
name|put
argument_list|(
name|acl
operator|.
name|accessControlledPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ignore hidden nodes
return|return
literal|true
return|;
block|}
name|String
name|path
init|=
name|parentPath
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
if|if
condition|(
name|isACL
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|Acl
name|acl
init|=
operator|new
name|Acl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|,
operator|new
name|BeforeNode
argument_list|(
name|path
argument_list|,
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|put
argument_list|(
name|acl
operator|.
name|accessControlledPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|EMPTY_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|private
specifier|abstract
specifier|static
class|class
name|Node
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|Node
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
specifier|abstract
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|BeforeNode
extends|extends
name|Node
block|{
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
name|BeforeNode
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
name|super
argument_list|(
name|parentPath
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
specifier|final
class|class
name|AfterNode
extends|extends
name|Node
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
name|AfterNode
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|path
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
annotation|@
name|Override
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
class|class
name|Acl
block|{
specifier|private
specifier|final
name|String
name|accessControlledPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AcEntry
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AcEntry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Acl
parameter_list|(
name|String
name|aclPath
parameter_list|,
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|REP_REPO_POLICY
argument_list|)
condition|)
block|{
name|this
operator|.
name|accessControlledPath
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|accessControlledPath
operator|=
name|aclPath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|"/"
else|:
name|aclPath
expr_stmt|;
block|}
name|nodeName
operator|=
name|PermissionUtil
operator|.
name|getEntryName
argument_list|(
name|accessControlledPath
argument_list|)
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
name|Tree
name|aclTree
init|=
name|getTree
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|aclTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|isACE
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|AcEntry
name|entry
init|=
operator|new
name|AcEntry
argument_list|(
name|child
argument_list|,
name|accessControlledPath
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AcEntry
argument_list|>
name|list
init|=
name|entries
operator|.
name|get
argument_list|(
name|entry
operator|.
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|AcEntry
argument_list|>
argument_list|()
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|entry
operator|.
name|principalName
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|remove
parameter_list|()
block|{
name|String
name|msg
init|=
literal|"Unable to remove permission entry"
decl_stmt|;
for|for
control|(
name|String
name|principalName
range|:
name|entries
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|permissionRoot
operator|.
name|hasChildNode
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|NodeBuilder
name|principalRoot
init|=
name|permissionRoot
operator|.
name|getChildNode
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
comment|// find the ACL node that for this path and principal
name|NodeBuilder
name|parent
init|=
name|principalRoot
operator|.
name|getChildNode
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// check if the node is the correct one
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|parent
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
comment|// remove and reconnect child nodes
name|NodeBuilder
name|newParent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|NodeBuilder
name|child
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newParent
operator|==
literal|null
condition|)
block|{
name|newParent
operator|=
name|child
expr_stmt|;
block|}
else|else
block|{
name|newParent
operator|.
name|setChildNode
argument_list|(
name|childName
argument_list|,
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|child
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|parent
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
name|principalRoot
operator|.
name|setChildNode
argument_list|(
name|nodeName
argument_list|,
name|newParent
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// check if any of the child nodes match
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|NodeBuilder
name|child
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|child
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
comment|// remove child
name|child
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"{} {}: Principal root missing."
argument_list|,
name|msg
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|update
parameter_list|()
block|{
for|for
control|(
name|String
name|principalName
range|:
name|entries
operator|.
name|keySet
argument_list|()
control|)
block|{
name|NodeBuilder
name|principalRoot
init|=
name|permissionRoot
operator|.
name|child
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|principalRoot
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|principalRoot
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
name|parent
init|=
name|principalRoot
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|parent
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
comment|// check if current parent already has the correct path
if|if
condition|(
name|parent
operator|.
name|hasProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|parent
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
comment|// hash collision, find a new child
name|NodeBuilder
name|child
init|=
literal|null
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|child
operator|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
expr_stmt|;
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|child
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
break|break;
block|}
name|child
operator|=
literal|null
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|String
name|name
init|=
literal|"c"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|idx
operator|++
argument_list|)
decl_stmt|;
name|child
operator|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
name|child
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|child
operator|=
name|parent
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|child
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
block|}
name|parent
operator|=
name|child
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
name|accessControlledPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// new parent
name|parent
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
name|accessControlledPath
argument_list|)
expr_stmt|;
block|}
name|updateEntries
argument_list|(
name|parent
argument_list|,
name|entries
operator|.
name|get
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|updateEntries
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|List
argument_list|<
name|AcEntry
argument_list|>
name|list
parameter_list|)
block|{
comment|// remove old entries
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|AcEntry
name|ace
range|:
name|list
control|)
block|{
name|NodeBuilder
name|n
init|=
name|parent
operator|.
name|child
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ace
operator|.
name|index
argument_list|)
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSIONS
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_IS_ALLOW
argument_list|,
name|ace
operator|.
name|isAllow
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_INDEX
argument_list|,
name|ace
operator|.
name|index
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ace
operator|.
name|privilegeBits
operator|.
name|asPropertyState
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Restriction
name|restriction
range|:
name|ace
operator|.
name|restrictions
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
class|class
name|AcEntry
block|{
specifier|private
specifier|final
name|String
name|accessControlledPath
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
specifier|final
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
decl_stmt|;
specifier|private
specifier|final
name|long
name|index
decl_stmt|;
specifier|private
name|int
name|hashCode
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|AcEntry
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|aceTree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|accessControlledPath
parameter_list|,
name|long
name|index
parameter_list|)
block|{
name|this
operator|.
name|accessControlledPath
operator|=
name|accessControlledPath
expr_stmt|;
name|principalName
operator|=
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
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
argument_list|)
expr_stmt|;
name|privilegeBits
operator|=
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
expr_stmt|;
name|isAllow
operator|=
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
expr_stmt|;
name|restrictions
operator|=
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|accessControlledPath
argument_list|)
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
operator|-
literal|1
condition|)
block|{
name|hashCode
operator|=
name|Objects
operator|.
name|hashCode
argument_list|(
name|accessControlledPath
argument_list|,
name|principalName
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|AcEntry
condition|)
block|{
name|AcEntry
name|other
init|=
operator|(
name|AcEntry
operator|)
name|o
decl_stmt|;
return|return
name|isAllow
operator|==
name|other
operator|.
name|isAllow
operator|&&
name|privilegeBits
operator|.
name|equals
argument_list|(
name|other
operator|.
name|privilegeBits
argument_list|)
operator|&&
name|principalName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|principalName
argument_list|)
operator|&&
name|accessControlledPath
operator|.
name|equals
argument_list|(
name|other
operator|.
name|accessControlledPath
argument_list|)
operator|&&
name|restrictions
operator|.
name|equals
argument_list|(
name|other
operator|.
name|restrictions
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
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
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|privilegeBits
argument_list|)
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
name|restrictions
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

