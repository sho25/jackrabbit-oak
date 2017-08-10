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
name|data
package|;
end_package

begin_class
specifier|public
class|class
name|StringData
block|{
specifier|private
specifier|final
name|String
name|string
decl_stmt|;
specifier|private
specifier|final
name|RecordIdData
name|recordId
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
name|StringData
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|recordId
operator|=
literal|null
expr_stmt|;
block|}
name|StringData
parameter_list|(
name|RecordIdData
name|recordId
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|recordId
operator|=
name|recordId
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|string
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|boolean
name|isString
parameter_list|()
block|{
return|return
name|string
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|isRecordId
parameter_list|()
block|{
return|return
name|recordId
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|string
return|;
block|}
specifier|public
name|RecordIdData
name|getRecordId
parameter_list|()
block|{
return|return
name|recordId
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
end_class

end_unit

