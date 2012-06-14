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
name|performance
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
comment|/**  * Test for measuring the performance of {@value #ITERATIONS} iterations of  * transiently adding and removing a child node to a node that already has  * {@value #CHILD_COUNT} existing child nodes.  */
end_comment

begin_class
specifier|public
class|class
name|TransientManyChildNodesTest
extends|extends
name|AbstractTest
block|{
specifier|private
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
specifier|static
specifier|final
name|int
name|ITERATIONS
init|=
literal|1000
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|node
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
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testnode"
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
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
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|addNode
argument_list|(
literal|"onemore"
argument_list|,
literal|"nt:unstructured"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterTest
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
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
literal|"testnode"
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
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

