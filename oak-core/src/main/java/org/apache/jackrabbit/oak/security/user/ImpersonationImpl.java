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
name|user
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
name|HashSet
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
name|Impersonation
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
name|User
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
name|PropertyState
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
name|Tree
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
name|Type
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
name|AdminPrincipal
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
name|GroupPrincipals
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
name|PrincipalImpl
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
name|PrincipalIteratorAdapter
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

begin_import
import|import static
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
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_comment
comment|/**  * ImpersonationImpl...  */
end_comment

begin_class
class|class
name|ImpersonationImpl
implements|implements
name|Impersonation
implements|,
name|UserConstants
block|{
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
name|ImpersonationImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserImpl
name|user
decl_stmt|;
specifier|private
specifier|final
name|PrincipalManager
name|principalManager
decl_stmt|;
name|ImpersonationImpl
parameter_list|(
annotation|@
name|NotNull
name|UserImpl
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|principalManager
operator|=
name|user
operator|.
name|getUserManager
argument_list|()
operator|.
name|getPrincipalManager
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------< Impersonation>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrincipalIterator
name|getImpersonators
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|impersonators
init|=
name|getImpersonatorNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|impersonators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|PrincipalIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|pName
range|:
name|impersonators
control|)
block|{
name|Principal
name|p
init|=
name|principalManager
operator|.
name|getPrincipal
argument_list|(
name|pName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Impersonator {} does not correspond to a known Principal."
argument_list|,
name|pName
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|PrincipalImpl
argument_list|(
name|pName
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PrincipalIteratorAdapter
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|grantImpersonation
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isValidPrincipal
argument_list|(
name|principal
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|principalName
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// make sure user does not impersonate himself
name|Tree
name|userTree
init|=
name|user
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|PropertyState
name|prop
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
operator|&&
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot grant impersonation to oneself."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|user
operator|.
name|getUserManager
argument_list|()
operator|.
name|onImpersonation
argument_list|(
name|user
argument_list|,
name|principal
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|impersonators
init|=
name|getImpersonatorNames
argument_list|(
name|userTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|impersonators
operator|.
name|add
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|updateImpersonatorNames
argument_list|(
name|userTree
argument_list|,
name|impersonators
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|revokeImpersonation
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|pName
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
name|user
operator|.
name|getUserManager
argument_list|()
operator|.
name|onImpersonation
argument_list|(
name|user
argument_list|,
name|principal
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Tree
name|userTree
init|=
name|user
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|impersonators
init|=
name|getImpersonatorNames
argument_list|(
name|userTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|impersonators
operator|.
name|remove
argument_list|(
name|pName
argument_list|)
condition|)
block|{
name|updateImpersonatorNames
argument_list|(
name|userTree
argument_list|,
name|impersonators
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allows
parameter_list|(
annotation|@
name|NotNull
name|Subject
name|subject
parameter_list|)
block|{
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
name|principalNames
operator|.
name|add
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|allows
init|=
name|getImpersonatorNames
argument_list|()
operator|.
name|removeAll
argument_list|(
name|principalNames
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allows
condition|)
block|{
comment|// check if subject belongs to administrator user
for|for
control|(
name|Principal
name|principal
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
if|if
condition|(
name|isAdmin
argument_list|(
name|principal
argument_list|)
condition|)
block|{
name|allows
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|allows
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|NotNull
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getImpersonatorNames
parameter_list|()
block|{
return|return
name|getImpersonatorNames
argument_list|(
name|user
operator|.
name|getTree
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getImpersonatorNames
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|princNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|PropertyState
name|impersonators
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|REP_IMPERSONATORS
argument_list|)
decl_stmt|;
if|if
condition|(
name|impersonators
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|v
range|:
name|impersonators
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|princNames
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|princNames
return|;
block|}
specifier|private
name|void
name|updateImpersonatorNames
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
parameter_list|)
block|{
if|if
condition|(
name|principalNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|userTree
operator|.
name|removeProperty
argument_list|(
name|REP_IMPERSONATORS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userTree
operator|.
name|setProperty
argument_list|(
name|REP_IMPERSONATORS
argument_list|,
name|principalNames
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isAdmin
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
name|AdminPrincipal
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|GroupPrincipals
operator|.
name|isGroup
argument_list|(
name|principal
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
try|try
block|{
name|Authorizable
name|authorizable
init|=
name|user
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
decl_stmt|;
return|return
name|authorizable
operator|!=
literal|null
operator|&&
operator|!
name|authorizable
operator|.
name|isGroup
argument_list|()
operator|&&
operator|(
operator|(
name|User
operator|)
name|authorizable
operator|)
operator|.
name|isAdmin
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
literal|false
return|;
block|}
block|}
block|}
specifier|private
name|boolean
name|isValidPrincipal
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
name|Principal
name|p
init|=
literal|null
decl_stmt|;
comment|// shortcut for TreeBasedPrincipal
if|if
condition|(
name|principal
operator|instanceof
name|TreeBasedPrincipal
condition|)
block|{
try|try
block|{
name|Authorizable
name|otherUser
init|=
name|user
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherUser
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|otherUser
operator|.
name|getPrincipal
argument_list|()
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
block|}
else|else
block|{
name|p
operator|=
name|principalManager
operator|.
name|getPrincipal
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot grant impersonation to an unknown principal."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|GroupPrincipals
operator|.
name|isGroup
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot grant impersonation to a principal that is a Group."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// make sure the given principal doesn't refer to the admin user.
if|if
condition|(
name|isAdmin
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Admin principal is already granted impersonation."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

