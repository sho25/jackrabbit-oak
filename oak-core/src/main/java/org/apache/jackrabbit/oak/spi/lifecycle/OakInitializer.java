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
name|api
operator|.
name|CommitFailedException
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
name|IndexUpdateProvider
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
name|NodeStore
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|OakInitializer
block|{
specifier|private
name|OakInitializer
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|RepositoryInitializer
name|initializer
parameter_list|,
annotation|@
name|Nonnull
name|IndexEditorProvider
name|indexEditor
parameter_list|)
block|{
try|try
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|initializer
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|CommitHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|indexEditor
argument_list|)
argument_list|)
decl_stmt|;
name|CommitInfo
name|info
init|=
operator|new
name|CommitInfo
argument_list|(
literal|"OakInitializer"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|WorkspaceInitializer
argument_list|>
name|initializer
parameter_list|,
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|IndexEditorProvider
name|indexEditor
parameter_list|,
annotation|@
name|Nonnull
name|QueryIndexProvider
name|indexProvider
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|WorkspaceInitializer
name|wspInit
range|:
name|initializer
control|)
block|{
name|wspInit
operator|.
name|initialize
argument_list|(
name|builder
argument_list|,
name|workspaceName
argument_list|,
name|indexProvider
argument_list|,
name|commitHook
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|CommitHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|indexEditor
argument_list|)
argument_list|)
decl_stmt|;
name|CommitInfo
name|info
init|=
operator|new
name|CommitInfo
argument_list|(
literal|"OakInitializer"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

