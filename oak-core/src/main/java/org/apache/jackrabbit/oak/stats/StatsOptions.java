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

begin_class
specifier|public
specifier|final
class|class
name|StatsOptions
block|{
comment|/**      * Default mode where both TimeSeries data and other statistical data      * would be collected      */
specifier|public
specifier|static
specifier|final
name|StatsOptions
name|DEFAULT
init|=
operator|new
name|StatsOptions
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**      * In this mode only TimeSeries data would be collected.      */
specifier|public
specifier|static
specifier|final
name|StatsOptions
name|TIME_SERIES_ONLY
init|=
operator|new
name|StatsOptions
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**      * In this mode only statistical data would be collected.      */
specifier|public
specifier|static
specifier|final
name|StatsOptions
name|METRICS_ONLY
init|=
operator|new
name|StatsOptions
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|timeSeriesEnabled
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|metricsEnabled
decl_stmt|;
specifier|private
name|StatsOptions
parameter_list|(
name|boolean
name|timeSeriesEnabled
parameter_list|,
name|boolean
name|metricsEnabled
parameter_list|)
block|{
name|this
operator|.
name|metricsEnabled
operator|=
name|metricsEnabled
expr_stmt|;
name|this
operator|.
name|timeSeriesEnabled
operator|=
name|timeSeriesEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTimeSeriesEnabled
parameter_list|()
block|{
return|return
name|timeSeriesEnabled
return|;
block|}
specifier|public
name|boolean
name|isMetricsEnabled
parameter_list|()
block|{
return|return
name|metricsEnabled
return|;
block|}
specifier|public
name|boolean
name|isOnlyMetricEnabled
parameter_list|()
block|{
return|return
operator|!
name|timeSeriesEnabled
operator|&&
name|metricsEnabled
return|;
block|}
specifier|public
name|boolean
name|isOnlyTimeSeriesEnabled
parameter_list|()
block|{
return|return
name|timeSeriesEnabled
operator|&&
operator|!
name|metricsEnabled
return|;
block|}
block|}
end_class

end_unit

