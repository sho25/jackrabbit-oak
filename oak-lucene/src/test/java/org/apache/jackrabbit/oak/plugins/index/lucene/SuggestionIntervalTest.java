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
name|Sets
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
name|api
operator|.
name|JackrabbitSession
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
name|Jcr
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
name|stats
operator|.
name|Clock
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
name|Before
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
name|TemporaryFolder
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
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|TimeUnit
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|TestUtil
operator|.
name|shutdown
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

begin_class
specifier|public
class|class
name|SuggestionIntervalTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
name|Repository
name|repository
init|=
literal|null
decl_stmt|;
specifier|private
name|JackrabbitSession
name|session
init|=
literal|null
decl_stmt|;
specifier|private
name|Node
name|root
init|=
literal|null
decl_stmt|;
specifier|private
name|Clock
name|clock
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|()
operator|.
name|with
argument_list|(
operator|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
operator|)
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
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
decl_stmt|;
name|repository
operator|=
name|jcr
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
operator|(
name|JackrabbitSession
operator|)
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
expr_stmt|;
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexEditorContext
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|LuceneIndexEditorContext
operator|.
name|setClock
argument_list|(
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|shutdown
argument_list|(
name|repository
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createSuggestIndex
parameter_list|(
name|String
name|indexedNodeType
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|indexName
init|=
literal|"lucene-suggest"
decl_stmt|;
name|Node
name|def
init|=
name|root
operator|.
name|getNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|addNode
argument_list|(
name|indexName
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
name|indexName
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|propertyIdxDef
init|=
name|def
operator|.
name|addNode
argument_list|(
name|INDEX_RULES
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|indexedNodeType
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"indexedProperty"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|propertyIdxDef
operator|.
name|setProperty
argument_list|(
literal|"propertyIndex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|propertyIdxDef
operator|.
name|setProperty
argument_list|(
literal|"analyzed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|propertyIdxDef
operator|.
name|setProperty
argument_list|(
literal|"useInSuggest"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|propertyIdxDef
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
name|LuceneIndexConstants
operator|.
name|PROPDEF_PROP_NODE_NAME
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|getSuggestions
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|String
name|suggestFor
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ret
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|String
name|suggQuery
init|=
name|createSuggestQuery
argument_list|(
name|nodeType
argument_list|,
name|suggestFor
argument_list|)
decl_stmt|;
name|QueryManager
name|queryManager
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|QueryResult
name|result
init|=
name|queryManager
operator|.
name|createQuery
argument_list|(
name|suggQuery
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
name|RowIterator
name|rows
init|=
name|result
operator|.
name|getRows
argument_list|()
decl_stmt|;
while|while
condition|(
name|rows
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Row
name|firstRow
init|=
name|rows
operator|.
name|nextRow
argument_list|()
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|firstRow
operator|.
name|getValue
argument_list|(
literal|"suggestion"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
specifier|private
name|String
name|createSuggestQuery
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|String
name|suggestFor
parameter_list|)
block|{
return|return
literal|"SELECT [rep:suggest()] as suggestion, [jcr:score] as score  FROM ["
operator|+
name|nodeType
operator|+
literal|"] WHERE suggest('"
operator|+
name|suggestFor
operator|+
literal|"')"
return|;
block|}
comment|//OAK-4068
annotation|@
name|Test
specifier|public
name|void
name|defaultSuggestInterval
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|nodeType
init|=
literal|"nt:unstructured"
decl_stmt|;
name|createSuggestIndex
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|//wait for documented time before suggestions are refreshed
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
comment|//get one more tick
name|root
operator|.
name|addNode
argument_list|(
literal|"indexedNode"
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
name|getSuggestions
argument_list|(
name|nodeType
argument_list|,
literal|"indexedn"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"indexedNode"
argument_list|,
name|suggestions
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//OAK-4068
annotation|@
name|Test
specifier|public
name|void
name|suggestionUpdateWithoutIndexChange
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|nodeType
init|=
literal|"nt:unstructured"
decl_stmt|;
name|createSuggestIndex
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|long
name|currTime
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|toTime
init|=
name|currTime
operator|+
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
name|IndexDefinition
operator|.
name|DEFAULT_SUGGESTER_UPDATE_FREQUENCY_MINUTES
argument_list|)
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"indexedNode"
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|//wait for suggestions refresh time
name|clock
operator|.
name|waitUntil
argument_list|(
name|toTime
argument_list|)
expr_stmt|;
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
comment|//get one more tick
comment|//push a change which should not make any change in the index but yet should help update suggestions
name|root
operator|.
name|addNode
argument_list|(
literal|"some-non-index-change"
argument_list|,
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
name|getSuggestions
argument_list|(
name|nodeType
argument_list|,
literal|"indexedn"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"indexedNode"
argument_list|,
name|suggestions
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

