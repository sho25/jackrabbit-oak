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
name|oak
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * An branch for modifying existing and creating new node states.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Branch
block|{
comment|/**      * Move the node state located at {@code sourcePath} to a node      * state at {@code destPath}. Do nothing if either the source      * does not exist, the parent of the destination does not exist      * or the destination exists already. Both paths must resolve      * to node states located in this branch.      *      * @param sourcePath source path relative to this node state      * @param destPath destination path relative to this node state      * @return  {@code true} on success, {@code false} otherwise.      */
name|boolean
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Copy the node state located at {@code sourcePath} to a node      * state at {@code destPath}. Do nothing if either the source      * does not exist, the parent of the destination does not exist      * or the destination exists already. Both paths must resolve      * to node states located in this branch.      *      * @param sourcePath source path relative to this node state      * @param destPath destination path relative to this node state      * @return  {@code true} on success, {@code false} otherwise.      */
name|boolean
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
function_decl|;
comment|/**      * Retrieve the child node state at the given {@code path}.      * The path must resolve to a node state located in this branch.      *      * @param path path of the child node state to getNode.      * @return transient node state at the given path or {@code null} if no      * such node state exists.      */
name|TransientNodeState
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

