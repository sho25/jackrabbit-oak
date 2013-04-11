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
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|AccessControlEntry
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
name|security
operator|.
name|JackrabbitAccessControlList
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
name|user
operator|.
name|Group
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
name|principal
operator|.
name|EveryonePrincipal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
comment|/**  * InheritanceTest... TODO  */
end_comment

begin_class
specifier|public
class|class
name|InheritanceTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
name|Group
name|group2
decl_stmt|;
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
comment|/* create a second group the test user is member of */
name|group2
operator|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|group2
operator|.
name|addMember
argument_list|(
name|testUser
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// recreate test session
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|testAcMgr
operator|=
name|testSession
operator|.
name|getAccessControlManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|group2
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
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
name|testInheritance
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'modify_properties' and 'remove_node' privilege on 'path'
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_REMOVE_NODE
block|,
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
block|}
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
comment|// give 'add-child-nodes', remove_child_nodes' on 'childNPath'
name|privileges
operator|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|}
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|childNPath
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
comment|/*         since evaluation respects inheritance through the node         hierarchy, the following privileges must now be given at 'childNPath':         - jcr:read         - jcr:modifyProperties         - jcr:addChildNodes         - jcr:removeChildNodes         - jcr:removeNode         */
name|Privilege
index|[]
name|expectedPrivileges
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|,
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_NODE
block|,
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|expectedPrivileges
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          ... permissions granted at childNPath:          - read          - set-property           BUT NOT:          - add-node          - remove.          */
name|String
name|aActions
init|=
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_SET_PROPERTY
operator|+
literal|','
operator|+
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_READ
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|childNPath
argument_list|,
name|aActions
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|dActions
init|=
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_REMOVE
operator|+
literal|','
operator|+
name|javax
operator|.
name|jcr
operator|.
name|Session
operator|.
name|ACTION_ADD_NODE
decl_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|childNPath
argument_list|,
name|dActions
argument_list|)
argument_list|)
expr_stmt|;
comment|/*         ... permissions granted at any child item of child-path:         - read         - set-property         - add-node         - remove         */
name|String
name|nonExistingItemPath
init|=
name|childNPath
operator|+
literal|"/anyItem"
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|nonExistingItemPath
argument_list|,
name|aActions
operator|+
literal|','
operator|+
name|dActions
argument_list|)
argument_list|)
expr_stmt|;
comment|/* try adding a new child node -> must succeed. */
name|Node
name|childN
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|String
name|testPath
init|=
name|childN
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|/* test privileges on the 'new' child node */
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|testPath
argument_list|,
name|expectedPrivileges
argument_list|)
argument_list|)
expr_stmt|;
comment|/* repeat test after save. */
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|testPath
argument_list|,
name|expectedPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInheritance2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give jcr:write privilege on 'path' and withdraw them on 'childNPath'
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_WRITE
block|}
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|childNPath
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
comment|/*         since evaluation respects inheritance through the node         hierarchy, the jcr:write privilege must not be granted at childNPath         */
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          ... same for permissions at 'childNPath'          */
name|String
name|actions
init|=
name|getActions
argument_list|(
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|,
name|Session
operator|.
name|ACTION_REMOVE
argument_list|,
name|Session
operator|.
name|ACTION_ADD_NODE
argument_list|)
decl_stmt|;
name|String
name|nonExistingItemPath
init|=
name|childNPath
operator|+
literal|"/anyItem"
decl_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|nonExistingItemPath
argument_list|,
name|actions
argument_list|)
argument_list|)
expr_stmt|;
comment|// yet another level in the hierarchy
name|Node
name|grandChild
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|gcPath
init|=
name|grandChild
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// grant write privilege again
name|allow
argument_list|(
name|gcPath
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|gcPath
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|gcPath
operator|+
literal|"/anyProp"
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
comment|// however: removing the grand-child nodes must not be allowed as
comment|// remove_child_node privilege is missing on the direct ancestor.
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|gcPath
argument_list|,
name|Session
operator|.
name|ACTION_REMOVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInheritedGroupPermissions
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* allow MODIFY_PROPERTIES privilege for testGroup at 'path' */
name|allow
argument_list|(
name|path
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/* deny MODIFY_PROPERTIES privilege for everyone at 'childNPath' */
name|deny
argument_list|(
name|childNPath
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|// result at 'child path' must be deny
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInheritedGroupPermissions2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: same as testInheritedGroupPermissions above but using
comment|// everyone on path, testgroup on childpath -> result must be the same
comment|/* allow MODIFY_PROPERTIES privilege for everyone at 'path' */
name|allow
argument_list|(
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/* deny MODIFY_PROPERTIES privilege for testGroup at 'childNPath' */
name|deny
argument_list|(
name|childNPath
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|// result at 'child path' must be deny
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleGroupPermissionsOnNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* add privileges for the Group the test-user is member of */
name|allow
argument_list|(
name|path
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/*          testuser must get the permissions/privileges inherited from          the group it is member of.          the denial of group2 must succeed         */
name|String
name|actions
init|=
name|getActions
argument_list|(
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|actions
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleGroupPermissionsOnNode2
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* add privileges for the Group the test-user is member of */
name|deny
argument_list|(
name|path
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/*          testuser must get the permissions/privileges inherited from          the group it is member of.          granting permissions for group2 must be effective         */
name|String
name|actions
init|=
name|getActions
argument_list|(
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|actions
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-526 : missing handling for reorder in PermissionHook"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testReorderGroupPermissions
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* add privileges for the Group the test-user is member of */
name|deny
argument_list|(
name|path
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|group2
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/*          testuser must get the permissions/privileges inherited from          the group it is member of.          granting permissions for group2 must be effective         */
name|String
name|actions
init|=
name|getActions
argument_list|(
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|actions
argument_list|)
argument_list|)
expr_stmt|;
name|Privilege
index|[]
name|privs
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privs
argument_list|)
argument_list|)
expr_stmt|;
comment|// reorder the ACEs
name|AccessControlEntry
name|srcEntry
init|=
literal|null
decl_stmt|;
name|AccessControlEntry
name|destEntry
init|=
literal|null
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
operator|(
name|JackrabbitAccessControlList
operator|)
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|AccessControlEntry
name|entry
range|:
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
control|)
block|{
name|Principal
name|princ
init|=
name|entry
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|testGroup
operator|.
name|getPrincipal
argument_list|()
operator|.
name|equals
argument_list|(
name|princ
argument_list|)
condition|)
block|{
name|destEntry
operator|=
name|entry
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|group2
operator|.
name|getPrincipal
argument_list|()
operator|.
name|equals
argument_list|(
name|princ
argument_list|)
condition|)
block|{
name|srcEntry
operator|=
name|entry
expr_stmt|;
block|}
block|}
name|acl
operator|.
name|orderBefore
argument_list|(
name|srcEntry
argument_list|,
name|destEntry
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/* after reordering the permissions must be denied */
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|actions
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privs
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInheritanceAndMixedUserGroupPermissions
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* give MODIFY_PROPERTIES privilege for testGroup at 'path' */
name|allow
argument_list|(
name|path
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/* withdraw MODIFY_PROPERTIES for the user at 'path' */
name|deny
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
comment|/*          since user-permissions overrule the group permissions, testuser must          not have set_property action / modify_properties privilege.          */
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          give MODIFY_PROPERTIES privilege for everyone at 'childNPath'          -> user-privileges still overrule group privileges          */
name|allow
argument_list|(
name|childNPath
argument_list|,
name|testGroup
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|modPropPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|modPropPrivileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCancelInheritanceRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|repWritePrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|repWritePrivileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|repWritePrivileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|childNPath
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|childNPath2
argument_list|,
name|repWritePrivileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|hasPermission
argument_list|(
name|childNPath2
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

