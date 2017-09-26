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
name|Bytes
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DuplicateKeyException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
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
name|WriteResult
import|;
end_import

begin_comment
comment|/**  * Implementation of blob store for the MongoDB extending from  * {@link org.apache.jackrabbit.oak.spi.blob.AbstractBlobStore}. It saves blobs into a separate collection in  * MongoDB (not using GridFS) and it supports basic garbage collection.  *  * FIXME: -Do we need to create commands for retry etc.? -Not sure if this is  * going to work for multiple MKs talking to same MongoDB?  */
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
specifier|final
name|DB
name|db
decl_stmt|;
specifier|private
name|long
name|minLastModified
decl_stmt|;
comment|/**      * Constructs a new {@code MongoBlobStore}      *      * @param db The DB.      */
specifier|public
name|MongoBlobStore
parameter_list|(
name|DB
name|db
parameter_list|)
block|{
name|this
argument_list|(
name|db
argument_list|,
name|DEFAULT_CACHE_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MongoBlobStore
parameter_list|(
name|DB
name|db
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|super
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
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
name|initBlobCollection
argument_list|()
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
comment|// Check if it already exists?
name|MongoBlob
name|mongoBlob
init|=
operator|new
name|MongoBlob
argument_list|()
decl_stmt|;
name|mongoBlob
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|mongoBlob
operator|.
name|setData
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|mongoBlob
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|mongoBlob
operator|.
name|setLastMod
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO check the return value
comment|// TODO verify insert is fast if the entry already exists
try|try
block|{
name|getBlobCollection
argument_list|()
operator|.
name|insert
argument_list|(
name|mongoBlob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DuplicateKeyException
name|e
parameter_list|)
block|{
comment|// the same block was already stored before: ignore
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
name|DBObject
name|query
init|=
name|getBlobQuery
argument_list|(
name|id
argument_list|,
name|minLastModified
argument_list|)
decl_stmt|;
name|DBObject
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
name|update
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
name|DBObject
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
name|countBefore
init|=
name|getBlobCollection
argument_list|()
operator|.
name|count
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|getBlobCollection
argument_list|()
operator|.
name|remove
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|long
name|countAfter
init|=
name|getBlobCollection
argument_list|()
operator|.
name|count
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|minLastModified
operator|=
literal|0
expr_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|countBefore
operator|-
name|countAfter
argument_list|)
return|;
block|}
specifier|private
name|DBCollection
name|getBlobCollection
parameter_list|()
block|{
name|DBCollection
name|collection
init|=
name|db
operator|.
name|getCollection
argument_list|(
name|COLLECTION_BLOBS
argument_list|)
decl_stmt|;
name|collection
operator|.
name|setObjectClass
argument_list|(
name|MongoBlob
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
specifier|private
name|void
name|initBlobCollection
parameter_list|()
block|{
if|if
condition|(
operator|!
name|db
operator|.
name|collectionExists
argument_list|(
name|COLLECTION_BLOBS
argument_list|)
condition|)
block|{
name|db
operator|.
name|createCollection
argument_list|(
name|COLLECTION_BLOBS
argument_list|,
operator|new
name|BasicDBObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|DBObject
name|query
init|=
name|getBlobQuery
argument_list|(
name|id
argument_list|,
name|lastMod
argument_list|)
decl_stmt|;
comment|// try the secondary first
comment|// TODO add a configuration option for whether to try reading from secondary
name|ReadPreference
name|pref
init|=
name|ReadPreference
operator|.
name|secondaryPreferred
argument_list|()
decl_stmt|;
name|DBObject
name|fields
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|MongoBlob
operator|.
name|KEY_DATA
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MongoBlob
name|blob
init|=
operator|(
name|MongoBlob
operator|)
name|getBlobCollection
argument_list|()
operator|.
name|findOne
argument_list|(
name|query
argument_list|,
name|fields
argument_list|,
name|pref
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob
operator|==
literal|null
condition|)
block|{
comment|// not found in the secondary: try the primary
name|pref
operator|=
name|ReadPreference
operator|.
name|primary
argument_list|()
expr_stmt|;
name|blob
operator|=
operator|(
name|MongoBlob
operator|)
name|getBlobCollection
argument_list|()
operator|.
name|findOne
argument_list|(
name|query
argument_list|,
name|fields
argument_list|,
name|pref
argument_list|)
expr_stmt|;
block|}
return|return
name|blob
return|;
block|}
specifier|private
specifier|static
name|DBObject
name|getBlobQuery
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|lastMod
parameter_list|)
block|{
name|QueryBuilder
name|queryBuilder
init|=
operator|new
name|QueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|)
operator|.
name|is
argument_list|(
name|id
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
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|)
operator|.
name|lessThan
argument_list|(
name|lastMod
argument_list|)
expr_stmt|;
block|}
return|return
name|queryBuilder
operator|.
name|get
argument_list|()
return|;
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
name|DBCollection
name|collection
init|=
name|getBlobCollection
argument_list|()
decl_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
operator|new
name|QueryBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|chunkIds
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|)
operator|.
name|in
argument_list|(
name|chunkIds
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|queryBuilder
operator|=
name|queryBuilder
operator|.
name|and
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|)
operator|.
name|lessThan
argument_list|(
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
block|}
name|WriteResult
name|result
init|=
name|collection
operator|.
name|remove
argument_list|(
name|queryBuilder
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|getN
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
name|DBCollection
name|collection
init|=
name|getBlobCollection
argument_list|()
decl_stmt|;
name|DBObject
name|fields
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|MongoBlob
operator|.
name|KEY_ID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|QueryBuilder
name|builder
init|=
operator|new
name|QueryBuilder
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
name|builder
operator|.
name|and
argument_list|(
name|MongoBlob
operator|.
name|KEY_LAST_MOD
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DBCursor
name|cur
init|=
name|collection
operator|.
name|find
argument_list|(
name|builder
operator|.
name|get
argument_list|()
argument_list|,
name|fields
argument_list|)
operator|.
name|hint
argument_list|(
name|fields
argument_list|)
operator|.
name|addOption
argument_list|(
name|Bytes
operator|.
name|QUERYOPTION_SLAVEOK
argument_list|)
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
operator|(
name|MongoBlob
operator|)
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
