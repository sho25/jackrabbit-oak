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
name|util
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
name|FilterInputStream
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Input stream that reads and decodes HTTP chunks, assuming that no chunk  * exceeds 32768 bytes and that a chunk's length is represented by exactly 4  * hexadecimal characters.  */
end_comment

begin_class
specifier|public
class|class
name|ChunkedInputStream
extends|extends
name|FilterInputStream
block|{
comment|/**      * Maximum chunk size.      */
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CHUNK_SIZE
init|=
literal|0x8000
decl_stmt|;
comment|/**      * CR + LF combination.      */
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|CRLF
init|=
literal|"\r\n"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
comment|/**      * Chunk prefix (length encoded as hexadecimal string).      */
specifier|private
specifier|final
name|byte
index|[]
name|prefix
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
comment|/**      * Chunk data.      */
specifier|private
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|MAX_CHUNK_SIZE
index|]
decl_stmt|;
comment|/**      * Chunk suffix (CR + LF).      */
specifier|private
specifier|final
name|byte
index|[]
name|suffix
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
comment|/**      * Current offset.      */
specifier|private
name|int
name|offset
decl_stmt|;
comment|/**      * Chunk length.      */
specifier|private
name|int
name|length
decl_stmt|;
comment|/**      * Flag indicating whether the last chunk was read.      */
specifier|private
name|boolean
name|lastChunk
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param in input stream      */
specifier|public
name|ChunkedInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.io.FilterInputStream#read()      */
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|lastChunk
condition|)
block|{
if|if
condition|(
name|offset
operator|==
name|length
condition|)
block|{
name|readChunk
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|<
name|length
condition|)
block|{
return|return
name|data
index|[
name|offset
operator|++
index|]
operator|&
literal|0xff
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/* (non-Javadoc)      * @see java.io.FilterInputStream#read(byte[], int, int)      */
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
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|len
operator|&&
operator|!
name|lastChunk
condition|)
block|{
if|if
condition|(
name|offset
operator|==
name|length
condition|)
block|{
name|readChunk
argument_list|()
expr_stmt|;
block|}
name|int
name|available
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
operator|-
name|read
argument_list|,
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|b
argument_list|,
name|off
operator|+
name|read
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|read
operator|+=
name|available
expr_stmt|;
name|offset
operator|+=
name|available
expr_stmt|;
block|}
return|return
name|read
operator|==
literal|0
operator|&&
name|lastChunk
condition|?
operator|-
literal|1
else|:
name|read
return|;
block|}
comment|/**      * Read a chunk from the underlying input stream.      *      * @throws IOException if an error occurs      */
specifier|private
name|void
name|readChunk
parameter_list|()
throws|throws
name|IOException
block|{
name|offset
operator|=
name|length
operator|=
literal|0
expr_stmt|;
name|readFully
argument_list|(
name|in
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|length
operator|=
name|parseInt
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
argument_list|<
literal|0
operator|||
name|length
argument_list|>
name|MAX_CHUNK_SIZE
condition|)
block|{
name|String
name|msg
init|=
literal|"Chunk size smaller than 0 or bigger than "
operator|+
name|MAX_CHUNK_SIZE
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|readFully
argument_list|(
name|in
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|suffix
argument_list|,
name|CRLF
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Missing carriage return/line feed combination."
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|readFully
argument_list|(
name|in
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|readFully
argument_list|(
name|in
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|suffix
argument_list|,
name|CRLF
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Missing carriage return/line feed combination."
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
name|lastChunk
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|readFully
argument_list|(
name|in
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
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
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
name|len
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Expected %d bytes, actually received: %d"
argument_list|,
name|len
argument_list|,
name|count
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|EOFException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parse an integer that is given in its hexadecimal representation as      * a byte array.      *      * @param b byte array containing 4 ASCII characters      * @return parsed integer      */
specifier|private
specifier|static
name|int
name|parseInt
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
operator|(
name|int
operator|)
name|b
index|[
name|i
index|]
decl_stmt|;
name|result
operator|<<=
literal|4
expr_stmt|;
if|if
condition|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
condition|)
block|{
name|result
operator|+=
name|c
operator|-
literal|'0'
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'F'
condition|)
block|{
name|result
operator|+=
name|c
operator|-
literal|'A'
operator|+
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|'a'
operator|&&
name|c
operator|<=
literal|'f'
condition|)
block|{
name|result
operator|+=
name|c
operator|-
literal|'a'
operator|+
literal|10
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"Not a hexadecimal character: "
operator|+
name|c
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Recycle this input stream.      *       * @param in new underlying input stream      */
specifier|public
name|void
name|recycle
parameter_list|(
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
name|offset
operator|=
name|length
operator|=
literal|0
expr_stmt|;
name|lastChunk
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Close this input stream. Finishes reading any pending chunks until      * the last chunk is received. Does<b>not</b> close the underlying input      * stream.      *      * @see java.io.FilterInputStream#close()      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
while|while
condition|(
operator|!
name|lastChunk
condition|)
block|{
name|readChunk
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

