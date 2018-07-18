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
name|authentication
operator|.
name|credentials
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|ImmutableMap
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
comment|/**  * Composite implementation of the  * {@link org.apache.jackrabbit.oak.spi.security.authentication.credentials.CredentialsSupport}  * interface that handles multiple providers.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CompositeCredentialsSupport
implements|implements
name|CredentialsSupport
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
argument_list|>
name|credentialSupplier
decl_stmt|;
specifier|private
name|CompositeCredentialsSupport
parameter_list|(
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
argument_list|>
name|credentialSupplier
parameter_list|)
block|{
name|this
operator|.
name|credentialSupplier
operator|=
name|credentialSupplier
expr_stmt|;
block|}
specifier|public
specifier|static
name|CredentialsSupport
name|newInstance
parameter_list|(
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
argument_list|>
name|credentialSupplier
parameter_list|)
block|{
return|return
operator|new
name|CompositeCredentialsSupport
argument_list|(
name|credentialSupplier
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Set
argument_list|<
name|Class
argument_list|>
name|getCredentialClasses
parameter_list|()
block|{
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
name|all
init|=
name|this
operator|.
name|credentialSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|all
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|all
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getCredentialClasses
argument_list|()
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|Class
argument_list|>
name|classes
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|CredentialsSupport
name|c
range|:
name|all
control|)
block|{
name|classes
operator|.
name|addAll
argument_list|(
name|c
operator|.
name|getCredentialClasses
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|classes
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|getUserId
parameter_list|(
annotation|@
name|NotNull
name|Credentials
name|credentials
parameter_list|)
block|{
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
name|all
init|=
name|this
operator|.
name|credentialSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|CredentialsSupport
name|c
range|:
name|all
control|)
block|{
name|String
name|userId
init|=
name|c
operator|.
name|getUserId
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
condition|)
block|{
return|return
name|userId
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getAttributes
parameter_list|(
annotation|@
name|NotNull
name|Credentials
name|credentials
parameter_list|)
block|{
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
name|all
init|=
name|this
operator|.
name|credentialSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|all
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|all
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getAttributes
argument_list|(
name|credentials
argument_list|)
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attrs
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|CredentialsSupport
name|c
range|:
name|all
control|)
block|{
name|attrs
operator|.
name|putAll
argument_list|(
name|c
operator|.
name|getAttributes
argument_list|(
name|credentials
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|attrs
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setAttributes
parameter_list|(
annotation|@
name|NotNull
name|Credentials
name|credentials
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|)
block|{
name|boolean
name|set
init|=
literal|false
decl_stmt|;
name|Collection
argument_list|<
name|CredentialsSupport
argument_list|>
name|all
init|=
name|this
operator|.
name|credentialSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|CredentialsSupport
name|c
range|:
name|all
control|)
block|{
name|set
operator|=
name|c
operator|.
name|setAttributes
argument_list|(
name|credentials
argument_list|,
name|attributes
argument_list|)
operator|||
name|set
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

