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
name|server
package|;
end_package

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

begin_class
class|class
name|GetBlobResponse
block|{
specifier|private
specifier|final
name|String
name|clientId
decl_stmt|;
specifier|private
specifier|final
name|Blob
name|blob
decl_stmt|;
name|GetBlobResponse
parameter_list|(
name|String
name|clientId
parameter_list|,
name|Blob
name|blob
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
name|blob
operator|=
name|blob
expr_stmt|;
block|}
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
name|Blob
name|getBlob
parameter_list|()
block|{
return|return
name|blob
return|;
block|}
block|}
end_class

end_unit

