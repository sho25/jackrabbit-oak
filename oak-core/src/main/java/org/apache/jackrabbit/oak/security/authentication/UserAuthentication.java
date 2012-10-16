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
name|authentication
package|;
end_package

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
name|GuestCredentials
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|Authentication
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
name|authentication
operator|.
name|ImpersonationCredentials
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
name|UserProvider
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
comment|/**  * Implementation of the Authentication interface that validates credentials  * against user information stored in the repository. If no user exists with  * the specified userID or if the user has been disabled authentication will  * will fail irrespective of the specified credentials. Otherwise the following  * validation is performed:  *  *<ul>  *<li>{@link SimpleCredentials}: Authentication succeeds if userID and  *     password match the information exposed by the {@link UserProvider}.</li>  *<li>{@link ImpersonationCredentials}: Authentication succeeds if the  *     subject to be authenticated is allowed to impersonate the user identified  *     by the userID.</li>  *<li>{@link GuestCredentials}: The authentication succeeds if an 'anonymous'  *     user exists in the repository.</li>  *</ul>  *  * For any other credentials {@link #authenticate(javax.jcr.Credentials)}  * will return {@code false} indicating that this implementation is not able  * to verify their validity.  */
end_comment

begin_class
specifier|public
class|class
name|UserAuthentication
implements|implements
name|Authentication
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
name|UserAuthentication
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|UserProvider
name|userProvider
decl_stmt|;
specifier|private
specifier|final
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|public
name|UserAuthentication
parameter_list|(
name|String
name|userId
parameter_list|,
name|UserProvider
name|userProvider
parameter_list|,
name|PrincipalProvider
name|principalProvider
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|principalProvider
operator|=
name|principalProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|LoginException
block|{
if|if
condition|(
name|userId
operator|==
literal|null
operator|||
name|userProvider
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Tree
name|userTree
init|=
name|userProvider
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
decl_stmt|;
if|if
condition|(
name|userTree
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Unknown user "
operator|+
name|userId
argument_list|)
throw|;
block|}
if|if
condition|(
name|userProvider
operator|.
name|isDisabled
argument_list|(
name|userTree
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"User with ID "
operator|+
name|userId
operator|+
literal|" has been disabled."
argument_list|)
throw|;
block|}
name|boolean
name|success
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|SimpleCredentials
name|creds
init|=
operator|(
name|SimpleCredentials
operator|)
name|credentials
decl_stmt|;
name|success
operator|=
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|userProvider
operator|.
name|getPasswordHash
argument_list|(
name|userTree
argument_list|)
argument_list|,
name|creds
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|checkSuccess
argument_list|(
name|success
argument_list|,
literal|"UserId/Password mismatch."
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|credentials
operator|instanceof
name|ImpersonationCredentials
condition|)
block|{
name|AuthInfo
name|info
init|=
operator|(
operator|(
name|ImpersonationCredentials
operator|)
name|credentials
operator|)
operator|.
name|getImpersonatorInfo
argument_list|()
decl_stmt|;
name|success
operator|=
name|impersonate
argument_list|(
name|info
argument_list|,
name|userTree
argument_list|)
expr_stmt|;
name|checkSuccess
argument_list|(
name|success
argument_list|,
literal|"Impersonation not allowed."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// guest login is allowed if an anonymous user exists in the content (see get user above)
name|success
operator|=
operator|(
name|credentials
operator|instanceof
name|GuestCredentials
operator|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|void
name|checkSuccess
parameter_list|(
name|boolean
name|success
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|LoginException
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|impersonate
parameter_list|(
name|AuthInfo
name|info
parameter_list|,
name|Tree
name|userTree
parameter_list|)
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|info
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|userProvider
operator|.
name|getImpersonation
argument_list|(
name|userTree
argument_list|,
name|principalProvider
argument_list|)
operator|.
name|allows
argument_list|(
name|subject
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
literal|"Error while validating impersonation"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

