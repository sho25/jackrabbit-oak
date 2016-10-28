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
name|standby
operator|.
name|client
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
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
name|javax
operator|.
name|management
operator|.
name|StandardMBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLException
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
name|base
operator|.
name|Supplier
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|standby
operator|.
name|jmx
operator|.
name|ClientStandbyStatusMBean
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
name|segment
operator|.
name|standby
operator|.
name|jmx
operator|.
name|StandbyStatusMBean
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
name|segment
operator|.
name|standby
operator|.
name|store
operator|.
name|CommunicationObserver
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

begin_class
specifier|public
specifier|final
class|class
name|StandbyClientSync
implements|implements
name|ClientStandbyStatusMBean
implements|,
name|Runnable
implements|,
name|Closeable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_ID_PROPERTY_NAME
init|=
literal|"standbyID"
decl_stmt|;
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
name|StandbyClientSync
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|host
decl_stmt|;
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
specifier|private
specifier|final
name|int
name|readTimeoutMs
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|autoClean
decl_stmt|;
specifier|private
specifier|final
name|CommunicationObserver
name|observer
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|secure
decl_stmt|;
specifier|private
name|boolean
name|active
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|failedRequests
decl_stmt|;
specifier|private
name|long
name|lastSuccessfulRequest
decl_stmt|;
specifier|private
specifier|volatile
name|String
name|state
decl_stmt|;
specifier|private
specifier|final
name|Object
name|sync
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
name|long
name|syncStartTimestamp
decl_stmt|;
specifier|private
name|long
name|syncEndTimestamp
decl_stmt|;
specifier|public
name|StandbyClientSync
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|FileStore
name|store
parameter_list|,
name|boolean
name|secure
parameter_list|,
name|int
name|readTimeoutMs
parameter_list|,
name|boolean
name|autoClean
parameter_list|)
throws|throws
name|SSLException
block|{
name|this
operator|.
name|state
operator|=
name|STATUS_INITIALIZING
expr_stmt|;
name|this
operator|.
name|lastSuccessfulRequest
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|syncStartTimestamp
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|syncEndTimestamp
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|failedRequests
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|secure
operator|=
name|secure
expr_stmt|;
name|this
operator|.
name|readTimeoutMs
operator|=
name|readTimeoutMs
expr_stmt|;
name|this
operator|.
name|autoClean
operator|=
name|autoClean
expr_stmt|;
name|this
operator|.
name|fileStore
operator|=
name|store
expr_stmt|;
name|String
name|s
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|CLIENT_ID_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|this
operator|.
name|observer
operator|=
operator|new
name|CommunicationObserver
argument_list|(
operator|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
else|:
name|s
argument_list|)
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
try|try
block|{
name|jmxServer
operator|.
name|registerMBean
argument_list|(
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|ClientStandbyStatusMBean
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ObjectName
argument_list|(
name|this
operator|.
name|getMBeanName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"can register standby status mbean"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getMBeanName
parameter_list|()
block|{
return|return
name|StandbyStatusMBean
operator|.
name|JMX_NAME
operator|+
literal|",id=\""
operator|+
name|this
operator|.
name|observer
operator|.
name|getID
argument_list|()
operator|+
literal|"\""
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|stop
argument_list|()
expr_stmt|;
name|state
operator|=
name|STATUS_CLOSING
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
try|try
block|{
name|jmxServer
operator|.
name|unregisterMBean
argument_list|(
operator|new
name|ObjectName
argument_list|(
name|this
operator|.
name|getMBeanName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"can unregister standby status mbean"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|observer
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|state
operator|=
name|STATUS_CLOSED
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRunning
argument_list|()
condition|)
block|{
comment|// manually stopped
return|return;
block|}
name|state
operator|=
name|STATUS_STARTING
expr_stmt|;
synchronized|synchronized
init|(
name|sync
init|)
block|{
if|if
condition|(
name|active
condition|)
block|{
return|return;
block|}
name|state
operator|=
name|STATUS_RUNNING
expr_stmt|;
name|active
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|long
name|startTimestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
init|(
name|StandbyClient
name|client
init|=
operator|new
name|StandbyClient
argument_list|(
name|observer
operator|.
name|getID
argument_list|()
argument_list|,
name|secure
argument_list|,
name|readTimeoutMs
argument_list|)
init|)
block|{
name|client
operator|.
name|connect
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|int
name|genBefore
init|=
name|headGeneration
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
operator|new
name|StandbyClientSyncExecution
argument_list|(
name|fileStore
argument_list|,
name|client
argument_list|,
name|newRunningSupplier
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|int
name|genAfter
init|=
name|headGeneration
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|autoClean
operator|&&
operator|(
name|genAfter
operator|>
name|genBefore
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"New head generation detected (prevHeadGen: {} newHeadGen: {}), running cleanup."
argument_list|,
name|genBefore
argument_list|,
name|genAfter
argument_list|)
expr_stmt|;
name|cleanupAndRemove
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|failedRequests
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|syncStartTimestamp
operator|=
name|startTimestamp
expr_stmt|;
name|this
operator|.
name|syncEndTimestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastSuccessfulRequest
operator|=
name|syncEndTimestamp
operator|/
literal|1000
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|failedRequests
operator|++
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Failed synchronizing state."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|sync
init|)
block|{
name|this
operator|.
name|active
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|int
name|headGeneration
parameter_list|(
name|FileStore
name|fileStore
parameter_list|)
block|{
return|return
name|fileStore
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegment
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
return|;
block|}
specifier|private
name|void
name|cleanupAndRemove
parameter_list|()
throws|throws
name|IOException
block|{
name|fileStore
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|newRunningSupplier
parameter_list|()
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|running
operator|.
name|get
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMode
parameter_list|()
block|{
return|return
literal|"client: "
operator|+
name|this
operator|.
name|observer
operator|.
name|getID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
name|running
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|STATUS_RUNNING
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|state
operator|=
name|STATUS_STOPPED
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getFailedRequests
parameter_list|()
block|{
return|return
name|this
operator|.
name|failedRequests
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSecondsSinceLastSuccess
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|lastSuccessfulRequest
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|/
literal|1000
operator|-
name|this
operator|.
name|lastSuccessfulRequest
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|calcFailedRequests
parameter_list|()
block|{
return|return
name|this
operator|.
name|getFailedRequests
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|calcSecondsSinceLastSuccess
parameter_list|()
block|{
return|return
name|this
operator|.
name|getSecondsSinceLastSuccess
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
try|try
block|{
name|cleanupAndRemove
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while cleaning up"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSyncStartTimestamp
parameter_list|()
block|{
return|return
name|syncStartTimestamp
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSyncEndTimestamp
parameter_list|()
block|{
return|return
name|syncEndTimestamp
return|;
block|}
block|}
end_class

end_unit

