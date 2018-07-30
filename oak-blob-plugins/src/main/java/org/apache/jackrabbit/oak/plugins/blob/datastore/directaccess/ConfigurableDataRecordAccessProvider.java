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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|directaccess
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ConfigurableDataRecordAccessProvider
extends|extends
name|DataRecordAccessProvider
block|{
comment|/**      * Specifies the number of seconds before a signed download URI will expire.      * Setting this to 0 is equivalent to turning off the ability to use      * direct download.      *      * @param expirySeconds Number of seconds before a download URI expires.      */
name|void
name|setDirectDownloadURIExpirySeconds
parameter_list|(
name|int
name|expirySeconds
parameter_list|)
function_decl|;
comment|/**      * Specifies the maximum number of read URIs to be cached in an in-memory      * cache.  Setting this to 0 is equivalent to disabling the cache.      *      * @param maxSize Number of read URIs to cache.      */
name|void
name|setDirectDownloadURICacheSize
parameter_list|(
name|int
name|maxSize
parameter_list|)
function_decl|;
comment|/**      * Specifies the number of seconds before a signed upload URI will expire.      * Setting this to 0 is equivalent to turning off the ability to use      * direct upload.      *      * @param expirySeconds Number of seconds before an upload URI expires.      */
name|void
name|setDirectUploadURIExpirySeconds
parameter_list|(
name|int
name|expirySeconds
parameter_list|)
function_decl|;
comment|/**      * Enables or disables binary transfer acceleration, if supported by the      * service provider.      *      * @param enabled True to enable binary transfer acceleration (if      *        supported); False otherwise.      */
name|void
name|setBinaryTransferAccelerationEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

