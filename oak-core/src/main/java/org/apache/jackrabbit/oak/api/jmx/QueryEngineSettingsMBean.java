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
name|api
operator|.
name|jmx
package|;
end_package

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
name|Description
import|;
end_import

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

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|QueryEngineSettingsMBean
block|{
name|String
name|TYPE
init|=
literal|"QueryEngineSettings"
decl_stmt|;
comment|/**      * Get the limit on how many nodes a query may read at most into memory, for      * "order by" and "distinct" queries. If this limit is exceeded, the query      * throws an exception.      *       * @return the limit      */
annotation|@
name|Description
argument_list|(
literal|"Get the limit on how many nodes a query may read at most into memory, for "
operator|+
literal|"\"order by\" and \"distinct\" queries. If this limit is exceeded, the query throws an exception."
argument_list|)
name|long
name|getLimitInMemory
parameter_list|()
function_decl|;
comment|/**      * Change the limit.      *       * @param limitInMemory the new limit      */
name|void
name|setLimitInMemory
parameter_list|(
name|long
name|limitInMemory
parameter_list|)
function_decl|;
comment|/**      * Get the limit on how many nodes a query may read at most (raw read      * operations, including skipped nodes). If this limit is exceeded, the      * query throws an exception.      *       * @return the limit      */
annotation|@
name|Description
argument_list|(
literal|"Get the limit on how many nodes a query may read at most (raw read "
operator|+
literal|"operations, including skipped nodes). If this limit is exceeded, the "
operator|+
literal|"query throws an exception."
argument_list|)
name|long
name|getLimitReads
parameter_list|()
function_decl|;
comment|/**      * Change the limit.      *       * @param limitReads the new limit      */
name|void
name|setLimitReads
parameter_list|(
name|long
name|limitReads
parameter_list|)
function_decl|;
comment|/**      * Whether queries that don't use an index will fail (throw an exception).      * The default is false.      *       * @return true if they fail      */
annotation|@
name|Description
argument_list|(
literal|"Whether queries that don't use an index will fail (throw an exception). "
operator|+
literal|"The default is false."
argument_list|)
name|boolean
name|getFailTraversal
parameter_list|()
function_decl|;
comment|/**      * Set whether queries that don't use an index will fail (throw an exception).      *       * @param failTraversal the new value for this setting      */
name|void
name|setFailTraversal
parameter_list|(
name|boolean
name|failTraversal
parameter_list|)
function_decl|;
comment|/**      * Whether the query result size should return an estimation for large queries.      *      * @return true if enabled      */
annotation|@
name|Description
argument_list|(
literal|"Whether the query result size should return an estimation for large queries."
argument_list|)
name|boolean
name|isFastQuerySize
parameter_list|()
function_decl|;
name|void
name|setFastQuerySize
parameter_list|(
name|boolean
name|fastQuerySize
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

