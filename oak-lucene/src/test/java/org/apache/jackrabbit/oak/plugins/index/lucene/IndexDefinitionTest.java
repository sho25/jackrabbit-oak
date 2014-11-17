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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|commons
operator|.
name|PathUtils
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
name|ImmutableTree
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
name|codecs
operator|.
name|Codec
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
name|IndexDefinition
operator|.
name|IndexingRule
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|TYPENAME_LONG
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
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|NAMES
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_NAMES
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
name|INCLUDE_PROPERTY_TYPES
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
name|PROP_NAME
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
name|PROP_NODE
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
name|plugins
operator|.
name|tree
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
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
name|IndexDefinitionTest
block|{
specifier|private
name|Codec
name|oakCodec
init|=
operator|new
name|OakCodec
argument_list|()
decl_stmt|;
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
name|fullTextEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule
init|=
name|idxDefn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"By default fulltext is enabled"
argument_list|,
name|idxDefn
operator|.
name|isFullTextEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"By default everything is indexed"
argument_list|,
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Property types need to be defined"
argument_list|,
name|rule
operator|.
name|includePropertyType
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"For fulltext storage is enabled"
argument_list|,
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|stored
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|skipTokenization
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|getConfig
argument_list|(
literal|"jcr:uuid"
argument_list|)
operator|.
name|skipTokenization
argument_list|(
literal|"jcr:uuid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|,
name|of
argument_list|(
name|TYPENAME_LONG
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FULL_TEXT_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule
init|=
name|idxDefn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|idxDefn
operator|.
name|isFullTextEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"If fulltext disabled then nothing stored"
argument_list|,
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|stored
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|includePropertyType
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rule
operator|.
name|includePropertyType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|skipTokenization
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyDefinition
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_DATE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule
init|=
name|idxDefn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyDefinitionWithExcludes
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_DATE
argument_list|)
expr_stmt|;
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule
init|=
name|idxDefn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule
operator|.
name|isIndexed
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
name|rule
operator|.
name|getConfig
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|codecConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oakCodec
operator|.
name|getName
argument_list|()
argument_list|,
name|defn
operator|.
name|getCodec
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FULL_TEXT_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|defn
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|defn
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
name|Codec
name|simple
init|=
name|Codec
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|CODEC_NAME
argument_list|,
name|simple
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|defn
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|simple
operator|.
name|getName
argument_list|()
argument_list|,
name|defn
operator|.
name|getCodec
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
name|relativeProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"foo1/bar"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexDefinition
name|idxDefn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule
init|=
name|idxDefn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rule
operator|.
name|getRelativeProps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo1/bar"
argument_list|,
name|Iterables
operator|.
name|getFirst
argument_list|(
name|rule
operator|.
name|getRelativeProps
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|propertyPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|idxDefn
operator|.
name|hasRelativeProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|idxDefn
operator|.
name|hasRelativeProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|relativePropertyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo1"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_DATE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_LONG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"foo1/bar"
argument_list|,
literal|"foo2/bar2/baz"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|defn
operator|.
name|getRelativeProps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|defn
operator|.
name|getPropDefn
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getPropDefn
argument_list|(
literal|"foo1/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
name|defn
operator|.
name|getPropDefn
argument_list|(
literal|"foo1/bar"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|,
name|defn
operator|.
name|getPropDefn
argument_list|(
literal|"foo2/bar2/baz"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleSanity
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:folder"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|rules
argument_list|,
literal|"nt:folder/properties/prop1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|3.0
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_BOOLEAN
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IndexingRule
name|rule1
init|=
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rule1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0f
argument_list|,
name|rule1
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyDefinition
name|pd
init|=
name|rule1
operator|.
name|getConfig
argument_list|(
literal|"prop1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3.0f
argument_list|,
name|pd
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|BOOLEAN
argument_list|,
name|pd
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleInheritance
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_NAME
argument_list|,
literal|"testIndex"
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO Inheritance and mixin
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleInheritanceDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|PROP_NAME
argument_list|,
literal|"testIndex"
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|2.0
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|RULE_INHERITED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"nt:folder should not be index as rule is not inheritable"
argument_list|,
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleInheritanceOrdering
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"nt:hierarchyNode"
argument_list|,
literal|"nt:base"
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:base"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:query"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleInheritanceOrdering2
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"nt:base"
argument_list|,
literal|"nt:hierarchyNode"
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
comment|//As nt:base is defined earlier it would supercede everything
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:base"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:hierarchyNode"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|getRule
argument_list|(
name|defn
argument_list|,
literal|"nt:file"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleWithPropertyRegEx
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|rules
argument_list|,
literal|"nt:folder/properties/prop1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|rules
argument_list|,
literal|"nt:folder/properties/prop2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NAME
argument_list|,
literal|"foo.*"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule1
init|=
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rule1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"fooProp"
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyDefinition
name|pd
init|=
name|rule1
operator|.
name|getConfig
argument_list|(
literal|"fooProp2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4.0f
argument_list|,
name|pd
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRuleWithPropertyOrdering
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|rules
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|rules
argument_list|,
literal|"nt:folder/properties/prop1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NAME
argument_list|,
literal|"foo.*"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|rules
argument_list|,
literal|"nt:folder/properties/prop2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NAME
argument_list|,
literal|".*"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FIELD_BOOST
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
name|rules
operator|.
name|child
argument_list|(
literal|"nt:folder"
argument_list|)
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"prop2"
argument_list|,
literal|"prop1"
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|IndexingRule
name|rule1
init|=
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rule1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"prop1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rule1
operator|.
name|isIndexed
argument_list|(
literal|"fooProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.0f
argument_list|,
name|rule1
operator|.
name|getConfig
argument_list|(
literal|"bazProp2"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//As prop2 is ordered before prop1 its regEx is evaluated first
comment|//hence even with a specific regex of foo.* the defn used is from .*
name|assertEquals
argument_list|(
literal|4.0f
argument_list|,
name|rule1
operator|.
name|getConfig
argument_list|(
literal|"fooProp"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//Order it correctly to get expected result
name|rules
operator|.
name|child
argument_list|(
literal|"nt:folder"
argument_list|)
operator|.
name|child
argument_list|(
name|PROP_NODE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"prop1"
argument_list|,
literal|"prop2"
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|defn
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|rule1
operator|=
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0f
argument_list|,
name|rule1
operator|.
name|getConfig
argument_list|(
literal|"fooProp"
argument_list|)
operator|.
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|IndexingRule
name|getRule
parameter_list|(
name|IndexDefinition
name|defn
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
return|return
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|newTree
argument_list|(
name|newNode
argument_list|(
name|typeName
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Tree
name|newTree
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|)
block|{
return|return
operator|new
name|ImmutableTree
argument_list|(
name|nb
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|newNode
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|child
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
block|}
end_class

end_unit

