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
package|;
end_package

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
name|primitives
operator|.
name|Ints
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
name|jackrabbit
operator|.
name|oak
operator|.
name|util
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NoLockFactory
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
name|checkElementIndex
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
name|checkPositionIndexes
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
name|JcrConstants
operator|.
name|JCR_DATA
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
name|JcrConstants
operator|.
name|JCR_LASTMODIFIED
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_comment
comment|/**  * Implementation of the Lucene {@link Directory} (a flat list of files)  * based on an Oak {@link NodeBuilder}.  */
end_comment

begin_class
class|class
name|OakDirectory
extends|extends
name|Directory
block|{
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
name|OakDirectory
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
specifier|static
specifier|final
name|String
name|PROP_DIR_LISTING
init|=
literal|"dirListing"
decl_stmt|;
specifier|static
specifier|final
name|String
name|PROP_BLOB_SIZE
init|=
literal|"blobSize"
decl_stmt|;
specifier|protected
specifier|final
name|NodeBuilder
name|directoryBuilder
decl_stmt|;
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
name|LockFactory
name|lockFactory
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
name|Sets
operator|.
name|newConcurrentHashSet
argument_list|()
decl_stmt|;
specifier|public
name|OakDirectory
parameter_list|(
name|NodeBuilder
name|directoryBuilder
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
name|this
operator|.
name|lockFactory
operator|=
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|directoryBuilder
operator|=
name|directoryBuilder
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|readOnly
expr_stmt|;
name|this
operator|.
name|fileNames
operator|.
name|addAll
argument_list|(
name|getListing
argument_list|()
argument_list|)
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
name|fileNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fileNames
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
return|return
name|fileNames
operator|.
name|contains
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
name|checkArgument
argument_list|(
operator|!
name|readOnly
argument_list|,
literal|"Read only directory"
argument_list|)
expr_stmt|;
name|fileNames
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|directoryBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
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
name|NodeBuilder
name|file
init|=
name|directoryBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|OakIndexInput
name|input
init|=
operator|new
name|OakIndexInput
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|input
operator|.
name|length
argument_list|()
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|checkArgument
argument_list|(
operator|!
name|readOnly
argument_list|,
literal|"Read only directory"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|file
decl_stmt|;
if|if
condition|(
operator|!
name|directoryBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|file
operator|=
name|directoryBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|file
operator|.
name|setProperty
argument_list|(
name|PROP_BLOB_SIZE
argument_list|,
name|definition
operator|.
name|getBlobSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|file
operator|=
name|directoryBuilder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|fileNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|OakIndexOutput
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
return|;
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
name|NodeBuilder
name|file
init|=
name|directoryBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|OakIndexInput
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
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
name|lockFactory
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
name|lockFactory
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
comment|// ?
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
if|if
condition|(
operator|!
name|readOnly
operator|&&
name|definition
operator|.
name|saveDirListing
argument_list|()
condition|)
block|{
name|directoryBuilder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|PROP_DIR_LISTING
argument_list|,
name|fileNames
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|this
operator|.
name|lockFactory
operator|=
name|lockFactory
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
name|lockFactory
return|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getListing
parameter_list|()
block|{
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|saveDirListing
argument_list|()
condition|)
block|{
name|PropertyState
name|listing
init|=
name|directoryBuilder
operator|.
name|getProperty
argument_list|(
name|PROP_DIR_LISTING
argument_list|)
decl_stmt|;
if|if
condition|(
name|listing
operator|!=
literal|null
condition|)
block|{
name|fileNames
operator|=
name|listing
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fileNames
operator|==
literal|null
condition|)
block|{
name|fileNames
operator|=
name|directoryBuilder
operator|.
name|getChildNodeNames
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|fileNames
argument_list|)
decl_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|100
argument_list|,
literal|"Directory listing performed. Total {} files"
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Size of the blob entries to which the Lucene files are split.      * Set to higher than the 4kB inline limit for the BlobStore,      */
specifier|static
specifier|final
name|int
name|DEFAULT_BLOB_SIZE
init|=
literal|32
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
class|class
name|OakIndexFile
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|file
decl_stmt|;
specifier|private
specifier|final
name|int
name|blobSize
decl_stmt|;
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|length
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Blob
argument_list|>
name|data
decl_stmt|;
specifier|private
name|boolean
name|dataModified
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|byte
index|[]
name|blob
decl_stmt|;
specifier|private
name|boolean
name|blobModified
init|=
literal|false
decl_stmt|;
specifier|public
name|OakIndexFile
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeBuilder
name|file
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
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|blobSize
operator|=
name|determineBlobSize
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|blob
operator|=
operator|new
name|byte
index|[
name|blobSize
index|]
expr_stmt|;
name|PropertyState
name|property
init|=
name|file
operator|.
name|getProperty
argument_list|(
name|JCR_DATA
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|BINARIES
condition|)
block|{
name|this
operator|.
name|data
operator|=
name|newArrayList
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|data
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|length
operator|=
operator|(
name|long
operator|)
name|data
operator|.
name|size
argument_list|()
operator|*
name|blobSize
expr_stmt|;
if|if
condition|(
operator|!
name|data
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Blob
name|last
init|=
name|data
operator|.
name|get
argument_list|(
name|data
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|this
operator|.
name|length
operator|-=
name|blobSize
operator|-
name|last
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|OakIndexFile
parameter_list|(
name|OakIndexFile
name|that
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|that
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|that
operator|.
name|file
expr_stmt|;
name|this
operator|.
name|blobSize
operator|=
name|that
operator|.
name|blobSize
expr_stmt|;
name|this
operator|.
name|blob
operator|=
operator|new
name|byte
index|[
name|blobSize
index|]
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|that
operator|.
name|position
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|that
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|newArrayList
argument_list|(
name|that
operator|.
name|data
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataModified
operator|=
name|that
operator|.
name|dataModified
expr_stmt|;
block|}
specifier|private
name|void
name|loadBlob
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|checkElementIndex
argument_list|(
name|i
argument_list|,
name|data
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|!=
name|i
condition|)
block|{
name|flushBlob
argument_list|()
expr_stmt|;
name|checkState
argument_list|(
operator|!
name|blobModified
argument_list|)
expr_stmt|;
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|blobSize
argument_list|,
name|length
operator|-
name|i
operator|*
name|blobSize
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|blob
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|index
operator|=
name|i
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|flushBlob
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobModified
condition|)
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|blobSize
argument_list|,
name|length
operator|-
name|index
operator|*
name|blobSize
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
name|file
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|blob
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|data
operator|.
name|size
argument_list|()
condition|)
block|{
name|data
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkState
argument_list|(
name|index
operator|==
name|data
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|dataModified
operator|=
literal|true
expr_stmt|;
name|blobModified
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// seek() may be called with pos == length
comment|// see https://issues.apache.org/jira/browse/LUCENE-1196
if|if
condition|(
name|pos
argument_list|<
literal|0
operator|||
name|pos
argument_list|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid seek request"
argument_list|)
throw|;
block|}
else|else
block|{
name|position
operator|=
name|pos
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|len
argument_list|,
name|checkNotNull
argument_list|(
name|b
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
argument_list|<
literal|0
operator|||
name|position
operator|+
name|len
argument_list|>
name|length
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Invalid byte range request [%s] : position : %d, length : "
operator|+
literal|"%d, len : %d"
argument_list|,
name|name
argument_list|,
name|position
argument_list|,
name|length
argument_list|,
name|len
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|/
name|blobSize
argument_list|)
decl_stmt|;
name|int
name|o
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|%
name|blobSize
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|loadBlob
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|int
name|l
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|blobSize
operator|-
name|o
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blob
argument_list|,
name|o
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|l
expr_stmt|;
name|len
operator|-=
name|l
expr_stmt|;
name|position
operator|+=
name|l
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|o
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|/
name|blobSize
argument_list|)
decl_stmt|;
name|int
name|o
init|=
call|(
name|int
call|)
argument_list|(
name|position
operator|%
name|blobSize
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|l
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|blobSize
operator|-
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
name|i
condition|)
block|{
if|if
condition|(
name|o
operator|>
literal|0
operator|||
operator|(
name|l
operator|<
name|blobSize
operator|&&
name|position
operator|+
name|l
operator|<
name|length
operator|)
condition|)
block|{
name|loadBlob
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flushBlob
argument_list|()
expr_stmt|;
name|index
operator|=
name|i
expr_stmt|;
block|}
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|blob
argument_list|,
name|o
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|blobModified
operator|=
literal|true
expr_stmt|;
name|offset
operator|+=
name|l
expr_stmt|;
name|len
operator|-=
name|l
expr_stmt|;
name|position
operator|+=
name|l
expr_stmt|;
name|length
operator|=
name|Math
operator|.
name|max
argument_list|(
name|length
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|o
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|int
name|determineBlobSize
parameter_list|(
name|NodeBuilder
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|hasProperty
argument_list|(
name|PROP_BLOB_SIZE
argument_list|)
condition|)
block|{
return|return
name|Ints
operator|.
name|checkedCast
argument_list|(
name|file
operator|.
name|getProperty
argument_list|(
name|PROP_BLOB_SIZE
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
return|;
block|}
return|return
name|DEFAULT_BLOB_SIZE
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBlob
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataModified
condition|)
block|{
name|file
operator|.
name|setProperty
argument_list|(
name|JCR_LASTMODIFIED
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|setProperty
argument_list|(
name|JCR_DATA
argument_list|,
name|data
argument_list|,
name|BINARIES
argument_list|)
expr_stmt|;
name|dataModified
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OakIndexInput
extends|extends
name|IndexInput
block|{
specifier|private
specifier|final
name|OakIndexFile
name|file
decl_stmt|;
specifier|public
name|OakIndexInput
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeBuilder
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
operator|new
name|OakIndexFile
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
specifier|private
name|OakIndexInput
parameter_list|(
name|OakIndexInput
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
operator|new
name|OakIndexFile
argument_list|(
name|that
operator|.
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OakIndexInput
name|clone
parameter_list|()
block|{
return|return
operator|new
name|OakIndexInput
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|o
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|b
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|file
operator|.
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|file
operator|.
name|blob
operator|=
literal|null
expr_stmt|;
name|file
operator|.
name|data
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|OakIndexOutput
extends|extends
name|IndexOutput
block|{
specifier|private
specifier|final
name|OakIndexFile
name|file
decl_stmt|;
specifier|public
name|OakIndexOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeBuilder
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|file
operator|=
operator|new
name|OakIndexFile
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|file
operator|.
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
operator|new
name|byte
index|[]
block|{
name|b
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|flush
argument_list|()
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
name|flush
argument_list|()
expr_stmt|;
name|file
operator|.
name|blob
operator|=
literal|null
expr_stmt|;
name|file
operator|.
name|data
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

