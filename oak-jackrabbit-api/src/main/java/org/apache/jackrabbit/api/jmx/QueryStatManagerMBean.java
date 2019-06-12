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
name|api
operator|.
name|stats
operator|.
name|QueryStat
import|;
end_import

begin_comment
comment|/**  * JMX Bindings for {@link QueryStat}.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryStatManagerMBean
block|{
name|String
name|NAME
init|=
literal|"org.apache.jackrabbit:type=QueryStats"
decl_stmt|;
comment|/**      * @return a sorted array containing the top      *         {@link #getSlowQueriesQueueSize()} slowest queries      */
name|TabularData
name|getSlowQueries
parameter_list|()
function_decl|;
comment|/**      * @return a sorted array containing the      *         {@link #getPopularQueriesQueueSize()} most popular queries      */
name|TabularData
name|getPopularQueries
parameter_list|()
function_decl|;
comment|/**      * @return size of the<b>Slow</b> queue      */
name|int
name|getSlowQueriesQueueSize
parameter_list|()
function_decl|;
comment|/**      * Change the size of the<b>Slow</b> queue      *       * @param size      *            the new size      */
name|void
name|setSlowQueriesQueueSize
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
comment|/**      * clears the<b>Slow</b> queue      */
name|void
name|clearSlowQueriesQueue
parameter_list|()
function_decl|;
comment|/**      * @return size of the<b>Popular</b> queue      */
name|int
name|getPopularQueriesQueueSize
parameter_list|()
function_decl|;
comment|/**      * Change the size of the<b>Popular</b> queue      *       * @param size      *            the new size      */
name|void
name|setPopularQueriesQueueSize
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
comment|/**      * clears the<b>Popular</b> queue      */
name|void
name|clearPopularQueriesQueue
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

