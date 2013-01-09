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
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
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
name|TreeMap
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
comment|/**  * TODO document  */
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
name|remove
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
operator|!
name|index
operator|.
name|hasChildNode
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return;
block|}
name|NodeBuilder
name|child
init|=
name|index
operator|.
name|child
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NodeBuilder
argument_list|>
name|parents
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|NodeBuilder
argument_list|>
argument_list|(
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|rm
range|:
name|values
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|rm
argument_list|)
condition|)
block|{
name|child
operator|.
name|removeProperty
argument_list|(
literal|"match"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|rm
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|rm
argument_list|)
decl_stmt|;
name|NodeBuilder
name|indexEntry
init|=
name|parents
operator|.
name|get
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexEntry
operator|==
literal|null
condition|)
block|{
name|indexEntry
operator|=
name|child
expr_stmt|;
name|String
name|segmentPath
init|=
literal|""
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|segments
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|parentPath
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|segments
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|segment
init|=
name|segments
operator|.
name|next
argument_list|()
decl_stmt|;
name|segmentPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|segmentPath
argument_list|,
name|segment
argument_list|)
expr_stmt|;
name|indexEntry
operator|=
name|indexEntry
operator|.
name|child
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|parents
operator|.
name|put
argument_list|(
name|segmentPath
argument_list|,
name|indexEntry
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexEntry
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|childEntry
init|=
name|indexEntry
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|childEntry
operator|.
name|removeProperty
argument_list|(
literal|"match"
argument_list|)
expr_stmt|;
if|if
condition|(
name|childEntry
operator|.
name|getChildNodeCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|indexEntry
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// prune the index: remove all children that have no children
comment|// and no "match" property progressing bottom up
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|parents
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeBuilder
name|parent
init|=
name|parents
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|pruneNode
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|// finally prune the index node
name|pruneNode
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|getChildNodeCount
argument_list|()
operator|==
literal|0
operator|&&
name|child
operator|.
name|getProperty
argument_list|(
literal|"match"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|index
operator|.
name|removeNode
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|pruneNode
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|.
name|isRemoved
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|name
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|segment
init|=
name|parent
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|getChildNodeCount
argument_list|()
operator|==
literal|0
operator|&&
name|segment
operator|.
name|getProperty
argument_list|(
literal|"match"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|parent
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|insert
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|child
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
name|add
range|:
name|values
control|)
block|{
name|NodeBuilder
name|indexEntry
init|=
name|child
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|add
argument_list|)
control|)
block|{
name|indexEntry
operator|=
name|indexEntry
operator|.
name|child
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
name|indexEntry
operator|.
name|setProperty
argument_list|(
literal|"match"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|long
name|matchCount
init|=
name|countMatchingLeaves
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchCount
operator|==
literal|0
condition|)
block|{
name|index
operator|.
name|removeNode
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|unique
operator|&&
name|matchCount
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Uniqueness constraint violated"
argument_list|)
throw|;
block|}
block|}
specifier|static
name|int
name|countMatchingLeaves
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|getProperty
argument_list|(
literal|"match"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
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
name|count
operator|+=
name|countMatchingLeaves
argument_list|(
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|find
parameter_list|(
name|NodeState
name|index
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|index
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|getMatchingPaths
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|""
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
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
operator|!=
literal|null
condition|)
block|{
comment|// We have an entry for this value, so use it
name|getMatchingPaths
argument_list|(
name|property
argument_list|,
literal|""
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|paths
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
name|String
name|indexName
parameter_list|,
specifier|final
name|NodeState
name|index
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
operator|!=
literal|null
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
comment|// avoid duplicate entries
comment|// TODO load entries lazily
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Iterators
operator|.
name|addAll
argument_list|(
name|paths
argument_list|,
name|it
argument_list|)
expr_stmt|;
return|return
name|paths
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
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
name|PathIterator
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
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
literal|100
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
name|matches
argument_list|(
name|node
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
name|result
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|value
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|void
name|getMatchingPaths
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|c
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|c
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|childState
init|=
name|c
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|getMatchingPaths
argument_list|(
name|childState
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|boolean
name|matches
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|state
operator|.
name|getProperty
argument_list|(
literal|"match"
argument_list|)
decl_stmt|;
return|return
name|ps
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isArray
argument_list|()
operator|&&
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|(
name|NodeState
name|index
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
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
name|count
operator|+=
name|countMatchingLeaves
argument_list|(
name|index
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
operator|!=
literal|null
condition|)
block|{
name|count
operator|+=
name|countMatchingLeaves
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

