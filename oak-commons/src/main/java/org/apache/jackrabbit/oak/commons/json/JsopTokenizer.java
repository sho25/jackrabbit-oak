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
comment|/**  * A tokenizer for Json and Jsop strings.  */
end_comment

begin_class
specifier|public
class|class
name|JsopTokenizer
implements|implements
name|JsopReader
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|TYPE
init|=
block|{
literal|"end"
block|,
literal|"string"
block|,
literal|"number"
block|,
literal|"true"
block|,
literal|"false"
block|,
literal|"null"
block|,
literal|"error"
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
name|jsop
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|private
name|int
name|lastPos
decl_stmt|;
specifier|private
name|int
name|pos
decl_stmt|;
specifier|private
name|int
name|currentType
decl_stmt|;
specifier|private
name|boolean
name|currentEscaped
decl_stmt|;
specifier|private
name|String
name|currentToken
decl_stmt|;
specifier|private
name|int
name|lastType
decl_stmt|;
specifier|private
name|String
name|lastToken
decl_stmt|;
specifier|private
name|boolean
name|lastEscaped
decl_stmt|;
specifier|public
name|JsopTokenizer
parameter_list|(
name|String
name|json
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|jsop
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|json
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|read
argument_list|()
expr_stmt|;
block|}
specifier|public
name|JsopTokenizer
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|this
argument_list|(
name|json
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetReader
parameter_list|()
block|{
name|pos
operator|=
literal|0
expr_stmt|;
name|read
argument_list|()
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
name|jsop
return|;
block|}
comment|/**      * Get the token type of the last token.      *      * @return the token type      */
annotation|@
name|Override
specifier|public
name|int
name|getTokenType
parameter_list|()
block|{
return|return
name|lastType
return|;
block|}
comment|/**      * Get the last token value if the the token type was STRING or NUMBER. For      * STRING, the text is decoded; for NUMBER, it is returned as parsed. In all      * other cases the result is undefined.      *      * @return the token      */
annotation|@
name|Override
specifier|public
name|String
name|getToken
parameter_list|()
block|{
if|if
condition|(
name|lastType
operator|>
name|COMMENT
condition|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
operator|(
name|char
operator|)
name|lastType
argument_list|)
return|;
block|}
return|return
name|lastEscaped
condition|?
name|decode
argument_list|(
name|lastToken
argument_list|)
else|:
name|lastToken
return|;
block|}
comment|/**      * Get the last encoded (raw) string, including escape sequences.      *      * @return the encoded string      */
specifier|public
name|String
name|getEscapedToken
parameter_list|()
block|{
return|return
name|lastToken
return|;
block|}
comment|/**      * Read a token which must match a given token type.      *      * @param type the token type      * @return the token (a null object when reading a null value)      * @throws IllegalStateException if the token type doesn't match      */
annotation|@
name|Override
specifier|public
name|String
name|read
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|getToken
argument_list|()
return|;
block|}
throw|throw
name|getFormatException
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|,
name|getTokenType
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
specifier|private
name|void
name|skip
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
name|matches
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
name|getFormatException
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|,
name|getTokenType
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * Read a string.      *      * @return the de-escaped string      * @throws IllegalStateException if the token type doesn't match      */
annotation|@
name|Override
specifier|public
name|String
name|readString
parameter_list|()
block|{
return|return
name|read
argument_list|(
name|STRING
argument_list|)
return|;
block|}
comment|/**      * Read a token which must match a given token type.      *      * @param type the token type      * @return true if there was a match      */
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|currentType
operator|==
name|type
condition|)
block|{
name|read
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Read a token and return the token type.      *      * @return the token type      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
name|lastPos
operator|=
name|pos
expr_stmt|;
name|lastType
operator|=
name|currentType
expr_stmt|;
name|lastToken
operator|=
name|currentToken
expr_stmt|;
name|lastEscaped
operator|=
name|currentEscaped
expr_stmt|;
try|try
block|{
name|currentType
operator|=
name|readToken
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|currentType
operator|=
name|ERROR
expr_stmt|;
name|currentToken
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|currentType
operator|=
name|ERROR
expr_stmt|;
name|currentToken
operator|=
name|addAsterisk
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
return|return
name|lastType
return|;
block|}
specifier|private
name|int
name|readToken
parameter_list|()
block|{
name|currentEscaped
operator|=
literal|false
expr_stmt|;
name|char
name|ch
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|pos
operator|>=
name|length
condition|)
block|{
return|return
name|END
return|;
block|}
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|>
literal|' '
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
name|int
name|start
init|=
name|pos
operator|++
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'\"'
case|:
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\"'
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
name|currentEscaped
operator|=
literal|true
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
block|}
name|currentToken
operator|=
name|jsop
operator|.
name|substring
argument_list|(
name|start
operator|+
literal|1
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|STRING
return|;
block|}
case|case
literal|'{'
case|:
case|case
literal|'}'
case|:
case|case
literal|'['
case|:
case|case
literal|']'
case|:
case|case
literal|'+'
case|:
case|case
literal|':'
case|:
case|case
literal|','
case|:
case|case
literal|'>'
case|:
case|case
literal|'^'
case|:
case|case
literal|'*'
case|:
case|case
literal|'='
case|:
case|case
literal|';'
case|:
return|return
name|ch
return|;
case|case
literal|'/'
case|:
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|!=
literal|'*'
condition|)
block|{
return|return
literal|'/'
return|;
block|}
name|pos
operator|++
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'*'
operator|&&
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|==
literal|'/'
condition|)
block|{
break|break;
block|}
block|}
name|currentToken
operator|=
name|jsop
operator|.
name|substring
argument_list|(
name|start
operator|+
literal|2
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|2
expr_stmt|;
return|return
name|COMMENT
return|;
block|}
case|case
literal|'-'
case|:
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
block|{
comment|// lookahead
return|return
literal|'-'
return|;
block|}
comment|// else fall though
default|default:
if|if
condition|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
condition|)
block|{
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|ch
operator|==
literal|'.'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
operator|++
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
operator|++
name|pos
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
block|}
name|currentToken
operator|=
name|jsop
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pos
argument_list|)
expr_stmt|;
return|return
name|NUMBER
return|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|>=
literal|'a'
operator|&&
name|ch
operator|<=
literal|'z'
condition|)
block|{
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|ch
operator|=
name|jsop
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|ch
argument_list|<
literal|'a'
operator|||
name|ch
argument_list|>
literal|'z'
operator|)
operator|&&
name|ch
operator|!=
literal|'_'
operator|&&
operator|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
operator|)
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
name|String
name|s
init|=
name|jsop
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|currentToken
operator|=
literal|null
expr_stmt|;
return|return
name|NULL
return|;
block|}
elseif|else
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|currentToken
operator|=
name|s
expr_stmt|;
return|return
name|TRUE
return|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|currentToken
operator|=
name|s
expr_stmt|;
return|return
name|FALSE
return|;
block|}
else|else
block|{
name|currentToken
operator|=
name|s
expr_stmt|;
return|return
name|IDENTIFIER
return|;
block|}
block|}
throw|throw
name|getFormatException
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|)
throw|;
block|}
block|}
comment|/**      * Decode a quoted Json string.      *      * @param s the encoded string, with double quotes      * @return the string      */
specifier|public
specifier|static
name|String
name|decodeQuoted
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
literal|2
operator|||
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'\"'
operator|||
name|s
operator|.
name|charAt
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'\"'
condition|)
block|{
throw|throw
name|getFormatException
argument_list|(
name|s
argument_list|,
literal|0
argument_list|)
throw|;
block|}
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|decode
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**      * Decode a Json string.      *      * @param s the encoded string, without double quotes      * @return the string      */
specifier|public
specifier|static
name|String
name|decode
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|s
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
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|)
decl_stmt|;
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
literal|'\\'
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|>=
name|length
condition|)
block|{
throw|throw
name|getFormatException
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'"'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\\'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\b'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\f'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\r'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
block|{
try|try
block|{
name|c
operator|=
call|(
name|char
call|)
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|i
operator|+
literal|5
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
name|getFormatException
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|i
operator|+=
literal|4
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
throw|throw
name|getFormatException
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
throw|;
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
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|getTokenType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
name|type
operator|<=
name|COMMENT
condition|?
name|TYPE
index|[
name|type
index|]
else|:
literal|"'"
operator|+
operator|(
name|char
operator|)
name|type
operator|+
literal|"'"
return|;
block|}
specifier|private
specifier|static
name|IllegalArgumentException
name|getFormatException
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|i
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
return|return
operator|new
name|IllegalArgumentException
argument_list|(
name|addAsterisk
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
operator|+
literal|" expected: "
operator|+
name|expected
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|IllegalArgumentException
name|getFormatException
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|new
name|IllegalArgumentException
argument_list|(
name|addAsterisk
argument_list|(
name|s
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Add an asterisk ('[*]') at the given position. This format is used to      * show where parsing failed in a statement.      *      * @param s the text      * @param index the position      * @return the text with asterisk      */
specifier|private
specifier|static
name|String
name|addAsterisk
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|index
operator|=
name|Math
operator|.
name|min
argument_list|(
name|index
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
operator|+
literal|"[*]"
operator|+
name|s
operator|.
name|substring
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**      * Read a value and return the raw Json representation. This includes arrays      * and nested arrays.      *      * @return the Json representation of the value      */
annotation|@
name|Override
specifier|public
name|String
name|readRawValue
parameter_list|()
block|{
name|int
name|start
init|=
name|lastPos
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|length
operator|&&
name|jsop
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|<=
literal|' '
condition|)
block|{
name|start
operator|++
expr_stmt|;
block|}
name|skipRawValue
argument_list|()
expr_stmt|;
return|return
name|jsop
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|lastPos
argument_list|)
return|;
block|}
specifier|private
name|void
name|skipRawValue
parameter_list|()
block|{
switch|switch
condition|(
name|currentType
condition|)
block|{
case|case
literal|'['
case|:
block|{
name|int
name|level
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
if|if
condition|(
operator|--
name|level
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
name|level
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matches
argument_list|(
name|END
argument_list|)
condition|)
block|{
throw|throw
name|getFormatException
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|,
literal|"value"
argument_list|)
throw|;
block|}
else|else
block|{
name|read
argument_list|()
expr_stmt|;
block|}
block|}
break|break;
block|}
case|case
literal|'{'
case|:
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|skip
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|skipRawValue
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|NULL
case|:
case|case
name|NUMBER
case|:
case|case
name|TRUE
case|:
case|case
name|FALSE
case|:
case|case
name|COMMENT
case|:
case|case
name|STRING
case|:
case|case
name|IDENTIFIER
case|:
name|read
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
name|getFormatException
argument_list|(
name|jsop
argument_list|,
name|pos
argument_list|,
literal|"value"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|getPos
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
specifier|public
name|int
name|getLastPos
parameter_list|()
block|{
return|return
name|lastPos
return|;
block|}
specifier|public
name|void
name|setPos
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
block|}
end_class

end_unit

