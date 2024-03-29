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
name|token
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
name|authentication
operator|.
name|token
operator|.
name|TokenCredentials
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
name|callback
operator|.
name|TokenProviderCallback
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
name|token
operator|.
name|TokenConfiguration
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
name|token
operator|.
name|TokenInfo
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
name|token
operator|.
name|TokenProvider
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

begin_comment
comment|/**  * {@code LoginModule} implementation that is able to handle login request  * based on {@link TokenCredentials}. In combination with another login module  * that handles other {@code Credentials} implementation this module will also  * take care of creating new login tokens and the corresponding credentials  * upon {@link #commit()}that it will be able to deal with in subsequent  * login calls.  *  *<h2>Login and Commit</h2>  *<h3>Login</h3>  * This {@code LoginModule} implementation performs the following tasks upon  * {@link #login()}.  *  *<ol>  *<li>Try to retrieve {@link TokenCredentials} credentials (see also  *     {@link AbstractLoginModule#getCredentials()})</li>  *<li>Validates the credentials based on the functionality provided by  *     {@link TokenAuthentication#authenticate(javax.jcr.Credentials)}</li>  *<li>Upon success it retrieves {@code userId} from the {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenInfo}  *     and calculates the principals associated with that user,</li>  *<li>and finally puts the credentials on the shared state.</li>  *</ol>  *  * If no {@code TokenProvider} has been configured {@link #login()} or if  * no {@code TokenCredentials} can be obtained this module will return {@code false}.  *  *<h3>Commit</h3>  * If login was successfully handled by this module the {@link #commit()} will  * just populate the subject.<p>  *  * If the login was successfully handled by another module in the chain, the  * {@code TokenLoginModule} will test if the login was associated with a  * request for login token generation. This mandates that there are credentials  * present on the shared state that fulfill the requirements defined by  * {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider#doCreateToken(javax.jcr.Credentials)}.  *  *<h3>Example Configurations</h3>  * The authentication configuration using this {@code LoginModule} could for  * example look as follows:  *  *<h4>TokenLoginModule in combination with another LoginModule</h4>  *<pre>  *    jackrabbit.oak {  *            org.apache.jackrabbit.oak.security.authentication.token.TokenLoginModule sufficient;  *            org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl required;  *    };  *</pre>  * In this case the TokenLoginModule would handle any login issued with  * {@link TokenCredentials} while the second module would take care any other  * credentials implementations as long they are supported by the module. In  * addition the {@link TokenLoginModule} will issue a new token if the login  * succeeded and the credentials provided by the shared state can be used  * to issue a new login token (see {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider#doCreateToken(javax.jcr.Credentials)}.  *  *<h4>TokenLoginModule as single way to login</h4>  *<pre>  *    jackrabbit.oak {  *            org.apache.jackrabbit.oak.security.authentication.token.TokenLoginModule required;  *    };  *</pre>  * If the {@code TokenLoginModule} as single entry in the login configuration  * the login token must be generated by the application by calling  * {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider#createToken(Credentials)} or  * {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider#createToken(String, java.util.Map)}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TokenLoginModule
extends|extends
name|AbstractLoginModule
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
name|TokenLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TokenProvider
name|tokenProvider
decl_stmt|;
specifier|private
name|TokenCredentials
name|tokenCredentials
decl_stmt|;
specifier|private
name|TokenInfo
name|tokenInfo
decl_stmt|;
specifier|private
name|Principal
name|principal
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
name|tokenProvider
operator|=
name|getTokenProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|tokenProvider
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Credentials
name|credentials
init|=
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|TokenCredentials
condition|)
block|{
name|TokenCredentials
name|tc
init|=
operator|(
name|TokenCredentials
operator|)
name|credentials
decl_stmt|;
name|TokenAuthentication
name|authentication
init|=
operator|new
name|TokenAuthentication
argument_list|(
name|tokenProvider
argument_list|)
decl_stmt|;
if|if
condition|(
name|authentication
operator|.
name|authenticate
argument_list|(
name|tc
argument_list|)
condition|)
block|{
name|tokenCredentials
operator|=
name|tc
expr_stmt|;
name|tokenInfo
operator|=
name|authentication
operator|.
name|getTokenInfo
argument_list|()
expr_stmt|;
name|principal
operator|=
name|authentication
operator|.
name|getUserPrincipal
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Login: adding login name to shared state."
argument_list|)
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|,
name|tokenInfo
operator|.
name|getUserId
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
name|tokenCredentials
operator|!=
literal|null
operator|&&
name|tokenInfo
operator|!=
literal|null
condition|)
block|{
name|principals
operator|=
operator|(
name|principal
operator|!=
literal|null
operator|)
condition|?
name|getPrincipals
argument_list|(
name|principal
argument_list|)
else|:
name|getPrincipals
argument_list|(
name|tokenInfo
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|authInfo
operator|=
name|getAuthInfo
argument_list|(
name|tokenInfo
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
expr_stmt|;
name|updateSubject
argument_list|(
name|subject
argument_list|,
name|tokenCredentials
argument_list|,
name|authInfo
argument_list|)
expr_stmt|;
name|closeSystemSession
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
try|try
block|{
if|if
condition|(
name|tokenProvider
operator|!=
literal|null
operator|&&
name|sharedState
operator|.
name|containsKey
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|)
condition|)
block|{
name|Credentials
name|shared
init|=
name|getSharedCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|shared
operator|!=
literal|null
operator|&&
name|tokenProvider
operator|.
name|doCreateToken
argument_list|(
name|shared
argument_list|)
condition|)
block|{
name|Root
name|r
init|=
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// refresh root, in case the external login module created users
block|}
name|TokenInfo
name|ti
init|=
name|tokenProvider
operator|.
name|createToken
argument_list|(
name|shared
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
block|{
name|TokenCredentials
name|tc
init|=
operator|new
name|TokenCredentials
argument_list|(
name|ti
operator|.
name|getToken
argument_list|()
argument_list|)
decl_stmt|;
name|ti
operator|.
name|getPrivateAttributes
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|tc
operator|.
name|setAttribute
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|ti
operator|.
name|getPublicAttributes
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
name|tc
operator|.
name|setAttribute
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_ATTRIBUTES
argument_list|,
name|ti
operator|.
name|getPublicAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|updateSubject
argument_list|(
name|subject
argument_list|,
name|tc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// failed to create token -> fail commit()
name|onError
argument_list|()
expr_stmt|;
name|Object
name|logId
init|=
name|sharedState
operator|.
name|get
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"TokenProvider failed to create a login token for user {}"
argument_list|,
name|logId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"Failed to create login token for user "
operator|+
name|logId
argument_list|)
throw|;
block|}
block|}
block|}
block|}
finally|finally
block|{
comment|// the login attempt on this module did not succeed: clear state
name|clearState
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
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
name|tokenCredentials
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
name|Collections
operator|.
name|singleton
argument_list|(
name|TokenCredentials
operator|.
name|class
argument_list|)
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
name|tokenCredentials
operator|=
literal|null
expr_stmt|;
name|tokenInfo
operator|=
literal|null
expr_stmt|;
name|tokenProvider
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
comment|//------------------------------------------------------------< private>---
comment|/**      * Retrieve the token provider      * @return the token provider or {@code null}.      */
annotation|@
name|Nullable
specifier|private
name|TokenProvider
name|getTokenProvider
parameter_list|()
block|{
name|TokenProvider
name|provider
init|=
literal|null
decl_stmt|;
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
name|root
operator|!=
literal|null
operator|&&
name|securityProvider
operator|!=
literal|null
condition|)
block|{
name|TokenConfiguration
name|tokenConfig
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|provider
operator|=
name|tokenConfig
operator|.
name|getTokenProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|provider
operator|==
literal|null
operator|&&
name|callbackHandler
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|TokenProviderCallback
name|tcCallback
init|=
operator|new
name|TokenProviderCallback
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
name|tcCallback
block|}
argument_list|)
expr_stmt|;
name|provider
operator|=
name|tcCallback
operator|.
name|getTokenProvider
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|UnsupportedCallbackException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|provider
return|;
block|}
comment|/**      * Create the {@code AuthInfo} for the specified {@code tokenInfo} as well as      * userId and principals, that have been set upon {@link #login}.      *      * @param tokenInfo The tokenInfo to retrieve attributes from.      * @return The {@code AuthInfo} resulting from the successful login.      */
annotation|@
name|NotNull
specifier|private
specifier|static
name|AuthInfo
name|getAuthInfo
parameter_list|(
annotation|@
name|NotNull
name|TokenInfo
name|tokenInfo
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
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
name|tokenInfo
operator|.
name|getPublicAttributes
argument_list|()
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
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|AuthInfoImpl
argument_list|(
name|tokenInfo
operator|.
name|getUserId
argument_list|()
argument_list|,
name|attributes
argument_list|,
name|principals
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|updateSubject
parameter_list|(
annotation|@
name|NotNull
name|Subject
name|subject
parameter_list|,
annotation|@
name|NotNull
name|TokenCredentials
name|tc
parameter_list|,
annotation|@
name|Nullable
name|AuthInfo
name|authInfo
parameter_list|)
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
name|getPublicCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|tc
argument_list|)
expr_stmt|;
if|if
condition|(
name|authInfo
operator|!=
literal|null
condition|)
block|{
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|addAll
argument_list|(
name|authInfo
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|setAuthInfo
argument_list|(
name|authInfo
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

