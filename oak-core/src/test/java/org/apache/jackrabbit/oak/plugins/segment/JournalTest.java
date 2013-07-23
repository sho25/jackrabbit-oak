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
name|segment
package|;
end_package

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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|PostCommitHook
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
name|NodeStoreBranch
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

begin_class
specifier|public
class|class
name|JournalTest
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|root
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|left
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|,
literal|"left"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|right
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|,
literal|"right"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testChangesFromRoot
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|oldState
init|=
name|root
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|oldState
operator|.
name|builder
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
name|newState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|root
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"left"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"right"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangesToRoot
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|oldState
init|=
name|root
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|oldState
operator|.
name|builder
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
name|newState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|left
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"left"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"right"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentChanges
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|oldState
init|=
name|root
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|leftBuilder
init|=
name|oldState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|leftBuilder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|leftState
init|=
name|leftBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|leftBranch
init|=
name|left
operator|.
name|branch
argument_list|()
decl_stmt|;
name|leftBranch
operator|.
name|setRoot
argument_list|(
name|leftState
argument_list|)
expr_stmt|;
name|leftBranch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|leftState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"left"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|leftState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|leftState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|rightBuilder
init|=
name|oldState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rightBuilder
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|rightState
init|=
name|rightBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|rightBranch
init|=
name|right
operator|.
name|branch
argument_list|()
decl_stmt|;
name|rightBranch
operator|.
name|setRoot
argument_list|(
name|rightState
argument_list|)
expr_stmt|;
name|rightBranch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"right"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|NodeState
name|newState
init|=
name|root
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|newState
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|newState
operator|.
name|getProperty
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|leftState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getJournal
argument_list|(
literal|"left"
argument_list|)
operator|.
name|merge
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|root
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|left
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newState
argument_list|,
name|right
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

