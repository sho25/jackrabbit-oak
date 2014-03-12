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

begin_comment
comment|/**  * Represents a synchronized identity managed by a {@link SyncHandler}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncedIdentity
block|{
comment|/**      * Returns the internal id or name of the corresponding authorizable.      * @return the id.      */
annotation|@
name|Nonnull
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**      * Returns the external reference of this identity.      * @return the reference or {@code null}      */
annotation|@
name|CheckForNull
name|ExternalIdentityRef
name|getExternalIdRef
parameter_list|()
function_decl|;
comment|/**      * Checks if this identity represents a group.      * @return {@code true} if group.      */
name|boolean
name|isGroup
parameter_list|()
function_decl|;
comment|/**      * Returns the time when this identity was last synced or a value less or equal to 0 if it was never synced.      * @return the time when this identity was last synced.      */
name|long
name|lastSynced
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

