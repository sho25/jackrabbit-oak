begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more   * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|util
operator|.
name|Utils
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
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|TestUtils
operator|.
name|merge
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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

begin_class
specifier|public
class|class
name|BackgroundSplitFailureTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|after
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|journalException
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|FailingDocumentStore
name|failingStore
init|=
operator|new
name|FailingDocumentStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|ns
init|=
operator|new
name|DocumentNodeStoreBuilder
argument_list|<>
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|failingStore
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|int
name|clusterId
init|=
name|ns
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|Path
name|fooPath
init|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|ROOT
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|String
name|fooId
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|fooPath
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|fooPath
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
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
operator|<=
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|fooPath
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
comment|// shut down without running background ops
name|failingStore
operator|.
name|fail
argument_list|()
operator|.
name|after
argument_list|(
literal|0
argument_list|)
operator|.
name|eternally
argument_list|()
expr_stmt|;
try|try
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"dispose is expected to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// must not have previous documents yet
name|NodeDocument
name|foo
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|fooId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|foo
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// start again with test store
specifier|final
name|AtomicBoolean
name|falseOnJournalEntryCreate
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|DocumentStore
name|testStore
init|=
operator|new
name|DocumentStoreWrapper
argument_list|(
name|store
argument_list|)
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|JOURNAL
operator|&&
name|falseOnJournalEntryCreate
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|super
operator|.
name|create
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ns
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|testStore
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ns
operator|.
name|addSplitCandidate
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|ROOT
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|falseOnJournalEntryCreate
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"background operations are expected to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Unable to create journal entry"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// must still not have previous documents
name|foo
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|fooId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|foo
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns
operator|.
name|getSplitCandidates
argument_list|()
operator|.
name|contains
argument_list|(
name|fooId
argument_list|)
argument_list|)
expr_stmt|;
name|falseOnJournalEntryCreate
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// now there must be a split document
name|foo
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|fooId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|foo
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ns
operator|.
name|getSplitCandidates
argument_list|()
operator|.
name|contains
argument_list|(
name|fooId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

