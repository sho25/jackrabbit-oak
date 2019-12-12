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
name|spi
operator|.
name|blob
operator|.
name|split
package|;
end_package

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
name|spi
operator|.
name|blob
operator|.
name|BlobOptions
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
name|blob
operator|.
name|BlobStoreWrapper
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

begin_class
specifier|public
class|class
name|WrappingSplitBlobStore
implements|implements
name|BlobStoreWrapper
implements|,
name|SplitBlobStore
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WrappingSplitBlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DefaultSplitBlobStore
name|splitBlobStore
decl_stmt|;
specifier|private
specifier|final
name|String
name|repositoryDir
decl_stmt|;
specifier|private
specifier|final
name|BlobStore
name|newBlobStore
decl_stmt|;
specifier|public
name|WrappingSplitBlobStore
parameter_list|(
name|String
name|repositoryDir
parameter_list|,
name|BlobStore
name|newBlobStore
parameter_list|)
block|{
name|this
operator|.
name|repositoryDir
operator|=
name|repositoryDir
expr_stmt|;
name|this
operator|.
name|newBlobStore
operator|=
name|newBlobStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBlobStore
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Internal blob store set: {}"
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
name|splitBlobStore
operator|=
operator|new
name|DefaultSplitBlobStore
argument_list|(
name|repositoryDir
argument_list|,
name|blobStore
argument_list|,
name|newBlobStore
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SplitBlobStore
name|getSplitBlobStore
parameter_list|()
block|{
if|if
condition|(
name|splitBlobStore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The old blob store hasn't been set yet."
argument_list|)
throw|;
block|}
return|return
name|splitBlobStore
return|;
block|}
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
return|return
name|getSplitBlobStore
argument_list|()
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**      * Ignores the options provided and delegates to {@link #writeBlob(InputStream)}.      *      * @param in the input stream to write      * @param options the options to use      * @return      * @throws IOException      */
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|BlobOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
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
return|return
name|getSplitBlobStore
argument_list|()
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|,
name|pos
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
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
return|return
name|getSplitBlobStore
argument_list|()
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
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
name|getSplitBlobStore
argument_list|()
operator|.
name|getInputStream
argument_list|(
name|blobId
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
name|getSplitBlobStore
argument_list|()
operator|.
name|getBlobId
argument_list|(
name|reference
argument_list|)
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
name|getSplitBlobStore
argument_list|()
operator|.
name|getReference
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMigrated
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSplitBlobStore
argument_list|()
operator|.
name|isMigrated
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|getSplitBlobStore
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

