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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_comment
comment|/**  * Process all HTTP requests on a single socket.  */
end_comment

begin_class
specifier|public
class|class
name|HttpProcessor
block|{
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_SO_TIMEOUT
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SO_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_KEEP_ALIVE_REQUESTS
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|Socket
name|socket
decl_stmt|;
specifier|private
specifier|final
name|Servlet
name|servlet
decl_stmt|;
specifier|private
name|InputStream
name|socketIn
decl_stmt|;
specifier|private
name|OutputStream
name|socketOut
decl_stmt|;
specifier|private
specifier|final
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param socket socket      * @param servlet servlet to invoke for incoming requests      */
specifier|public
name|HttpProcessor
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|Servlet
name|servlet
parameter_list|)
block|{
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|this
operator|.
name|servlet
operator|=
name|servlet
expr_stmt|;
block|}
comment|/**      * Process all requests on a single socket.      *      * @throws IOException if an I/O error occurs      */
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|socketIn
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|socket
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|socketOut
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|socket
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|INITIAL_SO_TIMEOUT
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|requestNum
init|=
literal|0
init|;
condition|;
name|requestNum
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|process
argument_list|(
name|requestNum
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|requestNum
operator|==
literal|0
condition|)
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|DEFAULT_SO_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|socketOut
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|socketIn
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Process a single request.      *      * @param requestNum number of this request on the same persistent connection      * @return {@code true} if the connection should be kept alive;      *         {@code false} otherwise      *      * @throws IOException if an I/O error occurs      */
specifier|private
name|boolean
name|process
parameter_list|(
name|int
name|requestNum
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|request
operator|.
name|parse
argument_list|(
name|socketIn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|requestNum
operator|==
literal|0
condition|)
block|{
comment|// ignore errors on the very first request (might be wrong protocol)
return|return
literal|false
return|;
block|}
throw|throw
name|e
throw|;
block|}
try|try
block|{
name|boolean
name|keepAlive
init|=
name|request
operator|.
name|isKeepAlive
argument_list|()
operator|&&
operator|(
name|requestNum
operator|+
literal|1
operator|<
name|MAX_KEEP_ALIVE_REQUESTS
operator|)
decl_stmt|;
name|response
operator|.
name|recycle
argument_list|(
name|socketOut
argument_list|,
name|keepAlive
argument_list|)
expr_stmt|;
name|servlet
operator|.
name|service
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
name|keepAlive
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

