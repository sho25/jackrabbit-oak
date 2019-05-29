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
name|user
package|;
end_package

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
name|AccessDeniedException
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
name|assertSame
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|UtilsTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Tree
name|tree
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
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
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
specifier|private
name|void
name|assertEqualPath
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|expected
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|result
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|getPath
argument_list|()
argument_list|,
name|result
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOrAddTreeCurrentElement
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|result
init|=
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
literal|"."
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|tree
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOrAddTreeParentElement
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|child
init|=
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
literal|"child"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|parent
init|=
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|child
argument_list|,
literal|".."
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertEqualPath
argument_list|(
name|tree
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetOrAddTreeParentElementFromRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
literal|".."
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOrAddTreeSingleElement
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|child
init|=
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
literal|"child"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertEqualPath
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/child"
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOrAddTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a/b/c"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"a/../b/c"
argument_list|,
literal|"/b/c"
argument_list|,
literal|"a/b/c/../.."
argument_list|,
literal|"/a"
argument_list|,
literal|"a/././././b/c"
argument_list|,
literal|"/a/b/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Tree
name|t
init|=
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
name|relPath
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertEqualPath
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|relPath
argument_list|)
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetOrAddTreeReachesParentOfRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|tree
argument_list|,
literal|"a/../../b"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetOrAddTreeTargetNotAccessible
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|nonExisting
init|=
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|nonExisting
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Tree
name|t
init|=
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nonExisting
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nonExisting
argument_list|)
expr_stmt|;
name|Utils
operator|.
name|getOrAddTree
argument_list|(
name|t
argument_list|,
literal|"a/a/b"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

