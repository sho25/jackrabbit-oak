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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|ValidatorProvider
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|PermissionProvider
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|CommitFailedException
operator|.
name|ACCESS
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
name|CONSTRAINT
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

begin_class
class|class
name|PrincipalPolicyValidatorProvider
extends|extends
name|ValidatorProvider
implements|implements
name|Constants
block|{
specifier|private
specifier|final
name|MgrProvider
name|mgrProvider
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|private
name|TypePredicate
name|isMixPrincipalBased
decl_stmt|;
name|PrincipalPolicyValidatorProvider
parameter_list|(
annotation|@
name|NotNull
name|MgrProvider
name|mgrProvider
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|NotNull
name|String
name|workspaceName
parameter_list|)
block|{
name|this
operator|.
name|mgrProvider
operator|=
name|mgrProvider
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|principals
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|PolicyValidator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|Root
name|rootBefore
init|=
name|mgrProvider
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|permissionProvider
operator|=
name|mgrProvider
operator|.
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPermissionProvider
argument_list|(
name|rootBefore
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|)
expr_stmt|;
name|isMixPrincipalBased
operator|=
operator|new
name|TypePredicate
argument_list|(
name|after
argument_list|,
name|MIX_REP_PRINCIPAL_BASED_MIXIN
argument_list|)
expr_stmt|;
return|return
operator|new
name|PolicyValidator
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
specifier|private
specifier|final
class|class
name|PolicyValidator
extends|extends
name|DefaultValidator
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
name|boolean
name|isNodetypeTree
decl_stmt|;
specifier|private
name|PolicyValidator
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|rootStateBefore
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|rootState
parameter_list|)
block|{
name|mgrProvider
operator|.
name|reset
argument_list|(
name|mgrProvider
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|rootState
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentBefore
operator|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|rootStateBefore
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|rootState
argument_list|)
expr_stmt|;
name|this
operator|.
name|isNodetypeTree
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|PolicyValidator
parameter_list|(
annotation|@
name|NotNull
name|PolicyValidator
name|parentValidator
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|before
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|after
parameter_list|)
block|{
name|this
operator|.
name|parentBefore
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|after
expr_stmt|;
if|if
condition|(
name|parentValidator
operator|.
name|isNodetypeTree
condition|)
block|{
name|this
operator|.
name|isNodetypeTree
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isNodetypeTree
operator|=
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|parentValidator
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|PolicyValidator
parameter_list|(
annotation|@
name|NotNull
name|PolicyValidator
name|parentValidator
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
name|boolean
name|isAfter
parameter_list|)
block|{
name|this
operator|.
name|parentBefore
operator|=
operator|(
name|isAfter
operator|)
condition|?
literal|null
else|:
name|tree
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
operator|(
name|isAfter
operator|)
condition|?
name|tree
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|parentValidator
operator|.
name|isNodetypeTree
condition|)
block|{
name|this
operator|.
name|isNodetypeTree
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isNodetypeTree
operator|=
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|parentValidator
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
name|String
name|getName
parameter_list|()
block|{
return|return
operator|(
name|parentBefore
operator|==
literal|null
operator|)
condition|?
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
operator|.
name|getName
argument_list|()
else|:
name|parentBefore
operator|.
name|getName
argument_list|()
return|;
block|}
comment|//------------------------------------------------------< Validator>---
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
name|String
name|propertyName
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
if|if
condition|(
name|NT_REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
operator|&&
operator|!
name|REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|30
argument_list|,
literal|"Attempt create policy node with different name than '"
operator|+
name|REP_PRINCIPAL_POLICY
operator|+
literal|"'."
argument_list|)
throw|;
block|}
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
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|NT_REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
operator|||
name|NT_REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|31
argument_list|,
literal|"Attempt to change primary type from/to rep:PrincipalPolicy."
argument_list|)
throw|;
block|}
block|}
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
if|if
condition|(
operator|!
name|isNodetypeTree
condition|)
block|{
if|if
condition|(
name|REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|validatePolicyNode
argument_list|(
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
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
name|validateRestrictions
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_PRINCIPAL_ENTRY
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|after
argument_list|)
argument_list|)
condition|)
block|{
name|validateEntry
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|VisibleValidator
argument_list|(
name|nextValidator
argument_list|(
name|name
argument_list|,
name|after
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
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
if|if
condition|(
operator|!
name|isNodetypeTree
condition|)
block|{
if|if
condition|(
name|after
operator|.
name|hasChildNode
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|)
condition|)
block|{
name|Tree
name|parent
init|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|validatePolicyNode
argument_list|(
name|parent
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|)
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
name|validateRestrictions
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_PRINCIPAL_ENTRY
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|after
argument_list|)
argument_list|)
condition|)
block|{
name|validateEntry
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|VisibleValidator
argument_list|(
name|nextValidator
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
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
if|if
condition|(
operator|!
name|isNodetypeTree
condition|)
block|{
name|PropertyState
name|effectivePath
init|=
literal|null
decl_stmt|;
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
name|effectivePath
operator|=
name|verifyNotNull
argument_list|(
name|parentBefore
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REP_EFFECTIVE_PATH
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_PRINCIPAL_ENTRY
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|before
argument_list|)
argument_list|)
condition|)
block|{
name|effectivePath
operator|=
name|before
operator|.
name|getProperty
argument_list|(
name|REP_EFFECTIVE_PATH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|effectivePath
operator|!=
literal|null
operator|&&
operator|!
name|Utils
operator|.
name|hasModAcPermission
argument_list|(
name|permissionProvider
argument_list|,
name|effectivePath
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|PATH
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS
argument_list|,
literal|3
argument_list|,
literal|"Access denied"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|VisibleValidator
argument_list|(
name|nextValidator
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------------------
specifier|private
name|void
name|validatePolicyNode
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|NT_REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|nodeState
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|32
argument_list|,
literal|"Reserved node name 'rep:principalPolicy' must only be used for nodes of type 'rep:PrincipalPolicy'."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isMixPrincipalBased
operator|.
name|apply
argument_list|(
name|parent
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|33
argument_list|,
literal|"Parent node not of mixin type 'rep:PrincipalBasedMixin'."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|validateRestrictions
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|NT_REP_RESTRICTIONS
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|nodeState
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|34
argument_list|,
literal|"Reserved node name 'rep:restrictions' must only be used for nodes of type 'rep:Restrictions'."
argument_list|)
throw|;
block|}
name|Tree
name|parent
init|=
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
decl_stmt|;
if|if
condition|(
name|NT_REP_PRINCIPAL_ENTRY
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
try|try
block|{
name|String
name|oakPath
init|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|parent
argument_list|,
name|REP_EFFECTIVE_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|mgrProvider
operator|.
name|getRestrictionProvider
argument_list|()
operator|.
name|validateRestrictions
argument_list|(
name|oakPath
argument_list|,
name|parent
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
literal|35
argument_list|,
literal|"Invalid restrictions"
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
else|else
block|{
comment|// assert the restrictions node resides within access control content
if|if
condition|(
operator|!
name|mgrProvider
operator|.
name|getContext
argument_list|()
operator|.
name|definesTree
argument_list|(
name|parent
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS_CONTROL
argument_list|,
literal|2
argument_list|,
literal|"Expected access control entry parent (isolated restriction)."
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|validateEntry
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Tree
name|parent
init|=
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
decl_stmt|;
name|String
name|entryPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|REP_PRINCIPAL_POLICY
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|36
argument_list|,
literal|"Isolated entry of principal policy at "
operator|+
name|entryPath
argument_list|)
throw|;
block|}
name|Iterable
argument_list|<
name|String
argument_list|>
name|privilegeNames
init|=
name|nodeState
operator|.
name|getNames
argument_list|(
name|REP_PRIVILEGES
argument_list|)
decl_stmt|;
if|if
condition|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|privilegeNames
argument_list|)
condition|)
block|{
throw|throw
name|accessControlViolation
argument_list|(
literal|37
argument_list|,
literal|"Empty rep:privileges property at "
operator|+
name|entryPath
argument_list|)
throw|;
block|}
name|PrivilegeManager
name|privilegeManager
init|=
name|mgrProvider
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
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
name|accessControlViolation
argument_list|(
literal|38
argument_list|,
literal|"Abstract privilege "
operator|+
name|privilegeName
operator|+
literal|" at "
operator|+
name|entryPath
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
name|accessControlViolation
argument_list|(
literal|39
argument_list|,
literal|"Invalid privilege "
operator|+
name|privilegeName
operator|+
literal|" at "
operator|+
name|entryPath
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
comment|// check mod-access-control permission on the effective path
name|PropertyState
name|effectivePath
init|=
name|nodeState
operator|.
name|getProperty
argument_list|(
name|REP_EFFECTIVE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|effectivePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|21
argument_list|,
literal|"Missing mandatory rep:effectivePath property at "
operator|+
name|entryPath
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Utils
operator|.
name|hasModAcPermission
argument_list|(
name|permissionProvider
argument_list|,
name|effectivePath
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|PATH
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS
argument_list|,
literal|3
argument_list|,
literal|"Access denied"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|CommitFailedException
name|accessControlViolation
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
name|PolicyValidator
name|nextValidator
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|beforeState
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|afterState
parameter_list|)
block|{
name|Tree
name|before
init|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|verifyNotNull
argument_list|(
name|parentBefore
argument_list|)
argument_list|,
name|name
argument_list|,
name|beforeState
argument_list|)
decl_stmt|;
name|Tree
name|after
init|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|verifyNotNull
argument_list|(
name|parentAfter
argument_list|)
argument_list|,
name|name
argument_list|,
name|afterState
argument_list|)
decl_stmt|;
return|return
operator|new
name|PolicyValidator
argument_list|(
name|this
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
specifier|private
name|PolicyValidator
name|nextValidator
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|,
name|boolean
name|isAfter
parameter_list|)
block|{
name|Tree
name|parent
init|=
operator|(
name|isAfter
operator|)
condition|?
name|parentAfter
else|:
name|parentBefore
decl_stmt|;
name|Tree
name|tree
init|=
name|mgrProvider
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|verifyNotNull
argument_list|(
name|parent
argument_list|)
argument_list|,
name|name
argument_list|,
name|nodeState
argument_list|)
decl_stmt|;
return|return
operator|new
name|PolicyValidator
argument_list|(
name|this
argument_list|,
name|tree
argument_list|,
name|isAfter
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|Tree
name|verifyNotNull
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
name|checkState
argument_list|(
name|tree
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|tree
return|;
block|}
block|}
block|}
end_class

end_unit

