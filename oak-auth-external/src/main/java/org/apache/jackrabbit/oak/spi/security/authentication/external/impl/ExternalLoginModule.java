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
name|external
operator|.
name|impl
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
name|commons
operator|.
name|DebugTimer
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
name|plugins
operator|.
name|value
operator|.
name|jcr
operator|.
name|ValueFactoryImpl
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
name|authentication
operator|.
name|credentials
operator|.
name|CredentialsSupport
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
name|credentials
operator|.
name|SimpleCredentialsSupport
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
name|external
operator|.
name|ExternalIdentityException
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
name|external
operator|.
name|ExternalIdentityProvider
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
name|external
operator|.
name|ExternalIdentityProviderManager
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
name|external
operator|.
name|ExternalIdentityRef
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
name|external
operator|.
name|ExternalUser
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
name|external
operator|.
name|SyncContext
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
name|external
operator|.
name|SyncException
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
name|external
operator|.
name|SyncHandler
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
name|external
operator|.
name|SyncManager
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
name|external
operator|.
name|SyncResult
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
name|external
operator|.
name|SyncedIdentity
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardUtils
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|CallbackHandler
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
name|HashMap
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
name|Objects
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * {@code ExternalLoginModule} implements a {@code LoginModule} that uses an  * {@link ExternalIdentityProvider} for authentication.  */
end_comment

begin_class
specifier|public
class|class
name|ExternalLoginModule
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
name|ExternalLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_SYNC_ATTEMPTS
init|=
literal|50
decl_stmt|;
comment|/**      * Name of the parameter that configures the name of the external identity provider.      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_IDP_NAME
init|=
name|SyncHandlerMapping
operator|.
name|PARAM_IDP_NAME
decl_stmt|;
comment|/**      * Name of the parameter that configures the name of the synchronization handler.      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_SYNC_HANDLER_NAME
init|=
name|SyncHandlerMapping
operator|.
name|PARAM_SYNC_HANDLER_NAME
decl_stmt|;
specifier|private
name|ExternalIdentityProviderManager
name|idpManager
decl_stmt|;
specifier|private
name|SyncManager
name|syncManager
decl_stmt|;
specifier|private
name|CredentialsSupport
name|credentialsSupport
init|=
name|SimpleCredentialsSupport
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|/**      * internal configuration when invoked from a factory rather than jaas      */
specifier|private
name|ConfigurationParameters
name|osgiConfig
decl_stmt|;
comment|/**      * The external identity provider as specified by the {@link #PARAM_IDP_NAME}      */
specifier|private
name|ExternalIdentityProvider
name|idp
decl_stmt|;
comment|/**      * The configured sync handler as specified by the {@link #PARAM_SYNC_HANDLER_NAME}      */
specifier|private
name|SyncHandler
name|syncHandler
decl_stmt|;
comment|/**      * The external user as resolved in the login call.      */
specifier|private
name|ExternalUser
name|externalUser
decl_stmt|;
comment|/**      * Login credentials      */
specifier|private
name|Credentials
name|credentials
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
comment|/**      * Default constructor for the OSGIi LoginModuleFactory case and the default non-OSGi JAAS case.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|ExternalLoginModule
parameter_list|()
block|{     }
comment|/**      * Creates a new ExternalLoginModule with the given OSGi config.      * @param osgiConfig the config      */
specifier|public
name|ExternalLoginModule
parameter_list|(
name|ConfigurationParameters
name|osgiConfig
parameter_list|)
block|{
name|this
operator|.
name|osgiConfig
operator|=
name|osgiConfig
expr_stmt|;
block|}
comment|//--------------------------------------------------------< LoginModule>---
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|sharedState
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|opts
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|callbackHandler
argument_list|,
name|sharedState
argument_list|,
name|opts
argument_list|)
expr_stmt|;
comment|// merge options with osgi options if needed
if|if
condition|(
name|osgiConfig
operator|!=
literal|null
condition|)
block|{
name|options
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|osgiConfig
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
name|Whiteboard
name|whiteboard
init|=
name|getWhiteboard
argument_list|()
decl_stmt|;
if|if
condition|(
name|whiteboard
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"External login module needs whiteboard. Will not be used for login."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|idpName
init|=
name|options
operator|.
name|getConfigValue
argument_list|(
name|PARAM_IDP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|idpName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"External login module needs IPD name. Will not be used for login."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|idpManager
operator|==
literal|null
condition|)
block|{
name|idpManager
operator|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|whiteboard
argument_list|,
name|ExternalIdentityProviderManager
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idpManager
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"External login module needs IDPManager. Will not be used for login."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idp
operator|=
name|idpManager
operator|.
name|getProvider
argument_list|(
name|idpName
argument_list|)
expr_stmt|;
if|if
condition|(
name|idp
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No IDP found with name {}. Will not be used for login."
argument_list|,
name|idpName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|syncHandlerName
init|=
name|options
operator|.
name|getConfigValue
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|syncHandlerName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"External login module needs SyncHandler name. Will not be used for login."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|syncManager
operator|==
literal|null
condition|)
block|{
name|syncManager
operator|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|whiteboard
argument_list|,
name|SyncManager
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|syncManager
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"External login module needs SyncManager. Will not be used for login."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|syncHandler
operator|=
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
name|syncHandlerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncHandler
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No SyncHandler found with name {}. Will not be used for login."
argument_list|,
name|syncHandlerName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|idp
operator|instanceof
name|CredentialsSupport
condition|)
block|{
name|credentialsSupport
operator|=
operator|(
name|CredentialsSupport
operator|)
name|idp
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No 'SupportedCredentials' configured. Using default implementation supporting 'SimpleCredentials'."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|LoginException
block|{
if|if
condition|(
name|idp
operator|==
literal|null
operator|||
name|syncHandler
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Credentials
name|creds
init|=
name|getCredentials
argument_list|()
decl_stmt|;
comment|// check if we have a pre authenticated login from a previous login module
specifier|final
name|PreAuthenticatedLogin
name|preAuthLogin
init|=
name|getSharedPreAuthLogin
argument_list|()
decl_stmt|;
specifier|final
name|String
name|userId
init|=
name|getUserId
argument_list|(
name|preAuthLogin
argument_list|,
name|creds
argument_list|)
decl_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
operator|&&
name|creds
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No credentials|userId found for external login module. ignoring."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// remember identification for log-output
name|Object
name|logId
init|=
operator|(
name|userId
operator|!=
literal|null
operator|)
condition|?
name|userId
else|:
name|creds
decl_stmt|;
try|try
block|{
comment|// check if there exists a user with the given ID that has been synchronized
comment|// before into the repository.
name|SyncedIdentity
name|sId
init|=
name|getSyncedIdentity
argument_list|(
name|userId
argument_list|)
decl_stmt|;
comment|// if there exists an authorizable with the given userid (syncedIdentity != null),
comment|// ignore it if any of the following conditions is met:
comment|// - identity is local (i.e. not an external identity)
comment|// - identity belongs to another IDP
comment|// - identity is valid but we have a preAuthLogin and the user doesn't need an updating sync (OAK-3508)
if|if
condition|(
name|ignore
argument_list|(
name|sId
argument_list|,
name|preAuthLogin
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|preAuthLogin
operator|!=
literal|null
condition|)
block|{
name|externalUser
operator|=
name|idp
operator|.
name|getUser
argument_list|(
name|preAuthLogin
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|externalUser
operator|=
name|idp
operator|.
name|authenticate
argument_list|(
name|creds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|externalUser
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"IDP {} returned valid user {}"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|externalUser
argument_list|)
expr_stmt|;
if|if
condition|(
name|creds
operator|!=
literal|null
condition|)
block|{
comment|//noinspection unchecked
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|,
name|creds
argument_list|)
expr_stmt|;
block|}
comment|//noinspection unchecked
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|,
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|syncUser
argument_list|(
name|externalUser
argument_list|)
expr_stmt|;
comment|// login successful -> remember credentials for commit/logout
name|credentials
operator|=
name|creds
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|debug
argument_list|(
literal|"IDP {} returned null for {}"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|logId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sId
operator|!=
literal|null
condition|)
block|{
comment|// invalidate the user if it exists as synced variant
name|log
operator|.
name|debug
argument_list|(
literal|"local user exists for '{}'. re-validating."
argument_list|,
name|sId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|validateUser
argument_list|(
name|sId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ExternalIdentityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while authenticating '{}' with {}"
argument_list|,
name|logId
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|onError
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"IDP {} throws login exception for '{}': {}"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|logId
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SyncException
decl||
name|RepositoryException
name|e
parameter_list|)
block|{
name|onError
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"SyncHandler {} throws sync exception for '{}'"
argument_list|,
name|syncHandler
operator|.
name|getName
argument_list|()
argument_list|,
name|logId
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LoginException
name|le
init|=
operator|new
name|LoginException
argument_list|(
literal|"Error while syncing user."
argument_list|)
decl_stmt|;
name|le
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|le
throw|;
block|}
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
name|externalUser
operator|==
literal|null
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
name|getPrincipals
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|authInfo
operator|=
name|createAuthInfo
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|,
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
literal|"Could not add information to read only subject."
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
annotation|@
name|Override
specifier|public
name|boolean
name|abort
parameter_list|()
block|{
name|clearState
argument_list|()
expr_stmt|;
comment|// do we need to remove the user again, in case we created it during login() ?
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|logout
parameter_list|()
throws|throws
name|LoginException
block|{
name|Set
name|creds
init|=
name|Stream
operator|.
name|of
argument_list|(
name|credentials
argument_list|,
name|authInfo
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|logout
argument_list|(
operator|(
name|creds
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|creds
operator|)
argument_list|,
name|principals
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nullable
specifier|private
name|String
name|getUserId
parameter_list|(
annotation|@
name|Nullable
name|PreAuthenticatedLogin
name|preAuthLogin
parameter_list|,
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|preAuthLogin
operator|!=
literal|null
condition|)
block|{
return|return
name|preAuthLogin
operator|.
name|getUserId
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
return|return
name|credentialsSupport
operator|.
name|getUserId
argument_list|(
name|credentials
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
annotation|@
name|Nullable
specifier|private
name|SyncedIdentity
name|getSyncedIdentity
parameter_list|(
annotation|@
name|Nullable
name|String
name|userId
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
operator|&&
name|userMgr
operator|!=
literal|null
condition|)
block|{
return|return
name|syncHandler
operator|.
name|findIdentity
argument_list|(
name|userMgr
argument_list|,
name|userId
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
specifier|private
name|boolean
name|ignore
parameter_list|(
annotation|@
name|Nullable
name|SyncedIdentity
name|syncedIdentity
parameter_list|,
annotation|@
name|Nullable
name|PreAuthenticatedLogin
name|preAuthLogin
parameter_list|)
block|{
if|if
condition|(
name|syncedIdentity
operator|!=
literal|null
condition|)
block|{
name|ExternalIdentityRef
name|externalIdRef
init|=
name|syncedIdentity
operator|.
name|getExternalIdRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|externalIdRef
operator|==
literal|null
condition|)
block|{
name|debug
argument_list|(
literal|"ignoring local user: {}"
argument_list|,
name|syncedIdentity
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|idp
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|externalIdRef
operator|.
name|getProviderName
argument_list|()
argument_list|)
condition|)
block|{
name|debug
argument_list|(
literal|"ignoring foreign identity: {} (idp={})"
argument_list|,
name|externalIdRef
operator|.
name|getString
argument_list|()
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|preAuthLogin
operator|!=
literal|null
operator|&&
operator|!
name|syncHandler
operator|.
name|requiresSync
argument_list|(
name|syncedIdentity
argument_list|)
condition|)
block|{
name|debug
argument_list|(
literal|"pre-authenticated external user {} does not require syncing."
argument_list|,
name|syncedIdentity
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Initiates synchronization of the external user.      * @param user the external user      * @throws SyncException if an error occurs      */
specifier|private
name|void
name|syncUser
parameter_list|(
annotation|@
name|NotNull
name|ExternalUser
name|user
parameter_list|)
throws|throws
name|SyncException
block|{
name|Root
name|root
init|=
name|getRootOrThrow
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|getUsermanagerOrThrow
argument_list|()
decl_stmt|;
name|int
name|numAttempt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|numAttempt
operator|++
operator|<
name|MAX_SYNC_ATTEMPTS
condition|)
block|{
name|SyncContext
name|context
init|=
name|syncHandler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
argument_list|,
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|DebugTimer
name|timer
init|=
operator|new
name|DebugTimer
argument_list|()
decl_stmt|;
name|SyncResult
name|syncResult
init|=
name|context
operator|.
name|sync
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|timer
operator|.
name|mark
argument_list|(
literal|"sync"
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|timer
operator|.
name|mark
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
block|}
name|debug
argument_list|(
literal|"syncUser({}) {}, status: {}"
argument_list|,
name|user
operator|.
name|getId
argument_list|()
argument_list|,
name|timer
operator|.
name|getString
argument_list|()
argument_list|,
name|syncResult
operator|.
name|getStatus
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"User synchronization failed during commit: {}. (attempt {}/{})"
argument_list|,
name|e
argument_list|,
name|numAttempt
argument_list|,
name|MAX_SYNC_ATTEMPTS
argument_list|)
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"User synchronization failed during commit after "
operator|+
name|MAX_SYNC_ATTEMPTS
operator|+
literal|" attempts"
argument_list|)
throw|;
block|}
comment|/**      * Initiates synchronization of a possible remove user      * @param id the user id      */
specifier|private
name|void
name|validateUser
parameter_list|(
annotation|@
name|NotNull
name|String
name|id
parameter_list|)
throws|throws
name|SyncException
block|{
name|Root
name|root
init|=
name|getRootOrThrow
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|getUsermanagerOrThrow
argument_list|()
decl_stmt|;
name|SyncContext
name|context
init|=
name|syncHandler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
argument_list|,
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|DebugTimer
name|timer
init|=
operator|new
name|DebugTimer
argument_list|()
decl_stmt|;
name|context
operator|=
name|syncHandler
operator|.
name|createContext
argument_list|(
name|idp
argument_list|,
name|userManager
argument_list|,
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|sync
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|timer
operator|.
name|mark
argument_list|(
literal|"sync"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|timer
operator|.
name|mark
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
name|debug
argument_list|(
literal|"validateUser({}) {}"
argument_list|,
name|id
argument_list|,
name|timer
operator|.
name|getString
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
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"User synchronization failed during commit."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
name|AuthInfo
name|createAuthInfo
parameter_list|(
annotation|@
name|NotNull
name|String
name|userId
parameter_list|,
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
if|if
condition|(
name|creds
operator|!=
literal|null
condition|)
block|{
name|attributes
operator|.
name|putAll
argument_list|(
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|creds
argument_list|)
argument_list|)
expr_stmt|;
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
annotation|@
name|NotNull
specifier|private
name|Root
name|getRootOrThrow
parameter_list|()
throws|throws
name|SyncException
block|{
name|Root
name|root
init|=
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"Cannot synchronize user. root == null"
argument_list|)
throw|;
block|}
return|return
name|root
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|UserManager
name|getUsermanagerOrThrow
parameter_list|()
throws|throws
name|SyncException
block|{
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|userManager
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"Cannot synchronize user. userManager == null"
argument_list|)
throw|;
block|}
return|return
name|userManager
return|;
block|}
specifier|private
specifier|static
name|void
name|debug
parameter_list|(
annotation|@
name|NotNull
name|String
name|msg
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------< AbstractLoginModule>---
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
name|externalUser
operator|=
literal|null
expr_stmt|;
name|credentials
operator|=
literal|null
expr_stmt|;
name|authInfo
operator|=
literal|null
expr_stmt|;
name|principals
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * @return the set of credentials classes as exposed by the configured      * {@link CredentialsSupport} implementation.      */
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
name|credentialsSupport
operator|.
name|getCredentialClasses
argument_list|()
return|;
block|}
comment|//----------------------------------------------< public setters (JAAS)>---
specifier|public
name|void
name|setSyncManager
parameter_list|(
annotation|@
name|NotNull
name|SyncManager
name|syncManager
parameter_list|)
block|{
name|this
operator|.
name|syncManager
operator|=
name|syncManager
expr_stmt|;
block|}
specifier|public
name|void
name|setIdpManager
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityProviderManager
name|idpManager
parameter_list|)
block|{
name|this
operator|.
name|idpManager
operator|=
name|idpManager
expr_stmt|;
block|}
block|}
end_class

end_unit

