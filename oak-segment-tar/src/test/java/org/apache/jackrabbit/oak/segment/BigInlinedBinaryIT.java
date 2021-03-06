begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Long
operator|.
name|signum
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|getProperty
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
name|BINARY
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
name|ListRecord
operator|.
name|LEVEL_SIZE
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
name|SegmentStream
operator|.
name|BLOCK_SIZE
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
name|io
operator|.
name|InputStream
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
name|CommitFailedException
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_comment
comment|/**  * Integration test trying to inline a large (67GB) binary.  * Skipped unless -Dtest=BigInlinedBinaryIT is specified.  */
end_comment

begin_class
specifier|public
class|class
name|BigInlinedBinaryIT
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|File
name|getFileStoreFolder
parameter_list|()
block|{
return|return
name|folder
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|BigInlinedBinaryIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
name|getProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|largeBlob
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|InvalidFileStoreVersionException
block|{
try|try
init|(
name|FileStore
name|fileStore
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|SegmentNodeStore
name|nodeStore
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"node"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"blob"
argument_list|,
name|createBlob
argument_list|(
operator|(
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
operator|*
name|LEVEL_SIZE
operator|+
literal|1L
operator|)
operator|*
name|BLOCK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|Blob
name|blob
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"node"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|blob
operator|.
name|getNewStream
argument_list|()
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|long
name|blobSize
parameter_list|)
block|{
return|return
operator|new
name|Blob
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
operator|new
name|InputStream
argument_list|()
block|{
name|long
name|pos
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
return|return
name|signum
argument_list|(
name|blobSize
operator|-
operator|++
name|pos
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|blobSize
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

