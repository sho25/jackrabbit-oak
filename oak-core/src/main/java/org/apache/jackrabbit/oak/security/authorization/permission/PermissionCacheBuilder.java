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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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

begin_class
specifier|final
class|class
name|PermissionCacheBuilder
block|{
specifier|private
specifier|static
specifier|final
name|long
name|MAX_PATHS_SIZE
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|PermissionStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|PermissionEntryCache
name|peCache
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
decl_stmt|;
specifier|private
name|boolean
name|usePathEntryMap
decl_stmt|;
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
name|PermissionCacheBuilder
parameter_list|(
annotation|@
name|NotNull
name|PermissionStore
name|store
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
name|peCache
operator|=
operator|new
name|PermissionEntryCache
argument_list|()
expr_stmt|;
block|}
name|boolean
name|init
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
parameter_list|,
name|long
name|maxSize
parameter_list|)
block|{
name|existingNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|long
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|principalNames
control|)
block|{
name|NumEntries
name|ne
init|=
name|store
operator|.
name|getNumEntries
argument_list|(
name|name
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|long
name|n
init|=
name|ne
operator|.
name|size
decl_stmt|;
comment|/*             if getNumEntries (n) returns a number bigger than 0, we             remember this principal name int the 'existingNames' set             */
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
if|if
condition|(
name|n
operator|<=
name|MAX_PATHS_SIZE
condition|)
block|{
name|peCache
operator|.
name|getFullyLoadedEntries
argument_list|(
name|store
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|expectedSize
init|=
operator|(
name|ne
operator|.
name|isExact
operator|)
condition|?
name|n
else|:
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|peCache
operator|.
name|init
argument_list|(
name|name
argument_list|,
name|expectedSize
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*             Estimate the total number of access controlled paths (cnt) defined             for the given set of principals in order to be able to determine if             the pathEntryMap should be loaded upfront.             Note however that cache.getNumEntries (n) may return Long.MAX_VALUE             if the underlying implementation does not know the exact value, and             the child node count is higher than maxSize (see OAK-2465).             */
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
name|usePathEntryMap
operator|=
operator|(
name|cnt
operator|>
literal|0
operator|&&
name|cnt
operator|<
name|maxSize
operator|)
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
return|return
name|existingNames
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|NotNull
name|PermissionCache
name|build
parameter_list|()
block|{
name|checkState
argument_list|(
name|initialized
argument_list|)
expr_stmt|;
if|if
condition|(
name|existingNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EmptyCache
operator|.
name|INSTANCE
return|;
block|}
if|if
condition|(
name|usePathEntryMap
condition|)
block|{
comment|// the total number of access controlled paths is smaller that maxSize,
comment|// so we can load all permission entries for all principals having
comment|// any entries right away into the pathEntryMap
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
argument_list|<>
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
name|PrincipalPermissionEntries
name|ppe
init|=
name|peCache
operator|.
name|getFullyLoadedEntries
argument_list|(
name|store
argument_list|,
name|name
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
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|e
range|:
name|ppe
operator|.
name|getEntries
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|pathEntries
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
name|pathEntries
operator|==
literal|null
condition|)
block|{
name|pathEntries
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|pathEntryMap
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|pathEntries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pathEntries
operator|.
name|addAll
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|pathEntryMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EmptyCache
operator|.
name|INSTANCE
return|;
block|}
else|else
block|{
return|return
operator|new
name|PathEntryMapCache
argument_list|(
name|pathEntryMap
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|DefaultPermissionCache
argument_list|(
name|store
argument_list|,
name|peCache
argument_list|,
name|existingNames
argument_list|)
return|;
block|}
block|}
comment|//------------------------------------< PermissionCache Implementations>---
comment|/**      * Default implementation of {@code PermissionCache} wrapping the      * {@code PermissionEntryCache}, which was previously hold as shared field      * inside the {@code PermissionEntryProviderImpl}      */
specifier|private
specifier|static
specifier|final
class|class
name|DefaultPermissionCache
implements|implements
name|PermissionCache
block|{
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
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
decl_stmt|;
name|DefaultPermissionCache
parameter_list|(
annotation|@
name|NotNull
name|PermissionStore
name|store
parameter_list|,
annotation|@
name|NotNull
name|PermissionEntryCache
name|cache
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
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
name|existingNames
operator|=
name|existingNames
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
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
argument_list|<>
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
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|accessControlledTree
parameter_list|)
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
name|getEntries
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
comment|/**      * Fixed size implementation of {@code PermissionCache} that holds a map      * containing all existing entries that in this case have been read eagerly      * upfront. This implementation replaces the optional {@code pathEntryMap}      * previously present inside the the {@code PermissionEntryProviderImpl}.      */
specifier|private
specifier|static
specifier|final
class|class
name|PathEntryMapCache
implements|implements
name|PermissionCache
block|{
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
name|pathEntryMap
decl_stmt|;
name|PathEntryMapCache
parameter_list|(
annotation|@
name|NotNull
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
name|this
operator|.
name|pathEntryMap
operator|=
name|pathEntryMap
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
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
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|accessControlledTree
parameter_list|)
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
block|}
comment|/**      * Empty implementation of {@code PermissionCache} for those cases where      * for a given (possibly empty) set of principals no permission entries are      * present.      */
specifier|private
specifier|static
specifier|final
class|class
name|EmptyCache
implements|implements
name|PermissionCache
block|{
specifier|private
specifier|static
specifier|final
name|PermissionCache
name|INSTANCE
init|=
operator|new
name|EmptyCache
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
return|return
name|Collections
operator|.
expr|<
name|PermissionEntry
operator|>
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|accessControlledTree
parameter_list|)
block|{
return|return
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
block|}
end_class

end_unit

