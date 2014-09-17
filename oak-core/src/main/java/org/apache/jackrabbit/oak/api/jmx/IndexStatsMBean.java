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
name|IndexStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"IndexStats"
decl_stmt|;
name|String
name|STATUS_INIT
init|=
literal|"init"
decl_stmt|;
name|String
name|STATUS_RUNNING
init|=
literal|"running"
decl_stmt|;
name|String
name|STATUS_DONE
init|=
literal|"done"
decl_stmt|;
comment|/**      * @return The time the indexing job stared at, or {@code ""} if it is      *         not currently running.      */
name|String
name|getStart
parameter_list|()
function_decl|;
comment|/**      * @return The time the indexing job finished at, or {@code ""} if it      *         is still running.      */
name|String
name|getDone
parameter_list|()
function_decl|;
comment|/**      * Returns the current status of the indexing job      *       * @return the current status of the indexing job: {@value #STATUS_INIT},      *         {@value #STATUS_RUNNING} or {@value #STATUS_DONE}      */
name|String
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * Pauses the background indexing process. Future changes are not indexed      * until the {@link #resume()} method is called.      *       * The pause call will take effect on the next run cycle and will affect all      * indexes marked as 'async'.      *       * Note: this is experimental and should only be used for      * debugging/diagnosis purposes!      *       */
name|void
name|pause
parameter_list|()
function_decl|;
comment|/**      * Resumes the indexing process. All changes from the previous indexed state      * will be indexed.      *       * @see #pause()      */
name|void
name|resume
parameter_list|()
function_decl|;
comment|/**      * Returns the value of the 'paused' flag      *       * @return true if the indexing job is paused      */
name|boolean
name|isPaused
parameter_list|()
function_decl|;
comment|/**      * Returns the number of updates from the current run cycle. This value is      * kept until the next cycle begins.      *       * @return the number of updates from the current run cycle. This value is      *         kept until the next cycle begins.      */
name|long
name|getUpdates
parameter_list|()
function_decl|;
comment|/**      * Returns the current reference checkpoint used by the async indexer      *       * @return the reference checkpoint      */
name|String
name|getReferenceCheckpoint
parameter_list|()
function_decl|;
comment|/**      * Returns the processed checkpoint used by the async indexer. If this index      * round finishes successfully, the processed checkpoint will become the      * reference checkpoint, and the old reference checkpoint wil be released.      *       * @return the processed checkpoint      */
name|String
name|getProcessedCheckpoint
parameter_list|()
function_decl|;
comment|/**      * Temporary checkpoints represent old checkpoints that have been processed      * but the cleanup was not successful of did not happen at all (like in the      * event the system was forcibly stopped).      *       * @return the already processed checkpoints      */
name|String
name|getTemporaryCheckpoints
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

