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
name|segment
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlob
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobDirectory
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|AzureUtilities
block|{
specifier|public
specifier|static
name|String
name|SEGMENT_FILE_NAME_PATTERN
init|=
literal|"^([0-9a-f]{4})\\.([0-9a-f-]+)$"
decl_stmt|;
specifier|private
name|AzureUtilities
parameter_list|()
block|{     }
specifier|public
specifier|static
name|String
name|getSegmentFileName
parameter_list|(
name|AzureSegmentArchiveEntry
name|indexEntry
parameter_list|)
block|{
return|return
name|getSegmentFileName
argument_list|(
name|indexEntry
operator|.
name|getPosition
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getMsb
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getLsb
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getSegmentFileName
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%04x.%s"
argument_list|,
name|offset
argument_list|,
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getName
parameter_list|(
name|CloudBlob
name|blob
parameter_list|)
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|blob
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|getName
parameter_list|(
name|CloudBlobDirectory
name|directory
parameter_list|)
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|directory
operator|.
name|getUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|Stream
argument_list|<
name|CloudBlob
argument_list|>
name|getBlobs
parameter_list|(
name|CloudBlobDirectory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|directory
operator|.
name|listBlobs
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|instanceof
name|CloudBlob
argument_list|)
operator|.
name|map
argument_list|(
name|i
lambda|->
operator|(
name|CloudBlob
operator|)
name|i
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|URISyntaxException
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
block|}
specifier|public
specifier|static
name|long
name|getDirectorySize
parameter_list|(
name|CloudBlobDirectory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CloudBlob
name|b
range|:
name|getBlobs
argument_list|(
name|directory
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
control|)
block|{
try|try
block|{
name|b
operator|.
name|downloadAttributes
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
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
name|size
operator|+=
name|b
operator|.
name|getProperties
argument_list|()
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
specifier|public
specifier|static
name|void
name|readBufferFully
parameter_list|(
name|CloudBlob
name|blob
parameter_list|,
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|buffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|long
name|readBytes
init|=
name|blob
operator|.
name|downloadToByteArray
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|limit
argument_list|()
operator|!=
name|readBytes
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Buffer size: "
operator|+
name|buffer
operator|.
name|limit
argument_list|()
operator|+
literal|", read bytes: "
operator|+
name|readBytes
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageException
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
block|}
block|}
end_class

end_unit
