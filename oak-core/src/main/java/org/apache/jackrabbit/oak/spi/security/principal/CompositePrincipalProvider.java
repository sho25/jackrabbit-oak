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
name|HashSet
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
name|List
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * {@code PrincipalProvider} implementation that aggregates a list of principal  * providers into a single.  */
end_comment

begin_class
specifier|public
class|class
name|CompositePrincipalProvider
implements|implements
name|PrincipalProvider
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|PrincipalProvider
argument_list|>
name|providers
decl_stmt|;
specifier|public
name|CompositePrincipalProvider
parameter_list|(
name|List
argument_list|<
name|PrincipalProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|=
name|checkNotNull
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------< PrincipalProvider>---
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
name|Principal
name|principal
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|providers
operator|.
name|size
argument_list|()
operator|&&
name|principal
operator|==
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|principal
operator|=
name|providers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
block|}
return|return
name|principal
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
name|Set
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PrincipalProvider
name|provider
range|:
name|providers
control|)
block|{
name|groups
operator|.
name|addAll
argument_list|(
name|provider
operator|.
name|getGroupMembership
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|groups
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
name|getPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userID
parameter_list|)
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PrincipalProvider
name|provider
range|:
name|providers
control|)
block|{
name|principals
operator|.
name|addAll
argument_list|(
name|provider
operator|.
name|getPrincipals
argument_list|(
name|userID
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|principals
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
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
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
index|[]
name|iterators
init|=
operator|new
name|Iterator
index|[
name|providers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PrincipalProvider
name|provider
range|:
name|providers
control|)
block|{
if|if
condition|(
name|nameHint
operator|==
literal|null
condition|)
block|{
name|iterators
index|[
name|i
operator|++
index|]
operator|=
name|provider
operator|.
name|findPrincipals
argument_list|(
name|searchType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iterators
index|[
name|i
operator|++
index|]
operator|=
name|provider
operator|.
name|findPrincipals
argument_list|(
name|nameHint
argument_list|,
name|searchType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|iterators
argument_list|)
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
name|findPrincipals
argument_list|(
literal|null
argument_list|,
name|searchType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

