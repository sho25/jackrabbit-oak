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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|api
operator|.
name|CommitFailedException
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
name|commit
operator|.
name|CommitInfo
import|;
end_import

begin_comment
comment|/**  * An instance of this class represents a private branch of the tree in a  * {@link NodeStore} to which transient changes can be applied and later merged  * back or discarded.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeStoreBranch
block|{
comment|/**      * Returns the base state of this branch.      * The base state is the state of the tree as it was at the time      * this branch was created.      *      * @return root node state      */
annotation|@
name|Nonnull
name|NodeState
name|getBase
parameter_list|()
function_decl|;
comment|/**      * Returns the head state of this branch.      * The head state is the state resulting from the      * base state by applying all subsequent modifications to this branch      * by {@link #setRoot(NodeState)}, {@link #move(String, String)},      * and {@link #copy(String, String)}.      *      * @return root node state      * @throws IllegalStateException if the branch is already merged      */
annotation|@
name|Nonnull
name|NodeState
name|getHead
parameter_list|()
function_decl|;
comment|/**      * Updates the state of the content tree of this private branch.      *      * @param newRoot new root node state      * @throws IllegalStateException if the branch is already merged      */
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
function_decl|;
comment|/**      * Merges the changes in this branch to the main content tree.      * Merging is done by rebasing the changes in this branch on top of      * the current head revision followed by a fast forward merge.      *      * @param hook the commit hook to apply while merging changes      * @param info commit info associated with this merge operation,      *             or {@code null} if no local commit information is available      * @return the node state resulting from the merge.      * @throws CommitFailedException if the merge failed      * @throws IllegalStateException if the branch is already merged      */
annotation|@
name|Nonnull
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Rebase the changes from this branch on top of the current      * root.      */
name|void
name|rebase
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

