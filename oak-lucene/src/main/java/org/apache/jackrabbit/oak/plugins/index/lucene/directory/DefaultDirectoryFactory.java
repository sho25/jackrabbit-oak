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
name|IOException
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
name|LuceneIndexDefinition
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
name|directory
operator|.
name|ActiveDeletedBlobCollectorFactory
operator|.
name|BlobDeletionCallback
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
name|search
operator|.
name|FulltextIndexConstants
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|SUGGEST_DATA_CHILD_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NoLockFactory
operator|.
name|getNoLockFactory
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultDirectoryFactory
implements|implements
name|DirectoryFactory
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|READ_BEFORE_WRITE
init|=
operator|!
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.lucene.readBeforeWriteDisabled"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
specifier|final
name|GarbageCollectableBlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|final
name|BlobDeletionCallback
name|blobDeletionCallback
decl_stmt|;
specifier|public
name|DefaultDirectoryFactory
parameter_list|(
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
annotation|@
name|Nullable
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{
name|this
argument_list|(
name|indexCopier
argument_list|,
name|blobStore
argument_list|,
name|BlobDeletionCallback
operator|.
name|NOOP
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDirectoryFactory
parameter_list|(
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
annotation|@
name|Nullable
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|,
annotation|@
name|NotNull
name|ActiveDeletedBlobCollectorFactory
operator|.
name|BlobDeletionCallback
name|blobDeletionCallback
parameter_list|)
block|{
name|this
operator|.
name|indexCopier
operator|=
name|indexCopier
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|blobDeletionCallback
operator|=
name|blobDeletionCallback
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Directory
name|newInstance
parameter_list|(
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|dirName
parameter_list|,
name|boolean
name|reindex
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newIndexDirectory
argument_list|(
name|definition
argument_list|,
name|builder
argument_list|,
name|dirName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexCopier
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|SUGGEST_DATA_CHILD_NAME
operator|.
name|equals
argument_list|(
name|dirName
argument_list|)
operator|&&
name|definition
operator|.
name|getUniqueId
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
if|if
condition|(
name|READ_BEFORE_WRITE
condition|)
block|{
comment|// prefetch the index when writing to it
comment|// (copy from the remote directory to the local directory)
comment|// to avoid having to stream it when merging
name|String
name|indexPath
init|=
name|definition
operator|.
name|getIndexPath
argument_list|()
decl_stmt|;
name|indexCopier
operator|.
name|wrapForRead
argument_list|(
name|indexPath
argument_list|,
name|definition
argument_list|,
name|directory
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
block|}
name|directory
operator|=
name|indexCopier
operator|.
name|wrapForWrite
argument_list|(
name|definition
argument_list|,
name|directory
argument_list|,
name|reindex
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
block|}
return|return
name|directory
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remoteDirectory
parameter_list|()
block|{
return|return
name|indexCopier
operator|==
literal|null
return|;
block|}
specifier|private
name|Directory
name|newIndexDirectory
parameter_list|(
name|LuceneIndexDefinition
name|indexDefinition
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|FulltextIndexConstants
operator|.
name|PERSISTENCE_FILE
operator|.
name|equalsIgnoreCase
argument_list|(
name|definition
operator|.
name|getString
argument_list|(
name|FulltextIndexConstants
operator|.
name|PERSISTENCE_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|path
operator|=
name|definition
operator|.
name|getString
argument_list|(
name|FulltextIndexConstants
operator|.
name|PERSISTENCE_PATH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|remoteDirectory
argument_list|()
condition|)
block|{
return|return
operator|new
name|BufferedOakDirectory
argument_list|(
name|definition
argument_list|,
name|dirName
argument_list|,
name|indexDefinition
argument_list|,
name|blobStore
argument_list|,
name|blobDeletionCallback
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|OakDirectory
argument_list|(
name|definition
argument_list|,
name|dirName
argument_list|,
name|indexDefinition
argument_list|,
literal|false
argument_list|,
name|blobStore
argument_list|,
name|blobDeletionCallback
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// try {
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// TODO: no locking used
comment|// --> using the FS backend for the index is in any case
comment|// troublesome in clustering scenarios and for backup
comment|// etc. so instead of fixing these issues we'd better
comment|// work on making the in-content index work without
comment|// problems (or look at the Solr indexer as alternative)
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|,
name|getNoLockFactory
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

