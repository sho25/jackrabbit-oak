begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|query
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|assertEquals
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRINGS
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Oak
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
name|ContentRepository
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|OakSolrConfiguration
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
name|index
operator|.
name|solr
operator|.
name|TestUtils
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|query
operator|.
name|AbstractQueryTest
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
name|security
operator|.
name|OpenSecurityProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
comment|/**  * General query extensive testcase for {@link SolrQueryIndex} and {@link  * org.apache.jackrabbit.oak.plugins.index.solr.index.SolrIndexDiff}  */
end_comment

begin_class
specifier|public
class|class
name|SolrIndexQueryTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|solrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|createTestIndexNode
argument_list|(
name|index
argument_list|,
name|SolrQueryIndex
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|OakSolrConfiguration
name|testConfiguration
init|=
name|TestUtils
operator|.
name|getTestConfiguration
argument_list|()
decl_stmt|;
try|try
block|{
name|solrServer
operator|=
name|TestUtils
operator|.
name|createSolrServer
argument_list|()
expr_stmt|;
return|return
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|TestUtils
operator|.
name|getTestQueryIndexProvider
argument_list|(
name|solrServer
argument_list|,
name|testConfiguration
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|TestUtils
operator|.
name|getTestIndexHookProvider
argument_list|(
name|solrServer
argument_list|,
name|testConfiguration
argument_list|)
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|sql2
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-420"
argument_list|)
specifier|public
name|void
name|sql2Measure
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2_measure.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|descendantTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|test
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|test
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|result
init|=
name|executeQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where isdescendantnode('/test')"
argument_list|,
literal|"JCR-SQL2"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/a"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/b"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|descendantTest2
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|test
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
name|asList
argument_list|(
literal|"Hello"
argument_list|,
literal|"World"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|test
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"Hello"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|result
init|=
name|executeQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where isdescendantnode('/test') and name='World'"
argument_list|,
literal|"JCR-SQL2"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/a"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ischildnodeTest
parameter_list|()
throws|throws
name|Exception
block|{
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
name|parents
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"parents"
argument_list|)
decl_stmt|;
name|parents
operator|.
name|addChild
argument_list|(
literal|"p0"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|parents
operator|.
name|addChild
argument_list|(
literal|"p1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|parents
operator|.
name|addChild
argument_list|(
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|Tree
name|children
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"children"
argument_list|)
decl_stmt|;
name|children
operator|.
name|addChild
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|children
operator|.
name|addChild
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|children
operator|.
name|addChild
argument_list|(
literal|"c3"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|children
operator|.
name|addChild
argument_list|(
literal|"c4"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|result
init|=
name|executeQuery
argument_list|(
literal|"select p.[jcr:path], p2.[jcr:path] from [nt:base] as p inner join [nt:base] as p2 on ischildnode(p2, p) where p.[jcr:path] = '/'"
argument_list|,
literal|"JCR-SQL2"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/, /children"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/, /jcr:system"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/, /oak:index"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/, /parents"
argument_list|,
name|result
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

