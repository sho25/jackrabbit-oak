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
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|IndexConstants
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
name|lucene
operator|.
name|indexAugment
operator|.
name|IndexAugmentorFactory
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
name|lucene
operator|.
name|indexAugment
operator|.
name|impl
operator|.
name|IndexAugmentorFactoryImpl
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
name|lucene
operator|.
name|score
operator|.
name|ScorerProviderFactory
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
name|lucene
operator|.
name|spi
operator|.
name|FulltextQueryTermsProvider
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
name|lucene
operator|.
name|spi
operator|.
name|IndexFieldProvider
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
name|NodeTypeRegistry
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
name|commit
operator|.
name|Observer
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
name|QueryIndexProvider
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexAugmentTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
specifier|final
name|SimpleIndexAugmentorFactory
name|factory
init|=
operator|new
name|SimpleIndexAugmentorFactory
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
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
literal|null
argument_list|,
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|(
operator|new
name|IndexTracker
argument_list|()
argument_list|,
name|ScorerProviderFactory
operator|.
name|DEFAULT
argument_list|,
name|factory
argument_list|)
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|()
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
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
name|editorProvider
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
comment|//OAK-3789
annotation|@
name|Test
specifier|public
name|void
name|skipDefaultIndexing
parameter_list|()
throws|throws
name|Exception
block|{
comment|//setup repo and index
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|TestUtil
operator|.
name|TEST_NODE_TYPE
argument_list|)
argument_list|,
literal|"test nodeType"
argument_list|)
expr_stmt|;
name|Tree
name|props
init|=
name|createIndex
argument_list|(
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|Tree
name|prop
init|=
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
literal|"subChild/foo"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_SKIP_DEFAULT_INDEXING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|=
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
literal|"foo1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_SKIP_DEFAULT_INDEXING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//add content
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
name|Tree
name|node
init|=
name|createNodeWithType
argument_list|(
name|test
argument_list|,
literal|"item1"
argument_list|,
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|Tree
name|subChild
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"subChild"
argument_list|)
decl_stmt|;
name|subChild
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//queries
name|String
name|query
init|=
literal|"select * from [oak:TestNode] AS s where [subChild/foo]='bar'"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|SQL2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"There should not be any rows"
argument_list|,
literal|0
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"select * from [oak:TestNode] AS s where [foo1]='bar1'"
expr_stmt|;
name|paths
operator|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"There should not be any rows"
argument_list|,
literal|0
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3576
annotation|@
name|Test
specifier|public
name|void
name|queryHook
parameter_list|()
throws|throws
name|Exception
block|{
comment|//setup repo and index
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|TestUtil
operator|.
name|TEST_NODE_TYPE
argument_list|)
argument_list|,
literal|"test nodeType"
argument_list|)
expr_stmt|;
name|Tree
name|props
init|=
name|createIndex
argument_list|(
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//setup query augmentor
specifier|final
name|String
name|searchText
init|=
literal|"search this text"
decl_stmt|;
name|factory
operator|.
name|fulltextQueryTermsProvider
operator|=
operator|new
name|FulltextQueryTermsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|getQueryTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Full text term passed to provider isn't same as the one passed in query"
argument_list|,
name|searchText
argument_list|,
name|text
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|":fulltext"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
comment|//add content
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
name|Tree
name|node
init|=
name|createNodeWithType
argument_list|(
name|test
argument_list|,
literal|"item"
argument_list|,
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//query (searchText doesn't have 'bar'... our augment would search for :fulltext:bar
name|String
name|query
init|=
literal|"select [jcr:path] from [oak:TestNode] where CONTAINS(*, '"
operator|+
name|searchText
operator|+
literal|"')"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|SQL2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Augmented query wasn't used to search"
argument_list|,
literal|1
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/item"
argument_list|,
name|paths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3576
annotation|@
name|Test
specifier|public
name|void
name|indexHookCallbackFrequency
parameter_list|()
throws|throws
name|Exception
block|{
comment|//setup repo and index
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|TestUtil
operator|.
name|TEST_NODE_TYPE
argument_list|)
argument_list|,
literal|"test nodeType"
argument_list|)
expr_stmt|;
name|Tree
name|props
init|=
name|createIndex
argument_list|(
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
literal|"foo1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
literal|"foo2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
literal|"subChild/foo3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//setup index augmentor
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|factory
operator|.
name|indexFieldProvider
operator|=
operator|new
name|IndexFieldProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Field
argument_list|>
name|getAugmentedFields
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|NodeState
name|document
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
comment|//add content
name|counter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
name|Tree
name|node
init|=
name|createNodeWithType
argument_list|(
name|test
argument_list|,
literal|"item"
argument_list|,
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|Tree
name|subChild
init|=
name|node
operator|.
name|addChild
argument_list|(
literal|"subChild"
argument_list|)
decl_stmt|;
name|subChild
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
literal|"bar3"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of callbacks should be same as number of changed properties"
argument_list|,
literal|3
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|//change sub-property
name|counter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|subChild
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/test/item/subChild"
argument_list|)
expr_stmt|;
name|subChild
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
literal|"bar4"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Sub child property change should make call backs for all indexed properties"
argument_list|,
literal|3
argument_list|,
name|counter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3576
annotation|@
name|Test
specifier|public
name|void
name|indexHookCallbackAndStorage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|propName
init|=
literal|"subChild/foo"
decl_stmt|;
comment|//setup repo and index
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|TestUtil
operator|.
name|TEST_NODE_TYPE
argument_list|)
argument_list|,
literal|"test nodeType"
argument_list|)
expr_stmt|;
name|Tree
name|props
init|=
name|createIndex
argument_list|(
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
name|propName
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//setup index augmentor
name|factory
operator|.
name|indexFieldProvider
operator|=
operator|new
name|IndexFieldProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Field
argument_list|>
name|getAugmentedFields
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|NodeState
name|document
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"/test/item"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|propName
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|NT_TEST
argument_list|,
name|document
operator|.
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|indexDefinition
operator|.
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Lists
operator|.
expr|<
name|Field
operator|>
name|newArrayList
argument_list|(
operator|new
name|StringField
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|+
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|factory
operator|.
name|fulltextQueryTermsProvider
operator|=
operator|new
name|FulltextQueryTermsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|getQueryTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|text
argument_list|,
literal|"1"
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
comment|//add content
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
name|Tree
name|node
init|=
name|createNodeWithType
argument_list|(
name|test
argument_list|,
literal|"item"
argument_list|,
name|TestUtil
operator|.
name|NT_TEST
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"subChild"
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//do some queries... explicitly looking for terms we augmented (i.e. barbar=1)
name|String
name|query
init|=
literal|"select [jcr:path] from [oak:TestNode] AS s where CONTAINS(*, 'barbar')"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|SQL2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"/test/item"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Tree
name|createNodeWithType
parameter_list|(
name|Tree
name|t
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
name|t
operator|=
name|t
operator|.
name|addChild
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
specifier|private
name|Tree
name|createIndex
parameter_list|(
name|String
name|nodeType
parameter_list|)
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
return|return
name|createIndex
argument_list|(
name|rootTree
argument_list|,
name|nodeType
argument_list|)
return|;
block|}
specifier|private
name|Tree
name|createIndex
parameter_list|(
name|Tree
name|root
parameter_list|,
name|String
name|nodeType
parameter_list|)
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|createTestIndexNode
argument_list|(
name|root
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
decl_stmt|;
return|return
name|TestUtil
operator|.
name|newRulePropTree
argument_list|(
name|index
argument_list|,
name|nodeType
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|SimpleIndexAugmentorFactory
implements|implements
name|IndexAugmentorFactory
block|{
specifier|private
name|IndexFieldProvider
name|indexFieldProvider
init|=
literal|null
decl_stmt|;
specifier|private
name|FulltextQueryTermsProvider
name|fulltextQueryTermsProvider
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|IndexFieldProvider
name|getIndexFieldProvider
parameter_list|()
block|{
return|return
name|indexFieldProvider
operator|==
literal|null
condition|?
name|IndexAugmentorFactoryImpl
operator|.
name|DEFAULT
operator|.
name|getIndexFieldProvider
argument_list|()
else|:
name|indexFieldProvider
return|;
block|}
annotation|@
name|Override
specifier|public
name|FulltextQueryTermsProvider
name|getFulltextQueryTermsProvider
parameter_list|()
block|{
return|return
name|fulltextQueryTermsProvider
operator|==
literal|null
condition|?
name|IndexAugmentorFactoryImpl
operator|.
name|DEFAULT
operator|.
name|getFulltextQueryTermsProvider
argument_list|()
else|:
name|fulltextQueryTermsProvider
return|;
block|}
block|}
block|}
end_class

end_unit

