begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|json
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

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
name|api
operator|.
name|Type
operator|.
name|BINARY
import|;
end_import

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
name|api
operator|.
name|Type
operator|.
name|BOOLEAN
import|;
end_import

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
name|api
operator|.
name|Type
operator|.
name|DOUBLE
import|;
end_import

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
name|api
operator|.
name|Type
operator|.
name|LONG
import|;
end_import

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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|regex
operator|.
name|Pattern
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
name|oak
operator|.
name|commons
operator|.
name|json
operator|.
name|JsopBuilder
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
name|Blob
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
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

begin_comment
comment|/**  * Utility class for serializing node and property states to JSON.  */
end_comment

begin_class
specifier|public
class|class
name|JsonSerializer
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FILTER_EXPRESSION
init|=
literal|"{\"properties\":[\"*\", \"-:childNodeCount\"]}"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|JsonFilter
name|DEFAULT_FILTER
init|=
operator|new
name|JsonFilter
argument_list|(
name|DEFAULT_FILTER_EXPRESSION
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|JsopBuilder
name|json
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxChildNodes
decl_stmt|;
specifier|private
specifier|final
name|JsonFilter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|BlobSerializer
name|blobs
decl_stmt|;
specifier|private
name|JsonSerializer
parameter_list|(
name|JsopBuilder
name|json
parameter_list|,
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|JsonFilter
name|filter
parameter_list|,
name|BlobSerializer
name|blobs
parameter_list|)
block|{
name|this
operator|.
name|json
operator|=
name|checkNotNull
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|maxChildNodes
operator|=
name|maxChildNodes
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|checkNotNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobs
operator|=
name|checkNotNull
argument_list|(
name|blobs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JsonSerializer
parameter_list|(
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|String
name|filter
parameter_list|,
name|BlobSerializer
name|blobs
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|JsopBuilder
argument_list|()
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|maxChildNodes
argument_list|,
operator|new
name|JsonFilter
argument_list|(
name|filter
argument_list|)
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JsonSerializer
parameter_list|(
name|JsopBuilder
name|json
parameter_list|,
name|BlobSerializer
name|blobs
parameter_list|)
block|{
name|this
argument_list|(
name|json
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|DEFAULT_FILTER
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|JsonSerializer
name|getChildSerializer
parameter_list|()
block|{
return|return
operator|new
name|JsonSerializer
argument_list|(
name|json
argument_list|,
name|depth
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
name|maxChildNodes
argument_list|,
name|filter
argument_list|,
name|blobs
argument_list|)
return|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
name|json
operator|.
name|object
argument_list|()
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|includeProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|json
operator|.
name|key
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|serialize
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|filter
operator|.
name|includeProperty
argument_list|(
literal|":childNodeCount"
argument_list|)
condition|)
block|{
name|json
operator|.
name|key
argument_list|(
literal|":childNodeCount"
argument_list|)
expr_stmt|;
name|json
operator|.
name|value
argument_list|(
name|node
operator|.
name|getChildNodeCount
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|child
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|includeNode
argument_list|(
name|name
argument_list|)
operator|&&
name|index
operator|++
operator|>=
name|offset
condition|)
block|{
if|if
condition|(
name|count
operator|++
operator|>=
name|maxChildNodes
condition|)
block|{
break|break;
block|}
name|json
operator|.
name|key
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|depth
operator|>
literal|0
condition|)
block|{
name|getChildSerializer
argument_list|()
operator|.
name|serialize
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|json
operator|.
name|object
argument_list|()
expr_stmt|;
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|serialize
argument_list|(
name|property
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|type
operator|.
name|getBaseType
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|property
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|==
name|STRING
operator|||
name|count
operator|>
literal|0
condition|)
block|{
name|json
operator|.
name|array
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|serialize
argument_list|(
name|property
argument_list|,
name|base
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// type-safe encoding of an empty array
name|json
operator|.
name|value
argument_list|(
name|TypeCodes
operator|.
name|EMPTY_ARRAY
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|BOOLEAN
condition|)
block|{
name|json
operator|.
name|value
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|,
name|index
argument_list|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|LONG
condition|)
block|{
name|json
operator|.
name|value
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|,
name|index
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DOUBLE
condition|)
block|{
name|Double
name|value
init|=
name|property
operator|.
name|getValue
argument_list|(
name|DOUBLE
argument_list|,
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|isNaN
argument_list|()
operator|||
name|value
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
name|json
operator|.
name|value
argument_list|(
name|TypeCodes
operator|.
name|encode
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|json
operator|.
name|encodedValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|Blob
name|blob
init|=
name|property
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|json
operator|.
name|value
argument_list|(
name|TypeCodes
operator|.
name|encode
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|,
name|blobs
operator|.
name|serialize
argument_list|(
name|blob
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|value
init|=
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|,
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|STRING
operator|||
name|TypeCodes
operator|.
name|split
argument_list|(
name|value
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|value
operator|=
name|TypeCodes
operator|.
name|encode
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|json
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Utility class for deciding which nodes and properties to serialize.      */
specifier|private
specifier|static
class|class
name|JsonFilter
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|EVERYTHING
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|nodeIncludes
init|=
name|newArrayList
argument_list|(
name|EVERYTHING
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|nodeExcludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|propertyIncludes
init|=
name|newArrayList
argument_list|(
name|EVERYTHING
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pattern
argument_list|>
name|propertyExcludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|JsonFilter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
name|JsopTokenizer
name|tokenizer
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
for|for
control|(
name|boolean
name|first
init|=
literal|true
init|;
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|;
name|first
operator|=
literal|false
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
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
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|readPatterns
argument_list|(
name|tokenizer
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"nodes"
argument_list|)
condition|)
block|{
name|nodeIncludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeIncludes
operator|.
name|addAll
argument_list|(
name|includes
argument_list|)
expr_stmt|;
name|nodeExcludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodeExcludes
operator|.
name|addAll
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"properties"
argument_list|)
condition|)
block|{
name|propertyIncludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|propertyIncludes
operator|.
name|addAll
argument_list|(
name|includes
argument_list|)
expr_stmt|;
name|propertyExcludes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|propertyExcludes
operator|.
name|addAll
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|key
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|readPatterns
parameter_list|(
name|JsopTokenizer
name|tokenizer
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
parameter_list|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
for|for
control|(
name|boolean
name|first
init|=
literal|true
init|;
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|;
name|first
operator|=
literal|false
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|String
name|pattern
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|excludes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"\\-"
argument_list|)
condition|)
block|{
name|includes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|includes
operator|.
name|add
argument_list|(
name|glob
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|Pattern
name|glob
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|star
init|=
name|pattern
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
decl_stmt|;
while|while
condition|(
name|star
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|star
operator|>
literal|0
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|star
operator|-
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|star
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|star
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|".*"
argument_list|)
expr_stmt|;
block|}
name|pattern
operator|=
name|pattern
operator|.
name|substring
argument_list|(
name|star
operator|+
literal|1
argument_list|)
expr_stmt|;
name|star
operator|=
name|pattern
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
name|boolean
name|includeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|name
argument_list|,
name|nodeIncludes
argument_list|,
name|nodeExcludes
argument_list|)
return|;
block|}
name|boolean
name|includeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|name
argument_list|,
name|propertyIncludes
argument_list|,
name|propertyExcludes
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|include
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|includes
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|excludes
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|include
range|:
name|includes
control|)
block|{
if|if
condition|(
name|include
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
for|for
control|(
name|Pattern
name|exclude
range|:
name|excludes
control|)
block|{
if|if
condition|(
name|exclude
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

