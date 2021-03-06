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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Deque
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
name|PropertyState
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
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyPropertyState
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|spi
operator|.
name|query
operator|.
name|QueryConstants
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A parser for function-based indexes. It converts the human-readable function  * definition (XPath) to the internal Polish notation.  */
end_comment

begin_class
specifier|public
class|class
name|FunctionIndexProcessor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FunctionIndexProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|remaining
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|PropertyState
name|EMPTY_PROPERTY_STATE
init|=
name|EmptyPropertyState
operator|.
name|emptyProperty
argument_list|(
literal|"empty"
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
decl_stmt|;
specifier|protected
name|FunctionIndexProcessor
parameter_list|(
name|String
name|function
parameter_list|)
block|{
name|this
operator|.
name|remaining
operator|=
name|function
expr_stmt|;
block|}
comment|/**      * Get the list of properties used in the given function code.      *       * @param functionCode the tokens, for example ["function", "lower", "@name"]      * @return the list of properties, for example ["name"]      */
specifier|public
specifier|static
name|String
index|[]
name|getProperties
parameter_list|(
name|String
index|[]
name|functionCode
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|token
range|:
name|functionCode
control|)
block|{
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|String
name|propertyName
init|=
name|token
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|properties
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**      * Try to calculate the value for the given function code.      *       * @param path the path of the node      * @param state the node state      * @param functionCode the tokens, for example ["function", "lower", "@name"]      * @return null, or the calculated value      */
specifier|public
specifier|static
name|PropertyState
name|tryCalculateValue
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|String
index|[]
name|functionCode
parameter_list|)
block|{
name|Deque
argument_list|<
name|PropertyState
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|functionCode
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|token
init|=
name|functionCode
index|[
name|i
index|]
decl_stmt|;
name|PropertyState
name|ps
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|String
name|propertyName
init|=
name|token
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ps
operator|=
name|getProperty
argument_list|(
name|path
argument_list|,
name|state
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ps
operator|=
name|calculateFunction
argument_list|(
name|token
argument_list|,
name|stack
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
name|ps
operator|=
name|EMPTY_PROPERTY_STATE
expr_stmt|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|ret
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
return|return
name|ret
operator|==
name|EMPTY_PROPERTY_STATE
condition|?
literal|null
else|:
name|ret
return|;
block|}
comment|/**      * Split the polish notation into a tokens that can more easily be processed.      *        *  @param functionDescription in polish notation, for example "function*lower*{@literal @}name"      *  @return tokens, for example ["function", "lower", "{@literal @}name"]      */
specifier|public
specifier|static
name|String
index|[]
name|getFunctionCode
parameter_list|(
name|String
name|functionDescription
parameter_list|)
block|{
if|if
condition|(
name|functionDescription
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|functionDescription
operator|.
name|split
argument_list|(
literal|"\\*"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|PropertyState
name|calculateFunction
parameter_list|(
name|String
name|functionName
parameter_list|,
name|Deque
argument_list|<
name|PropertyState
argument_list|>
name|stack
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"coalesce"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
comment|// coalesce (a, b) => (a != null ? a : b)
comment|// we pop stack again to consume the second parameter
comment|// also, if ps is EMPTY_PROPERTY_STATE, then newly popped value is to be used
name|PropertyState
name|ps2
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|ps
operator|==
name|EMPTY_PROPERTY_STATE
condition|)
block|{
name|ps
operator|=
name|ps2
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ps
operator|==
name|EMPTY_PROPERTY_STATE
condition|)
block|{
return|return
name|ps
return|;
block|}
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ps
operator|.
name|count
argument_list|()
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
name|ps
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Object
name|x
decl_stmt|;
if|if
condition|(
literal|"lower"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|x
operator|=
name|s
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|STRING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"coalesce"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|x
operator|=
name|s
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|STRING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"upper"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|x
operator|=
name|s
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|STRING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"length"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|x
operator|=
operator|(
name|long
operator|)
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|LONG
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unknown function {}"
argument_list|,
name|functionName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|values
operator|.
name|add
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|result
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|result
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"value"
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|type
operator|.
name|getArrayType
argument_list|()
expr_stmt|;
name|result
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"value"
argument_list|,
name|values
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|propertyName
argument_list|)
operator|!=
literal|1
condition|)
block|{
for|for
control|(
name|String
name|n
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|propertyName
argument_list|)
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
name|propertyName
operator|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|ps
decl_stmt|;
if|if
condition|(
literal|":localname"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"value"
argument_list|,
name|getLocalName
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|":name"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"value"
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ps
operator|=
name|state
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ps
operator|==
literal|null
operator|||
name|ps
operator|.
name|count
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ps
return|;
block|}
specifier|private
specifier|static
name|String
name|getLocalName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
comment|// TODO LOCALNAME: evaluation of local name might not be correct
return|return
name|colon
operator|<
literal|0
condition|?
name|name
else|:
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/**      * Convert a function (in human-readable form) to the polish notation.      *       * @param function the function, for example "lower([name])"      * @return the polish notation, for example "function*lower*{@literal @}name"      */
specifier|public
specifier|static
name|String
name|convertToPolishNotation
parameter_list|(
name|String
name|function
parameter_list|)
block|{
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FunctionIndexProcessor
name|p
init|=
operator|new
name|FunctionIndexProcessor
argument_list|(
name|function
argument_list|)
decl_stmt|;
return|return
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|p
operator|.
name|parse
argument_list|()
return|;
block|}
name|String
name|parse
parameter_list|()
block|{
if|if
condition|(
name|match
argument_list|(
literal|"fn:local-name()"
argument_list|)
operator|||
name|match
argument_list|(
literal|"localname()"
argument_list|)
condition|)
block|{
return|return
literal|"@:localname"
return|;
block|}
if|if
condition|(
name|match
argument_list|(
literal|"fn:name()"
argument_list|)
operator|||
name|match
argument_list|(
literal|"name()"
argument_list|)
condition|)
block|{
return|return
literal|"@:name"
return|;
block|}
if|if
condition|(
name|match
argument_list|(
literal|"fn:upper-case("
argument_list|)
operator|||
name|match
argument_list|(
literal|"upper("
argument_list|)
condition|)
block|{
return|return
literal|"upper*"
operator|+
name|parse
argument_list|()
operator|+
name|read
argument_list|(
literal|")"
argument_list|)
return|;
block|}
if|if
condition|(
name|match
argument_list|(
literal|"fn:lower-case("
argument_list|)
operator|||
name|match
argument_list|(
literal|"lower("
argument_list|)
condition|)
block|{
return|return
literal|"lower*"
operator|+
name|parse
argument_list|()
operator|+
name|read
argument_list|(
literal|")"
argument_list|)
return|;
block|}
if|if
condition|(
name|match
argument_list|(
literal|"fn:coalesce("
argument_list|)
operator|||
name|match
argument_list|(
literal|"coalesce("
argument_list|)
condition|)
block|{
return|return
literal|"coalesce*"
operator|+
name|parse
argument_list|()
operator|+
name|readCommaAndWhitespace
argument_list|()
operator|+
name|parse
argument_list|()
operator|+
name|read
argument_list|(
literal|")"
argument_list|)
return|;
block|}
if|if
condition|(
name|match
argument_list|(
literal|"fn:string-length("
argument_list|)
operator|||
name|match
argument_list|(
literal|"length("
argument_list|)
condition|)
block|{
return|return
literal|"length*"
operator|+
name|parse
argument_list|()
operator|+
name|read
argument_list|(
literal|")"
argument_list|)
return|;
block|}
comment|// property name
if|if
condition|(
name|match
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
name|String
name|prop
init|=
name|remaining
decl_stmt|;
name|int
name|indexOfComma
init|=
name|remaining
operator|.
name|indexOf
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfComma
operator|>
literal|0
condition|)
block|{
name|prop
operator|=
name|remaining
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexOfComma
argument_list|)
expr_stmt|;
block|}
name|prop
operator|=
name|prop
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prop
operator|.
name|lastIndexOf
argument_list|(
literal|']'
argument_list|)
argument_list|)
expr_stmt|;
name|remaining
operator|=
name|remaining
operator|.
name|substring
argument_list|(
name|prop
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|property
argument_list|(
name|prop
operator|.
name|replaceAll
argument_list|(
literal|"]]"
argument_list|,
literal|"]"
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|prop
init|=
name|remaining
decl_stmt|;
name|int
name|paren
init|=
name|remaining
operator|.
name|indexOf
argument_list|(
literal|')'
argument_list|)
decl_stmt|;
name|int
name|comma
init|=
name|remaining
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|comma
decl_stmt|;
if|if
condition|(
name|paren
operator|>=
literal|0
condition|)
block|{
name|end
operator|=
operator|(
name|end
operator|<
literal|0
operator|)
condition|?
name|paren
else|:
name|Math
operator|.
name|min
argument_list|(
name|end
argument_list|,
name|paren
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|>=
literal|0
condition|)
block|{
name|prop
operator|=
name|remaining
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
name|remaining
operator|=
name|remaining
operator|.
name|substring
argument_list|(
name|prop
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|property
argument_list|(
name|prop
operator|.
name|replaceAll
argument_list|(
literal|"@"
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
block|}
name|String
name|property
parameter_list|(
name|String
name|p
parameter_list|)
block|{
return|return
literal|"@"
operator|+
name|p
return|;
block|}
specifier|private
name|String
name|read
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|match
argument_list|(
name|string
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
specifier|private
name|String
name|readCommaAndWhitespace
parameter_list|()
block|{
while|while
condition|(
name|match
argument_list|(
literal|" "
argument_list|)
condition|)
block|{         }
name|match
argument_list|(
literal|","
argument_list|)
expr_stmt|;
while|while
condition|(
name|match
argument_list|(
literal|" "
argument_list|)
condition|)
block|{         }
return|return
literal|"*"
return|;
block|}
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|remaining
operator|.
name|startsWith
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|remaining
operator|=
name|remaining
operator|.
name|substring
argument_list|(
name|string
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

