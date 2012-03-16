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
name|mk
operator|.
name|concurrent
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|util
operator|.
name|Cache
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
comment|/**  * Tests the cache implementation.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentCacheTest
implements|implements
name|Cache
operator|.
name|Backend
argument_list|<
name|Integer
argument_list|,
name|ConcurrentCacheTest
operator|.
name|Data
argument_list|>
block|{
name|Cache
argument_list|<
name|Integer
argument_list|,
name|Data
argument_list|>
name|cache
init|=
name|Cache
operator|.
name|newInstance
argument_list|(
name|this
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|volatile
name|int
name|value
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Concurrent
operator|.
name|run
argument_list|(
literal|"cache"
argument_list|,
operator|new
name|Concurrent
operator|.
name|Task
argument_list|()
block|{
specifier|public
name|void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|k
init|=
name|value
operator|++
operator|%
literal|10
decl_stmt|;
name|Data
name|v
init|=
name|cache
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|k
argument_list|,
name|v
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Data
name|load
parameter_list|(
name|Integer
name|key
parameter_list|)
block|{
name|int
name|start
init|=
name|counter
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
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
if|if
condition|(
name|counter
operator|.
name|getAndIncrement
argument_list|()
operator|!=
name|start
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Concurrent load"
argument_list|)
throw|;
block|}
return|return
operator|new
name|Data
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|static
class|class
name|Data
implements|implements
name|Cache
operator|.
name|Value
block|{
name|int
name|value
decl_stmt|;
name|Data
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

