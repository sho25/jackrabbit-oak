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
operator|.
name|observation
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
name|Objects
operator|.
name|equal
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_MOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_REMOVED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PERSIST
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_ADDED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_CHANGED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PROPERTY_REMOVED
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
name|Executors
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
name|Future
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
name|TimeUnit
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
name|Property
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
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|EventListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|collect
operator|.
name|Sets
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
name|ForwardingListenableFuture
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
name|ListenableFuture
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
name|SettableFuture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
name|ObservationTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
specifier|static
specifier|final
name|int
name|ALL_EVENTS
init|=
name|NODE_ADDED
operator||
name|NODE_REMOVED
operator||
name|NODE_MOVED
operator||
name|PROPERTY_ADDED
operator||
name|PROPERTY_REMOVED
operator||
name|PROPERTY_CHANGED
operator||
name|PERSIST
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NODE
init|=
literal|"test_node"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|'/'
operator|+
name|TEST_NODE
decl_stmt|;
specifier|private
name|Session
name|observingSession
decl_stmt|;
specifier|private
name|ObservationManager
name|observationManager
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|TEST_NODE
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|observingSession
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
name|observationManager
operator|=
name|observingSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
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
name|observingSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observation
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|ALL_EVENTS
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|n
init|=
name|getNode
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|setProperty
argument_list|(
literal|"p0"
argument_list|,
literal|"v0"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n1
init|=
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"n1"
argument_list|)
argument_list|)
decl_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n1
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n1
operator|.
name|setProperty
argument_list|(
literal|"p2"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
expr_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|listener
operator|.
name|getUnexpected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|setProperty
argument_list|(
literal|"property"
argument_list|,
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n3
init|=
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"n3"
argument_list|)
argument_list|)
decl_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n3
operator|.
name|setProperty
argument_list|(
literal|"p3"
argument_list|,
literal|"v3"
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectChange
argument_list|(
name|n1
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"v1.1"
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectRemove
argument_list|(
name|n1
operator|.
name|getProperty
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|listener
operator|.
name|expectRemove
argument_list|(
name|n
operator|.
name|getNode
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"{4}"
argument_list|)
argument_list|)
expr_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|missing
operator|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|unexpected
operator|=
name|listener
operator|.
name|getUnexpected
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|observation2
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|ALL_EVENTS
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|n
init|=
name|getNode
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"n1"
argument_list|)
argument_list|)
expr_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|listener
operator|.
name|getUnexpected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|n
operator|.
name|addNode
argument_list|(
literal|"n2"
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|expectRemove
argument_list|(
name|n
operator|.
name|getNode
argument_list|(
literal|"n1"
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|missing
operator|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|unexpected
operator|=
name|listener
operator|.
name|getUnexpected
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|observationOnRootNode
parameter_list|()
throws|throws
name|Exception
block|{
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|PROPERTY_ADDED
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
comment|// add property to root node
name|Node
name|root
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|listener
operator|.
name|expectAdd
argument_list|(
name|root
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|listener
operator|.
name|getUnexpected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathFilter
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|path
init|=
literal|"/events/only/here"
decl_stmt|;
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|listener
operator|.
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
name|path
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|onEvent
parameter_list|(
name|Event
name|event
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|NODE_ADDED
argument_list|,
name|path
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|root
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"events"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"only"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"here"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"at"
argument_list|)
expr_stmt|;
name|root
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|listener
operator|.
name|getUnexpected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathFilterWithTrailingSlash
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|path
init|=
literal|"/events/only/here"
decl_stmt|;
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|listener
operator|.
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
name|path
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|onEvent
parameter_list|(
name|Event
name|event
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|NODE_ADDED
argument_list|,
name|path
operator|+
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|root
init|=
name|getNode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"events"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"only"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"here"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"at"
argument_list|)
expr_stmt|;
name|root
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|listener
operator|.
name|getMissing
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing events: "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|listener
operator|.
name|getUnexpected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected events: "
operator|+
name|unexpected
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|observationDispose
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
specifier|final
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|Expectation
name|hasEvents
init|=
name|listener
operator|.
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
literal|"has events after registering"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expectation
name|noEvents
init|=
name|listener
operator|.
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
literal|"has no more events after unregistering"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|ALL_EVENTS
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Generate events
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
name|int
name|c
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|getNode
argument_list|(
name|TEST_PATH
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"c"
operator|+
name|c
operator|++
argument_list|)
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// Make sure we see the events
name|assertNotNull
argument_list|(
name|hasEvents
operator|.
name|get
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove event listener
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|submit
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
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|noEvents
operator|.
name|enable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|get
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// Make sure we see no more events
name|assertFalse
argument_list|(
name|noEvents
operator|.
name|wait
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observationDisposeFromListener
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
specifier|final
name|ExpectationListener
name|listener
init|=
operator|new
name|ExpectationListener
argument_list|()
decl_stmt|;
name|Expectation
name|unregistered
init|=
name|listener
operator|.
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
literal|"Unregistering listener from event handler should not block"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|onEvent
parameter_list|(
name|Event
name|event
parameter_list|)
throws|throws
name|Exception
block|{
name|observationManager
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|observationManager
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|ALL_EVENTS
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Ensure the listener is there
name|assertTrue
argument_list|(
name|observationManager
operator|.
name|getRegisteredEventListeners
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// Generate events
name|Node
name|n
init|=
name|getNode
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|n
operator|.
name|addNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Make sure we see the events and the listener is gone
name|assertNotNull
argument_list|(
name|unregistered
operator|.
name|get
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|observationManager
operator|.
name|getRegisteredEventListeners
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Node
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getAdminSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< ExpectationListener>---
specifier|private
specifier|static
class|class
name|Expectation
extends|extends
name|ForwardingListenableFuture
argument_list|<
name|Event
argument_list|>
block|{
specifier|private
specifier|final
name|SettableFuture
argument_list|<
name|Event
argument_list|>
name|future
init|=
name|SettableFuture
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
name|Expectation
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
name|Expectation
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ListenableFuture
argument_list|<
name|Event
argument_list|>
name|delegate
parameter_list|()
block|{
return|return
name|future
return|;
block|}
specifier|public
name|void
name|enable
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
specifier|public
name|void
name|complete
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|future
operator|.
name|set
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|fail
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|future
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|wait
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
try|try
block|{
name|future
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|boolean
name|onEvent
parameter_list|(
name|Event
name|event
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ExpectationListener
implements|implements
name|EventListener
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|Expectation
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newCopyOnWriteArraySet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Event
argument_list|>
name|unexpected
init|=
name|Lists
operator|.
name|newCopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|Exception
name|failed
decl_stmt|;
specifier|public
name|Expectation
name|expect
parameter_list|(
name|Expectation
name|expectation
parameter_list|)
block|{
if|if
condition|(
name|failed
operator|==
literal|null
condition|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|expectation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectation
operator|.
name|fail
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
return|return
name|expectation
return|;
block|}
specifier|public
name|Future
argument_list|<
name|Event
argument_list|>
name|expect
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|int
name|type
parameter_list|)
block|{
return|return
name|expect
argument_list|(
operator|new
name|Expectation
argument_list|(
literal|"path = "
operator|+
name|path
operator|+
literal|", type = "
operator|+
name|type
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|onEvent
parameter_list|(
name|Event
name|event
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|type
operator|==
name|event
operator|.
name|getType
argument_list|()
operator|&&
name|equal
argument_list|(
name|path
argument_list|,
name|event
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Node
name|expectAdd
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|expect
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|NODE_ADDED
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
operator|+
literal|"/jcr:primaryType"
argument_list|,
name|PROPERTY_ADDED
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|expectRemove
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|expect
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|NODE_REMOVED
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
operator|+
literal|"/jcr:primaryType"
argument_list|,
name|PROPERTY_REMOVED
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Property
name|expectAdd
parameter_list|(
name|Property
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|expect
argument_list|(
name|property
operator|.
name|getPath
argument_list|()
argument_list|,
name|PROPERTY_ADDED
argument_list|)
expr_stmt|;
return|return
name|property
return|;
block|}
specifier|public
name|Property
name|expectRemove
parameter_list|(
name|Property
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|expect
argument_list|(
name|property
operator|.
name|getPath
argument_list|()
argument_list|,
name|PROPERTY_REMOVED
argument_list|)
expr_stmt|;
return|return
name|property
return|;
block|}
specifier|public
name|Property
name|expectChange
parameter_list|(
name|Property
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|expect
argument_list|(
name|property
operator|.
name|getPath
argument_list|()
argument_list|,
name|PROPERTY_CHANGED
argument_list|)
expr_stmt|;
return|return
name|property
return|;
block|}
specifier|public
name|List
argument_list|<
name|Expectation
argument_list|>
name|getMissing
parameter_list|(
name|int
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|Expectation
argument_list|>
name|missing
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|Futures
operator|.
name|allAsList
argument_list|(
name|expected
argument_list|)
operator|.
name|get
argument_list|(
name|time
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
for|for
control|(
name|Expectation
name|exp
range|:
name|expected
control|)
block|{
if|if
condition|(
operator|!
name|exp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|missing
operator|.
name|add
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|missing
return|;
block|}
specifier|public
name|List
argument_list|<
name|Event
argument_list|>
name|getUnexpected
parameter_list|()
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|unexpected
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onEvent
parameter_list|(
name|EventIterator
name|events
parameter_list|)
block|{
try|try
block|{
while|while
condition|(
name|events
operator|.
name|hasNext
argument_list|()
operator|&&
name|failed
operator|==
literal|null
condition|)
block|{
name|Event
name|event
init|=
name|events
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Expectation
name|exp
range|:
name|expected
control|)
block|{
if|if
condition|(
name|exp
operator|.
name|isEnabled
argument_list|()
operator|&&
name|exp
operator|.
name|onEvent
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|expected
operator|.
name|remove
argument_list|(
name|exp
argument_list|)
expr_stmt|;
name|exp
operator|.
name|complete
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|unexpected
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
for|for
control|(
name|Expectation
name|exp
range|:
name|expected
control|)
block|{
name|exp
operator|.
name|fail
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|failed
operator|=
name|e
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|key
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|type
parameter_list|)
block|{
return|return
name|path
operator|+
literal|':'
operator|+
name|type
return|;
block|}
block|}
block|}
end_class

end_unit

