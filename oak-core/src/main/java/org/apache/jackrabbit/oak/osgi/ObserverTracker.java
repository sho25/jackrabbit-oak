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
name|osgi
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
name|checkState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|io
operator|.
name|Closeables
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
name|io
operator|.
name|Closer
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
name|commit
operator|.
name|Observable
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
name|Observer
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
name|ObserverTracker
implements|implements
name|ServiceTrackerCustomizer
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|ServiceReference
argument_list|,
name|Closeable
argument_list|>
name|subscriptions
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OsgiExecutor
name|osgiExecutor
init|=
operator|new
name|OsgiExecutor
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Observable
name|observable
decl_stmt|;
specifier|private
name|BundleContext
name|bundleContext
decl_stmt|;
specifier|private
name|ServiceTracker
name|observerTracker
decl_stmt|;
specifier|public
name|ObserverTracker
parameter_list|(
annotation|@
name|Nonnull
name|Observable
name|observable
parameter_list|)
block|{
name|this
operator|.
name|observable
operator|=
name|checkNotNull
argument_list|(
name|observable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|(
annotation|@
name|Nonnull
name|BundleContext
name|bundleContext
parameter_list|)
block|{
name|checkState
argument_list|(
name|this
operator|.
name|bundleContext
operator|==
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|bundleContext
operator|=
name|checkNotNull
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|osgiExecutor
operator|.
name|start
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|observerTracker
operator|=
operator|new
name|ServiceTracker
argument_list|(
name|bundleContext
argument_list|,
name|Observer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|observerTracker
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|checkState
argument_list|(
name|this
operator|.
name|bundleContext
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|observerTracker
operator|.
name|close
argument_list|()
expr_stmt|;
name|osgiExecutor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//------------------------< ServiceTrackerCustomizer>----------------------
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
name|bundleContext
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
name|Observer
condition|)
block|{
name|Closer
name|subscription
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|BackgroundObserver
name|observer
init|=
name|subscription
operator|.
name|register
argument_list|(
operator|new
name|BackgroundObserver
argument_list|(
operator|(
name|Observer
operator|)
name|service
argument_list|,
name|osgiExecutor
argument_list|)
argument_list|)
decl_stmt|;
name|subscription
operator|.
name|register
argument_list|(
name|observable
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
argument_list|)
expr_stmt|;
name|subscriptions
operator|.
name|put
argument_list|(
name|reference
argument_list|,
name|subscription
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
else|else
block|{
name|bundleContext
operator|.
name|ungetService
argument_list|(
name|reference
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
if|if
condition|(
name|subscriptions
operator|.
name|containsKey
argument_list|(
name|reference
argument_list|)
condition|)
block|{
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|subscriptions
operator|.
name|get
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
name|bundleContext
operator|.
name|ungetService
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

