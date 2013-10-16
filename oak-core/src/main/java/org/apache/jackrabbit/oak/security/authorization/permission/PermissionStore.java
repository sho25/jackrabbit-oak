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
name|Iterators
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

begin_comment
comment|/**  * The Permission store reads the principal based access control permissions.  * One store is currently used to handle 1 set of principal trees (so the compiled  * permissions use 2 stores, one for the user principals and one for the  * group principals).  */
end_comment

begin_class
class|class
name|PermissionStore
implements|implements
name|PermissionConstants
block|{
specifier|private
specifier|static
specifier|final
name|long
name|MAX_SIZE
init|=
literal|250
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|principalTrees
decl_stmt|;
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
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
decl_stmt|;
specifier|private
name|PermissionStore
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
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
name|boolean
name|doCreateMap
parameter_list|)
block|{
name|this
operator|.
name|principalTrees
operator|=
name|principalTrees
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|pathEntryMap
operator|=
operator|(
name|doCreateMap
operator|)
condition|?
name|createMap
argument_list|(
name|principalTrees
operator|.
name|values
argument_list|()
argument_list|,
name|restrictionProvider
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
specifier|static
name|PermissionStore
name|create
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
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|long
name|cnt
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|principalTrees
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|treeItr
init|=
name|principalTrees
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|treeItr
operator|.
name|hasNext
argument_list|()
operator|&&
name|cnt
operator|<
name|MAX_SIZE
condition|)
block|{
name|Tree
name|t
init|=
name|treeItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|cnt
operator|+=
name|t
operator|.
name|getChildrenCount
argument_list|(
name|MAX_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PermissionStore
argument_list|(
name|principalTrees
argument_list|,
name|restrictionProvider
argument_list|,
operator|(
name|cnt
operator|<
name|MAX_SIZE
operator|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|pathEntryMap
operator|!=
literal|null
condition|)
block|{
name|pathEntryMap
operator|=
name|createMap
argument_list|(
name|principalTrees
operator|.
name|values
argument_list|()
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
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
if|if
condition|(
name|principalTrees
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|EntryIterator
argument_list|(
name|predicate
argument_list|)
return|;
block|}
block|}
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|principalTrees
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|pathEntryMap
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
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|entries
operator|!=
literal|null
operator|)
condition|?
name|entries
else|:
name|Collections
operator|.
expr|<
name|PermissionEntry
operator|>
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|tree
operator|.
name|hasChild
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
operator|)
condition|?
name|getEntries
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|PermissionEntry
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|ret
init|=
operator|new
name|TreeSet
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|()
decl_stmt|;
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|principalRoot
range|:
name|principalTrees
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Tree
name|parent
init|=
name|principalRoot
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
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
name|parent
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
name|loadPermissionsFromTree
argument_list|(
name|path
argument_list|,
name|ret
argument_list|,
name|child
argument_list|,
name|restrictionProvider
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
name|loadPermissionsFromTree
argument_list|(
name|path
argument_list|,
name|ret
argument_list|,
name|node
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|createMap
parameter_list|(
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|Tree
argument_list|>
name|principalTrees
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
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
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tree
name|principalTree
range|:
name|principalTrees
control|)
block|{
for|for
control|(
name|Tree
name|entryTree
range|:
name|principalTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|loadPermissionEntries
argument_list|(
name|entryTree
argument_list|,
name|pathEntryMap
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pathEntryMap
return|;
block|}
specifier|private
specifier|static
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
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionProvider
name|restrictionProvider
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
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
decl_stmt|;
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
argument_list|,
name|restrictionProvider
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entries
operator|.
name|add
argument_list|(
operator|new
name|PermissionEntry
argument_list|(
name|path
argument_list|,
name|child
argument_list|,
name|restrictionProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|loadPermissionsFromTree
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
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
name|ret
operator|.
name|add
argument_list|(
operator|new
name|PermissionEntry
argument_list|(
name|path
argument_list|,
name|ace
argument_list|,
name|restrictionProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
class|class
name|EntryIterator
extends|extends
name|AbstractEntryIterator
block|{
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
specifier|private
name|EntryIterator
parameter_list|(
annotation|@
name|Nonnull
name|EntryPredicate
name|predicate
parameter_list|)
block|{
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
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|protected
name|void
name|seekNext
parameter_list|()
block|{
for|for
control|(
name|next
operator|=
literal|null
init|;
name|next
operator|==
literal|null
condition|;
control|)
block|{
if|if
condition|(
name|nextEntries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PermissionEntry
name|pe
init|=
name|nextEntries
operator|.
name|next
argument_list|()
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
name|next
operator|=
name|pe
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|nextEntries
operator|=
name|getEntries
argument_list|(
name|path
argument_list|)
operator|.
name|iterator
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
block|}
block|}
block|}
block|}
end_class

end_unit

