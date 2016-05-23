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
name|blob
operator|.
name|cloud
operator|.
name|aws
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|CachingDataStore
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
name|TestCaseBase
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

begin_comment
comment|/**  * Test {@link org.apache.jackrabbit.core.data.CachingDataStore} with S3Backend and local cache on. It requires  * to pass aws config file via system property. For e.g.  * -Dconfig=/opt/cq/aws.properties. Sample aws properties located at  * src/test/resources/aws.properties  */
end_comment

begin_class
specifier|public
class|class
name|TestS3Ds
extends|extends
name|TestCaseBase
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestS3Ds
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CONFIG_PATH
init|=
literal|"./src/test/resources/aws.properties"
decl_stmt|;
specifier|private
name|Date
name|startTime
init|=
literal|null
decl_stmt|;
specifier|protected
name|Properties
name|props
decl_stmt|;
specifier|protected
name|String
name|config
decl_stmt|;
specifier|public
name|TestS3Ds
parameter_list|()
throws|throws
name|IOException
block|{
name|config
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|CONFIG
argument_list|)
expr_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|config
argument_list|)
condition|)
block|{
name|config
operator|=
name|DEFAULT_CONFIG_PATH
expr_stmt|;
block|}
name|props
operator|=
name|Utils
operator|.
name|readConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|startTime
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|String
name|bucket
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|randomGen
operator|.
name|nextInt
argument_list|(
literal|9999
argument_list|)
argument_list|)
operator|+
literal|"-"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|randomGen
operator|.
name|nextInt
argument_list|(
literal|9999
argument_list|)
argument_list|)
operator|+
literal|"-test"
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
comment|// delete bucket if exists
name|deleteBucket
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|deleteBucket
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|protected
name|CachingDataStore
name|createDataStore
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|S3DataStore
name|s3ds
init|=
operator|new
name|S3DataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|setSecret
argument_list|(
literal|"123456"
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|init
argument_list|(
name|dataStoreDir
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
name|s3ds
return|;
block|}
comment|/**      * Cleaning of bucket after test run.      */
comment|/**      * Cleaning of bucket after test run.      */
specifier|public
name|void
name|deleteBucket
parameter_list|()
throws|throws
name|Exception
block|{
name|Backend
name|backend
init|=
operator|(
operator|(
name|S3DataStore
operator|)
name|ds
operator|)
operator|.
name|getBackend
argument_list|()
decl_stmt|;
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
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|String
name|bucket
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"deleting bucket ["
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
name|config
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
name|startTime
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
name|s3service
operator|.
name|deleteBucket
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"bucket [ "
operator|+
name|bucket
operator|+
literal|"] deleted"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
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

