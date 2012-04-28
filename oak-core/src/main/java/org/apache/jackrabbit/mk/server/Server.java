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
name|mk
operator|.
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|concurrent
operator|.
name|ExecutorService
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
name|Executors
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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
name|mk
operator|.
name|MicroKernelFactory
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
import|;
end_import

begin_comment
comment|/**  * Server exposing a {@code MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|Server
block|{
comment|/** java.net.ServerSocket's default backlog size. */
specifier|private
specifier|static
specifier|final
name|int
name|BACKLOG
init|=
literal|50
decl_stmt|;
specifier|private
specifier|final
name|ServerSocketFactory
name|ssFactory
decl_stmt|;
specifier|private
name|AtomicReference
argument_list|<
name|MicroKernel
argument_list|>
name|mkref
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|ServerSocket
name|ss
decl_stmt|;
specifier|private
name|ExecutorService
name|es
decl_stmt|;
specifier|private
name|int
name|port
decl_stmt|;
specifier|private
name|InetAddress
name|addr
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param mk micro kernel      */
specifier|public
name|Server
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
argument_list|(
name|mk
argument_list|,
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|mkref
operator|=
operator|new
name|AtomicReference
argument_list|<
name|MicroKernel
argument_list|>
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new instance of this class.      *      * @param mk micro kernel      */
specifier|public
name|Server
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|ServerSocketFactory
name|ssFactory
parameter_list|)
block|{
name|this
operator|.
name|mkref
operator|=
operator|new
name|AtomicReference
argument_list|<
name|MicroKernel
argument_list|>
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|this
operator|.
name|ssFactory
operator|=
name|ssFactory
expr_stmt|;
block|}
comment|/**      * Set port number to listen to.      *      * @param port port numbern      * @throws IllegalStateException if the server is already started      */
specifier|public
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Server already started."
argument_list|)
throw|;
block|}
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
comment|/**      * Set bind address.      */
specifier|public
name|void
name|setBindAddress
parameter_list|(
name|InetAddress
name|addr
parameter_list|)
throws|throws
name|IllegalStateException
block|{
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Server already started."
argument_list|)
throw|;
block|}
name|this
operator|.
name|addr
operator|=
name|addr
expr_stmt|;
block|}
comment|/**      * Start this server.      *      * @throws IOException if an I/O error occurs      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
name|ss
operator|=
name|createServerSocket
argument_list|()
expr_stmt|;
name|es
operator|=
name|createExecutorService
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|accept
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"Acceptor"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|void
name|accept
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
specifier|final
name|Socket
name|socket
init|=
name|ss
operator|.
name|accept
argument_list|()
decl_stmt|;
name|es
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|process
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
block|}
specifier|private
name|ServerSocket
name|createServerSocket
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ssFactory
operator|.
name|createServerSocket
argument_list|(
name|port
argument_list|,
name|BACKLOG
argument_list|,
name|addr
argument_list|)
return|;
block|}
specifier|private
name|ExecutorService
name|createExecutorService
parameter_list|()
block|{
return|return
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
return|;
block|}
comment|/**      * Process a connection attempt by a client.      *      * @param socket client socket      */
name|void
name|process
parameter_list|(
name|Socket
name|socket
parameter_list|)
block|{
try|try
block|{
name|socket
operator|.
name|setTcpNoDelay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
name|HttpProcessor
name|processor
init|=
operator|new
name|HttpProcessor
argument_list|(
name|socket
argument_list|,
operator|new
name|Servlet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|service
parameter_list|(
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Server
operator|.
name|this
operator|.
name|service
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Service a request.      *      * @param request request      * @param response response      * @throws IOException if an I/O error occurs      */
name|void
name|service
parameter_list|(
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
name|MicroKernelServlet
operator|.
name|INSTANCE
operator|.
name|service
argument_list|(
name|mkref
operator|.
name|get
argument_list|()
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileServlet
operator|.
name|INSTANCE
operator|.
name|service
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Return the server's local socket address.      *      * @return socket address or {@code null} if the server is not started      */
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
if|if
condition|(
operator|!
name|started
operator|.
name|get
argument_list|()
operator|||
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SocketAddress
name|address
init|=
name|ss
operator|.
name|getLocalSocketAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|instanceof
name|InetSocketAddress
condition|)
block|{
name|InetSocketAddress
name|isa
init|=
operator|(
name|InetSocketAddress
operator|)
name|address
decl_stmt|;
if|if
condition|(
name|isa
operator|.
name|getAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
try|try
block|{
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|,
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|isa
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Stop this server.      */
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
operator|!
name|stopped
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
name|MicroKernel
name|mk
init|=
name|mkref
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|es
operator|!=
literal|null
condition|)
block|{
name|es
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"usage: %s microkernel-url [port] [bindaddr]"
argument_list|,
name|Server
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|MicroKernel
name|mk
init|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
specifier|final
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
name|mk
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
name|server
operator|.
name|setPort
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|server
operator|.
name|setPort
argument_list|(
literal|28080
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|3
condition|)
block|{
name|server
operator|.
name|setBindAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"ShutdownHook"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

