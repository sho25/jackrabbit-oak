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
name|segment
operator|.
name|azure
operator|.
name|tool
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
name|azure
operator|.
name|tool
operator|.
name|ToolUtils
operator|.
name|fetchByteArray
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
name|azure
operator|.
name|tool
operator|.
name|ToolUtils
operator|.
name|storeDescription
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobDirectory
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
name|azure
operator|.
name|AzurePersistence
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
name|azure
operator|.
name|tool
operator|.
name|ToolUtils
operator|.
name|SegmentStoreType
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
name|tar
operator|.
name|TarPersistence
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
name|spi
operator|.
name|monitor
operator|.
name|FileStoreMonitorAdapter
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
name|spi
operator|.
name|monitor
operator|.
name|IOMonitorAdapter
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
name|spi
operator|.
name|persistence
operator|.
name|Buffer
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
name|spi
operator|.
name|persistence
operator|.
name|GCJournalFile
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
name|spi
operator|.
name|persistence
operator|.
name|JournalFileReader
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
name|spi
operator|.
name|persistence
operator|.
name|JournalFileWriter
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveEntry
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveManager
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveReader
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentArchiveWriter
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
name|spi
operator|.
name|persistence
operator|.
name|SegmentNodeStorePersistence
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
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|net
operator|.
name|URISyntaxException
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
name|Collections
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|ExecutionException
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
name|Future
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentStoreMigrator
implements|implements
name|Closeable
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
name|SegmentStoreMigrator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|READ_THREADS
init|=
literal|20
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStorePersistence
name|source
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStorePersistence
name|target
decl_stmt|;
specifier|private
specifier|final
name|String
name|sourceName
decl_stmt|;
specifier|private
specifier|final
name|String
name|targetName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|appendMode
decl_stmt|;
specifier|private
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|READ_THREADS
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|SegmentStoreMigrator
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|builder
operator|.
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|builder
operator|.
name|target
expr_stmt|;
name|this
operator|.
name|sourceName
operator|=
name|builder
operator|.
name|sourceName
expr_stmt|;
name|this
operator|.
name|targetName
operator|=
name|builder
operator|.
name|targetName
expr_stmt|;
name|this
operator|.
name|appendMode
operator|=
name|builder
operator|.
name|appendMode
expr_stmt|;
block|}
specifier|public
name|void
name|migrate
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|migrateJournal
argument_list|()
expr_stmt|;
name|migrateGCJournal
argument_list|()
expr_stmt|;
name|migrateManifest
argument_list|()
expr_stmt|;
name|migrateArchives
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|migrateJournal
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}/journal.log -> {}"
argument_list|,
name|sourceName
argument_list|,
name|targetName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|getJournalFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No journal at {}; skipping."
argument_list|,
name|sourceName
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|journal
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|JournalFileReader
name|reader
init|=
name|source
operator|.
name|getJournalFile
argument_list|()
operator|.
name|openJournalReader
argument_list|()
init|)
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|journal
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|journal
argument_list|)
expr_stmt|;
try|try
init|(
name|JournalFileWriter
name|writer
init|=
name|target
operator|.
name|getJournalFile
argument_list|()
operator|.
name|openJournalWriter
argument_list|()
init|)
block|{
name|writer
operator|.
name|truncate
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|line
range|:
name|journal
control|)
block|{
name|writer
operator|.
name|writeLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|migrateGCJournal
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}/gc.log -> {}"
argument_list|,
name|sourceName
argument_list|,
name|targetName
argument_list|)
expr_stmt|;
name|GCJournalFile
name|targetGCJournal
init|=
name|target
operator|.
name|getGCJournalFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|appendMode
condition|)
block|{
name|targetGCJournal
operator|.
name|truncate
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|line
range|:
name|source
operator|.
name|getGCJournalFile
argument_list|()
operator|.
name|readLines
argument_list|()
control|)
block|{
name|targetGCJournal
operator|.
name|writeLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|migrateManifest
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}/manifest -> {}"
argument_list|,
name|sourceName
argument_list|,
name|targetName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|source
operator|.
name|getManifestFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No manifest at {}; skipping."
argument_list|,
name|sourceName
argument_list|)
expr_stmt|;
return|return;
block|}
name|Properties
name|manifest
init|=
name|source
operator|.
name|getManifestFile
argument_list|()
operator|.
name|load
argument_list|()
decl_stmt|;
name|target
operator|.
name|getManifestFile
argument_list|()
operator|.
name|save
argument_list|(
name|manifest
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|migrateArchives
parameter_list|()
throws|throws
name|IOException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|source
operator|.
name|segmentFilesExist
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No segment archives at {}; skipping."
argument_list|,
name|sourceName
argument_list|)
expr_stmt|;
return|return;
block|}
name|SegmentArchiveManager
name|sourceManager
init|=
name|source
operator|.
name|createArchiveManager
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|,
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentArchiveManager
name|targetManager
init|=
name|target
operator|.
name|createArchiveManager
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|,
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|targetArchives
init|=
name|targetManager
operator|.
name|listArchives
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|archiveName
range|:
name|sourceManager
operator|.
name|listArchives
argument_list|()
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}/{} -> {}"
argument_list|,
name|sourceName
argument_list|,
name|archiveName
argument_list|,
name|targetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|appendMode
operator|&&
name|targetArchives
operator|.
name|contains
argument_list|(
name|archiveName
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Already exists, skipping."
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
init|(
name|SegmentArchiveReader
name|reader
init|=
name|sourceManager
operator|.
name|forceOpen
argument_list|(
name|archiveName
argument_list|)
init|)
block|{
name|SegmentArchiveWriter
name|writer
init|=
name|targetManager
operator|.
name|create
argument_list|(
name|archiveName
argument_list|)
decl_stmt|;
try|try
block|{
name|migrateSegments
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|migrateBinaryRef
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|migrateGraph
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|migrateSegments
parameter_list|(
name|SegmentArchiveReader
name|reader
parameter_list|,
name|SegmentArchiveWriter
name|writer
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|Segment
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentArchiveEntry
name|entry
range|:
name|reader
operator|.
name|listSegments
argument_list|()
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
name|Segment
name|segment
init|=
operator|new
name|Segment
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|segment
operator|.
name|read
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|segment
return|;
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|Segment
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|Segment
name|segment
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|segment
operator|.
name|write
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|migrateBinaryRef
parameter_list|(
name|SegmentArchiveReader
name|reader
parameter_list|,
name|SegmentArchiveWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Buffer
name|binaryReferences
init|=
name|reader
operator|.
name|getBinaryReferences
argument_list|()
decl_stmt|;
if|if
condition|(
name|binaryReferences
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|array
init|=
name|fetchByteArray
argument_list|(
name|binaryReferences
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeBinaryReferences
argument_list|(
name|array
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|migrateGraph
parameter_list|(
name|SegmentArchiveReader
name|reader
parameter_list|,
name|SegmentArchiveWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|.
name|hasGraph
argument_list|()
condition|)
block|{
name|Buffer
name|graph
init|=
name|reader
operator|.
name|getGraph
argument_list|()
decl_stmt|;
name|byte
index|[]
name|array
init|=
name|fetchByteArray
argument_list|(
name|graph
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeGraph
argument_list|(
name|array
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Segment
block|{
specifier|private
specifier|final
name|SegmentArchiveEntry
name|entry
decl_stmt|;
specifier|private
specifier|volatile
name|Buffer
name|data
decl_stmt|;
specifier|private
name|Segment
parameter_list|(
name|SegmentArchiveEntry
name|entry
parameter_list|)
block|{
name|this
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
block|}
specifier|private
name|void
name|read
parameter_list|(
name|SegmentArchiveReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|=
name|reader
operator|.
name|readSegment
argument_list|(
name|entry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|entry
operator|.
name|getLsb
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|write
parameter_list|(
name|SegmentArchiveWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|array
init|=
name|data
operator|.
name|array
argument_list|()
decl_stmt|;
specifier|final
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|writer
operator|.
name|writeSegment
argument_list|(
name|entry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|entry
operator|.
name|getLsb
argument_list|()
argument_list|,
name|array
argument_list|,
name|offset
argument_list|,
name|entry
operator|.
name|getLength
argument_list|()
argument_list|,
name|entry
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|getFullGeneration
argument_list|()
argument_list|,
name|entry
operator|.
name|isCompacted
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|UUID
argument_list|(
name|entry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|entry
operator|.
name|getLsb
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|SegmentNodeStorePersistence
name|source
decl_stmt|;
specifier|private
name|SegmentNodeStorePersistence
name|target
decl_stmt|;
specifier|private
name|String
name|sourceName
decl_stmt|;
specifier|private
name|String
name|targetName
decl_stmt|;
specifier|private
name|boolean
name|appendMode
decl_stmt|;
specifier|public
name|Builder
name|withSource
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|TarPersistence
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceName
operator|=
name|storeDescription
argument_list|(
name|SegmentStoreType
operator|.
name|TAR
argument_list|,
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withSource
parameter_list|(
name|CloudBlobDirectory
name|dir
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|AzurePersistence
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceName
operator|=
name|storeDescription
argument_list|(
name|SegmentStoreType
operator|.
name|AZURE
argument_list|,
name|dir
operator|.
name|getContainer
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|dir
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withSourcePersistence
parameter_list|(
name|SegmentNodeStorePersistence
name|source
parameter_list|,
name|String
name|sourceName
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sourceName
operator|=
name|sourceName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withTargetPersistence
parameter_list|(
name|SegmentNodeStorePersistence
name|target
parameter_list|,
name|String
name|targetName
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|targetName
operator|=
name|targetName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withTarget
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
operator|new
name|TarPersistence
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetName
operator|=
name|storeDescription
argument_list|(
name|SegmentStoreType
operator|.
name|TAR
argument_list|,
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|withTarget
parameter_list|(
name|CloudBlobDirectory
name|dir
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|this
operator|.
name|target
operator|=
operator|new
name|AzurePersistence
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetName
operator|=
name|storeDescription
argument_list|(
name|SegmentStoreType
operator|.
name|AZURE
argument_list|,
name|dir
operator|.
name|getContainer
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|dir
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|setAppendMode
parameter_list|()
block|{
name|this
operator|.
name|appendMode
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SegmentStoreMigrator
name|build
parameter_list|()
block|{
return|return
operator|new
name|SegmentStoreMigrator
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

