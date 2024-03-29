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
name|Row
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
name|fixture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
comment|/**  * Tests the fulltext index.  */
end_comment

begin_class
specifier|public
class|class
name|QueryFulltextTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|QueryFulltextTest
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
name|excerpt
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
name|n1
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
name|n1
operator|.
name|setProperty
argument_list|(
literal|"desc"
argument_list|,
literal|"description"
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node2"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"Hello World"
argument_list|)
expr_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"desc"
argument_list|,
literal|"Description"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Query
name|q
decl_stmt|;
name|RowIterator
name|it
decl_stmt|;
name|Row
name|row
decl_stmt|;
name|String
name|s
decl_stmt|;
name|String
name|xpath
init|=
literal|"//*[jcr:contains(., 'hello')]/rep:excerpt(.) order by @jcr:path"
decl_stmt|;
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
name|it
operator|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getRows
argument_list|()
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|row
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|s
operator|=
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:excerpt(.)"
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (1)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"<strong>hello</strong> world"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (2)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"description"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|path
operator|=
name|row
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|s
operator|=
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:excerpt(.)"
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (3)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"<strong>Hello</strong> World"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (4)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"Description"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|xpath
operator|=
literal|"//*[jcr:contains(., 'hello')]/rep:excerpt(.) order by @jcr:path"
expr_stmt|;
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
name|it
operator|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getRows
argument_list|()
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|path
operator|=
name|row
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|s
operator|=
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:excerpt(text)"
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (5)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"<strong>hello</strong> world"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (6)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"description"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|path
operator|=
name|row
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|s
operator|=
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:excerpt(text)"
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (7)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"<strong>Hello</strong> World"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|+
literal|":"
operator|+
name|s
operator|+
literal|" (8)"
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"Description"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fulltextOrWithinText
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
name|n1
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node2"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hallo"
argument_list|)
expr_stmt|;
name|Node
name|n3
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node3"
argument_list|)
decl_stmt|;
name|n3
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello hallo"
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
literal|"where contains([text], 'hello OR hallo') order by [jcr:path]"
decl_stmt|;
name|Query
name|q
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
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* traverse \"*\" "
operator|+
literal|"where contains([nt:base].[text], 'hello OR hallo') */"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"plan"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the result
comment|// uppercase "OR" mean logical "or"
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
name|assertEquals
argument_list|(
literal|"/testroot/node1, /testroot/node2, /testroot/node3"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
comment|// lowercase "or" mean search for the term "or"
name|sql2
operator|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([text], 'hello or hallo') order by [jcr:path]"
expr_stmt|;
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
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|getResult
parameter_list|(
name|QueryResult
name|result
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|result
operator|.
name|getRows
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|buff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getValue
argument_list|(
name|propertyName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

