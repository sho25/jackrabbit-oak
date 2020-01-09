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
name|jcr
operator|.
name|binary
operator|.
name|fixtures
operator|.
name|datastore
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|AmazonS3
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
name|BucketAccelerateConfiguration
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
name|BucketAccelerateStatus
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
name|CreateBucketRequest
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
name|model
operator|.
name|SetBucketAccelerateConfigurationRequest
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
name|core
operator|.
name|data
operator|.
name|DataStoreException
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
name|s3
operator|.
name|S3Constants
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|jcr
operator|.
name|binary
operator|.
name|fixtures
operator|.
name|nodestore
operator|.
name|FixtureUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
comment|/**  * Fixture for S3DataStore based on an aws.properties config file. It creates  * a new temporary Azure Blob Container for each DataStore created.  *  *<p>  * Note: when using this, it's highly recommended to reuse the NodeStores across multiple tests (using  * {@link org.apache.jackrabbit.oak.jcr.AbstractRepositoryTest#AbstractRepositoryTest(NodeStoreFixture, boolean) AbstractRepositoryTest(fixture, true)})  * otherwise it will be slower and can lead to out of memory issues if there are many tests.  *  *<p>  * Test buckets are named "direct-binary-test-...". If some did not get cleaned up, you can  * list them using the aws cli with this command:  *<pre>  *     aws s3 ls | grep direct-binary-test-  *</pre>  *  * And after checking, delete them all in one go with this command:  *<pre>  *     aws s3 ls | grep direct-binary-test- | cut -f 3 -d " " | xargs -n 1 -I {} sh -c 'aws s3 rb s3://{} || exit 1'  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|S3DataStoreFixture
implements|implements
name|DataStoreFixture
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Nullable
specifier|private
specifier|final
name|Properties
name|s3Props
decl_stmt|;
specifier|public
name|S3DataStoreFixture
parameter_list|()
block|{
name|s3Props
operator|=
name|FixtureUtils
operator|.
name|loadDataStoreProperties
argument_list|(
literal|"s3.config"
argument_list|,
literal|"aws.properties"
argument_list|,
literal|".aws"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
if|if
condition|(
name|s3Props
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping S3 DataStore fixture because no S3 properties file was found given by "
operator|+
literal|"'s3.config' system property or named 'aws.properties' or '~/.aws/aws.properties'."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|DataStore
name|createDataStore
parameter_list|()
block|{
if|if
condition|(
name|s3Props
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"createDataStore() called but this fixture is not available"
argument_list|)
throw|;
block|}
name|AmazonS3
name|s3Client
init|=
name|Utils
operator|.
name|openService
argument_list|(
name|s3Props
argument_list|)
decl_stmt|;
comment|// Create a temporary bucket that will be removed at test completion
name|String
name|bucketName
init|=
literal|"direct-binary-test-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating S3 test bucket {}"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|CreateBucketRequest
name|createBucket
init|=
operator|new
name|CreateBucketRequest
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|s3Client
operator|.
name|createBucket
argument_list|(
name|createBucket
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to create test bucket ["
operator|+
name|bucketName
operator|+
literal|"]"
argument_list|,
name|Utils
operator|.
name|waitForBucket
argument_list|(
name|s3Client
argument_list|,
name|bucketName
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Enabling S3 acceleration for bucket {}"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|s3Client
operator|.
name|setBucketAccelerateConfiguration
argument_list|(
operator|new
name|SetBucketAccelerateConfigurationRequest
argument_list|(
name|bucketName
argument_list|,
operator|new
name|BucketAccelerateConfiguration
argument_list|(
name|BucketAccelerateStatus
operator|.
name|Enabled
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|s3Client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// create new properties since azProps is shared for all created DataStores
name|Properties
name|clonedS3Props
init|=
operator|new
name|Properties
argument_list|(
name|s3Props
argument_list|)
decl_stmt|;
name|clonedS3Props
operator|.
name|setProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|,
name|createBucket
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
comment|// setup Oak DS
name|S3DataStore
name|dataStore
init|=
operator|new
name|S3DataStore
argument_list|()
decl_stmt|;
name|dataStore
operator|.
name|setProperties
argument_list|(
name|clonedS3Props
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|setStagingSplitPercentage
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"s3props: "
operator|+
name|s3Props
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dataStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|DataStore
name|dataStore
parameter_list|)
block|{
if|if
condition|(
name|dataStore
operator|!=
literal|null
operator|&&
name|dataStore
operator|instanceof
name|S3DataStore
condition|)
block|{
try|try
block|{
name|dataStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Issue while disposing DataStore"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"IllegalStateException trying to close S3 connection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|S3DataStore
name|s3DataStore
init|=
operator|(
name|S3DataStore
operator|)
name|dataStore
decl_stmt|;
name|String
name|bucketName
init|=
operator|(
operator|(
name|S3Backend
operator|)
name|s3DataStore
operator|.
name|getBackend
argument_list|()
operator|)
operator|.
name|getBucket
argument_list|()
decl_stmt|;
if|if
condition|(
name|s3Props
operator|==
literal|null
condition|)
block|{
comment|// should be impossible if we created the client successfully in createDataStore()
name|log
operator|.
name|warn
argument_list|(
literal|"Could not cleanup and remove S3 bucket {}"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
return|return;
block|}
name|AmazonS3
name|s3Client
init|=
name|Utils
operator|.
name|openService
argument_list|(
name|s3Props
argument_list|)
decl_stmt|;
comment|// For S3, you have to empty the bucket before removing the bucket itself
name|log
operator|.
name|info
argument_list|(
literal|"Emptying S3 test bucket {}"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|ObjectListing
name|listing
init|=
name|s3Client
operator|.
name|listObjects
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
for|for
control|(
name|S3ObjectSummary
name|summary
range|:
name|listing
operator|.
name|getObjectSummaries
argument_list|()
control|)
block|{
name|s3Client
operator|.
name|deleteObject
argument_list|(
name|bucketName
argument_list|,
name|summary
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|listing
operator|.
name|isTruncated
argument_list|()
condition|)
block|{
break|break;
block|}
name|listing
operator|=
name|s3Client
operator|.
name|listNextBatchOfObjects
argument_list|(
name|listing
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Removing S3 test bucket {}"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|s3Client
operator|.
name|deleteBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|s3Client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

