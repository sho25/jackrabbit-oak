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
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
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
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|assertTrue
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|property
operator|.
name|PropertyIndexLookup
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
name|commit
operator|.
name|EditorHook
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
name|junit
operator|.
name|Test
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
name|ImmutableSet
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
name|Sets
import|;
end_import

begin_class
specifier|public
class|class
name|IndexUpdateTest
block|{
specifier|private
specifier|static
specifier|final
name|EditorHook
name|HOOK
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|EMPTY_NODE
argument_list|)
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
comment|/**      * Simple Test      *<ul>      *<li>Add an index definition</li>      *<li>Add some content</li>      *<li>Search& verify</li>      *</ul>      *       */
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"rootIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"newchild"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"subIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add nodes
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"newchild"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"testChild"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
comment|// first check that the index content nodes exist
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"rootIndex"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
literal|"newchild"
argument_list|,
literal|"other"
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"subIndex"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|PropertyIndexLookup
name|lookup
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|indexed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testRoot"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyIndexLookup
name|lookupChild
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|indexed
operator|.
name|getChildNode
argument_list|(
literal|"newchild"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"other"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testChild"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookupChild
argument_list|,
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookupChild
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reindex Test      *<ul>      *<li>Add some content</li>      *<li>Add an index definition with the reindex flag set</li>      *<li>Search& verify</li>      *</ul>      */
annotation|@
name|Test
specifier|public
name|void
name|testReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"rootIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
comment|// first check that the index content nodes exist
name|NodeState
name|ns
init|=
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"rootIndex"
argument_list|)
decl_stmt|;
name|checkPathExists
argument_list|(
name|ns
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|ns
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
comment|// next, lookup
name|PropertyIndexLookup
name|lookup
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|indexed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testRoot"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reindex Test      *<ul>      *<li>Add some content& an index definition</li>      *<li>Update the index def by setting the reindex flag to true</li>      *<li>Search& verify</li>      *</ul>      */
annotation|@
name|Test
specifier|public
name|void
name|testReindex2
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"rootIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"reindex"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"rootIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
comment|// first check that the index content nodes exist
name|NodeState
name|ns
init|=
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"rootIndex"
argument_list|)
decl_stmt|;
name|checkPathExists
argument_list|(
name|ns
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|ns
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
comment|// next, lookup
name|PropertyIndexLookup
name|lookup
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|indexed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testRoot"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIndexDefinitions
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"existing"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeBuilder
name|other
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
decl_stmt|;
comment|// Add index definition
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
name|other
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"index2"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
comment|// check that the index content nodes exist
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"existing"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
literal|"test"
argument_list|,
literal|"other"
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"index2"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|find
parameter_list|(
name|PropertyIndexLookup
name|lookup
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
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
name|NT_BASE
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
name|NT_BASE
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM [nt:base]"
argument_list|)
decl_stmt|;
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|name
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|checkPathExists
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
modifier|...
name|verify
parameter_list|)
block|{
name|NodeState
name|c
init|=
name|state
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|verify
control|)
block|{
name|c
operator|=
name|c
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

