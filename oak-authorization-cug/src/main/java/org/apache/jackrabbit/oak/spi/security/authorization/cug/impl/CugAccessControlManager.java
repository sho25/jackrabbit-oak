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
name|cug
operator|.
name|impl
package|;
end_package

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
name|ArrayList
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
name|List
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
name|javax
operator|.
name|annotation
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
name|AccessControlPolicy
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
name|AccessControlPolicyIterator
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
name|Function
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
name|ImmutableSet
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
name|JackrabbitAccessControlPolicy
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
name|principal
operator|.
name|PrincipalManager
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
name|commons
operator|.
name|iterator
operator|.
name|AccessControlPolicyIteratorAdapter
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
name|accesscontrol
operator|.
name|PolicyOwner
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
name|cug
operator|.
name|CugPolicy
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
name|ConfigurationParameters
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
name|SecurityProvider
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
name|accesscontrol
operator|.
name|AbstractAccessControlManager
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
name|Permissions
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
name|principal
operator|.
name|PrincipalConfiguration
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
name|principal
operator|.
name|PrincipalImpl
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
name|PrivilegeConstants
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
name|Type
operator|.
name|NAMES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@link org.apache.jackrabbit.api.security.JackrabbitAccessControlManager}  * interface that allows to create, modify and remove closed user group policies.  */
end_comment

begin_class
class|class
name|CugAccessControlManager
extends|extends
name|AbstractAccessControlManager
implements|implements
name|CugConstants
implements|,
name|PolicyOwner
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
name|CugAccessControlManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|private
specifier|final
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|public
name|CugAccessControlManager
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|securityProvider
argument_list|)
expr_stmt|;
name|config
operator|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
expr_stmt|;
name|principalManager
operator|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------< AccessControlManager>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getSupportedPrivileges
parameter_list|(
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isSupportedPath
argument_list|(
name|getOakPath
argument_list|(
name|absPath
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|new
name|Privilege
index|[]
block|{
name|privilegeFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|Privilege
index|[
literal|0
index|]
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|!=
literal|null
operator|&&
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
name|CugPolicy
name|cug
init|=
name|getCugPolicy
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|cug
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|AccessControlPolicy
index|[]
block|{
name|cug
block|}
return|;
block|}
block|}
return|return
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|getTree
argument_list|(
name|oakPath
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|enabled
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|enabled
condition|)
block|{
name|Root
name|r
init|=
name|getRoot
argument_list|()
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|effective
init|=
operator|new
name|ArrayList
argument_list|<
name|AccessControlPolicy
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
name|CugPolicy
name|cug
init|=
name|getCugPolicy
argument_list|(
name|oakPath
argument_list|,
name|r
operator|.
name|getTree
argument_list|(
name|oakPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cug
operator|!=
literal|null
condition|)
block|{
name|effective
operator|.
name|add
argument_list|(
name|cug
argument_list|)
expr_stmt|;
block|}
block|}
name|oakPath
operator|=
operator|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|oakPath
argument_list|)
operator|)
condition|?
literal|null
else|:
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|oakPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|effective
operator|.
name|toArray
argument_list|(
operator|new
name|AccessControlPolicy
index|[
name|effective
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicyIterator
name|getApplicablePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPath
operator|==
literal|null
operator|||
operator|!
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
return|return
name|AccessControlPolicyIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
name|CugPolicy
name|cug
init|=
name|getCugPolicy
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|cug
operator|==
literal|null
condition|)
block|{
name|cug
operator|=
operator|new
name|CugPolicyImpl
argument_list|(
name|oakPath
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|principalManager
argument_list|,
name|CugUtil
operator|.
name|getImportBehavior
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|AccessControlPolicyIteratorAdapter
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|cug
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|AccessControlPolicyIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removePolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
name|checkValidPolicy
argument_list|(
name|absPath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|oakPath
argument_list|,
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Tree
name|cug
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|cug
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unexpected primary type of node rep:cugPolicy."
argument_list|)
throw|;
block|}
else|else
block|{
comment|// remove the rep:CugMixin if it has been explicitly added upon setPolicy
name|Set
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|TreeUtil
operator|.
name|getNames
argument_list|(
name|tree
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixins
operator|.
name|remove
argument_list|(
name|MIX_REP_CUG_MIXIN
argument_list|)
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|mixins
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot remove mixin type "
operator|+
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
block|}
name|cug
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported path: "
operator|+
name|absPath
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPath
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
name|checkValidPolicy
argument_list|(
name|absPath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|oakPath
argument_list|,
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Tree
name|typeRoot
init|=
name|getRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|TreeUtil
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|MIX_REP_CUG_MIXIN
argument_list|,
name|typeRoot
argument_list|)
condition|)
block|{
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|tree
argument_list|,
name|MIX_REP_CUG_MIXIN
argument_list|,
name|typeRoot
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|Tree
name|cug
decl_stmt|;
if|if
condition|(
name|tree
operator|.
name|hasChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
condition|)
block|{
name|cug
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|cug
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unexpected primary type of node rep:cugPolicy."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|cug
operator|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|tree
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|,
name|typeRoot
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
operator|(
operator|(
name|CugPolicyImpl
operator|)
name|policy
operator|)
operator|.
name|getPrincipalNames
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported path: "
operator|+
name|absPath
argument_list|)
throw|;
block|}
block|}
comment|//-------------------------------------< JackrabbitAccessControlManager>---
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|getApplicablePolicies
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// editing by 'principal' is not supported
return|return
operator|new
name|JackrabbitAccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// editing by 'principal' is not supported
return|return
operator|new
name|JackrabbitAccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// editing by 'principal' is not supported
return|return
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
return|;
block|}
comment|//--------------------------------------------------------< PolicyOwner>---
annotation|@
name|Override
specifier|public
name|boolean
name|defines
parameter_list|(
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|,
annotation|@
name|Nonnull
name|AccessControlPolicy
name|accessControlPolicy
parameter_list|)
block|{
return|return
name|isValidPolicy
argument_list|(
name|absPath
argument_list|,
name|accessControlPolicy
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
name|boolean
name|isSupportedPath
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkValidPath
argument_list|(
name|oakPath
argument_list|)
expr_stmt|;
return|return
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|oakPath
argument_list|,
name|config
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkValidPath
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
name|getTree
argument_list|(
name|oakPath
argument_list|,
name|Permissions
operator|.
name|NO_PERMISSION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
name|CugPolicy
name|getCugPolicy
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getCugPolicy
argument_list|(
name|oakPath
argument_list|,
name|getTree
argument_list|(
name|oakPath
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|CugPolicy
name|getCugPolicy
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|Tree
name|cug
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|cug
argument_list|)
condition|)
block|{
return|return
operator|new
name|CugPolicyImpl
argument_list|(
name|oakPath
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|principalManager
argument_list|,
name|CugUtil
operator|.
name|getImportBehavior
argument_list|(
name|config
argument_list|)
argument_list|,
name|getPrincipals
argument_list|(
name|cug
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|cugTree
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|cugTree
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
name|String
name|principalName
parameter_list|)
block|{
name|Principal
name|principal
init|=
name|principalManager
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unknown principal "
operator|+
name|principalName
argument_list|)
expr_stmt|;
name|principal
operator|=
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
block|}
return|return
name|principal
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isValidPolicy
parameter_list|(
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|,
annotation|@
name|Nonnull
name|AccessControlPolicy
name|policy
parameter_list|)
block|{
return|return
name|policy
operator|instanceof
name|CugPolicyImpl
operator|&&
operator|(
operator|(
name|CugPolicyImpl
operator|)
name|policy
operator|)
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|absPath
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|checkValidPolicy
parameter_list|(
annotation|@
name|Nullable
name|String
name|absPath
parameter_list|,
annotation|@
name|Nonnull
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
operator|(
name|policy
operator|instanceof
name|CugPolicyImpl
operator|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported policy implementation: "
operator|+
name|policy
argument_list|)
throw|;
block|}
name|CugPolicyImpl
name|cug
init|=
operator|(
name|CugPolicyImpl
operator|)
name|policy
decl_stmt|;
if|if
condition|(
operator|!
name|cug
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|absPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Path mismatch: Expected "
operator|+
name|cug
operator|.
name|getPath
argument_list|()
operator|+
literal|", Found: "
operator|+
name|absPath
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

