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
name|commons
operator|.
name|json
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_comment
comment|/**  * A reader for Json and Jsop strings.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JsopReader
block|{
comment|/**      * The token type that signals the end of the stream.      */
name|int
name|END
init|=
literal|0
decl_stmt|;
comment|/**      * The token type of a string value.      */
name|int
name|STRING
init|=
literal|1
decl_stmt|;
comment|/**      * The token type of a number value.      */
name|int
name|NUMBER
init|=
literal|2
decl_stmt|;
comment|/**      * The token type of the value "true".      */
name|int
name|TRUE
init|=
literal|3
decl_stmt|;
comment|/**      * The token type of the value "false".      */
name|int
name|FALSE
init|=
literal|4
decl_stmt|;
comment|/**      * The token type of "null".      */
name|int
name|NULL
init|=
literal|5
decl_stmt|;
comment|/**      * The token type of a parse error.      */
name|int
name|ERROR
init|=
literal|6
decl_stmt|;
comment|/**      * The token type of an identifier (an unquoted string), if supported by the reader.      */
name|int
name|IDENTIFIER
init|=
literal|7
decl_stmt|;
comment|/**      * The token type of a comment, if supported by the reader.      */
name|int
name|COMMENT
init|=
literal|8
decl_stmt|;
comment|/**      * Read a token which must match a given token type.      *      * @param type the token type      * @return the token (null when reading a null value)      * @throws IllegalStateException if the token type doesn't match      */
name|String
name|read
parameter_list|(
name|int
name|type
parameter_list|)
function_decl|;
comment|/**      * Read a string.      *      * @return the de-escaped string (null when reading a null value)      * @throws IllegalStateException if the token type doesn't match      */
annotation|@
name|CheckForNull
name|String
name|readString
parameter_list|()
function_decl|;
comment|/**      * Read a token and return the token type.      *      * @return the token type      */
name|int
name|read
parameter_list|()
function_decl|;
comment|/**      * Read a token which must match a given token type.      *      * @param type the token type      * @return true if there was a match      */
name|boolean
name|matches
parameter_list|(
name|int
name|type
parameter_list|)
function_decl|;
comment|/**      * Return the row (escaped) token.      *      * @return the escaped string (null when reading a null value)      */
annotation|@
name|CheckForNull
name|String
name|readRawValue
parameter_list|()
function_decl|;
comment|/**      * Get the last token value if the the token type was STRING or NUMBER. For      * STRING, the text is decoded; for NUMBER, it is returned as parsed. In all      * other cases the result is undefined.      *      * @return the token      */
annotation|@
name|CheckForNull
name|String
name|getToken
parameter_list|()
function_decl|;
comment|/**      * Get the token type of the last token. The token type is one of the known      * types (END, STRING, NUMBER,...), or, for Jsop tags such as "+", "-",      * it is the Unicode character code of the tag.      *      * @return the token type      */
name|int
name|getTokenType
parameter_list|()
function_decl|;
comment|/**      * Reset the position to 0, so that to restart reading.      */
name|void
name|resetReader
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

