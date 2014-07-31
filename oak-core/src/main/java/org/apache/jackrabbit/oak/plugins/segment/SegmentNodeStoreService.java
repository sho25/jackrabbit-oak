begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
package|;
end_package

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
name|checkState
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
name|Closeable
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
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
name|blob
operator|.
name|MarkSweepGarbageCollector
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
name|commit
operator|.
name|Observable
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
name|commit
operator|.
name|Observer
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
name|ProxyNodeStore
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
name|WhiteboardExecutor
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

begin_comment
comment|/**  * An OSGi wrapper for the segment node store.  */
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
name|SegmentNodeStoreService
extends|extends
name|ProxyNodeStore
implements|implements
name|Observable
block|{
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"The unique name of this instance"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK directory"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|DIRECTORY
init|=
literal|"repository.home"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK mode (64 for memory mapping, 32 for normal file access)"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"tarmk.mode"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"TarMK maximum file size (MB)"
argument_list|,
name|intValue
operator|=
literal|256
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|SIZE
init|=
literal|"tarmk.size"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|description
operator|=
literal|"Cache size (MB)"
argument_list|,
name|intValue
operator|=
literal|256
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|CACHE
init|=
literal|"cache"
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
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|SegmentStore
name|store
decl_stmt|;
specifier|private
name|SegmentNodeStore
name|delegate
decl_stmt|;
specifier|private
name|ObserverTracker
name|observerTracker
decl_stmt|;
specifier|private
name|ComponentContext
name|context
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
name|ServiceRegistration
name|registration
decl_stmt|;
specifier|private
name|Registration
name|revisionGCRegistration
decl_stmt|;
specifier|private
name|Registration
name|blobGCRegistration
decl_stmt|;
specifier|private
name|WhiteboardExecutor
name|executor
decl_stmt|;
specifier|private
name|boolean
name|customBlobStore
decl_stmt|;
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|SegmentNodeStore
name|getNodeStore
parameter_list|()
block|{
name|checkState
argument_list|(
name|delegate
operator|!=
literal|null
argument_list|,
literal|"service must be activated when used"
argument_list|)
expr_stmt|;
return|return
name|delegate
return|;
block|}
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|customBlobStore
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|lookup
argument_list|(
name|context
argument_list|,
name|CUSTOM_BLOB_STORE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|blobStore
operator|==
literal|null
operator|&&
name|customBlobStore
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"BlobStore use enabled. SegmentNodeStore would be initialized when BlobStore would be available"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|registerNodeStore
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
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
name|Dictionary
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
init|=
name|context
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|name
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|directory
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|DIRECTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
literal|"tarmk"
expr_stmt|;
block|}
else|else
block|{
name|directory
operator|=
name|FilenameUtils
operator|.
name|concat
argument_list|(
name|directory
argument_list|,
literal|"segmentstore"
argument_list|)
expr_stmt|;
block|}
name|String
name|mode
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|MODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
block|{
name|mode
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|MODE
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|,
literal|"32"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|size
init|=
name|lookup
argument_list|(
name|context
argument_list|,
name|SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|null
condition|)
block|{
name|size
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|SIZE
argument_list|,
literal|"256"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|memoryMapping
init|=
literal|"64"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
decl_stmt|;
if|if
condition|(
name|customBlobStore
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Initializing SegmentNodeStore with BlobStore [{}]"
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|blobStore
argument_list|,
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|size
argument_list|)
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|store
operator|=
operator|new
name|FileStore
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|size
argument_list|)
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
block|}
name|delegate
operator|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|observerTracker
operator|=
operator|new
name|ObserverTracker
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
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
name|SegmentNodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|registration
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
name|this
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|OsgiWhiteboard
name|whiteboard
init|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
operator|.
name|getBundleContext
argument_list|()
argument_list|)
decl_stmt|;
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
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|revisionGCRegistration
operator|=
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
literal|"Segment node store revision garbage collection"
argument_list|)
expr_stmt|;
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
name|MarkSweepGarbageCollector
name|gc
init|=
operator|new
name|MarkSweepGarbageCollector
argument_list|(
operator|new
name|SegmentBlobReferenceRetriever
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
argument_list|,
operator|(
name|GarbageCollectableBlobStore
operator|)
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|gc
operator|.
name|collectGarbage
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|blobGCRegistration
operator|=
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
literal|"Segment node store blob garbage collection"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"SegmentNodeStore initialized"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|lookup
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|String
name|property
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|property
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|property
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Deactivate
specifier|public
specifier|synchronized
name|void
name|deactivate
parameter_list|()
block|{
name|unregisterNodeStore
argument_list|()
expr_stmt|;
name|observerTracker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|delegate
operator|=
literal|null
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
block|}
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
if|if
condition|(
name|registration
operator|!=
literal|null
condition|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|registration
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|revisionGCRegistration
operator|!=
literal|null
condition|)
block|{
name|revisionGCRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|revisionGCRegistration
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|blobGCRegistration
operator|!=
literal|null
condition|)
block|{
name|blobGCRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|blobGCRegistration
operator|=
literal|null
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
comment|/**      * needed for situations where you have to unwrap the      * SegmentNodeStoreService, to get the SegmentStore, like the failover      */
specifier|public
name|SegmentStore
name|getSegmentStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
comment|//------------------------------------------------------------< Observable>---
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
return|return
name|getNodeStore
argument_list|()
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|": "
operator|+
name|delegate
return|;
block|}
block|}
end_class

end_unit

