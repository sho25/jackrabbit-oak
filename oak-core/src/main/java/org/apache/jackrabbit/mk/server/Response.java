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
name|OutputStream
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

begin_import
import|import static
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
name|ChunkedInputStream
operator|.
name|MAX_CHUNK_SIZE
import|;
end_import

begin_comment
comment|/**  * HTTP Response implementation.  */
end_comment

begin_class
class|class
name|Response
implements|implements
name|Closeable
block|{
specifier|private
name|OutputStream
name|out
decl_stmt|;
specifier|private
name|boolean
name|keepAlive
decl_stmt|;
specifier|private
name|boolean
name|headersSent
decl_stmt|;
specifier|private
name|boolean
name|committed
decl_stmt|;
specifier|private
name|boolean
name|chunked
decl_stmt|;
specifier|private
name|int
name|statusCode
decl_stmt|;
specifier|private
name|String
name|contentType
decl_stmt|;
specifier|private
specifier|final
name|BodyOutputStream
name|bodyOut
init|=
operator|new
name|BodyOutputStream
argument_list|()
decl_stmt|;
specifier|private
name|OutputStream
name|respOut
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
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
comment|/**      * Recycle this instance, using another output stream and a keep-alive flag.      *       * @param out output stream      * @param keepAlive whether to keep alive the connection      */
name|void
name|recycle
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|boolean
name|keepAlive
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|keepAlive
operator|=
name|keepAlive
expr_stmt|;
name|headersSent
operator|=
name|committed
operator|=
name|chunked
operator|=
literal|false
expr_stmt|;
name|statusCode
operator|=
literal|0
expr_stmt|;
name|contentType
operator|=
literal|null
expr_stmt|;
name|bodyOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|respOut
operator|=
literal|null
expr_stmt|;
name|headers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Return the status message associated with a status code.      *       * @param sc status code      * @return associated status message      */
specifier|private
specifier|static
name|String
name|getStatusMsg
parameter_list|(
name|int
name|sc
parameter_list|)
block|{
switch|switch
condition|(
name|sc
condition|)
block|{
case|case
literal|200
case|:
return|return
literal|"OK"
return|;
case|case
literal|400
case|:
return|return
literal|"Bad request"
return|;
case|case
literal|401
case|:
return|return
literal|"Unauthorized"
return|;
case|case
literal|404
case|:
return|return
literal|"Not found"
return|;
default|default:
return|return
literal|"Internal server error"
return|;
block|}
block|}
specifier|private
name|void
name|sendHeaders
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|headersSent
condition|)
block|{
return|return;
block|}
name|headersSent
operator|=
literal|true
expr_stmt|;
name|int
name|statusCode
init|=
name|this
operator|.
name|statusCode
decl_stmt|;
if|if
condition|(
name|statusCode
operator|==
literal|0
condition|)
block|{
name|statusCode
operator|=
literal|200
expr_stmt|;
block|}
name|String
name|msg
init|=
name|getStatusMsg
argument_list|(
name|statusCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|respOut
operator|==
literal|null
condition|)
block|{
comment|/* Generate minimal body  */
name|String
name|body
init|=
name|String
operator|.
name|format
argument_list|(
literal|"<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">"
operator|+
literal|"<html><head>"
operator|+
literal|"<title>%d %s</title>"
operator|+
literal|"</head><body>"
operator|+
literal|"<h1>%s</h1>"
operator|+
literal|"</body></html>"
argument_list|,
name|statusCode
argument_list|,
name|msg
argument_list|,
name|msg
argument_list|)
decl_stmt|;
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|writeLine
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"HTTP/1.1 %d %s"
argument_list|,
name|statusCode
argument_list|,
name|msg
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|committed
condition|)
block|{
name|writeLine
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Content-Length: %d"
argument_list|,
name|bodyOut
operator|.
name|getCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|chunked
operator|=
literal|true
expr_stmt|;
name|writeLine
argument_list|(
literal|"Transfer-Encoding: chunked"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|writeLine
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Content-Type: %s"
argument_list|,
name|contentType
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|keepAlive
condition|)
block|{
name|writeLine
argument_list|(
literal|"Connection: Close"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|headers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|header
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeLine
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s: %s"
argument_list|,
name|header
operator|.
name|getKey
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writeLine
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|committed
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|sendHeaders
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|respOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeLine
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|out
operator|.
name|write
argument_list|(
name|s
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\r\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write some bytes to the body of the response.      * @param b buffer      * @param off offset      * @param len length      * @throws IOException if an I/O error occurs      */
name|void
name|writeBody
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
name|out
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|sendHeaders
argument_list|()
expr_stmt|;
if|if
condition|(
name|chunked
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%04X\r\n"
argument_list|,
name|len
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|chunked
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
operator|(
literal|"\r\n"
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
block|{
if|if
condition|(
name|respOut
operator|==
literal|null
condition|)
block|{
name|respOut
operator|=
name|bodyOut
expr_stmt|;
block|}
return|return
name|respOut
return|;
block|}
specifier|public
name|void
name|setStatusCode
parameter_list|(
name|int
name|statusCode
parameter_list|)
block|{
name|this
operator|.
name|statusCode
operator|=
name|statusCode
expr_stmt|;
block|}
specifier|public
name|void
name|addHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
literal|"8859_1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Internal<code>OutputStream</code> passed to servlet handlers.      */
class|class
name|BodyOutputStream
extends|extends
name|OutputStream
block|{
comment|/**          * Buffer size chosen intentionally to not exceed maximum chunk          * size we'd like to transmit.          */
specifier|private
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|MAX_CHUNK_SIZE
index|]
decl_stmt|;
specifier|private
name|int
name|offset
decl_stmt|;
comment|/**          * Return the number of valid bytes in the buffer.          *           * @return number of bytes           */
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|==
name|buf
operator|.
name|length
condition|)
block|{
name|writeBody
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|buf
index|[
name|offset
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
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
if|if
condition|(
name|offset
operator|==
name|buf
operator|.
name|length
condition|)
block|{
name|writeBody
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
operator|-
name|count
argument_list|,
name|buf
operator|.
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|count
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|count
operator|+=
name|n
expr_stmt|;
name|offset
operator|+=
name|n
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|writeBody
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|offset
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|writeBody
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

