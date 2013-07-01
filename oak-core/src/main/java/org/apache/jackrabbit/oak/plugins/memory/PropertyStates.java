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
name|Calendar
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|Iterables
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
name|plugins
operator|.
name|value
operator|.
name|ValueImpl
import|;
end_import

begin_comment
comment|/**  * Utility class for creating {@link PropertyState} instances.  */
end_comment

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
comment|/**      * Create a {@code PropertyState} based on a {@link Value}. The      * {@link Type} of the property state is determined by the      * type of the value.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state      * @throws RepositoryException forwarded from {@code value}      */
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
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|type
init|=
name|value
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
return|return
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
name|name
argument_list|,
name|getString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|DoublePropertyState
operator|.
name|doubleProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
name|LongPropertyState
operator|.
name|createDateProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|DecimalPropertyState
operator|.
name|decimalProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getDecimal
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
name|getString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
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
comment|/**      * Create a multi valued {@code PropertyState} based on a list of      * {@link Value} instances. The {@link Type} of the property is determined      * by the type of the first value in the list or {@link Type#STRING} if the      * list is empty.      *      * @param name  The name of the property state      * @param values  The values of the property state      * @return  The new property state      * @throws RepositoryException forwarded from {@code value}      */
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
name|Iterable
argument_list|<
name|Value
argument_list|>
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|type
init|=
name|PropertyType
operator|.
name|STRING
decl_stmt|;
name|Value
name|first
init|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|values
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|first
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Value
argument_list|>
name|values
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|getString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiStringPropertyState
operator|.
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
name|Value
name|value
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
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiBinaryPropertyState
operator|.
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|longs
operator|.
name|add
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiLongPropertyState
operator|.
name|createLongProperty
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|doubles
operator|.
name|add
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiDoublePropertyState
operator|.
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
name|DATE
case|:
name|List
argument_list|<
name|Long
argument_list|>
name|dates
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
name|dates
operator|.
name|add
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiLongPropertyState
operator|.
name|createDatePropertyFromLong
argument_list|(
name|name
argument_list|,
name|dates
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|booleans
operator|.
name|add
argument_list|(
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiBooleanPropertyState
operator|.
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|decimals
operator|.
name|add
argument_list|(
name|value
operator|.
name|getDecimal
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|MultiDecimalPropertyState
operator|.
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
name|Value
name|value
range|:
name|values
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|getString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiGenericPropertyState
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
specifier|private
specifier|static
name|String
name|getString
parameter_list|(
name|Value
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|instanceof
name|ValueImpl
condition|)
block|{
return|return
operator|(
operator|(
name|ValueImpl
operator|)
name|value
operator|)
operator|.
name|getOakString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|NAME
operator|||
name|type
operator|==
name|PropertyType
operator|.
name|PATH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot create name of path property state from Value "
operator|+
literal|"of class '"
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
else|else
block|{
return|return
name|value
operator|.
name|getString
argument_list|()
return|;
block|}
block|}
comment|/**      * Create a {@code PropertyState} from a string.      * @param name  The name of the property state      * @param value  The value of the property state      * @param type  The type of the property state      * @return  The new property state      */
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
name|StringPropertyState
operator|.
name|stringProperty
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
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toBinary
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toLong
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|DoublePropertyState
operator|.
name|doubleProperty
argument_list|(
name|name
argument_list|,
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
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
name|LongPropertyState
operator|.
name|createDateProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDate
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toBoolean
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|DecimalPropertyState
operator|.
name|decimalProperty
argument_list|(
name|name
argument_list|,
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
comment|/**      * Create a {@code PropertyState}.      * @param name  The name of the property state      * @param value  The value of the property state      * @param type  The type of the property state      * @return  The new property state      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|Object
name|value
parameter_list|,
name|Type
argument_list|<
name|?
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
name|MultiStringPropertyState
operator|.
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
name|StringPropertyState
operator|.
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
name|MultiBinaryPropertyState
operator|.
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
name|BinaryPropertyState
operator|.
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
name|MultiLongPropertyState
operator|.
name|createLongProperty
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
name|LongPropertyState
operator|.
name|createLongProperty
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
name|MultiDoublePropertyState
operator|.
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
name|DoublePropertyState
operator|.
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
name|MultiLongPropertyState
operator|.
name|createDatePropertyFromLong
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
name|LongPropertyState
operator|.
name|createDateProperty
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
name|BOOLEAN
case|:
return|return
name|type
operator|.
name|isArray
argument_list|()
condition|?
name|MultiBooleanPropertyState
operator|.
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
name|BooleanPropertyState
operator|.
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
name|MultiGenericPropertyState
operator|.
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
name|GenericPropertyState
operator|.
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
name|MultiGenericPropertyState
operator|.
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
name|GenericPropertyState
operator|.
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
name|MultiGenericPropertyState
operator|.
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
name|GenericPropertyState
operator|.
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
name|MultiGenericPropertyState
operator|.
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
name|GenericPropertyState
operator|.
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
name|MultiGenericPropertyState
operator|.
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
name|GenericPropertyState
operator|.
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
name|MultiDecimalPropertyState
operator|.
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
name|DecimalPropertyState
operator|.
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
comment|/**      * Create a {@code PropertyState} where the {@link Type} of the property state      * is inferred from the runtime type of {@code T} according to the mapping      * established through {@code Type}.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state      */
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
name|StringPropertyState
operator|.
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
name|BinaryPropertyState
operator|.
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
name|BinaryPropertyState
operator|.
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
name|LongPropertyState
operator|.
name|createLongProperty
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
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|name
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Integer
argument_list|)
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
name|DoublePropertyState
operator|.
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
name|Calendar
condition|)
block|{
return|return
name|LongPropertyState
operator|.
name|createDateProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Calendar
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
name|BooleanPropertyState
operator|.
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
name|DecimalPropertyState
operator|.
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
name|convert
parameter_list|(
name|PropertyState
name|state
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|state
operator|.
name|getType
argument_list|()
operator|||
operator|(
name|type
operator|==
name|Type
operator|.
name|UNDEFINED
operator|&&
operator|!
name|state
operator|.
name|isArray
argument_list|()
operator|)
operator|||
operator|(
name|type
operator|==
name|Type
operator|.
name|UNDEFINEDS
operator|&&
name|state
operator|.
name|isArray
argument_list|()
operator|)
condition|)
block|{
return|return
name|state
return|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|,
name|state
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

