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
name|base
operator|.
name|Function
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
name|checkArgument
import|;
end_import

begin_comment
comment|/**  * Abstract base class for multi valued {@code PropertyState} implementations.  */
end_comment

begin_class
specifier|abstract
class|class
name|MultiPropertyState
parameter_list|<
name|T
parameter_list|>
extends|extends
name|EmptyPropertyState
block|{
specifier|protected
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|values
decl_stmt|;
comment|/**      * Create a new property state with the given {@code name}      * and {@code values}      * @param name  The name of the property state.      * @param values  The values of the property state.      */
specifier|protected
name|MultiPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return  {@code Iterable} of the string representations of the values      * of the property state.      */
specifier|protected
specifier|abstract
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|()
function_decl|;
comment|/**      * @param index      * @return  String representation of the value at {@code index }of the      * property state.      */
specifier|protected
specifier|abstract
name|String
name|getString
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * @return  The values of this property state as {@link Blob}s      */
specifier|protected
name|Iterable
argument_list|<
name|Blob
argument_list|>
name|getBlobs
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Blob
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Blob
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toBinary
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @return  The values of this property state as {@code Long}s      */
specifier|protected
name|Iterable
argument_list|<
name|Long
argument_list|>
name|getLongs
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toLong
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @return  The values of this property state as {@code Double}s      */
specifier|protected
name|Iterable
argument_list|<
name|Double
argument_list|>
name|getDoubles
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Double
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDouble
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @return  The values of this property state as {@code Dates}s      */
specifier|protected
name|Iterable
argument_list|<
name|String
argument_list|>
name|getDates
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @return  The values of this property state as {@code Booleans}s      */
specifier|protected
name|Iterable
argument_list|<
name|Boolean
argument_list|>
name|getBooleans
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toBoolean
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @return  The values of this property state as {@code BigDecimal}s      */
specifier|protected
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
name|getDecimals
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getStrings
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|BigDecimal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BigDecimal
name|apply
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDecimal
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@link Blob}      */
specifier|protected
name|Blob
name|getBlob
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toBinary
argument_list|()
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@code long}      */
specifier|protected
name|long
name|getLong
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toLong
argument_list|()
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@code double}      */
specifier|protected
name|double
name|getDouble
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDouble
argument_list|()
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@code date}      */
specifier|protected
name|String
name|getDate
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@code boolean}      */
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toBoolean
argument_list|()
return|;
block|}
comment|/**      * @param index      * @return  The value at the given {@code index} as {@code BigDecimal}      */
specifier|protected
name|BigDecimal
name|getDecimal
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getString
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDecimal
argument_list|()
return|;
block|}
comment|/**      * @throws IllegalArgumentException if {@code type} is not one of the      * values defined in {@link Type} or if {@code type.isArray()} is {@code false}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|checkArgument
argument_list|(
name|type
operator|.
name|isArray
argument_list|()
argument_list|,
literal|"Type must not be an array type"
argument_list|)
expr_stmt|;
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
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|(
name|T
operator|)
name|getBlobs
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
operator|(
name|T
operator|)
name|getLongs
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
operator|(
name|T
operator|)
name|getDoubles
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
operator|(
name|T
operator|)
name|getDates
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
operator|(
name|T
operator|)
name|getBooleans
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|(
name|T
operator|)
name|getDecimals
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type:"
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/**      * @throws IllegalArgumentException if {@code type} is not one of the      * values defined in {@link Type} or if {@code type.isArray()} is {@code true}      * @throws IndexOutOfBoundsException if {@code index>= count()}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|checkArgument
argument_list|(
operator|!
name|type
operator|.
name|isArray
argument_list|()
argument_list|,
literal|"Type must not be an array type"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>=
name|count
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
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
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|(
name|T
operator|)
name|getBlob
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Long
argument_list|)
name|getLong
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Double
argument_list|)
name|getDouble
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
operator|(
name|T
operator|)
name|getDate
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Boolean
argument_list|)
name|getBoolean
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
operator|(
name|T
operator|)
name|getString
argument_list|(
name|index
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|(
name|T
operator|)
name|getDecimal
argument_list|(
name|index
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type:"
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|count
parameter_list|()
block|{
return|return
name|values
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
name|getString
argument_list|(
name|index
argument_list|)
operator|.
name|length
argument_list|()
return|;
block|}
block|}
end_class

end_unit

