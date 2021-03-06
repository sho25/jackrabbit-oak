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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|difference
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|emptySet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|sizeOfDirectory
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|file
operator|.
name|tar
operator|.
name|LocalJournalFile
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

begin_comment
comment|/**  * Perform an offline compaction of an existing segment store.  */
end_comment

begin_class
specifier|public
class|class
name|Compact
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
name|File
name|path
decl_stmt|;
specifier|private
name|Boolean
name|mmap
decl_stmt|;
specifier|private
name|String
name|os
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
comment|/**          * The path to an existing segment store. This parameter is required.          *          * @param path the path to an existing segment store.          * @return this builder.          */
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
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
comment|/**          * Whether to use memory mapped access or file access.          *          * @param mmap {@code true} for memory mapped access, {@code false} for          *             file access {@code null} to determine the access mode          *             from the system architecture: memory mapped on 64 bit          *             systems, file access on  32 bit systems.          * @return this builder.          */
specifier|public
name|Builder
name|withMmap
parameter_list|(
annotation|@
name|Nullable
name|Boolean
name|mmap
parameter_list|)
block|{
name|this
operator|.
name|mmap
operator|=
name|mmap
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Which operating system the code is running on.          *          * @param os The operating system as returned by the "os.name" standard          *           system property.          * @return this builder.          */
specifier|public
name|Builder
name|withOs
parameter_list|(
name|String
name|os
parameter_list|)
block|{
name|this
operator|.
name|os
operator|=
name|checkNotNull
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Whether to fail if run on an older version of the store of force          * upgrading its format.          *          * @param force upgrade iff {@code true}          * @return this builder.          */
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
comment|/**          * The size of the segment cache in MB. The default of {@link          * SegmentCache#DEFAULT_SEGMENT_CACHE_MB} when this method is not          * invoked.          *          * @param segmentCacheSize cache size in MB          * @return this builder          * @throws IllegalArgumentException if {@code segmentCacheSize} is not a          *                                  positive integer.          */
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
comment|/**          * The number of nodes after which an update about the compaction          * process is logged. Set to a negative number to disable progress          * logging. If not specified, it defaults to 150,000 nodes.          *          * @param gcLogInterval The log interval.          * @return this builder.          */
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
name|Compact
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
name|Compact
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
enum|enum
name|FileAccessMode
block|{
name|ARCH_DEPENDENT
argument_list|(
literal|null
argument_list|,
literal|"default access mode"
argument_list|)
block|,
name|MEMORY_MAPPED
argument_list|(
literal|true
argument_list|,
literal|"memory mapped access mode"
argument_list|)
block|,
name|REGULAR
argument_list|(
literal|false
argument_list|,
literal|"regular access mode"
argument_list|)
block|,
name|REGULAR_ENFORCED
argument_list|(
literal|false
argument_list|,
literal|"enforced regular access mode"
argument_list|)
block|;
specifier|final
name|Boolean
name|memoryMapped
decl_stmt|;
specifier|final
name|String
name|description
decl_stmt|;
name|FileAccessMode
parameter_list|(
name|Boolean
name|memoryMapped
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|memoryMapped
operator|=
name|memoryMapped
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|FileAccessMode
name|newFileAccessMode
parameter_list|(
name|Boolean
name|arg
parameter_list|,
name|String
name|os
parameter_list|)
block|{
if|if
condition|(
name|os
operator|!=
literal|null
operator|&&
name|os
operator|.
name|toLowerCase
argument_list|()
operator|.
name|contains
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
return|return
name|FileAccessMode
operator|.
name|REGULAR_ENFORCED
return|;
block|}
if|if
condition|(
name|arg
operator|==
literal|null
condition|)
block|{
return|return
name|FileAccessMode
operator|.
name|ARCH_DEPENDENT
return|;
block|}
if|if
condition|(
name|arg
condition|)
block|{
return|return
name|FileAccessMode
operator|.
name|MEMORY_MAPPED
return|;
block|}
return|return
name|FileAccessMode
operator|.
name|REGULAR
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|File
argument_list|>
name|listFiles
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
return|return
name|emptySet
argument_list|()
return|;
block|}
return|return
name|newHashSet
argument_list|(
name|files
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|printFiles
parameter_list|(
name|PrintStream
name|s
parameter_list|,
name|Set
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|s
operator|.
name|printf
argument_list|(
literal|"        %s, %s\n"
argument_list|,
name|getLastModified
argument_list|(
name|f
argument_list|)
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getLastModified
parameter_list|(
name|File
name|f
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|f
operator|.
name|lastModified
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
parameter_list|(
name|Set
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
specifier|private
specifier|static
name|String
name|printableSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%d bytes)"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|size
argument_list|)
argument_list|,
name|size
argument_list|)
return|;
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
name|File
name|path
decl_stmt|;
specifier|private
specifier|final
name|File
name|journal
decl_stmt|;
specifier|private
specifier|final
name|FileAccessMode
name|fileAccessMode
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
name|Compact
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
name|journal
operator|=
operator|new
name|File
argument_list|(
name|builder
operator|.
name|path
argument_list|,
literal|"journal.log"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileAccessMode
operator|=
name|newFileAccessMode
argument_list|(
name|builder
operator|.
name|mmap
argument_list|,
name|builder
operator|.
name|os
argument_list|)
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
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Compacting %s with %s\n"
argument_list|,
name|path
argument_list|,
name|fileAccessMode
operator|.
name|description
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
name|Set
argument_list|<
name|File
argument_list|>
name|beforeFiles
init|=
name|listFiles
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|printFiles
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|beforeFiles
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    size %s\n"
argument_list|,
name|printableSize
argument_list|(
name|sizeOfDirectory
argument_list|(
name|path
argument_list|)
argument_list|)
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
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
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
name|LocalJournalFile
argument_list|(
name|path
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
name|Set
argument_list|<
name|File
argument_list|>
name|afterFiles
init|=
name|listFiles
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|printFiles
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|afterFiles
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    size %s\n"
argument_list|,
name|printableSize
argument_list|(
name|sizeOfDirectory
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    removed files %s\n"
argument_list|,
name|fileNames
argument_list|(
name|difference
argument_list|(
name|beforeFiles
argument_list|,
name|afterFiles
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"    added files %s\n"
argument_list|,
name|fileNames
argument_list|(
name|difference
argument_list|(
name|afterFiles
argument_list|,
name|beforeFiles
argument_list|)
argument_list|)
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
name|FileStore
name|newFileStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
name|path
operator|.
name|getAbsoluteFile
argument_list|()
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
if|if
condition|(
name|fileAccessMode
operator|.
name|memoryMapped
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withMemoryMapping
argument_list|(
name|fileAccessMode
operator|.
name|memoryMapped
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

