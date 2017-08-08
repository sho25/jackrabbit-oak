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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Set
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
name|ConcurrentHashMap
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
name|CopyOnWriteArrayList
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
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
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
name|base
operator|.
name|Function
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
name|ImmutableList
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
name|Iterables
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ObjectArrays
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
name|Sets
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
name|PropertyState
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
name|Tree
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
name|tree
operator|.
name|TreeLocation
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
name|CommitHook
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
name|MoveTracker
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
name|ThreeWayConflictHandler
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
name|ValidatorProvider
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
name|CompositeInitializer
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
name|CompositeWorkspaceInitializer
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
name|lifecycle
operator|.
name|WorkspaceInitializer
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
name|xml
operator|.
name|ProtectedItemImporter
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
name|Constants
import|;
end_import

begin_comment
comment|/**  * Abstract base implementation for {@link SecurityConfiguration}s that can  * combine different implementations.  */
end_comment

begin_class
annotation|@
name|ProviderType
specifier|public
specifier|abstract
class|class
name|CompositeConfiguration
parameter_list|<
name|T
extends|extends
name|SecurityConfiguration
parameter_list|>
implements|implements
name|SecurityConfiguration
block|{
comment|/**      * Parameter used to define the ranking of a given configuration compared to      * other registered configuration in this aggregate. If the ranking parameter      * is missing a new configuration will be added at the end of the list.      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_RANKING
init|=
literal|"configurationRanking"
decl_stmt|;
comment|/**      * Default ranking value used to insert a new configuration at the end of      * the list.      */
specifier|private
specifier|static
specifier|final
name|int
name|NO_RANKING
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|configurations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Ranking
name|rankings
init|=
operator|new
name|Ranking
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|CompositeContext
name|ctx
init|=
operator|new
name|CompositeContext
argument_list|()
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|T
name|defaultConfig
decl_stmt|;
specifier|public
name|CompositeConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|CompositeConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|T
name|getDefaultConfig
parameter_list|()
block|{
return|return
name|defaultConfig
return|;
block|}
specifier|public
name|void
name|setDefaultConfig
parameter_list|(
annotation|@
name|Nonnull
name|T
name|defaultConfig
parameter_list|)
block|{
name|this
operator|.
name|defaultConfig
operator|=
name|defaultConfig
expr_stmt|;
name|ctx
operator|.
name|defaultCtx
operator|=
name|defaultConfig
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|T
name|configuration
parameter_list|)
block|{
name|addConfiguration
argument_list|(
name|configuration
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|T
name|configuration
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|params
parameter_list|)
block|{
name|int
name|ranking
init|=
name|configuration
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_RANKING
argument_list|,
name|NO_RANKING
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranking
operator|==
name|NO_RANKING
condition|)
block|{
name|ranking
operator|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|Constants
operator|.
name|SERVICE_RANKING
argument_list|,
name|NO_RANKING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ranking
operator|==
name|NO_RANKING
operator|||
name|configurations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|configurations
operator|.
name|add
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|c
range|:
name|configurations
control|)
block|{
name|int
name|r
init|=
name|rankings
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranking
operator|>
name|r
condition|)
block|{
break|break;
block|}
else|else
block|{
name|i
operator|++
expr_stmt|;
block|}
block|}
name|configurations
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
name|rankings
operator|.
name|set
argument_list|(
name|configuration
argument_list|,
name|ranking
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|add
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|T
name|configuration
parameter_list|)
block|{
name|configurations
operator|.
name|remove
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|rankings
operator|.
name|remove
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|refresh
argument_list|(
name|configurations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getConfigurations
parameter_list|()
block|{
if|if
condition|(
name|configurations
operator|.
name|isEmpty
argument_list|()
operator|&&
name|defaultConfig
operator|!=
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|defaultConfig
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|configurations
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|setSecurityProvider
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|protected
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"SecurityProvider missing => CompositeConfiguration is not ready."
argument_list|)
throw|;
block|}
return|return
name|securityProvider
return|;
block|}
comment|//----------------------------------------------< SecurityConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|()
block|{
name|List
argument_list|<
name|T
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
name|ConfigurationParameters
index|[]
name|params
init|=
operator|new
name|ConfigurationParameters
index|[
name|configs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|configs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|params
index|[
name|i
index|]
operator|=
name|configs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getParameters
argument_list|()
expr_stmt|;
block|}
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|params
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
block|{
return|return
operator|new
name|CompositeWorkspaceInitializer
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|T
argument_list|,
name|WorkspaceInitializer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|WorkspaceInitializer
name|apply
parameter_list|(
name|T
name|securityConfiguration
parameter_list|)
block|{
return|return
name|securityConfiguration
operator|.
name|getWorkspaceInitializer
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RepositoryInitializer
name|getRepositoryInitializer
parameter_list|()
block|{
return|return
operator|new
name|CompositeInitializer
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|T
argument_list|,
name|RepositoryInitializer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RepositoryInitializer
name|apply
parameter_list|(
name|T
name|securityConfiguration
parameter_list|)
block|{
return|return
name|securityConfiguration
operator|.
name|getRepositoryInitializer
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|getCommitHooks
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|T
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|CommitHook
argument_list|>
name|apply
parameter_list|(
name|T
name|securityConfiguration
parameter_list|)
block|{
return|return
name|securityConfiguration
operator|.
name|getCommitHooks
argument_list|(
name|workspaceName
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|getValidators
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|T
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|apply
parameter_list|(
name|T
name|securityConfiguration
parameter_list|)
block|{
return|return
name|securityConfiguration
operator|.
name|getValidators
argument_list|(
name|workspaceName
argument_list|,
name|principals
argument_list|,
name|moveTracker
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ThreeWayConflictHandler
argument_list|>
name|getConflictHandlers
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
name|securityConfiguration
lambda|->
name|securityConfiguration
operator|.
name|getConflictHandlers
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|T
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|ProtectedItemImporter
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ProtectedItemImporter
argument_list|>
name|apply
parameter_list|(
name|T
name|securityConfiguration
parameter_list|)
block|{
return|return
name|securityConfiguration
operator|.
name|getProtectedItemImporters
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Ranking
block|{
specifier|private
name|Map
argument_list|<
name|SecurityConfiguration
argument_list|,
name|Integer
argument_list|>
name|m
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|int
name|get
parameter_list|(
annotation|@
name|Nonnull
name|SecurityConfiguration
name|configuration
parameter_list|)
block|{
name|Integer
name|ranking
init|=
name|m
operator|.
name|get
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranking
operator|==
literal|null
condition|)
block|{
return|return
name|NO_RANKING
return|;
block|}
else|else
block|{
return|return
name|ranking
operator|.
name|intValue
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|set
parameter_list|(
annotation|@
name|Nonnull
name|SecurityConfiguration
name|configuration
parameter_list|,
name|int
name|ranking
parameter_list|)
block|{
if|if
condition|(
name|ranking
operator|!=
name|NO_RANKING
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
name|configuration
argument_list|,
name|ranking
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|remove
parameter_list|(
annotation|@
name|Nonnull
name|SecurityConfiguration
name|configuration
parameter_list|)
block|{
name|m
operator|.
name|remove
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|CompositeContext
implements|implements
name|Context
block|{
annotation|@
name|Nonnull
specifier|private
name|Context
name|defaultCtx
init|=
name|DEFAULT
decl_stmt|;
annotation|@
name|Nullable
specifier|private
name|Context
index|[]
name|delegatees
init|=
literal|null
decl_stmt|;
specifier|private
name|void
name|refresh
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|configurations
parameter_list|)
block|{
name|Set
argument_list|<
name|Context
argument_list|>
name|s
init|=
name|Sets
operator|.
name|newLinkedHashSetWithExpectedSize
argument_list|(
name|configurations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Context
name|c
range|:
name|Iterables
operator|.
name|transform
argument_list|(
name|configurations
argument_list|,
name|ContextFunction
operator|.
name|INSTANCE
argument_list|)
control|)
block|{
if|if
condition|(
name|DEFAULT
operator|!=
name|c
condition|)
block|{
name|s
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|delegatees
operator|=
operator|(
name|s
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|s
operator|.
name|toArray
argument_list|(
operator|new
name|Context
index|[
name|s
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|add
parameter_list|(
annotation|@
name|Nonnull
name|SecurityConfiguration
name|configuration
parameter_list|)
block|{
name|Context
name|c
init|=
name|configuration
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|DEFAULT
operator|!=
name|c
condition|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
name|delegatees
operator|=
operator|new
name|Context
index|[]
block|{
name|c
block|}
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|delegatees
operator|=
name|ObjectArrays
operator|.
name|concat
argument_list|(
name|delegatees
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
return|return
name|defaultCtx
operator|.
name|definesProperty
argument_list|(
name|parent
argument_list|,
name|property
argument_list|)
return|;
block|}
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|parent
argument_list|,
name|property
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
return|return
name|defaultCtx
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
return|;
block|}
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
return|return
name|defaultCtx
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
return|;
block|}
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
return|return
name|defaultCtx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
return|;
block|}
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|delegatees
operator|==
literal|null
condition|)
block|{
return|return
name|defaultCtx
operator|.
name|definesInternal
argument_list|(
name|tree
argument_list|)
return|;
block|}
for|for
control|(
name|Context
name|ctx
range|:
name|delegatees
control|)
block|{
if|if
condition|(
name|ctx
operator|.
name|definesInternal
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|ContextFunction
implements|implements
name|Function
argument_list|<
name|SecurityConfiguration
argument_list|,
name|Context
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|ContextFunction
name|INSTANCE
init|=
operator|new
name|ContextFunction
argument_list|()
decl_stmt|;
specifier|private
name|ContextFunction
parameter_list|()
block|{}
annotation|@
name|Override
specifier|public
name|Context
name|apply
parameter_list|(
name|SecurityConfiguration
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getContext
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

