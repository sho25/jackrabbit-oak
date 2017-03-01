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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * The backend storage interface used by the segment node store.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentStore
block|{
comment|/**      * Checks whether the identified segment exists in this store.      *      * @param id segment identifier      * @return {@code true} if the segment exists, {@code false} otherwise      */
name|boolean
name|containsSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
function_decl|;
comment|/**      * Reads the identified segment from this store.      *      * @param segmentId segment identifier      * @return identified segment, or a {@link SegmentNotFoundException} thrown if not found      */
annotation|@
name|Nonnull
name|Segment
name|readSegment
parameter_list|(
name|SegmentId
name|segmentId
parameter_list|)
function_decl|;
comment|/**      * Writes the given segment to the segment store.      *      * @param id segment identifier      * @param bytes byte buffer that contains the raw contents of the segment      * @param offset start offset within the byte buffer      * @param length length of the segment      */
name|void
name|writeSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Create a {@link SegmentId} represented by the given MSB/LSB pair.      *      * @param msb The most significant bits of the {@link SegmentId}.      * @param lsb The least significant bits of the {@link SegmentId}.      * @return A non-{@code null} instance of {@link SegmentId}.      */
annotation|@
name|Nonnull
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

