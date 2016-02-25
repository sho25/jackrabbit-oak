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
name|commons
operator|.
name|JcrUtils
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|JcrConstants
operator|.
name|NT_BASE
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
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|EVALUATE_PATH_RESTRICTION
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
name|LuceneIndexConstants
operator|.
name|PROPDEF_PROP_NODE_NAME
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
name|PROP_USE_IN_SUGGEST
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
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexDescendantSuggestionTest
block|{
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
name|createContent
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
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
name|createContent
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*         Make content with following structure:         * sugg-idx is a simple index to suggest node names on type "oak:Unstructured"         * test[1-6] nodes would be "oak:Unstructured".         * all other nodes, unless required are "nt:unstructured"         * Index on one sub-tree is on nt:base so that we can do sub-tree suggestion test with unambiguous indices         */
comment|//  /oak:index/sugg-idx, /test1
name|createSuggestIndex
argument_list|(
name|root
argument_list|,
literal|"sugg-idx"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|PROPDEF_PROP_NODE_NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"test1"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
comment|/*             /content1                 /tree1                     /test2                 /tree2                     /test3          */
name|Node
name|content1
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"content1"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|tree1
init|=
name|content1
operator|.
name|addNode
argument_list|(
literal|"tree1"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|tree1
operator|.
name|addNode
argument_list|(
literal|"test2"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|Node
name|tree2
init|=
name|content1
operator|.
name|addNode
argument_list|(
literal|"tree2"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|tree2
operator|.
name|addNode
argument_list|(
literal|"test3"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
comment|//  /content2/oak:index/sugg-idx, /content2/test4
name|Node
name|content2
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"content2"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|createSuggestIndex
argument_list|(
name|content2
argument_list|,
literal|"sugg-idx"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|PROPDEF_PROP_NODE_NAME
argument_list|)
expr_stmt|;
name|content2
operator|.
name|addNode
argument_list|(
literal|"test4"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
comment|//  /content3/oak:index/sugg-idx, /content3/test5, /content3/sC/test6
name|Node
name|content3
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"content3"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|createSuggestIndex
argument_list|(
name|content3
argument_list|,
literal|"sugg-idx"
argument_list|,
name|NT_BASE
argument_list|,
name|PROPDEF_PROP_NODE_NAME
argument_list|)
expr_stmt|;
name|content3
operator|.
name|addNode
argument_list|(
literal|"test5"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|Node
name|subChild
init|=
name|content3
operator|.
name|addNode
argument_list|(
literal|"sC"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|subChild
operator|.
name|addNode
argument_list|(
literal|"test6"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createSuggestIndex
parameter_list|(
name|Node
name|rootNode
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|indexedNodeType
parameter_list|,
name|String
name|indexedPropertyName
parameter_list|)
throws|throws
name|Exception
block|{
name|Node
name|def
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|rootNode
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|addNode
argument_list|(
name|name
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
name|name
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
name|def
operator|.
name|setProperty
argument_list|(
name|EVALUATE_PATH_RESTRICTION
argument_list|,
literal|true
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
literal|"analyzed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|propertyIdxDef
operator|.
name|setProperty
argument_list|(
name|PROP_USE_IN_SUGGEST
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
name|indexedPropertyName
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|createSuggestQuery
parameter_list|(
name|String
name|nodeTypeName
parameter_list|,
name|String
name|suggestFor
parameter_list|,
name|String
name|rootPath
parameter_list|)
block|{
return|return
literal|"SELECT [rep:suggest()] as suggestion, [jcr:score] as score  FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
operator|+
literal|" WHERE suggest('"
operator|+
name|suggestFor
operator|+
literal|"')"
operator|+
operator|(
name|rootPath
operator|==
literal|null
condition|?
literal|""
else|:
literal|" AND ISDESCENDANTNODE(["
operator|+
name|rootPath
operator|+
literal|"])"
operator|)
return|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getSuggestions
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|Exception
block|{
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
name|query
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
name|Set
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
name|newHashSet
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
name|suggestions
operator|.
name|add
argument_list|(
name|rows
operator|.
name|nextRow
argument_list|()
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
name|suggestions
return|;
block|}
specifier|private
name|void
name|validateSuggestions
parameter_list|(
name|String
name|query
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
name|getSuggestions
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect suggestions"
argument_list|,
name|expected
argument_list|,
name|suggestions
argument_list|)
expr_stmt|;
block|}
comment|//Don't break suggestions :)
annotation|@
name|Test
specifier|public
name|void
name|noDescendantSuggestsAll
parameter_list|()
throws|throws
name|Exception
block|{
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|null
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|,
literal|"test4"
argument_list|,
literal|"test5"
argument_list|,
literal|"test6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3994
annotation|@
name|Test
specifier|public
name|void
name|rootIndexWithDescendantConstraint
parameter_list|()
throws|throws
name|Exception
block|{
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|"/content1"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3994
annotation|@
name|Test
specifier|public
name|void
name|descendantSuggestionRequirePathRestrictionIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|rootIndexDef
init|=
name|root
operator|.
name|getNode
argument_list|(
literal|"oak:index/sugg-idx"
argument_list|)
decl_stmt|;
name|rootIndexDef
operator|.
name|getProperty
argument_list|(
name|EVALUATE_PATH_RESTRICTION
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|rootIndexDef
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|//Without path restriction indexing, descendant clause shouldn't be respected
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|"/content1"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|,
literal|"test4"
argument_list|,
literal|"test5"
argument_list|,
literal|"test6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-3992"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|ambiguousSubtreeIndexWithDescendantConstraint
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|query
init|=
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|"/content2"
argument_list|)
decl_stmt|;
name|String
name|explainQuery
init|=
literal|"EXPLAIN "
operator|+
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|"/content2"
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
name|explainQuery
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
name|String
name|explanation
init|=
name|rows
operator|.
name|nextRow
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Subtree index should get picked"
argument_list|,
name|explanation
operator|.
name|contains
argument_list|(
literal|"lucene:sugg-idx(/content2/oak:index/sugg-idx)"
argument_list|)
argument_list|)
expr_stmt|;
name|validateSuggestions
argument_list|(
name|query
argument_list|,
name|newHashSet
argument_list|(
literal|"test4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3994
annotation|@
name|Test
specifier|public
name|void
name|unambiguousSubtreeIndexWithDescendantConstraint
parameter_list|()
throws|throws
name|Exception
block|{
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_BASE
argument_list|,
literal|"te"
argument_list|,
literal|"/content3"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test5"
argument_list|,
literal|"test6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3994
annotation|@
name|Test
specifier|public
name|void
name|unambiguousSubtreeIndexWithSubDescendantConstraint
parameter_list|()
throws|throws
name|Exception
block|{
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_BASE
argument_list|,
literal|"te"
argument_list|,
literal|"/content3/sC"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-3993"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|unionOnTwoDescendants
parameter_list|()
throws|throws
name|Exception
block|{
name|validateSuggestions
argument_list|(
name|createSuggestQuery
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|,
literal|"te"
argument_list|,
literal|"/content1"
argument_list|)
operator|+
literal|" UNION "
operator|+
name|createSuggestQuery
argument_list|(
name|NT_BASE
argument_list|,
literal|"te"
argument_list|,
literal|"/content3"
argument_list|)
argument_list|,
name|newHashSet
argument_list|(
literal|"test2"
argument_list|,
literal|"test3"
argument_list|,
literal|"test5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

