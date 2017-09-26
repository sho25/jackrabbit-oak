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
name|mongo
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
name|AbstractMongoConnectionTest
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
name|DocumentMK
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
name|util
operator|.
name|MongoConnection
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|*
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|CacheInvalidationIT
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|DocumentNodeStore
name|c1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|c2
decl_stmt|;
specifier|private
name|int
name|initialCacheSizeC1
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|prepareStores
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO start with clusterNodeId 2, because 1 has already been
comment|// implicitly allocated in the base class
name|c1
operator|=
name|createNS
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|c2
operator|=
name|createNS
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|initialCacheSizeC1
operator|=
name|getCurrentCacheSize
argument_list|(
name|c1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|createScenario
parameter_list|()
throws|throws
name|CommitFailedException
block|{
comment|//          a
comment|//        / | \
comment|//       /  c  \
comment|//      b       d
comment|//     /|\      |
comment|//    / | \     h
comment|//   e  f  g
name|String
index|[]
name|paths
init|=
block|{
literal|"/a"
block|,
literal|"/a/c"
block|,
literal|"/a/b"
block|,
literal|"/a/b/e"
block|,
literal|"/a/b/f"
block|,
literal|"/a/b/g"
block|,
literal|"/a/d"
block|,
literal|"/a/d/h"
block|,         }
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|getRoot
argument_list|(
name|c1
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createTree
argument_list|(
name|root
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|c1
operator|.
name|merge
argument_list|(
name|root
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
name|assertEquals
argument_list|(
name|initialCacheSizeC1
operator|+
name|paths
operator|.
name|length
argument_list|,
name|getCurrentCacheSize
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|runBgOps
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|)
expr_stmt|;
return|return
name|paths
operator|.
name|length
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCacheInvalidation
parameter_list|()
throws|throws
name|CommitFailedException
block|{
specifier|final
name|int
name|totalPaths
init|=
name|createScenario
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|getRoot
argument_list|(
name|c2
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
argument_list|(
name|b2
argument_list|,
literal|"/a/d"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|c2
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
comment|//Push pending changes at /a
name|c2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//Refresh the head for c1
name|c1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//Only 2 entries /a and /a/d would be invalidated
comment|// '/' would have been added to cache in start of backgroundRead
comment|//itself
name|assertEquals
argument_list|(
name|initialCacheSizeC1
operator|+
name|totalPaths
operator|-
literal|2
argument_list|,
name|size
argument_list|(
name|ds
argument_list|(
name|c1
argument_list|)
operator|.
name|getNodeDocumentCache
argument_list|()
operator|.
name|keys
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCacheInvalidationHierarchicalNotExist
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|b2
init|=
name|getRoot
argument_list|(
name|c2
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// we create x/other, so that x is known to have a child node
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|c2
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
name|c2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|c1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// we check for the existence of "x/futureX", which
comment|// should create a negative entry in the cache
name|NodeState
name|x
init|=
name|getRoot
argument_list|(
name|c1
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|x
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|x
operator|.
name|getChildNode
argument_list|(
literal|"futureX"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// we don't check for the existence of "y/futureY"
name|NodeState
name|y
init|=
name|getRoot
argument_list|(
name|c1
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we add both "futureX" and "futureY"
comment|// in the other cluster node
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"futureX"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"z"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|c2
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
name|b2
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|child
argument_list|(
literal|"futureY"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"z"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|c2
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
name|c2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|c1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// both nodes should now be visible
name|assertTrue
argument_list|(
name|getRoot
argument_list|(
name|c1
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"futureY"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getRoot
argument_list|(
name|c1
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"futureX"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getCurrentCacheSize
parameter_list|(
name|DocumentNodeStore
name|ds
parameter_list|)
block|{
return|return
name|size
argument_list|(
name|ds
argument_list|(
name|ds
argument_list|)
operator|.
name|getNodeDocumentCache
argument_list|()
operator|.
name|keys
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|refreshHead
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|ds
argument_list|(
name|store
argument_list|)
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
literal|"/"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|MongoDocumentStore
name|ds
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
return|return
operator|(
name|MongoDocumentStore
operator|)
name|ns
operator|.
name|getDocumentStore
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|createTree
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
index|[]
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
name|createPath
argument_list|(
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|NodeBuilder
name|builder
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
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
name|builder
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|void
name|createPath
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|child
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|NodeState
name|getRoot
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|closeStores
parameter_list|()
block|{
if|if
condition|(
name|c2
operator|!=
literal|null
condition|)
block|{
name|c2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|c1
operator|!=
literal|null
condition|)
block|{
name|c1
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|DocumentNodeStore
name|createNS
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|MongoConnection
name|mc
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mc
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
comment|//Set delay to 0 so that effect of changes are immediately reflected
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setBundlingDisabled
argument_list|(
literal|true
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
block|}
end_class

end_unit
