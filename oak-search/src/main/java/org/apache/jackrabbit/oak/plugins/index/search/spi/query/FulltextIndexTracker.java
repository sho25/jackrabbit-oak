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
operator|.
name|search
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
name|List
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|ImmutableMap
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
name|commons
operator|.
name|PerfLogger
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
name|AsyncIndexInfoService
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
name|search
operator|.
name|BadIndexTracker
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
name|search
operator|.
name|IndexDefinition
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
name|search
operator|.
name|IndexNode
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
name|search
operator|.
name|util
operator|.
name|IndexHelper
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
name|CompositeEditor
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
name|DefaultEditor
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
name|Editor
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
name|EditorDiff
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
name|SubtreeEditor
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
name|EqualsDiff
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
name|base
operator|.
name|Predicates
operator|.
name|in
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
name|Predicates
operator|.
name|not
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
name|Predicates
operator|.
name|notNull
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
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|Maps
operator|.
name|filterKeys
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
name|Maps
operator|.
name|filterValues
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
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
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
name|search
operator|.
name|IndexDefinition
operator|.
name|INDEX_DEFINITION_NODE
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
name|search
operator|.
name|IndexDefinition
operator|.
name|STATUS_NODE
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

begin_class
specifier|public
specifier|abstract
class|class
name|FulltextIndexTracker
block|{
comment|/** Logger instance. */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FulltextIndexTracker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FulltextIndexTracker
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BadIndexTracker
name|badIndexTracker
init|=
operator|new
name|BadIndexTracker
argument_list|()
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
specifier|private
name|AsyncIndexInfoService
name|asyncIndexInfoService
decl_stmt|;
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNodeManager
argument_list|>
name|indices
init|=
name|emptyMap
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|refresh
decl_stmt|;
specifier|protected
specifier|abstract
name|IndexNodeManager
name|openIndex
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|node
parameter_list|)
function_decl|;
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNodeManager
argument_list|>
name|indices
init|=
name|this
operator|.
name|indices
decl_stmt|;
name|this
operator|.
name|indices
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexNodeManager
argument_list|>
name|entry
range|:
name|indices
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to close the Lucene index at "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|update
parameter_list|(
specifier|final
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|refresh
condition|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
name|refresh
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Refreshed the opened indexes"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diffAndUpdate
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setAsyncIndexInfoService
parameter_list|(
name|AsyncIndexInfoService
name|asyncIndexInfoService
parameter_list|)
block|{
name|this
operator|.
name|asyncIndexInfoService
operator|=
name|asyncIndexInfoService
expr_stmt|;
block|}
name|AsyncIndexInfoService
name|getAsyncIndexInfoService
parameter_list|()
block|{
return|return
name|asyncIndexInfoService
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|diffAndUpdate
parameter_list|(
specifier|final
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|asyncIndexInfoService
operator|!=
literal|null
operator|&&
operator|!
name|asyncIndexInfoService
operator|.
name|hasIndexerUpdatedForAnyLane
argument_list|(
name|this
operator|.
name|root
argument_list|,
name|root
argument_list|)
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"No changed detected in async indexer state. Skipping further diff"
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNodeManager
argument_list|>
name|original
init|=
name|indices
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexNodeManager
argument_list|>
name|updates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|indexPaths
operator|.
name|addAll
argument_list|(
name|original
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|indexPaths
operator|.
name|addAll
argument_list|(
name|badIndexTracker
operator|.
name|getIndexPaths
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Editor
argument_list|>
name|editors
init|=
name|newArrayListWithCapacity
argument_list|(
name|indexPaths
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|path
range|:
name|indexPaths
control|)
block|{
name|editors
operator|.
name|add
argument_list|(
operator|new
name|SubtreeEditor
argument_list|(
operator|new
name|DefaultEditor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|isStatusChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
operator|||
name|isIndexDefinitionChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
condition|)
block|{
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|IndexNodeManager
name|index
init|=
name|openIndex
argument_list|(
name|path
argument_list|,
name|root
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"[{}] Index found to be updated. Reopening the IndexNode"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|updates
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|index
argument_list|)
expr_stmt|;
comment|// index can be null
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|badIndexTracker
operator|.
name|markBadPersistedIndex
argument_list|(
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|EditorDiff
operator|.
name|process
argument_list|(
name|CompositeEditor
operator|.
name|compose
argument_list|(
name|editors
argument_list|)
argument_list|,
name|this
operator|.
name|root
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
if|if
condition|(
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indices
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|IndexNodeManager
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|filterKeys
argument_list|(
name|original
argument_list|,
name|not
argument_list|(
name|in
argument_list|(
name|updates
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|putAll
argument_list|(
name|filterValues
argument_list|(
name|updates
argument_list|,
name|notNull
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|badIndexTracker
operator|.
name|markGoodIndexes
argument_list|(
name|updates
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
comment|//This might take some time as close need to acquire the
comment|//write lock which might be held by current running searches
comment|//Given that Tracker is now invoked from a BackgroundObserver
comment|//not a high concern
for|for
control|(
name|String
name|path
range|:
name|updates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|IndexNodeManager
name|index
init|=
name|original
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to close Lucene index at "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|void
name|refresh
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Marked tracker to refresh upon next cycle"
argument_list|)
expr_stmt|;
name|refresh
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|IndexNode
name|acquireIndexNode
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|IndexNodeManager
name|index
init|=
name|indices
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IndexNode
name|indexNode
init|=
name|index
operator|!=
literal|null
condition|?
name|index
operator|.
name|acquire
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|indexNode
operator|!=
literal|null
condition|)
block|{
return|return
name|indexNode
return|;
block|}
else|else
block|{
return|return
name|findIndexNode
argument_list|(
name|path
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
name|IndexDefinition
name|getIndexDefinition
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|IndexNodeManager
name|node
init|=
name|indices
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
comment|//Accessing the definition should not require
comment|//locking as its immutable state
return|return
name|node
operator|.
name|getDefinition
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|getIndexNodePaths
parameter_list|()
block|{
return|return
name|indices
operator|.
name|keySet
argument_list|()
return|;
block|}
name|BadIndexTracker
name|getBadIndexTracker
parameter_list|()
block|{
return|return
name|badIndexTracker
return|;
block|}
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|private
specifier|synchronized
name|IndexNode
name|findIndexNode
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|type
parameter_list|)
block|{
comment|// Retry the lookup from acquireIndexNode now that we're
comment|// synchronized. The acquire() call is guaranteed to succeed
comment|// since the close() method is also synchronized.
name|IndexNodeManager
name|index
init|=
name|indices
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|IndexNode
name|indexNode
init|=
name|index
operator|.
name|acquire
argument_list|()
decl_stmt|;
return|return
name|checkNotNull
argument_list|(
name|indexNode
argument_list|)
return|;
block|}
if|if
condition|(
name|badIndexTracker
operator|.
name|isIgnoredBadIndex
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeState
name|node
init|=
name|root
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
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|IndexHelper
operator|.
name|isIndexNodeOfType
argument_list|(
name|node
argument_list|,
name|type
argument_list|)
condition|)
block|{
name|index
operator|=
name|openIndex
argument_list|(
name|path
argument_list|,
name|root
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|IndexNode
name|indexNode
init|=
name|index
operator|.
name|acquire
argument_list|()
decl_stmt|;
name|checkNotNull
argument_list|(
name|indexNode
argument_list|)
expr_stmt|;
name|indices
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|IndexNodeManager
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|indices
argument_list|)
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|index
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|badIndexTracker
operator|.
name|markGoodIndex
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|indexNode
return|;
block|}
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
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot open Index at path {} as the index is not of type {}"
argument_list|,
name|path
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|badIndexTracker
operator|.
name|markBadIndexForRead
argument_list|(
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isStatusChanged
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|!
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getChildNode
argument_list|(
name|STATUS_NODE
argument_list|)
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|STATUS_NODE
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isIndexDefinitionChanged
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|!
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|)
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

