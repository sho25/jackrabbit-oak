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
name|NodeStateDiffer
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

begin_class
specifier|public
class|class
name|SecondaryStoreObserverTest
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
name|basicSetup
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
name|SecondaryStoreObserver
name|observer
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
operator|.
name|buildObserver
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
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
expr_stmt|;
name|dump
argument_list|(
name|secondaryRoot
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|dump
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|secondaryRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|primary
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|childNodeAdded
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
name|SecondaryStoreObserver
name|observer
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
operator|.
name|buildObserver
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
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
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/d"
argument_list|)
expr_stmt|;
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
expr_stmt|;
name|assertMetaState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|secondaryRoot
argument_list|()
argument_list|,
literal|"/a/d"
argument_list|)
expr_stmt|;
name|assertMetaState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|secondaryRoot
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|childNodeChangedAndExclude
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
name|of
argument_list|(
literal|"a/b"
argument_list|)
argument_list|)
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
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
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
name|create
argument_list|(
name|nb
argument_list|,
literal|"/a/d"
argument_list|,
literal|"/a/b/e"
argument_list|)
expr_stmt|;
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
expr_stmt|;
name|assertMetaState
argument_list|(
name|primary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|secondaryRoot
argument_list|()
argument_list|,
literal|"/a/d"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|childNodeDeleted
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
name|SecondaryStoreObserver
name|observer
init|=
name|createBuilder
argument_list|(
name|pathFilter
argument_list|)
operator|.
name|buildObserver
argument_list|()
decl_stmt|;
name|primary
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
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
expr_stmt|;
name|assertFalse
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|secondaryRoot
argument_list|()
argument_list|,
literal|"/a/c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|secondaryRoot
parameter_list|()
block|{
return|return
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|secondary
operator|.
name|getRoot
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
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
specifier|static
name|void
name|assertMetaState
parameter_list|(
name|NodeState
name|root1
parameter_list|,
name|NodeState
name|root2
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|assertMetaState
argument_list|(
name|documentState
argument_list|(
name|root1
argument_list|,
name|path
argument_list|)
argument_list|,
name|documentState
argument_list|(
name|root2
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertMetaState
parameter_list|(
name|AbstractDocumentNodeState
name|a
parameter_list|,
name|AbstractDocumentNodeState
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|getRevision
argument_list|()
argument_list|,
name|b
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getRootRevision
argument_list|()
argument_list|,
name|b
operator|.
name|getRootRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|,
name|b
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
name|AbstractDocumentNodeState
name|documentState
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|(
name|AbstractDocumentNodeState
operator|)
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|dump
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|NodeStateUtils
operator|.
name|toString
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|NodeState
name|create
parameter_list|(
name|NodeBuilder
name|b
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|NodeBuilder
name|cb
init|=
name|b
decl_stmt|;
for|for
control|(
name|String
name|pathElement
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
name|pathElement
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

