begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|query
operator|.
name|stats
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
name|TabularData
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
name|Description
import|;
end_import

begin_interface
specifier|public
interface|interface
name|QueryStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"QueryStats"
decl_stmt|;
comment|/**      * Get the slow queries. Those are the ones that scan more than 100'000      * nodes, or the configured maximum number of nodes to scan. (Raw execution      * time is not taken into account, as execution can be slow if the code is      * not compiled yet.)      *       * @return the slow queries table      */
annotation|@
name|Description
argument_list|(
literal|"Get the slow queries (those that scan/traverse over many nodes)."
argument_list|)
name|TabularData
name|getSlowQueries
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Get the popular queries (those that take most of the time)."
argument_list|)
name|TabularData
name|getPopularQueries
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Get all data as Json."
argument_list|)
name|String
name|asJson
parameter_list|()
function_decl|;
annotation|@
name|Description
argument_list|(
literal|"Reset the statistics (clear the list of queries)."
argument_list|)
name|void
name|resetStats
parameter_list|()
function_decl|;
comment|/**      * Whether to capture a thread dump in addition to the thread name.      * No thread name / thread dump is captures for internal queries.      *       * @param captureStackTraces the new valu      */
annotation|@
name|Description
argument_list|(
literal|"Enable / disable capturing the thread dumps (in addition to the thread name)."
argument_list|)
name|void
name|setCaptureStackTraces
parameter_list|(
name|boolean
name|captureStackTraces
parameter_list|)
function_decl|;
name|boolean
name|getCaptureStackTraces
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

