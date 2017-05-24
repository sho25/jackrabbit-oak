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
name|FileFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|ArrayListMultimap
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
name|Iterables
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
name|ListMultimap
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
name|hybrid
operator|.
name|NRTIndex
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_comment
comment|/**  * Represents the root directory on file system used for storing index copy locally.  * For each Oak index in repository it creates a container directory which is a function of  * index path and a unique id which stored in index node in Oak. Under that container  * directory various sub directories can be created for storing different types of indexes  */
end_comment

begin_class
specifier|public
class|class
name|IndexRootDirectory
block|{
specifier|static
specifier|final
name|int
name|MAX_NAME_LENGTH
init|=
literal|127
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_METADATA_FILE_NAME
init|=
literal|"index-details.txt"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FileFilter
name|LOCAL_DIR_FILTER
init|=
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|File
name|metaFile
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|INDEX_METADATA_FILE_NAME
argument_list|)
decl_stmt|;
return|return
name|metaFile
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
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
name|File
name|indexRootDir
decl_stmt|;
specifier|public
name|IndexRootDirectory
parameter_list|(
name|File
name|indexRootDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|indexRootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexRootDirectory
parameter_list|(
name|File
name|indexRootDir
parameter_list|,
name|boolean
name|gcOnStart
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexRootDir
operator|=
name|indexRootDir
expr_stmt|;
if|if
condition|(
name|gcOnStart
condition|)
block|{
name|gcIndexDirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
name|indexRootDir
argument_list|)
return|;
block|}
specifier|public
name|File
name|getIndexDir
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|uid
init|=
name|definition
operator|.
name|getUniqueId
argument_list|()
decl_stmt|;
if|if
condition|(
name|uid
operator|==
literal|null
condition|)
block|{
comment|//Old format
name|File
name|baseFolder
init|=
name|getOldFormatDir
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|version
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
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|baseFolder
argument_list|,
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|indexDir
operator|.
name|mkdirs
argument_list|()
argument_list|,
literal|"Not able to create folder [%s]"
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
return|return
name|indexDir
return|;
block|}
else|else
block|{
name|String
name|fileSystemSafeName
init|=
name|getIndexFolderBaseName
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|folderName
init|=
name|fileSystemSafeName
operator|+
literal|"-"
operator|+
name|uid
decl_stmt|;
name|File
name|baseFolder
init|=
operator|new
name|File
argument_list|(
name|indexRootDir
argument_list|,
name|folderName
argument_list|)
decl_stmt|;
comment|//Create a base folder<index node name>-<uid>
comment|//and add a readme file having index info
if|if
condition|(
operator|!
name|baseFolder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|baseFolder
operator|.
name|mkdir
argument_list|()
argument_list|,
literal|"Not able to create folder [%s]"
argument_list|,
name|baseFolder
argument_list|)
expr_stmt|;
name|File
name|readMe
init|=
operator|new
name|File
argument_list|(
name|baseFolder
argument_list|,
name|INDEX_METADATA_FILE_NAME
argument_list|)
decl_stmt|;
name|IndexMeta
name|meta
init|=
operator|new
name|IndexMeta
argument_list|(
name|indexPath
argument_list|,
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|meta
operator|.
name|writeTo
argument_list|(
name|readMe
argument_list|)
expr_stmt|;
block|}
comment|//Create index folder under that
name|File
name|indexFolder
init|=
operator|new
name|File
argument_list|(
name|baseFolder
argument_list|,
name|getFSSafeName
argument_list|(
name|dirName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexFolder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|indexFolder
operator|.
name|mkdir
argument_list|()
argument_list|,
literal|"Not able to create folder [%s]"
argument_list|,
name|indexFolder
argument_list|)
expr_stmt|;
block|}
return|return
name|indexFolder
return|;
block|}
block|}
comment|/**      * Returns the most recent directory for each index. If for an index 2 versions are present      * then it would return the most recent version      */
specifier|public
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|getAllLocalIndexes
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|mapping
init|=
name|getIndexesPerPath
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|mapping
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|e
range|:
name|mapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|getLocalIndexes
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|result
init|=
name|getIndexesPerPath
argument_list|()
operator|.
name|get
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|LocalIndexDir
operator|>
name|emptyList
argument_list|()
else|:
name|result
return|;
block|}
comment|/**      * Performs garbage collection of older version of index directories based on      * index directory derived from the passed sub directory.      *      * @param subDir one of the sub directories like 'default' etc. Such that      *               correct local index directory (container dir) can be checked for deletion      */
specifier|public
name|long
name|gcEmptyDirs
parameter_list|(
name|File
name|subDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|parent
init|=
name|checkNotNull
argument_list|(
name|subDir
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
name|LocalIndexDir
name|indexDir
init|=
name|findMatchingIndexDir
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|long
name|totalDeletedSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|indexDir
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|idxDirs
init|=
name|getLocalIndexes
argument_list|(
name|indexDir
operator|.
name|getJcrPath
argument_list|()
argument_list|)
decl_stmt|;
comment|//Flag to determine in given ordered list of LocalIndexDir
comment|//we found the dir which matched the parent of passed dir. So its safe
comment|//to delete those dirs and its successors in the list (as they are older)
name|boolean
name|matchingDirFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|LocalIndexDir
name|d
range|:
name|idxDirs
control|)
block|{
if|if
condition|(
name|d
operator|.
name|dir
operator|.
name|equals
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|matchingDirFound
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|matchingDirFound
operator|&&
name|d
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|long
name|dirSize
init|=
name|FileUtils
operator|.
name|sizeOf
argument_list|(
name|d
operator|.
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|d
operator|.
name|dir
argument_list|)
condition|)
block|{
name|totalDeletedSize
operator|+=
name|dirSize
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to deleted unused local index directory [{}]. "
operator|+
literal|"Deletion would be retried later again."
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|totalDeletedSize
operator|+=
name|deleteOldFormatDir
argument_list|(
name|d
operator|.
name|getJcrPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|totalDeletedSize
return|;
block|}
comment|/**      *<ul>      *<li>abc -> abc</li>      *<li>xy:abc -> xyabc</li>      *<li>/oak:index/abc -> abc</li>      *</ul>      *      * The resulting file name would be truncated to MAX_NAME_LENGTH      */
specifier|static
name|String
name|getIndexFolderBaseName
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|elements
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|PathUtils
operator|.
name|elements
argument_list|(
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|elements
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|//Max 3 nodeNames including oak:index which is the immediate parent for any indexPath
for|for
control|(
name|String
name|e
range|:
name|Iterables
operator|.
name|limit
argument_list|(
name|elements
argument_list|,
literal|3
argument_list|)
control|)
block|{
if|if
condition|(
literal|"oak:index"
operator|.
name|equals
argument_list|(
name|e
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|//Strip of any char outside of a-zA-Z0-9-
name|result
operator|.
name|add
argument_list|(
name|getFSSafeName
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
operator|.
name|join
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>
name|MAX_NAME_LENGTH
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|MAX_NAME_LENGTH
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
specifier|static
name|String
name|getPathHash
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
return|return
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
return|;
block|}
comment|/**      * The value is a sorted list with most recent version of index at the start      */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|getIndexesPerPath
parameter_list|()
throws|throws
name|IOException
block|{
name|File
index|[]
name|dirs
init|=
name|indexRootDir
operator|.
name|listFiles
argument_list|(
name|LOCAL_DIR_FILTER
argument_list|)
decl_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|LocalIndexDir
argument_list|>
name|pathToDirMap
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|indexDir
range|:
name|dirs
control|)
block|{
name|LocalIndexDir
name|localIndexDir
init|=
operator|new
name|LocalIndexDir
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|pathToDirMap
operator|.
name|get
argument_list|(
name|localIndexDir
operator|.
name|getJcrPath
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|localIndexDir
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|e
range|:
name|pathToDirMap
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|sortedDirs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sortedDirs
argument_list|,
name|Collections
operator|.
expr|<
name|LocalIndexDir
operator|>
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|sortedDirs
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Garbage collect old index directories. Should only be invoked at startup      * as it assumes that none of the directories are getting used      */
specifier|private
name|void
name|gcIndexDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|mapping
init|=
name|getIndexesPerPath
argument_list|()
decl_stmt|;
name|long
name|totalDeletedSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|e
range|:
name|mapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|dirs
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|//In startup mode we can be sure that no directory is in use
comment|//so be more aggressive in what we delete i.e. even not empty dir
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|dirs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LocalIndexDir
name|dir
init|=
name|dirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|long
name|dirSize
init|=
name|FileUtils
operator|.
name|sizeOf
argument_list|(
name|dir
operator|.
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dir
operator|.
name|dir
argument_list|)
condition|)
block|{
name|totalDeletedSize
operator|+=
name|dirSize
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to deleted unused local index directory [{}]. "
operator|+
literal|"Deletion would be retried later again."
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|dirs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|totalDeletedSize
operator|+=
name|gcNRTIndexDirs
argument_list|(
name|dirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|totalDeletedSize
operator|+=
name|deleteOldFormatDir
argument_list|(
name|dirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getJcrPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|totalDeletedSize
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reclaimed [{}] space by removing unused/old index directories"
argument_list|,
name|IOUtils
operator|.
name|humanReadableByteCount
argument_list|(
name|totalDeletedSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Removes all directory created by NRTIndex which have      * nrt prefix      */
specifier|private
name|long
name|gcNRTIndexDirs
parameter_list|(
name|LocalIndexDir
name|idxDir
parameter_list|)
block|{
specifier|final
name|String
name|prefix
init|=
name|getFSSafeName
argument_list|(
name|NRTIndex
operator|.
name|NRT_DIR_PREFIX
argument_list|)
decl_stmt|;
name|File
index|[]
name|nrtDirs
init|=
name|idxDir
operator|.
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|size
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|nrtDirs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|f
range|:
name|nrtDirs
control|)
block|{
name|size
operator|+=
name|FileUtils
operator|.
name|sizeOf
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|LocalIndexDir
name|findMatchingIndexDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Resolve to canonical file so that equals can work reliable
name|dir
operator|=
name|dir
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|mapping
init|=
name|getIndexesPerPath
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
argument_list|>
name|e
range|:
name|mapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|LocalIndexDir
name|idxDir
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|idxDir
operator|.
name|dir
operator|.
name|equals
argument_list|(
name|dir
argument_list|)
condition|)
block|{
return|return
name|idxDir
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|long
name|deleteOldFormatDir
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
name|File
name|oldDir
init|=
name|getOldFormatDir
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|long
name|size
init|=
name|FileUtils
operator|.
name|sizeOf
argument_list|(
name|oldDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|oldDir
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to deleted unused local index directory [{}]"
argument_list|,
name|oldDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|size
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
specifier|private
name|File
name|getOldFormatDir
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|String
name|subDir
init|=
name|getPathHash
argument_list|(
name|indexPath
argument_list|)
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
specifier|static
name|String
name|getFSSafeName
parameter_list|(
name|String
name|e
parameter_list|)
block|{
comment|//TODO Exclude -_ like chars via [^\W_]
return|return
name|e
operator|.
name|replaceAll
argument_list|(
literal|"\\W"
argument_list|,
literal|""
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|getTime
parameter_list|()
block|{
try|try
block|{
return|return
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTimeIncreasing
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTimeMonotonic
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

