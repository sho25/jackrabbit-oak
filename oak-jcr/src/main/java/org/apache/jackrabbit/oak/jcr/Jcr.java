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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|plugins
operator|.
name|commit
operator|.
name|AnnotatingConflictHandler
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
name|ConflictValidatorProvider
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
name|plugins
operator|.
name|index
operator|.
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|property
operator|.
name|PropertyIndexHookProvider
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
name|property
operator|.
name|PropertyIndexProvider
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
name|name
operator|.
name|NameValidatorProvider
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
name|name
operator|.
name|NamespaceValidatorProvider
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
name|plugins
operator|.
name|nodetype
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
name|nodetype
operator|.
name|RegistrationValidatorProvider
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
name|TypeValidatorProvider
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
name|version
operator|.
name|VersionHook
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
name|ConflictHandler
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
name|Validator
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
name|security
operator|.
name|SecurityProvider
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
name|checkNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|Jcr
block|{
specifier|private
specifier|final
name|Oak
name|oak
decl_stmt|;
specifier|private
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|Jcr
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|this
operator|.
name|oak
operator|=
name|oak
expr_stmt|;
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|DefaultTypeEditor
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|VersionHook
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|SecurityProviderImpl
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|NameValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|NamespaceValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|TypeValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|RegistrationValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|ConflictValidatorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|PropertyIndexHookProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|AnnotatingConflictHandler
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|PropertyIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|NodeTypeIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Jcr
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Oak
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Jcr
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Oak
argument_list|(
name|kernel
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|RepositoryInitializer
name|initializer
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|initializer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|QueryIndexProvider
name|provider
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|provider
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|IndexHookProvider
name|indexHookProvider
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|indexHookProvider
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|hook
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ValidatorProvider
name|provider
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|provider
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|Validator
name|validator
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|validator
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|securityProvider
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|conflictHandler
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Repository
name|createRepository
parameter_list|()
block|{
return|return
operator|new
name|RepositoryImpl
argument_list|(
name|oak
operator|.
name|createContentRepository
argument_list|()
argument_list|,
name|executor
argument_list|,
name|securityProvider
argument_list|)
return|;
block|}
block|}
end_class

end_unit

