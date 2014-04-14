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
import|import static
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
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
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
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Locale
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

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Modified
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
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
name|api
operator|.
name|jmx
operator|.
name|CacheStatsMBean
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
name|kernel
operator|.
name|KernelNodeStore
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
name|osgi
operator|.
name|ObserverTracker
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|BlobGC
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
name|BlobGCMBean
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
name|BlobGarbageCollector
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
name|cache
operator|.
name|CachingDocumentStore
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
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|spi
operator|.
name|state
operator|.
name|RevisionGC
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
name|RevisionGCMBean
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardExecutor
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
name|whiteboard
operator|.
name|WhiteboardUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
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
name|component
operator|.
name|ComponentContext
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
name|MongoClient
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
name|MongoClientURI
import|;
end_import

begin_comment
comment|/**  * The OSGi service to start/stop a DocumentNodeStore instance.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
specifier|public
class|class
name|DocumentNodeStoreService
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_URI
init|=
literal|"mongodb://localhost:27017/oak"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_OFF_HEAP_CACHE
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CHANGES_SIZE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DB
init|=
literal|"oak"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"oak.documentstore."
decl_stmt|;
comment|/**      * Name of framework property to configure Mongo Connection URI      */
specifier|private
specifier|static
specifier|final
name|String
name|FWK_PROP_URI
init|=
literal|"oak.mongo.uri"
decl_stmt|;
comment|/**      * Name of framework property to configure Mongo Database name      * to use      */
specifier|private
specifier|static
specifier|final
name|String
name|FWK_PROP_DB
init|=
literal|"oak.mongo.db"
decl_stmt|;
comment|//DocumentMK would be done away with so better not
comment|//to expose this setting in config ui
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|false
argument_list|,
name|propertyPrivate
operator|=
literal|true
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_USE_MK
init|=
literal|"useMK"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_URI
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_URI
init|=
literal|"mongouri"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_DB
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_DB
init|=
literal|"db"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_CACHE
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_CACHE
init|=
literal|"cache"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_OFF_HEAP_CACHE
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_OFF_HEAP_CACHE
init|=
literal|"offHeapCache"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_CHANGES_SIZE
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_CHANGES_SIZE
init|=
literal|"changesSize"
decl_stmt|;
comment|/**      * Boolean value indicating a blobStore is to be used      */
specifier|public
specifier|static
specifier|final
name|String
name|CUSTOM_BLOB_STORE
init|=
literal|"customBlobStore"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ServiceRegistration
name|reg
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Registration
argument_list|>
name|registrations
init|=
operator|new
name|ArrayList
argument_list|<
name|Registration
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|WhiteboardExecutor
name|executor
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_UNARY
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|)
specifier|private
specifier|volatile
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
name|DocumentMK
name|mk
decl_stmt|;
specifier|private
name|ObserverTracker
name|observerTracker
decl_stmt|;
specifier|private
name|ComponentContext
name|context
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
comment|/**      * Revisions older than this time would be garbage collected      */
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_VER_GC_MAX_AGE
init|=
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toSeconds
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROP_VER_GC_MAX_AGE
init|=
literal|"versionGcMaxAgeInSecs"
decl_stmt|;
specifier|private
name|long
name|versionGcMaxAgeInSecs
init|=
name|DEFAULT_VER_GC_MAX_AGE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROP_REV_RECOVERY_INTERVAL
init|=
literal|"lastRevRecoveryJobIntervalInSecs"
decl_stmt|;
comment|/**      * Blob modified before this time duration would be considered for Blob GC      */
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_BLOB_GC_MAX_AGE
init|=
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|24
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROP_BLOB_GC_MAX_AGE
init|=
literal|"blobGcMaxAgeInSecs"
decl_stmt|;
specifier|private
name|long
name|blobGcMaxAgeInSecs
init|=
name|DEFAULT_BLOB_GC_MAX_AGE
decl_stmt|;
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
operator|new
name|WhiteboardExecutor
argument_list|()
expr_stmt|;
name|executor
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
if|if
condition|(
name|blobStore
operator|==
literal|null
operator|&&
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|prop
argument_list|(
name|CUSTOM_BLOB_STORE
argument_list|)
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"BlobStore use enabled. DocumentNodeStoreService would be initialized when "
operator|+
literal|"BlobStore would be available"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|registerNodeStore
argument_list|()
expr_stmt|;
block|}
name|modified
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|registerNodeStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Component still not activated. Ignoring the initialization call"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|uri
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|prop
argument_list|(
name|PROP_URI
argument_list|,
name|FWK_PROP_URI
argument_list|)
argument_list|,
name|DEFAULT_URI
argument_list|)
decl_stmt|;
name|String
name|db
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|prop
argument_list|(
name|PROP_DB
argument_list|,
name|FWK_PROP_DB
argument_list|)
argument_list|,
name|DEFAULT_DB
argument_list|)
decl_stmt|;
name|int
name|offHeapCache
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|prop
argument_list|(
name|PROP_OFF_HEAP_CACHE
argument_list|)
argument_list|,
name|DEFAULT_OFF_HEAP_CACHE
argument_list|)
decl_stmt|;
name|int
name|cacheSize
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|prop
argument_list|(
name|PROP_CACHE
argument_list|)
argument_list|,
name|DEFAULT_CACHE
argument_list|)
decl_stmt|;
name|int
name|changesSize
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|prop
argument_list|(
name|PROP_CHANGES_SIZE
argument_list|)
argument_list|,
name|DEFAULT_CHANGES_SIZE
argument_list|)
decl_stmt|;
name|boolean
name|useMK
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PROP_USE_MK
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|mkBuilder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
operator|.
name|offHeapCacheSize
argument_list|(
name|offHeapCache
operator|*
name|MB
argument_list|)
decl_stmt|;
comment|//Set blobstore before setting the DB
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|mkBuilder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|String
name|jdbcuri
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.jdbc.connection.uri"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|jdbcuri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// FIXME
name|String
name|username
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.jdbc.username"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|passwd
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.jdbc.password"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|driver
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.jdbc.driver.class"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|driver
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"trying to load {}"
argument_list|,
name|driver
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"driver not loaded"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|String
name|type
init|=
name|useMK
condition|?
literal|"MK"
else|:
literal|"NodeStore"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting Document{} with uri={}, cache size (MB)={}, Off Heap Cache size (MB)={}, 'changes' collection size (MB)={}"
argument_list|,
name|type
argument_list|,
name|jdbcuri
argument_list|,
name|cacheSize
argument_list|,
name|offHeapCache
argument_list|,
name|changesSize
argument_list|)
expr_stmt|;
block|}
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcuri
argument_list|,
name|username
argument_list|,
name|passwd
argument_list|)
decl_stmt|;
name|mkBuilder
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Connected to datasource {}"
argument_list|,
name|ds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MongoClientOptions
operator|.
name|Builder
name|builder
init|=
name|MongoConnection
operator|.
name|getDefaultBuilder
argument_list|()
decl_stmt|;
name|MongoClientURI
name|mongoURI
init|=
operator|new
name|MongoClientURI
argument_list|(
name|uri
argument_list|,
name|builder
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
comment|// Take care around not logging the uri directly as it
comment|// might contain passwords
name|String
name|type
init|=
name|useMK
condition|?
literal|"MK"
else|:
literal|"NodeStore"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting Document{} with host={}, db={}, cache size (MB)={}, Off Heap Cache size (MB)={}, 'changes' collection size (MB)={}"
argument_list|,
name|type
argument_list|,
name|mongoURI
operator|.
name|getHosts
argument_list|()
argument_list|,
name|db
argument_list|,
name|cacheSize
argument_list|,
name|offHeapCache
argument_list|,
name|changesSize
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Mongo Connection details {}"
argument_list|,
name|MongoConnection
operator|.
name|toString
argument_list|(
name|mongoURI
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MongoClient
name|client
init|=
operator|new
name|MongoClient
argument_list|(
name|mongoURI
argument_list|)
decl_stmt|;
name|DB
name|mongoDB
init|=
name|client
operator|.
name|getDB
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|mkBuilder
operator|.
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|,
name|changesSize
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Connected to database {}"
argument_list|,
name|mongoDB
argument_list|)
expr_stmt|;
block|}
name|mkBuilder
operator|.
name|setExecutor
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|mk
operator|=
name|mkBuilder
operator|.
name|open
argument_list|()
expr_stmt|;
name|registerJMXBeans
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|registerLastRevRecoveryJob
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|NodeStore
name|store
decl_stmt|;
if|if
condition|(
name|useMK
condition|)
block|{
name|KernelNodeStore
name|kns
init|=
operator|new
name|KernelNodeStore
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|store
operator|=
name|kns
expr_stmt|;
name|observerTracker
operator|=
operator|new
name|ObserverTracker
argument_list|(
name|kns
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocumentNodeStore
name|mns
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|store
operator|=
name|mns
expr_stmt|;
name|observerTracker
operator|=
operator|new
name|ObserverTracker
argument_list|(
name|mns
argument_list|)
expr_stmt|;
block|}
name|observerTracker
operator|.
name|start
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|Dictionary
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Constants
operator|.
name|SERVICE_PID
argument_list|,
name|DocumentNodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|reg
operator|=
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|store
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
comment|/**      * At runtime DocumentNodeStore only pickup modification of certain properties      */
annotation|@
name|Modified
specifier|protected
name|void
name|modified
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|versionGcMaxAgeInSecs
operator|=
name|PropertiesUtil
operator|.
name|toLong
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_VER_GC_MAX_AGE
argument_list|)
argument_list|,
name|DEFAULT_VER_GC_MAX_AGE
argument_list|)
expr_stmt|;
name|blobGcMaxAgeInSecs
operator|=
name|PropertiesUtil
operator|.
name|toLong
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_BLOB_GC_MAX_AGE
argument_list|)
argument_list|,
name|DEFAULT_BLOB_GC_MAX_AGE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|protected
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|observerTracker
operator|!=
literal|null
condition|)
block|{
name|observerTracker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|unregisterNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|bindBlobStore
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Initializing DocumentNodeStore with BlobStore [{}]"
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|registerNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|protected
name|void
name|unbindBlobStore
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
literal|null
expr_stmt|;
name|unregisterNodeStore
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|unregisterNodeStore
parameter_list|()
block|{
for|for
control|(
name|Registration
name|r
range|:
name|registrations
control|)
block|{
name|r
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reg
operator|!=
literal|null
condition|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|executor
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|registerJMXBeans
parameter_list|(
specifier|final
name|DocumentNodeStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|store
operator|.
name|getNodeCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|store
operator|.
name|getNodeCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|store
operator|.
name|getNodeChildrenCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|store
operator|.
name|getNodeChildrenCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|store
operator|.
name|getDocChildrenCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|store
operator|.
name|getDocChildrenCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DiffCache
name|cl
init|=
name|store
operator|.
name|getDiffCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|cl
operator|instanceof
name|MemoryDiffCache
condition|)
block|{
name|MemoryDiffCache
name|mcl
init|=
operator|(
name|MemoryDiffCache
operator|)
name|cl
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|mcl
operator|.
name|getDiffCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|mcl
operator|.
name|getDiffCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DocumentStore
name|ds
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|instanceof
name|CachingDocumentStore
condition|)
block|{
name|CachingDocumentStore
name|cds
init|=
operator|(
name|CachingDocumentStore
operator|)
name|ds
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|cds
operator|.
name|getCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|cds
operator|.
name|getCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|.
name|getBlobStore
argument_list|()
operator|instanceof
name|GarbageCollectableBlobStore
condition|)
block|{
name|BlobGarbageCollector
name|gc
init|=
operator|new
name|BlobGarbageCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|collectGarbage
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|createBlobGarbageCollector
argument_list|(
name|blobGcMaxAgeInSecs
argument_list|)
operator|.
name|collectGarbage
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|BlobGCMBean
operator|.
name|class
argument_list|,
operator|new
name|BlobGC
argument_list|(
name|gc
argument_list|,
name|executor
argument_list|)
argument_list|,
name|BlobGCMBean
operator|.
name|TYPE
argument_list|,
literal|"Document node store blob garbage collection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RevisionGC
name|revisionGC
init|=
operator|new
name|RevisionGC
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|(
name|versionGcMaxAgeInSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|RevisionGCMBean
operator|.
name|class
argument_list|,
name|revisionGC
argument_list|,
name|RevisionGCMBean
operator|.
name|TYPE
argument_list|,
literal|"Document node store revision garbage collection"
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO Register JMX bean for Off Heap Cache stats
block|}
specifier|private
name|void
name|registerLastRevRecoveryJob
parameter_list|(
specifier|final
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|long
name|leaseTime
init|=
name|PropertiesUtil
operator|.
name|toLong
argument_list|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PROP_REV_RECOVERY_INTERVAL
argument_list|)
argument_list|,
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_DURATION_MILLIS
argument_list|)
decl_stmt|;
name|Runnable
name|recoverJob
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|nodeStore
operator|.
name|getLastRevRecoveryAgent
argument_list|()
operator|.
name|performRecoveryIfNeeded
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|recoverJob
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|leaseTime
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Object
name|prop
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
return|return
name|prop
argument_list|(
name|propName
argument_list|,
name|PREFIX
operator|+
name|propName
argument_list|)
return|;
block|}
specifier|private
name|Object
name|prop
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|fwkPropName
parameter_list|)
block|{
comment|//Prefer framework property first
name|Object
name|value
init|=
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getProperty
argument_list|(
name|fwkPropName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
comment|//Fallback to one from config
return|return
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|propName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

