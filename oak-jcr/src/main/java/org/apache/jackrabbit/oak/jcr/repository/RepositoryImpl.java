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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
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
operator|.
name|registerMBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|concurrent
operator|.
name|Callable
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
name|ScheduledThreadPoolExecutor
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
name|ThreadFactory
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|api
operator|.
name|jmx
operator|.
name|SessionMBean
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
name|Composite
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
name|session
operator|.
name|SessionStats
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
name|observation
operator|.
name|CommitRateLimiter
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
name|gc
operator|.
name|DelegatingGCMonitor
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
name|gc
operator|.
name|GCMonitor
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
name|Registration
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
name|stats
operator|.
name|Clock
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
name|stats
operator|.
name|StatisticManager
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
name|util
operator|.
name|GenericDescriptors
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
comment|/**      * Name of the session attribute value determining the session refresh      * interval in seconds.      *      * @see org.apache.jackrabbit.oak.jcr.session.RefreshStrategy      */
specifier|public
specifier|static
specifier|final
name|String
name|REFRESH_INTERVAL
init|=
literal|"oak.refresh-interval"
decl_stmt|;
comment|/**      * Name of the session attribute for enabling relaxed locking rules      *      * @see<a href="https://issues.apache.org/jira/browse/OAK-1329">OAK-1329</a>      */
specifier|public
specifier|static
specifier|final
name|String
name|RELAXED_LOCKING
init|=
literal|"oak.relaxed-locking"
decl_stmt|;
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
specifier|protected
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|protected
specifier|final
name|boolean
name|fastQueryResultSize
decl_stmt|;
specifier|private
specifier|final
name|GenericDescriptors
name|descriptors
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|int
name|observationQueueLength
decl_stmt|;
specifier|private
specifier|final
name|CommitRateLimiter
name|commitRateLimiter
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|DelegatingGCMonitor
name|gcMonitor
init|=
operator|new
name|DelegatingGCMonitor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Registration
name|gcMonitorRegistration
decl_stmt|;
comment|/**      * {@link ThreadLocal} counter that keeps track of the save operations      * performed per thread so far. This is is then used to determine if      * the current session needs to be refreshed to see the changes done by      * another session in the same thread.      *<p>      *<b>Note</b> - This thread local is never cleared. However, we only      * store a {@link Long} instance and do not derive from      * {@link ThreadLocal} so that (class loader) leaks typically associated      * with thread locals do not occur.      */
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|threadSaveCount
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduledExecutor
init|=
name|createListeningScheduledExecutorService
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StatisticManager
name|statisticManager
decl_stmt|;
comment|/**      * Constructor used for backward compatibility.      */
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
parameter_list|,
name|int
name|observationQueueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|this
argument_list|(
name|contentRepository
argument_list|,
name|whiteboard
argument_list|,
name|securityProvider
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|int
name|observationQueueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|,
name|boolean
name|fastQueryResultSize
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
name|observationQueueLength
operator|=
name|observationQueueLength
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|commitRateLimiter
expr_stmt|;
name|this
operator|.
name|descriptors
operator|=
name|determineDescriptors
argument_list|()
expr_stmt|;
name|this
operator|.
name|statisticManager
operator|=
operator|new
name|StatisticManager
argument_list|(
name|whiteboard
argument_list|,
name|scheduledExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Fast
argument_list|(
name|scheduledExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|gcMonitorRegistration
operator|=
name|whiteboard
operator|.
name|register
argument_list|(
name|GCMonitor
operator|.
name|class
argument_list|,
name|gcMonitor
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fastQueryResultSize
operator|=
name|fastQueryResultSize
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
name|boolean
name|relaxedLocking
init|=
name|getRelaxedLocking
argument_list|(
name|attributes
argument_list|)
decl_stmt|;
name|RefreshStrategy
name|refreshStrategy
init|=
name|refreshInterval
operator|==
literal|null
condition|?
operator|new
name|RefreshStrategy
operator|.
name|LogOnce
argument_list|(
literal|60
argument_list|)
else|:
operator|new
name|RefreshStrategy
operator|.
name|Timed
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
name|createSessionDelegate
argument_list|(
name|refreshStrategy
argument_list|,
name|contentSession
argument_list|)
decl_stmt|;
name|SessionContext
name|context
init|=
name|createSessionContext
argument_list|(
name|statisticManager
argument_list|,
name|securityProvider
argument_list|,
name|createAttributes
argument_list|(
name|refreshInterval
argument_list|,
name|relaxedLocking
argument_list|)
argument_list|,
name|sessionDelegate
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
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
specifier|private
name|SessionDelegate
name|createSessionDelegate
parameter_list|(
name|RefreshStrategy
name|refreshStrategy
parameter_list|,
name|ContentSession
name|contentSession
parameter_list|)
block|{
specifier|final
name|RefreshOnGC
name|refreshOnGC
init|=
operator|new
name|RefreshOnGC
argument_list|(
name|gcMonitor
argument_list|)
decl_stmt|;
name|refreshStrategy
operator|=
name|Composite
operator|.
name|create
argument_list|(
name|refreshStrategy
argument_list|,
name|refreshOnGC
argument_list|)
expr_stmt|;
return|return
operator|new
name|SessionDelegate
argument_list|(
name|contentSession
argument_list|,
name|securityProvider
argument_list|,
name|refreshStrategy
argument_list|,
name|threadSaveCount
argument_list|,
name|statisticManager
argument_list|,
name|clock
argument_list|)
block|{
comment|// Defer session MBean registration to avoid cluttering the
comment|// JMX name space with short lived sessions
name|RegistrationTask
name|registrationTask
init|=
operator|new
name|RegistrationTask
argument_list|(
name|getSessionStats
argument_list|()
argument_list|,
name|whiteboard
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledTask
init|=
name|scheduledExecutor
operator|.
name|schedule
argument_list|(
name|registrationTask
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|()
block|{
name|refreshOnGC
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Cancel session MBean registration
name|registrationTask
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|scheduledTask
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|super
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|statisticManager
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|gcMonitorRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|closeExecutor
argument_list|()
expr_stmt|;
if|if
condition|(
name|contentRepository
operator|instanceof
name|Closeable
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
operator|(
name|Closeable
operator|)
name|contentRepository
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|closeExecutor
parameter_list|()
block|{
try|try
block|{
name|scheduledExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|scheduledExecutor
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while shutting down the executorService"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|scheduledExecutor
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"executorService didn't shutdown properly. Will be forced now."
argument_list|)
expr_stmt|;
block|}
name|scheduledExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< internal>---
comment|/**      * Factory method for creating a {@link SessionContext} instance for      * a new session. Called by {@link #login()}. Can be overridden by      * subclasses to customize the session implementation.      *      * @return session context      */
specifier|protected
name|SessionContext
name|createSessionContext
parameter_list|(
name|StatisticManager
name|statisticManager
parameter_list|,
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
parameter_list|,
name|int
name|observationQueueLength
parameter_list|,
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
return|return
operator|new
name|SessionContext
argument_list|(
name|this
argument_list|,
name|statisticManager
argument_list|,
name|securityProvider
argument_list|,
name|whiteboard
argument_list|,
name|attributes
argument_list|,
name|delegate
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
argument_list|,
name|fastQueryResultSize
argument_list|)
return|;
block|}
comment|/**      * Provides descriptors for current repository implementations. Can be overridden      * by the subclasses to add more values to the descriptor      * @return  repository descriptor      */
specifier|protected
name|GenericDescriptors
name|determineDescriptors
parameter_list|()
block|{
return|return
operator|new
name|JcrDescriptorsImpl
argument_list|(
name|contentRepository
operator|.
name|getDescriptors
argument_list|()
argument_list|,
operator|new
name|SimpleValueFactory
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the descriptors associated with the repository      * @return repository descriptor      */
specifier|protected
name|GenericDescriptors
name|getDescriptors
parameter_list|()
block|{
return|return
name|descriptors
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|ScheduledExecutorService
name|createListeningScheduledExecutorService
parameter_list|()
block|{
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|newName
argument_list|()
argument_list|)
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
specifier|private
name|String
name|newName
parameter_list|()
block|{
return|return
literal|"oak-repository-executor-"
operator|+
name|counter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
name|tf
argument_list|)
block|{
comment|// purge the list of schedule tasks before scheduling a new task in order
comment|// to reduce memory consumption in the face of many cancelled tasks. See OAK-1890.
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|ScheduledFuture
argument_list|<
name|V
argument_list|>
name|schedule
parameter_list|(
name|Callable
argument_list|<
name|V
argument_list|>
name|callable
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|schedule
argument_list|(
name|callable
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|schedule
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|schedule
argument_list|(
name|command
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleAtFixedRate
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|initialDelay
parameter_list|,
name|long
name|period
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|scheduleAtFixedRate
argument_list|(
name|command
argument_list|,
name|initialDelay
argument_list|,
name|period
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleWithFixedDelay
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|initialDelay
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|purge
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|command
argument_list|,
name|initialDelay
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
name|boolean
name|getRelaxedLocking
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
name|Object
name|value
init|=
name|attributes
operator|.
name|get
argument_list|(
name|RELAXED_LOCKING
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|value
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
name|Boolean
operator|.
name|parseBoolean
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
literal|false
return|;
block|}
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
parameter_list|,
name|boolean
name|relaxedLocking
parameter_list|)
block|{
if|if
condition|(
name|refreshInterval
operator|==
literal|null
operator|&&
operator|!
name|relaxedLocking
condition|)
block|{
return|return
name|emptyMap
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|refreshInterval
operator|==
literal|null
condition|)
block|{
return|return
name|singletonMap
argument_list|(
name|RELAXED_LOCKING
argument_list|,
operator|(
name|Object
operator|)
name|Boolean
operator|.
name|valueOf
argument_list|(
name|relaxedLocking
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|relaxedLocking
condition|)
block|{
return|return
name|singletonMap
argument_list|(
name|REFRESH_INTERVAL
argument_list|,
operator|(
name|Object
operator|)
name|refreshInterval
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
name|REFRESH_INTERVAL
argument_list|,
operator|(
name|Object
operator|)
name|refreshInterval
argument_list|,
name|RELAXED_LOCKING
argument_list|,
operator|(
name|Object
operator|)
name|Boolean
operator|.
name|valueOf
argument_list|(
name|relaxedLocking
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|RefreshOnGC
extends|extends
name|GCMonitor
operator|.
name|Empty
implements|implements
name|RefreshStrategy
block|{
specifier|private
specifier|final
name|Registration
name|registration
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|compacted
decl_stmt|;
specifier|public
name|RefreshOnGC
parameter_list|(
name|DelegatingGCMonitor
name|gcMonitor
parameter_list|)
block|{
name|registration
operator|=
name|gcMonitor
operator|.
name|registerGCMonitor
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|(
name|long
index|[]
name|segmentCounts
parameter_list|,
name|long
index|[]
name|recordCounts
parameter_list|,
name|long
index|[]
name|compactionMapWeights
parameter_list|)
block|{
name|compacted
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRefresh
parameter_list|(
name|long
name|secondsSinceLastAccess
parameter_list|)
block|{
return|return
name|compacted
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshed
parameter_list|()
block|{
name|compacted
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Refresh on revision garbage collection"
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|RegistrationTask
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|SessionStats
name|sessionStats
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|boolean
name|cancelled
decl_stmt|;
specifier|private
name|Registration
name|completed
decl_stmt|;
specifier|public
name|RegistrationTask
parameter_list|(
name|SessionStats
name|sessionStats
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|sessionStats
operator|=
name|sessionStats
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|cancelled
condition|)
block|{
name|completed
operator|=
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|SessionMBean
operator|.
name|class
argument_list|,
name|sessionStats
argument_list|,
name|SessionMBean
operator|.
name|TYPE
argument_list|,
name|sessionStats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|cancel
parameter_list|()
block|{
name|cancelled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|completed
operator|!=
literal|null
condition|)
block|{
name|completed
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|completed
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

