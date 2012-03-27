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
name|mk
operator|.
name|model
package|;
end_package

begin_comment
comment|/**  * Immutable property state. A property consists of a name and  * a JSON-encoded value.  *  *<h2>Equality and hash codes</h2>  *<p>  * Two property states are considered equal if and only if their names and  * encoded values match. The {@link Object#equals(Object)} method needs to  * be implemented so that it complies with this definition. And while  * property states are not meant for use as hash keys, the  * {@link Object#hashCode()} method should still be implemented according  * to this equality contract.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyState
block|{
comment|/**      * @return the name of this property state      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return the JSON encoded value of this property state.      */
name|String
name|getEncodedValue
parameter_list|()
function_decl|;
comment|/**      * Determine whether this is a multi valued property      * @return  {@code true} if and only if this is a multi valued property.      */
name|boolean
name|isArray
parameter_list|()
function_decl|;
comment|/**      * @return  the single value of this property or {@code null} if this is a multi      * valued property.      */
name|Scalar
name|getScalar
parameter_list|()
function_decl|;
comment|/**      * @return  an iterable of the values of this multi valued property or      * {@code null} if this is not a multi valued property.      */
name|Iterable
argument_list|<
name|Scalar
argument_list|>
name|getArray
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

