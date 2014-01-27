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
name|plugins
operator|.
name|mongomk
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|kernel
operator|.
name|StringCache
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
name|AbstractPropertyState
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

begin_comment
comment|/**  * PropertyState implementation with lazy parsing of the JSOP encoded value.  */
end_comment

begin_class
specifier|final
class|class
name|MongoPropertyState
implements|implements
name|PropertyState
block|{
specifier|private
specifier|final
name|MongoNodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
name|PropertyState
name|parsed
init|=
literal|null
decl_stmt|;
name|MongoPropertyState
parameter_list|(
name|MongoNodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|parsed
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|parsed
argument_list|()
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|parsed
argument_list|()
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|parsed
argument_list|()
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|parsed
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|parsed
argument_list|()
operator|.
name|size
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|parsed
argument_list|()
operator|.
name|count
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|MongoPropertyState
condition|)
block|{
name|MongoPropertyState
name|other
init|=
operator|(
name|MongoPropertyState
operator|)
name|object
decl_stmt|;
return|return
name|this
operator|.
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
name|this
operator|.
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|value
argument_list|)
return|;
block|}
comment|// fall back to default equality check in AbstractPropertyState
return|return
name|object
operator|instanceof
name|PropertyState
operator|&&
name|AbstractPropertyState
operator|.
name|equal
argument_list|(
name|parsed
argument_list|()
argument_list|,
operator|(
name|PropertyState
operator|)
name|object
argument_list|)
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
name|AbstractPropertyState
operator|.
name|hashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|AbstractPropertyState
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
name|PropertyState
name|parsed
parameter_list|()
block|{
if|if
condition|(
name|parsed
operator|==
literal|null
condition|)
block|{
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|value
argument_list|)
decl_stmt|;
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
name|parsed
operator|=
name|readArrayProperty
argument_list|(
name|name
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parsed
operator|=
name|readProperty
argument_list|(
name|name
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parsed
return|;
block|}
comment|/**      * FIXME: copied from KernelNodeState.      *      * Read a {@code PropertyState} from a {@link JsopReader}      * @param name  The name of the property state      * @param reader  The reader      * @return new property state      */
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
return|return
name|readProperty
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|reader
argument_list|)
return|;
block|}
comment|/**      * FIXME: copied from KernelNodeState.      *      * Read a {@code PropertyState} from a {@link JsopReader}.      *      * @param name the name of the property state      * @param store the store      * @param reader the reader      * @return new property state      */
specifier|static
name|PropertyState
name|readProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|MongoNodeStore
name|store
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
name|store
operator|.
name|getBlob
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|StringCache
operator|.
name|get
argument_list|(
name|value
argument_list|)
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
name|StringCache
operator|.
name|get
argument_list|(
name|jsonString
argument_list|)
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
comment|/**      * FIXME: copied from KernelNodeState.      *      * Read a multi valued {@code PropertyState} from a {@link JsopReader}.      *      * @param name the name of the property state      * @param reader the reader      * @return new property state      */
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
return|return
name|readArrayProperty
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|reader
argument_list|)
return|;
block|}
comment|/**      * FIXME: copied from KernelNodeState.      *      * Read a multi valued {@code PropertyState} from a {@link JsopReader}.      *      * @param name the name of the property state      * @param store the store      * @param reader the reader      * @return new property state      */
specifier|static
name|PropertyState
name|readArrayProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|MongoNodeStore
name|store
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
name|store
operator|.
name|getBlob
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
name|values
operator|.
name|add
argument_list|(
name|StringCache
operator|.
name|get
argument_list|(
name|value
argument_list|)
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
name|StringCache
operator|.
name|get
argument_list|(
name|jsonString
argument_list|)
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

