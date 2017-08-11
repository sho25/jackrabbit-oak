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
name|exporter
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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Files
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|stream
operator|.
name|JsonWriter
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
name|commons
operator|.
name|json
operator|.
name|JsopWriter
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
name|json
operator|.
name|Base64BlobSerializer
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
name|json
operator|.
name|BlobSerializer
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
name|json
operator|.
name|JsonSerializer
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
name|NodeStateUtils
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
name|Charsets
operator|.
name|UTF_8
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
name|checkState
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateSerializer
block|{
specifier|public
enum|enum
name|Format
block|{
name|JSON
block|,
name|TXT
block|}
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
specifier|private
name|String
name|blobDirName
init|=
literal|"blobs"
decl_stmt|;
specifier|private
name|String
name|jsonFileName
init|=
literal|"nodestates.json"
decl_stmt|;
specifier|private
name|String
name|txtFileName
init|=
literal|"nodestates.txt"
decl_stmt|;
specifier|private
name|int
name|depth
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|private
name|int
name|maxChildNodes
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|private
name|String
name|filter
init|=
literal|"{}"
decl_stmt|;
specifier|private
name|File
name|filterFile
decl_stmt|;
specifier|private
name|String
name|path
init|=
literal|"/"
decl_stmt|;
specifier|private
name|Format
name|format
init|=
name|Format
operator|.
name|JSON
decl_stmt|;
specifier|private
name|boolean
name|serializeBlobContent
decl_stmt|;
specifier|private
name|boolean
name|prettyPrint
init|=
literal|true
decl_stmt|;
specifier|private
name|FSBlobSerializer
name|blobSerializer
decl_stmt|;
specifier|public
name|NodeStateSerializer
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
block|}
specifier|public
name|String
name|serialize
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|serialize
argument_list|(
name|sw
argument_list|,
name|createBlobSerializer
argument_list|()
argument_list|)
expr_stmt|;
name|closeSerializer
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|checkArgument
argument_list|(
name|dir
operator|.
name|isDirectory
argument_list|()
argument_list|,
literal|"Input file must be directory [%s]"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkState
argument_list|(
name|dir
operator|.
name|mkdirs
argument_list|()
argument_list|,
literal|"Cannot create directory [%s]"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|getFileName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Writer
name|writer
init|=
name|Files
operator|.
name|newWriter
argument_list|(
name|file
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|serialize
argument_list|(
name|writer
argument_list|,
name|createBlobSerializer
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|closeSerializer
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|serialize
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|BlobSerializer
name|blobSerializer
parameter_list|)
throws|throws
name|IOException
block|{
name|JsopWriter
name|jsopWriter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|format
operator|==
name|Format
operator|.
name|JSON
condition|)
block|{
name|JsonWriter
name|jw
init|=
operator|new
name|JsonWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|prettyPrint
condition|)
block|{
name|jw
operator|.
name|setIndent
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|jsopWriter
operator|=
operator|new
name|JsopStreamWriter
argument_list|(
name|jw
argument_list|)
expr_stmt|;
block|}
name|serialize
argument_list|(
name|jsopWriter
argument_list|,
name|blobSerializer
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|serialize
parameter_list|(
name|JsopWriter
name|writer
parameter_list|,
name|BlobSerializer
name|blobSerializer
parameter_list|)
throws|throws
name|IOException
block|{
name|JsonSerializer
name|serializer
init|=
operator|new
name|JsonSerializer
argument_list|(
name|writer
argument_list|,
name|depth
argument_list|,
literal|0
argument_list|,
name|maxChildNodes
argument_list|,
name|getFilter
argument_list|()
argument_list|,
name|blobSerializer
argument_list|)
decl_stmt|;
name|NodeState
name|state
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeState
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BlobSerializer
name|createBlobSerializer
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
if|if
condition|(
operator|!
name|serializeBlobContent
condition|)
block|{
return|return
operator|new
name|BlobSerializer
argument_list|()
return|;
block|}
name|File
name|blobs
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|blobDirName
argument_list|)
decl_stmt|;
name|blobSerializer
operator|=
operator|new
name|FSBlobSerializer
argument_list|(
name|blobs
argument_list|)
expr_stmt|;
return|return
name|blobSerializer
return|;
block|}
specifier|private
name|void
name|closeSerializer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|blobSerializer
operator|!=
literal|null
condition|)
block|{
name|blobSerializer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|BlobSerializer
name|createBlobSerializer
parameter_list|()
block|{
return|return
name|serializeBlobContent
condition|?
operator|new
name|Base64BlobSerializer
argument_list|()
else|:
operator|new
name|BlobSerializer
argument_list|()
return|;
block|}
specifier|private
name|String
name|getFilter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|filterFile
operator|!=
literal|null
condition|?
name|Files
operator|.
name|toString
argument_list|(
name|filterFile
argument_list|,
name|UTF_8
argument_list|)
else|:
name|filter
return|;
block|}
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|format
operator|==
name|Format
operator|.
name|JSON
condition|?
name|jsonFileName
else|:
name|txtFileName
return|;
block|}
specifier|public
name|String
name|getBlobDirName
parameter_list|()
block|{
return|return
name|blobDirName
return|;
block|}
specifier|public
name|void
name|setBlobDirName
parameter_list|(
name|String
name|blobDirName
parameter_list|)
block|{
name|this
operator|.
name|blobDirName
operator|=
name|blobDirName
expr_stmt|;
block|}
specifier|public
name|void
name|setJsonFileName
parameter_list|(
name|String
name|jsonFileName
parameter_list|)
block|{
name|this
operator|.
name|jsonFileName
operator|=
name|jsonFileName
expr_stmt|;
block|}
specifier|public
name|void
name|setTxtFileName
parameter_list|(
name|String
name|txtFileName
parameter_list|)
block|{
name|this
operator|.
name|txtFileName
operator|=
name|txtFileName
expr_stmt|;
block|}
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxChildNodes
parameter_list|(
name|int
name|maxChildNodes
parameter_list|)
block|{
name|this
operator|.
name|maxChildNodes
operator|=
name|maxChildNodes
expr_stmt|;
block|}
specifier|public
name|void
name|setFilter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
specifier|public
name|void
name|setFilterFile
parameter_list|(
name|File
name|filterFile
parameter_list|)
block|{
name|this
operator|.
name|filterFile
operator|=
name|filterFile
expr_stmt|;
block|}
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|void
name|setFormat
parameter_list|(
name|Format
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
specifier|public
name|void
name|setSerializeBlobContent
parameter_list|(
name|boolean
name|serializeBlobContent
parameter_list|)
block|{
name|this
operator|.
name|serializeBlobContent
operator|=
name|serializeBlobContent
expr_stmt|;
block|}
specifier|public
name|void
name|setPrettyPrint
parameter_list|(
name|boolean
name|prettyPrint
parameter_list|)
block|{
name|this
operator|.
name|prettyPrint
operator|=
name|prettyPrint
expr_stmt|;
block|}
block|}
end_class

end_unit

