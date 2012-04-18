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
name|FilterOutputStream
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
comment|/**  * Output stream that encodes and writes HTTP chunks.  */
end_comment

begin_class
specifier|public
class|class
name|ChunkedOutputStream
extends|extends
name|FilterOutputStream
block|{
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
comment|/**      * Last chunk.      */
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|LAST_CHUNK
init|=
literal|"0000\r\n\r\n"
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
decl_stmt|;
comment|/**      * Current offset.      */
specifier|private
name|int
name|offset
decl_stmt|;
comment|/**      * Create a new instance of this class.      *      * @param out underlying output stream.      * @param size internal buffer size      * @throws IllegalArgumentException if {@code size} is smaller than 1      *         or bigger than {@code 65535}      */
specifier|public
name|ChunkedOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
argument_list|<
literal|1
operator|||
name|size
argument_list|>
name|MAX_CHUNK_SIZE
condition|)
block|{
name|String
name|msg
init|=
literal|"Chunk size smaller than 1 or bigger than "
operator|+
name|MAX_CHUNK_SIZE
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|this
operator|.
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
block|}
comment|/**      * Create a new instance of this class.      *      * @param out underlying output stream.      */
specifier|public
name|ChunkedOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
name|MAX_CHUNK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.io.FilterOutputStream#write(int)      */
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
name|data
operator|.
name|length
condition|)
block|{
name|writeChunk
argument_list|()
expr_stmt|;
block|}
name|data
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.io.FilterOutputStream#write(byte[], int, int)      */
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
name|written
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|offset
operator|==
name|data
operator|.
name|length
condition|)
block|{
name|writeChunk
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
name|written
argument_list|,
name|data
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
name|written
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|written
operator|+=
name|available
expr_stmt|;
name|offset
operator|+=
name|available
expr_stmt|;
block|}
block|}
comment|/**      * Writes the contents of the internal buffer as chunk to the underlying      * output stream.      *      * @throws IOException if an error occurs      */
specifier|private
name|void
name|writeChunk
parameter_list|()
throws|throws
name|IOException
block|{
name|toHexString
argument_list|(
name|offset
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|CRLF
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|CRLF
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Convert an integer into a byte array, consisting of its hexadecimal      * representation.      *      * @param n integer      * @param b byte array      */
specifier|private
specifier|static
name|void
name|toHexString
parameter_list|(
name|int
name|n
parameter_list|,
name|byte
index|[]
name|b
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|b
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|c
init|=
name|n
operator|&
literal|0x0f
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
operator|&&
name|c
operator|<=
literal|9
condition|)
block|{
name|c
operator|+=
literal|'0'
expr_stmt|;
block|}
else|else
block|{
name|c
operator|+=
literal|'A'
operator|-
literal|10
expr_stmt|;
block|}
name|b
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
name|n
operator|>>=
literal|4
expr_stmt|;
block|}
block|}
comment|/**      * Flush the contents of the internal buffer to the underlying output      * stream as a chunk if it is non-zero. Never do that for a zero-size      * chunk as this would indicate EOF.      *      * @see java.io.FilterOutputStream#flush()      */
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
name|writeChunk
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Recycle this output stream.      *       * @param out new underlying output stream      */
specifier|public
name|void
name|recycle
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Close this output stream. Flush the contents of the internal buffer      * and writes the last chunk to the underlying output stream. Sets      * the internal reference to the underlying output stream to       * {@code null}. Does<b>not</b> close the underlying output stream.      *      * @see java.io.FilterOutputStream#close()      */
specifier|public
name|void
name|close
parameter_list|()
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
try|try
block|{
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|writeChunk
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|LAST_CHUNK
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

