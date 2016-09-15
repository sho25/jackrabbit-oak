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
name|benchmark
package|;
end_package

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
name|ScheduledExecutorService
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
name|ScheduledThreadPoolExecutor
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|ConsoleReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricFilter
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
name|base
operator|.
name|Predicate
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
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|fixture
operator|.
name|JcrCreator
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
name|OakRepositoryFixture
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
name|RepositoryFixture
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
name|Jcr
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
name|AsyncIndexUpdate
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
name|IndexUtils
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
name|lucene
operator|.
name|IndexCopier
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
name|lucene
operator|.
name|IndexTracker
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
name|lucene
operator|.
name|LuceneIndexEditorProvider
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
name|lucene
operator|.
name|LuceneIndexProvider
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
name|lucene
operator|.
name|hybrid
operator|.
name|DocumentQueue
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
name|lucene
operator|.
name|hybrid
operator|.
name|LocalIndexObserver
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
name|lucene
operator|.
name|hybrid
operator|.
name|NRTIndexFactory
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
name|lucene
operator|.
name|reader
operator|.
name|DefaultIndexReaderFactory
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
name|lucene
operator|.
name|reader
operator|.
name|LuceneIndexReaderFactory
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
name|lucene
operator|.
name|util
operator|.
name|IndexDefinitionBuilder
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
name|metric
operator|.
name|MetricStatisticsProvider
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|query
operator|.
name|QueryIndexProvider
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
name|whiteboard
operator|.
name|Whiteboard
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
name|WhiteboardUtils
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
name|Clock
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
import|;
end_import

begin_class
specifier|public
class|class
name|HybridIndexTest
extends|extends
name|AbstractTest
argument_list|<
name|HybridIndexTest
operator|.
name|TestContext
argument_list|>
block|{
specifier|private
name|ScheduledExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|getExitingScheduledExecutorService
argument_list|(
operator|(
name|ScheduledThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|42
argument_list|)
decl_stmt|;
comment|//fixed seed
specifier|private
name|String
name|indexedPropName
init|=
literal|"foo"
decl_stmt|;
specifier|private
name|int
name|nodesPerIteration
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"nodesPerIteration"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
name|int
name|numOfIndexes
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"numOfIndexes"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|private
name|int
name|refreshDeltaMillis
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"refreshDeltaMillis"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|private
name|int
name|asyncInterval
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"asyncInterval"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|private
name|int
name|queueSize
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"queueSize"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|hybridIndexEnabled
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"hybridIndexEnabled"
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|metricStatsEnabled
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"metricStatsEnabled"
argument_list|)
decl_stmt|;
specifier|private
name|File
name|indexCopierDir
decl_stmt|;
specifier|private
name|IndexCopier
name|copier
decl_stmt|;
specifier|private
name|NRTIndexFactory
name|nrtIndexFactory
decl_stmt|;
specifier|private
name|LuceneIndexProvider
name|luceneIndexProvider
decl_stmt|;
specifier|private
name|LuceneIndexEditorProvider
name|luceneEditorProvider
decl_stmt|;
specifier|private
name|DocumentQueue
name|queue
decl_stmt|;
specifier|private
name|LocalIndexObserver
name|localIndexObserver
decl_stmt|;
specifier|private
name|RepositoryInitializer
name|indexInitializer
init|=
operator|new
name|PropertyIndexInitializer
argument_list|()
decl_stmt|;
specifier|private
name|TestContext
name|defaultContext
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indexedValues
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"STARTING"
argument_list|,
literal|"STARTED"
argument_list|,
literal|"STOPPING"
argument_list|,
literal|"STOPPED"
argument_list|,
literal|"ABORTED"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|workDir
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|MetricStatisticsProvider
name|metricStatsProvider
decl_stmt|;
specifier|public
name|HybridIndexTest
parameter_list|(
name|File
name|workDir
parameter_list|)
block|{
name|this
operator|.
name|workDir
operator|=
name|workDir
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
decl_stmt|;
if|if
condition|(
name|hybridIndexEnabled
condition|)
block|{
name|oak
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
name|asyncInterval
argument_list|)
expr_stmt|;
name|prepareLuceneIndexer
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|jcr
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|luceneIndexProvider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|luceneIndexProvider
argument_list|)
operator|.
name|with
argument_list|(
name|localIndexObserver
argument_list|)
operator|.
name|with
argument_list|(
name|luceneEditorProvider
argument_list|)
expr_stmt|;
name|indexInitializer
operator|=
operator|new
name|LuceneIndexInitializer
argument_list|()
expr_stmt|;
block|}
name|whiteboard
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
name|jcr
operator|.
name|with
argument_list|(
name|indexInitializer
argument_list|)
expr_stmt|;
return|return
name|jcr
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hybridIndexEnabled
condition|)
block|{
name|runAsyncIndex
argument_list|()
expr_stmt|;
block|}
name|defaultContext
operator|=
operator|new
name|TestContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|TestContext
name|prepareThreadExecutionContext
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|TestContext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
name|defaultContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|(
name|TestContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesPerIteration
condition|;
name|i
operator|++
control|)
block|{
name|ctx
operator|.
name|dump
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|indexedPropName
argument_list|,
name|nextIndexedValue
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|disposeThreadExecutionContext
parameter_list|(
name|TestContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|context
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|hybridIndexEnabled
condition|)
block|{
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
name|nrtIndexFactory
operator|.
name|close
argument_list|()
expr_stmt|;
name|dumpStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|indexCopierDir
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|indexCopierDir
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"numOfIndexes: %d, refreshDeltaMillis: %d, asyncInterval: %d, queueSize: %d , "
operator|+
literal|"hybridIndexEnabled: %s, metricStatsEnabled: %s %n"
argument_list|,
name|numOfIndexes
argument_list|,
name|refreshDeltaMillis
argument_list|,
name|asyncInterval
argument_list|,
name|queueSize
argument_list|,
name|hybridIndexEnabled
argument_list|,
name|metricStatsEnabled
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|dumpStats
parameter_list|()
block|{
name|ConsoleReporter
operator|.
name|forRegistry
argument_list|(
name|metricStatsProvider
operator|.
name|getRegistry
argument_list|()
argument_list|)
operator|.
name|outputTo
argument_list|(
name|System
operator|.
name|out
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|MetricFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|name
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"HYBRID"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|report
argument_list|()
expr_stmt|;
block|}
specifier|protected
class|class
name|TestContext
block|{
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|dump
decl_stmt|;
specifier|public
name|TestContext
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dump
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|addNode
argument_list|(
name|nextNodeName
argument_list|()
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dump
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|nextIndexedValue
parameter_list|()
block|{
return|return
name|indexedValues
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|indexedValues
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|prepareLuceneIndexer
parameter_list|(
name|File
name|workDir
parameter_list|)
block|{
try|try
block|{
name|indexCopierDir
operator|=
name|createTemporaryFolderIn
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|copier
operator|=
operator|new
name|IndexCopier
argument_list|(
name|executorService
argument_list|,
name|indexCopierDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|nrtIndexFactory
operator|=
operator|new
name|NRTIndexFactory
argument_list|(
name|copier
argument_list|,
name|Clock
operator|.
name|SIMPLE
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|refreshDeltaMillis
argument_list|)
argument_list|)
expr_stmt|;
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
name|LuceneIndexReaderFactory
name|indexReaderFactory
init|=
operator|new
name|DefaultIndexReaderFactory
argument_list|(
name|mip
argument_list|,
name|copier
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|(
name|indexReaderFactory
argument_list|,
name|nrtIndexFactory
argument_list|)
decl_stmt|;
name|luceneIndexProvider
operator|=
operator|new
name|LuceneIndexProvider
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|luceneEditorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|copier
argument_list|,
name|tracker
argument_list|,
literal|null
argument_list|,
comment|//extractedTextCache
literal|null
argument_list|,
comment|//augmentorFactory
name|mip
argument_list|)
expr_stmt|;
name|StatisticsProvider
name|sp
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
if|if
condition|(
name|metricStatsEnabled
condition|)
block|{
name|metricStatsProvider
operator|=
operator|new
name|MetricStatisticsProvider
argument_list|(
literal|null
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|sp
operator|=
name|metricStatsProvider
expr_stmt|;
block|}
name|queue
operator|=
operator|new
name|DocumentQueue
argument_list|(
name|queueSize
argument_list|,
name|tracker
argument_list|,
name|executorService
argument_list|,
name|sp
argument_list|)
expr_stmt|;
name|localIndexObserver
operator|=
operator|new
name|LocalIndexObserver
argument_list|(
name|queue
argument_list|,
name|sp
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|runAsyncIndex
parameter_list|()
block|{
name|Runnable
name|async
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|whiteboard
argument_list|,
name|Runnable
operator|.
name|class
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Runnable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Runnable
name|input
parameter_list|)
block|{
return|return
name|input
operator|instanceof
name|AsyncIndexUpdate
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|async
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|File
name|createTemporaryFolderIn
parameter_list|(
name|File
name|parentFolder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|createdFolder
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"oak-"
argument_list|,
literal|""
argument_list|,
name|parentFolder
argument_list|)
decl_stmt|;
name|createdFolder
operator|.
name|delete
argument_list|()
expr_stmt|;
name|createdFolder
operator|.
name|mkdir
argument_list|()
expr_stmt|;
return|return
name|createdFolder
return|;
block|}
specifier|private
class|class
name|PropertyIndexInitializer
implements|implements
name|RepositoryInitializer
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|NodeBuilder
name|oakIndex
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|addPropIndexDefn
argument_list|(
name|oakIndex
argument_list|,
name|indexedPropName
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfIndexes
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|addPropIndexDefn
argument_list|(
name|oakIndex
argument_list|,
name|indexedPropName
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addPropIndexDefn
parameter_list|(
name|NodeBuilder
name|parent
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
try|try
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|parent
argument_list|,
name|propName
argument_list|,
literal|false
argument_list|,
name|singleton
argument_list|(
name|propName
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|"property"
argument_list|,
literal|null
argument_list|)
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
specifier|private
class|class
name|LuceneIndexInitializer
implements|implements
name|RepositoryInitializer
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|NodeBuilder
name|oakIndex
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|IndexDefinitionBuilder
name|defnBuilder
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnBuilder
operator|.
name|evaluatePathRestrictions
argument_list|()
expr_stmt|;
name|defnBuilder
operator|.
name|async
argument_list|(
literal|"async"
argument_list|,
literal|"sync"
argument_list|)
expr_stmt|;
name|defnBuilder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
name|indexedPropName
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfIndexes
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|defnBuilder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
name|indexedPropName
operator|+
name|i
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
block|}
name|oakIndex
operator|.
name|setChildNode
argument_list|(
name|indexedPropName
argument_list|,
name|defnBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

