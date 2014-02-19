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
name|List
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
name|Properties
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
name|Property
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
name|mk
operator|.
name|blobs
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
name|BlobStoreConfiguration
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
name|BlobStoreHelper
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
name|mongo
operator|.
name|MongoDocumentStore
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
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
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
name|base
operator|.
name|Strings
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
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"%oak.documentns.label"
argument_list|,
name|description
operator|=
literal|"%oak.documentns.description"
argument_list|,
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
name|logger
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
name|DocumentMK
name|mk
decl_stmt|;
specifier|private
name|ObserverTracker
name|observerTracker
decl_stmt|;
specifier|private
name|BundleContext
name|bundleContext
decl_stmt|;
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|BundleContext
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
name|bundleContext
operator|=
name|context
expr_stmt|;
name|String
name|uri
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|prop
argument_list|(
name|config
argument_list|,
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
name|config
argument_list|,
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
name|config
argument_list|,
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
name|config
argument_list|,
name|PROP_CACHE
argument_list|)
argument_list|,
name|DEFAULT_CACHE
argument_list|)
decl_stmt|;
name|boolean
name|useMK
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_USE_MK
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|blobStoreType
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|BlobStoreConfiguration
operator|.
name|PROP_BLOB_STORE_PROVIDER
argument_list|)
argument_list|,
name|BlobStoreConfiguration
operator|.
name|DEFAULT_BLOB_STORE_PROVIDER
argument_list|)
decl_stmt|;
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
name|logger
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
name|logger
operator|.
name|info
argument_list|(
literal|"Starting Document{} with host={}, db={}, cache size (MB)={}, Off Heap Cache size (MB)={}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|type
block|,
name|mongoURI
operator|.
name|getHosts
argument_list|()
block|,
name|db
block|,
name|cacheSize
block|,
name|offHeapCache
block|}
argument_list|)
expr_stmt|;
name|logger
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
comment|// Check if any valid external BlobStore is defined.
comment|// If not then use the default which is MongoBlobStore
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|blobStoreType
argument_list|)
condition|)
block|{
name|blobStore
operator|=
name|BlobStoreHelper
operator|.
name|create
argument_list|(
name|BlobStoreConfiguration
operator|.
name|newInstance
argument_list|()
operator|.
name|loadFromContextOrMap
argument_list|(
name|config
argument_list|,
name|context
argument_list|)
argument_list|)
operator|.
name|orNull
argument_list|()
expr_stmt|;
block|}
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
operator|.
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|)
decl_stmt|;
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
name|mk
operator|=
name|mkBuilder
operator|.
name|open
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Connected to database {}"
argument_list|,
name|mongoDB
argument_list|)
expr_stmt|;
name|registerJMXBeans
argument_list|(
name|mk
operator|.
name|getNodeStore
argument_list|()
argument_list|,
name|context
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
argument_list|)
expr_stmt|;
name|reg
operator|=
name|context
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
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Object
name|prop
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
return|return
name|prop
argument_list|(
name|config
argument_list|,
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
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|,
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
name|bundleContext
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
name|config
operator|.
name|get
argument_list|(
name|propName
argument_list|)
return|;
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
block|}
specifier|private
name|void
name|registerJMXBeans
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|BundleContext
name|context
parameter_list|)
block|{
name|Whiteboard
name|wb
init|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|wb
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
name|wb
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
name|wb
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|store
operator|.
name|getDiffCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|store
operator|.
name|getDiffCacheStats
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
name|wb
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
name|MongoDocumentStore
condition|)
block|{
name|MongoDocumentStore
name|mds
init|=
operator|(
name|MongoDocumentStore
operator|)
name|ds
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|wb
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|mds
operator|.
name|getCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|mds
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
comment|//TODO Register JMX bean for Off Heap Cache stats
block|}
block|}
end_class

end_unit

