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
name|PropertyState
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
name|plugins
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authorization (Permission Evaluation)  * =============================================================================  *  * Title: Introduction  * -----------------------------------------------------------------------------  *  * Become familiar with the way to verify permissions using JCR API.  * Get a basic understanding how permission evaluation is used and exposed in Oak  * and finally gain insight into some details of the default implementation.  *  * Exercises:  *  * - Overview and Usages of Permission Evaluation  *   Search and list for permission related methods in the JCR API and recap what  *   the specification states about permissions compared to access control  *   management.  *  *   Question: What are the areas in JCR that deal with permissions?  *   Question: Who is the expected API consumer?  *   Question: Can you elaborate when it actually makes sense to use this API?  *   Question: Can you think about potential drawbacks of doing so?  *  * - Permission Evaluation in Oak  *   In a second step try to become more familiar with the nature of the  *   permission evaluation in Oak.  *  *   Question: What is the nature of the public SPI?  *   Question: Can you identify the main entry point for permission evaluation?  *   Question: Can you identify to exact location(s) in Oak where read-access is being enforced?  *   Question: Can you identify the exact location(s) in Oak where all kind of write access is being enforced?  *  * - Configuration  *   Look at the default implementation(s) of the {@link org.apache.jackrabbit.oak.spi.security.authorization.AuthorizationConfiguration}  *   and try to identify the configurable parts with respect to permission evaluation.  *   Compare your results with the Oak documentation.  *  *   Question: Can you provide a list of configuration options for the permission evaluation?  *   Question: Can you identify where these configuration options are being evaluated?  *   Question: Which options also affect the access control management?  *  * - Pluggability  *   Become familar with the pluggable parts of the permission evaluation  *  *   Question: What means does Oak provide to change or extend the permission evaluation?  *   Question: Can you identify the interfaces that you needed to implement?  *   Question: Would it be possible to only replace the implementation of {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider}?  *             How could you achieve this?  *             And what would be the consequences for the whole authorization module?  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * - Read Permission Walkthrough  *   Use {@link #testReadPermissionWalkThrough()} to become familiar with the  *   very internals of the permission evaluation. Walk through the item read  *   methods (for simplicity reduced to oak-core only) and be aware of  *   permission-evaluation related parts.  *  *   Question: Can you list the relevant steps wrt permission evalution?  *   Question: What is the nature of the returned tree objects?  *   Question: How can you verify if the editing test session can actually read those trees without using the permission-evaluation code?  *   Question: How can you verify if the properties are accessible.  *  * - Write Permission Walkthrough  *   Use {@link #testWritePermissionWalkThrough()} to become familiar with the  *   internals of the permission evaluation with respect to writing. Walk through  *   the item write methods (for simplicity reduced to oak-core only) and the  *   subsequent {@link org.apache.jackrabbit.oak.api.Root#commit()} and be aware  *   of permission-evaluation related parts.  *  *   Question: Can you list the relevant steps wrt permission evalution?  *   Question: What can you say about write permissions for special (protected) items?  *  * - Extending {@link #testReadPermissionWalkThrough()} and {@link #testWritePermissionWalkThrough()}  *   Use the two test-cases and play with additional access/writes and or  *   additional (more complex) permission setup.  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L2_PermissionDiscoveryTest}  *  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|L1_IntroductionTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|ContentSession
name|testSession
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
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|Principal
name|testPrincipal
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
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
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|setupPermission
argument_list|(
name|root
argument_list|,
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|root
argument_list|,
literal|"/a/b"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|root
argument_list|,
literal|"/a/bb"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|root
argument_list|,
literal|"/a/b/c"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_ADD_PROPERTIES
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
comment|/**      * Setup simple allow/deny permissions (without restrictions).      *      * @param root The editing root.      * @param path The path of the access controlled tree.      * @param principal The principal for which new ACE is being created.      * @param isAllow {@code true} if privileges are granted; {@code false} otherwise.      * @param privilegeNames The privilege names.      * @throws Exception If an error occurs.      */
specifier|private
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
annotation|@
name|Test
specifier|public
name|void
name|testReadPermissionWalkThrough
parameter_list|()
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
comment|// EXERCISE verify if these tree are accessible using Tree#exists()
comment|// Question: can you explain why using Tree.exists is sufficient and you don't necessarily need to perform the check on the PermissionProvider?
name|Tree
name|rootTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|bTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/b"
argument_list|)
decl_stmt|;
name|Tree
name|cTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/b/c"
argument_list|)
decl_stmt|;
comment|// EXERCISE verify if this is an accessible property? Q: how can you do this withouth testing the readability on the PermissionProvider?
name|PropertyState
name|bProp
init|=
name|bTree
operator|.
name|getProperty
argument_list|(
literal|"bProp"
argument_list|)
decl_stmt|;
name|PropertyState
name|bbProp
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/bb"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"bbProp"
argument_list|)
decl_stmt|;
name|PropertyState
name|cProp
init|=
name|cTree
operator|.
name|getProperty
argument_list|(
literal|"cProp"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWritePermissionWalkThrough
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
comment|// EXERCISE walk through the test and fix it such that it passes.
name|Tree
name|bTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/b"
argument_list|)
decl_stmt|;
comment|// add a new child node at '/a/b' and persist the change to trigger the permission evaluation.
comment|// EXERCISE: does it work with the current permission setup? if not, why (+ add exception handling)?
try|try
block|{
name|Tree
name|child
init|=
name|bTree
operator|.
name|addChild
argument_list|(
literal|"childName"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|// now change the primary type of the 'bTree'
comment|// EXERCISE: does it work with the current permission setup? if not, why (+ add exception handling)?
try|try
block|{
name|bTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
name|Tree
name|cTree
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a/b/c"
argument_list|)
decl_stmt|;
comment|// now change the regula property 'cProp' of the 'cTree'
comment|// EXERCISE: does it work with the current permission setup? if not, why (+ add exception handling)?
try|try
block|{
name|cTree
operator|.
name|setProperty
argument_list|(
literal|"cProp"
argument_list|,
literal|"changedValue"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|// finally we try to add a new property to the 'cTree'
comment|// EXERCISE: does it work with the current permission setup? if not, why (+ add exception handling)?
try|try
block|{
name|cTree
operator|.
name|setProperty
argument_list|(
literal|"anotherCProp"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
