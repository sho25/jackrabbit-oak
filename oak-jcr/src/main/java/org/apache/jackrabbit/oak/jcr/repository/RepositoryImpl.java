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
name|jcr
operator|.
name|repository
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|Repository
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
name|Session
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
name|jcr
operator|.
name|Value
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
name|JackrabbitRepository
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
name|commons
operator|.
name|SimpleValueFactory
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
name|jcr
operator|.
name|session
operator|.
name|RefreshStrategy
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
name|jcr
operator|.
name|session
operator|.
name|RefreshStrategy
operator|.
name|LogOnce
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
name|jcr
operator|.
name|session
operator|.
name|RefreshStrategy
operator|.
name|Once
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
name|jcr
operator|.
name|session
operator|.
name|RefreshStrategy
operator|.
name|ThreadSynchronising
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
name|jcr
operator|.
name|session
operator|.
name|RefreshStrategy
operator|.
name|Timed
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
name|jcr
operator|.
name|session
operator|.
name|SessionContext
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
name|jcr
operator|.
name|delegate
operator|.
name|SessionDelegate
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
name|whiteboard
operator|.
name|Whiteboard
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
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|RepositoryImpl
implements|implements
name|JackrabbitRepository
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
name|RepositoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Name of the session attribute value determining the session refresh      * interval in seconds.      *      * @see org.apache.jackrabbit.oak.jcr.session.RefreshStrategy      */
specifier|public
specifier|static
specifier|final
name|String
name|REFRESH_INTERVAL
init|=
literal|"oak.refresh-interval"
decl_stmt|;
specifier|private
specifier|final
name|Descriptors
name|descriptors
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|protected
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|threadSaveCount
decl_stmt|;
specifier|public
name|RepositoryImpl
parameter_list|(
annotation|@
name|Nonnull
name|ContentRepository
name|contentRepository
parameter_list|,
annotation|@
name|Nonnull
name|Whiteboard
name|whiteboard
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|contentRepository
operator|=
name|checkNotNull
argument_list|(
name|contentRepository
argument_list|)
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|checkNotNull
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|checkNotNull
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadSaveCount
operator|=
operator|new
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|descriptors
operator|=
name|determineDescriptors
argument_list|()
expr_stmt|;
block|}
comment|//---------------------------------------------------------< Repository>---
comment|/**      * @see javax.jcr.Repository#getDescriptorKeys()      */
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDescriptorKeys
parameter_list|()
block|{
return|return
name|descriptors
operator|.
name|getKeys
argument_list|()
return|;
block|}
comment|/**      * @see Repository#isStandardDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isStandardDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|isStandardDescriptor
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|String
name|getDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
name|Value
name|v
init|=
name|getDescriptorValue
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
literal|null
else|:
name|v
operator|.
name|getString
argument_list|()
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
literal|"Error converting value for descriptor with key {} to string"
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptorValue(String)      */
annotation|@
name|Override
specifier|public
name|Value
name|getDescriptorValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|getValue
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptorValues(String)      */
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getDescriptorValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|getValues
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#isSingleValueDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isSingleValueDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|isSingleValueDescriptor
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#login(javax.jcr.Credentials, String)      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|,
annotation|@
name|Nullable
name|String
name|workspaceName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * {@code null} arguments.      *      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * the given credentials and a {@code null} workspace name.      *      * @param credentials login credentials      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
name|credentials
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * {@code null} credentials and the given workspace name.      *      * @param workspace workspace name      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|String
name|workspace
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|,
name|workspace
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< JackrabbitRepository>---
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
annotation|@
name|CheckForNull
name|Credentials
name|credentials
parameter_list|,
annotation|@
name|CheckForNull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|CheckForNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|Long
name|refreshInterval
init|=
name|getRefreshInterval
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
if|if
condition|(
name|refreshInterval
operator|==
literal|null
condition|)
block|{
name|refreshInterval
operator|=
name|getRefreshInterval
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
name|REFRESH_INTERVAL
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Duplicate attribute '"
operator|+
name|REFRESH_INTERVAL
operator|+
literal|"'."
argument_list|)
throw|;
block|}
name|RefreshStrategy
name|refreshStrategy
init|=
name|createRefreshStrategy
argument_list|(
name|refreshInterval
argument_list|)
decl_stmt|;
name|ContentSession
name|contentSession
init|=
name|contentRepository
operator|.
name|login
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|)
decl_stmt|;
name|SessionDelegate
name|sessionDelegate
init|=
operator|new
name|SessionDelegate
argument_list|(
name|contentSession
argument_list|,
name|refreshStrategy
argument_list|,
name|securityProvider
argument_list|)
decl_stmt|;
name|SessionContext
name|context
init|=
name|createSessionContext
argument_list|(
name|securityProvider
argument_list|,
name|createAttributes
argument_list|(
name|refreshInterval
argument_list|)
argument_list|,
name|sessionDelegate
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|getSession
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|jcr
operator|.
name|LoginException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
comment|// empty
block|}
comment|//------------------------------------------------------------< internal>---
comment|/**      * Factory method for creating a {@link SessionContext} instance for      * a new session. Called by {@link #login()}. Can be overridden by      * subclasses to customize the session implementation.      *      * @return session context      */
specifier|protected
name|SessionContext
name|createSessionContext
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|,
name|SessionDelegate
name|delegate
parameter_list|)
block|{
return|return
operator|new
name|SessionContext
argument_list|(
name|this
argument_list|,
name|securityProvider
argument_list|,
name|whiteboard
argument_list|,
name|attributes
argument_list|,
name|delegate
argument_list|)
return|;
block|}
comment|/**      * Provides descriptors for current repository implementations. Can be overridden      * by the subclasses to add more values to the descriptor      * @return  repository descriptor      */
specifier|protected
name|Descriptors
name|determineDescriptors
parameter_list|()
block|{
return|return
operator|new
name|Descriptors
argument_list|(
operator|new
name|SimpleValueFactory
argument_list|()
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|Long
name|getRefreshInterval
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|Object
name|value
init|=
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
operator|.
name|getAttribute
argument_list|(
name|REFRESH_INTERVAL
argument_list|)
decl_stmt|;
return|return
name|toLong
argument_list|(
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|credentials
operator|instanceof
name|TokenCredentials
condition|)
block|{
name|String
name|value
init|=
operator|(
operator|(
name|TokenCredentials
operator|)
name|credentials
operator|)
operator|.
name|getAttribute
argument_list|(
name|REFRESH_INTERVAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|toLong
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|Long
name|getRefreshInterval
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
block|{
return|return
name|toLong
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|REFRESH_INTERVAL
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Long
name|toLong
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
return|return
operator|(
name|Long
operator|)
name|value
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
return|return
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|toLong
argument_list|(
operator|(
name|String
operator|)
name|value
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
specifier|static
name|Long
name|toLong
parameter_list|(
name|String
name|longValue
parameter_list|)
block|{
try|try
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|longValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid value '"
operator|+
name|longValue
operator|+
literal|"' for "
operator|+
name|REFRESH_INTERVAL
operator|+
literal|". Expected long. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|createAttributes
parameter_list|(
name|Long
name|refreshInterval
parameter_list|)
block|{
return|return
name|refreshInterval
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|emptyMap
argument_list|()
operator|:
name|Collections
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|singletonMap
argument_list|(
name|REFRESH_INTERVAL
argument_list|,
name|refreshInterval
argument_list|)
return|;
block|}
comment|/**      * Auto refresh logic for sessions, which is done to enhance backwards compatibility with      * Jackrabbit 2.      *<p>      * A sessions is automatically refreshed when      *<ul>      *<li>it has not been accessed for the number of seconds specified by the      *         {@code refreshInterval} parameter,</li>      *<li>an observation event has been delivered to a listener registered from within this      *         session,</li>      *<li>an updated occurred through a different session from<em>within the same      *         thread.</em></li>      *</ul>      * In addition a warning is logged once per session if the session is accessed after one      * minute of inactivity.      */
specifier|private
name|RefreshStrategy
name|createRefreshStrategy
parameter_list|(
name|Long
name|refreshInterval
parameter_list|)
block|{
return|return
operator|new
name|RefreshStrategy
argument_list|(
name|refreshInterval
operator|==
literal|null
condition|?
operator|new
name|RefreshStrategy
index|[]
block|{
operator|new
name|Once
argument_list|(
literal|false
argument_list|)
block|,
operator|new
name|LogOnce
argument_list|(
literal|60
argument_list|)
block|,
operator|new
name|ThreadSynchronising
argument_list|(
name|threadSaveCount
argument_list|)
block|}
else|:
operator|new
name|RefreshStrategy
index|[]
block|{
operator|new
name|Once
argument_list|(
literal|false
argument_list|)
block|,
operator|new
name|Timed
argument_list|(
name|refreshInterval
argument_list|)
block|,
operator|new
name|LogOnce
argument_list|(
literal|60
argument_list|)
block|,
operator|new
name|ThreadSynchronising
argument_list|(
name|threadSaveCount
argument_list|)
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

