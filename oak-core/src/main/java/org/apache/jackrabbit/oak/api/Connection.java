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
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * The {@code Connection} interface ...  *  * - retrieving information from persistent layer (MK) that are accessible to  *   a given session  *  * - validate information being written back to the persistent layer. this includes  *   permission evaluation, node type and name constraints etc.  *  * - update the revision ID a given session is operating on when certain actions  *   take place (save, refresh, TBD)  *  * - Provide access to authentication and authorization related information  *  * - Connection instances are not thread safe.  *  * - The implementation of this and all related interfaces are intended to only  *   hold the state of the persistent layer at a given revision without any  *   session-related state modifications.  *  * - Whether this connection is bound to a workspace or to the whole repository  *   is determined by how the connection was acquired: connections acquired by  *   {@link RepositoryService#login(Object, String)} are bound to the workspace  *   passed to the login method. Connections acquired by  *   {@link Connection#getRepositoryConnection()} are bound to the whole repository.  *  * TODO: describe how this interface is intended to handle validation: nt, names, ac, constraints...  */
end_comment

begin_interface
specifier|public
interface|interface
name|Connection
extends|extends
name|Closeable
block|{
comment|/**      * This methods provides access to information related to authentication      * and authorization of this connection. Multiple calls to this method      * may return different instances which are guaranteed to be equal wrt.      * to {@link Object#equals(Object)}.      *      * @return  immutable {@link AuthInfo} instance      */
name|AuthInfo
name|getAuthInfo
parameter_list|()
function_decl|;
comment|/**      * The immutable name of the workspace this {@code Connection} instance has      * been created for. If no workspace name has been specified during      * repository login this method will return the name of the default      * workspace.      *      * @return name of the workspace this instance has been created for or      * {@code null} if this connection is repository bound.      */
name|String
name|getWorkspaceName
parameter_list|()
function_decl|;
comment|/**      * Provide access to the whole repository (i.e. all workspaces) respecting      * the access rights of the current connection. This method returns the      * same connection instance on every invocation.      *      * @return a {@code Connection} which covers the whole repository.      */
name|Connection
name|getRepositoryConnection
parameter_list|()
function_decl|;
name|NodeState
name|getCurrentRoot
parameter_list|()
function_decl|;
comment|/**      * Refresh this connection to the latest revision of the underlying Microkernel.      */
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**      * Atomically apply all changes in the passed {@code editor} to the underlying      * Microkernel. On success this connection is implicitly refreshed to the state      * created by applying the editor.      *      * @param editor  editor carrying the changes to be applies      * @return  node state resulting from applying the changed      * @throws CommitFailedException      */
name|NodeState
name|commit
parameter_list|(
name|NodeStateEditor
name|editor
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Get an node state editor for the given state. Use {@link #commit(NodeStateEditor)}      * to atomically apply the changes made in this editor to the underlying Microkernel.      *      * @param state  node state to edit      * @return  editor for the passed {@code state}      */
name|NodeStateEditor
name|getNodeStateEditor
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
comment|/**      * Get the query engine.      *      * @return the query engine      */
name|QueryEngine
name|getQueryEngine
parameter_list|()
function_decl|;
comment|// TODO : add versioning operations
block|}
end_interface

end_unit

