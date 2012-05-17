begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|state
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
name|api
operator|.
name|CommitFailedException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|NodeStoreBranch
block|{
comment|/**      * Returns the latest state of the branch.      *      * @return root node state      */
name|NodeState
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Returns the base state of this branch.      *      * @return base node state      */
name|NodeState
name|getBase
parameter_list|()
function_decl|;
comment|/**      * Updates the state of the content tree.      *      * @param newRoot new root node state      */
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
function_decl|;
comment|/**      * Moves a node.      *      * @param source source path      * @param target target path      * @return  {@code true} iff the move succeeded      */
name|boolean
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
function_decl|;
comment|/**      * Copies a node.      *      * @param source source path      * @param target target path      * @return  {@code true} iff the copy succeeded      */
name|boolean
name|copy
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
function_decl|;
comment|/**      * Merges the changes in this branch to the main content tree.      * @return the node state resulting from the merge.      *      * @throws CommitFailedException if the merge failed      */
name|NodeState
name|merge
parameter_list|()
throws|throws
name|CommitFailedException
function_decl|;
block|}
end_interface

end_unit

