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
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|BlockingQueue
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
name|LinkedBlockingDeque
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
name|io
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|Bootstrap
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
name|Channel
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
name|ChannelInitializer
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
name|ChannelOption
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
name|ChannelPipeline
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
name|nio
operator|.
name|NioEventLoopGroup
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
name|socket
operator|.
name|SocketChannel
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
name|socket
operator|.
name|nio
operator|.
name|NioSocketChannel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|LengthFieldBasedFrameDecoder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|compression
operator|.
name|SnappyFrameDecoder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|string
operator|.
name|StringEncoder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|ssl
operator|.
name|SslContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|ssl
operator|.
name|SslContextBuilder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|ssl
operator|.
name|util
operator|.
name|InsecureTrustManagerFactory
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|timeout
operator|.
name|ReadTimeoutHandler
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|CharsetUtil
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
name|GetBlobRequestEncoder
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
name|GetBlobResponse
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
name|GetHeadRequestEncoder
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
name|GetHeadResponse
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
name|GetReferencesRequest
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
name|GetReferencesRequestEncoder
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
name|GetReferencesResponse
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
name|codec
operator|.
name|GetSegmentRequestEncoder
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
name|GetSegmentResponse
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
name|ResponseDecoder
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
class|class
name|StandbyClient
implements|implements
name|AutoCloseable
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
name|StandbyClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|GetHeadResponse
argument_list|>
name|headQueue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|GetSegmentResponse
argument_list|>
name|segmentQueue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|GetBlobResponse
argument_list|>
name|blobQueue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|GetReferencesResponse
argument_list|>
name|referencesQueue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|readTimeoutMs
decl_stmt|;
specifier|private
specifier|final
name|String
name|clientId
decl_stmt|;
specifier|private
name|Channel
name|channel
decl_stmt|;
name|StandbyClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|NioEventLoopGroup
name|group
parameter_list|,
name|String
name|clientId
parameter_list|,
name|boolean
name|secure
parameter_list|,
name|int
name|readTimeoutMs
parameter_list|,
name|File
name|spoolFolder
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|readTimeoutMs
operator|=
name|readTimeoutMs
expr_stmt|;
name|Bootstrap
name|b
init|=
operator|new
name|Bootstrap
argument_list|()
operator|.
name|group
argument_list|(
name|group
argument_list|)
operator|.
name|channel
argument_list|(
name|NioSocketChannel
operator|.
name|class
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|CONNECT_TIMEOUT_MILLIS
argument_list|,
name|readTimeoutMs
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|TCP_NODELAY
argument_list|,
literal|true
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_REUSEADDR
argument_list|,
literal|true
argument_list|)
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_KEEPALIVE
argument_list|,
literal|true
argument_list|)
operator|.
name|handler
argument_list|(
operator|new
name|ChannelInitializer
argument_list|<
name|SocketChannel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initChannel
parameter_list|(
name|SocketChannel
name|ch
parameter_list|)
throws|throws
name|Exception
block|{
name|ChannelPipeline
name|p
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
if|if
condition|(
name|secure
condition|)
block|{
name|p
operator|.
name|addLast
argument_list|(
name|SslContextBuilder
operator|.
name|forClient
argument_list|()
operator|.
name|trustManager
argument_list|(
name|InsecureTrustManagerFactory
operator|.
name|INSTANCE
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|newHandler
argument_list|(
name|ch
operator|.
name|alloc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ReadTimeoutHandler
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Decoders
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|SnappyFrameDecoder
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// The frame length limits the chunk size to max. 2.2GB
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|LengthFieldBasedFrameDecoder
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ResponseDecoder
argument_list|(
name|spoolFolder
argument_list|)
argument_list|)
expr_stmt|;
comment|// Encoders
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|StringEncoder
argument_list|(
name|CharsetUtil
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetHeadRequestEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetSegmentRequestEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetBlobRequestEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetReferencesRequestEncoder
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handlers
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetHeadResponseHandler
argument_list|(
name|headQueue
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetSegmentResponseHandler
argument_list|(
name|segmentQueue
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetBlobResponseHandler
argument_list|(
name|blobQueue
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetReferencesResponseHandler
argument_list|(
name|referencesQueue
argument_list|)
argument_list|)
expr_stmt|;
comment|// Exception handler
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ExceptionHandler
argument_list|(
name|clientId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|channel
operator|=
name|b
operator|.
name|connect
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
operator|.
name|sync
argument_list|()
operator|.
name|channel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|channel
operator|.
name|close
argument_list|()
operator|.
name|awaitUninterruptibly
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Channel closed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Channel close timed out"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nullable
name|String
name|getHead
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|channel
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|GetHeadRequest
argument_list|(
name|clientId
argument_list|)
argument_list|)
expr_stmt|;
name|GetHeadResponse
name|response
init|=
name|headQueue
operator|.
name|poll
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|response
operator|.
name|getHeadRecordId
argument_list|()
return|;
block|}
annotation|@
name|Nullable
name|byte
index|[]
name|getSegment
parameter_list|(
name|String
name|segmentId
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|channel
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|GetSegmentRequest
argument_list|(
name|clientId
argument_list|,
name|segmentId
argument_list|)
argument_list|)
expr_stmt|;
name|GetSegmentResponse
name|response
init|=
name|segmentQueue
operator|.
name|poll
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|response
operator|.
name|getSegmentData
argument_list|()
return|;
block|}
annotation|@
name|Nullable
name|InputStream
name|getBlob
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|channel
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|GetBlobRequest
argument_list|(
name|clientId
argument_list|,
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
name|GetBlobResponse
name|response
init|=
name|blobQueue
operator|.
name|poll
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|response
operator|.
name|getInputStream
argument_list|()
return|;
block|}
annotation|@
name|Nullable
name|Iterable
argument_list|<
name|String
argument_list|>
name|getReferences
parameter_list|(
name|String
name|segmentId
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|channel
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|GetReferencesRequest
argument_list|(
name|clientId
argument_list|,
name|segmentId
argument_list|)
argument_list|)
expr_stmt|;
name|GetReferencesResponse
name|response
init|=
name|referencesQueue
operator|.
name|poll
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|response
operator|.
name|getReferences
argument_list|()
return|;
block|}
specifier|public
name|int
name|getReadTimeoutMs
parameter_list|()
block|{
return|return
name|readTimeoutMs
return|;
block|}
block|}
end_class

end_unit

