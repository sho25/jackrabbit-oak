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
operator|.
name|directory
package|;
end_package

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
name|Arrays
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|Executor
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|ImmutableSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|lucene
operator|.
name|IndexCopier
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
name|FilterDirectory
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|collect
operator|.
name|Maps
operator|.
name|newConcurrentMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|stream
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

begin_comment
comment|/**  * Directory implementation which lazily copies the index files from a  * remote directory in background.  */
end_comment

begin_class
specifier|public
class|class
name|CopyOnReadDirectory
extends|extends
name|FilterDirectory
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
name|CopyOnReadDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|log
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_MARGIN_MILLIS_NAME
init|=
literal|"oak.lucene.delete.margin"
decl_stmt|;
specifier|public
specifier|final
name|long
name|DELETE_MARGIN_MILLIS
init|=
name|Long
operator|.
name|getLong
argument_list|(
name|DELETE_MARGIN_MILLIS_NAME
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|remote
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|local
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|prefetch
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|// exported as package private to be useful in tests
specifier|static
specifier|final
name|String
name|WAIT_OTHER_COPY_SYSPROP_NAME
init|=
literal|"cor.waitCopyMillis"
decl_stmt|;
name|long
name|waitOtherCopyTimeoutMillis
init|=
name|Long
operator|.
name|getLong
argument_list|(
name|WAIT_OTHER_COPY_SYSPROP_NAME
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|CORFileReference
argument_list|>
name|files
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|public
name|CopyOnReadDirectory
parameter_list|(
name|IndexCopier
name|indexCopier
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|Directory
name|local
parameter_list|,
name|boolean
name|prefetch
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|Executor
name|executor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|remote
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexCopier
operator|=
name|indexCopier
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|remote
operator|=
name|remote
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
name|this
operator|.
name|prefetch
operator|=
name|prefetch
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
if|if
condition|(
name|prefetch
condition|)
block|{
name|prefetchIndexFiles
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot delete in a ReadOnly directory"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot write in a ReadOnly directory"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|IndexCopier
operator|.
name|REMOTE_ONLY
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] opening remote only file {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|remote
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
name|CORFileReference
name|ref
init|=
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ref
operator|.
name|isLocalValid
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] opening existing local file {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|files
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|openLocalInput
argument_list|(
name|context
argument_list|)
return|;
block|}
else|else
block|{
name|indexCopier
operator|.
name|readFromRemote
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|logRemoteAccess
argument_list|(
literal|"[{}] opening existing remote file as local version is not valid {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|remote
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
comment|//If file does not exist then just delegate to remote and not
comment|//schedule a copy task
if|if
condition|(
operator|!
name|remote
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"[{}] Looking for non existent file {}. Current known files {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|remote
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|remote
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
name|CORFileReference
name|toPut
init|=
operator|new
name|CORFileReference
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|CORFileReference
name|old
init|=
name|files
operator|.
name|putIfAbsent
argument_list|(
name|name
argument_list|,
name|toPut
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] scheduled local copy for {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|copy
argument_list|(
name|toPut
argument_list|)
expr_stmt|;
block|}
comment|//If immediate executor is used the result would be ready right away
if|if
condition|(
name|toPut
operator|.
name|isLocalValid
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] opening new local file {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|toPut
operator|.
name|openLocalInput
argument_list|(
name|context
argument_list|)
return|;
block|}
name|logRemoteAccess
argument_list|(
literal|"[{}] opening new remote file {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|indexCopier
operator|.
name|readFromRemote
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|remote
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
specifier|public
name|Directory
name|getLocal
parameter_list|()
block|{
return|return
name|local
return|;
block|}
specifier|private
name|void
name|copy
parameter_list|(
specifier|final
name|CORFileReference
name|reference
parameter_list|)
block|{
name|indexCopier
operator|.
name|scheduledForCopy
argument_list|()
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|indexCopier
operator|.
name|copyDone
argument_list|()
expr_stmt|;
name|copyFilesToLocal
argument_list|(
name|reference
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|prefetchIndexFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
name|int
name|copyCount
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|copiedFileNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|remote
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|IndexCopier
operator|.
name|REMOTE_ONLY
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|CORFileReference
name|fileRef
init|=
operator|new
name|CORFileReference
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|files
operator|.
name|putIfAbsent
argument_list|(
name|name
argument_list|,
name|fileRef
argument_list|)
expr_stmt|;
name|long
name|fileSize
init|=
name|copyFilesToLocal
argument_list|(
name|fileRef
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileSize
operator|>
literal|0
condition|)
block|{
name|copyCount
operator|++
expr_stmt|;
name|totalSize
operator|+=
name|fileSize
expr_stmt|;
name|copiedFileNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|local
operator|.
name|sync
argument_list|(
name|copiedFileNames
argument_list|)
expr_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"[{}] Copied {} files totaling {}"
argument_list|,
name|indexPath
argument_list|,
name|copyCount
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|copyFilesToLocal
parameter_list|(
name|CORFileReference
name|reference
parameter_list|,
name|boolean
name|sync
parameter_list|,
name|boolean
name|logDuration
parameter_list|)
block|{
name|String
name|name
init|=
name|reference
operator|.
name|name
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|boolean
name|copyAttempted
init|=
literal|false
decl_stmt|;
name|long
name|fileSize
init|=
literal|0
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|local
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|long
name|perfStart
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|logDuration
condition|)
block|{
name|perfStart
operator|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|fileSize
operator|=
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|LocalIndexFile
name|file
init|=
operator|new
name|LocalIndexFile
argument_list|(
name|local
argument_list|,
name|name
argument_list|,
name|fileSize
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|indexCopier
operator|.
name|startCopy
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|copyAttempted
operator|=
literal|true
expr_stmt|;
name|remote
operator|.
name|copy
argument_list|(
name|local
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
expr_stmt|;
name|reference
operator|.
name|markValid
argument_list|()
expr_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|local
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexCopier
operator|.
name|doneCopy
argument_list|(
name|file
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|logDuration
condition|)
block|{
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|perfStart
argument_list|,
literal|0
argument_list|,
literal|"[{}] Copied file {} of size {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|,
name|humanReadableByteCount
argument_list|(
name|fileSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|remoteLength
init|=
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|LocalIndexFile
name|file
init|=
operator|new
name|LocalIndexFile
argument_list|(
name|local
argument_list|,
name|name
argument_list|,
name|remoteLength
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// as a local file exists, attempt a wait for completion of any potential ongoing concurrent copy
name|indexCopier
operator|.
name|waitForCopyCompletion
argument_list|(
name|file
argument_list|,
name|waitOtherCopyTimeoutMillis
argument_list|)
expr_stmt|;
name|long
name|localLength
init|=
name|local
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//Do a simple consistency check. Ideally Lucene index files are never
comment|//updated but still do a check if the copy is consistent
if|if
condition|(
name|localLength
operator|!=
name|remoteLength
condition|)
block|{
if|if
condition|(
operator|!
name|indexCopier
operator|.
name|isCopyInProgress
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Found local copy for {} in {} but size of local {} differs from remote {}. "
operator|+
literal|"Content would be read from remote file only"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|,
name|local
argument_list|,
name|localLength
argument_list|,
name|remoteLength
argument_list|)
expr_stmt|;
name|indexCopier
operator|.
name|foundInvalidFile
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logRemoteAccess
argument_list|(
literal|"[{}] Found in progress copy of file {}. Would read from remote"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|reference
operator|.
name|markValid
argument_list|()
expr_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] found local copy of file {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//TODO In case of exception there would not be any other attempt
comment|//to download the file. Look into support for retry
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred while copying file [{}] from {} to {}"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|,
name|remote
argument_list|,
name|local
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|copyAttempted
operator|&&
operator|!
name|success
condition|)
block|{
try|try
block|{
if|if
condition|(
name|local
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|local
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred while deleting corrupted file [{}] from [{}]"
argument_list|,
name|indexPath
argument_list|,
name|name
argument_list|,
name|local
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|fileSize
return|;
block|}
comment|/**      * On close file which are not present in remote are removed from local.      * CopyOnReadDir is opened at different revisions of the index state      *      * CDir1 - V1      * CDir2 - V2      *      * Its possible that two different IndexSearcher are opened at same local      * directory but pinned to different revisions. So while removing it must      * be ensured that any currently opened IndexSearcher does not get affected.      * The way IndexSearchers get created in IndexTracker it ensures that new searcher      * pinned to newer revision gets opened first and then existing ones are closed.      *      *      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
comment|//Always remove old index file on close as it ensures that
comment|//no other IndexSearcher are opened with previous revision of Index due to
comment|//way IndexTracker closes LuceneIndexNode. At max there would be only two LuceneIndexNode
comment|//opened pinned to different revision of same Lucene index
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|removeDeletedFiles
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred while removing deleted files from Local {}, Remote {}"
argument_list|,
name|indexPath
argument_list|,
name|local
argument_list|,
name|remote
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|//This would also remove old index files if current
comment|//directory was based on newerRevision as local would
comment|//be of type DeleteOldDirOnClose
name|local
operator|.
name|close
argument_list|()
expr_stmt|;
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred while closing directory "
argument_list|,
name|indexPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|String
operator|.
name|format
argument_list|(
literal|"[COR] Local %s, Remote %s"
argument_list|,
name|local
argument_list|,
name|remote
argument_list|)
return|;
block|}
specifier|private
name|void
name|removeDeletedFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|remoteFiles
init|=
name|stream
argument_list|(
name|remote
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|name
lambda|->
operator|!
name|IndexCopier
operator|.
name|REMOTE_ONLY
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|maxTS
init|=
name|IndexCopier
operator|.
name|getNewestLocalFSTimestampFor
argument_list|(
name|remoteFiles
argument_list|,
name|local
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxTS
operator|==
operator|-
literal|1
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't compute safe timestamp to delete files from {}"
argument_list|,
name|local
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// subtract DELETE_MARGIN_MILLIS from maxTS for safety (you can never be too careful with time)
specifier|final
name|long
name|deleteBeforeTS
init|=
name|maxTS
operator|-
name|DELETE_MARGIN_MILLIS
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filesToBeDeleted
init|=
comment|// Files present locally
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|local
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
comment|// but not in my view
operator|.
name|filter
argument_list|(
name|name
lambda|->
operator|!
name|remoteFiles
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
comment|// and also older than a safe timestamp (deleteBeforeTS)
operator|.
name|filter
argument_list|(
name|name
lambda|->
name|IndexCopier
operator|.
name|isFileModifiedBefore
argument_list|(
name|name
argument_list|,
name|local
argument_list|,
name|deleteBeforeTS
argument_list|)
argument_list|)
comment|// can be deleted
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failedToDelete
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
name|filesToBeDeleted
control|)
block|{
name|boolean
name|deleted
init|=
name|indexCopier
operator|.
name|deleteFile
argument_list|(
name|local
argument_list|,
name|fileName
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|failedToDelete
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
name|filesToBeDeleted
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|filesToBeDeleted
argument_list|)
expr_stmt|;
name|filesToBeDeleted
operator|.
name|removeAll
argument_list|(
name|failedToDelete
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|filesToBeDeleted
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"[{}] Following files have been removed from Lucene index directory {}"
argument_list|,
name|indexPath
argument_list|,
name|filesToBeDeleted
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|logRemoteAccess
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
if|if
condition|(
name|prefetch
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|format
argument_list|,
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|trace
argument_list|(
name|format
argument_list|,
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|CORFileReference
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|valid
decl_stmt|;
specifier|private
name|CORFileReference
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
name|boolean
name|isLocalValid
parameter_list|()
block|{
return|return
name|valid
return|;
block|}
name|IndexInput
name|openLocalInput
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|indexCopier
operator|.
name|readFromLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|local
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
name|void
name|markValid
parameter_list|()
block|{
name|this
operator|.
name|valid
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

