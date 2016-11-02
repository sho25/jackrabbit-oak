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
operator|.
name|persistentCache
operator|.
name|async
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|BlockingQueue
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
comment|/**  * An asynchronous buffer of the CacheAction objects. The buffer only accepts  * {@link #MAX_SIZE} number of elements. If the queue is already full, the new  * elements are dropped.  */
end_comment

begin_class
specifier|public
class|class
name|CacheActionDispatcher
implements|implements
name|Runnable
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
name|CacheActionDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * What's the length of the queue.      */
specifier|static
specifier|final
name|int
name|MAX_SIZE
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
specifier|final
name|BlockingQueue
argument_list|<
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|(
name|MAX_SIZE
argument_list|)
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|isRunning
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|isRunning
condition|)
block|{
try|try
block|{
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|action
init|=
name|queue
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
operator|&&
name|isRunning
condition|)
block|{
name|action
operator|.
name|execute
argument_list|()
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
name|debug
argument_list|(
literal|"Interrupted the queue.poll()"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Stop the processing.      */
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|isRunning
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Tries to add new action.      *      * @param action to be added      */
name|boolean
name|add
parameter_list|(
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|action
parameter_list|)
block|{
return|return
name|queue
operator|.
name|offer
argument_list|(
name|action
argument_list|)
return|;
block|}
block|}
end_class

end_unit

