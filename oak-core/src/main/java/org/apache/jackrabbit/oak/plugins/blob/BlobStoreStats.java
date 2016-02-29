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
name|blob
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
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|spi
operator|.
name|blob
operator|.
name|stats
operator|.
name|BlobStoreStatsMBean
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
name|spi
operator|.
name|blob
operator|.
name|stats
operator|.
name|BlobStatsCollector
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
name|stats
operator|.
name|TimeSeriesAverage
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Duplicates"
argument_list|)
specifier|public
class|class
name|BlobStoreStats
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|BlobStoreStatsMBean
implements|,
name|BlobStatsCollector
block|{
specifier|private
specifier|final
name|Logger
name|opsLogger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.operations.blobs"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_DOWNLOAD_COUNT
init|=
literal|"BLOB_DOWNLOAD_COUNT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_UPLOADS
init|=
literal|"BLOB_UPLOADS"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_DOWNLOADS
init|=
literal|"BLOB_DOWNLOADS"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_UPLOAD_COUNT
init|=
literal|"BLOB_UPLOAD_COUNT"
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|HistogramStats
name|uploadHisto
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|uploadCount
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|uploadSizeSeries
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|uploadTimeSeries
decl_stmt|;
specifier|private
specifier|final
name|TimeSeries
name|uploadRateSeries
decl_stmt|;
specifier|private
specifier|final
name|HistogramStats
name|downloadHisto
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|downloadCount
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|downloadSizeSeries
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|downloadTimeSeries
decl_stmt|;
specifier|private
specifier|final
name|TimeSeries
name|downloadRateSeries
decl_stmt|;
specifier|private
specifier|final
name|TimeUnit
name|recordedTimeUnit
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
decl_stmt|;
specifier|public
name|BlobStoreStats
parameter_list|(
annotation|@
name|Nonnull
name|StatisticsProvider
name|sp
parameter_list|)
block|{
name|super
argument_list|(
name|BlobStoreStatsMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|statisticsProvider
operator|=
name|checkNotNull
argument_list|(
name|sp
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploadHisto
operator|=
name|sp
operator|.
name|getHistogram
argument_list|(
name|BLOB_UPLOADS
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploadCount
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BLOB_UPLOAD_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploadSizeSeries
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"BLOB_UPLOAD_SIZE"
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploadTimeSeries
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"BLOB_UPLOAD_TIME"
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploadRateSeries
operator|=
name|getAvgTimeSeries
argument_list|(
literal|"BLOB_UPLOAD_SIZE"
argument_list|,
literal|"BLOB_UPLOAD_TIME"
argument_list|)
expr_stmt|;
name|this
operator|.
name|downloadHisto
operator|=
name|sp
operator|.
name|getHistogram
argument_list|(
name|BLOB_DOWNLOADS
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|downloadCount
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
name|BLOB_DOWNLOAD_COUNT
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|downloadSizeSeries
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"BLOB_DOWNLOAD_SIZE"
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
expr_stmt|;
name|this
operator|.
name|downloadTimeSeries
operator|=
name|sp
operator|.
name|getMeter
argument_list|(
literal|"BLOB_DOWNLOAD_TIME"
argument_list|,
name|StatsOptions
operator|.
name|TIME_SERIES_ONLY
argument_list|)
expr_stmt|;
name|this
operator|.
name|downloadRateSeries
operator|=
name|getAvgTimeSeries
argument_list|(
literal|"BLOB_DOWNLOAD_SIZE"
argument_list|,
literal|"BLOB_DOWNLOAD_TIME"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|uploaded
parameter_list|(
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|uploadHisto
operator|.
name|update
argument_list|(
name|size
argument_list|)
expr_stmt|;
comment|//Recording upload like this is not accurate. A more accurate way
comment|//would be to mark as upload or download is progressing.
comment|//That would however add quite a bit of overhead
comment|//Approach below would record an upload/download at moment when
comment|//it got completed. So acts like a rough approximation
name|uploadSizeSeries
operator|.
name|mark
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|uploadTimeSeries
operator|.
name|mark
argument_list|(
name|recordedTimeUnit
operator|.
name|convert
argument_list|(
name|timeTaken
argument_list|,
name|unit
argument_list|)
argument_list|)
expr_stmt|;
name|opsLogger
operator|.
name|debug
argument_list|(
literal|"Uploaded {} bytes in {} ms"
argument_list|,
name|size
argument_list|,
name|unit
operator|.
name|toMillis
argument_list|(
name|timeTaken
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|downloaded
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|downloadHisto
operator|.
name|update
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|downloadSizeSeries
operator|.
name|mark
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|downloadTimeSeries
operator|.
name|mark
argument_list|(
name|recordedTimeUnit
operator|.
name|convert
argument_list|(
name|timeTaken
argument_list|,
name|unit
argument_list|)
argument_list|)
expr_stmt|;
name|opsLogger
operator|.
name|debug
argument_list|(
literal|"Downloaded {} - {} bytes in {} ms"
argument_list|,
name|blobId
argument_list|,
name|size
argument_list|,
name|unit
operator|.
name|toMillis
argument_list|(
name|timeTaken
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|uploadCompleted
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
name|uploadCount
operator|.
name|mark
argument_list|()
expr_stmt|;
name|opsLogger
operator|.
name|debug
argument_list|(
literal|"Upload completed - {}"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|downloadCompleted
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
name|downloadCount
operator|.
name|mark
argument_list|()
expr_stmt|;
name|opsLogger
operator|.
name|debug
argument_list|(
literal|"Download completed - {}"
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
comment|//~--------------------------------------< BlobStoreMBean>
annotation|@
name|Override
specifier|public
name|long
name|getUploadTotalSize
parameter_list|()
block|{
return|return
name|uploadSizeSeries
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getUploadCount
parameter_list|()
block|{
return|return
name|uploadCount
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getUploadTotalSeconds
parameter_list|()
block|{
return|return
name|recordedTimeUnit
operator|.
name|toSeconds
argument_list|(
name|uploadTimeSeries
operator|.
name|getCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDownloadTotalSize
parameter_list|()
block|{
return|return
name|downloadSizeSeries
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDownloadCount
parameter_list|()
block|{
return|return
name|downloadCount
operator|.
name|getCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDownloadTotalSeconds
parameter_list|()
block|{
return|return
name|recordedTimeUnit
operator|.
name|toSeconds
argument_list|(
name|downloadTimeSeries
operator|.
name|getCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|blobStoreInfoAsString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Uploads - size = %s, count = %d%nDownloads - size = %s, count = %d"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|getUploadTotalSize
argument_list|()
argument_list|)
argument_list|,
name|getUploadCount
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
name|getDownloadTotalSize
argument_list|()
argument_list|)
argument_list|,
name|getDownloadCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUploadSizeHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|BLOB_UPLOADS
argument_list|,
literal|"Blob Uploads (bytes)"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDownloadSizeHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|BLOB_DOWNLOADS
argument_list|,
literal|"Blob Downloads (bytes)"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUploadRateHistory
parameter_list|()
block|{
return|return
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
argument_list|(
name|uploadRateSeries
argument_list|,
literal|"Blob uploads bytes/secs"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDownloadRateHistory
parameter_list|()
block|{
return|return
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
argument_list|(
name|downloadRateSeries
argument_list|,
literal|"Blob downloads bytes/secs"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getUploadCountHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|BLOB_UPLOAD_COUNT
argument_list|,
literal|"Blob Upload Counts"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getDownloadCountHistory
parameter_list|()
block|{
return|return
name|getTimeSeriesData
argument_list|(
name|BLOB_DOWNLOAD_COUNT
argument_list|,
literal|"Blob Download Counts"
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
specifier|private
name|TimeSeries
name|getAvgTimeSeries
parameter_list|(
name|String
name|nameValue
parameter_list|,
name|String
name|nameCounter
parameter_list|)
block|{
return|return
operator|new
name|TimeSeriesAverage
argument_list|(
name|getTimeSeries
argument_list|(
name|nameValue
argument_list|)
argument_list|,
operator|new
name|UnitConvertingTimeSeries
argument_list|(
name|getTimeSeries
argument_list|(
name|nameCounter
argument_list|)
argument_list|,
name|recordedTimeUnit
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * TimeSeries which converts a Nanonsecond based time to Seconds for      * calculating bytes/sec rate for upload and download      */
specifier|private
specifier|static
class|class
name|UnitConvertingTimeSeries
implements|implements
name|TimeSeries
block|{
specifier|private
specifier|final
name|TimeSeries
name|series
decl_stmt|;
specifier|private
specifier|final
name|TimeUnit
name|source
decl_stmt|;
specifier|private
specifier|final
name|TimeUnit
name|dest
decl_stmt|;
specifier|public
name|UnitConvertingTimeSeries
parameter_list|(
name|TimeSeries
name|series
parameter_list|,
name|TimeUnit
name|source
parameter_list|,
name|TimeUnit
name|dest
parameter_list|)
block|{
name|this
operator|.
name|series
operator|=
name|series
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
index|[]
name|getValuePerSecond
parameter_list|()
block|{
return|return
name|convert
argument_list|(
name|series
operator|.
name|getValuePerSecond
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
index|[]
name|getValuePerMinute
parameter_list|()
block|{
return|return
name|convert
argument_list|(
name|series
operator|.
name|getValuePerMinute
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
index|[]
name|getValuePerHour
parameter_list|()
block|{
return|return
name|convert
argument_list|(
name|series
operator|.
name|getValuePerHour
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
index|[]
name|getValuePerWeek
parameter_list|()
block|{
return|return
name|convert
argument_list|(
name|series
operator|.
name|getValuePerWeek
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMissingValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|private
name|long
index|[]
name|convert
parameter_list|(
name|long
index|[]
name|timings
parameter_list|)
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
name|timings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|timings
index|[
name|i
index|]
operator|=
name|dest
operator|.
name|convert
argument_list|(
name|timings
index|[
name|i
index|]
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
return|return
name|timings
return|;
block|}
block|}
block|}
end_class

end_unit

