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
name|nodetype
operator|.
name|write
operator|.
name|ReadWriteNodeTypeManager
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|Validator
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
name|state
operator|.
name|NodeState
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
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinitionTemplate
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
name|NodeTypeTemplate
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_class
specifier|public
class|class
name|CugValidatorTest
extends|extends
name|AbstractCugTest
block|{
specifier|private
name|Tree
name|node
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
name|node
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePrimaryType
parameter_list|()
block|{
name|node
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyChangedBeforeWasCug
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|ns
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
operator|new
name|CugValidatorProvider
argument_list|()
operator|.
name|getRootValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
operator|new
name|CommitInfo
argument_list|(
literal|"sid"
argument_list|,
literal|"uid"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|PropertyState
name|before
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|PropertyState
name|after
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|validator
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyChangedAfterIsCug
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|ns
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
operator|new
name|CugValidatorProvider
argument_list|()
operator|.
name|getRootValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
operator|new
name|CommitInfo
argument_list|(
literal|"sid"
argument_list|,
literal|"uid"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|PropertyState
name|before
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|PropertyState
name|after
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|validator
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyChangedNoCugInvolved
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|ns
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Validator
name|validator
init|=
operator|new
name|CugValidatorProvider
argument_list|()
operator|.
name|getRootValidator
argument_list|(
name|ns
argument_list|,
name|ns
argument_list|,
operator|new
name|CommitInfo
argument_list|(
literal|"sid"
argument_list|,
literal|"uid"
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyState
name|before
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|PropertyState
name|after
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|validator
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangePrimaryTypeOfCug
parameter_list|()
throws|throws
name|Exception
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|MIX_REP_CUG_MIXIN
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|cug
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
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|21
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidPrimaryType
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|21
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testMissingMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|MIX_REP_CUG_MIXIN
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|node
operator|.
name|removeProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testCugPolicyWithDifferentName
parameter_list|()
throws|throws
name|Exception
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|MIX_REP_CUG_MIXIN
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
literal|"anotherName"
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|23
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testNodeTypeWithCugNames
parameter_list|()
throws|throws
name|Exception
block|{
name|ReadWriteNodeTypeManager
name|ntMgr
init|=
operator|new
name|ReadWriteNodeTypeManager
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|NodeTypeTemplate
name|ntTemplate
init|=
name|ntMgr
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|ntTemplate
operator|.
name|setName
argument_list|(
literal|"testNT"
argument_list|)
expr_stmt|;
name|NodeDefinitionTemplate
name|ndt
init|=
name|ntMgr
operator|.
name|createNodeDefinitionTemplate
argument_list|()
decl_stmt|;
name|ndt
operator|.
name|setName
argument_list|(
name|REP_CUG_POLICY
argument_list|)
expr_stmt|;
name|ndt
operator|.
name|setRequiredPrimaryTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|NT_BASE
block|}
argument_list|)
expr_stmt|;
name|ntTemplate
operator|.
name|getNodeDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|ndt
argument_list|)
expr_stmt|;
name|ntMgr
operator|.
name|registerNodeType
argument_list|(
name|ntTemplate
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrNodeTypesOutsideOfSystemIsValidated
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|n
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|node
argument_list|,
name|JCR_NODE_TYPES
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|cug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|n
argument_list|,
name|REP_CUG_POLICY
argument_list|,
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|cug
operator|.
name|setProperty
argument_list|(
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
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
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
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

