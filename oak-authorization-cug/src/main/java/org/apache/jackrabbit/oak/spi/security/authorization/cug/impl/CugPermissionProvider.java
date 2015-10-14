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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|Predicates
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
name|plugins
operator|.
name|tree
operator|.
name|RootFactory
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

begin_class
class|class
name|CugPermissionProvider
implements|implements
name|AggregatedPermissionProvider
implements|,
name|CugConstants
block|{
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
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
decl_stmt|;
specifier|private
specifier|final
name|Context
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|SupportedPaths
name|supportedPaths
decl_stmt|;
specifier|private
name|Root
name|immutableRoot
decl_stmt|;
name|CugPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
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
name|Set
argument_list|<
name|String
argument_list|>
name|supportedPaths
parameter_list|,
annotation|@
name|Nonnull
name|Context
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|immutableRoot
operator|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|principalNames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|principals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|Iterables
operator|.
name|filter
argument_list|(
name|principals
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
control|)
block|{
name|principalNames
operator|.
name|add
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|supportedPaths
operator|=
operator|new
name|SupportedPaths
argument_list|(
name|supportedPaths
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
block|}
comment|//-------------------------------------------------< PermissionProvider>---
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|immutableRoot
operator|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|tree
operator|!=
literal|null
operator|&&
name|canRead
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|READ_PRIVILEGE_NAMES
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
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
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
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
operator|!
name|READ_PRIVILEGE_NAMES
operator|.
name|contains
argument_list|(
name|privilegeName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|canRead
argument_list|(
name|tree
argument_list|)
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
name|Tree
name|immutableTree
init|=
name|getImmutableTree
argument_list|(
name|tree
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|TreePermission
operator|.
name|EMPTY
operator|==
name|parentPermission
operator|)
operator|&&
operator|!
name|immutableTree
operator|.
name|isRoot
argument_list|()
operator|)
operator|||
name|isAcContent
argument_list|(
name|immutableTree
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|TreePermission
operator|.
name|EMPTY
return|;
block|}
name|TreePermission
name|tp
decl_stmt|;
if|if
condition|(
name|parentPermission
operator|instanceof
name|CugTreePermission
condition|)
block|{
name|tp
operator|=
name|createCugPermission
argument_list|(
name|immutableTree
argument_list|,
operator|(
name|CugTreePermission
operator|)
name|parentPermission
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|path
init|=
name|immutableTree
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|tp
operator|=
name|createCugPermission
argument_list|(
name|immutableTree
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|supportedPaths
operator|.
name|mayContainCug
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|tp
operator|=
operator|new
name|EmptyCugTreePermission
argument_list|(
name|immutableTree
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tp
operator|=
name|TreePermission
operator|.
name|EMPTY
expr_stmt|;
block|}
block|}
return|return
name|tp
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
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|isRead
argument_list|(
name|permissions
argument_list|)
condition|)
block|{
return|return
name|canRead
argument_list|(
name|tree
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
name|TreeLocation
name|location
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|immutableRoot
argument_list|,
name|oakPath
argument_list|)
decl_stmt|;
name|boolean
name|isAcContent
init|=
name|isAcContent
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|long
name|permissions
init|=
name|Permissions
operator|.
name|getPermissions
argument_list|(
name|jcrActions
argument_list|,
name|location
argument_list|,
name|isAcContent
argument_list|)
decl_stmt|;
return|return
name|isGranted
argument_list|(
name|location
argument_list|,
name|permissions
argument_list|)
return|;
block|}
comment|//---------------------------------------< AggregatedPermissionProvider>---
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
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
name|PrivilegeBits
operator|.
name|EMPTY
return|;
block|}
name|PrivilegeBits
name|pb
decl_stmt|;
if|if
condition|(
name|privilegeBits
operator|==
literal|null
condition|)
block|{
name|pb
operator|=
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pb
operator|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|privilegeBits
argument_list|)
expr_stmt|;
name|pb
operator|.
name|retain
argument_list|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pb
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|includesCug
argument_list|(
name|tree
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|PrivilegeBits
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
name|pb
return|;
block|}
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
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
comment|// repository level permissions are not supported
return|return
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
name|long
name|supported
init|=
name|permissions
operator|&
name|Permissions
operator|.
name|READ
decl_stmt|;
if|if
condition|(
name|supported
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
operator|&&
name|includesCug
argument_list|(
name|tree
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|supported
return|;
block|}
else|else
block|{
return|return
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
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
name|long
name|supported
init|=
name|permissions
operator|&
name|Permissions
operator|.
name|READ
decl_stmt|;
if|if
condition|(
name|supported
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
operator|&&
name|includesCug
argument_list|(
name|getTreeFromLocation
argument_list|(
name|location
argument_list|,
name|location
operator|.
name|getProperty
argument_list|()
argument_list|)
argument_list|,
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|supported
return|;
block|}
else|else
block|{
return|return
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
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
name|propertyState
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|long
name|supported
init|=
name|permissions
operator|&
name|Permissions
operator|.
name|READ
decl_stmt|;
if|if
condition|(
name|supported
operator|!=
name|Permissions
operator|.
name|NO_PERMISSION
operator|&&
operator|(
name|treePermission
operator|instanceof
name|CugTreePermission
operator|)
condition|)
block|{
return|return
name|supported
return|;
block|}
else|else
block|{
return|return
name|Permissions
operator|.
name|NO_PERMISSION
return|;
block|}
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
if|if
condition|(
name|isRead
argument_list|(
name|permissions
argument_list|)
condition|)
block|{
name|PropertyState
name|property
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|getTreeFromLocation
argument_list|(
name|location
argument_list|,
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
return|return
name|isGranted
argument_list|(
name|tree
argument_list|,
name|property
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|boolean
name|isRead
parameter_list|(
name|long
name|permission
parameter_list|)
block|{
return|return
name|permission
operator|==
name|Permissions
operator|.
name|READ_NODE
operator|||
name|permission
operator|==
name|Permissions
operator|.
name|READ_PROPERTY
operator|||
name|permission
operator|==
name|Permissions
operator|.
name|READ
return|;
block|}
specifier|private
specifier|static
name|boolean
name|hasCug
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|exists
argument_list|()
operator|&&
name|tree
operator|.
name|hasChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isAcContent
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|boolean
name|testForCtxRoot
parameter_list|)
block|{
return|return
operator|(
name|testForCtxRoot
operator|)
condition|?
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
else|:
name|ctx
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isAcContent
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|includesCug
parameter_list|(
annotation|@
name|CheckForNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
return|return
name|tree
operator|!=
literal|null
operator|&&
name|getCugRoot
argument_list|(
name|tree
argument_list|,
name|path
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the {@code tree} that holds a CUG policy in the ancestry of the      * given {@code tree} with the specified {@code path} or {@code null} if no      * such tree exists and thus no CUG is effective at the specified path.      *      * @param tree The target tree.      * @param path The path of the given target tree.      * @return the {@code tree} holding the CUG policy that effects the specified      * path or {@code null} if no such policy exists.      */
annotation|@
name|CheckForNull
specifier|private
name|Tree
name|getCugRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Tree
name|immutableTree
init|=
name|getImmutableTree
argument_list|(
name|tree
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasCug
argument_list|(
name|immutableTree
argument_list|)
condition|)
block|{
return|return
name|immutableTree
return|;
block|}
name|String
name|parentPath
decl_stmt|;
while|while
condition|(
operator|!
name|immutableTree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|parentPath
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|supportedPaths
operator|.
name|includes
argument_list|(
name|parentPath
argument_list|)
condition|)
block|{
break|break;
block|}
name|immutableTree
operator|=
name|immutableTree
operator|.
name|getParent
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasCug
argument_list|(
name|immutableTree
argument_list|)
condition|)
block|{
return|return
name|immutableTree
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|canRead
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|Tree
name|immutableTree
init|=
name|getImmutableTree
argument_list|(
name|tree
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAcContent
argument_list|(
name|immutableTree
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// cug defining access control content is not accessible
return|return
literal|false
return|;
block|}
name|Tree
name|cugRoot
init|=
name|getCugRoot
argument_list|(
name|immutableTree
argument_list|,
name|immutableTree
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|cugRoot
operator|!=
literal|null
operator|&&
name|createCugPermission
argument_list|(
name|cugRoot
argument_list|,
literal|null
argument_list|)
operator|.
name|canRead
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|TreeUtil
operator|.
name|isReadOnlyTree
argument_list|(
name|tree
argument_list|)
condition|?
name|tree
else|:
name|immutableRoot
operator|.
name|getTree
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|Tree
name|getTreeFromLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|,
annotation|@
name|CheckForNull
name|PropertyState
name|property
parameter_list|)
block|{
name|Tree
name|tree
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|location
operator|.
name|getTree
argument_list|()
else|:
name|location
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
while|while
condition|(
name|tree
operator|==
literal|null
operator|&&
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|location
operator|=
name|location
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|tree
operator|=
name|location
operator|.
name|getTree
argument_list|()
expr_stmt|;
block|}
return|return
name|tree
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|TreePermission
name|createCugPermission
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|CugTreePermission
name|parent
parameter_list|)
block|{
name|TreePermission
name|tp
decl_stmt|;
name|Tree
name|cugTree
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|PropertyState
name|princNamesState
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
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|cugTree
argument_list|)
operator|&&
name|princNamesState
operator|!=
literal|null
condition|)
block|{
comment|// a new (possibly nested) cug starts off here
name|boolean
name|allow
init|=
name|Iterables
operator|.
name|any
argument_list|(
name|princNamesState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|principalName
parameter_list|)
block|{
return|return
operator|(
name|principalName
operator|!=
literal|null
operator|)
operator|&&
name|principalNames
operator|.
name|contains
argument_list|(
name|principalName
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|tp
operator|=
operator|new
name|CugTreePermission
argument_list|(
name|tree
argument_list|,
name|allow
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
comment|// still within the parents CUG
name|tp
operator|=
operator|new
name|CugTreePermission
argument_list|(
name|tree
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tp
operator|=
operator|new
name|EmptyCugTreePermission
argument_list|(
name|tree
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|tp
return|;
block|}
block|}
end_class

end_unit

