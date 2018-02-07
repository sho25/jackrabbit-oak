begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|util
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
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  * Utility class related to encoding characters into (UTF-8) byte sequences.  */
end_comment

begin_class
specifier|public
class|class
name|CharsetEncodingUtils
block|{
specifier|private
name|CharsetEncodingUtils
parameter_list|()
block|{     }
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|CharsetEncoder
argument_list|>
name|CSE
init|=
operator|new
name|ThreadLocal
argument_list|<
name|CharsetEncoder
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|CharsetEncoder
name|initialValue
parameter_list|()
block|{
name|CharsetEncoder
name|e
init|=
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|newEncoder
argument_list|()
decl_stmt|;
name|e
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
expr_stmt|;
name|e
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
name|byte
index|[]
name|bytes
parameter_list|(
name|ByteBuffer
name|b
parameter_list|)
block|{
name|byte
index|[]
name|a
init|=
operator|new
name|byte
index|[
name|b
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|b
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**      * Like {@link String#getBytes(java.nio.charset.Charset)} (with "UTF-8"),      * except that encoding problems (like unpaired surrogates) are reported as      * exceptions (see {@link CodingErrorAction#REPORT}, instead of being      * silently replaces as it would happen otherwise.      *       * @param input      *            String to encode      * @return String encoded using {@link StandardCharsets#UTF_8}      * @throws IOException      *             on encoding error      */
specifier|public
specifier|static
name|byte
index|[]
name|encodeAsUTF8
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|CharsetEncoder
name|e
init|=
name|CSE
operator|.
name|get
argument_list|()
decl_stmt|;
name|e
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|bytes
argument_list|(
name|e
operator|.
name|encode
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|input
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

