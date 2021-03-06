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
name|blob
package|;
end_package

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
name|AbstractListeningExecutorService
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
name|RejectedExecutionException
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
name|concurrent
operator|.
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * Class copied from the Guava 15, to make the AzureDataStore compatible with  * the Guava 26 (where the SameThreadExecutorService is not present).  *  * TODO: Remove this class once the whole Oak is migrated to use Guava 26.  */
end_comment

begin_class
class|class
name|SameThreadExecutorService
extends|extends
name|AbstractListeningExecutorService
block|{
comment|/**      * Lock used whenever accessing the state variables      * (runningTasks, shutdown, terminationCondition) of the executor      */
specifier|private
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/** Signaled after the executor is shutdown and running tasks are done */
specifier|private
specifier|final
name|Condition
name|termination
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/*      * Conceptually, these two variables describe the executor being in      * one of three states:      *   - Active: shutdown == false      *   - Shutdown: runningTasks> 0 and shutdown == true      *   - Terminated: runningTasks == 0 and shutdown == true      */
specifier|private
name|int
name|runningTasks
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|shutdown
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
block|{
name|startTask
argument_list|()
expr_stmt|;
try|try
block|{
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|endTask
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isShutdown
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|shutdown
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|shutdown
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// See sameThreadExecutor javadoc for unusual behavior of this method.
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Runnable
argument_list|>
name|shutdownNow
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isTerminated
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|shutdown
operator|&&
name|runningTasks
operator|==
literal|0
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|awaitTermination
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|nanos
init|=
name|unit
operator|.
name|toNanos
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|isTerminated
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|nanos
operator|=
name|termination
operator|.
name|awaitNanos
argument_list|(
name|nanos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Checks if the executor has been shut down and increments the running      * task count.      *      * @throws RejectedExecutionException if the executor has been previously      *         shutdown      */
specifier|private
name|void
name|startTask
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|isShutdown
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RejectedExecutionException
argument_list|(
literal|"Executor already shutdown"
argument_list|)
throw|;
block|}
name|runningTasks
operator|++
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Decrements the running task count.      */
specifier|private
name|void
name|endTask
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|runningTasks
operator|--
expr_stmt|;
if|if
condition|(
name|isTerminated
argument_list|()
condition|)
block|{
name|termination
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

