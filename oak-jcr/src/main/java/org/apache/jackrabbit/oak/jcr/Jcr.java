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
name|Sets
operator|.
name|newLinkedHashSet
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
name|plugins
operator|.
name|commit
operator|.
name|JcrConflictHandler
operator|.
name|createJcrConflictHandler
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
name|Executor
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|IndexEditorProvider
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
name|counter
operator|.
name|NodeCounterEditorProvider
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
name|OrderedPropertyIndexEditorProvider
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
name|PropertyIndexEditorProvider
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
name|index
operator|.
name|reference
operator|.
name|ReferenceEditorProvider
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
name|reference
operator|.
name|ReferenceIndexProvider
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
name|itemsave
operator|.
name|ItemSaveValidatorProvider
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
name|NamespaceEditorProvider
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
name|TypeEditorProvider
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
name|write
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
name|observation
operator|.
name|CommitRateLimiter
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
name|VersionEditorProvider
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
name|query
operator|.
name|QueryEngineSettings
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
name|CompositeConflictHandler
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
name|Editor
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
name|EditorHook
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
name|EditorProvider
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
name|PartialConflictHandler
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
name|Clusterable
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
name|Whiteboard
import|;
end_import

begin_comment
comment|/**  * Builder class which encapsulates the details of building a JCR  *<tt>Repository</tt> backed by an Oak<tt>ContentRepository</tt> instance  *  *<p>The backing<tt>ContentRepository</tt> instance will be constructed with  * reasonable defaults and additional components can be registered by calling  * the<tt>with</tt> methods. Note that it is not possible to remove components  * once registered.</p>  *  *<p>The Jcr builder is a lazy initializer, to have a working repository make sure  * you call {@link Jcr#createContentRepository()} or  * {@link Jcr#createRepository()}.</p>  */
end_comment

begin_class
specifier|public
class|class
name|Jcr
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|Oak
name|oak
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|RepositoryInitializer
argument_list|>
name|repositoryInitializers
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|QueryIndexProvider
argument_list|>
name|queryIndexProviders
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|CommitHook
argument_list|>
name|commitHooks
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexEditorProvider
argument_list|>
name|indexEditorProviders
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|EditorProvider
argument_list|>
name|editorProviders
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Editor
argument_list|>
name|editors
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Observer
argument_list|>
name|observers
init|=
name|newLinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CompositeConflictHandler
name|conflictHandler
init|=
name|createJcrConflictHandler
argument_list|()
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|CommitRateLimiter
name|commitRateLimiter
decl_stmt|;
specifier|private
name|ScheduledExecutorService
name|scheduledExecutor
decl_stmt|;
specifier|private
name|Executor
name|executor
decl_stmt|;
specifier|private
name|QueryEngineSettings
name|queryEngineSettings
decl_stmt|;
specifier|private
name|String
name|defaultWorkspaceName
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|int
name|observationQueueLength
init|=
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
decl_stmt|;
specifier|private
name|boolean
name|fastQueryResultSize
decl_stmt|;
specifier|private
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Clusterable
name|clusterable
decl_stmt|;
specifier|public
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
name|EditorHook
argument_list|(
operator|new
name|VersionEditorProvider
argument_list|()
argument_list|)
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
name|ItemSaveValidatorProvider
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
name|NamespaceEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|TypeEditorProvider
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
name|ReferenceEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|ReferenceIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|NodeCounterEditorProvider
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
name|with
argument_list|(
operator|new
name|OrderedPropertyIndexEditorProvider
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
name|NodeStore
name|store
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Oak
argument_list|(
name|store
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
name|Clusterable
name|c
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|clusterable
operator|=
name|checkNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|RepositoryInitializer
name|initializer
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|repositoryInitializers
operator|.
name|add
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
specifier|public
name|Jcr
name|withAtomicCounter
parameter_list|()
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|oak
operator|.
name|withAtomicCounter
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|ensureRepositoryIsNotCreated
parameter_list|()
block|{
name|checkState
argument_list|(
name|repository
operator|==
literal|null
operator|&&
name|contentRepository
operator|==
literal|null
argument_list|,
literal|"Repository was already created"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|QueryIndexProvider
name|provider
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|queryIndexProviders
operator|.
name|add
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|IndexEditorProvider
name|indexEditorProvider
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|indexEditorProviders
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|indexEditorProvider
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|CommitHook
name|hook
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|commitHooks
operator|.
name|add
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|EditorProvider
name|provider
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|editorProviders
operator|.
name|add
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|Editor
name|editor
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|editors
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|editor
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|checkNotNull
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|PartialConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|conflictHandler
operator|.
name|addHandler
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
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ScheduledExecutorService
name|executor
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|scheduledExecutor
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
annotation|@
name|Nonnull
specifier|public
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
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
annotation|@
name|Nonnull
specifier|public
specifier|final
name|Jcr
name|with
parameter_list|(
annotation|@
name|Nonnull
name|Observer
name|observer
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|observers
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|observer
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
name|withAsyncIndexing
parameter_list|()
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|oak
operator|.
name|withAsyncIndexing
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Jcr
name|withObservationQueueLength
parameter_list|(
name|int
name|observationQueueLength
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|observationQueueLength
operator|=
name|observationQueueLength
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
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|checkNotNull
argument_list|(
name|commitRateLimiter
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
name|QueryEngineSettings
name|qs
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryEngineSettings
operator|=
name|checkNotNull
argument_list|(
name|qs
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
name|withFastQueryResultSize
parameter_list|(
name|boolean
name|fastQueryResultSize
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|fastQueryResultSize
operator|=
name|fastQueryResultSize
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
name|String
name|defaultWorkspaceName
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultWorkspaceName
operator|=
name|checkNotNull
argument_list|(
name|defaultWorkspaceName
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
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|ensureRepositoryIsNotCreated
argument_list|()
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|checkNotNull
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|setUpOak
parameter_list|()
block|{
comment|// whiteboard
if|if
condition|(
name|whiteboard
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
block|}
comment|// repository initializers
for|for
control|(
name|RepositoryInitializer
name|repositoryInitializer
range|:
name|repositoryInitializers
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|repositoryInitializer
argument_list|)
expr_stmt|;
block|}
comment|// query index providers
for|for
control|(
name|QueryIndexProvider
name|queryIndexProvider
range|:
name|queryIndexProviders
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|queryIndexProvider
argument_list|)
expr_stmt|;
block|}
comment|// commit hooks
for|for
control|(
name|CommitHook
name|commitHook
range|:
name|commitHooks
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|commitHook
argument_list|)
expr_stmt|;
block|}
comment|// conflict handlers
name|oak
operator|.
name|with
argument_list|(
name|conflictHandler
argument_list|)
expr_stmt|;
comment|// index editor providers
for|for
control|(
name|IndexEditorProvider
name|indexEditorProvider
range|:
name|indexEditorProviders
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|indexEditorProvider
argument_list|)
expr_stmt|;
block|}
comment|// editors
for|for
control|(
name|Editor
name|editor
range|:
name|editors
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|editor
argument_list|)
expr_stmt|;
block|}
comment|// editor providers
for|for
control|(
name|EditorProvider
name|editorProvider
range|:
name|editorProviders
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|editorProvider
argument_list|)
expr_stmt|;
block|}
comment|// securityProvider
name|oak
operator|.
name|with
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
comment|// executors
if|if
condition|(
name|scheduledExecutor
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|scheduledExecutor
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
comment|// observers
for|for
control|(
name|Observer
name|observer
range|:
name|observers
control|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
comment|// commit rate limiter
if|if
condition|(
name|commitRateLimiter
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|commitRateLimiter
argument_list|)
expr_stmt|;
block|}
comment|// query engine settings
if|if
condition|(
name|queryEngineSettings
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|queryEngineSettings
argument_list|)
expr_stmt|;
block|}
comment|// default workspace name
if|if
condition|(
name|defaultWorkspaceName
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|defaultWorkspaceName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clusterable
operator|!=
literal|null
condition|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|clusterable
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|ContentRepository
name|createContentRepository
parameter_list|()
block|{
if|if
condition|(
name|contentRepository
operator|==
literal|null
condition|)
block|{
name|setUpOak
argument_list|()
expr_stmt|;
name|contentRepository
operator|=
name|oak
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
block|}
return|return
name|contentRepository
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Repository
name|createRepository
parameter_list|()
block|{
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|repository
operator|=
operator|new
name|RepositoryImpl
argument_list|(
name|createContentRepository
argument_list|()
argument_list|,
name|oak
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|securityProvider
argument_list|,
name|observationQueueLength
argument_list|,
name|commitRateLimiter
argument_list|,
name|fastQueryResultSize
argument_list|)
expr_stmt|;
block|}
return|return
name|repository
return|;
block|}
block|}
end_class

end_unit

