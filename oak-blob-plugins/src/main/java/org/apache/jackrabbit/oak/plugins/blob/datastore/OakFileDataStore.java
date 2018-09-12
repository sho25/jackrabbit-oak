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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
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
name|Collections
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
name|Set
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
name|Charsets
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
name|io
operator|.
name|BaseEncoding
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
name|FileDataRecord
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
name|plugins
operator|.
name|blob
operator|.
name|SharedDataStore
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
comment|/**  *  Oak specific extension of JR2 FileDataStore which enables  *  provisioning the signing key via OSGi config  */
end_comment

begin_class
specifier|public
class|class
name|OakFileDataStore
extends|extends
name|FileDataStore
implements|implements
name|SharedDataStore
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OakFileDataStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_RECORD_LENGTH
init|=
literal|4096
decl_stmt|;
specifier|private
name|byte
index|[]
name|referenceKey
decl_stmt|;
specifier|public
name|OakFileDataStore
parameter_list|()
block|{
comment|//TODO FIXME Temporary workaround for OAK-1666. Override the default
comment|//synchronized map with a Noop. This should be removed when fix
comment|//for JCR-3764 is part of release.
name|inUse
operator|=
operator|new
name|NoOpMap
argument_list|<
name|DataIdentifier
argument_list|,
name|WeakReference
argument_list|<
name|DataIdentifier
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
comment|// Set default min record length overiding the 100 set for FileDataStore
name|setMinRecordLength
argument_list|(
name|DEFAULT_MIN_RECORD_LENGTH
argument_list|)
expr_stmt|;
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
block|{
specifier|final
name|String
name|path
init|=
name|normalizeNoEndSeparator
argument_list|(
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Files
operator|.
name|fileTreeTraverser
argument_list|()
operator|.
name|postOrderTraversal
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
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
name|input
operator|.
name|getParent
argument_list|()
operator|.
name|equals
argument_list|(
name|path
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
specifier|protected
name|byte
index|[]
name|getOrCreateReferenceKey
parameter_list|()
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|referenceKey
operator|!=
literal|null
condition|)
block|{
return|return
name|referenceKey
return|;
block|}
return|return
name|super
operator|.
name|getOrCreateReferenceKey
argument_list|()
return|;
block|}
comment|/**      * Set Base64 encoded signing key      */
specifier|public
name|void
name|setReferenceKeyEncoded
parameter_list|(
name|String
name|encodedKey
parameter_list|)
block|{
name|this
operator|.
name|referenceKey
operator|=
name|BaseEncoding
operator|.
name|base64
argument_list|()
operator|.
name|decode
argument_list|(
name|encodedKey
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the referenceKey from plain text. Key content would be      * UTF-8 encoding of the string.      *      *<p>This is useful when setting key via generic      *  bean property manipulation from string properties. User can specify the      *  key in plain text and that would be passed on this object via      *  {@link org.apache.jackrabbit.oak.commons.PropertiesUtil#populate(Object, java.util.Map, boolean)}      *      * @param textKey base64 encoded key      * @see org.apache.jackrabbit.oak.commons.PropertiesUtil#populate(Object, java.util.Map, boolean)      */
specifier|public
name|void
name|setReferenceKeyPlainText
parameter_list|(
name|String
name|textKey
parameter_list|)
block|{
name|this
operator|.
name|referenceKey
operator|=
name|textKey
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReferenceKey
parameter_list|(
name|byte
index|[]
name|referenceKey
parameter_list|)
block|{
name|this
operator|.
name|referenceKey
operator|=
name|referenceKey
expr_stmt|;
block|}
comment|/**      * Noop map which eats up all the put call      */
specifier|static
class|class
name|NoOpMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
comment|//Eat the put call
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
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
name|checkArgument
argument_list|(
name|input
operator|!=
literal|null
argument_list|,
literal|"input should not be null"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"name should not be empty"
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
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
name|checkArgument
argument_list|(
name|input
operator|!=
literal|null
argument_list|,
literal|"input should not be null"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"name should not be empty"
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
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
operator|new
name|Object
index|[]
block|{
name|input
block|,
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
name|DataRecord
name|getMetadataRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"name should not be empty"
argument_list|)
expr_stmt|;
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
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
name|root
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
name|FileDataRecord
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
name|boolean
name|metadataRecordExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"name should not be empty"
argument_list|)
expr_stmt|;
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
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
name|nameFileFilter
argument_list|(
name|name
argument_list|)
argument_list|,
name|root
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
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"File does not exist {} "
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
name|checkArgument
argument_list|(
literal|null
operator|!=
name|prefix
argument_list|,
literal|"prefix should not be null"
argument_list|)
expr_stmt|;
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
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
name|root
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
name|FileDataRecord
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
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"name should not be empty"
argument_list|)
expr_stmt|;
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
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
name|nameFileFilter
argument_list|(
name|name
argument_list|)
argument_list|,
name|root
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
name|checkArgument
argument_list|(
literal|null
operator|!=
name|prefix
argument_list|,
literal|"prefix should not be empty"
argument_list|)
expr_stmt|;
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
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
name|root
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
name|String
name|path
init|=
name|normalizeNoEndSeparator
argument_list|(
operator|new
name|File
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|OakFileDataStore
name|store
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
operator|new
name|File
argument_list|(
name|path
argument_list|)
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
name|input
operator|.
name|getParent
argument_list|()
operator|.
name|equals
argument_list|(
name|path
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
name|FileDataRecord
argument_list|(
name|store
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
name|DataRecord
name|getRecordForId
parameter_list|(
name|DataIdentifier
name|id
parameter_list|)
throws|throws
name|DataStoreException
block|{
return|return
name|getRecord
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|SHARED
return|;
block|}
block|}
end_class

end_unit

