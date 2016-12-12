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
operator|.
name|util
package|;
end_package

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
name|core
operator|.
name|ImmutableRoot
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
name|PathFilter
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
name|LuceneIndexConstants
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
name|tree
operator|.
name|TreeFactory
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
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
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
name|Test
import|;
end_import

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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|AGGREGATES
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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

begin_class
specifier|public
class|class
name|IndexDefinitionBuilderTest
block|{
specifier|private
name|IndexDefinitionBuilder
name|builder
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
specifier|private
name|NodeBuilder
name|nodeBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|dumpState
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|NodeStateUtils
operator|.
name|toString
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|state
operator|.
name|getLong
argument_list|(
literal|"compatVersion"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|state
operator|.
name|getString
argument_list|(
literal|"async"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lucene"
argument_list|,
name|state
operator|.
name|getString
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRule
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|includedPaths
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|ordered
argument_list|()
operator|.
name|enclosingRule
argument_list|()
operator|.
name|property
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|analyzed
argument_list|()
operator|.
name|propertyIndex
argument_list|()
operator|.
name|enclosingRule
argument_list|()
operator|.
name|property
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
name|PathFilter
operator|.
name|PROP_INCLUDED_PATHS
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyDefIndexPropertySetIndexFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|disable
argument_list|()
expr_stmt|;
name|PropertyState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"index property must exist"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Incorrect default value of index property"
argument_list|,
name|state
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|aggregates
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"cq:Page"
argument_list|)
operator|.
name|include
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|relativeNode
argument_list|()
expr_stmt|;
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"dam:Asset"
argument_list|,
literal|"*"
argument_list|,
literal|"*/*"
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"aggregates"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"aggregates"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"dam:Asset"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"aggregates"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"cq:Page"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|duplicatePropertyName
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|ordered
argument_list|()
operator|.
name|enclosingRule
argument_list|()
operator|.
name|property
argument_list|(
literal|"jcr:content/foo"
argument_list|)
operator|.
name|analyzed
argument_list|()
operator|.
name|propertyIndex
argument_list|()
operator|.
name|enclosingRule
argument_list|()
operator|.
name|property
argument_list|(
literal|"metadata/content/foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|state
operator|.
name|getChildNode
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ruleOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|build
argument_list|(
name|tree
argument_list|)
expr_stmt|;
comment|//Assert the order
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|children
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"indexRules"
argument_list|)
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nt:unstructured"
argument_list|,
name|children
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nt:base"
argument_list|,
name|children
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|regexProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
name|LuceneIndexConstants
operator|.
name|REGEX_ALL_PROPS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|state
argument_list|,
literal|"indexRules/nt:base/properties/prop"
argument_list|)
operator|.
name|getBoolean
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mergeExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|state
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|state
operator|.
name|getString
argument_list|(
literal|"async"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mergeExisting_IndexRule
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|=
name|builder
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|hasIndexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|hasIndexRule
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
operator|.
name|property
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
operator|.
name|hasPropertyRule
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:unstructured"
argument_list|)
operator|.
name|hasPropertyRule
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mergeExisting_Aggregates
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|include
argument_list|(
literal|"/path1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|include
argument_list|(
literal|"/path2"
argument_list|)
expr_stmt|;
name|nodeBuilder
operator|=
name|builder
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|include
argument_list|(
literal|"/path1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|aggregateRule
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|include
argument_list|(
literal|"/path3"
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|state
operator|.
name|getChildNode
argument_list|(
name|AGGREGATES
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noReindexIfNoChange
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|includedPaths
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|ordered
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|=
name|builder
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|includedPaths
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|isReindexRequired
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|getBoolean
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|baseState
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|nodeBuilder
operator|=
name|baseState
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|isReindexRequired
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getBoolean
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|baseState
operator|.
name|builder
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|isReindexRequired
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|isReindexRequired
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|getBoolean
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reindexAndAsyncFlagChange
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|async
argument_list|(
literal|"async"
argument_list|,
name|IndexConstants
operator|.
name|INDEXING_MODE_NRT
argument_list|)
expr_stmt|;
name|nodeBuilder
operator|=
name|builder
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NodeState
name|oldState
init|=
name|nodeBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|nodeBuilder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|async
argument_list|(
literal|"async"
argument_list|,
name|IndexConstants
operator|.
name|INDEXING_MODE_SYNC
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|build
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|IndexDefinitionBuilder
argument_list|(
name|oldState
operator|.
name|builder
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|async
argument_list|(
literal|"fulltext-async"
argument_list|,
name|IndexConstants
operator|.
name|INDEXING_MODE_SYNC
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|build
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propRuleCustomName
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|property
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"fooProp"
argument_list|,
literal|"foo2"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"fooProp"
argument_list|,
literal|"foo2"
argument_list|)
expr_stmt|;
name|Root
name|idx
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|idx
operator|.
name|getTree
argument_list|(
literal|"/indexRules/nt:base/properties/fooProp"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|idx
operator|.
name|getTree
argument_list|(
literal|"/indexRules/nt:base/properties/bar"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|idx
operator|.
name|getTree
argument_list|(
literal|"/indexRules/nt:base/properties/foo"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

