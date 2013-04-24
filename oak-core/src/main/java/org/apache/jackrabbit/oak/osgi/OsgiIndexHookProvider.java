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
name|List
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
name|plugins
operator|.
name|index
operator|.
name|CompositeIndexHookProvider
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
name|IndexHook
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
name|IndexHookProvider
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
name|NodeBuilder
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
name|NodeState
import|;
end_import

begin_comment
comment|/**  * This IndexHook provider combines all index hooks of all available OSGi  * IndexHook providers.  */
end_comment

begin_class
specifier|public
class|class
name|OsgiIndexHookProvider
extends|extends
name|AbstractServiceTracker
argument_list|<
name|IndexHookProvider
argument_list|>
implements|implements
name|IndexHookProvider
block|{
specifier|public
name|OsgiIndexHookProvider
parameter_list|()
block|{
name|super
argument_list|(
name|IndexHookProvider
operator|.
name|class
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
name|IndexHook
argument_list|>
name|getIndexHooks
parameter_list|(
name|String
name|type
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
name|IndexHookProvider
name|composite
init|=
name|CompositeIndexHookProvider
operator|.
name|compose
argument_list|(
name|getServices
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|composite
operator|.
name|getIndexHooks
argument_list|(
name|type
argument_list|,
name|builder
argument_list|,
name|root
argument_list|)
return|;
block|}
block|}
end_class

end_unit

