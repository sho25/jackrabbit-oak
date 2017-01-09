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
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|IndexTracker
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
name|LuceneIndexEditorProvider
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
name|TestUtil
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
name|util
operator|.
name|IndexDefinitionBuilder
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|LocalIndexWriterFactoryTest
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
specifier|private
name|EditorHook
name|syncHook
decl_stmt|;
specifier|private
name|EditorHook
name|asyncHook
decl_stmt|;
specifier|private
name|CommitInfo
name|info
decl_stmt|;
specifier|private
name|LuceneIndexEditorProvider
name|editorProvider
decl_stmt|;
specifier|private
name|IndexTracker
name|tracker
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|tracker
operator|=
operator|new
name|IndexTracker
argument_list|()
expr_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
literal|100
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|editorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
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
expr_stmt|;
name|editorProvider
operator|.
name|setIndexingQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|syncHook
operator|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|editorProvider
argument_list|)
argument_list|)
expr_stmt|;
name|asyncHook
operator|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|editorProvider
argument_list|,
literal|"async"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Test
specifier|public
name|void
name|ignoreReindexCase
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|,
name|IndexingMode
operator|.
name|NRT
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|syncHook
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
comment|//This is reindex case so nothing would be indexed
comment|//So now holder should be present in context
name|assertNotNull
argument_list|(
name|getHolder
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getCommitAttribute
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|localIndexWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|syncHook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|holder
init|=
name|getHolder
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|holder
argument_list|)
expr_stmt|;
comment|//2 add none for delete
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getIndexedDocList
argument_list|(
name|holder
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mutlipleIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|indexed
init|=
name|createAndPopulateTwoAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|syncHook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|holder
init|=
name|getHolder
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|holder
argument_list|)
expr_stmt|;
comment|//1 add  - bar
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getIndexedDocList
argument_list|(
name|holder
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//1 add  - bar
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getIndexedDocList
argument_list|(
name|holder
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|syncIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|SYNC
argument_list|)
decl_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|syncHook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|holder
init|=
name|getHolder
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|holder
argument_list|)
expr_stmt|;
comment|//2 add none for delete
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getIndexedDocList
argument_list|(
name|holder
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|inMemoryDocLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|indexed
init|=
name|createAndPopulateAsyncIndex
argument_list|(
name|IndexingMode
operator|.
name|NRT
argument_list|)
decl_stmt|;
name|editorProvider
operator|.
name|setInMemoryDocsLimit
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|editorProvider
operator|.
name|setIndexingQueue
argument_list|(
operator|new
name|DocumentQueue
argument_list|(
literal|1
argument_list|,
name|tracker
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"b"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|syncHook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|holder
init|=
name|getHolder
argument_list|()
decl_stmt|;
comment|//5 for in memory list and 1 in queue
name|assertEquals
argument_list|(
literal|5
operator|+
literal|1
argument_list|,
name|getIndexedDocList
argument_list|(
name|holder
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|createAndPopulateAsyncIndex
parameter_list|(
name|IndexingMode
name|indexingMode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
comment|//Have some stuff to be indexed
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
return|return
name|asyncHook
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|NodeState
name|createAndPopulateTwoAsyncIndex
parameter_list|(
name|IndexingMode
name|indexingMode
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|createIndexDefinition
argument_list|(
literal|"fooIndex"
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
literal|"barIndex"
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
comment|//Have some stuff to be indexed
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
return|return
name|asyncHook
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|after
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|LuceneDocumentHolder
name|getHolder
parameter_list|()
block|{
return|return
operator|(
name|LuceneDocumentHolder
operator|)
name|getCommitAttribute
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
return|;
block|}
specifier|private
name|Object
name|getCommitAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|CommitContext
name|cc
init|=
operator|(
name|CommitContext
operator|)
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
return|return
name|cc
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|()
block|{
name|info
operator|=
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
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|private
name|void
name|createIndexDefinition
parameter_list|(
name|String
name|idxName
parameter_list|,
name|IndexingMode
name|indexingMode
parameter_list|)
block|{
name|IndexDefinitionBuilder
name|idx
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|enableIndexingMode
argument_list|(
name|idx
operator|.
name|getBuilderTree
argument_list|()
argument_list|,
name|indexingMode
argument_list|)
expr_stmt|;
name|idx
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|idxName
argument_list|,
name|idx
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getIndexedDocList
parameter_list|(
name|LuceneDocumentHolder
name|holder
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|LuceneDocInfo
name|doc
range|:
name|holder
operator|.
name|getAllLuceneDocInfo
argument_list|()
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|getIndexPath
argument_list|()
operator|.
name|equals
argument_list|(
name|indexPath
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getDocPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|paths
return|;
block|}
block|}
end_class

end_unit

