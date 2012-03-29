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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|NodeBuilder
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
name|mk
operator|.
name|model
operator|.
name|NodeState
import|;
end_import

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
comment|/**  * The {@code Connection} interface ...  *  * - retrieving information from persistent layer (MK) that are accessible to  *   a given session  *  * - validate information being written back to the persistent layer. this includes  *   permission evaluation, node type and name constraints etc.  *  * - update the revision ID a given session is operating on.  *  * TODO: define whether this is a repository-level connection or just bound to a single workspace.  * TODO: describe how this interface is intended to handle validation: nt, names, ac, constraints...  */
end_comment

begin_interface
specifier|public
interface|interface
name|Connection
extends|extends
name|Closeable
block|{
name|AuthInfo
name|getAuthInfo
parameter_list|()
function_decl|;
comment|/**      * The immutable name of the workspace this {@code SessionInfo} instance has      * been created for. If no workspace name has been specified during      * repository login this method will return the name of the default      * workspace.      *      * @return name of the workspace this instance has been created for.      */
name|String
name|getWorkspaceName
parameter_list|()
function_decl|;
name|NodeState
name|getCurrentRoot
parameter_list|()
function_decl|;
name|NodeState
name|commit
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
name|NodeBuilder
name|getNodeBuilder
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
comment|// TODO : add versioning operations
comment|// TODO : add query execution operations
block|}
end_interface

end_unit

