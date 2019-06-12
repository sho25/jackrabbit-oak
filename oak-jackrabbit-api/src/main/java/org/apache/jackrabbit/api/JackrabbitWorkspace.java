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
package|;
end_package

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
name|authorization
operator|.
name|PrivilegeManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
import|;
end_import

begin_comment
comment|/**  * The Jackrabbit workspace interface. This interface contains the  * Jackrabbit-specific extensions to the JCR {@link Workspace} interface.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JackrabbitWorkspace
extends|extends
name|Workspace
block|{
comment|/**      * Creates a workspace with the given name.      *      * @param workspaceName name of the new workspace      * @throws AccessDeniedException if the current session is not allowed to      *                               create the workspace      * @throws RepositoryException   if a workspace with the given name      *                               already exists or if another error occurs      * @see #getAccessibleWorkspaceNames()      */
name|void
name|createWorkspace
parameter_list|(
name|String
name|workspaceName
parameter_list|)
throws|throws
name|AccessDeniedException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Creates a workspace with the given name and a workspace configuration      * template.      *      * @param workspaceName name of the new workspace      * @param workspaceTemplate the configuration template of the new workspace      * @throws AccessDeniedException if the current session is not allowed to      *                               create the workspace      * @throws RepositoryException   if a workspace with the given name      *                               already exists or if another error occurs      * @see #getAccessibleWorkspaceNames()      */
name|void
name|createWorkspace
parameter_list|(
name|String
name|workspaceName
parameter_list|,
name|InputSource
name|workspaceTemplate
parameter_list|)
throws|throws
name|AccessDeniedException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Returns the privilege manager.      *      * @return the privilege manager.      * @throws RepositoryException If an error occurs.      */
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

