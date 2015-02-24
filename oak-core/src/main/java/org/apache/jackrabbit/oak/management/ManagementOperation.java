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
name|management
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
name|Objects
operator|.
name|toStringHelper
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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|DAYS
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
name|NANOSECONDS
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
name|HOURS
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
name|MINUTES
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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|MICROSECONDS
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
operator|.
name|INTEGER
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|FAILED
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|INITIATED
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|NONE
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|RUNNING
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|SUCCEEDED
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
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|UNAVAILABLE
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|failed
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|none
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|running
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|succeeded
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
name|FutureTask
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
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
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
comment|/**  * A {@code ManagementOperation} is a background task, which can be  * executed by an {@code Executor}. Its {@link Status} indicates  * whether execution has already been started, is currently under the  * way or has already finished.  *  * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_class
specifier|public
class|class
name|ManagementOperation
parameter_list|<
name|R
parameter_list|>
extends|extends
name|FutureTask
argument_list|<
name|R
argument_list|>
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
name|ManagementOperation
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|idGen
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|int
name|id
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Create a new {@code ManagementOperation} of the given name. The name      * is an informal value attached to this instance.      *      * @param name  informal name      * @param task  task to execute for this operation      */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|ManagementOperation
argument_list|<
name|R
argument_list|>
name|newManagementOperation
parameter_list|(
name|String
name|name
parameter_list|,
name|Callable
argument_list|<
name|R
argument_list|>
name|task
parameter_list|)
block|{
return|return
operator|new
name|ManagementOperation
argument_list|<
name|R
argument_list|>
argument_list|(
name|name
argument_list|,
name|task
argument_list|)
return|;
block|}
comment|/**      * An operation that is already done with the given {@code value}.      *      * @param name   name of the operation      * @param result result returned by the operation      * @return  a {@code ManagementOperation} instance that is already done.      */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|ManagementOperation
argument_list|<
name|R
argument_list|>
name|done
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|R
name|result
parameter_list|)
block|{
return|return
operator|new
name|ManagementOperation
argument_list|<
name|R
argument_list|>
argument_list|(
literal|"not started"
argument_list|,
operator|new
name|Callable
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|R
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|result
return|;
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|get
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This task is done"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|none
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" not started"
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a new {@code ManagementOperation} of the given name. The name      * is an informal value attached to this instance.      *      * @param name  informal name      * @param task  task to execute for this operation      */
specifier|private
name|ManagementOperation
parameter_list|(
name|String
name|name
parameter_list|,
name|Callable
argument_list|<
name|R
argument_list|>
name|task
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|idGen
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Each instance of a {@code ManagementOperation} has an unique id      * associated with it. This id is returned as a part of its      * {@link #getStatus() status}      *      * @return  id of this operation      */
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Informal name      * @return  name of this operation      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * The {@link ManagementOperation.Status status} of this operation:      *<ul>      *<li>{@link Status#running(String) running} if the operation is currently      *     being executed.</li>      *<li>{@link Status#succeeded(String) succeeded} if the operation has terminated      *     without errors.</li>      *<li>{@link Status#failed(String) failed} if the operation has been cancelled,      *     its thread has been interrupted during execution or the operation has failed      *     with an exception.</li>      *</ul>      *      * @return  the current status of this operation      */
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
return|return
name|failed
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" cancelled"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isDone
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|succeeded
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" succeeded: "
operator|+
name|get
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
name|failed
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" status unknown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|name
operator|+
literal|" failed"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|failed
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" failed: "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|running
argument_list|(
name|id
argument_list|,
name|name
operator|+
literal|" running"
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Status of a {@link ManagementOperation}. One of      * {@link #unavailable(String)}, {@link #none(String)}, {@link #initiated(String)},      * {@link #running(String)}, {@link #succeeded(String)} and {@link #failed(String)},      * the semantics of which correspond to the respective status codes in      * {@link org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean}.      */
specifier|public
specifier|static
specifier|final
class|class
name|Status
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ITEM_CODE
init|=
literal|"code"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ITEM_ID
init|=
literal|"id"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ITEM_MESSAGE
init|=
literal|"message"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|ITEM_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|ITEM_CODE
block|,
name|ITEM_ID
block|,
name|ITEM_MESSAGE
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CompositeType
name|ITEM_TYPES
init|=
name|createItemTypes
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|CompositeType
name|createItemTypes
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CompositeType
argument_list|(
literal|"status"
argument_list|,
literal|"status"
argument_list|,
name|ITEM_NAMES
argument_list|,
name|ITEM_NAMES
argument_list|,
operator|new
name|OpenType
index|[]
block|{
name|INTEGER
block|,
name|INTEGER
block|,
name|STRING
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
comment|// should never happen
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|final
name|StatusCode
name|code
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|private
name|Status
parameter_list|(
name|StatusCode
name|code
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
operator|==
literal|null
condition|?
literal|""
else|:
name|message
expr_stmt|;
block|}
specifier|public
specifier|static
name|Status
name|unavailable
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|unavailable
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Status
name|none
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|none
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Status
name|initiated
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|initiated
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Status
name|running
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|running
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Status
name|succeeded
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|succeeded
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Status
name|failed
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|failed
argument_list|(
name|idGen
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|unavailable
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|UNAVAILABLE
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|none
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|NONE
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|initiated
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|INITIATED
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|running
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|RUNNING
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|succeeded
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|SUCCEEDED
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|static
name|Status
name|failed
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|FAILED
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/**          * Utility method for formatting a duration in nano seconds          * into a human readable string.          * @param nanos  number of nano seconds          * @return human readable string          */
specifier|public
specifier|static
name|String
name|formatTime
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|TimeUnit
name|unit
init|=
name|chooseUnit
argument_list|(
name|nanos
argument_list|)
decl_stmt|;
name|double
name|value
init|=
operator|(
name|double
operator|)
name|nanos
operator|/
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|1
argument_list|,
name|unit
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%.4g %s"
argument_list|,
name|value
argument_list|,
name|abbreviate
argument_list|(
name|unit
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|TimeUnit
name|chooseUnit
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
if|if
condition|(
name|DAYS
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|DAYS
return|;
block|}
if|if
condition|(
name|HOURS
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|HOURS
return|;
block|}
if|if
condition|(
name|MINUTES
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|MINUTES
return|;
block|}
if|if
condition|(
name|SECONDS
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|SECONDS
return|;
block|}
if|if
condition|(
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|MILLISECONDS
return|;
block|}
if|if
condition|(
name|MICROSECONDS
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|NANOSECONDS
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|MICROSECONDS
return|;
block|}
return|return
name|NANOSECONDS
return|;
block|}
specifier|private
specifier|static
name|String
name|abbreviate
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
name|NANOSECONDS
case|:
return|return
literal|"ns"
return|;
case|case
name|MICROSECONDS
case|:
return|return
literal|"\u03bcs"
return|;
comment|// μs
case|case
name|MILLISECONDS
case|:
return|return
literal|"ms"
return|;
case|case
name|SECONDS
case|:
return|return
literal|"s"
return|;
case|case
name|MINUTES
case|:
return|return
literal|"min"
return|;
case|case
name|HOURS
case|:
return|return
literal|"h"
return|;
case|case
name|DAYS
case|:
return|return
literal|"d"
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|unit
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**          * Utility method for converting a {@code CompositeData} encoding          * of a status to a {@code Status} instance.          *          * @param status  {@code CompositeData} encoding of a status          * @return {@code Status} for {@code status}          * @throws IllegalArgumentException  if {@code status} is not a valid          *         encoding of a {@code Status}.          */
specifier|public
specifier|static
name|Status
name|fromCompositeData
parameter_list|(
name|CompositeData
name|status
parameter_list|)
block|{
name|int
name|code
init|=
name|toInt
argument_list|(
name|status
operator|.
name|get
argument_list|(
name|ITEM_CODE
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|id
init|=
name|toInt
argument_list|(
name|status
operator|.
name|get
argument_list|(
name|ITEM_ID
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|toString
argument_list|(
name|status
operator|.
name|get
argument_list|(
name|ITEM_MESSAGE
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Status
argument_list|(
name|StatusCode
operator|.
name|values
argument_list|()
index|[
name|code
index|]
argument_list|,
name|id
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|toInt
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
return|return
operator|(
name|Integer
operator|)
name|value
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an integer value:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|value
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a string value:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
comment|/**          * Utility method for converting this instance to a {@code CompositeData}          * encoding of the respective status.          *          * @return {@code CompositeData} of this {@code Status}          */
specifier|public
name|CompositeData
name|toCompositeData
parameter_list|()
block|{
try|try
block|{
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[]
block|{
name|code
operator|.
name|ordinal
argument_list|()
block|,
name|id
block|,
name|message
block|}
decl_stmt|;
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|ITEM_TYPES
argument_list|,
name|ITEM_NAMES
argument_list|,
name|values
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
comment|// should never happen
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|StatusCode
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|code
operator|.
name|name
return|;
block|}
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|SUCCEEDED
operator|==
name|code
return|;
block|}
specifier|public
name|boolean
name|isFailure
parameter_list|()
block|{
return|return
name|FAILED
operator|==
name|code
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|addValue
argument_list|(
name|code
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|add
argument_list|(
literal|"message"
argument_list|,
name|message
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|that
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|that
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Status
name|status
init|=
operator|(
name|Status
operator|)
name|that
decl_stmt|;
return|return
name|id
operator|==
name|status
operator|.
name|id
operator|&&
name|code
operator|==
name|status
operator|.
name|code
operator|&&
name|message
operator|.
name|equals
argument_list|(
name|status
operator|.
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|code
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|id
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|message
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

