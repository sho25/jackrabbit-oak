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
name|segment
operator|.
name|azure
operator|.
name|tool
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_ACCOUNT_NAME
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_DIR
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_STORAGE_URI
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromUri
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|Stopwatch
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
name|StorageCredentials
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
name|StorageCredentialsAccountAndKey
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
name|StorageException
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
name|CloudBlobDirectory
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
name|segment
operator|.
name|azure
operator|.
name|AzurePersistence
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
name|segment
operator|.
name|azure
operator|.
name|AzureUtilities
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarPersistence
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
name|segment
operator|.
name|spi
operator|.
name|monitor
operator|.
name|FileStoreMonitorAdapter
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
name|segment
operator|.
name|spi
operator|.
name|monitor
operator|.
name|IOMonitorAdapter
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
name|segment
operator|.
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveManager
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
name|segment
operator|.
name|spi
operator|.
name|persistence
operator|.
name|SegmentNodeStorePersistence
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Utility class for common stuff pertaining to tooling.  */
end_comment

begin_class
specifier|public
class|class
name|ToolUtils
block|{
specifier|private
name|ToolUtils
parameter_list|()
block|{
comment|// prevent instantiation
block|}
specifier|public
enum|enum
name|SegmentStoreType
block|{
name|TAR
argument_list|(
literal|"TarMK Segment Store"
argument_list|)
block|,
name|AZURE
argument_list|(
literal|"Azure Segment Store"
argument_list|)
block|;
specifier|private
name|String
name|type
decl_stmt|;
name|SegmentStoreType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|description
parameter_list|(
name|String
name|pathOrUri
parameter_list|)
block|{
name|String
name|location
init|=
name|pathOrUri
decl_stmt|;
if|if
condition|(
name|pathOrUri
operator|.
name|startsWith
argument_list|(
literal|"az:"
argument_list|)
condition|)
block|{
name|location
operator|=
name|pathOrUri
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
return|return
name|type
operator|+
literal|"@"
operator|+
name|location
return|;
block|}
block|}
specifier|public
specifier|static
name|FileStore
name|newFileStore
parameter_list|(
name|SegmentNodeStorePersistence
name|persistence
parameter_list|,
name|File
name|directory
parameter_list|,
name|boolean
name|strictVersionCheck
parameter_list|,
name|int
name|segmentCacheSize
parameter_list|,
name|long
name|gcLogInterval
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|URISyntaxException
throws|,
name|StorageException
block|{
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|directory
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|persistence
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|withStrictVersionCheck
argument_list|(
name|strictVersionCheck
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
name|segmentCacheSize
argument_list|)
operator|.
name|withGCOptions
argument_list|(
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
operator|.
name|setGCLogInterval
argument_list|(
name|gcLogInterval
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|SegmentNodeStorePersistence
name|newSegmentNodeStorePersistence
parameter_list|(
name|SegmentStoreType
name|storeType
parameter_list|,
name|String
name|pathOrUri
parameter_list|)
block|{
name|SegmentNodeStorePersistence
name|persistence
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|storeType
condition|)
block|{
case|case
name|AZURE
case|:
name|CloudBlobDirectory
name|cloudBlobDirectory
init|=
name|createCloudBlobDirectory
argument_list|(
name|pathOrUri
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|persistence
operator|=
operator|new
name|AzurePersistence
argument_list|(
name|cloudBlobDirectory
argument_list|)
expr_stmt|;
break|break;
default|default:
name|persistence
operator|=
operator|new
name|TarPersistence
argument_list|(
operator|new
name|File
argument_list|(
name|pathOrUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|persistence
return|;
block|}
specifier|public
specifier|static
name|SegmentArchiveManager
name|createArchiveManager
parameter_list|(
name|SegmentNodeStorePersistence
name|persistence
parameter_list|)
block|{
name|SegmentArchiveManager
name|archiveManager
init|=
literal|null
decl_stmt|;
try|try
block|{
name|archiveManager
operator|=
name|persistence
operator|.
name|createArchiveManager
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|,
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not access the Azure Storage. Please verify the path provided!"
argument_list|)
throw|;
block|}
return|return
name|archiveManager
return|;
block|}
specifier|public
specifier|static
name|CloudBlobDirectory
name|createCloudBlobDirectory
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|parseAzureConfigurationFromUri
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|accountName
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_ACCOUNT_NAME
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"AZURE_SECRET_KEY"
argument_list|)
decl_stmt|;
name|StorageCredentials
name|credentials
init|=
literal|null
decl_stmt|;
try|try
block|{
name|credentials
operator|=
operator|new
name|StorageCredentialsAccountAndKey
argument_list|(
name|accountName
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not connect to the Azure Storage. Please verify if AZURE_SECRET_KEY environment variable "
operator|+
literal|"is correctly set!"
argument_list|)
throw|;
block|}
name|String
name|uri
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_STORAGE_URI
argument_list|)
decl_stmt|;
name|String
name|dir
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|AzureUtilities
operator|.
name|cloudBlobDirectoryFrom
argument_list|(
name|credentials
argument_list|,
name|uri
argument_list|,
name|dir
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not connect to the Azure Storage. Please verify the path provided!"
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|SegmentStoreType
name|storeTypeFromPathOrUri
parameter_list|(
name|String
name|pathOrUri
parameter_list|)
block|{
if|if
condition|(
name|pathOrUri
operator|.
name|startsWith
argument_list|(
literal|"az:"
argument_list|)
condition|)
block|{
return|return
name|SegmentStoreType
operator|.
name|AZURE
return|;
block|}
return|return
name|SegmentStoreType
operator|.
name|TAR
return|;
block|}
specifier|public
specifier|static
name|String
name|storeDescription
parameter_list|(
name|SegmentStoreType
name|storeType
parameter_list|,
name|String
name|pathOrUri
parameter_list|)
block|{
return|return
name|storeType
operator|.
name|description
argument_list|(
name|pathOrUri
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|printableStopwatch
parameter_list|(
name|Stopwatch
name|s
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%ds)"
argument_list|,
name|s
argument_list|,
name|s
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|printMessage
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|arg
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|fetchByteArray
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

