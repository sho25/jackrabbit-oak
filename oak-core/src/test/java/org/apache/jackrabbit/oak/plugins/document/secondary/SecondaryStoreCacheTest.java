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
operator|.
name|secondary
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
name|Collections
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
name|document
operator|.
name|AbstractDocumentNodeState
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
name|DocumentMKBuilderProvider
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
name|DocumentNodeStore
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
name|Revision
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
name|RevisionVector
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
name|PathFilter
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
name|memory
operator|.
name|MemoryNodeStore
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
name|EqualsDiff
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
name|StatisticsProvider
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
name|of
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
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
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
name|secondary
operator|.
name|SecondaryStoreObserverTest
operator|.
name|create
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
name|secondary
operator|.
name|SecondaryStoreObserverTest
operator|.
name|documentState
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|SecondaryStoreCacheTest
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
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
name|DocumentNodeStore
name|primary
decl_stmt|;
specifier|private
name|NodeStore
name|secondary
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
name|primary
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|secondary
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SecondaryStoreCache
name|cache
init|=
name|createCache
argument_list|(
operator|new
name|PathFilter
argument_list|(
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|,
literal|"/x/y/z"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|nb
argument_list|)
expr_stmt|;
name|RevisionVector
name|rv1
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|rv2
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/b"
argument_list|,
name|rv1
argument_list|,
name|rv2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/x"
argument_list|,
name|rv1
argument_list|,
name|rv2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateAndReadAtReadRev
parameter_list|()
throws|throws
name|Exception
block|{
name|SecondaryStoreCache
name|cache
init|=
name|createCache
argument_list|(
operator|new
name|PathFilter
argument_list|(
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|,
literal|"/x/y/z"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r1
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
comment|//Update some other part of tree i.e. which does not change lastRev for /a/c
name|nb
operator|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/e/d"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r2
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
comment|//Lookup should work fine
name|AbstractDocumentNodeState
name|a_r2
init|=
name|documentState
argument_list|(
name|r2
argument_list|,
literal|"/a/c"
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|result
init|=
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/c"
argument_list|,
name|r2
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|a_r2
operator|.
name|getLastRevision
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|a_r2
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|nb
operator|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|nb
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|AbstractDocumentNodeState
name|r3
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
comment|//Now look from older revision
name|result
operator|=
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/c"
argument_list|,
name|r3
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|a_r2
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
comment|//now as its not visible from head it would not be visible
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateAndReadAtPrevRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|SecondaryStoreCache
name|cache
init|=
name|createCache
argument_list|(
operator|new
name|PathFilter
argument_list|(
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r0
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|a_c_0
init|=
name|documentState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/a/c"
argument_list|)
decl_stmt|;
comment|//Update some other part of tree i.e. which does not change lastRev for /a/c
name|nb
operator|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/c/d"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r1
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|a_c_1
init|=
name|documentState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/a/c"
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|result
init|=
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/c"
argument_list|,
name|r1
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|a_c_1
operator|.
name|getLastRevision
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|a_c_1
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
comment|//Read from older revision
name|result
operator|=
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/c"
argument_list|,
name|r0
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|a_c_0
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|a_c_0
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|binarySearch
parameter_list|()
throws|throws
name|Exception
block|{
name|SecondaryStoreCache
name|cache
init|=
name|createCache
argument_list|(
operator|new
name|PathFilter
argument_list|(
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AbstractDocumentNodeState
argument_list|>
name|roots
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RevisionVector
argument_list|>
name|revs
init|=
name|Lists
operator|.
name|newArrayList
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|nb
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/b"
operator|+
name|i
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
name|roots
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|revs
operator|.
name|add
argument_list|(
name|r
operator|.
name|getRootRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AbstractDocumentNodeState
index|[]
name|rootsArr
init|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|roots
argument_list|,
name|AbstractDocumentNodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|revs
argument_list|)
expr_stmt|;
for|for
control|(
name|RevisionVector
name|rev
range|:
name|revs
control|)
block|{
name|AbstractDocumentNodeState
name|result
init|=
name|SecondaryStoreCache
operator|.
name|findMatchingRoot
argument_list|(
name|rootsArr
argument_list|,
name|rev
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rev
argument_list|,
name|result
operator|.
name|getRootRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|nb
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/m"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|r
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|result
init|=
name|SecondaryStoreCache
operator|.
name|findMatchingRoot
argument_list|(
name|rootsArr
argument_list|,
name|r
operator|.
name|getRootRevision
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SecondaryStoreCache
name|createCache
parameter_list|(
name|PathFilter
name|pathFilter
parameter_list|)
block|{
name|SecondaryStoreCache
name|cache
init|=
operator|new
name|SecondaryStoreCache
argument_list|(
name|secondary
argument_list|,
name|pathFilter
argument_list|,
name|DEFAULT_DIFFER
argument_list|)
decl_stmt|;
name|SecondaryStoreObserver
name|observer
init|=
operator|new
name|SecondaryStoreObserver
argument_list|(
name|secondary
argument_list|,
name|pathFilter
argument_list|,
name|cache
argument_list|,
name|DEFAULT_DIFFER
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|primary
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
return|return
name|cache
return|;
block|}
specifier|private
name|AbstractDocumentNodeState
name|merge
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|(
name|AbstractDocumentNodeState
operator|)
name|primary
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
block|}
end_class

end_unit

