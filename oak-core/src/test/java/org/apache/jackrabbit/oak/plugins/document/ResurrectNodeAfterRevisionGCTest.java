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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|util
operator|.
name|Utils
operator|.
name|getIdFromPath
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
name|ResurrectNodeAfterRevisionGCTest
extends|extends
name|AbstractMultiDocumentStoreTest
block|{
specifier|private
name|Clock
name|c
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns2
decl_stmt|;
specifier|public
name|ResurrectNodeAfterRevisionGCTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/baz"
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|removeMe
control|)
block|{
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ClusterNodeInfoDocument
name|doc
range|:
name|ClusterNodeInfoDocument
operator|.
name|all
argument_list|(
name|ds
argument_list|)
control|)
block|{
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|c
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
name|c
argument_list|)
expr_stmt|;
name|ns1
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|wrap
argument_list|(
name|ds1
argument_list|)
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|ns2
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|wrap
argument_list|(
name|ds2
argument_list|)
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|disposeNodeStores
parameter_list|()
block|{
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectAfterGC
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|ns1
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
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns1
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
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|getLong
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
comment|// removing both will result in a commit root on 0:/
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"baz"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|ds2
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|getIdFromPath
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|ns2
argument_list|,
name|ns2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|ns1
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stats
operator|.
name|deletedDocGCCount
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns1
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
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
comment|// setting 'x' puts the commit root on /foo
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns1
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
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|getLong
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidate
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidate
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidateAll
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidate
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidateIndividual
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidate
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidateWithModified
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidateWithModified
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidateAllWithModified
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidateWithModified
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|resurrectInvalidateIndividualWithModified
parameter_list|()
throws|throws
name|Exception
block|{
name|resurrectInvalidateWithModified
argument_list|(
operator|new
name|Invalidate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|store
operator|.
name|invalidateCache
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|resurrectInvalidateWithModified
parameter_list|(
name|Invalidate
name|inv
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ds1
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|ds2
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50L
argument_list|,
operator|(
name|long
operator|)
name|doc
operator|.
name|getModified
argument_list|()
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// recreate with different value for 'p'
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|55
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ds1
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|inv
operator|.
name|perform
argument_list|(
name|ds2
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ds2
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|55L
argument_list|,
operator|(
name|long
operator|)
name|doc
operator|.
name|getModified
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|resurrectInvalidate
parameter_list|(
name|Invalidate
name|inv
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ds1
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|ds2
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|ds1
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// recreate with different value for 'p'
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ds1
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|inv
operator|.
name|perform
argument_list|(
name|ds2
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ds2
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
block|}
interface|interface
name|Invalidate
block|{
name|void
name|perform
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
function_decl|;
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
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
block|}
specifier|private
specifier|static
name|DocumentStore
name|wrap
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|DSWrapper
argument_list|(
name|store
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|DSWrapper
extends|extends
name|DocumentStoreWrapper
block|{
name|DSWrapper
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

