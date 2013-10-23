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
name|preAsyncRun
argument_list|(
name|store
argument_list|)
expr_stmt|;
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
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
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
argument_list|)
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
condition|)
block|{
try|try
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
name|postAsyncRunStatus
argument_list|(
name|builder
argument_list|)
expr_stmt|;
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
name|after
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
literal|null
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
name|void
name|preAsyncRun
parameter_list|(
name|NodeStore
name|store
parameter_list|)
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
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Index status update {} failed"
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|preAsyncRunStatus
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
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
literal|"running"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-start"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|void
name|postAsyncRunStatus
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
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
literal|"done"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"async-done"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
block|}
block|}
end_class

end_unit

