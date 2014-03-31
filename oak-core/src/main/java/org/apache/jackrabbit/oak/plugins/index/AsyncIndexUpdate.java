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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|MISSING_NODE
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
name|jmx
operator|.
name|IndexStatsMBean
operator|.
name|STATUS_DONE
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
name|jmx
operator|.
name|IndexStatsMBean
operator|.
name|STATUS_RUNNING
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|Objects
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
name|api
operator|.
name|jmx
operator|.
name|IndexStatsMBean
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
name|value
operator|.
name|Conversions
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
name|CommitHook
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|ISO8601
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
name|AsyncIndexUpdate
implements|implements
name|Runnable
block|{
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
name|AsyncIndexUpdate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Name of the hidden node under which information about the checkpoints      * seen and indexed by each async indexer is kept.      */
specifier|private
specifier|static
specifier|final
name|String
name|ASYNC
init|=
literal|":async"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_LIFETIME
init|=
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CommitFailedException
name|CONCURRENT_UPDATE
init|=
operator|new
name|CommitFailedException
argument_list|(
literal|"Async"
argument_list|,
literal|1
argument_list|,
literal|"Concurrent update detected"
argument_list|)
decl_stmt|;
comment|/**      * Timeout in minutes after which an async job would be considered as timed out. Another      * node in cluster would wait for timeout before taking over a running job      */
specifier|private
specifier|static
specifier|final
name|int
name|ASYNC_TIMEOUT
init|=
literal|15
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|IndexEditorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|long
name|lifetime
init|=
name|DEFAULT_LIFETIME
decl_stmt|;
comment|// TODO: make configurable
comment|/** Flag to avoid repeatedly logging failure warnings */
specifier|private
name|boolean
name|failing
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|AsyncIndexStats
name|indexStats
init|=
operator|new
name|AsyncIndexStats
argument_list|()
decl_stmt|;
specifier|public
name|AsyncIndexUpdate
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|IndexEditorProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|checkNotNull
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
comment|/**      * Index update callback that tries to raise the async status flag when      * the first index change is detected.      *      * @see<a href="https://issues.apache.org/jira/browse/OAK-1292">OAK-1292</a>      */
specifier|private
class|class
name|AsyncUpdateCallback
implements|implements
name|IndexUpdateCallback
block|{
specifier|private
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|dirty
condition|)
block|{
name|dirty
operator|=
literal|true
expr_stmt|;
name|preAsyncRun
argument_list|(
name|store
argument_list|,
name|name
argument_list|,
name|indexStats
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running background index task {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAlreadyRunning
argument_list|(
name|store
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Async job found to be already running. Skipping"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|checkpoint
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
name|store
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to retrieve checkpoint {}"
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|NodeBuilder
name|async
init|=
name|builder
operator|.
name|child
argument_list|(
name|ASYNC
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
literal|null
decl_stmt|;
specifier|final
name|PropertyState
name|state
init|=
name|async
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|getType
argument_list|()
operator|==
name|STRING
condition|)
block|{
name|before
operator|=
name|store
operator|.
name|retrieve
argument_list|(
name|state
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
name|before
operator|=
name|MISSING_NODE
expr_stmt|;
block|}
name|AsyncUpdateCallback
name|callback
init|=
operator|new
name|AsyncUpdateCallback
argument_list|()
decl_stmt|;
name|IndexUpdate
name|indexUpdate
init|=
operator|new
name|IndexUpdate
argument_list|(
name|provider
argument_list|,
name|name
argument_list|,
name|after
argument_list|,
name|builder
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|indexUpdate
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
operator|&&
name|callback
operator|.
name|dirty
condition|)
block|{
name|async
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// check for concurrent updates by this async task
name|PropertyState
name|stateAfterRebase
init|=
name|before
operator|.
name|getChildNode
argument_list|(
name|ASYNC
argument_list|)
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|state
argument_list|,
name|stateAfterRebase
argument_list|)
condition|)
block|{
return|return
name|postAsyncRunStatus
argument_list|(
name|after
operator|.
name|builder
argument_list|()
argument_list|,
name|indexStats
argument_list|)
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
name|CONCURRENT_UPDATE
throw|;
block|}
block|}
block|}
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|!=
name|CONCURRENT_UPDATE
condition|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|failing
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Index update {} failed"
argument_list|,
name|name
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
name|failing
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|failing
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Index update {} no longer fails"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|failing
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|preAsyncRun
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|,
name|AsyncIndexStats
name|stats
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|preAsyncRunStatus
argument_list|(
name|builder
argument_list|,
name|stats
argument_list|)
expr_stmt|;
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
block|}
specifier|private
specifier|static
name|boolean
name|isAlreadyRunning
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|NodeState
name|indexState
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
comment|//Probably the first run
if|if
condition|(
operator|!
name|indexState
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|//Check if already running or timed out
if|if
condition|(
name|STATUS_RUNNING
operator|.
name|equals
argument_list|(
name|indexState
operator|.
name|getString
argument_list|(
literal|"async-status"
argument_list|)
argument_list|)
condition|)
block|{
name|PropertyState
name|startTime
init|=
name|indexState
operator|.
name|getProperty
argument_list|(
literal|"async-start"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|Conversions
operator|.
name|convert
argument_list|(
name|startTime
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
operator|.
name|toCalendar
argument_list|()
decl_stmt|;
name|Calendar
name|now
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
name|now
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|start
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
comment|//Check if the job has timed out and we need to take over
if|if
condition|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|delta
argument_list|)
operator|>
name|ASYNC_TIMEOUT
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Async job found which stated on {} has timed out in {} minutes. "
operator|+
literal|"This node would take over the job."
argument_list|,
name|startTime
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|,
name|ASYNC_TIMEOUT
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|void
name|preAsyncRunStatus
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|AsyncIndexStats
name|stats
parameter_list|)
block|{
name|String
name|now
init|=
name|now
argument_list|()
decl_stmt|;
name|stats
operator|.
name|start
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-status"
argument_list|,
name|STATUS_RUNNING
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-start"
argument_list|,
name|now
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"async-done"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|postAsyncRunStatus
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|AsyncIndexStats
name|stats
parameter_list|)
block|{
name|String
name|now
init|=
name|now
argument_list|()
decl_stmt|;
name|stats
operator|.
name|done
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-status"
argument_list|,
name|STATUS_DONE
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-done"
argument_list|,
name|now
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"async-start"
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|String
name|now
parameter_list|()
block|{
return|return
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|AsyncIndexStats
name|getIndexStats
parameter_list|()
block|{
return|return
name|indexStats
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|AsyncIndexStats
implements|implements
name|IndexStatsMBean
block|{
specifier|private
name|String
name|start
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|done
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|status
init|=
name|STATUS_INIT
decl_stmt|;
specifier|public
name|void
name|start
parameter_list|(
name|String
name|now
parameter_list|)
block|{
name|status
operator|=
name|STATUS_RUNNING
expr_stmt|;
name|start
operator|=
name|now
expr_stmt|;
name|done
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|void
name|done
parameter_list|(
name|String
name|now
parameter_list|)
block|{
name|status
operator|=
name|STATUS_DONE
expr_stmt|;
name|start
operator|=
literal|""
expr_stmt|;
name|done
operator|=
name|now
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDone
parameter_list|()
block|{
return|return
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
block|}
end_class

end_unit

