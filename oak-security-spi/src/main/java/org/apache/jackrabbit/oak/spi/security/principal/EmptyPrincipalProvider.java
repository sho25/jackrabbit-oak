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
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@code PrincipalProvider} interface that never  * returns any principals.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|EmptyPrincipalProvider
implements|implements
name|PrincipalProvider
block|{
specifier|public
specifier|static
specifier|final
name|PrincipalProvider
name|INSTANCE
init|=
operator|new
name|EmptyPrincipalProvider
argument_list|()
decl_stmt|;
specifier|private
name|EmptyPrincipalProvider
parameter_list|()
block|{}
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupMembership
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getMembershipPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|getPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userID
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|findPrincipals
parameter_list|(
annotation|@
name|Nullable
name|String
name|nameHint
parameter_list|,
name|int
name|searchType
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|findPrincipals
parameter_list|(
name|int
name|searchType
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

