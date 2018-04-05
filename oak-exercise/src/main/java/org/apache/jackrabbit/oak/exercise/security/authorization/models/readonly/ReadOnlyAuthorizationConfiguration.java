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
name|exercise
operator|.
name|security
operator|.
name|authorization
operator|.
name|models
operator|.
name|readonly
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
name|AccessControlManager
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
name|NamedAccessControlPolicy
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
name|ImmutableList
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
name|Iterators
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
name|tree
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
name|tree
operator|.
name|TreeType
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
name|commit
operator|.
name|MoveTracker
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
name|ThreeWayConflictHandler
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|lifecycle
operator|.
name|WorkspaceInitializer
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
name|ConfigurationBase
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
name|Context
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
name|SecurityConfiguration
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
name|AggregatedPermissionProvider
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
name|EmptyPermissionProvider
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
name|authorization
operator|.
name|permission
operator|.
name|RepositoryPermission
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
name|TreePermission
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
name|principal
operator|.
name|SystemPrincipal
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
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
name|security
operator|.
name|RegistrationConstants
operator|.
name|OAK_SECURITY_NAME
import|;
end_import

begin_comment
comment|/**  *<h1>Read Only Authorization Model</h1>  *  * This authorization module forms part of the training material provided by the  *<i>oak-exercise</i> module and must not be used in a productive environment!  *  *<h3>Overview</h3>  * This simplistic authorization model is limited to permission evaluation and  * doesn't support access control management.  *  * The permission evaluation is hardcoded to only allow read access to every single  * item in the repository (even access control content). All other permissions are  * denied for every set of principals.  *  * There exists a single exception to that rule: For the internal {@link SystemPrincipal}  * permission evaluation is not enforced by this module i.e. this module is skipped.  *  *<h3>Intended Usage</h3>  * This authorization model is intended to be used in 'AND' combination with the  * default authorization setup defined by Oak (and optionally additional models  * such as e.g.<i>oak-authorization-cug</i>.  *  * It is not intended to be used as standalone model as it would grant full read  * access to everyone.  *  *<h3>Limitations</h3>  * Experimental model for training purpose and not intended for usage in production.  *  *<h3>Key Features</h3>  *  *<h4>Access Control Management</h4>  *  *<table align="left">  *<tr><th align="left">Feature</th><th align="left">Description</th></tr>  *<tr><td>Supported Privileges</td><td>all</td></tr>  *<tr><td>Supports Custom Privileges</td><td>yes</td></tr>  *<tr><td>Management by Path</td><td>not supported</td></tr>  *<tr><td>Management by Principals</td><td>not supported</td></tr>  *<tr><td>Owned Policies</td><td>None</td></tr>  *<tr><td>Effective Policies by Path</td><td>for every path a single effective policy of type {@link NamedAccessControlPolicy}</td></tr>  *<tr><td>Effective Policies by Principals</td><td>for every set of principals a single effective policy of type {@link NamedAccessControlPolicy}</td></tr>  *</table>  *  *<h4>Permission Evaluation</h4>  *  *<table>  *<tr><th align="left">Feature</th><th align="left">Description</th></tr>  *<tr><td>Supported Permissions</td><td>all</td></tr>  *<tr><td>Aggregated Permission Provider</td><td>yes</td></tr>  *</table>  *  *<h3>Representation in the Repository</h3>  *  * There exists no dedicated access control or permission content for this  * authorization model as it doesn't persist any information into the repository.  * {@link SecurityConfiguration#getContext()} therefore returns the {@link Context#DEFAULT default}.  *  *<h3>Configuration</h3>  *  * This model doesn't come with any configuration options.  *  *<h3>Installation Instructions</h3>  *  * The following steps are required to install this authorization model in an OSGi based Oak setup.  *  *<ul>  *<li>Upload the oak-exercise bundle</li>  *<li>Edit configuration of {@link org.apache.jackrabbit.oak.security.internal.SecurityProviderRegistration}  *<ul>  *<li>add {@code org.apache.jackrabbit.oak.exercise.security.authorization.models.readonly.ReadOnlyAuthorizationConfiguration}  *             to the list of required service IDs</li>  *<li>make sure the 'Authorization Composition Type' is set to AND</li>  *</ul>  *</li>  *<li>Wait for the {@link SecurityProvider} to be successfully registered again.</li>  *</ul>  *  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
block|{
name|AuthorizationConfiguration
operator|.
name|class
block|,
name|SecurityConfiguration
operator|.
name|class
block|}
argument_list|,
name|immediate
operator|=
literal|true
argument_list|,
name|property
operator|=
name|OAK_SECURITY_NAME
operator|+
literal|"=org.apache.jackrabbit.oak.exercise.security.authorization.models.readonly.ReadOnlyAuthorizationConfiguration"
argument_list|)
specifier|public
specifier|final
class|class
name|ReadOnlyAuthorizationConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|AuthorizationConfiguration
block|{
specifier|private
specifier|static
specifier|final
name|long
name|READ_PERMISSIONS
init|=
name|Permissions
operator|.
name|READ
operator||
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|READ_PRIVILEGE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
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
parameter_list|)
block|{
return|return
operator|new
name|AbstractAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
block|{
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
block|{
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
block|{
return|return
operator|new
name|AccessControlPolicy
index|[]
block|{
name|ReadOnlyPolicy
operator|.
name|INSTANCE
block|}
return|;
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
block|{
return|return
operator|new
name|AccessControlPolicyIteratorAdapter
argument_list|(
name|Iterators
operator|.
name|emptyIterator
argument_list|()
argument_list|)
return|;
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
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|()
throw|;
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
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|()
throw|;
block|}
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
block|{
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
block|{
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
name|set
parameter_list|)
block|{
return|return
operator|new
name|AccessControlPolicy
index|[]
block|{
name|ReadOnlyPolicy
operator|.
name|INSTANCE
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|RestrictionProvider
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
return|return
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|AggregatedPermissionProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrivilegeBits
name|supportedPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PrivilegeBits
name|privilegeBits
parameter_list|)
block|{
return|return
operator|(
name|privilegeBits
operator|!=
literal|null
operator|)
condition|?
name|privilegeBits
else|:
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|supportedPermissions
parameter_list|(
annotation|@
name|Nonnull
name|TreePermission
name|treePermission
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|permissions
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|onlyReadPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreeType
name|type
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
operator|new
name|ReadOnlyPermissions
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{                 }
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|READ_PRIVILEGE_NAMES
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|privs
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|privilegeNames
argument_list|)
decl_stmt|;
name|privs
operator|.
name|removeAll
argument_list|(
name|READ_PRIVILEGE_NAMES
argument_list|)
expr_stmt|;
return|return
name|privs
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
return|return
name|RepositoryPermission
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|TreePermission
name|parentPermission
parameter_list|)
block|{
return|return
name|ReadOnlyPermissions
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|onlyReadPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|jcrActions
parameter_list|)
block|{
return|return
name|onlyReadPermissions
argument_list|(
name|Permissions
operator|.
name|getPermissions
argument_list|(
name|jcrActions
argument_list|,
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|oakPath
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|boolean
name|onlyReadPermissions
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
name|Permissions
operator|.
name|diff
argument_list|(
name|permissions
argument_list|,
name|READ_PERMISSIONS
argument_list|)
operator|==
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|AuthorizationConfiguration
operator|.
name|NAME
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
block|{
return|return
name|WorkspaceInitializer
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryInitializer
name|getRepositoryInitializer
parameter_list|()
block|{
return|return
name|RepositoryInitializer
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|getCommitHooks
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|getValidators
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ThreeWayConflictHandler
argument_list|>
name|getConflictHandlers
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|Context
operator|.
name|DEFAULT
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|ReadOnlyPermissions
implements|implements
name|TreePermission
block|{
specifier|private
specifier|static
specifier|final
name|TreePermission
name|INSTANCE
init|=
operator|new
name|ReadOnlyPermissions
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TreePermission
name|getChildPermission
parameter_list|(
annotation|@
name|Nonnull
name|String
name|childName
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|childState
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadAll
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canReadProperties
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
name|onlyReadPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|long
name|permissions
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|onlyReadPermissions
argument_list|(
name|permissions
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|ReadOnlyPolicy
implements|implements
name|NamedAccessControlPolicy
block|{
specifier|private
specifier|static
specifier|final
name|NamedAccessControlPolicy
name|INSTANCE
init|=
operator|new
name|ReadOnlyPolicy
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Read-only Policy defined by 'ReadOnlyAuthorizationConfiguration'"
return|;
block|}
block|}
block|}
end_class

end_unit
