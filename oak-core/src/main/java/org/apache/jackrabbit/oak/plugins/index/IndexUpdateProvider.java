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
name|plugins
operator|.
name|index
package|;
end_package

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
name|IndexUpdate
operator|.
name|MissingIndexProviderStrategy
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
name|CommitInfo
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
name|VisibleEditor
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|public
class|class
name|IndexUpdateProvider
implements|implements
name|EditorProvider
block|{
specifier|private
specifier|static
specifier|final
name|IndexUpdateCallback
name|NOOP_CALLBACK
init|=
operator|new
name|IndexUpdateCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|indexUpdate
parameter_list|()
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|IndexEditorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|String
name|async
decl_stmt|;
specifier|private
specifier|final
name|MissingIndexProviderStrategy
name|missingStrategy
decl_stmt|;
specifier|private
name|CorruptIndexHandler
name|corruptIndexHandler
init|=
name|CorruptIndexHandler
operator|.
name|NOOP
decl_stmt|;
specifier|private
name|boolean
name|ignoreReindexFlags
init|=
name|IndexUpdate
operator|.
name|IGNORE_REINDEX_FLAGS
decl_stmt|;
specifier|public
name|IndexUpdateProvider
parameter_list|(
name|IndexEditorProvider
name|provider
parameter_list|,
name|boolean
name|failOnMissingIndexProvider
parameter_list|)
block|{
name|this
argument_list|(
name|provider
argument_list|,
literal|null
argument_list|,
name|failOnMissingIndexProvider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexUpdateProvider
parameter_list|(
name|IndexEditorProvider
name|provider
parameter_list|)
block|{
name|this
argument_list|(
name|provider
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexUpdateProvider
parameter_list|(
annotation|@
name|NotNull
name|IndexEditorProvider
name|provider
parameter_list|,
annotation|@
name|Nullable
name|String
name|async
parameter_list|,
name|boolean
name|failOnMissingIndexProvider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|async
operator|=
name|async
expr_stmt|;
name|this
operator|.
name|missingStrategy
operator|=
operator|new
name|MissingIndexProviderStrategy
argument_list|()
expr_stmt|;
name|this
operator|.
name|missingStrategy
operator|.
name|setFailOnMissingIndexProvider
argument_list|(
name|failOnMissingIndexProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|IndexUpdate
name|editor
init|=
operator|new
name|IndexUpdate
argument_list|(
name|provider
argument_list|,
name|async
argument_list|,
name|after
argument_list|,
name|builder
argument_list|,
name|NOOP_CALLBACK
argument_list|,
name|NodeTraversalCallback
operator|.
name|NOOP
argument_list|,
name|info
argument_list|,
name|corruptIndexHandler
argument_list|)
operator|.
name|withMissingProviderStrategy
argument_list|(
name|missingStrategy
argument_list|)
decl_stmt|;
name|editor
operator|.
name|setIgnoreReindexFlags
argument_list|(
name|ignoreReindexFlags
argument_list|)
expr_stmt|;
return|return
name|VisibleEditor
operator|.
name|wrap
argument_list|(
name|editor
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCorruptIndexHandler
parameter_list|(
name|CorruptIndexHandler
name|corruptIndexHandler
parameter_list|)
block|{
name|this
operator|.
name|corruptIndexHandler
operator|=
name|corruptIndexHandler
expr_stmt|;
block|}
specifier|public
name|void
name|setIgnoreReindexFlags
parameter_list|(
name|boolean
name|ignoreReindexFlags
parameter_list|)
block|{
name|this
operator|.
name|ignoreReindexFlags
operator|=
name|ignoreReindexFlags
expr_stmt|;
block|}
block|}
end_class

end_unit

