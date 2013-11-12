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
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|jcr
operator|.
name|NoSuchWorkspaceException
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
name|ContentRepository
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
name|ContentSession
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
name|security
operator|.
name|authentication
operator|.
name|SystemSubject
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
name|UserManagerCallback
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
name|PrincipalConfiguration
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
comment|/**  * Abstract implementation of the {@link LoginModule} interface that can act  * as base class for login modules that aim to authenticate subjects against  * information stored in the content repository.  *<p/>  *<h2>LoginModule Methods</h2>  * This base class provides a simple implementation for the following methods  * of the {@code LoginModule} interface:  *<p/>  *<ul>  *<li>{@link LoginModule#initialize(Subject, CallbackHandler, Map, Map) Initialize}:  * Initialization of this abstract module sets the following protected instance  * fields:  *<ul>  *<li>subject: The subject to be authenticated,</li>  *<li>callbackHandler: The callback handler passed to the login module,</li>  *<li>shareState: The map used to share state information with other login modules,</li>  *<li>options: The configuration options of this login module as specified  * in the {@link javax.security.auth.login.Configuration}.</li>  *</ul>  *</li>  *<li>{@link LoginModule#logout() Logout}:  * If the authenticated subject is not empty this logout implementation  * attempts to clear both principals and public credentials and returns  * {@code true}.</li>  *<li>{@link LoginModule#abort() Abort}: Clears the state of this login  * module by setting all private instance variables created in phase 1 or 2  * to {@code null}. Subclasses are in charge of releasing their own state  * information by either overriding {@link #clearState()}.</li>  *</ul>  *<p/>  *<h2>Utility Methods</h2>  * The following methods are provided in addition:  *<p/>  *<ul>  *<li>{@link #clearState()}: Clears all private state information that has  * be created during login. This method in called in {@link #abort()} and  * subclasses are expected to override this method.</li>  *<p/>  *<li>{@link #getSupportedCredentials()}: Abstract method used by  * {@link #getCredentials()} that reveals which credential implementations  * are supported by the {@code LoginModule}.</li>  *<p/>  *<li>{@link #getCredentials()}: Tries to retrieve valid (supported)  * Credentials in the following order:  *<ol>  *<li>using a {@link CredentialsCallback},</li>  *<li>looking for a {@link #SHARED_KEY_CREDENTIALS} entry in the shared  * state (see also {@link #getSharedCredentials()} and finally by</li>  *<li>searching for valid credentials in the subject.</li>  *</ol></li>  *<p/>  *<li>{@link #getSharedCredentials()}: This method returns credentials  * passed to the login module with the share state. The key to share credentials  * with a another module extending from this base class is  * {@link #SHARED_KEY_CREDENTIALS}. Note, that this method does not verify  * if the credentials provided by the shared state are  * {@link #getSupportedCredentials() supported}.</li>  *<p/>  *<li>{@link #getSharedLoginName()}: If the shared state contains an entry  * for {@link #SHARED_KEY_LOGIN_NAME} this method returns the value as login name.</li>  *<p/>  *<li>{@link #getSecurityProvider()}: Returns the configured security  * provider or {@code null}.</li>  *<p/>  *<li>{@link #getRoot()}: Provides access to the latest state of the  * repository in order to retrieve user or principal information required to  * authenticate the subject as well as to write back information during  * {@link #commit()}.</li>  *<p/>  *<li>{@link #getUserManager()}: Returns an instance of the configured  * {@link UserManager} or {@code null}.</li>  *<p/>  *<li>{@link #getPrincipalProvider()}: Returns an instance of the configured  * principal provider or {@code null}.</li>  *<p/>  *<li>{@link #getPrincipals(String)}: Utility that returns all principals  * associated with a given user id. This method might be be called after  * successful authentication in order to be able to populate the subject  * during {@link #commit()}. The implementation is a shortcut for calling  * {@link PrincipalProvider#getPrincipals(String) getPrincipals(String userId}  * on the provider exposed by {@link #getPrincipalProvider()}</li>  *</ul>  */
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
specifier|protected
name|ConfigurationParameters
name|options
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|ContentSession
name|systemSession
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
name|this
operator|.
name|options
operator|=
operator|(
name|options
operator|==
literal|null
operator|)
condition|?
name|ConfigurationParameters
operator|.
name|EMPTY
else|:
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|options
argument_list|)
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
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
name|success
operator|=
literal|true
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
name|abort
parameter_list|()
throws|throws
name|LoginException
block|{
name|clearState
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Clear state information that has been created during {@link #login()}.      */
specifier|protected
name|void
name|clearState
parameter_list|()
block|{
name|securityProvider
operator|=
literal|null
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|systemSession
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|systemSession
operator|.
name|close
argument_list|()
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
block|}
comment|/**      * @return A set of supported credential classes.      */
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
comment|/**      * Tries to retrieve valid (supported) Credentials:      *<ol>      *<li>using a {@link CredentialsCallback},</li>      *<li>looking for a {@link #SHARED_KEY_CREDENTIALS} entry in the      * shared state (see also {@link #getSharedCredentials()} and finally by</li>      *<li>searching for valid credentials in the subject.</li>      *</ol>      *      * @return Valid (supported) credentials or {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
name|Credentials
name|getCredentials
parameter_list|()
block|{
name|Set
argument_list|<
name|Class
argument_list|>
name|supported
init|=
name|getSupportedCredentials
argument_list|()
decl_stmt|;
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
operator|&&
name|supported
operator|.
name|contains
argument_list|(
name|creds
operator|.
name|getClass
argument_list|()
argument_list|)
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
literal|"Login: No supported credentials obtained from callback; trying shared state."
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
operator|!=
literal|null
operator|&&
name|supported
operator|.
name|contains
argument_list|(
name|creds
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login: Credentials obtained from shared state."
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
literal|"Login: No supported credentials found in shared state; looking for credentials in subject."
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
comment|/**      * @return The credentials passed to this login module with the shared state.      * @see #SHARED_KEY_CREDENTIALS      */
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
comment|/**      * @return The login name passed to this login module with the shared state.      * @see #SHARED_KEY_LOGIN_NAME      */
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
name|sharedState
operator|.
name|get
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Tries to obtain the {@code SecurityProvider} object from the callback      * handler using a new SecurityProviderCallback and keeps the value as      * private field. If the callback handler isn't able to handle the      * SecurityProviderCallback this method returns {@code null}.      *      * @return The {@code SecurityProvider} associated with this      *         {@code LoginModule} or {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
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
name|securityProvider
operator|=
name|rcb
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
comment|/**      * Tries to obtain a {@code Root} object from the callback handler using      * a new RepositoryCallback and keeps the value as private field.      * If the callback handler isn't able to handle the RepositoryCallback      * this method returns {@code null}.      *      * @return The {@code Root} associated with this {@code LoginModule} or      *         {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
name|Root
name|getRoot
parameter_list|()
block|{
if|if
condition|(
name|root
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
specifier|final
name|RepositoryCallback
name|rcb
init|=
operator|new
name|RepositoryCallback
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
name|rcb
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ContentRepository
name|repository
init|=
name|rcb
operator|.
name|getContentRepository
argument_list|()
decl_stmt|;
name|systemSession
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
name|rcb
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|root
operator|=
name|systemSession
operator|.
name|getLatestRoot
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
catch|catch
parameter_list|(
name|PrivilegedActionException
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
comment|/**      * Retrieves the {@link UserManager} that should be used to handle      * this authentication. If no user manager has been configure this      * method returns {@code null}.      *      * @return A instance of {@code UserManager} or {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
name|UserManager
name|getUserManager
parameter_list|()
block|{
name|UserManager
name|userManager
init|=
literal|null
decl_stmt|;
name|SecurityProvider
name|sp
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
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
operator|&&
name|sp
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
name|userManager
operator|=
name|uc
operator|.
name|getUserManager
argument_list|(
name|r
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userManager
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
name|UserManagerCallback
name|userCallBack
init|=
operator|new
name|UserManagerCallback
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
name|userCallBack
block|}
argument_list|)
expr_stmt|;
name|userManager
operator|=
name|userCallBack
operator|.
name|getUserManager
argument_list|()
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
block|}
return|return
name|userManager
return|;
block|}
comment|/**      * Retrieves the {@link PrincipalProvider} that should be used to handle      * this authentication. If no principal provider has been configure this      * method returns {@code null}.      *      * @return A instance of {@code PrincipalProvider} or {@code null}.      */
annotation|@
name|CheckForNull
specifier|protected
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|()
block|{
name|PrincipalProvider
name|principalProvider
init|=
literal|null
decl_stmt|;
name|SecurityProvider
name|sp
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
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
operator|&&
name|sp
operator|!=
literal|null
condition|)
block|{
name|PrincipalConfiguration
name|pc
init|=
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|principalProvider
operator|=
name|pc
operator|.
name|getPrincipalProvider
argument_list|(
name|r
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|principalProvider
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
name|PrincipalProviderCallback
name|principalCallBack
init|=
operator|new
name|PrincipalProviderCallback
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
name|principalCallBack
block|}
argument_list|)
expr_stmt|;
name|principalProvider
operator|=
name|principalCallBack
operator|.
name|getPrincipalProvider
argument_list|()
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
block|}
return|return
name|principalProvider
return|;
block|}
comment|/**      * Retrieves all principals associated with the specified {@code userId} for      * the configured principal provider.      *      * @param userId The id of the user.      * @return The set of principals associated with the given {@code userId}.      * @see #getPrincipalProvider()      */
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
name|userId
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
name|userId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

