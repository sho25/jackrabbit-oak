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
name|server
package|;
end_package

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
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelInboundHandlerAdapter
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
name|codec
operator|.
name|GetBlobRequest
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
name|codec
operator|.
name|GetHeadRequest
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
name|codec
operator|.
name|GetSegmentRequest
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

begin_comment
comment|/**  * Notifies an observer when a valid request has been received and parsed by  * this server.  */
end_comment

begin_class
class|class
name|RequestObserverHandler
extends|extends
name|ChannelInboundHandlerAdapter
block|{
specifier|private
specifier|final
name|CommunicationObserver
name|observer
decl_stmt|;
name|RequestObserverHandler
parameter_list|(
name|CommunicationObserver
name|observer
parameter_list|)
block|{
name|this
operator|.
name|observer
operator|=
name|observer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelRead
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|address
init|=
operator|(
name|InetSocketAddress
operator|)
name|ctx
operator|.
name|channel
argument_list|()
operator|.
name|remoteAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|instanceof
name|GetHeadRequest
condition|)
block|{
name|onGetHeadRequest
argument_list|(
operator|(
name|GetHeadRequest
operator|)
name|msg
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|msg
operator|instanceof
name|GetSegmentRequest
condition|)
block|{
name|onGetSegmentRequest
argument_list|(
operator|(
name|GetSegmentRequest
operator|)
name|msg
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|msg
operator|instanceof
name|GetBlobRequest
condition|)
block|{
name|onGetBlobRequest
argument_list|(
operator|(
name|GetBlobRequest
operator|)
name|msg
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
name|ctx
operator|.
name|fireChannelRead
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onGetHeadRequest
parameter_list|(
name|GetHeadRequest
name|request
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
name|request
operator|.
name|getClientId
argument_list|()
argument_list|,
literal|"get head"
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onGetSegmentRequest
parameter_list|(
name|GetSegmentRequest
name|request
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
name|request
operator|.
name|getClientId
argument_list|()
argument_list|,
literal|"get segment"
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onGetBlobRequest
parameter_list|(
name|GetBlobRequest
name|request
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
name|request
operator|.
name|getClientId
argument_list|()
argument_list|,
literal|"get blob id"
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

