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
name|stats
operator|.
name|HistogramStats
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

begin_class
specifier|public
class|class
name|DocumentNodeStoreStats
implements|implements
name|DocumentNodeStoreStatsCollector
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BGR_READ_HEAD
init|=
literal|"DOCUMENT_NS_BGR_READ_HEAD"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_CACHE_INVALIDATE
init|=
literal|"DOCUMENT_NS_BGR_CACHE_INVALIDATE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_DIFF_CACHE
init|=
literal|"DOCUMENT_NS_BGR_DIFF_CACHE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_LOCK
init|=
literal|"DOCUMENT_NS_BGR_LOCK"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_DISPATCH
init|=
literal|"DOCUMENT_NS_BGR_DISPATCH"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_TOTAL_TIME
init|=
literal|"DOCUMENT_NS_BGR_TOTAL_TIME"
decl_stmt|;
specifier|static
specifier|final
name|String
name|BGR_NUM_CHANGES_RATE
init|=
literal|"DOCUMENT_NS_BGR_NUM_CHANGES_RATE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGR_NUM_CHANGES_HISTO
init|=
literal|"DOCUMENT_NS_BGR_NUM_CHANGES_HISTO"
decl_stmt|;
specifier|static
specifier|final
name|String
name|BGR_LAG
init|=
literal|"DOCUMENT_NS_BGR_LAG"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGW_CLEAN
init|=
literal|"DOCUMENT_NS_BGW_CLEAN"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGW_SPLIT
init|=
literal|"DOCUMENT_NS_BGW_SPLIT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGW_WRITE
init|=
literal|"DOCUMENT_NS_BGW_LOCK"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGW_SWEEP
init|=
literal|"DOCUMENT_NS_BGW_SWEEP"
decl_stmt|;
specifier|static
specifier|final
name|String
name|BGW_NUM
init|=
literal|"DOCUMENT_NS_BGW_NUM"
decl_stmt|;
specifier|static
specifier|final
name|String
name|BGW_NUM_WRITES_RATE
init|=
literal|"DOCUMENT_NS_BGW_NUM_WRITE_RATE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BGW_TOTAL
init|=
literal|"DOCUMENT_NS_BGW_TOTAL_TIME"
decl_stmt|;
specifier|static
specifier|final
name|String
name|LEASE_UPDATE
init|=
literal|"DOCUMENT_NS_LEASE_UPDATE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_NUM_RETRY
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_RETRY"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_COUNT
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_COUNT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_TIME
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_NORMALIZED_TIME
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_NORMALIZED_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_SUSPENDED
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_SUSPENDED"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUCCESS_EXCLUSIVE
init|=
literal|"DOCUMENT_NS_MERGE_SUCCESS_EXCLUSIVE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MERGE_FAILED_EXCLUSIVE
init|=
literal|"DOCUMENT_NS_MERGE_FAILED_EXCLUSIVE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HEAD_OF_QUEUE_WAIT_TIME
init|=
literal|"DOCUMENT_NS_HEAD_OF_QUEUE_WAIT_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_SUSPEND_TIME
init|=
literal|"DOCUMENT_NS_MERGE_SUSPEND_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_LOCK_TIME
init|=
literal|"DOCUMENT_NS_MERGE_LOCK_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_COMMIT_HOOK_TIME
init|=
literal|"DOCUMENT_NS_MERGE_COMMIT_HOOK_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_CHANGES_APPLIED_TIME
init|=
literal|"DOCUMENT_NS_MERGE_CHANGES_APPLIED_TIME"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MERGE_CHANGES_RATE
init|=
literal|"DOCUMENT_NS_MERGE_CHANGES_RATE"
decl_stmt|;
specifier|static
specifier|final
name|String
name|BRANCH_COMMIT_COUNT
init|=
literal|"DOCUMENT_NS_BRANCH_COMMIT_COUNT"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MERGE_BRANCH_COMMIT_COUNT
init|=
literal|"DOCUMENT_NS_MERGE_BRANCH_COMMIT_COUNT"
decl_stmt|;
comment|// background read
specifier|private
specifier|final
name|TimerStats
name|readHead
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readCacheInvalidate
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readDiffCache
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readLock
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readDispatch
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readTotalTime
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|numChangesRate
decl_stmt|;
specifier|private
specifier|final
name|HistogramStats
name|numChangesHisto
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|changesLag
decl_stmt|;
comment|// background update
specifier|private
specifier|final
name|TimerStats
name|writeClean
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|writeSplit
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|writeSweep
decl_stmt|;
specifier|private
specifier|final
name|HistogramStats
name|writeNum
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|writeWrite
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|writeTotal
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|numWritesRate
decl_stmt|;
comment|// lease update
specifier|private
specifier|final
name|TimerStats
name|leaseUpdate
decl_stmt|;
comment|// merge stats
specifier|private
specifier|final
name|HistogramStats
name|mergeSuccessRetries
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeSuccessRate
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeSuccessTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeSuccessNormalizedTime
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeSuccessExclusive
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeSuccessSuspended
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeFailedExclusive
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|headOfQueueWaitTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeSuspendTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeLockTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeCommitHookTime
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|mergeChangesApplied
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeChangesRate
decl_stmt|;
comment|// branch stats
specifier|private
specifier|final
name|MeterStats
name|branchCommitRate
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|mergeBranchCommitRate
decl_stmt|;
specifier|public
name|DocumentNodeStoreStats
parameter_list|(
name|StatisticsProvider
name|sp
parameter_list|)
block|{
name|readHead
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_READ_HEAD
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|readCacheInvalidate
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_CACHE_INVALIDATE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|readDiffCache
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_DIFF_CACHE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|readLock
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_LOCK
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|readDispatch
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_DISPATCH
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|readTotalTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGR_TOTAL_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|numChangesRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BGR_NUM_CHANGES_RATE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|//Enable time series
name|numChangesHisto
operator|=
name|sp
operator|.
name|getHistogram
argument_list|(
name|BGR_NUM_CHANGES_HISTO
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|changesLag
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BGR_LAG
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeClean
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGW_CLEAN
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeSplit
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGW_SPLIT
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeSweep
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGW_SWEEP
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeWrite
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGW_WRITE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeTotal
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|BGW_TOTAL
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|writeNum
operator|=
name|sp
operator|.
name|getHistogram
argument_list|(
name|BGW_NUM
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|numWritesRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BGW_NUM_WRITES_RATE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|//Enable time series
name|leaseUpdate
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|LEASE_UPDATE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuccessRetries
operator|=
name|sp
operator|.
name|getHistogram
argument_list|(
name|MERGE_SUCCESS_NUM_RETRY
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuccessRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_SUCCESS_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|//Enable time series
name|mergeSuccessTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_SUCCESS_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuccessNormalizedTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_SUCCESS_NORMALIZED_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuccessExclusive
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_SUCCESS_EXCLUSIVE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuccessSuspended
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_SUCCESS_SUSPENDED
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeFailedExclusive
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_FAILED_EXCLUSIVE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|//Enable time series
name|headOfQueueWaitTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|HEAD_OF_QUEUE_WAIT_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeSuspendTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_SUSPEND_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeLockTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_LOCK_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeCommitHookTime
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_COMMIT_HOOK_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeChangesApplied
operator|=
name|sp
operator|.
name|getTimer
argument_list|(
name|MERGE_CHANGES_APPLIED_TIME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|mergeChangesRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_CHANGES_RATE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|branchCommitRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BRANCH_COMMIT_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|mergeBranchCommitRate
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|MERGE_BRANCH_COMMIT_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneBackgroundRead
parameter_list|(
name|BackgroundReadStats
name|stats
parameter_list|)
block|{
name|readHead
operator|.
name|update
argument_list|(
name|stats
operator|.
name|readHead
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readCacheInvalidate
operator|.
name|update
argument_list|(
name|stats
operator|.
name|cacheInvalidationTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readDiffCache
operator|.
name|update
argument_list|(
name|stats
operator|.
name|populateDiffCache
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readLock
operator|.
name|update
argument_list|(
name|stats
operator|.
name|lock
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readDispatch
operator|.
name|update
argument_list|(
name|stats
operator|.
name|dispatchChanges
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|readTotalTime
operator|.
name|update
argument_list|(
name|stats
operator|.
name|totalReadTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|//Record rate of num of external changes pulled per second
name|numChangesRate
operator|.
name|mark
argument_list|(
name|stats
operator|.
name|numExternalChanges
argument_list|)
expr_stmt|;
name|numChangesHisto
operator|.
name|update
argument_list|(
name|stats
operator|.
name|numExternalChanges
argument_list|)
expr_stmt|;
comment|// update lag of external changes
name|changesLag
operator|.
name|mark
argument_list|(
name|stats
operator|.
name|externalChangesLag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneBackgroundUpdate
parameter_list|(
name|BackgroundWriteStats
name|stats
parameter_list|)
block|{
name|writeClean
operator|.
name|update
argument_list|(
name|stats
operator|.
name|clean
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|writeSplit
operator|.
name|update
argument_list|(
name|stats
operator|.
name|split
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|writeSweep
operator|.
name|update
argument_list|(
name|stats
operator|.
name|sweep
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|writeWrite
operator|.
name|update
argument_list|(
name|stats
operator|.
name|write
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|writeTotal
operator|.
name|update
argument_list|(
name|stats
operator|.
name|totalWriteTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|writeNum
operator|.
name|update
argument_list|(
name|stats
operator|.
name|num
argument_list|)
expr_stmt|;
comment|//Record rate of num of bg writes pushed per second
name|numWritesRate
operator|.
name|mark
argument_list|(
name|stats
operator|.
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneLeaseUpdate
parameter_list|(
name|long
name|timeMicros
parameter_list|)
block|{
name|leaseUpdate
operator|.
name|update
argument_list|(
name|timeMicros
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneBranchCommit
parameter_list|()
block|{
name|branchCommitRate
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneMergeBranch
parameter_list|(
name|int
name|numCommits
parameter_list|,
name|int
name|numChanges
parameter_list|)
block|{
name|mergeBranchCommitRate
operator|.
name|mark
argument_list|(
name|numCommits
argument_list|)
expr_stmt|;
name|mergeChangesRate
operator|.
name|mark
argument_list|(
name|numChanges
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneMerge
parameter_list|(
name|int
name|numChanges
parameter_list|,
name|int
name|numRetries
parameter_list|,
name|long
name|timeMillis
parameter_list|,
name|long
name|suspendMillis
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
block|{
name|mergeSuccessRate
operator|.
name|mark
argument_list|()
expr_stmt|;
name|mergeSuccessRetries
operator|.
name|update
argument_list|(
name|numRetries
argument_list|)
expr_stmt|;
name|mergeSuccessTime
operator|.
name|update
argument_list|(
name|timeMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|numChanges
operator|>
literal|0
condition|)
block|{
name|mergeSuccessNormalizedTime
operator|.
name|update
argument_list|(
name|timeMillis
operator|/
name|numChanges
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|mergeChangesRate
operator|.
name|mark
argument_list|(
name|numChanges
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|exclusive
condition|)
block|{
name|mergeSuccessExclusive
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
name|mergeSuspendTime
operator|.
name|update
argument_list|(
name|suspendMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|suspendMillis
operator|>
literal|0
condition|)
block|{
name|mergeSuccessSuspended
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
name|failedMerge
parameter_list|(
name|int
name|numRetries
parameter_list|,
name|long
name|timeMillis
parameter_list|,
name|long
name|suspendMillis
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
block|{
if|if
condition|(
name|exclusive
condition|)
block|{
name|mergeFailedExclusive
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
name|doneWaitUntilHead
parameter_list|(
name|long
name|waitMicros
parameter_list|)
block|{
name|headOfQueueWaitTime
operator|.
name|update
argument_list|(
name|waitMicros
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneMergeLockAcquired
parameter_list|(
name|long
name|timeMicros
parameter_list|)
block|{
name|mergeLockTime
operator|.
name|update
argument_list|(
name|timeMicros
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneCommitHookProcessed
parameter_list|(
name|long
name|timeMicros
parameter_list|)
block|{
name|mergeCommitHookTime
operator|.
name|update
argument_list|(
name|timeMicros
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneChangesApplied
parameter_list|(
name|long
name|timeMicros
parameter_list|)
block|{
name|mergeChangesApplied
operator|.
name|update
argument_list|(
name|timeMicros
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

