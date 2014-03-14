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
name|ByteArrayInputStream
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
name|io
operator|.
name|SequenceInputStream
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Preconditions
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
name|Iterators
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
name|ByteStreams
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
name|core
operator|.
name|data
operator|.
name|MultiDataStoreAware
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
name|blob
operator|.
name|GarbageCollectableBlobStore
import|;
end_import

begin_comment
comment|/**  * BlobStore wrapper for DataStore. Wraps Jackrabbit 2 DataStore and expose them as BlobStores  * It also handles inlining binaries if there size is smaller than  * {@link org.apache.jackrabbit.core.data.DataStore#getMinRecordLength()}  */
end_comment

begin_class
specifier|public
class|class
name|DataStoreBlobStore
implements|implements
name|DataStore
implements|,
name|BlobStore
implements|,
name|GarbageCollectableBlobStore
block|{
specifier|private
specifier|final
name|DataStore
name|delegate
decl_stmt|;
specifier|public
name|DataStoreBlobStore
parameter_list|(
name|DataStore
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
comment|//~----------------------------------< DataStore>
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecordIfStored
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
throws|throws
name|DataStoreException
block|{
if|if
condition|(
name|isInMemoryRecord
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return
name|getDataRecord
argument_list|(
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|delegate
operator|.
name|getRecordIfStored
argument_list|(
name|identifier
argument_list|)
return|;
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
if|if
condition|(
name|isInMemoryRecord
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return
name|getDataRecord
argument_list|(
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|delegate
operator|.
name|getRecord
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|getRecordFromReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|DataStoreException
block|{
return|return
name|delegate
operator|.
name|getRecordFromReference
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DataRecord
name|addRecord
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|DataStoreException
block|{
try|try
block|{
return|return
name|writeStream
argument_list|(
name|stream
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
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateModifiedDateOnAccess
parameter_list|(
name|long
name|before
parameter_list|)
block|{
name|delegate
operator|.
name|updateModifiedDateOnAccess
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|deleteAllOlderThan
parameter_list|(
name|long
name|min
parameter_list|)
throws|throws
name|DataStoreException
block|{
return|return
name|delegate
operator|.
name|deleteAllOlderThan
argument_list|(
name|min
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
name|delegate
operator|.
name|getAllIdentifiers
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|String
name|homeDir
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"DataStore cannot be initialized again"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMinRecordLength
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getMinRecordLength
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
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//~-------------------------------------------< BlobStore>
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
try|try
block|{
name|String
name|id
init|=
name|writeStream
argument_list|(
name|stream
argument_list|)
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|threw
operator|=
literal|false
expr_stmt|;
return|return
name|id
return|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
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
comment|//DataStore does not closes the stream internally
comment|//So close the stream explicitly
name|Closeables
operator|.
name|close
argument_list|(
name|stream
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
comment|//This is inefficient as repeated calls for same blobId would involve opening new Stream
comment|//instead clients should directly access the stream from DataRecord by special casing for
comment|//BlobStore which implements DataStore
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
try|try
block|{
name|ByteStreams
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|int
name|readCount
init|=
name|stream
operator|.
name|read
argument_list|(
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|threw
operator|=
literal|false
expr_stmt|;
return|return
name|readCount
return|;
block|}
finally|finally
block|{
name|Closeables
operator|.
name|close
argument_list|(
name|stream
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|getDataRecord
argument_list|(
name|blobId
argument_list|)
operator|.
name|getLength
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
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
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getStream
argument_list|(
name|blobId
argument_list|)
return|;
block|}
comment|//~-------------------------------------------< GarbageCollectableBlobStore>
annotation|@
name|Override
specifier|public
name|void
name|setBlockSize
parameter_list|(
name|int
name|x
parameter_list|)
block|{      }
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|String
name|tempFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tempFileName
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startMark
parameter_list|()
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|clearInUse
parameter_list|()
block|{
name|delegate
operator|.
name|clearInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearCache
parameter_list|()
block|{      }
annotation|@
name|Override
specifier|public
name|long
name|getBlockSizeMin
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllChunkIds
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
comment|//TODO Ignores the maxLastModifiedTime currently.
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getAllIdentifiers
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|DataIdentifier
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|DataIdentifier
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteChunk
parameter_list|(
name|String
name|chunkId
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|delegate
operator|instanceof
name|MultiDataStoreAware
condition|)
block|{
name|DataIdentifier
name|identifier
init|=
operator|new
name|DataIdentifier
argument_list|(
name|chunkId
argument_list|)
decl_stmt|;
name|DataRecord
name|dataRecord
init|=
name|delegate
operator|.
name|getRecord
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|maxLastModifiedTime
operator|<=
literal|0
operator|)
operator|||
name|dataRecord
operator|.
name|getLastModified
argument_list|()
operator|<=
name|maxLastModifiedTime
condition|)
block|{
operator|(
operator|(
name|MultiDataStoreAware
operator|)
name|delegate
operator|)
operator|.
name|deleteRecord
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|resolveChunks
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|blobId
argument_list|)
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
name|String
operator|.
name|format
argument_list|(
literal|"DataStore backed BlobStore [%s]"
argument_list|,
name|delegate
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|DataStore
name|getDataStore
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
specifier|private
name|InputStream
name|getStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|getDataRecord
argument_list|(
name|blobId
argument_list|)
operator|.
name|getStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
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
block|}
specifier|private
name|DataRecord
name|getDataRecord
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|DataRecord
name|id
decl_stmt|;
if|if
condition|(
name|InMemoryDataRecord
operator|.
name|isInstance
argument_list|(
name|blobId
argument_list|)
condition|)
block|{
name|id
operator|=
name|InMemoryDataRecord
operator|.
name|getInstance
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|id
operator|=
name|delegate
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|id
argument_list|,
literal|"No DataRecord found for blodId [%s]"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
specifier|private
name|boolean
name|isInMemoryRecord
parameter_list|(
name|DataIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|InMemoryDataRecord
operator|.
name|isInstance
argument_list|(
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Create a BLOB value from in input stream. Small objects will create an in-memory object,      * while large objects are stored in the data store      *      * @param in the input stream      * @return the value      */
specifier|private
name|DataRecord
name|writeStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|DataStoreException
block|{
name|int
name|maxMemorySize
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|delegate
operator|.
name|getMinRecordLength
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|maxMemorySize
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|len
init|=
name|maxMemorySize
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|maxMemorySize
condition|)
block|{
name|int
name|l
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|pos
operator|+=
name|l
expr_stmt|;
name|len
operator|-=
name|l
expr_stmt|;
block|}
name|DataRecord
name|record
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|maxMemorySize
condition|)
block|{
comment|// shrink the buffer
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|pos
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|record
operator|=
name|InMemoryDataRecord
operator|.
name|getInstance
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// a few bytes are already read, need to re-build the input stream
name|in
operator|=
operator|new
name|SequenceInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|record
operator|=
name|delegate
operator|.
name|addRecord
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|record
return|;
block|}
block|}
end_class

end_unit

