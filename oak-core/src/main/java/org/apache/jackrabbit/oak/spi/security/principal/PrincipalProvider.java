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
name|Set
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
name|Principal
name|getPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|)
function_decl|;
comment|/**      * Returns an iterator over all group principals for which the given      * principal is either direct or indirect member of. Thus for any principal      * returned in the iterator {@link java.security.acl.Group#isMember(Principal)}      * must return {@code true}.      *<p/>      * Example:<br>      * If Principal is member of Group A, and Group A is member of      * Group B, this method will return Group A and Group B.      *      * @param principal the principal to return it's membership from.      * @return an iterator returning all groups the given principal is member of.      * @see java.security.acl.Group#isMember(java.security.Principal)      */
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
block|}
end_interface

end_unit

