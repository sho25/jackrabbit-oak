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
operator|.
name|impl
package|;
end_package

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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Counter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|cache
operator|.
name|AbstractCacheStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
import|import static
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
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|ELEMENT
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
name|cache
operator|.
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|EVICTION
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
name|cache
operator|.
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|HIT
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
name|cache
operator|.
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|MISS
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
name|cache
operator|.
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|REQUEST
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
name|cache
operator|.
name|impl
operator|.
name|CacheStatsMetrics
operator|.
name|metricName
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|CacheStatsMetricsTest
block|{
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|HIT_COUNT
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MISS_COUNT
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|REQUEST_COUNT
init|=
name|HIT_COUNT
operator|+
name|MISS_COUNT
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|EVICTION_COUNT
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|ELEMENT_COUNT
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|LOAD_TIME
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|metrics
parameter_list|()
block|{
name|MetricRegistry
name|registry
init|=
operator|new
name|MetricRegistry
argument_list|()
decl_stmt|;
name|CacheStatsMetrics
name|metrics
init|=
operator|new
name|CacheStatsMetrics
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|setMetricRegistry
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|CacheStatsMBean
name|bean
init|=
operator|new
name|TestStats
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|addCacheStatsMBean
argument_list|(
name|bean
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
init|=
name|registry
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|Counter
name|counter
init|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|REQUEST
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REQUEST_COUNT
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|HIT
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HIT_COUNT
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|MISS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MISS_COUNT
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|EVICTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EVICTION_COUNT
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|ELEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ELEMENT_COUNT
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counter
operator|=
name|counters
operator|.
name|get
argument_list|(
name|metricName
argument_list|(
name|bean
operator|.
name|getName
argument_list|()
argument_list|,
name|CacheStatsMetrics
operator|.
name|LOAD_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LOAD_TIME
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|removeCacheStatsMBean
argument_list|(
name|bean
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|registry
operator|.
name|getCounters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestStats
extends|extends
name|AbstractCacheStats
block|{
name|TestStats
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
name|getCurrentStats
parameter_list|()
block|{
return|return
operator|new
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
argument_list|(
name|HIT_COUNT
argument_list|,
name|MISS_COUNT
argument_list|,
name|MISS_COUNT
argument_list|,
literal|0
argument_list|,
name|LOAD_TIME
argument_list|,
name|EVICTION_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getElementCount
parameter_list|()
block|{
return|return
name|ELEMENT_COUNT
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotalWeight
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentWeight
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
block|}
block|}
end_class

end_unit

