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
operator|.
name|client
package|;
end_package

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
name|compression
operator|.
name|SnappyFramedDecoder
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
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|concurrent
operator|.
name|DefaultEventExecutorGroup
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
name|concurrent
operator|.
name|EventExecutorGroup
import|;
end_import

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
name|TimeUnit
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
name|failover
operator|.
name|CommunicationObserver
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
name|codec
operator|.
name|RecordIdDecoder
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
name|store
operator|.
name|FailoverStore
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

begin_class
specifier|public
specifier|final
class|class
name|FailoverClient
implements|implements
name|FailoverStatusMBean
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
literal|"failOverID"
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
name|FailoverClient
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
name|boolean
name|checkChecksums
decl_stmt|;
specifier|private
name|int
name|readTimeoutMs
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|FailoverStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|CommunicationObserver
name|observer
decl_stmt|;
specifier|private
name|FailoverClientHandler
name|handler
decl_stmt|;
specifier|private
name|EventLoopGroup
name|group
decl_stmt|;
specifier|private
name|EventExecutorGroup
name|executor
decl_stmt|;
specifier|private
name|SslContext
name|sslContext
decl_stmt|;
specifier|private
name|boolean
name|active
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|running
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
specifier|public
name|FailoverClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|SSLException
block|{
name|this
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|store
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FailoverClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|SegmentStore
name|store
parameter_list|,
name|boolean
name|secure
parameter_list|)
throws|throws
name|SSLException
block|{
name|this
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|store
argument_list|,
name|secure
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FailoverClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|SegmentStore
name|store
parameter_list|,
name|boolean
name|secure
parameter_list|,
name|boolean
name|checksums
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
name|checkChecksums
operator|=
name|checksums
expr_stmt|;
if|if
condition|(
name|secure
condition|)
block|{
name|this
operator|.
name|sslContext
operator|=
name|SslContext
operator|.
name|newClientContext
argument_list|(
name|InsecureTrustManagerFactory
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|store
operator|=
operator|new
name|FailoverStore
argument_list|(
name|store
argument_list|)
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
name|FailoverStatusMBean
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
literal|"can register failover status mbean"
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
name|FailoverStatusMBean
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
literal|"can unregister failover status mbean"
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
if|if
condition|(
name|group
operator|!=
literal|null
operator|&&
operator|!
name|group
operator|.
name|isShuttingDown
argument_list|()
condition|)
block|{
name|group
operator|.
name|shutdownGracefully
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|executor
operator|!=
literal|null
operator|&&
operator|!
name|executor
operator|.
name|isShuttingDown
argument_list|()
condition|)
block|{
name|executor
operator|.
name|shutdownGracefully
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|syncUninterruptibly
argument_list|()
expr_stmt|;
block|}
name|state
operator|=
name|STATUS_CLOSED
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Bootstrap
name|b
decl_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|sync
init|)
block|{
if|if
condition|(
name|this
operator|.
name|active
condition|)
block|{
return|return;
block|}
name|state
operator|=
name|STATUS_STARTING
expr_stmt|;
name|executor
operator|=
operator|new
name|DefaultEventExecutorGroup
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|FailoverClientHandler
argument_list|(
name|this
operator|.
name|store
argument_list|,
name|executor
argument_list|,
name|this
operator|.
name|observer
argument_list|)
expr_stmt|;
name|group
operator|=
operator|new
name|NioEventLoopGroup
argument_list|()
expr_stmt|;
name|b
operator|=
operator|new
name|Bootstrap
argument_list|()
expr_stmt|;
name|b
operator|.
name|group
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|b
operator|.
name|channel
argument_list|(
name|NioSocketChannel
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
name|CONNECT_TIMEOUT_MILLIS
argument_list|,
name|readTimeoutMs
argument_list|)
expr_stmt|;
name|b
operator|.
name|option
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
name|option
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
name|sslContext
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|addLast
argument_list|(
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
comment|// WriteTimeoutHandler& ReadTimeoutHandler
name|p
operator|.
name|addLast
argument_list|(
literal|"readTimeoutHandler"
argument_list|,
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
if|if
condition|(
name|FailoverClient
operator|.
name|this
operator|.
name|checkChecksums
condition|)
block|{
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|SnappyFramedDecoder
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|addLast
argument_list|(
operator|new
name|RecordIdDecoder
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addLast
argument_list|(
name|executor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|state
operator|=
name|STATUS_RUNNING
expr_stmt|;
name|this
operator|.
name|running
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|active
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
comment|// Start the client.
name|ChannelFuture
name|f
init|=
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
decl_stmt|;
comment|// Wait until the connection is closed.
name|f
operator|.
name|channel
argument_list|()
operator|.
name|closeFuture
argument_list|()
operator|.
name|sync
argument_list|()
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
literal|"Failed synchronizing state."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|stop
argument_list|()
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|running
condition|)
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|//TODO running flag doesn't make sense this way, since run() is usually scheduled to be called repeatedly.
if|if
condition|(
name|running
condition|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|state
operator|=
name|STATUS_STOPPED
expr_stmt|;
block|}
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
block|}
end_class

end_unit

