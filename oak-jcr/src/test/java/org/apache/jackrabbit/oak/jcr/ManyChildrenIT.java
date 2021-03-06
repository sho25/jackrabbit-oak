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
name|jcr
package|;
end_package

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
name|NodeIterator
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
name|fixture
operator|.
name|NodeStoreFixture
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
comment|/**  * Test nodes with many child nodes.  */
end_comment

begin_class
specifier|public
class|class
name|ManyChildrenIT
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|ManyChildrenIT
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
name|Test
specifier|public
name|void
name|sizeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|5000
decl_stmt|;
name|String
name|nodeType
init|=
literal|"nt:folder"
decl_stmt|;
name|Node
name|many
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"many"
argument_list|,
name|nodeType
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|many
operator|.
name|addNode
argument_list|(
literal|"test"
operator|+
name|i
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|many
operator|=
name|session2
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"many"
argument_list|)
expr_stmt|;
name|NodeIterator
name|it
init|=
name|many
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|it
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"size: "
operator|+
name|size
argument_list|,
name|size
operator|==
operator|-
literal|1
operator|||
name|size
operator|==
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addRemoveNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numNodes
init|=
literal|101
decl_stmt|;
name|Session
name|writer
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|test
init|=
name|writer
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|test
operator|.
name|addNode
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|save
argument_list|()
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|test
operator|.
name|getNode
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|save
argument_list|()
expr_stmt|;
name|test
operator|.
name|addNode
argument_list|(
literal|"node-x"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasNode
argument_list|(
literal|"node-x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

