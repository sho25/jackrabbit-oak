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
name|osgi
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
name|CountDownLatch
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
name|Executor
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
name|atomic
operator|.
name|AtomicBoolean
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
name|jmx
operator|.
name|RepositoryManagementMBean
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
name|security
operator|.
name|OpenSecurityProvider
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
name|security
operator|.
name|SecurityProvider
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
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
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
name|assertNull
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
name|RepositoryManagerTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|executorSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|registerService
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|,
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|RepositoryManager
argument_list|()
argument_list|)
expr_stmt|;
name|Executor
name|executor
init|=
name|context
operator|.
name|getService
argument_list|(
name|Executor
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Repository initialization should have registered an Executor"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|invoked
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
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
name|invoked
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|invoked
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|repositoryShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|registerService
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|,
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|RepositoryManager
name|mgr
init|=
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|RepositoryManager
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"MBean should be registered"
argument_list|,
name|context
operator|.
name|getService
argument_list|(
name|RepositoryManagementMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|deactivate
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"MBean should have been removed upon repository shutdown"
argument_list|,
name|context
operator|.
name|getService
argument_list|(
name|RepositoryManagementMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

