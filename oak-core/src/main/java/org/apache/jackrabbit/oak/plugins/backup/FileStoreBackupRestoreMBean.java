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
name|backup
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

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
comment|/**  * MBean for backing up and restoring a {@code NodeStore}.  *  * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_interface
specifier|public
interface|interface
name|FileStoreBackupRestoreMBean
block|{
comment|/**      * Initiate a backup operation.      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startBackup
parameter_list|()
function_decl|;
comment|/**      * Backup status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getBackupStatus
parameter_list|()
function_decl|;
comment|/**      * Initiate a restore operation.      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startRestore
parameter_list|()
function_decl|;
comment|/**      * Restore status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getRestoreStatus
parameter_list|()
function_decl|;
comment|/**      * Creates a new checkpoint of the latest root of the tree. The checkpoint      * remains valid for at least as long as requested and allows that state      * of the repository to be retrieved using the returned opaque string      * reference.      *      * @param lifetime time (in milliseconds,&gt; 0) that the checkpoint      *                 should remain available      * @return string reference of this checkpoint      *      * @deprecated Use {@link org.apache.jackrabbit.oak.api.jmx.CheckpointMBean} instead      */
annotation|@
name|Deprecated
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

