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
name|spi
operator|.
name|query
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|query
operator|.
name|FilterIterators
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
name|index
operator|.
name|IndexRowImpl
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
operator|.
name|PathRestriction
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicate
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
name|checkArgument
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
operator|.
name|isAbsolute
import|;
end_import

begin_comment
comment|/**  * This utility class provides factory methods to create commonly used types of  * {@link Cursor}s.  */
end_comment

begin_class
specifier|public
class|class
name|Cursors
block|{
specifier|private
name|Cursors
parameter_list|()
block|{     }
comment|/**      * Creates a {@link Cursor} over paths.      *      * @param paths the paths to iterate over (must return distinct paths)      * @return the Cursor.      */
specifier|public
specifier|static
name|Cursor
name|newPathCursor
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
return|return
operator|new
name|PathCursor
argument_list|(
name|paths
operator|.
name|iterator
argument_list|()
argument_list|,
literal|true
argument_list|,
name|settings
argument_list|)
return|;
block|}
comment|/**      * Creates a {@link Cursor} over paths, and make the result distinct.      * The iterator might return duplicate paths      *       * @param paths the paths to iterate over (might contain duplicate entries)      * @return the Cursor.      */
specifier|public
specifier|static
name|Cursor
name|newPathCursorDistinct
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
return|return
operator|new
name|PathCursor
argument_list|(
name|paths
operator|.
name|iterator
argument_list|()
argument_list|,
literal|true
argument_list|,
name|settings
argument_list|)
return|;
block|}
comment|/**      * Returns a traversing cursor based on the path restriction in the given      * {@link Filter}.      *       * @param filter the filter.      * @param rootState the root {@link NodeState}.      * @return the {@link Cursor}.      */
specifier|public
specifier|static
name|Cursor
name|newTraversingCursor
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
operator|new
name|TraversingCursor
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
return|;
block|}
comment|/**      * Returns a cursor wrapper, which returns the ancestor rows at the given      *<code>level</code> of the wrapped cursor<code>c</code>. With      *<code>level</code> e.g. set to<code>1</code>, the returned cursor      * iterates over the parent rows of the passed cursor<code>c</code>. The      * returned cursor guarantees distinct rows.      *      * @param c the cursor to wrap.      * @param level the ancestor level. Must be>= 1.      * @return cursor over the ancestors of<code>c</code> at<code>level</code>.      */
specifier|public
specifier|static
name|Cursor
name|newAncestorCursor
parameter_list|(
name|Cursor
name|c
parameter_list|,
name|int
name|level
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|level
operator|>=
literal|1
argument_list|)
expr_stmt|;
return|return
operator|new
name|AncestorCursor
argument_list|(
name|c
argument_list|,
name|level
argument_list|,
name|settings
argument_list|)
return|;
block|}
comment|/**      * A Cursor implementation where the remove method throws an      * UnsupportedOperationException.      */
specifier|public
specifier|abstract
specifier|static
class|class
name|AbstractCursor
implements|implements
name|Cursor
block|{
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
comment|/**      * This class allows to iterate over the parent nodes of the wrapped cursor.      */
specifier|private
specifier|static
class|class
name|AncestorCursor
extends|extends
name|PathCursor
block|{
specifier|public
name|AncestorCursor
parameter_list|(
name|Cursor
name|cursor
parameter_list|,
name|int
name|level
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
name|level
argument_list|)
argument_list|,
literal|true
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Iterator
argument_list|<
name|String
argument_list|>
name|transform
parameter_list|(
name|Cursor
name|cursor
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|unfiltered
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|IndexRow
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|IndexRow
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
condition|?
name|input
operator|.
name|getPath
argument_list|()
else|:
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|filtered
init|=
name|Iterators
operator|.
name|filter
argument_list|(
name|unfiltered
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
operator|&&
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|input
argument_list|)
operator|>=
name|level
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|filtered
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|input
argument_list|,
name|level
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
comment|/**      *<code>PathCursor</code> implements a simple {@link Cursor} that iterates      * over a {@link String} based path {@link Iterable}.      */
specifier|public
specifier|static
class|class
name|PathCursor
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
decl_stmt|;
specifier|public
name|PathCursor
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|boolean
name|distinct
parameter_list|,
specifier|final
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|paths
decl_stmt|;
if|if
condition|(
name|distinct
condition|)
block|{
name|it
operator|=
name|Iterators
operator|.
name|filter
argument_list|(
name|it
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|known
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|maxMemoryEntries
init|=
name|settings
operator|.
name|getLimitInMemory
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
name|FilterIterators
operator|.
name|checkMemoryLimit
argument_list|(
name|known
operator|.
name|size
argument_list|()
argument_list|,
name|maxMemoryEntries
argument_list|)
expr_stmt|;
comment|// Set.add returns true for new entries
return|return
name|known
operator|.
name|add
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|iterator
operator|=
name|it
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
comment|// TODO support jcr:score and possibly rep:excerpt
name|String
name|path
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|IndexRowImpl
argument_list|(
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|?
name|path
else|:
literal|"/"
operator|+
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
block|}
comment|/**      * A cursor that reads all nodes in a given subtree.      */
specifier|private
specifier|static
class|class
name|TraversingCursor
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TraversingCursor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
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
name|String
name|parentPath
decl_stmt|;
specifier|private
name|String
name|currentPath
decl_stmt|;
specifier|private
name|long
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
specifier|final
name|long
name|maxReadEntries
decl_stmt|;
specifier|public
name|TraversingCursor
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
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
name|maxReadEntries
operator|=
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
operator|.
name|getLimitReads
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|parentPath
operator|=
literal|null
expr_stmt|;
name|currentPath
operator|=
literal|"/"
expr_stmt|;
name|NodeState
name|parent
init|=
literal|null
decl_stmt|;
name|NodeState
name|node
init|=
name|rootState
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
comment|// nothing can match this filter, leave nodes empty
return|return;
block|}
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
control|)
block|{
name|parentPath
operator|=
name|currentPath
expr_stmt|;
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
name|parent
operator|=
name|node
expr_stmt|;
name|node
operator|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|node
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// nothing can match this filter, leave nodes empty
return|return;
block|}
block|}
name|Filter
operator|.
name|PathRestriction
name|restriction
init|=
name|filter
operator|.
name|getPathRestriction
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|restriction
condition|)
block|{
case|case
name|NO_RESTRICTION
case|:
case|case
name|EXACT
case|:
case|case
name|ALL_CHILDREN
case|:
name|nodeIterators
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|currentPath
argument_list|,
name|node
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|parentPath
operator|=
literal|""
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|nodeIterators
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|parentPath
argument_list|,
name|parent
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|parentPath
operator|=
literal|""
expr_stmt|;
block|}
break|break;
case|case
name|DIRECT_CHILDREN
case|:
name|nodeIterators
operator|.
name|add
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
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown restriction: "
operator|+
name|restriction
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
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
literal|"This cursor is closed"
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
name|IndexRowImpl
name|result
init|=
operator|new
name|IndexRowImpl
argument_list|(
name|currentPath
argument_list|)
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
literal|1000
operator|==
literal|0
condition|)
block|{
name|FilterIterators
operator|.
name|checkReadLimit
argument_list|(
name|readCount
argument_list|,
name|maxReadEntries
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Traversed "
operator|+
name|readCount
operator|+
literal|" nodes with filter "
operator|+
name|filter
operator|+
literal|"; consider creating an index or changing the query"
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
name|PathRestriction
name|r
init|=
name|filter
operator|.
name|getPathRestriction
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|PathRestriction
operator|.
name|ALL_CHILDREN
operator|||
name|r
operator|==
name|PathRestriction
operator|.
name|NO_RESTRICTION
condition|)
block|{
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
block|}
return|return;
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
block|}
block|}
end_class

end_unit

