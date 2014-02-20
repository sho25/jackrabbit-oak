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
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|commons
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Contains the details of a request to some remote {@code MicroKernel}  * implementation.  */
end_comment

begin_class
class|class
name|Request
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|SocketFactory
name|socketFactory
decl_stmt|;
specifier|private
specifier|final
name|InetSocketAddress
name|socketAddress
decl_stmt|;
specifier|private
specifier|final
name|String
name|command
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|InputStream
name|in
decl_stmt|;
comment|/**      * Create a new instance of this class.      *       * @param socketFactory socket factory      * @param socketAddress server address      * @param command command name      */
specifier|public
name|Request
parameter_list|(
name|SocketFactory
name|socketFactory
parameter_list|,
name|InetSocketAddress
name|socketAddress
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|socketFactory
operator|=
name|socketFactory
expr_stmt|;
name|this
operator|.
name|socketAddress
operator|=
name|socketAddress
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
comment|/**      * Add a string parameter.      *      * @param name name      * @param value value, if {@code null} the call is ignored      */
specifier|public
name|Request
name|addParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Add an integer parameter, equivalent to       * {@code addParameter(name, String.valueOf(value))}.      *      * @param name name      * @param value value      */
specifier|public
name|Request
name|addParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a long parameter, equivalent to       * {@code addParameter(name, String.valueOf(value))}.      *      * @param name name      * @param value value      */
specifier|public
name|Request
name|addParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a file parameter that will be transmitted as form data.       *       * @param name name      * @param in input stream      */
specifier|public
name|Request
name|addFileParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Execute the request.      *       * @throws IOException if an I/O error occurs      */
specifier|private
name|byte
index|[]
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpExecutor
name|executor
init|=
operator|new
name|HttpExecutor
argument_list|(
name|socketFactory
argument_list|,
name|socketAddress
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|executor
operator|.
name|execute
argument_list|(
name|command
argument_list|,
name|params
argument_list|,
name|in
argument_list|)
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toByteArray
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|executor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Return a string from the result stream. Automatically executes      * the request first.      *       * @return string      * @throws IOException if an I/O error occurs      */
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|String
argument_list|(
name|execute
argument_list|()
argument_list|,
literal|"8859_1"
argument_list|)
return|;
block|}
comment|/**      * Return a boolean from the result stream, equivalent to       * {@code Boolean.parseBoolean(getString())}.      * Automatically executes the request first.      *      * @return boolean      * @throws IOException if an I/O error occurs      */
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Return a long from the result stream, equivalent to       * {@code Long.parseLong(getString())}.      * Automatically executes the request first.      *      * @return boolean      * @throws IOException if an I/O error occurs      */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Read bytes from the result stream. Automatically executes the      * request first.      *      * @param b buffer      * @param off offset      * @param len length      * @return number of bytes or {@code -1} if no more bytes are available      *      * @throws IOException if an I/O error occurs      */
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|byte
index|[]
name|bytes
init|=
name|execute
argument_list|()
decl_stmt|;
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// do nothing
block|}
block|}
end_class

end_unit

