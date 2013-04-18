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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|annotation
operator|.
name|Nonnull
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
name|JackrabbitAccessControlManager
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
name|api
operator|.
name|ContentSession
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * EffectivePolicyTest... TODO  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"OAK-51"
argument_list|)
specifier|public
class|class
name|EffectivePolicyTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
name|String
name|childNPath
decl_stmt|;
specifier|protected
name|ContentSession
name|testSession
decl_stmt|;
specifier|protected
name|Root
name|testRoot
decl_stmt|;
specifier|protected
name|JackrabbitAccessControlManager
name|testAccessControlManager
decl_stmt|;
annotation|@
name|Override
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
comment|// create some nodes below the test root in order to apply ac-stuff
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
argument_list|)
decl_stmt|;
name|NodeUtil
name|testRootNode
init|=
name|rootNode
operator|.
name|getOrAddChild
argument_list|(
literal|"testRoot"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeUtil
name|testNode
init|=
name|testRootNode
operator|.
name|addChild
argument_list|(
literal|"testNode"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeUtil
name|cn1
init|=
name|testNode
operator|.
name|addChild
argument_list|(
literal|"child1"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setString
argument_list|(
literal|"property1"
argument_list|,
literal|"anyValue"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|path
operator|=
name|testNode
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|childNPath
operator|=
name|cn1
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|testRoot
operator|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|testAccessControlManager
operator|=
name|getAccessControlManager
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
comment|/*          precondition:          testuser must have READ-only permission on test-node and below         */
name|Privilege
index|[]
name|privs
init|=
name|testAccessControlManager
operator|.
name|getPrivileges
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|privilegesFromNames
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|privs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|testSession
operator|!=
literal|null
condition|)
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
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
name|Nonnull
specifier|private
name|JackrabbitAccessControlList
name|modify
parameter_list|(
name|String
name|path
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|Exception
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|acl
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|JackrabbitAccessControlList
name|allow
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|getTestPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|JackrabbitAccessControlList
name|deny
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|getTestPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEffectivePoliciesByPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'testUser' READ_AC privileges at 'path'
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromNames
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
name|assertFalse
argument_list|(
name|testAccessControlManager
operator|.
name|hasPrivileges
argument_list|(
literal|"/"
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAccessControlManager
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
comment|// since read-ac access is denied on the root that by default is
comment|// access controlled, getEffectivePolicies must fail due to missing
comment|// permissions to view all the effective policies.
try|try
block|{
name|testAccessControlManager
operator|.
name|getEffectivePolicies
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
comment|// ... and same on childNPath.
try|try
block|{
name|testAccessControlManager
operator|.
name|getEffectivePolicies
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testGetEffectivePoliciesByPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// give 'testUser' READ_AC privileges at 'path'
name|Privilege
index|[]
name|privileges
init|=
name|privilegesFromNames
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
comment|// effective policies for testPrinicpal only on path -> must succeed.
name|testAccessControlManager
operator|.
name|getEffectivePolicies
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|getTestPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// effective policies for a combination of principals -> must fail since
comment|// policy for 'everyone' at root node cannot be read by testuser
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
try|try
block|{
name|testAccessControlManager
operator|.
name|getEffectivePolicies
argument_list|(
name|principals
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|deny
argument_list|(
name|childNPath
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// the effective policies included the allowed acl at 'path' and
comment|// the denied acl at 'childNPath' -> must fail
try|try
block|{
name|testAccessControlManager
operator|.
name|getEffectivePolicies
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|getTestPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
block|}
end_class

end_unit

