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
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
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
name|Comparator
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
name|Iterator
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
name|NoSuchElementException
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
name|ImmutableSortedSet
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
name|primitives
operator|.
name|Longs
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
name|ReadStatus
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
name|RestrictionPattern
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

begin_comment
comment|/**  * TODO: WIP  */
end_comment

begin_class
class|class
name|CompiledPermissionImpl
implements|implements
name|CompiledPermissions
implements|,
name|PermissionConstants
block|{
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
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|userTrees
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|groupTrees
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|readPaths
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
name|CompiledPermissionImpl
parameter_list|(
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
name|ImmutableTree
name|permissionsTree
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBitsProvider
name|bitsProvider
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|readPaths
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|principals
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|bitsProvider
operator|=
name|bitsProvider
expr_stmt|;
name|this
operator|.
name|readPaths
operator|=
name|readPaths
expr_stmt|;
name|userTrees
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
argument_list|(
name|principals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|groupTrees
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
argument_list|(
name|principals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|permissionsTree
operator|.
name|exists
argument_list|()
condition|)
block|{
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|Tree
name|t
init|=
name|getPrincipalRoot
argument_list|(
name|permissionsTree
argument_list|,
name|principal
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|target
init|=
name|getTargetMap
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
name|target
operator|.
name|put
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|.
name|remove
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//------------------------------------------------< CompiledPermissions>---
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|permissionsTree
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBitsProvider
name|bitsProvider
parameter_list|)
block|{
name|this
operator|.
name|bitsProvider
operator|=
name|bitsProvider
expr_stmt|;
comment|// test if a permission has been added for those principals that didn't have one before
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|target
init|=
name|getTargetMap
argument_list|(
name|principal
argument_list|)
decl_stmt|;
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|permissionsTree
argument_list|,
name|principal
argument_list|)
decl_stmt|;
name|String
name|pName
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|principalRoot
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|target
operator|.
name|containsKey
argument_list|(
name|pName
argument_list|)
operator|||
operator|!
name|principalRoot
operator|.
name|equals
argument_list|(
name|target
operator|.
name|get
argument_list|(
name|pName
argument_list|)
argument_list|)
condition|)
block|{
name|target
operator|.
name|put
argument_list|(
name|pName
argument_list|,
name|principalRoot
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|target
operator|.
name|remove
argument_list|(
name|pName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|ReadStatus
name|getReadStatus
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
parameter_list|)
block|{
if|if
condition|(
name|isReadablePath
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
condition|)
block|{
return|return
name|ReadStatus
operator|.
name|ALLOW_ALL_REGULAR
return|;
block|}
name|long
name|permission
init|=
operator|(
name|property
operator|==
literal|null
operator|)
condition|?
name|Permissions
operator|.
name|READ_NODE
else|:
name|Permissions
operator|.
name|READ_PROPERTY
decl_stmt|;
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|it
init|=
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PermissionEntry
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|readStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|entry
operator|.
name|readStatus
return|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|privilegeBits
operator|.
name|includesRead
argument_list|(
name|permission
argument_list|)
condition|)
block|{
return|return
operator|(
name|entry
operator|.
name|isAllow
operator|)
condition|?
name|ReadStatus
operator|.
name|ALLOW_THIS
else|:
name|ReadStatus
operator|.
name|DENY_THIS
return|;
block|}
block|}
return|return
name|ReadStatus
operator|.
name|DENY_THIS
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
name|hasPermissions
argument_list|(
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|()
argument_list|)
argument_list|,
name|permissions
argument_list|,
literal|null
argument_list|,
literal|null
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
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|it
init|=
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|hasPermissions
argument_list|(
name|it
argument_list|,
name|permissions
argument_list|,
name|tree
argument_list|,
literal|null
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
name|path
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|it
init|=
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|hasPermissions
argument_list|(
name|it
argument_list|,
name|permissions
argument_list|,
literal|null
argument_list|,
name|path
argument_list|)
return|;
block|}
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
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|getPrivilegeBits
argument_list|(
name|tree
argument_list|)
argument_list|)
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
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
return|return
name|getPrivilegeBits
argument_list|(
name|tree
argument_list|)
operator|.
name|includes
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|privilegeNames
argument_list|)
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Tree
name|getPrincipalRoot
parameter_list|(
name|Tree
name|permissionsTree
parameter_list|,
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|permissionsTree
operator|.
name|getChild
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|getTargetMap
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
return|return
operator|(
name|principal
operator|instanceof
name|Group
operator|)
condition|?
name|groupTrees
else|:
name|userTrees
return|;
block|}
specifier|private
name|boolean
name|hasPermissions
parameter_list|(
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
parameter_list|,
name|long
name|permissions
parameter_list|,
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
block|{
comment|// calculate readable paths if the given permissions includes any read permission.
name|boolean
name|isReadable
init|=
name|Permissions
operator|.
name|diff
argument_list|(
name|Permissions
operator|.
name|READ
argument_list|,
name|permissions
argument_list|)
operator|!=
name|Permissions
operator|.
name|READ
operator|&&
name|isReadablePath
argument_list|(
name|tree
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entries
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|isReadable
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|respectParent
init|=
operator|(
name|tree
operator|!=
literal|null
operator|||
name|path
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|Permissions
operator|.
name|includes
argument_list|(
name|permissions
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
operator|||
name|Permissions
operator|.
name|includes
argument_list|(
name|permissions
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
operator|||
name|Permissions
operator|.
name|includes
argument_list|(
name|permissions
argument_list|,
name|Permissions
operator|.
name|MODIFY_CHILD_NODE_COLLECTION
argument_list|)
operator|)
decl_stmt|;
name|long
name|allows
init|=
operator|(
name|isReadable
operator|)
condition|?
name|Permissions
operator|.
name|READ
else|:
name|Permissions
operator|.
name|NO_PERMISSION
decl_stmt|;
name|long
name|denies
init|=
name|Permissions
operator|.
name|NO_PERMISSION
decl_stmt|;
name|PrivilegeBits
name|allowBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|isReadable
condition|)
block|{
name|allowBits
operator|.
name|add
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PrivilegeBits
name|denyBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|PrivilegeBits
name|parentAllowBits
decl_stmt|;
name|PrivilegeBits
name|parentDenyBits
decl_stmt|;
name|String
name|parentPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|respectParent
condition|)
block|{
name|parentAllowBits
operator|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|parentDenyBits
operator|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|||
name|tree
operator|!=
literal|null
condition|)
block|{
name|parentPath
operator|=
name|PermissionUtil
operator|.
name|getParentPathOrNull
argument_list|(
operator|(
name|path
operator|!=
literal|null
operator|)
condition|?
name|path
else|:
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|parentAllowBits
operator|=
name|PrivilegeBits
operator|.
name|EMPTY
expr_stmt|;
name|parentDenyBits
operator|=
name|PrivilegeBits
operator|.
name|EMPTY
expr_stmt|;
name|parentPath
operator|=
literal|null
expr_stmt|;
block|}
while|while
condition|(
name|entries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PermissionEntry
name|entry
init|=
name|entries
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|respectParent
operator|&&
operator|(
name|parentPath
operator|!=
literal|null
operator|)
condition|)
block|{
name|boolean
name|matchesParent
init|=
name|entry
operator|.
name|matchesParent
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchesParent
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|isAllow
condition|)
block|{
name|parentAllowBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|parentDenyBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentDenyBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|parentAllowBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|entry
operator|.
name|isAllow
condition|)
block|{
name|allowBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|denyBits
argument_list|)
expr_stmt|;
name|long
name|ap
init|=
name|PrivilegeBits
operator|.
name|calculatePermissions
argument_list|(
name|allowBits
argument_list|,
name|parentAllowBits
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|allows
operator||=
name|Permissions
operator|.
name|diff
argument_list|(
name|ap
argument_list|,
name|denies
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|allows
operator||
operator|~
name|permissions
operator|)
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|denyBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|allowBits
argument_list|)
expr_stmt|;
name|long
name|dp
init|=
name|PrivilegeBits
operator|.
name|calculatePermissions
argument_list|(
name|denyBits
argument_list|,
name|parentDenyBits
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|denies
operator||=
name|Permissions
operator|.
name|diff
argument_list|(
name|dp
argument_list|,
name|allows
argument_list|)
expr_stmt|;
if|if
condition|(
name|Permissions
operator|.
name|includes
argument_list|(
name|denies
argument_list|,
name|permissions
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
operator|(
name|allows
operator||
operator|~
name|permissions
operator|)
operator|==
operator|-
literal|1
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeBits
name|getPrivilegeBits
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|)
block|{
name|EntryPredicate
name|pred
init|=
operator|(
name|tree
operator|==
literal|null
operator|)
condition|?
operator|new
name|EntryPredicate
argument_list|()
else|:
operator|new
name|EntryPredicate
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
name|getEntryIterator
argument_list|(
name|pred
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|allowBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|PrivilegeBits
name|denyBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PermissionEntry
name|entry
init|=
name|entries
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|isAllow
condition|)
block|{
name|allowBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|denyBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|denyBits
operator|.
name|addDifference
argument_list|(
name|entry
operator|.
name|privilegeBits
argument_list|,
name|allowBits
argument_list|)
expr_stmt|;
block|}
block|}
comment|// special handling for paths that are always readable
if|if
condition|(
name|isReadablePath
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|allowBits
operator|.
name|add
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allowBits
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntryIterator
parameter_list|(
annotation|@
name|Nonnull
name|EntryPredicate
name|predicate
parameter_list|)
block|{
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|userEntries
init|=
operator|(
name|userTrees
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
name|Iterators
operator|.
expr|<
name|PermissionEntry
operator|>
name|emptyIterator
argument_list|()
else|:
operator|new
name|EntryIterator
argument_list|(
name|userTrees
argument_list|,
name|predicate
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|groupEntries
init|=
operator|(
name|groupTrees
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
name|Iterators
operator|.
expr|<
name|PermissionEntry
operator|>
name|emptyIterator
argument_list|()
else|:
operator|new
name|EntryIterator
argument_list|(
name|groupTrees
argument_list|,
name|predicate
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|userEntries
argument_list|,
name|groupEntries
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isReadablePath
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|String
name|treePath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|readPaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|targetPath
init|=
operator|(
name|tree
operator|!=
literal|null
operator|)
condition|?
name|tree
operator|.
name|getPath
argument_list|()
else|:
name|treePath
decl_stmt|;
if|if
condition|(
name|targetPath
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|path
range|:
name|readPaths
control|)
block|{
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|targetPath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|PermissionEntry
block|{
specifier|private
specifier|final
name|boolean
name|isAllow
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
specifier|private
specifier|final
name|long
name|index
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|RestrictionPattern
name|restriction
decl_stmt|;
specifier|private
name|ReadStatus
name|readStatus
init|=
literal|null
decl_stmt|;
comment|// TODO
specifier|private
name|PermissionEntry
parameter_list|(
name|Tree
name|entryTree
parameter_list|,
name|RestrictionProvider
name|restrictionsProvider
parameter_list|)
block|{
name|isAllow
operator|=
name|entryTree
operator|.
name|getProperty
argument_list|(
name|REP_IS_ALLOW
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|privilegeBits
operator|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|entryTree
operator|.
name|getProperty
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|checkNotNull
argument_list|(
name|entryTree
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|path
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|entryTree
argument_list|,
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|restriction
operator|=
name|restrictionsProvider
operator|.
name|getPattern
argument_list|(
name|path
argument_list|,
name|entryTree
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|matches
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
parameter_list|)
block|{
return|return
name|restriction
operator|.
name|matches
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|matches
parameter_list|(
annotation|@
name|Nonnull
name|String
name|treePath
parameter_list|)
block|{
return|return
name|restriction
operator|.
name|matches
argument_list|(
name|treePath
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|matches
parameter_list|()
block|{
return|return
name|restriction
operator|.
name|matches
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|matchesParent
parameter_list|(
annotation|@
name|Nonnull
name|String
name|parentPath
parameter_list|)
block|{
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|parentPath
argument_list|)
condition|)
block|{
return|return
name|restriction
operator|.
name|matches
argument_list|(
name|parentPath
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
block|}
specifier|private
class|class
name|EntryIterator
implements|implements
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
block|{
specifier|private
specifier|final
name|Collection
argument_list|<
name|Tree
argument_list|>
name|principalTrees
decl_stmt|;
specifier|private
specifier|final
name|EntryPredicate
name|predicate
decl_stmt|;
comment|// the next oak path for which to retrieve permission entries
specifier|private
name|String
name|path
decl_stmt|;
comment|// the ordered permission entries at a given path in the hierarchy
specifier|private
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|nextEntries
init|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
decl_stmt|;
comment|// the next permission entry
specifier|private
name|PermissionEntry
name|next
decl_stmt|;
specifier|private
name|EntryIterator
parameter_list|(
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|principalTrees
parameter_list|,
annotation|@
name|Nonnull
name|EntryPredicate
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|principalTrees
operator|=
name|principalTrees
operator|.
name|values
argument_list|()
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
name|predicate
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|predicate
operator|.
name|path
argument_list|)
expr_stmt|;
name|next
operator|=
name|seekNext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PermissionEntry
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|PermissionEntry
name|pe
init|=
name|next
decl_stmt|;
name|next
operator|=
name|seekNext
argument_list|()
expr_stmt|;
return|return
name|pe
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|PermissionEntry
name|seekNext
parameter_list|()
block|{
comment|// calculate the ordered entries for the next hierarchy level.
while|while
condition|(
operator|!
name|nextEntries
operator|.
name|hasNext
argument_list|()
operator|&&
name|path
operator|!=
literal|null
condition|)
block|{
name|nextEntries
operator|=
name|getNextEntries
argument_list|()
expr_stmt|;
name|path
operator|=
name|PermissionUtil
operator|.
name|getParentPathOrNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextEntries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|nextEntries
operator|.
name|next
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|getNextEntries
parameter_list|()
block|{
name|ImmutableSortedSet
operator|.
name|Builder
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
operator|new
name|ImmutableSortedSet
operator|.
name|Builder
argument_list|(
operator|new
name|EntryComparator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|principalRoot
range|:
name|principalTrees
control|)
block|{
name|String
name|name
init|=
name|PermissionUtil
operator|.
name|getEntryName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Tree
name|parent
init|=
name|principalRoot
decl_stmt|;
while|while
condition|(
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|parent
operator|=
name|parent
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|PermissionEntry
name|pe
init|=
operator|new
name|PermissionEntry
argument_list|(
name|parent
argument_list|,
name|restrictionProvider
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|pe
argument_list|)
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|pe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|entries
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|EntryComparator
implements|implements
name|Comparator
argument_list|<
name|PermissionEntry
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
annotation|@
name|Nonnull
name|PermissionEntry
name|entry
parameter_list|,
annotation|@
name|Nonnull
name|PermissionEntry
name|otherEntry
parameter_list|)
block|{
return|return
name|Longs
operator|.
name|compare
argument_list|(
name|otherEntry
operator|.
name|index
argument_list|,
name|entry
operator|.
name|index
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|EntryPredicate
implements|implements
name|Predicate
argument_list|<
name|PermissionEntry
argument_list|>
block|{
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|property
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|EntryPredicate
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
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|tree
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
specifier|private
name|EntryPredicate
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|property
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|private
name|EntryPredicate
parameter_list|()
block|{
name|this
operator|.
name|tree
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|property
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|PermissionEntry
name|entry
parameter_list|)
block|{
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
return|return
name|entry
operator|.
name|matches
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
return|return
name|entry
operator|.
name|matches
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|entry
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

