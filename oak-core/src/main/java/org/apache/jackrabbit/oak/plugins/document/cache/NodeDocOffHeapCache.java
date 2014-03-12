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
operator|.
name|cache
package|;
end_package

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
name|IOException
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
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|com
operator|.
name|esotericsoftware
operator|.
name|kryo
operator|.
name|Kryo
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
name|Stopwatch
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|ForwardingCache
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
name|cache
operator|.
name|RemovalCause
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
name|cache
operator|.
name|RemovalListener
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
name|cache
operator|.
name|RemovalNotification
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
name|ImmutableMap
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
name|Lists
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
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directmemory
operator|.
name|measures
operator|.
name|Ram
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directmemory
operator|.
name|memory
operator|.
name|MemoryManagerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directmemory
operator|.
name|memory
operator|.
name|MemoryManagerServiceImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directmemory
operator|.
name|memory
operator|.
name|Pointer
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
name|cache
operator|.
name|CacheStats
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
name|cache
operator|.
name|CacheValue
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
name|CachedNodeDocument
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
name|DocumentMK
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
name|NodeDocument
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
name|cache
operator|.
name|AbstractCache
operator|.
name|SimpleStatsCounter
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
name|cache
operator|.
name|AbstractCache
operator|.
name|StatsCounter
import|;
end_import

begin_class
specifier|public
class|class
name|NodeDocOffHeapCache
extends|extends
name|ForwardingCache
operator|.
name|SimpleForwardingCache
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
implements|implements
name|Closeable
implements|,
name|OffHeapCache
block|{
specifier|private
specifier|final
name|StatsCounter
name|statsCounter
init|=
operator|new
name|SimpleStatsCounter
argument_list|()
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
specifier|final
name|Cache
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocReference
argument_list|>
name|offHeapCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|offHeapCacheStats
decl_stmt|;
specifier|private
specifier|final
name|MemoryManagerService
argument_list|<
name|NodeDocument
argument_list|>
name|memoryManager
decl_stmt|;
specifier|private
specifier|final
name|KryoSerializer
name|serializer
decl_stmt|;
specifier|public
name|NodeDocOffHeapCache
parameter_list|(
name|Cache
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|delegate
parameter_list|,
name|ForwardingListener
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|forwardingListener
parameter_list|,
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|,
name|DocumentStore
name|documentStore
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|forwardingListener
operator|.
name|setDelegate
argument_list|(
operator|new
name|PrimaryRemovalListener
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|maxMemory
init|=
name|builder
operator|.
name|getOffHeapCacheSize
argument_list|()
decl_stmt|;
comment|//TODO We may also expire the entries from cache if not accessed for some time
name|offHeapCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|weigher
argument_list|(
name|builder
operator|.
name|getWeigher
argument_list|()
argument_list|)
operator|.
name|maximumWeight
argument_list|(
name|maxMemory
argument_list|)
operator|.
name|removalListener
argument_list|(
operator|new
name|SecondaryRemovalListener
argument_list|()
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|offHeapCacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|offHeapCache
argument_list|,
literal|"DocumentMk-Documents-L2"
argument_list|,
name|builder
operator|.
name|getWeigher
argument_list|()
argument_list|,
name|builder
operator|.
name|getOffHeapCacheSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|bufferSize
init|=
name|Ram
operator|.
name|Gb
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|noOfBuffers
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
call|(
name|int
call|)
argument_list|(
name|maxMemory
operator|/
name|bufferSize
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|buffSize
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|maxMemory
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
comment|//TODO Check if UnsafeMemoryManagerServiceImpl should be preferred
comment|//on Sun/Oracle JDK
name|memoryManager
operator|=
operator|new
name|MemoryManagerServiceImpl
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
expr_stmt|;
name|memoryManager
operator|.
name|init
argument_list|(
name|noOfBuffers
argument_list|,
name|buffSize
argument_list|)
expr_stmt|;
name|serializer
operator|=
operator|new
name|KryoSerializer
argument_list|(
operator|new
name|OakKryoPool
argument_list|(
name|documentStore
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDocument
name|getIfPresent
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|NodeDocument
name|result
init|=
name|super
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|retrieve
argument_list|(
name|key
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDocument
name|get
parameter_list|(
specifier|final
name|CacheValue
name|key
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|?
extends|extends
name|NodeDocument
argument_list|>
name|valueLoader
parameter_list|)
throws|throws
name|ExecutionException
block|{
return|return
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|,
operator|new
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Check in offHeap first
name|NodeDocument
name|result
init|=
name|retrieve
argument_list|(
name|key
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//Not found in L2 then load
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|valueLoader
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableMap
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|getAllPresent
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|keys
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|CacheValue
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|(
name|Iterable
argument_list|<
name|CacheValue
argument_list|>
operator|)
name|keys
argument_list|)
decl_stmt|;
name|ImmutableMap
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|result
init|=
name|super
operator|.
name|getAllPresent
argument_list|(
name|list
argument_list|)
decl_stmt|;
comment|//All the requested keys found then no
comment|//need to check L2
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|result
return|;
block|}
comment|//Look up value from L2
name|Map
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|r2
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|result
argument_list|)
decl_stmt|;
for|for
control|(
name|CacheValue
name|key
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|result
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|NodeDocument
name|val
init|=
name|retrieve
argument_list|(
name|key
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|r2
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|r2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|super
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|offHeapCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateAll
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|keys
parameter_list|)
block|{
name|super
operator|.
name|invalidateAll
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|offHeapCache
operator|.
name|invalidateAll
argument_list|(
name|keys
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateAll
parameter_list|()
block|{
name|super
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|offHeapCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|memoryManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|CacheValue
argument_list|,
name|?
extends|extends
name|CachedNodeDocument
argument_list|>
name|offHeapEntriesMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|offHeapCache
operator|.
name|asMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|offHeapCacheStats
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|CachedNodeDocument
name|getCachedDocument
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|NodeDocument
name|doc
init|=
name|super
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
return|return
name|doc
return|;
block|}
return|return
name|offHeapCache
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Retrieves the value from the off heap cache.      *      * @param key                     cache entry key to retrieve      * @param invalidateAfterRetrieve set it to true if the entry from off heap cache has      *                                to be invalidated. This would be the case when value loaded is      *                                made part of L1 cache      */
specifier|private
name|NodeDocument
name|retrieve
parameter_list|(
name|Object
name|key
parameter_list|,
name|boolean
name|invalidateAfterRetrieve
parameter_list|)
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|NodeDocReference
name|value
init|=
name|offHeapCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|statsCounter
operator|.
name|recordMisses
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|NodeDocument
name|result
init|=
name|value
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|statsCounter
operator|.
name|recordLoadSuccess
argument_list|(
name|watch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statsCounter
operator|.
name|recordMisses
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|invalidateAfterRetrieve
condition|)
block|{
comment|//The value would be made part of L1 cache so no need to keep it
comment|//in backend
name|offHeapCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
class|class
name|PrimaryRemovalListener
implements|implements
name|RemovalListener
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocument
argument_list|>
name|n
parameter_list|)
block|{
comment|//If removed explicitly then we clear from L2
if|if
condition|(
name|n
operator|.
name|getCause
argument_list|()
operator|==
name|RemovalCause
operator|.
name|EXPLICIT
operator|||
name|n
operator|.
name|getCause
argument_list|()
operator|==
name|RemovalCause
operator|.
name|REPLACED
condition|)
block|{
name|offHeapCache
operator|.
name|invalidate
argument_list|(
name|n
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//If removed because of size then we move it to
comment|//L2
if|if
condition|(
name|n
operator|.
name|getCause
argument_list|()
operator|==
name|RemovalCause
operator|.
name|SIZE
condition|)
block|{
name|NodeDocument
name|doc
init|=
name|n
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
name|offHeapCache
operator|.
name|put
argument_list|(
name|n
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|NodeDocReference
argument_list|(
name|n
operator|.
name|getKey
argument_list|()
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
class|class
name|SecondaryRemovalListener
implements|implements
name|RemovalListener
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocReference
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|CacheValue
argument_list|,
name|NodeDocReference
argument_list|>
name|notification
parameter_list|)
block|{
name|NodeDocReference
name|doc
init|=
name|notification
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
operator|&&
name|doc
operator|.
name|getPointer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|memoryManager
operator|.
name|free
argument_list|(
name|doc
operator|.
name|getPointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|NodeDocReference
implements|implements
name|CachedNodeDocument
implements|,
name|CacheValue
block|{
specifier|private
specifier|final
name|Number
name|modCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|created
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|lastCheckTime
decl_stmt|;
specifier|private
specifier|final
name|Pointer
argument_list|<
name|NodeDocument
argument_list|>
name|documentPointer
decl_stmt|;
specifier|private
specifier|final
name|CacheValue
name|key
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|public
name|NodeDocReference
parameter_list|(
name|CacheValue
name|key
parameter_list|,
name|NodeDocument
name|doc
parameter_list|)
block|{
name|this
operator|.
name|modCount
operator|=
name|doc
operator|.
name|getModCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|created
operator|=
name|doc
operator|.
name|getCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastCheckTime
operator|=
operator|new
name|AtomicLong
argument_list|(
name|doc
operator|.
name|getLastCheckTime
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentPointer
operator|=
name|serialize
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|doc
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|getModCount
parameter_list|()
block|{
return|return
name|modCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastCheckTime
parameter_list|()
block|{
return|return
name|lastCheckTime
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|markUpToDate
parameter_list|(
name|long
name|checkTime
parameter_list|)
block|{
name|lastCheckTime
operator|.
name|set
argument_list|(
name|checkTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUpToDate
parameter_list|(
name|long
name|lastCheckTime
parameter_list|)
block|{
return|return
name|lastCheckTime
operator|<=
name|this
operator|.
name|lastCheckTime
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|NodeDocument
name|getDocument
parameter_list|()
block|{
return|return
name|deserialize
argument_list|(
name|documentPointer
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|Pointer
argument_list|<
name|NodeDocument
argument_list|>
name|getPointer
parameter_list|()
block|{
return|return
name|documentPointer
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|Pointer
argument_list|<
name|NodeDocument
argument_list|>
name|serialize
parameter_list|(
name|NodeDocument
name|doc
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|payload
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|Pointer
argument_list|<
name|NodeDocument
argument_list|>
name|ptr
init|=
name|memoryManager
operator|.
name|store
argument_list|(
name|payload
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ptr
operator|.
name|setClazz
argument_list|(
name|NodeDocument
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|ptr
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to serialize doc {}"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SynchronizationOnLocalVariableOrMethodParameter"
argument_list|)
annotation|@
name|CheckForNull
specifier|private
name|NodeDocument
name|deserialize
parameter_list|(
annotation|@
name|CheckForNull
name|Pointer
argument_list|<
name|NodeDocument
argument_list|>
name|pointer
parameter_list|)
block|{
try|try
block|{
comment|//If there was some error in serializing then pointer
comment|// would be null
if|if
condition|(
name|pointer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//TODO Look for a way to have a direct access to MemoryManager buffer
comment|//for Kryo so that no copying is involved
specifier|final
name|byte
index|[]
name|value
decl_stmt|;
comment|//Workaround for DIRECTMEMORY-137 Concurrent access via same pointer
comment|//can lead to issues. For now synchronizing on the pointer
synchronized|synchronized
init|(
name|pointer
init|)
block|{
name|value
operator|=
name|memoryManager
operator|.
name|retrieve
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
block|}
name|NodeDocument
name|doc
init|=
name|serializer
operator|.
name|deserialize
argument_list|(
name|value
argument_list|,
name|pointer
operator|.
name|getClazz
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|markUpToDate
argument_list|(
name|getLastCheckTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to deserialize doc {} with pointer {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|key
block|,
name|pointer
block|,
name|e
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
name|int
name|result
init|=
literal|168
decl_stmt|;
if|if
condition|(
name|documentPointer
operator|!=
literal|null
condition|)
block|{
name|result
operator|+=
operator|(
name|int
operator|)
name|documentPointer
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OakKryoPool
extends|extends
name|KryoSerializer
operator|.
name|KryoPool
block|{
specifier|private
specifier|final
name|DocumentStore
name|documentStore
decl_stmt|;
specifier|public
name|OakKryoPool
parameter_list|(
name|DocumentStore
name|documentStore
parameter_list|)
block|{
name|this
operator|.
name|documentStore
operator|=
name|documentStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Kryo
name|createInstance
parameter_list|()
block|{
return|return
name|KryoFactory
operator|.
name|createInstance
argument_list|(
name|documentStore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

