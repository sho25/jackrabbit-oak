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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_comment
comment|/**  * Abstract base class for single valued {@code PropertyState} implementations.  */
end_comment

begin_class
specifier|abstract
class|class
name|SinglePropertyState
extends|extends
name|EmptyPropertyState
block|{
comment|/**      * Create a new property state with the given {@code name}      * @param name  The name of the property state.      */
specifier|protected
name|SinglePropertyState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * String representation of the value of the property state.      * @return      */
specifier|protected
specifier|abstract
name|String
name|getString
parameter_list|()
function_decl|;
comment|/**      * @return  A {@link StringBasedBlob} instance created by calling      * {@link #getString()}.      */
specifier|protected
name|Blob
name|getBlob
parameter_list|()
block|{
return|return
name|getBinary
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  {@code getLong(getString())}      */
specifier|protected
name|long
name|getLong
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  {@code getDouble(getString())}      */
specifier|protected
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|getDouble
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  {@code StringPropertyState.getBoolean(getString())}      */
specifier|protected
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  {@code getDecimal(getString())}      */
specifier|protected
name|BigDecimal
name|getDecimal
parameter_list|()
block|{
return|return
name|getDecimal
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return  {@code false}      */
annotation|@
name|Override
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @throws IllegalArgumentException if {@code type} is not one of the      * values defined in {@link Type}.      */
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
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
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
operator|(
name|T
operator|)
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getBlob
argument_list|()
argument_list|)
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
name|singleton
argument_list|(
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
operator|(
name|T
operator|)
name|singleton
argument_list|(
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
operator|(
name|T
operator|)
name|singleton
argument_list|(
name|getString
argument_list|()
argument_list|)
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
name|singleton
argument_list|(
name|getBoolean
argument_list|()
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
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getString
argument_list|()
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
name|singleton
argument_list|(
name|getDecimal
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid primitive type:"
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
else|else
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
operator|(
name|T
operator|)
name|getString
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
name|getBlob
argument_list|()
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
argument_list|()
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
name|getString
argument_list|()
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
name|getString
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
name|getString
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
name|getString
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
name|getString
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
name|getString
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
name|getDecimal
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid array type:"
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * @throws IllegalArgumentException  if {@code type.isArray} is {@code true}      * @throws IndexOutOfBoundsException  if {@code index != 0}      */
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
operator|!=
literal|0
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
return|return
name|getValue
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * @return  {@code getString().length()}      */
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|getString
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**      * @return  {@code size}      * @throws IndexOutOfBoundsException  if {@code index != 0}      */
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
if|if
condition|(
name|index
operator|!=
literal|0
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
return|return
name|size
argument_list|()
return|;
block|}
comment|/**      * @return {@code 1}      */
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

