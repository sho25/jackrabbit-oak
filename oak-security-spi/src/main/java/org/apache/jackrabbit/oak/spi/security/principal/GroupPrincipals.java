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
name|util
operator|.
name|Collections
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
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|GroupPrincipal
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
comment|/**  * Helper class to deal with the migration between the 2 types of groups  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|GroupPrincipals
block|{
specifier|private
name|GroupPrincipals
parameter_list|()
block|{     }
comment|/**      * Checks if the provided principal is a group.      *      * @param principal      *            to be checked.      *      * @return true if the principal is of type group.      */
specifier|public
specifier|static
name|boolean
name|isGroup
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|principal
operator|instanceof
name|GroupPrincipal
return|;
block|}
comment|/**      * Returns an enumeration of the members in the group.      * @param principal the principal whose membership is listed.      * @return an enumeration of the group members.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|GroupPrincipal
condition|)
block|{
return|return
operator|(
operator|(
name|GroupPrincipal
operator|)
name|principal
operator|)
operator|.
name|members
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|emptyEnumeration
argument_list|()
return|;
block|}
comment|/**      * Returns true if the passed principal is a member of the group.      * @param principal the principal whose members are being checked.      * @param member the principal whose membership is to be checked.      * @return true if the principal is a member of this group, false otherwise.      */
specifier|public
specifier|static
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|Principal
name|member
parameter_list|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|GroupPrincipal
condition|)
block|{
return|return
operator|(
operator|(
name|GroupPrincipal
operator|)
name|principal
operator|)
operator|.
name|isMember
argument_list|(
name|member
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

