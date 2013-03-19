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
name|Collections
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
name|ImmutableSortedMap
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
name|security
operator|.
name|authorization
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
name|ImmutableTree
argument_list|>
name|trees
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|repoEntries
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|userEntries
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|groupEntries
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
name|trees
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ImmutableTree
argument_list|>
argument_list|(
name|principals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buildEntries
argument_list|(
name|permissionsTree
argument_list|)
expr_stmt|;
block|}
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
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
comment|// test if a permission has been added for those principals that didn't have one before
if|if
condition|(
name|trees
operator|.
name|size
argument_list|()
operator|!=
name|principals
operator|.
name|size
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
if|if
condition|(
operator|!
name|trees
operator|.
name|containsKey
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|getPrincipalRoot
argument_list|(
name|permissionsTree
argument_list|,
name|principal
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|refresh
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// test if any of the trees has been modified in the mean time
if|if
condition|(
operator|!
name|refresh
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ImmutableTree
argument_list|>
name|entry
range|:
name|trees
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ImmutableTree
name|t
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ImmutableTree
name|t2
init|=
name|permissionsTree
operator|.
name|getChild
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t2
operator|!=
literal|null
operator|&&
operator|!
name|t
operator|.
name|getNodeState
argument_list|()
operator|.
name|equals
argument_list|(
name|t2
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|refresh
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|refresh
condition|)
block|{
name|buildEntries
argument_list|(
name|permissionsTree
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------< CompiledPermissions>---
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
for|for
control|(
name|PermissionEntry
name|entry
range|:
name|filterEntries
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|privilegeBits
operator|.
name|includesRead
argument_list|(
name|Permissions
operator|.
name|READ_NODE
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|isAllow
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
for|for
control|(
name|PermissionEntry
name|entry
range|:
name|filterEntries
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|privilegeBits
operator|.
name|includesRead
argument_list|(
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|isAllow
return|;
block|}
block|}
return|return
literal|false
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
literal|null
argument_list|,
name|permissions
argument_list|,
name|repoEntries
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|hasPermissions
argument_list|(
name|tree
argument_list|,
name|permissions
argument_list|,
name|filterEntries
argument_list|(
name|tree
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
return|return
name|hasPermissions
argument_list|(
name|parent
argument_list|,
name|permissions
argument_list|,
name|filterEntries
argument_list|(
name|parent
argument_list|,
name|property
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
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
name|CheckForNull
specifier|private
specifier|static
name|ImmutableTree
name|getPrincipalRoot
parameter_list|(
name|ImmutableTree
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
specifier|private
name|void
name|buildEntries
parameter_list|(
annotation|@
name|Nullable
name|ImmutableTree
name|permissionsTree
parameter_list|)
block|{
if|if
condition|(
name|permissionsTree
operator|==
literal|null
condition|)
block|{
name|repoEntries
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|userEntries
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|groupEntries
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|EntriesBuilder
name|builder
init|=
operator|new
name|EntriesBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|ImmutableTree
name|t
init|=
name|getPrincipalRoot
argument_list|(
name|permissionsTree
argument_list|,
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|trees
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
name|builder
operator|.
name|addEntries
argument_list|(
name|principal
argument_list|,
name|t
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
block|}
name|repoEntries
operator|=
name|builder
operator|.
name|repoEntries
operator|.
name|build
argument_list|()
expr_stmt|;
name|userEntries
operator|=
name|builder
operator|.
name|userEntries
operator|.
name|build
argument_list|()
expr_stmt|;
name|groupEntries
operator|=
name|builder
operator|.
name|groupEntries
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|PermissionEntry
argument_list|>
name|filterEntries
parameter_list|(
specifier|final
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
specifier|final
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|userEntries
operator|.
name|values
argument_list|()
argument_list|,
name|groupEntries
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PermissionEntry
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
name|PermissionEntry
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|!=
literal|null
operator|&&
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
block|}
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|hasPermissions
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|tree
parameter_list|,
name|long
name|permissions
parameter_list|,
name|Iterable
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
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
comment|// TODO
return|return
name|PrivilegeBits
operator|.
name|EMPTY
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Key
implements|implements
name|Comparable
argument_list|<
name|Key
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
specifier|final
name|long
name|index
decl_stmt|;
specifier|private
name|Key
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
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
name|tree
argument_list|,
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|=
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|index
operator|=
name|checkNotNull
argument_list|(
name|tree
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
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Key
name|key
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|path
argument_list|,
name|key
operator|.
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|index
operator|==
name|key
operator|.
name|index
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|index
operator|<
name|key
operator|.
name|index
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|depth
operator|==
name|key
operator|.
name|depth
condition|)
block|{
return|return
name|path
operator|.
name|compareTo
argument_list|(
name|key
operator|.
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|depth
operator|<
name|key
operator|.
name|depth
operator|)
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
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
name|path
argument_list|,
name|index
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
name|Key
condition|)
block|{
name|Key
name|other
init|=
operator|(
name|Key
operator|)
name|o
decl_stmt|;
return|return
name|index
operator|==
name|other
operator|.
name|index
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|path
argument_list|,
name|other
operator|.
name|path
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
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
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|RestrictionPattern
name|restriction
decl_stmt|;
specifier|private
name|PermissionEntry
parameter_list|(
name|String
name|accessControlledPath
parameter_list|,
name|Tree
name|entryTree
parameter_list|,
name|RestrictionProvider
name|restrictionsProvider
parameter_list|)
block|{
name|isAllow
operator|=
operator|(
name|PREFIX_ALLOW
operator|==
name|entryTree
operator|.
name|getName
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|)
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
name|this
operator|.
name|path
operator|=
name|accessControlledPath
expr_stmt|;
name|restriction
operator|=
name|restrictionsProvider
operator|.
name|getPattern
argument_list|(
name|accessControlledPath
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
name|String
name|treePath
init|=
name|tree
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|treePath
argument_list|)
condition|)
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
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**      * Collects permission entries for different principals and asserts they are      * in the correct order for proper and efficient evaluation.      */
specifier|private
specifier|static
specifier|final
class|class
name|EntriesBuilder
block|{
specifier|private
name|ImmutableSortedMap
operator|.
name|Builder
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|repoEntries
init|=
name|ImmutableSortedMap
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
specifier|private
name|ImmutableSortedMap
operator|.
name|Builder
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|userEntries
init|=
name|ImmutableSortedMap
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
specifier|private
name|ImmutableSortedMap
operator|.
name|Builder
argument_list|<
name|Key
argument_list|,
name|PermissionEntry
argument_list|>
name|groupEntries
init|=
name|ImmutableSortedMap
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
specifier|private
name|void
name|addEntries
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|principalRoot
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
for|for
control|(
name|Tree
name|entryTree
range|:
name|principalRoot
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|entryTree
argument_list|)
decl_stmt|;
name|PermissionEntry
name|entry
init|=
operator|new
name|PermissionEntry
argument_list|(
name|key
operator|.
name|path
argument_list|,
name|entryTree
argument_list|,
name|restrictionProvider
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entry
operator|.
name|privilegeBits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|path
operator|==
literal|null
condition|)
block|{
name|repoEntries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|principal
operator|instanceof
name|Group
condition|)
block|{
name|groupEntries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userEntries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

