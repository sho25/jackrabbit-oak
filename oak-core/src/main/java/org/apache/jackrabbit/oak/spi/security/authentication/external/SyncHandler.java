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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
import|;
end_import

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
name|api
operator|.
name|Root
import|;
end_import

begin_comment
comment|/**  * SyncHandler is used to sync users and groups from an {@link ExternalIdentityProvider}.  * The synchronization performed within the scope of a {@link SyncContext} which is acquired during the  * {@link #createContext(ExternalIdentityProvider, org.apache.jackrabbit.api.security.user.UserManager, org.apache.jackrabbit.oak.api.Root)} call.  *  * The exact configuration is managed by the sync handler instance. The system may contain several sync handler  * implementations with different configurations. those are managed by the {@link SyncManager}.  *  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.SyncContext  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.SyncManager  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncHandler
block|{
comment|/**      * Returns the name of this sync handler.      * @return sync handler name      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Initializes a sync context which is used to start the sync operations.      *      * @param idp the external identity provider used for syncing      * @param userManager user manager for managing authorizables      * @param root root of the current tree      * @return the sync context      * @throws SyncException if an error occurs      */
annotation|@
name|Nonnull
name|SyncContext
name|createContext
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|)
throws|throws
name|SyncException
function_decl|;
block|}
end_interface

end_unit

