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
name|run
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|base
operator|.
name|Function
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
name|Joiner
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
name|Splitter
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
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
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
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|FileFilterUtils
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
name|Blob
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
name|PropertyState
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
name|Type
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
name|FileIOUtils
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
name|FileIOUtils
operator|.
name|BurnOnCloseFileIterator
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
name|commons
operator|.
name|concurrent
operator|.
name|ExecutorCloser
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
name|sort
operator|.
name|EscapeUtils
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
name|blob
operator|.
name|BlobReferenceRetriever
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
name|blob
operator|.
name|MarkSweepGarbageCollector
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
name|blob
operator|.
name|ReferenceCollector
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
name|DocumentBlobReferenceRetriever
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
name|DocumentNodeStore
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
name|run
operator|.
name|cli
operator|.
name|BlobStoreOptions
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
name|run
operator|.
name|cli
operator|.
name|CommonOptions
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixture
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixtureProvider
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
name|run
operator|.
name|cli
operator|.
name|Options
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|run
operator|.
name|commons
operator|.
name|LoggingInitializer
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
name|segment
operator|.
name|SegmentBlobReferenceRetriever
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
name|blob
operator|.
name|GarbageCollectableBlobStore
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
name|cluster
operator|.
name|ClusterRepositoryInfo
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
name|ChildNodeEntry
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
name|NodeState
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|Charsets
operator|.
name|UTF_8
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|StandardSystemProperty
operator|.
name|FILE_SEPARATOR
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
name|run
operator|.
name|cli
operator|.
name|BlobStoreOptions
operator|.
name|Type
operator|.
name|AZURE
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
name|run
operator|.
name|cli
operator|.
name|BlobStoreOptions
operator|.
name|Type
operator|.
name|FAKE
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
name|run
operator|.
name|cli
operator|.
name|BlobStoreOptions
operator|.
name|Type
operator|.
name|FDS
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
name|run
operator|.
name|cli
operator|.
name|BlobStoreOptions
operator|.
name|Type
operator|.
name|S3
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
name|getService
import|;
end_import

begin_comment
comment|/**  * Command to check data store consistency and also optionally retrieve ids  * and references.  */
end_comment

begin_class
specifier|public
class|class
name|DataStoreCommand
implements|implements
name|Command
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataStoreCommand
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"datastore"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|summary
init|=
literal|"Provides DataStore management operations"
decl_stmt|;
specifier|private
name|Options
name|opts
decl_stmt|;
specifier|private
name|DataStoreOptions
name|dataStoreOpts
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|opts
operator|=
operator|new
name|Options
argument_list|()
expr_stmt|;
name|opts
operator|.
name|setCommandName
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setSummary
argument_list|(
name|summary
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setConnectionString
argument_list|(
name|CommonOptions
operator|.
name|DEFAULT_CONNECTION_STRING
argument_list|)
expr_stmt|;
name|opts
operator|.
name|registerOptionsFactory
argument_list|(
name|DataStoreOptions
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
name|opts
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|dataStoreOpts
operator|=
name|opts
operator|.
name|getOptionBean
argument_list|(
name|DataStoreOptions
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Clean up before setting up NodeStore as the temp
comment|//directory might be used by NodeStore for cache stuff like persistentCache
name|setupDirectories
argument_list|(
name|dataStoreOpts
argument_list|)
expr_stmt|;
name|setupLogging
argument_list|(
name|dataStoreOpts
argument_list|)
expr_stmt|;
name|logCliArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
init|)
block|{
name|NodeStoreFixture
name|fixture
init|=
name|NodeStoreFixtureProvider
operator|.
name|create
argument_list|(
name|opts
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checkParameters
argument_list|(
name|dataStoreOpts
argument_list|,
name|opts
argument_list|,
name|fixture
argument_list|,
name|parser
argument_list|)
condition|)
block|{
return|return;
block|}
name|execute
argument_list|(
name|fixture
argument_list|,
name|dataStoreOpts
argument_list|,
name|opts
argument_list|,
name|closer
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error occurred while performing datastore operation"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shutdownLogging
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|checkParameters
parameter_list|(
name|DataStoreOptions
name|dataStoreOpts
parameter_list|,
name|Options
name|opts
parameter_list|,
name|NodeStoreFixture
name|fixture
parameter_list|,
name|OptionParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dataStoreOpts
operator|.
name|anyActionSelected
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No actions specified"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|fixture
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No NodeStore specified"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|opts
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isDocument
argument_list|()
operator|&&
name|fixture
operator|.
name|getBlobStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No BlobStore specified"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|execute
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|,
name|DataStoreOptions
name|dataStoreOpts
parameter_list|,
name|Options
name|opts
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|Exception
block|{
name|MarkSweepGarbageCollector
name|collector
init|=
name|getCollector
argument_list|(
name|fixture
argument_list|,
name|dataStoreOpts
argument_list|,
name|opts
argument_list|,
name|closer
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataStoreOpts
operator|.
name|checkConsistency
argument_list|()
condition|)
block|{
name|long
name|missing
init|=
name|collector
operator|.
name|checkConsistency
argument_list|()
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Found {} missing blobs"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataStoreOpts
operator|.
name|isVerbose
argument_list|()
condition|)
block|{
operator|new
name|VerboseIdLogger
argument_list|(
name|opts
argument_list|)
operator|.
name|log
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|dataStoreOpts
operator|.
name|collectGarbage
argument_list|()
condition|)
block|{
name|collector
operator|.
name|collectGarbage
argument_list|(
name|dataStoreOpts
operator|.
name|markOnly
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|setupDirectories
parameter_list|(
name|DataStoreOptions
name|opts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|opts
operator|.
name|getOutDir
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|opts
operator|.
name|getOutDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|opts
operator|.
name|getWorkDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|MarkSweepGarbageCollector
name|getCollector
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|,
name|DataStoreOptions
name|dataStoreOpts
parameter_list|,
name|Options
name|opts
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|BlobReferenceRetriever
name|retriever
decl_stmt|;
if|if
condition|(
name|opts
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isDocument
argument_list|()
condition|)
block|{
name|retriever
operator|=
operator|new
name|DocumentBlobReferenceRetriever
argument_list|(
operator|(
name|DocumentNodeStore
operator|)
name|fixture
operator|.
name|getStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|dataStoreOpts
operator|.
name|isVerbose
argument_list|()
condition|)
block|{
name|retriever
operator|=
operator|new
name|NodeTraverserReferenceRetriever
argument_list|(
name|fixture
operator|.
name|getStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileStore
name|fileStore
init|=
name|getService
argument_list|(
name|fixture
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|FileStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|retriever
operator|=
operator|new
name|SegmentBlobReferenceRetriever
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
block|}
block|}
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|service
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|repositoryId
init|=
name|ClusterRepositoryInfo
operator|.
name|getId
argument_list|(
name|fixture
operator|.
name|getStore
argument_list|()
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|repositoryId
argument_list|)
expr_stmt|;
name|MarkSweepGarbageCollector
name|collector
init|=
operator|new
name|MarkSweepGarbageCollector
argument_list|(
name|retriever
argument_list|,
operator|(
name|GarbageCollectableBlobStore
operator|)
name|fixture
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|service
argument_list|,
name|dataStoreOpts
operator|.
name|getOutDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|dataStoreOpts
operator|.
name|getBatchCount
argument_list|()
argument_list|,
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|dataStoreOpts
operator|.
name|getBlobGcMaxAgeInSecs
argument_list|()
argument_list|)
argument_list|,
name|repositoryId
argument_list|,
name|fixture
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|getService
argument_list|(
name|fixture
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|StatisticsProvider
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|collector
operator|.
name|setTraceOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|collector
return|;
block|}
specifier|protected
specifier|static
name|void
name|setupLogging
parameter_list|(
name|DataStoreOptions
name|dataStoreOpts
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|LoggingInitializer
argument_list|(
name|dataStoreOpts
operator|.
name|getWorkDir
argument_list|()
argument_list|,
name|NAME
argument_list|,
name|dataStoreOpts
operator|.
name|isResetLoggingConfig
argument_list|()
argument_list|)
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|shutdownLogging
parameter_list|()
block|{
name|LoggingInitializer
operator|.
name|shutdownLogging
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|logCliArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Command line arguments used for datastore command [{}]"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|inputArgs
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|.
name|getInputArguments
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|inputArgs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"System properties and vm options passed {}"
argument_list|,
name|inputArgs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|DataStoreCommand
name|cmd
init|=
operator|new
name|DataStoreCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|execute
argument_list|(
literal|"--fds"
argument_list|,
literal|"/Users/amjain/installs/org.apache.jackrabbit.oak.plugins.blob.datastore.FileDataStore.config"
argument_list|,
literal|"--read-write"
argument_list|,
literal|"/Users/amjain/installs/cq640/crx-quickstart/repository/segmentstore"
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@link BlobReferenceRetriever} instance which iterates over the whole node store to find      * blobs being referred. Useful when path of those blobs needed and the underlying {@link NodeStore}      * native implementation does not provide that.      */
specifier|static
class|class
name|NodeTraverserReferenceRetriever
implements|implements
name|BlobReferenceRetriever
block|{
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|public
name|NodeTraverserReferenceRetriever
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
specifier|private
name|void
name|binaryProperties
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|,
name|ReferenceCollector
name|collector
parameter_list|)
block|{
for|for
control|(
name|PropertyState
name|p
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|propPath
init|=
name|path
decl_stmt|;
comment|//PathUtils.concat(path, p.getName());
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
name|String
name|blobId
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobId
operator|!=
literal|null
condition|)
block|{
name|collector
operator|.
name|addReference
argument_list|(
name|blobId
argument_list|,
name|propPath
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARIES
operator|&&
name|p
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Iterator
argument_list|<
name|Blob
argument_list|>
name|iterator
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|blobId
init|=
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobId
operator|!=
literal|null
condition|)
block|{
name|collector
operator|.
name|addReference
argument_list|(
name|blobId
argument_list|,
name|propPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|traverseChildren
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|,
name|ReferenceCollector
name|collector
parameter_list|)
block|{
name|binaryProperties
argument_list|(
name|state
argument_list|,
name|path
argument_list|,
name|collector
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|c
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|traverseChildren
argument_list|(
name|c
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectReferences
parameter_list|(
name|ReferenceCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting dump of blob references by traversing"
argument_list|)
expr_stmt|;
name|traverseChildren
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|VerboseIdLogger
block|{
specifier|static
specifier|final
name|String
name|DELIM
init|=
literal|","
decl_stmt|;
specifier|static
specifier|final
name|String
name|DASH
init|=
literal|"-"
decl_stmt|;
specifier|static
specifier|final
name|String
name|HASH
init|=
literal|"#"
decl_stmt|;
specifier|static
specifier|final
name|Comparator
argument_list|<
name|String
argument_list|>
name|idComparator
init|=
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
return|return
name|s1
operator|.
name|split
argument_list|(
name|DELIM
argument_list|)
index|[
literal|0
index|]
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|split
argument_list|(
name|DELIM
argument_list|)
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Joiner
name|delimJoiner
init|=
name|Joiner
operator|.
name|on
argument_list|(
name|DELIM
argument_list|)
operator|.
name|skipNulls
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Splitter
name|delimSplitter
init|=
name|Splitter
operator|.
name|on
argument_list|(
name|DELIM
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlobStoreOptions
name|optionBean
decl_stmt|;
specifier|private
specifier|final
name|BlobStoreOptions
operator|.
name|Type
name|blobStoreType
decl_stmt|;
specifier|private
specifier|final
name|File
name|outDir
decl_stmt|;
specifier|private
specifier|final
name|File
name|outFile
decl_stmt|;
specifier|public
name|VerboseIdLogger
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|this
operator|.
name|optionBean
operator|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|BlobStoreOptions
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStoreType
operator|=
name|optionBean
operator|.
name|getBlobStoreType
argument_list|()
expr_stmt|;
name|outDir
operator|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|DataStoreOptions
operator|.
name|class
argument_list|)
operator|.
name|getOutDir
argument_list|()
expr_stmt|;
name|outFile
operator|=
name|filterFiles
argument_list|(
name|outDir
argument_list|,
literal|"gccand-"
argument_list|)
expr_stmt|;
if|if
condition|(
name|outFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No candidate file found"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nullable
specifier|static
name|File
name|filterFiles
parameter_list|(
name|File
name|outDir
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|subDirs
init|=
name|FileFilterUtils
operator|.
name|filterList
argument_list|(
name|FileFilterUtils
operator|.
name|and
argument_list|(
name|FileFilterUtils
operator|.
name|prefixFileFilter
argument_list|(
literal|"gcworkdir-"
argument_list|)
argument_list|,
name|FileFilterUtils
operator|.
name|directoryFileFilter
argument_list|()
argument_list|)
argument_list|,
name|outDir
operator|.
name|listFiles
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subDirs
operator|!=
literal|null
operator|&&
operator|!
name|subDirs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|File
name|workDir
init|=
name|subDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|outFiles
init|=
name|FileFilterUtils
operator|.
name|filterList
argument_list|(
name|FileFilterUtils
operator|.
name|prefixFileFilter
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|workDir
operator|.
name|listFiles
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|outFiles
operator|!=
literal|null
operator|&&
operator|!
name|outFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|outFiles
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|String
name|encodeId
parameter_list|(
name|String
name|line
parameter_list|,
name|BlobStoreOptions
operator|.
name|Type
name|dsType
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|delimSplitter
operator|.
name|splitToList
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|idLengthSepList
init|=
name|Splitter
operator|.
name|on
argument_list|(
name|HASH
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|splitToList
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|String
name|blobId
init|=
name|idLengthSepList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|dsType
operator|==
name|FAKE
operator|||
name|dsType
operator|==
name|FDS
condition|)
block|{
name|blobId
operator|=
operator|(
name|blobId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
operator|+
name|FILE_SEPARATOR
operator|.
name|value
argument_list|()
operator|+
name|blobId
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
operator|+
name|FILE_SEPARATOR
operator|.
name|value
argument_list|()
operator|+
name|blobId
operator|.
name|substring
argument_list|(
literal|4
argument_list|,
literal|6
argument_list|)
operator|+
name|FILE_SEPARATOR
operator|.
name|value
argument_list|()
operator|+
name|blobId
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dsType
operator|==
name|S3
operator|||
name|dsType
operator|==
name|AZURE
condition|)
block|{
name|blobId
operator|=
operator|(
name|blobId
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
operator|+
name|DASH
operator|+
name|blobId
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|delimJoiner
operator|.
name|join
argument_list|(
name|blobId
argument_list|,
name|EscapeUtils
operator|.
name|unescapeLineBreaks
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|log
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|outFile
operator|.
name|getName
argument_list|()
operator|+
literal|"-temp"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|moveFile
argument_list|(
name|outFile
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
try|try
init|(
name|BurnOnCloseFileIterator
name|iterator
init|=
operator|new
name|BurnOnCloseFileIterator
argument_list|(
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|tempFile
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|tempFile
argument_list|,
operator|(
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|input
lambda|->
name|encodeId
argument_list|(
name|input
argument_list|,
name|blobStoreType
argument_list|)
argument_list|)
init|)
block|{
name|FileIOUtils
operator|.
name|writeStrings
argument_list|(
name|iterator
argument_list|,
name|outFile
argument_list|,
literal|true
argument_list|,
name|log
argument_list|,
literal|"Transformed to verbose ids - "
argument_list|)
block|;             }
block|}
block|}
block|}
end_class

end_unit

