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
name|principal
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_comment
comment|/**  * This interface defines the principal manager which is the clients view on all  * principals known to the repository. Each principal manager is bound to a  * session and is restricted by the respective access control. The principal  * manager in addition provides basic search facilities.  *<p>  * A<strong>{@link Principal}</strong> is an object used to connect to any kind  * of security mechanism. Example for this are the  * {@link javax.security.auth.spi.LoginModule login modules} that use principals  * to process the login procedure.<br>  * A principal can be a member of a<strong>{@link GroupPrincipal}</strong>. A  * group is a principal itself and can therefore be a member of a group again.  *<p>  * Please note the following security considerations that need to be respected  * when implementing the PrincipalManager: All principals returned by this  * manager as well as {@link GroupPrincipal#members()} must respect access  * restrictions that may be present for the<code>Session</code> this manager  * has been built for. The same applies for  * {@link #getGroupMembership(Principal)}.  *<p>  * Since Jackrabbit 2.18, a new interface has been introduced to represent the  * concept of a group of principals: {@link GroupPrincipal}, alongside  * {@code java.security.acl.Group} which is deprecated to be deleted. Until the  * final deletion of {@code java.security.acl.Group}, the 2 interfaces will be  * used concurrently for backwards compatibility reasons. See JCR-4249 for more  * details.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrincipalManager
block|{
comment|/**      * Filter flag indicating that only<code>Principal</code>s that do NOT      * represent a group should be searched and returned.      */
name|int
name|SEARCH_TYPE_NOT_GROUP
init|=
literal|1
decl_stmt|;
comment|/**      * Filter flag indicating that only<code>Principal</code>s that represent      * a group of Principals should be searched and returned.      */
name|int
name|SEARCH_TYPE_GROUP
init|=
literal|2
decl_stmt|;
comment|/**      * Filter flag indicating that all<code>Principal</code>s should be search      * irrespective whether they represent a group of Principals or not.      */
name|int
name|SEARCH_TYPE_ALL
init|=
literal|3
decl_stmt|;
comment|/**      * Checks if the principal with the given name is known to this manager      * (in respect to the sessions access rights). If this method returns      *<code>true</code> then the following expression evaluates to<code>true</code>      * as well:<code>PrincipalManager.getPrincipal(name).getName().equals(name)</code>      *      * @param principalName the name of the principal to check      * @return return<code>true</code> if the principal with this name is known      *         to this manager;<code>false</code> otherwise.      */
name|boolean
name|hasPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
function_decl|;
comment|/**      * Returns the principal with the given name if is known to this manager      * (with respect to the sessions access rights).      * Please note that due to security reasons group principals will only      * reveal those members that are visible to the Session this      *<code>PrincipalManager</code> has been built for.      *      * @param principalName the name of the principal to retrieve      * @return return the requested principal or<code>null</code> if a      * principal with the given name does not exist or is not accessible      * for the editing session.      */
annotation|@
name|Nullable
name|Principal
name|getPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
function_decl|;
comment|/**      * Gets the principals matching a simple filter expression applied against      * the {@link Principal#getName() principal name}.      * TODO: define the filter expression.<br>      * An implementation may limit the number of principals returned.      * If there are no matching principals, an empty iterator is returned.      *      * @param simpleFilter      * @return a<code>PrincipalIterator</code> over the<code>Principal</code>s      * matching the given filter.      */
annotation|@
name|NotNull
name|PrincipalIterator
name|findPrincipals
parameter_list|(
annotation|@
name|Nullable
name|String
name|simpleFilter
parameter_list|)
function_decl|;
comment|/**      * Gets the principals matching a simple filter expression applied against      * the {@link Principal#getName() principal name} AND the specified search      * type.      * TODO: define the filter expression.<br>      * An implementation may limit the number of principals returned.      * If there are no matching principals, an empty iterator is returned.      *      * @param simpleFilter      * @param searchType Any of the following constants:      *<ul>      *<li>{@link PrincipalManager#SEARCH_TYPE_ALL}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_GROUP}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_NOT_GROUP}</li>      *</ul>      * @return a<code>PrincipalIterator</code> over the<code>Principal</code>s      * matching the given filter and search type.      */
annotation|@
name|NotNull
name|PrincipalIterator
name|findPrincipals
parameter_list|(
annotation|@
name|Nullable
name|String
name|simpleFilter
parameter_list|,
name|int
name|searchType
parameter_list|)
function_decl|;
comment|/**      * Returns all<code>Principal</code>s matching the specified search type.      *      * @param searchType Any of the following constants:      *<ul>      *<li>{@link PrincipalManager#SEARCH_TYPE_ALL}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_GROUP}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_NOT_GROUP}</li>      *</ul>      * @return a<code>PrincipalIterator</code> over all the<code>Principal</code>s      * matching the given search type.      */
annotation|@
name|NotNull
name|PrincipalIterator
name|getPrincipals
parameter_list|(
name|int
name|searchType
parameter_list|)
function_decl|;
comment|/**      * Returns an iterator over all group principals for which the given      * principal is either direct or indirect member of.      *<p>      * Example:<br>      * If Principal P is member of Group A, and Group A is member of      * Group B, this method will return Principal A and Principal B.      *      * @param principal the principal to return it's membership from.      * @return an iterator returning all groups the given principal is member of.      */
annotation|@
name|NotNull
name|PrincipalIterator
name|getGroupMembership
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
function_decl|;
comment|/**      * Returns the<code>Principal</code> which is implicitly applied to      * every subject.      *      * @return the 'everyone' principal      */
annotation|@
name|NotNull
name|Principal
name|getEveryone
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

