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
name|index
package|;
end_package

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
name|TimeUnit
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
name|ImmutableMap
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
name|ImmutableSet
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
name|IndexStatsMBean
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
name|AsyncIndexUpdateTest
operator|.
name|CommitInfoCollector
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
name|AsyncIndexerService
operator|.
name|AsyncConfig
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|plugins
operator|.
name|observation
operator|.
name|ChangeCollectorProvider
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
name|CommitContext
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
name|commit
operator|.
name|ValidatorProvider
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
name|observation
operator|.
name|ChangeSet
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
name|Clusterable
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
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AsyncIndexerServiceTest
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
specifier|private
name|MemoryNodeStore
name|nodeStore
init|=
operator|new
name|FakeClusterableMemoryNodeStore
argument_list|()
decl_stmt|;
specifier|private
name|AsyncIndexerService
name|service
init|=
operator|new
name|AsyncIndexerService
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|asyncReg
parameter_list|()
throws|throws
name|Exception
block|{
name|injectDefaultServices
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"asyncConfigs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async:5"
block|}
argument_list|)
decl_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
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
name|Runnable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
argument_list|,
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
operator|.
name|getLeaseTimeOut
argument_list|()
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|indexUpdate
init|=
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
decl_stmt|;
name|IndexStatsMBean
name|mbean
init|=
name|context
operator|.
name|getService
argument_list|(
name|IndexStatsMBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|mbean
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
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
name|Runnable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|indexUpdate
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leaseTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|injectDefaultServices
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"asyncConfigs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async:5"
block|}
argument_list|,
literal|"leaseTimeOutMinutes"
argument_list|,
literal|"20"
argument_list|)
decl_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|indexUpdate
init|=
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|,
name|indexUpdate
operator|.
name|getLeaseTimeOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeCollectionEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|injectDefaultServices
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"asyncConfigs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async:5"
block|}
argument_list|)
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|,
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"rootIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testRoot"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
comment|// merge it back in
name|nodeStore
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
expr_stmt|;
name|CommitInfoCollector
name|infoCollector
init|=
operator|new
name|CommitInfoCollector
argument_list|()
decl_stmt|;
name|nodeStore
operator|.
name|addObserver
argument_list|(
name|infoCollector
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|indexUpdate
init|=
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
decl_stmt|;
name|indexUpdate
operator|.
name|run
argument_list|()
expr_stmt|;
name|CommitContext
name|commitContext
init|=
operator|(
name|CommitContext
operator|)
name|infoCollector
operator|.
name|infos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|commitContext
argument_list|)
expr_stmt|;
name|ChangeSet
name|changeSet
init|=
operator|(
name|ChangeSet
operator|)
name|commitContext
operator|.
name|get
argument_list|(
name|ChangeSet
operator|.
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|changeSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonClusterableNodeStoreAndLeaseTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|injectDefaultServices
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"asyncConfigs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async:5"
block|}
argument_list|,
literal|"leaseTimeOutMinutes"
argument_list|,
literal|"20"
argument_list|)
decl_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|indexUpdate
init|=
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|indexUpdate
operator|.
name|getLeaseTimeOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|configParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|AsyncConfig
argument_list|>
name|configs
init|=
name|AsyncIndexerService
operator|.
name|getAsyncConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"async:15"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|configs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|configs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|timeIntervalInSecs
argument_list|)
expr_stmt|;
name|configs
operator|=
name|AsyncIndexerService
operator|.
name|getAsyncConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"async:15"
block|,
literal|"foo-async:23"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo-async"
argument_list|,
name|configs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|23
argument_list|,
name|configs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|timeIntervalInSecs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|corruptIndexTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|injectDefaultServices
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"asyncConfigs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async:5"
block|}
argument_list|,
literal|"failingIndexTimeoutSeconds"
argument_list|,
literal|"43"
argument_list|,
literal|"errorWarnIntervalSeconds"
argument_list|,
literal|"53"
argument_list|)
decl_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|AsyncIndexUpdate
name|indexUpdate
init|=
name|getIndexUpdate
argument_list|(
literal|"async"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|43
argument_list|)
argument_list|,
name|indexUpdate
operator|.
name|getCorruptIndexHandler
argument_list|()
operator|.
name|getCorruptIntervalMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|53
argument_list|)
argument_list|,
name|indexUpdate
operator|.
name|getCorruptIndexHandler
argument_list|()
operator|.
name|getErrorWarnIntervalMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|injectDefaultServices
parameter_list|()
block|{
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
name|context
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|,
name|nodeStore
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|ValidatorProvider
operator|.
name|class
argument_list|,
operator|new
name|ChangeCollectorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|injectServices
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|AsyncIndexUpdate
name|getIndexUpdate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|AsyncIndexUpdate
operator|)
name|context
operator|.
name|getServices
argument_list|(
name|Runnable
operator|.
name|class
argument_list|,
literal|"(oak.async="
operator|+
name|name
operator|+
literal|")"
argument_list|)
index|[
literal|0
index|]
return|;
block|}
specifier|private
specifier|static
class|class
name|FakeClusterableMemoryNodeStore
extends|extends
name|MemoryNodeStore
implements|implements
name|Clusterable
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getInstanceId
parameter_list|()
block|{
return|return
literal|"foo"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getVisibilityToken
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVisible
parameter_list|(
name|String
name|visibilityToken
parameter_list|,
name|long
name|maxWaitMillis
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

