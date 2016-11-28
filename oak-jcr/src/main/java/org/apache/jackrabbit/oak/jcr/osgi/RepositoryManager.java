begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|osgi
package|;
end_package

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
name|Properties
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|References
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
name|Oak
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
name|PropertiesUtil
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|commit
operator|.
name|JcrConflictHandler
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
name|index
operator|.
name|IndexEditorProvider
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|commit
operator|.
name|BackgroundObserver
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|state
operator|.
name|NodeStore
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
name|Tracker
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
name|WhiteboardEditorProvider
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
name|WhiteboardIndexEditorProvider
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
name|WhiteboardIndexProvider
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
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_comment
comment|/**  * RepositoryManager constructs the Repository instance and registers it with OSGi Service Registry.  * By default it would not be active and would require explicit configuration to be registered so as  * create repository. This is done to prevent repository creation in scenarios where repository needs  * to be configured in a custom way  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|References
argument_list|(
block|{
annotation|@
name|Reference
argument_list|(
name|referenceInterface
operator|=
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|strategy
operator|=
name|ReferenceStrategy
operator|.
name|LOOKUP
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|RepositoryManager
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
init|=
name|BackgroundObserver
operator|.
name|DEFAULT_QUEUE_SIZE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_COMMIT_RATE_LIMIT
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_FAST_QUERY_RESULT_SIZE
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardEditorProvider
name|editorProvider
init|=
operator|new
name|WhiteboardEditorProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardIndexEditorProvider
name|indexEditorProvider
init|=
operator|new
name|WhiteboardIndexEditorProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardIndexProvider
name|indexProvider
init|=
operator|new
name|WhiteboardIndexProvider
argument_list|()
decl_stmt|;
specifier|private
name|Tracker
argument_list|<
name|RepositoryInitializer
argument_list|>
name|initializers
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|ServiceRegistration
name|registration
decl_stmt|;
specifier|private
name|int
name|observationQueueLength
decl_stmt|;
specifier|private
name|CommitRateLimiter
name|commitRateLimiter
decl_stmt|;
specifier|private
name|boolean
name|fastQueryResultSize
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|NodeStore
name|store
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|target
operator|=
literal|"(type=property)"
argument_list|)
specifier|private
name|IndexEditorProvider
name|propertyIndex
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|target
operator|=
literal|"(type=reference)"
argument_list|)
specifier|private
name|IndexEditorProvider
name|referenceIndex
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
argument_list|,
name|name
operator|=
literal|"Observation queue length"
argument_list|,
name|description
operator|=
literal|"Maximum number of pending revisions in a observation listener queue"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|OBSERVATION_QUEUE_LENGTH
init|=
literal|"oak.observation.queue-length"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|DEFAULT_COMMIT_RATE_LIMIT
argument_list|,
name|name
operator|=
literal|"Commit rate limiter"
argument_list|,
name|description
operator|=
literal|"Limit the commit rate once the number of pending revisions in the observation "
operator|+
literal|"queue exceed 90% of its capacity."
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|COMMIT_RATE_LIMIT
init|=
literal|"oak.observation.limit-commit-rate"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|DEFAULT_FAST_QUERY_RESULT_SIZE
argument_list|,
name|name
operator|=
literal|"Fast query result size"
argument_list|,
name|description
operator|=
literal|"Whether the query result size should return an estimation (or -1 if disabled) for large queries"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|FAST_QUERY_RESULT_SIZE
init|=
literal|"oak.query.fastResultSize"
decl_stmt|;
specifier|private
name|OsgiRepository
name|repository
decl_stmt|;
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|observationQueueLength
operator|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|prop
argument_list|(
name|config
argument_list|,
name|bundleContext
argument_list|,
name|OBSERVATION_QUEUE_LENGTH
argument_list|)
argument_list|,
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|prop
argument_list|(
name|config
argument_list|,
name|bundleContext
argument_list|,
name|COMMIT_RATE_LIMIT
argument_list|)
argument_list|,
name|DEFAULT_COMMIT_RATE_LIMIT
argument_list|)
condition|)
block|{
name|commitRateLimiter
operator|=
operator|new
name|CommitRateLimiter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commitRateLimiter
operator|=
literal|null
expr_stmt|;
block|}
name|fastQueryResultSize
operator|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|prop
argument_list|(
name|config
argument_list|,
name|bundleContext
argument_list|,
name|FAST_QUERY_RESULT_SIZE
argument_list|)
argument_list|,
name|DEFAULT_FAST_QUERY_RESULT_SIZE
argument_list|)
expr_stmt|;
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|initializers
operator|=
name|whiteboard
operator|.
name|track
argument_list|(
name|RepositoryInitializer
operator|.
name|class
argument_list|)
expr_stmt|;
name|editorProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|indexEditorProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|indexProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|registration
operator|=
name|registerRepository
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Object
name|prop
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|,
name|BundleContext
name|bundleContext
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|//Prefer framework property first
name|Object
name|value
init|=
name|bundleContext
operator|.
name|getProperty
argument_list|(
name|name
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
name|value
return|;
block|}
comment|//Fallback to one from config
return|return
name|config
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Deactivate
specifier|public
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|registration
operator|!=
literal|null
condition|)
block|{
name|registration
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|registration
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|repository
operator|!=
literal|null
condition|)
block|{
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|repository
operator|=
literal|null
expr_stmt|;
block|}
name|initializers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|indexProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|indexEditorProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|editorProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ServiceRegistration
name|registerRepository
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|JcrConflictHandler
operator|.
name|createJcrConflictHandler
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|whiteboard
argument_list|)
operator|.
name|with
argument_list|(
name|securityProvider
argument_list|)
operator|.
name|with
argument_list|(
name|editorProvider
argument_list|)
operator|.
name|with
argument_list|(
name|indexEditorProvider
argument_list|)
operator|.
name|with
argument_list|(
name|indexProvider
argument_list|)
operator|.
name|withFailOnMissingIndexProvider
argument_list|()
operator|.
name|withAsyncIndexing
argument_list|()
decl_stmt|;
for|for
control|(
name|RepositoryInitializer
name|initializer
range|:
name|initializers
operator|.
name|getServices
argument_list|()
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|initializer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commitRateLimiter
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|commitRateLimiter
argument_list|)
expr_stmt|;
block|}
name|repository
operator|=
operator|new
name|OsgiRepository
argument_list|(
name|oak
operator|.
name|createContentRepository
argument_list|()
argument_list|,
name|whiteboard
argument_list|,
name|securityProvider
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
argument_list|,
name|fastQueryResultSize
argument_list|)
expr_stmt|;
return|return
name|bundleContext
operator|.
name|registerService
argument_list|(
name|Repository
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|repository
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

