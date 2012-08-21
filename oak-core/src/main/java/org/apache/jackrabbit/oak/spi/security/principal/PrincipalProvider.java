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
name|spi
operator|.
name|security
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * PrincipalProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrincipalProvider
block|{
comment|/**      * Returns the principal with the specified name or {@code null} if the      * principal does not exist.      *      * @param principalName the name of the principal to retrieve      * @return return the requested principal or {@code null}      */
annotation|@
name|CheckForNull
name|Principal
name|getPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|)
function_decl|;
comment|/**      * Returns an iterator over all group principals for which the given      * principal is either direct or indirect member of. Thus for any principal      * returned in the iterator {@link java.security.acl.Group#isMember(Principal)}      * must return {@code true}.      *<p/>      * Example:<br>      * If Principal is member of Group A, and Group A is member of      * Group B, this method will return Group A and Group B.      *      * @param principal the principal to return it's membership from.      * @return an iterator returning all groups the given principal is member of.      * @see java.security.acl.Group#isMember(java.security.Principal)      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupMembership
parameter_list|(
name|Principal
name|principal
parameter_list|)
function_decl|;
comment|/**      * Tries to resolve the specified {@code userID} to a valid principal and      * it's group membership. This method returns an empty set if the      * specified ID cannot be resolved.      *      * @param userID A userID.      * @return The set of principals associated with the specified {@code userID}      * or an empty set if it cannot be resolved.      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|(
name|String
name|userID
parameter_list|)
function_decl|;
comment|/**      * Find the principals that match the specified nameHint and search type.      *      * @param nameHint A name hint to use for non-exact matching.      * @param searchType Limit the search to certain types of principals. Valid      * values are any of      *<ul><li>{@link org.apache.jackrabbit.api.security.principal.PrincipalManager#SEARCH_TYPE_ALL}</li></ul>      *<ul><li>{@link org.apache.jackrabbit.api.security.principal.PrincipalManager#SEARCH_TYPE_NOT_GROUP}</li></ul>      *<ul><li>{@link org.apache.jackrabbit.api.security.principal.PrincipalManager#SEARCH_TYPE_GROUP}</li></ul>      * @return An iterator of principals.      */
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|Principal
argument_list|>
name|findPrincipals
parameter_list|(
name|String
name|nameHint
parameter_list|,
name|int
name|searchType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

