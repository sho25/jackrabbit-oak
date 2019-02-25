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
name|PrincipalIterator
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
name|PrincipalManager
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
comment|/**  * Default implementation of the {@code PrincipalManager} interface.  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalManagerImpl
implements|implements
name|PrincipalQueryManager
implements|,
name|PrincipalManager
block|{
specifier|private
specifier|final
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|public
name|PrincipalManagerImpl
parameter_list|(
annotation|@
name|NotNull
name|PrincipalProvider
name|principalProvider
parameter_list|)
block|{
name|this
operator|.
name|principalProvider
operator|=
name|principalProvider
expr_stmt|;
block|}
comment|//---------------------------------------------------< PrincipalManager>---
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
block|{
return|return
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Principal
name|getPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|)
block|{
return|return
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|PrincipalIterator
name|findPrincipals
parameter_list|(
annotation|@
name|Nullable
name|String
name|simpleFilter
parameter_list|)
block|{
return|return
name|findPrincipals
argument_list|(
name|simpleFilter
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
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
block|{
return|return
operator|new
name|PrincipalIteratorAdapter
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|simpleFilter
argument_list|,
name|searchType
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|PrincipalIterator
name|getPrincipals
parameter_list|(
name|int
name|searchType
parameter_list|)
block|{
return|return
operator|new
name|PrincipalIteratorAdapter
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|searchType
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|PrincipalIterator
name|getGroupMembership
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
return|return
operator|new
name|PrincipalIteratorAdapter
argument_list|(
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
name|principal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Principal
name|getEveryone
parameter_list|()
block|{
name|Principal
name|everyone
init|=
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|everyone
operator|==
literal|null
condition|)
block|{
name|everyone
operator|=
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
return|return
name|everyone
return|;
block|}
annotation|@
name|Override
specifier|public
name|PrincipalIterator
name|findPrincipals
parameter_list|(
name|String
name|simpleFilter
parameter_list|,
name|int
name|searchType
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|limit
parameter_list|)
block|{
return|return
operator|new
name|PrincipalIteratorAdapter
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|simpleFilter
argument_list|,
name|searchType
argument_list|,
name|offset
argument_list|,
name|limit
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

