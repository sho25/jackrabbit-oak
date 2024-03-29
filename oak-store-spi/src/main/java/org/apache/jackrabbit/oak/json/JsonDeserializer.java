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
name|json
package|;
end_package

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
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|CharMatcher
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
name|plugins
operator|.
name|memory
operator|.
name|BinaryPropertyState
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
name|DoublePropertyState
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
name|LongPropertyState
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
name|plugins
operator|.
name|value
operator|.
name|Conversions
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
name|NodeBuilder
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_class
specifier|public
class|class
name|JsonDeserializer
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OAK_CHILD_ORDER
init|=
literal|":childOrder"
decl_stmt|;
specifier|private
specifier|final
name|BlobDeserializer
name|blobHandler
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|DeserializationSupport
name|deserializationSupport
decl_stmt|;
specifier|private
name|JsonDeserializer
parameter_list|(
name|BlobDeserializer
name|blobHandler
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|DeserializationSupport
name|support
parameter_list|)
block|{
name|this
operator|.
name|blobHandler
operator|=
name|blobHandler
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|deserializationSupport
operator|=
name|support
expr_stmt|;
block|}
specifier|public
name|JsonDeserializer
parameter_list|(
name|BlobDeserializer
name|blobHandler
parameter_list|)
block|{
name|this
argument_list|(
name|blobHandler
argument_list|,
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|,
name|DeserializationSupport
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JsonDeserializer
parameter_list|(
name|BlobDeserializer
name|blobHandler
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
argument_list|(
name|blobHandler
argument_list|,
name|builder
argument_list|,
name|DeserializationSupport
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeState
name|deserialize
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|NodeState
name|state
init|=
name|deserialize
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
specifier|public
name|NodeState
name|deserialize
parameter_list|(
name|JsopReader
name|reader
parameter_list|)
block|{
name|readNode
argument_list|(
name|reader
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|void
name|readNode
parameter_list|(
name|JsopReader
name|reader
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|String
name|key
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|childNames
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|readNode
argument_list|(
name|reader
argument_list|,
name|builder
operator|.
name|child
argument_list|(
name|key
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
literal|'['
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|readArrayProperty
argument_list|(
name|key
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|readProperty
argument_list|(
name|key
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deserializationSupport
operator|.
name|hasOrderableChildren
argument_list|(
name|builder
argument_list|)
operator|&&
operator|!
name|builder
operator|.
name|hasProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|childNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Read a {@code PropertyState} from a {@link JsopReader}      * @param name  The name of the property state      * @param reader  The reader      * @return new property state      */
specifier|private
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
try|try
block|{
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|number
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
operator|new
name|DoublePropertyState
argument_list|(
name|name
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|number
argument_list|)
argument_list|)
return|;
block|}
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
name|Type
name|inferredType
init|=
name|deserializationSupport
operator|.
name|inferPropertyType
argument_list|(
name|name
argument_list|,
name|jsonString
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonString
operator|.
name|startsWith
argument_list|(
name|TypeCodes
operator|.
name|EMPTY_ARRAY
argument_list|)
condition|)
block|{
name|int
name|type
init|=
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|jsonString
operator|.
name|substring
argument_list|(
name|TypeCodes
operator|.
name|EMPTY_ARRAY
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|emptyList
argument_list|()
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
return|return
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
name|name
argument_list|,
name|blobHandler
operator|.
name|deserialize
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|//It can happen that a value like oak:Unstructured is also interpreted
comment|//as type code. So if oakType is not undefined then use raw value
comment|//Also default to STRING in case of UNDEFINED
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
name|Type
name|oakType
init|=
name|inferredType
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|?
name|inferredType
else|:
name|Type
operator|.
name|STRING
decl_stmt|;
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|jsonString
argument_list|,
name|oakType
argument_list|)
return|;
block|}
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
name|Type
name|oakType
init|=
name|inferredType
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|?
name|inferredType
else|:
name|Type
operator|.
name|STRING
decl_stmt|;
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|jsonString
argument_list|,
name|oakType
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
try|try
block|{
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
name|Long
operator|.
name|parseLong
argument_list|(
name|number
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
name|type
operator|=
name|PropertyType
operator|.
name|DOUBLE
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|Type
name|inferredType
init|=
name|deserializationSupport
operator|.
name|inferPropertyType
argument_list|(
name|name
argument_list|,
name|jsonString
argument_list|)
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
name|values
operator|.
name|add
argument_list|(
name|blobHandler
operator|.
name|deserialize
argument_list|(
name|value
argument_list|)
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
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
comment|//If determine type is undefined then check if inferred type is defined
comment|//else default to STRING
name|type
operator|=
name|inferredType
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|?
name|inferredType
operator|.
name|tag
argument_list|()
else|:
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
block|}
else|else
block|{
name|type
operator|=
name|inferredType
operator|!=
name|Type
operator|.
name|UNDEFINED
condition|?
name|inferredType
operator|.
name|tag
argument_list|()
else|:
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
comment|/**      * Provides support for inferring types for some common property name and types      */
specifier|private
specifier|static
class|class
name|DeserializationSupport
block|{
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|NAME_PROPS
init|=
name|of
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ORDERABLE_TYPES
init|=
name|of
argument_list|(
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|DeserializationSupport
name|INSTANCE
init|=
operator|new
name|DeserializationSupport
argument_list|()
decl_stmt|;
name|Type
name|inferPropertyType
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|jsonString
parameter_list|)
block|{
if|if
condition|(
name|NAME_PROPS
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
operator|&&
name|hasSingleColon
argument_list|(
name|jsonString
argument_list|)
condition|)
block|{
return|return
name|Type
operator|.
name|NAME
return|;
block|}
return|return
name|Type
operator|.
name|UNDEFINED
return|;
block|}
name|boolean
name|hasOrderableChildren
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|PropertyState
name|primaryType
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
return|return
name|primaryType
operator|!=
literal|null
operator|&&
name|ORDERABLE_TYPES
operator|.
name|contains
argument_list|(
name|primaryType
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|hasSingleColon
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
comment|//In case the primaryType was encoded then it would be like nam:oak:Unstructured
comment|//So check if there is only one occurrence of ';'
return|return
name|CharMatcher
operator|.
name|is
argument_list|(
literal|':'
argument_list|)
operator|.
name|countIn
argument_list|(
name|jsonString
argument_list|)
operator|==
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

