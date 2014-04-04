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
operator|.
name|evaluation
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
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
import|;
end_import

begin_comment
comment|/**  * Base class for all classes that attempt to test OAK API and OAK core functionality  * in combination with permission evaluation  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractOakCoreTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|protected
name|Principal
name|testPrincipal
decl_stmt|;
specifier|private
name|ContentSession
name|testSession
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
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
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
argument_list|)
decl_stmt|;
name|NodeUtil
name|a
init|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|a
operator|.
name|setString
argument_list|(
literal|"aProp"
argument_list|,
literal|"aValue"
argument_list|)
expr_stmt|;
name|NodeUtil
name|b
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|b
operator|.
name|setString
argument_list|(
literal|"bProp"
argument_list|,
literal|"bValue"
argument_list|)
expr_stmt|;
comment|// sibling
name|NodeUtil
name|bb
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"bb"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|bb
operator|.
name|setString
argument_list|(
literal|"bbProp"
argument_list|,
literal|"bbValue"
argument_list|)
expr_stmt|;
name|NodeUtil
name|c
init|=
name|b
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|c
operator|.
name|setString
argument_list|(
literal|"cProp"
argument_list|,
literal|"cValue"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
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
comment|// revert uncommited changes
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// clean up policies at the root node
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|policies
control|)
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
literal|"/"
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
comment|// remove all test content
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
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
comment|// release test session
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
specifier|protected
name|ContentSession
name|getTestSession
parameter_list|()
throws|throws
name|Exception
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
name|createTestSession
argument_list|()
expr_stmt|;
block|}
return|return
name|testSession
return|;
block|}
annotation|@
name|Nonnull
specifier|protected
name|Root
name|getTestRoot
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getTestSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
comment|/**      * Same as {@link #setupPermission(org.apache.jackrabbit.oak.api.Root, String, java.security.Principal, boolean, String...)}      * where the specified root is the current root associated with the admin      * session created in the test setup.      *      * @param path The path of the access controlled tree.      * @param principal The principal for which new ACE is being created.      * @param isAllow {@code true} if privileges are granted; {@code false} otherwise.      * @param privilegeNames The privilege names.      * @throws Exception If an error occurs.      */
specifier|protected
name|void
name|setupPermission
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|root
argument_list|,
name|path
argument_list|,
name|principal
argument_list|,
name|isAllow
argument_list|,
name|privilegeNames
argument_list|)
expr_stmt|;
block|}
comment|/**      * Setup simple allow/deny permissions (without restrictions).      *      * @param root The editing root.      * @param path The path of the access controlled tree.      * @param principal The principal for which new ACE is being created.      * @param isAllow {@code true} if privileges are granted; {@code false} otherwise.      * @param privilegeNames The privilege names.      * @throws Exception If an error occurs.      */
specifier|protected
name|void
name|setupPermission
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nullable
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|privilegeNames
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
name|checkNotNull
argument_list|(
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|privilegeNames
argument_list|)
argument_list|,
name|isAllow
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
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

