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
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AbstractAccessControlTest
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
name|util
operator|.
name|NodeUtil
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * AccessControlValidatorTest... TODO  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlValidatorTest
extends|extends
name|AbstractAccessControlTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|final
name|String
name|testName
init|=
literal|"testRoot"
decl_stmt|;
specifier|private
specifier|final
name|String
name|testPath
init|=
literal|'/'
operator|+
name|testName
decl_stmt|;
specifier|private
specifier|final
name|String
name|aceName
init|=
literal|"validAce"
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal2
decl_stmt|;
annotation|@
name|Before
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
name|NodeUtil
name|rootNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|addChild
argument_list|(
name|testName
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// TODO
name|testPrincipal
operator|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"testPrincipal"
argument_list|)
expr_stmt|;
name|testPrincipal2
operator|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"anotherPrincipal"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Tree
name|testRoot
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|testRoot
operator|!=
literal|null
condition|)
block|{
name|testRoot
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|NodeUtil
name|getTestRoot
parameter_list|()
block|{
return|return
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|testPath
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|NodeUtil
name|createAcl
parameter_list|()
block|{
name|NodeUtil
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|setNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|MIX_REP_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
name|NodeUtil
name|acl
init|=
name|testRoot
operator|.
name|addChild
argument_list|(
name|REP_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|)
decl_stmt|;
name|NodeUtil
name|ace
init|=
name|createACE
argument_list|(
name|acl
argument_list|,
name|aceName
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|ace
operator|.
name|addChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
expr_stmt|;
return|return
name|acl
return|;
block|}
specifier|private
specifier|static
name|NodeUtil
name|createACE
parameter_list|(
name|NodeUtil
name|acl
parameter_list|,
name|String
name|aceName
parameter_list|,
name|String
name|ntName
parameter_list|,
name|String
name|principalName
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|addChild
argument_list|(
name|aceName
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|ace
operator|.
name|setString
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|principalName
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setNames
argument_list|(
name|REP_PRIVILEGES
argument_list|,
name|privilegeNames
argument_list|)
expr_stmt|;
return|return
name|ace
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPolicyWithOutChildOrder
parameter_list|()
block|{
name|NodeUtil
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|setNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|MIX_REP_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|addChild
argument_list|(
name|REP_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Policy node with child node ordering"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid policy node: Order of children is not stable."
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyRootIsRepoAccessControllable
parameter_list|()
block|{
name|NodeUtil
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|setNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Only the root node can be made RepoAccessControllable."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddInvalidRepoPolicy
parameter_list|()
block|{
name|NodeUtil
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|setNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|MIX_REP_ACCESS_CONTROLLABLE
argument_list|)
expr_stmt|;
name|NodeUtil
name|policy
init|=
name|getTestRoot
argument_list|()
operator|.
name|addChild
argument_list|(
name|REP_REPO_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to add repo-policy with rep:AccessControllable node."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|policy
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddPolicyWithAcContent
parameter_list|()
block|{
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
decl_stmt|;
name|NodeUtil
index|[]
name|acContent
init|=
operator|new
name|NodeUtil
index|[]
block|{
name|acl
block|,
name|ace
block|,
name|ace
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|NodeUtil
name|node
range|:
name|acContent
control|)
block|{
name|NodeUtil
name|policy
init|=
name|node
operator|.
name|addChild
argument_list|(
name|REP_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding an ACL below access control content should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|policy
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRepoPolicyWithAcContent
parameter_list|()
block|{
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
decl_stmt|;
name|NodeUtil
index|[]
name|acContent
init|=
operator|new
name|NodeUtil
index|[]
block|{
name|acl
block|,
name|ace
block|,
name|ace
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|NodeUtil
name|node
range|:
name|acContent
control|)
block|{
name|NodeUtil
name|policy
init|=
name|node
operator|.
name|addChild
argument_list|(
name|REP_REPO_POLICY
argument_list|,
name|NT_REP_ACL
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding an ACL below access control content should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|policy
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddAceWithAcContent
parameter_list|()
block|{
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
decl_stmt|;
name|NodeUtil
index|[]
name|acContent
init|=
operator|new
name|NodeUtil
index|[]
block|{
name|ace
block|,
name|ace
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|NodeUtil
name|node
range|:
name|acContent
control|)
block|{
name|NodeUtil
name|entry
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"invalidACE"
argument_list|,
name|NT_REP_DENY_ACE
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding an ACE below an ACE or restriction should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|entry
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRestrictionWithAcContent
parameter_list|()
block|{
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|NodeUtil
name|ace
init|=
name|acl
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
decl_stmt|;
name|NodeUtil
index|[]
name|acContent
init|=
operator|new
name|NodeUtil
index|[]
block|{
name|acl
block|,
name|ace
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|NodeUtil
name|node
range|:
name|acContent
control|)
block|{
name|NodeUtil
name|entry
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"invalidRestriction"
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Adding an ACE below an ACE or restriction should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|entry
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddIsolatedPolicy
parameter_list|()
block|{
name|String
index|[]
name|policyNames
init|=
operator|new
name|String
index|[]
block|{
literal|"isolatedACL"
block|,
name|REP_POLICY
block|,
name|REP_REPO_POLICY
block|}
decl_stmt|;
name|NodeUtil
name|node
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|policyName
range|:
name|policyNames
control|)
block|{
name|NodeUtil
name|policy
init|=
name|node
operator|.
name|addChild
argument_list|(
name|policyName
argument_list|,
name|NT_REP_ACL
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Writing an isolated ACL without the parent being rep:AccessControllable should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// revert pending changes that cannot be saved.
name|policy
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddIsolatedAce
parameter_list|()
block|{
name|String
index|[]
name|ntNames
init|=
operator|new
name|String
index|[]
block|{
name|NT_REP_DENY_ACE
block|,
name|NT_REP_GRANT_ACE
block|}
decl_stmt|;
name|NodeUtil
name|node
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|aceNtName
range|:
name|ntNames
control|)
block|{
name|NodeUtil
name|ace
init|=
name|createACE
argument_list|(
name|node
argument_list|,
literal|"isolatedACE"
argument_list|,
name|aceNtName
argument_list|,
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Writing an isolated ACE should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// revert pending changes that cannot be saved.
name|ace
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddIsolatedRestriction
parameter_list|()
block|{
name|NodeUtil
name|node
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|NodeUtil
name|restriction
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"isolatedRestriction"
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Writing an isolated Restriction should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// revert pending changes that cannot be saved.
name|restriction
operator|.
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidPrivilege
parameter_list|()
block|{
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|String
name|privName
init|=
literal|"invalidPrivilegeName"
decl_stmt|;
name|createACE
argument_list|(
name|acl
argument_list|,
literal|"invalid"
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|testPrincipal2
operator|.
name|getName
argument_list|()
argument_list|,
name|privName
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating an ACE with invalid privilege should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAbstractPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|PrivilegeManager
name|pMgr
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getPrivilegeConfiguration
argument_list|()
operator|.
name|getPrivilegeManager
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|pMgr
operator|.
name|registerPrivilege
argument_list|(
literal|"abstractPrivilege"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|NodeUtil
name|acl
init|=
name|createAcl
argument_list|()
decl_stmt|;
name|createACE
argument_list|(
name|acl
argument_list|,
literal|"invalid"
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|testPrincipal2
operator|.
name|getName
argument_list|()
argument_list|,
literal|"abstractPrivilege"
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating an ACE with an abstract privilege should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeUtil
name|restriction
init|=
name|createAcl
argument_list|()
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|restriction
operator|.
name|setString
argument_list|(
literal|"invalid"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating an unsupported restriction should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestrictionWithInvalidType
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeUtil
name|restriction
init|=
name|createAcl
argument_list|()
operator|.
name|getChild
argument_list|(
name|aceName
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|restriction
operator|.
name|setName
argument_list|(
name|REP_GLOB
argument_list|,
literal|"rep:glob"
argument_list|)
expr_stmt|;
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating restriction with invalid type should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

