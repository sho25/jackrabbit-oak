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
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ProviderType
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

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|StatisticsProvider
block|{
name|StatisticsProvider
name|NOOP
init|=
operator|new
name|StatisticsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RepositoryStatistics
name|getStats
parameter_list|()
block|{
return|return
name|NoopRepositoryStatistics
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|MeterStats
name|getMeter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NoopStats
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|CounterStats
name|getCounterStats
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NoopStats
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|TimerStats
name|getTimer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NoopStats
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|HistogramStats
name|getHistogram
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NoopStats
operator|.
name|INSTANCE
return|;
block|}
block|}
decl_stmt|;
name|RepositoryStatistics
name|getStats
parameter_list|()
function_decl|;
name|MeterStats
name|getMeter
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|CounterStats
name|getCounterStats
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|TimerStats
name|getTimer
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|HistogramStats
name|getHistogram
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

