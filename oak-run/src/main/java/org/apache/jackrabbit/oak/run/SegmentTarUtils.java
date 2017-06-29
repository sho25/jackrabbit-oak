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
import|import static
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
name|FileStoreHelper
operator|.
name|isValidFileStoreOrFail
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
name|segment
operator|.
name|SegmentVersion
operator|.
name|LATEST_VERSION
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
import|;
end_import

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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|SegmentVersion
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|tool
operator|.
name|Backup
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
name|tool
operator|.
name|Check
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
name|tool
operator|.
name|Compact
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
name|tool
operator|.
name|DebugSegments
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
name|tool
operator|.
name|DebugStore
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
name|tool
operator|.
name|DebugTars
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
name|tool
operator|.
name|Diff
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
name|tool
operator|.
name|History
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
name|tool
operator|.
name|Restore
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
name|tool
operator|.
name|Revisions
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

begin_class
specifier|final
class|class
name|SegmentTarUtils
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|TAR_STORAGE_MEMORY_MAPPED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tar.memoryMapped"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|TAR_SEGMENT_CACHE_SIZE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"cache"
argument_list|,
literal|256
argument_list|)
decl_stmt|;
specifier|private
name|SegmentTarUtils
parameter_list|()
block|{
comment|// Prevent instantiation
block|}
specifier|static
name|NodeStore
name|bootstrapNodeStore
parameter_list|(
name|String
name|path
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|bootstrapFileStore
argument_list|(
name|path
argument_list|,
name|closer
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|static
name|BlobReferenceRetriever
name|newBlobReferenceRetriever
parameter_list|(
name|String
name|path
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|SegmentBlobReferenceRetriever
argument_list|(
name|closer
operator|.
name|register
argument_list|(
name|openFileStore
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|static
name|void
name|backup
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|target
parameter_list|)
block|{
name|Backup
operator|.
name|builder
argument_list|()
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
operator|.
name|withTarget
argument_list|(
name|target
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|restore
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|target
parameter_list|)
block|{
name|Restore
operator|.
name|builder
argument_list|()
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
operator|.
name|withTarget
argument_list|(
name|target
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|debug
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|tars
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|segs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".tar"
argument_list|)
condition|)
block|{
name|tars
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|segs
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tars
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|debugTars
argument_list|(
name|file
argument_list|,
name|tars
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|segs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|debugSegments
argument_list|(
name|file
argument_list|,
name|segs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tars
operator|.
name|isEmpty
argument_list|()
operator|&&
name|segs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|debugStore
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|debugTars
parameter_list|(
name|File
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|tars
parameter_list|)
block|{
name|DebugTars
operator|.
name|Builder
name|builder
init|=
name|DebugTars
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tar
range|:
name|tars
control|)
block|{
name|builder
operator|.
name|withTar
argument_list|(
name|tar
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|debugSegments
parameter_list|(
name|File
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|segments
parameter_list|)
block|{
name|DebugSegments
operator|.
name|Builder
name|builder
init|=
name|DebugSegments
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|segments
control|)
block|{
name|builder
operator|.
name|withSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|debugStore
parameter_list|(
name|File
name|store
parameter_list|)
block|{
name|DebugStore
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
empty_stmt|;
block|}
specifier|static
name|void
name|history
parameter_list|(
name|File
name|directory
parameter_list|,
name|File
name|journal
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|History
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|directory
argument_list|)
operator|.
name|withJournal
argument_list|(
name|journal
argument_list|)
operator|.
name|withNode
argument_list|(
name|path
argument_list|)
operator|.
name|withDepth
argument_list|(
name|depth
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|check
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|journalFileName
parameter_list|,
name|long
name|debugLevel
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
parameter_list|,
name|boolean
name|ioStatistics
parameter_list|,
name|PrintWriter
name|outWriter
parameter_list|,
name|PrintWriter
name|errWriter
parameter_list|)
block|{
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|dir
argument_list|)
operator|.
name|withJournal
argument_list|(
name|journalFileName
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|debugLevel
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
name|checkBinaries
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
name|ioStatistics
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|compact
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|mmap
parameter_list|)
block|{
name|Compact
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|directory
argument_list|)
operator|.
name|withMmap
argument_list|(
name|mmap
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|diff
parameter_list|(
name|File
name|store
parameter_list|,
name|File
name|out
parameter_list|,
name|boolean
name|listOnly
parameter_list|,
name|String
name|interval
parameter_list|,
name|boolean
name|incremental
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|ignoreSNFEs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|listOnly
condition|)
block|{
name|revisions
argument_list|(
name|store
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diff
argument_list|(
name|store
argument_list|,
name|out
argument_list|,
name|interval
argument_list|,
name|incremental
argument_list|,
name|path
argument_list|,
name|ignoreSNFEs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|revisions
parameter_list|(
name|File
name|path
parameter_list|,
name|File
name|output
parameter_list|)
block|{
name|Revisions
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|path
argument_list|)
operator|.
name|withOutput
argument_list|(
name|output
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|diff
parameter_list|(
name|File
name|path
parameter_list|,
name|File
name|output
parameter_list|,
name|String
name|interval
parameter_list|,
name|boolean
name|incremental
parameter_list|,
name|String
name|filter
parameter_list|,
name|boolean
name|ignoreMissingSegments
parameter_list|)
block|{
name|Diff
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|path
argument_list|)
operator|.
name|withOutput
argument_list|(
name|output
argument_list|)
operator|.
name|withInterval
argument_list|(
name|interval
argument_list|)
operator|.
name|withIncremental
argument_list|(
name|incremental
argument_list|)
operator|.
name|withFilter
argument_list|(
name|filter
argument_list|)
operator|.
name|withIgnoreMissingSegments
argument_list|(
name|ignoreMissingSegments
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|FileStore
name|bootstrapFileStore
parameter_list|(
name|String
name|path
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|closer
operator|.
name|register
argument_list|(
name|bootstrapFileStore
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|FileStore
name|bootstrapFileStore
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|ReadOnlyFileStore
name|openReadOnlyFileStore
parameter_list|(
name|File
name|path
parameter_list|,
name|boolean
name|memoryMapped
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|isValidFileStoreOrFail
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
name|TAR_SEGMENT_CACHE_SIZE
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapped
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|FileStoreBuilder
name|newFileStoreBuilder
parameter_list|(
name|String
name|directory
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|checkFileStoreVersionOrFail
argument_list|(
name|directory
argument_list|,
name|force
argument_list|)
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
name|TAR_SEGMENT_CACHE_SIZE
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|FileStore
name|openFileStore
parameter_list|(
name|String
name|directory
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|newFileStoreBuilder
argument_list|(
name|directory
argument_list|,
name|force
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|File
name|checkFileStoreVersionOrFail
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|directory
return|;
block|}
name|ReadOnlyFileStore
name|store
init|=
name|openReadOnlyFileStore
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|SegmentVersion
name|segmentVersion
init|=
name|getSegmentVersion
argument_list|(
name|store
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentVersion
operator|!=
name|LATEST_VERSION
condition|)
block|{
if|if
condition|(
name|force
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Segment version mismatch. Found %s, expected %s. Forcing execution.\n"
argument_list|,
name|segmentVersion
argument_list|,
name|LATEST_VERSION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Segment version mismatch. Found %s, expected %s. Aborting."
argument_list|,
name|segmentVersion
argument_list|,
name|LATEST_VERSION
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|directory
return|;
block|}
specifier|private
specifier|static
name|SegmentVersion
name|getSegmentVersion
parameter_list|(
name|ReadOnlyFileStore
name|fileStore
parameter_list|)
block|{
return|return
name|fileStore
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentVersion
argument_list|()
return|;
block|}
block|}
end_class

end_unit

