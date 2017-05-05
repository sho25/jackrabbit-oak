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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|PrintStream
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
name|io
operator|.
name|StringWriter
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|ByteStreams
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
name|CountingInputStream
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
name|api
operator|.
name|Blob
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
name|PropertyState
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
name|Root
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
name|api
operator|.
name|Type
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
name|PathUtils
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
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
name|OakDirectory
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
name|writer
operator|.
name|MultiplexersLucene
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
name|tree
operator|.
name|RootFactory
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
name|NodeState
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
name|NodeStateUtils
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
name|ReadOnlyBuilder
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
name|index
operator|.
name|CheckIndex
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
name|index
operator|.
name|DirectoryReader
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
name|IOContext
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

begin_class
specifier|public
class|class
name|IndexConsistencyChecker
block|{
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
name|NodeState
name|rootState
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|File
name|workDirRoot
decl_stmt|;
specifier|private
name|File
name|workDir
decl_stmt|;
specifier|public
enum|enum
name|Level
block|{
comment|/**          * Consistency check would only check if all blobs referred by index nodes          * are present in BlobStore          */
name|BLOBS_ONLY
block|,
comment|/**          * Performs full check via {@code org.apache.lucene.index.CheckIndex}. This          * reads whole index and hence can take time          */
name|FULL
block|}
specifier|public
specifier|static
class|class
name|Result
block|{
comment|/** True if no problems were found with the index. */
specifier|public
name|boolean
name|clean
decl_stmt|;
specifier|public
name|boolean
name|typeMismatch
decl_stmt|;
specifier|public
name|boolean
name|missingBlobs
decl_stmt|;
specifier|public
name|boolean
name|blobSizeMismatch
decl_stmt|;
specifier|public
name|String
name|indexPath
decl_stmt|;
specifier|public
name|long
name|binaryPropSize
decl_stmt|;
specifier|public
name|List
argument_list|<
name|FileSizeStatus
argument_list|>
name|invalidBlobIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|missingBlobIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|DirectoryStatus
argument_list|>
name|dirStatus
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Stopwatch
name|watch
decl_stmt|;
specifier|public
name|void
name|dump
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
if|if
condition|(
name|clean
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"%s => VALID%n"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"%s => INVALID%n"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|printf
argument_list|(
literal|"\tSize : %s%n"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|binaryPropSize
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|missingBlobIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"Missing blobs"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|missingBlobIds
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"\t - "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|invalidBlobIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"Invalid blobs"
argument_list|)
expr_stmt|;
for|for
control|(
name|FileSizeStatus
name|status
range|:
name|invalidBlobIds
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"\t - "
operator|+
name|status
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|DirectoryStatus
name|dirStatus
range|:
name|dirStatus
control|)
block|{
name|dirStatus
operator|.
name|dump
argument_list|(
name|pw
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|printf
argument_list|(
literal|"Time taken : %s%n"
argument_list|,
name|watch
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
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|dump
argument_list|(
name|pw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|DirectoryStatus
block|{
specifier|public
specifier|final
name|String
name|dirName
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|missingFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|FileSizeStatus
argument_list|>
name|filesWithSizeMismatch
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|clean
decl_stmt|;
specifier|public
name|long
name|size
decl_stmt|;
specifier|public
name|CheckIndex
operator|.
name|Status
name|status
decl_stmt|;
specifier|public
name|long
name|numDocs
decl_stmt|;
specifier|public
name|DirectoryStatus
parameter_list|(
name|String
name|dirName
parameter_list|)
block|{
name|this
operator|.
name|dirName
operator|=
name|dirName
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"Directory : "
operator|+
name|dirName
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"\tSize     : %s%n"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"\tNum docs : %d%n"
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|missingFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"\tMissing Files"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|missingFiles
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"\t\t- "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|filesWithSizeMismatch
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"Invalid files"
argument_list|)
expr_stmt|;
for|for
control|(
name|FileSizeStatus
name|status
range|:
name|filesWithSizeMismatch
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"\t - "
operator|+
name|status
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"\tCheckIndex status : %s%n"
argument_list|,
name|status
operator|.
name|clean
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|FileSizeStatus
block|{
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|long
name|actualSize
decl_stmt|;
specifier|public
specifier|final
name|long
name|expectedSize
decl_stmt|;
specifier|public
name|FileSizeStatus
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|actualSize
parameter_list|,
name|long
name|expectedSize
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|actualSize
operator|=
name|actualSize
expr_stmt|;
name|this
operator|.
name|expectedSize
operator|=
name|expectedSize
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
literal|"%s => expected %d, actual %d"
argument_list|,
name|name
argument_list|,
name|expectedSize
argument_list|,
name|actualSize
argument_list|)
return|;
block|}
block|}
comment|/**      * Checks the index at given path for consistency      *      * @param rootState root state of repository      * @param indexPath path of index which needs to be checked      * @param workDirRoot directory which would be used for copying the index file locally to perform      *                    check. File would be created in a subdirectory. If the index is valid      *                    then the files would be removed otherwise whatever files have been copied      *                    would be left as is      */
specifier|public
name|IndexConsistencyChecker
parameter_list|(
name|NodeState
name|rootState
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|File
name|workDirRoot
parameter_list|)
block|{
name|this
operator|.
name|rootState
operator|=
name|checkNotNull
argument_list|(
name|rootState
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|checkNotNull
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|workDirRoot
operator|=
name|checkNotNull
argument_list|(
name|workDirRoot
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Result
name|check
parameter_list|(
name|Level
name|level
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|check
argument_list|(
name|level
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
name|Result
name|check
parameter_list|(
name|Level
name|level
parameter_list|,
name|boolean
name|cleanWorkDir
parameter_list|)
throws|throws
name|IOException
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|result
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|watch
operator|=
name|watch
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}] Starting check"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|checkBlobs
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|==
name|Level
operator|.
name|FULL
operator|&&
name|result
operator|.
name|clean
condition|)
block|{
name|checkIndex
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|clean
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"[] No problems were detected with this index. Time taken {}"
argument_list|,
name|indexPath
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[] Problems detected with this index. Time taken {}"
argument_list|,
name|indexPath
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cleanWorkDir
condition|)
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|workDir
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"[] Index files are copied to {}"
argument_list|,
name|indexPath
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|checkIndex
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|NodeState
name|idx
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|rootState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
argument_list|,
name|indexPath
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|workDir
operator|=
name|createWorkDir
argument_list|(
name|workDirRoot
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|indexPath
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|dirName
range|:
name|idx
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
comment|//TODO Check for SuggestionDirectory Pending
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|dirName
argument_list|)
operator|&&
name|MultiplexersLucene
operator|.
name|isIndexDirName
argument_list|(
name|dirName
argument_list|)
condition|)
block|{
name|DirectoryStatus
name|dirStatus
init|=
operator|new
name|DirectoryStatus
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|result
operator|.
name|dirStatus
operator|.
name|add
argument_list|(
name|dirStatus
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Checking directory {}"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
try|try
block|{
name|checkIndexDirectory
argument_list|(
name|dirStatus
argument_list|,
name|idx
argument_list|,
name|defn
argument_list|,
name|workDir
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|dirStatus
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}][{}] Error occurred while performing directory check"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|dirStatus
operator|.
name|clean
condition|)
block|{
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|checkIndexDirectory
parameter_list|(
name|DirectoryStatus
name|dirStatus
parameter_list|,
name|NodeState
name|idx
parameter_list|,
name|IndexDefinition
name|defn
parameter_list|,
name|File
name|workDir
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|idxDir
init|=
name|createWorkDir
argument_list|(
name|workDir
argument_list|,
name|dirName
argument_list|)
decl_stmt|;
name|Directory
name|sourceDir
init|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|idx
argument_list|)
argument_list|,
name|dirName
argument_list|,
name|defn
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Directory
name|targetDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|idxDir
argument_list|)
decl_stmt|;
name|boolean
name|clean
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|sourceDir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"[{}][{}] Checking {}"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|,
name|file
argument_list|)
expr_stmt|;
try|try
block|{
name|sourceDir
operator|.
name|copy
argument_list|(
name|targetDir
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignore
parameter_list|)
block|{
name|dirStatus
operator|.
name|missingFiles
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|clean
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}][{}] File {} missing"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|targetDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
operator|!=
name|sourceDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|FileSizeStatus
name|fileStatus
init|=
operator|new
name|FileSizeStatus
argument_list|(
name|file
argument_list|,
name|targetDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|,
name|sourceDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|dirStatus
operator|.
name|filesWithSizeMismatch
operator|.
name|add
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
name|clean
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}][{}] File size mismatch {}"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|,
name|fileStatus
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dirStatus
operator|.
name|size
operator|+=
name|sourceDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}][{}] File {} is consistent"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clean
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"[{}][{}] Directory content found to be consistent. Proceeding to IndexCheck"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
name|CheckIndex
name|ci
init|=
operator|new
name|CheckIndex
argument_list|(
name|targetDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|ci
operator|.
name|setInfoStream
argument_list|(
operator|new
name|LoggingPrintStream
argument_list|(
name|log
argument_list|)
argument_list|,
name|log
operator|.
name|isTraceEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dirStatus
operator|.
name|status
operator|=
name|ci
operator|.
name|checkIndex
argument_list|()
expr_stmt|;
name|dirStatus
operator|.
name|clean
operator|=
name|dirStatus
operator|.
name|status
operator|.
name|clean
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}][{}] IndexCheck was successful. Proceeding to open DirectoryReader"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dirStatus
operator|.
name|clean
condition|)
block|{
name|DirectoryReader
name|dirReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|targetDir
argument_list|)
decl_stmt|;
name|dirStatus
operator|.
name|numDocs
operator|=
name|dirReader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}][{}] DirectoryReader can be opened"
argument_list|,
name|indexPath
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
name|dirReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//~---------------------------------------< Blob Validation>
specifier|private
name|void
name|checkBlobs
parameter_list|(
name|Result
name|result
parameter_list|)
block|{
name|Root
name|root
init|=
name|RootFactory
operator|.
name|createReadOnlyRoot
argument_list|(
name|rootState
argument_list|)
decl_stmt|;
name|Tree
name|idx
init|=
name|root
operator|.
name|getTree
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|PropertyState
name|type
init|=
name|idx
operator|.
name|getProperty
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
name|checkBlobs
argument_list|(
name|result
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|typeMismatch
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkBlobs
parameter_list|(
name|Result
name|result
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
for|for
control|(
name|PropertyState
name|ps
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
if|if
condition|(
name|ps
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ps
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|checkBlob
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|,
name|b
argument_list|,
name|tree
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|checkBlob
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|,
name|b
argument_list|,
name|tree
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|checkBlobs
argument_list|(
name|result
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkBlob
parameter_list|(
name|String
name|propName
parameter_list|,
name|Blob
name|blob
parameter_list|,
name|Tree
name|tree
parameter_list|,
name|Result
name|result
parameter_list|)
block|{
name|String
name|id
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
name|String
name|blobPath
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s/%s/%s"
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|propName
argument_list|,
name|id
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
name|CountingInputStream
name|cis
init|=
operator|new
name|CountingInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyLarge
argument_list|(
name|cis
argument_list|,
name|ByteStreams
operator|.
name|nullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cis
operator|.
name|getCount
argument_list|()
operator|!=
name|blob
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid blob %s. Length mismatch - expected ${%d} -> found ${%d}"
argument_list|,
name|blobPath
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|,
name|cis
operator|.
name|getCount
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|invalidBlobIds
operator|.
name|add
argument_list|(
operator|new
name|FileSizeStatus
argument_list|(
name|blobPath
argument_list|,
name|cis
operator|.
name|getCount
argument_list|()
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] {}"
argument_list|,
name|indexPath
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|blobSizeMismatch
operator|=
literal|true
expr_stmt|;
block|}
name|result
operator|.
name|binaryPropSize
operator|+=
name|cis
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Error occurred reading blob at {}"
argument_list|,
name|indexPath
argument_list|,
name|blobPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|result
operator|.
name|missingBlobIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|result
operator|.
name|clean
operator|=
literal|false
expr_stmt|;
name|result
operator|.
name|missingBlobs
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|//~-----------------------------------------------< utility>
specifier|private
specifier|static
name|File
name|createWorkDir
parameter_list|(
name|File
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fsSafeName
init|=
name|IndexRootDirectory
operator|.
name|getFSSafeName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|parent
argument_list|,
name|fsSafeName
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
return|return
name|dir
return|;
block|}
comment|/**      * Adapter to pipe info messages from lucene into log messages.      */
specifier|private
specifier|static
specifier|final
class|class
name|LoggingPrintStream
extends|extends
name|PrintStream
block|{
comment|/** Buffer print calls until a newline is written */
specifier|private
specifier|final
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
decl_stmt|;
specifier|public
name|LoggingPrintStream
parameter_list|(
name|Logger
name|log
parameter_list|)
block|{
name|super
argument_list|(
name|ByteStreams
operator|.
name|nullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
specifier|public
name|void
name|print
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|println
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

