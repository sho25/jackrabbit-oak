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
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_class
specifier|public
class|class
name|SafeEncode
block|{
specifier|private
name|SafeEncode
parameter_list|()
block|{
comment|// Prevent instantiation.
block|}
comment|/**      * Encodes the input string by translating special characters into escape      * sequences. The resulting string is encoded according to the rules for URL      * encoding with the exception of the forward slashes and the colon, which      * are left as-is.      *      * @param s A UTF-8 string.      * @return The encoded string.      * @throws UnsupportedEncodingException      */
specifier|public
specifier|static
name|String
name|safeEncode
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|s
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"%2F"
argument_list|,
literal|"/"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"%3A"
argument_list|,
literal|":"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

