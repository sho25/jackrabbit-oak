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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|index
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
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
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|IndexLoaderV1Test
block|{
specifier|private
specifier|static
name|IndexV1
name|loadIndex
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loadIndex
argument_list|(
literal|1
argument_list|,
name|buffer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|IndexV1
name|loadIndex
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|Buffer
name|buffer
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|IndexLoaderV1
argument_list|(
name|blockSize
argument_list|)
operator|.
name|loadIndex
argument_list|(
parameter_list|(
name|whence
parameter_list|,
name|length
parameter_list|)
lambda|->
block|{
name|Buffer
name|slice
init|=
name|buffer
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|slice
operator|.
name|position
argument_list|(
name|slice
operator|.
name|limit
argument_list|()
operator|-
name|whence
argument_list|)
expr_stmt|;
name|slice
operator|.
name|limit
argument_list|(
name|slice
operator|.
name|position
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
return|return
name|slice
operator|.
name|slice
argument_list|()
return|;
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|assertInvalidIndexException
parameter_list|(
name|Buffer
name|buffer
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|loadIndex
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|message
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertInvalidIndexException
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|Buffer
name|buffer
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|loadIndex
argument_list|(
name|blockSize
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|message
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
specifier|static
name|int
name|checksum
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|int
name|position
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|update
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|position
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidMagic
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
try|try
block|{
name|loadIndex
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Magic number mismatch"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidCount
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Invalid entry count"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Invalid size"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidSizeAlignment
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
operator|+
literal|1
argument_list|,
name|buffer
argument_list|,
literal|"Invalid size alignment"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putLong
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|3
argument_list|)
operator|.
name|putInt
argument_list|(
literal|4
argument_list|)
operator|.
name|putInt
argument_list|(
literal|5
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Invalid checksum"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testIncorrectEntryOrderingByMsb
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Incorrect entry ordering"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testIncorrectEntryOrderingByLsb
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Incorrect entry ordering"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testDuplicateEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Duplicate entry"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidEntryOffset
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Invalid entry offset"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidEntryOffsetAlignment
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|index
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|index
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
operator|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
operator|)
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
operator|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
operator|)
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|mark
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|buffer
operator|.
name|limit
argument_list|()
operator|-
name|IndexEntryV1
operator|.
name|SIZE
operator|-
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
literal|2
argument_list|,
name|buffer
argument_list|,
literal|"Invalid entry offset alignment"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidIndexException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidEntrySize
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertInvalidIndexException
argument_list|(
name|buffer
argument_list|,
literal|"Invalid entry size"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|Buffer
name|entries
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|entries
operator|.
name|duplicate
argument_list|()
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|0
argument_list|)
operator|.
name|putLong
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|1
argument_list|)
operator|.
name|putInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|Buffer
operator|.
name|allocate
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|duplicate
argument_list|()
operator|.
name|put
argument_list|(
name|entries
operator|.
name|duplicate
argument_list|()
argument_list|)
operator|.
name|putInt
argument_list|(
name|checksum
argument_list|(
name|entries
argument_list|)
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
argument_list|)
operator|.
name|putInt
argument_list|(
literal|2
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
argument_list|)
operator|.
name|putInt
argument_list|(
name|IndexLoaderV1
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|loadIndex
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

