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
name|mk
operator|.
name|json
package|;
end_package

begin_comment
comment|/**  * A fast Jsop writer / reader.  */
end_comment

begin_class
specifier|public
class|class
name|JsopStream
implements|implements
name|JsopReader
implements|,
name|JsopWriter
block|{
specifier|private
name|boolean
name|needComma
decl_stmt|;
specifier|private
name|int
name|len
decl_stmt|,
name|pos
decl_stmt|,
name|lastPos
decl_stmt|,
name|valuesLen
decl_stmt|;
specifier|private
name|int
index|[]
name|tokens
init|=
operator|new
name|int
index|[
literal|4
index|]
decl_stmt|;
specifier|private
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
literal|4
index|]
decl_stmt|;
comment|// write
specifier|public
name|JsopStream
name|append
parameter_list|(
name|JsopWriter
name|w
parameter_list|)
block|{
name|JsopStream
name|s
init|=
operator|(
name|JsopStream
operator|)
name|w
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|s
operator|.
name|pos
init|;
name|i
operator|<
name|s
operator|.
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|token
init|=
name|s
operator|.
name|tokens
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|token
operator|&
literal|255
condition|)
block|{
case|case
name|JsopReader
operator|.
name|STRING
case|:
case|case
name|JsopReader
operator|.
name|NUMBER
case|:
case|case
name|JsopReader
operator|.
name|IDENTIFIER
case|:
case|case
name|JsopReader
operator|.
name|COMMENT
case|:
name|Object
name|o
init|=
name|s
operator|.
name|values
index|[
name|token
operator|>>
literal|8
index|]
decl_stmt|;
name|addToken
argument_list|(
operator|(
name|token
operator|&
literal|255
operator|)
operator|+
name|addValue
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
specifier|private
name|void
name|addToken
parameter_list|(
name|int
name|x
parameter_list|)
block|{
if|if
condition|(
name|tokens
operator|.
name|length
operator|<
name|len
operator|+
literal|1
condition|)
block|{
name|growTokens
argument_list|()
expr_stmt|;
block|}
name|tokens
index|[
name|len
operator|++
index|]
operator|=
name|x
expr_stmt|;
block|}
specifier|private
name|int
name|addValue
parameter_list|(
name|Object
name|x
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|<
name|valuesLen
operator|+
literal|1
condition|)
block|{
name|growValues
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|valuesLen
index|]
operator|=
name|x
expr_stmt|;
return|return
name|valuesLen
operator|++
operator|<<
literal|8
return|;
block|}
specifier|private
name|void
name|growTokens
parameter_list|()
block|{
name|int
index|[]
name|t2
init|=
operator|new
name|int
index|[
name|tokens
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tokens
argument_list|,
literal|0
argument_list|,
name|t2
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|t2
expr_stmt|;
block|}
specifier|private
name|void
name|growValues
parameter_list|()
block|{
name|Object
index|[]
name|v2
init|=
operator|new
name|Object
index|[
name|values
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|v2
argument_list|,
literal|0
argument_list|,
name|valuesLen
argument_list|)
expr_stmt|;
name|values
operator|=
name|v2
expr_stmt|;
block|}
specifier|public
name|JsopStream
name|tag
parameter_list|(
name|char
name|tag
parameter_list|)
block|{
name|addToken
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
specifier|public
name|JsopStream
name|array
parameter_list|()
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
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
specifier|public
name|JsopStream
name|encodedValue
parameter_list|(
name|String
name|raw
parameter_list|)
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
argument_list|(
name|JsopReader
operator|.
name|COMMENT
operator|+
name|addValue
argument_list|(
name|raw
argument_list|)
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
specifier|public
name|JsopStream
name|endArray
parameter_list|()
block|{
name|addToken
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
specifier|public
name|JsopStream
name|endObject
parameter_list|()
block|{
name|addToken
argument_list|(
literal|'}'
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
specifier|public
name|JsopStream
name|key
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
argument_list|(
name|JsopReader
operator|.
name|STRING
operator|+
name|addValue
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|addToken
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
specifier|public
name|JsopStream
name|newline
parameter_list|()
block|{
name|addToken
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|JsopStream
name|object
parameter_list|()
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
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
specifier|public
name|JsopStream
name|value
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|optionalComma
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|addToken
argument_list|(
name|JsopReader
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addToken
argument_list|(
name|JsopReader
operator|.
name|STRING
operator|+
name|addValue
argument_list|(
name|value
argument_list|)
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
specifier|public
name|JsopStream
name|value
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
argument_list|(
name|JsopReader
operator|.
name|NUMBER
operator|+
name|addValue
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|x
argument_list|)
argument_list|)
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
specifier|public
name|JsopStream
name|value
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|optionalComma
argument_list|()
expr_stmt|;
name|addToken
argument_list|(
name|b
condition|?
name|JsopReader
operator|.
name|TRUE
else|:
name|JsopReader
operator|.
name|FALSE
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
specifier|public
name|void
name|resetReader
parameter_list|()
block|{
name|pos
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|resetWriter
parameter_list|()
block|{
name|needComma
operator|=
literal|false
expr_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|setLineLength
parameter_list|(
name|int
name|i
parameter_list|)
block|{
comment|// ignore
block|}
specifier|private
name|void
name|optionalComma
parameter_list|()
block|{
if|if
condition|(
name|needComma
condition|)
block|{
name|addToken
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
comment|// read
specifier|public
name|String
name|getToken
parameter_list|()
block|{
name|int
name|x
init|=
name|tokens
index|[
name|lastPos
index|]
decl_stmt|;
switch|switch
condition|(
name|x
operator|&
literal|255
condition|)
block|{
case|case
name|JsopReader
operator|.
name|STRING
case|:
case|case
name|JsopReader
operator|.
name|NUMBER
case|:
case|case
name|JsopReader
operator|.
name|IDENTIFIER
case|:
case|case
name|JsopReader
operator|.
name|COMMENT
case|:
return|return
name|values
index|[
name|x
operator|>>
literal|8
index|]
operator|.
name|toString
argument_list|()
return|;
case|case
name|JsopReader
operator|.
name|TRUE
case|:
return|return
literal|"true"
return|;
case|case
name|JsopReader
operator|.
name|FALSE
case|:
return|return
literal|"false"
return|;
case|case
name|JsopReader
operator|.
name|NULL
case|:
return|return
literal|"null"
return|;
block|}
return|return
name|Character
operator|.
name|toString
argument_list|(
call|(
name|char
call|)
argument_list|(
name|x
operator|&
literal|255
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|getTokenType
parameter_list|()
block|{
return|return
name|tokens
index|[
name|lastPos
index|]
operator|&
literal|255
return|;
block|}
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
name|getType
argument_list|()
operator|==
name|type
condition|)
block|{
name|lastPos
operator|=
name|pos
expr_stmt|;
name|pos
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|int
name|getType
parameter_list|()
block|{
name|skipNewline
argument_list|()
expr_stmt|;
return|return
name|tokens
index|[
name|pos
index|]
operator|&
literal|255
return|;
block|}
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
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expected: "
operator|+
name|type
operator|+
literal|" got: "
operator|+
name|tokens
index|[
name|pos
index|]
argument_list|)
throw|;
block|}
specifier|public
name|int
name|read
parameter_list|()
block|{
name|int
name|t
init|=
name|getType
argument_list|()
decl_stmt|;
name|lastPos
operator|=
name|pos
operator|++
expr_stmt|;
return|return
name|t
return|;
block|}
specifier|private
name|void
name|skipNewline
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|x
init|=
name|tokens
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|'\n'
condition|)
block|{
return|return;
block|}
name|pos
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|readRawValue
parameter_list|()
block|{
name|skipNewline
argument_list|()
expr_stmt|;
name|int
name|x
init|=
name|tokens
index|[
name|pos
index|]
decl_stmt|;
name|lastPos
operator|=
name|pos
operator|++
expr_stmt|;
switch|switch
condition|(
name|x
operator|&
literal|255
condition|)
block|{
case|case
name|JsopReader
operator|.
name|COMMENT
case|:
case|case
name|JsopReader
operator|.
name|NUMBER
case|:
case|case
name|JsopReader
operator|.
name|IDENTIFIER
case|:
return|return
name|values
index|[
name|x
operator|>>
literal|8
index|]
operator|.
name|toString
argument_list|()
return|;
case|case
name|JsopReader
operator|.
name|STRING
case|:
return|return
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|values
index|[
name|x
operator|>>
literal|8
index|]
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
name|JsopReader
operator|.
name|TRUE
case|:
return|return
literal|"true"
return|;
case|case
name|JsopReader
operator|.
name|FALSE
case|:
return|return
literal|"false"
return|;
case|case
name|JsopReader
operator|.
name|NULL
case|:
return|return
literal|"null"
return|;
case|case
literal|'['
case|:
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|s
init|=
name|readRawValue
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"]"
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
name|Character
operator|.
name|toString
argument_list|(
call|(
name|char
call|)
argument_list|(
name|x
operator|&
literal|255
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|readString
parameter_list|()
block|{
return|return
name|read
argument_list|(
name|JsopReader
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|x
init|=
name|tokens
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|x
operator|&
literal|255
condition|)
block|{
case|case
literal|'{'
case|:
name|buff
operator|.
name|object
argument_list|()
expr_stmt|;
break|break;
case|case
literal|'}'
case|:
name|buff
operator|.
name|endObject
argument_list|()
expr_stmt|;
break|break;
case|case
literal|'['
case|:
name|buff
operator|.
name|array
argument_list|()
expr_stmt|;
break|break;
case|case
literal|']'
case|:
name|buff
operator|.
name|endArray
argument_list|()
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|STRING
case|:
name|buff
operator|.
name|value
argument_list|(
name|values
index|[
name|x
operator|>>
literal|8
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|TRUE
case|:
name|buff
operator|.
name|value
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|FALSE
case|:
name|buff
operator|.
name|value
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|NULL
case|:
name|buff
operator|.
name|value
argument_list|(
literal|null
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopReader
operator|.
name|IDENTIFIER
case|:
case|case
name|JsopReader
operator|.
name|NUMBER
case|:
case|case
name|JsopReader
operator|.
name|COMMENT
case|:
name|buff
operator|.
name|encodedValue
argument_list|(
name|values
index|[
name|x
operator|>>
literal|8
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buff
operator|.
name|tag
argument_list|(
call|(
name|char
call|)
argument_list|(
name|x
operator|&
literal|255
argument_list|)
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
block|}
end_class

end_unit

