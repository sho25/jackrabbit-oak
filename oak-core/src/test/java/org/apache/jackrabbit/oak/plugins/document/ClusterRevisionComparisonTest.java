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
name|commons
operator|.
name|PathUtils
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
name|spi
operator|.
name|blob
operator|.
name|MemoryBlobStore
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
name|Ignore
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
name|ClusterRevisionComparisonTest
block|{
specifier|private
name|MemoryDocumentStore
name|ds
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
specifier|private
name|MemoryBlobStore
name|bs
init|=
operator|new
name|MemoryBlobStore
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
name|setUp
parameter_list|()
block|{
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2144"
argument_list|)
comment|//FIX ME OAK-2144
annotation|@
name|Test
specifier|public
name|void
name|revisionComparisonMultipleClusterNode
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|c1
init|=
name|createNS
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|c2
init|=
name|createNS
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|c3
init|=
name|createNS
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|//1. Create /a and make it visible to all cluster nodes
name|createNode
argument_list|(
name|c1
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|runBgOps
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|,
name|c3
argument_list|)
expr_stmt|;
comment|//2. Time T1. Create /a/c2 but do not push the changes yet rT1-C2
name|createNode
argument_list|(
name|c2
argument_list|,
literal|"/a/c2"
argument_list|)
expr_stmt|;
comment|//3. Time T2. Create /a/c3 and push the changes rT2-C3
name|createNode
argument_list|(
name|c3
argument_list|,
literal|"/a/c3"
argument_list|)
expr_stmt|;
name|runBgOps
argument_list|(
name|c3
argument_list|)
expr_stmt|;
comment|//4. Time T3. Read the changes /a/c3 by c3 created at T2
comment|// would be considered seen at T3 i.e. rT2-C3 -> rT3-C1
name|runBgOps
argument_list|(
name|c1
argument_list|)
expr_stmt|;
comment|//5. Push changes
name|runBgOps
argument_list|(
name|c2
argument_list|)
expr_stmt|;
comment|//6. Time T4. Read the changes /a/c2 by c2 created at T1.
comment|// Would be considered seen at T4 i.e. rT1-C2 -> rT4-C1
comment|// Now from C1 view rT1-C2> rT2-C3 even though T1< T2
comment|//so effectively changes done in future in C3 in absolute time terms
comment|//is considered to be seen in past by C1
name|runBgOps
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|DocumentNodeState
name|c1ns1
init|=
name|c1
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|Iterables
operator|.
name|size
argument_list|(
name|c1ns1
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|)
expr_stmt|;
name|createNode
argument_list|(
name|c1
argument_list|,
literal|"/a/c1"
argument_list|)
expr_stmt|;
comment|//7. Purge revision comparator. Also purge entries from nodeCache
comment|//such that later reads at rT1-C2 triggers read from underlying DocumentStore
name|c1
operator|.
name|invalidateNodeCache
argument_list|(
literal|"/a/c2"
argument_list|,
operator|(
operator|(
name|DocumentNodeState
operator|)
name|c1ns1
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|)
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|.
name|invalidateNodeCache
argument_list|(
literal|"/a/c3"
argument_list|,
operator|(
operator|(
name|DocumentNodeState
operator|)
name|c1ns1
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|)
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
comment|//Revision comparator purge by moving in future
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|DocumentNodeStore
operator|.
name|REMEMBER_REVISION_ORDER_MILLIS
operator|*
literal|2
argument_list|)
expr_stmt|;
name|runBgOps
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|NodeState
name|a
init|=
name|c1ns1
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"/a/c2 disappeared"
argument_list|,
name|a
operator|.
name|hasChildNode
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"/a/c3 disappeared"
argument_list|,
name|a
operator|.
name|hasChildNode
argument_list|(
literal|"c3"
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentNodeState
name|c1ns2
init|=
name|c1
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|//Trigger a diff. With OAK-2144 an exception would be thrown as diff traverses
comment|//the /a children
name|c1ns1
operator|.
name|compareAgainstBaseState
argument_list|(
name|c1ns2
argument_list|,
operator|new
name|ClusterTest
operator|.
name|TrackingDiff
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
specifier|private
name|DocumentNodeStore
name|createNS
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|open
argument_list|()
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
specifier|private
name|NodeState
name|createNode
parameter_list|(
name|NodeStore
name|ns
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|cb
init|=
name|nb
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|cb
operator|=
name|cb
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|ns
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
return|;
block|}
specifier|private
specifier|static
name|void
name|runBgOps
parameter_list|(
name|DocumentNodeStore
modifier|...
name|stores
parameter_list|)
block|{
for|for
control|(
name|DocumentNodeStore
name|ns
range|:
name|stores
control|)
block|{
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

