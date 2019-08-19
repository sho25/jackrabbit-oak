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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|Privilege
import|;
end_import

begin_comment
comment|/**  *<code>PrivilegeManager</code> is a jackrabbit specific extensions to  * JCR access control management that allows to retrieve privileges known  * by this JCR implementation and to register new custom privileges according  * to implementation specific rules.  *  * @see javax.jcr.security.AccessControlManager#privilegeFromName(String)   */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeManager
block|{
comment|/**      * Returns all registered privileges.      *      * @return all registered privileges.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|Privilege
index|[]
name|getRegisteredPrivileges
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Returns the privilege with the specified<code>privilegeName</code>.      *      * @param privilegeName Name of the principal.      * @return the privilege with the specified<code>privilegeName</code>.      * @throws javax.jcr.security.AccessControlException If no privilege with the given name exists.      * @throws javax.jcr.RepositoryException If another error occurs.      */
annotation|@
name|NotNull
name|Privilege
name|getPrivilege
parameter_list|(
annotation|@
name|NotNull
name|String
name|privilegeName
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Creates and registers a new custom privilege with the specified      * characteristics and returns the new privilege.      *<p>      * If the registration succeeds, the changes are immediately effective;      * there is no need to call<code>save</code>.      *      * @param privilegeName The name of the new custom privilege.      * @param isAbstract Boolean flag indicating if the privilege is abstract.      * @param declaredAggregateNames An array of privilege names referring to      * registered privileges being aggregated by this new custom privilege.      * In case of a non aggregate privilege an empty array should be passed.      * @return the new privilege.      * @throws AccessDeniedException If the session this manager has been created      * for is not allowed to register new privileges.      * @throws NamespaceException If any of the specified JCR names is illegal.      * @throws RepositoryException If the privilege could not be registered due      * to any implementation specific constraint violations or if persisting the      * custom privilege fails.      */
annotation|@
name|NotNull
name|Privilege
name|registerPrivilege
parameter_list|(
annotation|@
name|NotNull
name|String
name|privilegeName
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|declaredAggregateNames
parameter_list|)
throws|throws
name|AccessDeniedException
throws|,
name|NamespaceException
throws|,
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

