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
name|jcr
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
name|commons
operator|.
name|JcrUtils
operator|.
name|in
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Iterator
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
name|atomic
operator|.
name|AtomicInteger
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
name|Iterators
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

begin_comment
comment|/**  *<code>ConcurrentAddIT</code> adds nodes with multiple sessions in separate  * locations of the repository and under the same parent.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
comment|// OAK-2704
specifier|public
class|class
name|ConcurrentAddIT
extends|extends
name|AbstractRepositoryTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WORKERS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NODES_PER_WORKER
init|=
literal|100
decl_stmt|;
specifier|public
name|ConcurrentAddIT
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
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|addNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|test
init|=
name|getAdminSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|worker
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
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
name|NUM_WORKERS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|test
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|worker
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Worker
argument_list|(
name|createAdminSession
argument_list|()
argument_list|,
name|path
argument_list|,
name|exceptions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|worker
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|worker
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getAdminSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|n
range|:
name|in
argument_list|(
operator|(
name|Iterator
argument_list|<
name|Node
argument_list|>
operator|)
name|test
operator|.
name|getNodes
argument_list|()
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|NODES_PER_WORKER
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|n
operator|.
name|getNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|addNodesSameParent
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// use nt:unstructured to force conflicts on :childOrder property
name|Node
name|test
init|=
name|getAdminSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|worker
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
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
name|NUM_WORKERS
condition|;
name|i
operator|++
control|)
block|{
name|worker
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Worker
argument_list|(
name|createAdminSession
argument_list|()
argument_list|,
name|test
operator|.
name|getPath
argument_list|()
argument_list|,
name|exceptions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|worker
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|worker
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getAdminSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODES_PER_WORKER
operator|*
name|NUM_WORKERS
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|test
operator|.
name|getNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Worker
implements|implements
name|Runnable
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|WORKER_ID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Session
name|s
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
init|=
literal|"worker-"
operator|+
name|WORKER_ID
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|Worker
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|s
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|s
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
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
name|NODES_PER_WORKER
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|addNode
argument_list|(
name|id
operator|+
literal|"-node-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

