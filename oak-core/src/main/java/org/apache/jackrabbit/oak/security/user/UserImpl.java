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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|security
operator|.
name|principal
operator|.
name|AdminPrincipalImpl
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
name|TreeBasedPrincipal
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
name|AuthorizableType
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
name|util
operator|.
name|PasswordUtility
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
name|util
operator|.
name|UserUtility
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
name|util
operator|.
name|NodeUtil
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
name|STRING
import|;
end_import

begin_comment
comment|/**  * UserImpl...  */
end_comment

begin_class
class|class
name|UserImpl
extends|extends
name|AuthorizableImpl
implements|implements
name|User
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
name|UserImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAdmin
decl_stmt|;
name|UserImpl
parameter_list|(
name|String
name|id
parameter_list|,
name|Tree
name|tree
parameter_list|,
name|UserManagerImpl
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|id
argument_list|,
name|tree
argument_list|,
name|userManager
argument_list|)
expr_stmt|;
name|isAdmin
operator|=
name|UserUtility
operator|.
name|getAdminId
argument_list|(
name|userManager
operator|.
name|getConfig
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|void
name|checkValidTree
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|UserUtility
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid user node: node type rep:User expected."
argument_list|)
throw|;
block|}
block|}
comment|//-------------------------------------------------------< Authorizable>---
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#isGroup()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getPrincipal()      */
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|userTree
init|=
name|getTree
argument_list|()
decl_stmt|;
name|String
name|principalName
init|=
name|getPrincipalName
argument_list|(
name|userTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAdmin
argument_list|()
condition|)
block|{
return|return
operator|new
name|AdminPrincipalImpl
argument_list|(
name|principalName
argument_list|,
name|userTree
argument_list|,
name|getUserManager
argument_list|()
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TreeBasedPrincipal
argument_list|(
name|principalName
argument_list|,
name|userTree
argument_list|,
name|getUserManager
argument_list|()
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|//---------------------------------------------------------------< User>---
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#isAdmin()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isAdmin
parameter_list|()
block|{
return|return
name|isAdmin
return|;
block|}
comment|/**      * Always throws {@code UnsupportedRepositoryOperationException}      *      * @see org.apache.jackrabbit.api.security.user.User#getCredentials()      */
annotation|@
name|Override
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
operator|new
name|CredentialsImpl
argument_list|(
name|getID
argument_list|()
argument_list|,
name|getPasswordHash
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#getImpersonation()      */
annotation|@
name|Override
specifier|public
name|Impersonation
name|getImpersonation
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|ImpersonationImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#changePassword(String)      */
annotation|@
name|Override
specifier|public
name|void
name|changePassword
parameter_list|(
name|String
name|password
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Attempt to set 'null' password for user "
operator|+
name|getID
argument_list|()
argument_list|)
throw|;
block|}
name|UserManagerImpl
name|userManager
init|=
name|getUserManager
argument_list|()
decl_stmt|;
name|userManager
operator|.
name|onPasswordChange
argument_list|(
name|this
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|userManager
operator|.
name|setPassword
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|password
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#changePassword(String, String)      */
annotation|@
name|Override
specifier|public
name|void
name|changePassword
parameter_list|(
name|String
name|password
parameter_list|,
name|String
name|oldPassword
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// make sure the old password matches.
name|String
name|pwHash
init|=
name|getPasswordHash
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|oldPassword
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to change password: Old password does not match."
argument_list|)
throw|;
block|}
name|changePassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#disable(String)      */
annotation|@
name|Override
specifier|public
name|void
name|disable
parameter_list|(
name|String
name|reason
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isAdmin
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The administrator user cannot be disabled."
argument_list|)
throw|;
block|}
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|reason
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|tree
operator|.
name|hasProperty
argument_list|(
name|REP_DISABLED
argument_list|)
condition|)
block|{
comment|// enable the user again.
name|tree
operator|.
name|removeProperty
argument_list|(
name|REP_DISABLED
argument_list|)
expr_stmt|;
block|}
comment|// else: not disabled -> nothing to
block|}
else|else
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|REP_DISABLED
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#isDisabled()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isDisabled
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|REP_DISABLED
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.User#getDisabledReason()      */
annotation|@
name|Override
specifier|public
name|String
name|getDisabledReason
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyState
name|disabled
init|=
name|getTree
argument_list|()
operator|.
name|getProperty
argument_list|(
name|REP_DISABLED
argument_list|)
decl_stmt|;
if|if
condition|(
name|disabled
operator|!=
literal|null
condition|)
block|{
return|return
name|disabled
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|CheckForNull
specifier|private
name|String
name|getPasswordHash
parameter_list|()
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|n
operator|.
name|getString
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

