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
name|InetAddress
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
name|net
operator|.
name|UnknownHostException
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|Unpooled
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
name|ChannelHandler
operator|.
name|Sharable
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
name|SimpleChannelInboundHandler
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
name|api
operator|.
name|Blob
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
name|api
operator|.
name|IllegalRepositoryStateException
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
name|RecordId
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
name|Segment
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
name|SegmentId
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
name|SegmentStore
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
name|standby
operator|.
name|codec
operator|.
name|Messages
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
annotation|@
name|Sharable
specifier|public
class|class
name|StandbyServerHandler
extends|extends
name|SimpleChannelInboundHandler
argument_list|<
name|String
argument_list|>
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
name|StandbyServerHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|CommunicationObserver
name|observer
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|allowedIPRanges
decl_stmt|;
specifier|public
name|String
name|state
decl_stmt|;
specifier|public
name|StandbyServerHandler
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|CommunicationObserver
name|observer
parameter_list|,
name|String
index|[]
name|allowedIPRanges
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|observer
operator|=
name|observer
expr_stmt|;
name|this
operator|.
name|allowedIPRanges
operator|=
name|allowedIPRanges
expr_stmt|;
block|}
specifier|private
name|RecordId
name|headId
parameter_list|()
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
return|return
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|long
name|ipToLong
parameter_list|(
name|InetAddress
name|ip
parameter_list|)
block|{
name|byte
index|[]
name|octets
init|=
name|ip
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|long
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|byte
name|octet
range|:
name|octets
control|)
block|{
name|result
operator|<<=
literal|8
expr_stmt|;
name|result
operator||=
name|octet
operator|&
literal|0xff
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|clientAllowed
parameter_list|(
name|InetSocketAddress
name|client
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|allowedIPRanges
operator|!=
literal|null
operator|&&
name|this
operator|.
name|allowedIPRanges
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|this
operator|.
name|allowedIPRanges
control|)
block|{
try|try
block|{
if|if
condition|(
name|ipToLong
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|s
argument_list|)
argument_list|)
operator|==
name|ipToLong
argument_list|(
name|client
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ignored
parameter_list|)
block|{
comment|/* it's an ip range */
block|}
name|int
name|i
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|long
name|startIPRange
init|=
name|ipToLong
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|endIPRange
init|=
name|ipToLong
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|ipl
init|=
name|ipToLong
argument_list|(
name|client
operator|.
name|getAddress
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|startIPRange
operator|<=
name|ipl
operator|&&
name|ipl
operator|<=
name|endIPRange
condition|)
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"invalid IP-range format: "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelRegistered
parameter_list|(
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|state
operator|=
literal|"channel registered"
expr_stmt|;
name|super
operator|.
name|channelRegistered
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelActive
parameter_list|(
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|state
operator|=
literal|"channel active"
expr_stmt|;
name|super
operator|.
name|channelActive
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelInactive
parameter_list|(
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|state
operator|=
literal|"channel inactive"
expr_stmt|;
name|super
operator|.
name|channelInactive
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelUnregistered
parameter_list|(
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
name|ctx
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|state
operator|=
literal|"channel unregistered"
expr_stmt|;
name|super
operator|.
name|channelUnregistered
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelRead0
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|String
name|payload
parameter_list|)
throws|throws
name|Exception
block|{
name|state
operator|=
literal|"got message"
expr_stmt|;
name|String
name|request
init|=
name|Messages
operator|.
name|extractMessageFrom
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|client
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
operator|!
name|clientAllowed
argument_list|(
name|client
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Got request from client "
operator|+
name|client
operator|+
literal|" which is not in the allowed ip ranges! Request will be ignored."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|clientID
init|=
name|Messages
operator|.
name|extractClientFrom
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|observer
operator|.
name|gotMessageFrom
argument_list|(
name|clientID
argument_list|,
name|request
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
name|Messages
operator|.
name|GET_HEAD
operator|.
name|equalsIgnoreCase
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|RecordId
name|r
init|=
name|headId
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|startsWith
argument_list|(
name|Messages
operator|.
name|GET_SEGMENT
argument_list|)
condition|)
block|{
name|String
name|sid
init|=
name|request
operator|.
name|substring
argument_list|(
name|Messages
operator|.
name|GET_SEGMENT
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"request segment id {}"
argument_list|,
name|sid
argument_list|)
expr_stmt|;
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|sid
argument_list|)
decl_stmt|;
name|Segment
name|s
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|s
operator|=
name|store
operator|.
name|readSegment
argument_list|(
operator|new
name|SegmentId
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalRepositoryStateException
name|e
parameter_list|)
block|{
comment|// segment not found
name|log
operator|.
name|debug
argument_list|(
literal|"waiting for segment. Got exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
break|break;
block|}
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"sending segment "
operator|+
name|sid
operator|+
literal|" to "
operator|+
name|client
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendSegmentBytes
argument_list|(
name|clientID
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|startsWith
argument_list|(
name|Messages
operator|.
name|GET_BLOB
argument_list|)
condition|)
block|{
name|String
name|bid
init|=
name|request
operator|.
name|substring
argument_list|(
name|Messages
operator|.
name|GET_BLOB
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"request blob id {}"
argument_list|,
name|bid
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
name|store
operator|.
name|readBlob
argument_list|(
name|bid
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"sending blob "
operator|+
name|bid
operator|+
literal|" to "
operator|+
name|client
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendBinariesBytes
argument_list|(
name|clientID
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|b
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown request {}, ignoring."
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
block|}
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|Unpooled
operator|.
name|EMPTY_BUFFER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelReadComplete
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
block|{
name|ctx
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|state
operator|=
literal|"exception occurred: "
operator|+
name|cause
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|boolean
name|isReadTimeout
init|=
name|cause
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|cause
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Connection reset by peer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|isReadTimeout
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception occurred: "
operator|+
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception occurred: "
operator|+
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

