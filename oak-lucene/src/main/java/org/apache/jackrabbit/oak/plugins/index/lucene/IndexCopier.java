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
name|HashSet
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
name|atomic
operator|.
name|AtomicInteger
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
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|Charsets
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
name|Maps
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|hash
operator|.
name|Hashing
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
name|lucene
operator|.
name|store
operator|.
name|BaseDirectory
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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

begin_class
class|class
name|IndexCopier
implements|implements
name|CopyOnReadStatsMBean
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|REMOTE_ONLY
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"segments.gen"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
specifier|final
name|File
name|indexRootDir
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|localReadCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|remoteReadCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|invalidFileCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|downloadSize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|downloadTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexPathMapping
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|indexPathVersionMapping
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|public
name|IndexCopier
parameter_list|(
name|Executor
name|executor
parameter_list|,
name|File
name|indexRootDir
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|indexRootDir
operator|=
name|indexRootDir
expr_stmt|;
block|}
specifier|public
name|Directory
name|wrap
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|,
name|Directory
name|remote
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|local
init|=
name|createLocalDir
argument_list|(
name|indexPath
argument_list|,
name|definition
argument_list|)
decl_stmt|;
return|return
operator|new
name|CopyOnReadDirectory
argument_list|(
name|remote
argument_list|,
name|local
argument_list|)
return|;
block|}
specifier|protected
name|Directory
name|createLocalDir
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|indexDir
init|=
name|getIndexDir
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|newVersion
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|definition
operator|.
name|getReindexCount
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|versionedIndexDir
init|=
operator|new
name|File
argument_list|(
name|indexDir
argument_list|,
name|newVersion
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|versionedIndexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|versionedIndexDir
operator|.
name|mkdirs
argument_list|()
argument_list|,
literal|"Cannot create directory %s"
argument_list|,
name|versionedIndexDir
argument_list|)
expr_stmt|;
block|}
name|indexPathMapping
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|result
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|versionedIndexDir
argument_list|)
decl_stmt|;
name|String
name|oldVersion
init|=
name|indexPathVersionMapping
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|newVersion
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newVersion
operator|.
name|equals
argument_list|(
name|oldVersion
argument_list|)
operator|&&
name|oldVersion
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|DeleteOldDirOnClose
argument_list|(
name|result
argument_list|,
operator|new
name|File
argument_list|(
name|indexDir
argument_list|,
name|oldVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|File
name|getIndexDir
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|String
name|subDir
init|=
name|Hashing
operator|.
name|sha256
argument_list|()
operator|.
name|hashString
argument_list|(
name|indexPath
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|indexRootDir
argument_list|,
name|subDir
argument_list|)
return|;
block|}
comment|/**      * Directory implementation which lazily copies the index files from a      * remote directory in background.      */
specifier|private
class|class
name|CopyOnReadDirectory
extends|extends
name|BaseDirectory
block|{
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
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|FileReference
argument_list|>
name|files
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|public
name|CopyOnReadDirectory
parameter_list|(
name|Directory
name|remote
parameter_list|,
name|Directory
name|local
parameter_list|)
throws|throws
name|IOException
block|{
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
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|remote
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|remote
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
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
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
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
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|remote
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
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
name|REMOTE_ONLY
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
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
name|FileReference
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
name|remoteReadCount
operator|.
name|incrementAndGet
argument_list|()
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
name|FileReference
name|toPut
init|=
operator|new
name|FileReference
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|FileReference
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
return|return
name|toPut
operator|.
name|openLocalInput
argument_list|(
name|context
argument_list|)
return|;
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
specifier|private
name|void
name|copy
parameter_list|(
specifier|final
name|FileReference
name|reference
parameter_list|)
block|{
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
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|downloadTime
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
name|downloadSize
operator|.
name|addAndGet
argument_list|(
name|remote
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
comment|//Do a simple consistency check. Ideally Lucene index files are never
comment|//updated but still do a check if the copy is consistent
if|if
condition|(
name|localLength
operator|!=
name|remoteLength
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Found local copy for {} in {} but size of local {} differs from remote {}. "
operator|+
literal|"Content would be read from remote file only"
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
name|invalidFileCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|reference
operator|.
name|markValid
argument_list|()
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
literal|"Error occurred while copying file [{}] "
operator|+
literal|"from {} to {}"
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
literal|"Error occurred while deleting corrupted file [{}] from [{}]"
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**          * On close file which are not present in remote are removed from local.          * CopyOnReadDir is opened at different revisions of the index state          *          * CDir1 - V1          * CDir2 - V2          *          * Its possible that two different IndexSearcher are opened at same local          * directory but pinned to different revisions. So while removing it must          * be ensured that any currently opened IndexSearcher does not get affected.          * The way IndexSearchers get created in IndexTracker it ensures that new searcher          * pinned to newer revision gets opened first and then existing ones are closed.          *          *          * @throws IOException          */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Always remove old index file on close as it ensures that
comment|//no other IndexSearcher are opened with previous revision of Index due to
comment|//way IndexTracker closes IndexNode. At max there would be only two IndexNode
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
literal|"Error occurred while removing deleted files from Local {}, "
operator|+
literal|"Remote {}"
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
literal|"Error occurred while closing directory "
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
specifier|private
name|void
name|removeDeletedFiles
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Files present in dest but not present in source have to be deleted
name|Set
argument_list|<
name|String
argument_list|>
name|filesToBeDeleted
init|=
name|Sets
operator|.
name|difference
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|local
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|remote
operator|.
name|listAll
argument_list|()
argument_list|)
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
try|try
block|{
name|local
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|failedToDelete
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Error occurred while removing deleted file {} from Local {} "
argument_list|,
name|fileName
argument_list|,
name|local
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Error occurred while deleting following files from the local index directory [{}]. "
operator|+
literal|"This can happen on Windows based system. Attempt would be made to remove them "
operator|+
literal|"in next attempt "
argument_list|,
name|local
argument_list|,
name|failedToDelete
argument_list|)
expr_stmt|;
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
literal|"Following files have been removed from Lucene "
operator|+
literal|"index directory [{}]"
argument_list|,
name|filesToBeDeleted
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|FileReference
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
name|FileReference
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
name|localReadCount
operator|.
name|incrementAndGet
argument_list|()
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
specifier|private
class|class
name|DeleteOldDirOnClose
extends|extends
name|FilterDirectory
block|{
specifier|private
specifier|final
name|File
name|oldIndexDir
decl_stmt|;
specifier|protected
name|DeleteOldDirOnClose
parameter_list|(
name|Directory
name|in
parameter_list|,
name|File
name|oldIndexDir
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|oldIndexDir
operator|=
name|oldIndexDir
expr_stmt|;
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
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|oldIndexDir
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Removed old index content from {} "
argument_list|,
name|oldIndexDir
argument_list|)
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
literal|"Not able to remove old version of copied index at {}"
argument_list|,
name|oldIndexDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//~------------------------------------------< CopyOnReadStatsMBean>
annotation|@
name|Override
specifier|public
name|TabularData
name|getIndexPathMapping
parameter_list|()
block|{
name|TabularDataSupport
name|tds
decl_stmt|;
try|try
block|{
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
name|IndexMappingData
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Lucene Index Stats"
argument_list|,
name|IndexMappingData
operator|.
name|TYPE
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcrPath"
block|}
argument_list|)
decl_stmt|;
name|tds
operator|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|indexPathMapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|tds
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|IndexMappingData
operator|.
name|TYPE
argument_list|,
name|IndexMappingData
operator|.
name|FIELD_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|e
operator|.
name|getKey
argument_list|()
block|,
name|e
operator|.
name|getValue
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OpenDataException
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
return|return
name|tds
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLocalReadCount
parameter_list|()
block|{
return|return
name|localReadCount
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRemoteReadCount
parameter_list|()
block|{
return|return
name|remoteReadCount
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|int
name|getInvalidFileCount
parameter_list|()
block|{
return|return
name|invalidFileCount
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDownloadSize
parameter_list|()
block|{
return|return
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|downloadSize
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDownloadTime
parameter_list|()
block|{
return|return
name|downloadTime
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalIndexSize
parameter_list|()
block|{
return|return
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
name|indexRootDir
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|IndexMappingData
block|{
specifier|static
specifier|final
name|String
index|[]
name|FIELD_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"jcrPath"
block|,
literal|"fsPath"
block|,         }
decl_stmt|;
specifier|static
specifier|final
name|String
index|[]
name|FIELD_DESCRIPTIONS
init|=
operator|new
name|String
index|[]
block|{
literal|"JCR Path"
block|,
literal|"Filesystem Path"
block|,         }
decl_stmt|;
specifier|static
specifier|final
name|OpenType
index|[]
name|FIELD_TYPES
init|=
operator|new
name|OpenType
index|[]
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,         }
decl_stmt|;
specifier|static
specifier|final
name|CompositeType
name|TYPE
init|=
name|createCompositeType
argument_list|()
decl_stmt|;
specifier|static
name|CompositeType
name|createCompositeType
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CompositeType
argument_list|(
name|IndexMappingData
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Composite data type for Index Mapping Data"
argument_list|,
name|IndexMappingData
operator|.
name|FIELD_NAMES
argument_list|,
name|IndexMappingData
operator|.
name|FIELD_DESCRIPTIONS
argument_list|,
name|IndexMappingData
operator|.
name|FIELD_TYPES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
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
block|}
block|}
end_class

end_unit

