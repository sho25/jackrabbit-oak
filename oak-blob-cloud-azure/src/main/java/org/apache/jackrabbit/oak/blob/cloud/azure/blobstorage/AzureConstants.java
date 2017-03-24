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
name|blob
operator|.
name|cloud
operator|.
name|azure
operator|.
name|blobstorage
package|;
end_package

begin_class
specifier|public
specifier|final
class|class
name|AzureConstants
block|{
comment|/**      * Azure Stoage Account name      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_STORAGE_ACCOUNT_NAME
init|=
literal|"accessKey"
decl_stmt|;
comment|/**      * Azure Stoage Account Key      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_STORAGE_ACCOUNT_KEY
init|=
literal|"secretKey"
decl_stmt|;
comment|/**      * Azure Blob Storage container name      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOB_CONTAINER_NAME
init|=
literal|"container"
decl_stmt|;
comment|/**      * Azure Blob Storage request timeout.      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOB_REQUEST_TIMEOUT
init|=
literal|"socketTimeout"
decl_stmt|;
comment|/**      * Azure Blob Storage maximum retries per request.      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOB_MAX_REQUEST_RETRY
init|=
literal|"maxErrorRetry"
decl_stmt|;
comment|/**      * Azure Blob Storage maximum connections per operation (default 1)      */
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOB_CONCURRENT_REQUESTS_PER_OPERATION
init|=
literal|"maxConnections"
decl_stmt|;
comment|/**      *  Constant to set proxy host.      */
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST
init|=
literal|"proxyHost"
decl_stmt|;
comment|/**      *  Constant to set proxy port.      */
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PORT
init|=
literal|"proxyPort"
decl_stmt|;
specifier|private
name|AzureConstants
parameter_list|()
block|{ }
block|}
end_class

end_unit

