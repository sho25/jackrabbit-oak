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
name|checkArgument
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
name|segment
operator|.
name|SegmentCache
operator|.
name|DEFAULT_SEGMENT_CACHE_MB
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
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_ACCOUNT_NAME
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
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_DIR
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
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_STORAGE_URI
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
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromUri
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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|PrintStream
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
name|SegmentCache
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
name|AzureJournalFile
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
name|AzureUtilities
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
name|JournalReader
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
name|JournalFile
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
name|SegmentNodeStorePersistence
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
name|io
operator|.
name|Files
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
name|StorageCredentials
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
name|StorageCredentialsAccountAndKey
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

begin_comment
comment|/**  * Perform an offline compaction of an existing Azure Segment Store.  */
end_comment

begin_class
specifier|public
class|class
name|AzureCompact
block|{
comment|/**      * Create a builder for the {@link Compact} command.      *      * @return an instance of {@link Builder}.      */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Collect options for the {@link Compact} command.      */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
name|boolean
name|force
decl_stmt|;
specifier|private
name|long
name|gcLogInterval
init|=
literal|150000
decl_stmt|;
specifier|private
name|int
name|segmentCacheSize
init|=
name|DEFAULT_SEGMENT_CACHE_MB
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The path (URI) to an existing segment store. This parameter is required.          *          * @param path          *            the path to an existing segment store.          * @return this builder.          */
specifier|public
name|Builder
name|withPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Whether to fail if run on an older version of the store of force upgrading          * its format.          *          * @param force          *            upgrade iff {@code true}          * @return this builder.          */
specifier|public
name|Builder
name|withForce
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * The size of the segment cache in MB. The default of          * {@link SegmentCache#DEFAULT_SEGMENT_CACHE_MB} when this method is not          * invoked.          *          * @param segmentCacheSize          *            cache size in MB          * @return this builder          * @throws IllegalArgumentException          *             if {@code segmentCacheSize} is not a positive integer.          */
specifier|public
name|Builder
name|withSegmentCacheSize
parameter_list|(
name|int
name|segmentCacheSize
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|segmentCacheSize
operator|>
literal|0
argument_list|,
literal|"segmentCacheSize must be strictly positive"
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentCacheSize
operator|=
name|segmentCacheSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * The number of nodes after which an update about the compaction process is          * logged. Set to a negative number to disable progress logging. If not          * specified, it defaults to 150,000 nodes.          *          * @param gcLogInterval          *            The log interval.          * @return this builder.          */
specifier|public
name|Builder
name|withGCLogInterval
parameter_list|(
name|long
name|gcLogInterval
parameter_list|)
block|{
name|this
operator|.
name|gcLogInterval
operator|=
name|gcLogInterval
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link Compact} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|AzureCompact
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
operator|new
name|AzureCompact
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|printableStopwatch
parameter_list|(
name|Stopwatch
name|s
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%ds)"
argument_list|,
name|s
argument_list|,
name|s
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|int
name|segmentCacheSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|strictVersionCheck
decl_stmt|;
specifier|private
specifier|final
name|long
name|gcLogInterval
decl_stmt|;
specifier|private
name|AzureCompact
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|segmentCacheSize
operator|=
name|builder
operator|.
name|segmentCacheSize
expr_stmt|;
name|this
operator|.
name|strictVersionCheck
operator|=
operator|!
name|builder
operator|.
name|force
expr_stmt|;
name|this
operator|.
name|gcLogInterval
operator|=
name|builder
operator|.
name|gcLogInterval
expr_stmt|;
block|}
specifier|public
name|int
name|run
parameter_list|()
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|CloudBlobDirectory
name|cloudBlobDirectory
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cloudBlobDirectory
operator|=
name|createCloudBlobDirectory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|StorageException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not connect to the Azure Storage. Please verify the path provided!"
argument_list|)
throw|;
block|}
name|SegmentNodeStorePersistence
name|persistence
init|=
operator|new
name|AzurePersistence
argument_list|(
name|cloudBlobDirectory
argument_list|)
decl_stmt|;
name|SegmentArchiveManager
name|archiveManager
init|=
literal|null
decl_stmt|;
try|try
block|{
name|archiveManager
operator|=
name|persistence
operator|.
name|createArchiveManager
argument_list|(
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
name|IllegalArgumentException
argument_list|(
literal|"Could not access the Azure Storage. Please verify the path provided!"
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Compacting %s\n"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    before\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|beforeArchives
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
try|try
block|{
name|beforeArchives
operator|=
name|archiveManager
operator|.
name|listArchives
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|printArchives
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|beforeArchives
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    -> compacting\n"
argument_list|)
expr_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|(
name|persistence
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|store
operator|.
name|compactFull
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Compaction cancelled after %s.\n"
argument_list|,
name|printableStopwatch
argument_list|(
name|watch
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    -> cleaning up\n"
argument_list|)
expr_stmt|;
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|JournalFile
name|journal
init|=
operator|new
name|AzureJournalFile
argument_list|(
name|cloudBlobDirectory
argument_list|,
literal|"journal.log"
argument_list|)
decl_stmt|;
name|String
name|head
decl_stmt|;
try|try
init|(
name|JournalReader
name|journalReader
init|=
operator|new
name|JournalReader
argument_list|(
name|journal
argument_list|)
init|)
block|{
name|head
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s root %s\n"
argument_list|,
name|journalReader
operator|.
name|next
argument_list|()
operator|.
name|getRevision
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|JournalFileWriter
name|journalWriter
init|=
name|journal
operator|.
name|openJournalWriter
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    -> writing new %s: %s\n"
argument_list|,
name|journal
operator|.
name|getName
argument_list|()
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|journalWriter
operator|.
name|truncate
argument_list|()
expr_stmt|;
name|journalWriter
operator|.
name|writeLine
argument_list|(
name|head
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|watch
operator|.
name|stop
argument_list|()
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
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Compaction failed after %s.\n"
argument_list|,
name|printableStopwatch
argument_list|(
name|watch
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    after\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|afterArchives
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
try|try
block|{
name|afterArchives
operator|=
name|archiveManager
operator|.
name|listArchives
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|printArchives
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|afterArchives
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Compaction succeeded in %s.\n"
argument_list|,
name|printableStopwatch
argument_list|(
name|watch
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|private
specifier|static
name|void
name|printArchives
parameter_list|(
name|PrintStream
name|s
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|archives
parameter_list|)
block|{
for|for
control|(
name|String
name|a
range|:
name|archives
control|)
block|{
name|s
operator|.
name|printf
argument_list|(
literal|"        %s\n"
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|FileStore
name|newFileStore
parameter_list|(
name|SegmentNodeStorePersistence
name|persistence
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
throws|,
name|URISyntaxException
throws|,
name|StorageException
block|{
name|FileStoreBuilder
name|builder
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|Files
operator|.
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|withCustomPersistence
argument_list|(
name|persistence
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|withStrictVersionCheck
argument_list|(
name|strictVersionCheck
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
name|segmentCacheSize
argument_list|)
operator|.
name|withGCOptions
argument_list|(
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
operator|.
name|setGCLogInterval
argument_list|(
name|gcLogInterval
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|CloudBlobDirectory
name|createCloudBlobDirectory
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|parseAzureConfigurationFromUri
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|accountName
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_ACCOUNT_NAME
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"AZURE_SECRET_KEY"
argument_list|)
decl_stmt|;
name|StorageCredentials
name|credentials
init|=
operator|new
name|StorageCredentialsAccountAndKey
argument_list|(
name|accountName
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_STORAGE_URI
argument_list|)
decl_stmt|;
name|String
name|dir
init|=
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
decl_stmt|;
return|return
name|AzureUtilities
operator|.
name|cloudBlobDirectoryFrom
argument_list|(
name|credentials
argument_list|,
name|uri
argument_list|,
name|dir
argument_list|)
return|;
block|}
block|}
end_class

end_unit

