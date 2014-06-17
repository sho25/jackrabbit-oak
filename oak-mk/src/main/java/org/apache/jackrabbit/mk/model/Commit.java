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
name|model
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
name|store
operator|.
name|Binding
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Commit
block|{
name|Id
name|getRootNodeId
parameter_list|()
function_decl|;
name|Id
name|getParentId
parameter_list|()
function_decl|;
name|long
name|getCommitTS
parameter_list|()
function_decl|;
name|String
name|getMsg
parameter_list|()
function_decl|;
name|String
name|getChanges
parameter_list|()
function_decl|;
comment|/**      * Returns {@code null} if this commit does not represent a branch.      *<p>      * Otherwise, returns the id of the branch root commit      * (i.e. the<i>public</i> commit that this<i>private</i> branch is based upon).      *      *      * @return the id of the branch root commit or {@code null} if this commit      * does not represent a branch.      */
name|Id
name|getBranchRootId
parameter_list|()
function_decl|;
name|void
name|serialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

