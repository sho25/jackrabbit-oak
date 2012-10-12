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
name|memory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * This {@code Blob} implementations is based on a string.  */
end_comment

begin_class
specifier|public
class|class
name|StringBasedBlob
extends|extends
name|AbstractBlob
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|StringBasedBlob
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * This implementation returns the bytes of the UTF-8 encoding      * of the underlying string.      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"UTF-8 is not supported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * This implementation returns the number of bytes in the UTF-8 encoding      * of the underlying string.      */
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
name|value
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|length
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"UTF-8 is not supported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

