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
name|index
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|felix
operator|.
name|inventory
operator|.
name|Format
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
name|CommitFailedException
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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

begin_class
specifier|public
class|class
name|IndexCommand
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
name|IndexCommand
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
literal|"index"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DEFINITIONS_JSON
init|=
literal|"index-definitions.json"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_INFO_TXT
init|=
literal|"index-info.txt"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_CONSISTENCY_CHECK_TXT
init|=
literal|"index-consistency-check-report.txt"
decl_stmt|;
specifier|private
specifier|final
name|String
name|summary
init|=
literal|"Provides index management related operations"
decl_stmt|;
specifier|private
name|File
name|info
decl_stmt|;
specifier|private
name|File
name|definitions
decl_stmt|;
specifier|private
name|File
name|consistencyCheckReport
decl_stmt|;
specifier|private
name|Options
name|opts
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
name|IndexOptions
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
name|IndexOptions
name|indexOpts
init|=
name|opts
operator|.
name|getOptionBean
argument_list|(
name|IndexOptions
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Clean up before setting up NodeStore as the temp
comment|//directory might be used by NodeStore for cache stuff like persistentCache
name|setupDirectories
argument_list|(
name|indexOpts
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexOpts
operator|.
name|isReindex
argument_list|()
operator|&&
name|opts
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isReadWrite
argument_list|()
condition|)
block|{
name|performReindexInReadWriteMode
argument_list|(
name|indexOpts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|execute
argument_list|(
name|fixture
argument_list|,
name|indexOpts
argument_list|,
name|closer
argument_list|)
expr_stmt|;
name|tellReportPaths
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|tellReportPaths
parameter_list|()
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Index stats stored at %s%n"
argument_list|,
name|getPath
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|definitions
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Index definitions stored at %s%n"
argument_list|,
name|getPath
argument_list|(
name|definitions
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|consistencyCheckReport
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Index consistency check report stored at %s%n"
argument_list|,
name|getPath
argument_list|(
name|consistencyCheckReport
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|execute
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|,
name|IndexOptions
name|indexOpts
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|IndexHelper
name|indexHelper
init|=
name|createIndexHelper
argument_list|(
name|fixture
argument_list|,
name|indexOpts
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|dumpIndexStats
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|dumpIndexDefinitions
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|performConsistencyCheck
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|dumpIndexContents
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|reindexIndex
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|importIndex
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|IndexHelper
name|createIndexHelper
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|,
name|IndexOptions
name|indexOpts
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexHelper
name|indexHelper
init|=
operator|new
name|IndexHelper
argument_list|(
name|fixture
operator|.
name|getStore
argument_list|()
argument_list|,
name|fixture
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|fixture
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|indexOpts
operator|.
name|getOutDir
argument_list|()
argument_list|,
name|indexOpts
operator|.
name|getWorkDir
argument_list|()
argument_list|,
name|indexOpts
operator|.
name|getIndexPaths
argument_list|()
argument_list|)
decl_stmt|;
name|configurePreExtractionSupport
argument_list|(
name|indexOpts
argument_list|,
name|indexHelper
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|indexHelper
argument_list|)
expr_stmt|;
return|return
name|indexHelper
return|;
block|}
specifier|private
name|void
name|configurePreExtractionSupport
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|preExtractedTextDir
init|=
name|indexOpts
operator|.
name|getPreExtractedTextDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|preExtractedTextDir
operator|!=
literal|null
condition|)
block|{
name|indexHelper
operator|.
name|setPreExtractedTextDir
argument_list|(
name|preExtractedTextDir
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Using pre-extracted text directory {}"
argument_list|,
name|getPath
argument_list|(
name|preExtractedTextDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|reindexIndex
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|indexOpts
operator|.
name|isReindex
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|checkpoint
init|=
name|indexOpts
operator|.
name|getCheckpoint
argument_list|()
decl_stmt|;
name|reindex
argument_list|(
name|indexHelper
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reindex
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|,
name|String
name|checkpoint
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|,
literal|"Checkpoint value is required for reindexing done in read only mode"
argument_list|)
expr_stmt|;
try|try
init|(
name|OutOfBandIndexer
name|indexer
init|=
operator|new
name|OutOfBandIndexer
argument_list|(
name|indexHelper
argument_list|,
name|checkpoint
argument_list|)
init|)
block|{
name|indexer
operator|.
name|reindex
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|importIndex
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
if|if
condition|(
name|indexOpts
operator|.
name|isImportIndex
argument_list|()
condition|)
block|{
name|File
name|importDir
init|=
name|indexOpts
operator|.
name|getIndexImportDir
argument_list|()
decl_stmt|;
name|importIndex
argument_list|(
name|indexHelper
argument_list|,
name|importDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|importIndex
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|,
name|File
name|importDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
operator|new
name|IndexImporterSupport
argument_list|(
name|indexHelper
argument_list|)
operator|.
name|importIndex
argument_list|(
name|importDir
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|performReindexInReadWriteMode
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|)
throws|throws
name|Exception
block|{
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
comment|//TODO To support restart we need to store this checkpoint somewhere
name|String
name|checkpoint
init|=
name|connectInReadWriteModeAndCreateCheckPoint
argument_list|(
name|indexOpts
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created checkpoint [{}] for indexing"
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to reindex with read only access to NodeStore"
argument_list|)
expr_stmt|;
name|File
name|indexDir
init|=
name|performReindexInReadOnlyMode
argument_list|(
name|indexOpts
argument_list|,
name|checkpoint
argument_list|)
decl_stmt|;
name|Stopwatch
name|importWatch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to import index data from [{}] by connecting to NodeStore in read-write mode"
argument_list|,
name|getPath
argument_list|(
name|indexDir
argument_list|)
argument_list|)
expr_stmt|;
name|connectInReadWriteModeAndImportIndex
argument_list|(
name|indexOpts
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Indexes imported successfully in {}"
argument_list|,
name|importWatch
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Indexing completed successfully in {}"
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|performReindexInReadOnlyMode
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|String
name|checkpoint
parameter_list|)
throws|throws
name|Exception
block|{
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
name|IndexHelper
name|indexHelper
init|=
name|createIndexHelper
argument_list|(
name|fixture
argument_list|,
name|indexOpts
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|reindex
argument_list|(
name|indexHelper
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|indexOpts
operator|.
name|getOutDir
argument_list|()
argument_list|,
name|OutOfBandIndexer
operator|.
name|LOCAL_INDEX_ROOT_DIR
argument_list|)
return|;
block|}
block|}
specifier|private
name|String
name|connectInReadWriteModeAndCreateCheckPoint
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|NodeStoreFixture
name|fixture
init|=
name|NodeStoreFixtureProvider
operator|.
name|create
argument_list|(
name|opts
argument_list|)
init|)
block|{
return|return
name|fixture
operator|.
name|getStore
argument_list|()
operator|.
name|checkpoint
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"creator"
argument_list|,
literal|"oak-run-indexer"
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
name|void
name|connectInReadWriteModeAndImportIndex
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|File
name|indexDir
parameter_list|)
throws|throws
name|Exception
block|{
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
name|IndexHelper
name|indexHelper
init|=
name|createIndexHelper
argument_list|(
name|fixture
argument_list|,
name|indexOpts
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|importIndex
argument_list|(
name|indexHelper
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dumpIndexContents
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexOpts
operator|.
name|dumpIndex
argument_list|()
condition|)
block|{
operator|new
name|IndexDumper
argument_list|(
name|indexHelper
argument_list|,
name|indexOpts
operator|.
name|getOutDir
argument_list|()
argument_list|)
operator|.
name|dump
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|performConsistencyCheck
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexOpts
operator|.
name|checkConsistency
argument_list|()
condition|)
block|{
name|IndexConsistencyCheckPrinter
name|printer
init|=
operator|new
name|IndexConsistencyCheckPrinter
argument_list|(
name|indexHelper
argument_list|,
name|indexOpts
operator|.
name|consistencyCheckLevel
argument_list|()
argument_list|)
decl_stmt|;
name|PrinterDumper
name|dumper
init|=
operator|new
name|PrinterDumper
argument_list|(
name|indexHelper
operator|.
name|getOutputDir
argument_list|()
argument_list|,
name|INDEX_CONSISTENCY_CHECK_TXT
argument_list|,
literal|false
argument_list|,
name|Format
operator|.
name|TEXT
argument_list|,
name|printer
argument_list|)
decl_stmt|;
name|dumper
operator|.
name|dump
argument_list|()
expr_stmt|;
name|consistencyCheckReport
operator|=
name|dumper
operator|.
name|getOutFile
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dumpIndexDefinitions
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexOpts
operator|.
name|dumpDefinitions
argument_list|()
condition|)
block|{
name|PrinterDumper
name|dumper
init|=
operator|new
name|PrinterDumper
argument_list|(
name|indexHelper
operator|.
name|getOutputDir
argument_list|()
argument_list|,
name|INDEX_DEFINITIONS_JSON
argument_list|,
literal|false
argument_list|,
name|Format
operator|.
name|JSON
argument_list|,
name|indexHelper
operator|.
name|getIndexDefnPrinter
argument_list|()
argument_list|)
decl_stmt|;
name|dumper
operator|.
name|dump
argument_list|()
expr_stmt|;
name|definitions
operator|=
name|dumper
operator|.
name|getOutFile
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dumpIndexStats
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|,
name|IndexHelper
name|indexHelper
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexOpts
operator|.
name|dumpStats
argument_list|()
condition|)
block|{
name|PrinterDumper
name|dumper
init|=
operator|new
name|PrinterDumper
argument_list|(
name|indexHelper
operator|.
name|getOutputDir
argument_list|()
argument_list|,
name|INDEX_INFO_TXT
argument_list|,
literal|true
argument_list|,
name|Format
operator|.
name|TEXT
argument_list|,
name|indexHelper
operator|.
name|getIndexPrinter
argument_list|()
argument_list|)
decl_stmt|;
name|dumper
operator|.
name|dump
argument_list|()
expr_stmt|;
name|info
operator|=
name|dumper
operator|.
name|getOutFile
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|setupDirectories
parameter_list|(
name|IndexOptions
name|indexOpts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexOpts
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
name|indexOpts
operator|.
name|getOutDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cleanWorkDir
argument_list|(
name|indexOpts
operator|.
name|getWorkDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|cleanWorkDir
parameter_list|(
name|File
name|workDir
parameter_list|)
throws|throws
name|IOException
block|{
comment|//TODO Do not clean if restarting
name|String
index|[]
name|dirListing
init|=
name|workDir
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|dirListing
operator|!=
literal|null
operator|&&
name|dirListing
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Cleaning existing work directory {}"
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|Path
name|getPath
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|toPath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
return|;
block|}
block|}
end_class

end_unit

