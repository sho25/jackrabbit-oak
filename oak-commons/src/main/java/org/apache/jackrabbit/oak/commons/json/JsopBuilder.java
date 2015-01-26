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
comment|/**  * A builder for Json and Jsop strings. It encodes string values, and knows when  * a comma is needed. A comma is appended before '{', '[', a value, or a key;  * but only if the last appended token was '}', ']', or a value. There is no  * limit to the number of nesting levels.  */
end_comment

begin_class
specifier|public
class|class
name|JsopBuilder
implements|implements
name|JsopWriter
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|JSON_NEWLINES
init|=
literal|false
decl_stmt|;
specifier|private
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|needComma
decl_stmt|;
specifier|private
name|int
name|lineLength
decl_stmt|,
name|previous
decl_stmt|;
comment|/**      * Resets this instance.      */
annotation|@
name|Override
specifier|public
name|void
name|resetWriter
parameter_list|()
block|{
name|needComma
operator|=
literal|false
expr_stmt|;
name|buff
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLineLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|lineLength
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Append all entries of the given buffer.      *      * @param buffer the buffer      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|append
parameter_list|(
name|JsopWriter
name|buffer
parameter_list|)
block|{
name|appendTag
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append a Jsop tag character.      *      * @param tag the string to append      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|tag
parameter_list|(
name|char
name|tag
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append an (already formatted) Jsop tag. This will allow to append      * non-Json data. This method resets the comma flag, so no comma is added      * before the next key or value.      *      * @param string the string to append      * @return this      */
specifier|private
name|JsopBuilder
name|appendTag
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append a newline character.      *      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|newline
parameter_list|()
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append '{'. A comma is appended first if needed.      *      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|object
parameter_list|()
block|{
name|optionalCommaAndNewline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append '}'.      *      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|endObject
parameter_list|()
block|{
if|if
condition|(
name|JSON_NEWLINES
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"\n}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
name|needComma
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append '['. A comma is appended first if needed.      *      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|array
parameter_list|()
block|{
name|optionalCommaAndNewline
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append ']'.      *      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|endArray
parameter_list|()
block|{
name|buff
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append the key (in quotes) plus a colon. A comma is appended first if      * needed.      *      * @param name the key      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|key
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|optionalCommaAndNewline
argument_list|(
name|name
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|JSON_NEWLINES
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|encode
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Append a number. A comma is appended first if needed.      *      * @param value the value      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|value
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|encodedValue
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Append the boolean value 'true' or 'false'. A comma is appended first if      * needed.      *      * @param value the value      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|value
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
name|encodedValue
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Append a string or null. A comma is appended first if needed.      *      * @param value the value      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|value
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|encodedValue
argument_list|(
name|encode
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Append an already encoded value. A comma is appended first if needed.      *      * @param value the value      * @return this      */
annotation|@
name|Override
specifier|public
name|JsopBuilder
name|encodedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|optionalCommaAndNewline
argument_list|(
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|optionalCommaAndNewline
parameter_list|(
name|int
name|add
parameter_list|)
block|{
if|if
condition|(
name|needComma
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lineLength
operator|>
literal|0
condition|)
block|{
name|int
name|len
init|=
name|buff
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|>
name|lineLength
operator|/
literal|4
operator|&&
name|len
operator|+
name|add
operator|-
name|previous
operator|>
name|lineLength
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|previous
operator|=
name|len
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Get the generated string.      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Convert a string to a quoted Json literal using the correct escape      * sequences. The literal is enclosed in double quotes. Characters outside      * the range 32..127 are encoded (backslash u xxxx). The forward slash      * (solidus) is not escaped. Null is encoded as "null" (without quotes).      *      * @param s the text to convert      * @return the Json representation (including double quotes)      */
specifier|public
specifier|static
name|String
name|encode
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
name|int
name|length
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|"\"\""
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\"'
operator|||
name|c
operator|==
literal|'\\'
operator|||
name|c
operator|<
literal|' '
operator|||
operator|(
name|c
operator|>=
literal|0xd800
operator|&&
name|c
operator|<=
literal|0xdbff
operator|)
condition|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
operator|+
literal|2
operator|+
name|length
operator|/
literal|8
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
name|escape
argument_list|(
name|s
argument_list|,
name|length
argument_list|,
name|buff
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
operator|+
literal|2
argument_list|)
decl_stmt|;
return|return
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Escape a string into the target buffer.      *      * @param s      the string to escape      * @param buff   the target buffer      */
specifier|public
specifier|static
name|void
name|escape
parameter_list|(
name|String
name|s
parameter_list|,
name|StringBuilder
name|buff
parameter_list|)
block|{
name|escape
argument_list|(
name|s
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|buff
argument_list|)
expr_stmt|;
block|}
comment|/**      * Escape a string into the target buffer.      *      * @param s      the string to escape      * @param length the number of characters.      * @param buff   the target buffer      */
specifier|private
specifier|static
name|void
name|escape
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|length
parameter_list|,
name|StringBuilder
name|buff
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'"'
case|:
comment|// quotation mark
name|buff
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\\'
case|:
comment|// backslash
name|buff
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\b'
case|:
comment|// backspace
name|buff
operator|.
name|append
argument_list|(
literal|"\\b"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\f'
case|:
comment|// formfeed
name|buff
operator|.
name|append
argument_list|(
literal|"\\f"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
comment|// newline
name|buff
operator|.
name|append
argument_list|(
literal|"\\n"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
comment|// carriage return
name|buff
operator|.
name|append
argument_list|(
literal|"\\r"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\t'
case|:
comment|// horizontal tab
name|buff
operator|.
name|append
argument_list|(
literal|"\\t"
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|c
operator|<
literal|' '
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\\u%04x"
argument_list|,
operator|(
name|int
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|0xd800
operator|&&
name|c
operator|<=
literal|0xdbff
condition|)
block|{
comment|// isSurrogate(), only available in Java 7
if|if
condition|(
name|i
operator|<
name|length
operator|-
literal|1
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|c
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
block|{
comment|// ok surrogate
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// broken surrogate -> escape
name|buff
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\\u%04x"
argument_list|,
operator|(
name|int
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Get the buffer length.      *      * @return the length      */
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|buff
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**      * Beautify (format) the json / jsop string.      *      * @param jsop the jsop string      * @return the formatted string      */
specifier|public
specifier|static
name|String
name|prettyPrint
parameter_list|(
name|String
name|jsop
parameter_list|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|jsop
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|prettyPrint
argument_list|(
name|buff
argument_list|,
name|t
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|getTokenType
argument_list|()
operator|==
name|JsopReader
operator|.
name|END
condition|)
block|{
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
specifier|static
name|String
name|prettyPrint
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|JsopTokenizer
name|t
parameter_list|,
name|String
name|ident
parameter_list|)
block|{
name|String
name|space
init|=
literal|""
decl_stmt|;
name|boolean
name|inArray
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|token
init|=
name|t
operator|.
name|read
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|JsopReader
operator|.
name|END
case|:
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
case|case
name|JsopReader
operator|.
name|STRING
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|getEscapedToken
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\"'
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|NUMBER
case|:
case|case
name|JsopReader
operator|.
name|TRUE
case|:
case|case
name|JsopReader
operator|.
name|FALSE
case|:
case|case
name|JsopReader
operator|.
name|NULL
case|:
case|case
name|JsopReader
operator|.
name|IDENTIFIER
case|:
case|case
name|JsopReader
operator|.
name|ERROR
case|:
name|buff
operator|.
name|append
argument_list|(
name|t
operator|.
name|getEscapedToken
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'{'
case|:
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"{}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
operator|.
name|append
argument_list|(
name|space
operator|+=
name|ident
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|'}'
case|:
name|space
operator|=
name|space
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|space
operator|.
name|length
argument_list|()
operator|-
name|ident
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|space
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'['
case|:
name|inArray
operator|=
literal|true
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
break|break;
case|case
literal|']'
case|:
name|inArray
operator|=
literal|false
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|','
case|:
if|if
condition|(
operator|!
name|inArray
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
operator|.
name|append
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|buff
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|token
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

