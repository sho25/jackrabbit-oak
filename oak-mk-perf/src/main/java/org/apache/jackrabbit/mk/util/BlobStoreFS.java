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
name|mk
operator|.
name|util
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
name|mk
operator|.
name|blobs
operator|.
name|BlobStore
import|;
end_import

begin_class
specifier|public
class|class
name|BlobStoreFS
implements|implements
name|BlobStore
block|{
specifier|public
name|BlobStoreFS
parameter_list|(
name|String
name|rootPath
parameter_list|)
block|{
name|File
name|rootDir
init|=
operator|new
name|File
argument_list|(
name|rootPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rootDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|rootDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|blobOffset
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|bufferOffset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

