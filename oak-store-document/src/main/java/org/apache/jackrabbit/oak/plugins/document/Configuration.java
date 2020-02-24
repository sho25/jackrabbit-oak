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
name|document
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Option
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
name|Configuration
operator|.
name|PID
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_CACHE_SEGMENT_COUNT
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_CACHE_STACK_MOVE_DISTANCE
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_CHILDREN_CACHE_PERCENTAGE
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_DIFF_CACHE_PERCENTAGE
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_NODE_CACHE_PERCENTAGE
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_PREV_DOC_CACHE_PERCENTAGE
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
name|DocumentNodeStoreBuilder
operator|.
name|DEFAULT_UPDATE_LIMIT
import|;
end_import

begin_annotation_defn
annotation|@
name|ObjectClassDefinition
argument_list|(
name|pid
operator|=
block|{
name|PID
block|}
argument_list|,
name|name
operator|=
literal|"Apache Jackrabbit Oak Document NodeStore Service"
argument_list|,
name|description
operator|=
literal|"NodeStore implementation based on Document model. For configuration option refer "
operator|+
literal|"to http://jackrabbit.apache.org/oak/docs/osgi_config.html#DocumentNodeStore. Note that for system "
operator|+
literal|"stability purpose it is advisable to not change these settings at runtime. Instead the config change "
operator|+
literal|"should be done via file system based config file and this view should ONLY be used to determine which "
operator|+
literal|"options are supported"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
name|String
name|PID
init|=
literal|"org.apache.jackrabbit.oak.plugins.document.DocumentNodeStoreService"
decl_stmt|;
name|String
name|PRESET_PID
init|=
name|PID
operator|+
literal|"Preset"
decl_stmt|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Mongo URI"
argument_list|,
name|description
operator|=
literal|"Mongo connection URI used to connect to Mongo. Refer to "
operator|+
literal|"http://docs.mongodb.org/manual/reference/connection-string/ for details. Note that this value "
operator|+
literal|"can be overridden via framework property 'oak.mongo.uri'"
argument_list|)
name|String
name|mongouri
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_URI
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Mongo DB name"
argument_list|,
name|description
operator|=
literal|"Name of the database in Mongo. Note that this value "
operator|+
literal|"can be overridden via framework property 'oak.mongo.db'"
argument_list|)
name|String
name|db
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_DB
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"MongoDB socket keep-alive option"
argument_list|,
name|description
operator|=
literal|"Whether socket keep-alive should be enabled for "
operator|+
literal|"connections to MongoDB. Note that this value can be "
operator|+
literal|"overridden via framework property 'oak.mongo.socketKeepAlive'"
argument_list|)
name|boolean
name|socketKeepAlive
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_SO_KEEP_ALIVE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Cache Size (in MB)"
argument_list|,
name|description
operator|=
literal|"Cache size in MB. This is distributed among various caches used in DocumentNodeStore"
argument_list|)
name|int
name|cache
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_CACHE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"NodeState Cache"
argument_list|,
name|description
operator|=
literal|"Percentage of cache to be allocated towards Node cache"
argument_list|)
name|int
name|nodeCachePercentage
parameter_list|()
default|default
name|DEFAULT_NODE_CACHE_PERCENTAGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"PreviousDocument Cache"
argument_list|,
name|description
operator|=
literal|"Percentage of cache to be allocated towards Previous Document cache"
argument_list|)
name|int
name|prevDocCachePercentage
parameter_list|()
default|default
name|DEFAULT_PREV_DOC_CACHE_PERCENTAGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"NodeState Children Cache"
argument_list|,
name|description
operator|=
literal|"Percentage of cache to be allocated towards Children cache"
argument_list|)
name|int
name|childrenCachePercentage
parameter_list|()
default|default
name|DEFAULT_CHILDREN_CACHE_PERCENTAGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Diff Cache"
argument_list|,
name|description
operator|=
literal|"Percentage of cache to be allocated towards Diff cache"
argument_list|)
name|int
name|diffCachePercentage
parameter_list|()
default|default
name|DEFAULT_DIFF_CACHE_PERCENTAGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"LIRS Cache Segment Count"
argument_list|,
name|description
operator|=
literal|"The number of segments in the LIRS cache "
operator|+
literal|"(default 16, a higher count means higher concurrency "
operator|+
literal|"but slightly lower cache hit rate)"
argument_list|)
name|int
name|cacheSegmentCount
parameter_list|()
default|default
name|DEFAULT_CACHE_SEGMENT_COUNT
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"LIRS Cache Stack Move Distance"
argument_list|,
name|description
operator|=
literal|"The delay to move entries to the head of the queue "
operator|+
literal|"in the LIRS cache "
operator|+
literal|"(default 16, a higher value means higher concurrency "
operator|+
literal|"but slightly lower cache hit rate)"
argument_list|)
name|int
name|cacheStackMoveDistance
parameter_list|()
default|default
name|DEFAULT_CACHE_STACK_MOVE_DISTANCE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Blob Cache Size (in MB)"
argument_list|,
name|description
operator|=
literal|"Cache size to store blobs in memory. Used only with default BlobStore "
operator|+
literal|"(as per DocumentStore type)"
argument_list|)
name|int
name|blobCacheSize
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_BLOB_CACHE_SIZE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Persistent Cache Config"
argument_list|,
name|description
operator|=
literal|"Configuration for persistent cache. Refer to "
operator|+
literal|"http://jackrabbit.apache.org/oak/docs/nodestore/persistent-cache.html for various options"
argument_list|)
name|String
name|persistentCache
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_PERSISTENT_CACHE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Journal Cache Config"
argument_list|,
name|description
operator|=
literal|"Configuration for journal cache. Refer to "
operator|+
literal|"http://jackrabbit.apache.org/oak/docs/nodestore/persistent-cache.html for various options"
argument_list|)
name|String
name|journalCache
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_JOURNAL_CACHE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Custom BlobStore"
argument_list|,
name|description
operator|=
literal|"Boolean value indicating that a custom BlobStore is to be used. "
operator|+
literal|"By default, for MongoDB, MongoBlobStore is used; for RDB, RDBBlobStore is used."
argument_list|)
name|boolean
name|customBlobStore
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_CUSTOM_BLOB_STORE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Journal Garbage Collection Interval (millis)"
argument_list|,
name|description
operator|=
literal|"Long value indicating interval (in milliseconds) with which the "
operator|+
literal|"journal (for external changes) is cleaned up. Default is "
operator|+
name|DocumentNodeStoreService
operator|.
name|DEFAULT_JOURNAL_GC_INTERVAL_MILLIS
argument_list|)
name|long
name|journalGCInterval
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_JOURNAL_GC_INTERVAL_MILLIS
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Maximum Age of Journal Entries (millis)"
argument_list|,
name|description
operator|=
literal|"Long value indicating max age (in milliseconds) that "
operator|+
literal|"journal (for external changes) entries are kept (older ones are candidates for gc). "
operator|+
literal|"Default is "
operator|+
name|DocumentNodeStoreService
operator|.
name|DEFAULT_JOURNAL_GC_MAX_AGE_MILLIS
argument_list|)
name|long
name|journalGCMaxAge
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_JOURNAL_GC_MAX_AGE_MILLIS
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Pre-fetch external changes"
argument_list|,
name|description
operator|=
literal|"Boolean value indicating if external changes should "
operator|+
literal|"be pre-fetched in a background thread."
argument_list|)
name|boolean
name|prefetchExternalChanges
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_PREFETCH_EXTERNAL_CHANGES
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"NodeStoreProvider role"
argument_list|,
name|description
operator|=
literal|"Property indicating that this component will not register as a NodeStore but as a "
operator|+
literal|"NodeStoreProvider with given role"
argument_list|)
name|String
name|role
parameter_list|()
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Version GC Max Age (in secs)"
argument_list|,
name|description
operator|=
literal|"Version Garbage Collector (GC) logic will only consider those deleted for GC which "
operator|+
literal|"are not accessed recently (currentTime - lastModifiedTime> versionGcMaxAgeInSecs). For "
operator|+
literal|"example as per default only those document which have been *marked* deleted 24 hrs ago will be "
operator|+
literal|"considered for GC. This also applies how older revision of live document are GC."
argument_list|)
name|long
name|versionGcMaxAgeInSecs
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_VER_GC_MAX_AGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Version GC scheduler expression"
argument_list|,
name|description
operator|=
literal|"A cron expression that defines when the Version GC is scheduled. "
operator|+
literal|"If this configuration entry is left empty, the default behaviour is to "
operator|+
literal|"schedule a run every five seconds (also known as Continuous Revision Garbage "
operator|+
literal|"Collection). Otherwise, the schedule can be configured with a cron "
operator|+
literal|"expression. E.g. the following expression triggers a GC run every night at 2 AM: '"
operator|+
name|DocumentNodeStoreService
operator|.
name|CLASSIC_RGC_EXPR
operator|+
literal|"'."
argument_list|)
name|String
name|versionGCExpression
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_VER_GC_EXPRESSION
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Time limit for a Version GC run (in sec)"
argument_list|,
name|description
operator|=
literal|"A Version GC run is canceled after this number of seconds. "
operator|+
literal|"The default value is "
operator|+
name|DocumentNodeStoreService
operator|.
name|DEFAULT_RGC_TIME_LIMIT_SECS
operator|+
literal|" seconds."
argument_list|)
name|long
name|versionGCTimeLimitInSecs
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_RGC_TIME_LIMIT_SECS
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Delay factor for a Version GC run"
argument_list|,
name|description
operator|=
literal|"A Version GC run has a gap of this delay factor to reduce continuous load on system"
operator|+
literal|"The default value is "
operator|+
name|DocumentNodeStoreService
operator|.
name|DEFAULT_RGC_DELAY_FACTOR
argument_list|)
name|double
name|versionGCDelayFactor
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_RGC_DELAY_FACTOR
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Blob GC Max Age (in secs)"
argument_list|,
name|description
operator|=
literal|"Blob Garbage Collector (GC) logic will only consider those blobs for GC which "
operator|+
literal|"are not accessed recently (currentTime - lastModifiedTime> blobGcMaxAgeInSecs). For "
operator|+
literal|"example as per default only those blobs which have been created 24 hrs ago will be "
operator|+
literal|"considered for GC"
argument_list|)
name|long
name|blobGcMaxAgeInSecs
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_BLOB_GC_MAX_AGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Blob tracking snapshot interval (in secs)"
argument_list|,
name|description
operator|=
literal|"This is the default interval in which the snapshots of locally tracked blob ids will"
operator|+
literal|"be taken and synchronized with the blob store. This should be configured to be less than the "
operator|+
literal|"frequency of blob GC so that deletions during blob GC can be accounted for "
operator|+
literal|"in the next GC execution."
argument_list|)
name|long
name|blobTrackSnapshotIntervalInSecs
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_BLOB_SNAPSHOT_INTERVAL
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Root directory"
argument_list|,
name|description
operator|=
literal|"Root directory for local tracking of blob ids. This service "
operator|+
literal|"will first lookup the 'repository.home' framework property and "
operator|+
literal|"then a component context property with the same name. If none "
operator|+
literal|"of them is defined, a sub directory 'repository' relative to "
operator|+
literal|"the current working directory is used."
argument_list|)
name|String
name|repository_home
parameter_list|()
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Max Replication Lag (in secs)"
argument_list|,
name|description
operator|=
literal|"Value in seconds. Determines the duration beyond which it can be safely assumed "
operator|+
literal|"that the state on the secondaries is consistent with the primary, and it is safe to read from them"
argument_list|)
name|long
name|maxReplicationLagInSecs
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_MAX_REPLICATION_LAG
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"DocumentStore Type"
argument_list|,
name|description
operator|=
literal|"Type of DocumentStore to use for persistence. Defaults to MONGO"
argument_list|,
name|options
operator|=
block|{
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"MONGO"
argument_list|,
name|value
operator|=
literal|"MONGO"
argument_list|)
block|,
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"RDB"
argument_list|,
name|value
operator|=
literal|"RDB"
argument_list|)
block|}
argument_list|)
name|String
name|documentStoreType
parameter_list|()
default|default
literal|"MONGO"
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Bundling Disabled"
argument_list|,
name|description
operator|=
literal|"Boolean value indicating that Node bundling is disabled"
argument_list|)
name|boolean
name|bundlingDisabled
parameter_list|()
default|default
name|DocumentNodeStoreService
operator|.
name|DEFAULT_BUNDLING_DISABLED
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"DocumentNodeStore update.limit"
argument_list|,
name|description
operator|=
literal|"Number of content updates that need to happen before "
operator|+
literal|"the updates are automatically purged to the private branch."
argument_list|)
name|int
name|updateLimit
parameter_list|()
default|default
name|DEFAULT_UPDATE_LIMIT
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Persistent Cache Includes"
argument_list|,
name|description
operator|=
literal|"Paths which should be cached in persistent cache"
argument_list|)
name|String
index|[]
name|persistentCacheIncludes
argument_list|()
expr|default
block|{
literal|"/"
block|}
expr_stmt|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Lease check mode"
argument_list|,
name|description
operator|=
literal|"The lease check mode. 'STRICT' is the default and "
operator|+
literal|"will stop the DocumentNodeStore as soon as the lease "
operator|+
literal|"expires. 'LENIENT' will give the background lease update "
operator|+
literal|"a chance to renew the lease even when the lease expired. "
operator|+
literal|"This mode is only recommended for development, e.g. when "
operator|+
literal|"debugging an application and the lease may expire when "
operator|+
literal|"the JVM is stopped at a breakpoint."
argument_list|,
name|options
operator|=
block|{
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"STRICT"
argument_list|,
name|value
operator|=
literal|"STRICT"
argument_list|)
block|,
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"LENIENT"
argument_list|,
name|value
operator|=
literal|"LENIENT"
argument_list|)
block|}
argument_list|)
name|String
name|leaseCheckMode
parameter_list|()
default|default
literal|"STRICT"
function_decl|;
block|}
end_annotation_defn

end_unit

