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
name|spi
operator|.
name|state
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
name|CheckForNull
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

begin_comment
comment|/**  * Builder interface for constructing new {@link PropertyState node states}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyBuilder
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * @return The name of the property state      */
annotation|@
name|CheckForNull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return The value of the property state or {@code null} if {@code isEmpty} is {@code true}      */
annotation|@
name|CheckForNull
name|T
name|getValue
parameter_list|()
function_decl|;
comment|/**      * @return  A list of values of the property state      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|T
argument_list|>
name|getValues
parameter_list|()
function_decl|;
comment|/**      * @param index      * @return  The value of the property state at the given {@code index}.      * @throws IndexOutOfBoundsException  if {@code index>= count}      */
annotation|@
name|Nonnull
name|T
name|getValue
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * @param value      * @return  {@code true} iff the property state contains {@code value}.      */
name|boolean
name|hasValue
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * @return  The number of values of the property state      */
name|int
name|count
parameter_list|()
function_decl|;
comment|/**      * @return  {@code true} iff {@code count() != 1}      */
name|boolean
name|isArray
parameter_list|()
function_decl|;
comment|/**      * @return  {{@code true}} iff {@code count() == 0}      * @return      */
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * Returns an immutable property state that matches the current state of      * the builder. The {@code asArray} flag can be used to coerce a property      * state with a single value into a multi valued property state.      * Equivalent to {@code getPropertyState(false)}      *      * @return immutable property state      * @throws IllegalStateException  If the name of the property is not set      */
annotation|@
name|Nonnull
name|PropertyState
name|getPropertyState
parameter_list|()
function_decl|;
comment|/**      * Returns an immutable property state that matches the current state of      * the builder. The {@code asArray} flag can be used to coerce a property      * state with a single value into a multi valued property state.      *      * @param asArray  If {@code true} the builder creates a multi valued property state      * @return immutable property state      * @throws IllegalStateException  If the name of the property is not set      */
annotation|@
name|Nonnull
name|PropertyState
name|getPropertyState
parameter_list|(
name|boolean
name|asArray
parameter_list|)
function_decl|;
comment|/**      * Clone {@code property} to the property state being built. After      * this call {@code getPropertyState(property.isArray()).equals(property)} will hold.      * @param property  the property to clone      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|assignFrom
parameter_list|(
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * Set the name of the property      * @param name      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Set the value of the property state clearing all previously set values.      * @param value  value to set      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValue
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**      * Add a value to the end of the list of values of the property state.      * @param value  value to add      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|addValue
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
comment|/**      * Set the value of the property state at the given {@code index}.      * @param value  value to set      * @param index  index to set the value      * @return  {@code this}      * @throws IndexOutOfBoundsException  if {@code index>= count}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValue
parameter_list|(
name|T
name|value
parameter_list|,
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * Set the values of the property state clearing all previously set values.      * @param values      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValues
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Remove the value at the given {@code index}      * @param index      * @return  {@code this}      * @throws IndexOutOfBoundsException  if {@code index>= count}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|removeValue
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * Remove the given value from the property state      * @param value  value to remove      * @return  {@code this}      */
annotation|@
name|Nonnull
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|removeValue
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

