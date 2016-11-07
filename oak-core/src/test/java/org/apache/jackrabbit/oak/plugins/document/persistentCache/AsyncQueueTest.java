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
name|persistentCache
package|;
end_package

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
name|oak
operator|.
name|cache
operator|.
name|CacheLIRS
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
name|DocumentMKBuilderProvider
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
name|PathRev
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
name|Revision
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
name|RevisionVector
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
name|persistentCache
operator|.
name|async
operator|.
name|CacheWriteQueue
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
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|atomic
operator|.
name|AtomicReference
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|AsyncQueueTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|StringValue
name|VAL
init|=
operator|new
name|StringValue
argument_list|(
literal|"xyz"
argument_list|)
decl_stmt|;
specifier|private
name|PersistentCache
name|pCache
decl_stmt|;
specifier|private
name|List
argument_list|<
name|PathRev
argument_list|>
name|putActions
decl_stmt|;
specifier|private
name|List
argument_list|<
name|PathRev
argument_list|>
name|invalidateActions
decl_stmt|;
specifier|private
name|NodeCache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|nodeCache
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
name|pCache
operator|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/cacheTest"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|NodeCache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
argument_list|>
name|nodeCacheRef
init|=
operator|new
name|AtomicReference
argument_list|<
name|NodeCache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|CacheLIRS
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|1
argument_list|)
operator|.
name|evictionCallback
argument_list|(
operator|new
name|CacheLIRS
operator|.
name|EvictionCallback
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evicted
parameter_list|(
annotation|@
name|Nonnull
name|PathRev
name|key
parameter_list|,
annotation|@
name|Nullable
name|StringValue
name|value
parameter_list|,
annotation|@
name|Nonnull
name|RemovalCause
name|cause
parameter_list|)
block|{
if|if
condition|(
name|nodeCacheRef
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|nodeCacheRef
operator|.
name|get
argument_list|()
operator|.
name|evicted
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|nodeCache
operator|=
operator|(
name|NodeCache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
operator|)
name|pCache
operator|.
name|wrap
argument_list|(
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
argument_list|,
literal|null
argument_list|,
name|cache
argument_list|,
name|CacheType
operator|.
name|NODE
argument_list|)
expr_stmt|;
name|nodeCacheRef
operator|.
name|set
argument_list|(
name|nodeCache
argument_list|)
expr_stmt|;
name|CacheWriteQueueWrapper
name|writeQueue
init|=
operator|new
name|CacheWriteQueueWrapper
argument_list|(
name|nodeCache
operator|.
name|writeQueue
argument_list|)
decl_stmt|;
name|nodeCache
operator|.
name|writeQueue
operator|=
name|writeQueue
expr_stmt|;
name|this
operator|.
name|putActions
operator|=
name|writeQueue
operator|.
name|putActions
expr_stmt|;
name|this
operator|.
name|invalidateActions
operator|=
name|writeQueue
operator|.
name|invalidateActions
expr_stmt|;
name|this
operator|.
name|id
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|pCache
operator|!=
literal|null
condition|)
block|{
name|pCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|unusedItemsShouldntBePersisted
parameter_list|()
block|{
name|PathRev
name|k
init|=
name|generatePathRev
argument_list|()
decl_stmt|;
name|nodeCache
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|VAL
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|putActions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readItemsShouldntBePersistedAgain
parameter_list|()
block|{
name|PathRev
name|k
init|=
name|generatePathRev
argument_list|()
decl_stmt|;
name|nodeCache
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|VAL
argument_list|)
expr_stmt|;
name|nodeCache
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
name|k
argument_list|)
argument_list|,
name|putActions
argument_list|)
expr_stmt|;
name|putActions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeCache
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
expr_stmt|;
comment|// k should be loaded from persisted cache
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|putActions
argument_list|)
expr_stmt|;
comment|// k is not persisted again
block|}
annotation|@
name|Test
specifier|public
name|void
name|usedItemsShouldBePersisted
parameter_list|()
block|{
name|PathRev
name|k
init|=
name|generatePathRev
argument_list|()
decl_stmt|;
name|nodeCache
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|VAL
argument_list|)
expr_stmt|;
name|nodeCache
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
name|k
argument_list|)
argument_list|,
name|putActions
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PathRev
name|generatePathRev
parameter_list|()
block|{
return|return
operator|new
name|PathRev
argument_list|(
literal|"/"
operator|+
name|id
operator|++
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|flush
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|nodeCache
operator|.
name|put
argument_list|(
name|generatePathRev
argument_list|()
argument_list|,
name|VAL
argument_list|)
expr_stmt|;
comment|// cause eviction of k
block|}
block|}
specifier|private
specifier|static
class|class
name|CacheWriteQueueWrapper
extends|extends
name|CacheWriteQueue
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
block|{
specifier|private
specifier|final
name|CacheWriteQueue
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|wrapped
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|PathRev
argument_list|>
name|putActions
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|PathRev
argument_list|>
name|invalidateActions
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|public
name|CacheWriteQueueWrapper
parameter_list|(
name|CacheWriteQueue
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|wrapped
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addPut
parameter_list|(
name|PathRev
name|key
parameter_list|,
name|StringValue
name|value
parameter_list|)
block|{
name|putActions
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
name|wrapped
operator|.
name|addPut
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|addInvalidate
parameter_list|(
name|Iterable
argument_list|<
name|PathRev
argument_list|>
name|keys
parameter_list|)
block|{
name|invalidateActions
operator|.
name|addAll
argument_list|(
name|newArrayList
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|wrapped
operator|.
name|addInvalidate
argument_list|(
name|keys
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

