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
name|util
operator|.
name|Map
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
name|principal
operator|.
name|EveryonePrincipal
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
comment|/**  * The {@code GuestLoginModule} is intended to provide backwards compatibility  * with the login handling present in the JCR reference implementation located  * in jackrabbit-core. While the specification claims that {@link javax.jcr.Repository#login}  * with {@code null} Credentials implies that the authentication process is  * handled externally, the default implementation jackrabbit-core treated it  * as 'anonymous' login such as covered by using {@link GuestCredentials}.<p/>  *  * This {@code LoginModule} implementation performs the following tasks upon  * {@link #login()}.  *  *<ol>  *<li>Try to retrieve JCR credentials from the {@link CallbackHandler} using  *     the {@link CredentialsCallback}</li>  *<li>In case no credentials could be obtained it pushes a new instance of  *     {@link GuestCredentials} to the shared stated. Subsequent login modules  *     in the authentication process may retrieve the {@link GuestCredentials}  *     instead of failing to obtain any credentials.</li>  *</ol>  *  * If this login module pushed {@link GuestLoginModule} to the shared state  * in phase 1 it will add those credentials and the {@link EveryonePrincipal}  * to the subject in phase 2 of the login process. Subsequent login modules  * my choose to provide additional principals/credentials associated with  * a guest login.<p/>  *  * The authentication configuration using this {@code LoginModule} could for  * example look as follows:  *  *<pre>  *  *    jackrabbit.oak {  *            org.apache.jackrabbit.oak.spi.security.authentication.GuestLoginModule  optional;  *            org.apache.jackrabbit.oak.security.authentication.user.LoginModuleImpl required;  *    };  *  *</pre>  *  * In this case calling {@link javax.jcr.Repository#login()} would be equivalent  * to {@link javax.jcr.Repository#login(javax.jcr.Credentials) repository.login(new GuestCredentials()}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|GuestLoginModule
implements|implements
name|LoginModule
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
name|GuestLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|CallbackHandler
name|callbackHandler
decl_stmt|;
specifier|private
name|Map
name|sharedState
decl_stmt|;
specifier|private
name|GuestCredentials
name|guestCredentials
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
name|login
parameter_list|()
block|{
if|if
condition|(
name|callbackHandler
operator|!=
literal|null
condition|)
block|{
name|CredentialsCallback
name|ccb
init|=
operator|new
name|CredentialsCallback
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
name|ccb
block|}
argument_list|)
expr_stmt|;
name|Credentials
name|credentials
init|=
name|ccb
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
block|{
name|guestCredentials
operator|=
operator|new
name|GuestCredentials
argument_list|()
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|AbstractLoginModule
operator|.
name|SHARED_KEY_CREDENTIALS
argument_list|,
name|guestCredentials
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
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
literal|"Login: Failed to retrieve Credentials from CallbackHandler"
argument_list|,
name|e
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
literal|"Login: Failed to retrieve Credentials from CallbackHandler"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ignore this login module
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
block|{
if|if
condition|(
name|guestCredentials
operator|!=
literal|null
operator|&&
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
name|guestCredentials
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|abort
parameter_list|()
block|{
name|guestCredentials
operator|=
literal|null
expr_stmt|;
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
block|{
comment|// nothing to do.
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

