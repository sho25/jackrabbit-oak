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
name|plugins
operator|.
name|nodetype
operator|.
name|DefaultTypeEditor
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
name|SecurityProviderImpl
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
name|state
operator|.
name|NodeStore
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
name|ServiceTracker
name|nodeStoreTracker
decl_stmt|;
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
name|OsgiIndexHookProvider
name|indexHookProvider
init|=
operator|new
name|OsgiIndexHookProvider
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
name|indexProvider
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|indexHookProvider
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
name|nodeStoreTracker
operator|=
operator|new
name|ServiceTracker
argument_list|(
name|context
argument_list|,
name|NodeStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|nodeStoreTracker
operator|.
name|open
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
name|nodeStoreTracker
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|indexHookProvider
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
operator|new
name|KernelNodeStore
argument_list|(
name|kernel
argument_list|)
argument_list|,
operator|new
name|Properties
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
name|indexHookProvider
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
comment|// FIXME: proper osgi setup for security provider (see OAK-17 and sub-tasks)
operator|.
name|with
argument_list|(
operator|new
name|SecurityProviderImpl
argument_list|()
argument_list|)
comment|// TODO: DefaultTypeEditor is JCR specific and does not belong here
operator|.
name|with
argument_list|(
operator|new
name|DefaultTypeEditor
argument_list|()
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
name|indexHookProvider
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
name|indexHookProvider
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

