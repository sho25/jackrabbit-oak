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
name|cache
package|;
end_package

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
name|ConcurrentMap
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
name|atomic
operator|.
name|AtomicBoolean
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the LIRS cache by concurrently reading and writing.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testLoaderBlock
parameter_list|()
throws|throws
name|Exception
block|{
comment|// access to the same segment should not be blocked while loading an entry
comment|// only access to this entry is blocked
specifier|final
name|CacheLIRS
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
literal|100
argument_list|)
operator|.
name|averageWeight
argument_list|(
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Exception
index|[]
name|ex
init|=
operator|new
name|Exception
index|[
literal|1
index|]
decl_stmt|;
name|int
name|threadCount
init|=
literal|10
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadCount
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|nextKey
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|additionalWait
init|=
operator|new
name|AtomicLong
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
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
specifier|final
name|int
name|key
init|=
name|nextKey
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
specifier|final
name|int
name|wait
init|=
name|key
decl_stmt|;
name|Callable
argument_list|<
name|Integer
argument_list|>
name|callable
init|=
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|ExecutionException
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|wait
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|cache
operator|.
name|get
argument_list|(
name|key
operator|*
literal|10
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|callable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|additionalWait
operator|.
name|addAndGet
argument_list|(
name|time
operator|-
name|wait
argument_list|)
expr_stmt|;
name|cache
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
name|t
expr_stmt|;
block|}
comment|// test for 1000 ms
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// if the thread is still alive after 1 second, we assume
comment|// there is a deadlock - we just let the threads alive,
comment|// but report a failure (what else could we do?)
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Deadlock detected!"
argument_list|,
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ex
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
index|[
literal|0
index|]
throw|;
block|}
name|long
name|add
init|=
name|additionalWait
operator|.
name|get
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"add: "
operator|+
name|add
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Had to wait unexpectedly long for other threads: "
operator|+
name|add
argument_list|,
name|add
operator|<
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCacheAccessInLoaderDeadlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CacheLIRS
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
literal|100
argument_list|)
operator|.
name|averageWeight
argument_list|(
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Exception
index|[]
name|ex
init|=
operator|new
name|Exception
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|int
name|entryCount
init|=
literal|100
decl_stmt|;
name|int
name|size
init|=
literal|3
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
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
name|size
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
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Callable
argument_list|<
name|Integer
argument_list|>
name|callable
init|=
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|ExecutionException
block|{
name|cache
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|entryCount
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|Integer
name|key
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|entryCount
argument_list|)
decl_stmt|;
try|try
block|{
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|callable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
block|}
name|cache
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
name|t
expr_stmt|;
block|}
comment|// test for 100 ms
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// if the thread is still alive after 1 second, we assume
comment|// there is a deadlock - we just let the threads alive,
comment|// but report a failure (what else could we do?)
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Deadlock detected!"
argument_list|,
name|t
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ex
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
index|[
literal|0
index|]
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRandomOperations
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CacheLIRS
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
literal|100
argument_list|)
operator|.
name|averageWeight
argument_list|(
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Exception
index|[]
name|ex
init|=
operator|new
name|Exception
index|[
literal|1
index|]
decl_stmt|;
name|int
name|size
init|=
literal|3
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
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
name|size
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
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|cache
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
name|cache
operator|.
name|containsKey
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cache
operator|.
name|containsValue
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|cache
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getMaxMemory
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getIfPresent
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getAverageMemory
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getMemory
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getUsedMemory
argument_list|()
expr_stmt|;
name|cache
operator|.
name|invalidate
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
name|cache
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|cache
operator|.
name|keySet
argument_list|()
expr_stmt|;
name|cache
operator|.
name|peek
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|cache
operator|.
name|refresh
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|cache
operator|.
name|remove
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|cache
operator|.
name|setAverageMemory
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|cache
operator|.
name|setMaxMemory
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|cache
operator|.
name|size
argument_list|()
expr_stmt|;
name|cache
operator|.
name|stats
argument_list|()
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|cache
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|size
argument_list|()
expr_stmt|;
name|map
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|map
operator|.
name|containsKey
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|containsValue
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|get
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|12
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|13
argument_list|)
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|map
operator|.
name|keySet
argument_list|()
expr_stmt|;
name|map
operator|.
name|values
argument_list|()
expr_stmt|;
name|map
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|map
operator|.
name|putIfAbsent
argument_list|(
literal|14
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|16
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|map
operator|.
name|replace
argument_list|(
literal|17
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|map
operator|.
name|replace
argument_list|(
literal|18
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|cache
operator|.
name|get
argument_list|(
literal|19
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getUnchecked
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
index|[
literal|0
index|]
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
name|t
expr_stmt|;
block|}
try|try
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|start
operator|+
literal|1000
condition|)
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
literal|100000
operator|&&
name|ex
index|[
literal|0
index|]
operator|==
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
block|}
if|if
condition|(
name|ex
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ex
index|[
literal|0
index|]
throw|;
block|}
block|}
block|}
end_class

end_unit

