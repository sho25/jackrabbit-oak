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
name|namepath
package|;
end_package

begin_class
specifier|public
class|class
name|JcrPathParser
block|{
comment|// constants for parser
specifier|private
specifier|static
specifier|final
name|int
name|STATE_PREFIX_START
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_PREFIX
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_NAME_START
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_NAME
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_INDEX
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_INDEX_END
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_DOT
init|=
literal|6
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_DOTDOT
init|=
literal|7
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_URI
init|=
literal|8
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STATE_URI_END
init|=
literal|9
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
name|EOF
init|=
operator|(
name|char
operator|)
operator|-
literal|1
decl_stmt|;
specifier|private
name|JcrPathParser
parameter_list|()
block|{     }
interface|interface
name|Listener
extends|extends
name|JcrNameParser
operator|.
name|Listener
block|{
name|boolean
name|root
parameter_list|()
function_decl|;
name|boolean
name|current
parameter_list|()
function_decl|;
name|boolean
name|parent
parameter_list|()
function_decl|;
block|}
specifier|public
specifier|static
name|void
name|parse
parameter_list|(
name|String
name|jcrPath
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
comment|// check for length
name|int
name|len
init|=
name|jcrPath
operator|==
literal|null
condition|?
literal|0
else|:
name|jcrPath
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// shortcut for root path
if|if
condition|(
name|len
operator|==
literal|1
operator|&&
name|jcrPath
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
name|listener
operator|.
name|root
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// short cut for empty path
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// check if absolute path
name|int
name|pos
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|jcrPath
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
operator|!
name|listener
operator|.
name|root
argument_list|()
condition|)
block|{
return|return;
block|}
name|pos
operator|++
expr_stmt|;
block|}
comment|// parse the path
name|int
name|state
init|=
name|STATE_PREFIX_START
decl_stmt|;
name|int
name|lastPos
init|=
name|pos
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
name|boolean
name|wasSlash
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|pos
operator|<=
name|len
condition|)
block|{
name|char
name|c
init|=
name|pos
operator|==
name|len
condition|?
name|EOF
else|:
name|jcrPath
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|++
expr_stmt|;
comment|// special check for whitespace
if|if
condition|(
name|c
operator|!=
literal|' '
operator|&&
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|c
operator|=
literal|'\t'
expr_stmt|;
block|}
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'/'
case|:
case|case
name|EOF
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
operator|&&
name|c
operator|!=
name|EOF
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. "
operator|+
literal|"double slash '//' not allowed."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX
operator|||
name|state
operator|==
name|STATE_NAME
operator|||
name|state
operator|==
name|STATE_INDEX_END
operator|||
name|state
operator|==
name|STATE_URI_END
condition|)
block|{
comment|// eof path element
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|wasSlash
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path: "
operator|+
literal|"Trailing slashes not allowed in prefixes and names."
argument_list|)
expr_stmt|;
return|return;
block|}
name|name
operator|=
name|jcrPath
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|JcrNameParser
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|listener
argument_list|,
name|index
argument_list|)
condition|)
block|{
return|return;
block|}
name|state
operator|=
name|STATE_PREFIX_START
expr_stmt|;
name|lastPos
operator|=
name|pos
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_DOT
condition|)
block|{
if|if
condition|(
operator|!
name|listener
operator|.
name|current
argument_list|()
condition|)
block|{
return|return;
block|}
name|lastPos
operator|=
name|pos
expr_stmt|;
name|state
operator|=
name|STATE_PREFIX_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_DOTDOT
condition|)
block|{
if|if
condition|(
operator|!
name|listener
operator|.
name|parent
argument_list|()
condition|)
block|{
return|return;
block|}
name|lastPos
operator|=
name|pos
expr_stmt|;
name|state
operator|=
name|STATE_PREFIX_START
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|!=
name|STATE_URI
operator|&&
operator|!
operator|(
name|state
operator|==
name|STATE_PREFIX_START
operator|&&
name|c
operator|==
name|EOF
operator|)
condition|)
block|{
comment|// ignore trailing slash
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not a valid name character."
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
literal|'.'
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|state
operator|=
name|STATE_DOT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_DOT
condition|)
block|{
name|state
operator|=
name|STATE_DOTDOT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_DOTDOT
condition|)
block|{
name|state
operator|=
name|STATE_PREFIX
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_INDEX_END
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not valid after index. '/' expected."
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
literal|':'
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. Prefix "
operator|+
literal|"must not be empty"
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX
condition|)
block|{
if|if
condition|(
name|wasSlash
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path: "
operator|+
literal|"Trailing slashes not allowed in prefixes and names."
argument_list|)
expr_stmt|;
return|return;
block|}
name|state
operator|=
name|STATE_NAME_START
expr_stmt|;
comment|// don't reset the lastPos/pos since prefix+name are passed together to the NameResolver
block|}
elseif|else
if|if
condition|(
name|state
operator|!=
name|STATE_URI
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not valid name character"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
literal|'['
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX
operator|||
name|state
operator|==
name|STATE_NAME
condition|)
block|{
if|if
condition|(
name|wasSlash
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path: "
operator|+
literal|"Trailing slashes not allowed in prefixes and names."
argument_list|)
expr_stmt|;
return|return;
block|}
name|state
operator|=
name|STATE_INDEX
expr_stmt|;
name|name
operator|=
name|jcrPath
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|pos
expr_stmt|;
block|}
break|break;
case|case
literal|']'
case|:
if|if
condition|(
name|state
operator|==
name|STATE_INDEX
condition|)
block|{
try|try
block|{
name|index
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|jcrPath
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|,
name|pos
operator|-
literal|1
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
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. "
operator|+
literal|"NumberFormatException in index: "
operator|+
name|jcrPath
operator|.
name|substring
argument_list|(
name|lastPos
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. "
operator|+
literal|"Index number invalid: "
operator|+
name|index
argument_list|)
expr_stmt|;
return|return;
block|}
name|state
operator|=
name|STATE_INDEX_END
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not a valid name character."
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
literal|' '
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
operator|||
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not valid name start"
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_INDEX_END
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not valid after index. '/' expected."
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_DOT
operator|||
name|state
operator|==
name|STATE_DOTDOT
condition|)
block|{
name|state
operator|=
name|STATE_PREFIX
expr_stmt|;
block|}
break|break;
case|case
literal|'\t'
case|:
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. "
operator|+
literal|"Whitespace not a allowed in name."
argument_list|)
expr_stmt|;
return|return;
case|case
literal|'*'
case|:
case|case
literal|'|'
case|:
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not a valid name character."
argument_list|)
expr_stmt|;
return|return;
case|case
literal|'{'
case|:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
operator|&&
name|lastPos
operator|==
name|pos
operator|-
literal|1
condition|)
block|{
comment|// '{' marks the start of a uri enclosed in an expanded name
comment|// instead of the usual namespace prefix, if it is
comment|// located at the beginning of a new segment.
name|state
operator|=
name|STATE_URI
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_NAME_START
operator|||
name|state
operator|==
name|STATE_DOT
operator|||
name|state
operator|==
name|STATE_DOTDOT
condition|)
block|{
comment|// otherwise it's part of the local name
name|state
operator|=
name|STATE_NAME
expr_stmt|;
block|}
break|break;
case|case
literal|'}'
case|:
if|if
condition|(
name|state
operator|==
name|STATE_URI
condition|)
block|{
name|state
operator|=
name|STATE_URI_END
expr_stmt|;
block|}
break|break;
default|default:
if|if
condition|(
name|state
operator|==
name|STATE_PREFIX_START
operator|||
name|state
operator|==
name|STATE_DOT
operator|||
name|state
operator|==
name|STATE_DOTDOT
condition|)
block|{
name|state
operator|=
name|STATE_PREFIX
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_NAME_START
condition|)
block|{
name|state
operator|=
name|STATE_NAME
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|STATE_INDEX_END
condition|)
block|{
name|listener
operator|.
name|error
argument_list|(
literal|'\''
operator|+
name|jcrPath
operator|+
literal|"' is not a valid path. '"
operator|+
name|c
operator|+
literal|"' not valid after index. '/' expected."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|wasSlash
operator|=
name|c
operator|==
literal|' '
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

