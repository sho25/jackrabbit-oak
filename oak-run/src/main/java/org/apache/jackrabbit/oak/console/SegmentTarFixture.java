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
name|console
package|;
end_package

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

begin_class
class|class
name|SegmentTarFixture
implements|implements
name|NodeStoreFixture
block|{
specifier|static
name|NodeStoreFixture
name|create
parameter_list|(
name|File
name|path
parameter_list|,
name|boolean
name|readOnly
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
name|path
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|256
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
name|FileStore
name|store
decl_stmt|;
if|if
condition|(
name|readOnly
condition|)
block|{
name|store
operator|=
name|builder
operator|.
name|buildReadOnly
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|store
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SegmentTarFixture
argument_list|(
name|store
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|FileStore
name|fileStore
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|segmentNodeStore
decl_stmt|;
specifier|private
name|SegmentTarFixture
parameter_list|(
name|FileStore
name|fileStore
parameter_list|)
block|{
name|this
operator|.
name|fileStore
operator|=
name|fileStore
expr_stmt|;
name|this
operator|.
name|segmentNodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
name|segmentNodeStore
return|;
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
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

