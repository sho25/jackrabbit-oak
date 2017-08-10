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
name|json
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|StringReader
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|plugins
operator|.
name|memory
operator|.
name|ArrayBasedBlob
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
name|util
operator|.
name|Base64
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

begin_class
specifier|public
class|class
name|Base64BlobSerializer
extends|extends
name|BlobSerializer
implements|implements
name|BlobDeserializer
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.serializer.maxBlobSize"
argument_list|,
operator|(
name|int
operator|)
name|FileUtils
operator|.
name|ONE_MB
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
specifier|public
name|Base64BlobSerializer
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_LIMIT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Base64BlobSerializer
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|serialize
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|blob
operator|.
name|length
argument_list|()
operator|<
name|maxSize
argument_list|,
literal|"Cannot serialize Blob of size [%s] "
operator|+
literal|"which is more than allowed maxSize of [%s]"
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
try|try
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Base64
operator|.
name|encode
argument_list|(
name|is
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|deserialize
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|value
argument_list|)
decl_stmt|;
try|try
block|{
name|Base64
operator|.
name|decode
argument_list|(
name|reader
argument_list|,
name|baos
argument_list|)
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
