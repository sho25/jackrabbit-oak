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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|memory
operator|.
name|MemoryNodeStore
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
name|CommitInfo
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
name|EmptyHook
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
name|NodeStore
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

begin_class
specifier|public
class|class
name|AsyncIndexUpdateTest
block|{
comment|// TODO test index config deletes
specifier|private
specifier|static
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
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|lookup
operator|.
name|query
argument_list|(
literal|null
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
comment|/**      * Async Index Test      *<ul>      *<li>Add an index definition</li>      *<li>Add some content</li>      *<li>Search& verify</li>      *</ul>      *       */
annotation|@
name|Test
specifier|public
name|void
name|testAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|IndexEditorProvider
name|provider
init|=
operator|new
name|PropertyIndexEditorProvider
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
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
comment|// merge it back in
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|async
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
literal|"async"
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
decl_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// first check that the index content nodes exist
name|checkPathExists
argument_list|(
name|root
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"rootIndex"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|":conflict"
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyIndexLookup
name|lookup
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|root
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
comment|/**      * Async Index Test with 2 index defs at the same location      *<ul>      *<li>Add an index definition</li>      *<li>Add some content</li>      *<li>Search& verify</li>      *</ul>      *       */
annotation|@
name|Test
specifier|public
name|void
name|testAsyncDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|IndexEditorProvider
name|provider
init|=
operator|new
name|PropertyIndexEditorProvider
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
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
literal|"rootIndexSecond"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
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
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"def"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testSecond"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"ghi"
argument_list|)
expr_stmt|;
comment|// merge it back in
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|async
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
literal|"async"
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
decl_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// first check that the index content nodes exist
name|checkPathExists
argument_list|(
name|root
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
name|root
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"rootIndexSecond"
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
name|root
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
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"def"
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
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"ghi"
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
name|lookup
argument_list|,
literal|"bar"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"bar"
argument_list|,
literal|"def"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testSecond"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"bar"
argument_list|,
literal|"ghi"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Async Index Test with 2 index defs at different tree locations      *<ul>      *<li>Add an index definition</li>      *<li>Add some content</li>      *<li>Search& verify</li>      *</ul>      *       */
annotation|@
name|Test
specifier|public
name|void
name|testAsyncDoubleSubtree
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|IndexEditorProvider
name|provider
init|=
operator|new
name|PropertyIndexEditorProvider
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
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
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
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
comment|// merge it back in
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|async
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
literal|"async"
argument_list|,
name|store
argument_list|,
name|provider
argument_list|)
decl_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// first check that the index content nodes exist
name|checkPathExists
argument_list|(
name|root
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
name|root
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
name|root
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
name|root
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
block|}
end_class

end_unit

