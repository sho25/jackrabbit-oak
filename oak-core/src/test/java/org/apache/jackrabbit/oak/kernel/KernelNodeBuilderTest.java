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
name|kernel
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeStore
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
name|EmptyHook
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
name|PostCommitHook
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
name|NodeStore
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
name|NodeStoreBranch
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

begin_class
specifier|public
class|class
name|KernelNodeBuilderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|deletesKernelNodeStore
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|KernelNodeStore
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deletesMemoryNodeStore
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|init
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|init
parameter_list|(
name|NodeStore
name|store
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|run
parameter_list|(
name|NodeStore
name|store
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"child node x should be present"
argument_list|,
name|builder
operator|.
name|hasChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"child node x/y should be present"
argument_list|,
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"child node x/y/z should be present"
argument_list|,
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"child node x not should be present"
argument_list|,
name|builder
operator|.
name|hasChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"child node x/y not should be present"
argument_list|,
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
comment|// See OAK-531
name|assertFalse
argument_list|(
literal|"child node x/y/z not should not be present"
argument_list|,
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

