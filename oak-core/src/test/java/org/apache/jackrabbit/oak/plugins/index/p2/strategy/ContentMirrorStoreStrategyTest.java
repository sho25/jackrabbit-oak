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
name|p2
operator|.
name|strategy
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|Sets
import|;
end_import

begin_class
specifier|public
class|class
name|ContentMirrorStoreStrategyTest
block|{
comment|/**      *<p>      * Tests the index pruning mechanism      *</p>      *<ul>      *<li>      * adds a few levels of nodes, nodes with an even index will have the      * 'match' property set</li>      *       *<li>      * pruning in this case means that whatever path that doesn't have a 'match'      * property is considered dead weight and should be removed from the index</li>      *</ul>      */
annotation|@
name|Test
specifier|public
name|void
name|testIndexPruning
parameter_list|()
throws|throws
name|Exception
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
name|store
operator|.
name|insert
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
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
argument_list|)
expr_stmt|;
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
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|)
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
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"d/e"
argument_list|)
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
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"d/e/f"
argument_list|)
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
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a/b/d"
argument_list|,
literal|"a/b"
argument_list|)
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
name|insert
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"d/e/f"
argument_list|,
literal|"b"
argument_list|,
literal|"a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove the root key when the index is empty
name|store
operator|.
name|remove
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|)
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
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
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
name|index
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|store
operator|.
name|insert
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|insert
argument_list|(
name|index
argument_list|,
literal|"key"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"b"
argument_list|)
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
name|index
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
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
block|}
end_class

end_unit

