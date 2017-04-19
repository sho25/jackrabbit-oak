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
name|stats
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|OBSERVATION_EVENT_AVERAGE
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|OBSERVATION_EVENT_COUNTER
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|OBSERVATION_EVENT_DURATION
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|QUERY_AVERAGE
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|QUERY_COUNT
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|QUERY_DURATION
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_COUNT
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_LOGIN_COUNTER
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_READ_AVERAGE
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_READ_COUNTER
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_READ_DURATION
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_WRITE_AVERAGE
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_WRITE_COUNTER
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
operator|.
name|SESSION_WRITE_DURATION
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
name|RepositoryStatistics
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryStatsMBean
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
name|stats
operator|.
name|TimeSeriesStatsUtil
import|;
end_import

begin_class
specifier|public
class|class
name|RepositoryStats
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|RepositoryStatsMBean
block|{
specifier|private
specifier|final
name|RepositoryStatistics
name|repoStats
decl_stmt|;
specifier|private
specifier|final
name|TimeSeries
name|maxQueueLength
decl_stmt|;
specifier|public
name|RepositoryStats
parameter_list|(
name|RepositoryStatistics
name|repoStats
parameter_list|,
name|TimeSeries
name|maxQueueLength
parameter_list|)
block|{
name|super
argument_list|(
name|RepositoryStatsMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|repoStats
operator|=
name|repoStats
expr_stmt|;
name|this
operator|.
name|maxQueueLength
operator|=
name|maxQueueLength
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionLogin
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_LOGIN_COUNTER
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionReadCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_READ_COUNTER
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionReadDuration
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_READ_DURATION
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionReadAverage
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_READ_AVERAGE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionWriteCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_WRITE_COUNTER
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionWriteDuration
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_WRITE_DURATION
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getSessionWriteAverage
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|SESSION_WRITE_AVERAGE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|QUERY_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryDuration
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|QUERY_DURATION
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getQueryAverage
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|QUERY_AVERAGE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getObservationEventCount
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|OBSERVATION_EVENT_COUNTER
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getObservationEventDuration
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|OBSERVATION_EVENT_DURATION
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getObservationEventAverage
parameter_list|()
block|{
return|return
name|asCompositeData
argument_list|(
name|OBSERVATION_EVENT_AVERAGE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getObservationQueueMaxLength
parameter_list|()
block|{
return|return
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
argument_list|(
name|maxQueueLength
argument_list|,
literal|"maximal length of observation queue"
argument_list|)
return|;
block|}
specifier|private
name|TimeSeries
name|getTimeSeries
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|repoStats
operator|.
name|getTimeSeries
argument_list|(
name|type
argument_list|)
return|;
block|}
specifier|private
name|CompositeData
name|asCompositeData
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
name|TimeSeriesStatsUtil
operator|.
name|asCompositeData
argument_list|(
name|getTimeSeries
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
