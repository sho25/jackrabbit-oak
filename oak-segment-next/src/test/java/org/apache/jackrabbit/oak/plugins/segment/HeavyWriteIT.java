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
name|segment
package|;
end_package

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
name|commons
operator|.
name|CIHelper
operator|.
name|travis
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
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|SEGMENT_MK
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
name|segment
operator|.
name|compaction
operator|.
name|CompactionStrategy
operator|.
name|CleanupType
operator|.
name|CLEAN_OLD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

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
name|AtomicBoolean
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Blob
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
name|FixturesHelper
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
name|FixturesHelper
operator|.
name|Fixture
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
name|compaction
operator|.
name|CompactionStrategy
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
name|file
operator|.
name|FileStore
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
name|BeforeClass
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|HeavyWriteIT
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Fixture
argument_list|>
name|FIXTURES
init|=
name|FixturesHelper
operator|.
name|getFixtures
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|usePersistedMap
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
name|File
name|getFileStoreFolder
parameter_list|()
block|{
return|return
name|folder
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"usePersistedMap: {0}"
argument_list|)
specifier|public
specifier|static
name|List
argument_list|<
name|Boolean
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|Boolean
index|[]
block|{
literal|true
block|}
argument_list|,
operator|new
name|Boolean
index|[]
block|{
literal|false
block|}
argument_list|)
return|;
block|}
specifier|public
name|HeavyWriteIT
parameter_list|(
name|boolean
name|usePersistedMap
parameter_list|)
block|{
name|this
operator|.
name|usePersistedMap
operator|=
name|usePersistedMap
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|checkFixtures
parameter_list|()
block|{
name|assumeTrue
argument_list|(
operator|!
name|travis
argument_list|()
argument_list|)
expr_stmt|;
comment|// FIXME OAK-2375. Often fails on Travis
name|assumeTrue
argument_list|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|SEGMENT_MK
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|heavyWrite
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|InterruptedException
block|{
specifier|final
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|128
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SegmentNodeStore
name|nodeStore
init|=
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CompactionStrategy
name|custom
init|=
operator|new
name|CompactionStrategy
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|CLEAN_OLD
argument_list|,
literal|30000
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|setHead
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|nodeStore
operator|.
name|locked
argument_list|(
name|setHead
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|custom
operator|.
name|setPersistCompactionMap
argument_list|(
name|usePersistedMap
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCompactionStrategy
argument_list|(
name|custom
argument_list|)
expr_stmt|;
name|int
name|writes
init|=
literal|100
decl_stmt|;
specifier|final
name|AtomicBoolean
name|run
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|run
operator|.
name|get
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|store
operator|.
name|gc
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<=
name|writes
condition|;
name|k
operator|++
control|)
block|{
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|test
init|=
name|root
operator|.
name|setChildNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|nodeStore
argument_list|,
name|test
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|nodeStore
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
name|root
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|root
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeStore
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
block|}
block|}
finally|finally
block|{
name|run
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|createNodes
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|count
condition|;
name|k
operator|++
control|)
block|{
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"node"
operator|+
name|k
argument_list|)
decl_stmt|;
name|createProperties
argument_list|(
name|nodeStore
argument_list|,
name|child
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
name|nodeStore
argument_list|,
name|child
argument_list|,
name|count
argument_list|,
name|depth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|createProperties
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|count
condition|;
name|k
operator|++
control|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
literal|"property-"
operator|+
name|k
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
literal|100000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

