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
name|ChannelOutboundHandlerAdapter
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
name|ChannelPromise
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
comment|/**  * Notifies an observer when a 'get segment' or 'get blob' response is sent  * from this server.  */
end_comment

begin_class
class|class
name|ResponseObserverHandler
extends|extends
name|ChannelOutboundHandlerAdapter
block|{
specifier|private
specifier|final
name|CommunicationObserver
name|observer
decl_stmt|;
name|ResponseObserverHandler
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
name|write
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|msg
parameter_list|,
name|ChannelPromise
name|promise
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|msg
operator|instanceof
name|GetSegmentResponse
condition|)
block|{
name|onGetSegmentResponse
argument_list|(
operator|(
name|GetSegmentResponse
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|msg
operator|instanceof
name|GetBlobResponse
condition|)
block|{
name|onGetBlobResponse
argument_list|(
operator|(
name|GetBlobResponse
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
name|ctx
operator|.
name|write
argument_list|(
name|msg
argument_list|,
name|promise
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onGetSegmentResponse
parameter_list|(
name|GetSegmentResponse
name|response
parameter_list|)
block|{
name|observer
operator|.
name|didSendSegmentBytes
argument_list|(
name|response
operator|.
name|getClientId
argument_list|()
argument_list|,
name|response
operator|.
name|getSegment
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|onGetBlobResponse
parameter_list|(
name|GetBlobResponse
name|response
parameter_list|)
block|{
name|observer
operator|.
name|didSendBinariesBytes
argument_list|(
name|response
operator|.
name|getClientId
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getBlob
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

