begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|directory
package|;
end_package

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
name|IOFileFilter
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
name|RegexFileFilter
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
name|benchmark
operator|.
name|PerfLogger
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
name|IndexCommitCallback
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
name|stats
operator|.
name|Clock
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|AtomicBoolean
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
name|checkArgument
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveDeletedBlobCollectorFactory
block|{
specifier|public
interface|interface
name|ActiveDeletedBlobCollector
block|{
name|BlobDeletionCallback
name|getBlobDeletionCallback
parameter_list|()
function_decl|;
name|void
name|purgeBlobsDeleted
parameter_list|(
name|long
name|before
parameter_list|,
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
function_decl|;
block|}
comment|//                        LOG.info("Added {} to delete.", info);
specifier|public
specifier|static
name|ActiveDeletedBlobCollector
name|NOOP
init|=
operator|new
name|ActiveDeletedBlobCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BlobDeletionCallback
name|getBlobDeletionCallback
parameter_list|()
block|{
return|return
name|BlobDeletionCallback
operator|.
name|NOOP
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|purgeBlobsDeleted
parameter_list|(
name|long
name|before
parameter_list|,
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{          }
block|}
decl_stmt|;
specifier|public
interface|interface
name|BlobDeletionCallback
extends|extends
name|IndexCommitCallback
block|{
name|void
name|deleted
parameter_list|(
name|String
name|blobId
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
function_decl|;
name|BlobDeletionCallback
name|NOOP
init|=
operator|new
name|BlobDeletionCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|deleted
parameter_list|(
name|String
name|blobId
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|commitProgress
parameter_list|(
name|IndexProgress
name|indexProgress
parameter_list|)
block|{             }
block|}
decl_stmt|;
block|}
specifier|public
specifier|static
name|ActiveDeletedBlobCollector
name|newInstance
parameter_list|(
annotation|@
name|Nonnull
name|File
name|rootDirectory
parameter_list|,
name|ExecutorService
name|executorService
parameter_list|)
block|{
try|try
block|{
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|rootDirectory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ActiveDeletedBlobCollectorImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Disabling active blob collector as we couldn't not create folder: "
operator|+
name|rootDirectory
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return
name|NOOP
return|;
block|}
if|if
condition|(
operator|!
name|rootDirectory
operator|.
name|canRead
argument_list|()
operator|||
operator|!
name|rootDirectory
operator|.
name|canWrite
argument_list|()
operator|||
operator|!
name|rootDirectory
operator|.
name|canExecute
argument_list|()
condition|)
block|{
name|ActiveDeletedBlobCollectorImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Insufficient access in directory - {}. Disabling active blob collector"
argument_list|,
name|rootDirectory
argument_list|)
expr_stmt|;
return|return
name|NOOP
return|;
block|}
return|return
operator|new
name|ActiveDeletedBlobCollectorImpl
argument_list|(
name|rootDirectory
argument_list|,
name|executorService
argument_list|)
return|;
block|}
comment|/**      * Blob collector which takes *no* guarantees about checking whether the      * blob might be referred by paths other than one for which it is notified      * due deleted blob      */
specifier|static
class|class
name|ActiveDeletedBlobCollectorImpl
implements|implements
name|ActiveDeletedBlobCollector
block|{
specifier|private
specifier|static
name|PerfLogger
name|PERF_LOG
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveDeletedBlobCollectorImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveDeletedBlobCollectorImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|File
name|rootDirectory
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_FILE_PATTERN_PREFIX
init|=
literal|"blobs-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_FILE_PATTERN_SUFFIX
init|=
literal|".txt"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BLOB_FILE_PATTERN
init|=
name|BLOB_FILE_PATTERN_PREFIX
operator|+
literal|"%s"
operator|+
name|BLOB_FILE_PATTERN_SUFFIX
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IOFileFilter
name|blobFileNameFilter
init|=
operator|new
name|RegexFileFilter
argument_list|(
literal|"blobs-.*\\.txt"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|BlobIdInfoStruct
argument_list|>
name|deletedBlobs
decl_stmt|;
specifier|private
specifier|final
name|DeletedBlobsFileWriter
name|deletedBlobsFileWriter
decl_stmt|;
comment|/**          * @param rootDirectory directory that may be used by this instance to          *                      keep temporary data (e.g. reported deleted blob-ids).          * @param executorService executor service to asynchronously flush deleted blobs          *                        to a file.          */
name|ActiveDeletedBlobCollectorImpl
parameter_list|(
annotation|@
name|Nonnull
name|File
name|rootDirectory
parameter_list|,
annotation|@
name|Nonnull
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|this
argument_list|(
name|Clock
operator|.
name|SIMPLE
argument_list|,
name|rootDirectory
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
block|}
name|ActiveDeletedBlobCollectorImpl
parameter_list|(
name|Clock
name|clock
parameter_list|,
annotation|@
name|Nonnull
name|File
name|rootDirectory
parameter_list|,
annotation|@
name|Nonnull
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|rootDirectory
operator|=
name|rootDirectory
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|executorService
expr_stmt|;
name|this
operator|.
name|deletedBlobs
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<>
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//1000 items should be ok for async index commits
name|this
operator|.
name|deletedBlobsFileWriter
operator|=
operator|new
name|DeletedBlobsFileWriter
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|purgeBlobsDeleted
parameter_list|(
name|long
name|before
parameter_list|,
annotation|@
name|Nonnull
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{
name|long
name|numBlobsDeleted
init|=
literal|0
decl_stmt|;
name|long
name|numChunksDeleted
init|=
literal|0
decl_stmt|;
name|long
name|lastCheckedBlobTimestamp
init|=
name|readLastCheckedBlobTimestamp
argument_list|()
decl_stmt|;
name|deletedBlobsFileWriter
operator|.
name|releaseInUseFile
argument_list|()
expr_stmt|;
for|for
control|(
name|File
name|deletedBlobListFile
range|:
name|FileUtils
operator|.
name|listFiles
argument_list|(
name|rootDirectory
argument_list|,
name|blobFileNameFilter
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|deletedBlobListFile
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|deletedBlobsFileWriter
operator|.
name|inUseFileName
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Purging blobs from {}"
argument_list|,
name|deletedBlobListFile
argument_list|)
expr_stmt|;
name|long
name|timestamp
decl_stmt|;
try|try
block|{
name|timestamp
operator|=
name|getTimestampFromBlobFileName
argument_list|(
name|deletedBlobListFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't extract timestamp from filename - "
operator|+
name|deletedBlobListFile
argument_list|,
name|iae
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|timestamp
operator|<
name|before
condition|)
block|{
try|try
block|{
for|for
control|(
name|String
name|deletedBlobLine
range|:
name|FileUtils
operator|.
name|readLines
argument_list|(
name|deletedBlobListFile
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
control|)
block|{
name|String
index|[]
name|parsedDeletedBlobIdLine
init|=
name|deletedBlobLine
operator|.
name|split
argument_list|(
literal|"\\|"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|parsedDeletedBlobIdLine
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unparseable line ({}) in file {}. It won't be retried."
argument_list|,
name|parsedDeletedBlobIdLine
argument_list|,
name|deletedBlobListFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|deletedBlobId
init|=
name|parsedDeletedBlobIdLine
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|long
name|blobDeletionTimestamp
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|parsedDeletedBlobIdLine
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobDeletionTimestamp
operator|<
name|lastCheckedBlobTimestamp
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|blobDeletionTimestamp
operator|>=
name|before
condition|)
block|{
break|break;
block|}
name|long
name|deleted
init|=
name|blobStore
operator|.
name|countDeleteChunks
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|blobStore
operator|.
name|resolveChunks
argument_list|(
name|deletedBlobId
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleted
operator|<
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Blob {} in file {} not deleted"
argument_list|,
name|deletedBlobId
argument_list|,
name|deletedBlobListFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numBlobsDeleted
operator|++
expr_stmt|;
name|numChunksDeleted
operator|+=
name|deleted
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't parse blobTimestamp("
operator|+
name|parsedDeletedBlobIdLine
index|[
literal|1
index|]
operator|+
literal|"). deletedBlobLine - "
operator|+
name|deletedBlobLine
operator|+
literal|"; file - "
operator|+
name|deletedBlobListFile
operator|.
name|getName
argument_list|()
argument_list|,
name|nfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception occurred while attempting to delete blob "
operator|+
name|deletedBlobId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//log error and continue
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't read deleted blob list file - "
operator|+
name|deletedBlobListFile
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|deletedBlobListFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"File {} couldn't be deleted while all blobs listed in it have been purged"
argument_list|,
name|deletedBlobListFile
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping {} as its timestamp is newer than {}"
argument_list|,
name|deletedBlobListFile
operator|.
name|getName
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted {} blobs contained in {} chunks"
argument_list|,
name|numBlobsDeleted
argument_list|,
name|numChunksDeleted
argument_list|)
expr_stmt|;
name|writeOutLastCheckedBlobTimestamp
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|readLastCheckedBlobTimestamp
parameter_list|()
block|{
name|File
name|blobCollectorInfoFile
init|=
operator|new
name|File
argument_list|(
name|rootDirectory
argument_list|,
literal|"collection-info.txt"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|Properties
name|p
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|blobCollectorInfoFile
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|p
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Couldn't read last checked blob timestamp... would do a bit more scan"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|String
name|resString
init|=
name|p
operator|.
name|getProperty
argument_list|(
literal|"last-checked-blob-timestamp"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resString
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't fine last checked blob timestamp property in collection-info.txt"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|resString
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't read last checked blob timestamp '"
operator|+
name|resString
operator|+
literal|"' as long"
argument_list|,
name|nfe
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|private
name|void
name|writeOutLastCheckedBlobTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"last-checked-blob-timestamp"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|blobCollectorInfoFile
init|=
operator|new
name|File
argument_list|(
name|rootDirectory
argument_list|,
literal|"collection-info.txt"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|blobCollectorInfoFile
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"Last checked blob timestamp"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't write out last checked blob timestamp("
operator|+
name|timestamp
operator|+
literal|")"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|BlobDeletionCallback
name|getBlobDeletionCallback
parameter_list|()
throws|throws
name|IllegalStateException
block|{
return|return
operator|new
name|DeletedBlobCollector
argument_list|()
return|;
block|}
specifier|static
name|long
name|getTimestampFromBlobFileName
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
name|filename
operator|.
name|startsWith
argument_list|(
name|BLOB_FILE_PATTERN_PREFIX
argument_list|)
argument_list|,
literal|"Filename(%s) must start with %s"
argument_list|,
name|filename
argument_list|,
name|BLOB_FILE_PATTERN_PREFIX
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|filename
operator|.
name|endsWith
argument_list|(
name|BLOB_FILE_PATTERN_SUFFIX
argument_list|)
argument_list|,
literal|"Filename(%s) must end with %s"
argument_list|,
name|filename
argument_list|,
name|BLOB_FILE_PATTERN_SUFFIX
argument_list|)
expr_stmt|;
name|String
name|timestampStr
init|=
name|filename
operator|.
name|substring
argument_list|(
name|BLOB_FILE_PATTERN_PREFIX
operator|.
name|length
argument_list|()
argument_list|,
name|filename
operator|.
name|length
argument_list|()
operator|-
name|BLOB_FILE_PATTERN_SUFFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|timestampStr
argument_list|)
return|;
block|}
specifier|private
name|void
name|addDeletedBlobs
parameter_list|(
name|Collection
argument_list|<
name|BlobIdInfoStruct
argument_list|>
name|deletedBlobs
parameter_list|)
block|{
for|for
control|(
name|BlobIdInfoStruct
name|info
range|:
name|deletedBlobs
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|deletedBlobs
operator|.
name|offer
argument_list|(
name|info
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Timed out while offer-ing {} into queue."
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while adding "
operator|+
name|info
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Added {} to be flushed"
argument_list|,
name|deletedBlobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|deletedBlobsFileWriter
operator|.
name|scheduleFileFlushIfNeeded
argument_list|()
expr_stmt|;
block|}
specifier|private
class|class
name|DeletedBlobsFileWriter
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|fileFlushScheduled
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|volatile
name|String
name|inUseFileName
init|=
literal|null
decl_stmt|;
specifier|private
specifier|synchronized
name|void
name|flushDeletedBlobs
parameter_list|()
block|{
name|List
argument_list|<
name|BlobIdInfoStruct
argument_list|>
name|localDeletedBlobs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|deletedBlobs
operator|.
name|peek
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|localDeletedBlobs
operator|.
name|add
argument_list|(
name|deletedBlobs
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|rootDirectory
argument_list|,
name|getBlobFileName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|start
init|=
name|PERF_LOG
operator|.
name|start
argument_list|()
decl_stmt|;
name|FileUtils
operator|.
name|writeLines
argument_list|(
name|outFile
argument_list|,
name|localDeletedBlobs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PERF_LOG
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"Flushing deleted blobs"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't write out to "
operator|+
name|outFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|scheduleFileFlushIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|fileFlushScheduled
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|releaseInUseFile
parameter_list|()
block|{
name|inUseFileName
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|flushDeletedBlobs
argument_list|()
expr_stmt|;
name|fileFlushScheduled
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getBlobFileName
parameter_list|()
block|{
if|if
condition|(
name|inUseFileName
operator|==
literal|null
condition|)
block|{
name|inUseFileName
operator|=
name|String
operator|.
name|format
argument_list|(
name|BLOB_FILE_PATTERN
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|inUseFileName
return|;
block|}
block|}
specifier|private
class|class
name|DeletedBlobCollector
implements|implements
name|BlobDeletionCallback
block|{
name|List
argument_list|<
name|BlobIdInfoStruct
argument_list|>
name|deletedBlobs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|deleted
parameter_list|(
name|String
name|blobId
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|deletedBlobs
operator|.
name|add
argument_list|(
operator|new
name|BlobIdInfoStruct
argument_list|(
name|blobId
argument_list|,
name|ids
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commitProgress
parameter_list|(
name|IndexProgress
name|indexProgress
parameter_list|)
block|{
if|if
condition|(
name|indexProgress
operator|!=
name|IndexProgress
operator|.
name|COMMIT_SUCCEDED
operator|&&
name|indexProgress
operator|!=
name|IndexProgress
operator|.
name|COMMIT_FAILED
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"We only care for commit success/failure"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|indexProgress
operator|==
name|IndexProgress
operator|.
name|COMMIT_SUCCEDED
condition|)
block|{
name|addDeletedBlobs
argument_list|(
name|deletedBlobs
argument_list|)
expr_stmt|;
block|}
name|deletedBlobs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|BlobIdInfoStruct
block|{
specifier|final
name|String
name|blobId
decl_stmt|;
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
decl_stmt|;
name|BlobIdInfoStruct
parameter_list|(
name|String
name|blobId
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|this
operator|.
name|blobId
operator|=
name|blobId
expr_stmt|;
name|this
operator|.
name|ids
operator|=
name|ids
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
name|String
operator|.
name|format
argument_list|(
literal|"%s|%s|%s"
argument_list|,
name|blobId
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|"|"
argument_list|)
operator|.
name|join
argument_list|(
name|ids
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
