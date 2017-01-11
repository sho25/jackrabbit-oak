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
name|document
package|;
end_package

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
name|Map
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Joiner
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
name|Splitter
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
name|Iterables
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|document
operator|.
name|spi
operator|.
name|JournalProperty
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
name|document
operator|.
name|spi
operator|.
name|JournalPropertyBuilder
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
name|document
operator|.
name|spi
operator|.
name|JournalPropertyService
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
name|ChangeCollectorProvider
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
name|ChangeSet
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
name|whiteboard
operator|.
name|DefaultWhiteboard
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|collection
operator|.
name|IsIterableContainingInAnyOrder
operator|.
name|containsInAnyOrder
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
name|assertFalse
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
name|assertNotNull
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
name|assertThat
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalChangesTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns2
decl_stmt|;
specifier|private
name|CommitInfoCollector
name|c1
init|=
operator|new
name|CommitInfoCollector
argument_list|()
decl_stmt|;
specifier|private
name|CommitInfoCollector
name|c2
init|=
operator|new
name|CommitInfoCollector
argument_list|()
decl_stmt|;
specifier|private
name|JournalPropertyHandlerFactory
name|tracker
init|=
operator|new
name|JournalPropertyHandlerFactory
argument_list|()
decl_stmt|;
specifier|private
name|Whiteboard
name|wb
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|tracker
operator|.
name|start
argument_list|(
name|wb
argument_list|)
expr_stmt|;
name|MemoryDocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|ns1
operator|=
name|newDocumentNodeStore
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ns2
operator|=
name|newDocumentNodeStore
argument_list|(
name|store
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|addObserver
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|addObserver
argument_list|(
name|c2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|ns1
operator|.
name|getChangeSetMaxItems
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|ns1
operator|.
name|getChangeSetMaxDepth
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeSetForExternalChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b2
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b2
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|c2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|c2
operator|.
name|getExternalChange
argument_list|()
decl_stmt|;
name|CommitContext
name|cc
init|=
operator|(
name|CommitContext
operator|)
name|ci
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
name|assertNotNull
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|ChangeSet
name|cs
init|=
operator|(
name|ChangeSet
operator|)
name|cc
operator|.
name|get
argument_list|(
name|ChangeCollectorProvider
operator|.
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cs
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cs
operator|.
name|anyOverflow
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cs
operator|.
name|getPropertyNames
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"foo1"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|missingChangeSetResultsInOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|b2
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|//Commit without ChangeSet
name|ns1
operator|.
name|merge
argument_list|(
name|b2
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
name|ns1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|c2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|c2
operator|.
name|getExternalChange
argument_list|()
decl_stmt|;
name|CommitContext
name|cc
init|=
operator|(
name|CommitContext
operator|)
name|ci
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
name|assertNotNull
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|ChangeSet
name|cs
init|=
operator|(
name|ChangeSet
operator|)
name|cc
operator|.
name|get
argument_list|(
name|ChangeCollectorProvider
operator|.
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cs
argument_list|)
expr_stmt|;
comment|//ChangeSet should result in overflow
name|assertTrue
argument_list|(
name|cs
operator|.
name|anyOverflow
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeSetForBranchCommit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_NODES
init|=
name|DocumentRootBuilder
operator|.
name|UPDATE_LIMIT
operator|/
literal|2
decl_stmt|;
specifier|final
name|int
name|NUM_PROPS
init|=
literal|10
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|propNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|c
init|=
name|b1
operator|.
name|child
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_PROPS
condition|;
name|j
operator|++
control|)
block|{
name|c
operator|.
name|setProperty
argument_list|(
literal|"q"
operator|+
name|j
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
literal|"p"
operator|+
name|j
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|propNames
operator|.
name|add
argument_list|(
literal|"q"
operator|+
name|j
argument_list|)
expr_stmt|;
name|propNames
operator|.
name|add
argument_list|(
literal|"p"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
block|}
name|ns1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|c2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|c2
operator|.
name|getExternalChange
argument_list|()
decl_stmt|;
name|CommitContext
name|cc
init|=
operator|(
name|CommitContext
operator|)
name|ci
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
name|assertNotNull
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|ChangeSet
name|cs
init|=
operator|(
name|ChangeSet
operator|)
name|cc
operator|.
name|get
argument_list|(
name|ChangeCollectorProvider
operator|.
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cs
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|containsAll
argument_list|(
name|propNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|journalService
parameter_list|()
throws|throws
name|Exception
block|{
name|wb
operator|.
name|register
argument_list|(
name|JournalPropertyService
operator|.
name|class
argument_list|,
operator|new
name|TestJournalService
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Do a dummy write so that journal property handler gets refreshed
comment|//and picks our newly registered service
name|NodeBuilder
name|b0
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b0
operator|.
name|child
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b0
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|CommitContext
name|cc
init|=
operator|new
name|SimpleCommitContext
argument_list|()
decl_stmt|;
name|cc
operator|.
name|set
argument_list|(
name|TestProperty
operator|.
name|NAME
argument_list|,
operator|new
name|TestProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|(
name|cc
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|cc
operator|=
operator|new
name|SimpleCommitContext
argument_list|()
expr_stmt|;
name|cc
operator|.
name|set
argument_list|(
name|TestProperty
operator|.
name|NAME
argument_list|,
operator|new
name|TestProperty
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b2
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|(
name|cc
argument_list|)
argument_list|)
expr_stmt|;
comment|//null entry
name|NodeBuilder
name|b3
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b3
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|merge
argument_list|(
name|b3
argument_list|,
name|newCollectingHook
argument_list|()
argument_list|,
name|newCommitInfo
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
name|c2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|CommitInfo
name|ci
init|=
name|c2
operator|.
name|getExternalChange
argument_list|()
decl_stmt|;
name|cc
operator|=
operator|(
name|CommitContext
operator|)
name|ci
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
expr_stmt|;
name|CumulativeTestProperty
name|ct
init|=
operator|(
name|CumulativeTestProperty
operator|)
name|cc
operator|.
name|get
argument_list|(
name|TestProperty
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ct
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ct
operator|.
name|values
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CommitHook
name|newCollectingHook
parameter_list|()
block|{
return|return
operator|new
name|EditorHook
argument_list|(
operator|new
name|ChangeCollectorProvider
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|()
block|{
return|return
name|newCommitInfo
argument_list|(
operator|new
name|SimpleCommitContext
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CommitInfo
name|newCommitInfo
parameter_list|(
name|CommitContext
name|commitContext
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|,
name|commitContext
argument_list|)
decl_stmt|;
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
name|info
argument_list|)
return|;
block|}
specifier|private
name|DocumentNodeStore
name|newDocumentNodeStore
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setJournalPropertyHandlerFactory
argument_list|(
name|tracker
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
comment|// disabled for debugging purposes
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|CommitInfoCollector
implements|implements
name|Observer
block|{
name|List
argument_list|<
name|CommitInfo
argument_list|>
name|infos
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
name|infos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CommitInfo
name|getExternalChange
parameter_list|()
block|{
name|List
argument_list|<
name|CommitInfo
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CommitInfo
name|info
range|:
name|infos
control|)
block|{
if|if
condition|(
name|info
operator|.
name|isExternal
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|void
name|reset
parameter_list|()
block|{
name|infos
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestJournalService
implements|implements
name|JournalPropertyService
block|{
annotation|@
name|Override
specifier|public
name|JournalPropertyBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|TestJournalBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|TestProperty
operator|.
name|NAME
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestProperty
implements|implements
name|JournalProperty
block|{
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"test.props"
decl_stmt|;
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|TestProperty
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|CumulativeTestProperty
implements|implements
name|JournalProperty
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestJournalBuilder
implements|implements
name|JournalPropertyBuilder
argument_list|<
name|TestProperty
argument_list|>
block|{
specifier|final
name|CumulativeTestProperty
name|allProps
init|=
operator|new
name|CumulativeTestProperty
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|addProperty
parameter_list|(
annotation|@
name|Nullable
name|TestProperty
name|journalProperty
parameter_list|)
block|{
if|if
condition|(
name|journalProperty
operator|!=
literal|null
condition|)
block|{
name|allProps
operator|.
name|values
operator|.
name|add
argument_list|(
name|journalProperty
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allProps
operator|.
name|values
operator|.
name|add
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|buildAsString
parameter_list|()
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|allProps
operator|.
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addSerializedProperty
parameter_list|(
annotation|@
name|Nullable
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|allProps
operator|.
name|values
argument_list|,
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|split
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|JournalProperty
name|build
parameter_list|()
block|{
return|return
name|allProps
return|;
block|}
block|}
block|}
end_class

end_unit

