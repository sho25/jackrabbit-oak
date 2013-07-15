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
name|jcr
operator|.
name|query
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
name|jcr
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
comment|/**  * Tests query plans.  */
end_comment

begin_class
specifier|public
class|class
name|QueryPlanTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|QueryPlanTest
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
name|nodeType
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
name|QueryManager
name|qm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Node
name|testRootNode
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"testroot"
argument_list|)
decl_stmt|;
name|Node
name|n1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node1"
argument_list|)
decl_stmt|;
name|Node
name|n2
init|=
name|n1
operator|.
name|addNode
argument_list|(
literal|"node2"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|addNode
argument_list|(
literal|"node3"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|sql2
init|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where [node2/node3/jcr:primaryType] is not null"
decl_stmt|;
name|Query
name|q
decl_stmt|;
name|QueryResult
name|result
decl_stmt|;
name|RowIterator
name|it
decl_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|result
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|it
operator|=
name|result
operator|.
name|getRows
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|plan
init|=
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"plan"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
comment|// should not use the index on "jcr:primaryType"
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* traverse \"*\" "
operator|+
literal|"where [nt:base].[node2/node3/jcr:primaryType] is not null */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
comment|// verify the result
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|result
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|it
operator|=
name|result
operator|.
name|getRows
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"path"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot/node1"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

