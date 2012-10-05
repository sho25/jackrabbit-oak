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
name|memory
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
name|CoreValue
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
name|DATE
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
name|DATES
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
name|NAME
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
name|NAMES
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
name|PATH
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
name|PATHS
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
name|REFERENCE
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
name|REFERENCES
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
name|STRINGS
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
name|URI
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
name|URIS
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
name|WEAKREFERENCE
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
name|WEAKREFERENCES
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|PropertyStates
block|{
specifier|private
name|PropertyStates
parameter_list|()
block|{}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
block|{
name|int
name|type
init|=
name|value
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|PropertyType
operator|.
name|BINARY
operator|==
name|type
condition|)
block|{
return|return
name|binaryProperty
argument_list|(
name|name
argument_list|,
operator|new
name|ValueBasedBlob
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
name|value
operator|.
name|getString
argument_list|()
argument_list|,
name|value
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|emptyProperty
argument_list|(
name|name
argument_list|,
name|STRINGS
argument_list|)
return|;
block|}
name|int
name|type
init|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|stringProperty
argument_list|(
name|name
argument_list|,
name|strings
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|blobs
operator|.
name|add
argument_list|(
operator|new
name|ValueBasedBlob
argument_list|(
name|cv
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|binaryPropertyFromBlob
argument_list|(
name|name
argument_list|,
name|blobs
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
name|List
argument_list|<
name|Long
argument_list|>
name|longs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|longs
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|longProperty
argument_list|(
name|name
argument_list|,
name|longs
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
name|List
argument_list|<
name|Double
argument_list|>
name|doubles
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|doubles
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|doubleProperty
argument_list|(
name|name
argument_list|,
name|doubles
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
name|List
argument_list|<
name|Boolean
argument_list|>
name|booleans
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|booleans
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|booleanProperty
argument_list|(
name|name
argument_list|,
name|booleans
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|decimals
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|decimals
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getDecimal
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|decimalProperty
argument_list|(
name|name
argument_list|,
name|decimals
argument_list|)
return|;
default|default:
name|List
argument_list|<
name|String
argument_list|>
name|vals
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|cv
range|:
name|values
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|cv
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|vals
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
specifier|public
specifier|static
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
operator|new
name|StringPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|new
name|BinaryPropertyState
argument_list|(
name|name
argument_list|,
operator|new
name|StringBasedBlob
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|SinglePropertyState
operator|.
name|getLong
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoublePropertyState
argument_list|(
name|name
argument_list|,
name|StringPropertyState
operator|.
name|getDouble
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
operator|new
name|BooleanPropertyState
argument_list|(
name|name
argument_list|,
name|StringPropertyState
operator|.
name|getBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalPropertyState
argument_list|(
name|name
argument_list|,
name|StringPropertyState
operator|.
name|getDecimal
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
default|default:
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|type
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|tag
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|stringProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|stringProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|binaryPropertyFromBlob
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Blob
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|binaryProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Blob
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|longProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Long
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|longProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|doubleProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Double
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|doubleProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Double
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|dateProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|dateProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|booleanProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Boolean
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|booleanProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Boolean
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|nameProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|nameProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|pathProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|pathProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|referenceProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|referenceProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|weakreferenceProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|weakreferenceProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|uriProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|uriProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|decimalProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
operator|)
name|value
argument_list|)
else|:
name|decimalProperty
argument_list|(
name|name
argument_list|,
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|stringProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Blob
condition|)
block|{
return|return
name|binaryProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Blob
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
return|return
name|binaryProperty
argument_list|(
name|name
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
return|return
name|longProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|longProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Integer
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Double
condition|)
block|{
return|return
name|doubleProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Double
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
return|return
name|booleanProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Boolean
operator|)
name|value
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
return|return
name|decimalProperty
argument_list|(
name|name
argument_list|,
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't infer type of value of class '"
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|PropertyState
name|emptyProperty
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an array type:"
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
operator|new
name|EmptyPropertyState
argument_list|(
name|name
argument_list|)
block|{
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
name|type
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|stringProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|StringPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|binaryProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
operator|new
name|BinaryPropertyState
argument_list|(
name|name
argument_list|,
operator|new
name|ArrayBasedBlob
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|longProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|doubleProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|DoublePropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|dateProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|DATE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|booleanProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|BooleanPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|nameProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|NAME
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|pathProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|PATH
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|referenceProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|REFERENCE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|weakreferenceProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|WEAKREFERENCE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|uriProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|URI
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|decimalProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
operator|new
name|DecimalPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|binaryProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Blob
name|value
parameter_list|)
block|{
return|return
operator|new
name|BinaryPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|stringProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|StringsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|binaryPropertyFromBlob
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Blob
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|BinariesPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|longProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Long
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|LongsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|doubleProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Double
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|DoublesPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|dateProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|DATES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|booleanProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Boolean
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|BooleansPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|nameProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|NAMES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|pathProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|PATHS
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|referenceProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|REFERENCES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|weakreferenceProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|WEAKREFERENCES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|uriProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|GenericsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|URIS
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|decimalProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|DecimalsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|binaryPropertyFromArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|byte
index|[]
argument_list|>
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|data
range|:
name|values
control|)
block|{
name|blobs
operator|.
name|add
argument_list|(
operator|new
name|ArrayBasedBlob
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BinariesPropertyState
argument_list|(
name|name
argument_list|,
name|blobs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

