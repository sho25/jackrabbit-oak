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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
comment|/**  * This interface exposes repository management operations and the status  * of such operations. This interface only provides high level functionality  * for starting certain management operations and monitoring their outcomes.  * Parametrisation and configuration of the operations is beyond the scope  * of this interface and must be achieved by other means. For example  * through a dedicated MBean of the specific service providing the  * respective functionality. Furthermore not all operations might be  * available in all deployments or at all times. However the status should  * give a clear indication for this case.  *<p>  * The status of an operation is represented by a {@code CompositeData}  * instance consisting at least of the items {@code code}, {@code id},  * and {@code message}. Implementations are free to add further items.  *<p>  * The {@code code} item is an integer encoding the current status of  * the respective operation. Valid values and its semantics are:  *<ul>  *<li>{@code 0}:<em>Operation not available</em>. For example  *     because the system does not implement the operation or the  *     system is in a state where it does not allow the operation to  *     be carried out (e.g. the operation is already running). The  *     {@code message} should give further indication of the exact  *     reason.</li>  *<li>{@code 1}:<em>Status not available</em>. Usually because  *     there was no prior attempt to start the operation. The  *     {@code message} should give further indication of the exact  *     reason.</li>  *<li>{@code 2}:<em>Operation initiated</em>. The {@code message}  *     should give further information of when the operation was  *     initiated. This status mean that the operation will be performed  *     some time in the future without impacting overall system behaviour  *     and that no further status updates will be available until this  *     operation is performed next time.</li>  *<li>{@code 3}:<em>Operation running</em>.</li>  *<li>{@code 4}:<em>Operation succeeded</em>. The {@code message} should  *     give further information on how long the operation took to  *     complete.</li>  *<li>{@code 5}: Operation failed. The {@code message} should give  *     further information on the reason for the failure.</li>  *</ul>  *<p>  * In all cases the {@code message} may provide additional information  * that might be useful in the context of the operation.  *<p>  * The {@code id} is an identifier for the invocation of an operation.  * It is reported as a part of the status for clients to relate the  * status to invocation. {@code -1} is returned when not available.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RepositoryManagementMBean
block|{
name|String
name|TYPE
init|=
literal|"RepositoryManagement"
decl_stmt|;
comment|/**      * Enum whose ordinals correspond to the status codes.      */
enum|enum
name|StatusCode
block|{
name|UNAVAILABLE
argument_list|(
literal|"Operation not available"
argument_list|)
block|,
name|NONE
argument_list|(
literal|"Status not available"
argument_list|)
block|,
name|INITIATED
argument_list|(
literal|"Operation initiated"
argument_list|)
block|,
name|RUNNING
argument_list|(
literal|"Operation running"
argument_list|)
block|,
name|SUCCEEDED
argument_list|(
literal|"Operation succeeded"
argument_list|)
block|,
name|FAILED
argument_list|(
literal|"Operation failed"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
name|StatusCode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
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
comment|/**      * Initiate a data store garbage collection operation      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startDataStoreGC
parameter_list|()
function_decl|;
comment|/**      * Data store garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getDataStoreGCStatus
parameter_list|()
function_decl|;
comment|/**      * Initiate a revision garbage collection operation      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startRevisionGC
parameter_list|()
function_decl|;
comment|/**      * Revision garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getRevisionGCStatus
parameter_list|()
function_decl|;
comment|/**      * Creates a new checkpoint of the latest root of the tree. The checkpoint      * remains valid for at least as long as requested and allows that state      * of the repository to be retrieved using the returned opaque string      * reference.      *      * @param lifetime time (in milliseconds,&gt; 0) that the checkpoint      *                 should remain available      * @return string reference of this checkpoint or {@code null} if      * the checkpoint could not be set.      *      * @deprecated Use {@link CheckpointMBean} instead      */
annotation|@
name|Deprecated
annotation|@
name|CheckForNull
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
function_decl|;
comment|/**      * Initiate a reindex operation for the property indexes marked for      * reindexing      *       * @return the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startPropertyIndexAsyncReindex
parameter_list|()
function_decl|;
comment|/**      * Asynchronous Property Index reindexing status      *       * @return the status of the ongoing operation or if none the terminal      *         status of the last operation or<em>Status not available</em> if      *         none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getPropertyIndexAsyncReindexStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

