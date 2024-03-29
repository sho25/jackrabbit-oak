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
name|lang
operator|.
name|Thread
operator|.
name|currentThread
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
comment|/**  * A {@code Runnable} implementation that is safe to submit to an executor or  * {@link Scheduler}.  *<p>  * When this implementation's {@link #run()} method is invoked, it will set the  * name of the current thread to the name passed to {@link SafeRunnable}, run  * the wrapped runnable and finally restore the initial thread name. When the  * wrapped runnable throws any unhandled exception, this exception is logged at  * error level and the exception is re-thrown.  */
end_comment

begin_class
class|class
name|SafeRunnable
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
name|SafeRunnable
operator|.
name|class
argument_list|)
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
name|Runnable
name|runnable
decl_stmt|;
comment|/**      * New instance with the given {@code name} wrapping the passed {@code      * runnable}.      *      * @param name     The name of the background operation.      * @param runnable The background operation.      */
name|SafeRunnable
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|Runnable
name|runnable
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
name|runnable
operator|=
name|checkNotNull
argument_list|(
name|runnable
argument_list|)
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
name|String
name|n
init|=
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Uncaught exception in %s"
argument_list|,
name|name
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

