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
name|upgrade
operator|.
name|cli
operator|.
name|node
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
name|Closeable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|blob
operator|.
name|ReferenceCollector
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
name|SegmentNodeBuilder
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
name|SegmentNodeState
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
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|NodeStore
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

begin_class
specifier|public
class|class
name|SegmentTarFactory
implements|implements
name|NodeStoreFactory
block|{
specifier|private
specifier|final
name|File
name|dir
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|disableMmap
decl_stmt|;
specifier|public
name|SegmentTarFactory
parameter_list|(
name|String
name|directory
parameter_list|,
name|boolean
name|disableMmap
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableMmap
operator|=
name|disableMmap
expr_stmt|;
name|createDirectoryIfMissing
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a directory: "
operator|+
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|createDirectoryIfMissing
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|create
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"segmentstore"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|withMaxFileSize
argument_list|(
literal|256
argument_list|)
expr_stmt|;
if|if
condition|(
name|disableMmap
condition|)
block|{
name|builder
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|withDefaultMemoryMapping
argument_list|()
expr_stmt|;
block|}
specifier|final
name|FileStore
name|fs
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
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
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|fs
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TarNodeStore
argument_list|(
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|TarNodeStore
operator|.
name|SuperRootProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setSuperRoot
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|builder
operator|instanceof
name|SegmentNodeBuilder
argument_list|)
expr_stmt|;
name|SegmentNodeBuilder
name|segmentBuilder
init|=
operator|(
name|SegmentNodeBuilder
operator|)
name|builder
decl_stmt|;
name|SegmentNodeState
name|lastRoot
init|=
operator|(
name|SegmentNodeState
operator|)
name|getSuperRoot
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|lastRoot
operator|.
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SegmentNodeState
operator|)
name|segmentBuilder
operator|.
name|getBaseState
argument_list|()
operator|)
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The new head is out of date"
argument_list|)
throw|;
block|}
name|fs
operator|.
name|getRevisions
argument_list|()
operator|.
name|setHead
argument_list|(
name|lastRoot
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|segmentBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getSuperRoot
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getHead
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasExternalBlobReferences
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"segmentstore"
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|withMaxFileSize
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|builder
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|FileStore
name|fs
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFileStoreVersionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|fs
operator|.
name|collectBlobReferences
argument_list|(
operator|new
name|ReferenceCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|addReference
parameter_list|(
name|String
name|reference
parameter_list|,
annotation|@
name|Nullable
name|String
name|nodeId
parameter_list|)
block|{
comment|// FIXME the collector should allow to stop processing
comment|// see java.nio.file.FileVisitor
throw|throw
operator|new
name|ExternalBlobFound
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|ExternalBlobFound
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|File
name|getRepositoryDir
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|FileStore
name|fs
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
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
literal|"SegmentTarNodeStore[%s]"
argument_list|,
name|dir
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|ExternalBlobFound
extends|extends
name|RuntimeException
block|{     }
block|}
end_class

end_unit

