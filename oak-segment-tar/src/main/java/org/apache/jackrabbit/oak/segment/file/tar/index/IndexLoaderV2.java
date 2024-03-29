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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|Buffer
operator|.
name|wrap
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|util
operator|.
name|ReaderAtEnd
import|;
end_import

begin_class
class|class
name|IndexLoaderV2
block|{
specifier|static
specifier|final
name|int
name|MAGIC
init|=
operator|(
literal|'\n'
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|'1'
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|'K'
operator|<<
literal|8
operator|)
operator|+
literal|'\n'
decl_stmt|;
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
name|IndexLoaderV2
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
name|IndexV2
name|loadIndex
parameter_list|(
name|ReaderAtEnd
name|reader
parameter_list|)
throws|throws
name|InvalidIndexException
throws|,
name|IOException
block|{
name|Buffer
name|meta
init|=
name|reader
operator|.
name|readAtEnd
argument_list|(
name|IndexV2
operator|.
name|FOOTER_SIZE
argument_list|,
name|IndexV2
operator|.
name|FOOTER_SIZE
argument_list|)
decl_stmt|;
name|int
name|crc32
init|=
name|meta
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|meta
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|bytes
init|=
name|meta
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|magic
init|=
name|meta
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magic
operator|!=
name|MAGIC
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Magic number mismatch"
argument_list|)
throw|;
block|}
if|if
condition|(
name|count
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid entry count"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytes
operator|<
name|count
operator|*
name|IndexEntryV1
operator|.
name|SIZE
operator|+
name|IndexV1
operator|.
name|FOOTER_SIZE
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid size"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytes
operator|%
name|blockSize
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid size alignment"
argument_list|)
throw|;
block|}
name|Buffer
name|entries
init|=
name|reader
operator|.
name|readAtEnd
argument_list|(
name|IndexV2
operator|.
name|FOOTER_SIZE
operator|+
name|count
operator|*
name|IndexEntryV2
operator|.
name|SIZE
argument_list|,
name|count
operator|*
name|IndexEntryV2
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|entries
operator|.
name|mark
argument_list|()
expr_stmt|;
name|entries
operator|.
name|update
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|entries
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|crc32
operator|!=
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid checksum"
argument_list|)
throw|;
block|}
name|long
name|lastMsb
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|long
name|lastLsb
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|byte
index|[]
name|entry
init|=
operator|new
name|byte
index|[
name|IndexEntryV2
operator|.
name|SIZE
index|]
decl_stmt|;
name|entries
operator|.
name|mark
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|entries
operator|.
name|get
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|Buffer
name|buffer
init|=
name|wrap
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|long
name|msb
init|=
name|buffer
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|buffer
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastMsb
operator|>
name|msb
operator|||
operator|(
name|lastMsb
operator|==
name|msb
operator|&&
name|lastLsb
operator|>
name|lsb
operator|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Incorrect entry ordering"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastMsb
operator|==
name|msb
operator|&&
name|lastLsb
operator|==
name|lsb
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Duplicate entry"
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid entry offset"
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|%
name|blockSize
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid entry offset alignment"
argument_list|)
throw|;
block|}
if|if
condition|(
name|size
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Invalid entry size"
argument_list|)
throw|;
block|}
name|lastMsb
operator|=
name|msb
expr_stmt|;
name|lastLsb
operator|=
name|lsb
expr_stmt|;
block|}
name|entries
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexV2
argument_list|(
name|entries
argument_list|)
return|;
block|}
block|}
end_class

end_unit

