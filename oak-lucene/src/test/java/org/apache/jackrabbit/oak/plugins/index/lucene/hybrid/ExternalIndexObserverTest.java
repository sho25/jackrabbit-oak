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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashMultimap
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
name|Multimap
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
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
name|LuceneIndexDefinition
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
name|Rule
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|junit
operator|.
name|MockitoJUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|junit
operator|.
name|MockitoRule
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
name|InitialContentHelper
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
name|stats
operator|.
name|StatisticsProvider
operator|.
name|NOOP
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
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|verify
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
name|verifyZeroInteractions
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
name|ExternalIndexObserverTest
block|{
annotation|@
name|Rule
specifier|public
name|MockitoRule
name|rule
init|=
name|MockitoJUnit
operator|.
name|rule
argument_list|()
decl_stmt|;
annotation|@
name|Mock
specifier|private
name|IndexingQueue
name|queue
decl_stmt|;
annotation|@
name|Mock
specifier|private
name|IndexTracker
name|tracker
decl_stmt|;
specifier|private
name|ExternalIndexObserver
name|observer
decl_stmt|;
specifier|private
name|CommitContext
name|commitContext
init|=
operator|new
name|SimpleCommitContext
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|observer
operator|=
operator|new
name|ExternalIndexObserver
argument_list|(
name|queue
argument_list|,
name|tracker
argument_list|,
name|NOOP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|internalChange
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|externalChangeNullContext
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|CommitInfo
operator|.
name|EMPTY_EXTERNAL
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyCommitContext
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonExistingIndexDefn
parameter_list|()
throws|throws
name|Exception
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexedPaths
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|indexedPaths
operator|.
name|put
argument_list|(
literal|"/a"
argument_list|,
literal|"/oak:index/foo"
argument_list|)
expr_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|,
operator|new
name|IndexedPaths
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonExistingPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexedPaths
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|indexedPaths
operator|.
name|put
argument_list|(
literal|"/a"
argument_list|,
literal|"/oak:index/foo"
argument_list|)
expr_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|,
operator|new
name|IndexedPaths
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|tracker
operator|.
name|getIndexDefinition
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|createNRTIndex
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
expr_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonApplicableRule
parameter_list|()
throws|throws
name|Exception
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexedPaths
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|indexedPaths
operator|.
name|put
argument_list|(
literal|"/a"
argument_list|,
literal|"/oak:index/foo"
argument_list|)
expr_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|,
operator|new
name|IndexedPaths
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
comment|//Rule is on nt:file but node if of type nt:base
name|when
argument_list|(
name|tracker
operator|.
name|getIndexDefinition
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|createNRTIndex
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|nb
init|=
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|nb
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ruleNotResultingInDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexedPaths
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|indexedPaths
operator|.
name|put
argument_list|(
literal|"/a"
argument_list|,
literal|"/oak:index/foo"
argument_list|)
expr_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|,
operator|new
name|IndexedPaths
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
comment|//Rule is of type nt:base but does not have any matching property definition
name|when
argument_list|(
name|tracker
operator|.
name|getIndexDefinition
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|createNRTIndex
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|nb
init|=
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|observer
operator|.
name|contentChanged
argument_list|(
name|nb
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|docAddedToQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|assertIndexing
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertIndexing
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexedPaths
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|indexedPaths
operator|.
name|put
argument_list|(
literal|"/a"
argument_list|,
literal|"/oak:index/foo"
argument_list|)
expr_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|,
operator|new
name|IndexedPaths
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|newCommitInfo
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|queue
operator|.
name|add
argument_list|(
name|any
argument_list|(
name|LuceneDoc
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|tracker
operator|.
name|getIndexDefinition
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|createNRTIndex
argument_list|(
literal|"nt:base"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|nb
init|=
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
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
name|observer
operator|.
name|contentChanged
argument_list|(
name|nb
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|ci
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|LuceneDoc
argument_list|>
name|doc
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|LuceneDoc
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|queue
argument_list|)
operator|.
name|add
argument_list|(
name|doc
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|doc
operator|.
name|getValue
argument_list|()
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|builder
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalObserverBuilder
name|builder
init|=
operator|new
name|ExternalObserverBuilder
argument_list|(
name|queue
argument_list|,
name|tracker
argument_list|,
name|NOOP
argument_list|,
name|MoreExecutors
operator|.
name|sameThreadExecutor
argument_list|()
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Observer
name|o
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|o
operator|.
name|contentChanged
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|CommitInfo
operator|.
name|EMPTY_EXTERNAL
argument_list|)
expr_stmt|;
name|verifyZeroInteractions
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|builder_NonFiltered
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalObserverBuilder
name|builder
init|=
operator|new
name|ExternalObserverBuilder
argument_list|(
name|queue
argument_list|,
name|tracker
argument_list|,
name|NOOP
argument_list|,
name|MoreExecutors
operator|.
name|sameThreadExecutor
argument_list|()
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertIndexing
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|()
block|{
return|return
operator|new
name|CommitInfo
argument_list|(
name|CommitInfo
operator|.
name|OAK_UNKNOWN
argument_list|,
name|CommitInfo
operator|.
name|OAK_UNKNOWN
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
name|commitContext
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|LuceneIndexDefinition
name|createNRTIndex
parameter_list|(
name|String
name|ruleName
parameter_list|)
block|{
name|IndexDefinitionBuilder
name|idx
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|idx
operator|.
name|indexRule
argument_list|(
name|ruleName
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
name|idx
operator|.
name|async
argument_list|(
literal|"async"
argument_list|,
literal|"sync"
argument_list|)
expr_stmt|;
return|return
operator|new
name|LuceneIndexDefinition
argument_list|(
name|INITIAL_CONTENT
argument_list|,
name|idx
operator|.
name|build
argument_list|()
argument_list|,
literal|"/oak:index/foo"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

