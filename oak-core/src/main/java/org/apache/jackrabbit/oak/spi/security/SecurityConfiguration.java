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
name|Collections
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
name|Set
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

begin_comment
comment|/**  * Base interface for all security related configurations.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|SecurityConfiguration
block|{
comment|/**      * Returns the name of this security configuration.      *      * @return The name of this configuration.      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns the configuration parameters associated with this security      * configuration instance. If no parameters are present      * {@link ConfigurationParameters#EMPTY} should be returned.      *      * @return The configuration parameters.      */
annotation|@
name|Nonnull
name|ConfigurationParameters
name|getParameters
parameter_list|()
function_decl|;
comment|/**      * Returns a workspace initializer for this security configuration. If this      * configuration doesn't require any specific workspace initialization      * {@link WorkspaceInitializer#DEFAULT} should be returned.      *      * @return An instance of {@code WorkspaceInitializer}.      */
annotation|@
name|Nonnull
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
function_decl|;
comment|/**      * Returns a repository initializer for this security configuration. If this      * configuration doesn't require any specific repository initialization      * {@link RepositoryInitializer#DEFAULT} should be returned.      *      * @return An instance of {@code RepositoryInitializer}.      */
annotation|@
name|Nonnull
name|RepositoryInitializer
name|getRepositoryInitializer
parameter_list|()
function_decl|;
comment|/**      * Returns the list of commit hooks that need to be executed for the      * specified workspace name.      *      * @param workspaceName The name of the workspace.      * @return A list of commit hooks.      */
annotation|@
name|Nonnull
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
name|String
name|workspaceName
parameter_list|)
function_decl|;
comment|/**      * Returns the list of validators that need to be executed for the specified      * workspace name.      *      * @param workspaceName The name of the workspace.      * @param principals The set of principals associated with the subject      * that is committing modifications.      * @param moveTracker The move tracker associated with the commit.      * @return A list of validators.      */
annotation|@
name|Nonnull
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
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
function_decl|;
comment|/**      * Returns the list of conflict handlers available for this security configuration.      *      * @return A list of {@link org.apache.jackrabbit.oak.spi.commit.ThreeWayConflictHandler}.      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|ThreeWayConflictHandler
argument_list|>
name|getConflictHandlers
parameter_list|()
function_decl|;
comment|/**      * @return The list of protected item importers defined by this configuration.      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
function_decl|;
comment|/**      * @return The context defined by this configuration.      */
annotation|@
name|Nonnull
name|Context
name|getContext
parameter_list|()
function_decl|;
comment|/**      * Default implementation that provides empty initializers, validators,      * commit hooks and parameters.      */
class|class
name|Default
implements|implements
name|SecurityConfiguration
block|{
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
literal|"org.apache.jackrabbit.oak"
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
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
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
name|WorkspaceInitializer
operator|.
name|DEFAULT
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
name|RepositoryInitializer
operator|.
name|DEFAULT
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
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|Context
operator|.
name|DEFAULT
return|;
block|}
block|}
block|}
end_interface

end_unit

