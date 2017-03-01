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
name|segment
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
name|segment
operator|.
name|RecordCache
operator|.
name|newRecordCache
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
name|Random
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
name|Supplier
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|RecordCacheStatsTest
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
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RecordCache
argument_list|<
name|Integer
argument_list|>
name|cache
init|=
name|newRecordCache
argument_list|(
name|KEYS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RecordCacheStats
name|cacheStats
init|=
operator|new
name|RecordCacheStats
argument_list|(
name|NAME
argument_list|,
operator|new
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CacheStats
name|get
parameter_list|()
block|{
return|return
name|cache
operator|.
name|getStats
argument_list|()
return|;
block|}
block|}
argument_list|,
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|,
operator|new
name|Supplier
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|get
parameter_list|()
block|{
return|return
name|cache
operator|.
name|estimateCurrentWeight
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|private
name|int
name|hits
decl_stmt|;
specifier|public
name|RecordCacheStatsTest
parameter_list|()
throws|throws
name|IOException
block|{}
specifier|private
name|RecordId
name|newRecordId
parameter_list|()
block|{
return|return
name|TestUtils
operator|.
name|newRecordId
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|rnd
argument_list|)
return|;
block|}
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
name|newRecordId
argument_list|()
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
if|if
condition|(
name|cache
operator|.
name|get
argument_list|(
literal|4
operator|*
name|k
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|hits
operator|++
expr_stmt|;
block|}
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
name|hits
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
operator|(
name|double
operator|)
name|hits
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
name|KEYS
operator|-
name|hits
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
name|KEYS
operator|-
operator|(
name|double
operator|)
name|hits
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
name|KEYS
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
name|KEYS
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
literal|0
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|getTotalLoadTime
parameter_list|()
block|{
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|getAverageLoadPenalty
parameter_list|()
block|{
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
operator|-
literal|1
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
argument_list|,
name|cacheStats
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
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

