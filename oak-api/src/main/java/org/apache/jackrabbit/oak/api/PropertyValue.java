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
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Immutable property value.  * A value is either an atom or an array of atoms.  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyValue
extends|extends
name|Comparable
argument_list|<
name|PropertyValue
argument_list|>
block|{
comment|/**      * Determine whether the value is an array of atoms      * @return {@code true} if and only if the value is an array of atoms.      */
name|boolean
name|isArray
parameter_list|()
function_decl|;
comment|/**      * Determine the type of this value      * @return the type of this value      */
name|Type
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
function_decl|;
comment|/**      * Value of this object.      * The type of the return value is determined by the target {@code type}      * argument. If {@code type.isArray()} is true, this method returns an      * {@code Iterable} of the {@link Type#getBaseType() base type} of      * {@code type} containing all values of this property.      * If the target type is not the same as the type of this property an attempt      * is made to convert the value to the target type. If the conversion fails an      * exception is thrown.      * @param type target type      * @param<T>      * @return the value of this property      * @throws IllegalStateException  if {@code type.isArray() == false} and      *         {@code this.isArray() == true}. In other words, when trying to convert      *         from an array to an atom.      * @throws IllegalArgumentException  if {@code type} refers to an unknown type.      * @throws NumberFormatException  if conversion to a number failed.      * @throws UnsupportedOperationException  if conversion to boolean failed.      */
annotation|@
name|Nonnull
argument_list|<
name|T
argument_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Value at the given {@code index}.      * The type of the return value is determined by the target {@code type}      * argument.      * If the target type is not the same as the type of this property an attempt      * is made to convert the value to the target type. If the conversion fails an      * exception is thrown.      * @param type target type      * @param index      * @param<T>      * @return the value of this object at the given {@code index}      * @throws IndexOutOfBoundsException  if {@code index} is less than {@code 0} or      *         greater or equals {@code count()}.      * @throws IllegalArgumentException  if {@code type} refers to an unknown type or if      *         {@code type.isArray()} is true.      */
annotation|@
name|Nonnull
argument_list|<
name|T
argument_list|>
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
function_decl|;
comment|/**      * The size of the value of this object.      * @return size of the value of this property      * @throws IllegalStateException  if the value is an array      */
name|long
name|size
parameter_list|()
function_decl|;
comment|/**      * The size of the value at the given {@code index}.      * @param index      * @return size of the value at the given {@code index}.      * @throws IndexOutOfBoundsException  if {@code index} is less than {@code 0} or      *         greater or equals {@code count()}.      */
name|long
name|size
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * The number of values of this object. {@code 1} for atoms.      * @return number of values      */
name|int
name|count
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

