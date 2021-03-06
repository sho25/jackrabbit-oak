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
name|advanced
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
name|GuestCredentials
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
name|ImmutableList
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
name|Iterables
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
name|commons
operator|.
name|PathUtils
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
name|models
operator|.
name|predefined
operator|.
name|Editor
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
name|models
operator|.
name|predefined
operator|.
name|PredefinedAuthorizationConfiguration
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
name|models
operator|.
name|predefined
operator|.
name|Reader
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
name|memory
operator|.
name|PropertyStates
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|authentication
operator|.
name|AuthenticationConfigurationImpl
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
name|authentication
operator|.
name|token
operator|.
name|TokenConfigurationImpl
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
name|internal
operator|.
name|SecurityProviderBuilder
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
name|PrincipalConfigurationImpl
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
name|PrivilegeConfigurationImpl
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
name|user
operator|.
name|UserConfigurationImpl
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
name|SecurityProvider
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
name|principal
operator|.
name|EveryonePrincipal
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
name|util
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Advanced Authorization Topics  * =============================================================================  *  * Title: Writing Custom Authorization : Permission Evaluation  * -----------------------------------------------------------------------------  *  * Goal:  * Write a custom {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.PermissionProvider}  * for a predefined requirement in order to become familiar with the details of  * the Oak permission evaluation.  *  * Exercises:  *  * Complete the implementation of {@link org.apache.jackrabbit.oak.exercise.security.authorization.models.predefined.PredefinedPermissionProvider}  * such that the tests pass.  *  * - {@link #testAdministrativeAccess}  *   Complete the {@link org.apache.jackrabbit.oak.exercise.security.authorization.models.predefined.PredefinedPermissionProvider}  *   such that at least the admin principal has full access everywhere.  *  *   Questions:  *   - How can you identify the 'administrator' from a given set of principals?  *   - Would it make sense to include other principals in that category? How would you identify them?  *   - Take another look at the built-in authorization models (default and oak-authorization-cug):  *     Can you describe what types of 'administrative' access they define? And how?  *  * - {@link #testGuestAccess()}  *   The {@link org.apache.jackrabbit.oak.exercise.security.authorization.models.predefined.PredefinedPermissionProvider}  *   assumes that the guest account doesn't have any permissions granted. Complete  *   the permission provider implementation accordingly.  *  *   Question:  *   Do you need to explicitly identify the guest account? If yes, how would you do that?  *  * - {@link #testWriteAccess()}  *   This tests asserts that 'editors' have basic read/write permissions. Complete  *   the permission provider implementation accordingly.  *  *   Questions:  *   - The test hard-codes the 'editor' principal. Can you come up with a setup scenario  *     where the Editor principal would be placed into the Subject upon login? What are the criteria?  *   - Can you make sure a given test-user won't be able to map itself to the 'editor' principal?  *  * - {@link #testReadAccess()}  *   This tests asserts that 'readers' exclusively have basic read access. Complete  *   the permission provider implementation accordingly.  *  *  * Advanced Exercises  * -----------------------------------------------------------------------------  *  * 1. Aggregation  *  * Currently the {@link org.apache.jackrabbit.oak.exercise.security.authorization.models.predefined.PredefinedPermissionProvider}  * doesn't implement {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.AggregatedPermissionProvider} interface  * and can therefore not be used in a setup that combines multiple authorization models.  *  * As an advanced exercise modify the {@link org.apache.jackrabbit.oak.exercise.security.authorization.models.predefined.PredefinedPermissionProvider}  * to additionally implement {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.AggregatedPermissionProvider}  * and deploy the {@link PredefinedAuthorizationConfiguration} in a setup with  * multiple authorization models.  *  * - Discuss the additional methods defined by {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.AggregatedPermissionProvider}.  * - Clarify which type of 'Authorization Composition' your implementation should be used.  * - Observe the result of your combination and explain the results to effective permissions.  *  *  * 2. Limit Access  *  * Currently the predefined {@code PermissionProvider} grants/denies the same permissions  * on the whole content repository. As an advanced exercise discuss how you would  * limit the permissions to certain parts of the content repository.  *  * For example: Imagine the first hierarchy level would define a trust-boundary  * based on continent. So, every 'Editor' only has write access to the continent  * he/she has been assigned to.  *  * Questions:  *  * - Should read-access be granted across continents?  * - Do you need a distinction between repository-administrators and continent-administrators?  * - How do you identify a given continent and map it to the access pattern of a given principal set?  * - Can you come up with your own PrincipalConfiguration serving a custom Principal implementation that help you with that task?  * - Are the items used for continent-identification properly protected to prevent unintended (or malicious) meddling?  * - At which point would you additionally need access control management?  *  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|L5_CustomPermissionEvaluationTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|ACTION_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_READ
block|,
name|Session
operator|.
name|ACTION_ADD_NODE
block|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
block|,
name|Session
operator|.
name|ACTION_REMOVE
block|}
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Tree
argument_list|>
name|trees
decl_stmt|;
specifier|private
name|PropertyState
name|prop
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|SecurityProvider
name|initSecurityProvider
parameter_list|()
block|{
name|AuthorizationConfiguration
name|ac
init|=
operator|new
name|PredefinedAuthorizationConfiguration
argument_list|()
decl_stmt|;
return|return
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|AuthenticationConfigurationImpl
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
operator|new
name|PrivilegeConfigurationImpl
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
operator|new
name|UserConfigurationImpl
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|ac
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
operator|new
name|PrincipalConfigurationImpl
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
operator|new
name|TokenConfigurationImpl
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|with
argument_list|(
name|getSecurityConfigParameters
argument_list|()
argument_list|)
operator|.
name|withRootProvider
argument_list|(
name|getRootProvider
argument_list|()
argument_list|)
operator|.
name|withTreeProvider
argument_list|(
name|getTreeProvider
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
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
name|prop
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|Tree
name|testTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"contentA"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|aTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|testTree
argument_list|,
literal|"a"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|aTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|Tree
name|aaTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|aTree
argument_list|,
literal|"a"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|aaTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|Tree
name|bTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"contentB"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|bTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|Tree
name|bbTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|bTree
argument_list|,
literal|"b"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|bbTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|Tree
name|cTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"contentC"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|cTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|Tree
name|ccTree
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|cTree
argument_list|,
literal|"c"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|ccTree
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|trees
operator|=
name|ImmutableList
operator|.
expr|<
name|Tree
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|testTree
argument_list|)
operator|.
name|add
argument_list|(
name|aTree
argument_list|)
operator|.
name|add
argument_list|(
name|aaTree
argument_list|)
operator|.
name|add
argument_list|(
name|bTree
argument_list|)
operator|.
name|add
argument_list|(
name|bbTree
argument_list|)
operator|.
name|add
argument_list|(
name|cTree
argument_list|)
operator|.
name|add
argument_list|(
name|ccTree
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|private
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
name|getConfig
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
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|principals
argument_list|)
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|getTreePaths
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|trees
argument_list|,
name|Tree
operator|::
name|getPath
argument_list|)
return|;
block|}
specifier|private
name|Set
argument_list|<
name|Principal
argument_list|>
name|getGuestPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|guest
init|=
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
init|)
block|{
return|return
name|guest
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdministrativeAccess
parameter_list|()
block|{
for|for
control|(
name|String
name|path
range|:
name|getTreePaths
argument_list|()
control|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|adminSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|trees
control|)
block|{
name|pp
operator|.
name|getPrivileges
argument_list|(
name|t
argument_list|)
operator|.
name|contains
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|treePath
init|=
name|t
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|allActions
init|=
name|Text
operator|.
name|implode
argument_list|(
name|ACTION_NAMES
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|treePath
argument_list|,
name|allActions
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|treePath
argument_list|,
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|allActions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGuestAccess
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ContentSession
name|guest
init|=
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
init|)
block|{
name|Root
name|r
init|=
name|guest
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|getTreePaths
argument_list|()
control|)
block|{
name|Tree
name|t
init|=
name|r
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|guest
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|trees
control|)
block|{
name|pp
operator|.
name|getPrivileges
argument_list|(
name|t
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|permission
range|:
name|Permissions
operator|.
name|aggregates
argument_list|(
name|Permissions
operator|.
name|ALL
argument_list|)
control|)
block|{
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|permission
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|permission
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|action
range|:
name|ACTION_NAMES
control|)
block|{
name|String
name|treePath
init|=
name|t
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|treePath
argument_list|,
name|action
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|treePath
argument_list|,
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|editors
init|=
name|ImmutableList
operator|.
expr|<
name|Set
argument_list|<
name|Principal
argument_list|>
operator|>
name|of
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
operator|new
name|Editor
argument_list|(
literal|"ida"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|Editor
argument_list|(
literal|"amanda"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|Editor
argument_list|(
literal|"susi"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|getGuestPrincipals
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|Editor
argument_list|(
literal|"naima"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
range|:
name|editors
control|)
block|{
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|principals
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|trees
control|)
block|{
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_WRITE
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_USER_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|WRITE
operator||
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|WRITE
operator||
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|USER_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|MODIFY_ACCESS_CONTROL
operator||
name|Permissions
operator|.
name|USER_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|action
range|:
name|ACTION_NAMES
control|)
block|{
name|String
name|treePath
init|=
name|t
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|treePath
argument_list|,
name|action
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|treePath
argument_list|,
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|deniedActions
init|=
name|Text
operator|.
name|implode
argument_list|(
operator|new
name|String
index|[]
block|{
name|JackrabbitSession
operator|.
name|ACTION_MODIFY_ACCESS_CONTROL
block|,
name|JackrabbitSession
operator|.
name|ACTION_READ_ACCESS_CONTROL
block|,
name|JackrabbitSession
operator|.
name|ACTION_USER_MANAGEMENT
block|}
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|deniedActions
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
name|testReadAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Set
argument_list|<
name|Principal
argument_list|>
argument_list|>
name|readers
init|=
name|ImmutableList
operator|.
expr|<
name|Set
argument_list|<
name|Principal
argument_list|>
operator|>
name|of
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
operator|new
name|Reader
argument_list|(
literal|"ida"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|Reader
argument_list|(
literal|"fairuz"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|of
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|Editor
argument_list|(
literal|"juni"
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Principal
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|getGuestPrincipals
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|Editor
argument_list|(
literal|"ale"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|privilegeManager
init|=
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Privilege
name|all
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|readPrivNames
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
decl_stmt|;
for|for
control|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
range|:
name|readers
control|)
block|{
name|PermissionProvider
name|pp
init|=
name|getPermissionProvider
argument_list|(
name|principals
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|trees
control|)
block|{
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|readPrivNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|readPrivNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Privilege
name|p
range|:
name|all
operator|.
name|getAggregatePrivileges
argument_list|()
control|)
block|{
name|String
name|pName
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|readPrivNames
operator|.
name|contains
argument_list|(
name|pName
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|pName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|pName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|READ_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|WRITE
operator||
name|Permissions
operator|.
name|VERSION_MANAGEMENT
operator||
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
name|prop
argument_list|,
name|Permissions
operator|.
name|SET_PROPERTY
operator||
name|Permissions
operator|.
name|VERSION_MANAGEMENT
operator||
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|treePath
init|=
name|t
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|treePath
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|treePath
argument_list|,
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|deniedActions
init|=
name|Text
operator|.
name|implode
argument_list|(
operator|new
name|String
index|[]
block|{
name|Session
operator|.
name|ACTION_ADD_NODE
block|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
block|,
name|Session
operator|.
name|ACTION_REMOVE
block|,
name|JackrabbitSession
operator|.
name|ACTION_READ_ACCESS_CONTROL
block|}
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|deniedActions
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
literal|"/path/to/nonexisting/item"
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pp
operator|.
name|isGranted
argument_list|(
literal|"/path/to/nonexisting/item"
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

