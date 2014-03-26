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

begin_interface
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
block|}
end_interface

end_unit

