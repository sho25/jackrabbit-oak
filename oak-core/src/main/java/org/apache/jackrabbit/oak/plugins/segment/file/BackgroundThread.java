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
name|System
operator|.
name|currentTimeMillis
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
name|Date
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
comment|/**  * A small wrapper around the Thread class that periodically calls a runnable.  * Please note the Runnable.run() method is not supposed to loop itself, instead  * it should just do one operation. This class calls Runnable.run() repeatedly.  * This class also measures and logs the time taken by the Runnable.run()  * method.  */
end_comment

begin_class
class|class
name|BackgroundThread
extends|extends
name|Thread
implements|implements
name|Closeable
block|{
comment|/** Logger instance */
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
name|BackgroundThread
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
specifier|private
name|boolean
name|alive
init|=
literal|true
decl_stmt|;
specifier|private
name|long
name|iterations
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|sumDuration
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|maxDuration
init|=
literal|0
decl_stmt|;
name|BackgroundThread
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|interval
parameter_list|,
name|Runnable
name|target
parameter_list|)
block|{
name|super
argument_list|(
name|target
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setPriority
argument_list|(
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|waitUntilNextIteration
argument_list|()
condition|)
block|{
name|setName
argument_list|(
name|name
operator|+
literal|", active since "
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|", previous max duration "
operator|+
name|maxDuration
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|super
operator|.
name|run
argument_list|()
expr_stmt|;
name|long
name|duration
init|=
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|iterations
operator|++
expr_stmt|;
name|sumDuration
operator|+=
name|duration
expr_stmt|;
name|maxDuration
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxDuration
argument_list|,
name|duration
argument_list|)
expr_stmt|;
comment|// make execution statistics visible in thread dumps
name|setName
argument_list|(
name|name
operator|+
literal|", avg "
operator|+
operator|(
name|sumDuration
operator|/
name|iterations
operator|)
operator|+
literal|"ms"
operator|+
literal|", max "
operator|+
name|maxDuration
operator|+
literal|"ms"
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|name
operator|+
literal|" interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|trigger
parameter_list|()
block|{
name|trigger
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|trigger
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|name
operator|+
literal|" join interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|trigger
parameter_list|(
name|boolean
name|close
parameter_list|)
block|{
if|if
condition|(
name|close
condition|)
block|{
name|alive
operator|=
literal|false
expr_stmt|;
block|}
name|notify
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|boolean
name|waitUntilNextIteration
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|alive
condition|)
block|{
if|if
condition|(
name|interval
operator|<
literal|0
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|wait
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|alive
return|;
block|}
block|}
end_class

end_unit

