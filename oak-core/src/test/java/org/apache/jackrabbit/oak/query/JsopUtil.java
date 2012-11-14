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
name|query
package|;
end_package

begin_import
import|import static
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
operator|.
name|createProperty
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|mk
operator|.
name|json
operator|.
name|JsopTokenizer
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
name|Root
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
name|Tree
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
name|kernel
operator|.
name|TypeCodes
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
name|BooleanPropertyState
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
name|StringPropertyState
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
name|value
operator|.
name|Conversions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Utility class for working with jsop string diffs  *   */
end_comment

begin_class
specifier|public
class|class
name|JsopUtil
block|{
specifier|private
name|JsopUtil
parameter_list|()
block|{      }
comment|/**      * Applies the commit string to a given Root instance      *       *       * The commit string represents a sequence of operations, jsonp style:      *       *<p>      * / + "test": { "a": { "id": "ref:123" }, "b": { "id" : "str:123" }}      *<p>      * or      *<p>      * "/ - "test"      *</p>      *       * @param root      * @param commit the commit string      * @throws UnsupportedOperationException if the operation is not supported      */
specifier|public
specifier|static
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|,
name|String
name|commit
parameter_list|)
throws|throws
name|UnsupportedOperationException
block|{
name|int
name|index
init|=
name|commit
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|commit
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|Tree
name|c
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
comment|// TODO create intermediary?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Non existing path "
operator|+
name|path
argument_list|)
throw|;
block|}
name|commit
operator|=
name|commit
operator|.
name|substring
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|JsopTokenizer
name|tokenizer
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|commit
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'-'
argument_list|)
condition|)
block|{
name|removeTree
argument_list|(
name|c
argument_list|,
name|tokenizer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'+'
argument_list|)
condition|)
block|{
name|addTree
argument_list|(
name|c
argument_list|,
name|tokenizer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unsupported "
operator|+
operator|(
name|char
operator|)
name|tokenizer
operator|.
name|read
argument_list|()
operator|+
literal|". This should be either '+' or '-'."
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|removeTree
parameter_list|(
name|Tree
name|t
parameter_list|,
name|JsopTokenizer
name|tokenizer
parameter_list|)
block|{
name|String
name|path
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|t
operator|.
name|hasChild
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return;
block|}
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addTree
parameter_list|(
name|Tree
name|t
parameter_list|,
name|JsopTokenizer
name|tokenizer
parameter_list|)
block|{
do|do
block|{
name|String
name|key
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|Tree
name|c
init|=
name|t
operator|.
name|addChild
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
name|addTree
argument_list|(
name|c
argument_list|,
name|tokenizer
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
name|t
operator|.
name|setProperty
argument_list|(
name|readArrayProperty
argument_list|(
name|key
argument_list|,
name|tokenizer
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|setProperty
argument_list|(
name|readProperty
argument_list|(
name|key
argument_list|,
name|tokenizer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
block|}
comment|/**      * Read a {@code PropertyState} from a {@link JsopReader}      * @param name  The name of the property state      * @param reader  The reader      * @return new property state      */
specifier|private
specifier|static
name|PropertyState
name|readProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|JsopReader
name|reader
parameter_list|)
block|{
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
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|number
argument_list|,
name|PropertyType
operator|.
name|LONG
argument_list|)
return|;
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
return|return
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
return|;
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
return|return
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
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
name|int
name|split
init|=
name|TypeCodes
operator|.
name|split
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|type
init|=
name|TypeCodes
operator|.
name|decodeType
argument_list|(
name|split
argument_list|,
name|jsonString
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|TypeCodes
operator|.
name|decodeName
argument_list|(
name|split
argument_list|,
name|jsonString
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
name|name
argument_list|,
name|jsonString
argument_list|)
return|;
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
block|}
comment|/**      * Read a multi valued {@code PropertyState} from a {@link JsopReader}      * @param name  The name of the property state      * @param reader  The reader      * @return new property state      */
specifier|private
specifier|static
name|PropertyState
name|readArrayProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|JsopReader
name|reader
parameter_list|)
block|{
name|int
name|type
init|=
name|PropertyType
operator|.
name|STRING
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
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
name|type
operator|=
name|PropertyType
operator|.
name|LONG
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|number
argument_list|)
operator|.
name|toLong
argument_list|()
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
name|type
operator|=
name|PropertyType
operator|.
name|BOOLEAN
expr_stmt|;
name|values
operator|.
name|add
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
name|type
operator|=
name|PropertyType
operator|.
name|BOOLEAN
expr_stmt|;
name|values
operator|.
name|add
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
name|int
name|split
init|=
name|TypeCodes
operator|.
name|split
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|!=
operator|-
literal|1
condition|)
block|{
name|type
operator|=
name|TypeCodes
operator|.
name|decodeType
argument_list|(
name|split
argument_list|,
name|jsonString
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|TypeCodes
operator|.
name|decodeName
argument_list|(
name|split
argument_list|,
name|jsonString
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|DOUBLE
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|DECIMAL
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDecimal
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|type
operator|=
name|PropertyType
operator|.
name|STRING
expr_stmt|;
name|values
operator|.
name|add
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
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

