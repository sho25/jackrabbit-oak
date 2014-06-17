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
name|mk
operator|.
name|store
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
name|ChildNodeEntries
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
name|Id
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
name|MutableCommit
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
name|MutableNode
import|;
end_import

begin_comment
comment|/**  * Write operations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RevisionStore
extends|extends
name|RevisionProvider
block|{
comment|/**      * Token that must be created first before invoking any put operation.      */
specifier|public
specifier|abstract
class|class
name|PutToken
block|{
comment|/* Prevent other implementations. */
name|PutToken
parameter_list|()
block|{}
block|}
comment|/**      * Create a put token.      *       * @return put token      */
name|PutToken
name|createPutToken
parameter_list|()
function_decl|;
name|Id
comment|/*id*/
name|putNode
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableNode
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|Id
comment|/*id*/
name|putCNEMap
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|ChildNodeEntries
name|map
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Lock the head. Must be called prior to putting a new head commit.      *       * @see #putHeadCommit(PutToken, MutableCommit, Id, Id)      * @see #unlockHead()      */
name|void
name|lockHead
parameter_list|()
function_decl|;
comment|/**      * Put a new head commit. Must be called while holding a lock on the head.      *      * @param token      *            put token      * @param commit      *            commit      * @param branchRootId      *            former branch root id, if this is a merge; otherwise      *            {@code null}      * @param branchRevId      *            current branch head, i.e. last commit on this branch,       *            if this is a merge; otherwise {@code null}      * @return head commit id      * @throws Exception      *             if an error occurs      * @see #lockHead()      */
name|Id
comment|/*id*/
name|putHeadCommit
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableCommit
name|commit
parameter_list|,
name|Id
name|branchRootId
parameter_list|,
name|Id
name|branchRevId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Unlock the head.      *      * @see #lockHead()      */
name|void
name|unlockHead
parameter_list|()
function_decl|;
comment|/**      * Store a new commit.      *<p>      * Unlike {@code putHeadCommit(MutableCommit)}, this method      * does not affect the current head commit and therefore doesn't      * require a lock on the head.      *      * @param token put token      * @param commit commit      * @return new commit id      * @throws Exception if an error occurs      */
name|Id
comment|/*id*/
name|putCommit
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableCommit
name|commit
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

