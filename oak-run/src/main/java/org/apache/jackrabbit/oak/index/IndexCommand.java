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
name|StatisticsProvider
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
name|closer
operator|.
name|register
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
name|setupDirectories
argument_list|(
name|indexOpts
argument_list|)
expr_stmt|;
name|StatisticsProvider
name|statisticsProvider
init|=
name|WhiteboardUtils
operator|.
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
decl_stmt|;
name|execute
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
name|statisticsProvider
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
name|NodeStore
name|store
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
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
operator|new
name|IndexHelper
argument_list|(
name|store
argument_list|,
name|blobStore
argument_list|,
name|statisticsProvider
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
name|closer
operator|.
name|register
argument_list|(
name|indexHelper
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|opts
operator|.
name|getCommonOpts
argument_list|()
operator|.
name|isReadWrite
argument_list|()
condition|)
block|{
operator|new
name|ReIndexer
argument_list|(
name|indexHelper
argument_list|)
operator|.
name|reindex
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|checkpoint
init|=
name|indexOpts
operator|.
name|getCheckpoint
argument_list|()
decl_stmt|;
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

