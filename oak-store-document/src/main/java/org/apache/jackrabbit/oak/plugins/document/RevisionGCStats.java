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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MICROSECONDS
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
name|DEFAULT
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

begin_comment
comment|/**  * DocumentNodeStore revision garbage collection statistics.  */
end_comment

begin_class
class|class
name|RevisionGCStats
implements|implements
name|RevisionGCStatsCollector
implements|,
name|RevisionGCStatsMBean
block|{
specifier|static
specifier|final
name|String
name|RGC
init|=
literal|"RevisionGC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|READ_DOC
init|=
literal|"READ_DOC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_DOC
init|=
literal|"DELETE_DOC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_LEAF_DOC
init|=
literal|"DELETE_LEAF_DOC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_SPLIT_DOC
init|=
literal|"DELETE_SPLIT_DOC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_INT_SPLIT_DOC
init|=
literal|"DELETE_INT_SPLIT_DOC"
decl_stmt|;
specifier|static
specifier|final
name|String
name|RESET_DELETED_FLAG
init|=
literal|"RESET_DELETED_FLAG"
decl_stmt|;
specifier|static
specifier|final
name|String
name|ACTIVE_TIMER
init|=
literal|"ACTIVE_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|READ_DOC_TIMER
init|=
literal|"READ_DOC_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|CHECK_DELETED_TIMER
init|=
literal|"CHECK_DELETED_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SORT_IDS_TIMER
init|=
literal|"SORT_IDS_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|RESET_DELETED_FLAG_TIMER
init|=
literal|"RESET_DELETED_FLAG_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_DOC_TIMER
init|=
literal|"DELETE_DOC_TIMER"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DELETE_SPLIT_DOC_TIMER
init|=
literal|"DELETE_SPLIT_DOC_TIMER"
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|readDoc
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|deletedDoc
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|deletedLeafDoc
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|deletedSplitDoc
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|deletedIntSplitDoc
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|resetDeletedFlag
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|activeTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|readDocTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|checkDeletedTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|sortIdsTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|resetDeletedFlagTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|deletedDocTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|deletedSplitDocTimer
decl_stmt|;
name|RevisionGCStats
parameter_list|(
name|StatisticsProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|readDoc
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|READ_DOC
argument_list|)
expr_stmt|;
name|deletedDoc
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|DELETE_DOC
argument_list|)
expr_stmt|;
name|deletedLeafDoc
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|DELETE_LEAF_DOC
argument_list|)
expr_stmt|;
name|deletedSplitDoc
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|DELETE_SPLIT_DOC
argument_list|)
expr_stmt|;
name|deletedIntSplitDoc
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|DELETE_INT_SPLIT_DOC
argument_list|)
expr_stmt|;
name|resetDeletedFlag
operator|=
name|meter
argument_list|(
name|provider
argument_list|,
name|RESET_DELETED_FLAG
argument_list|)
expr_stmt|;
name|activeTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|ACTIVE_TIMER
argument_list|)
expr_stmt|;
name|readDocTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|READ_DOC_TIMER
argument_list|)
expr_stmt|;
name|checkDeletedTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|CHECK_DELETED_TIMER
argument_list|)
expr_stmt|;
name|sortIdsTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|SORT_IDS_TIMER
argument_list|)
expr_stmt|;
name|resetDeletedFlagTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|RESET_DELETED_FLAG_TIMER
argument_list|)
expr_stmt|;
name|deletedDocTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|DELETE_DOC_TIMER
argument_list|)
expr_stmt|;
name|deletedSplitDocTimer
operator|=
name|timer
argument_list|(
name|provider
argument_list|,
name|DELETE_SPLIT_DOC_TIMER
argument_list|)
expr_stmt|;
block|}
comment|//---------------------< RevisionGCStatsCollector>-------------------------
annotation|@
name|Override
specifier|public
name|void
name|documentRead
parameter_list|()
block|{
name|readDoc
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentsDeleted
parameter_list|(
name|long
name|numDocs
parameter_list|)
block|{
name|deletedDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leafDocumentsDeleted
parameter_list|(
name|long
name|numDocs
parameter_list|)
block|{
name|deletedDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|deletedLeafDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|splitDocumentsDeleted
parameter_list|(
name|long
name|numDocs
parameter_list|)
block|{
name|deletedDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|deletedSplitDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|intermediateSplitDocumentsDeleted
parameter_list|(
name|long
name|numDocs
parameter_list|)
block|{
name|deletedIntSplitDoc
operator|.
name|mark
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deletedOnceFlagReset
parameter_list|()
block|{
name|resetDeletedFlag
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finished
parameter_list|(
name|VersionGCStats
name|stats
parameter_list|)
block|{
name|activeTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|active
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|readDocTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|collectDeletedDocsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|checkDeletedTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|checkDeletedDocsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|deletedDocTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|deleteDeletedDocsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|deletedSplitDocTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|collectAndDeleteSplitDocsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|sortIdsTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|sortDocIdsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
name|resetDeletedFlagTimer
operator|.
name|update
argument_list|(
name|stats
operator|.
name|updateResurrectedDocumentsElapsed
argument_list|,
name|MICROSECONDS
argument_list|)
expr_stmt|;
block|}
comment|//------------------------< RevisionGCStatsMBean>--------------------------
annotation|@
name|Override
specifier|public
name|long
name|getReadDocCount
parameter_list|()
block|{
return|return
name|readDoc
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedDocCount
parameter_list|()
block|{
return|return
name|deletedDoc
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedLeafDocCount
parameter_list|()
block|{
return|return
name|deletedLeafDoc
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedSplitDocCount
parameter_list|()
block|{
return|return
name|deletedSplitDoc
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedIntSplitDocCount
parameter_list|()
block|{
return|return
name|deletedIntSplitDoc
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getResetDeletedFlagCount
parameter_list|()
block|{
return|return
name|resetDeletedFlag
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getReadDocHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|READ_DOC
argument_list|,
literal|"Documents read by RevisionGC"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDeletedDocHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|DELETE_DOC
argument_list|,
literal|"Documents deleted by RevisionGC"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDeletedLeafDocHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|DELETE_LEAF_DOC
argument_list|,
literal|"Leaf documents deleted by RevisionGC"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDeletedSplitDocHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|DELETE_SPLIT_DOC
argument_list|,
literal|"Split documents deleted by RevisionGC"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDeletedIntSplitDocHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|DELETE_INT_SPLIT_DOC
argument_list|,
literal|"Intermediate split documents deleted by RevisionGC"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getResetDeletedFlagHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|RESET_DELETED_FLAG
argument_list|,
literal|"Deleted once flags reset by RevisionGC"
argument_list|)
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
specifier|static
name|MeterStats
name|meter
parameter_list|(
name|StatisticsProvider
name|provider
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|provider
operator|.
name|getMeter
argument_list|(
name|qualifiedName
argument_list|(
name|name
argument_list|)
argument_list|,
name|DEFAULT
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|TimerStats
name|timer
parameter_list|(
name|StatisticsProvider
name|provider
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|provider
operator|.
name|getTimer
argument_list|(
name|qualifiedName
argument_list|(
name|name
argument_list|)
argument_list|,
name|METRICS_ONLY
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|qualifiedName
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
return|return
name|RGC
operator|+
literal|"."
operator|+
name|metricName
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
name|provider
operator|.
name|getStats
argument_list|()
operator|.
name|getTimeSeries
argument_list|(
name|qualifiedName
argument_list|(
name|name
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit
