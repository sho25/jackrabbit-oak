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
name|spi
operator|.
name|blob
package|;
end_package

begin_comment
comment|/**  * Options while writing blobs to the blob store / data store.  */
end_comment

begin_class
specifier|public
class|class
name|BlobOptions
block|{
specifier|private
name|UploadType
name|uploadType
init|=
name|UploadType
operator|.
name|DEFAULT
decl_stmt|;
specifier|public
name|UploadType
name|getUpload
parameter_list|()
block|{
return|return
name|uploadType
return|;
block|}
specifier|public
name|BlobOptions
name|setUpload
parameter_list|(
name|UploadType
name|uploadType
parameter_list|)
block|{
name|this
operator|.
name|uploadType
operator|=
name|uploadType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specifies the upload type for the blob.      */
specifier|public
enum|enum
name|UploadType
block|{
name|SYNCHRONOUS
block|,
name|DEFAULT
block|}
block|}
end_class

end_unit

