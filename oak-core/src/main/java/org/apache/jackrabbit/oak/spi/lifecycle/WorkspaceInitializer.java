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
name|lifecycle
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
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|CommitHook
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
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * Initializer of a workspace and it's initial content. A module that needs  * to add content to a workspace can implement this interface.  *<p/>  * TODO: define if/how runtime configuration changes may affect the workspace content.  * TODO: review params of initialize()  */
end_comment

begin_interface
specifier|public
interface|interface
name|WorkspaceInitializer
block|{
comment|/**      * Initialize the content of a new workspace. This method is called before      * the workspace becomes available.      *      * @param workspaceRoot The workspace root state.      * @param workspaceName The name of the workspace that is being initialized.      * @param indexProvider The query index provider used within this workspace.      * @param commitHook    The commit hook(s) defined for this workspace.      * @return The modified workspace root state.      */
annotation|@
name|Nonnull
name|NodeState
name|initialize
parameter_list|(
name|NodeState
name|workspaceRoot
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|CommitHook
name|commitHook
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

