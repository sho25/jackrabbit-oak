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
name|jcr
operator|.
name|xml
package|;
end_package

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
name|ByteArrayInputStream
import|;
end_import

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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|xml
operator|.
name|TextValue
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
name|util
operator|.
name|Base64
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
name|util
operator|.
name|TransientFileFactory
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

begin_comment
comment|/**  * {@code BufferedStringValue} represents an appendable  * serialized value that is either buffered in-memory or backed  * by a temporary file if its size exceeds a certain limit.  *<p>  *<b>Important:</b> Note that in order to free resources  * {@code {@link #dispose()}} should be called as soon as  * {@code BufferedStringValue} instance is not used anymore.  */
end_comment

begin_class
class|class
name|BufferedStringValue
implements|implements
name|TextValue
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
name|BufferedStringValue
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The maximum size for buffering data in memory.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_BUFFER_SIZE
init|=
literal|0x10000
decl_stmt|;
comment|/**      * The in-memory buffer.      */
specifier|private
name|StringWriter
name|buffer
decl_stmt|;
comment|/**      * The number of characters written so far.      * If the in-memory buffer is used, this is position within buffer (size of actual data in buffer)      */
specifier|private
name|long
name|length
decl_stmt|;
comment|/**      * Backing temporary file created when size of data exceeds      * MAX_BUFFER_SIZE.      */
specifier|private
name|File
name|tmpFile
decl_stmt|;
comment|/**      * Writer used to write to tmpFile.      */
specifier|private
name|Writer
name|writer
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
comment|/**      * Whether the value is base64 encoded.      */
specifier|private
specifier|final
name|boolean
name|base64
decl_stmt|;
comment|/**      * Constructs a new empty {@code BufferedStringValue}.      *      * @param valueFactory The value factory      * @param namePathMapper the name/path mapper      */
specifier|protected
name|BufferedStringValue
parameter_list|(
name|ValueFactory
name|valueFactory
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|boolean
name|base64
parameter_list|)
block|{
name|buffer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|tmpFile
operator|=
literal|null
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|valueFactory
expr_stmt|;
name|this
operator|.
name|base64
operator|=
name|base64
expr_stmt|;
block|}
comment|/**      * Returns the length of the serialized value.      *      * @return the length of the serialized value      * @throws IOException if an I/O error occurs      */
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
try|try
block|{
return|return
name|retrieveString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"could not retrieve string value"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
specifier|private
name|String
name|retrieveString
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|value
init|=
name|retrieve
argument_list|()
decl_stmt|;
if|if
condition|(
name|base64
condition|)
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Base64
operator|.
name|decode
argument_list|(
name|value
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|value
operator|=
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Retrieves the serialized value.      *      * @return the serialized value      * @throws IOException if an I/O error occurs      */
specifier|public
name|String
name|retrieve
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
comment|// close writer first
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|tmpFile
operator|.
name|length
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"size of value is too big, use reader()"
argument_list|)
throw|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
operator|(
name|int
operator|)
name|length
argument_list|)
decl_stmt|;
name|char
index|[]
name|chunk
init|=
operator|new
name|char
index|[
literal|0x2000
index|]
decl_stmt|;
name|Reader
name|reader
init|=
name|openReader
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|read
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|reader
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Reader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InputStreamReader
argument_list|(
name|openStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
specifier|private
name|InputStream
name|openStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tmpFile
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|InputStream
name|stream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|base64
condition|)
block|{
return|return
operator|new
name|Base64ReaderInputStream
argument_list|(
name|reader
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|retrieve
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
comment|// close writer first
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|openStream
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a {@code Reader} for reading the serialized value.      *      * @return a {@code Reader} for reading the serialized value.      * @throws IOException if an I/O error occurs      */
specifier|public
name|Reader
name|reader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|retrieve
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
comment|// close writer first
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|openReader
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Append a portion of an array of characters.      *      * @param chars the characters to be appended      * @param start the index of the first character to append      * @param len   the number of characters to append      * @throws IOException if an I/O error occurs      */
specifier|public
name|void
name|append
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|length
operator|+
name|len
operator|>
name|MAX_BUFFER_SIZE
condition|)
block|{
comment|// threshold for keeping data in memory exceeded;
comment|// create temp file and spool buffer contents
name|TransientFileFactory
name|fileFactory
init|=
name|TransientFileFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|tmpFile
operator|=
name|fileFactory
operator|.
name|createTransientFile
argument_list|(
literal|"txt"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|BufferedOutputStream
name|fout
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|fout
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|chars
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// reset the in-memory buffer
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|write
argument_list|(
name|chars
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|chars
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
throw|;
block|}
name|length
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Close this value. Once a value has been closed,      * further append() invocations will cause an IOException to be thrown.      *      * @throws IOException if an I/O error occurs      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
comment|// nop
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
throw|;
block|}
block|}
comment|//--------------------------------------------------------< TextValue>
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|Value
name|getValue
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
if|if
condition|(
name|targetType
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|stream
argument_list|()
argument_list|)
return|;
block|}
name|String
name|jcrValue
init|=
name|retrieveString
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetType
operator|==
name|PropertyType
operator|.
name|NAME
condition|)
block|{
name|jcrValue
operator|=
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|jcrValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|targetType
operator|==
name|PropertyType
operator|.
name|PATH
condition|)
block|{
name|jcrValue
operator|=
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|jcrValue
argument_list|)
expr_stmt|;
block|}
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|jcrValue
argument_list|,
name|targetType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"failed to retrieve serialized value"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tmpFile
operator|=
literal|null
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Problem disposing property value"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"this instance has already been disposed"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This class converts the text read Converts a base64 reader to an input stream.      */
specifier|private
specifier|static
class|class
name|Base64ReaderInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|char
index|[]
name|chars
decl_stmt|;
specifier|private
specifier|final
name|ByteArrayOutputStream
name|out
decl_stmt|;
specifier|private
specifier|final
name|Reader
name|reader
decl_stmt|;
specifier|private
name|int
name|pos
decl_stmt|;
specifier|private
name|int
name|remaining
decl_stmt|;
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|public
name|Base64ReaderInputStream
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|chars
operator|=
operator|new
name|char
index|[
name|BUFFER_SIZE
index|]
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|out
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|fillBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|reader
operator|.
name|read
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
name|remaining
operator|=
operator|-
literal|1
expr_stmt|;
return|return;
block|}
name|Base64
operator|.
name|decode
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|out
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|remaining
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|remaining
operator|==
literal|0
condition|)
block|{
name|fillBuffer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|remaining
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|remaining
operator|--
expr_stmt|;
return|return
name|buffer
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
return|;
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

