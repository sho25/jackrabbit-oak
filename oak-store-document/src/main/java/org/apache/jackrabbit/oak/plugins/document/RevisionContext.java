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
name|plugins
operator|.
name|document
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
name|stats
operator|.
name|Clock
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
name|NotNull
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
comment|/**  * Provides revision related context.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RevisionContext
block|{
comment|/**      * @return the branches of the local DocumentMK instance, which are not yet      *         merged.      */
name|UnmergedBranches
name|getBranches
parameter_list|()
function_decl|;
comment|/**      * @return the pending modifications.      */
name|UnsavedModifications
name|getPendingModifications
parameter_list|()
function_decl|;
comment|/**      * @return the cluster id of the local DocumentMK instance.      */
name|int
name|getClusterId
parameter_list|()
function_decl|;
comment|/**      * @return the current head revision.      */
annotation|@
name|NotNull
name|RevisionVector
name|getHeadRevision
parameter_list|()
function_decl|;
comment|/**      * @return a new revision for the local document node store instance.      */
annotation|@
name|NotNull
name|Revision
name|newRevision
parameter_list|()
function_decl|;
comment|/**      * @return the clock in use when a new revision is created.      */
annotation|@
name|NotNull
name|Clock
name|getClock
parameter_list|()
function_decl|;
comment|/**      * Retrieves the commit value for a given change. This method returns the      * following types of commit values:      *<ul>      *<li>"c" : the change revision is committed as is.</li>      *<li>"c-rX-Y-Z" : the change revision is a branch commit merged in      *          revision "rX-Y-Z".</li>      *<li>"brX-Y-Z" : the change revision is a branch commit done at      *          "rX-Y-Z" but not yet merged.</li>      *<li>{@code null} : the change revision does not have an entry on      *          the commit root document and is not committed.</li>      *</ul>      *      * @param changeRevision the revision a change was made.      * @param doc the document where the change was made.      * @return the commit value or {@code null} if the change does not      *          have a commit value (yet).      */
annotation|@
name|Nullable
name|String
name|getCommitValue
parameter_list|(
annotation|@
name|NotNull
name|Revision
name|changeRevision
parameter_list|,
annotation|@
name|NotNull
name|NodeDocument
name|doc
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

