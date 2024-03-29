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
name|spi
operator|.
name|query
operator|.
name|fulltext
package|;
end_package

begin_comment
comment|/**  * A pattern matcher.  */
end_comment

begin_class
specifier|public
class|class
name|LikePattern
block|{
comment|// TODO LIKE: optimize condition to '=' when no patterns are used, or 'between x and x+1'
comment|// TODO LIKE: what to do for invalid patterns (patterns ending with a backslash)
specifier|private
specifier|static
specifier|final
name|int
name|MATCH
init|=
literal|0
decl_stmt|,
name|ONE
init|=
literal|1
decl_stmt|,
name|ANY
init|=
literal|2
decl_stmt|;
specifier|private
name|String
name|patternString
decl_stmt|;
specifier|private
name|boolean
name|invalidPattern
decl_stmt|;
specifier|private
name|char
index|[]
name|patternChars
decl_stmt|;
specifier|private
name|int
index|[]
name|patternTypes
decl_stmt|;
specifier|private
name|int
name|patternLength
decl_stmt|;
specifier|private
name|String
name|lowerBounds
decl_stmt|,
name|upperBound
decl_stmt|;
specifier|public
name|LikePattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|initPattern
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|initBounds
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|!
name|invalidPattern
operator|&&
name|compareAt
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|,
name|patternChars
argument_list|,
name|patternTypes
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|compare
parameter_list|(
name|char
index|[]
name|pattern
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|pi
parameter_list|,
name|int
name|si
parameter_list|)
block|{
return|return
name|pattern
index|[
name|pi
index|]
operator|==
name|s
operator|.
name|charAt
argument_list|(
name|si
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|compareAt
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|pi
parameter_list|,
name|int
name|si
parameter_list|,
name|int
name|sLen
parameter_list|,
name|char
index|[]
name|pattern
parameter_list|,
name|int
index|[]
name|types
parameter_list|)
block|{
for|for
control|(
init|;
name|pi
operator|<
name|patternLength
condition|;
name|pi
operator|++
control|)
block|{
name|int
name|type
init|=
name|types
index|[
name|pi
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|MATCH
case|:
if|if
condition|(
name|si
operator|>=
name|sLen
operator|||
operator|!
name|compare
argument_list|(
name|pattern
argument_list|,
name|s
argument_list|,
name|pi
argument_list|,
name|si
operator|++
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|ONE
case|:
if|if
condition|(
name|si
operator|++
operator|>=
name|sLen
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|ANY
case|:
if|if
condition|(
operator|++
name|pi
operator|>=
name|patternLength
condition|)
block|{
return|return
literal|true
return|;
block|}
while|while
condition|(
name|si
operator|<
name|sLen
condition|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|pattern
argument_list|,
name|s
argument_list|,
name|pi
argument_list|,
name|si
argument_list|)
operator|&&
name|compareAt
argument_list|(
name|s
argument_list|,
name|pi
argument_list|,
name|si
argument_list|,
name|sLen
argument_list|,
name|pattern
argument_list|,
name|types
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|si
operator|++
expr_stmt|;
block|}
return|return
literal|false
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Internal error: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
return|return
name|si
operator|==
name|sLen
return|;
block|}
specifier|private
name|void
name|initPattern
parameter_list|(
name|String
name|p
parameter_list|)
block|{
name|patternLength
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|patternTypes
operator|=
literal|null
expr_stmt|;
name|patternChars
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|int
name|len
init|=
name|p
operator|.
name|length
argument_list|()
decl_stmt|;
name|patternChars
operator|=
operator|new
name|char
index|[
name|len
index|]
expr_stmt|;
name|patternTypes
operator|=
operator|new
name|int
index|[
name|len
index|]
expr_stmt|;
name|boolean
name|lastAny
init|=
literal|false
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
name|char
name|c
init|=
name|p
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|type
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
operator|>=
name|len
operator|-
literal|1
condition|)
block|{
name|invalidPattern
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|c
operator|=
name|p
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
name|type
operator|=
name|MATCH
expr_stmt|;
name|lastAny
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'%'
condition|)
block|{
if|if
condition|(
name|lastAny
condition|)
block|{
continue|continue;
block|}
name|type
operator|=
name|ANY
expr_stmt|;
name|lastAny
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|type
operator|=
name|ONE
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|MATCH
expr_stmt|;
name|lastAny
operator|=
literal|false
expr_stmt|;
block|}
name|patternTypes
index|[
name|patternLength
index|]
operator|=
name|type
expr_stmt|;
name|patternChars
index|[
name|patternLength
operator|++
index|]
operator|=
name|c
expr_stmt|;
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
name|patternLength
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|patternTypes
index|[
name|i
index|]
operator|==
name|ANY
operator|&&
name|patternTypes
index|[
name|i
operator|+
literal|1
index|]
operator|==
name|ONE
condition|)
block|{
name|patternTypes
index|[
name|i
index|]
operator|=
name|ONE
expr_stmt|;
name|patternTypes
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|ANY
expr_stmt|;
block|}
block|}
name|patternString
operator|=
operator|new
name|String
argument_list|(
name|patternChars
argument_list|,
literal|0
argument_list|,
name|patternLength
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
name|patternString
return|;
block|}
comment|/**      * Get the lower bound if any.      *      * @return return the lower bound, or null if unbound      */
specifier|public
name|String
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBounds
return|;
block|}
comment|/**      * Get the upper bound if any.      *      * @return return the upper bound, or null if unbound      */
specifier|public
name|String
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
specifier|private
name|void
name|initBounds
parameter_list|()
block|{
if|if
condition|(
name|invalidPattern
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|patternLength
operator|<=
literal|0
operator|||
name|patternTypes
index|[
literal|0
index|]
operator|!=
name|MATCH
condition|)
block|{
comment|// can't use an index
return|return;
block|}
name|int
name|maxMatch
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|maxMatch
operator|<
name|patternLength
operator|&&
name|patternTypes
index|[
name|maxMatch
index|]
operator|==
name|MATCH
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|patternChars
index|[
name|maxMatch
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|lower
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|lower
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|maxMatch
operator|==
name|patternLength
condition|)
block|{
name|lowerBounds
operator|=
name|upperBound
operator|=
name|lower
expr_stmt|;
return|return;
block|}
name|lowerBounds
operator|=
name|lower
expr_stmt|;
name|char
name|next
init|=
name|lower
operator|.
name|charAt
argument_list|(
name|lower
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// search the 'next' unicode character (or at least a character
comment|// that is higher)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|upper
init|=
name|lower
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lower
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
call|(
name|char
call|)
argument_list|(
name|next
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|upper
operator|.
name|compareTo
argument_list|(
name|lower
argument_list|)
operator|>
literal|0
condition|)
block|{
name|upperBound
operator|=
name|upper
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
end_class

end_unit

