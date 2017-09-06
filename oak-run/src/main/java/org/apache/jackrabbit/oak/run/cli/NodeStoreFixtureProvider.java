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
name|run
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Map
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
name|ScheduledThreadPoolExecutor
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
name|ConsoleReporter
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
name|Counting
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
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
name|metric
operator|.
name|MetricStatisticsProvider
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
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|BlobStore
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
name|state
operator|.
name|NodeStore
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Tracker
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
name|whiteboard
operator|.
name|Whiteboard
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
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStoreFixtureProvider
block|{
specifier|public
specifier|static
name|NodeStoreFixture
name|create
parameter_list|(
name|Options
name|options
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
name|options
argument_list|,
operator|!
name|options
operator|.
name|getOptionBean
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
operator|.
name|isReadWrite
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeStoreFixture
name|create
parameter_list|(
name|Options
name|options
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|Exception
block|{
name|CommonOptions
name|commonOpts
init|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
decl_stmt|;
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|Whiteboard
name|wb
init|=
operator|new
name|ClosingWhiteboard
argument_list|(
name|options
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|BlobStoreFixture
name|blobFixture
init|=
name|BlobStoreFixtureProvider
operator|.
name|create
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|blobFixture
operator|!=
literal|null
condition|)
block|{
name|blobStore
operator|=
name|blobFixture
operator|.
name|getBlobStore
argument_list|()
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|blobFixture
argument_list|)
expr_stmt|;
block|}
name|StatisticsProvider
name|statisticsProvider
init|=
name|createStatsProvider
argument_list|(
name|options
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|wb
operator|.
name|register
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|statisticsProvider
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|NodeStore
name|store
decl_stmt|;
if|if
condition|(
name|commonOpts
operator|.
name|isMongo
argument_list|()
operator|||
name|commonOpts
operator|.
name|isRDB
argument_list|()
condition|)
block|{
name|store
operator|=
name|DocumentFixtureProvider
operator|.
name|configureDocumentMk
argument_list|(
name|options
argument_list|,
name|blobStore
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|,
name|readOnly
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commonOpts
operator|.
name|isOldSegment
argument_list|()
condition|)
block|{
name|store
operator|=
name|SegmentFixtureProvider
operator|.
name|create
argument_list|(
name|options
argument_list|,
name|blobStore
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|,
name|readOnly
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|store
operator|=
name|SegmentTarFixtureProvider
operator|.
name|configureSegment
argument_list|(
name|options
argument_list|,
name|blobStore
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|,
name|readOnly
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|oldSegmentStore
argument_list|(
name|options
argument_list|)
condition|)
block|{
name|store
operator|=
name|SegmentFixtureProvider
operator|.
name|create
argument_list|(
name|options
argument_list|,
name|blobStore
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|,
name|readOnly
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
return|return
operator|new
name|SimpleNodeStoreFixture
argument_list|(
name|store
argument_list|,
name|blobStore
argument_list|,
name|wb
argument_list|,
name|closer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|oldSegmentStore
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|String
name|path
init|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
operator|.
name|getStoreArg
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// manifest file was introduced with oak-segment-tar
name|File
name|manifest
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"manifest"
argument_list|)
decl_stmt|;
return|return
operator|!
name|manifest
operator|.
name|exists
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|StatisticsProvider
name|createStatsProvider
parameter_list|(
name|Options
name|options
parameter_list|,
name|Whiteboard
name|wb
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isMetricsEnabled
argument_list|()
condition|)
block|{
name|ScheduledExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|getExitingScheduledExecutorService
argument_list|(
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|MetricStatisticsProvider
name|statsProvider
init|=
operator|new
name|MetricStatisticsProvider
argument_list|(
name|getPlatformMBeanServer
argument_list|()
argument_list|,
name|executorService
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|statsProvider
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
parameter_list|()
lambda|->
name|reportMetrics
argument_list|(
name|statsProvider
argument_list|)
argument_list|)
expr_stmt|;
name|wb
operator|.
name|register
argument_list|(
name|MetricRegistry
operator|.
name|class
argument_list|,
name|statsProvider
operator|.
name|getRegistry
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|statsProvider
return|;
block|}
return|return
name|StatisticsProvider
operator|.
name|NOOP
return|;
block|}
specifier|private
specifier|static
name|void
name|reportMetrics
parameter_list|(
name|MetricStatisticsProvider
name|statsProvider
parameter_list|)
block|{
name|MetricRegistry
name|metricRegistry
init|=
name|statsProvider
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|ConsoleReporter
operator|.
name|forRegistry
argument_list|(
name|metricRegistry
argument_list|)
operator|.
name|outputTo
argument_list|(
name|System
operator|.
name|out
argument_list|)
operator|.
name|filter
argument_list|(
parameter_list|(
name|name
parameter_list|,
name|metric
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|metric
operator|instanceof
name|Counting
condition|)
block|{
comment|//Only report non zero metrics
return|return
operator|(
operator|(
name|Counting
operator|)
name|metric
operator|)
operator|.
name|getCount
argument_list|()
operator|>
literal|0
return|;
block|}
return|return
literal|true
return|;
block|}
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|report
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|SimpleNodeStoreFixture
implements|implements
name|NodeStoreFixture
block|{
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|SimpleNodeStoreFixture
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
name|nodeStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|Whiteboard
name|getWhiteboard
parameter_list|()
block|{
return|return
name|whiteboard
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ClosingWhiteboard
implements|implements
name|Whiteboard
block|{
specifier|private
specifier|final
name|Whiteboard
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|public
name|ClosingWhiteboard
parameter_list|(
name|Whiteboard
name|delegate
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Registration
name|register
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|service
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
parameter_list|)
block|{
name|Registration
name|reg
init|=
name|delegate
operator|.
name|register
argument_list|(
name|type
argument_list|,
name|service
argument_list|,
name|properties
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|reg
operator|::
name|unregister
argument_list|)
expr_stmt|;
return|return
name|reg
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Tracker
argument_list|<
name|T
argument_list|>
name|track
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|track
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Tracker
argument_list|<
name|T
argument_list|>
name|track
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filterProperties
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|track
argument_list|(
name|type
argument_list|,
name|filterProperties
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

