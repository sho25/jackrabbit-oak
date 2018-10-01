begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|mongo
package|;
end_package

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
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|AbstractMongoConnectionTest
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
name|Collection
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
name|UpdateOp
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
name|stats
operator|.
name|DefaultStatisticsProvider
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
name|stats
operator|.
name|StatisticsProvider
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
name|plugins
operator|.
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentNodeStoreBuilder
operator|.
name|newMongoDocumentNodeStoreBuilder
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
name|stats
operator|.
name|StatsOptions
operator|.
name|METRICS_ONLY
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|MongoDocumentStoreMetricsTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|ScheduledExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
specifier|private
name|StatisticsProvider
name|statsProvider
init|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executorService
argument_list|)
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateCounters
parameter_list|()
block|{
name|MongoConnection
name|connection
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|assumeNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|MongoDocumentStore
name|store
init|=
operator|new
name|MongoDocumentStore
argument_list|(
name|connection
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|connection
operator|.
name|getDatabase
argument_list|()
argument_list|,
name|newMongoDocumentNodeStoreBuilder
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|MongoDocumentStoreMetrics
name|metrics
init|=
operator|new
name|MongoDocumentStoreMetrics
argument_list|(
name|store
argument_list|,
name|statsProvider
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// document for root node
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getCount
argument_list|(
literal|"MongoDB.nodes.count"
argument_list|)
argument_list|)
expr_stmt|;
comment|// one cluster node
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getCount
argument_list|(
literal|"MongoDB.clusterNodes.count"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|updates
operator|.
name|add
argument_list|(
operator|new
name|UpdateOp
argument_list|(
literal|"id-"
operator|+
name|i
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|getCount
argument_list|(
literal|"MongoDB.nodes.count"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|getCount
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|statsProvider
operator|.
name|getCounterStats
argument_list|(
name|name
argument_list|,
name|METRICS_ONLY
argument_list|)
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

