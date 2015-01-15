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
name|property
operator|.
name|strategy
package|;
end_package

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
name|ENTRY_COUNT_PROPERTY_NAME
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
name|KEY_COUNT_PROPERTY_NAME
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
name|counter
operator|.
name|NodeCounterEditor
operator|.
name|COUNT_PROPERTY_NAME
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
name|counter
operator|.
name|NodeCounterEditor
operator|.
name|DEFAULT_RESOLUTION
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
name|util
operator|.
name|ApproximateCounter
operator|.
name|COUNT_PROPERTY_PREFIX
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
name|CommitFailedException
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
name|Assert
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

begin_comment
comment|/**  * Test the content mirror strategy  */
end_comment

begin_class
specifier|public
class|class
name|ContentMirrorStoreStrategyTest
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|EMPTY
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|KEY
init|=
name|newHashSet
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
comment|/**      *<p>      * Tests the index pruning mechanism      *</p>      *<ul>      *<li>      * adds a few levels of nodes, nodes with an even index will have the      * 'match' property set</li>      *      *<li>      * pruning in this case means that whatever path that doesn't have a 'match'      * property is considered dead weight and should be removed from the index</li>      *</ul>      */
annotation|@
name|Test
specifier|public
name|void
name|testIndexPruning
parameter_list|()
block|{
name|IndexStoreStrategy
name|store
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"a/b/c"
argument_list|,
literal|"a/b/d"
argument_list|,
literal|"b"
argument_list|,
literal|"d/e"
argument_list|,
literal|"d/e/f"
argument_list|)
control|)
block|{
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EMPTY
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
block|}
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"a/b/c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"a/b/d"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"d/e"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"d/e/f"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// remove the root key, removes just the "match" property, when the
comment|// index is not empty
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"d/e/f"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// removing intermediary path doesn't remove the entire subtree
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"d/e"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"d/e/f"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// removing intermediary path doesn't remove the entire subtree
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"d/e/f"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|checkNotPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
comment|// brother segment removed
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"a/b/d"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"a/b"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
literal|"a/b/c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// reinsert root and remove everything else
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EMPTY
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"d/e/f"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"b"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"a/b/c"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
comment|// remove the root key when the index is empty
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|KEY
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|index
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkPath
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkMatch
parameter_list|)
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|NodeBuilder
name|check
init|=
name|node
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Missing child node "
operator|+
name|p
operator|+
literal|" on path "
operator|+
name|path
argument_list|,
name|check
operator|.
name|hasChildNode
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|check
operator|=
name|check
operator|.
name|child
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkMatch
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|check
operator|.
name|hasProperty
argument_list|(
literal|"match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|checkNotPath
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|NodeBuilder
name|check
init|=
name|node
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Missing child node "
operator|+
name|p
operator|+
literal|" on path "
operator|+
name|path
argument_list|,
name|check
operator|.
name|hasChildNode
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|check
operator|=
name|check
operator|.
name|child
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|check
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnique
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|IndexStoreStrategy
name|store
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|indexMeta
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|indexMeta
operator|.
name|child
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"a"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EMPTY
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
name|store
operator|.
name|update
argument_list|(
name|index
argument_list|,
literal|"b"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EMPTY
argument_list|,
name|KEY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"ContentMirrorStoreStrategy should guarantee uniqueness on insert"
argument_list|,
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"key"
argument_list|)
argument_list|,
literal|2
argument_list|)
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIndexCountersUsageWithoutPathRestriction
parameter_list|()
block|{
specifier|final
name|long
name|approxNodeCount
init|=
literal|50
decl_stmt|;
specifier|final
name|long
name|approxKeyCount
init|=
literal|25
decl_stmt|;
specifier|final
name|long
name|entryCount
init|=
literal|30
operator|*
name|DEFAULT_RESOLUTION
decl_stmt|;
specifier|final
name|long
name|keyCount
init|=
literal|75
decl_stmt|;
specifier|final
name|int
name|maxTraversal
init|=
literal|200
decl_stmt|;
specifier|final
name|String
name|keyValue
init|=
name|KEY
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|approxPropName
init|=
name|COUNT_PROPERTY_PREFIX
operator|+
literal|"gen_uuid"
decl_stmt|;
name|IndexStoreStrategy
name|store
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|indexMeta
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|indexMeta
operator|.
name|child
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|key
init|=
name|index
operator|.
name|child
argument_list|(
name|keyValue
argument_list|)
decl_stmt|;
comment|// is-not-null query without entryCount
name|index
operator|.
name|setProperty
argument_list|(
name|approxPropName
argument_list|,
name|approxNodeCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Approximate count not used for is-not-null query"
argument_list|,
name|approxNodeCount
argument_list|,
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query without entryCount
name|key
operator|.
name|setProperty
argument_list|(
name|approxPropName
argument_list|,
name|approxKeyCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Approximate count not used for key=value query"
argument_list|,
name|approxKeyCount
argument_list|,
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// is-not-null query with entryCount
name|indexMeta
operator|.
name|setProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|,
name|entryCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Entry count not used even when present for is-not-null query"
argument_list|,
name|entryCount
argument_list|,
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query with entryCount but without keyCount
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Rough key count not considered for key=value query"
argument_list|,
name|entryCount
operator|>
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query with entryCount and keyCount
name|indexMeta
operator|.
name|setProperty
argument_list|(
name|KEY_COUNT_PROPERTY_NAME
argument_list|,
name|keyCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Key count not considered for key=value query"
argument_list|,
name|entryCount
operator|>
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// is-not-null query with entryCount=-1 (this should lead to traversal
comment|// and hence should result in '0'
name|indexMeta
operator|.
name|setProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|,
operator|(
name|long
operator|)
operator|-
literal|1
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Entry count not used even when present for is-not-null query"
argument_list|,
literal|0
argument_list|,
name|store
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2341"
argument_list|)
specifier|public
name|void
name|testIndexCountersUsageWithPathRestriction
parameter_list|()
block|{
specifier|final
name|String
name|subPathName
init|=
literal|"sub-path"
decl_stmt|;
specifier|final
name|int
name|filteredNodeFactor
init|=
literal|2
decl_stmt|;
specifier|final
name|long
name|repoTreeApproxNodeCount
init|=
literal|50000
decl_stmt|;
specifier|final
name|long
name|repoSubPathApproxNodeCount
init|=
name|repoTreeApproxNodeCount
operator|/
name|filteredNodeFactor
decl_stmt|;
specifier|final
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|()
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/"
operator|+
name|subPathName
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
specifier|final
name|long
name|approxNodeCount
init|=
literal|100
decl_stmt|;
specifier|final
name|long
name|approxKeyCount
init|=
literal|50
decl_stmt|;
specifier|final
name|long
name|entryCount
init|=
literal|60
operator|*
name|DEFAULT_RESOLUTION
decl_stmt|;
specifier|final
name|long
name|keyCount
init|=
literal|150
decl_stmt|;
specifier|final
name|int
name|maxTraversal
init|=
literal|200
decl_stmt|;
specifier|final
name|String
name|keyValue
init|=
name|KEY
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|approxPropName
init|=
name|COUNT_PROPERTY_PREFIX
operator|+
literal|"gen_uuid"
decl_stmt|;
name|IndexStoreStrategy
name|store
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// setup tree for NodeCounter to work
name|rootBuilder
operator|.
name|setProperty
argument_list|(
name|COUNT_PROPERTY_NAME
argument_list|,
name|repoTreeApproxNodeCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|NodeBuilder
name|subPath
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|subPathName
argument_list|)
decl_stmt|;
name|subPath
operator|.
name|setProperty
argument_list|(
name|COUNT_PROPERTY_NAME
argument_list|,
name|repoSubPathApproxNodeCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeBuilder
name|indexMeta
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"propIndex"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|indexMeta
operator|.
name|child
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|key
init|=
name|index
operator|.
name|child
argument_list|(
name|keyValue
argument_list|)
decl_stmt|;
comment|// is-not-null query without entryCount
name|index
operator|.
name|setProperty
argument_list|(
name|approxPropName
argument_list|,
name|approxNodeCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertInRange
argument_list|(
literal|"Approximate count not used for is-not-null query"
argument_list|,
name|approxNodeCount
argument_list|,
name|filteredNodeFactor
operator|*
name|store
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query without entryCount
name|key
operator|.
name|setProperty
argument_list|(
name|approxPropName
argument_list|,
name|approxKeyCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertInRange
argument_list|(
literal|"Approximate count not used for key=value query"
argument_list|,
name|approxKeyCount
argument_list|,
name|filteredNodeFactor
operator|*
name|store
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// is-not-null query with entryCount
name|indexMeta
operator|.
name|setProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|,
name|entryCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertInRange
argument_list|(
literal|"Entry count not used even when present for is-not-null query"
argument_list|,
name|entryCount
argument_list|,
name|filteredNodeFactor
operator|*
name|store
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|null
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query with entryCount but without keyCount
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Rough key count not considered for key=value query"
argument_list|,
name|entryCount
operator|>
name|filteredNodeFactor
operator|*
name|store
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
comment|// prop=value query with entryCount and keyCount
name|indexMeta
operator|.
name|setProperty
argument_list|(
name|KEY_COUNT_PROPERTY_NAME
argument_list|,
name|keyCount
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Key count not considered for key=value query"
argument_list|,
name|entryCount
operator|>
name|filteredNodeFactor
operator|*
name|store
operator|.
name|count
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|indexMeta
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|KEY
argument_list|,
name|maxTraversal
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertInRange
parameter_list|(
name|String
name|msg
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|actual
parameter_list|)
block|{
specifier|final
name|double
name|allowedError
init|=
literal|0.1
decl_stmt|;
name|double
name|diff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|expected
operator|-
name|actual
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|diff
operator|<
name|expected
operator|*
name|allowedError
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

