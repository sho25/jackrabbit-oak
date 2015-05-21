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
operator|.
name|lucene
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
name|ExecutorService
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
name|LinkedBlockingQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|AtomicInteger
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
name|management
operator|.
name|NotCompliantMBeanException
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
name|Strings
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicyOption
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
name|PropertiesUtil
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|IndexEditorProvider
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
name|aggregate
operator|.
name|NodeAggregator
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
name|BackgroundObserver
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
name|score
operator|.
name|ScorerProviderFactory
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
name|whiteboard
operator|.
name|Registration
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
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|CharFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenizerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|InfoStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
operator|.
name|registerMBean
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak LuceneIndexProvider"
argument_list|)
specifier|public
class|class
name|LuceneIndexProviderService
block|{
specifier|public
specifier|static
specifier|final
name|String
name|REPOSITORY_HOME
init|=
literal|"repository.home"
decl_stmt|;
specifier|private
name|LuceneIndexProvider
name|indexProvider
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ServiceRegistration
argument_list|>
name|regs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Registration
argument_list|>
name|oakRegs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_UNARY
argument_list|,
name|policyOption
operator|=
name|ReferencePolicyOption
operator|.
name|GREEDY
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|)
specifier|private
name|NodeAggregator
name|nodeAggregator
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|false
argument_list|,
name|label
operator|=
literal|"Enable Debug Logging"
argument_list|,
name|description
operator|=
literal|"Enables debug logging in Lucene. After enabling this actual logging can be "
operator|+
literal|"controlled via changing log level for category 'oak.lucene' to debug"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_DEBUG
init|=
literal|"debug"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Enable CopyOnRead"
argument_list|,
name|description
operator|=
literal|"Enable copying of Lucene index to local file system to improve query performance"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_COPY_ON_READ
init|=
literal|"enableCopyOnReadSupport"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Local index storage path"
argument_list|,
name|description
operator|=
literal|"Local file system path where Lucene indexes would be copied when CopyOnRead is enabled. "
operator|+
literal|"If not specified then indexes would be stored under 'index' dir under Repository Home"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_LOCAL_INDEX_DIR
init|=
literal|"localIndexDir"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|false
argument_list|,
name|label
operator|=
literal|"Enable CopyOnWrite"
argument_list|,
name|description
operator|=
literal|"Enable copying of Lucene index to local file system to improve index writer performance"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_COPY_ON_WRITE
init|=
literal|"enableCopyOnWriteSupport"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Open index asynchronously"
argument_list|,
name|description
operator|=
literal|"Enable opening of indexes in asynchronous mode"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ASYNC_INDEX_OPEN
init|=
literal|"enableOpenIndexAsync"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PROP_THREAD_POOL_SIZE_DEFAULT
init|=
literal|5
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|PROP_THREAD_POOL_SIZE_DEFAULT
argument_list|,
name|label
operator|=
literal|"Thread pool size"
argument_list|,
name|description
operator|=
literal|"Thread pool size used to perform various asynchronous task in Oak Lucene"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_THREAD_POOL_SIZE
init|=
literal|"threadPoolSize"
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|BackgroundObserver
name|backgroundObserver
decl_stmt|;
annotation|@
name|Reference
name|ScorerProviderFactory
name|scorerFactory
decl_stmt|;
specifier|private
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
name|File
name|indexDir
decl_stmt|;
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
specifier|private
name|int
name|threadPoolSize
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|NotCompliantMBeanException
throws|,
name|IOException
block|{
name|initializeFactoryClassLoaders
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|whiteboard
operator|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|threadPoolSize
operator|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_THREAD_POOL_SIZE
argument_list|)
argument_list|,
name|PROP_THREAD_POOL_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|indexProvider
operator|=
operator|new
name|LuceneIndexProvider
argument_list|(
name|createTracker
argument_list|(
name|bundleContext
argument_list|,
name|config
argument_list|)
argument_list|,
name|scorerFactory
argument_list|)
expr_stmt|;
name|initializeLogging
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
name|regs
operator|.
name|add
argument_list|(
name|bundleContext
operator|.
name|registerService
argument_list|(
name|QueryIndexProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|indexProvider
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|registerObserver
argument_list|(
name|bundleContext
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|registerIndexEditor
argument_list|(
name|bundleContext
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|oakRegs
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|LuceneIndexMBean
operator|.
name|class
argument_list|,
operator|new
name|LuceneIndexMBeanImpl
argument_list|(
name|indexProvider
operator|.
name|getTracker
argument_list|()
argument_list|)
argument_list|,
name|LuceneIndexMBean
operator|.
name|TYPE
argument_list|,
literal|"Lucene Index statistics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|ServiceRegistration
name|reg
range|:
name|regs
control|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Registration
name|reg
range|:
name|oakRegs
control|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|backgroundObserver
operator|!=
literal|null
condition|)
block|{
name|backgroundObserver
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|indexProvider
operator|!=
literal|null
condition|)
block|{
name|indexProvider
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexProvider
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
name|InfoStream
operator|.
name|setDefault
argument_list|(
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initialize
parameter_list|()
block|{
if|if
condition|(
name|indexProvider
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|nodeAggregator
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Using NodeAggregator {}"
argument_list|,
name|nodeAggregator
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexProvider
operator|.
name|setAggregator
argument_list|(
name|nodeAggregator
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initializeLogging
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|boolean
name|debug
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_DEBUG
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|InfoStream
operator|.
name|setDefault
argument_list|(
name|LoggingInfoStream
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Registered LoggingInfoStream with Lucene. Lucene logs can be enabled "
operator|+
literal|"now via category [{}]"
argument_list|,
name|LoggingInfoStream
operator|.
name|PREFIX
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|registerIndexEditor
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|enableCopyOnWrite
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_COPY_ON_WRITE
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
decl_stmt|;
if|if
condition|(
name|enableCopyOnWrite
condition|)
block|{
name|initializeIndexCopier
argument_list|(
name|bundleContext
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|editorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|indexCopier
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Enabling CopyOnWrite support. Index files would be copied under {}"
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|editorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|()
expr_stmt|;
block|}
name|regs
operator|.
name|add
argument_list|(
name|bundleContext
operator|.
name|registerService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|editorProvider
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexTracker
name|createTracker
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|enableCopyOnRead
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_COPY_ON_READ
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableCopyOnRead
condition|)
block|{
name|initializeIndexCopier
argument_list|(
name|bundleContext
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Enabling CopyOnRead support. Index files would be copied under {}"
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexTracker
argument_list|(
name|indexCopier
argument_list|)
return|;
block|}
return|return
operator|new
name|IndexTracker
argument_list|()
return|;
block|}
specifier|private
name|void
name|initializeIndexCopier
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexCopier
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|indexDirPath
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_LOCAL_INDEX_DIR
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|indexDirPath
argument_list|)
condition|)
block|{
name|String
name|repoHome
init|=
name|bundleContext
operator|.
name|getProperty
argument_list|(
name|REPOSITORY_HOME
argument_list|)
decl_stmt|;
if|if
condition|(
name|repoHome
operator|!=
literal|null
condition|)
block|{
name|indexDirPath
operator|=
name|FilenameUtils
operator|.
name|concat
argument_list|(
name|repoHome
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
block|}
block|}
name|checkNotNull
argument_list|(
name|indexDirPath
argument_list|,
literal|"Index directory cannot be determined as neither index "
operator|+
literal|"directory path [%s] nor repository home [%s] defined"
argument_list|,
name|PROP_LOCAL_INDEX_DIR
argument_list|,
name|REPOSITORY_HOME
argument_list|)
expr_stmt|;
name|indexDir
operator|=
operator|new
name|File
argument_list|(
name|indexDirPath
argument_list|)
expr_stmt|;
name|indexCopier
operator|=
operator|new
name|IndexCopier
argument_list|(
name|getExecutorService
argument_list|()
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
name|oakRegs
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|CopyOnReadStatsMBean
operator|.
name|class
argument_list|,
name|indexCopier
argument_list|,
name|CopyOnReadStatsMBean
operator|.
name|TYPE
argument_list|,
literal|"IndexCopier support statistics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ExecutorService
name|getExecutorService
parameter_list|()
block|{
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
name|executorService
operator|=
name|createExecutor
argument_list|()
expr_stmt|;
block|}
return|return
name|executorService
return|;
block|}
specifier|private
name|ExecutorService
name|createExecutor
parameter_list|()
block|{
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Thread
operator|.
name|UncaughtExceptionHandler
name|handler
init|=
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred in asynchronous processing "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|createName
argument_list|()
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MIN_PRIORITY
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
specifier|private
name|String
name|createName
parameter_list|()
block|{
return|return
literal|"oak-lucene-"
operator|+
name|counter
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|executor
operator|.
name|setKeepAliveTime
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|executor
return|;
block|}
specifier|private
name|void
name|registerObserver
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|boolean
name|enableAsyncIndexOpen
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_ASYNC_INDEX_OPEN
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Observer
name|observer
init|=
name|indexProvider
decl_stmt|;
if|if
condition|(
name|enableAsyncIndexOpen
condition|)
block|{
name|backgroundObserver
operator|=
operator|new
name|BackgroundObserver
argument_list|(
name|indexProvider
argument_list|,
name|getExecutorService
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|observer
operator|=
name|backgroundObserver
expr_stmt|;
name|oakRegs
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|BackgroundObserverMBean
operator|.
name|class
argument_list|,
name|backgroundObserver
operator|.
name|getMBean
argument_list|()
argument_list|,
name|BackgroundObserverMBean
operator|.
name|TYPE
argument_list|,
literal|"LuceneIndexConfigObserver queue stats"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Registering the LuceneIndexProvider as a BackgroundObserver"
argument_list|)
expr_stmt|;
block|}
name|regs
operator|.
name|add
argument_list|(
name|bundleContext
operator|.
name|registerService
argument_list|(
name|Observer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|observer
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initializeFactoryClassLoaders
parameter_list|(
name|ClassLoader
name|classLoader
parameter_list|)
block|{
name|ClassLoader
name|originalClassLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
comment|//Access TokenizerFactory etc trigger a static initialization
comment|//so switch the TCCL so that static initializer picks up the right
comment|//classloader
name|initializeFactoryClassLoaders0
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while initializing the Lucene "
operator|+
literal|"Factories"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|originalClassLoader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initializeFactoryClassLoaders0
parameter_list|(
name|ClassLoader
name|classLoader
parameter_list|)
block|{
comment|//Factories use the Threads context classloader to perform SPI classes
comment|//lookup by default which would not work in OSGi world. So reload the
comment|//factories by providing the bundle classloader
name|TokenizerFactory
operator|.
name|reloadTokenizers
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
name|CharFilterFactory
operator|.
name|reloadCharFilters
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
name|TokenFilterFactory
operator|.
name|reloadTokenFilters
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|bindNodeAggregator
parameter_list|(
name|NodeAggregator
name|aggregator
parameter_list|)
block|{
name|this
operator|.
name|nodeAggregator
operator|=
name|aggregator
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindNodeAggregator
parameter_list|(
name|NodeAggregator
name|aggregator
parameter_list|)
block|{
name|this
operator|.
name|nodeAggregator
operator|=
literal|null
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

