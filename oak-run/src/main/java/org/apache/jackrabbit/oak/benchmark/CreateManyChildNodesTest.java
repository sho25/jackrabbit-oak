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
comment|/**  * Test for measuring the performance of creating a node with  * {@value #CHILD_COUNT} child nodes.  */
end_comment

begin_class
specifier|public
class|class
name|CreateManyChildNodesTest
extends|extends
name|AbstractTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
literal|"test"
operator|+
name|TEST_ID
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|CHILD_COUNT
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
specifier|private
name|Session
name|session
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeTest
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CHILD_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

