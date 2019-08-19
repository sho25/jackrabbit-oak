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
name|util
operator|.
name|Enumeration
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
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * This interface is used to represent a group of principals. It is meant to  * replace the deprecated {@code java.security.acl.Group}.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|GroupPrincipal
extends|extends
name|Principal
block|{
comment|/**      * Returns true if the passed principal is a member of the group.      * This method does a recursive search, so if a principal belongs to a      * group which is a member of this group, true is returned.      *      * @param member the principal whose membership is to be checked.      * @return true if the principal is a member of this group,      * false otherwise.      */
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|member
parameter_list|)
function_decl|;
comment|/**      * Returns an enumeration of the members in the group. This includes both      * declared members and all principals that are indirect group members. The      * returned objects can be instances of either Principal or GroupPrincipal      * (which is a subclass of Principal).      *      * @return an enumeration of the group members.      */
annotation|@
name|NotNull
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

