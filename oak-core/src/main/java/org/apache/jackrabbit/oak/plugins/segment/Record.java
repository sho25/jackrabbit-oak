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

begin_comment
comment|/**  * Record within a segment.  */
end_comment

begin_class
class|class
name|Record
block|{
comment|/**      * Identifier of this record.      */
specifier|private
specifier|final
name|RecordId
name|id
decl_stmt|;
specifier|protected
name|Record
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * Returns the identifier of this record.      *      * @return record identifier      */
specifier|public
name|RecordId
name|getRecordId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Returns the segment offset of this record.      *      * @return segment offset of this record      */
specifier|protected
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|id
operator|.
name|getOffset
argument_list|()
return|;
block|}
comment|/**      * Returns the segment offset of the given byte position in this record.      *      * @param position byte position within this record      * @return segment offset of the given byte position      */
specifier|protected
name|int
name|getOffset
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|getOffset
argument_list|()
operator|+
name|position
return|;
block|}
comment|/**      * Returns the segment offset of a byte position in this record.      * The position is calculated from the given number of raw bytes and      * record identifiers.      *      * @param bytes number of raw bytes before the position      * @param ids number of record identifiers before the position      * @return segment offset of the specified byte position      */
specifier|protected
name|int
name|getOffset
parameter_list|(
name|int
name|bytes
parameter_list|,
name|int
name|ids
parameter_list|)
block|{
return|return
name|getOffset
argument_list|(
name|bytes
operator|+
name|ids
operator|*
name|Segment
operator|.
name|RECORD_ID_BYTES
argument_list|)
return|;
block|}
block|}
end_class

end_unit

