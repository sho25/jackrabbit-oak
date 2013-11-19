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
name|Queues
operator|.
name|newArrayDeque
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
name|ENTRY_COUNT_PROPERTY_NAME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

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
name|memory
operator|.
name|MemoryChildNodeEntry
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
name|ChildNodeEntry
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Iterators
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
name|Queues
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

begin_comment
comment|/**  * An IndexStoreStrategy implementation that saves the nodes under a hierarchy  * that mirrors the repository tree.<br>  * This should minimize the chance that concurrent updates overlap on the same  * content node.<br>  *<br>  * For example for a node that is under {@code /test/node}, the index  * structure will be {@code /oak:index/index/test/node}:  *  *<pre>  * {@code  * /  *   test  *     node  *   oak:index  *     index  *       test  *         node  * }  *</pre>  *  */
end_comment

begin_class
specifier|public
class|class
name|ContentMirrorStoreStrategy
implements|implements
name|IndexStoreStrategy
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContentMirrorStoreStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|path
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|beforeKeys
control|)
block|{
name|remove
argument_list|(
name|index
argument_list|,
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|afterKeys
control|)
block|{
name|insert
argument_list|(
name|index
argument_list|,
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|remove
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// Collect all builders along the given path
name|Deque
argument_list|<
name|NodeBuilder
argument_list|>
name|builders
init|=
name|newArrayDeque
argument_list|()
decl_stmt|;
name|builders
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
comment|// Descend to the correct location in the index tree
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|value
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builders
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
comment|// Drop the match value,  if present
if|if
condition|(
name|builder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"match"
argument_list|)
expr_stmt|;
block|}
comment|// Prune all index nodes that are no longer needed
for|for
control|(
name|NodeBuilder
name|node
range|:
name|builders
control|)
block|{
if|if
condition|(
name|node
operator|.
name|getBoolean
argument_list|(
literal|"match"
argument_list|)
operator|||
name|node
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|exists
argument_list|()
condition|)
block|{
name|node
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|insert
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|index
operator|.
name|child
argument_list|(
name|key
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|value
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setProperty
argument_list|(
literal|"match"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|String
name|indexName
parameter_list|,
specifier|final
name|NodeState
name|indexMeta
parameter_list|,
specifier|final
name|String
name|indexStorageNodeName
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
specifier|final
name|NodeState
name|index
init|=
name|indexMeta
operator|.
name|getChildNode
argument_list|(
name|indexStorageNodeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
name|PathIterator
name|it
init|=
operator|new
name|PathIterator
argument_list|(
name|filter
argument_list|,
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|setPathContainsValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|it
operator|.
name|enqueue
argument_list|(
name|index
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|p
range|:
name|values
control|)
block|{
name|NodeState
name|property
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// we have an entry for this value, so use it
name|it
operator|.
name|enqueue
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
literal|""
argument_list|,
name|property
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|it
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|String
name|indexName
parameter_list|,
specifier|final
name|NodeState
name|indexMeta
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|filter
argument_list|,
name|indexName
argument_list|,
name|indexMeta
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|,
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|(
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|count
argument_list|(
name|indexMeta
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|,
name|values
argument_list|,
name|max
argument_list|)
return|;
block|}
specifier|public
name|long
name|count
parameter_list|(
name|NodeState
name|indexMeta
parameter_list|,
specifier|final
name|String
name|indexStorageNodeName
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|NodeState
name|index
init|=
name|indexMeta
operator|.
name|getChildNode
argument_list|(
name|indexStorageNodeName
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|PropertyState
name|ec
init|=
name|indexMeta
operator|.
name|getProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|ec
operator|!=
literal|null
condition|)
block|{
return|return
name|ec
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
name|CountingNodeVisitor
name|v
init|=
operator|new
name|CountingNodeVisitor
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|count
operator|=
name|v
operator|.
name|getEstimatedCount
argument_list|()
expr_stmt|;
comment|// "is not null" queries typically read more data
name|count
operator|*=
literal|10
expr_stmt|;
block|}
else|else
block|{
name|int
name|size
init|=
name|values
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|10
argument_list|,
name|max
operator|/
name|size
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|values
control|)
block|{
if|if
condition|(
name|count
operator|>
name|max
operator|&&
name|i
operator|>
literal|3
condition|)
block|{
comment|// the total count is extrapolated from the the number
comment|// of values counted so far to the total number of values
name|count
operator|=
name|count
operator|*
name|size
operator|/
name|i
expr_stmt|;
break|break;
block|}
name|NodeState
name|s
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
name|CountingNodeVisitor
name|v
init|=
operator|new
name|CountingNodeVisitor
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|count
operator|+=
name|v
operator|.
name|getEstimatedCount
argument_list|()
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * An iterator over paths within an index node.      */
specifier|static
class|class
name|PathIterator
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
argument_list|>
name|nodeIterators
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|()
decl_stmt|;
specifier|private
name|int
name|readCount
decl_stmt|;
specifier|private
name|boolean
name|init
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|String
name|parentPath
decl_stmt|;
specifier|private
name|String
name|currentPath
decl_stmt|;
specifier|private
name|boolean
name|pathContainsValue
decl_stmt|;
comment|/**          * Keep the returned path, to avoid returning duplicate entries.          */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|knownPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|PathIterator
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
name|parentPath
operator|=
literal|""
expr_stmt|;
name|currentPath
operator|=
literal|"/"
expr_stmt|;
block|}
name|void
name|enqueue
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|it
parameter_list|)
block|{
name|nodeIterators
operator|.
name|addLast
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
name|void
name|setPathContainsValue
parameter_list|(
name|boolean
name|pathContainsValue
parameter_list|)
block|{
if|if
condition|(
name|init
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This iterator is already initialized"
argument_list|)
throw|;
block|}
name|this
operator|.
name|pathContainsValue
operator|=
name|pathContainsValue
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
operator|&&
operator|!
name|init
condition|)
block|{
name|fetchNext
argument_list|()
expr_stmt|;
name|init
operator|=
literal|true
expr_stmt|;
block|}
return|return
operator|!
name|closed
return|;
block|}
specifier|private
name|void
name|fetchNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|fetchNextPossiblyDuplicate
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|pathContainsValue
condition|)
block|{
name|String
name|value
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|currentPath
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|currentPath
operator|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|value
argument_list|,
name|currentPath
argument_list|)
expr_stmt|;
comment|// don't return duplicate paths:
comment|// Set.add returns true if the entry was new,
comment|// so if it returns false, it was already known
if|if
condition|(
operator|!
name|knownPaths
operator|.
name|add
argument_list|(
name|currentPath
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
break|break;
block|}
block|}
specifier|private
name|void
name|fetchNextPossiblyDuplicate
parameter_list|()
block|{
while|while
condition|(
operator|!
name|nodeIterators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|iterator
init|=
name|nodeIterators
operator|.
name|getLast
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ChildNodeEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|readCount
operator|++
expr_stmt|;
if|if
condition|(
name|readCount
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Traversed "
operator|+
name|readCount
operator|+
literal|" nodes using index "
operator|+
name|indexName
operator|+
literal|" with filter "
operator|+
name|filter
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|node
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|currentPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|nodeIterators
operator|.
name|addLast
argument_list|(
name|node
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|parentPath
operator|=
name|currentPath
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getBoolean
argument_list|(
literal|"match"
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
name|nodeIterators
operator|.
name|removeLast
argument_list|()
expr_stmt|;
name|parentPath
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|parentPath
argument_list|)
expr_stmt|;
block|}
block|}
name|currentPath
operator|=
literal|null
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This iterator is closed"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|init
condition|)
block|{
name|fetchNext
argument_list|()
expr_stmt|;
name|init
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|result
init|=
name|currentPath
decl_stmt|;
name|fetchNext
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/**      * A node visitor to recursively traverse a number of nodes.      */
interface|interface
name|NodeVisitor
block|{
name|void
name|visit
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
block|}
comment|/**      * A node visitor that counts the number of matching nodes up to a given      * maximum, in order to estimate the number of matches.      */
specifier|static
class|class
name|CountingNodeVisitor
implements|implements
name|NodeVisitor
block|{
comment|/**          * The maximum number of matching nodes to count.          */
specifier|final
name|int
name|maxCount
decl_stmt|;
comment|/**          * The current count of matching nodes.          */
name|int
name|count
decl_stmt|;
comment|/**          * The current depth (number of parent nodes).          */
name|int
name|depth
decl_stmt|;
comment|/**          * The total number of child nodes per node, for those nodes that were          * fully traversed and do have child nodes. This value is used to          * calculate the average width.          */
name|long
name|widthTotal
decl_stmt|;
comment|/**          * The number of nodes that were fully traversed and do have child          * nodes. This value is used to calculate the average width.          */
name|int
name|widthCount
decl_stmt|;
comment|/**          * The sum of the depth of all matching nodes. This value is used to          * calculate the average depth.          */
name|long
name|depthTotal
decl_stmt|;
name|CountingNodeVisitor
parameter_list|(
name|int
name|maxCount
parameter_list|)
block|{
name|this
operator|.
name|maxCount
operator|=
name|maxCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
literal|"match"
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|depthTotal
operator|+=
name|depth
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|<
name|maxCount
condition|)
block|{
name|depth
operator|++
expr_stmt|;
name|int
name|width
init|=
literal|0
decl_stmt|;
name|boolean
name|finished
init|=
literal|true
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
if|if
condition|(
name|count
operator|>=
name|maxCount
condition|)
block|{
name|finished
operator|=
literal|false
expr_stmt|;
break|break;
block|}
name|width
operator|++
expr_stmt|;
name|visit
argument_list|(
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|finished
operator|&&
name|width
operator|>
literal|0
condition|)
block|{
name|widthTotal
operator|+=
name|width
expr_stmt|;
name|widthCount
operator|++
expr_stmt|;
block|}
name|depth
operator|--
expr_stmt|;
block|}
block|}
comment|/**          * The number of matches (at most the maximum count).          *           * @return the match count          */
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**          * The number of estimated matches. This value might be higher than the          * number of counted matches, if the maximum number of matches has been          * reached. It is based on the average depth of matches, and the average          * number of child nodes.          *           * @return the estimated matches          */
name|int
name|getEstimatedCount
parameter_list|()
block|{
if|if
condition|(
name|count
operator|<
name|maxCount
condition|)
block|{
return|return
name|count
return|;
block|}
name|double
name|averageDepth
init|=
call|(
name|int
call|)
argument_list|(
name|depthTotal
operator|/
name|count
argument_list|)
decl_stmt|;
name|double
name|averageWidth
init|=
literal|2
decl_stmt|;
if|if
condition|(
name|widthCount
operator|>
literal|0
condition|)
block|{
name|averageWidth
operator|=
call|(
name|int
call|)
argument_list|(
name|widthTotal
operator|/
name|widthCount
argument_list|)
expr_stmt|;
block|}
comment|// calculate with an average width of at least 2
name|averageWidth
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
name|averageWidth
argument_list|)
expr_stmt|;
comment|// the number of estimated matches is calculated as the
comment|// of a estimated
name|long
name|estimatedNodes
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|pow
argument_list|(
name|averageWidth
argument_list|,
literal|2
operator|*
name|averageDepth
argument_list|)
decl_stmt|;
name|estimatedNodes
operator|=
name|Math
operator|.
name|min
argument_list|(
name|estimatedNodes
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|count
argument_list|,
operator|(
name|int
operator|)
name|estimatedNodes
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

