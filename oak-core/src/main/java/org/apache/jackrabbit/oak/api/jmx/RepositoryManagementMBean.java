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

begin_comment
comment|/**  * This interface exposes repository management operations  * and the status of such operations. This interface only  * provides high level functionality for starting certain  * management operations and monitoring their outcomes.  * Parametrisation and configuration of the operations is  * beyond the scope of this interface and must be achieved  * by other means. For example through a dedicated MBean of  * the specific service providing the respective functionality.  * Furthermore not all operations might be available in all  * deployments or at all times. However the status should give  * a clear indication for this case.  *<p>  * The status of an operation is an opaque string describing  * in a human readable form what the operation currently does,  * which might depend on the particular implementation performing  * the operation. However the status status<em>must</em> always  * indicate whether an operation is ongoing, not started  or  * terminated. In the latter case it<em>must</em> indicate whether  * it terminated successfully or whether it failed. Furthermore the  * status<em>must</em> indicate when an operation is not available.  * In all cases the status<em>may</em> provide additional  * information like e.g. how far an ongoing operation progressed,  * what time it took to complete a terminated operation, or information  * about what caused a terminated operation to fail.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RepositoryManagementMBean
block|{
comment|/**      * Initiate a backup operation.      *      * @return  the status of the operation right after it was initiated      */
name|String
name|startBackup
parameter_list|()
function_decl|;
comment|/**      * Backup status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or {@code null} if none.      */
name|String
name|getBackupStatus
parameter_list|()
function_decl|;
comment|/**      * Initiate a restore operation.      *      * @return  the status of the operation right after it was initiated      */
name|String
name|startRestore
parameter_list|()
function_decl|;
comment|/**      * Restore status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or {@code null} if none.      */
name|String
name|getRestoreStatus
parameter_list|()
function_decl|;
comment|/**      * Initiate a data store garbage collection operation      *      * @return  the status of the operation right after it was initiated      */
name|String
name|startDataStoreGC
parameter_list|()
function_decl|;
comment|/**      * Data store garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or {@code null} if none.      */
name|String
name|getDataStoreGCStatus
parameter_list|()
function_decl|;
comment|/**      * Initiate a revision garbage collection operation      *      * @return  the status of the operation right after it was initiated      */
name|String
name|startRevisionGC
parameter_list|()
function_decl|;
comment|/**      * Revision garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or {@code null} if none.      */
name|String
name|getRevisionGCStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

