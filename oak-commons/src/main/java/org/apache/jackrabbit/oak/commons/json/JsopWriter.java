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

begin_comment
comment|/**  * A builder for Json and Json diff strings. It knows when a comma is needed. A  * comma is appended before '{', '[', a value, or a key; but only if the last  * appended token was '}', ']', or a value. There is no limit to the number of  * nesting levels.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JsopWriter
block|{
comment|/**      * Append '['. A comma is appended first if needed.      *      * @return this      */
name|JsopWriter
name|array
parameter_list|()
function_decl|;
comment|/**      * Append '{'. A comma is appended first if needed.      *      * @return this      */
name|JsopWriter
name|object
parameter_list|()
function_decl|;
comment|/**      * Append the key (in quotes) plus a colon. A comma is appended first if      * needed.      *      * @param name the key      * @return this      */
name|JsopWriter
name|key
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * Append a string or null. A comma is appended first if needed.      *      * @param value the value      * @return this      */
name|JsopWriter
name|value
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**      * Append an already encoded value. A comma is appended first if needed.      *      * @param value the value      * @return this      */
name|JsopWriter
name|encodedValue
parameter_list|(
name|String
name|raw
parameter_list|)
function_decl|;
comment|/**      * Append '}'.      *      * @return this      */
name|JsopWriter
name|endObject
parameter_list|()
function_decl|;
comment|/**      * Append ']'.      *      * @return this      */
name|JsopWriter
name|endArray
parameter_list|()
function_decl|;
comment|/**      * Append a Jsop tag character.      *      * @param tag the string to append      * @return this      */
name|JsopWriter
name|tag
parameter_list|(
name|char
name|tag
parameter_list|)
function_decl|;
comment|/**      * Append all entries of the given buffer.      *      * @param buffer the buffer      * @return this      */
name|JsopWriter
name|append
parameter_list|(
name|JsopWriter
name|diff
parameter_list|)
function_decl|;
comment|/**      * Append a number. A comma is appended first if needed.      *      * @param value the value      * @return this      */
name|JsopWriter
name|value
parameter_list|(
name|long
name|x
parameter_list|)
function_decl|;
comment|/**      * Append the boolean value 'true' or 'false'. A comma is appended first if      * needed.      *      * @param value the value      * @return this      */
name|JsopWriter
name|value
parameter_list|(
name|boolean
name|b
parameter_list|)
function_decl|;
comment|/**      * Append a newline character.      *      * @return this      */
name|JsopWriter
name|newline
parameter_list|()
function_decl|;
comment|/**      * Resets this instance, so that all data is discarded.      */
name|void
name|resetWriter
parameter_list|()
function_decl|;
comment|/**      * Set the line length, after which a newline is added (to improve      * readability).      *      * @param length the length      */
name|void
name|setLineLength
parameter_list|(
name|int
name|length
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

