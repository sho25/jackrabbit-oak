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
name|commons
operator|.
name|jmx
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|listeningDecorator
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|currentThread
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|sleep
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newCachedThreadPool
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|FAILED
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|RUNNING
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
name|api
operator|.
name|jmx
operator|.
name|RepositoryManagementMBean
operator|.
name|StatusCode
operator|.
name|SUCCEEDED
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
name|jmx
operator|.
name|ManagementOperation
operator|.
name|newManagementOperation
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
name|LinkedBlockingDeque
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
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|ListeningExecutorService
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
name|jmx
operator|.
name|ManagementOperation
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
name|jmx
operator|.
name|ManagementOperation
operator|.
name|Status
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

begin_class
specifier|public
class|class
name|ManagementOperationTest
block|{
specifier|private
name|ListeningExecutorService
name|executor
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|executor
operator|=
name|listeningDecorator
argument_list|(
name|newCachedThreadPool
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|notStarted
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|ManagementOperation
argument_list|<
name|Integer
argument_list|>
name|op
init|=
name|ManagementOperation
operator|.
name|done
argument_list|(
literal|"test"
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|op
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
operator|(
name|int
operator|)
name|op
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|sameThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|succeeded
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|ManagementOperation
argument_list|<
name|Long
argument_list|>
name|op
init|=
name|newManagementOperation
argument_list|(
literal|"test"
argument_list|,
operator|new
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|42L
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42L
argument_list|,
operator|(
name|long
operator|)
name|op
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|op
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|Status
name|status
init|=
name|op
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SUCCEEDED
argument_list|,
name|status
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|failed
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
specifier|final
name|Exception
name|failure
init|=
operator|new
name|Exception
argument_list|(
literal|"fail"
argument_list|)
decl_stmt|;
name|ManagementOperation
argument_list|<
name|Void
argument_list|>
name|op
init|=
name|newManagementOperation
argument_list|(
literal|"test"
argument_list|,
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
throw|throw
name|failure
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|op
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|op
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected "
operator|+
name|failure
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|failure
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|op
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|Status
name|status
init|=
name|op
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FAILED
argument_list|,
name|status
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test failed: "
operator|+
name|failure
operator|.
name|getMessage
argument_list|()
argument_list|,
name|status
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|running
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|LinkedBlockingDeque
argument_list|<
name|Thread
argument_list|>
name|thread
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<
name|Thread
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ManagementOperation
argument_list|<
name|Void
argument_list|>
name|op
init|=
name|newManagementOperation
argument_list|(
literal|"test"
argument_list|,
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
name|thread
operator|.
name|add
argument_list|(
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|sleep
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|Status
name|status
init|=
name|op
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RUNNING
argument_list|,
name|status
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|poll
argument_list|(
literal|5
argument_list|,
name|SECONDS
argument_list|)
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|op
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected InterruptedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InterruptedException
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|op
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|op
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FAILED
argument_list|,
name|status
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"test failed: "
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cancelled
parameter_list|()
block|{
name|ManagementOperation
argument_list|<
name|Void
argument_list|>
name|op
init|=
name|newManagementOperation
argument_list|(
literal|"test"
argument_list|,
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
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|op
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|op
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|Status
name|status
init|=
name|op
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FAILED
argument_list|,
name|status
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test cancelled"
argument_list|,
name|status
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkConversion
parameter_list|(
name|Status
name|status
parameter_list|)
block|{
name|CompositeData
name|cd
init|=
name|status
operator|.
name|toCompositeData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getCode
argument_list|()
operator|.
name|ordinal
argument_list|()
argument_list|,
name|cd
operator|.
name|get
argument_list|(
literal|"code"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getId
argument_list|()
argument_list|,
name|cd
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cd
operator|.
name|get
argument_list|(
literal|"message"
argument_list|)
argument_list|)
expr_stmt|;
name|Status
name|status2
init|=
name|Status
operator|.
name|fromCompositeData
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|CompositeData
name|cd2
init|=
name|status2
operator|.
name|toCompositeData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|status
argument_list|,
name|status2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cd
argument_list|,
name|cd2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|statusToCompositeDataConversion
parameter_list|()
block|{
name|checkConversion
argument_list|(
name|Status
operator|.
name|unavailable
argument_list|(
literal|"forty two"
argument_list|)
argument_list|)
expr_stmt|;
name|checkConversion
argument_list|(
name|Status
operator|.
name|none
argument_list|(
literal|"forty three"
argument_list|)
argument_list|)
expr_stmt|;
name|checkConversion
argument_list|(
name|Status
operator|.
name|initiated
argument_list|(
literal|"forty four"
argument_list|)
argument_list|)
expr_stmt|;
name|checkConversion
argument_list|(
name|Status
operator|.
name|running
argument_list|(
literal|"forty five"
argument_list|)
argument_list|)
expr_stmt|;
name|checkConversion
argument_list|(
name|Status
operator|.
name|succeeded
argument_list|(
literal|"forty six"
argument_list|)
argument_list|)
expr_stmt|;
name|checkConversion
argument_list|(
name|Status
operator|.
name|failed
argument_list|(
literal|"forty seven"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

