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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

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

begin_interface
specifier|public
interface|interface
name|SegmentStore
block|{
name|SegmentWriter
name|getWriter
parameter_list|()
function_decl|;
comment|/**      * Returns the named journal.      *      * @param name journal name      * @return named journal, or {@code null} if not found      */
annotation|@
name|CheckForNull
name|Journal
name|getJournal
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Reads the identified segment from this store.      *      * @param segmentId segment identifier      * @return identified segment, or {@code null} if not found      */
annotation|@
name|CheckForNull
name|Segment
name|readSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
function_decl|;
comment|/**      * Writes the given segment to the segment store.      *      * @param segmentId segment identifier      * @param bytes byte buffer that contains the raw contents of the segment      * @param offset start offset within the byte buffer      * @param length length of the segment      */
name|void
name|writeSegment
parameter_list|(
name|UUID
name|segmentId
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
comment|/**      * Checks whether the given object is a record of the given type and      * is stored in this segment store.      *      * @param object possible record object      * @param type record type      * @return {@code true} if the object is a record of the given type      *         from this store, {@code false} otherwise      */
name|boolean
name|isInstance
parameter_list|(
name|Object
name|object
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Record
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Read a blob from external storage.      *      * @param reference blob reference      * @return external blob      */
name|Blob
name|readBlob
parameter_list|(
name|String
name|reference
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

