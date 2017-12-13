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
name|accesscontrol
package|;
end_package

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
name|Iterables
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
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|plugins
operator|.
name|nodetype
operator|.
name|TypePredicate
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
name|tree
operator|.
name|TreeConstants
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
name|tree
operator|.
name|TreeProvider
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
name|tree
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
name|oak
operator|.
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|AbstractTree
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
name|DefaultValidator
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
name|commit
operator|.
name|VisibleValidator
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
name|CommitFailedException
operator|.
name|ACCESS_CONTROL
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
name|CommitFailedException
operator|.
name|OAK
import|;
end_import

begin_comment
comment|/**  * Validation for access control information changed by regular JCR (and Jackrabbit)  * access control management API.  */
end_comment

begin_class
class|class
name|AccessControlValidator
extends|extends
name|DefaultValidator
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|final
name|Tree
name|parentAfter
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|privilegeBitsProvider
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|TypePredicate
name|isRepoAccessControllable
decl_stmt|;
specifier|private
specifier|final
name|TypePredicate
name|isAccessControllable
decl_stmt|;
name|AccessControlValidator
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|parentAfter
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeManager
name|privilegeManager
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBitsProvider
name|privilegeBitsProvider
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
annotation|@
name|Nonnull
name|TreeProvider
name|treeProvider
parameter_list|)
block|{
name|this
operator|.
name|parentAfter
operator|=
name|treeProvider
operator|.
name|createReadOnlyTree
argument_list|(
name|parentAfter
argument_list|)
expr_stmt|;
name|this
operator|.
name|privilegeBitsProvider
operator|=
name|privilegeBitsProvider
expr_stmt|;
name|this
operator|.
name|privilegeManager
operator|=
name|privilegeManager
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|isRepoAccessControllable
operator|=
operator|new
name|TypePredicate
argument_list|(
name|parentAfter
argument_list|,
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAccessControllable
operator|=
operator|new
name|TypePredicate
argument_list|(
name|parentAfter
argument_list|,
name|MIX_REP_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|AccessControlValidator
parameter_list|(
name|AccessControlValidator
name|parent
parameter_list|,
name|Tree
name|parentAfter
parameter_list|)
block|{
name|this
operator|.
name|parentAfter
operator|=
name|parentAfter
expr_stmt|;
name|this
operator|.
name|privilegeBitsProvider
operator|=
name|parent
operator|.
name|privilegeBitsProvider
expr_stmt|;
name|this
operator|.
name|privilegeManager
operator|=
name|parent
operator|.
name|privilegeManager
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|parent
operator|.
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|isRepoAccessControllable
operator|=
name|parent
operator|.
name|isRepoAccessControllable
expr_stmt|;
name|this
operator|.
name|isAccessControllable
operator|=
name|parent
operator|.
name|isAccessControllable
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
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|newValidator
argument_list|(
name|this
argument_list|,
name|treeAfter
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
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|newValidator
argument_list|(
name|this
argument_list|,
name|treeAfter
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
comment|// nothing to do
return|return
literal|null
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|Validator
name|newValidator
parameter_list|(
name|AccessControlValidator
name|parent
parameter_list|,
name|Tree
name|parentAfter
parameter_list|)
block|{
return|return
operator|new
name|VisibleValidator
argument_list|(
operator|new
name|AccessControlValidator
argument_list|(
name|parent
argument_list|,
name|parentAfter
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkValidTree
parameter_list|(
name|Tree
name|parentAfter
parameter_list|,
name|Tree
name|treeAfter
parameter_list|,
name|NodeState
name|nodeAfter
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
argument_list|,
name|nodeAfter
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
throw|throw
name|accessViolation
argument_list|(
literal|2
argument_list|,
literal|"Access control entry node expected at "
operator|+
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
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
name|policyTree
parameter_list|,
name|NodeState
name|policyNode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|REP_REPO_POLICY
operator|.
name|equals
argument_list|(
name|policyTree
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|checkValidAccessControlledNode
argument_list|(
name|parent
argument_list|,
name|isRepoAccessControllable
argument_list|)
expr_stmt|;
name|checkValidRepoAccessControlled
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkValidAccessControlledNode
argument_list|(
name|parent
argument_list|,
name|isAccessControllable
argument_list|)
expr_stmt|;
block|}
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
name|policyTree
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|3
argument_list|,
literal|"Invalid policy name "
operator|+
name|policyTree
operator|.
name|getName
argument_list|()
operator|+
literal|" at "
operator|+
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|policyNode
operator|.
name|hasProperty
argument_list|(
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|4
argument_list|,
literal|"Invalid policy node at "
operator|+
name|policyTree
operator|.
name|getPath
argument_list|()
operator|+
literal|": Order of children is not stable."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|Entry
argument_list|>
name|aceSet
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|policyTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|isAccessControlEntry
argument_list|(
name|child
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|aceSet
operator|.
name|add
argument_list|(
operator|new
name|Entry
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|child
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|13
argument_list|,
literal|"Duplicate ACE '"
operator|+
name|child
operator|.
name|getPath
argument_list|()
operator|+
literal|"' found in policy"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|checkValidAccessControlledNode
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|accessControlledTree
parameter_list|,
annotation|@
name|Nonnull
name|TypePredicate
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
throw|throw
name|accessViolation
argument_list|(
literal|5
argument_list|,
literal|"Access control policy within access control content ("
operator|+
name|accessControlledTree
operator|.
name|getPath
argument_list|()
operator|+
literal|')'
argument_list|)
throw|;
block|}
name|NodeState
name|ns
init|=
operator|(
name|accessControlledTree
operator|instanceof
name|AbstractTree
operator|)
condition|?
operator|(
operator|(
name|AbstractTree
operator|)
name|accessControlledTree
operator|)
operator|.
name|getNodeState
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|requiredMixin
operator|.
name|apply
argument_list|(
name|ns
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Isolated policy node ("
operator|+
name|accessControlledTree
operator|.
name|getPath
argument_list|()
operator|+
literal|"). Parent is not of type "
operator|+
name|requiredMixin
decl_stmt|;
throw|throw
name|accessViolation
argument_list|(
literal|6
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|checkValidAccessControlEntry
parameter_list|(
annotation|@
name|Nonnull
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
operator|!
name|parent
operator|.
name|exists
argument_list|()
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
throw|throw
name|accessViolation
argument_list|(
literal|7
argument_list|,
literal|"Isolated access control entry at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
name|checkValidPrincipal
argument_list|(
name|aceNode
argument_list|)
expr_stmt|;
name|checkValidPrivileges
argument_list|(
name|aceNode
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
annotation|@
name|Nonnull
name|Tree
name|aceNode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|principalName
init|=
name|TreeUtil
operator|.
name|getString
argument_list|(
name|aceNode
argument_list|,
name|REP_PRINCIPAL_NAME
argument_list|)
decl_stmt|;
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
throw|throw
name|accessViolation
argument_list|(
literal|8
argument_list|,
literal|"Missing principal name at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
comment|// validity of principal is only a JCR specific contract and will not be
comment|// enforced on the oak level.
block|}
specifier|private
name|void
name|checkValidPrivileges
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|aceNode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|privilegeNames
init|=
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|aceNode
argument_list|,
name|REP_PRIVILEGES
argument_list|)
decl_stmt|;
if|if
condition|(
name|privilegeNames
operator|==
literal|null
operator|||
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|privilegeNames
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|9
argument_list|,
literal|"Missing privileges at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|privilegeName
range|:
name|privilegeNames
control|)
block|{
try|try
block|{
name|Privilege
name|privilege
init|=
name|privilegeManager
operator|.
name|getPrivilege
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
throw|throw
name|accessViolation
argument_list|(
literal|11
argument_list|,
literal|"Abstract privilege "
operator|+
name|privilegeName
operator|+
literal|" at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|10
argument_list|,
literal|"Invalid privilege "
operator|+
name|privilegeName
operator|+
literal|" at "
operator|+
name|aceNode
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Failed to read privileges"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|checkValidRestrictions
parameter_list|(
annotation|@
name|Nonnull
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
name|ACCESS_CONTROL
argument_list|,
literal|1
argument_list|,
literal|"Access control violation"
argument_list|,
name|e
argument_list|)
throw|;
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
name|OAK
argument_list|,
literal|13
argument_list|,
literal|"Internal error"
argument_list|,
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinNames
init|=
name|TreeUtil
operator|.
name|getNames
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
name|Iterables
operator|.
name|contains
argument_list|(
name|mixinNames
argument_list|,
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
throw|throw
name|accessViolation
argument_list|(
literal|12
argument_list|,
literal|"Only root can store repository level policies ("
operator|+
name|accessControlledTree
operator|.
name|getPath
argument_list|()
operator|+
literal|')'
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|CommitFailedException
name|accessViolation
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS_CONTROL
argument_list|,
name|code
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|private
specifier|final
class|class
name|Entry
block|{
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
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
decl_stmt|;
specifier|private
name|Entry
parameter_list|(
name|String
name|path
parameter_list|,
name|Tree
name|aceTree
parameter_list|)
block|{
name|principalName
operator|=
name|aceTree
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|privilegeBits
operator|=
name|privilegeBitsProvider
operator|.
name|getBits
argument_list|(
name|aceTree
operator|.
name|getProperty
argument_list|(
name|REP_PRIVILEGES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|restrictions
operator|=
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|path
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|principalName
argument_list|,
name|privilegeBits
argument_list|,
name|restrictions
argument_list|)
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
name|Entry
condition|)
block|{
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|principalName
argument_list|,
name|other
operator|.
name|principalName
argument_list|)
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
block|}
block|}
end_class

end_unit

