begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|api
operator|.
name|blob
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Download options to be provided to a call to {@link  * BlobAccessProvider#initiateBlobUpload(long, int, BlobUploadOptions)}.  *<p>  * This object is an internal corollary to {@code  * org.apache.jackrabbit.api.binary.BinaryUploadOptions}.  */
end_comment

begin_class
annotation|@
name|ProviderType
specifier|public
class|class
name|BlobUploadOptions
block|{
specifier|private
name|boolean
name|domainOverrideIgnored
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|BlobUploadOptions
name|DEFAULT
init|=
operator|new
name|BlobUploadOptions
argument_list|()
decl_stmt|;
specifier|private
name|BlobUploadOptions
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new upload options instance.      *      * @param domainOverrideIgnored true if any configured domain override      *                              should be ignored when generating URIs;      *                              false otherwise.      */
specifier|public
name|BlobUploadOptions
parameter_list|(
name|boolean
name|domainOverrideIgnored
parameter_list|)
block|{
name|this
operator|.
name|domainOverrideIgnored
operator|=
name|domainOverrideIgnored
expr_stmt|;
block|}
comment|/**      * Get the setting to determine if any configured domain override should be      * ignored when generating URIs.      *      * Default behavior is to honor (i.e. not ignore) any configured domain      * override value (false).      *      * @return true to ignore the domain override; false otherwise.      */
specifier|public
name|boolean
name|isDomainOverrideIgnored
parameter_list|()
block|{
return|return
name|domainOverrideIgnored
return|;
block|}
block|}
end_class

end_unit

