begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|core
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
name|OakBaseTest
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ImmutableTreeTest
extends|extends
name|OakBaseTest
block|{
specifier|private
name|Root
name|root
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|ContentSession
name|session
init|=
name|createContentSession
argument_list|()
decl_stmt|;
comment|// Add test content
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|x
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|x
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|Tree
name|z
init|=
name|y
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Acquire a fresh new root to avoid problems from lingering state
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|root
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPath
parameter_list|()
block|{
name|TreeImpl
name|tree
init|=
operator|(
name|TreeImpl
operator|)
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|ImmutableTree
name|immutable
init|=
operator|new
name|ImmutableTree
argument_list|(
name|tree
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x/y"
argument_list|,
name|immutable
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|immutable
operator|=
name|immutable
operator|.
name|getChild
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x/y/z"
argument_list|,
name|immutable
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
name|testGetNodeState
parameter_list|()
block|{
name|ImmutableTree
name|tree
init|=
name|ImmutableTree
operator|.
name|createFromRoot
argument_list|(
name|root
argument_list|,
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tree
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|ImmutableTree
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|ImmutableTree
operator|)
name|child
operator|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRoot
parameter_list|()
block|{
name|ImmutableTree
name|tree
init|=
name|ImmutableTree
operator|.
name|createFromRoot
argument_list|(
name|root
argument_list|,
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|isRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|TYPE_DEFAULT
argument_list|,
name|tree
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParent
parameter_list|()
block|{
name|ImmutableTree
name|tree
init|=
name|ImmutableTree
operator|.
name|createFromRoot
argument_list|(
name|root
argument_list|,
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|ImmutableTree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|child
operator|.
name|getParent
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|ImmutableTree
name|disconnected
init|=
operator|new
name|ImmutableTree
argument_list|(
name|ImmutableTree
operator|.
name|ParentProvider
operator|.
name|UNSUPPORTED
argument_list|,
name|child
operator|.
name|getName
argument_list|()
argument_list|,
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|ImmutableTree
operator|.
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
try|try
block|{
name|disconnected
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

end_unit

