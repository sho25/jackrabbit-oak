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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
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
name|core
operator|.
name|data
operator|.
name|FileDataStore
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
name|InitialContent
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
name|api
operator|.
name|ContentRepository
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
name|Tree
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
name|IOUtils
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
name|plugins
operator|.
name|blob
operator|.
name|BlobStoreStats
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
name|datastore
operator|.
name|DataStoreBlobStore
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
name|datastore
operator|.
name|OakFileDataStore
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
name|directory
operator|.
name|CopyOnReadDirectory
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
name|index
operator|.
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|index
operator|.
name|search
operator|.
name|ExtractedTextCache
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
name|search
operator|.
name|IndexDefinition
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
name|query
operator|.
name|AbstractQueryTest
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
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|FileStoreStats
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
name|InvalidFileStoreVersionException
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
name|stats
operator|.
name|BlobStatsCollector
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
name|DefaultStatisticsProvider
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|store
operator|.
name|FilterDirectory
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
name|Ignore
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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

begin_comment
comment|/**  * Tests for checking impacts of Lucene writes wrt storage / configuration adjustments on the  * {@link org.apache.jackrabbit.oak.segment.SegmentNodeStore}.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"this is meant to be a benchmark, it shouldn't be part of everyday builds"
argument_list|)
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|LuceneWritesOnSegmentStatsTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
specifier|static
specifier|final
name|File
name|DIRECTORY
init|=
operator|new
name|File
argument_list|(
literal|"target/fs"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FOO_QUERY
init|=
literal|"select [jcr:path] from [nt:base] where contains('foo', '*')"
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|copyOnRW
decl_stmt|;
specifier|private
specifier|final
name|String
name|codec
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|indexOnFS
decl_stmt|;
specifier|private
specifier|final
name|int
name|minRecordLength
decl_stmt|;
specifier|private
specifier|final
name|String
name|mergePolicy
decl_stmt|;
specifier|private
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduledExecutorService
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|String
name|corDir
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|cowDir
init|=
literal|null
decl_stmt|;
specifier|private
name|TestUtil
operator|.
name|OptionalEditorProvider
name|optionalEditorProvider
init|=
operator|new
name|TestUtil
operator|.
name|OptionalEditorProvider
argument_list|()
decl_stmt|;
specifier|private
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
name|DataStoreBlobStore
name|dataStoreBlobStore
decl_stmt|;
specifier|private
name|DefaultStatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
name|String
name|fdsDir
decl_stmt|;
specifier|private
name|String
name|indexPath
decl_stmt|;
specifier|public
name|LuceneWritesOnSegmentStatsTest
parameter_list|(
name|boolean
name|copyOnRW
parameter_list|,
name|String
name|codec
parameter_list|,
name|boolean
name|indexOnFS
parameter_list|,
name|int
name|minRecordLength
parameter_list|,
name|String
name|mergePolicy
parameter_list|)
block|{
name|this
operator|.
name|copyOnRW
operator|=
name|copyOnRW
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
name|this
operator|.
name|indexOnFS
operator|=
name|indexOnFS
expr_stmt|;
name|this
operator|.
name|minRecordLength
operator|=
name|minRecordLength
expr_stmt|;
name|this
operator|.
name|mergePolicy
operator|=
name|mergePolicy
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"no"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|4096
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|4096
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|4096
block|,
literal|"no"
block|}
block|,
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"oakCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"no"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|100
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|100
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"Lucene46"
block|,
literal|false
block|,
literal|100
block|,
literal|"no"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|4096
block|,
literal|"no"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"tiered"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"mitigated"
block|}
block|,
block|{
literal|false
block|,
literal|"compressingCodec"
block|,
literal|false
block|,
literal|100
block|,
literal|"no"
block|}
block|,         }
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|DIRECTORY
operator|.
name|exists
argument_list|()
condition|)
block|{
assert|assert
name|DIRECTORY
operator|.
name|mkdirs
argument_list|()
assert|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexDefinition
operator|.
name|setDisableStoredIndexDefinition
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|DIRECTORY
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|DIRECTORY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|LuceneIndexEditorProvider
name|editorProvider
decl_stmt|;
name|LuceneIndexProvider
name|provider
decl_stmt|;
if|if
condition|(
name|copyOnRW
condition|)
block|{
name|IndexCopier
name|copier
init|=
name|createIndexCopier
argument_list|()
decl_stmt|;
name|editorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|copier
argument_list|,
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|provider
operator|=
operator|new
name|LuceneIndexProvider
argument_list|(
name|copier
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
name|provider
operator|=
operator|new
name|LuceneIndexProvider
argument_list|()
expr_stmt|;
block|}
name|NodeStore
name|nodeStore
decl_stmt|;
try|try
block|{
name|statisticsProvider
operator|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|scheduledExecutorService
argument_list|)
expr_stmt|;
name|fileStore
operator|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|DIRECTORY
argument_list|)
operator|.
name|withStatisticsProvider
argument_list|(
name|statisticsProvider
argument_list|)
operator|.
name|withBlobStore
argument_list|(
name|createBlobStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|nodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|InvalidFileStoreVersionException
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
return|return
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
name|editorProvider
argument_list|)
operator|.
name|with
argument_list|(
name|optionalEditorProvider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeTypeIndexProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
specifier|private
name|BlobStore
name|createBlobStore
parameter_list|()
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|OakFileDataStore
argument_list|()
decl_stmt|;
name|fdsDir
operator|=
literal|"target/fds-"
operator|+
name|codec
operator|+
name|copyOnRW
operator|+
name|minRecordLength
operator|+
name|mergePolicy
expr_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|fdsDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|minRecordLength
operator|>
literal|0
condition|)
block|{
name|fds
operator|.
name|setMinRecordLength
argument_list|(
name|minRecordLength
argument_list|)
expr_stmt|;
block|}
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|dataStoreBlobStore
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
expr_stmt|;
name|StatisticsProvider
name|sp
init|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|scheduledExecutorService
argument_list|)
decl_stmt|;
name|BlobStatsCollector
name|collector
init|=
operator|new
name|BlobStoreStats
argument_list|(
name|sp
argument_list|)
decl_stmt|;
name|dataStoreBlobStore
operator|.
name|setBlobStatsCollector
argument_list|(
name|collector
argument_list|)
expr_stmt|;
return|return
name|dataStoreBlobStore
return|;
block|}
specifier|private
name|IndexCopier
name|createIndexCopier
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|IndexCopier
argument_list|(
name|executorService
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Directory
name|wrapForRead
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|ret
init|=
name|super
operator|.
name|wrapForRead
argument_list|(
name|indexPath
argument_list|,
name|definition
argument_list|,
name|remote
argument_list|,
name|dirName
argument_list|)
decl_stmt|;
name|corDir
operator|=
name|getFSDirPath
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
specifier|public
name|Directory
name|wrapForWrite
parameter_list|(
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|boolean
name|reindexMode
parameter_list|,
name|String
name|dirName
parameter_list|,
name|COWDirectoryTracker
name|cowDirectoryTracker
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|ret
init|=
name|super
operator|.
name|wrapForWrite
argument_list|(
name|definition
argument_list|,
name|remote
argument_list|,
name|reindexMode
argument_list|,
name|dirName
argument_list|,
name|cowDirectoryTracker
argument_list|)
decl_stmt|;
name|cowDir
operator|=
name|getFSDirPath
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|private
name|String
name|getFSDirPath
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|instanceof
name|CopyOnReadDirectory
condition|)
block|{
name|dir
operator|=
operator|(
operator|(
name|CopyOnReadDirectory
operator|)
name|dir
operator|)
operator|.
name|getLocal
argument_list|()
expr_stmt|;
block|}
name|dir
operator|=
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|FSDirectory
condition|)
block|{
return|return
operator|(
operator|(
name|FSDirectory
operator|)
name|dir
operator|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Directory
name|unwrap
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|instanceof
name|FilterDirectory
condition|)
block|{
return|return
name|unwrap
argument_list|(
operator|(
operator|(
name|FilterDirectory
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
argument_list|)
return|;
block|}
return|return
name|dir
return|;
block|}
block|}
return|;
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
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdownExecutor
parameter_list|()
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|scheduledExecutorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLuceneIndexSegmentStats
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|idxb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
operator|.
name|noAsync
argument_list|()
operator|.
name|codec
argument_list|(
name|codec
argument_list|)
operator|.
name|mergePolicy
argument_list|(
name|mergePolicy
argument_list|)
decl_stmt|;
name|idxb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|analyzed
argument_list|()
operator|.
name|nodeScopeIndex
argument_list|()
operator|.
name|ordered
argument_list|()
operator|.
name|useInExcerpt
argument_list|()
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|idxb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"bin"
argument_list|)
operator|.
name|analyzed
argument_list|()
operator|.
name|nodeScopeIndex
argument_list|()
operator|.
name|ordered
argument_list|()
operator|.
name|useInExcerpt
argument_list|()
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|Tree
name|idx
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"lucenePropertyIndex"
argument_list|)
decl_stmt|;
name|Tree
name|idxDef
init|=
name|idxb
operator|.
name|build
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|codec
operator|.
name|equals
argument_list|(
literal|"oakCodec"
argument_list|)
operator|&&
name|indexOnFS
condition|)
block|{
name|idxDef
operator|.
name|setProperty
argument_list|(
literal|"persistence"
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|indexPath
operator|=
literal|"target/index-"
operator|+
name|codec
operator|+
name|copyOnRW
expr_stmt|;
name|idxDef
operator|.
name|setProperty
argument_list|(
literal|"path"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"***"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|codec
operator|+
literal|","
operator|+
name|copyOnRW
operator|+
literal|","
operator|+
name|indexOnFS
operator|+
literal|","
operator|+
name|minRecordLength
operator|+
literal|","
operator|+
name|mergePolicy
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|multiplier
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|multiplier
condition|;
name|n
operator|++
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"iteration "
operator|+
operator|(
name|n
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|Tree
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|10240
index|]
decl_stmt|;
name|Charset
name|charset
init|=
name|Charset
operator|.
name|defaultCharset
argument_list|()
decl_stmt|;
name|String
name|text
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|text
operator|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|rootTree
operator|.
name|addChild
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|n
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"bin"
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|printStats
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"reindex"
argument_list|)
expr_stmt|;
comment|// do nothing, reindex and measure
name|idx
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/lucenePropertyIndex"
argument_list|)
expr_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|printStats
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"add and delete"
argument_list|)
expr_stmt|;
comment|// add and delete some content and measure
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|text
operator|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|rootTree
operator|.
name|addChild
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|n
operator|+
literal|100
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"bin"
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|+
name|i
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
comment|// delete one of the already existing nodes every 3
assert|assert
name|rootTree
operator|.
name|getChild
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|n
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|()
assert|;
block|}
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|printStats
argument_list|()
expr_stmt|;
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"finished in "
operator|+
operator|(
name|time
operator|/
operator|(
literal|60000
operator|)
operator|)
operator|+
literal|" minutes"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"***"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|double
name|evaluateQuery
parameter_list|(
name|String
name|fooQuery
parameter_list|)
block|{
name|long
name|q1Start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|res1
init|=
name|executeQuery
argument_list|(
name|fooQuery
argument_list|,
name|SQL2
argument_list|)
decl_stmt|;
name|long
name|q1End
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|double
name|time
init|=
operator|(
name|q1End
operator|-
name|q1Start
operator|)
operator|/
literal|1000d
decl_stmt|;
name|assertNotNull
argument_list|(
name|res1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|res1
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|time
return|;
block|}
specifier|private
name|void
name|printStats
parameter_list|()
throws|throws
name|IOException
block|{
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
name|FileStoreStats
name|stats
init|=
name|fileStore
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|long
name|sizeOfDirectory
init|=
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|fdsDir
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|fdsSize
init|=
operator|(
name|sizeOfDirectory
operator|/
operator|(
literal|1024
operator|*
literal|1000
operator|)
operator|)
operator|+
literal|" MB"
decl_stmt|;
name|double
name|time
init|=
name|evaluateQuery
argument_list|(
name|FOO_QUERY
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"||codec||min record length||merge policy||segment size||FDS size||query time||"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"|"
operator|+
name|codec
operator|+
literal|"|"
operator|+
name|minRecordLength
operator|+
literal|"|"
operator|+
name|mergePolicy
operator|+
literal|"|"
operator|+
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|stats
operator|.
name|getApproximateSize
argument_list|()
argument_list|)
operator|+
literal|"|"
operator|+
name|fdsSize
operator|+
literal|"|"
operator|+
name|time
operator|+
literal|" s|"
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexOnFS
condition|)
block|{
name|long
name|sizeOfFSIndex
init|=
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Index on FS size : "
operator|+
name|FileUtils
operator|.
name|byteCountToDisplaySize
argument_list|(
name|sizeOfFSIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|dumpFileStoreTo
parameter_list|(
name|File
name|to
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|to
operator|.
name|exists
argument_list|()
condition|)
block|{
assert|assert
name|to
operator|.
name|mkdirs
argument_list|()
assert|;
block|}
for|for
control|(
name|File
name|f
range|:
name|DIRECTORY
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|f
argument_list|,
operator|new
name|File
argument_list|(
name|to
operator|.
name|getPath
argument_list|()
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|sizeOfDirectory
init|=
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|to
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
return|return
name|sizeOfDirectory
return|;
block|}
block|}
end_class

end_unit

