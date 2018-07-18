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
name|commons
operator|.
name|concurrent
package|;
end_package

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
name|ExecutorService
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
comment|/**  *<p>  * Utility class to properly close any ExecutorService.  *</p>  *   *<p>  * It will attempt a graceful close within the provided timeout. If after such any of the contained  * tasks are not terminated yet, it will force a shutdown and track a warning in the logs.  *</p>  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ExecutorCloser
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
name|ExecutorCloser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
specifier|private
specifier|final
name|int
name|timeout
decl_stmt|;
specifier|private
specifier|final
name|TimeUnit
name|timeUnit
decl_stmt|;
comment|/**      * will attempt a graceful close in 5 seconds      *       * @param executorService      */
specifier|public
name|ExecutorCloser
parameter_list|(
annotation|@
name|Nullable
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|this
argument_list|(
name|executorService
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**      * will attempt a graceful close by the provided time.      *       * @param executorService the executor to close      * @param timeout the time to wait for      * @param unit the unit of time      */
specifier|public
name|ExecutorCloser
parameter_list|(
annotation|@
name|Nullable
name|ExecutorService
name|executorService
parameter_list|,
name|int
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|executorService
operator|=
name|executorService
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|this
operator|.
name|timeUnit
operator|=
name|unit
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while shutting down the ExecutorService"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|executorService
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"ExecutorService `{}` didn't shutdown property. Will be forced now."
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

