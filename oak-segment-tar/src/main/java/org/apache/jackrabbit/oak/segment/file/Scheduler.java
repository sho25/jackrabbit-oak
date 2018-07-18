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
name|segment
operator|.
name|file
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|currentThread
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|defaultThreadFactory
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newScheduledThreadPool
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|ScheduledExecutorService
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
name|ThreadFactory
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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

begin_comment
comment|/**  * A simple scheduler for executing and scheduling tasks in the background.  * This implementation delegates all background execution to an instance  * of a {@link ScheduledExecutorService} with core pool size 1. The behaviour  * of this underlying scheduler service determines the semantics of the methods  * in this class. Namely: Execution of background tasks never overlaps and is  * FIFO for tasks scheduled for the same time.  * In addition all tasks scheduled through methods of this class are automatically  * wrapped into {@link SafeRunnable} instances. The background thread executing  * submitted tasks is a deamon thread.  */
end_comment

begin_class
specifier|public
class|class
name|Scheduler
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
name|int
name|schedulerNumber
init|=
literal|0
decl_stmt|;
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
name|Scheduler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|AtomicLong
name|executionCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
decl_stmt|;
comment|/**      * Create a new instance with the given {@code name}. The name is used to      * derive the default name of the background thread from..      * @param name      */
specifier|public
name|Scheduler
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|Scheduler
operator|.
name|class
init|)
block|{
name|this
operator|.
name|name
operator|=
literal|"scheduler-"
operator|+
name|schedulerNumber
expr_stmt|;
name|schedulerNumber
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
name|this
operator|.
name|executor
operator|=
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|SchedulerThreadFactory
argument_list|(
name|this
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Immediately execute {@code task}. The background thread's name is      * set to {@code name} during execution of {@code task}.      * @param name      * @param task      * @see ScheduledExecutorService#execute(Runnable)      */
specifier|public
name|void
name|execute
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|Runnable
name|task
parameter_list|)
block|{
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|SafeRunnable
argument_list|(
name|name
argument_list|,
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Run {@code task} once after some delay. The background thread's name is      * set to {@code name} during execution of {@code task}.      * @param name      * @param delay      * @param unit      * @param task      * @see ScheduledExecutorService#schedule(Runnable, long, TimeUnit)      */
specifier|public
name|void
name|scheduleOnce
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
name|long
name|delay
parameter_list|,
annotation|@
name|NotNull
name|TimeUnit
name|unit
parameter_list|,
annotation|@
name|NotNull
name|Runnable
name|task
parameter_list|)
block|{
name|executor
operator|.
name|schedule
argument_list|(
operator|new
name|SafeRunnable
argument_list|(
name|name
argument_list|,
name|task
argument_list|)
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Run {@code task} regularly at a given interval. The background thread's name is      * set to {@code name} during execution of {@code task}.      * @param name      * @param period      * @param unit      * @param task      * @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)      */
specifier|public
name|void
name|scheduleAtFixedRate
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
name|long
name|period
parameter_list|,
annotation|@
name|NotNull
name|TimeUnit
name|unit
parameter_list|,
annotation|@
name|NotNull
name|Runnable
name|task
parameter_list|)
block|{
name|executor
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|SafeRunnable
argument_list|(
name|name
argument_list|,
name|task
argument_list|)
argument_list|,
name|period
argument_list|,
name|period
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Run {@code task} regularly after a fixed delay. The background thread's name is      * set to {@code name} during execution of {@code task}.      * @param name      * @param delay      * @param unit      * @param task      * @see ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)      */
specifier|public
name|void
name|scheduleWithFixedDelay
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
name|long
name|delay
parameter_list|,
annotation|@
name|NotNull
name|TimeUnit
name|unit
parameter_list|,
annotation|@
name|NotNull
name|Runnable
name|task
parameter_list|)
block|{
name|executor
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|SafeRunnable
argument_list|(
name|name
argument_list|,
name|task
argument_list|)
argument_list|,
name|delay
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Close this scheduler.      * @see ScheduledExecutorService#shutdown()      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The scheduler {} was successfully shut down"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The scheduler {} takes too long to shut down"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupt while shutting down he scheduler {}"
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|SchedulerThreadFactory
implements|implements
name|ThreadFactory
block|{
specifier|private
specifier|final
name|ThreadFactory
name|threadFactory
init|=
name|defaultThreadFactory
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|SchedulerThreadFactory
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
annotation|@
name|NotNull
name|Runnable
name|runnable
parameter_list|)
block|{
name|Thread
name|thread
init|=
name|threadFactory
operator|.
name|newThread
argument_list|(
name|runnable
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
block|}
end_class

end_unit

