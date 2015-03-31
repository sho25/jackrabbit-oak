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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|PermissionEntryProviderImplTest
block|{
specifier|private
specifier|final
name|String
name|GROUP_LONG_MAX
init|=
literal|"groupLongMax"
decl_stmt|;
specifier|private
specifier|final
name|String
name|GROUP_LONG_MAX_MINUS_10
init|=
literal|"groupLongMaxMinus10"
decl_stmt|;
specifier|private
specifier|final
name|String
name|GROUP_50
init|=
literal|"group50"
decl_stmt|;
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2465">OAK-2465</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testInitLongOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|MockPermissionStore
name|store
init|=
operator|new
name|MockPermissionStore
argument_list|()
decl_stmt|;
name|PermissionEntryCache
name|cache
init|=
operator|new
name|MockPermissionEntryCache
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|GROUP_LONG_MAX
argument_list|)
decl_stmt|;
comment|/*         create a new PermissionEntryProviderImpl to have it's #init() method         called, which may trigger the cache to be pre-filled if the max number         if entries is not exceeded -> in case of PermissionStore#getNumEntries         return Long.MAX_VALUE the cache should not be filled (-> the mock-cache         implementation will fail.         */
name|PermissionEntryProviderImpl
name|provider
init|=
operator|new
name|PermissionEntryProviderImpl
argument_list|(
name|store
argument_list|,
name|cache
argument_list|,
name|principalNames
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Field
name|existingNamesField
init|=
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"existingNames"
argument_list|)
decl_stmt|;
name|existingNamesField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// test that PermissionEntryProviderImpl.existingNames nevertheless is
comment|// properly filled with all principal names for which permission entries exist
name|assertEquals
argument_list|(
name|principalNames
argument_list|,
name|existingNamesField
operator|.
name|get
argument_list|(
name|provider
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|Iterators
operator|.
name|emptyIterator
argument_list|()
argument_list|,
name|provider
operator|.
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2465">OAK-2465</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testInitLongOverflow2
parameter_list|()
throws|throws
name|Exception
block|{
name|MockPermissionStore
name|store
init|=
operator|new
name|MockPermissionStore
argument_list|()
decl_stmt|;
name|PermissionEntryCache
name|cache
init|=
operator|new
name|MockPermissionEntryCache
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|GROUP_LONG_MAX_MINUS_10
argument_list|,
name|GROUP_50
argument_list|)
decl_stmt|;
comment|/*         create a new PermissionEntryProviderImpl to have it's #init() method         called, which may trigger the cache to be pre-filled if the max number         if entries is not exceeded -> still counting up the number of permission         entries must deal with the fact that the counter may become bigger that         Long.MAX_VALUE         */
name|PermissionEntryProviderImpl
name|provider
init|=
operator|new
name|PermissionEntryProviderImpl
argument_list|(
name|store
argument_list|,
name|cache
argument_list|,
name|principalNames
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
init|=
name|getExistingNames
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|principalNames
argument_list|,
name|existingNames
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|Iterators
operator|.
name|emptyIterator
argument_list|()
argument_list|,
name|provider
operator|.
name|getEntryIterator
argument_list|(
operator|new
name|EntryPredicate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2465">OAK-2465</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testExistingNamesAndLongOverFlow
parameter_list|()
throws|throws
name|Exception
block|{
name|MockPermissionStore
name|store
init|=
operator|new
name|MockPermissionStore
argument_list|()
decl_stmt|;
name|PermissionEntryCache
name|cache
init|=
operator|new
name|MockPermissionEntryCache
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|GROUP_LONG_MAX_MINUS_10
argument_list|,
name|GROUP_50
argument_list|,
literal|"noEntries"
argument_list|)
decl_stmt|;
comment|/*         same as before but principal-set contains a name for which not entries         exist -> the 'existingNames' set must properly reflect that         */
name|PermissionEntryProviderImpl
name|provider
init|=
operator|new
name|PermissionEntryProviderImpl
argument_list|(
name|store
argument_list|,
name|cache
argument_list|,
name|principalNames
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
init|=
name|getExistingNames
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|principalNames
operator|.
name|equals
argument_list|(
name|existingNames
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|existingNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|principalNames
operator|.
name|remove
argument_list|(
literal|"noEntries"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|principalNames
argument_list|,
name|existingNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoExistingName
parameter_list|()
throws|throws
name|Exception
block|{
name|MockPermissionStore
name|store
init|=
operator|new
name|MockPermissionStore
argument_list|()
decl_stmt|;
name|PermissionEntryCache
name|cache
init|=
operator|new
name|MockPermissionEntryCache
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"noEntries"
argument_list|,
literal|"noEntries2"
argument_list|,
literal|"noEntries3"
argument_list|)
decl_stmt|;
name|PermissionEntryProviderImpl
name|provider
init|=
operator|new
name|PermissionEntryProviderImpl
argument_list|(
name|store
argument_list|,
name|cache
argument_list|,
name|principalNames
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingNames
init|=
name|getExistingNames
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|existingNames
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Field
name|pathMapField
init|=
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"pathEntryMap"
argument_list|)
decl_stmt|;
name|pathMapField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pathMapField
operator|.
name|get
argument_list|(
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Use reflection to access the private "existingNames" field storing the      * names of those principals associated with the entry provider for which      * any permission entries exist.      *      * @param provider The permission entry provider      * @return the existingNames set.      * @throws Exception      */
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getExistingNames
parameter_list|(
annotation|@
name|Nonnull
name|PermissionEntryProviderImpl
name|provider
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|existingNamesField
init|=
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"existingNames"
argument_list|)
decl_stmt|;
name|existingNamesField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Set
argument_list|<
name|String
argument_list|>
operator|)
name|existingNamesField
operator|.
name|get
argument_list|(
name|provider
argument_list|)
return|;
block|}
comment|// Inner Classes
specifier|private
class|class
name|MockPermissionStore
implements|implements
name|PermissionStore
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|PermissionEntry
argument_list|>
name|load
parameter_list|(
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|load
parameter_list|(
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
name|entries
parameter_list|,
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
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
return|return
operator|new
name|PrincipalPermissionEntries
argument_list|(
name|principalName
argument_list|)
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
name|long
name|cnt
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|GROUP_LONG_MAX_MINUS_10
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|cnt
operator|=
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GROUP_50
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|cnt
operator|=
literal|50
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GROUP_LONG_MAX
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|cnt
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
block|}
specifier|private
class|class
name|MockPermissionEntryCache
extends|extends
name|PermissionEntryCache
block|{
annotation|@
name|Override
specifier|public
name|void
name|load
parameter_list|(
annotation|@
name|Nonnull
name|PermissionStore
name|store
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
name|String
name|principalName
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"The number of  entries exceeds the max cache size"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

