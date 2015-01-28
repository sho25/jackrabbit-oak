begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|upgrade
package|;
end_package

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
name|List
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
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|Session
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
name|Maps
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
name|JackrabbitSession
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
name|JackrabbitWorkspace
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
name|PrivilegeConstants
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
operator|.
name|JCR_ADD_CHILD_NODES
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_ALL
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_LIFECYCLE_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_LOCK_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_MODIFY_PROPERTIES
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_NAMESPACE_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_READ
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_READ_ACCESS_CONTROL
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_REMOVE_CHILD_NODES
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_REMOVE_NODE
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_RETENTION_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_VERSION_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_WORKSPACE_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|JCR_WRITE
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_ADD_PROPERTIES
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_ALTER_PROPERTIES
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_INDEX_DEFINITION_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_PRIVILEGE_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_READ_NODES
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_READ_PROPERTIES
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_REMOVE_PROPERTIES
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_USER_MANAGEMENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|REP_WRITE
import|;
end_import

begin_class
specifier|public
class|class
name|PrivilegeUpgradeTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|void
name|createSourceContent
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
decl_stmt|;
try|try
block|{
name|JackrabbitWorkspace
name|workspace
init|=
operator|(
name|JackrabbitWorkspace
operator|)
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|NamespaceRegistry
name|registry
init|=
name|workspace
operator|.
name|getNamespaceRegistry
argument_list|()
decl_stmt|;
name|registry
operator|.
name|registerNamespace
argument_list|(
literal|"test"
argument_list|,
literal|"http://www.example.org/"
argument_list|)
expr_stmt|;
name|PrivilegeManager
name|privilegeManager
init|=
name|workspace
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:privilege"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:aggregate"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"test:privilege"
block|}
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:privilege2"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:aggregate2"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test:aggregate"
block|,
literal|"test:privilege2"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyPrivileges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nonAggregatePrivileges
init|=
name|newHashSet
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
name|REP_ADD_PROPERTIES
argument_list|,
name|REP_ALTER_PROPERTIES
argument_list|,
name|REP_REMOVE_PROPERTIES
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|JCR_REMOVE_NODE
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
name|JCR_RETENTION_MANAGEMENT
argument_list|,
name|JCR_WORKSPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
name|REP_USER_MANAGEMENT
argument_list|,
name|REP_INDEX_DEFINITION_MANAGEMENT
argument_list|,
literal|"test:privilege"
argument_list|,
literal|"test:privilege2"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|aggregatePrivileges
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
name|JCR_READ
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_ADD_PROPERTIES
argument_list|,
name|REP_ALTER_PROPERTIES
argument_list|,
name|REP_REMOVE_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|REP_ADD_PROPERTIES
argument_list|,
name|REP_ALTER_PROPERTIES
argument_list|,
name|REP_REMOVE_PROPERTIES
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
name|REP_WRITE
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_WRITE
argument_list|,
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|REP_ADD_PROPERTIES
argument_list|,
name|REP_ALTER_PROPERTIES
argument_list|,
name|REP_REMOVE_PROPERTIES
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|JCR_REMOVE_NODE
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
name|JCR_ALL
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
name|REP_ADD_PROPERTIES
argument_list|,
name|REP_ALTER_PROPERTIES
argument_list|,
name|REP_REMOVE_PROPERTIES
argument_list|,
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|JCR_REMOVE_NODE
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|,
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
name|JCR_RETENTION_MANAGEMENT
argument_list|,
name|JCR_WORKSPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
name|REP_USER_MANAGEMENT
argument_list|,
name|REP_INDEX_DEFINITION_MANAGEMENT
argument_list|,
name|JCR_READ
argument_list|,
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|JCR_WRITE
argument_list|,
name|REP_WRITE
argument_list|,
literal|"test:privilege"
argument_list|,
literal|"test:privilege2"
argument_list|,
literal|"test:aggregate"
argument_list|,
literal|"test:aggregate2"
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
literal|"test:aggregate"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
literal|"test:privilege"
argument_list|)
argument_list|)
expr_stmt|;
name|aggregatePrivileges
operator|.
name|put
argument_list|(
literal|"test:aggregate2"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|REP_READ_NODES
argument_list|,
name|REP_READ_PROPERTIES
argument_list|,
literal|"test:privilege"
argument_list|,
literal|"test:privilege2"
argument_list|,
literal|"test:aggregate"
argument_list|)
argument_list|)
expr_stmt|;
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|JackrabbitWorkspace
name|workspace
init|=
operator|(
name|JackrabbitWorkspace
operator|)
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|PrivilegeManager
name|manager
init|=
name|workspace
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
name|Privilege
index|[]
name|privileges
init|=
name|manager
operator|.
name|getRegisteredPrivileges
argument_list|()
decl_stmt|;
for|for
control|(
name|Privilege
name|privilege
range|:
name|privileges
control|)
block|{
if|if
condition|(
name|privilege
operator|.
name|isAggregate
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|aggregatePrivileges
operator|.
name|remove
argument_list|(
name|privilege
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|actual
init|=
name|getNames
argument_list|(
name|privilege
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Miss match in aggregate privilege "
operator|+
name|privilege
operator|.
name|getName
argument_list|()
operator|+
literal|" expected "
operator|+
name|expected
operator|+
literal|" actual "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|actual
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
name|expected
argument_list|)
operator|.
name|equals
argument_list|(
name|newHashSet
argument_list|(
name|actual
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|nonAggregatePrivileges
operator|.
name|remove
argument_list|(
name|privilege
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Missing non aggregate privileges: "
operator|+
name|nonAggregatePrivileges
argument_list|,
name|nonAggregatePrivileges
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing aggregate privileges: "
operator|+
name|aggregatePrivileges
operator|.
name|keySet
argument_list|()
argument_list|,
name|aggregatePrivileges
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
index|[]
name|getNames
parameter_list|(
name|Privilege
index|[]
name|privileges
parameter_list|)
block|{
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|privileges
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|privileges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|names
index|[
name|i
index|]
operator|=
name|privileges
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyCustomPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|JackrabbitWorkspace
name|workspace
init|=
operator|(
name|JackrabbitWorkspace
operator|)
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|PrivilegeManager
name|manager
init|=
name|workspace
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
name|Privilege
name|privilege
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:privilege"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|privilege
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privilege
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privilege
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|privilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Privilege
name|privilege2
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:privilege2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|privilege2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|privilege2
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privilege2
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|privilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Privilege
name|aggregate
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:aggregate"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|aggregate
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|aggregate
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggregate
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|agg
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|aggregate
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|agg
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agg
operator|.
name|contains
argument_list|(
name|privilege
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agg
operator|.
name|contains
argument_list|(
name|manager
operator|.
name|getPrivilege
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Privilege
name|aggregate2
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:aggregate2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|aggregate2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggregate2
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggregate2
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|agg2
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|aggregate2
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|agg2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agg2
operator|.
name|contains
argument_list|(
name|aggregate
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agg2
operator|.
name|contains
argument_list|(
name|privilege2
argument_list|)
argument_list|)
expr_stmt|;
name|Privilege
name|jcrAll
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:all"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|privileges
init|=
name|asList
argument_list|(
name|jcrAll
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|privileges
operator|.
name|contains
argument_list|(
name|privilege
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|privileges
operator|.
name|contains
argument_list|(
name|privilege2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|privileges
operator|.
name|contains
argument_list|(
name|aggregate
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|privileges
operator|.
name|contains
argument_list|(
name|aggregate2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyCustomPrivilegeBits
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|privilegeRoot
init|=
name|session
operator|.
name|getNode
argument_list|(
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
name|Node
name|testPrivilegeNode
init|=
name|privilegeRoot
operator|.
name|getNode
argument_list|(
literal|"test:privilege"
argument_list|)
decl_stmt|;
name|long
name|l
init|=
name|getLong
argument_list|(
name|testPrivilegeNode
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|pb
init|=
name|readBits
argument_list|(
name|testPrivilegeNode
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_BITS
argument_list|)
decl_stmt|;
name|Node
name|testPrivilege2Node
init|=
name|privilegeRoot
operator|.
name|getNode
argument_list|(
literal|"test:privilege2"
argument_list|)
decl_stmt|;
name|long
name|l2
init|=
name|getLong
argument_list|(
name|testPrivilege2Node
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|pb2
init|=
name|readBits
argument_list|(
name|testPrivilege2Node
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_BITS
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|nextExpected
decl_stmt|;
if|if
condition|(
name|l
operator|<
name|l2
condition|)
block|{
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|NEXT_AFTER_BUILT_INS
argument_list|,
name|pb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pb
operator|.
name|nextBits
argument_list|()
argument_list|,
name|pb2
argument_list|)
expr_stmt|;
name|nextExpected
operator|=
name|pb2
operator|.
name|nextBits
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|PrivilegeBits
operator|.
name|NEXT_AFTER_BUILT_INS
argument_list|,
name|pb2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pb2
operator|.
name|nextBits
argument_list|()
argument_list|,
name|pb
argument_list|)
expr_stmt|;
name|nextExpected
operator|=
name|pb
operator|.
name|nextBits
argument_list|()
expr_stmt|;
block|}
comment|// make sure the next-value has been properly set
name|PrivilegeBits
name|nextBits
init|=
name|readBits
argument_list|(
name|privilegeRoot
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_NEXT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nextExpected
argument_list|,
name|nextBits
argument_list|)
expr_stmt|;
name|Node
name|testAggregateNode
init|=
name|privilegeRoot
operator|.
name|getNode
argument_list|(
literal|"test:aggregate"
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|aggrPb
init|=
name|readBits
argument_list|(
name|testAggregateNode
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_BITS
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|expected
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|pb
argument_list|)
operator|.
name|unmodifiable
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|aggrPb
argument_list|)
expr_stmt|;
name|Node
name|testAggregate2Node
init|=
name|privilegeRoot
operator|.
name|getNode
argument_list|(
literal|"test:aggregate2"
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|aggr2Pb
init|=
name|readBits
argument_list|(
name|testAggregate2Node
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_BITS
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|expected2
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|aggrPb
argument_list|,
name|pb2
argument_list|)
operator|.
name|unmodifiable
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected2
argument_list|,
name|aggr2Pb
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|PrivilegeBits
name|readBits
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|getLong
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_BITS
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|0
index|]
operator|.
name|getLong
argument_list|()
return|;
block|}
block|}
end_class

end_unit

