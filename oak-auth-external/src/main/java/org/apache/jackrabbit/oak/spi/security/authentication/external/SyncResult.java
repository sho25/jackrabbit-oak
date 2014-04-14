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
comment|/**  * Defines the result of a sync operation  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncResult
block|{
comment|/**      * The synchronized identity      * @return the identity      */
annotation|@
name|CheckForNull
name|SyncedIdentity
name|getIdentity
parameter_list|()
function_decl|;
comment|/**      * The status of the sync operation      * @return the status      */
annotation|@
name|Nonnull
name|Status
name|getStatus
parameter_list|()
function_decl|;
comment|/**      * Result codes for sync operation.      */
enum|enum
name|Status
block|{
comment|/**          * No update          */
name|NOP
block|,
comment|/**          * authorizable added          */
name|ADD
block|,
comment|/**          * authorizable updated          */
name|UPDATE
block|,
comment|/**          * authorizable deleted          */
name|DELETE
block|,
comment|/**          * nothing changed. no such authorizable found.          */
name|NO_SUCH_AUTHORIZABLE
block|,
comment|/**          * nothing changed. no such identity found.          */
name|NO_SUCH_IDENTITY
block|,
comment|/**          * nothing changed. corresponding identity missing          */
name|MISSING
block|,
comment|/**          * nothing changed. idp name not correct          */
name|FOREIGN
block|}
block|}
end_interface

end_unit

