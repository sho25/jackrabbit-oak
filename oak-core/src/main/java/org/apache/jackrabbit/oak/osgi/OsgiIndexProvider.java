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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|query
operator|.
name|CompositeQueryIndexProvider
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
name|query
operator|.
name|QueryIndex
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
name|query
operator|.
name|QueryIndexProvider
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

begin_comment
comment|/**  * This index provider combines all indexes of all available OSGi index  * providers.  */
end_comment

begin_class
specifier|public
class|class
name|OsgiIndexProvider
implements|implements
name|ServiceTrackerCustomizer
implements|,
name|QueryIndexProvider
block|{
specifier|private
name|BundleContext
name|context
decl_stmt|;
specifier|private
name|ServiceTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ServiceReference
argument_list|,
name|QueryIndexProvider
argument_list|>
name|providers
init|=
operator|new
name|HashMap
argument_list|<
name|ServiceReference
argument_list|,
name|QueryIndexProvider
argument_list|>
argument_list|()
decl_stmt|;
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
name|tracker
operator|=
operator|new
name|ServiceTracker
argument_list|(
name|bundleContext
argument_list|,
name|QueryIndexProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|QueryIndexProvider
condition|)
block|{
name|QueryIndexProvider
name|provider
init|=
operator|(
name|QueryIndexProvider
operator|)
name|service
decl_stmt|;
name|providers
operator|.
name|put
argument_list|(
name|reference
argument_list|,
name|provider
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
else|else
block|{
name|context
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
name|providers
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|context
operator|.
name|ungetService
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|QueryIndex
argument_list|>
name|getQueryIndexes
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|QueryIndexProvider
name|composite
init|=
name|CompositeQueryIndexProvider
operator|.
name|compose
argument_list|(
name|providers
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|composite
operator|.
name|getQueryIndexes
argument_list|(
name|nodeStore
argument_list|)
return|;
block|}
block|}
end_class

end_unit

