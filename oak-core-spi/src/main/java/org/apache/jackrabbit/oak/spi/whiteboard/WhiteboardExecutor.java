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
name|spi
operator|.
name|whiteboard
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
name|Executor
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

begin_comment
comment|/**  * Dynamic {@link Executor} based on the available whiteboard services.  * The {@link #execute(Runnable)} method passes the given command to the  * first available executor service. Alternatively the command is run  * in the calling thread if no executor services are available.  */
end_comment

begin_class
specifier|public
class|class
name|WhiteboardExecutor
extends|extends
name|AbstractServiceTracker
argument_list|<
name|Executor
argument_list|>
implements|implements
name|Executor
block|{
specifier|public
name|WhiteboardExecutor
parameter_list|()
block|{
name|super
argument_list|(
name|Executor
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Executor>--
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|command
parameter_list|)
block|{
for|for
control|(
name|Executor
name|executor
range|:
name|getServices
argument_list|()
control|)
block|{
comment|// use the first executor to run the command
name|executor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// no executor services available, so use the current thread instead
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

