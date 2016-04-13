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
name|SegmentNodeStore
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
name|Builder
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
name|NodeStore
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

begin_class
specifier|public
class|class
name|SegmentFactory
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
name|mmap
decl_stmt|;
specifier|public
name|SegmentFactory
parameter_list|(
name|String
name|directory
parameter_list|,
name|boolean
name|mmap
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
name|mmap
operator|=
name|mmap
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
name|Builder
name|builder
init|=
name|FileStore
operator|.
name|builder
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
operator|.
name|withMemoryMapping
argument_list|(
name|mmap
argument_list|)
expr_stmt|;
name|FileStore
name|fs
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|fs
argument_list|)
operator|.
name|build
argument_list|()
return|;
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
literal|"SegmentNodeStore[%s]"
argument_list|,
name|dir
argument_list|)
return|;
block|}
block|}
end_class

end_unit

