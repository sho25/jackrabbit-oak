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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Deflater
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
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

begin_class
class|class
name|FlatFileStoreUtils
block|{
specifier|public
specifier|static
name|BufferedReader
name|createReader
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|compressionEnabled
parameter_list|)
block|{
try|try
block|{
name|BufferedReader
name|br
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressionEnabled
condition|)
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|GZIPInputStream
argument_list|(
name|in
argument_list|,
literal|2048
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|br
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
literal|"Error opening file "
operator|+
name|file
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|BufferedWriter
name|createWriter
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|compressionEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressionEnabled
condition|)
block|{
name|out
operator|=
operator|new
name|GZIPOutputStream
argument_list|(
name|out
argument_list|,
literal|2048
argument_list|)
block|{
block|{
name|def
operator|.
name|setLevel
parameter_list|(
name|Deflater
operator|.
name|BEST_SPEED
parameter_list|)
constructor_decl|;
block|}
block|}
expr_stmt|;
block|}
return|return
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|long
name|sizeOf
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|sortedFiles
parameter_list|)
block|{
return|return
name|sortedFiles
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|File
operator|::
name|length
argument_list|)
operator|.
name|sum
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|getSortedStoreFileName
parameter_list|(
name|boolean
name|compressionEnabled
parameter_list|)
block|{
return|return
name|compressionEnabled
condition|?
literal|"store-sorted.json.gz"
else|:
literal|"store-sorted.json"
return|;
block|}
block|}
end_class

end_unit
