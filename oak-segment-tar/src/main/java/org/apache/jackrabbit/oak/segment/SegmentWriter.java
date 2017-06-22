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
name|List
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Supplier
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
name|api
operator|.
name|PropertyState
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_interface
specifier|public
interface|interface
name|SegmentWriter
block|{
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a map record.      *      * @param base    base map relative to which the {@code changes} are applied      *                ot {@code null} for the empty map.      * @param changes the changed mapping to apply to the {@code base} map.      * @return the map record written      * @throws IOException      */
annotation|@
name|Nonnull
comment|// TODO frm this method is only used from test code, should it be removed?
name|RecordId
name|writeMap
parameter_list|(
annotation|@
name|Nullable
name|MapRecord
name|base
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|changes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a list record.      *      * @param list the list to write.      * @return the record id of the list written      * @throws IOException      */
annotation|@
name|Nonnull
comment|// TODO frm this method is only used from test code, should it be removed?
name|RecordId
name|writeList
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|RecordId
argument_list|>
name|list
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a string record.      *      * @param string the string to write.      * @return the record id of the string written.      * @throws IOException      */
annotation|@
name|Nonnull
comment|// TODO frm this method is only used from test code, should it be removed?
name|RecordId
name|writeString
parameter_list|(
annotation|@
name|Nonnull
name|String
name|string
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a blob (as list of block records)      *      * @param blob blob to write      * @return The segment blob written      * @throws IOException      */
annotation|@
name|Nonnull
name|RecordId
name|writeBlob
parameter_list|(
annotation|@
name|Nonnull
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes a block record containing the given block of bytes.      *      * @param bytes  source buffer      * @param offset offset within the source buffer      * @param length number of bytes to write      * @return block record identifier      */
annotation|@
name|Nonnull
comment|// TODO frm this method is only used from test code, should it be removed?
name|RecordId
name|writeBlock
parameter_list|(
annotation|@
name|Nonnull
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
comment|/**      * Writes a stream value record. The given stream is consumed<em>and      * closed</em> by this method.      *      * @param stream stream to be written      * @return blob for the passed {@code stream}      * @throws IOException if the input stream could not be read or the output      *                     could not be written      */
annotation|@
name|Nonnull
name|RecordId
name|writeStream
parameter_list|(
annotation|@
name|Nonnull
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a property.      *      * @param state the property to write      * @return the property state written      * @throws IOException      */
annotation|@
name|Nonnull
comment|// TODO frm this method is only used from test code, should it be removed?
name|RecordId
name|writeProperty
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a node state.      *<p>      *<em>Note:</em> the returned {@code SegmentNodeState} instance is bound to      * this {@code SegmentWriter} instance. That is, future calls to {@code      * #builder()} return a {@code NodeBuilder} that is also bound to the same      * {@code SegmentWriter} instance and uses it for writing any changes. This      * might not always be desired and callers of this method need to take care      * not to proliferate this writer through the returned node states beyond      * the intended bounds.      *      * @param state node state to write      * @return segment node state equal to {@code state}      * @throws IOException      */
annotation|@
name|Nonnull
name|RecordId
name|writeNode
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write a node state, unless cancelled.      *<p>      *<em>Note:</em> the returned {@code SegmentNodeState} instance is bound to      * this {@code SegmentWriter} instance. That is, future calls to {@code      * #builder()} return a {@code NodeBuilder} that is also bound to the same      * {@code SegmentWriter} instance and uses it for writing any changes. This      * might not always be desired and callers of this method need to take care      * not to proliferate this writer through the returned node states beyond      * the intended bounds.      *      * @param state  node state to write      * @param cancel supplier to signal cancellation of this write operation      * @return segment node state equal to {@code state} or {@code null} if      * cancelled.      * @throws IOException      */
annotation|@
name|CheckForNull
name|RecordId
name|writeNode
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|cancel
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

