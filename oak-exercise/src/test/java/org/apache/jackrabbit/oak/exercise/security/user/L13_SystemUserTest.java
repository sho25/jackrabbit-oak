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
name|user
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
name|Iterator
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
name|Credentials
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
name|principal
operator|.
name|PrincipalIterator
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
name|principal
operator|.
name|PrincipalManager
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
name|CommitFailedException
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
name|exercise
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|L3_PrecedenceRulesTest
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
name|user
operator|.
name|UserConstants
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: User Management  * =============================================================================  *  * Title: System Users  * -----------------------------------------------------------------------------  *  * Goal:  * Understand the difference between system users and regular users and why we  * decided to introduce a dedicated API for system users in Jackrabbit API 2.10.  *  * Exercises:  *  * - Overview  *   Walk through the system user creation in the setup and also take a closer  *   look at the node type definition in the builtin_nodetypes.cnd  *  *   Question: What can you say about the {@code rep:SystemUser} node type definition?  *   Question: Can you explain how this is reflected in the default user management implementation?  *   Question: How is a system user different from a regular user? Can you list all differences?  *  * - {@link #testSystemUser()}  *   This test retrieves the system user created in the setup. Complete the  *   test to become familiar with the handing of system users in the user  *   management API.  *  * - {@link #testSystemUserNode()}  *   This test illustrates some of the implementation details on how system users  *   are being represented in the repository. Complete the test to get it pass.  *  *   Question: Can you explain what the 'memberOf' method will return if there  *             exists an everyone authorizable group?  *  * - {@link #testSystemUserPrincipal()}  *   Look at the principal associated with the system user created in the setup.  *   Verify your expectations wrt principal name and type of principal and the  *   group membership and fix the test accordingly.  *  *   Question: Can you elaborate about the impact of the test results when it comes to  *             permission evaluation for system users?  *             Use {@link L3_PrecedenceRulesTest}  *             to verify your expectations or to get some more insight.  *  * - {@link #testSetPassword()}  *   This test attempts to set a password to the system user created in the  *   setup. Fix the test and the assertion and explain the behavior.  *  *   Question: How is setting passwords different for system users compared to regular users?  *   Question: Look at the node type definition again. What can you state wrt rep:password in the effective node type?  *   Question: Walk through the test again. Can you identify the exact location(s) for special handling?  *  * - {@link #testGetCredentials()}  *   Look at the credentials object exposed by the system user and compare it  *   with the result as return in {@link L11_PasswordTest#testGetCredentials()}  *  *</pre>  *  * @see<a href="https://issues.apache.org/jira/browse/JCR-3802">JCR-3802</a>  */
end_comment

begin_class
specifier|public
class|class
name|L13_SystemUserTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|User
name|systemUser
decl_stmt|;
specifier|private
name|Group
name|testGroup
decl_stmt|;
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
name|systemUser
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createSystemUser
argument_list|(
name|ExerciseUtility
operator|.
name|getTestId
argument_list|(
literal|"testSystemUser"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testGroup
operator|=
name|ExerciseUtility
operator|.
name|createTestGroup
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|systemUser
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|systemUser
operator|!=
literal|null
condition|)
block|{
name|systemUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Authorizable
name|authorizable
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|systemUser
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|Boolean
name|isGroup
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|isGroup
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|authorizable
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|isAdmin
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|isAdmin
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|systemUser
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|// EXERCISE: retrieve the authorizable by class: what is the correct authorizble-class to use?
name|Class
name|cls
init|=
literal|null
decl_stmt|;
name|Authorizable
name|sUser
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|systemUser
operator|.
name|getID
argument_list|()
argument_list|,
name|cls
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Group
argument_list|>
name|memberOf
init|=
name|sUser
operator|.
name|memberOf
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Group
argument_list|>
name|expectedGroups
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
while|while
condition|(
name|memberOf
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|expectedGroups
operator|.
name|remove
argument_list|(
name|memberOf
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedGroups
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
name|testSystemUserNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|systemUserTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|systemUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|systemUserTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expectedPrimaryTypeName
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedPrimaryTypeName
argument_list|,
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|systemUserTree
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expectedId
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedId
argument_list|,
name|TreeUtil
operator|.
name|getString
argument_list|(
name|systemUserTree
argument_list|,
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expectedPw
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedPw
argument_list|,
name|TreeUtil
operator|.
name|getString
argument_list|(
name|systemUserTree
argument_list|,
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemUserPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Authorizable
name|authorizable
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|systemUser
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
comment|// EXERCISE: what is the nature of the principal of the system user? Assert your expectedation.
name|Principal
name|principal
init|=
name|authorizable
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|PrincipalManager
name|principalManager
init|=
name|getPrincipalManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|PrincipalIterator
name|pIter
init|=
name|principalManager
operator|.
name|getGroupMembership
argument_list|(
name|principal
argument_list|)
decl_stmt|;
name|int
name|expectedSize
init|=
operator|-
literal|1
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|pIter
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Principal
argument_list|>
name|expectedGroupPrincipals
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
while|while
condition|(
name|pIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|group
init|=
name|pIter
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|expectedGroupPrincipals
operator|.
name|remove
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedGroupPrincipals
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
name|testSetPassword
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|systemUser
operator|.
name|changePassword
argument_list|(
name|ExerciseUtility
operator|.
name|TEST_PW
argument_list|)
expr_stmt|;
name|Tree
name|systemUserTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|systemUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedPw
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedPw
argument_list|,
name|TreeUtil
operator|.
name|getString
argument_list|(
name|systemUserTree
argument_list|,
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
name|systemUserTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
literal|"anotherPw"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCredentials
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE look at the Credentials object returned from the system user and compare it with the result from PasswordTest#getCredentials()
name|Credentials
name|creds
init|=
name|systemUser
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
comment|// EXERCISE fix the expectation
name|Credentials
name|expected
init|=
literal|null
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|creds
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

