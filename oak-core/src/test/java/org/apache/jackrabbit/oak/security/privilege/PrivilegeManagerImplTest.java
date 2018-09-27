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
name|privilege
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
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
name|AccessControlException
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
name|ImmutableMap
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|AbstractSecurityTest
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
name|namepath
operator|.
name|impl
operator|.
name|GlobalNameMapper
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
name|impl
operator|.
name|LocalNameMapper
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
name|namepath
operator|.
name|impl
operator|.
name|NamePathMapperImpl
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
name|EmptyNodeState
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
name|name
operator|.
name|ReadWriteNamespaceRegistry
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
name|PrivilegeManagerImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|PrivilegeManagerImpl
name|privilegeManager
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|privilegeManager
operator|=
name|create
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PrivilegeManagerImpl
name|create
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|)
block|{
return|return
operator|new
name|PrivilegeManagerImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|PrivilegeManagerImpl
name|create
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|mapper
parameter_list|)
block|{
return|return
operator|new
name|PrivilegeManagerImpl
argument_list|(
name|root
argument_list|,
name|mapper
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRegisteredPrivileges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Privilege
index|[]
name|registered
init|=
name|privilegeManager
operator|.
name|getRegisteredPrivileges
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|registered
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|1
argument_list|,
name|registered
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRegisteredPrivilegesFromEmptyRoot
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Privilege
index|[]
name|registered
init|=
name|create
argument_list|(
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
operator|.
name|getRegisteredPrivileges
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|registered
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|registered
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
name|p
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegeExpandedNameMissingMapper
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
name|p
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeExpandedName
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
name|p
init|=
name|create
argument_list|(
name|root
argument_list|,
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|GlobalNameMapper
argument_list|(
name|root
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|Privilege
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegeRemappedNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"prefix"
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Privilege
name|p
init|=
name|create
argument_list|(
name|root
argument_list|,
name|mapper
argument_list|)
operator|.
name|getPrivilege
argument_list|(
literal|"prefix:read"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"prefix:read"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegeInvalidRemappedNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"prefix"
argument_list|,
literal|"unknownUri"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|create
argument_list|(
name|root
argument_list|,
name|mapper
argument_list|)
operator|.
name|getPrivilege
argument_list|(
literal|"prefix:read"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegeFromEmptyRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|create
argument_list|(
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetUnknownPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|create
argument_list|(
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:someName"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegeEmptyName
parameter_list|()
throws|throws
name|Exception
block|{
name|create
argument_list|(
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
operator|.
name|getPrivilege
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrivilegeNullName
parameter_list|()
throws|throws
name|Exception
block|{
name|create
argument_list|(
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
operator|.
name|getPrivilege
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegePendingChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|r
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|create
argument_list|(
name|r
argument_list|)
operator|.
name|registerPrivilege
argument_list|(
literal|"privName"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegeEmptyName
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"jcr:write"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegeNullName
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"jcr:write"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegeUnknownAggreate
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"unknown"
block|,
literal|"jcr:read"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegeReservedNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"jcr:customPrivilege"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"jcr:write"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisterAggregated
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:customPrivilege"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"jcr:write"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterAggregatedNonExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:customPrivilege"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test:nan"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisterPrivilegeReservedRemappedNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"prefix"
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|pmgr
init|=
name|create
argument_list|(
name|root
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
name|pmgr
operator|.
name|registerPrivilege
argument_list|(
literal|"prefix:customPrivilege"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"prefix:read"
block|,
literal|"prefix:write"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisterPrivilegeRemappedNamespace
parameter_list|()
throws|throws
name|Exception
block|{
name|ReadWriteNamespaceRegistry
name|nsRegistry
init|=
operator|new
name|ReadWriteNamespaceRegistry
argument_list|(
name|root
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
block|}
decl_stmt|;
name|nsRegistry
operator|.
name|registerNamespace
argument_list|(
literal|"ns"
argument_list|,
literal|"http://jackrabbit.apache.org/oak/ns"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|localMapping
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"prefix"
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|,
literal|"prefix2"
argument_list|,
literal|"http://jackrabbit.apache.org/oak/ns"
argument_list|)
decl_stmt|;
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|localMapping
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|pmgr
init|=
name|create
argument_list|(
name|root
argument_list|,
name|mapper
argument_list|)
decl_stmt|;
name|Privilege
name|p
init|=
name|pmgr
operator|.
name|registerPrivilege
argument_list|(
literal|"prefix2:customPrivilege"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"prefix:read"
block|,
literal|"prefix:write"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"prefix2:customPrivilege"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Tree
name|privilegesTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|privilegesTree
operator|.
name|hasChild
argument_list|(
literal|"prefix2:customPrivilege"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|privTree
init|=
name|privilegesTree
operator|.
name|getChild
argument_list|(
literal|"ns:customPrivilege"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|privTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TreeUtil
operator|.
name|getBoolean
argument_list|(
name|privTree
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_IS_ABSTRACT
argument_list|)
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|aggr
init|=
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|privTree
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_AGGREGATES
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|aggr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"jcr:read"
argument_list|,
literal|"jcr:write"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|aggr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

