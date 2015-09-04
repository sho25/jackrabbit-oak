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
name|restriction
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
name|HashMap
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
name|api
operator|.
name|Type
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
name|ConfigurationParameters
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
name|AuthorizationConfiguration
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|PermissionProvider
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
name|authorization
operator|.
name|restriction
operator|.
name|AbstractRestrictionProvider
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
name|restriction
operator|.
name|CompositeRestrictionProvider
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
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionDefinition
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
name|restriction
operator|.
name|RestrictionDefinitionImpl
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
name|restriction
operator|.
name|RestrictionPattern
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
name|restriction
operator|.
name|RestrictionProvider
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
name|apache
operator|.
name|jackrabbit
operator|.
name|value
operator|.
name|StringValue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test suite for a custom restriction provider. The restriction is enabled based on the (non) existence of a property.  * The test creates nodes along '/testRoot/a/b/c/d/e' and sets the 'protect-me' property on '/testRoot/a/b/c'.  */
end_comment

begin_class
specifier|public
class|class
name|CustomRestrictionProviderTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_PATH
init|=
literal|"/testRoot"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_A_PATH
init|=
literal|"/testRoot/a"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_B_PATH
init|=
literal|"/testRoot/a/b"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_C_PATH
init|=
literal|"/testRoot/a/b/c"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_D_PATH
init|=
literal|"/testRoot/a/b/c/d"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_E_PATH
init|=
literal|"/testRoot/a/b/c/d/e"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_NAME_PROTECT_ME
init|=
literal|"protect-me"
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|RestrictionProvider
name|rProvider
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
operator|new
name|PropertyRestrictionProvider
argument_list|()
argument_list|,
operator|new
name|RestrictionProviderImpl
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RestrictionProvider
argument_list|>
name|authorizMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|PARAM_RESTRICTION_PROVIDER
argument_list|,
name|rProvider
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|authorizMap
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
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
name|addChild
argument_list|(
literal|"testRoot"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeUtil
name|a
init|=
name|testRootNode
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
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
name|setBoolean
argument_list|(
name|PROP_NAME_PROTECT_ME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NodeUtil
name|d
init|=
name|c
operator|.
name|addChild
argument_list|(
literal|"d"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|d
operator|.
name|addChild
argument_list|(
literal|"e"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
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
comment|// revert uncommitted changes
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// remove all test content
name|root
operator|.
name|getTree
argument_list|(
name|TEST_ROOT_PATH
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
specifier|private
name|void
name|addEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|grant
parameter_list|,
name|String
name|restriction
parameter_list|,
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
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|restriction
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|rs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
name|rs
operator|.
name|put
argument_list|(
name|PropertyRestrictionProvider
operator|.
name|RESTRICTION_NAME
argument_list|,
operator|new
name|StringValue
argument_list|(
name|restriction
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
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
name|grant
argument_list|,
name|rs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
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
name|grant
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|void
name|assertIsGranted
parameter_list|(
name|PermissionProvider
name|pp
parameter_list|,
name|Root
name|root
parameter_list|,
name|boolean
name|allow
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"user should "
operator|+
operator|(
name|allow
condition|?
literal|""
else|:
literal|"not "
operator|)
operator|+
literal|"have "
operator|+
name|permissions
operator|+
literal|" on "
operator|+
name|path
argument_list|,
name|allow
argument_list|,
name|pp
operator|.
name|isGranted
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
literal|null
argument_list|,
name|permissions
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
name|ContentSession
name|session
parameter_list|)
block|{
return|return
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|session
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|session
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Tests the custom restriction provider that checks on the existence of a property.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testProtectByRestriction
parameter_list|()
throws|throws
name|Exception
block|{
comment|// allow rep:write      /testroot
comment|// deny  jcr:removeNode /testroot/a  hasProperty=protect-me
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|false
argument_list|,
name|PROP_NAME_PROTECT_ME
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_A_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_B_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|false
argument_list|,
name|TEST_C_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_D_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_E_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
comment|// should be able to remove /a/b/c/d
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_D_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|testRoot
operator|.
name|getTree
argument_list|(
name|TEST_C_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should not be able to delete "
operator|+
name|TEST_C_PATH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// all ok
block|}
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Tests the custom restriction provider that checks on the existence of a property.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testProtectPropertiesByRestriction
parameter_list|()
throws|throws
name|Exception
block|{
comment|// allow rep:write            /testroot
comment|// deny  jcr:modifyProperties /testroot/a  hasProperty=protect-me
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|false
argument_list|,
name|PROP_NAME_PROTECT_ME
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_A_PATH
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_B_PATH
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|false
argument_list|,
name|TEST_C_PATH
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_D_PATH
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_E_PATH
argument_list|,
name|Permissions
operator|.
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Tests the custom restriction provider that checks on the absence of a property.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testUnProtectByRestriction
parameter_list|()
throws|throws
name|Exception
block|{
comment|// allow rep:write      /testroot
comment|// deny  jcr:removeNode /testroot
comment|// allow jcr:removeNode /testroot/a  hasProperty=!protect-me
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_ROOT_PATH
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|addEntry
argument_list|(
name|TEST_A_PATH
argument_list|,
literal|true
argument_list|,
literal|"!"
operator|+
name|PROP_NAME_PROTECT_ME
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_A_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_B_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|false
argument_list|,
name|TEST_C_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_D_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|assertIsGranted
argument_list|(
name|pp
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
name|TEST_E_PATH
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Customs restriction provider that matches restrictions based on the existence of a property.      */
specifier|public
specifier|static
class|class
name|PropertyRestrictionProvider
extends|extends
name|AbstractRestrictionProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|RESTRICTION_NAME
init|=
literal|"hasProperty"
decl_stmt|;
specifier|public
name|PropertyRestrictionProvider
parameter_list|()
block|{
name|super
argument_list|(
name|supportedRestrictions
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|RestrictionDefinition
argument_list|>
name|supportedRestrictions
parameter_list|()
block|{
name|RestrictionDefinition
name|dates
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|RESTRICTION_NAME
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|dates
operator|.
name|getName
argument_list|()
argument_list|,
name|dates
argument_list|)
return|;
block|}
comment|//------------------------------------------------< RestrictionProvider>---
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|RESTRICTION_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
return|return
name|HasPropertyPattern
operator|.
name|create
argument_list|(
name|property
argument_list|)
return|;
block|}
block|}
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Restriction
name|r
range|:
name|restrictions
control|)
block|{
name|String
name|name
init|=
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|RESTRICTION_NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|HasPropertyPattern
operator|.
name|create
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|HasPropertyPattern
implements|implements
name|RestrictionPattern
block|{
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|negate
decl_stmt|;
specifier|private
name|HasPropertyPattern
parameter_list|(
annotation|@
name|Nonnull
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
name|propertyName
operator|.
name|startsWith
argument_list|(
literal|"!"
argument_list|)
condition|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|negate
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|negate
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|static
name|RestrictionPattern
name|create
parameter_list|(
name|PropertyState
name|stringProperty
parameter_list|)
block|{
if|if
condition|(
name|stringProperty
operator|.
name|count
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|HasPropertyPattern
argument_list|(
name|stringProperty
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
name|boolean
name|match
init|=
literal|false
decl_stmt|;
comment|// configured property name found on underlying jcr:content node has precedence
if|if
condition|(
name|tree
operator|.
name|hasChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|)
condition|)
block|{
name|match
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|)
operator|.
name|hasProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|match
condition|)
block|{
name|match
operator|=
name|tree
operator|.
name|hasProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
return|return
name|negate
condition|?
operator|!
name|match
else|:
name|match
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
return|return
name|matches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"HasPropertyPattern{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"propertyName='"
argument_list|)
operator|.
name|append
argument_list|(
name|propertyName
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", negate="
argument_list|)
operator|.
name|append
argument_list|(
name|negate
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

