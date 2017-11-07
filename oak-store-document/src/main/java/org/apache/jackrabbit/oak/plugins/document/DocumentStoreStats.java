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
package|;
end_package

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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|api
operator|.
name|stats
operator|.
name|TimeSeries
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
name|Utils
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
name|MeterStats
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatsOptions
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
name|TimerStats
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
name|stats
operator|.
name|TimeSeriesStatsUtil
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Document Store statistics helper class.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentStoreStats
implements|implements
name|DocumentStoreStatsCollector
implements|,
name|DocumentStoreStatsMBean
block|{
specifier|private
specifier|final
name|Logger
name|perfLog
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentStoreStats
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PERF_LOG_THRESHOLD
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_CACHED
init|=
literal|"DOCUMENT_NODES_FIND_CACHED"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_SPLIT
init|=
literal|"DOCUMENT_NODES_FIND_SPLIT"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_SLAVE
init|=
literal|"DOCUMENT_NODES_FIND_SLAVE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_PRIMARY
init|=
literal|"DOCUMENT_NODES_FIND_PRIMARY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_MISSING_TIMER
init|=
literal|"DOCUMENT_NODES_FIND_MISSING"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_FIND_TIMER
init|=
literal|"DOCUMENT_NODES_FIND"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_FIND_READ_COUNT
init|=
literal|"DOCUMENT_NODES_QUERY_FIND"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_FILTER
init|=
literal|"DOCUMENT_NODES_QUERY_FILTER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_TIMER
init|=
literal|"DOCUMENT_NODES_QUERY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_SLAVE
init|=
literal|"DOCUMENT_NODES_QUERY_SLAVE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_PRIMARY
init|=
literal|"DOCUMENT_NODES_QUERY_PRIMARY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_LOCK
init|=
literal|"DOCUMENT_NODES_QUERY_LOCK"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_QUERY_LOCK_TIMER
init|=
literal|"DOCUMENT_NODES_QUERY_LOCK_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_CREATE
init|=
literal|"DOCUMENT_NODES_CREATE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_CREATE_UPSERT
init|=
literal|"DOCUMENT_NODES_CREATE_UPSERT"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_CREATE_SPLIT
init|=
literal|"DOCUMENT_NODES_CREATE_SPLIT"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_CREATE_UPSERT_TIMER
init|=
literal|"DOCUMENT_NODES_CREATE_UPSERT_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_CREATE_TIMER
init|=
literal|"DOCUMENT_NODES_CREATE_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_UPDATE
init|=
literal|"DOCUMENT_NODES_UPDATE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_UPDATE_FAILURE
init|=
literal|"DOCUMENT_NODES_UPDATE_FAILURE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_UPDATE_RETRY_COUNT
init|=
literal|"DOCUMENT_NODES_UPDATE_RETRY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_UPDATE_TIMER
init|=
literal|"DOCUMENT_NODES_UPDATE_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_REMOVE
init|=
literal|"DOCUMENT_NODES_REMOVE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NODES_REMOVE_TIMER
init|=
literal|"DOCUMENT_NODES_REMOVE_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|JOURNAL_QUERY
init|=
literal|"DOCUMENT_JOURNAL_QUERY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|JOURNAL_CREATE
init|=
literal|"DOCUMENT_JOURNAL_CREATE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|JOURNAL_QUERY_TIMER
init|=
literal|"DOCUMENT_JOURNAL_QUERY_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|JOURNAL_CREATE_TIMER
init|=
literal|"DOCUMENT_JOURNAL_CREATE_TIMER"
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|findNodesCachedMeter
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|findNodesMissingTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|findNodesSlave
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|findNodesTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|findNodesPrimary
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|queryNodesSlave
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|queryNodesPrimary
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|queryNodesResult
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|queryNodesWithFilterTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|queryNodesTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|queryJournal
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|queryJournalTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|createNodeUpsertTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|createNodeTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|updateNodeTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|createNodeUpsertMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|createNodeMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|updateNodeMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|createJournal
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|createJournalTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|findSplitNodes
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|queryNodesLock
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|queryNodesLockTimer
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|createSplitNodeMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|updateNodeFailureMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|updateNodeRetryCountMeter
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|removeNodes
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|removeNodesTimer
decl_stmt|;
specifier|public
name|DocumentStoreStats
parameter_list|(
name|StatisticsProvider
name|provider
parameter_list|)
block|{
name|statisticsProvider
operator|=
name|checkNotNull
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|findNodesCachedMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_FIND_CACHED
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|findNodesMissingTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_FIND_MISSING_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|findNodesTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_FIND_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|findSplitNodes
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_FIND_SPLIT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|findNodesSlave
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_FIND_SLAVE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|findNodesPrimary
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_FIND_PRIMARY
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesSlave
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_QUERY_SLAVE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesPrimary
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_QUERY_PRIMARY
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesResult
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_QUERY_FIND_READ_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesWithFilterTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_QUERY_FILTER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|queryNodesTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_QUERY_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|queryJournal
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|JOURNAL_QUERY
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryJournalTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|JOURNAL_QUERY_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|createJournal
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|JOURNAL_CREATE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|createJournalTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|JOURNAL_CREATE_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|createNodeUpsertTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_CREATE_UPSERT_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|createNodeTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_CREATE_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|updateNodeTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_UPDATE_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|createNodeMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_CREATE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|createNodeUpsertMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_CREATE_UPSERT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|createSplitNodeMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_CREATE_SPLIT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|updateNodeMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_UPDATE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|updateNodeFailureMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_UPDATE_FAILURE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|updateNodeRetryCountMeter
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_UPDATE_RETRY_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesLock
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_QUERY_LOCK
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|queryNodesLockTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_QUERY_LOCK_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|removeNodes
operator|=
name|provider
operator|.
name|getMeter
argument_list|(
name|NODES_REMOVE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|removeNodesTimer
operator|=
name|provider
operator|.
name|getTimer
argument_list|(
name|NODES_REMOVE_TIMER
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
block|}
comment|//~------------------------------------------< DocumentStoreStatsCollector>
annotation|@
name|Override
specifier|public
name|void
name|doneFindCached
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
comment|//findCached call is almost done for NODES collection only
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|findNodesCachedMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneFindUncached
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|docFound
parameter_list|,
name|boolean
name|isSlaveOk
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
comment|//For now collect time for reads from primary/secondary in same timer
name|TimerStats
name|timer
init|=
name|docFound
condition|?
name|findNodesTimer
else|:
name|findNodesMissingTimer
decl_stmt|;
name|timer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
comment|//For now only nodes can be looked up from slave
if|if
condition|(
name|isSlaveOk
condition|)
block|{
name|findNodesSlave
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|findNodesPrimary
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Utils
operator|.
name|isPreviousDocId
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|findSplitNodes
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"findUncached on key={}, isSlaveOk={}"
argument_list|,
name|key
argument_list|,
name|isSlaveOk
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneQuery
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|boolean
name|indexedProperty
parameter_list|,
name|int
name|resultSize
parameter_list|,
name|long
name|lockTime
parameter_list|,
name|boolean
name|isSlaveOk
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
comment|//Distinguish between query done with filter and without filter
name|TimerStats
name|timer
init|=
name|indexedProperty
condition|?
name|queryNodesWithFilterTimer
else|:
name|queryNodesTimer
decl_stmt|;
name|timer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
comment|//Number of nodes read
name|queryNodesResult
operator|.
name|mark
argument_list|(
name|resultSize
argument_list|)
expr_stmt|;
comment|//Stats for queries to slaves
if|if
condition|(
name|isSlaveOk
condition|)
block|{
name|queryNodesSlave
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queryNodesPrimary
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lockTime
operator|>
literal|0
condition|)
block|{
name|queryNodesLock
operator|.
name|mark
argument_list|()
expr_stmt|;
name|queryNodesLockTimer
operator|.
name|update
argument_list|(
name|lockTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
comment|//TODO What more to gather
comment|// - Histogram of result - How the number of children vary
block|}
elseif|else
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|JOURNAL
condition|)
block|{
comment|//Journals are read from primary and without any extra condition on indexedProperty
name|queryJournal
operator|.
name|mark
argument_list|(
name|resultSize
argument_list|)
expr_stmt|;
name|queryJournalTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"query for children from [{}] to [{}], lock:{}"
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|lockTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneCreate
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|,
name|boolean
name|insertSuccess
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
operator|&&
name|insertSuccess
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|createNodeMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
if|if
condition|(
name|Utils
operator|.
name|isPreviousDocId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|createSplitNodeMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|createNodeTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
operator|/
name|ids
operator|.
name|size
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|JOURNAL
condition|)
block|{
name|createJournal
operator|.
name|mark
argument_list|(
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|createJournalTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"create"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneCreateOrUpdate
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|createNodeUpsertMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
if|if
condition|(
name|Utils
operator|.
name|isPreviousDocId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|createSplitNodeMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|createNodeUpsertTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
operator|/
name|ids
operator|.
name|size
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"createOrUpdate {}"
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneFindAndModify
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|newEntry
parameter_list|,
name|boolean
name|success
parameter_list|,
name|int
name|retryCount
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|newEntry
condition|)
block|{
name|createNodeUpsertMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
name|createNodeUpsertTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateNodeMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
name|updateNodeTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|retryCount
operator|>
literal|0
condition|)
block|{
name|updateNodeRetryCountMeter
operator|.
name|mark
argument_list|(
name|retryCount
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|updateNodeRetryCountMeter
operator|.
name|mark
argument_list|(
name|retryCount
argument_list|)
expr_stmt|;
name|updateNodeFailureMeter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"findAndModify [{}]"
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneRemove
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
name|collection
parameter_list|,
name|int
name|removeCount
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
if|if
condition|(
name|removeCount
operator|>
literal|0
condition|)
block|{
name|removeNodes
operator|.
name|mark
argument_list|(
name|removeCount
argument_list|)
expr_stmt|;
name|removeNodesTimer
operator|.
name|update
argument_list|(
name|timeTakenNanos
operator|/
name|removeCount
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
block|}
name|perfLog
argument_list|(
name|timeTakenNanos
argument_list|,
literal|"remove [{}]"
argument_list|,
name|removeCount
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|perfLog
parameter_list|(
name|long
name|timeTakenNanos
parameter_list|,
name|String
name|logMessagePrefix
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
if|if
condition|(
operator|!
name|perfLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|long
name|diff
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|timeTakenNanos
argument_list|)
decl_stmt|;
if|if
condition|(
name|perfLog
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// if log level is TRACE, then always log - and do that on TRACE
comment|// then:
name|perfLog
operator|.
name|trace
argument_list|(
name|logMessagePrefix
operator|+
literal|" [took "
operator|+
name|diff
operator|+
literal|"ms]"
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|arguments
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|diff
operator|>
name|PERF_LOG_THRESHOLD
condition|)
block|{
name|perfLog
operator|.
name|debug
argument_list|(
name|logMessagePrefix
operator|+
literal|" [took "
operator|+
name|diff
operator|+
literal|"ms]"
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|arguments
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~--------------------------------------------< DocumentStoreStatsMBean>
annotation|@
name|Override
specifier|public
name|long
name|getNodesFindCount
parameter_list|()
block|{
return|return
name|findNodesSlave
operator|.
name|getCount
argument_list|()
operator|+
name|queryNodesPrimary
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNodesFindQueryCount
parameter_list|()
block|{
return|return
name|queryNodesSlave
operator|.
name|getCount
argument_list|()
operator|+
name|queryNodesPrimary
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNodesReadByQueryCount
parameter_list|()
block|{
return|return
name|queryNodesResult
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNodesCreateCount
parameter_list|()
block|{
return|return
name|createNodeMeter
operator|.
name|getCount
argument_list|()
operator|+
name|createNodeUpsertMeter
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNodesUpdateCount
parameter_list|()
block|{
return|return
name|updateNodeMeter
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getNodesRemoveCount
parameter_list|()
block|{
return|return
name|removeNodes
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getJournalCreateCount
parameter_list|()
block|{
return|return
name|createJournal
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getJournalReadCount
parameter_list|()
block|{
return|return
name|queryJournal
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getFindCachedNodesHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_FIND_CACHED
argument_list|,
name|NODES_FIND_CACHED
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getFindSplitNodesHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_FIND_SPLIT
argument_list|,
name|NODES_FIND_SPLIT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getFindNodesFromPrimaryHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_FIND_PRIMARY
argument_list|,
name|NODES_FIND_PRIMARY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getFindNodesFromSlaveHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_FIND_SLAVE
argument_list|,
name|NODES_FIND_SLAVE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryNodesFromSlaveHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_QUERY_SLAVE
argument_list|,
name|NODES_QUERY_SLAVE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryNodesFromPrimaryHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_QUERY_PRIMARY
argument_list|,
name|NODES_QUERY_PRIMARY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryNodesLockHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_QUERY_LOCK
argument_list|,
name|NODES_QUERY_LOCK
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryJournalHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|JOURNAL_QUERY
argument_list|,
name|JOURNAL_QUERY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getCreateJournalHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|JOURNAL_CREATE
argument_list|,
name|JOURNAL_CREATE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getCreateNodesHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_CREATE
argument_list|,
name|NODES_CREATE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUpdateNodesHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_UPDATE
argument_list|,
name|NODES_UPDATE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUpdateNodesRetryHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_UPDATE_RETRY_COUNT
argument_list|,
name|NODES_UPDATE_RETRY_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUpdateNodesFailureHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_UPDATE_FAILURE
argument_list|,
name|NODES_UPDATE_FAILURE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getRemoveNodesHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|NODES_REMOVE
argument_list|,
name|NODES_REMOVE
argument_list|)
return|;
block|}
specifier|private
name|CompositeData
name|getTimeSeriesData
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
return|return
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|name
argument_list|)
argument_list|,
name|desc
argument_list|)
return|;
block|}
specifier|private
name|TimeSeries
name|getTimeSeries
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|statisticsProvider
operator|.
name|getStats
argument_list|()
operator|.
name|getTimeSeries
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

