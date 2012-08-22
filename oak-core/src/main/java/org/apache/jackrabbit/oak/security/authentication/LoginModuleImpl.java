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
name|NameCallback
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
comment|/**  * Default login module implementation that authenticates JCR {@code Credentials}  * against the repository. Based on the credentials the {@link Principal}s  * associated with user are retrieved from a configurable {@link PrincipalProvider}.  *  *<h3>Credentials</h3>  *  * The {@code Credentials} are collected during {@link #login()} using the  * following logic:  *  *<ul>  *<li>{@code Credentials} as specified in {@link javax.jcr.Repository#login(javax.jcr.Credentials)}  *     in which case they are retrieved from the {@code CallbackHandler}.</li>  *<li>A {@link #SHARED_KEY_CREDENTIALS} entry in the shared state. The  *     expected value is a validated single {@code Credentials} object.</li>  *<li>If neither of the above variants provides Credentials this module  *     tries to obtain them from the subject. See also  *     {@link Subject#getSubject(java.security.AccessControlContext)}</li>  *</ul>  *  * This implementation of the {@code LoginModule} currently supports the following  * types of JCR Credentials:  *  *<ul>  *<li>{@link SimpleCredentials}</li>  *<li>{@link GuestCredentials}</li>  *<li>{@link ImpersonationCredentials}</li>  *</ul>  *  * The {@link Credentials} obtained during the {@link #login()} are added to  * the shared state and - upon successful {@link #commit()} to the {@link Subject}.  *  *<h3>Principals</h3>  *  * TODO  * - principal lookup -> principal provider  * - principal resolution based on credentials  *  *<h3>Impersonation</h3>  *  * TODO  *  *  *  *  */
end_comment

begin_class
specifier|public
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
argument_list|<
name|Class
argument_list|>
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
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|private
name|String
name|userID
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
name|options
argument_list|)
expr_stmt|;
comment|// TODO
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
comment|// TODO
name|credentials
operator|=
name|getCredentials
argument_list|()
expr_stmt|;
name|userID
operator|=
name|getUserID
argument_list|()
expr_stmt|;
name|principals
operator|=
name|getPrincipals
argument_list|(
name|userID
argument_list|)
expr_stmt|;
name|Authentication
name|authentication
init|=
operator|new
name|AuthenticationImpl
argument_list|(
name|userID
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|authentication
operator|.
name|authenticate
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|success
operator|=
name|impersonate
argument_list|(
name|authentication
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
literal|"Login: adding Credentials to shared state."
argument_list|)
expr_stmt|;
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
literal|"Login: adding login name to shared state."
argument_list|)
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|,
name|userID
argument_list|)
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
throws|throws
name|LoginException
block|{
if|if
condition|(
name|credentials
operator|==
literal|null
operator|||
name|principals
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
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|getAuthInfo
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
name|credentials
operator|=
literal|null
expr_stmt|;
name|principals
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|//------------------------------------------------< AbstractLoginModule>---
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
comment|//--------------------------------------------------------------------------
annotation|@
name|CheckForNull
specifier|private
name|String
name|getUserID
parameter_list|()
block|{
comment|// TODO add proper implementation
name|String
name|userID
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|userID
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
name|userID
operator|=
literal|"anonymous"
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
name|userID
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
else|else
block|{
try|try
block|{
name|NameCallback
name|callback
init|=
operator|new
name|NameCallback
argument_list|(
literal|"User-ID: "
argument_list|)
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
name|userID
operator|=
name|callback
operator|.
name|getName
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
name|warn
argument_list|(
literal|"Credentials- or NameCallback must be supported"
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
literal|"Name-Callback failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|userID
operator|==
literal|null
condition|)
block|{
name|userID
operator|=
name|getSharedLoginName
argument_list|()
expr_stmt|;
block|}
return|return
name|userID
return|;
block|}
specifier|private
name|boolean
name|impersonate
parameter_list|(
name|Authentication
name|authentication
parameter_list|)
block|{
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
if|if
condition|(
name|authentication
operator|.
name|impersonate
argument_list|(
name|info
operator|.
name|getPrincipals
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|AuthInfo
name|getAuthInfo
parameter_list|()
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
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
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
name|credentials
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
name|userID
argument_list|,
name|attributes
argument_list|,
name|principals
argument_list|)
return|;
block|}
block|}
end_class

end_unit

