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
name|jcr
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|atomic
operator|.
name|AtomicCounterEditor
operator|.
name|PROP_COUNTER
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
name|atomic
operator|.
name|AtomicCounterEditor
operator|.
name|PROP_INCREMENT
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|MIX_ATOMIC_COUNTER
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
name|Assume
operator|.
name|assumeTrue
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
name|ExecutionException
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
name|AtomicLong
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
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Futures
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFutureTask
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
name|NodeStoreFixtures
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|AtomicCounterIT
extends|extends
name|AbstractRepositoryTest
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
specifier|public
name|AtomicCounterIT
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumptions
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|SEGMENT_TAR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|concurrentSegmentIncrements
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
comment|// ensuring the run only on allowed fix
name|assumeTrue
argument_list|(
name|NodeStoreFixtures
operator|.
name|SEGMENT_TAR
operator|.
name|equals
argument_list|(
name|fixture
argument_list|)
argument_list|)
expr_stmt|;
comment|// setting-up
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|counter
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"counter"
argument_list|)
decl_stmt|;
name|counter
operator|.
name|addMixin
argument_list|(
name|MIX_ATOMIC_COUNTER
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
specifier|final
name|AtomicLong
name|expected
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|counterPath
init|=
name|counter
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
literal|11
argument_list|)
decl_stmt|;
comment|// ensuring initial state
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|,
name|counter
operator|.
name|getProperty
argument_list|(
name|PROP_COUNTER
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ListenableFutureTask
argument_list|<
name|Void
argument_list|>
argument_list|>
name|tasks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
literal|100
condition|;
name|t
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
name|updateCounter
argument_list|(
name|counterPath
argument_list|,
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Futures
operator|.
name|allAsList
argument_list|(
name|tasks
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|,
name|session
operator|.
name|getNode
argument_list|(
name|counterPath
argument_list|)
operator|.
name|getProperty
argument_list|(
name|PROP_COUNTER
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|ListenableFutureTask
argument_list|<
name|Void
argument_list|>
name|updateCounter
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|counterPath
parameter_list|,
specifier|final
name|long
name|delta
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|AtomicLong
name|expected
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|counterPath
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|ListenableFutureTask
argument_list|<
name|Void
argument_list|>
name|task
init|=
name|ListenableFutureTask
operator|.
name|create
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|c
init|=
name|session
operator|.
name|getNode
argument_list|(
name|counterPath
argument_list|)
decl_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
name|PROP_INCREMENT
argument_list|,
name|delta
argument_list|)
expr_stmt|;
name|expected
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|task
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|task
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Jcr
name|initJcr
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
return|return
name|super
operator|.
name|initJcr
argument_list|(
name|jcr
argument_list|)
operator|.
name|withAtomicCounter
argument_list|()
return|;
block|}
block|}
end_class

end_unit

