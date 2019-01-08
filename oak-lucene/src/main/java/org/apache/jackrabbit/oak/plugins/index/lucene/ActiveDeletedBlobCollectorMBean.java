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
name|index
operator|.
name|lucene
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * MBean for starting and monitoring the progress of  * collection of deleted lucene index blobs.  *  * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_interface
specifier|public
interface|interface
name|ActiveDeletedBlobCollectorMBean
block|{
name|String
name|TYPE
init|=
literal|"ActiveDeletedBlobCollector"
decl_stmt|;
comment|/**      * Initiate collection operation of deleted lucene index blobs      *      * @return the status of the operation right after it was initiated      */
annotation|@
name|NotNull
name|CompositeData
name|startActiveCollection
parameter_list|()
function_decl|;
comment|/**      * Cancel a running collection of deleted lucene index blobs operation.      * Does nothing if collection is not running.      *      * @return the status of the operation right after it was initiated      */
annotation|@
name|NotNull
name|CompositeData
name|cancelActiveCollection
parameter_list|()
function_decl|;
comment|/**      * Status of collection of deleted lucene index blobs.      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|NotNull
name|CompositeData
name|getActiveCollectionStatus
parameter_list|()
function_decl|;
comment|/**      * @return true: if recording deleted blob for active deletion is unsafe; false: otherwise      */
annotation|@
name|NotNull
name|boolean
name|isActiveDeletionUnsafe
parameter_list|()
function_decl|;
comment|/**      * Flag current blobs (head state) referred by all indexes so that they won't      * be marked to be collected by active deletion later. It would also set an      * in-memory flag so that new blobs also are flagged to be not marked for deletion      * by active deletion      */
annotation|@
name|NotNull
name|void
name|flagActiveDeletionUnsafeForCurrentState
parameter_list|()
function_decl|;
comment|/**      * Resets the in-memory flag so that new blobs are not flagged anymore and hence      * would get marked for active deletion when active deletion is active.      */
name|void
name|flagActiveDeletionSafe
parameter_list|()
function_decl|;
comment|/**      * Whether active deletion is disabled.      *      * @return true if disabled      */
name|boolean
name|isDisabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

