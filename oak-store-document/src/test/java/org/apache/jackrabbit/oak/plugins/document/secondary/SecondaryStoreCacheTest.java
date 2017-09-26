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
name|Collection
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
name|NodeDocument
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
name|document
operator|.
name|bundlor
operator|.
name|BundledTypesRegistry
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
name|bundlor
operator|.
name|BundlingConfigHandler
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
name|bundlor
operator|.
name|BundlingConfigInitializer
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
name|InitialContent
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
name|filter
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
name|NodeStateUtils
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
comment|//Child docs should only have lastRev and not root rev
name|assertTrue
argument_list|(
name|result
operator|.
name|hasProperty
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|PROP_LAST_REV
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|hasProperty
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|PROP_REVISION
argument_list|)
argument_list|)
expr_stmt|;
comment|//Root doc would have both meta props
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|PROP_LAST_REV
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getRoot
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|PROP_REVISION
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
annotation|@
name|Test
specifier|public
name|void
name|readWithSecondaryLagging
parameter_list|()
throws|throws
name|Exception
block|{
name|PathFilter
name|pathFilter
init|=
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
decl_stmt|;
name|SecondaryStoreCache
name|cache
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
operator|.
name|buildCache
argument_list|()
decl_stmt|;
name|SecondaryStoreObserver
name|observer
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
operator|.
name|buildObserver
argument_list|(
name|cache
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
name|observer
operator|.
name|contentChanged
argument_list|(
name|r0
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|result
init|=
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
decl_stmt|;
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
comment|//Make change in some other part of tree i.e. /a/c is unmodified
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
literal|"/a/e"
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
comment|//Change is yet not pushed to secondary i.e. observer not invoked
comment|//but lookup with latest root should still work fine if lastRev matches
name|result
operator|=
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
comment|//Change which is not pushed would though not be visible
name|AbstractDocumentNodeState
name|a_e_1
init|=
name|documentState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/a/e"
argument_list|)
decl_stmt|;
name|result
operator|=
name|cache
operator|.
name|getDocumentNodeState
argument_list|(
literal|"/a/e"
argument_list|,
name|r1
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|a_e_1
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
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
name|isCached
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
name|assertTrue
argument_list|(
name|cache
operator|.
name|isCached
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|isCached
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cache
operator|.
name|isCached
argument_list|(
literal|"/x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|bundledNodes
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
literal|"/"
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
decl_stmt|;
name|primary
operator|.
name|setNodeStateCache
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|BundlingConfigInitializer
operator|.
name|INSTANCE
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|BundledTypesRegistry
name|registry
init|=
name|BundledTypesRegistry
operator|.
name|from
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|BundlingConfigHandler
operator|.
name|CONFIG_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"DocumentBundling not found to be enabled for nt:file"
argument_list|,
name|registry
operator|.
name|getBundlor
argument_list|(
name|newNode
argument_list|(
literal|"nt:file"
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//1. Create a file node
name|builder
operator|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|fileNode
init|=
name|newNode
argument_list|(
literal|"nt:file"
argument_list|)
decl_stmt|;
name|fileNode
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"book.jpg"
argument_list|,
name|fileNode
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
comment|//2. Assert that bundling is working
name|assertNull
argument_list|(
name|getNodeDocument
argument_list|(
literal|"/test/book.jpg/jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
comment|//3. Now update the file node
name|builder
operator|=
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"book.jpg"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
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
name|SecondaryStoreBuilder
name|builder
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
decl_stmt|;
name|builder
operator|.
name|metaPropNames
argument_list|(
name|DocumentNodeStore
operator|.
name|META_PROP_NAMES
argument_list|)
expr_stmt|;
name|SecondaryStoreCache
name|cache
init|=
name|builder
operator|.
name|buildCache
argument_list|()
decl_stmt|;
name|SecondaryStoreObserver
name|observer
init|=
name|builder
operator|.
name|buildObserver
argument_list|(
name|cache
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
specifier|static
name|NodeBuilder
name|newNode
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
name|NodeDocument
name|getNodeDocument
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|primary
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|SecondaryStoreBuilder
name|createBuilder
parameter_list|(
name|PathFilter
name|pathFilter
parameter_list|)
block|{
return|return
operator|new
name|SecondaryStoreBuilder
argument_list|(
name|secondary
argument_list|)
operator|.
name|pathFilter
argument_list|(
name|pathFilter
argument_list|)
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
