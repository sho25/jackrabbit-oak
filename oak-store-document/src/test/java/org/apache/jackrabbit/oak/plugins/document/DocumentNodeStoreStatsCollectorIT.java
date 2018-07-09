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
name|Matchers
operator|.
name|anyInt
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
name|anyLong
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
name|eq
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
name|times
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

begin_class
specifier|public
class|class
name|DocumentNodeStoreStatsCollectorIT
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
name|DocumentNodeStoreStatsCollector
name|statsCollector
init|=
name|mock
argument_list|(
name|DocumentNodeStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
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
name|nodeStore
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setNodeStoreStatsCollector
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|setUpdateLimit
argument_list|(
literal|10
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
comment|// do not retry failed merges
name|nodeStore
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|reset
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
name|doneBackgroundRead
parameter_list|()
block|{
name|nodeStore
operator|.
name|runBackgroundReadOperations
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|doneBackgroundRead
argument_list|(
name|any
argument_list|(
name|BackgroundReadStats
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|doneBackgroundUpdate
parameter_list|()
block|{
name|nodeStore
operator|.
name|runBackgroundUpdateOperations
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|doneBackgroundUpdate
argument_list|(
name|any
argument_list|(
name|BackgroundWriteStats
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|doneMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|nb
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
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
name|nodeStore
operator|.
name|merge
argument_list|(
name|nb
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
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|doneMerge
argument_list|(
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|failedMerge
parameter_list|()
block|{
name|CommitHook
name|failingHook
init|=
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|MERGE
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|NodeBuilder
name|nb1
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb1
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
try|try
block|{
name|nodeStore
operator|.
name|merge
argument_list|(
name|nb1
argument_list|,
name|failingHook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|ignore
parameter_list|)
block|{          }
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|failedMerge
argument_list|(
name|anyInt
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|//Should be called once more with exclusive lock
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|failedMerge
argument_list|(
name|anyInt
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|branchCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|updateLimit
init|=
name|nodeStore
operator|.
name|getUpdateLimit
argument_list|()
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|nodeStore
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
name|updateLimit
condition|;
name|i
operator|++
control|)
block|{
name|nb
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
block|}
name|merge
argument_list|(
name|nodeStore
argument_list|,
name|nb
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|statsCollector
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|doneBranchCommit
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|doneMergeBranch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leaseUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeStore
operator|.
name|renewClusterIdLease
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|statsCollector
argument_list|)
operator|.
name|doneLeaseUpdate
argument_list|(
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

