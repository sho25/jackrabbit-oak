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
name|progress
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|Stopwatch
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
name|TimeDurationFormatter
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
name|IndexUpdate
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
name|IndexUpdateCallback
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
name|NodeTraversalCallback
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

begin_class
specifier|public
class|class
name|IndexingProgressReporter
implements|implements
name|NodeTraversalCallback
block|{
specifier|private
specifier|static
specifier|final
name|String
name|REINDEX_MSG
init|=
literal|"Reindexing"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_MSG
init|=
literal|"Incremental indexing"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexUpdate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|private
specifier|final
name|NodeTraversalCallback
name|traversalCallback
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexUpdateState
argument_list|>
name|indexUpdateStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|traversalCount
decl_stmt|;
specifier|private
name|String
name|messagePrefix
init|=
name|INDEX_MSG
decl_stmt|;
specifier|private
name|TraversalRateEstimator
name|traversalRateEstimator
init|=
operator|new
name|SimpleRateEstimator
argument_list|()
decl_stmt|;
specifier|private
name|NodeCountEstimator
name|nodeCountEstimator
init|=
name|NodeCountEstimator
operator|.
name|NOOP
decl_stmt|;
specifier|private
name|long
name|estimatedCount
decl_stmt|;
specifier|public
name|IndexingProgressReporter
parameter_list|(
name|IndexUpdateCallback
name|updateCallback
parameter_list|,
name|NodeTraversalCallback
name|traversalCallback
parameter_list|)
block|{
name|this
operator|.
name|updateCallback
operator|=
name|updateCallback
expr_stmt|;
name|this
operator|.
name|traversalCallback
operator|=
name|traversalCallback
expr_stmt|;
block|}
specifier|public
name|Editor
name|wrapProgress
parameter_list|(
name|Editor
name|editor
parameter_list|)
block|{
return|return
name|ProgressTrackingEditor
operator|.
name|wrap
argument_list|(
name|editor
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**      * Invoked to indicate that reindexing phase has started in current      * indexing cycle      * @param path      */
specifier|public
name|void
name|reindexingTraversalStart
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|estimatedCount
operator|=
name|nodeCountEstimator
operator|.
name|getEstimatedNodeCount
argument_list|(
name|path
argument_list|,
name|getReindexedIndexPaths
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|estimatedCount
operator|>=
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Estimated node count to be traversed for reindexing under {} is [{}]"
argument_list|,
name|path
argument_list|,
name|estimatedCount
argument_list|)
expr_stmt|;
block|}
name|messagePrefix
operator|=
name|REINDEX_MSG
expr_stmt|;
block|}
comment|/**      * Invoked to indicate that reindexing phase has ended in current      * indexing cycle      */
specifier|public
name|void
name|reindexingTraversalEnd
parameter_list|()
block|{
name|messagePrefix
operator|=
name|INDEX_MSG
expr_stmt|;
block|}
specifier|public
name|void
name|setMessagePrefix
parameter_list|(
name|String
name|messagePrefix
parameter_list|)
block|{
name|this
operator|.
name|messagePrefix
operator|=
name|messagePrefix
expr_stmt|;
block|}
specifier|public
name|void
name|traversedNode
parameter_list|(
name|PathSource
name|pathSource
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|++
name|traversalCount
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|double
name|rate
init|=
name|traversalRateEstimator
operator|.
name|getNodesTraversedPerSecond
argument_list|()
decl_stmt|;
name|String
name|formattedRate
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%1.2f nodes/s, %1.2f nodes/hr"
argument_list|,
name|rate
argument_list|,
name|rate
operator|*
literal|3600
argument_list|)
decl_stmt|;
name|String
name|estimate
init|=
name|estimatePendingTraversal
argument_list|(
name|rate
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"{} Traversed #{} {} [{}] {}"
argument_list|,
name|messagePrefix
argument_list|,
name|traversalCount
argument_list|,
name|pathSource
operator|.
name|getPath
argument_list|()
argument_list|,
name|formattedRate
argument_list|,
name|estimate
argument_list|)
expr_stmt|;
block|}
name|traversalCallback
operator|.
name|traversedNode
argument_list|(
name|pathSource
argument_list|)
expr_stmt|;
name|traversalRateEstimator
operator|.
name|traversedNode
argument_list|()
expr_stmt|;
block|}
comment|/**      * Registers the index for progress tracking      *      * @param indexPath path of index      * @param reindexing true if the index is being reindexed      * @param estimatedCount an estimate of count of number of entries in the index. If less      *                       than zero then it indicates that estimation cannot be done      */
specifier|public
name|void
name|registerIndex
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|boolean
name|reindexing
parameter_list|,
name|long
name|estimatedCount
parameter_list|)
block|{
name|indexUpdateStates
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
operator|new
name|IndexUpdateState
argument_list|(
name|indexPath
argument_list|,
name|reindexing
argument_list|,
name|estimatedCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Callback to indicate that index at give path has got an update      */
specifier|public
name|void
name|indexUpdate
parameter_list|(
name|String
name|indexPath
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|indexUpdateStates
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|logReport
parameter_list|()
block|{
if|if
condition|(
name|isReindexingPerformed
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Reindexing completed"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|somethingIndexed
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getReindexStats
parameter_list|()
block|{
return|return
name|indexUpdateStates
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|st
lambda|->
name|st
operator|.
name|reindex
argument_list|)
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns true if any reindexing is performed in current indexing      * cycle      */
specifier|public
name|boolean
name|isReindexingPerformed
parameter_list|()
block|{
return|return
name|indexUpdateStates
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|st
lambda|->
name|st
operator|.
name|reindex
argument_list|)
return|;
block|}
comment|/**      * Set of indexPaths which have been updated or accessed      * in this indexing cycle.      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getUpdatedIndexPaths
parameter_list|()
block|{
return|return
name|indexUpdateStates
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Set of indexPaths which have been reindexed      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getReindexedIndexPaths
parameter_list|()
block|{
return|return
name|indexUpdateStates
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|st
lambda|->
name|st
operator|.
name|reindex
argument_list|)
operator|.
name|map
argument_list|(
name|st
lambda|->
name|st
operator|.
name|indexPath
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|somethingIndexed
parameter_list|()
block|{
return|return
name|indexUpdateStates
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|st
lambda|->
name|st
operator|.
name|updateCount
operator|>
literal|0
argument_list|)
return|;
block|}
specifier|public
name|void
name|setTraversalRateEstimator
parameter_list|(
name|TraversalRateEstimator
name|traversalRate
parameter_list|)
block|{
name|this
operator|.
name|traversalRateEstimator
operator|=
name|traversalRate
expr_stmt|;
block|}
specifier|public
name|void
name|setNodeCountEstimator
parameter_list|(
name|NodeCountEstimator
name|nodeCountEstimator
parameter_list|)
block|{
name|this
operator|.
name|nodeCountEstimator
operator|=
name|nodeCountEstimator
expr_stmt|;
block|}
specifier|public
name|void
name|setEstimatedCount
parameter_list|(
name|long
name|estimatedCount
parameter_list|)
block|{
name|this
operator|.
name|estimatedCount
operator|=
name|estimatedCount
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|watch
operator|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
expr_stmt|;
name|traversalCount
operator|=
literal|0
expr_stmt|;
name|messagePrefix
operator|=
name|INDEX_MSG
expr_stmt|;
block|}
specifier|private
name|String
name|estimatePendingTraversal
parameter_list|(
name|double
name|nodesPerSecond
parameter_list|)
block|{
if|if
condition|(
name|estimatedCount
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|estimatedCount
operator|>
name|traversalCount
condition|)
block|{
name|long
name|pending
init|=
name|estimatedCount
operator|-
name|traversalCount
decl_stmt|;
name|long
name|timeRequired
init|=
call|(
name|long
call|)
argument_list|(
name|pending
operator|/
name|nodesPerSecond
argument_list|)
decl_stmt|;
name|double
name|percentComplete
init|=
operator|(
operator|(
name|double
operator|)
name|traversalCount
operator|/
name|estimatedCount
operator|)
operator|*
literal|100
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"(Elapsed %s, Expected %s, Completed %1.2f%%)"
argument_list|,
name|watch
argument_list|,
name|TimeDurationFormatter
operator|.
name|forLogging
argument_list|()
operator|.
name|format
argument_list|(
name|timeRequired
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|percentComplete
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"(Elapsed %s)"
argument_list|,
name|watch
argument_list|)
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
specifier|private
name|String
name|getReport
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Indexing report"
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexUpdateState
name|st
range|:
name|indexUpdateStates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|log
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
name|st
operator|.
name|reindex
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|st
operator|.
name|updateCount
operator|>
literal|0
operator|||
name|st
operator|.
name|reindex
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"    - %s%n"
argument_list|,
name|st
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
class|class
name|IndexUpdateState
block|{
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|final
name|boolean
name|reindex
decl_stmt|;
specifier|final
name|long
name|estimatedCount
decl_stmt|;
specifier|final
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|long
name|updateCount
decl_stmt|;
specifier|public
name|IndexUpdateState
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|boolean
name|reindex
parameter_list|,
name|long
name|estimatedCount
parameter_list|)
block|{
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|reindex
operator|=
name|reindex
expr_stmt|;
name|this
operator|.
name|estimatedCount
operator|=
name|estimatedCount
expr_stmt|;
block|}
specifier|public
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|updateCount
operator|++
expr_stmt|;
if|if
condition|(
name|updateCount
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{} => Indexed {} nodes in {} ..."
argument_list|,
name|indexPath
argument_list|,
name|updateCount
argument_list|,
name|watch
argument_list|)
expr_stmt|;
name|watch
operator|.
name|reset
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|reindexMarker
init|=
name|reindex
condition|?
literal|"*"
else|:
literal|""
decl_stmt|;
return|return
name|indexPath
operator|+
name|reindexMarker
operator|+
literal|"("
operator|+
name|updateCount
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

