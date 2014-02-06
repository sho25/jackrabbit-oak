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
name|api
operator|.
name|jmx
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

begin_comment
comment|/**  * MBean for providing repository wide statistics.  * This MBean exposes the time series provided by  * {@link org.apache.jackrabbit.api.stats.RepositoryStatistics RepositoryStatistics}  * through JMX as {@code CompositeData} of arrays.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RepositoryStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"RepositoryStats"
decl_stmt|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_COUNT      */
name|CompositeData
name|getSessionCount
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_LOGIN_COUNTER      */
name|CompositeData
name|getSessionLogin
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_READ_COUNTER      */
name|CompositeData
name|getSessionReadCount
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_READ_DURATION      */
name|CompositeData
name|getSessionReadDuration
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_READ_AVERAGE      */
name|CompositeData
name|getSessionReadAverage
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_WRITE_COUNTER      */
name|CompositeData
name|getSessionWriteCount
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_READ_DURATION      */
name|CompositeData
name|getSessionWriteDuration
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#SESSION_WRITE_AVERAGE      */
name|CompositeData
name|getSessionWriteAverage
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#QUERY_COUNT      */
name|CompositeData
name|getQueryCount
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#QUERY_DURATION      */
name|CompositeData
name|getQueryDuration
parameter_list|()
function_decl|;
comment|/**      * @see org.apache.jackrabbit.api.stats.RepositoryStatistics.Type#QUERY_AVERAGE      */
name|CompositeData
name|getQueryAverage
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

