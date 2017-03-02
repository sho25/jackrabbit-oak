begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|assertTrue
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
name|Weigher
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

begin_class
specifier|public
class|class
name|CacheStatsTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"cache stats"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|KEYS
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|Weigher
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|weigher
init|=
operator|new
name|Weigher
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
annotation|@
name|Nonnull
name|Integer
name|key
parameter_list|,
annotation|@
name|Nonnull
name|Integer
name|value
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|recordStats
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
init|=
operator|new
name|CacheStats
argument_list|(
name|cache
argument_list|,
name|NAME
argument_list|,
name|weigher
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
name|int
name|misses
decl_stmt|;
specifier|private
name|int
name|fails
decl_stmt|;
specifier|private
name|long
name|loadTime
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|KEYS
condition|;
name|k
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
specifier|final
name|int
name|key
init|=
literal|4
operator|*
name|k
decl_stmt|;
try|try
block|{
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
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
name|Exception
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|key
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|fails
operator|++
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"simulated load failure"
argument_list|)
throw|;
block|}
else|else
block|{
name|misses
operator|++
expr_stmt|;
return|return
name|key
return|;
block|}
block|}
finally|finally
block|{
name|loadTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ignore
parameter_list|)
block|{ }
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|name
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|NAME
argument_list|,
name|cacheStats
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getRequestCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|KEYS
argument_list|,
name|cacheStats
operator|.
name|getRequestCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getHitCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|KEYS
operator|-
name|misses
operator|-
name|fails
argument_list|,
name|cacheStats
operator|.
name|getHitCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getHitRate
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|KEYS
operator|-
operator|(
name|double
operator|)
name|misses
operator|-
name|fails
operator|)
operator|/
name|KEYS
argument_list|,
name|cacheStats
operator|.
name|getHitRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getMissCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|misses
operator|+
name|fails
argument_list|,
name|cacheStats
operator|.
name|getMissCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getMissRate
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
operator|(
name|double
operator|)
name|misses
operator|+
name|fails
operator|)
operator|/
name|KEYS
argument_list|,
name|cacheStats
operator|.
name|getMissRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getLoadCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|misses
operator|+
name|fails
argument_list|,
name|cacheStats
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getLoadSuccessCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|misses
argument_list|,
name|cacheStats
operator|.
name|getLoadSuccessCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getLoadExceptionCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|fails
argument_list|,
name|cacheStats
operator|.
name|getLoadExceptionCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getLoadExceptionRate
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|double
operator|)
name|fails
operator|/
operator|(
name|misses
operator|+
name|fails
operator|)
argument_list|,
name|cacheStats
operator|.
name|getLoadExceptionRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getTotalLoadTime
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|loadTime
operator|<=
name|cacheStats
operator|.
name|getTotalLoadTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getAverageLoadPenalty
parameter_list|()
block|{
name|assertTrue
argument_list|(
operator|(
operator|(
name|double
operator|)
name|loadTime
operator|/
operator|(
name|misses
operator|+
name|fails
operator|)
operator|)
operator|<=
name|cacheStats
operator|.
name|getAverageLoadPenalty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getEvictionCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getEvictionCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getElementCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|KEYS
operator|+
name|misses
argument_list|,
name|cacheStats
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getMaxTotalWeight
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|cacheStats
operator|.
name|getMaxTotalWeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|estimateCurrentWeight
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|KEYS
operator|+
name|misses
argument_list|,
name|cacheStats
operator|.
name|estimateCurrentWeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resetStats
parameter_list|()
block|{
name|cacheStats
operator|.
name|resetStats
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getRequestCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getHitCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|cacheStats
operator|.
name|getHitRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getMissCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|cacheStats
operator|.
name|getMissRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getLoadSuccessCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getLoadExceptionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getLoadExceptionRate
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getTotalLoadTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getAverageLoadPenalty
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheStats
operator|.
name|getEvictionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KEYS
operator|+
name|misses
argument_list|,
name|cacheStats
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|cacheStats
operator|.
name|getMaxTotalWeight
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KEYS
operator|+
name|misses
argument_list|,
name|cacheStats
operator|.
name|estimateCurrentWeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
