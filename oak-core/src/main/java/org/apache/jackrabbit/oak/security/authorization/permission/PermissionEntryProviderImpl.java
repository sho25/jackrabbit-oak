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
name|HashSet
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
name|Set
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
name|AbstractLazyIterator
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
name|commons
operator|.
name|LongUtils
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
import|;
end_import

begin_class
class|class
name|PermissionEntryProviderImpl
implements|implements
name|PermissionEntryProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|EAGER_CACHE_SIZE_PARAM
init|=
literal|"eagerCacheSize"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_SIZE
init|=
literal|250
decl_stmt|;
comment|/**      * The set of principal names for which this {@code PermissionEntryProvider}      * has been created.      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
decl_stmt|;
comment|/**      * The set of principal names for which the store contains any permission      * entries. This set is equals or just a subset of the {@code principalNames}      * defined above. The methods collecting the entries will shortcut in case      * this set is empty and thus no permission entries exist for the specified      * set of principal.      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|PermissionStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|PermissionEntryCache
name|cache
decl_stmt|;
specifier|private
specifier|final
name|long
name|maxSize
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
name|PermissionEntryProviderImpl
parameter_list|(
annotation|@
name|Nonnull
name|PermissionStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|PermissionEntryCache
name|cache
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|options
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|principalNames
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|principalNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|options
operator|.
name|getConfigValue
argument_list|(
name|EAGER_CACHE_SIZE_PARAM
argument_list|,
name|DEFAULT_SIZE
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|()
block|{
name|long
name|cnt
init|=
literal|0
decl_stmt|;
name|existingNames
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|principalNames
control|)
block|{
name|long
name|n
init|=
name|cache
operator|.
name|getNumEntries
argument_list|(
name|store
argument_list|,
name|name
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
comment|/*             if cache.getNumEntries (n) returns a number bigger than 0, we             remember this principal name int the 'existingNames' set             */
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|existingNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/*             Calculate the total number of permission entries (cnt) defined for the             given set of principals in order to be able to determine if the cache             should be loaded upfront.             Note however that cache.getNumEntries (n) may return Long.MAX_VALUE             if the underlying implementation does not know the exact value, and             the child node count is higher than maxSize (see OAK-2465).             */
if|if
condition|(
name|cnt
operator|<
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
if|if
condition|(
name|Long
operator|.
name|MAX_VALUE
operator|==
name|n
condition|)
block|{
name|cnt
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|cnt
operator|=
name|LongUtils
operator|.
name|safeAdd
argument_list|(
name|cnt
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|cnt
operator|>
literal|0
operator|&&
name|cnt
operator|<
name|maxSize
condition|)
block|{
comment|// the total number of entries is smaller that maxSize, so we can
comment|// cache all entries for all principals having any entries right away
name|pathEntryMap
operator|=
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
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|existingNames
control|)
block|{
name|cache
operator|.
name|load
argument_list|(
name|store
argument_list|,
name|pathEntryMap
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pathEntryMap
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------< PermissionEntryProvider>---
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|cache
operator|.
name|flush
argument_list|(
name|principalNames
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
name|existingNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
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
annotation|@
name|Override
annotation|@
name|Nonnull
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
name|accessControlledTree
parameter_list|)
block|{
if|if
condition|(
name|existingNames
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
name|accessControlledTree
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
name|accessControlledTree
operator|.
name|hasChild
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
operator|)
condition|?
name|loadEntries
argument_list|(
name|accessControlledTree
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
comment|//------------------------------------------------------------< private>---
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
if|if
condition|(
name|existingNames
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
name|path
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
name|loadEntries
argument_list|(
name|path
argument_list|)
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
name|loadEntries
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
for|for
control|(
name|String
name|name
range|:
name|existingNames
control|)
block|{
name|cache
operator|.
name|load
argument_list|(
name|store
argument_list|,
name|ret
argument_list|,
name|name
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
specifier|private
specifier|final
class|class
name|EntryIterator
extends|extends
name|AbstractLazyIterator
argument_list|<
name|PermissionEntry
argument_list|>
block|{
specifier|private
specifier|final
name|EntryPredicate
name|predicate
decl_stmt|;
comment|// the ordered permission entries at a given path in the hierarchy
specifier|private
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|nextEntries
init|=
name|Collections
operator|.
name|emptyIterator
argument_list|()
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
name|Override
specifier|protected
name|PermissionEntry
name|getNext
parameter_list|()
block|{
name|PermissionEntry
name|next
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|next
operator|==
literal|null
condition|)
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
return|return
name|next
return|;
block|}
block|}
block|}
end_class

end_unit

