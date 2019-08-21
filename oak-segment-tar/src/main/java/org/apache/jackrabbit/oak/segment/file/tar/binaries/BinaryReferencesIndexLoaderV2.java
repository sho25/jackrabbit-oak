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
name|binaries
package|;
end_package

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|BinaryReferencesIndexLoaderV2
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
literal|'B'
operator|<<
literal|8
operator|)
operator|+
literal|'\n'
decl_stmt|;
specifier|static
specifier|final
name|int
name|FOOTER_SIZE
init|=
literal|16
decl_stmt|;
specifier|static
name|Buffer
name|loadBinaryReferencesIndex
parameter_list|(
name|ReaderAtEnd
name|reader
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidBinaryReferencesIndexException
block|{
name|Buffer
name|meta
init|=
name|reader
operator|.
name|readAtEnd
argument_list|(
name|FOOTER_SIZE
argument_list|,
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
name|size
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
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid magic number"
argument_list|)
throw|;
block|}
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid count"
argument_list|)
throw|;
block|}
if|if
condition|(
name|size
operator|<
name|count
operator|*
literal|22
operator|+
literal|16
condition|)
block|{
throw|throw
operator|new
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid size"
argument_list|)
throw|;
block|}
return|return
name|reader
operator|.
name|readAtEnd
argument_list|(
name|size
argument_list|,
name|size
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BinaryReferencesIndex
name|parseBinaryReferencesIndex
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
throws|throws
name|InvalidBinaryReferencesIndexException
block|{
name|Buffer
name|data
init|=
name|buffer
operator|.
name|slice
argument_list|()
decl_stmt|;
name|data
operator|.
name|limit
argument_list|(
name|data
operator|.
name|limit
argument_list|()
operator|-
name|FOOTER_SIZE
argument_list|)
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
name|FOOTER_SIZE
argument_list|)
expr_stmt|;
name|Buffer
name|meta
init|=
name|buffer
operator|.
name|slice
argument_list|()
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
name|size
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
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid magic number"
argument_list|)
throw|;
block|}
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid count"
argument_list|)
throw|;
block|}
if|if
condition|(
name|size
operator|<
name|count
operator|*
literal|22
operator|+
literal|16
condition|)
block|{
throw|throw
operator|new
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid size"
argument_list|)
throw|;
block|}
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|data
operator|.
name|mark
argument_list|()
expr_stmt|;
name|data
operator|.
name|update
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|data
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
call|(
name|int
call|)
argument_list|(
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
operator|!=
name|crc32
condition|)
block|{
throw|throw
operator|new
name|InvalidBinaryReferencesIndexException
argument_list|(
literal|"Invalid checksum"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BinaryReferencesIndex
argument_list|(
name|parseBinaryReferencesIndex
argument_list|(
name|count
argument_list|,
name|data
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|Generation
argument_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|parseBinaryReferencesIndex
parameter_list|(
name|int
name|count
parameter_list|,
name|Buffer
name|buffer
parameter_list|)
block|{
name|Map
argument_list|<
name|Generation
argument_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Generation
name|k
init|=
name|parseGeneration
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|v
init|=
name|parseEntriesBySegment
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|Generation
name|parseGeneration
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
name|int
name|generation
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|full
init|=
name|buffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|boolean
name|compacted
init|=
name|buffer
operator|.
name|get
argument_list|()
operator|!=
literal|0
decl_stmt|;
return|return
operator|new
name|Generation
argument_list|(
name|generation
argument_list|,
name|full
argument_list|,
name|compacted
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|parseEntriesBySegment
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
return|return
name|parseEntriesBySegment
argument_list|(
name|buffer
operator|.
name|getInt
argument_list|()
argument_list|,
name|buffer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|parseEntriesBySegment
parameter_list|(
name|int
name|count
parameter_list|,
name|Buffer
name|buffer
parameter_list|)
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|UUID
name|k
init|=
name|parseUUID
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|v
init|=
name|parseEntries
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|UUID
name|parseUUID
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
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
return|return
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|parseEntries
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
return|return
name|parseEntries
argument_list|(
name|buffer
operator|.
name|getInt
argument_list|()
argument_list|,
name|buffer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|parseEntries
parameter_list|(
name|int
name|count
parameter_list|,
name|Buffer
name|buffer
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|entries
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|parseString
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
specifier|private
specifier|static
name|String
name|parseString
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
return|return
name|parseString
argument_list|(
name|buffer
operator|.
name|getInt
argument_list|()
argument_list|,
name|buffer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|parseString
parameter_list|(
name|int
name|length
parameter_list|,
name|Buffer
name|buffer
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

