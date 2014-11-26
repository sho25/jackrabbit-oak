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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|fulltext
operator|.
name|FullTextParser
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
name|query
operator|.
name|QueryIndex
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
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_STRING
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
name|JCR_SYSTEM
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|ORDERED_PROP_NAMES
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
name|VERSION
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
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLuceneIndexDefinition
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
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLucenePropertyIndexDefinition
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
name|PropertyStates
operator|.
name|createProperty
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
name|JCR_NODE_TYPES
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
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|spi
operator|.
name|query
operator|.
name|QueryIndex
operator|.
name|OrderEntry
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
name|assertNull
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
name|IndexPlannerTest
block|{
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|planForSortField
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|defn
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|ORDERED_PROP_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|OrderEntry
argument_list|(
literal|"foo"
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fullTextQueryNonFulltextIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setFullTextConstraint
argument_list|(
name|FullTextParser
operator|.
name|parse
argument_list|(
literal|"."
argument_list|,
literal|"mountain"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noApplicableRule
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|defn
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|of
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|=
name|createFilter
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeInheritance
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Index if for nt:hierarchyNode and query is for nt:folder
comment|//as nt:folder extends nt:hierarchyNode we should get a plan
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|defn
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|of
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:folder"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noMatchingProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"bar"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|matchingProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|defn
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
argument_list|,
literal|"test"
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defn
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|QueryIndex
operator|.
name|IndexPlan
name|plan
init|=
name|planner
operator|.
name|getPlan
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pr
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pr
argument_list|(
name|plan
argument_list|)
operator|.
name|evaluateNonFullTextConstraints
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|worksWithIndexFormatV2Onwards
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
decl_stmt|;
comment|//Dummy data node to ensure that IndexDefinition does not consider it
comment|//as a fresh indexing case
name|nb
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
expr_stmt|;
name|IndexNode
name|node
init|=
name|createIndexNode
argument_list|(
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|nb
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setFullTextConstraint
argument_list|(
name|FullTextParser
operator|.
name|parse
argument_list|(
literal|"."
argument_list|,
literal|"mountain"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexPlanner
name|planner
init|=
operator|new
name|IndexPlanner
argument_list|(
name|node
argument_list|,
literal|"/foo"
argument_list|,
name|filter
argument_list|,
name|Collections
operator|.
expr|<
name|OrderEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|planner
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexNode
name|createIndexNode
parameter_list|(
name|IndexDefinition
name|defn
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexNode
argument_list|(
literal|"foo"
argument_list|,
name|defn
argument_list|,
name|createSampleDirectory
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeState
name|system
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|NodeState
name|types
init|=
name|system
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|NodeState
name|type
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Directory
name|createSampleDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|LuceneIndexConstants
operator|.
name|ANALYZER
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir
return|;
block|}
specifier|private
specifier|static
name|IndexPlanner
operator|.
name|PlanResult
name|pr
parameter_list|(
name|QueryIndex
operator|.
name|IndexPlan
name|plan
parameter_list|)
block|{
return|return
operator|(
name|IndexPlanner
operator|.
name|PlanResult
operator|)
name|plan
operator|.
name|getAttribute
argument_list|(
name|LucenePropertyIndex
operator|.
name|ATTR_PLAN_RESULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

