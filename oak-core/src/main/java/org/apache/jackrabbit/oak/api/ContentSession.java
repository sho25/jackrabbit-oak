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
comment|/**  * Authentication session for accessing (TODO: a workspace inside) a content  * repository.  *<p>  * - retrieving information from persistent layer (MK) that are accessible to  *   a given JCR session  *  * - validate information being written back to the persistent layer. this includes  *   permission evaluation, node type and name constraints etc.  *  * - update the revision ID a given session is operating on when certain actions  *   take place (save, refresh, TBD)  *  * - Provide access to authentication and authorization related information  *  * - The implementation of this and all related interfaces are intended to only  *   hold the state of the persistent layer at a given revision without any  *   session-related state modifications.  *<p>  * TODO: describe how this interface is intended to handle validation:  * nt, names, ac, constraints...  *<p>  * This interface is thread-safe.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentSession
extends|extends
name|Closeable
block|{
comment|/**      * This methods provides access to information related to authentication      * and authorization of this content session. Multiple calls to this method      * may return different instances which are guaranteed to be equal wrt.      * to {@link Object#equals(Object)}.      *      * @return  immutable {@link AuthInfo} instance      */
name|AuthInfo
name|getAuthInfo
parameter_list|()
function_decl|;
comment|/**      * The name of the workspace this {@code ContentSession} instance has      * been created for. If no workspace name has been specified during      * repository login this method will return the name of the default      * workspace.      *      * @return name of the workspace this instance has been created for or      * {@code null} if this content session is repository bound.      */
name|String
name|getWorkspaceName
parameter_list|()
function_decl|;
comment|/**      * Refresh this content session to the latest revision of the underlying Microkernel.      */
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**      * Atomically apply all changes in the passed {@code branch} to the underlying      * Microkernel.      *      * @param branch  branch carrying the changes to be applies      * @throws CommitFailedException      */
name|void
name|commit
parameter_list|(
name|Branch
name|branch
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Branch the current root. Use {@link #commit(Branch)} to atomically apply the      * changes made in this branch to the underlying Microkernel.      *      * @return branch of the current root.      */
name|Branch
name|branchRoot
parameter_list|()
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

