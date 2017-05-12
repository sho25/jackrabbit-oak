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
name|Executors
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
name|ImmutableMap
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
name|DocumentNodeStateCache
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
name|blob
operator|.
name|BlobStore
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
name|blob
operator|.
name|MemoryBlobStore
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
name|BackgroundObserverMBean
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
name|Observer
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
name|state
operator|.
name|NodeStoreProvider
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
name|stats
operator|.
name|StatisticsProvider
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
name|MockOsgi
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

begin_class
specifier|public
class|class
name|SecondaryStoreCacheServiceTest
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
name|SecondaryStoreCacheService
name|cacheService
init|=
operator|new
name|SecondaryStoreCacheService
argument_list|()
decl_stmt|;
specifier|private
name|NodeStore
name|secondaryStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|configureDefaultServices
parameter_list|()
block|{
name|context
operator|.
name|registerService
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|,
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|NodeStoreProvider
operator|.
name|class
argument_list|,
operator|new
name|NodeStoreProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
name|secondaryStore
return|;
block|}
block|}
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
literal|"role"
argument_list|,
literal|"secondary"
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|Executor
operator|.
name|class
argument_list|,
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|injectServices
argument_list|(
name|cacheService
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|cacheService
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|BackgroundObserverMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|DocumentNodeStateCache
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|cacheService
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|BackgroundObserverMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|DocumentNodeStateCache
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disableBackground
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"enableAsyncObserver"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|cacheService
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|BackgroundObserverMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|DocumentNodeStateCache
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|configurePathFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"includedPaths"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a"
block|}
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|cacheService
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PathFilter
operator|.
name|Result
operator|.
name|INCLUDE
argument_list|,
name|cacheService
operator|.
name|getPathFilter
argument_list|()
operator|.
name|filter
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PathFilter
operator|.
name|Result
operator|.
name|EXCLUDE
argument_list|,
name|cacheService
operator|.
name|getPathFilter
argument_list|()
operator|.
name|filter
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

