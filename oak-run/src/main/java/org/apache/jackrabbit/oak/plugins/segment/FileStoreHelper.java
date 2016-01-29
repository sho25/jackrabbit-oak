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
name|segment
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|reverseOrder
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
name|sort
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
name|plugins
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|newFileStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
operator|.
name|SimpleImmutableEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|Map
operator|.
name|Entry
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
name|UUID
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|FileStoreHelper
block|{
specifier|public
specifier|static
specifier|final
name|String
name|newline
init|=
literal|"\n"
decl_stmt|;
specifier|public
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
specifier|public
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
specifier|static
class|class
name|BasicReadOnlyBlobStore
implements|implements
name|BlobStore
block|{
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// best effort length extraction
name|int
name|indexOfSep
init|=
name|blobId
operator|.
name|lastIndexOf
argument_list|(
literal|"#"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfSep
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|blobId
operator|.
name|substring
argument_list|(
name|indexOfSep
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBlobId
parameter_list|(
name|String
name|reference
parameter_list|)
block|{
return|return
name|reference
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
return|return
name|blobId
return|;
block|}
block|}
specifier|private
name|FileStoreHelper
parameter_list|()
block|{     }
comment|/**      * Helper method to determine the segment version of the segment      * containing the current root node state.      * @param fileStore      * @return      */
specifier|public
specifier|static
name|SegmentVersion
name|getSegmentVersion
parameter_list|(
name|FileStore
name|fileStore
parameter_list|)
block|{
return|return
name|fileStore
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentVersion
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getTarFiles
parameter_list|(
name|FileStore
name|store
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|store
operator|.
name|getTarReaderIndex
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|files
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|p
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sort
argument_list|(
name|files
argument_list|,
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|files
return|;
block|}
specifier|public
specifier|static
name|void
name|getGcRoots
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|UUID
name|uuidIn
parameter_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|links
parameter_list|)
throws|throws
name|IOException
block|{
name|Deque
argument_list|<
name|UUID
argument_list|>
name|todos
init|=
operator|new
name|ArrayDeque
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
name|todos
operator|.
name|add
argument_list|(
name|uuidIn
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|visited
init|=
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|todos
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|UUID
name|uuid
init|=
name|todos
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|visited
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|String
name|f
range|:
name|getTarFiles
argument_list|(
name|store
argument_list|)
control|)
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|graph
init|=
name|store
operator|.
name|getTarGraph
argument_list|(
name|f
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|g
range|:
name|graph
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|g
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
name|g
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|UUID
name|uuidP
init|=
name|g
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|todos
operator|.
name|contains
argument_list|(
name|uuidP
argument_list|)
condition|)
block|{
name|todos
operator|.
name|add
argument_list|(
name|uuidP
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
name|deps
init|=
name|links
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|deps
operator|==
literal|null
condition|)
block|{
name|deps
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
name|links
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|deps
argument_list|)
expr_stmt|;
block|}
name|deps
operator|.
name|add
argument_list|(
operator|new
name|SimpleImmutableEntry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|(
name|uuidP
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|printGcRoots
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|links
parameter_list|,
name|UUID
name|uuid
parameter_list|,
name|String
name|space
parameter_list|,
name|String
name|inc
parameter_list|)
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
name|roots
init|=
name|links
operator|.
name|remove
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|roots
operator|==
literal|null
operator|||
name|roots
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// TODO is sorting by file name needed?
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
name|r
range|:
name|roots
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|space
operator|+
name|r
operator|.
name|getKey
argument_list|()
operator|+
literal|"["
operator|+
name|r
operator|.
name|getValue
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|printGcRoots
argument_list|(
name|sb
argument_list|,
name|links
argument_list|,
name|r
operator|.
name|getKey
argument_list|()
argument_list|,
name|space
operator|+
name|inc
argument_list|,
name|inc
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|readRevisions
parameter_list|(
name|File
name|store
parameter_list|)
block|{
name|File
name|journal
init|=
operator|new
name|File
argument_list|(
name|store
argument_list|,
literal|"journal.log"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|journal
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|newArrayList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|revs
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|JournalReader
name|journalReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|journalReader
operator|=
operator|new
name|JournalReader
argument_list|(
name|journal
argument_list|)
expr_stmt|;
try|try
block|{
name|revs
operator|=
name|newArrayList
argument_list|(
name|journalReader
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|journalReader
operator|!=
literal|null
condition|)
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{             }
block|}
return|return
name|revs
return|;
block|}
specifier|public
specifier|static
name|File
name|isValidFileStoreOrFail
parameter_list|(
name|File
name|store
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|isValidFileStore
argument_list|(
name|store
argument_list|)
argument_list|,
literal|"Invalid FileStore directory "
operator|+
name|store
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
comment|/**      * Checks if the provided directory is a valid FileStore      *      * @return true if the provided directory is a valid FileStore      */
specifier|public
specifier|static
name|boolean
name|isValidFileStore
parameter_list|(
name|File
name|store
parameter_list|)
block|{
if|if
condition|(
operator|!
name|store
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|store
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// for now the only check is the existence of the journal file
for|for
control|(
name|String
name|f
range|:
name|store
operator|.
name|list
argument_list|()
control|)
block|{
if|if
condition|(
literal|"journal.log"
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
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
name|FileStore
name|store
init|=
name|openReadOnlyFileStore
argument_list|(
name|directory
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
specifier|public
specifier|static
name|FileStore
name|openFileStore
parameter_list|(
name|String
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|openFileStore
argument_list|(
name|directory
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
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
block|{
return|return
name|newFileStore
argument_list|(
name|checkFileStoreVersionOrFail
argument_list|(
name|directory
argument_list|,
name|force
argument_list|)
argument_list|)
operator|.
name|withCacheSize
argument_list|(
name|TAR_SEGMENT_CACHE_SIZE
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
operator|.
name|create
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|FileStore
name|openFileStore
parameter_list|(
name|String
name|directory
parameter_list|,
name|boolean
name|force
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|newFileStore
argument_list|(
name|checkFileStoreVersionOrFail
argument_list|(
name|directory
argument_list|,
name|force
argument_list|)
argument_list|)
operator|.
name|withCacheSize
argument_list|(
name|TAR_SEGMENT_CACHE_SIZE
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|create
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|ReadOnlyStore
name|openReadOnlyFileStore
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReadOnlyStore
argument_list|(
name|isValidFileStoreOrFail
argument_list|(
name|directory
argument_list|)
argument_list|,
name|TAR_SEGMENT_CACHE_SIZE
argument_list|,
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ReadOnlyStore
name|openReadOnlyFileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReadOnlyStore
argument_list|(
name|isValidFileStoreOrFail
argument_list|(
name|directory
argument_list|)
argument_list|,
name|TAR_SEGMENT_CACHE_SIZE
argument_list|,
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|,
name|blobStore
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BlobStore
name|newBasicReadOnlyBlobStore
parameter_list|()
block|{
return|return
operator|new
name|BasicReadOnlyBlobStore
argument_list|()
return|;
block|}
block|}
end_class

end_unit

