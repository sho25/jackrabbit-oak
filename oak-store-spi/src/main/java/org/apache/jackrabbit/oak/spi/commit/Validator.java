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
name|commit
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
name|PropertyState
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Content change validator. An instance of this interface is used to  * validate changes against a specific {@link NodeState}.  *  * @see<a href="http://jackrabbit.apache.org/oak/docs/nodestate.html#Commit_validators"  *>Commit validators</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Validator
extends|extends
name|Editor
block|{
comment|/**      * Validate an added property      * @param after  the added property      * @throws CommitFailedException  if validation fails.      */
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Validate a changed property      * @param before the original property      * @param after  the changed property      * @throws CommitFailedException  if validation fails.      */
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Validate a deleted property      * @param before the original property      * @throws CommitFailedException  if validation fails.      */
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Validate an added node      * @param name the name of the added node      * @param after  the added node      * @return a {@code Validator} for {@code after} or {@code null} if validation      * should not decent into the subtree rooted at {@code after}.      * @throws CommitFailedException  if validation fails.      */
annotation|@
name|Nullable
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Validate a changed node      * @param name the name of the changed node      * @param before the original node      * @param after  the changed node      * @return a {@code Validator} for {@code after} or {@code null} if validation      * should not decent into the subtree rooted at {@code after}.      * @throws CommitFailedException  if validation fails.      */
annotation|@
name|Nullable
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
comment|/**      * Validate a deleted node      * @param name The name of the deleted node.      * @param before the original node      * @return a {@code Validator} for the removed subtree or      * {@code null} if validation should not decent into the subtree      * @throws CommitFailedException  if validation fails.      */
annotation|@
name|Nullable
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
function_decl|;
block|}
end_interface

end_unit

