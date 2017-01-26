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
name|segment
operator|.
name|standby
operator|.
name|store
package|;
end_package

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
name|segment
operator|.
name|standby
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

begin_class
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
specifier|private
name|String
name|lastRequest
decl_stmt|;
specifier|private
name|Date
name|lastSeen
decl_stmt|;
specifier|private
name|String
name|lastSeenTimestamp
decl_stmt|;
specifier|private
name|String
name|remoteAddress
decl_stmt|;
specifier|private
name|int
name|remotePort
decl_stmt|;
specifier|private
name|long
name|segmentsSent
decl_stmt|;
specifier|private
name|long
name|segmentBytesSent
decl_stmt|;
specifier|private
name|long
name|binariesSent
decl_stmt|;
specifier|private
name|long
name|binariesBytesSent
decl_stmt|;
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
name|StandbyStatusMBean
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
name|Nonnull
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
name|lastSeenTimestamp
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
annotation|@
name|Override
specifier|public
name|long
name|getTransferredBinaries
parameter_list|()
block|{
return|return
name|this
operator|.
name|binariesSent
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTransferredBinariesBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|binariesBytesSent
return|;
block|}
name|void
name|setRemoteAddress
parameter_list|(
name|String
name|remoteAddress
parameter_list|)
block|{
name|this
operator|.
name|remoteAddress
operator|=
name|remoteAddress
expr_stmt|;
block|}
name|void
name|setRemotePort
parameter_list|(
name|int
name|remotePort
parameter_list|)
block|{
name|this
operator|.
name|remotePort
operator|=
name|remotePort
expr_stmt|;
block|}
name|Date
name|getLastSeen
parameter_list|()
block|{
return|return
name|lastSeen
return|;
block|}
name|void
name|setLastSeen
parameter_list|(
name|Date
name|lastSeen
parameter_list|)
block|{
name|this
operator|.
name|lastSeen
operator|=
name|lastSeen
expr_stmt|;
name|this
operator|.
name|lastSeenTimestamp
operator|=
name|lastSeen
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|void
name|setLastRequest
parameter_list|(
name|String
name|lastRequest
parameter_list|)
block|{
name|this
operator|.
name|lastRequest
operator|=
name|lastRequest
expr_stmt|;
block|}
name|void
name|onSegmentSent
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|segmentsSent
operator|++
expr_stmt|;
name|segmentBytesSent
operator|+=
name|bytes
expr_stmt|;
block|}
name|void
name|onBinarySent
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|binariesSent
operator|++
expr_stmt|;
name|binariesBytesSent
operator|+=
name|bytes
expr_stmt|;
block|}
block|}
end_class

end_unit

