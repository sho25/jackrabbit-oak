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
name|util
operator|.
name|List
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
name|plugins
operator|.
name|tree
operator|.
name|TreeLocation
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

begin_class
specifier|public
class|class
name|CugContextTest
extends|extends
name|AbstractCugTest
implements|implements
name|NodeTypeConstants
block|{
specifier|private
specifier|static
name|String
name|CUG_PATH
init|=
literal|"/content/a/rep:cugPolicy"
decl_stmt|;
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|NO_CUG_PATH
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/content"
argument_list|,
literal|"/content/a"
argument_list|,
literal|"/content/rep:policy"
argument_list|,
literal|"/content/rep:cugPolicy"
argument_list|,
literal|"/content/a/rep:cugPolicy/rep:principalNames"
argument_list|,
name|UNSUPPORTED_PATH
operator|+
literal|"/rep:cugPolicy"
argument_list|)
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
comment|// add more child nodes
name|Tree
name|n
init|=
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
decl_stmt|;
name|createTrees
argument_list|(
name|n
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|createTrees
argument_list|(
name|n
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"aa"
argument_list|,
literal|"bb"
argument_list|,
literal|"cc"
argument_list|)
expr_stmt|;
comment|// create cugs
name|createCug
argument_list|(
literal|"/content/a"
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
comment|// setup regular acl at /content
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|AccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/content"
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
literal|"/content"
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
name|refresh
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
name|testDefinesContextRoot
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesContextRoot
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|CUG_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|NO_CUG_PATH
control|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesContextRoot
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesTree
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesTree
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|CUG_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|NO_CUG_PATH
control|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesTree
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesProperty
parameter_list|()
block|{
name|Tree
name|cugTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|CUG_PATH
argument_list|)
decl_stmt|;
name|PropertyState
name|repPrincipalNames
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
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesProperty
argument_list|(
name|cugTree
argument_list|,
name|repPrincipalNames
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesProperty
argument_list|(
name|cugTree
argument_list|,
name|cugTree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|NO_CUG_PATH
control|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesProperty
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
name|repPrincipalNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesLocation
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|CUG_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|CUG_PATH
operator|+
literal|"/"
operator|+
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|existingNoCug
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/content"
argument_list|,
literal|"/content/a"
argument_list|,
literal|"/content/rep:policy"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|existingNoCug
control|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|nonExistingCug
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/content/rep:cugPolicy"
argument_list|,
name|UNSUPPORTED_PATH
operator|+
literal|"/rep:cugPolicy"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|nonExistingCug
control|)
block|{
name|assertTrue
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
argument_list|,
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidCug
parameter_list|()
throws|throws
name|Exception
block|{
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
decl_stmt|;
comment|// cug at unsupported path -> context doesn't take supported paths into account.
name|Tree
name|invalidCug
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
argument_list|,
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|,
name|CugConstants
operator|.
name|NT_REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|invalidCug
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesContextRoot
argument_list|(
name|invalidCug
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesTree
argument_list|(
name|invalidCug
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesProperty
argument_list|(
name|invalidCug
argument_list|,
name|invalidCug
operator|.
name|getProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'cug' with wrong node type -> detected as no-cug by context
name|invalidCug
operator|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|UNSUPPORTED_PATH
argument_list|)
argument_list|,
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|invalidCug
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesContextRoot
argument_list|(
name|invalidCug
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesTree
argument_list|(
name|invalidCug
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugContext
operator|.
name|INSTANCE
operator|.
name|definesProperty
argument_list|(
name|invalidCug
argument_list|,
name|invalidCug
operator|.
name|getProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

