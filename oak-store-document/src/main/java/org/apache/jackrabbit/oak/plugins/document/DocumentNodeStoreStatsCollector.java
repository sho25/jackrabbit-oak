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
name|document
package|;
end_package

begin_interface
specifier|public
interface|interface
name|DocumentNodeStoreStatsCollector
block|{
comment|/**      * Report to the collector that a background read was done.      *      * @param stats the stats for the background read operation.      */
name|void
name|doneBackgroundRead
parameter_list|(
name|BackgroundReadStats
name|stats
parameter_list|)
function_decl|;
comment|/**      * Report to the collector that a background update was done.      *      * @param stats the stats for the background update operation.      */
name|void
name|doneBackgroundUpdate
parameter_list|(
name|BackgroundWriteStats
name|stats
parameter_list|)
function_decl|;
comment|/**      * Report to the collector that a lease update was done.      *      * @param timeMicros the time in microseconds it took to update the lease.      */
name|void
name|doneLeaseUpdate
parameter_list|(
name|long
name|timeMicros
parameter_list|)
function_decl|;
comment|/**      * Report to the collector that a branch commit was done.      */
name|void
name|doneBranchCommit
parameter_list|()
function_decl|;
comment|/**      * Report to the collector that a branch was merged.      *      * @param numCommits the number of branch commits merged.      */
name|void
name|doneMergeBranch
parameter_list|(
name|int
name|numCommits
parameter_list|)
function_decl|;
comment|/**      * Reports to the collector that a merge was done.      *      * @param numRetries the number of retries that were necessary.      * @param timeMillis the time in milliseconds it took to merge the changes.      * @param suspended whether the merge had to be suspended.      * @param exclusive whether the merge was holding an exclusive lock.      */
name|void
name|doneMerge
parameter_list|(
name|int
name|numRetries
parameter_list|,
name|long
name|timeMillis
parameter_list|,
name|boolean
name|suspended
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
function_decl|;
comment|/**      * Reports to the collector that a merge failed.      *      * @param numRetries the number of retries that were done.      * @param timeMillis the time in milliseconds it took to attempt the merge.      * @param suspended whether the merge had to be suspended.      * @param exclusive whether the merge was holding an exclusive lock.      */
name|void
name|failedMerge
parameter_list|(
name|int
name|numRetries
parameter_list|,
name|long
name|timeMillis
parameter_list|,
name|boolean
name|suspended
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

