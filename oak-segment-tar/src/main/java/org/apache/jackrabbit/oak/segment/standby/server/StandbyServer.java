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
name|server
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
name|checkState
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
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
name|ServerBootstrap
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
name|ChannelFuture
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
name|EventLoopGroup
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
name|NioServerSocketChannel
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
name|LineBasedFrameDecoder
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
name|SnappyFrameEncoder
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
name|StringDecoder
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
name|SelfSignedCertificate
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
name|stream
operator|.
name|ChunkedWriteHandler
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
name|core
operator|.
name|data
operator|.
name|util
operator|.
name|NamedThreadFactory
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
name|codec
operator|.
name|GetBlobResponseEncoder
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
name|GetHeadResponseEncoder
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
name|GetReferencesResponseEncoder
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
name|GetSegmentResponseEncoder
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
name|RequestDecoder
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
class|class
name|StandbyServer
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
name|StandbyServer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * If a persisted head state cannot be acquired in less than this timeout,      * the 'get head' request from the client will be discarded.      */
specifier|private
specifier|static
specifier|final
name|long
name|READ_HEAD_TIMEOUT
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"standby.server.timeout"
argument_list|,
literal|10_000L
argument_list|)
decl_stmt|;
specifier|static
name|Builder
name|builder
parameter_list|(
name|int
name|port
parameter_list|,
name|StoreProvider
name|provider
parameter_list|,
name|int
name|blobChunkSize
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|port
argument_list|,
name|provider
argument_list|,
name|blobChunkSize
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
specifier|private
specifier|final
name|EventLoopGroup
name|bossGroup
decl_stmt|;
specifier|private
specifier|final
name|EventLoopGroup
name|workerGroup
decl_stmt|;
specifier|private
specifier|final
name|ServerBootstrap
name|b
decl_stmt|;
specifier|private
name|SslContext
name|sslContext
decl_stmt|;
specifier|private
name|ChannelFuture
name|channelFuture
decl_stmt|;
specifier|static
class|class
name|Builder
block|{
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
specifier|private
specifier|final
name|StoreProvider
name|storeProvider
decl_stmt|;
specifier|private
specifier|final
name|int
name|blobChunkSize
decl_stmt|;
specifier|private
name|boolean
name|secure
decl_stmt|;
specifier|private
name|String
index|[]
name|allowedClientIPRanges
decl_stmt|;
specifier|private
name|StateConsumer
name|stateConsumer
decl_stmt|;
specifier|private
name|CommunicationObserver
name|observer
decl_stmt|;
specifier|private
name|StandbyHeadReader
name|standbyHeadReader
decl_stmt|;
specifier|private
name|StandbySegmentReader
name|standbySegmentReader
decl_stmt|;
specifier|private
name|StandbyReferencesReader
name|standbyReferencesReader
decl_stmt|;
specifier|private
name|StandbyBlobReader
name|standbyBlobReader
decl_stmt|;
specifier|private
name|Builder
parameter_list|(
specifier|final
name|int
name|port
parameter_list|,
specifier|final
name|StoreProvider
name|storeProvider
parameter_list|,
specifier|final
name|int
name|blobChunkSize
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|storeProvider
operator|=
name|storeProvider
expr_stmt|;
name|this
operator|.
name|blobChunkSize
operator|=
name|blobChunkSize
expr_stmt|;
block|}
name|Builder
name|secure
parameter_list|(
name|boolean
name|secure
parameter_list|)
block|{
name|this
operator|.
name|secure
operator|=
name|secure
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|allowIPRanges
parameter_list|(
name|String
index|[]
name|allowedClientIPRanges
parameter_list|)
block|{
name|this
operator|.
name|allowedClientIPRanges
operator|=
name|allowedClientIPRanges
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withStateConsumer
parameter_list|(
name|StateConsumer
name|stateConsumer
parameter_list|)
block|{
name|this
operator|.
name|stateConsumer
operator|=
name|stateConsumer
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withObserver
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
return|return
name|this
return|;
block|}
name|Builder
name|withStandbyHeadReader
parameter_list|(
name|StandbyHeadReader
name|standbyHeadReader
parameter_list|)
block|{
name|this
operator|.
name|standbyHeadReader
operator|=
name|standbyHeadReader
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withStandbySegmentReader
parameter_list|(
name|StandbySegmentReader
name|standbySegmentReader
parameter_list|)
block|{
name|this
operator|.
name|standbySegmentReader
operator|=
name|standbySegmentReader
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withStandbyReferencesReader
parameter_list|(
name|StandbyReferencesReader
name|standbyReferencesReader
parameter_list|)
block|{
name|this
operator|.
name|standbyReferencesReader
operator|=
name|standbyReferencesReader
expr_stmt|;
return|return
name|this
return|;
block|}
name|Builder
name|withStandbyBlobReader
parameter_list|(
name|StandbyBlobReader
name|standbyBlobReader
parameter_list|)
block|{
name|this
operator|.
name|standbyBlobReader
operator|=
name|standbyBlobReader
expr_stmt|;
return|return
name|this
return|;
block|}
name|StandbyServer
name|build
parameter_list|()
throws|throws
name|CertificateException
throws|,
name|SSLException
block|{
name|checkState
argument_list|(
name|storeProvider
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|FileStore
name|store
init|=
name|storeProvider
operator|.
name|provideStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|standbyReferencesReader
operator|==
literal|null
condition|)
block|{
name|standbyReferencesReader
operator|=
operator|new
name|DefaultStandbyReferencesReader
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|standbyBlobReader
operator|==
literal|null
condition|)
block|{
name|standbyBlobReader
operator|=
operator|new
name|DefaultStandbyBlobReader
argument_list|(
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|standbySegmentReader
operator|==
literal|null
condition|)
block|{
name|standbySegmentReader
operator|=
operator|new
name|DefaultStandbySegmentReader
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|standbyHeadReader
operator|==
literal|null
condition|)
block|{
name|standbyHeadReader
operator|=
operator|new
name|DefaultStandbyHeadReader
argument_list|(
name|store
argument_list|,
name|READ_HEAD_TIMEOUT
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StandbyServer
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
name|StandbyServer
parameter_list|(
specifier|final
name|Builder
name|builder
parameter_list|)
throws|throws
name|CertificateException
throws|,
name|SSLException
block|{
name|this
operator|.
name|port
operator|=
name|builder
operator|.
name|port
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|secure
condition|)
block|{
name|SelfSignedCertificate
name|ssc
init|=
operator|new
name|SelfSignedCertificate
argument_list|()
decl_stmt|;
name|sslContext
operator|=
name|SslContextBuilder
operator|.
name|forServer
argument_list|(
name|ssc
operator|.
name|certificate
argument_list|()
argument_list|,
name|ssc
operator|.
name|privateKey
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|bossGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|(
literal|1
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"primary-run"
argument_list|)
argument_list|)
expr_stmt|;
name|workerGroup
operator|=
operator|new
name|NioEventLoopGroup
argument_list|(
literal|0
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"primary"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|ServerBootstrap
argument_list|()
expr_stmt|;
name|b
operator|.
name|group
argument_list|(
name|bossGroup
argument_list|,
name|workerGroup
argument_list|)
expr_stmt|;
name|b
operator|.
name|channel
argument_list|(
name|NioServerSocketChannel
operator|.
name|class
argument_list|)
expr_stmt|;
name|b
operator|.
name|option
argument_list|(
name|ChannelOption
operator|.
name|SO_REUSEADDR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|.
name|childOption
argument_list|(
name|ChannelOption
operator|.
name|TCP_NODELAY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|.
name|childOption
argument_list|(
name|ChannelOption
operator|.
name|SO_REUSEADDR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|.
name|childOption
argument_list|(
name|ChannelOption
operator|.
name|SO_KEEPALIVE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|b
operator|.
name|childHandler
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
block|{
name|ChannelPipeline
name|p
init|=
name|ch
operator|.
name|pipeline
argument_list|()
decl_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ClientFilterHandler
argument_list|(
operator|new
name|ClientIpFilter
argument_list|(
name|builder
operator|.
name|allowedClientIPRanges
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sslContext
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|addLast
argument_list|(
literal|"ssl"
argument_list|,
name|sslContext
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
comment|// Decoders
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|LineBasedFrameDecoder
argument_list|(
literal|8192
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|StringDecoder
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
name|RequestDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|StateHandler
argument_list|(
name|builder
operator|.
name|stateConsumer
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|RequestObserverHandler
argument_list|(
name|builder
operator|.
name|observer
argument_list|)
argument_list|)
expr_stmt|;
comment|// Snappy Encoder
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|SnappyFrameEncoder
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use chunking transparently
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ChunkedWriteHandler
argument_list|()
argument_list|)
expr_stmt|;
comment|// Other Encoders
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetHeadResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetSegmentResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetBlobResponseEncoder
argument_list|(
name|builder
operator|.
name|blobChunkSize
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetReferencesResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|ResponseObserverHandler
argument_list|(
name|builder
operator|.
name|observer
argument_list|)
argument_list|)
expr_stmt|;
comment|// Handlers
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetHeadRequestHandler
argument_list|(
name|builder
operator|.
name|standbyHeadReader
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetSegmentRequestHandler
argument_list|(
name|builder
operator|.
name|standbySegmentReader
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetBlobRequestHandler
argument_list|(
name|builder
operator|.
name|standbyBlobReader
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|GetReferencesRequestHandler
argument_list|(
name|builder
operator|.
name|standbyReferencesReader
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
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|channelFuture
operator|=
name|b
operator|.
name|bind
argument_list|(
name|port
argument_list|)
expr_stmt|;
if|if
condition|(
name|channelFuture
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
name|onTimelyConnect
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|onConnectTimeOut
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|channelFuture
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|channelFuture
operator|.
name|channel
argument_list|()
operator|.
name|disconnect
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
literal|"Channel disconnected"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Channel disconnect timed out"
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|shutDown
argument_list|(
name|bossGroup
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Boss group shut down"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Boss group shutdown timed out"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shutDown
argument_list|(
name|workerGroup
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Worker group shut down"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Worker group shutdown timed out"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|shutDown
parameter_list|(
name|EventLoopGroup
name|group
parameter_list|)
block|{
return|return
name|group
operator|.
name|shutdownGracefully
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|awaitUninterruptibly
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
specifier|private
name|void
name|onTimelyConnect
parameter_list|()
block|{
if|if
condition|(
name|channelFuture
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Binding was successful"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|channelFuture
operator|.
name|cause
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|channelFuture
operator|.
name|cause
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|onConnectTimeOut
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Binding timed out, canceling"
argument_list|)
expr_stmt|;
name|channelFuture
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

