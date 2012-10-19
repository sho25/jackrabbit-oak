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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|base
operator|.
name|Predicates
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
name|user
operator|.
name|Authorizable
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
name|user
operator|.
name|UserManager
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalProvider
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConfiguration
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * The {@code PrincipalProviderImpl} is a principal provider implementation  * that operates on principal information read from user information exposed by  * the configured {@link UserManager}.  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalProviderImpl
implements|implements
name|PrincipalProvider
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PrincipalProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserManager
name|userManager
decl_stmt|;
specifier|public
name|PrincipalProviderImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|UserConfiguration
name|userConfiguration
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|userManager
operator|=
name|userConfiguration
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
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
specifier|final
name|String
name|principalName
parameter_list|)
block|{
name|Authorizable
name|authorizable
init|=
name|getAuthorizable
argument_list|(
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|principalName
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|authorizable
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no such principal or error while accessing principal from user/group
return|return
operator|(
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
operator|)
condition|?
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupMembership
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
name|Authorizable
name|authorizable
init|=
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getGroupMembership
argument_list|(
name|authorizable
argument_list|)
return|;
block|}
block|}
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
try|try
block|{
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
operator|&&
operator|!
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|principals
operator|.
name|add
argument_list|(
name|authorizable
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|principals
operator|.
name|addAll
argument_list|(
name|getGroupMembership
argument_list|(
name|authorizable
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|principals
return|;
block|}
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
name|String
name|nameHint
parameter_list|,
name|int
name|searchType
parameter_list|)
block|{
try|try
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
init|=
name|userManager
operator|.
name|findAuthorizables
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|nameHint
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|authorizables
argument_list|,
name|Predicates
operator|.
expr|<
name|Object
operator|>
name|notNull
argument_list|()
argument_list|)
argument_list|,
operator|new
name|AuthorizableToPrincipal
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Authorizable
name|getAuthorizable
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
try|try
block|{
return|return
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error while retrieving principal: "
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupMembership
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
name|Set
argument_list|<
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
argument_list|>
name|groupPrincipals
init|=
operator|new
name|HashSet
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
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
name|user
operator|.
name|Group
argument_list|>
name|groups
init|=
name|authorizable
operator|.
name|memberOf
argument_list|()
decl_stmt|;
while|while
condition|(
name|groups
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|grPrincipal
init|=
name|groups
operator|.
name|next
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|grPrincipal
operator|instanceof
name|Group
condition|)
block|{
name|groupPrincipals
operator|.
name|add
argument_list|(
operator|(
name|Group
operator|)
name|grPrincipal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|groupPrincipals
operator|.
name|add
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|groupPrincipals
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Function to covert an authorizable tree to a principal.      */
specifier|private
specifier|final
class|class
name|AuthorizableToPrincipal
implements|implements
name|Function
argument_list|<
name|Authorizable
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
name|Authorizable
name|authorizable
parameter_list|)
block|{
try|try
block|{
return|return
name|authorizable
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

