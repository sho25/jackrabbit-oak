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
name|exercise
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
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
name|Property
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
name|Value
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
name|AccessControlManager
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
name|JcrConstants
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
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|UserManager
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
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|exercise
operator|.
name|security
operator|.
name|privilege
operator|.
name|L5_PrivilegeContentTest
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|exercise
operator|.
name|ExerciseUtility
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
name|Permissions
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
name|test
operator|.
name|AbstractJCRTest
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
name|api
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authorization (Permission Evaluation)  * =============================================================================  *  * Title: Privileges and Permissions  * -----------------------------------------------------------------------------  *  * Goal:  * The aim of this is test is to make you familiar with the subtle differences  * between privileges (that are always granted on an existing node) and effective  * permissions on the individual items (nodes, properties or even non-existing  * items).  * Having completed this exercise you should also be familiar with the oddities  * of those privileges that allow modify the parent collection, while the effective  * permission is evaluated the target item.  *  * Exercises:  *  * - {@link #testAddNodes()}  *   This test aims to practise the subtle difference between granting 'jcr:addChildNode'  *   privilege at a given parent node and {@link Session#hasPermission(String, String)}  *   using {@link Session#ACTION_ADD_NODE}, which effectively tests if a given  *   new node could be created.  *   Fill in the expected values for the permission discovery with the given  *   permission setup. Subsequently, list the expected values for  *   {@link javax.jcr.security.AccessControlManager#getPrivileges(String)}.  *   Explain the difference given the mini-explanation in the test introduction.  *  * - {@link #testAddProperties()}  *   This test grants the test user privilege to add properties at 'childPath'.  *   Fill in the expected result of {@link javax.jcr.Session#hasPermission(String, String)}  *   both for the regular JCR action {@link Session#ACTION_SET_PROPERTY} and  *   for the string value of {@link Permissions#ADD_PROPERTY}. Compare and explain  *   the differences.  *  * - {@link #testRemoveNodes()} ()}  *   This test illustrates what kind of privileges are required in order to  *   remove a given node. Fix the test-case by setting the correct permission  *   setup.  *  * - {@link #testRemoveProperties()} ()}  *   In this test the test-user is only granted rep:removeProperties privilege  *   and lists which properties must be removable. Nevertheless the test-case is  *   broken... can you identify the root cause without modifying the result map  *   nor the permission setup?  *  * - {@link #testRemoveNonExistingItems()}  *   Look at the contract of {@link Session#hasPermission(String, String)} and  *   the implementation present in Oak to understand what happens if you test  *   permissions for removal of non-existing items.  *  * - {@link #testModifyProperties()}  *   Now the test-user is just allowed to modify properties. Complete the test  *   by modifying the result-map: add for each path whether accessing and setting  *   the properties is expected to succeed. Explain for each path why it passes  *   or fails. In case of doubt debug the test to understand what is going on.  *  * - {@link #testSetProperty()}  *   Properties cannot only be modified by calling {@link Property#setValue}  *   but also by calling {@link Node#setProperty} each for different types and  *   single vs multivalued properties. While they are mostly equivalent there  *   are subtle differences that have an impact on the permission evaluation.  *   Can you find suitable value(s) for each of these paths such that the  *   test passes?  *   Hint: passing a 'null' value is defined to be equivalent to removal.  *   Discuss your findings.  *  *   Question: Can you explain the required values if the test user was allowed  *   to remove properties instead? -> Modify the test to verify your expectations.  *  * - {@link #testChangingPrimaryAndMixinTypes()}  *   In order to change the primary (or mixin) type(s) of a given existing node  *   the editing session must have {@link Privilege#JCR_NODE_TYPE_MANAGEMENT}  *   privilege granted.  *   For consistency with this requirement, also {@link Node#addNode(String, String)},  *   which explicitly specifies the primary type requires this privilege as  *   the effective operation is equivalent to the combination of:  *   1. {@link Node#addNode(String)} +  *   2. {@link Node#setPrimaryType(String)}  *   Use this test case to become familiar with setting or changing the primary  *   type and modifying the set of mixin types.  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * - Modifying Nodes  *   Discuss why there is no dedicated privilege (and test case) for "modify nodes".  *   Explain how {@link Privilege#JCR_NODE_TYPE_MANAGEMENT, {@link Privilege#JCR_ADD_CHILD_NODES},  *   {@link Privilege#JCR_REMOVE_CHILD_NODES} (and maybe others) are linked to  *   node modification.  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * If you wished to become more familiar with the interals of the Oak permission  * evaluation, it is indispensible to understand the internal representation of  * registered privileges (as {@link org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeBits}  * and their mapping to {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions}.  *  * Precondition for these advanced exercises is the following test:  *  * - {@link L5_PrivilegeContentTest }  *  * The following exercises aim to provide you with some insight and allow you  * to understand the internal structure of the permission store.  *  * - PrivilegeBits and the corresponding provider class  *   Take a look at the methods exposed by  *   {@link org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeBitsProvider}  *   and the {@link org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeBits}  *   class itself. Using these classes Oak generates the repository internal  *   representation of the privileges that are used for  *   - calculation of effective privileges  *   - calculation of final permission from effective privileges granted (or denied)  *  * - Mapping Privileges to Permissions  *   As you could see in the above exercises there is no 1:1 mapping between  *   privileges and permissions as a few privileges (like jcr:addChildNodes and  *   jcr:removeChildNodes) are granted for the parent node. This needs to be  *   handled specially when evaluating if a given node can be created or removed.  *   Look at {@link org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeBits#calculatePermissions()}  *   and compare the code with the results obtained from the test-cases above.  *  * - Mapping of JCR Actions to Permissions  *   The exercises also illustrated the subtle differences between 'actions'  *   as defined on {@link javax.jcr.Session} and the effective permissions  *   being evaluated.  *   Take a closer look at {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions#getPermissions(String, org.apache.jackrabbit.oak.plugins.tree.TreeLocation, boolean)}  *   to improve your understanding on how the mapping is being implemented  *   and discuss the consequences for {@link Session#hasPermission(String, String)}.  *   Compare the code with the test-results.  *  *  * Related Exercises  * -----------------------------------------------------------------------------  *  * - {@link L5_SpecialPermissionsTest }  * - {@link L7_PermissionContentTest }  *  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|L4_PrivilegesAndPermissionsTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|Principal
name|testGroupPrincipal
decl_stmt|;
specifier|private
name|Session
name|testSession
decl_stmt|;
specifier|private
name|String
name|childPath
decl_stmt|;
specifier|private
name|String
name|grandChildPath
decl_stmt|;
specifier|private
name|String
name|propertyPath
decl_stmt|;
specifier|private
name|String
name|childPropertyPath
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
name|Property
name|p
init|=
name|testRootNode
operator|.
name|setProperty
argument_list|(
name|propertyName1
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
name|propertyPath
operator|=
name|p
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|Node
name|child
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|childPath
operator|=
name|child
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|p
operator|=
name|child
operator|.
name|setProperty
argument_list|(
name|propertyName2
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|childPropertyPath
operator|=
name|p
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|Node
name|grandChild
init|=
name|child
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|grandChildPath
operator|=
name|grandChild
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|testUser
operator|=
name|ExerciseUtility
operator|.
name|createTestUser
argument_list|(
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
argument_list|)
expr_stmt|;
name|Group
name|testGroup
init|=
name|ExerciseUtility
operator|.
name|createTestGroup
argument_list|(
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
argument_list|)
decl_stmt|;
name|testGroup
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
name|testPrincipal
operator|=
name|testUser
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|testGroupPrincipal
operator|=
name|testGroup
operator|.
name|getPrincipal
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
try|try
block|{
if|if
condition|(
name|testSession
operator|!=
literal|null
operator|&&
name|testSession
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|UserManager
name|uMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|Authorizable
name|testGroup
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|testGroupPrincipal
argument_list|)
decl_stmt|;
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Session
name|createTestSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|testSession
operator|==
literal|null
condition|)
block|{
name|testSession
operator|=
name|superuser
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|ExerciseUtility
operator|.
name|getTestCredentials
argument_list|(
name|testUser
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|testSession
return|;
block|}
specifier|public
name|void
name|testAddNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test principal jcr:addChildNode privilege at 'childPath'
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
comment|// EXERCISE: fill in the expected return values for Session.hasPermission as performed below
comment|// EXERCISE: verify that the test passes and explain the individual results
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathHasPermissionMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|,
literal|null
argument_list|,
name|childPath
argument_list|,
literal|null
argument_list|,
name|childPath
operator|+
literal|"/toCreate"
argument_list|,
literal|null
argument_list|,
name|grandChildPath
operator|+
literal|"/nextGeneration"
argument_list|,
literal|null
argument_list|,
name|propertyPath
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathHasPermissionMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|expectedHasPermission
init|=
name|pathHasPermissionMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedHasPermission
argument_list|,
name|userSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|Session
operator|.
name|ACTION_ADD_NODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// EXERCISE: fill in the expected return values for AccessControlManager#getPrivileges as performed below
comment|// EXERCISE: verify that the test passes and compare the results with your findings from the permission-discovery
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
index|[]
argument_list|>
name|pathPrivilegesMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|,
literal|null
argument_list|,
name|childPath
argument_list|,
literal|null
argument_list|,
name|childPath
operator|+
literal|"/toCreate"
argument_list|,
literal|null
argument_list|,
name|grandChildPath
operator|+
literal|"/nextGeneration"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathPrivilegesMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Privilege
index|[]
name|expectedPrivileges
init|=
name|pathPrivilegesMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|expectedPrivileges
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userSession
operator|.
name|getAccessControlManager
argument_list|()
operator|.
name|getPrivileges
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// EXERCISE: optionally add nodes at the expected allowed path(s)
comment|// EXERCISE: using 'userSession' to verify that it actually works and
comment|// EXERCISE: save the changes to trigger the evaluation
block|}
specifier|public
name|void
name|testAddProperties
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test principal rep:addProperties privilege at 'childPath'
comment|// EXERCISE: explain the difference between rep:addProperites and jcr:modifyProperties privilege!
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_ADD_PROPERTIES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: fill in the expected return values for Session.hasPermission as performed below
comment|// EXERCISE: verify that the test passes and explain the individual results
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
index|[]
argument_list|>
name|pathHasPermissionMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|propertyPath
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|null
block|,
literal|null
block|}
argument_list|,
name|childPath
operator|+
literal|"/newProp"
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|null
block|,
literal|null
block|}
argument_list|,
name|childPropertyPath
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|null
block|,
literal|null
block|}
argument_list|,
name|grandChildPath
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|null
block|,
literal|null
block|}
argument_list|)
decl_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathHasPermissionMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Boolean
index|[]
name|result
init|=
name|pathHasPermissionMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|setPropertyAction
init|=
name|result
index|[
literal|0
index|]
decl_stmt|;
name|boolean
name|addPropertiesPermission
init|=
name|result
index|[
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|setPropertyAction
argument_list|,
name|userSession
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
name|assertEquals
argument_list|(
name|addPropertiesPermission
argument_list|,
name|userSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|Permissions
operator|.
name|getString
argument_list|(
name|Permissions
operator|.
name|ADD_PROPERTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testRemoveNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// EXERCISE: setup the correct set of privileges such that the test passes
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathHasPermissionMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|,
name|childPath
argument_list|,
literal|false
argument_list|,
name|grandChildPath
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathHasPermissionMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|expectedHasPermission
init|=
name|pathHasPermissionMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedHasPermission
argument_list|,
name|userSession
operator|.
name|hasPermission
argument_list|(
name|path
argument_list|,
name|Session
operator|.
name|ACTION_REMOVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AccessControlManager
name|acMgr
init|=
name|userSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|hasPrivileges
argument_list|(
name|childPath
argument_list|,
operator|new
name|Privilege
index|[]
block|{
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_NODE
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|getNode
argument_list|(
name|grandChildPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveProperties
parameter_list|()
throws|throws
name|Exception
block|{
comment|// EXERCISE: fix the test case without changing the result-map :-)
comment|// grant the test principal rep:removeProperties privilege at 'childPath'
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_REMOVE_PROPERTIES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathCanRemoveMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|propertyPath
argument_list|,
literal|false
argument_list|,
name|childPropertyPath
argument_list|,
literal|true
argument_list|,
name|grandChildPath
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
literal|false
argument_list|,
name|grandChildPath
operator|+
literal|"/"
operator|+
name|propertyName2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathCanRemoveMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|canRemove
init|=
name|pathCanRemoveMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|userSession
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|canRemove
condition|)
block|{
name|fail
argument_list|(
literal|"property at "
operator|+
name|path
operator|+
literal|" should not be removable."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
if|if
condition|(
name|canRemove
condition|)
block|{
name|fail
argument_list|(
literal|"property at "
operator|+
name|path
operator|+
literal|" should be removable."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testRemoveNonExistingItems
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
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
name|JCR_REMOVE_CHILD_NODES
block|,
name|PrivilegeConstants
operator|.
name|REP_REMOVE_PROPERTIES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: fill in the expected values for Session.hasPermission(path, Session.ACTION_REMOVE) and explain
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathHasPermission
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|childPath
operator|+
literal|"_non_existing_sibling"
argument_list|,
literal|null
argument_list|,
name|childPath
operator|+
literal|"/_non_existing_childitem"
argument_list|,
literal|null
argument_list|,
name|grandChildPath
operator|+
literal|"/_non_existing_child_item"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nonExistingItemPath
range|:
name|pathHasPermission
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|hasPermission
init|=
name|pathHasPermission
operator|.
name|get
argument_list|(
name|nonExistingItemPath
argument_list|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hasPermission
argument_list|,
name|userSession
operator|.
name|hasPermission
argument_list|(
name|nonExistingItemPath
argument_list|,
name|Session
operator|.
name|ACTION_REMOVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Additional exercise:
comment|// EXERCISE: change the set of privileges granted initially and observe the result of Session.hasPermission. Explain the diff
block|}
specifier|public
name|void
name|testModifyProperties
parameter_list|()
throws|throws
name|Exception
block|{
comment|// grant the test principal rep:alterProperties privilege at 'childPath'
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_ALTER_PROPERTIES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: Fill if setting the property at the path(s) is expected to pass or not
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathCanModify
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|propertyPath
argument_list|,
literal|null
argument_list|,
name|childPropertyPath
argument_list|,
literal|null
argument_list|,
name|grandChildPath
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
literal|null
argument_list|,
name|grandChildPath
operator|+
literal|"/"
operator|+
name|propertyName2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathCanModify
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|canModify
init|=
name|pathCanModify
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|userSession
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
operator|.
name|setValue
argument_list|(
literal|"newVal"
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|canModify
condition|)
block|{
name|fail
argument_list|(
literal|"setting property at "
operator|+
name|path
operator|+
literal|" should fail."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
if|if
condition|(
name|canModify
condition|)
block|{
name|fail
argument_list|(
literal|"setting property at "
operator|+
name|path
operator|+
literal|" should not fail."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testSetProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// grant the test principal rep:removeProperties privilege at 'childPath'
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|REP_ALTER_PROPERTIES
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: Fill if new properties values such that the test-cases succeeds
comment|// EXERCISE: Discuss your findings and explain each value.
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|pathResultMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|pathResultMap
operator|.
name|put
argument_list|(
name|childPropertyPath
argument_list|,
operator|(
name|Value
operator|)
literal|null
argument_list|)
expr_stmt|;
name|pathResultMap
operator|.
name|put
argument_list|(
name|grandChildPath
operator|+
literal|"/nonexisting"
argument_list|,
operator|(
name|Value
operator|)
literal|null
argument_list|)
expr_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathResultMap
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|Value
name|val
init|=
name|pathResultMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Node
name|parent
init|=
name|userSession
operator|.
name|getNode
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testChangingPrimaryAndMixinTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// 1 - grant privilege to change node type information
comment|// EXERCISE: fill in the required privilege name such that the test passes
name|String
name|privilegeName
init|=
literal|null
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|privilegeName
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|userSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|userSession
operator|.
name|getNode
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|n
operator|.
name|setPrimaryType
argument_list|(
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// 2 - additionally grant privilege to add a child node
comment|// EXERCISE: extend the set of privileges such that the adding a child node succeeds as well
name|privilegeName
operator|=
literal|null
expr_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|privilegeName
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|getNode
argument_list|(
name|childPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName4
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// 3 - revoke privilege to change node type information
comment|// EXERCISE: change the permission setup again such that the rest of the test passes
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|n
operator|=
name|userSession
operator|.
name|getNode
argument_list|(
name|childPath
argument_list|)
expr_stmt|;
name|n
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|n
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|userSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding node with explicitly the primary type should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|userSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|superuser
argument_list|,
name|childPath
argument_list|,
name|testPrincipal
argument_list|,
operator|new
name|String
index|[]
block|{
name|privilegeName
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

