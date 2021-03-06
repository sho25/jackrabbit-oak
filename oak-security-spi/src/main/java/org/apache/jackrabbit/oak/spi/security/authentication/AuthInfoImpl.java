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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|Objects
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|AuthInfo
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
comment|/**  * Default implementation of the AuthInfo interface.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AuthInfoImpl
implements|implements
name|AuthInfo
block|{
specifier|private
specifier|final
name|String
name|userID
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|public
name|AuthInfoImpl
parameter_list|(
annotation|@
name|Nullable
name|String
name|userID
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|this
argument_list|(
name|userID
argument_list|,
name|attributes
argument_list|,
operator|(
name|Iterable
operator|)
name|principals
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AuthInfoImpl
parameter_list|(
annotation|@
name|Nullable
name|String
name|userID
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|,
annotation|@
name|Nullable
name|Iterable
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|this
operator|.
name|userID
operator|=
name|userID
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
operator|(
name|attributes
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|emptyMap
argument_list|()
else|:
name|attributes
expr_stmt|;
name|this
operator|.
name|principals
operator|=
operator|(
name|principals
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|emptySet
argument_list|()
else|:
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principals
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|AuthInfo
name|createFromSubject
parameter_list|(
annotation|@
name|NotNull
name|Subject
name|subject
parameter_list|)
block|{
name|Set
argument_list|<
name|AuthInfo
argument_list|>
name|infoSet
init|=
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|AuthInfo
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|SimpleCredentials
argument_list|>
name|scs
init|=
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|SimpleCredentials
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|userId
init|=
operator|(
name|scs
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|scs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getUserID
argument_list|()
decl_stmt|;
return|return
operator|new
name|AuthInfoImpl
argument_list|(
name|userId
argument_list|,
literal|null
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|infoSet
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"userID"
argument_list|,
name|userID
argument_list|)
operator|.
name|add
argument_list|(
literal|"attributes"
argument_list|,
name|attributes
argument_list|)
operator|.
name|add
argument_list|(
literal|"principals"
argument_list|,
name|principals
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//-----------------------------------------------------------< AuthInfo>---
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|userID
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
return|return
name|attributes
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|attributeName
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|principals
return|;
block|}
block|}
end_class

end_unit

