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
name|privilege
package|;
end_package

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
name|jcr
operator|.
name|RepositoryException
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
name|Privilege
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
name|memory
operator|.
name|PropertyStates
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
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|assertNotEquals
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
name|assertNotNull
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
name|assertSame
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|PrivilegeBitsProviderTest
implements|implements
name|PrivilegeConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
name|KNOWN_PRIV_NAME
init|=
literal|"prefix:known"
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_BITS
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|5000
argument_list|)
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBits
name|bits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|ps
argument_list|)
decl_stmt|;
specifier|private
name|Tree
name|privTree
decl_stmt|;
specifier|private
name|Tree
name|pTree
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|privTree
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
expr_stmt|;
name|root
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|privTree
argument_list|)
expr_stmt|;
name|pTree
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getProperty
argument_list|(
name|REP_BITS
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesTree
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|bitsProvider
operator|.
name|getPrivilegesTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsNonExistingPrivilegesTree
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsEmptyNames
parameter_list|()
block|{
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsEmptyArray
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsEmptyString
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsEmptyIterable
parameter_list|()
block|{
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsBuiltInSingleName
parameter_list|()
block|{
name|PrivilegeBits
name|bits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsBuiltInSingleton
parameter_list|()
block|{
name|PrivilegeBits
name|bits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsBuiltInNames
parameter_list|()
block|{
name|PrivilegeBits
name|bits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegeBits
name|mod
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bits
argument_list|,
name|mod
operator|.
name|unmodifiable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsBuiltInIterable
parameter_list|()
block|{
name|PrivilegeBits
name|bits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegeBits
name|mod
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bits
argument_list|,
name|mod
operator|.
name|unmodifiable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsNonExistingTree
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|hasChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// privilegesTree has no child for KNOWN_PRIV_NAME
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsKnownPrivName
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|hasChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bits
operator|.
name|unmodifiable
argument_list|()
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsFromEmptyPrivileges
parameter_list|()
block|{
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
operator|new
name|Privilege
index|[
literal|0
index|]
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBitsFromPrivilegesInvalidMapping
parameter_list|()
block|{
name|Privilege
name|p
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Privilege
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getOakName
parameter_list|(
annotation|@
name|NotNull
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|assertSame
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
operator|new
name|Privilege
index|[]
block|{
name|p
block|}
argument_list|,
name|mapper
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNamesFromEmpty
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|PrivilegeBits
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNamesFromNull
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNamesNonExistingPrivilegesTree
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|bits
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNames
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|pTree
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|bits
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNamesFromCache
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|pTree
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|bits
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|names
argument_list|,
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|bits
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeNamesWithAggregation
parameter_list|()
block|{
name|when
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|pTree
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|anotherPriv
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|anotherPriv
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|anotherPriv
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"name2"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|anotherPriv
operator|.
name|hasProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|anotherPriv
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|bits2
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_BITS
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|7500
argument_list|)
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|anotherPriv
operator|.
name|getProperty
argument_list|(
name|REP_BITS
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|bits2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChildren
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|pTree
argument_list|,
name|anotherPriv
argument_list|)
argument_list|)
expr_stmt|;
comment|// aggregation must be removed from the result set
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"name2"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|bits
argument_list|)
argument_list|,
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|bits2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesEmpty
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesEmptyArray
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesSingleNonAggregates
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
name|NON_AGGREGATE_PRIVILEGES
control|)
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|name
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesNonAggregates
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|,
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|REP_READ_NODES
argument_list|,
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesJcrRead
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|AGGREGATE_PRIVILEGES
operator|.
name|get
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesJcrWrite
parameter_list|()
block|{
comment|// nested aggregated privileges in this case
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_WRITE
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|AGGREGATE_PRIVILEGES
operator|.
name|get
argument_list|(
name|JCR_WRITE
argument_list|)
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|String
index|[]
name|expected
init|=
operator|new
name|String
index|[]
block|{
name|JCR_ADD_CHILD_NODES
block|,
name|JCR_REMOVE_CHILD_NODES
block|,
name|JCR_REMOVE_NODE
block|,
name|REP_ADD_PROPERTIES
block|,
name|REP_ALTER_PROPERTIES
block|,
name|REP_REMOVE_PROPERTIES
block|}
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|expected
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesBuiltInTwice
parameter_list|()
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|agg
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|agg
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesMultipleBuiltIn
parameter_list|()
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_WRITE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create new to avoid reading from cache
name|PrivilegeBitsProvider
name|bp
init|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bp
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesMultipleBuiltIn2
parameter_list|()
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_WRITE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// read with same provider (i.e. reading from cache)
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesMixedBuiltIn
parameter_list|()
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_WRITE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_WRITE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesNonExistingTree
parameter_list|()
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|names
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|names
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesMissingAggProperty
parameter_list|()
block|{
name|when
argument_list|(
name|pTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNames
parameter_list|()
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|expected
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesNested
parameter_list|()
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|values
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|values
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
decl_stmt|;
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAggregatedPrivilegeNamesNestedWithCache
parameter_list|()
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|values
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|getProperty
argument_list|(
name|REP_AGGREGATES
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|values
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|pTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|privTree
operator|.
name|getChild
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pTree
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|result
init|=
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|KNOWN_PRIV_NAME
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|,
name|bitsProvider
operator|.
name|getAggregatedPrivilegeNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

