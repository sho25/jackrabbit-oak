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
name|NodeBuilder
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
name|WorkspaceInitializer
name|DEFAULT
init|=
operator|new
name|WorkspaceInitializer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|workspaceName
parameter_list|)
block|{         }
block|}
decl_stmt|;
comment|/**      * Initialize the content of a new workspace. This method is called before      * the workspace becomes available.      *      * @param builder       builder for accessing and modifying the workspace      * @param workspaceName The name of the workspace that is being initialized.      */
name|void
name|initialize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|workspaceName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

