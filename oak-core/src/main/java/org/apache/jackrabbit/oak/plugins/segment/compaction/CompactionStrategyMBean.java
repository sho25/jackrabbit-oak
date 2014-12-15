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
name|compaction
package|;
end_package

begin_interface
specifier|public
interface|interface
name|CompactionStrategyMBean
block|{
name|String
name|TYPE
init|=
literal|"CompactionStrategy"
decl_stmt|;
name|boolean
name|isCloneBinaries
parameter_list|()
function_decl|;
name|void
name|setCloneBinaries
parameter_list|(
name|boolean
name|cloneBinaries
parameter_list|)
function_decl|;
name|boolean
name|isPausedCompaction
parameter_list|()
function_decl|;
name|void
name|setPausedCompaction
parameter_list|(
name|boolean
name|pausedCompaction
parameter_list|)
function_decl|;
name|String
name|getCleanupStrategy
parameter_list|()
function_decl|;
name|void
name|setCleanupStrategy
parameter_list|(
name|String
name|cleanup
parameter_list|)
function_decl|;
name|long
name|getOlderThan
parameter_list|()
function_decl|;
name|void
name|setOlderThan
parameter_list|(
name|long
name|olderThan
parameter_list|)
function_decl|;
name|byte
name|getMemoryThreshold
parameter_list|()
function_decl|;
name|void
name|setMemoryThreshold
parameter_list|(
name|byte
name|memory
parameter_list|)
function_decl|;
name|String
name|getCompactionMapStats
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

