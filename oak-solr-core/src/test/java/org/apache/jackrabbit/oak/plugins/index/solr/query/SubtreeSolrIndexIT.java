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
name|JcrConstants
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
name|api
operator|.
name|Type
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
name|configuration
operator|.
name|DefaultSolrConfigurationProvider
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
name|index
operator|.
name|SolrIndexEditorProvider
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
name|server
operator|.
name|DefaultSolrServerProvider
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
name|junit
operator|.
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
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
name|*
import|;
end_import

begin_comment
comment|/**  * Integration test for indexing / search over subtrees with Solr index.  */
end_comment

begin_class
specifier|public
class|class
name|SubtreeSolrIndexIT
extends|extends
name|AbstractQueryTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SUBTREE
init|=
literal|"subtree"
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
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
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|subtree
init|=
name|rootTree
operator|.
name|addChild
argument_list|(
name|SUBTREE
argument_list|)
decl_stmt|;
name|Tree
name|solrIndexNode
init|=
name|createTestIndexNode
argument_list|(
name|subtree
argument_list|,
name|SolrQueryIndex
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|solrIndexNode
operator|.
name|setProperty
argument_list|(
literal|"pathRestrictions"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|solrIndexNode
operator|.
name|setProperty
argument_list|(
literal|"propertyRestrictions"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|solrIndexNode
operator|.
name|setProperty
argument_list|(
literal|"primaryTypes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|solrIndexNode
operator|.
name|setProperty
argument_list|(
literal|"commitPolicy"
argument_list|,
literal|"hard"
argument_list|)
expr_stmt|;
name|Tree
name|server
init|=
name|solrIndexNode
operator|.
name|addChild
argument_list|(
literal|"server"
argument_list|)
decl_stmt|;
name|server
operator|.
name|setProperty
argument_list|(
literal|"solrServerType"
argument_list|,
literal|"embedded"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setProperty
argument_list|(
literal|"solrHomePath"
argument_list|,
literal|"target/"
operator|+
name|name
operator|.
name|getMethodName
argument_list|()
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
try|try
block|{
name|DefaultSolrServerProvider
name|solrServerProvider
init|=
operator|new
name|DefaultSolrServerProvider
argument_list|()
decl_stmt|;
name|DefaultSolrConfigurationProvider
name|oakSolrConfigurationProvider
init|=
operator|new
name|DefaultSolrConfigurationProvider
argument_list|()
decl_stmt|;
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
operator|new
name|SolrQueryIndexProvider
argument_list|(
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexEditorProvider
argument_list|(
name|solrServerProvider
argument_list|,
name|oakSolrConfigurationProvider
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
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
name|SUBTREE
argument_list|)
decl_stmt|;
name|Tree
name|a
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"doc bye"
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"loc"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|Tree
name|b
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bye doc bye"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"loc"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:base] where contains(*,'doc') "
operator|+
literal|"AND isdescendantnode('/"
operator|+
name|SUBTREE
operator|+
literal|"')"
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
operator|+
name|SUBTREE
operator|+
literal|"/a"
argument_list|,
name|results
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
operator|+
name|SUBTREE
operator|+
literal|"/b"
argument_list|,
name|results
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

