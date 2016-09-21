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
name|container
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|SegmentTarNodeStoreContainer
operator|.
name|deleteRecursive
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
operator|.
name|SEGMENT_OLD_PREFIX
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeStoreContainer
implements|implements
name|NodeStoreContainer
block|{
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
specifier|final
name|BlobStoreContainer
name|blob
decl_stmt|;
specifier|private
name|FileStore
name|fs
decl_stmt|;
specifier|public
name|SegmentNodeStoreContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentNodeStoreContainer
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentNodeStoreContainer
parameter_list|(
name|BlobStoreContainer
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|blob
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SegmentNodeStoreContainer
parameter_list|(
name|BlobStoreContainer
name|blob
parameter_list|,
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|blob
operator|=
name|blob
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
operator|==
literal|null
condition|?
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
operator|.
name|createTempDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"target"
argument_list|)
argument_list|,
literal|"segment"
argument_list|)
operator|.
name|toFile
argument_list|()
else|:
name|directory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileStore
operator|.
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
name|directory
argument_list|,
literal|"segmentstore"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
name|blob
operator|.
name|open
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clean
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteRecursive
argument_list|(
name|directory
argument_list|)
expr_stmt|;
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
name|blob
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|SEGMENT_OLD_PREFIX
operator|+
name|directory
operator|.
name|getPath
argument_list|()
return|;
block|}
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
block|}
end_class

end_unit

