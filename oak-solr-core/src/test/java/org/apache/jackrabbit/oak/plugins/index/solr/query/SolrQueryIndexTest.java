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
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|query
operator|.
name|QueryEngineSettings
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
name|ast
operator|.
name|Operator
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
name|ast
operator|.
name|SelectorImpl
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
name|index
operator|.
name|FilterImpl
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
name|query
operator|.
name|Cursor
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|PropertyValues
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
name|common
operator|.
name|SolrInputDocument
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
comment|/**  * Testcase for {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex}  */
end_comment

begin_class
specifier|public
class|class
name|SolrQueryIndexTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testDefaultCostWithNoRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
name|mock
argument_list|(
name|SelectorImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|""
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultCostWithPathRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where isdescendantnode(a, '/test')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCostWithPathRestrictionsEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
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
name|boolean
name|useForPathRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where isdescendantnode(a, '/test')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|10
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultCostWithPropertyRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where name = 'hello')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"name"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCostWithPropertyRestrictionsEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
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
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where name = 'hello')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"name"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|10
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultCostWithPrimaryTypeRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where jcr:primaryType = 'nt:unstructured')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCostWithPrimaryTypeRestrictionsEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
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
name|boolean
name|useForPrimaryTypes
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where jcr:primaryType = 'nt:unstructured')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|10
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCostWithPropertyRestrictionsEnabledButPropertyIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SolrServer
name|solrServer
init|=
name|mock
argument_list|(
name|SolrServer
operator|.
name|class
argument_list|)
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
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Nonnull
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
literal|"name"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where name = 'hello')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"name"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|double
name|cost
init|=
name|solrQueryIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|cost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueryOnIgnoredExistingProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
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
name|SolrInputDocument
name|document
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"path_exact"
argument_list|,
literal|"/a/b"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Nonnull
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
literal|"name"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where name = 'hello')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"name"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|plan
init|=
name|solrQueryIndex
operator|.
name|getPlan
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|contains
argument_list|(
literal|"q=*%3A*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// querying on property name is not possible, then falling back to a match all query
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueryOnExistingProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
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
name|SolrInputDocument
name|document
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"path_exact"
argument_list|,
literal|"/a/b"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"select * from [nt:base] as a where name = 'hello')"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"name"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|plan
init|=
name|solrQueryIndex
operator|.
name|getPlan
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|contains
argument_list|(
literal|"q=name%3Ahello"
argument_list|)
argument_list|)
expr_stmt|;
comment|// query gets converted to a fielded query on name field
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getNames
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|sqlQuery
init|=
literal|"select [jcr:path], [jcr:score], [rep:excerpt] from [nt:hierarchyNode] as a where"
operator|+
literal|" isdescendantnode(a, '/content') and contains([jcr:content/*], 'founded') union select [jcr:path],"
operator|+
literal|" [jcr:score], [rep:excerpt] from [nt:hierarchyNode] as a where isdescendantnode(a, '/content') and "
operator|+
literal|"contains([jcr:content/jcr:title], 'founded') union select [jcr:path], [jcr:score], [rep:excerpt]"
operator|+
literal|" from [nt:hierarchyNode] as a where isdescendantnode(a, '/content') and "
operator|+
literal|"contains([jcr:content/jcr:description], 'founded') order by [jcr:score] desc"
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
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|SolrQueryIndex
name|solrQueryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
literal|"solr"
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
name|sqlQuery
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|Cursor
name|cursor
init|=
name|solrQueryIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cursor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

