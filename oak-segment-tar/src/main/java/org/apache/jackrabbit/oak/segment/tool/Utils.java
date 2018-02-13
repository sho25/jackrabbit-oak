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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|util
operator|.
name|Iterator
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
name|annotation
operator|.
name|Nonnull
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
name|Function
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
name|Iterators
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
name|SegmentNodeStorePersistence
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
name|JournalEntry
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
name|tooling
operator|.
name|BasicReadOnlyBlobStore
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
specifier|final
class|class
name|Utils
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
name|Utils
parameter_list|()
block|{}
specifier|static
name|ReadOnlyFileStore
name|openReadOnlyFileStore
parameter_list|(
name|File
name|path
parameter_list|,
name|BlobStore
name|blobStore
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
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
return|;
block|}
specifier|static
name|ReadOnlyFileStore
name|openReadOnlyFileStore
parameter_list|(
name|File
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
name|TAR_STORAGE_MEMORY_MAPPED
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
return|;
block|}
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
name|JournalFile
name|journal
init|=
operator|new
name|LocalJournalFile
argument_list|(
name|store
argument_list|,
literal|"journal.log"
argument_list|)
decl_stmt|;
if|if
condition|(
name|journal
operator|.
name|exists
argument_list|()
condition|)
block|{
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
name|Iterator
argument_list|<
name|String
argument_list|>
name|revisionIterator
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|journalReader
argument_list|,
operator|new
name|Function
argument_list|<
name|JournalEntry
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|JournalEntry
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|.
name|getRevision
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|newArrayList
argument_list|(
name|revisionIterator
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|newArrayList
argument_list|()
return|;
block|}
specifier|private
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
specifier|private
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
name|String
index|[]
name|fileNames
init|=
name|store
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileNames
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|f
range|:
name|fileNames
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
block|}
end_class

end_unit

