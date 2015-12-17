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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|AbstractTree
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
name|NodeBuilder
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
name|assertNull
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
name|assertSame
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
name|CugUtilTest
extends|extends
name|AbstractCugTest
block|{
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
name|createCug
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
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
name|Nonnull
specifier|private
specifier|static
name|NodeState
name|getNodeState
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
operator|(
operator|(
name|AbstractTree
operator|)
name|tree
operator|)
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasCug
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|PathUtils
operator|.
name|ROOT_PATH
block|,
name|INVALID_PATH
block|,
name|UNSUPPORTED_PATH
block|,
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
block|,
name|SUPPORTED_PATH2
block|,
name|SUPPORTED_PATH3
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
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
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasCugNodeState
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
operator|(
name|NodeState
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|PathUtils
operator|.
name|ROOT_PATH
block|,
name|INVALID_PATH
block|,
name|UNSUPPORTED_PATH
block|,
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
block|,
name|SUPPORTED_PATH2
block|,
name|SUPPORTED_PATH3
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasCugNodeBuilder
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
operator|.
name|builder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
operator|(
name|NodeBuilder
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|PathUtils
operator|.
name|ROOT_PATH
block|,
name|INVALID_PATH
block|,
name|UNSUPPORTED_PATH
block|,
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
block|,
name|SUPPORTED_PATH2
block|,
name|SUPPORTED_PATH3
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|builder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|getNodeState
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|builder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCug
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|CugUtil
operator|.
name|getCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
operator|new
name|String
index|[]
block|{
name|PathUtils
operator|.
name|ROOT_PATH
block|,
name|INVALID_PATH
block|,
name|UNSUPPORTED_PATH
block|,
name|SUPPORTED_PATH
operator|+
literal|"/subtree"
block|,
name|SUPPORTED_PATH2
block|,
name|SUPPORTED_PATH3
block|}
control|)
block|{
name|assertNull
argument_list|(
name|CugUtil
operator|.
name|getCug
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
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|CugUtil
operator|.
name|getCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesCug
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|INVALID_PATH
argument_list|,
name|REP_CUG_POLICY
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|REP_CUG_POLICY
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|invalid
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|SUPPORTED_PATH2
argument_list|)
argument_list|)
operator|.
name|addChild
argument_list|(
name|REP_CUG_POLICY
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|definesCug
argument_list|(
name|invalid
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSupportedPath
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
literal|null
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|UNSUPPORTED_PATH
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|SUPPORTED_PATH
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|SUPPORTED_PATH2
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|SUPPORTED_PATH
operator|+
literal|"/child"
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CugUtil
operator|.
name|isSupportedPath
argument_list|(
name|SUPPORTED_PATH2
operator|+
literal|"/child"
argument_list|,
name|CUG_CONFIG
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetImportBehavior
parameter_list|()
block|{
name|assertSame
argument_list|(
name|ImportBehavior
operator|.
name|ABORT
argument_list|,
name|CugUtil
operator|.
name|getImportBehavior
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

