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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Represents a write-enabled, append-only archive. It allows to append segments  * and other data structures (segment graph, serialized binary references) to the  * archive and also to read the already persisted segments.<p>  * Caller will use the methods modifying the archive in the following order:  *<ol>  *<li>phase 1:  *<ul>  *<li>{@link #writeSegment(long, long, byte[], int, int, int, int, boolean)}</li>  *<li>{@link #flush()}</li>  *</ul>  *         repeated in an unspecified order</li>  *<li>{@link #writeBinaryReferences(byte[])}</li>  *<li>{@link #writeGraph(byte[])} (optionally)</li>  *<li>{@link #close()}</li>  *</ol>  * All the calls above are synchronized by the caller.  * In the first phase of the writer lifecycle, the  * write() and the flush() will be called many times, in an unspecified order. At  * the end of the writer life cycle, the rest of the methods (2-4) will be called.  *<p>  * Before the {@link #close()}, all the non-modifying methods  * (eg. {@link #readSegment(long, long)}, {@link #getLength()}} can be invoked at  * any time. They<b>should be thread safe</b>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentArchiveWriter
block|{
comment|/**      * Write the new segment to the archive.      *      * @param msb the most significant bits of the identifier of the segment      * @param lsb the least significant bits of the identifier of the segment      * @param data the data.      * @param offset the start offset in the data.      * @param size the number of bytes to write.      * @param generation the segment generation, see {@link SegmentArchiveEntry#getGeneration()}      * @param fullGeneration the segment full generation, see {@link SegmentArchiveEntry#getFullGeneration()}      * @param isCompacted the segment compaction property, see {@link SegmentArchiveEntry#isCompacted()}      * @throws IOException      */
annotation|@
name|NotNull
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
name|NotNull
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
comment|/**      * Read the segment.      *      * @param msb the most significant bits of the identifier of the segment      * @param lsb the least significant bits of the identifier of the segment      * @return byte buffer containing the segment data or null if segment doesn't exist      */
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
comment|/**      * Check if the segment exists.      *      * @param msb the most significant bits of the identifier of the segment      * @param lsb the least significant bits of the identifier of the segment      * @return true if the segment exists      */
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
comment|/**      * Write the graph data.      *      * @param data serialized segment graph data      */
name|void
name|writeGraph
parameter_list|(
annotation|@
name|NotNull
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write the binary references data.      *      * @param data serialized binary references data      */
name|void
name|writeBinaryReferences
parameter_list|(
annotation|@
name|NotNull
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
comment|/**      * Get the number of entries currently contained in the archive.      *      * @return  number of entries      */
name|int
name|getEntryCount
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
comment|/**      * Flush all the data to the storage. After returning from this method      * successfully, all the segments written with the {@link #writeSegment(long, long, byte[], int, int, int, int, boolean)}      * should be actually saved to the storage.      */
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the name of the archive.      *      * @return archive name      */
annotation|@
name|NotNull
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

