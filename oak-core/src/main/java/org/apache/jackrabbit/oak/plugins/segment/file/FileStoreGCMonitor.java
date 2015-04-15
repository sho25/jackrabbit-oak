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
name|segment
operator|.
name|file
package|;
end_package

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
name|java
operator|.
name|text
operator|.
name|DateFormat
operator|.
name|getDateTimeInstance
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
name|stats
operator|.
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|slf4j
operator|.
name|helpers
operator|.
name|MessageFormatter
operator|.
name|arrayFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|gc
operator|.
name|GCMonitor
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
name|Clock
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
name|TimeSeriesRecorder
import|;
end_import

begin_comment
comment|/**  * {@link GCMonitor} implementation providing the file store gc status  * as {@link GCMonitorMBean}.  *<p>  * Users of this class need to schedule a call to {@link #run()} once per  * second to ensure the various time series maintained by this implementation  * are correctly aggregated.  */
end_comment

begin_class
specifier|public
class|class
name|FileStoreGCMonitor
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|GCMonitor
implements|,
name|GCMonitorMBean
implements|,
name|Runnable
block|{
specifier|private
specifier|final
name|TimeSeriesRecorder
name|gcCount
init|=
operator|new
name|TimeSeriesRecorder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TimeSeriesRecorder
name|repositorySize
init|=
operator|new
name|TimeSeriesRecorder
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TimeSeriesRecorder
name|reclaimedSize
init|=
operator|new
name|TimeSeriesRecorder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
name|long
name|lastCompaction
decl_stmt|;
specifier|private
name|long
name|lastCleanup
decl_stmt|;
specifier|private
name|String
name|lastError
decl_stmt|;
specifier|private
name|String
name|status
init|=
literal|"NA"
decl_stmt|;
specifier|public
name|FileStoreGCMonitor
parameter_list|(
annotation|@
name|Nonnull
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|GCMonitorMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|checkNotNull
argument_list|(
name|clock
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Runnable>---
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|gcCount
operator|.
name|recordOneSecond
argument_list|()
expr_stmt|;
name|repositorySize
operator|.
name|recordOneSecond
argument_list|()
expr_stmt|;
name|reclaimedSize
operator|.
name|recordOneSecond
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------------< GCMonitor>---
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|status
operator|=
name|arrayFormat
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|status
operator|=
name|arrayFormat
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|sw
operator|.
name|write
argument_list|(
name|message
operator|+
literal|": "
argument_list|)
expr_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|lastError
operator|=
name|sw
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|status
operator|=
name|arrayFormat
argument_list|(
name|reason
argument_list|,
name|arguments
argument_list|)
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|()
block|{
name|lastCompaction
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimed
parameter_list|,
name|long
name|current
parameter_list|)
block|{
name|lastCleanup
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|gcCount
operator|.
name|getCounter
argument_list|()
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|repositorySize
operator|.
name|getCounter
argument_list|()
operator|.
name|set
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|reclaimedSize
operator|.
name|getCounter
argument_list|()
operator|.
name|addAndGet
argument_list|(
name|reclaimed
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< GCMonitorMBean>---
annotation|@
name|Override
specifier|public
name|String
name|getLastCompaction
parameter_list|()
block|{
return|return
name|toString
argument_list|(
name|lastCompaction
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastCleanup
parameter_list|()
block|{
return|return
name|toString
argument_list|(
name|lastCleanup
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
if|if
condition|(
name|timestamp
operator|!=
literal|0
condition|)
block|{
return|return
name|getDateTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastError
parameter_list|()
block|{
return|return
name|lastError
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
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
name|repositorySize
argument_list|,
literal|"RepositorySize"
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getReclaimedSize
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|reclaimedSize
argument_list|,
literal|"ReclaimedSize"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

