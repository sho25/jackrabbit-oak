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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|util
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
name|HttpExecutor
name|executor
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
specifier|private
name|InputStream
name|resultIn
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|/**      * Create a new instance of this class.      *       * @param executor executor      * @param command command name      */
specifier|public
name|Request
parameter_list|(
name|HttpExecutor
name|executor
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
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
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|executed
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
name|resultIn
operator|=
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
expr_stmt|;
block|}
comment|/**      * Return a string from the result stream. Automatically executes      * the request first.      *       * @return string      * @throws IOException if an I/O error occurs      */
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|IOException
block|{
name|execute
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|toByteArray
argument_list|(
name|resultIn
argument_list|)
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
name|execute
argument_list|()
expr_stmt|;
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
name|execute
argument_list|()
expr_stmt|;
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
name|execute
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|len
condition|)
block|{
name|int
name|n
init|=
name|resultIn
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|count
argument_list|,
name|len
operator|-
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|count
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|count
operator|==
literal|0
operator|&&
name|len
operator|!=
literal|0
condition|?
operator|-
literal|1
else|:
name|count
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|resultIn
argument_list|)
expr_stmt|;
name|executor
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

