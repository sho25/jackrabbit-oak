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
name|commit
operator|.
name|JcrConflictHandler
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
name|OrderedPropertyIndexProvider
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
name|NodeStore
import|;
end_import

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
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|int
name|observationQueueLength
init|=
name|DEFAULT_OBSERVATION_QUEUE_LENGTH
decl_stmt|;
specifier|private
name|CommitRateLimiter
name|commitRateLimiter
init|=
literal|null
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
name|JcrConflictHandler
operator|.
name|JCR_CONFLICT_HANDLER
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
name|PropertyIndexProvider
argument_list|()
argument_list|)
expr_stmt|;
name|with
argument_list|(
operator|new
name|OrderedPropertyIndexProvider
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
name|oak
operator|.
name|with
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
name|oak
operator|.
name|with
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
specifier|final
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
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|executor
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
name|Executor
name|executor
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|checkNotNull
argument_list|(
name|executor
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
name|Observer
name|observer
parameter_list|)
block|{
name|oak
operator|.
name|with
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
name|CommitRateLimiter
name|commitRateLimiter
parameter_list|)
block|{
name|oak
operator|.
name|with
argument_list|(
name|commitRateLimiter
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitRateLimiter
operator|=
name|commitRateLimiter
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
argument_list|)
return|;
block|}
block|}
end_class

end_unit

