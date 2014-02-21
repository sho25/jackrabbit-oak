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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|index
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|SolrBaseTest
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
name|SolrQuery
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
name|response
operator|.
name|QueryResponse
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

begin_comment
comment|/**  * Integration test for {@link org.apache.jackrabbit.oak.plugins.index.solr.index.SolrCommitHook}  */
end_comment

begin_class
specifier|public
class|class
name|SolrCommitHookIT
extends|extends
name|SolrBaseTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAddSomeNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|createRoot
argument_list|()
decl_stmt|;
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"doc1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hit that hot hat tattoo"
argument_list|)
expr_stmt|;
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"doc2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"it hits hot hats"
argument_list|)
expr_stmt|;
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"doc3"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"tattoos hate hot hits"
argument_list|)
expr_stmt|;
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"doc4"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hats tattoos hit hot"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"no documents were indexed"
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|r
init|=
name|createRoot
argument_list|()
decl_stmt|;
comment|// Add a node
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"testRemoveNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// check the node is not in Solr anymore
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"path_exact:\\/testRemoveNode"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"item with id:testRemoveNode was not found in the index"
argument_list|,
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// remove the node in oak
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"testRemoveNode"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// check the node is not in Solr anymore
name|assertTrue
argument_list|(
literal|"item with id:testRemoveNode was found in the index"
argument_list|,
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

