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
name|jcr
operator|.
name|json
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * A {@code JsonValue} represents either an {@link JsonArray array}, an  * {@link JsonObject object} or a {@link JsonAtom primitive value} (atom)  * of a JSON document.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JsonValue
block|{
specifier|public
enum|enum
name|Type
block|{
name|STRING
argument_list|(
literal|false
argument_list|)
block|,
name|NUMBER
argument_list|(
literal|false
argument_list|)
block|,
name|BOOLEAN
argument_list|(
literal|false
argument_list|)
block|,
name|NULL
argument_list|(
literal|false
argument_list|)
block|,
name|OBJECT
argument_list|(
literal|true
argument_list|)
block|,
name|ARRAY
argument_list|(
literal|true
argument_list|)
block|;
specifier|private
specifier|final
name|boolean
name|compound
decl_stmt|;
name|Type
parameter_list|(
name|boolean
name|compound
parameter_list|)
block|{
name|this
operator|.
name|compound
operator|=
name|compound
expr_stmt|;
block|}
comment|/**          * @return {@code true} for {@link Type#ARRAY} and {@link Type#OBJECT},          * {@code false} otherwise.          */
specifier|public
name|boolean
name|compound
parameter_list|()
block|{
return|return
name|compound
return|;
block|}
block|}
comment|/**      * Visitor for dispatching compound {@code JsonValue}s.      */
specifier|public
specifier|abstract
specifier|static
class|class
name|Visitor
block|{
specifier|public
name|void
name|visit
parameter_list|(
name|JsonAtom
name|atom
parameter_list|)
block|{ }
specifier|public
name|void
name|visit
parameter_list|(
name|JsonArray
name|array
parameter_list|)
block|{ }
specifier|public
name|void
name|visit
parameter_list|(
name|JsonObject
name|object
parameter_list|)
block|{ }
block|}
comment|/**      * Convert {@code jsonValue} to its JSON representation.      * @param jsonValue      * @return a JSON representation of {@code jsonValue}      */
specifier|public
specifier|static
name|String
name|toJson
parameter_list|(
name|JsonValue
name|jsonValue
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|jsonValue
operator|.
name|accept
argument_list|(
operator|new
name|Visitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|JsonAtom
name|atom
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|toJson
argument_list|(
name|atom
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|JsonArray
name|array
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|String
name|comma
init|=
literal|""
decl_stmt|;
for|for
control|(
name|JsonValue
name|value
range|:
name|array
operator|.
name|value
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|comma
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|","
expr_stmt|;
name|value
operator|.
name|accept
argument_list|(
name|this
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|JsonObject
name|object
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|String
name|comma
init|=
literal|""
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
name|entry
range|:
name|object
operator|.
name|value
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|comma
argument_list|)
expr_stmt|;
name|comma
operator|=
literal|","
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters      * (U+0000 through U+001F) in {@code text}.      * @param text      * @return {@code text} with control characters escaped      */
specifier|public
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'"'
case|:
name|sb
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
name|sb
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
name|sb
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
name|sb
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
name|sb
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
name|sb
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
name|sb
operator|.
name|append
argument_list|(
literal|"\\t"
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|//Reference: http://www.unicode.org/versions/Unicode5.1.0/
if|if
condition|(
name|ch
operator|>=
literal|'\u0000'
operator|&&
name|ch
operator|<=
literal|'\u001F'
operator|||
name|ch
operator|>=
literal|'\u007F'
operator|&&
name|ch
operator|<=
literal|'\u009F'
operator|||
name|ch
operator|>=
literal|'\u2000'
operator|&&
name|ch
operator|<=
literal|'\u20FF'
condition|)
block|{
name|String
name|ss
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|ch
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\\u"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|4
operator|-
name|ss
operator|.
name|length
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|ss
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Unescape escaped control characters in {@code text}      * @param text      * @return {@code text} with control characters escaped      * @throws StringIndexOutOfBoundsException  on unterminated escape sequences      * @throws NumberFormatException  on invalid escape sequences      */
specifier|public
specifier|static
name|String
name|unescape
parameter_list|(
name|String
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|text
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
operator|++
name|k
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'b'
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\b'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\f'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\r'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
name|String
name|u
init|=
name|text
operator|.
name|substring
argument_list|(
operator|++
name|k
argument_list|,
name|k
operator|+=
literal|4
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|u
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'x'
case|:
name|String
name|x
init|=
name|text
operator|.
name|substring
argument_list|(
operator|++
name|k
argument_list|,
name|k
operator|+=
literal|2
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|x
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * @return the value carried by this {@code JsonValue}      */
specifier|public
specifier|abstract
name|Object
name|value
parameter_list|()
function_decl|;
comment|/**      * @return the type of this {@code JsonValue}      */
specifier|public
specifier|abstract
name|Type
name|type
parameter_list|()
function_decl|;
comment|/**      * Dispatch this {@code JsonValue} using {@code visitor}      * @param visitor      */
specifier|public
specifier|abstract
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
function_decl|;
comment|/**      * @return {@code true} iff {@code this} is an instance of {@code JsonAtom}      */
specifier|public
name|boolean
name|isAtom
parameter_list|()
block|{
return|return
operator|!
name|type
argument_list|()
operator|.
name|compound
argument_list|()
return|;
block|}
comment|/**      * @return {@code true} iff {@code this} is an instance of {@code JsonArray}      */
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|type
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
return|;
block|}
comment|/**      * @return {@code true} iff {@code this} is an instance of {@code JsonObject}      */
specifier|public
name|boolean
name|isObject
parameter_list|()
block|{
return|return
name|type
argument_list|()
operator|==
name|Type
operator|.
name|OBJECT
return|;
block|}
comment|/**      * @return {@code true} iff {@code this} represents a JSON {@code null} value      */
specifier|public
name|boolean
name|isNull
parameter_list|()
block|{
return|return
name|this
operator|==
name|JsonAtom
operator|.
name|NULL
operator|||
name|equals
argument_list|(
name|JsonAtom
operator|.
name|NULL
argument_list|)
return|;
block|}
comment|/**      * @return {@code true} iff {@code this} represents a JSON {@code true} value      */
specifier|public
name|boolean
name|isTrue
parameter_list|()
block|{
return|return
name|this
operator|==
name|JsonAtom
operator|.
name|TRUE
operator|||
name|equals
argument_list|(
name|JsonAtom
operator|.
name|TRUE
argument_list|)
return|;
block|}
comment|/**      * @return {@code true} iff {@code this} represents a JSON {@code false} value      */
specifier|public
name|boolean
name|isFalse
parameter_list|()
block|{
return|return
name|this
operator|==
name|JsonAtom
operator|.
name|FALSE
operator|||
name|equals
argument_list|(
name|JsonAtom
operator|.
name|FALSE
argument_list|)
return|;
block|}
comment|/**      * @return {@code this} as {@code JsonAtom}      * @throws UnsupportedOperationException if {@code this} is not an instance of {@code JsonAtom}      */
specifier|public
name|JsonAtom
name|asAtom
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * @return {@code this} as {@code JsonArray}      * @throws UnsupportedOperationException if {@code this} is not an instance of {@code JsonArray}      */
specifier|public
name|JsonArray
name|asArray
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * @return {@code this} as {@code JsonObject}      * @throws UnsupportedOperationException if {@code this} is not an instance of {@code JsonObject}      */
specifier|public
name|JsonObject
name|asObject
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Convert this {@code JsonValue} to its JSON representation.      * @return a JSON representation of this {@code JsonValue}      */
specifier|public
name|String
name|toJson
parameter_list|()
block|{
return|return
name|toJson
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * This class represents primitive JSON values (atoms). These are values of type      * {@link Type#STRING} {@link Type#NUMBER} {@link Type#BOOLEAN} and {@link Type#NULL}.      */
specifier|public
specifier|static
class|class
name|JsonAtom
extends|extends
name|JsonValue
block|{
specifier|public
specifier|static
specifier|final
name|JsonAtom
name|NULL
init|=
operator|new
name|JsonAtom
argument_list|(
literal|"null"
argument_list|,
name|Type
operator|.
name|NULL
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|JsonAtom
name|TRUE
init|=
operator|new
name|JsonAtom
argument_list|(
literal|"true"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|JsonAtom
name|FALSE
init|=
operator|new
name|JsonAtom
argument_list|(
literal|"false"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|public
name|JsonAtom
parameter_list|(
name|String
name|value
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
specifier|static
name|JsonAtom
name|string
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|JsonAtom
argument_list|(
name|value
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|JsonAtom
name|number
parameter_list|(
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
throw|;
block|}
return|return
operator|new
name|JsonAtom
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|JsonAtom
name|number
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|JsonAtom
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|JsonAtom
name|number
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
operator|new
name|JsonAtom
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
return|;
block|}
comment|/**          * Create a new {@code JsonAtom} from {@code token}.          * @param token          * @throws IllegalArgumentException  if {@code token} does not represent          * an primitive type (atom).          */
specifier|public
name|JsonAtom
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
argument_list|(
name|token
operator|.
name|text
argument_list|()
argument_list|,
name|valueType
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JsonAtom
name|asAtom
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
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
operator|+
literal|": "
operator|+
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|37
operator|*
operator|(
literal|37
operator|*
operator|(
literal|17
operator|+
name|value
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
operator|+
name|type
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|JsonAtom
condition|)
block|{
name|JsonAtom
name|that
init|=
operator|(
name|JsonAtom
operator|)
name|other
decl_stmt|;
return|return
name|that
operator|.
name|value
argument_list|()
operator|.
name|equals
argument_list|(
name|value
argument_list|()
argument_list|)
operator|&&
name|that
operator|.
name|type
argument_list|()
operator|==
name|type
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|//------------------------------------------< private>---
specifier|private
specifier|static
name|JsonValue
operator|.
name|Type
name|valueType
parameter_list|(
name|Token
operator|.
name|Type
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|TRUE
case|:
case|case
name|FALSE
case|:
return|return
name|JsonValue
operator|.
name|Type
operator|.
name|BOOLEAN
return|;
case|case
name|NULL
case|:
return|return
name|JsonValue
operator|.
name|Type
operator|.
name|NULL
return|;
case|case
name|STRING
case|:
return|return
name|JsonValue
operator|.
name|Type
operator|.
name|STRING
return|;
case|case
name|NUMBER
case|:
return|return
name|JsonValue
operator|.
name|Type
operator|.
name|NUMBER
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot map token type "
operator|+
name|type
operator|+
literal|" to value type"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * This class represents JSON arrays.      */
specifier|public
specifier|static
class|class
name|JsonArray
extends|extends
name|JsonValue
block|{
specifier|public
specifier|static
name|JsonArray
name|EMPTY
init|=
operator|new
name|JsonArray
argument_list|(
name|Collections
operator|.
expr|<
name|JsonValue
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|JsonValue
argument_list|>
name|values
decl_stmt|;
specifier|public
name|JsonArray
parameter_list|(
name|List
argument_list|<
name|JsonValue
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|JsonArray
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|JsonValue
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**          * Append {@code value} to the end of this array.          * @param value          */
specifier|public
name|void
name|add
parameter_list|(
name|JsonValue
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**          * Removes a value from this array          * @param value          * @return  {@code true} iff the array contains {@code value}          */
specifier|public
name|boolean
name|remove
parameter_list|(
name|JsonValue
name|value
parameter_list|)
block|{
return|return
name|values
operator|.
name|remove
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**          * @param index          * @return the {@code JsonValue} at {@code index}.          * @throws IndexOutOfBoundsException  if {@code index} is out of range          */
specifier|public
name|JsonValue
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsonArray
name|asArray
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|JsonValue
argument_list|>
name|value
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ARRAY
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
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
name|values
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|JsonArray
condition|)
block|{
name|JsonArray
name|that
init|=
operator|(
name|JsonArray
operator|)
name|other
decl_stmt|;
return|return
name|that
operator|.
name|value
argument_list|()
operator|.
name|equals
argument_list|(
name|value
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**      * This class represents JSON objects.      */
specifier|public
specifier|static
class|class
name|JsonObject
extends|extends
name|JsonValue
block|{
specifier|public
specifier|static
specifier|final
name|JsonObject
name|EMPTY
init|=
operator|new
name|JsonObject
argument_list|(
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|JsonValue
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
name|values
decl_stmt|;
specifier|public
name|JsonObject
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|JsonObject
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**          * Put {@code value} into this object          * @param key          * @param value          */
specifier|public
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|JsonValue
name|value
parameter_list|)
block|{
name|values
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**          * @param key          * @return  the {@code JsonValue} identified by {@code key} or {@code null}          * if no value exists for {@code key}.          */
specifier|public
name|JsonValue
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**          * Remove {@code key} from this object          * @param key          * @return  the {@code JsonValue} identified by {@code key} or {@code null}          * if no value exists for {@code key}.          */
specifier|public
name|JsonValue
name|remove
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|values
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|values
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsonObject
name|asObject
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|JsonValue
argument_list|>
name|value
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|OBJECT
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
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
name|values
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|JsonObject
condition|)
block|{
name|JsonObject
name|that
init|=
operator|(
name|JsonObject
operator|)
name|other
decl_stmt|;
return|return
name|that
operator|.
name|value
argument_list|()
operator|.
name|equals
argument_list|(
name|value
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|//------------------------------------------< private>---
specifier|private
specifier|static
name|String
name|toJson
parameter_list|(
name|JsonAtom
name|atom
parameter_list|)
block|{
return|return
name|atom
operator|.
name|type
argument_list|()
operator|==
name|Type
operator|.
name|STRING
condition|?
name|quote
argument_list|(
name|escape
argument_list|(
name|atom
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
else|:
name|atom
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|quote
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
literal|'\"'
operator|+
name|text
operator|+
literal|'\"'
return|;
block|}
block|}
end_class

end_unit

