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
name|jcr
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|io
operator|.
name|File
operator|.
name|createTempFile
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
name|CLEAN_NONE
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
name|file
operator|.
name|FileStore
operator|.
name|newFileStore
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
name|FutureTask
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
name|Repository
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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|api
operator|.
name|JackrabbitRepository
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
name|Oak
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
name|gc
operator|.
name|GCMonitorTracker
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
name|SegmentNodeStore
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
name|spi
operator|.
name|whiteboard
operator|.
name|DefaultWhiteboard
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
name|whiteboard
operator|.
name|Whiteboard
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
name|RefreshOnGCTest
block|{
specifier|private
specifier|final
name|boolean
name|usePersistedMap
decl_stmt|;
specifier|private
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|GCMonitorTracker
name|gcMonitor
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
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
name|RefreshOnGCTest
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
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|directory
init|=
name|createTempFile
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"test"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|Whiteboard
name|whiteboard
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
name|gcMonitor
operator|=
operator|new
name|GCMonitorTracker
argument_list|()
expr_stmt|;
name|gcMonitor
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|CompactionStrategy
name|strategy
init|=
operator|new
name|CompactionStrategy
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|CLEAN_NONE
argument_list|,
literal|0
argument_list|,
name|CompactionStrategy
operator|.
name|MEMORY_THRESHOLD_DEFAULT
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
name|setHead
operator|.
name|call
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|strategy
operator|.
name|setPersistCompactionMap
argument_list|(
name|usePersistedMap
argument_list|)
expr_stmt|;
name|fileStore
operator|=
name|newFileStore
argument_list|(
name|directory
argument_list|)
operator|.
name|withGCMonitor
argument_list|(
name|gcMonitor
argument_list|)
operator|.
name|create
argument_list|()
operator|.
name|setCompactionStrategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
name|NodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|repository
operator|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|gcMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compactionCausesRefresh
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"one"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|addNode
argument_list|(
name|repository
argument_list|,
literal|"two"
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|compact
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|hasNode
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Node two must be visible as compaction should cause the session to refresh"
argument_list|,
name|root
operator|.
name|hasNode
argument_list|(
literal|"two"
argument_list|)
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
specifier|static
name|void
name|addNode
parameter_list|(
specifier|final
name|Repository
name|repository
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|// Execute on different threads to ensure same thread session
comment|// refreshing doesn't come into our way
name|run
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
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
name|name
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
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|run
parameter_list|(
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|FutureTask
argument_list|<
name|Void
argument_list|>
name|task
init|=
operator|new
name|FutureTask
argument_list|<
name|Void
argument_list|>
argument_list|(
name|callable
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
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

