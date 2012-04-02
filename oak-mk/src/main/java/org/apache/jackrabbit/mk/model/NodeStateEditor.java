begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
package|;
end_package

begin_comment
comment|/**  * An editor for modifying existing and creating new  * {@link NodeState node states}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStateEditor
block|{
comment|/**      * Add or replace the child node state with the given {@code name}.      * @param name name of the new node state      * @return editor for the added node state      */
name|NodeStateEditor
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Remove the child node state with the given {@code name}.      * @param name  name of the node state to remove      */
name|void
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set a property on this node state      * @param name name of the property      * @param value value of the property      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Scalar
name|value
parameter_list|)
function_decl|;
comment|/**      * Remove a property from this node state      * @param name name of the property      */
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Move the node state located at {@code sourcePath} to a node      * state at {@code destPath}.      * @param sourcePath source path relative to this node state      * @param destPath destination path relative to this node state      */
name|void
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Copy the node state located at {@code sourcePath} to a node      * state at {@code destPath}.      * @param sourcePath source path relative to this node state      * @param destPath destination path relative to this node state      */
name|void
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Edit the child node state with the given {@code name}.      * @param name name of the child node state to edit.      * @return editor for the child node state of the given name or      *         {@code null} if no such node state exists.      */
name|NodeStateEditor
name|edit
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns an immutable node state that matches the current state of      * the editor.      *      * @return immutable node state      */
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
comment|/**      * Return the base node state of this private branch      * @return base node state      */
name|NodeState
name|getBaseNodeState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

