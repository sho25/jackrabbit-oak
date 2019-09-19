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
name|blob
operator|.
name|cloud
operator|.
name|azure
operator|.
name|blobstorage
package|;
end_package

begin_import
import|import static
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
operator|.
name|AzureConstants
operator|.
name|PRESIGNED_HTTP_DOWNLOAD_URI_DOMAIN_OVERRIDE
import|;
end_import

begin_import
import|import static
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
operator|.
name|AzureConstants
operator|.
name|PRESIGNED_HTTP_DOWNLOAD_URI_VERIFY_EXISTS
import|;
end_import

begin_import
import|import static
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
operator|.
name|AzureConstants
operator|.
name|PRESIGNED_HTTP_UPLOAD_URI_DOMAIN_OVERRIDE
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

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
name|net
operator|.
name|URI
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
name|DataIdentifier
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
name|directaccess
operator|.
name|ConfigurableDataRecordAccessProvider
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
name|directaccess
operator|.
name|DataRecordDownloadOptions
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
name|directaccess
operator|.
name|DataRecordUpload
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|AzureDataRecordAccessProviderCDNTest
extends|extends
name|AzureDataRecordAccessProviderTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|TemporaryFolder
name|homeDir
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|AzureDataStore
name|cdnDataStore
decl_stmt|;
specifier|private
specifier|static
name|String
name|DOWNLOAD_URI_DOMAIN
init|=
name|AzureDataStoreUtils
operator|.
name|getDirectAccessDataStoreProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PRESIGNED_HTTP_DOWNLOAD_URI_DOMAIN_OVERRIDE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|UPLOAD_URI_DOMAIN
init|=
name|AzureDataStoreUtils
operator|.
name|getDirectAccessDataStoreProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PRESIGNED_HTTP_UPLOAD_URI_DOMAIN_OVERRIDE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|cdnSetupNotice
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s\n%s %s '%s' %s '%s' %s %s"
argument_list|,
literal|"No override domains configured - skipping Azure CDN tests."
argument_list|,
literal|"To run these tests, set up an Azure CDN in the Azure console or command line,"
argument_list|,
literal|"then set the CDN domain as the property value for"
argument_list|,
name|PRESIGNED_HTTP_DOWNLOAD_URI_DOMAIN_OVERRIDE
argument_list|,
literal|"and/or"
argument_list|,
name|PRESIGNED_HTTP_UPLOAD_URI_DOMAIN_OVERRIDE
argument_list|,
literal|"in your Azure configuration file, and then provide this file to the"
argument_list|,
literal|"test via the -Dazure.config command-line switch"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|cdnSetupNotice
argument_list|,
name|isCDNConfigured
argument_list|()
argument_list|)
expr_stmt|;
name|cdnDataStore
operator|=
name|AzureDataStoreUtils
operator|.
name|setupDirectAccessDataStore
argument_list|(
name|homeDir
argument_list|,
name|expirySeconds
argument_list|,
name|expirySeconds
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isCDNConfigured
parameter_list|()
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|DOWNLOAD_URI_DOMAIN
argument_list|)
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|UPLOAD_URI_DOMAIN
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|AzureDataStore
name|createDataStore
parameter_list|(
annotation|@
name|NotNull
name|Properties
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|AzureDataStoreUtils
operator|.
name|setupDirectAccessDataStore
argument_list|(
name|homeDir
argument_list|,
name|expirySeconds
argument_list|,
name|expirySeconds
argument_list|,
name|properties
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurableDataRecordAccessProvider
name|getDataStore
parameter_list|()
block|{
return|return
name|cdnDataStore
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurableDataRecordAccessProvider
name|getDataStore
parameter_list|(
annotation|@
name|NotNull
name|Properties
name|overrideProperties
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createDataStore
argument_list|(
name|AzureDataStoreUtils
operator|.
name|getDirectAccessDataStoreProperties
argument_list|(
name|overrideProperties
argument_list|)
argument_list|)
return|;
block|}
comment|// CDN Tests
annotation|@
name|Test
specifier|public
name|void
name|testCDNDownloadURIContainsDownloadDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|PRESIGNED_HTTP_DOWNLOAD_URI_VERIFY_EXISTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ConfigurableDataRecordAccessProvider
name|ds
init|=
name|getDataStore
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|DataIdentifier
name|id
init|=
operator|new
name|DataIdentifier
argument_list|(
literal|"identifier"
argument_list|)
decl_stmt|;
name|URI
name|downloadUri
init|=
name|ds
operator|.
name|getDownloadURI
argument_list|(
name|id
argument_list|,
name|DataRecordDownloadOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|downloadUri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOWNLOAD_URI_DOMAIN
argument_list|,
name|downloadUri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCDNUploadURIContainsUploadDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|PRESIGNED_HTTP_DOWNLOAD_URI_VERIFY_EXISTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ConfigurableDataRecordAccessProvider
name|ds
init|=
name|getDataStore
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|DataRecordUpload
name|upload
init|=
name|ds
operator|.
name|initiateDataRecordUpload
argument_list|(
name|ONE_MB
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|upload
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|upload
operator|.
name|getUploadURIs
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|upload
operator|.
name|getUploadURIs
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|UPLOAD_URI_DOMAIN
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
