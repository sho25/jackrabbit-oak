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
name|spi
operator|.
name|lifecycle
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
comment|/**  * Composite repository initializer that delegates the  * {@link #initialize(NodeBuilder)} call in sequence to all the  * component initializers.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeInitializer
implements|implements
name|RepositoryInitializer
block|{
specifier|private
specifier|final
name|Collection
argument_list|<
name|RepositoryInitializer
argument_list|>
name|initializers
decl_stmt|;
specifier|public
name|CompositeInitializer
parameter_list|(
name|Collection
argument_list|<
name|RepositoryInitializer
argument_list|>
name|trackers
parameter_list|)
block|{
name|this
operator|.
name|initializers
operator|=
name|trackers
expr_stmt|;
block|}
specifier|public
name|CompositeInitializer
parameter_list|(
name|RepositoryInitializer
modifier|...
name|initializers
parameter_list|)
block|{
name|this
operator|.
name|initializers
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|initializers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
for|for
control|(
name|RepositoryInitializer
name|tracker
range|:
name|initializers
control|)
block|{
name|tracker
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

