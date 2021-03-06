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
name|benchmark
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|Session
import|;
end_import

begin_comment
comment|/**  * Test for measuring the performance of creating a nodes (with a low fanout,  * that is, with few child nodes for each node).  */
end_comment

begin_class
specifier|public
class|class
name|CreateManyNodesTest
extends|extends
name|AbstractTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
literal|"many"
operator|+
name|TEST_ID
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FANOUT
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LEVELS
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SAVE_EVERY
init|=
literal|150
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|testRootNode
decl_stmt|;
specifier|private
name|int
name|nodeCount
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|testRootNode
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testRootNode
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|nodeCount
operator|=
literal|0
expr_stmt|;
name|nodeCount
operator|++
expr_stmt|;
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|addChildNodes
argument_list|(
name|node
argument_list|,
name|LEVELS
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addChildNodes
parameter_list|(
name|Node
name|parent
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FANOUT
condition|;
name|i
operator|++
control|)
block|{
name|nodeCount
operator|++
expr_stmt|;
name|Node
name|n
init|=
name|parent
operator|.
name|addNode
argument_list|(
literal|"l"
operator|+
name|level
operator|+
literal|"n"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeCount
operator|%
name|SAVE_EVERY
operator|==
literal|0
condition|)
block|{
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|addChildNodes
argument_list|(
name|n
argument_list|,
name|level
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

