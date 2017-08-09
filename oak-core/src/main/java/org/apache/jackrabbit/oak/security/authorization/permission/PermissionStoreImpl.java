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
name|Collection
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
name|TreeSet
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

begin_comment
comment|/**  * {@code PermissionStoreImpl}...  */
end_comment

begin_class
class|class
name|PermissionStoreImpl
implements|implements
name|PermissionStore
implements|,
name|PermissionConstants
block|{
comment|/**      * default logger      */
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
name|PermissionStoreImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|permissionRootName
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
name|principalTreeMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Tree
name|permissionsTree
decl_stmt|;
specifier|private
name|PrivilegeBits
name|allBits
decl_stmt|;
name|PermissionStoreImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|String
name|permissionRootName
parameter_list|,
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|this
operator|.
name|permissionRootName
operator|=
name|permissionRootName
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|reset
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|)
block|{
name|principalTreeMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reset
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|)
block|{
name|permissionsTree
operator|=
name|PermissionUtil
operator|.
name|getPermissionsRoot
argument_list|(
name|root
argument_list|,
name|permissionRootName
argument_list|)
expr_stmt|;
name|allBits
operator|=
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
expr_stmt|;
block|}
comment|//----------------------------------------------------< PermissionStore>---
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|load
parameter_list|(
annotation|@
name|Nullable
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
parameter_list|,
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalRoot
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|principalRoot
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Tree
name|child
init|=
name|principalRoot
operator|.
name|getChild
argument_list|(
name|name
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
name|path
argument_list|)
condition|)
block|{
name|entries
operator|=
name|loadPermissionEntries
argument_list|(
name|path
argument_list|,
name|entries
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check for child node
for|for
control|(
name|Tree
name|node
range|:
name|child
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|node
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|entries
operator|=
name|loadPermissionEntries
argument_list|(
name|path
argument_list|,
name|entries
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|entries
operator|==
literal|null
operator|||
name|entries
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|entries
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNumEntries
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|,
name|long
name|max
parameter_list|)
block|{
comment|// we ignore the hash-collisions here
name|Tree
name|tree
init|=
name|getPrincipalRoot
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|0
else|:
name|tree
operator|.
name|getChildrenCount
argument_list|(
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|PrincipalPermissionEntries
name|load
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|PrincipalPermissionEntries
name|ret
init|=
operator|new
name|PrincipalPermissionEntries
argument_list|()
decl_stmt|;
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalRoot
operator|!=
literal|null
condition|)
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
name|loadPermissionEntries
argument_list|(
name|entryTree
argument_list|,
name|ret
operator|.
name|getEntries
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ret
operator|.
name|setFullyLoaded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"loaded %d entries in %.2fus for %s.%n"
argument_list|,
name|ret
operator|.
name|getSize
argument_list|()
argument_list|,
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000.0
argument_list|,
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|CheckForNull
specifier|private
name|Tree
name|getPrincipalRoot
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
if|if
condition|(
name|principalTreeMap
operator|.
name|containsKey
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
return|return
name|principalTreeMap
operator|.
name|get
argument_list|(
name|principalName
argument_list|)
return|;
block|}
else|else
block|{
name|Tree
name|principalRoot
init|=
name|PermissionUtil
operator|.
name|getPrincipalRoot
argument_list|(
name|permissionsTree
argument_list|,
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|principalRoot
operator|.
name|exists
argument_list|()
condition|)
block|{
name|principalRoot
operator|=
literal|null
expr_stmt|;
block|}
name|principalTreeMap
operator|.
name|put
argument_list|(
name|principalName
argument_list|,
name|principalRoot
argument_list|)
expr_stmt|;
return|return
name|principalRoot
return|;
block|}
block|}
specifier|private
name|void
name|loadPermissionEntries
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|pathEntryMap
parameter_list|)
block|{
name|String
name|path
init|=
name|TreeUtil
operator|.
name|getString
argument_list|(
name|tree
argument_list|,
name|PermissionConstants
operator|.
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|entries
init|=
name|pathEntryMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
block|{
name|entries
operator|=
operator|new
name|TreeSet
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|()
expr_stmt|;
name|pathEntryMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|.
name|getName
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'c'
condition|)
block|{
name|loadPermissionEntries
argument_list|(
name|child
argument_list|,
name|pathEntryMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entries
operator|.
name|add
argument_list|(
name|createPermissionEntry
argument_list|(
name|path
argument_list|,
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Permission entry at '{}' without rep:accessControlledPath property."
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|loadPermissionEntries
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|ret
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
for|for
control|(
name|Tree
name|ace
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|ace
operator|.
name|getName
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
name|ret
operator|=
operator|new
name|TreeSet
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|ret
operator|.
name|add
argument_list|(
name|createPermissionEntry
argument_list|(
name|path
argument_list|,
name|ace
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PermissionEntry
name|createPermissionEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|entryTree
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|entryTree
operator|.
name|getProperty
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|bits
init|=
operator|(
name|isJcrAll
argument_list|(
name|ps
argument_list|)
operator|)
condition|?
name|allBits
else|:
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|ps
argument_list|)
decl_stmt|;
name|boolean
name|isAllow
init|=
name|TreeUtil
operator|.
name|getBoolean
argument_list|(
name|entryTree
argument_list|,
name|REP_IS_ALLOW
argument_list|)
decl_stmt|;
return|return
operator|new
name|PermissionEntry
argument_list|(
name|path
argument_list|,
name|isAllow
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|entryTree
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|bits
argument_list|,
name|restrictionProvider
operator|.
name|getPattern
argument_list|(
name|path
argument_list|,
name|entryTree
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isJcrAll
parameter_list|(
annotation|@
name|CheckForNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|count
argument_list|()
operator|==
literal|1
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
literal|0
argument_list|)
operator|==
name|DYNAMIC_ALL_BITS
return|;
block|}
block|}
end_class

end_unit

