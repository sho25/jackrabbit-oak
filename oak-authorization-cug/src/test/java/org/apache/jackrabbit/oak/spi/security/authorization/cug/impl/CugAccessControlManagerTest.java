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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|List
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
name|RepositoryException
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
name|NamedAccessControlPolicy
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
name|ImmutableMap
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
name|namepath
operator|.
name|NamePathMapper
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
name|cug
operator|.
name|CugPolicy
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
name|oak
operator|.
name|spi
operator|.
name|xml
operator|.
name|ImportBehavior
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
name|oak
operator|.
name|util
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

begin_class
specifier|public
class|class
name|CugAccessControlManagerTest
extends|extends
name|AbstractCugTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SUPPORTED_PATH
init|=
literal|"/content"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UNSUPPORTED_PATH
init|=
literal|"/testNode"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INVALID_PATH
init|=
literal|"/path/to/non/existing/tree"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ConfigurationParameters
name|CUG_CONFIG
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
specifier|private
name|CugAccessControlManager
name|cugAccessControlManager
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
name|content
init|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|content
operator|.
name|addChild
argument_list|(
literal|"subtree"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"testNode"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cugAccessControlManager
operator|=
operator|new
name|CugAccessControlManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
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
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
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
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
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
name|CUG_CONFIG
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|CugPolicy
name|createCug
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|CugPolicyImpl
argument_list|(
name|path
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|,
name|getPrincipalManager
argument_list|(
name|root
argument_list|)
argument_list|,
name|ImportBehavior
operator|.
name|ABORT
argument_list|)
return|;
block|}
specifier|private
name|CugPolicy
name|getApplicableCug
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|(
name|CugPolicy
operator|)
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|path
argument_list|)
operator|.
name|nextAccessControlPolicy
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSupportedPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
index|[]
name|readPrivs
init|=
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
index|[]
argument_list|>
name|pathMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|readPrivs
argument_list|,
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
argument_list|,
name|readPrivs
argument_list|,
name|UNSUPPORTED_PATH
argument_list|,
operator|new
name|Privilege
index|[
literal|0
index|]
argument_list|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
operator|new
name|Privilege
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Privilege
index|[]
name|expected
init|=
name|pathMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|cugAccessControlManager
operator|.
name|getSupportedPrivileges
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetSupportedPrivilegesInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|getSupportedPrivileges
argument_list|(
name|INVALID_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetApplicablePolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlPolicyIterator
name|it
init|=
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|AccessControlPolicy
name|policy
init|=
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|policy
operator|instanceof
name|CugPolicyImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetApplicablePoliciesAfterSet
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|AccessControlPolicyIterator
name|it
init|=
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetApplicablePoliciesInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|INVALID_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetApplicablePoliciesUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlPolicyIterator
name|it
init|=
name|cugAccessControlManager
operator|.
name|getApplicablePolicies
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlPolicy
index|[]
name|policies
init|=
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPoliciesAfterSet
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
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
name|CugPolicyImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPoliciesInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|INVALID_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPoliciesUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlPolicy
index|[]
name|policies
init|=
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|CugPolicy
name|cug
init|=
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|cug
operator|.
name|addPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cug
argument_list|)
expr_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
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
name|AccessControlPolicy
name|policy
init|=
name|policies
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|policy
operator|instanceof
name|CugPolicyImpl
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|(
operator|(
name|CugPolicy
operator|)
name|policy
operator|)
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|principals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|principals
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPolicyPersisted
parameter_list|()
throws|throws
name|Exception
block|{
name|CugPolicy
name|cug
init|=
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|cug
operator|.
name|addPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cug
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|TreeUtil
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|cugTree
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cugTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CugConstants
operator|.
name|NT_REP_CUG_POLICY
argument_list|,
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|cugTree
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|prop
init|=
name|cugTree
operator|.
name|getProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prop
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|,
name|prop
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|prop
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetInvalidPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|invalidPolicies
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|AccessControlPolicy
argument_list|()
block|{}
argument_list|,
operator|new
name|NamedAccessControlPolicy
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|,
name|InvalidCug
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|invalidPolicies
control|)
block|{
try|try
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid cug policy must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetPolicyInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|INVALID_PATH
argument_list|,
name|createCug
argument_list|(
name|INVALID_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetPolicyUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|UNSUPPORTED_PATH
argument_list|,
name|createCug
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetPolicyPathMismatch
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|createCug
argument_list|(
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
argument_list|)
argument_list|)
expr_stmt|;
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
name|CugPolicy
name|cug
init|=
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cug
argument_list|)
expr_stmt|;
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|AccessControlPolicy
index|[
literal|0
index|]
argument_list|,
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemovePolicyPersisted
parameter_list|()
throws|throws
name|Exception
block|{
name|CugPolicy
name|cug
init|=
name|getApplicableCug
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|cugAccessControlManager
operator|.
name|setPolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cug
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|cugAccessControlManager
operator|.
name|getPolicies
argument_list|(
name|SUPPORTED_PATH
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveInvalidPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|invalidPolicies
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|AccessControlPolicy
argument_list|()
block|{}
argument_list|,
operator|new
name|NamedAccessControlPolicy
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|,
name|InvalidCug
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|invalidPolicies
control|)
block|{
try|try
block|{
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid cug policy must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PathNotFoundException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePolicyInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|INVALID_PATH
argument_list|,
name|createCug
argument_list|(
name|INVALID_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePolicyUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|UNSUPPORTED_PATH
argument_list|,
name|createCug
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePolicyPathMismatch
parameter_list|()
throws|throws
name|Exception
block|{
name|cugAccessControlManager
operator|.
name|removePolicy
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|createCug
argument_list|(
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|InvalidCug
implements|implements
name|CugPolicy
block|{
specifier|private
specifier|static
specifier|final
name|InvalidCug
name|INSTANCE
init|=
operator|new
name|InvalidCug
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removePrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

