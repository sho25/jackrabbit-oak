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
name|concurrent
operator|.
name|TimeUnit
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
name|AccountLockedException
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
name|AccountNotFoundException
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
name|FailedLoginException
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
name|CredentialExpiredException
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
name|CommitFailedException
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
name|ConfigurationParameters
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
name|authentication
operator|.
name|PreAuthenticatedLogin
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
name|PasswordUtil
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
comment|/**  * Implementation of the Authentication interface that validates credentials  * against user information stored in the repository. If no user exists with  * the specified userID or if the user has been disabled authentication will  * will fail irrespective of the specified credentials. Otherwise the following  * validation is performed:  *  *<ul>  *<li>{@link SimpleCredentials}: Authentication succeeds if userID and  *     password match the information exposed by the {@link UserManager}.</li>  *<li>{@link ImpersonationCredentials}: Authentication succeeds if the  *     subject to be authenticated is allowed to impersonate the user identified  *     by the userID.</li>  *<li>{@link GuestCredentials}: The authentication succeeds if an 'anonymous'  *     user exists in the repository.</li>  *</ul>  *  * For any other credentials {@link #authenticate(javax.jcr.Credentials)}  * will return {@code false} indicating that this implementation is not able  * to verify their validity.  */
end_comment

begin_class
class|class
name|UserAuthentication
implements|implements
name|Authentication
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
name|UserAuthentication
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserConfiguration
name|config
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
name|UserAuthentication
parameter_list|(
annotation|@
name|Nonnull
name|UserConfiguration
name|config
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nullable
name|String
name|userId
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
comment|//-----------------------------------------------------< Authentication>---
annotation|@
name|Override
specifier|public
name|boolean
name|authenticate
parameter_list|(
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|LoginException
block|{
if|if
condition|(
name|credentials
operator|==
literal|null
operator|||
name|userId
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|UserManager
name|userManager
init|=
name|config
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userId
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
literal|false
return|;
block|}
if|if
condition|(
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccountNotFoundException
argument_list|(
literal|"Not a user "
operator|+
name|userId
argument_list|)
throw|;
block|}
name|User
name|user
init|=
operator|(
name|User
operator|)
name|authorizable
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isDisabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccountLockedException
argument_list|(
literal|"User with ID "
operator|+
name|userId
operator|+
literal|" has been disabled: "
operator|+
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
throw|;
block|}
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
name|Credentials
name|userCreds
init|=
name|user
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|.
name|equals
argument_list|(
name|creds
operator|.
name|getUserID
argument_list|()
argument_list|)
operator|&&
name|userCreds
operator|instanceof
name|CredentialsImpl
condition|)
block|{
name|success
operator|=
name|PasswordUtil
operator|.
name|isSame
argument_list|(
operator|(
operator|(
name|CredentialsImpl
operator|)
name|userCreds
operator|)
operator|.
name|getPasswordHash
argument_list|()
argument_list|,
name|creds
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|checkSuccess
argument_list|(
name|success
argument_list|,
literal|"UserId/Password mismatch."
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPasswordExpired
argument_list|(
name|user
argument_list|)
condition|)
block|{
comment|// change the password if the credentials object has the
comment|// UserConstants.CREDENTIALS_ATTRIBUTE_NEWPASSWORD attribute set
if|if
condition|(
operator|!
name|changePassword
argument_list|(
name|user
argument_list|,
name|creds
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CredentialExpiredException
argument_list|(
literal|"User password has expired"
argument_list|)
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|credentials
operator|instanceof
name|ImpersonationCredentials
condition|)
block|{
name|ImpersonationCredentials
name|ipCreds
init|=
operator|(
name|ImpersonationCredentials
operator|)
name|credentials
decl_stmt|;
name|AuthInfo
name|info
init|=
name|ipCreds
operator|.
name|getImpersonatorInfo
argument_list|()
decl_stmt|;
name|success
operator|=
name|equalUserId
argument_list|(
name|ipCreds
argument_list|,
name|userId
argument_list|)
operator|&&
name|impersonate
argument_list|(
name|info
argument_list|,
name|user
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
operator|||
name|credentials
operator|==
name|PreAuthenticatedLogin
operator|.
name|PRE_AUTHENTICATED
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
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
name|FailedLoginException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|equalUserId
parameter_list|(
annotation|@
name|Nonnull
name|ImpersonationCredentials
name|creds
parameter_list|,
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|)
block|{
name|Credentials
name|base
init|=
name|creds
operator|.
name|getBaseCredentials
argument_list|()
decl_stmt|;
return|return
operator|(
name|base
operator|instanceof
name|SimpleCredentials
operator|)
operator|&&
name|userId
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SimpleCredentials
operator|)
name|base
operator|)
operator|.
name|getUserID
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|changePassword
parameter_list|(
name|User
name|user
parameter_list|,
name|SimpleCredentials
name|credentials
parameter_list|)
block|{
try|try
block|{
name|Object
name|newPasswordObject
init|=
name|credentials
operator|.
name|getAttribute
argument_list|(
name|CREDENTIALS_ATTRIBUTE_NEWPASSWORD
argument_list|)
decl_stmt|;
if|if
condition|(
name|newPasswordObject
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|newPasswordObject
operator|instanceof
name|String
condition|)
block|{
name|user
operator|.
name|changePassword
argument_list|(
operator|(
name|String
operator|)
name|newPasswordObject
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|userId
operator|+
literal|": changed user password"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Aborted password change for user "
operator|+
name|userId
operator|+
literal|": provided new password is of incompatible type "
operator|+
name|newPasswordObject
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
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
name|error
argument_list|(
literal|"Failed to change password for user "
operator|+
name|userId
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Failed to change password for user "
operator|+
name|userId
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
specifier|private
name|boolean
name|impersonate
parameter_list|(
name|AuthInfo
name|info
parameter_list|,
name|User
name|user
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|user
operator|.
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getUserID
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|info
operator|.
name|getUserID
argument_list|()
operator|+
literal|" wants to impersonate himself -> success."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|info
operator|.
name|getUserID
argument_list|()
operator|+
literal|" wants to impersonate "
operator|+
name|user
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
name|user
operator|.
name|getImpersonation
argument_list|()
operator|.
name|allows
argument_list|(
name|subject
argument_list|)
return|;
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
literal|"Error while validating impersonation: {}"
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
annotation|@
name|CheckForNull
specifier|private
name|Long
name|getPasswordLastModified
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|userTree
decl_stmt|;
if|if
condition|(
name|user
operator|instanceof
name|UserImpl
condition|)
block|{
name|userTree
operator|=
operator|(
operator|(
name|UserImpl
operator|)
name|user
operator|)
operator|.
name|getTree
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|userTree
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|property
init|=
name|userTree
operator|.
name|getChild
argument_list|(
name|REP_PWD
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REP_PASSWORD_LAST_MODIFIED
argument_list|)
decl_stmt|;
return|return
operator|(
name|property
operator|!=
literal|null
operator|)
condition|?
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
else|:
literal|null
return|;
block|}
specifier|private
name|boolean
name|isPasswordExpired
parameter_list|(
annotation|@
name|Nonnull
name|User
name|user
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// the password of the "admin" user never expires
if|if
condition|(
name|user
operator|.
name|isAdmin
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|expired
init|=
literal|false
decl_stmt|;
name|ConfigurationParameters
name|params
init|=
name|config
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|int
name|maxAge
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_PASSWORD_MAX_AGE
argument_list|,
name|DEFAULT_PASSWORD_MAX_AGE
argument_list|)
decl_stmt|;
name|boolean
name|forceInitialPwChange
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_PASSWORD_INITIAL_CHANGE
argument_list|,
name|DEFAULT_PASSWORD_INITIAL_CHANGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxAge
operator|>
literal|0
condition|)
block|{
comment|// password expiry is enabled
name|Long
name|passwordLastModified
init|=
name|getPasswordLastModified
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|passwordLastModified
operator|==
literal|null
condition|)
block|{
comment|// no pw last modified property exists (yet) => expire!
name|expired
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// calculate expiry time (pw last mod + pw max age) and compare
name|long
name|expiryTime
init|=
name|passwordLastModified
operator|+
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|maxAge
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
decl_stmt|;
comment|// System.currentTimeMillis() may be inaccurate on windows. This is accepted for this feature.
name|expired
operator|=
name|expiryTime
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|forceInitialPwChange
condition|)
block|{
name|Long
name|passwordLastModified
init|=
name|getPasswordLastModified
argument_list|(
name|user
argument_list|)
decl_stmt|;
comment|// no pw last modified property exists (yet) => expire!
name|expired
operator|=
operator|(
literal|null
operator|==
name|passwordLastModified
operator|)
expr_stmt|;
block|}
return|return
name|expired
return|;
block|}
block|}
end_class

end_unit

