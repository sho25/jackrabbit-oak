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
name|net
operator|.
name|UnknownHostException
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
name|MongoClientOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadConcernLevel
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
name|ReferencedBlob
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
name|DocumentNodeStore
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
name|DocumentStore
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
name|MissingLastRevSeeker
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
name|VersionGCSupport
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
name|util
operator|.
name|MongoConnection
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
name|Suppliers
operator|.
name|memoize
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|MongoConnection
operator|.
name|readConcernLevel
import|;
end_import

begin_comment
comment|/**  * A base builder implementation for a {@link DocumentNodeStore} backed by  * MongoDB.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MongoDocumentNodeStoreBuilderBase
parameter_list|<
name|T
extends|extends
name|MongoDocumentNodeStoreBuilderBase
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|DocumentNodeStoreBuilder
argument_list|<
name|T
argument_list|>
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
name|MongoDocumentNodeStoreBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|mongoUri
decl_stmt|;
specifier|private
name|boolean
name|socketKeepAlive
decl_stmt|;
specifier|private
name|MongoStatus
name|mongoStatus
decl_stmt|;
specifier|private
name|long
name|maxReplicationLagMillis
init|=
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|6
argument_list|)
decl_stmt|;
comment|/**      * Uses the given information to connect to to MongoDB as backend      * storage for the DocumentNodeStore. The write concern is either      * taken from the URI or determined automatically based on the MongoDB      * setup. When running on a replica set without explicit write concern      * in the URI, the write concern will be {@code MAJORITY}, otherwise      * {@code ACKNOWLEDGED}.      *      * @param uri a MongoDB URI.      * @param name the name of the database to connect to. This overrides      *             any database name given in the {@code uri}.      * @param blobCacheSizeMB the blob cache size in MB.      * @return this      * @throws UnknownHostException if one of the hosts given in the URI      *          is unknown.      */
specifier|public
name|T
name|setMongoDB
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uri
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
name|int
name|blobCacheSizeMB
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|this
operator|.
name|mongoUri
operator|=
name|uri
expr_stmt|;
name|MongoClientOptions
operator|.
name|Builder
name|options
init|=
name|MongoConnection
operator|.
name|getDefaultBuilder
argument_list|()
decl_stmt|;
name|options
operator|.
name|socketKeepAlive
argument_list|(
name|socketKeepAlive
argument_list|)
expr_stmt|;
name|DB
name|db
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|,
name|options
argument_list|)
operator|.
name|getDB
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|MongoStatus
name|status
init|=
operator|new
name|MongoStatus
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|MongoConnection
operator|.
name|hasWriteConcern
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|db
operator|.
name|setWriteConcern
argument_list|(
name|MongoConnection
operator|.
name|getDefaultWriteConcern
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|isMajorityReadConcernSupported
argument_list|()
operator|&&
name|status
operator|.
name|isMajorityReadConcernEnabled
argument_list|()
operator|&&
operator|!
name|MongoConnection
operator|.
name|hasReadConcern
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|db
operator|.
name|setReadConcern
argument_list|(
name|MongoConnection
operator|.
name|getDefaultReadConcern
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setMongoDB
argument_list|(
name|db
argument_list|,
name|status
argument_list|,
name|blobCacheSizeMB
argument_list|)
expr_stmt|;
return|return
name|thisBuilder
argument_list|()
return|;
block|}
comment|/**      * Use the given MongoDB as backend storage for the DocumentNodeStore.      *      * @param db the MongoDB connection      * @return this      */
specifier|public
name|T
name|setMongoDB
parameter_list|(
annotation|@
name|Nonnull
name|DB
name|db
parameter_list|,
name|int
name|blobCacheSizeMB
parameter_list|)
block|{
return|return
name|setMongoDB
argument_list|(
name|db
argument_list|,
operator|new
name|MongoStatus
argument_list|(
name|db
argument_list|)
argument_list|,
name|blobCacheSizeMB
argument_list|)
return|;
block|}
comment|/**      * Use the given MongoDB as backend storage for the DocumentNodeStore.      *      * @param db the MongoDB connection      * @return this      */
specifier|public
name|T
name|setMongoDB
parameter_list|(
annotation|@
name|Nonnull
name|DB
name|db
parameter_list|)
block|{
return|return
name|setMongoDB
argument_list|(
name|db
argument_list|,
literal|16
argument_list|)
return|;
block|}
comment|/**      * Enables the socket keep-alive option for MongoDB. The default is      * disabled.      *      * @param enable whether to enable it.      * @return this      */
specifier|public
name|T
name|setSocketKeepAlive
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|socketKeepAlive
operator|=
name|enable
expr_stmt|;
return|return
name|thisBuilder
argument_list|()
return|;
block|}
specifier|public
name|T
name|setMaxReplicationLag
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|maxReplicationLagMillis
operator|=
name|unit
operator|.
name|toMillis
argument_list|(
name|duration
argument_list|)
expr_stmt|;
return|return
name|thisBuilder
argument_list|()
return|;
block|}
specifier|public
name|VersionGCSupport
name|createVersionGCSupport
parameter_list|()
block|{
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
return|return
operator|new
name|MongoVersionGCSupport
argument_list|(
operator|(
name|MongoDocumentStore
operator|)
name|store
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createVersionGCSupport
argument_list|()
return|;
block|}
block|}
specifier|public
name|Iterable
argument_list|<
name|ReferencedBlob
argument_list|>
name|createReferencedBlobs
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
specifier|final
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
return|return
parameter_list|()
lambda|->
operator|new
name|MongoBlobReferenceIterator
argument_list|(
name|ns
argument_list|,
operator|(
name|MongoDocumentStore
operator|)
name|store
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createReferencedBlobs
argument_list|(
name|ns
argument_list|)
return|;
block|}
block|}
specifier|public
name|MissingLastRevSeeker
name|createMissingLastRevSeeker
parameter_list|()
block|{
specifier|final
name|DocumentStore
name|store
init|=
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|store
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
return|return
operator|new
name|MongoMissingLastRevSeeker
argument_list|(
operator|(
name|MongoDocumentStore
operator|)
name|store
argument_list|,
name|getClock
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createMissingLastRevSeeker
argument_list|()
return|;
block|}
block|}
comment|/**      * Returns the Mongo URI used in the {@link #setMongoDB(String, String, int)} method.      *      * @return the Mongo URI or null if the {@link #setMongoDB(String, String, int)} method hasn't      * been called.      */
name|String
name|getMongoUri
parameter_list|()
block|{
return|return
name|mongoUri
return|;
block|}
comment|/**      * Returns the status of the Mongo server configured in the {@link #setMongoDB(String, String, int)} method.      *      * @return the status or null if the {@link #setMongoDB(String, String, int)} method hasn't      * been called.      */
name|MongoStatus
name|getMongoStatus
parameter_list|()
block|{
return|return
name|mongoStatus
return|;
block|}
name|long
name|getMaxReplicationLagMillis
parameter_list|()
block|{
return|return
name|maxReplicationLagMillis
return|;
block|}
specifier|private
name|T
name|setMongoDB
parameter_list|(
annotation|@
name|Nonnull
name|DB
name|db
parameter_list|,
name|MongoStatus
name|status
parameter_list|,
name|int
name|blobCacheSizeMB
parameter_list|)
block|{
if|if
condition|(
operator|!
name|MongoConnection
operator|.
name|hasSufficientWriteConcern
argument_list|(
name|db
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Insufficient write concern: "
operator|+
name|db
operator|.
name|getWriteConcern
argument_list|()
operator|+
literal|" At least "
operator|+
name|MongoConnection
operator|.
name|getDefaultWriteConcern
argument_list|(
name|db
argument_list|)
operator|+
literal|" is recommended."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|isMajorityReadConcernSupported
argument_list|()
operator|&&
operator|!
name|status
operator|.
name|isMajorityReadConcernEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The read concern should be enabled on mongod using --enableMajorityReadConcern"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|status
operator|.
name|isMajorityReadConcernSupported
argument_list|()
operator|&&
operator|!
name|MongoConnection
operator|.
name|hasSufficientReadConcern
argument_list|(
name|db
argument_list|)
condition|)
block|{
name|ReadConcernLevel
name|currentLevel
init|=
name|readConcernLevel
argument_list|(
name|db
operator|.
name|getReadConcern
argument_list|()
argument_list|)
decl_stmt|;
name|ReadConcernLevel
name|recommendedLevel
init|=
name|readConcernLevel
argument_list|(
name|MongoConnection
operator|.
name|getDefaultReadConcern
argument_list|(
name|db
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLevel
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Read concern hasn't been set. At least "
operator|+
name|recommendedLevel
operator|+
literal|" is recommended."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Insufficient read concern: "
operator|+
name|currentLevel
operator|+
literal|". At least "
operator|+
name|recommendedLevel
operator|+
literal|" is recommended."
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|mongoStatus
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|documentStoreSupplier
operator|=
name|memoize
argument_list|(
parameter_list|()
lambda|->
operator|new
name|MongoDocumentStore
argument_list|(
name|db
argument_list|,
name|MongoDocumentNodeStoreBuilderBase
operator|.
name|this
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blobStore
operator|==
literal|null
condition|)
block|{
name|GarbageCollectableBlobStore
name|s
init|=
operator|new
name|MongoBlobStore
argument_list|(
name|db
argument_list|,
name|blobCacheSizeMB
operator|*
literal|1024
operator|*
literal|1024L
argument_list|)
decl_stmt|;
name|setGCBlobStore
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|thisBuilder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

