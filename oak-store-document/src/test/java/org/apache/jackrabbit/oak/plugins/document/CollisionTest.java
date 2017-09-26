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
name|atomic
operator|.
name|AtomicInteger
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
name|Collection
operator|.
name|NODES
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
name|NodeDocument
operator|.
name|COLLISIONS
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|CollisionTest
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
specifier|static
specifier|final
name|AtomicInteger
name|COUNTER
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|// OAK-2342
annotation|@
name|Test
specifier|public
name|void
name|purge
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentMK
name|mk1
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns1
init|=
name|mk1
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|store
init|=
name|ns1
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns2
init|=
name|mk2
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|createCollision
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|createCollision
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|.
name|getLocalMap
argument_list|(
name|COLLISIONS
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// restart node store
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk1
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
name|ns1
operator|=
name|mk1
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
comment|// must purge collision for clusterId 1
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|.
name|getLocalMap
argument_list|(
name|COLLISIONS
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// restart other node store
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk2
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
name|ns2
operator|=
name|mk2
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
comment|// must purge collision for clusterId 2
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
operator|.
name|getLocalMap
argument_list|(
name|COLLISIONS
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isConflicting
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|DocumentNodeStore
name|ns
init|=
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
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
comment|// test:{p:"a"}
name|Revision
name|r1
init|=
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|getDocument
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
decl_stmt|;
comment|// concurrent create
name|Revision
name|c
init|=
name|ns
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NodeDocument
operator|.
name|setDeleted
argument_list|(
name|op
argument_list|,
name|c
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Collision
name|col
init|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r1
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
comment|// concurrent change
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"p"
argument_list|,
name|c
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r1
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
comment|// test:{p:"b"}
name|Revision
name|r2
init|=
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|doc
operator|=
name|getDocument
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// concurrent delete
name|c
operator|=
name|ns
operator|.
name|newRevision
argument_list|()
expr_stmt|;
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setDeleted
argument_list|(
name|op
argument_list|,
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r2
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
comment|// concurrent conflicting property set
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"p"
argument_list|,
name|c
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r2
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
comment|// concurrent non-conflicting property set
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"q"
argument_list|,
name|c
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r2
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// test (removed)
name|Revision
name|r3
init|=
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
operator|.
name|getRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|doc
operator|=
name|getDocument
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// concurrent delete
name|c
operator|=
name|ns
operator|.
name|newRevision
argument_list|()
expr_stmt|;
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setDeleted
argument_list|(
name|op
argument_list|,
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r3
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
comment|// concurrent conflicting property set
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"p"
argument_list|,
name|c
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|col
operator|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r3
argument_list|,
name|op
argument_list|,
name|c
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|col
operator|.
name|isConflicting
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|RevisionVector
name|merge
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|NodeBuilder
name|nb
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
expr_stmt|;
return|return
name|ns
operator|.
name|getHeadRevision
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|NodeDocument
name|getDocument
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
specifier|private
name|void
name|createCollision
parameter_list|(
name|DocumentMK
name|mk
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|nodeName
init|=
literal|"test-"
operator|+
name|COUNTER
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
comment|// create branch
name|String
name|b
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|nodeName
operator|+
literal|"\":{\"p\":\"a\"}"
argument_list|,
name|b
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// commit a change resulting in a collision on branch
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|nodeName
operator|+
literal|"\":{\"p\":\"b\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

