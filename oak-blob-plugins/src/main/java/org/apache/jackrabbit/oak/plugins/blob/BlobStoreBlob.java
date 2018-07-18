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
name|blob
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
name|api
operator|.
name|Blob
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

begin_comment
comment|/**  * A blob implementation.  */
end_comment

begin_class
specifier|public
class|class
name|BlobStoreBlob
implements|implements
name|Blob
block|{
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|final
name|String
name|blobId
decl_stmt|;
specifier|public
name|BlobStoreBlob
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|String
name|blobId
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|blobId
operator|=
name|blobId
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
try|try
block|{
return|return
name|blobStore
operator|.
name|getInputStream
argument_list|(
name|blobId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while obtaining "
operator|+
literal|"InputStream for blobId ["
operator|+
name|blobId
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
try|try
block|{
return|return
name|blobStore
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid blob id: "
operator|+
name|blobId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
name|blobStore
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
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|blobId
return|;
block|}
specifier|public
name|String
name|getBlobId
parameter_list|()
block|{
return|return
name|blobId
return|;
block|}
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|blobId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|blobId
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|BlobStoreBlob
condition|)
block|{
name|BlobStoreBlob
name|b
init|=
operator|(
name|BlobStoreBlob
operator|)
name|other
decl_stmt|;
comment|// theoretically, the data could be the same
comment|// even if the id is different
return|return
name|b
operator|.
name|blobId
operator|.
name|equals
argument_list|(
name|blobId
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

