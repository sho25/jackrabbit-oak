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
name|upgrade
operator|.
name|cli
operator|.
name|node
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
name|SegmentCache
operator|.
name|DEFAULT_SEGMENT_CACHE_MB
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
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|FileStoreUtils
operator|.
name|asCloseable
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
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
name|SegmentNodeStoreBuilders
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
name|ReadOnlyFileStore
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|FileStoreUtils
operator|.
name|NodeStoreWithFileStore
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
name|io
operator|.
name|Closer
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
name|io
operator|.
name|Files
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

begin_class
specifier|public
class|class
name|SegmentAzureFactory
implements|implements
name|NodeStoreFactory
block|{
specifier|private
specifier|final
name|String
name|accountName
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|String
name|connectionString
decl_stmt|;
specifier|private
specifier|final
name|String
name|containerName
decl_stmt|;
specifier|private
specifier|final
name|String
name|dir
decl_stmt|;
specifier|private
name|int
name|segmentCacheSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
specifier|final
name|String
name|dir
decl_stmt|;
specifier|private
specifier|final
name|int
name|segmentCacheSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|private
name|String
name|accountName
decl_stmt|;
specifier|private
name|String
name|uri
decl_stmt|;
specifier|private
name|String
name|connectionString
decl_stmt|;
specifier|private
name|String
name|containerName
decl_stmt|;
specifier|public
name|Builder
parameter_list|(
name|String
name|dir
parameter_list|,
name|int
name|segmentCacheSize
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|segmentCacheSize
operator|=
name|segmentCacheSize
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|readOnly
expr_stmt|;
block|}
specifier|public
name|Builder
name|accountName
parameter_list|(
name|String
name|accountName
parameter_list|)
block|{
name|this
operator|.
name|accountName
operator|=
name|accountName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|uri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|connectionString
parameter_list|(
name|String
name|connectionString
parameter_list|)
block|{
name|this
operator|.
name|connectionString
operator|=
name|connectionString
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|containerName
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
name|this
operator|.
name|containerName
operator|=
name|containerName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SegmentAzureFactory
name|build
parameter_list|()
block|{
return|return
operator|new
name|SegmentAzureFactory
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|public
name|SegmentAzureFactory
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|accountName
operator|=
name|builder
operator|.
name|accountName
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|builder
operator|.
name|uri
expr_stmt|;
name|this
operator|.
name|connectionString
operator|=
name|builder
operator|.
name|connectionString
expr_stmt|;
name|this
operator|.
name|containerName
operator|=
name|builder
operator|.
name|containerName
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|builder
operator|.
name|dir
expr_stmt|;
name|this
operator|.
name|segmentCacheSize
operator|=
name|builder
operator|.
name|segmentCacheSize
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|builder
operator|.
name|readOnly
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|create
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|AzurePersistence
name|azPersistence
init|=
literal|null
decl_stmt|;
try|try
block|{
name|azPersistence
operator|=
name|createAzurePersistence
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|URISyntaxException
decl||
name|InvalidKeyException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|File
name|tmpDir
init|=
name|Files
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
parameter_list|()
lambda|->
name|tmpDir
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|tmpDir
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|azPersistence
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|readOnly
condition|)
block|{
specifier|final
name|ReadOnlyFileStore
name|fs
decl_stmt|;
name|builder
operator|.
name|withSegmentCacheSize
argument_list|(
name|segmentCacheSize
operator|>
literal|0
condition|?
name|segmentCacheSize
else|:
name|DEFAULT_SEGMENT_CACHE_MB
argument_list|)
expr_stmt|;
name|fs
operator|=
name|builder
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|fs
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|FileStore
name|fs
decl_stmt|;
name|fs
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|fs
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeStoreWithFileStore
argument_list|(
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|fs
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|AzurePersistence
name|createAzurePersistence
parameter_list|()
throws|throws
name|StorageException
throws|,
name|URISyntaxException
throws|,
name|InvalidKeyException
block|{
name|CloudBlobDirectory
name|cloudBlobDirectory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|accountName
operator|!=
literal|null
operator|&&
name|uri
operator|!=
literal|null
condition|)
block|{
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
operator|new
name|StorageCredentialsAccountAndKey
argument_list|(
name|accountName
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|cloudBlobDirectory
operator|=
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
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|connectionString
operator|!=
literal|null
operator|&&
name|containerName
operator|!=
literal|null
condition|)
block|{
name|cloudBlobDirectory
operator|=
name|AzureUtilities
operator|.
name|cloudBlobDirectoryFrom
argument_list|(
name|connectionString
argument_list|,
name|containerName
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cloudBlobDirectory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not connect to Azure storage. Too few connection parameters specified!"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AzurePersistence
argument_list|(
name|cloudBlobDirectory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasExternalBlobReferences
parameter_list|()
throws|throws
name|IOException
block|{
name|AzurePersistence
name|azPersistence
init|=
literal|null
decl_stmt|;
try|try
block|{
name|azPersistence
operator|=
name|createAzurePersistence
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|URISyntaxException
decl||
name|InvalidKeyException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|File
name|tmpDir
init|=
name|Files
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|tmpDir
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|azPersistence
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|ReadOnlyFileStore
name|fs
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|builder
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
return|return
name|FileStoreUtils
operator|.
name|hasExternalBlobReferences
argument_list|(
name|fs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|tmpDir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"AzureSegmentNodeStore[%s]"
argument_list|,
name|dir
argument_list|)
return|;
block|}
block|}
end_class

end_unit

