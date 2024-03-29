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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Maps
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
name|cache
operator|.
name|CacheStats
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
name|LocalDiffCache
operator|.
name|Diff
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LocalDiffCacheTest
block|{
name|DocumentNodeStore
name|store
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|TestNodeObserver
name|o
init|=
operator|new
name|TestNodeObserver
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|store
operator|=
name|createMK
argument_list|()
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|addObserver
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|o
operator|.
name|reset
argument_list|()
expr_stmt|;
name|DiffCache
name|cache
init|=
name|store
operator|.
name|getDiffCache
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|stats
init|=
name|cache
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
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
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"a2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getHitCount
argument_list|(
name|stats
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getMissCount
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|o
operator|.
name|added
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|store
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
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"a2"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|o
operator|.
name|reset
argument_list|()
expr_stmt|;
name|resetStats
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getHitCount
argument_list|(
name|stats
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getMissCount
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|o
operator|.
name|changed
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|diffFromAsString
parameter_list|()
block|{
name|Map
argument_list|<
name|Path
argument_list|,
name|String
argument_list|>
name|changes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|Path
operator|.
name|ROOT
argument_list|,
literal|"+\"foo\":{}^\"bar\":{}-\"baz\""
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/bar"
argument_list|)
argument_list|,
literal|"+\"qux\""
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/bar/qux"
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
name|changes
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|changes
argument_list|,
name|Diff
operator|.
name|fromString
argument_list|(
name|diff
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|getChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|Path
argument_list|,
name|String
argument_list|>
name|changes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
name|changes
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
name|asString
init|=
name|diff
operator|.
name|asString
argument_list|()
decl_stmt|;
name|Diff
name|diff2
init|=
name|Diff
operator|.
name|fromString
argument_list|(
name|asString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|diff
argument_list|,
name|diff2
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|DocumentNodeState
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
return|return
operator|(
name|DocumentNodeState
operator|)
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
return|;
block|}
specifier|private
specifier|static
name|DocumentMK
name|createMK
parameter_list|()
block|{
return|return
name|create
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|DocumentMK
name|create
parameter_list|(
name|DocumentStore
name|ds
parameter_list|,
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
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache,time"
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|long
name|getHitCount
parameter_list|(
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|stats
parameter_list|)
block|{
name|long
name|hitCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CacheStats
name|cs
range|:
name|stats
control|)
block|{
name|hitCount
operator|+=
name|cs
operator|.
name|getHitCount
argument_list|()
expr_stmt|;
block|}
return|return
name|hitCount
return|;
block|}
specifier|private
specifier|static
name|long
name|getMissCount
parameter_list|(
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|stats
parameter_list|)
block|{
name|long
name|missCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CacheStats
name|cs
range|:
name|stats
control|)
block|{
name|missCount
operator|+=
name|cs
operator|.
name|getMissCount
argument_list|()
expr_stmt|;
block|}
return|return
name|missCount
return|;
block|}
specifier|private
specifier|static
name|void
name|resetStats
parameter_list|(
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|stats
parameter_list|)
block|{
for|for
control|(
name|CacheStats
name|cs
range|:
name|stats
control|)
block|{
name|cs
operator|.
name|resetStats
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

