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
name|plugins
operator|.
name|blob
operator|.
name|datastore
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|core
operator|.
name|data
operator|.
name|FileDataStore
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
name|commons
operator|.
name|PropertiesUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
operator|.
name|concat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Helper for retrieving the {@link DataStoreBlobStore} instantiated via system properties  *  * User must specify the class of DataStore to use via 'dataStore' system property  *  * Further to configure properties of DataStore instance one can specify extra system property  * where the key has a prefix 'ds.' or 'bs.'. So to set 'minRecordLength' of FileDataStore specify  * the system property as 'ds.minRecordLength'  */
end_comment

begin_class
specifier|public
class|class
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
name|DataStoreUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DS_CLASS_NAME
init|=
literal|"dataStore"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DS_PROP_PREFIX
init|=
literal|"ds."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BS_PROP_PREFIX
init|=
literal|"bs."
decl_stmt|;
comment|/**      * By default create a default directory. But if overridden will need to be unset      */
specifier|public
specifier|static
name|long
name|time
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|File
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getBlobStore
argument_list|(
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|String
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|className
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|DS_CLASS_NAME
argument_list|,
name|OakFileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|DataStore
name|ds
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|DataStore
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|getConfig
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getBlobStore
argument_list|(
name|getHomeDir
argument_list|()
argument_list|)
return|;
block|}
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
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|e
range|:
name|Maps
operator|.
name|fromProperties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|DS_PROP_PREFIX
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
name|BS_PROP_PREFIX
argument_list|)
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|//length of bs.
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|concat
argument_list|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"target/blobstore/"
operator|+
operator|(
name|time
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|time
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|FileDataStore
name|createFDS
parameter_list|(
name|File
name|root
parameter_list|,
name|int
name|minRecordLength
parameter_list|)
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
name|minRecordLength
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|fds
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertySetup
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|DS_CLASS_NAME
argument_list|,
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"ds.minRecordLength"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
name|getBlobStore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|dbs
operator|.
name|getDataStore
argument_list|()
operator|.
name|getMinRecordLength
argument_list|()
argument_list|)
expr_stmt|;
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

