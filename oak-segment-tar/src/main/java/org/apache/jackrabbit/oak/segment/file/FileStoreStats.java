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
name|segment
operator|.
name|file
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
name|stats
operator|.
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
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
name|commons
operator|.
name|IOUtils
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
name|spi
operator|.
name|monitor
operator|.
name|FileStoreMonitor
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
name|CounterStats
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

begin_class
specifier|public
class|class
name|FileStoreStats
implements|implements
name|FileStoreStatsMBean
implements|,
name|FileStoreMonitor
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SEGMENT_REPO_SIZE
init|=
literal|"SEGMENT_REPO_SIZE"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SEGMENT_WRITES
init|=
literal|"SEGMENT_WRITES"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOURNAL_WRITES
init|=
literal|"JOURNAL_WRITES"
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|FileStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|writeStats
decl_stmt|;
specifier|private
specifier|final
name|CounterStats
name|repoSize
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|journalWriteStats
decl_stmt|;
specifier|public
name|FileStoreStats
parameter_list|(
name|StatisticsProvider
name|statisticsProvider
parameter_list|,
name|FileStore
name|store
parameter_list|,
name|long
name|initialSize
parameter_list|)
block|{
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|writeStats
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
name|SEGMENT_WRITES
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|repoSize
operator|=
name|statisticsProvider
operator|.
name|getCounterStats
argument_list|(
name|SEGMENT_REPO_SIZE
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|journalWriteStats
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
name|JOURNAL_WRITES
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|repoSize
operator|.
name|inc
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|long
name|initialSize
parameter_list|)
block|{
name|repoSize
operator|.
name|inc
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
comment|//~-----------------------------< FileStoreMonitor>
annotation|@
name|Override
specifier|public
name|void
name|written
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|writeStats
operator|.
name|mark
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|repoSize
operator|.
name|inc
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reclaimed
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|repoSize
operator|.
name|dec
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flushed
parameter_list|()
block|{
name|journalWriteStats
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
comment|//~--------------------------------< FileStoreStatsMBean>
annotation|@
name|Override
specifier|public
name|long
name|getApproximateSize
parameter_list|()
block|{
return|return
name|repoSize
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTarFileCount
parameter_list|()
block|{
return|return
name|store
operator|.
name|readerCount
argument_list|()
operator|+
literal|1
return|;
comment|//1 for the writer
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|store
operator|.
name|getSegmentCount
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getWriteStats
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|SEGMENT_WRITES
argument_list|)
argument_list|,
name|SEGMENT_WRITES
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getRepositorySize
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|SEGMENT_REPO_SIZE
argument_list|)
argument_list|,
name|SEGMENT_REPO_SIZE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|fileStoreInfoAsString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Segment store size : %s%n"
operator|+
literal|"Number of tar files : %d"
argument_list|,
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|getApproximateSize
argument_list|()
argument_list|)
argument_list|,
name|getTarFileCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getJournalWriteStatsAsCount
parameter_list|()
block|{
return|return
name|journalWriteStats
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getJournalWriteStatsAsCompositeData
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|JOURNAL_WRITES
argument_list|)
argument_list|,
name|JOURNAL_WRITES
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

