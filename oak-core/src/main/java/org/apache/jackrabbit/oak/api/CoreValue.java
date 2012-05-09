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
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_comment
comment|/**  * {@code CoreValue} is the internal representation of a {@link javax.jcr.Value  * JCR value}. It is therefore isolated from session-specific namespace mappings  * and relies on the internal representation of JCR names and paths.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CoreValue
extends|extends
name|Comparable
argument_list|<
name|CoreValue
argument_list|>
block|{
comment|/**      * Returns the type of this value object which any of the following property      * types defined by the JCR specification:      *      *<ul>      *<li>{@link javax.jcr.PropertyType#BINARY BINARY}</li>      *<li>{@link javax.jcr.PropertyType#BOOLEAN BOOLEAN}</li>      *<li>{@link javax.jcr.PropertyType#DATE DATE}</li>      *<li>{@link javax.jcr.PropertyType#DECIMAL DECIMAL}</li>      *<li>{@link javax.jcr.PropertyType#DOUBLE DOUBLE}</li>      *<li>{@link javax.jcr.PropertyType#LONG LONG}</li>      *<li>{@link javax.jcr.PropertyType#NAME NAME}</li>      *<li>{@link javax.jcr.PropertyType#PATH PATH}</li>      *<li>{@link javax.jcr.PropertyType#REFERENCE REFERENCE}</li>      *<li>{@link javax.jcr.PropertyType#STRING STRING}</li>      *<li>{@link javax.jcr.PropertyType#URI URI}</li>      *<li>{@link javax.jcr.PropertyType#WEAKREFERENCE WEAKREFERENCE}</li>      *</ul>      *      * @return The type of this value instance. The return value is any of      * the types defined by {@link javax.jcr.PropertyType} except for      * {@link javax.jcr.PropertyType#UNDEFINED UNDEFINED}.      */
name|int
name|getType
parameter_list|()
function_decl|;
comment|/**      * Returns a {@code String} representation of this value. Note that the      * string reflects the internal state and doesn't respect any session level      * namespace remapping.      *      * @return The string representation of this value.      */
name|String
name|getString
parameter_list|()
function_decl|;
comment|/**      * Returns a {@code long} representation of this value.      *      * @return A {@code long} representation of this value based on an internal      * conversion.      * @throws NumberFormatException If the conversion fails.      */
name|long
name|getLong
parameter_list|()
function_decl|;
comment|/**      * Returns a {@code double} representation of this value.      *      * @return A {@code double} representation of this value based on an internal      * conversion.      * @throws NumberFormatException If the conversion fails.      */
name|double
name|getDouble
parameter_list|()
function_decl|;
comment|/**      * Returns a {@code boolean} representation of this value.      *      * @return A {@code boolean} representation of this value based on an internal      * conversion.      * @throws {@code UnsupportedOperationException} If the value cannot be      * converted a {@code boolean}.      */
name|boolean
name|getBoolean
parameter_list|()
function_decl|;
comment|/**      * Returns a {@code BigDecimal} representation of this value.      *      * @return A {@code BigDecimal} representation of this value based on an      * internal conversion.      * @throws {@code NumberFormatException} If the value cannot be converted      * a {@code BigDecimal}.      */
name|BigDecimal
name|getDecimal
parameter_list|()
function_decl|;
comment|/**      * Returns a new stream for this value object.      *      * @return a new stream for this value based on an internal conversion.      * @throws //TODO define exceptions      */
name|InputStream
name|getNewStream
parameter_list|()
function_decl|;
comment|/**      * Returns the length of this value.      *      * @return the length of this value.      */
name|long
name|length
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

