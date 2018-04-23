begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|document
package|;
end_package

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
name|junit
operator|.
name|Rule
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|TestUtils
operator|.
name|persistToBranch
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
name|DocumentNodeStoreBranchTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|branchedBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|persistToBranch
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|b1
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|persistToBranch
argument_list|(
name|b1
argument_list|)
expr_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|persistToBranch
argument_list|(
name|b2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// b1 must still see 'a' and 'b', but not 'c'
name|assertTrue
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// b2 must not be able to merge
try|try
block|{
name|merge
argument_list|(
name|ns
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Merge must fail with IllegalStateException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|/**      * Similar test as {@link #branchedBranch()} but without persistent branch.      */
annotation|@
name|Test
specifier|public
name|void
name|builderFromStateFromBuilder
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|b1
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|hasChildNode
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// b1 must still see 'a' and 'b', but not 'c'
name|assertTrue
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|hasChildNode
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// b2 must not be able to merge
try|try
block|{
name|merge
argument_list|(
name|ns
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Merge must fail with IllegalStateException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

