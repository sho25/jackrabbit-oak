begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|Properties
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
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|jmx
operator|.
name|CacheStatsMBean
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
name|core
operator|.
name|ContentRepositoryImpl
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
name|kernel
operator|.
name|KernelNodeStore
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
name|OsgiRepositoryInitializer
operator|.
name|RepositoryInitializerObserver
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
name|OakInitializer
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|AuthorizableNodeName
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
name|UserConstants
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
name|osgi
operator|.
name|framework
operator|.
name|Bundle
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
name|BundleActivator
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
name|ServiceFactory
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
name|ServiceReference
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

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|util
operator|.
name|tracker
operator|.
name|ServiceTracker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|util
operator|.
name|tracker
operator|.
name|ServiceTrackerCustomizer
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

begin_class
specifier|public
class|class
name|Activator
implements|implements
name|BundleActivator
implements|,
name|ServiceTrackerCustomizer
implements|,
name|RepositoryInitializerObserver
block|{
specifier|private
name|BundleContext
name|context
decl_stmt|;
specifier|private
name|ServiceTracker
name|microKernelTracker
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
comment|// see OAK-795 for a reason why the nodeStore tracker is disabled
comment|// private ServiceTracker nodeStoreTracker;
specifier|private
specifier|final
name|OsgiIndexProvider
name|indexProvider
init|=
operator|new
name|OsgiIndexProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiIndexEditorProvider
name|indexEditorProvider
init|=
operator|new
name|OsgiIndexEditorProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiEditorProvider
name|validatorProvider
init|=
operator|new
name|OsgiEditorProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiRepositoryInitializer
name|repositoryInitializerTracker
init|=
operator|new
name|OsgiRepositoryInitializer
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiAuthorizableActionProvider
name|authorizableActionProvider
init|=
operator|new
name|OsgiAuthorizableActionProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiRestrictionProvider
name|restrictionProvider
init|=
operator|new
name|OsgiRestrictionProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiSecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ServiceReference
argument_list|,
name|ServiceRegistration
argument_list|>
name|services
init|=
operator|new
name|HashMap
argument_list|<
name|ServiceReference
argument_list|,
name|ServiceRegistration
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Registration
argument_list|>
name|registrations
init|=
operator|new
name|ArrayList
argument_list|<
name|Registration
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Activator
parameter_list|()
block|{
name|securityProvider
operator|=
operator|new
name|OsgiSecurityProvider
argument_list|(
name|getSecurityConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------------< BundleActivator>---
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{
name|context
operator|=
name|bundleContext
expr_stmt|;
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|indexProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|indexEditorProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|validatorProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|repositoryInitializerTracker
operator|.
name|setObserver
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|repositoryInitializerTracker
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|authorizableActionProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|securityProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|microKernelTracker
operator|=
operator|new
name|ServiceTracker
argument_list|(
name|context
argument_list|,
name|MicroKernel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|microKernelTracker
operator|.
name|open
argument_list|()
expr_stmt|;
comment|// nodeStoreTracker = new ServiceTracker(
comment|// context, NodeStore.class.getName(), this);
comment|// nodeStoreTracker.open();
name|registerSecurityProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|)
throws|throws
name|Exception
block|{
comment|// nodeStoreTracker.close();
name|microKernelTracker
operator|.
name|close
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
name|validatorProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|repositoryInitializerTracker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|authorizableActionProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|restrictionProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
name|securityProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
for|for
control|(
name|Registration
name|r
range|:
name|registrations
control|)
block|{
name|r
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------< ServiceTrackerCustomizer>---
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|Object
name|service
init|=
name|context
operator|.
name|getService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|instanceof
name|MicroKernel
condition|)
block|{
name|MicroKernel
name|kernel
init|=
operator|(
name|MicroKernel
operator|)
name|service
decl_stmt|;
name|KernelNodeStore
name|store
init|=
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|)
decl_stmt|;
name|services
operator|.
name|put
argument_list|(
name|reference
argument_list|,
name|context
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|store
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|registrations
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CacheStatsMBean
operator|.
name|class
argument_list|,
name|store
operator|.
name|getCacheStats
argument_list|()
argument_list|,
name|CacheStatsMBean
operator|.
name|TYPE
argument_list|,
name|store
operator|.
name|getCacheStats
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|service
operator|instanceof
name|NodeStore
condition|)
block|{
name|NodeStore
name|store
init|=
operator|(
name|NodeStore
operator|)
name|service
decl_stmt|;
name|OakInitializer
operator|.
name|initialize
argument_list|(
name|store
argument_list|,
name|repositoryInitializerTracker
argument_list|,
name|indexEditorProvider
argument_list|)
expr_stmt|;
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
name|securityProvider
argument_list|)
operator|.
name|with
argument_list|(
name|validatorProvider
argument_list|)
operator|.
name|with
argument_list|(
name|indexProvider
argument_list|)
operator|.
name|with
argument_list|(
name|whiteboard
argument_list|)
operator|.
name|with
argument_list|(
name|indexEditorProvider
argument_list|)
decl_stmt|;
name|services
operator|.
name|put
argument_list|(
name|reference
argument_list|,
name|context
operator|.
name|registerService
argument_list|(
name|ContentRepository
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|oak
operator|.
name|createContentRepository
argument_list|()
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|service
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modifiedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|removedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
name|services
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|context
operator|.
name|ungetService
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------< RepositoryInitializerObserver>---
annotation|@
name|Override
specifier|public
name|void
name|newRepositoryInitializer
parameter_list|(
name|RepositoryInitializer
name|ri
parameter_list|)
block|{
name|List
argument_list|<
name|ServiceReference
argument_list|>
name|mkRefs
init|=
operator|new
name|ArrayList
argument_list|<
name|ServiceReference
argument_list|>
argument_list|(
name|services
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ServiceReference
name|ref
range|:
name|mkRefs
control|)
block|{
name|Object
name|service
init|=
name|context
operator|.
name|getService
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|instanceof
name|ContentRepositoryImpl
condition|)
block|{
name|ContentRepositoryImpl
name|repository
init|=
operator|(
name|ContentRepositoryImpl
operator|)
name|service
decl_stmt|;
name|OakInitializer
operator|.
name|initialize
argument_list|(
name|repository
operator|.
name|getNodeStore
argument_list|()
argument_list|,
name|ri
argument_list|,
name|indexEditorProvider
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|ConfigurationParameters
name|getSecurityConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|authorizableActionProvider
argument_list|,
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|AuthorizableNodeName
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
comment|// TODO
name|Map
argument_list|<
name|String
argument_list|,
name|OsgiRestrictionProvider
argument_list|>
name|authorizMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|PARAM_RESTRICTION_PROVIDER
argument_list|,
name|restrictionProvider
argument_list|)
decl_stmt|;
name|ConfigurationParameters
name|securityConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|userMap
argument_list|)
argument_list|,
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|authorizMap
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|securityConfig
return|;
block|}
specifier|private
name|void
name|registerSecurityProvider
parameter_list|()
block|{
name|ServiceFactory
name|sf
init|=
operator|new
name|ServiceFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|getService
parameter_list|(
name|Bundle
name|bundle
parameter_list|,
name|ServiceRegistration
name|serviceRegistration
parameter_list|)
block|{
return|return
name|securityProvider
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|ungetService
parameter_list|(
name|Bundle
name|bundle
parameter_list|,
name|ServiceRegistration
name|serviceRegistration
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
decl_stmt|;
specifier|final
name|ServiceRegistration
name|r
init|=
name|context
operator|.
name|registerService
argument_list|(
name|SecurityProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|registrations
operator|.
name|add
argument_list|(
operator|new
name|Registration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|r
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

