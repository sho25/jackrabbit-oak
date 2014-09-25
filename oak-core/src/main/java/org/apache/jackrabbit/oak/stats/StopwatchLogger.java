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
name|stats
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
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
name|concurrent
operator|.
name|ScheduledExecutorService
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
comment|/**  * Utility class to be used for tracking of timing within methods. It makes use of the  * {@link Clock.Fast} for speeding up the operation.  */
end_comment

begin_class
specifier|public
class|class
name|StopwatchLogger
implements|implements
name|Closeable
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
name|StopwatchLogger
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
init|=
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|clazz
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|Logger
name|customLog
decl_stmt|;
specifier|private
name|long
name|start
decl_stmt|;
comment|/**      * Create a class with the provided class.      *       * @param claz      */
specifier|public
name|StopwatchLogger
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|checkNotNull
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * instantiate a class with the provided class      *       * @param clazz      */
specifier|public
name|StopwatchLogger
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|checkNotNull
argument_list|(
name|clazz
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Instantiate a class with the provided class and custom logger. The provided logger, if not      * null, will be then used for tracking down times      *       * @param customLog      * @param clazz      */
specifier|public
name|StopwatchLogger
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Logger
name|customLog
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|customLog
argument_list|,
name|checkNotNull
argument_list|(
name|clazz
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Instantiate a class with the provided class and custom logger. The provided logger, if not      * null, will be then used for tracking down times      *      * @param customLog      * @param clazz      */
specifier|public
name|StopwatchLogger
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Logger
name|customLog
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|checkNotNull
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|this
operator|.
name|customLog
operator|=
name|customLog
expr_stmt|;
block|}
comment|/**      * starts the clock      */
specifier|public
name|void
name|start
parameter_list|()
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|start
operator|=
name|clock
operator|.
name|getTimeMonotonic
argument_list|()
expr_stmt|;
block|}
comment|/**      * track of an intermediate time without stopping the ticking.      *       * @param message      */
specifier|public
name|void
name|split
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|message
parameter_list|)
block|{
name|track
argument_list|(
name|this
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * track the time and stop the clock.      *       * @param message      */
specifier|public
name|void
name|stop
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|message
parameter_list|)
block|{
name|track
argument_list|(
name|this
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|clock
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * convenience method for tracking the messages      *       * @param customLog a potential custom logger. If null the static instance will be used      * @param clock the clock used for tracking.      * @param clazz the class to be used during the tracking of times      * @param message a custom message for the tracking.      */
specifier|private
specifier|static
name|void
name|track
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|StopwatchLogger
name|swl
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|message
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|swl
argument_list|)
expr_stmt|;
if|if
condition|(
name|swl
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|Logger
name|l
init|=
name|swl
operator|.
name|getLogger
argument_list|()
decl_stmt|;
if|if
condition|(
name|swl
operator|.
name|clock
operator|==
literal|null
condition|)
block|{
name|l
operator|.
name|debug
argument_list|(
literal|"{} - clock has not been started yet."
argument_list|,
name|swl
operator|.
name|clazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Clock
name|c
init|=
name|swl
operator|.
name|clock
decl_stmt|;
name|l
operator|.
name|debug
argument_list|(
literal|"{} - {} {}ms"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|checkNotNull
argument_list|(
name|swl
operator|.
name|clazz
argument_list|)
block|,
name|message
operator|==
literal|null
condition|?
literal|""
else|:
name|message
block|,
name|c
operator|.
name|getTimeMonotonic
argument_list|()
operator|-
name|swl
operator|.
name|start
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
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
name|error
argument_list|(
literal|"Error while shutting down the scheduler."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return true if the clock has been started. False otherwise.      */
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|clock
operator|!=
literal|null
return|;
block|}
specifier|private
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
operator|(
name|customLog
operator|==
literal|null
operator|)
condition|?
name|LOG
else|:
name|customLog
return|;
block|}
comment|/**      * @return true whether the provided appender has DEBUG enabled and therefore asked to track      *         times.      */
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|getLogger
argument_list|()
operator|.
name|isDebugEnabled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

