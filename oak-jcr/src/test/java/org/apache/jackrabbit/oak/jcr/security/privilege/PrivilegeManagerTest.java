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
name|jcr
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * PrivilegeManagerTest...  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeManagerTest
extends|extends
name|AbstractPrivilegeTest
block|{
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|privilegeManager
operator|=
name|getPrivilegeManager
argument_list|(
name|superuser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|privilegeManager
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
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
name|Set
argument_list|<
name|Privilege
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Privilege
argument_list|>
argument_list|()
decl_stmt|;
name|Privilege
name|all
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|all
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|all
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Privilege
name|p
range|:
name|registered
control|)
block|{
name|assertTrue
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|set
operator|.
name|remove
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|set
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
name|testGetPrivilege
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|aggregatedPrivilegeNames
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"jcr:read"
argument_list|,
literal|"jcr:modifyProperties"
argument_list|,
literal|"jcr:write"
argument_list|,
literal|"rep:write"
argument_list|,
literal|"jcr:all"
argument_list|)
decl_stmt|;
for|for
control|(
name|Privilege
name|priv
range|:
name|privilegeManager
operator|.
name|getRegisteredPrivileges
argument_list|()
control|)
block|{
name|String
name|privName
init|=
name|priv
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|isAggregate
init|=
name|aggregatedPrivilegeNames
operator|.
name|contains
argument_list|(
name|privName
argument_list|)
decl_stmt|;
name|assertPrivilege
argument_list|(
name|priv
argument_list|,
name|privName
argument_list|,
name|isAggregate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrAll
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Privilege
name|all
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
name|assertPrivilege
argument_list|(
name|all
argument_list|,
literal|"jcr:all"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|decl
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|all
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|aggr
init|=
operator|new
name|ArrayList
argument_list|<
name|Privilege
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|all
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|decl
operator|.
name|contains
argument_list|(
name|all
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|aggr
operator|.
name|contains
argument_list|(
name|all
argument_list|)
argument_list|)
expr_stmt|;
comment|// declared and aggregated privileges are the same for jcr:all
name|assertTrue
argument_list|(
name|decl
operator|.
name|containsAll
argument_list|(
name|aggr
argument_list|)
argument_list|)
expr_stmt|;
comment|// test individual built-in privileges are listed in the aggregates
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_RETENTION_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_WRITE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_ADD_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_ALTER_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_REMOVE_PROPERTIES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_NAMESPACE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_WORKSPACE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_PRIVILEGE_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggr
operator|.
name|remove
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_INDEX_DEFINITION_MANAGEMENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// there may be no privileges left
name|assertTrue
argument_list|(
name|aggr
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
name|testGetPrivilegeFromName
parameter_list|()
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
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
name|assertTrue
argument_list|(
name|p
operator|!=
literal|null
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
name|assertFalse
argument_list|(
name|p
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_WRITE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_WRITE
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesFromInvalidName
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
literal|"unknown"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid privilege name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// OK
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesFromInvalidName2
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|nonExistingPrivilegeName
init|=
literal|"{http://www.nonexisting.com/1.0}nonexisting"
decl_stmt|;
try|try
block|{
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|nonExistingPrivilegeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|//expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesFromEmptyNames
parameter_list|()
block|{
try|try
block|{
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid privilege name array"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// OK
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// OK
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrivilegesFromNullNames
parameter_list|()
block|{
try|try
block|{
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid privilege name (null)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// OK
block|}
block|}
block|}
end_class

end_unit

