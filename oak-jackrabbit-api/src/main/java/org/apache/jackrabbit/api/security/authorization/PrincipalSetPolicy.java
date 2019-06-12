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
name|api
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
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
comment|/**  * Extension of the JCR {@link javax.jcr.security.AccessControlPolicy AccessControlPolicy}  * intended to grant a set of {@code Principal}s the ability to perform certain  * actions. The scope of this policy (and thus the affected items) is an  * implementation detail; it may e.g. take effect on the tree defined by the  * {@link javax.jcr.Node}, where a given {@code PrincipalSetPolicy} is being  * applied.  *  *<p>The very details on what actions are granted by a given {@code PrincipalSetPolicy}  * remains an implementation detail. Similarly a given permission model is  * in charge of defining the interactions and effects different  * {@link AccessControlPolicy policies} will have if used together in the same  * repository.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrincipalSetPolicy
extends|extends
name|AccessControlPolicy
block|{
comment|/**      * Returns the set of {@code Principal}s that are allowed to perform      * implementation specific actions on the items affected by this policy.      *      * @return The set of {@code Principal}s that are allowed to perform      * implementation specific actions on the those items where this policy      * takes effect.      */
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
function_decl|;
comment|/**      * Add {@code Principal}s that are allowed to perform some implementation      * specific actions on those items where this policy takes effect.      *      * @param principals The {@code Principal}s that are granted access.      * @return {@code true} if this policy was modified; {@code false} otherwise.      * @throws javax.jcr.security.AccessControlException If any of the specified      * principals is considered invalid or if another access control specific      * error occurs.      */
name|boolean
name|addPrincipals
parameter_list|(
annotation|@
name|NotNull
name|Principal
modifier|...
name|principals
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
comment|/**      * Remove the specified {@code Principal}s for the set of allowed principals      * thus revoking their ability to perform the implementation specific actions      * on items where this policy takes effect.      *      * @param principals The {@code Principal}s for which access should be revoked.      * @return {@code true} if this policy was modified; {@code false} otherwise.      * @throws javax.jcr.security.AccessControlException If any of the specified      * principals is considered invalid or if another access control specific      * error occurs.      */
name|boolean
name|removePrincipals
parameter_list|(
annotation|@
name|NotNull
name|Principal
modifier|...
name|principals
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
block|}
end_interface

end_unit

