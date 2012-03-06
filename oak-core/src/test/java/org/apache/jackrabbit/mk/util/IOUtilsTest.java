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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Random
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Test the utility classes.  */
end_comment

begin_class
specifier|public
class|class
name|IOUtilsTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testReadFully
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
specifier|final
name|AtomicInteger
name|readCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|FilterInputStream
name|in
init|=
operator|new
name|FilterInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
block|{
specifier|public
name|int
name|read
parameter_list|(
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
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|max
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|in
operator|.
name|mark
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|byte
index|[]
name|test
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
comment|// readFully is not supposed to call read when reading 0 bytes
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|test
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|test
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtilsTest
operator|.
name|assertEquals
argument_list|(
name|data
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|test
operator|=
operator|new
name|byte
index|[
literal|1001
index|]
expr_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|in
operator|.
name|mark
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|test
argument_list|,
literal|0
argument_list|,
literal|1001
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|test
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSkipFully
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1000
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|FilterInputStream
name|in
init|=
operator|new
name|FilterInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
block|{
specifier|public
name|int
name|read
parameter_list|(
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
return|return
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|off
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|max
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|in
operator|.
name|mark
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
literal|1001
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
specifier|public
name|void
name|testStringReadWrite
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
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
literal|100000
condition|;
name|i
operator|+=
name|i
operator|/
literal|10
operator|+
literal|1
control|)
block|{
name|String
name|s
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|String
name|p
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[
name|i
index|]
argument_list|)
operator|.
name|replace
argument_list|(
operator|(
name|char
operator|)
literal|0
argument_list|,
literal|'a'
argument_list|)
decl_stmt|;
name|s
operator|+=
name|p
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
block|{
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
block|{
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|len
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|String
name|t
init|=
name|IOUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
specifier|public
name|void
name|testBytesReadWrite
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|iterations
init|=
literal|1000
decl_stmt|;
while|while
condition|(
name|iterations
operator|--
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|abs
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|%
literal|0x40000
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|n
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|writeBytes
argument_list|(
name|out
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf1
init|=
name|IOUtils
operator|.
name|readBytes
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|buf
argument_list|,
name|buf1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testVarInt
parameter_list|()
throws|throws
name|IOException
block|{
name|testVarInt
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x7f
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x80
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x3fff
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x4000
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x1fffff
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x200000
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0xfffffff
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x10000000
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
operator|-
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|0x20000
condition|;
name|x
operator|++
control|)
block|{
name|testVarInt
argument_list|(
name|x
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
operator|+
name|x
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|x
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x200000
operator|+
name|x
operator|-
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
literal|0x10000000
operator|+
name|x
operator|-
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|testVarInt
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarInt
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10000000
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// trailing 0s are never written, but are an alternative way to encode a value
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVarLong
parameter_list|()
throws|throws
name|IOException
block|{
name|testVarLong
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x7f
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x80
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x3fff
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x4000
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x1fffff
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x200000
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0xfffffff
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x10000000
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x1fffffffL
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x2000000000L
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x3ffffffffffL
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x40000000000L
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x1ffffffffffffL
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x2000000000000L
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0xffffffffffffffL
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x100000000000000L
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|0x20000
condition|;
name|x
operator|++
control|)
block|{
name|testVarLong
argument_list|(
name|x
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
name|Long
operator|.
name|MIN_VALUE
operator|+
name|x
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|-
name|x
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x200000
operator|+
name|x
operator|-
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
literal|0x10000000
operator|+
name|x
operator|-
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|testVarLong
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testVarLong
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// trailing 0s are never written, but are an alternative way to encode a value
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testVarInt
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|expectedLen
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|length
operator|<=
literal|5
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedLen
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedLen
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|int
name|x2
init|=
name|IOUtils
operator|.
name|readVarInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|x
argument_list|,
name|x2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testVarLong
parameter_list|(
name|long
name|x
parameter_list|,
name|int
name|expectedLen
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|writeVarLong
argument_list|(
name|out
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|length
operator|<=
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedLen
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedLen
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|long
name|x2
init|=
name|IOUtils
operator|.
name|readVarLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|x
argument_list|,
name|x2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|assertEquals
parameter_list|(
name|byte
index|[]
name|expected
parameter_list|,
name|byte
index|[]
name|got
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|got
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|got
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|got
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

