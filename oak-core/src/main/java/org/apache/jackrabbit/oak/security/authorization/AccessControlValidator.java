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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

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
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|Validator
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

begin_comment
comment|/**  * AccessControlValidator... TODO  */
end_comment

begin_class
class|class
name|AccessControlValidator
implements|implements
name|Validator
implements|,
name|AccessControlConstants
block|{
specifier|private
specifier|final
name|Tree
name|parentBefore
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|parentAfter
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
argument_list|>
name|privileges
decl_stmt|;
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
name|AccessControlValidator
parameter_list|(
name|Tree
name|parentBefore
parameter_list|,
name|Tree
name|parentAfter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
argument_list|>
name|privileges
parameter_list|,
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
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
name|privileges
operator|=
name|privileges
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Validator>---
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
if|if
condition|(
name|isAccessControlEntry
argument_list|(
name|parentAfter
argument_list|)
condition|)
block|{
name|checkValidAccessControlEntry
argument_list|(
name|parentAfter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|checkMixinTypes
argument_list|(
name|parentAfter
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
if|if
condition|(
name|isAccessControlEntry
argument_list|(
name|parentAfter
argument_list|)
condition|)
block|{
name|checkValidAccessControlEntry
argument_list|(
name|parentAfter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|checkMixinTypes
argument_list|(
name|parentAfter
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
comment|// nothing to do: mandatory properties will be enforced by node type validator
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
name|Tree
name|treeAfter
init|=
name|checkNotNull
argument_list|(
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|checkValidTree
argument_list|(
name|parentAfter
argument_list|,
name|treeAfter
argument_list|)
expr_stmt|;
return|return
operator|new
name|AccessControlValidator
argument_list|(
literal|null
argument_list|,
name|treeAfter
argument_list|,
name|privileges
argument_list|,
name|restrictionProvider
argument_list|,
name|ntMgr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
name|Tree
name|treeBefore
init|=
name|checkNotNull
argument_list|(
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|treeAfter
init|=
name|checkNotNull
argument_list|(
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|checkValidTree
argument_list|(
name|parentAfter
argument_list|,
name|treeAfter
argument_list|)
expr_stmt|;
return|return
operator|new
name|AccessControlValidator
argument_list|(
name|treeBefore
argument_list|,
name|treeAfter
argument_list|,
name|privileges
argument_list|,
name|restrictionProvider
argument_list|,
name|ntMgr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
comment|// TODO validate acl / ace / restriction removal
return|return
literal|null
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|checkValidTree
parameter_list|(
name|Tree
name|parentAfter
parameter_list|,
name|Tree
name|treeAfter
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isPolicy
argument_list|(
name|treeAfter
argument_list|)
condition|)
block|{
name|checkValidPolicy
argument_list|(
name|parentAfter
argument_list|,
name|treeAfter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAccessControlEntry
argument_list|(
name|treeAfter
argument_list|)
condition|)
block|{
name|checkValidAccessControlEntry
argument_list|(
name|treeAfter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_RESTRICTIONS
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|treeAfter
argument_list|)
argument_list|)
condition|)
block|{
name|checkIsAccessControlEntry
argument_list|(
name|parentAfter
argument_list|)
expr_stmt|;
name|checkValidRestrictions
argument_list|(
name|parentAfter
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isPolicy
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|NT_REP_ACL
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isAccessControlEntry
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
decl_stmt|;
return|return
name|NT_REP_DENY_ACE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_GRANT_ACE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|checkIsAccessControlEntry
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|isAccessControlEntry
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Access control entry node expected."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkValidPolicy
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|Tree
name|policyNode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|mixinType
init|=
operator|(
name|REP_REPO_POLICY
operator|.
name|equals
argument_list|(
name|policyNode
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
condition|?
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
else|:
name|MIX_REP_ACCESS_CONTROLLABLE
decl_stmt|;
name|checkValidAccessControlledNode
argument_list|(
name|parent
argument_list|,
name|mixinType
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|validPolicyNames
init|=
operator|(
name|parent
operator|.
name|isRoot
argument_list|()
operator|)
condition|?
name|POLICY_NODE_NAMES
else|:
name|Collections
operator|.
name|singleton
argument_list|(
name|REP_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|validPolicyNames
operator|.
name|contains
argument_list|(
name|policyNode
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Invalid policy name "
operator|+
name|policyNode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkValidAccessControlledNode
parameter_list|(
name|Tree
name|accessControlledTree
parameter_list|,
name|String
name|requiredMixin
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|AC_NODETYPE_NAMES
operator|.
name|contains
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|accessControlledTree
argument_list|)
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Access control policy within access control content ("
operator|+
name|accessControlledTree
operator|.
name|getPath
argument_list|()
operator|+
literal|')'
argument_list|)
expr_stmt|;
block|}
name|String
name|msg
init|=
literal|"Isolated policy node. Parent is not of type "
operator|+
name|requiredMixin
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|accessControlledTree
argument_list|,
name|requiredMixin
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
operator|.
name|equals
argument_list|(
name|requiredMixin
argument_list|)
condition|)
block|{
name|checkValidRepoAccessControlled
argument_list|(
name|accessControlledTree
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkValidAccessControlEntry
parameter_list|(
name|Tree
name|aceNode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Tree
name|parent
init|=
name|aceNode
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
operator|||
operator|!
name|NT_REP_ACL
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|parent
argument_list|)
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Isolated access control entry at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|checkValidPrincipal
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|aceNode
argument_list|,
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|checkValidPrivileges
argument_list|(
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|aceNode
argument_list|,
name|REP_PRIVILEGES
argument_list|)
argument_list|)
expr_stmt|;
name|checkValidRestrictions
argument_list|(
name|aceNode
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkValidPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|principalName
operator|==
literal|null
operator|||
name|principalName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Missing principal name."
argument_list|)
expr_stmt|;
block|}
comment|// validity of principal is only a JCR specific contract and will not be
comment|// enforced on the oak level.
block|}
specifier|private
name|void
name|checkValidPrivileges
parameter_list|(
name|String
index|[]
name|privilegeNames
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|privilegeNames
operator|==
literal|null
operator|||
name|privilegeNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Missing privileges."
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|privilegeName
range|:
name|privilegeNames
control|)
block|{
if|if
condition|(
name|privilegeName
operator|==
literal|null
operator|||
operator|!
name|privileges
operator|.
name|containsKey
argument_list|(
name|privilegeName
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Invalid privilege "
operator|+
name|privilegeName
argument_list|)
expr_stmt|;
block|}
name|Privilege
name|privilege
init|=
name|privileges
operator|.
name|get
argument_list|(
name|privilegeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|privilege
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Abstract privilege "
operator|+
name|privilegeName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|checkValidRestrictions
parameter_list|(
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|path
decl_stmt|;
name|Tree
name|aclTree
init|=
name|checkNotNull
argument_list|(
name|aceTree
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|aclPath
init|=
name|aclTree
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|REP_REPO_POLICY
operator|.
name|equals
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|aclPath
argument_list|)
argument_list|)
condition|)
block|{
name|path
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|aclPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|path
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|checkMixinTypes
parameter_list|(
name|Tree
name|parentTree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
index|[]
name|mixinNames
init|=
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|parentTree
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixinNames
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|asList
argument_list|(
name|mixinNames
argument_list|)
operator|.
name|contains
argument_list|(
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
argument_list|)
condition|)
block|{
name|checkValidRepoAccessControlled
argument_list|(
name|parentTree
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|checkValidRepoAccessControlled
parameter_list|(
name|Tree
name|accessControlledTree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|accessControlledTree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Only root can store repository level policies."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|fail
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
operator|new
name|AccessControlException
argument_list|(
name|msg
argument_list|)
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

