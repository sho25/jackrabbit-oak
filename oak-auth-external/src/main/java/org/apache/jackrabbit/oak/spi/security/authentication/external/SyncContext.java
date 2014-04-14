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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
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

begin_comment
comment|/**  * {@code SyncContext} is used as scope for sync operations. Implementations are free to associate any resources with  * the sync context. The sync context must not be accessed concurrently and must be closed after use.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncContext
block|{
comment|/**      * Defines if synchronization keeps missing external identities on synchronization of authorizables. Default      * is {@code false}.      * @return {@code true} if keep missing.      */
name|boolean
name|isKeepMissing
parameter_list|()
function_decl|;
comment|/**      * See {@link #isKeepMissing()}      */
annotation|@
name|Nonnull
name|SyncContext
name|setKeepMissing
parameter_list|(
name|boolean
name|keep
parameter_list|)
function_decl|;
comment|/**      * Defines if synchronization of users always will perform, i.e. ignores the last synced properties.      * @return {@code true} if forced syncing users      */
name|boolean
name|isForceUserSync
parameter_list|()
function_decl|;
comment|/**      * See {@link #isForceUserSync()}      */
annotation|@
name|Nonnull
name|SyncContext
name|setForceUserSync
parameter_list|(
name|boolean
name|force
parameter_list|)
function_decl|;
comment|/**      * Defines if synchronization of groups always will perform, i.e. ignores the last synced properties.      * @return {@code true} if forced syncing groups      */
name|boolean
name|isForceGroupSync
parameter_list|()
function_decl|;
comment|/**      * See {@link #isForceGroupSync()}      */
annotation|@
name|Nonnull
name|SyncContext
name|setForceGroupSync
parameter_list|(
name|boolean
name|force
parameter_list|)
function_decl|;
comment|/**      * Synchronizes an external identity with the repository based on the respective configuration.      *      * @param identity the identity to sync.      * @return the result of the operation      * @throws SyncException if an error occurrs      */
name|SyncResult
name|sync
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentity
name|identity
parameter_list|)
throws|throws
name|SyncException
function_decl|;
comment|/**      * Synchronizes an authorizable with the corresponding external identity with the repository based on the respective      * configuration.      *      * @param id the id of the authorizable      * @return the result of the operation      * @throws SyncException if an error occurrs      */
name|SyncResult
name|sync
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
throws|throws
name|SyncException
function_decl|;
comment|/**      * Closes this context and releases any resources bound to it. Note that an implementation must not commit the      * {@link org.apache.jackrabbit.oak.api.Root} passed during the creation call. This is the responsibility of the      * application.      */
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

