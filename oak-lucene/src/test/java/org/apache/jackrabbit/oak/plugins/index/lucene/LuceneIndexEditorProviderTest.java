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
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

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
name|ImmutableMap
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
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|reflect
operator|.
name|FieldUtils
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
name|core
operator|.
name|SimpleCommitContext
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
name|ContextAwareCallback
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
name|IndexUpdateCallback
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
name|IndexingContext
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|IndexingMode
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
name|lucene
operator|.
name|hybrid
operator|.
name|DocumentQueue
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
name|CommitContext
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
name|mount
operator|.
name|Mounts
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
name|junit
operator|.
name|Test
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLucenePropertyIndexDefinition
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexEditorProviderTest
block|{
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|readOnlyBuilderUsedForSync
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|editorProvider
operator|.
name|setIndexingQueue
argument_list|(
name|mock
argument_list|(
name|DocumentQueue
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IndexUpdateCallback
name|callback
init|=
operator|new
name|TestCallback
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeBuilder
name|defnBuilder
init|=
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Editor
name|editor
init|=
name|editorProvider
operator|.
name|getIndexEditor
argument_list|(
name|TYPE_LUCENE
argument_list|,
name|defnBuilder
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|LuceneIndexEditor
name|luceneEditor
init|=
operator|(
name|LuceneIndexEditor
operator|)
name|editor
decl_stmt|;
name|NodeBuilder
name|builderFromContext
init|=
operator|(
name|NodeBuilder
operator|)
name|FieldUtils
operator|.
name|readField
argument_list|(
name|luceneEditor
operator|.
name|getContext
argument_list|()
argument_list|,
literal|"definitionBuilder"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|builderFromContext
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have been read only builder"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ignore
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|reuseOldIndexDefinition
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexTracker
name|tracker
init|=
name|mock
argument_list|(
name|IndexTracker
operator|.
name|class
argument_list|)
decl_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
literal|null
argument_list|,
name|tracker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|editorProvider
operator|.
name|setIndexingQueue
argument_list|(
name|mock
argument_list|(
name|DocumentQueue
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|//Set up a different IndexDefinition which needs to be returned
comment|//from tracker with a marker property
name|NodeBuilder
name|testBuilder
init|=
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|testBuilder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|testBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|tracker
operator|.
name|getIndexDefinition
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|defn
argument_list|)
expr_stmt|;
name|IndexUpdateCallback
name|callback
init|=
operator|new
name|TestCallback
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeBuilder
name|defnBuilder
init|=
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Editor
name|editor
init|=
name|editorProvider
operator|.
name|getIndexEditor
argument_list|(
name|TYPE_LUCENE
argument_list|,
name|defnBuilder
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|LuceneIndexEditor
name|luceneEditor
init|=
operator|(
name|LuceneIndexEditor
operator|)
name|editor
decl_stmt|;
name|LuceneIndexEditorContext
name|context
init|=
name|luceneEditor
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//Definition should reflect the marker property
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|context
operator|.
name|getDefinition
argument_list|()
operator|.
name|getDefinitionNodeState
argument_list|()
operator|.
name|getString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|editorNullInCaseOfReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|IndexUpdateCallback
name|callback
init|=
operator|new
name|TestCallback
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeBuilder
name|defnBuilder
init|=
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Editor
name|editor
init|=
name|editorProvider
operator|.
name|getIndexEditor
argument_list|(
name|TYPE_LUCENE
argument_list|,
name|defnBuilder
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|editor
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|createIndexDefinition
parameter_list|(
name|String
name|idxName
parameter_list|)
block|{
name|NodeBuilder
name|idx
init|=
name|newLucenePropertyIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
argument_list|,
name|idxName
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"async"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enableIndexingMode
argument_list|(
name|idx
argument_list|,
name|IndexingMode
operator|.
name|NRT
argument_list|)
expr_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|IndexDefinition
operator|.
name|updateDefinition
argument_list|(
name|idx
argument_list|)
expr_stmt|;
return|return
name|idx
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|()
block|{
name|CommitInfo
name|info
init|=
operator|new
name|CommitInfo
argument_list|(
literal|"admin"
argument_list|,
literal|"s1"
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|,
operator|new
name|SimpleCommitContext
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|private
specifier|static
class|class
name|TestCallback
implements|implements
name|IndexUpdateCallback
implements|,
name|IndexingContext
implements|,
name|ContextAwareCallback
block|{
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|commitInfo
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|reindexing
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|async
decl_stmt|;
specifier|private
name|TestCallback
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|CommitInfo
name|commitInfo
parameter_list|,
name|boolean
name|reindexing
parameter_list|,
name|boolean
name|async
parameter_list|)
block|{
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|commitInfo
operator|=
name|commitInfo
expr_stmt|;
name|this
operator|.
name|reindexing
operator|=
name|reindexing
expr_stmt|;
name|this
operator|.
name|async
operator|=
name|async
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexPath
parameter_list|()
block|{
return|return
name|indexPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|CommitInfo
name|getCommitInfo
parameter_list|()
block|{
return|return
name|commitInfo
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReindexing
parameter_list|()
block|{
return|return
name|reindexing
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAsync
parameter_list|()
block|{
return|return
name|async
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|indexUpdateFailed
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{          }
annotation|@
name|Override
specifier|public
name|IndexingContext
name|getIndexingContext
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

