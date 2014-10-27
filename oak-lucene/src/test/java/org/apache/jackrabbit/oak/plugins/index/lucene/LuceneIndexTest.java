begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|File
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
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
name|Iterators
operator|.
name|transform
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_STRING
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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
name|JcrConstants
operator|.
name|JCR_SYSTEM
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
name|JcrConstants
operator|.
name|NT_BASE
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|PERSISTENCE_FILE
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
name|PERSISTENCE_NAME
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
name|PERSISTENCE_PATH
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
name|newLuceneIndexDefinition
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
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
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
name|QueryIndex
operator|.
name|AdvancedQueryIndex
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
name|spi
operator|.
name|query
operator|.
name|QueryIndex
operator|.
name|IndexPlan
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
name|Type
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
name|util
operator|.
name|IndexCopier
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
name|segment
operator|.
name|SegmentNodeStore
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
name|query
operator|.
name|ast
operator|.
name|Operator
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
name|ast
operator|.
name|SelectorImpl
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
name|index
operator|.
name|FilterImpl
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
name|commit
operator|.
name|EmptyHook
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
name|Observable
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
name|query
operator|.
name|Cursor
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
name|Filter
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
name|IndexRow
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
name|PropertyValues
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|ImmutableSet
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexTest
block|{
specifier|private
specifier|static
specifier|final
name|Analyzer
name|analyzer
init|=
name|LuceneIndexConstants
operator|.
name|ANALYZER
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|EditorHook
name|HOOK
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|analyzer
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
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
name|testLucene
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
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
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|AdvancedQueryIndex
name|queryIndex
init|=
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|createPlan
argument_list|(
name|filter
argument_list|)
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLuceneLazyCursor
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
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
name|LuceneIndex
operator|.
name|LUCENE_QUERY_BATCH_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"child"
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
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|AdvancedQueryIndex
name|queryIndex
init|=
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|createPlan
argument_list|(
name|filter
argument_list|)
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|copyOf
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|IndexRow
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|IndexRow
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|paths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LuceneIndex
operator|.
name|LUCENE_QUERY_BATCH_SIZE
operator|+
literal|1
argument_list|,
name|paths
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
name|testLucene2
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
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
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
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
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|AdvancedQueryIndex
name|queryIndex
init|=
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
comment|// filter.restrictPath("/", Filter.PathRestriction.EXACT);
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|createPlan
argument_list|(
name|filter
argument_list|)
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b/c"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLucene3
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
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
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
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
argument_list|,
name|Type
operator|.
name|NAME
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
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|AdvancedQueryIndex
name|queryIndex
init|=
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
comment|// filter.restrictPath("/", Filter.PathRestriction.EXACT);
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|createPlan
argument_list|(
name|filter
argument_list|)
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeState
name|system
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|NodeState
name|types
init|=
name|system
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|NodeState
name|type
init|=
name|types
operator|.
name|getChildNode
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTokens
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"parent"
argument_list|,
literal|"child"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"/parent/child"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"p1234"
argument_list|,
literal|"p5678"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"/p1234/p5678"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"first"
argument_list|,
literal|"second"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"first_second"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"first1"
argument_list|,
literal|"second2"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"first1_second2"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"first"
argument_list|,
literal|"second"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"first. second"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"first"
argument_list|,
literal|"second"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"first.second"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"hello-world"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"hello"
argument_list|,
literal|"wor*"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"hello-wor*"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"*llo"
argument_list|,
literal|"world"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"*llo-world"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"*llo"
argument_list|,
literal|"wor*"
argument_list|)
argument_list|,
name|LuceneIndex
operator|.
name|tokenize
argument_list|(
literal|"*llo-wor*"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|luceneWithFSDirectory
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Issue is not reproducible with MemoryNodeBuilder and
comment|//MemoryNodeState as they cannot determine change in childNode without
comment|//entering
name|NodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|()
decl_stmt|;
specifier|final
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Observable
operator|)
name|nodeStore
operator|)
operator|.
name|addObserver
argument_list|(
operator|new
name|Observer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
name|tracker
operator|.
name|update
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|idxb
init|=
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
decl_stmt|;
name|idxb
operator|.
name|setProperty
argument_list|(
name|PERSISTENCE_NAME
argument_list|,
name|PERSISTENCE_FILE
argument_list|)
expr_stmt|;
name|idxb
operator|.
name|setProperty
argument_list|(
name|PERSISTENCE_PATH
argument_list|,
name|getIndexDir
argument_list|()
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|indexed
init|=
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|HOOK
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|tracker
argument_list|,
name|indexed
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|indexed
operator|=
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|HOOK
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|tracker
argument_list|,
name|indexed
argument_list|,
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|luceneWithCopyOnReadDir
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
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
name|NodeState
name|indexed
init|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|File
name|indexRootDir
init|=
operator|new
name|File
argument_list|(
name|getIndexDir
argument_list|()
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|(
operator|new
name|IndexCopier
argument_list|(
name|sameThreadExecutor
argument_list|()
argument_list|,
name|indexRootDir
argument_list|)
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|tracker
argument_list|,
name|indexed
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|indexed
operator|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|tracker
argument_list|,
name|indexed
argument_list|,
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertQuery
parameter_list|(
name|IndexTracker
name|tracker
parameter_list|,
name|NodeState
name|indexed
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|AdvancedQueryIndex
name|queryIndex
init|=
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|(
name|NT_BASE
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
name|key
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|createPlan
argument_list|(
name|filter
argument_list|)
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getIndexDir
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"indexdir"
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|dir
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|private
name|IndexPlan
name|createPlan
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
operator|.
name|setAttribute
argument_list|(
name|LuceneIndex
operator|.
name|ATTR_INDEX_PATH
argument_list|,
literal|"/oak:index/lucene"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

