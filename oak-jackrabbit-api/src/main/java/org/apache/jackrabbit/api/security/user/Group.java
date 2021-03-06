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
name|api
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * A Group is a collection of {@link #getMembers() Authorizable}s.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Group
extends|extends
name|Authorizable
block|{
comment|/**      * @return Iterator of<code>Authorizable</code>s which are declared      * members of this Group.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getDeclaredMembers
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * @return Iterator of<code>Authorizable</code>s which are members of      * this Group. This includes both declared members and all authorizables      * that are indirect group members.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|NotNull
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Test whether an {@link Authorizable} is a declared member of this group.      * @param authorizable  The<code>Authorizable</code> to test.      * @return<code>true</code> if the Authorizable to test is a direct member      * @throws RepositoryException  If an error occurs.      */
name|boolean
name|isDeclaredMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * @param authorizable The<code>Authorizable</code> to test.      * @return true if the Authorizable to test is a direct or indirect member      * of this Group.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Add a member to this Group.      *      * @param authorizable The<code>Authorizable</code> to be added as      * member to this group.      * @return true if the<code>Authorizable</code> has successfully been added      * to this Group, false otherwise (e.g. unknown implementation      * or if it already is a member or if the passed authorizable is this      * group itself or for some implementation specific constraint).      * @throws RepositoryException If an error occurs.      */
name|boolean
name|addMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Add one or more member(s) to this Group. Note, that an implementation may      * define circumstances under which this method allows to add non-existing      * {@code Authorizable}s as new members. Also an implementation may choose to      * (partially) postpone validation/verification util {@link Session#save()}.      *      * @param memberIds The {@code Id}s of the authorizables to be added as      * members to this group.      * @return a set of those {@code memberIds} that could not be added or an      * empty set of all ids have been successfully processed. The former may include      * those cases where a given id cannot be resolved to an existing authorizable,      * one that is already member or if adding the member would create a      * cyclic group membership.      * @throws RepositoryException If one of the specified memberIds is invalid or      * if some other error occurs.      */
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|addMembers
parameter_list|(
annotation|@
name|NotNull
name|String
modifier|...
name|memberIds
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Remove a member from this Group.      *      * @param authorizable The<code>Authorizable</code> to be removed from      * the list of group members.      * @return true if the Authorizable was successfully removed. False otherwise.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|removeMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Remove one or several members from this Group. Note, that an implementation      * may define circumstances under which this method allows to remove members      * that (no longer) exist. An implementation may choose to (partially)      * postpone validation/verification util {@link Session#save()}.      *      * @param memberIds The {@code Id}s of the authorizables to be removed      * from the members of this group.      * @return a set of those {@code memberIds} that could not be removed or an      * empty set if all ids have been successfully processed. The former may include      * those cases where a given id cannot be resolved to an existing authorizable.      * @throws RepositoryException If one of the specified memberIds is invalid      * or if some other error occurs.      */
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|removeMembers
parameter_list|(
annotation|@
name|NotNull
name|String
modifier|...
name|memberIds
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

