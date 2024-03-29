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

begin_comment
comment|/**  * A {@link SegmentStoreProvider} that returns a {@link SegmentStore} instance  * provided by the user.  */
end_comment

begin_class
class|class
name|DefaultSegmentStoreProvider
implements|implements
name|SegmentStoreProvider
block|{
specifier|private
specifier|final
name|SegmentStore
name|segmentStore
decl_stmt|;
name|DefaultSegmentStoreProvider
parameter_list|(
name|SegmentStore
name|segmentStore
parameter_list|)
block|{
name|this
operator|.
name|segmentStore
operator|=
name|segmentStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentStore
name|getSegmentStore
parameter_list|()
block|{
return|return
name|segmentStore
return|;
block|}
block|}
end_class

end_unit

