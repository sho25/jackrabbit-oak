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
name|PropertyType
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
name|Ignore
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
comment|// OAK-1902
specifier|public
name|void
name|propertyIndexVersusNodeTypeIndex
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
name|Node
name|nt
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getNode
argument_list|(
literal|"nodetype"
argument_list|)
decl_stmt|;
name|nt
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|Node
name|uuid
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getNode
argument_list|(
literal|"uuid"
argument_list|)
decl_stmt|;
name|uuid
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"testroot"
argument_list|)
condition|)
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"testroot"
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|i
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"mix:referenceable"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Query
name|q
decl_stmt|;
name|QueryResult
name|result
decl_stmt|;
name|RowIterator
name|it
decl_stmt|;
name|String
name|xpath
init|=
literal|"/jcr:root//element(*, oak:Unstructured)"
decl_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|xpath
argument_list|,
literal|"xpath"
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
comment|// System.out.println("plan: " + plan);
comment|// should use the node type index
name|assertEquals
argument_list|(
literal|"[oak:Unstructured] as [a] "
operator|+
literal|"/* nodeType Filter(query=explain select [jcr:path], [jcr:score], * "
operator|+
literal|"from [oak:Unstructured] as a "
operator|+
literal|"where isdescendantnode(a, '/') "
operator|+
literal|"/* xpath: /jcr:root//element(*, oak:Unstructured) */"
operator|+
literal|", path=//*) where isdescendantnode([a], [/]) */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
name|String
name|xpath2
init|=
literal|"/jcr:root//element(*, oak:Unstructured)[@jcr:uuid]"
decl_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|xpath2
operator|+
literal|""
argument_list|,
literal|"xpath"
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
name|plan
operator|=
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
expr_stmt|;
comment|// should use the index on "jcr:uuid"
name|assertEquals
argument_list|(
literal|"[oak:Unstructured] as [a] "
operator|+
literal|"/* property uuid IS NOT NULL where ([a].[jcr:uuid] is not null) "
operator|+
literal|"and (isdescendantnode([a], [/])) */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// OAK-1903
specifier|public
name|void
name|propertyEqualsVersusPropertyNotNull
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
name|createPropertyIndex
argument_list|(
name|session
argument_list|,
literal|"notNull"
argument_list|)
expr_stmt|;
name|createPropertyIndex
argument_list|(
name|session
argument_list|,
literal|"equals"
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|i
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"notNull"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|n
operator|.
name|setProperty
argument_list|(
literal|"equals"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|xpath
init|=
literal|"/jcr:root//*[@notNull and @equals=1]"
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
name|xpath
argument_list|,
literal|"xpath"
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
comment|// System.out.println("plan: " + plan);
comment|// should not use the index on "jcr:uuid"
name|assertEquals
argument_list|(
literal|"[nt:base] as [a] /* property notNull IS NOT NULL "
operator|+
literal|"where ([a].[notNull] is not null) "
operator|+
literal|"and ([a].[equals] = 1) "
operator|+
literal|"and (isdescendantnode([a], [/])) */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// OAK-1898
specifier|public
name|void
name|correctPropertyIndexUsage
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
name|createPropertyIndex
argument_list|(
name|session
argument_list|,
literal|"fiftyPercent"
argument_list|)
expr_stmt|;
name|createPropertyIndex
argument_list|(
name|session
argument_list|,
literal|"tenPercent"
argument_list|)
expr_stmt|;
name|createPropertyIndex
argument_list|(
name|session
argument_list|,
literal|"hundredPercent"
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
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|i
argument_list|,
literal|"oak:Unstructured"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"tenPercent"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"fiftyPercent"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|n
operator|.
name|setProperty
argument_list|(
literal|"hundredPercent"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|xpath
init|=
literal|"/jcr:root//*[@tenPercent and @fiftyPercent and @hundredPercent]"
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
name|xpath
argument_list|,
literal|"xpath"
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
comment|// System.out.println("plan: " + plan);
comment|// should not use the index on "jcr:uuid"
name|assertEquals
argument_list|(
literal|"[nt:base] as [a] /* property tenPercent IS NOT NULL "
operator|+
literal|"where ([a].[tenPercent] is not null) "
operator|+
literal|"and ([a].[fiftyPercent] is not null) "
operator|+
literal|"and ([a].[hundredPercent] is not null) "
operator|+
literal|"and (isdescendantnode([a], [/])) */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// OAK-1898
specifier|public
name|void
name|traversalVersusPropertyIndex
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
name|n
init|=
name|testRootNode
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
literal|"depth"
argument_list|,
name|i
operator|+
literal|2
argument_list|)
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
literal|"n"
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"mix:referenceable"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|xpath
init|=
literal|"/jcr:root/testroot/n/n/n/n/n/n/n//*[jcr:uuid]"
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
name|xpath
argument_list|,
literal|"xpath"
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
comment|// System.out.println("plan: " + plan);
comment|// should not use the index on "jcr:uuid"
name|assertEquals
argument_list|(
literal|"[nt:base] as [a] /* property uuid IS NOT NULL "
operator|+
literal|"where ([a].[jcr:uuid] is not null) and "
operator|+
literal|"(isdescendantnode([a], [/testroot/n/n/n/n/n/n/n])) */"
argument_list|,
name|plan
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
name|nodetype
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getNode
argument_list|(
literal|"nodetype"
argument_list|)
decl_stmt|;
name|nodetype
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|uuidIndex
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
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"mix:referenceable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// this matches just one node (exact path),
comment|// so it should use the TraversintIndex
name|String
name|xpath
init|=
literal|"/jcr:root/testroot/node[@jcr:uuid]"
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
name|xpath
argument_list|,
literal|"xpath"
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
name|assertEquals
argument_list|(
literal|"[nt:base] as [a] /* traverse \"/testroot/node\" where "
operator|+
literal|"([a].[jcr:uuid] is not null) "
operator|+
literal|"and (issamenode([a], [/testroot/node])) */"
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
name|xpath
argument_list|,
literal|"xpath"
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
name|getPath
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot/node"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// this potentially matches many nodes,
comment|// so it should use the index on the UUID property
name|xpath
operator|=
literal|"/jcr:root/testroot/*[@jcr:uuid]"
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|xpath
argument_list|,
literal|"xpath"
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
name|plan
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[nt:base] as [a] /* property uuid IS NOT NULL "
operator|+
literal|"where ([a].[jcr:uuid] is not null) "
operator|+
literal|"and (ischildnode([a], [/testroot])) */"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1372"
argument_list|)
specifier|public
name|void
name|pathAndPropertyRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
empty_stmt|;
comment|// TODO work in progress
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
name|b
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|Node
name|c
init|=
name|b
operator|.
name|addNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|Node
name|d
init|=
name|c
operator|.
name|addNode
argument_list|(
literal|"d"
argument_list|)
decl_stmt|;
name|Node
name|e1
init|=
name|d
operator|.
name|addNode
argument_list|(
literal|"e1"
argument_list|)
decl_stmt|;
name|e1
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|Node
name|e2
init|=
name|d
operator|.
name|addNode
argument_list|(
literal|"e2"
argument_list|)
decl_stmt|;
name|e2
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|Node
name|e3
init|=
name|d
operator|.
name|addNode
argument_list|(
literal|"e3"
argument_list|)
decl_stmt|;
name|e3
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|xpath
init|=
literal|"/jcr:root/testroot//b/c/d/*[@jcr:uuid='1' or @jcr:uuid='2'] "
decl_stmt|;
name|String
name|sql2
init|=
literal|"select d.[jcr:path] as [jcr:path], d.[jcr:score] as [jcr:score], d.* "
operator|+
literal|"from [nt:base] as a inner join [nt:base] as b on ischildnode(b, a) "
operator|+
literal|"inner join [nt:base] as c on ischildnode(c, b) "
operator|+
literal|"inner join [nt:base] as d on ischildnode(d, c) "
operator|+
literal|"where name(a) = 'b' "
operator|+
literal|"and isdescendantnode(a, '/testroot') "
operator|+
literal|"and name(b) = 'c' "
operator|+
literal|"and name(c) = 'd' "
operator|+
literal|"and (d.[jcr:uuid] = '1' or d.[jcr:uuid] = '2')"
decl_stmt|;
name|sql2
operator|=
literal|"select d.[jcr:path] as [jcr:path], d.[jcr:score] as [jcr:score], d.* "
operator|+
literal|"from [nt:base] as d "
operator|+
literal|"where (d.[jcr:uuid] = '1' or d.[jcr:uuid] = '2')"
expr_stmt|;
name|sql2
operator|=
literal|"select d.[jcr:path] as [jcr:path], d.[jcr:score] as [jcr:score], d.* "
operator|+
literal|"from [nt:base] as d "
operator|+
literal|"inner join [nt:base] as c on ischildnode(d, c) "
operator|+
literal|"inner join [nt:base] as b on ischildnode(c, b) "
operator|+
literal|"inner join [nt:base] as a on ischildnode(b, a) "
operator|+
literal|"where name(a) = 'b' "
operator|+
literal|"and isdescendantnode(a, '/testroot') "
operator|+
literal|"and name(b) = 'c' "
operator|+
literal|"and name(c) = 'd' "
operator|+
literal|"and (d.[jcr:uuid] = '1' or d.[jcr:uuid] = '2')"
expr_stmt|;
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
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|plan
argument_list|)
expr_stmt|;
comment|// [nt:base] as [a] /* traverse "/testroot//*"
comment|// where (name([a]) = cast('b' as string))
comment|// and (isdescendantnode([a], [/testroot])) */
comment|// inner join [nt:base] as [b] /* traverse
comment|// "/path/from/the/join/selector/*" where name([b]) = cast('c' as string) */
comment|// on ischildnode([b], [a]) inner join [nt:base] as [c]
comment|// /* traverse "/path/from/the/join/selector/*"
comment|// where name([c]) = cast('d' as string) */ on ischildnode([c], [b])
comment|// inner join [nt:base] as [d] /* traverse "/path/from/the/join/selector/*"
comment|// where ([d].[type] is not null) and ([d].[type] in(cast('1' as string), cast('2' as string))) */
comment|// on ischildnode([d], [c])
comment|//        assertEquals("[nt:base] as [nt:base] /* traverse \"*\" " +
comment|//                "where [nt:base].[node2/node3/jcr:primaryType] is not null */",
comment|//                plan);
comment|// verify the result
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|xpath
argument_list|,
literal|"xpath"
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
literal|"/testroot/b/c/d/e1"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|path
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot/b/c/d/e2"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|createPropertyIndex
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|n
init|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|addNode
argument_list|(
name|propertyName
argument_list|,
literal|"oak:QueryIndexDefinition"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
operator|new
name|String
index|[]
block|{
name|propertyName
block|}
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

