begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
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
name|jmx
operator|.
name|JmxUtil
import|;
end_import

begin_import
import|import
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
name|GuavaDeprecation
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
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
name|Iterables
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|ScheduleExecutionInstanceTypes
operator|.
name|DEFAULT
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|ScheduleExecutionInstanceTypes
operator|.
name|RUN_ON_LEADER
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|ScheduleExecutionInstanceTypes
operator|.
name|RUN_ON_SINGLE
import|;
end_import

begin_class
specifier|public
class|class
name|WhiteboardUtils
block|{
comment|/**      * JMX Domain name under which Oak related JMX MBeans are registered      */
specifier|public
specifier|static
specifier|final
name|String
name|JMX_OAK_DOMAIN
init|=
literal|"org.apache.jackrabbit.oak"
decl_stmt|;
specifier|public
enum|enum
name|ScheduleExecutionInstanceTypes
block|{
name|DEFAULT
block|,
name|RUN_ON_SINGLE
block|,
name|RUN_ON_LEADER
block|}
specifier|public
specifier|static
name|Registration
name|scheduleWithFixedDelay
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Runnable
name|runnable
parameter_list|,
name|long
name|delayInSeconds
parameter_list|)
block|{
return|return
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|runnable
argument_list|,
name|delayInSeconds
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Registration
name|scheduleWithFixedDelay
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Runnable
name|runnable
parameter_list|,
name|long
name|delayInSeconds
parameter_list|,
name|boolean
name|runOnSingleClusterNode
parameter_list|,
name|boolean
name|useDedicatedPool
parameter_list|)
block|{
return|return
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|runnable
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|delayInSeconds
argument_list|,
name|runOnSingleClusterNode
argument_list|,
name|useDedicatedPool
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Registration
name|scheduleWithFixedDelay
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Runnable
name|runnable
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|extraProps
parameter_list|,
name|long
name|delayInSeconds
parameter_list|,
name|boolean
name|runOnSingleClusterNode
parameter_list|,
name|boolean
name|useDedicatedPool
parameter_list|)
block|{
return|return
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|runnable
argument_list|,
name|extraProps
argument_list|,
name|delayInSeconds
argument_list|,
name|runOnSingleClusterNode
condition|?
name|RUN_ON_SINGLE
else|:
name|DEFAULT
argument_list|,
name|useDedicatedPool
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Registration
name|scheduleWithFixedDelay
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Runnable
name|runnable
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|extraProps
parameter_list|,
name|long
name|delayInSeconds
parameter_list|,
name|ScheduleExecutionInstanceTypes
name|scheduleExecutionInstanceTypes
parameter_list|,
name|boolean
name|useDedicatedPool
parameter_list|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|putAll
argument_list|(
name|extraProps
argument_list|)
decl|.
name|put
argument_list|(
literal|"scheduler.period"
argument_list|,
name|delayInSeconds
argument_list|)
decl|.
name|put
argument_list|(
literal|"scheduler.concurrent"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scheduleExecutionInstanceTypes
operator|==
name|RUN_ON_SINGLE
condition|)
block|{
comment|//Make use of feature while running in Sling SLING-5387
name|builder
operator|.
name|put
argument_list|(
literal|"scheduler.runOn"
argument_list|,
literal|"SINGLE"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scheduleExecutionInstanceTypes
operator|==
name|RUN_ON_LEADER
condition|)
block|{
comment|//Make use of feature while running in Sling SLING-2979
name|builder
operator|.
name|put
argument_list|(
literal|"scheduler.runOn"
argument_list|,
literal|"LEADER"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useDedicatedPool
condition|)
block|{
comment|//Make use of dedicated threadpool SLING-5831
name|builder
operator|.
name|put
argument_list|(
literal|"scheduler.threadPool"
argument_list|,
literal|"oak"
argument_list|)
expr_stmt|;
block|}
return|return
name|whiteboard
operator|.
name|register
argument_list|(
name|Runnable
operator|.
name|class
argument_list|,
name|runnable
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Registration
name|registerMBean
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|,
name|T
name|bean
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|iface
argument_list|,
name|bean
argument_list|,
name|type
argument_list|,
name|name
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Registration
name|registerMBean
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|,
name|T
name|bean
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|)
block|{
try|try
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|table
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|attrs
argument_list|)
decl_stmt|;
name|table
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|JmxUtil
operator|.
name|quoteValueIfRequired
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
name|properties
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"jmx.objectname"
argument_list|,
operator|new
name|ObjectName
argument_list|(
name|JMX_OAK_DOMAIN
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
return|return
name|whiteboard
operator|.
name|register
argument_list|(
name|iface
argument_list|,
name|bean
argument_list|,
name|properties
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the currently available services from the whiteboard of the tracked type.      *      * Note that the underlying tracker is closed automatically.      *      * @param wb the whiteboard      * @param type the service type      * @return a list of services      */
annotation|@
name|NotNull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getServices
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|getServices
argument_list|(
name|wb
argument_list|,
name|type
argument_list|,
operator|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
argument_list|<
name|T
argument_list|>
operator|)
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns the one of the currently available services from the whiteboard of the tracked type.      *      * Note that the underlying tracker is closed automatically.      *      * @return one service or {@code null}      */
annotation|@
name|Nullable
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getService
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|getService
argument_list|(
name|wb
argument_list|,
name|type
argument_list|,
operator|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
argument_list|<
name|T
argument_list|>
operator|)
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns the currently available services from the whiteboard of the tracked type. If {@code predicate} is      * not {@code null} the returned list is limited to the ones that match the predicate.      *      * Note that the underlying tracker is stopped automatically after the services are returned.      *      * @param wb the whiteboard      * @param type the service type      * @param predicate filtering predicate or {@code null}      * @return a list of services      */
annotation|@
name|NotNull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getServices
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
annotation|@
name|Nullable
name|Predicate
argument_list|<
name|T
argument_list|>
name|predicate
parameter_list|)
block|{
name|Tracker
argument_list|<
name|T
argument_list|>
name|tracker
init|=
name|wb
operator|.
name|track
argument_list|(
name|type
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
condition|)
block|{
return|return
name|tracker
operator|.
name|getServices
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|tracker
operator|.
name|getServices
argument_list|()
argument_list|,
parameter_list|(
name|input
parameter_list|)
lambda|->
name|predicate
operator|.
name|test
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|tracker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @deprecated use {@link #getServices(Whiteboard, Class, java.util.function.Predicate)} instead      */
annotation|@
name|NotNull
annotation|@
name|Deprecated
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getServices
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
annotation|@
name|Nullable
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|T
argument_list|>
name|predicate
parameter_list|)
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8685"
argument_list|)
expr_stmt|;
return|return
name|getServices
argument_list|(
name|wb
argument_list|,
name|type
argument_list|,
call|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
argument_list|<
name|T
argument_list|>
call|)
argument_list|(
name|input
argument_list|)
operator|->
name|predicate
operator|.
name|apply
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the one of the currently available services from the whiteboard of the tracked type. If {@code predicate} is      * not {@code null} only a service that match the predicate is returned.      *      * Note that the underlying tracker is closed automatically.      *      * @param wb the whiteboard      * @param type the service type      * @param predicate filtering predicate or {@code null}      * @return one service or {@code null}      */
annotation|@
name|Nullable
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getService
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
annotation|@
name|Nullable
name|Predicate
argument_list|<
name|T
argument_list|>
name|predicate
parameter_list|)
block|{
name|Tracker
argument_list|<
name|T
argument_list|>
name|tracker
init|=
name|wb
operator|.
name|track
argument_list|(
name|type
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|T
name|service
range|:
name|tracker
operator|.
name|getServices
argument_list|()
control|)
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
operator|||
name|predicate
operator|.
name|test
argument_list|(
name|service
argument_list|)
condition|)
block|{
return|return
name|service
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|tracker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @deprecated use {@link #getService(Whiteboard, Class, Predicate)} instead      */
annotation|@
name|Nullable
annotation|@
name|Deprecated
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getService
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|wb
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
annotation|@
name|Nullable
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|T
argument_list|>
name|predicate
parameter_list|)
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8685"
argument_list|)
expr_stmt|;
return|return
name|getService
argument_list|(
name|wb
argument_list|,
name|type
argument_list|,
call|(
name|Predicate
argument_list|<
name|T
argument_list|>
call|)
argument_list|(
name|input
argument_list|)
operator|->
name|predicate
operator|.
name|apply
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

