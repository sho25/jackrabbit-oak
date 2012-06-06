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
name|List
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
name|test
operator|.
name|AbstractJCRTest
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
name|PrivilegeManagerImplTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|PrivilegeManager
name|privilegeMgr
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|protected
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
name|privilegeMgr
operator|=
operator|(
operator|(
name|JackrabbitWorkspace
operator|)
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|)
operator|.
name|getPrivilegeManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisteredPrivileges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Privilege
index|[]
name|ps
init|=
name|privilegeMgr
operator|.
name|getRegisteredPrivileges
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|l
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
name|ps
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:write"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:addProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:alterProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:removeProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// including repo-level operation privileges
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:namespaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:nodeTypeDefinitionManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:workspaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:privilegeManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
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
name|testAllPrivilege
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Privilege
name|p
init|=
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"jcr:all"
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
name|assertFalse
argument_list|(
name|p
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|l
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
name|p
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:write"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:addProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:alterProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:removeProperties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// including repo-level operation privileges
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:namespaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:nodeTypeDefinitionManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:workspaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:privilegeManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
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
name|p
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:write"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
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
comment|// including repo-level operation privileges
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:namespaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:nodeTypeDefinitionManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"jcr:workspaceManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
operator|.
name|remove
argument_list|(
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
literal|"rep:privilegeManagement"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l
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
name|privilegeMgr
operator|.
name|getPrivilege
argument_list|(
name|Privilege
operator|.
name|JCR_READ
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
literal|"jcr:read"
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
name|privilegeMgr
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
literal|"jcr:write"
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
name|privilegeMgr
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
name|testGetPrivilegesFromEmptyNames
parameter_list|()
block|{
try|try
block|{
name|privilegeMgr
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
name|privilegeMgr
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
comment|// TODO test privilege registration
block|}
end_class

end_unit

