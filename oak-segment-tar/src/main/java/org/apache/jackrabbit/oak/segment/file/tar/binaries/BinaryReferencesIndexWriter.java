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
name|nio
operator|.
name|ByteBuffer
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
name|Map
operator|.
name|Entry
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

begin_comment
comment|/**  * Maintains the transient state of a binary references index, formats it and  * serializes it.  */
end_comment

begin_class
specifier|public
class|class
name|BinaryReferencesIndexWriter
block|{
comment|/**      * Create a new, empty instance of {@link BinaryReferencesIndexWriter}.      *      * @return An instance of {@link BinaryReferencesIndexWriter}.      */
specifier|public
specifier|static
name|BinaryReferencesIndexWriter
name|newBinaryReferencesIndexWriter
parameter_list|()
block|{
return|return
operator|new
name|BinaryReferencesIndexWriter
argument_list|()
return|;
block|}
specifier|private
specifier|final
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
name|entries
decl_stmt|;
specifier|private
name|BinaryReferencesIndexWriter
parameter_list|()
block|{
name|entries
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Add an entry to the binary references index.      *      * @param generation The generation of the segment containing the      *                   reference.      * @param full       The full generation of the segment containing the      *                   reference.      * @param compacted  {@code true} if the segment containing the reference is      *                   created by a compaction operation.      * @param segment    The identifier of the segment containing the      *                   reference.      * @param reference  The binary reference.      */
specifier|public
name|void
name|addEntry
parameter_list|(
name|int
name|generation
parameter_list|,
name|int
name|full
parameter_list|,
name|boolean
name|compacted
parameter_list|,
name|UUID
name|segment
parameter_list|,
name|String
name|reference
parameter_list|)
block|{
name|entries
operator|.
name|computeIfAbsent
argument_list|(
operator|new
name|Generation
argument_list|(
name|generation
argument_list|,
name|full
argument_list|,
name|compacted
argument_list|)
argument_list|,
name|k
lambda|->
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|computeIfAbsent
argument_list|(
name|segment
argument_list|,
name|k
lambda|->
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write the current state of this instance to an array of bytes.      *      * @return An array of bytes containing the serialized state of the binary      * references index.      */
specifier|public
name|byte
index|[]
name|write
parameter_list|()
block|{
name|int
name|binaryReferenceSize
init|=
literal|0
decl_stmt|;
comment|// The following information are stored in the footer as meta-
comment|// information about the entry.
comment|// 4 bytes to store a magic number identifying this entry as containing
comment|// references to binary values.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the CRC32 checksum of the data in this entry.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the length of this entry, without including the
comment|// optional padding.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes to store the number of generations pairs in the binary
comment|// references map.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// The following information are stored as part of the main content of
comment|// this entry, after the optional padding.
for|for
control|(
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|segmentToReferences
range|:
name|entries
operator|.
name|values
argument_list|()
control|)
block|{
comment|// 4 bytes per generation to store the full generation number.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// 4 bytes per generation to store the tail generation number.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// 1 byte per generation to store the "tail" flag.
name|binaryReferenceSize
operator|+=
literal|1
expr_stmt|;
comment|// 4 bytes per generation to store the number of segments.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
for|for
control|(
name|Set
argument_list|<
name|String
argument_list|>
name|references
range|:
name|segmentToReferences
operator|.
name|values
argument_list|()
control|)
block|{
comment|// 16 bytes per segment identifier.
name|binaryReferenceSize
operator|+=
literal|16
expr_stmt|;
comment|// 4 bytes to store the number of references for this segment.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
for|for
control|(
name|String
name|reference
range|:
name|references
control|)
block|{
comment|// 4 bytes for each reference to store the length of the reference.
name|binaryReferenceSize
operator|+=
literal|4
expr_stmt|;
comment|// A variable amount of bytes, depending on the reference itself.
name|binaryReferenceSize
operator|+=
name|reference
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
expr_stmt|;
block|}
block|}
block|}
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|binaryReferenceSize
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
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
name|be
range|:
name|entries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Generation
name|generation
init|=
name|be
operator|.
name|getKey
argument_list|()
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
name|segmentToReferences
init|=
name|be
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|generation
operator|.
name|generation
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|generation
operator|.
name|full
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|generation
operator|.
name|compacted
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|segmentToReferences
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|se
range|:
name|segmentToReferences
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|UUID
name|segmentId
init|=
name|se
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|references
init|=
name|se
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|references
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|reference
range|:
name|references
control|)
block|{
name|byte
index|[]
name|bytes
init|=
name|reference
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|CRC32
name|checksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|checksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|binaryReferenceSize
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|putInt
argument_list|(
name|BinaryReferencesIndexLoaderV2
operator|.
name|MAGIC
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|array
argument_list|()
return|;
block|}
block|}
end_class

end_unit
