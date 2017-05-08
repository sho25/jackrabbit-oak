begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|commons
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
comment|/**  * Input/output utility methods.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|IOUtils
block|{
comment|/**      * Avoid instantiation      */
specifier|private
name|IOUtils
parameter_list|()
block|{     }
comment|/**      * Try to read the given number of bytes to the buffer. This method reads      * until the maximum number of bytes have been read or until the end of      * file.      *      * @param in     the input stream      * @param buffer the output buffer      * @param off    the offset in the buffer      * @param max    the number of bytes to read at most      * @return the number of bytes read, 0 meaning EOF      * @throws java.io.IOException If an error occurs.      */
specifier|public
specifier|static
name|int
name|readFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|l
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|result
operator|+=
name|l
expr_stmt|;
name|off
operator|+=
name|l
expr_stmt|;
name|len
operator|-=
name|l
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Skip a number of bytes in an input stream.      *      * @param in   the input stream      * @param skip the number of bytes to skip      * @throws EOFException if the end of file has been reached before all bytes      *                      could be skipped      * @throws IOException  if an IO exception occurred while skipping      */
specifier|public
specifier|static
name|void
name|skipFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|skip
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|skip
operator|>
literal|0
condition|)
block|{
name|long
name|skipped
init|=
name|in
operator|.
name|skip
argument_list|(
name|skip
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipped
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|skip
operator|-=
name|skipped
expr_stmt|;
block|}
block|}
comment|/**      * Write a String. This will first write the length as 4 bytes, and then the      * UTF-8 encoded string.      *      * @param out the data output stream      * @param s   the string (maximum length about 2 GB)      * @throws IOException if an IO exception occurred while writing      */
specifier|public
specifier|static
name|void
name|writeString
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
name|out
argument_list|,
name|s
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read a String. This will first read the length as 4 bytes, and then the      * UTF-8 encoded string.      *      * @param in the data input stream      * @return the string      * @throws IOException if an IO exception occurred while reading      */
specifier|public
specifier|static
name|String
name|readString
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|String
argument_list|(
name|readBytes
argument_list|(
name|in
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
comment|/**      * Write a byte array. This will first write the length as 4 bytes, and then      * the actual bytes.      *      * @param out  the data output stream      * @param data the byte array      * @throws IOException if an IO exception occurred while writing.      */
specifier|public
specifier|static
name|void
name|writeBytes
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVarInt
argument_list|(
name|out
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read a byte array. This will first read the length as 4 bytes, and then      * the actual bytes.      *      * @param in the data input stream      * @return the bytes      * @throws IOException if an IO exception occurred while reading from the stream.      */
specifier|public
specifier|static
name|byte
index|[]
name|readBytes
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|readVarInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|len
condition|;
control|)
block|{
name|int
name|l
init|=
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|data
operator|.
name|length
operator|-
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|pos
operator|+=
name|l
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
comment|/**      * Write a variable size integer. Negative values need 5 bytes.      *      * @param out the output stream      * @param x   the value      * @throws IOException if an IO exception occurred while writing.      */
specifier|public
specifier|static
name|void
name|writeVarInt
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|x
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|x
operator|&
operator|~
literal|0x7f
operator|)
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
operator|(
name|x
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
expr_stmt|;
name|x
operator|>>>=
literal|7
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read a variable size integer.      *      * @param in the input stream      * @return the integer      * @throws IOException if an IO exception occurred while reading.      */
specifier|public
specifier|static
name|int
name|readVarInt
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|x
init|=
operator|(
name|byte
operator|)
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|>=
literal|0
condition|)
block|{
return|return
name|x
return|;
block|}
name|x
operator|&=
literal|0x7f
expr_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|7
init|;
condition|;
name|s
operator|+=
literal|7
control|)
block|{
name|int
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|b
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
name|x
operator||=
operator|(
name|b
operator|&
literal|0x7f
operator|)
operator|<<
name|s
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
return|return
name|x
return|;
block|}
block|}
block|}
comment|/**      * Write a variable size long.      * Negative values need 10 bytes.      *      * @param out the output stream      * @param x   the value      * @throws IOException if an IO exception occurred while writing.      */
specifier|public
specifier|static
name|void
name|writeVarLong
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|long
name|x
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|x
operator|&
operator|~
literal|0x7f
operator|)
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|x
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|>>>=
literal|7
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
name|x
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write a long (8 bytes).      *      * @param out the output stream      * @param x   the value      * @throws IOException if an IO exception occurred while writing.      */
specifier|public
specifier|static
name|void
name|writeLong
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|long
name|x
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|out
argument_list|,
call|(
name|int
call|)
argument_list|(
name|x
operator|>>>
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|out
argument_list|,
operator|(
name|int
operator|)
name|x
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read a long (8 bytes).      *      * @param in the input stream      * @return the value      * @throws IOException if an IO exception occurred while reading.      */
specifier|public
specifier|static
name|long
name|readLong
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
call|(
name|long
call|)
argument_list|(
name|readInt
argument_list|(
name|in
argument_list|)
argument_list|)
operator|<<
literal|32
operator|)
operator|+
operator|(
name|readInt
argument_list|(
name|in
argument_list|)
operator|&
literal|0xffffffffL
operator|)
return|;
block|}
comment|/**      * Write an integer (4 bytes).      *      * @param out the output stream      * @param x   the value      * @throws IOException if an IO exception occurred while writing.      */
specifier|public
specifier|static
name|void
name|writeInt
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|x
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
name|x
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read an integer (4 bytes).      *      * @param in the input stream      * @return the value      * @throws IOException if an IO exception occurred while reading.      */
specifier|public
specifier|static
name|int
name|readInt
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0xff
operator|)
return|;
block|}
comment|/**      * Read a variable size long.      *      * @param in the input stream      * @return the long      * @throws IOException if an IO exception occurred while reading.      */
specifier|public
specifier|static
name|long
name|readVarLong
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|x
init|=
operator|(
name|byte
operator|)
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|>=
literal|0
condition|)
block|{
return|return
name|x
return|;
block|}
name|x
operator|&=
literal|0x7f
expr_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|7
init|;
condition|;
name|s
operator|+=
literal|7
control|)
block|{
name|long
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|b
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
name|x
operator||=
operator|(
name|b
operator|&
literal|0x7f
operator|)
operator|<<
name|s
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
return|return
name|x
return|;
block|}
block|}
block|}
comment|/**      * Get the value that is equal or higher than this value, and that is a      * power of two.  The returned value will be in the range [0, 2^31].      * If the input is less than zero, the result of 1 is returned (powers of      * negative numbers are not integer values).      *      * @param x the original value.      * @return the next power of two value.  Results are always in the      * range [0, 2^31].      */
specifier|public
specifier|static
name|long
name|nextPowerOf2
parameter_list|(
name|int
name|x
parameter_list|)
block|{
name|long
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|x
condition|)
block|{
name|i
operator|+=
name|i
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/**      * Unconditionally close a {@code Closeable}.      *<p>      * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored.      * This is typically used in finally blocks.      *      * @param closeable the object to close, may be null or already closed      */
specifier|public
specifier|static
name|void
name|closeQuietly
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|closeable
operator|!=
literal|null
condition|)
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|/**      * Unconditionally close a {@code Socket}.      *<p>      * Equivalent to {@link Socket#close()}, except any exceptions will be ignored.      * This is typically used in finally blocks.      *      * @param sock the Socket to close, may be null or already closed      */
specifier|public
specifier|static
name|void
name|closeQuietly
parameter_list|(
name|Socket
name|sock
parameter_list|)
block|{
if|if
condition|(
name|sock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|sock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
comment|/**      * Copy bytes from an {@code InputStream} to an      * {@code OutputStream}.      *<p>      * This method buffers the input internally, so there is no need to use a      * {@code BufferedInputStream}.      *      * @param input  the {@code InputStream} to read from      * @param output the {@code OutputStream} to write to      * @return the number of bytes copied      * @throws IOException if an I/O error occurs      */
specifier|public
specifier|static
name|long
name|copy
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|-
literal|1
operator|!=
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
condition|)
block|{
name|output
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|count
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**      * Returns a human-readable version of the file size, where the input represents      * a specific number of bytes. Based on http://stackoverflow.com/a/3758880/1035417      */
specifier|public
specifier|static
name|String
name|humanReadableByteCount
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|<
literal|0
condition|)
block|{
return|return
literal|"0"
return|;
block|}
name|int
name|unit
init|=
literal|1000
decl_stmt|;
if|if
condition|(
name|bytes
operator|<
name|unit
condition|)
block|{
return|return
name|bytes
operator|+
literal|" B"
return|;
block|}
name|int
name|exp
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|bytes
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
name|unit
argument_list|)
argument_list|)
decl_stmt|;
name|char
name|pre
init|=
literal|"kMGTPE"
operator|.
name|charAt
argument_list|(
name|exp
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%.1f %sB"
argument_list|,
name|bytes
operator|/
name|Math
operator|.
name|pow
argument_list|(
name|unit
argument_list|,
name|exp
argument_list|)
argument_list|,
name|pre
argument_list|)
return|;
block|}
block|}
end_class

end_unit

