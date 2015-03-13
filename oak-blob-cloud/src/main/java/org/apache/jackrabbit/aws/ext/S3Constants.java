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
name|aws
operator|.
name|ext
package|;
end_package

begin_comment
comment|/**  * Defined Amazon S3 constants.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|S3Constants
block|{
comment|/**      * Amazon aws access key.      */
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_KEY
init|=
literal|"accessKey"
decl_stmt|;
comment|/**      * Amazon aws secret key.      */
specifier|public
specifier|static
specifier|final
name|String
name|SECRET_KEY
init|=
literal|"secretKey"
decl_stmt|;
comment|/**      * Amazon S3 Http connection timeout.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_CONN_TIMEOUT
init|=
literal|"connectionTimeout"
decl_stmt|;
comment|/**      * Amazon S3  socket timeout.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_SOCK_TIMEOUT
init|=
literal|"socketTimeout"
decl_stmt|;
comment|/**      * Amazon S3  maximum connections to be used.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_MAX_CONNS
init|=
literal|"maxConnections"
decl_stmt|;
comment|/**      * Amazon S3  maximum retries.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_MAX_ERR_RETRY
init|=
literal|"maxErrorRetry"
decl_stmt|;
comment|/**      * Amazon aws S3 bucket.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_BUCKET
init|=
literal|"s3Bucket"
decl_stmt|;
comment|/**      * Amazon aws S3 region.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_REGION
init|=
literal|"s3Region"
decl_stmt|;
comment|/**      * Amazon aws S3 region.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_END_POINT
init|=
literal|"s3EndPoint"
decl_stmt|;
comment|/**      * Constant for S3 Connector Protocol      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_CONN_PROTOCOL
init|=
literal|"s3ConnProtocol"
decl_stmt|;
comment|/**      * Constant to rename keys      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_RENAME_KEYS
init|=
literal|"s3RenameKeys"
decl_stmt|;
comment|/**      * Constant to rename keys      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_WRITE_THREADS
init|=
literal|"writeThreads"
decl_stmt|;
comment|/**      * Constant to enable encryption in S3.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_ENCRYPTION
init|=
literal|"s3Encryption"
decl_stmt|;
comment|/**      * Constant for no encryption. it is default.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_ENCRYPTION_NONE
init|=
literal|"NONE"
decl_stmt|;
comment|/**      *  Constant to set SSE_S3 encryption.      */
specifier|public
specifier|static
specifier|final
name|String
name|S3_ENCRYPTION_SSE_S3
init|=
literal|"SSE_S3"
decl_stmt|;
comment|/**      * private constructor so that class cannot initialized from outside.      */
specifier|private
name|S3Constants
parameter_list|()
block|{      }
block|}
end_class

end_unit

