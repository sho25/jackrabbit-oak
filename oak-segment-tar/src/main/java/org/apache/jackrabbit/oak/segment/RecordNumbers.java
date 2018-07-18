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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|RecordNumbers
operator|.
name|Entry
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

begin_comment
comment|/**  * A table to translate record numbers to offsets.  */
end_comment

begin_interface
interface|interface
name|RecordNumbers
extends|extends
name|Iterable
argument_list|<
name|Entry
argument_list|>
block|{
comment|/**      * An always empty {@code RecordNumber} table.      */
name|RecordNumbers
name|EMPTY_RECORD_NUMBERS
init|=
operator|new
name|RecordNumbers
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOffset
parameter_list|(
name|int
name|recordNumber
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Translate a record number to an offset.      *      * @param recordNumber A record number.      * @return the offset corresponding to the record number, or {@code -1} if      * no offset is associated to the record number.      */
name|int
name|getOffset
parameter_list|(
name|int
name|recordNumber
parameter_list|)
function_decl|;
comment|/**      * Represents an entry in the record table.      */
interface|interface
name|Entry
block|{
comment|/**          * The record number.          *          * @return a record number.          */
name|int
name|getRecordNumber
parameter_list|()
function_decl|;
comment|/**          * The offset of this record..          *          * @return an offset.          */
name|int
name|getOffset
parameter_list|()
function_decl|;
comment|/**          * The type of this record.          *          * @return a record type.          */
name|RecordType
name|getType
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

