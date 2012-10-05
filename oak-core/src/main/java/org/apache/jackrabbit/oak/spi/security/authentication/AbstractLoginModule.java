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
name|io
operator|.
name|IOException
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
name|jcr
operator|.
name|Credentials
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
name|Callback
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
name|callback
operator|.
name|UnsupportedCallbackException
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|spi
operator|.
name|LoginModule
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
name|callback
operator|.
name|CredentialsCallback
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
name|callback
operator|.
name|PrincipalProviderCallback
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
name|callback
operator|.
name|RepositoryCallback
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
name|callback
operator|.
name|SecurityProviderCallback
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
name|OpenPrincipalProvider
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
comment|/**  * AbstractLoginModule... TODO  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractLoginModule
implements|implements
name|LoginModule
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
name|AbstractLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Key of the sharedState entry referring to validated Credentials that is      * shared between multiple login modules.      */
specifier|public
specifier|static
specifier|final
name|String
name|SHARED_KEY_CREDENTIALS
init|=
literal|"org.apache.jackrabbit.credentials"
decl_stmt|;
comment|/**      * Key of the sharedState entry referring to a valid login ID that is shared      * between multiple login modules.      */
specifier|public
specifier|static
specifier|final
name|String
name|SHARED_KEY_LOGIN_NAME
init|=
literal|"javax.security.auth.login.name"
decl_stmt|;
specifier|protected
name|Subject
name|subject
decl_stmt|;
specifier|protected
name|CallbackHandler
name|callbackHandler
decl_stmt|;
specifier|protected
name|Map
name|sharedState
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
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
name|options
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|callbackHandler
operator|=
name|callbackHandler
expr_stmt|;
name|this
operator|.
name|sharedState
operator|=
name|sharedState
expr_stmt|;
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
if|if
condition|(
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|Credentials
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// clear subject if not readonly
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
name|clear
argument_list|()
expr_stmt|;
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|Set
argument_list|<
name|Class
argument_list|>
name|getSupportedCredentials
parameter_list|()
function_decl|;
annotation|@
name|CheckForNull
specifier|protected
name|Credentials
name|getCredentials
parameter_list|()
block|{
if|if
condition|(
name|callbackHandler
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: retrieving Credentials using callback."
argument_list|)
expr_stmt|;
try|try
block|{
name|CredentialsCallback
name|callback
init|=
operator|new
name|CredentialsCallback
argument_list|()
decl_stmt|;
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|callback
block|}
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|callback
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|creds
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: Credentials '{}' obtained from callback"
argument_list|,
name|creds
argument_list|)
expr_stmt|;
return|return
name|creds
return|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: No credentials obtained from callback; trying shared state."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedCallbackException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Credentials
name|creds
init|=
name|getSharedCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|creds
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: No credentials found in shared state; looking for supported credentials in subject."
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|clz
range|:
name|getSupportedCredentials
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|Credentials
argument_list|>
name|cds
init|=
name|subject
operator|.
name|getPublicCredentials
argument_list|(
name|clz
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: Credentials found in subject."
argument_list|)
expr_stmt|;
return|return
name|cds
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"No credentials found."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|CheckForNull
specifier|protected
name|Credentials
name|getSharedCredentials
parameter_list|()
block|{
name|Credentials
name|shared
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|)
condition|)
block|{
name|Object
name|sc
init|=
name|sharedState
operator|.
name|get
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|)
decl_stmt|;
if|if
condition|(
name|sc
operator|instanceof
name|Credentials
condition|)
block|{
name|shared
operator|=
operator|(
name|Credentials
operator|)
name|sc
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: Invalid value for share state entry "
operator|+
name|SHARED_KEY_CREDENTIALS
operator|+
literal|". Credentials expected."
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|shared
return|;
block|}
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getSharedLoginName
parameter_list|()
block|{
if|if
condition|(
name|sharedState
operator|.
name|containsKey
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|)
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|sharedState
operator|.
name|get
argument_list|(
name|SHARED_KEY_LOGIN_NAME
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
name|Nonnull
specifier|protected
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
name|PrincipalProvider
name|principalProvider
init|=
name|getPrincipalProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|principalProvider
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot retrieve principals. No principal provider configured."
argument_list|)
expr_stmt|;
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
name|principalProvider
operator|.
name|getPrincipals
argument_list|(
name|userID
argument_list|)
return|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|protected
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|()
block|{
comment|// TODO: replace fake pp to enable proper principal resolution.
return|return
operator|new
name|OpenPrincipalProvider
argument_list|()
return|;
comment|//        PrincipalProvider principalProvider = null;
comment|//
comment|//        SecurityProvider sp = getSecurityProvider();
comment|//        Root r = getRoot();
comment|//        if (root != null&& securityProvider != null) {
comment|//            principalProvider = securityProvider.getPrincipalConfiguration().getPrincipalProvider(root, NamePathMapper.DEFAULT);
comment|//        }
comment|//
comment|//        if (principalProvider == null&& callbackHandler != null) {
comment|//            try {
comment|//                PrincipalProviderCallback principalCallBack = new PrincipalProviderCallback();
comment|//                callbackHandler.handle(new Callback[] {principalCallBack});
comment|//                principalProvider = principalCallBack.getPrincipalProvider();
comment|//            } catch (IOException e) {
comment|//                log.debug(e.getMessage());
comment|//            } catch (UnsupportedCallbackException e) {
comment|//                log.debug(e.getMessage());
comment|//            }
comment|//        }
comment|//        return principalProvider;
block|}
annotation|@
name|CheckForNull
specifier|protected
name|UserProvider
name|getUserProvider
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// TODO
comment|//        SecurityProvider sp = getSecurityProvider();
comment|//        Root r = getRoot();
comment|//        if (root != null&& securityProvider != null) {
comment|//            return securityProvider.getUserContext().getUserProvider(root);
comment|//        } else {
comment|//            return null;
comment|//        }
block|}
annotation|@
name|CheckForNull
specifier|private
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
operator|&&
name|callbackHandler
operator|!=
literal|null
condition|)
block|{
name|SecurityProviderCallback
name|scb
init|=
operator|new
name|SecurityProviderCallback
argument_list|()
decl_stmt|;
try|try
block|{
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|scb
block|}
argument_list|)
expr_stmt|;
name|securityProvider
operator|=
name|scb
operator|.
name|getSecurityProvider
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedCallbackException
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
catch|catch
parameter_list|(
name|IOException
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
return|return
name|securityProvider
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|Root
name|getRoot
parameter_list|()
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|RepositoryCallback
name|rcb
init|=
operator|new
name|RepositoryCallback
argument_list|()
decl_stmt|;
try|try
block|{
name|callbackHandler
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|rcb
block|}
argument_list|)
expr_stmt|;
name|root
operator|=
name|rcb
operator|.
name|getRoot
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedCallbackException
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
catch|catch
parameter_list|(
name|IOException
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
return|return
name|root
return|;
block|}
block|}
end_class

end_unit

