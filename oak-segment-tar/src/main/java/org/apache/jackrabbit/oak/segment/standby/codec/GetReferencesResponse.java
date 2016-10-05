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
name|standby
operator|.
name|codec
package|;
end_package

begin_class
specifier|public
class|class
name|GetReferencesResponse
block|{
specifier|private
specifier|final
name|String
name|clientId
decl_stmt|;
specifier|private
specifier|final
name|String
name|segmentId
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|references
decl_stmt|;
specifier|public
name|GetReferencesResponse
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|segmentId
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|references
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|segmentId
operator|=
name|segmentId
expr_stmt|;
name|this
operator|.
name|references
operator|=
name|references
expr_stmt|;
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|String
name|getSegmentId
parameter_list|()
block|{
return|return
name|segmentId
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getReferences
parameter_list|()
block|{
return|return
name|references
return|;
block|}
block|}
end_class

end_unit

