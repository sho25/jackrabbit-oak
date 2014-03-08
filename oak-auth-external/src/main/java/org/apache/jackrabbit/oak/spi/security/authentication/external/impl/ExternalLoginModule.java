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
name|Credentials
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
comment|/**  * ExternalLoginModule implements a LoginModule that uses and external identity provider for login.  */
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
comment|/**      * Name of the parameter that configures the name of the external identity provider.      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_IDP_NAME
init|=
literal|"idp.name"
decl_stmt|;
comment|/**      * Name of the parameter that configures the name of the synchronization handler.      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_SYNC_HANDLER_NAME
init|=
literal|"sync.handlerName"
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
name|ss
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
name|ss
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
name|length
argument_list|()
operator|==
literal|0
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
name|ExternalIdentityProviderManager
name|idpMgr
init|=
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
decl_stmt|;
if|if
condition|(
name|idpMgr
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
name|idpMgr
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
name|length
argument_list|()
operator|==
literal|0
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
name|SyncManager
name|syncMgr
init|=
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
decl_stmt|;
if|if
condition|(
name|syncMgr
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
name|syncMgr
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
name|credentials
operator|=
name|getCredentials
argument_list|()
expr_stmt|;
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No credentials found for external login module. ignoring."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
name|externalUser
operator|=
name|idp
operator|.
name|authenticate
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
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
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"IDP {} returned null for simple creds of {}"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"IDP {} returned null for {}"
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error while authenticating credentials {} with {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|credentials
block|,
name|idp
operator|.
name|getName
argument_list|()
block|,
name|e
block|}
argument_list|)
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
literal|"IDP {} throws login exception for {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|idp
operator|.
name|getName
argument_list|()
block|,
name|credentials
block|,
name|e
block|}
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SyncHandler {} throws sync exception for {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|idp
operator|.
name|getName
argument_list|()
block|,
name|credentials
block|,
name|e
block|}
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
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|commit
parameter_list|()
throws|throws
name|LoginException
block|{
if|if
condition|(
name|externalUser
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
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|getPrincipals
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|principals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|setAuthInfo
argument_list|(
operator|new
name|AuthInfoImpl
argument_list|(
name|externalUser
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|,
name|principals
argument_list|)
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
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|abort
parameter_list|()
throws|throws
name|LoginException
block|{
name|clearState
argument_list|()
expr_stmt|;
comment|// do we need to remove the user again, in case we created it during login() ?
return|return
literal|true
return|;
block|}
comment|/**      * Initiates synchronization of the external user.      * @param user the external user      * @throws SyncException if an error occurs      */
specifier|private
name|void
name|syncUser
parameter_list|(
name|ExternalUser
name|user
parameter_list|)
throws|throws
name|SyncException
block|{
name|SyncContext
name|context
init|=
literal|null
decl_stmt|;
try|try
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
name|root
argument_list|)
expr_stmt|;
name|context
operator|.
name|sync
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
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
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
block|}
comment|/**      * @return An immutable set containing only the {@link SimpleCredentials} class.      */
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
comment|// TODO: maybe delegate getSupportedCredentials to IDP
name|Class
name|scClass
init|=
name|SimpleCredentials
operator|.
name|class
decl_stmt|;
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|scClass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

