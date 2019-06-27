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
name|document
operator|.
name|mongo
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|model
operator|.
name|UpdateOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|result
operator|.
name|UpdateResult
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
name|StringUtils
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
name|CachingBlobStore
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
name|document
operator|.
name|DocumentNodeStoreBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|configuration
operator|.
name|CodecRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|conversions
operator|.
name|Bson
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
name|AbstractIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|model
operator|.
name|Filters
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
operator|.
name|primary
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
operator|.
name|stream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|configuration
operator|.
name|CodecRegistries
operator|.
name|fromCodecs
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bson
operator|.
name|codecs
operator|.
name|configuration
operator|.
name|CodecRegistries
operator|.
name|fromRegistries
import|;
end_import

begin_comment
comment|/**  * Implementation of blob store for the MongoDB extending from  * {@link CachingBlobStore}. It saves blobs into a separate collection in  * MongoDB (not using GridFS) and it supports basic garbage collection.  *  * FIXME: -Do we need to create commands for retry etc.? -Not sure if this is  * going to work for multiple MKs talking to same MongoDB?  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlobStore
extends|extends
name|CachingBlobStore
block|{
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_BLOBS
init|=
literal|"blobs"
decl_stmt|;
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
name|MongoBlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DUPLICATE_KEY_ERROR_CODE
init|=
literal|11000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CodecRegistry
name|CODEC_REGISTRY
init|=
name|fromRegistries
argument_list|(
name|MongoClient
operator|.
name|getDefaultCodecRegistry
argument_list|()
argument_list|,
name|fromCodecs
argument_list|(
operator|new
name|MongoBlobCodec
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReadPreference
name|defaultReadPreference
decl_stmt|;
specifier|private
specifier|final
name|MongoCollection
argument_list|<
name|MongoBlob
argument_list|>
name|blobCollection
decl_stmt|;
specifier|private
name|long
name|minLastModified
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
comment|/**      * Constructs a new {@code MongoBlobStore}      *      * @param db the database      */
specifier|public
name|MongoBlobStore
parameter_list|(
name|MongoDatabase
name|db
parameter_list|)
block|{
name|this
argument_list|(
name|db
argument_list|,
name|DEFAULT_CACHE_SIZE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new {@code MongoBlobStore}      *      * @param db the database      * @param cacheSize the cache size      */
specifier|public
name|MongoBlobStore
parameter_list|(
name|MongoDatabase
name|db
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|this
argument_list|(
name|db
argument_list|,
name|cacheSize
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new {@code MongoBlobStore}      *      * @param db the database      * @param cacheSize the cache size      * @param builder {@link DocumentNodeStoreBuilder}, supplying further options      */
specifier|public
name|MongoBlobStore
parameter_list|(
annotation|@
name|NotNull
name|MongoDatabase
name|db
parameter_list|,
name|long
name|cacheSize
parameter_list|,
annotation|@
name|Nullable
name|DocumentNodeStoreBuilder
argument_list|<
name|?
argument_list|>
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
name|readOnly
operator|=
name|builder
operator|==
literal|null
condition|?
literal|false
else|:
name|builder
operator|.
name|getReadOnlyMode
argument_list|()
expr_stmt|;
comment|// use a block size of 2 MB - 1 KB, because MongoDB rounds up the
comment|// space allocated for a record to the next power of two
comment|// (there is an overhead per record, let's assume it is 1 KB at most)
name|setBlockSize
argument_list|(
literal|2
operator|*
literal|1024
operator|*
literal|1024
operator|-
literal|1024
argument_list|)
expr_stmt|;
name|defaultReadPreference
operator|=
name|db
operator|.
name|getReadPreference
argument_list|()
expr_stmt|;
name|blobCollection
operator|=
name|initBlobCollection
argument_list|(
name|db
argument_list|,
name|readOnly
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// Create the mongo blob object
name|BasicDBObject
name|mongoBlob
init|=
operator|new
name|BasicDBObject
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|mongoBlob
operator|.
name|append
argument_list|(
name|MongoBlob
operator|.
name|KEY_DATA
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|mongoBlob
operator|.
name|append
argument_list|(
name|MongoBlob
operator|.
name|KEY_LEVEL
argument_list|,
name|level
argument_list|)
expr_stmt|;
comment|// If update only the lastMod needs to be modified
name|BasicDBObject
name|updateBlob
init|=
operator|new
name|BasicDBObject
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|BasicDBObject
name|upsert
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|upsert
operator|.
name|append
argument_list|(
literal|"$setOnInsert"
argument_list|,
name|mongoBlob
argument_list|)
operator|.
name|append
argument_list|(
literal|"$set"
argument_list|,
name|updateBlob
argument_list|)
expr_stmt|;
try|try
block|{
name|Bson
name|query
init|=
name|getBlobQuery
argument_list|(
name|id
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|UpdateOptions
name|options
init|=
operator|new
name|UpdateOptions
argument_list|()
operator|.
name|upsert
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|UpdateResult
name|result
init|=
name|getBlobCollection
argument_list|()
operator|.
name|updateOne
argument_list|(
name|query
argument_list|,
name|upsert
argument_list|,
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getUpsertedId
argument_list|()
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Block with id [{}] updated"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Block with id [{}] created"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MongoException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|blockId
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|MongoBlob
name|blobMongo
init|=
name|getBlob
argument_list|(
name|id
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobMongo
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Did not find block "
operator|+
name|id
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|data
operator|=
name|blobMongo
operator|.
name|getData
argument_list|()
expr_stmt|;
name|getStatsCollector
argument_list|()
operator|.
name|downloaded
argument_list|(
name|id
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|blockId
operator|.
name|getPos
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|data
return|;
block|}
name|int
name|len
init|=
call|(
name|int
call|)
argument_list|(
name|data
operator|.
name|length
operator|-
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|byte
index|[]
name|d2
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|,
name|d2
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|d2
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
block|{
name|minLastModified
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|markInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isMarkEnabled
parameter_list|()
block|{
return|return
name|minLastModified
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|mark
parameter_list|(
name|BlockId
name|blockId
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|minLastModified
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|Bson
name|query
init|=
name|getBlobQuery
argument_list|(
name|id
argument_list|,
name|minLastModified
argument_list|)
decl_stmt|;
name|Bson
name|update
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"$set"
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|getBlobCollection
argument_list|()
operator|.
name|updateOne
argument_list|(
name|query
argument_list|,
name|update
argument_list|)
expr_stmt|;
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
name|Bson
name|query
init|=
name|getBlobQuery
argument_list|(
literal|null
argument_list|,
name|minLastModified
argument_list|)
decl_stmt|;
name|long
name|num
init|=
name|getBlobCollection
argument_list|()
operator|.
name|deleteMany
argument_list|(
name|query
argument_list|)
operator|.
name|getDeletedCount
argument_list|()
decl_stmt|;
name|minLastModified
operator|=
literal|0
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|num
return|;
block|}
specifier|private
name|MongoCollection
argument_list|<
name|MongoBlob
argument_list|>
name|initBlobCollection
parameter_list|(
name|MongoDatabase
name|db
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
if|if
condition|(
name|stream
argument_list|(
name|db
operator|.
name|listCollectionNames
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|noneMatch
argument_list|(
name|COLLECTION_BLOBS
operator|::
name|equals
argument_list|)
condition|)
block|{
if|if
condition|(
name|readOnly
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MongoBlobStore instantiated read-only, but collection "
operator|+
name|COLLECTION_BLOBS
operator|+
literal|" not present"
argument_list|)
throw|;
block|}
name|db
operator|.
name|createCollection
argument_list|(
name|COLLECTION_BLOBS
argument_list|)
expr_stmt|;
block|}
comment|// override the read preference configured with the MongoDB URI
comment|// and use the primary as default. Reading a blob will still
comment|// try a secondary first and then fallback to the primary.
return|return
name|db
operator|.
name|getCollection
argument_list|(
name|COLLECTION_BLOBS
argument_list|,
name|MongoBlob
operator|.
name|class
argument_list|)
operator|.
name|withCodecRegistry
argument_list|(
name|CODEC_REGISTRY
argument_list|)
operator|.
name|withReadPreference
argument_list|(
name|primary
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|MongoCollection
argument_list|<
name|MongoBlob
argument_list|>
name|getBlobCollection
parameter_list|()
block|{
return|return
name|this
operator|.
name|blobCollection
return|;
block|}
specifier|private
name|MongoBlob
name|getBlob
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|lastMod
parameter_list|)
block|{
name|Bson
name|query
init|=
name|getBlobQuery
argument_list|(
name|id
argument_list|,
name|lastMod
argument_list|)
decl_stmt|;
name|Bson
name|fields
init|=
operator|new
name|BasicDBObject
argument_list|(
name|MongoBlob
operator|.
name|KEY_DATA
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// try with default read preference first, may be from secondary
name|List
argument_list|<
name|MongoBlob
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|getBlobCollection
argument_list|()
operator|.
name|withReadPreference
argument_list|(
name|defaultReadPreference
argument_list|)
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|projection
argument_list|(
name|fields
argument_list|)
operator|.
name|into
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// not found in the secondary: try the primary
name|getBlobCollection
argument_list|()
operator|.
name|withReadPreference
argument_list|(
name|primary
argument_list|()
argument_list|)
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|projection
argument_list|(
name|fields
argument_list|)
operator|.
name|into
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Bson
name|getBlobQuery
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|lastMod
parameter_list|)
block|{
name|List
argument_list|<
name|Bson
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
name|Filters
operator|.
name|eq
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastMod
operator|>
literal|0
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
name|Filters
operator|.
name|lt
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|,
name|lastMod
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Filters
operator|.
name|and
argument_list|(
name|clauses
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|countDeleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
name|Bson
name|query
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|chunkIds
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|Filters
operator|.
name|in
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|,
name|chunkIds
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|query
operator|=
name|Filters
operator|.
name|and
argument_list|(
name|query
argument_list|,
name|Filters
operator|.
name|lt
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|,
name|maxLastModifiedTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|getBlobCollection
argument_list|()
operator|.
name|deleteMany
argument_list|(
name|query
argument_list|)
operator|.
name|getDeletedCount
argument_list|()
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
name|Bson
name|fields
init|=
operator|new
name|BasicDBObject
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Bson
name|query
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|!=
literal|0
operator|&&
name|maxLastModifiedTime
operator|!=
operator|-
literal|1
condition|)
block|{
name|query
operator|=
name|Filters
operator|.
name|lte
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
specifier|final
name|MongoCursor
argument_list|<
name|MongoBlob
argument_list|>
name|cur
init|=
name|getBlobCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|projection
argument_list|(
name|fields
argument_list|)
operator|.
name|hint
argument_list|(
name|fields
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//TODO The cursor needs to be closed
return|return
operator|new
name|AbstractIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|cur
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MongoBlob
name|blob
init|=
name|cur
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
return|return
name|blob
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

