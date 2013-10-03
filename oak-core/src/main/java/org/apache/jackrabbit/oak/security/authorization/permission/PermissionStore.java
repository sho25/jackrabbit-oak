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
name|util
operator|.
name|Text
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
comment|/**  * The Permission store reads the principal based access control permissions. One store is currently used to handle  * 1 set of principal trees (so the compiled permissions use 2 stores, one for the user principals and one for the  * group principals)  */
end_comment

begin_class
class|class
name|PermissionStore
implements|implements
name|PermissionConstants
block|{
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|cache
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
name|PermissionStore
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Tree
argument_list|>
name|principalTrees
parameter_list|,
name|RestrictionProvider
name|restrictionProvider
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
name|cache
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
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
name|child
init|=
name|principalRoot
operator|.
name|getValue
argument_list|()
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
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
name|loadPermissionsFromNode
argument_list|(
name|path
argument_list|,
name|ret
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
name|loadPermissionsFromNode
argument_list|(
name|path
argument_list|,
name|ret
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|//cache.put(path, ret);
block|}
return|return
name|ret
return|;
block|}
specifier|private
name|void
name|loadPermissionsFromNode
parameter_list|(
name|String
name|path
parameter_list|,
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|ret
parameter_list|,
name|Tree
name|node
parameter_list|)
block|{
for|for
control|(
name|Tree
name|ace
range|:
name|node
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
return|return
operator|new
name|EntryIterator
argument_list|(
name|predicate
argument_list|)
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
comment|// lazy initialization
if|if
condition|(
name|nextEntries
operator|==
literal|null
condition|)
block|{
name|nextEntries
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
name|seekNext
argument_list|()
expr_stmt|;
block|}
block|}
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
specifier|public
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
specifier|public
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
specifier|public
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
specifier|public
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
specifier|public
specifier|static
specifier|final
class|class
name|PermissionEntry
implements|implements
name|Comparable
argument_list|<
name|PermissionEntry
argument_list|>
block|{
comment|/**          * flag controls if this is an allow or deny entry          */
specifier|public
specifier|final
name|boolean
name|isAllow
decl_stmt|;
comment|/**          * the privilege bits          */
specifier|public
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
comment|/**          * the index (order) of the original ACE in the ACL.          */
specifier|public
specifier|final
name|int
name|index
decl_stmt|;
comment|/**          * the access controlled (node) path          */
specifier|public
specifier|final
name|String
name|path
decl_stmt|;
comment|/**          * the restriction pattern for this entry          */
specifier|public
specifier|final
name|RestrictionPattern
name|restriction
decl_stmt|;
comment|/**          * pre-evaluated read status          */
specifier|public
name|ReadStatus
name|readStatus
init|=
literal|null
decl_stmt|;
comment|// TODO
specifier|private
name|PermissionEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|Tree
name|entryTree
parameter_list|,
name|RestrictionProvider
name|restrictionsProvider
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
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
call|(
name|int
call|)
argument_list|(
name|long
argument_list|)
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
specifier|public
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
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
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
specifier|public
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
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|(
name|treePath
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|()
block|{
return|return
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|matchesParent
parameter_list|(
annotation|@
name|Nonnull
name|String
name|parentPath
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|parentPath
argument_list|)
operator|&&
operator|(
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|(
name|parentPath
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|PermissionEntry
name|o
parameter_list|)
block|{
specifier|final
name|int
name|anotherVal
init|=
name|o
operator|.
name|index
decl_stmt|;
comment|// reverse order
return|return
operator|(
name|index
operator|<
name|anotherVal
condition|?
literal|1
else|:
operator|(
name|index
operator|==
name|anotherVal
condition|?
literal|0
else|:
operator|-
literal|1
operator|)
operator|)
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PermissionEntry
name|that
init|=
operator|(
name|PermissionEntry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|index
operator|!=
name|that
operator|.
name|index
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|index
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
block|}
end_class

end_unit

