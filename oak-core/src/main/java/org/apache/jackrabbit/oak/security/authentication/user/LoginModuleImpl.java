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
operator|.
name|user
package|;
end_package

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
name|Iterables
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
name|SecurityProvider
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
name|AbstractLoginModule
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
name|AuthInfoImpl
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
name|UserAuthenticationFactory
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
name|UserUtil
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
name|login
operator|.
name|LoginException
import|;
end_import

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
name|HashMap
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

begin_comment
comment|/**  * Default login module implementation that authenticates JCR {@code Credentials}  * against the repository. Based on the credentials the {@link Principal}s  * associated with user are retrieved from a configurable  * {@link org.apache.jackrabbit.oak.spi.security.principal.PrincipalProvider}.  *  *<h3>Credentials</h3>  *  * The {@code Credentials} are collected during {@link #login()} using the  * following logic:  *  *<ul>  *<li>{@code Credentials} as specified in {@link javax.jcr.Repository#login(javax.jcr.Credentials)}  *     in which case they are retrieved from the {@code CallbackHandler}.</li>  *<li>A {@link #SHARED_KEY_CREDENTIALS} entry in the shared state. The  *     expected value is a validated single {@code Credentials} object.</li>  *<li>If neither of the above variants provides Credentials this module  *     tries to obtain them from the subject. See also  *     {@link javax.security.auth.Subject#getSubject(java.security.AccessControlContext)}</li>  *</ul>  *  * This implementation of the {@code LoginModule} currently supports the following  * types of JCR Credentials:  *  *<ul>  *<li>{@link SimpleCredentials}</li>  *<li>{@link GuestCredentials}</li>  *<li>{@link ImpersonationCredentials}</li>  *</ul>  *  * The {@link Credentials} obtained during the {@code #login()} are added to  * the shared state and - upon successful {@code #commit()} to the {@code Subject}.  *  *<h3>Principals</h3>  * Upon successful login the principals associated with the user are calculated  * (see also {@link AbstractLoginModule#getPrincipals(String)}. These principals  * are finally added to the subject during {@code #commit()}.  *  *<h3>Impersonation</h3>  * Impersonation such as defined by {@link javax.jcr.Session#impersonate(javax.jcr.Credentials)}  * is covered by this login module by the means of {@link ImpersonationCredentials}.  * Impersonation will succeed if the {@link ImpersonationCredentials#getBaseCredentials() base credentials}  * refer to a valid user that has not been disabled. If the authenticating  * subject is not allowed to impersonate the specified user, the login attempt  * will fail with {@code LoginException}.<p>  * Please note, that a user will always be allowed to impersonate him/herself  * irrespective of the impersonation definitions exposed by  * {@link org.apache.jackrabbit.api.security.user.User#getImpersonation()}  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LoginModuleImpl
extends|extends
name|AbstractLoginModule
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
name|LoginModuleImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|SUPPORTED_CREDENTIALS
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
static|static
block|{
name|SUPPORTED_CREDENTIALS
operator|.
name|add
argument_list|(
name|SimpleCredentials
operator|.
name|class
argument_list|)
expr_stmt|;
name|SUPPORTED_CREDENTIALS
operator|.
name|add
argument_list|(
name|GuestCredentials
operator|.
name|class
argument_list|)
expr_stmt|;
name|SUPPORTED_CREDENTIALS
operator|.
name|add
argument_list|(
name|ImpersonationCredentials
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Credentials
name|credentials
decl_stmt|;
specifier|private
name|String
name|userId
decl_stmt|;
specifier|private
name|Principal
name|principal
decl_stmt|;
specifier|private
name|boolean
name|success
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|private
name|AuthInfo
name|authInfo
decl_stmt|;
comment|//--------------------------------------------------------< LoginModule>---
annotation|@
name|Override
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|LoginException
block|{
name|credentials
operator|=
name|getCredentials
argument_list|()
expr_stmt|;
comment|// check if we have a pre authenticated login from a previous login module
name|PreAuthenticatedLogin
name|preAuthLogin
init|=
name|getSharedPreAuthLogin
argument_list|()
decl_stmt|;
name|String
name|loginName
init|=
name|getLoginId
argument_list|(
name|preAuthLogin
argument_list|)
decl_stmt|;
name|Authentication
name|authentication
init|=
name|getUserAuthentication
argument_list|(
name|loginName
argument_list|)
decl_stmt|;
if|if
condition|(
name|authentication
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|preAuthLogin
operator|!=
literal|null
condition|)
block|{
name|success
operator|=
name|authentication
operator|.
name|authenticate
argument_list|(
name|PreAuthenticatedLogin
operator|.
name|PRE_AUTHENTICATED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|success
operator|=
name|authentication
operator|.
name|authenticate
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Adding Credentials to shared state."
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Adding login name to shared state."
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|,
name|loginName
argument_list|)
expr_stmt|;
name|userId
operator|=
name|authentication
operator|.
name|getUserId
argument_list|()
expr_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
condition|)
block|{
name|userId
operator|=
name|loginName
expr_stmt|;
block|}
name|principal
operator|=
name|authentication
operator|.
name|getUserPrincipal
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// ensure that we don't commit (OAK-2998, OAK-3032)
name|credentials
operator|=
literal|null
expr_stmt|;
name|userId
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|commit
parameter_list|()
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// login attempt in this login module was not successful
name|clearState
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|principals
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
name|principals
operator|=
name|getPrincipals
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|userId
operator|!=
literal|null
condition|)
block|{
name|principals
operator|=
name|getPrincipals
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
name|authInfo
operator|=
name|createAuthInfo
argument_list|(
name|principals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|subject
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|addAll
argument_list|(
name|principals
argument_list|)
expr_stmt|;
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
name|setAuthInfo
argument_list|(
name|authInfo
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not add information to read only subject {}"
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
name|closeSystemSession
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|//------------------------------------------------< AbstractLoginModule>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|Class
argument_list|>
name|getSupportedCredentials
parameter_list|()
block|{
return|return
name|SUPPORTED_CREDENTIALS
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|clearState
parameter_list|()
block|{
name|super
operator|.
name|clearState
argument_list|()
expr_stmt|;
name|credentials
operator|=
literal|null
expr_stmt|;
name|userId
operator|=
literal|null
expr_stmt|;
name|principal
operator|=
literal|null
expr_stmt|;
name|principals
operator|=
literal|null
expr_stmt|;
name|authInfo
operator|=
literal|null
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|Nullable
specifier|private
name|String
name|getLoginId
parameter_list|(
annotation|@
name|Nullable
name|PreAuthenticatedLogin
name|preAuthenticatedLogin
parameter_list|)
block|{
if|if
condition|(
name|preAuthenticatedLogin
operator|!=
literal|null
condition|)
block|{
return|return
name|preAuthenticatedLogin
operator|.
name|getUserId
argument_list|()
return|;
block|}
name|String
name|uid
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|uid
operator|=
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
operator|.
name|getUserID
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|credentials
operator|instanceof
name|GuestCredentials
condition|)
block|{
name|uid
operator|=
name|getAnonymousId
argument_list|()
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
name|Credentials
name|bc
init|=
operator|(
operator|(
name|ImpersonationCredentials
operator|)
name|credentials
operator|)
operator|.
name|getBaseCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|bc
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|uid
operator|=
operator|(
operator|(
name|SimpleCredentials
operator|)
name|bc
operator|)
operator|.
name|getUserID
argument_list|()
expr_stmt|;
block|}
block|}
comment|// null or other (unsupported) type of credentials (see SUPPORTED_CREDENTIALS)
if|if
condition|(
name|uid
operator|==
literal|null
condition|)
block|{
name|uid
operator|=
name|getSharedLoginName
argument_list|()
expr_stmt|;
block|}
return|return
name|uid
return|;
block|}
annotation|@
name|Nullable
specifier|private
name|String
name|getAnonymousId
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|sp
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|ConfigurationParameters
name|params
init|=
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getParameters
argument_list|()
decl_stmt|;
return|return
name|UserUtil
operator|.
name|getAnonymousId
argument_list|(
name|params
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nullable
specifier|private
name|Authentication
name|getUserAuthentication
parameter_list|(
annotation|@
name|Nullable
name|String
name|loginName
parameter_list|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|securityProvider
operator|!=
literal|null
operator|&&
name|root
operator|!=
literal|null
condition|)
block|{
name|UserConfiguration
name|uc
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserAuthenticationFactory
name|factory
init|=
name|uc
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_AUTHENTICATION_FACTORY
argument_list|,
literal|null
argument_list|,
name|UserAuthenticationFactory
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
return|return
name|factory
operator|.
name|getAuthentication
argument_list|(
name|uc
argument_list|,
name|root
argument_list|,
name|loginName
argument_list|)
return|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No user authentication factory configured in user configuration."
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|AuthInfo
name|createAuthInfo
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|Credentials
name|creds
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|ImpersonationCredentials
condition|)
block|{
name|creds
operator|=
operator|(
operator|(
name|ImpersonationCredentials
operator|)
name|credentials
operator|)
operator|.
name|getBaseCredentials
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|creds
operator|=
name|credentials
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Object
name|shared
init|=
name|sharedState
operator|.
name|get
argument_list|(
name|SHARED_KEY_ATTRIBUTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|shared
operator|instanceof
name|Map
condition|)
block|{
operator|(
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|shared
operator|)
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|attributes
operator|.
name|put
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|creds
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|creds
decl_stmt|;
for|for
control|(
name|String
name|attrName
range|:
name|sc
operator|.
name|getAttributeNames
argument_list|()
control|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|attrName
argument_list|,
name|sc
operator|.
name|getAttribute
argument_list|(
name|attrName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|AuthInfoImpl
argument_list|(
name|userId
argument_list|,
name|attributes
argument_list|,
name|Iterables
operator|.
name|concat
argument_list|(
name|principals
argument_list|,
name|subject
operator|.
name|getPrincipals
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

