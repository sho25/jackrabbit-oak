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
name|state
operator|.
name|NodeState
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
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * Extension point for validating and modifying content changes. Available  * commit hooks are called in sequence to process incoming content changes  * before they get persisted and shared with other clients.  *<p>  * A commit hook can throw a {@link CommitFailedException} for a particular  * change to prevent it from being persisted, or it can modify the changes  * for example to update an in-content index or to add auto-generated content.  *<p>  * Note that instead of implementing this interface directly, most commit  * editors and validators are better expressed as implementations of the  * more specific extension interfaces defined in this package.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommitHook
block|{
comment|/**      * Validates and/or modifies the given content change before it gets      * persisted.      *      * @param store the node store that contains the repository content      * @param before content tree before the commit      * @param after content tree prepared for the commit      * @return content tree to be committed      * @throws CommitFailedException if the commit should be rejected      */
annotation|@
name|Nonnull
name|NodeState
name|processCommit
parameter_list|(
name|NodeStore
name|store
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
block|}
end_interface

end_unit

