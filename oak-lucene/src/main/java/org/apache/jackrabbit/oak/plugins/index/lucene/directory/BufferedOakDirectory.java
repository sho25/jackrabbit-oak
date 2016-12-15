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
name|Collection
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|OakDirectory
operator|.
name|BlobFactory
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Lock
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
name|LockFactory
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
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|ModifiedNodeState
operator|.
name|squeeze
import|;
end_import

begin_comment
comment|/**  * A directory implementation that buffers changes until {@link #close()},  * except for blob values. Those are written immediately to the store.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|BufferedOakDirectory
extends|extends
name|Directory
block|{
specifier|static
specifier|final
name|int
name|DELETE_THRESHOLD_UNTIL_REOPEN
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BufferedOakDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BlobFactory
name|blobFactory
decl_stmt|;
specifier|private
specifier|final
name|String
name|dataNodeName
decl_stmt|;
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|OakDirectory
name|base
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|bufferedForDelete
init|=
name|Sets
operator|.
name|newConcurrentHashSet
argument_list|()
decl_stmt|;
specifier|private
name|NodeBuilder
name|bufferedBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|OakDirectory
name|buffered
decl_stmt|;
specifier|private
name|int
name|deleteCount
decl_stmt|;
specifier|public
name|BufferedOakDirectory
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|String
name|dataNodeName
parameter_list|,
annotation|@
name|Nonnull
name|IndexDefinition
name|definition
parameter_list|,
annotation|@
name|Nullable
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobFactory
operator|=
name|blobStore
operator|!=
literal|null
condition|?
operator|new
name|OakDirectory
operator|.
name|BlobStoreBlobFactory
argument_list|(
name|blobStore
argument_list|)
else|:
operator|new
name|OakDirectory
operator|.
name|NodeBuilderBlobFactory
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataNodeName
operator|=
name|checkNotNull
argument_list|(
name|dataNodeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|checkNotNull
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
operator|new
name|OakDirectory
argument_list|(
name|checkNotNull
argument_list|(
name|builder
argument_list|)
argument_list|,
name|dataNodeName
argument_list|,
name|definition
argument_list|,
literal|false
argument_list|,
name|blobFactory
argument_list|)
expr_stmt|;
name|reopenBuffered
argument_list|()
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]listAll()"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|all
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
name|base
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
name|buffered
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|removeAll
argument_list|(
name|bufferedForDelete
argument_list|)
expr_stmt|;
return|return
name|all
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|all
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]fileExists({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferedForDelete
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|buffered
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
operator|||
name|base
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]deleteFile({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|base
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|bufferedForDelete
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|buffered
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|buffered
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fileDeleted
argument_list|()
expr_stmt|;
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]fileLength({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferedForDelete
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"already deleted: [%s] %s"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|Directory
name|dir
init|=
name|base
decl_stmt|;
if|if
condition|(
name|buffered
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|dir
operator|=
name|buffered
expr_stmt|;
block|}
return|return
name|dir
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]createOutput({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|bufferedForDelete
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|buffered
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]sync({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|names
argument_list|)
expr_stmt|;
name|buffered
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|base
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]openInput({})"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferedForDelete
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"already deleted: [%s] %s"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|Directory
name|dir
init|=
name|base
decl_stmt|;
if|if
condition|(
name|buffered
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|dir
operator|=
name|buffered
expr_stmt|;
block|}
return|return
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|base
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"[{}]close()"
argument_list|,
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// copy buffered files to base
for|for
control|(
name|String
name|name
range|:
name|buffered
operator|.
name|listAll
argument_list|()
control|)
block|{
name|buffered
operator|.
name|copy
argument_list|(
name|base
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|// remove files marked as deleted
for|for
control|(
name|String
name|name
range|:
name|bufferedForDelete
control|)
block|{
name|base
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|base
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
specifier|private
name|void
name|fileDeleted
parameter_list|()
throws|throws
name|IOException
block|{
comment|// get rid of non existing files once in a while
if|if
condition|(
operator|++
name|deleteCount
operator|>=
name|DELETE_THRESHOLD_UNTIL_REOPEN
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reopen buffered OakDirectory. Current list of files: {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|buffered
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
name|reopenBuffered
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|reopenBuffered
parameter_list|()
block|{
comment|// squeeze out child nodes marked as non existing
comment|// those are files that were created and later deleted again
name|bufferedBuilder
operator|=
name|squeeze
argument_list|(
name|bufferedBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|builder
argument_list|()
expr_stmt|;
name|buffered
operator|=
operator|new
name|OakDirectory
argument_list|(
name|bufferedBuilder
argument_list|,
name|dataNodeName
argument_list|,
name|definition
argument_list|,
literal|false
argument_list|,
name|blobFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
