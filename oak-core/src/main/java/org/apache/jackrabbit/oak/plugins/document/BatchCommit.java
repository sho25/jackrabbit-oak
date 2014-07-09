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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|concurrent
operator|.
name|Callable
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
name|CountDownLatch
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
name|ExecutionException
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
name|Future
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
name|Lists
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
name|util
operator|.
name|concurrent
operator|.
name|Futures
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
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
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
name|checkState
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
name|document
operator|.
name|Collection
operator|.
name|NODES
import|;
end_import

begin_comment
comment|/**  * Combines multiple {@link UpdateOp} into a single call to the  * {@link DocumentStore}.  */
end_comment

begin_class
specifier|final
class|class
name|BatchCommit
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
name|BatchCommit
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|finished
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|BatchCommitQueue
name|queue
decl_stmt|;
specifier|private
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|ops
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Future
argument_list|<
name|NodeDocument
argument_list|>
argument_list|>
name|results
decl_stmt|;
specifier|private
name|boolean
name|executing
decl_stmt|;
name|BatchCommit
parameter_list|(
name|String
name|id
parameter_list|,
name|BatchCommitQueue
name|queue
parameter_list|,
name|boolean
name|onHold
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
if|if
condition|(
name|onHold
condition|)
block|{
name|ops
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|results
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
name|enqueue
parameter_list|(
specifier|final
name|UpdateOp
name|op
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|op
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"Cannot add UpdateOp with id %s to BatchCommit with id %s"
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
name|result
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|checkState
argument_list|(
operator|!
name|executing
argument_list|,
literal|"Cannot enqueue when batch is already executing"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ops
operator|!=
literal|null
condition|)
block|{
name|ops
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
name|int
name|idx
init|=
name|ops
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NodeDocument
name|call
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|BatchCommit
operator|.
name|this
init|)
block|{
while|while
condition|(
operator|!
name|executing
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting until BatchCommit is executing. {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|BatchCommit
operator|.
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
return|return
name|execute
argument_list|(
name|idx
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|DocumentStoreException
operator|.
name|convert
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// not on hold and no other operation in this batch
name|executing
operator|=
literal|true
expr_stmt|;
name|result
operator|=
operator|new
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
return|return
name|queue
operator|.
name|getStore
argument_list|()
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
return|;
block|}
finally|finally
block|{
name|queue
operator|.
name|finished
argument_list|(
name|BatchCommit
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
name|void
name|release
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|executing
operator|=
literal|true
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|Future
argument_list|<
name|NodeDocument
argument_list|>
name|execute
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|idx
operator|==
literal|0
condition|)
block|{
name|NodeDocument
name|before
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UpdateOp
name|combined
init|=
name|UpdateOp
operator|.
name|combine
argument_list|(
name|id
argument_list|,
name|ops
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Batch committing {} updates"
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|before
operator|=
name|queue
operator|.
name|getStore
argument_list|()
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|combined
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"BatchCommit failed, will retry individually. "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|queue
operator|.
name|finished
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
comment|// batch commit unsuccessful, execute individually
name|executeIndividually
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|populateResults
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|finished
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|finished
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Interrupted while waiting for batch commit to finish"
decl_stmt|;
return|return
name|Futures
operator|.
name|immediateFailedFuture
argument_list|(
operator|new
name|DocumentStoreException
argument_list|(
name|msg
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|results
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
name|void
name|executeIndividually
parameter_list|()
block|{
name|DocumentStore
name|store
init|=
name|queue
operator|.
name|getStore
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|ops
control|)
block|{
name|SettableFuture
argument_list|<
name|NodeDocument
argument_list|>
name|result
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|result
operator|.
name|set
argument_list|(
name|store
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|result
operator|.
name|setException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|populateResults
parameter_list|(
name|NodeDocument
name|before
parameter_list|)
block|{
name|DocumentStore
name|store
init|=
name|queue
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comparator
init|=
name|queue
operator|.
name|getComparator
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|ops
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|Futures
operator|.
name|immediateFuture
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|NodeDocument
name|after
init|=
operator|new
name|NodeDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|before
operator|.
name|deepCopy
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|after
argument_list|,
name|op
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|before
operator|=
name|after
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

