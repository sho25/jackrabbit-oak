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
name|plugins
operator|.
name|segment
operator|.
name|failover
package|;
end_package

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
name|plugins
operator|.
name|segment
operator|.
name|failover
operator|.
name|jmx
operator|.
name|FailoverStatusMBean
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
name|plugins
operator|.
name|segment
operator|.
name|failover
operator|.
name|jmx
operator|.
name|ObservablePartnerMBean
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
name|javax
operator|.
name|management
operator|.
name|StandardMBean
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
name|net
operator|.
name|InetSocketAddress
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
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
specifier|public
class|class
name|CommunicationObserver
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CLIENT_STATISTICS
init|=
literal|10
decl_stmt|;
specifier|private
class|class
name|CommunicationPartnerMBean
implements|implements
name|ObservablePartnerMBean
block|{
specifier|private
specifier|final
name|ObjectName
name|mbeanName
decl_stmt|;
specifier|private
specifier|final
name|String
name|clientName
decl_stmt|;
specifier|public
name|String
name|lastRequest
decl_stmt|;
specifier|public
name|Date
name|lastSeen
decl_stmt|;
specifier|public
name|String
name|remoteAddress
decl_stmt|;
specifier|public
name|int
name|remotePort
decl_stmt|;
specifier|public
name|long
name|segmentsSent
decl_stmt|;
specifier|public
name|long
name|segmentBytesSent
decl_stmt|;
specifier|public
name|CommunicationPartnerMBean
parameter_list|(
name|String
name|clientName
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
name|this
operator|.
name|mbeanName
operator|=
operator|new
name|ObjectName
argument_list|(
name|FailoverStatusMBean
operator|.
name|JMX_NAME
operator|+
literal|",id=\"Client "
operator|+
name|clientName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
name|getMBeanName
parameter_list|()
block|{
return|return
name|this
operator|.
name|mbeanName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|remoteAddress
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastRequest
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRemotePort
parameter_list|()
block|{
return|return
name|this
operator|.
name|remotePort
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastSeenTimestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastSeen
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|lastSeen
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTransferredSegments
parameter_list|()
block|{
return|return
name|this
operator|.
name|segmentsSent
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTransferredSegmentBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|segmentBytesSent
return|;
block|}
block|}
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
name|CommunicationObserver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|identifier
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CommunicationPartnerMBean
argument_list|>
name|partnerDetails
decl_stmt|;
specifier|public
name|CommunicationObserver
parameter_list|(
name|String
name|myID
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|myID
expr_stmt|;
name|this
operator|.
name|partnerDetails
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CommunicationPartnerMBean
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|unregister
parameter_list|(
name|CommunicationPartnerMBean
name|m
parameter_list|)
block|{
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
name|m
operator|.
name|getMBeanName
argument_list|()
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
literal|"error unregistering mbean for client '"
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|unregister
parameter_list|()
block|{
for|for
control|(
name|CommunicationPartnerMBean
name|m
range|:
name|this
operator|.
name|partnerDetails
operator|.
name|values
argument_list|()
control|)
block|{
name|unregister
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|gotMessageFrom
parameter_list|(
name|String
name|client
parameter_list|,
name|String
name|request
parameter_list|,
name|InetSocketAddress
name|remote
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"got message '"
operator|+
name|request
operator|+
literal|"' from client "
operator|+
name|client
argument_list|)
expr_stmt|;
name|CommunicationPartnerMBean
name|m
init|=
name|this
operator|.
name|partnerDetails
operator|.
name|get
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|boolean
name|register
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|cleanUp
argument_list|()
expr_stmt|;
name|m
operator|=
operator|new
name|CommunicationPartnerMBean
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|m
operator|.
name|remoteAddress
operator|=
name|remote
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
name|m
operator|.
name|remotePort
operator|=
name|remote
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|register
operator|=
literal|true
expr_stmt|;
block|}
name|m
operator|.
name|lastSeen
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|m
operator|.
name|lastRequest
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|partnerDetails
operator|.
name|put
argument_list|(
name|client
argument_list|,
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
name|register
condition|)
block|{
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
name|m
argument_list|,
name|ObservablePartnerMBean
operator|.
name|class
argument_list|)
argument_list|,
name|m
operator|.
name|getMBeanName
argument_list|()
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
literal|"can register mbean for client '"
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|didSendSegmentBytes
parameter_list|(
name|String
name|client
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"did send segment with "
operator|+
name|size
operator|+
literal|" bytes to client "
operator|+
name|client
argument_list|)
expr_stmt|;
name|CommunicationPartnerMBean
name|m
init|=
name|this
operator|.
name|partnerDetails
operator|.
name|get
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|m
operator|.
name|segmentsSent
operator|++
expr_stmt|;
name|m
operator|.
name|segmentBytesSent
operator|+=
name|size
expr_stmt|;
name|this
operator|.
name|partnerDetails
operator|.
name|put
argument_list|(
name|client
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getID
parameter_list|()
block|{
return|return
name|this
operator|.
name|identifier
return|;
block|}
comment|// helper
specifier|private
name|void
name|cleanUp
parameter_list|()
block|{
while|while
condition|(
name|this
operator|.
name|partnerDetails
operator|.
name|size
argument_list|()
operator|>=
name|MAX_CLIENT_STATISTICS
condition|)
block|{
name|CommunicationPartnerMBean
name|oldestEntry
init|=
name|oldestEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldestEntry
operator|==
literal|null
condition|)
return|return;
name|log
operator|.
name|info
argument_list|(
literal|"housekeeping: removing statistics for "
operator|+
name|oldestEntry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|unregister
argument_list|(
name|oldestEntry
argument_list|)
expr_stmt|;
name|this
operator|.
name|partnerDetails
operator|.
name|remove
argument_list|(
name|oldestEntry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|CommunicationPartnerMBean
name|oldestEntry
parameter_list|()
block|{
name|CommunicationPartnerMBean
name|ret
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CommunicationPartnerMBean
name|m
range|:
name|this
operator|.
name|partnerDetails
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|ret
operator|==
literal|null
operator|||
name|ret
operator|.
name|lastSeen
operator|.
name|after
argument_list|(
name|m
operator|.
name|lastSeen
argument_list|)
condition|)
name|ret
operator|=
name|m
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

