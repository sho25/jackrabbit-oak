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
name|user
operator|.
name|action
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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

begin_comment
comment|/**  * Aggregates a collection of {@link AuthorizableActionProvider}s into a single  * provider.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeActionProvider
implements|implements
name|AuthorizableActionProvider
block|{
specifier|private
specifier|final
name|Collection
argument_list|<
name|?
extends|extends
name|AuthorizableActionProvider
argument_list|>
name|providers
decl_stmt|;
specifier|public
name|CompositeActionProvider
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|AuthorizableActionProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|=
name|providers
expr_stmt|;
block|}
specifier|public
name|CompositeActionProvider
parameter_list|(
name|AuthorizableActionProvider
modifier|...
name|providers
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizableAction
argument_list|>
name|actions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|AuthorizableActionProvider
name|p
range|:
name|providers
control|)
block|{
name|actions
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getAuthorizableActions
argument_list|(
name|securityProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|actions
return|;
block|}
block|}
end_class

end_unit

