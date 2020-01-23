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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
specifier|static
specifier|final
name|GroupTransformer
name|TRANSFORMER
init|=
operator|new
name|GroupTransformer
argument_list|()
decl_stmt|;
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
name|Group
operator|||
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
name|Group
condition|)
block|{
return|return
operator|(
operator|(
name|Group
operator|)
name|principal
operator|)
operator|.
name|members
argument_list|()
return|;
block|}
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
name|Group
condition|)
block|{
return|return
operator|(
operator|(
name|Group
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
annotation|@
name|NotNull
specifier|public
specifier|static
name|Set
argument_list|<
name|Principal
argument_list|>
name|transform
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|Principal
argument_list|>
name|g2
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Group
name|g
range|:
name|groups
control|)
block|{
name|g2
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipalWrapper
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|g2
operator|.
name|build
argument_list|()
return|;
block|}
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
name|transform
parameter_list|(
annotation|@
name|NotNull
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Principal
argument_list|>
name|m2
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|forEnumeration
argument_list|(
name|members
argument_list|)
argument_list|,
name|TRANSFORMER
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
name|m2
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|GroupTransformer
implements|implements
name|Function
argument_list|<
name|Principal
argument_list|,
name|Principal
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
name|Principal
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|instanceof
name|Group
condition|)
block|{
return|return
operator|new
name|GroupPrincipalWrapper
argument_list|(
operator|(
name|Group
operator|)
name|input
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|input
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

