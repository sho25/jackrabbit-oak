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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3Client
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|DeleteObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectListing
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectSummary
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|transfer
operator|.
name|TransferManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|Backend
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|DataStore
import|;
end_import

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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
operator|.
name|S3Backend
import|;
end_import

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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
operator|.
name|S3DataStore
import|;
end_import

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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
operator|.
name|SharedS3DataStore
import|;
end_import

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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
operator|.
name|Utils
import|;
end_import

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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Extension to {@link DataStoreUtils} to enable S3 extensions for cleaning.  */
end_comment

begin_class
specifier|public
class|class
name|S3DataStoreUtils
extends|extends
name|DataStoreUtils
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|S3DataStoreUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|isS3DataStore
parameter_list|()
block|{
name|String
name|dsName
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|DS_CLASS_NAME
argument_list|)
decl_stmt|;
return|return
operator|(
name|dsName
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|dsName
operator|.
name|equals
argument_list|(
name|S3DataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|dsName
operator|.
name|equals
argument_list|(
name|SharedS3DataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
return|;
block|}
comment|/**      * S3 specific cleanup      *      * @param dataStore the underlying DataStore instance      * @param date the date of initialization      * @throws Exception      */
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|(
name|DataStore
name|dataStore
parameter_list|,
name|Date
name|date
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|dataStore
operator|instanceof
name|S3DataStore
condition|)
block|{
name|Backend
name|backend
init|=
operator|(
operator|(
name|S3DataStore
operator|)
name|dataStore
operator|)
operator|.
name|getBackend
argument_list|()
decl_stmt|;
if|if
condition|(
name|backend
operator|instanceof
name|S3Backend
condition|)
block|{
name|String
name|bucket
init|=
operator|(
operator|(
name|S3Backend
operator|)
name|backend
operator|)
operator|.
name|getBucket
argument_list|()
decl_stmt|;
name|deleteBucket
argument_list|(
name|bucket
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|deleteBucket
parameter_list|(
name|String
name|bucket
parameter_list|,
name|Date
name|date
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"cleaning bucket ["
operator|+
name|bucket
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|Utils
operator|.
name|readConfig
argument_list|(
operator|(
name|String
operator|)
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"config"
argument_list|)
argument_list|)
decl_stmt|;
name|AmazonS3Client
name|s3service
init|=
name|Utils
operator|.
name|openService
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|TransferManager
name|tmx
init|=
operator|new
name|TransferManager
argument_list|(
name|s3service
argument_list|)
decl_stmt|;
if|if
condition|(
name|s3service
operator|.
name|doesBucketExist
argument_list|(
name|bucket
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|tmx
operator|.
name|abortMultipartUploads
argument_list|(
name|bucket
argument_list|,
name|date
argument_list|)
expr_stmt|;
name|ObjectListing
name|prevObjectListing
init|=
name|s3service
operator|.
name|listObjects
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
while|while
condition|(
name|prevObjectListing
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|deleteList
init|=
operator|new
name|ArrayList
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|S3ObjectSummary
name|s3ObjSumm
range|:
name|prevObjectListing
operator|.
name|getObjectSummaries
argument_list|()
control|)
block|{
name|deleteList
operator|.
name|add
argument_list|(
operator|new
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|(
name|s3ObjSumm
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DeleteObjectsRequest
name|delObjsReq
init|=
operator|new
name|DeleteObjectsRequest
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
name|delObjsReq
operator|.
name|setKeys
argument_list|(
name|deleteList
argument_list|)
expr_stmt|;
name|s3service
operator|.
name|deleteObjects
argument_list|(
name|delObjsReq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|prevObjectListing
operator|.
name|isTruncated
argument_list|()
condition|)
break|break;
name|prevObjectListing
operator|=
name|s3service
operator|.
name|listNextBatchOfObjects
argument_list|(
name|prevObjectListing
argument_list|)
expr_stmt|;
block|}
block|}
comment|//s3service.deleteBucket(bucket);
name|log
operator|.
name|info
argument_list|(
literal|"bucket [ "
operator|+
name|bucket
operator|+
literal|"] cleaned"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"bucket ["
operator|+
name|bucket
operator|+
literal|"] doesn't exists"
argument_list|)
expr_stmt|;
block|}
name|tmx
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|s3service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

