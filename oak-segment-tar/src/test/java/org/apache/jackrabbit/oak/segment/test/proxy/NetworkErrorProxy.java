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
name|test
operator|.
name|proxy
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
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
class|class
name|NetworkErrorProxy
implements|implements
name|Closeable
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
name|NetworkErrorProxy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|bossThreadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|workerThreadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_FLIP_POSITION
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP_POSITION
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP_LENGTH
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|int
name|inboundPort
decl_stmt|;
specifier|private
specifier|final
name|int
name|outboundPort
decl_stmt|;
specifier|private
specifier|final
name|String
name|host
decl_stmt|;
specifier|private
name|int
name|flipPosition
init|=
name|DEFAULT_FLIP_POSITION
decl_stmt|;
specifier|private
name|int
name|skipPosition
init|=
name|DEFAULT_SKIP_POSITION
decl_stmt|;
specifier|private
name|int
name|skipLength
init|=
name|DEFAULT_SKIP_LENGTH
decl_stmt|;
specifier|private
name|Channel
name|server
decl_stmt|;
specifier|private
name|EventLoopGroup
name|boss
init|=
operator|new
name|NioEventLoopGroup
argument_list|(
literal|0
argument_list|,
name|r
lambda|->
block|{
return|return
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"proxy-boss-%d"
argument_list|,
name|bossThreadNumber
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
specifier|private
name|EventLoopGroup
name|worker
init|=
operator|new
name|NioEventLoopGroup
argument_list|(
literal|0
argument_list|,
name|r
lambda|->
block|{
return|return
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"proxy-worker-%d"
argument_list|,
name|workerThreadNumber
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
specifier|public
name|NetworkErrorProxy
parameter_list|(
name|int
name|inboundPort
parameter_list|,
name|String
name|outboundHost
parameter_list|,
name|int
name|outboundPort
parameter_list|)
block|{
name|this
operator|.
name|inboundPort
operator|=
name|inboundPort
expr_stmt|;
name|this
operator|.
name|outboundPort
operator|=
name|outboundPort
expr_stmt|;
name|this
operator|.
name|host
operator|=
name|outboundHost
expr_stmt|;
block|}
specifier|public
name|void
name|skipBytes
parameter_list|(
name|int
name|pos
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|skipPosition
operator|=
name|pos
expr_stmt|;
name|skipLength
operator|=
name|n
expr_stmt|;
block|}
specifier|public
name|void
name|flipByte
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|flipPosition
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting proxy with flip={}, skip={},{}"
argument_list|,
name|flipPosition
argument_list|,
name|skipPosition
argument_list|,
name|skipLength
argument_list|)
expr_stmt|;
name|ServerBootstrap
name|b
init|=
operator|new
name|ServerBootstrap
argument_list|()
operator|.
name|group
argument_list|(
name|boss
argument_list|,
name|worker
argument_list|)
operator|.
name|channel
argument_list|(
name|NioServerSocketChannel
operator|.
name|class
argument_list|)
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
throws|throws
name|Exception
block|{
name|ch
operator|.
name|pipeline
argument_list|()
operator|.
name|addLast
argument_list|(
operator|new
name|ForwardHandler
argument_list|(
name|host
argument_list|,
name|outboundPort
argument_list|,
name|flipPosition
argument_list|,
name|skipPosition
argument_list|,
name|skipLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|ChannelFuture
name|f
init|=
name|b
operator|.
name|bind
argument_list|(
name|this
operator|.
name|inboundPort
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
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
literal|"Bound on port {}"
argument_list|,
name|inboundPort
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Binding on port {} timed out"
argument_list|,
name|inboundPort
argument_list|)
expr_stmt|;
block|}
name|server
operator|=
name|f
operator|.
name|channel
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|Exception
block|{
name|flipPosition
operator|=
name|DEFAULT_FLIP_POSITION
expr_stmt|;
name|skipPosition
operator|=
name|DEFAULT_SKIP_POSITION
expr_stmt|;
name|skipLength
operator|=
name|DEFAULT_SKIP_LENGTH
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|server
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
name|connect
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
name|server
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|server
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
if|if
condition|(
name|boss
operator|.
name|shutdownGracefully
argument_list|(
literal|0
argument_list|,
literal|150
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
name|worker
operator|.
name|shutdownGracefully
argument_list|(
literal|0
argument_list|,
literal|150
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
block|}
end_class

end_unit

