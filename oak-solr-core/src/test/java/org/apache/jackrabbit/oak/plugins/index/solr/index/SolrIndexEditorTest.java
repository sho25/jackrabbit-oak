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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|api
operator|.
name|PropertyState
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
name|IndexUpdateCallback
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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|CommitPolicy
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
name|DefaultSolrConfiguration
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
name|memory
operator|.
name|PropertyStates
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|SolrServer
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link org.apache.jackrabbit.oak.plugins.index.solr.index.SolrIndexEditor}  */
end_comment

begin_class
specifier|public
class|class
name|SolrIndexEditorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIndexedProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mock
argument_list|(
name|NodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|TestUtils
operator|.
name|createSolrServer
argument_list|()
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
name|TestUtils
operator|.
name|getTestConfiguration
argument_list|()
decl_stmt|;
name|IndexUpdateCallback
name|callback
init|=
name|mock
argument_list|(
name|IndexUpdateCallback
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrIndexEditor
name|solrIndexEditor
init|=
operator|new
name|SolrIndexEditor
argument_list|(
name|builder
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterable
name|properties
init|=
operator|new
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|when
argument_list|(
name|after
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|solrIndexEditor
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|solrServer
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"foo1:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIgnoredPropertiesNotIndexed
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mock
argument_list|(
name|NodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|TestUtils
operator|.
name|createSolrServer
argument_list|()
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getIgnoredProperties
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo2"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
block|{
return|return
name|CommitPolicy
operator|.
name|HARD
return|;
block|}
block|}
decl_stmt|;
name|IndexUpdateCallback
name|callback
init|=
name|mock
argument_list|(
name|IndexUpdateCallback
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrIndexEditor
name|solrIndexEditor
init|=
operator|new
name|SolrIndexEditor
argument_list|(
name|builder
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterable
name|properties
init|=
operator|new
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|when
argument_list|(
name|after
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|solrIndexEditor
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|solrServer
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"foo2:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

