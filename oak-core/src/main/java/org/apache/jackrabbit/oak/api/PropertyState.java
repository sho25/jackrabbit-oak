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

begin_comment
comment|/**  * Immutable property state. A property consists of a name and  * a JSON-encoded value.  *  *<h2>Equality and hash codes</h2>  *<p>  * Two property states are considered equal if and only if their names and  * encoded values match. The {@link Object#equals(Object)} method needs to  * be implemented so that it complies with this definition. And while  * property states are not meant for use as hash keys, the  * {@link Object#hashCode()} method should still be implemented according  * to this equality contract.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyState
block|{
comment|/**      * If a JCR node is orderable the OAK API will expose an "{@code oak:childOrder}"      * property state indicating the order of child nodes.      *      * NOTE: this is an temporary solution (OAK-232) until we reach consensus (see also OAK-182)      *      * // TODO: Use a proper namespace for this property?      * // TODO: Expose this a API method on the Tree interface (see http://markmail.org/message/kzt7csiz2bd5n3ww) ?      * // TODO: Define if/how this internal property is exposed on the JCR API      */
specifier|public
specifier|static
specifier|final
name|String
name|OAK_CHILD_ORDER
init|=
literal|"childOrder"
decl_stmt|;
comment|/**      * @return the name of this property state      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Determine whether this is a multi valued property      * @return  {@code true} if and only if this is a multi valued property.      */
name|boolean
name|isArray
parameter_list|()
function_decl|;
comment|/**      * Value of this property.      * @return  the single value of this property.      * @throws IllegalStateException  if {@code isArray()} is {@code true}.      */
annotation|@
name|Nonnull
name|CoreValue
name|getValue
parameter_list|()
function_decl|;
comment|/**      * Values of this property. The returned list is immutable and contains      * all the values of this property. If this is a single-valued property,      * then the returned list will simply contain the single value returned      * by the {@link #getValue()} method.      *      * @return immutable list of the values of this property      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|CoreValue
argument_list|>
name|getValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

