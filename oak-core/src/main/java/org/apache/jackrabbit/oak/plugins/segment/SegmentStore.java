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
name|plugins
operator|.
name|segment
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|api
operator|.
name|Blob
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_interface
specifier|public
interface|interface
name|SegmentStore
block|{
name|SegmentTracker
name|getTracker
parameter_list|()
function_decl|;
comment|/**      * Returns the head state.      *      * @return head state      */
annotation|@
name|Nonnull
name|SegmentNodeState
name|getHead
parameter_list|()
function_decl|;
name|boolean
name|setHead
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|,
name|SegmentNodeState
name|head
parameter_list|)
function_decl|;
comment|/**      * Checks whether the identified segment exists in this store.      *      * @param id segment identifier      * @return {@code true} if the segment exists, {@code false} otherwise      */
name|boolean
name|containsSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
function_decl|;
comment|/**      * Reads the identified segment from this store.      *      * @param segmentId segment identifier      * @return identified segment, or {@code null} if not found      */
annotation|@
name|CheckForNull
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
function_decl|;
name|void
name|close
parameter_list|()
function_decl|;
comment|/**      * Read a blob from external storage.      *      * @param reference blob reference      * @return external blob      */
name|Blob
name|readBlob
parameter_list|(
name|String
name|reference
parameter_list|)
function_decl|;
comment|/**      * Returns the external BlobStore (if configured) with this store      */
annotation|@
name|CheckForNull
name|BlobStore
name|getBlobStore
parameter_list|()
function_decl|;
comment|/**      * Triggers removal of segments that are no longer referenceable.      */
name|void
name|gc
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

