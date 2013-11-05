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
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|AccessControlList
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
name|AccessControlPolicy
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
name|AccessControlPolicyIterator
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
name|test
operator|.
name|NotExecutableException
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
name|util
operator|.
name|Text
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
comment|/**  * Permission evaluation tests related to access control management.  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlManagementTest
extends|extends
name|AbstractEvaluationTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testReadAccessControlContent
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test access to ac content if the corresponding access controlled
comment|// parent node is not accessible.
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// the policy node however must be visible to the test-user
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|path
operator|+
literal|"/rep:policy/jcr:primaryType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|path
operator|+
literal|"/jcr:primaryType"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* grant 'testUser' rep:write, rep:readAccessControl and            rep:modifyAccessControl privileges at 'path' */
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
name|REP_WRITE
block|,
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
block|,
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
block|}
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
comment|/*          testuser must have          - permission to view AC items          - permission to modify AC items         */
comment|// the policy node however must be visible to the test-user
name|assertTrue
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test if a new applicable policy can be applied within a      * sub-tree where AC-modification is allowed.      *      * @see<a href="https://issues.apache.org/jira/browse/JCR-2869">JCR-2869</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testSetNewPolicy
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* grant 'testUser' rep:write, rep:readAccessControl and            rep:modifyAccessControl privileges at 'path' */
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
name|REP_WRITE
block|,
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
block|,
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
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
comment|/*          testuser must be allowed to set a new policy at a child node.         */
name|AccessControlPolicyIterator
name|it
init|=
name|testAcMgr
operator|.
name|getApplicablePolicies
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccessControlPolicy
name|plc
init|=
name|it
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
name|testAcMgr
operator|.
name|setPolicy
argument_list|(
name|childNPath
argument_list|,
name|plc
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|childNPath
argument_list|,
name|plc
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetModifiedPolicy
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* grant 'testUser' rep:write, rep:readAccessControl and            rep:modifyAccessControl privileges at 'path' */
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
name|REP_WRITE
block|,
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
block|,
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
block|}
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
comment|/*          testuser must be allowed to set (modified) policy at target node.         */
name|AccessControlPolicy
index|[]
name|policies
init|=
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|AccessControlList
argument_list|)
expr_stmt|;
name|AccessControlList
name|policy
init|=
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|policy
operator|.
name|addAccessControlEntry
argument_list|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_LOCK_MANAGEMENT
argument_list|)
argument_list|)
condition|)
block|{
name|testAcMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemovePolicyWithoutPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
comment|// re-grant READ in order to have an ACL-node
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|AccessControlPolicy
name|policy
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
comment|/*          Testuser must still have READ-only access only and must not be          allowed to view the acl-node that has been created.         */
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|path
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Test user must not be allowed to remove the access control policy."
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemovePolicy
parameter_list|()
throws|throws
name|Exception
block|{
comment|// re-grant READ in order to have an ACL-node
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
name|JCR_READ
block|,
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
block|,
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
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
comment|/*          Testuser must be allowed to view and remove the acl-node that has been created.         */
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|path
argument_list|,
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRetrievePrivilegesOnAcNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'testUser' jcr:readAccessControl privileges at 'path'
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
comment|/*          testuser must be allowed to read ac-content at target node.         */
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|JackrabbitAccessControlList
argument_list|)
expr_stmt|;
name|String
name|aclNodePath
init|=
literal|null
decl_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeIterator
name|itr
init|=
name|n
operator|.
name|getNodes
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|child
init|=
name|itr
operator|.
name|nextNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isNodeType
argument_list|(
literal|"rep:Policy"
argument_list|)
condition|)
block|{
name|aclNodePath
operator|=
name|child
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|aclNodePath
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Expected node at "
operator|+
name|path
operator|+
literal|" to have an ACL child node."
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|aclNodePath
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
name|aclNodePath
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeIterator
name|aceNodes
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|aclNodePath
argument_list|)
operator|.
name|getNodes
argument_list|()
init|;
name|aceNodes
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|aceNodePath
init|=
name|aceNodes
operator|.
name|nextNode
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|aceNodePath
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
name|aceNodePath
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadAccessControlWithoutPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
comment|// re-grant READ in order to have an ACL-node
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
name|String
name|policyPath
init|=
name|tmpl
operator|.
name|getPath
argument_list|()
operator|+
literal|"/rep:policy"
decl_stmt|;
comment|// make sure the 'rep:policy' node has been created.
name|assertTrue
argument_list|(
name|superuser
operator|.
name|itemExists
argument_list|(
name|policyPath
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          Testuser must still have READ-only access only and must not be          allowed to view the acl-node nor any item in the subtree that          has been created.         */
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|policyPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|policyPath
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|getNode
argument_list|(
name|policyPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Accessing the rep:policy node must throw PathNotFoundException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
try|try
block|{
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"test user must not have READ_AC privilege."
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
try|try
block|{
name|testAcMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"test user must not have READ_AC privilege."
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
for|for
control|(
name|NodeIterator
name|aceNodes
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|policyPath
argument_list|)
operator|.
name|getNodes
argument_list|()
init|;
name|aceNodes
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|aceNode
init|=
name|aceNodes
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|String
name|aceNodePath
init|=
name|aceNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|aceNodePath
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyIterator
name|it
init|=
name|aceNode
operator|.
name|getProperties
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|it
operator|.
name|nextProperty
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadAccessControl
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* give 'testUser' jcr:readAccessControl privileges at subtree below            path excluding the node at path itself. */
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
comment|/*          testuser must be allowed to read AC content at the target node...         */
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|/*          ... and the child node          */
name|assertTrue
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|childNPath
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadAccessControlWithRestriction
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* give 'testUser' jcr:readAccessControl privileges at subtree below            path excluding the node at path itself. */
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|'/'
operator|+
name|nodeName2
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          testuser must not be allowed to read AC content at the target node;          however, retrieving potential AC content at 'childPath' is granted.         */
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"AccessDeniedException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
name|assertTrue
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|childNPath
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAclReferingToRemovedPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|repWritePrivileges
argument_list|)
decl_stmt|;
name|String
name|acPath
init|=
name|acl
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// remove the test user
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testUser
operator|=
literal|null
expr_stmt|;
comment|// try to retrieve the acl again
name|Session
name|s
init|=
name|getHelper
argument_list|()
operator|.
name|getSuperuserSession
argument_list|()
decl_stmt|;
try|try
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|acPath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
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
name|testAccessControlModificationWithoutPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'testUser' ADD_CHILD_NODES|MODIFY_PROPERTIES| REMOVE_CHILD_NODES privileges at 'path'
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
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
block|}
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
name|String
name|policyPath
init|=
name|tmpl
operator|.
name|getPath
argument_list|()
operator|+
literal|"/rep:policy"
decl_stmt|;
comment|// make sure the 'rep:policy' node has been created.
name|assertTrue
argument_list|(
name|superuser
operator|.
name|itemExists
argument_list|(
name|policyPath
argument_list|)
argument_list|)
expr_stmt|;
comment|/*          testuser must not have          - permission to modify AC items         */
try|try
block|{
name|testAcMgr
operator|.
name|setPolicy
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|,
name|tmpl
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"test user must not have MODIFY_AC privilege."
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
try|try
block|{
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|,
name|tmpl
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"test user must not have MODIFY_AC privilege."
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlModification
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'testUser' READ_AC|MODIFY_AC privileges at 'path'
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
name|JCR_READ_ACCESS_CONTROL
block|,
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
block|}
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|allow
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
decl_stmt|;
comment|/*          testuser must          - still have the inherited READ permission.          - must have permission to view AC items at 'path' (and below)          - must have permission to modify AC items at 'path'           testuser must not have          - permission to view AC items outside of the tree defined by path.         */
comment|// make sure the 'rep:policy' node has been created.
name|assertTrue
argument_list|(
name|superuser
operator|.
name|itemExists
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test: MODIFY_AC granted at 'path'
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test if testuser can READ access control on the path and on the
comment|// entire subtree that gets the policy inherited.
name|AccessControlPolicy
index|[]
name|policies
init|=
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
comment|// test: READ_AC privilege does not apply outside of the tree.
try|try
block|{
name|testAcMgr
operator|.
name|getPolicies
argument_list|(
name|siblingPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"READ_AC privilege must not apply outside of the tree it has applied to."
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
comment|// test: MODIFY_AC privilege does not apply outside of the tree.
name|assertFalse
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|siblingPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test if testuser can modify AC-items
comment|// 1) add an ac-entry
name|AccessControlList
name|acl
init|=
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|repWritePrivileges
argument_list|)
expr_stmt|;
name|testAcMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
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
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2) remove the policy
name|testAcMgr
operator|.
name|removePolicy
argument_list|(
name|path
argument_list|,
name|policies
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Finally: testuser removed the policy that granted him permission
comment|// to modify the AC content. Since testuser removed the policy, it's
comment|// privileges must be gone again...
try|try
block|{
name|testAcMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"READ_AC privilege has been revoked -> must throw again."
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
comment|// ... and since the ACE is stored with the policy all right except
comment|// READ must be gone.
name|assertReadOnly
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAcContentIsProtected
parameter_list|()
throws|throws
name|Exception
block|{
comment|// search for a rep:policy node
name|Node
name|policyNode
init|=
name|findPolicyNode
argument_list|(
name|superuser
operator|.
name|getRootNode
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|policyNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
literal|"no policy node found."
argument_list|)
throw|;
block|}
name|assertTrue
argument_list|(
literal|"The rep:Policy node must be protected"
argument_list|,
name|policyNode
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|policyNode
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"rep:Policy node must be protected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
for|for
control|(
name|NodeIterator
name|it
init|=
name|policyNode
operator|.
name|getNodes
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"rep:ACE"
argument_list|)
condition|)
block|{
try|try
block|{
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"ACE node must be protected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
break|break;
block|}
block|}
try|try
block|{
name|policyNode
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"anyvalue"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"rep:policy node must be protected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|policyNode
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"rep:ACE"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"rep:policy node must be protected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
specifier|private
specifier|static
name|Node
name|findPolicyNode
parameter_list|(
name|Node
name|start
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|policyNode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|start
operator|.
name|isNodeType
argument_list|(
literal|"rep:Policy"
argument_list|)
condition|)
block|{
name|policyNode
operator|=
name|start
expr_stmt|;
block|}
for|for
control|(
name|NodeIterator
name|it
init|=
name|start
operator|.
name|getNodes
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|policyNode
operator|==
literal|null
condition|;
control|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"jcr:system"
operator|.
name|equals
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|policyNode
operator|=
name|findPolicyNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|policyNode
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReorderPolicyNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|n
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|hasOrderableChildNodes
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
literal|"Reordering child nodes is not supported.."
argument_list|)
throw|;
block|}
name|n
operator|.
name|orderBefore
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|childNPath2
argument_list|)
argument_list|,
name|Text
operator|.
name|getName
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"test session must not be allowed to reorder nodes."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
comment|// grant all privileges
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ALL
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|orderBefore
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|childNPath2
argument_list|)
argument_list|,
name|Text
operator|.
name|getName
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|n
operator|.
name|orderBefore
argument_list|(
literal|"rep:policy"
argument_list|,
name|Text
operator|.
name|getName
argument_list|(
name|childNPath2
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasNode
argument_list|(
literal|"rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"rep:AccessControllable"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|removeMixin
argument_list|(
literal|"rep:AccessControllable"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasNode
argument_list|(
literal|"rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"rep:AccessControllable"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

