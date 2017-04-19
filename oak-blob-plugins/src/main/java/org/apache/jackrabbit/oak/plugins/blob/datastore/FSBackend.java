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
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|RandomAccessFile
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
name|Iterator
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
name|annotation
operator|.
name|Nonnull
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
name|Function
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
name|io
operator|.
name|Closeables
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|FileFilterUtils
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
name|core
operator|.
name|data
operator|.
name|DataRecord
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
name|core
operator|.
name|data
operator|.
name|LazyFileInputStream
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
name|AbstractDataRecord
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
name|AbstractSharedBackend
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
name|normalizeNoEndSeparator
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|FSBackend
extends|extends
name|AbstractSharedBackend
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FSBackend
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FS_BACKEND_PATH
init|=
literal|"fsBackendPath"
decl_stmt|;
comment|/**      * The maximum last modified time resolution of the file system.      */
specifier|private
specifier|static
specifier|final
name|int
name|ACCESS_TIME_RESOLUTION
init|=
literal|2000
decl_stmt|;
specifier|private
name|Properties
name|properties
decl_stmt|;
specifier|private
name|String
name|fsPath
decl_stmt|;
specifier|private
name|File
name|fsPathDir
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|DataStoreException
block|{
name|fsPath
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
name|FS_BACKEND_PATH
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|fsPath
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|this
operator|.
name|fsPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Could not initialize FSBackend from "
operator|+
name|properties
operator|+
literal|". ["
operator|+
name|FS_BACKEND_PATH
operator|+
literal|"] property not found."
argument_list|)
throw|;
block|}
name|this
operator|.
name|fsPath
operator|=
name|normalizeNoEndSeparator
argument_list|(
name|fsPath
argument_list|)
expr_stmt|;
name|fsPathDir
operator|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|fsPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|fsPathDir
operator|.
name|exists
argument_list|()
operator|&&
name|fsPathDir
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Can not create a directory "
operator|+
literal|"because a file exists with the same name: "
operator|+
name|this
operator|.
name|fsPath
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fsPathDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|boolean
name|created
init|=
name|fsPathDir
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|created
condition|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Could not create directory: "
operator|+
name|fsPathDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|read
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|File
name|file
init|=
name|getFile
argument_list|(
name|identifier
argument_list|,
name|fsPathDir
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|LazyFileInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Error opening input stream of "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|File
name|dest
init|=
name|getFile
argument_list|(
name|identifier
argument_list|,
name|fsPathDir
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|getLastModified
argument_list|(
name|dest
argument_list|)
operator|<
name|now
operator|+
name|ACCESS_TIME_RESOLUTION
condition|)
block|{
name|setLastModified
argument_list|(
name|dest
argument_list|,
name|now
operator|+
name|ACCESS_TIME_RESOLUTION
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|file
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to copy [{}] to [{}]"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|dest
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Not able to write file ["
operator|+
name|identifier
operator|+
literal|"]"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|File
name|file
init|=
name|getFile
argument_list|(
name|identifier
argument_list|,
name|fsPathDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"getRecord:Identifier [{}] not found. Took [{}] ms."
argument_list|,
name|identifier
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Identifier ["
operator|+
name|identifier
operator|+
literal|"] not found."
argument_list|)
throw|;
block|}
return|return
operator|new
name|FSBackendDataRecord
argument_list|(
name|this
argument_list|,
name|identifier
argument_list|,
name|file
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataIdentifier
argument_list|>
name|getAllIdentifiers
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
name|Files
operator|.
name|fileTreeTraverser
argument_list|()
operator|.
name|postOrderTraversal
argument_list|(
name|fsPathDir
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|Predicate
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|File
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|isFile
argument_list|()
operator|&&
operator|!
name|normalizeNoEndSeparator
argument_list|(
name|input
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|fsPath
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|File
argument_list|,
name|DataIdentifier
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DataIdentifier
name|apply
parameter_list|(
name|File
name|input
parameter_list|)
block|{
return|return
operator|new
name|DataIdentifier
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|File
name|file
init|=
name|getFile
argument_list|(
name|identifier
argument_list|,
name|fsPathDir
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|exists
argument_list|()
operator|&&
name|file
operator|.
name|isFile
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|File
name|file
init|=
name|getFile
argument_list|(
name|identifier
argument_list|,
name|fsPathDir
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
name|deleteEmptyParentDirs
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fsPathDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyLarge
argument_list|(
name|input
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Closeables
operator|.
name|close
argument_list|(
name|os
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Closeables
operator|.
name|close
argument_list|(
name|input
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while adding metadata record with name {}, {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|name
block|,
name|e
block|}
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Could not add root record"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMetadataRecord
parameter_list|(
name|File
name|input
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fsPathDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|input
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while adding metadata record file {} with name {}, {}"
argument_list|,
name|input
argument_list|,
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Could not add root record"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|File
name|file
range|:
name|FileFilterUtils
operator|.
name|filter
argument_list|(
name|FileFilterUtils
operator|.
name|nameFileFilter
argument_list|(
name|name
argument_list|)
argument_list|,
name|fsPathDir
operator|.
name|listFiles
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
operator|new
name|FSBackendDataRecord
argument_list|(
name|this
argument_list|,
operator|new
name|DataIdentifier
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|file
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|DataRecord
argument_list|>
name|getAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|List
argument_list|<
name|DataRecord
argument_list|>
name|rootRecords
init|=
operator|new
name|ArrayList
argument_list|<
name|DataRecord
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|FileFilterUtils
operator|.
name|filterList
argument_list|(
name|FileFilterUtils
operator|.
name|prefixFileFilter
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|fsPathDir
operator|.
name|listFiles
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// skip directories which are actual data store files
name|rootRecords
operator|.
name|add
argument_list|(
operator|new
name|FSBackendDataRecord
argument_list|(
name|this
argument_list|,
operator|new
name|DataIdentifier
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rootRecords
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|File
name|file
range|:
name|FileFilterUtils
operator|.
name|filterList
argument_list|(
name|FileFilterUtils
operator|.
name|nameFileFilter
argument_list|(
name|name
argument_list|)
argument_list|,
name|fsPathDir
operator|.
name|listFiles
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// skip directories which are actual data store files
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete root record {} "
argument_list|,
operator|new
name|Object
index|[]
block|{
name|file
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteAllMetadataRecords
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
for|for
control|(
name|File
name|file
range|:
name|FileFilterUtils
operator|.
name|filterList
argument_list|(
name|FileFilterUtils
operator|.
name|prefixFileFilter
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|fsPathDir
operator|.
name|listFiles
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// skip directories which are actual data store files
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete root record {} "
argument_list|,
operator|new
name|Object
index|[]
block|{
name|file
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DataRecord
argument_list|>
name|getAllRecords
parameter_list|()
block|{
specifier|final
name|AbstractSharedBackend
name|backend
init|=
name|this
decl_stmt|;
return|return
name|Files
operator|.
name|fileTreeTraverser
argument_list|()
operator|.
name|postOrderTraversal
argument_list|(
name|fsPathDir
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|Predicate
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|File
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|isFile
argument_list|()
operator|&&
operator|!
name|normalizeNoEndSeparator
argument_list|(
name|input
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|fsPath
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|transform
argument_list|(
operator|new
name|Function
argument_list|<
name|File
argument_list|,
name|DataRecord
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DataRecord
name|apply
parameter_list|(
name|File
name|input
parameter_list|)
block|{
return|return
operator|new
name|FSBackendDataRecord
argument_list|(
name|backend
argument_list|,
operator|new
name|DataIdentifier
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DataStoreException
block|{     }
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getOrCreateReferenceKey
parameter_list|()
throws|throws
name|DataStoreException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fsPathDir
argument_list|,
literal|"reference.key"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|FileUtils
operator|.
name|readFileToByteArray
argument_list|(
name|file
argument_list|)
return|;
block|}
else|else
block|{
name|byte
index|[]
name|key
init|=
name|super
operator|.
name|getOrCreateReferenceKey
argument_list|()
decl_stmt|;
name|FileUtils
operator|.
name|writeByteArrayToFile
argument_list|(
name|file
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Unable to access reference key file "
operator|+
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/*----------------------------------- Helper Methods-- -------------------------------------**/
comment|/**      * Returns the identified file. This method implements the pattern used to      * avoid problems with too many files in a single directory.      *<p>      * No sanity checks are performed on the given identifier.      *      * @param identifier data identifier      * @return identified file      */
specifier|private
specifier|static
name|File
name|getFile
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|,
name|File
name|root
parameter_list|)
block|{
name|String
name|string
init|=
name|identifier
operator|.
name|toString
argument_list|()
decl_stmt|;
name|File
name|file
init|=
name|root
decl_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|string
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|string
operator|.
name|substring
argument_list|(
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|string
argument_list|)
return|;
block|}
comment|/**      * Get the last modified date of a file.      *      * @param file the file      * @return the last modified date      * @throws DataStoreException if reading fails      */
specifier|private
specifier|static
name|long
name|getLastModified
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|long
name|lastModified
init|=
name|file
operator|.
name|lastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastModified
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"Failed to read record modified date: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|lastModified
return|;
block|}
comment|/**      * Set the last modified date of a file, if the file is writable.      *      * @param file the file      * @param time the new last modified date      * @throws DataStoreException if the file is writable but modifying the date      *                            fails      */
specifier|private
specifier|static
name|void
name|setLastModified
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|time
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|setLastModified
argument_list|(
name|time
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|canWrite
argument_list|()
condition|)
block|{
comment|// if we can't write to the file, so garbage collection will
comment|// also not delete it
comment|// (read only files or file systems)
return|return;
block|}
try|try
block|{
comment|// workaround for Windows: if the file is already open for
comment|// reading
comment|// (in this or another process), then setting the last modified
comment|// date
comment|// doesn't work - see also JCR-2872
name|RandomAccessFile
name|r
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|r
operator|.
name|setLength
argument_list|(
name|r
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataStoreException
argument_list|(
literal|"An IO Exception occurred while trying to set the last modified date: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|deleteEmptyParentDirs
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|File
name|parent
init|=
name|file
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Only iterate& delete if parent directory of the blob file is
comment|// child
comment|// of the base directory and if it is empty
while|while
condition|(
name|FileUtils
operator|.
name|directoryContains
argument_list|(
name|fsPathDir
argument_list|,
name|parent
argument_list|)
condition|)
block|{
name|String
index|[]
name|entries
init|=
name|parent
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to list directory {}"
argument_list|,
name|parent
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|entries
operator|.
name|length
operator|>
literal|0
condition|)
block|{
break|break;
block|}
name|boolean
name|deleted
init|=
name|parent
operator|.
name|delete
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleted parent [{}] of file [{}]: {}"
argument_list|,
name|parent
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error in parents deletion for "
operator|+
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*--------------------------------- Gettters& Setters -------------------------------------**/
comment|/**      * Properties used to configure the backend. These are mandatorily to be provided explicitly      * before calling {{@link #init()} is invoked.      *      * @param properties to configure Backend      */
specifier|public
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
comment|/*-------------------------------- Inner classes -------------------------------------------**/
comment|/**      * FSBackendDataRecord which lazily retrieves the input stream of the record.      */
class|class
name|FSBackendDataRecord
extends|extends
name|AbstractDataRecord
block|{
specifier|private
name|long
name|length
decl_stmt|;
specifier|private
name|long
name|lastModified
decl_stmt|;
specifier|private
name|File
name|file
decl_stmt|;
specifier|public
name|FSBackendDataRecord
parameter_list|(
name|AbstractSharedBackend
name|backend
parameter_list|,
annotation|@
name|Nonnull
name|DataIdentifier
name|identifier
parameter_list|,
annotation|@
name|Nonnull
name|File
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|backend
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|file
operator|.
name|lastModified
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|DataStoreException
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|DataStoreException
block|{
try|try
block|{
return|return
operator|new
name|LazyFileInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in returning stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"S3DataRecord{"
operator|+
literal|"identifier="
operator|+
name|getIdentifier
argument_list|()
operator|+
literal|", length="
operator|+
name|length
operator|+
literal|", lastModified="
operator|+
name|lastModified
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit
