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
operator|.
name|async
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Random
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
name|AtomicInteger
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
name|PersistentCache
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
name|CacheAction
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
name|CacheActionDispatcher
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
specifier|public
class|class
name|CacheWriteQueueTest
block|{
specifier|private
name|CacheWriteQueue
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|queue
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
name|List
argument_list|<
name|CacheAction
argument_list|>
name|actions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|CacheAction
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|initQueue
parameter_list|()
block|{
name|actions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CacheActionDispatcher
name|dispatcher
init|=
operator|new
name|CacheActionDispatcher
argument_list|()
block|{
specifier|public
name|void
name|add
parameter_list|(
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|action
parameter_list|)
block|{
name|actions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|PersistentCache
name|cache
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PersistentCache
operator|.
name|class
argument_list|)
decl_stmt|;
name|queue
operator|=
operator|new
name|CacheWriteQueue
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|dispatcher
argument_list|,
name|cache
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCounters
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|threadCount
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|actionsPerThread
init|=
literal|50
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AtomicInteger
argument_list|>
name|counters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AtomicInteger
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"key_"
operator|+
name|i
decl_stmt|;
name|counters
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|AtomicInteger
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|actionsPerThread
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|String
name|key
range|:
name|counters
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|queue
operator|.
name|addPut
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|addPut
argument_list|(
name|key
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|counters
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|counters
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|queue
operator|.
name|queuedKeys
operator|.
name|count
argument_list|(
name|key
argument_list|)
argument_list|,
name|counters
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CacheAction
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|action
range|:
name|actions
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|action
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|action
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|queue
operator|.
name|queuedKeys
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWaitsForInvalidation
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addInvalidate
argument_list|(
name|singleton
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addPut
argument_list|(
literal|"key"
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addInvalidate
argument_list|(
name|singleton
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|actions
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|actions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|actions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|waitsForInvalidation
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

