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
name|kernel
package|;
end_package

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
name|json
operator|.
name|JsonBuilder
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
name|json
operator|.
name|JsopReader
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
name|CoreValue
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
name|CoreValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/**  * CoreValueUtil provides methods to convert {@code CoreValue}s to the JSON  * representation passed to MicroKernel and vice versa.  */
end_comment

begin_class
class|class
name|CoreValueMapper
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|TYPE2HINT
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|HINT2TYPE
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|type
init|=
name|PropertyType
operator|.
name|UNDEFINED
init|;
name|type
operator|<=
name|PropertyType
operator|.
name|DECIMAL
condition|;
name|type
operator|++
control|)
block|{
name|String
name|hint
init|=
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|type
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|TYPE2HINT
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|hint
argument_list|)
expr_stmt|;
name|HINT2TYPE
operator|.
name|put
argument_list|(
name|hint
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Avoid instantiation.      */
specifier|private
name|CoreValueMapper
parameter_list|()
block|{     }
comment|/**      * Returns the internal JSON representation of the specified {@code value}      * that is stored in the MicroKernel. All property types that are not      * reflected as JSON types are converted to strings and get a type prefix.      *      * @param value The core value to be converted.      * @return The encoded JSON string.      * @see JsonBuilder#encode(String)      * @see JsonBuilder#encode(long)      * @see JsonBuilder#encode(long)      */
specifier|public
specifier|static
name|String
name|toJsonValue
parameter_list|(
name|CoreValue
name|value
parameter_list|)
block|{
name|String
name|jsonString
decl_stmt|;
switch|switch
condition|(
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
name|jsonString
operator|=
name|JsonBuilder
operator|.
name|encode
argument_list|(
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PropertyType
operator|.
name|LONG
case|:
name|jsonString
operator|=
name|JsonBuilder
operator|.
name|encode
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PropertyType
operator|.
name|STRING
case|:
name|String
name|str
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|startsWithHint
argument_list|(
name|str
argument_list|)
condition|)
block|{
name|jsonString
operator|=
name|buildJsonStringWithHint
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jsonString
operator|=
name|JsonBuilder
operator|.
name|encode
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
comment|// any other type
name|jsonString
operator|=
name|buildJsonStringWithHint
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|jsonString
return|;
block|}
comment|/**      * Returns an JSON array containing the JSON representation of the      * specified values.      *      * @param values The values to be converted to a JSON array.      * @return JSON array containing the JSON representation of the specified      * values.      * @see #toJsonValue(org.apache.jackrabbit.oak.api.CoreValue)      */
specifier|public
specifier|static
name|String
name|toJsonArray
parameter_list|(
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|toJsonValue
argument_list|(
name|cv
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Read a single value from the specified reader and convert it into a      * {@code CoreValue}. This method takes type-hint prefixes into account.      *      * @param reader The JSON reader.      * @param valueFactory The factory used to create the value.      * @return The value such as defined by the token obtained from the reader.      */
specifier|public
specifier|static
name|CoreValue
name|fromJsopReader
parameter_list|(
name|JsopReader
name|reader
parameter_list|,
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
name|CoreValue
name|value
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|String
name|number
init|=
name|reader
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|value
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|TRUE
argument_list|)
condition|)
block|{
name|value
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|FALSE
argument_list|)
condition|)
block|{
name|value
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|String
name|jsonString
init|=
name|reader
operator|.
name|getToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|startsWithHint
argument_list|(
name|jsonString
argument_list|)
condition|)
block|{
name|int
name|type
init|=
name|HINT2TYPE
operator|.
name|get
argument_list|(
name|jsonString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|value
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
name|jsonString
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
name|jsonString
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token: "
operator|+
name|reader
operator|.
name|getToken
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Read the list of values from the specified reader and convert them into      * {@link CoreValue}s. This method takes type-hint prefixes into account.      *      * @param reader The JSON reader.      * @param valueFactory The factory used to create the values.      * @return A list of values such as defined by the reader.      */
specifier|public
specifier|static
name|List
argument_list|<
name|CoreValue
argument_list|>
name|listFromJsopReader
parameter_list|(
name|JsopReader
name|reader
parameter_list|,
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|reader
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|fromJsopReader
argument_list|(
name|reader
argument_list|,
name|valueFactory
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Build the JSON representation of the specified value consisting of      * a leading type hint, followed by ':" and the String conversion of this      * value.      *      * @param value The value to be serialized.      * @return The string representation of the specified value including a      * leading type hint.      */
specifier|private
specifier|static
name|String
name|buildJsonStringWithHint
parameter_list|(
name|CoreValue
name|value
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|TYPE2HINT
operator|.
name|get
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|JsonBuilder
operator|.
name|encode
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if the specified JSON String represents a value      * serialization that includes a leading type hint.      *      * @param jsonString The JSON String representation of a {@code CoreValue}      * @return {@code true} if the {@code jsonString} starts with a type      * hint; {@code false} otherwise.      * @see #buildJsonStringWithHint(org.apache.jackrabbit.oak.api.CoreValue)      */
specifier|private
specifier|static
name|boolean
name|startsWithHint
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
return|return
name|jsonString
operator|.
name|length
argument_list|()
operator|>=
literal|4
operator|&&
name|jsonString
operator|.
name|charAt
argument_list|(
literal|3
argument_list|)
operator|==
literal|':'
return|;
block|}
block|}
end_class

end_unit

