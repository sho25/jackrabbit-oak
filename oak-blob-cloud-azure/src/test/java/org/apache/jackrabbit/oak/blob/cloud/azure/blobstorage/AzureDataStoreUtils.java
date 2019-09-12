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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|StandardSystemProperty
operator|.
name|USER_HOME
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
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HttpsURLConnection
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
name|Predicate
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
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|commons
operator|.
name|PropertiesUtil
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
comment|/**  * Extension to {@link DataStoreUtils} to enable Azure extensions for cleaning and initialization.  */
end_comment

begin_class
specifier|public
class|class
name|AzureDataStoreUtils
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
name|AzureDataStoreUtils
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
literal|"./src/test/resources/azure.properties"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PROPERTY_FILE
init|=
literal|"azure.properties"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SYS_PROP_NAME
init|=
literal|"azure.config"
decl_stmt|;
comment|/**      * Check for presence of mandatory properties.      *      * @return true if mandatory props configured.      */
specifier|public
specifier|static
name|boolean
name|isAzureConfigured
parameter_list|()
block|{
name|Properties
name|props
init|=
name|getAzureConfig
argument_list|()
decl_stmt|;
comment|//need either access keys or sas
if|if
condition|(
operator|!
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_KEY
argument_list|)
operator|||
operator|!
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_NAME
argument_list|)
operator|||
operator|!
operator|(
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_BLOB_CONTAINER_NAME
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_SAS
argument_list|)
operator|||
operator|!
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_BLOB_ENDPOINT
argument_list|)
operator|||
operator|!
operator|(
name|props
operator|.
name|containsKey
argument_list|(
name|AzureConstants
operator|.
name|AZURE_BLOB_CONTAINER_NAME
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Read any config property configured.      * Also, read any props available as system properties.      * System properties take precedence.      *      * @return Properties instance      */
specifier|public
specifier|static
name|Properties
name|getAzureConfig
parameter_list|()
block|{
name|String
name|config
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|SYS_PROP_NAME
argument_list|)
decl_stmt|;
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
name|File
name|cfgFile
init|=
operator|new
name|File
argument_list|(
name|USER_HOME
operator|.
name|value
argument_list|()
argument_list|,
name|DEFAULT_PROPERTY_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfgFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|config
operator|=
name|cfgFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
block|}
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
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|config
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|putAll
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|Map
name|filtered
init|=
name|Maps
operator|.
name|filterEntries
argument_list|(
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Object
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Object
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|input
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
operator|(
name|String
operator|)
name|input
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|)
empty_stmt|;
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|putAll
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
end_class

begin_function
specifier|public
specifier|static
name|DataStore
name|getAzureDataStore
parameter_list|(
name|Properties
name|props
parameter_list|,
name|String
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|AzureDataStore
name|ds
init|=
operator|new
name|AzureDataStore
argument_list|()
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setProperties
argument_list|(
name|props
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
name|ds
return|;
block|}
end_function

begin_function
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|DataStore
parameter_list|>
name|T
name|setupDirectAccessDataStore
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|TemporaryFolder
name|homeDir
parameter_list|,
name|int
name|directDownloadExpirySeconds
parameter_list|,
name|int
name|directUploadExpirySeconds
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|setupDirectAccessDataStore
argument_list|(
name|homeDir
argument_list|,
name|directDownloadExpirySeconds
argument_list|,
name|directUploadExpirySeconds
argument_list|,
literal|null
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|DataStore
parameter_list|>
name|T
name|setupDirectAccessDataStore
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|TemporaryFolder
name|homeDir
parameter_list|,
name|int
name|directDownloadExpirySeconds
parameter_list|,
name|int
name|directUploadExpirySeconds
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Properties
name|overrideProperties
parameter_list|)
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isAzureConfigured
argument_list|()
argument_list|)
expr_stmt|;
name|DataStore
name|ds
init|=
operator|(
name|T
operator|)
name|getAzureDataStore
argument_list|(
name|getDirectAccessDataStoreProperties
argument_list|(
name|overrideProperties
argument_list|)
argument_list|,
name|homeDir
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ds
operator|instanceof
name|ConfigurableDataRecordAccessProvider
condition|)
block|{
operator|(
operator|(
name|ConfigurableDataRecordAccessProvider
operator|)
name|ds
operator|)
operator|.
name|setDirectDownloadURIExpirySeconds
argument_list|(
name|directDownloadExpirySeconds
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ConfigurableDataRecordAccessProvider
operator|)
name|ds
operator|)
operator|.
name|setDirectUploadURIExpirySeconds
argument_list|(
name|directUploadExpirySeconds
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|T
operator|)
name|ds
return|;
block|}
end_function

begin_function
specifier|public
specifier|static
name|Properties
name|getDirectAccessDataStoreProperties
parameter_list|()
block|{
return|return
name|getDirectAccessDataStoreProperties
argument_list|(
literal|null
argument_list|)
return|;
block|}
end_function

begin_function
specifier|public
specifier|static
name|Properties
name|getDirectAccessDataStoreProperties
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Properties
name|overrideProperties
parameter_list|)
block|{
name|Properties
name|mergedProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|mergedProperties
operator|.
name|putAll
argument_list|(
name|getAzureConfig
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|overrideProperties
condition|)
block|{
name|mergedProperties
operator|.
name|putAll
argument_list|(
name|overrideProperties
argument_list|)
expr_stmt|;
block|}
comment|// set properties needed for direct access testing
if|if
condition|(
literal|null
operator|==
name|mergedProperties
operator|.
name|getProperty
argument_list|(
literal|"cacheSize"
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|mergedProperties
operator|.
name|put
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
return|return
name|mergedProperties
return|;
block|}
end_function

begin_function
specifier|public
specifier|static
name|void
name|deleteContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|containerName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot delete container with null or empty name. containerName={}"
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Starting to delete container. containerName={}"
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getAzureConfig
argument_list|()
decl_stmt|;
name|CloudBlobContainer
name|container
init|=
name|Utils
operator|.
name|getBlobContainer
argument_list|(
name|Utils
operator|.
name|getConnectionStringFromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|container
operator|.
name|deleteIfExists
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Container deleted. containerName={} existed={}"
argument_list|,
name|containerName
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|protected
specifier|static
name|HttpsURLConnection
name|getHttpsConnection
parameter_list|(
name|long
name|length
parameter_list|,
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Content-Length"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Date"
argument_list|,
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ssX"
argument_list|)
operator|.
name|withZone
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|format
argument_list|(
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"x-ms-version"
argument_list|,
literal|"2017-11-09"
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
end_function

unit|}
end_unit

