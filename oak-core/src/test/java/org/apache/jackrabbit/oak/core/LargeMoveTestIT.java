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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|NodeStoreFixture
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

begin_comment
comment|/**  * Test case for asserting large moves don't run out of memory.  * See OAK-463, OAK-464  */
end_comment

begin_class
specifier|public
class|class
name|LargeMoveTestIT
extends|extends
name|OakBaseTest
block|{
specifier|private
name|ContentSession
name|session
decl_stmt|;
specifier|private
name|String
name|treeAPath
decl_stmt|;
specifier|private
name|String
name|treeBPath
decl_stmt|;
specifier|public
name|LargeMoveTestIT
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|CommitFailedException
block|{
comment|// FIXME slow on MongoMK. See OAK-964
name|assumeTrue
argument_list|(
name|fixture
operator|!=
name|NodeStoreFixture
operator|.
name|MONGO_MK
argument_list|)
expr_stmt|;
name|session
operator|=
name|createContentSession
argument_list|()
expr_stmt|;
comment|// Add test content
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
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
name|treeA
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"tree-a"
argument_list|)
decl_stmt|;
name|this
operator|.
name|treeAPath
operator|=
name|treeA
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|Tree
name|treeB
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"tree-b"
argument_list|)
decl_stmt|;
name|this
operator|.
name|treeBPath
operator|=
name|treeB
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|createNodes
argument_list|(
name|treeA
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// 111111 nodes in treeA
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|createNodes
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|count
condition|;
name|c
operator|++
control|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"n-"
operator|+
name|depth
operator|+
literal|'-'
operator|+
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|depth
operator|>
literal|1
condition|)
block|{
name|createNodes
argument_list|(
name|child
argument_list|,
name|count
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|moveTest
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Root
name|root1
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
comment|// Concurrent changes to trunk: enforce rebase
name|Root
name|root2
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"any"
argument_list|)
expr_stmt|;
name|root2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|root1
operator|.
name|move
argument_list|(
name|treeAPath
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|treeBPath
argument_list|,
literal|"tree-a-moved"
argument_list|)
argument_list|)
expr_stmt|;
name|root1
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

