begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|spi
operator|.
name|persistence
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Allows to write in the new archive.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentArchiveWriter
block|{
comment|/**      * Write the new segment to the archive.      *      * @param msb      * @param lsb      * @param data      * @param offset      * @param size      * @param generation      * @param fullGeneration      * @param isCompacted      * @return the entry representing the new segment. Can be later used for the {@link #readSegment(long, long)} method.      */
annotation|@
name|Nonnull
name|void
name|writeSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
annotation|@
name|Nonnull
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|generation
parameter_list|,
name|int
name|fullGeneration
parameter_list|,
name|boolean
name|isCompacted
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Read the segment.      *      * @param msb      * @param lsb      * @return byte buffer containing the segment data or null if segment doesn't exist      */
annotation|@
name|Nullable
name|ByteBuffer
name|readSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Check if the segment exists.      *      * @param msb      * @param lsb      * @return true if the segment exists      */
name|boolean
name|containsSegment
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
function_decl|;
comment|/**      * Write the graph data.      *      * @param data      */
name|void
name|writeGraph
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write the binary references data.      *      * @param data      */
name|void
name|writeBinaryReferences
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the current length of the archive.      *      * @return length of the archive, in bytes      */
name|long
name|getLength
parameter_list|()
function_decl|;
comment|/**      * Close the archive.      */
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Check if the archive has been created (eg. something has been written).      *      * @return true if the archive has been created, false otherwise      */
name|boolean
name|isCreated
parameter_list|()
function_decl|;
comment|/**      * Flush all the data to the storage.      */
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the name of the archive.      *      * @return archive name      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

