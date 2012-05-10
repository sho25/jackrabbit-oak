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
name|IOException
import|;
end_import

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
comment|/**  * {@code CoreValueFactory} defines methods to create new instances of  * {@code CoreValue}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CoreValueFactory
block|{
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#STRING}.      *      * @param value A non-null {@code String} defining the new value.      * @return a new value instance.      * @throws IllegalArgumentException if the specified {@code String}      * is {@code null}.      */
name|CoreValue
name|createValue
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#DOUBLE}.      *      * @param value The {@code double} that defines the new value.      * @return a new value instance.      */
name|CoreValue
name|createValue
parameter_list|(
name|double
name|value
parameter_list|)
function_decl|;
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#DOUBLE}.      *      * @param value The {@code double} that defines the new value.      * @return a new value instance.      */
name|CoreValue
name|createValue
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#BOOLEAN}.      *      * @param value The {@code boolean} that defines the new value.      * @return a new value instance.      */
name|CoreValue
name|createValue
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#DECIMAL}.      *      * @param value A non-null {@code BigDecimal} that defines the new value.      * @return a new value instance.      * @throws IllegalArgumentException if the specified {@code BigDecimal} is {@code null}.      */
name|CoreValue
name|createValue
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
function_decl|;
comment|/**      * Creates a new value of type {@link javax.jcr.PropertyType#BINARY}.      *      * @param value A non-null {@code InputStream} that defines the new value.      * @return a new value instance.      * @throws IllegalArgumentException if the specified {@code InputStream} is {@code null}.      * @throws IOException If an error occurs while processing the stream.      * @throws //TODO define exceptions (currently impl. throws MicrokernelException)      */
name|CoreValue
name|createValue
parameter_list|(
name|InputStream
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a new value of the specified type.      *      * @param value A non-null {@code String} that defines the new value.      * @param type The desired target type of the new value.      * @return a new value instance.      * @throws IllegalArgumentException if the specified {@code value} is {@code null}      * or if the given type is not supported.      * @throws NumberFormatException If the specified {@code type} requires      * conversion to any of the number types and the conversion fails.      * @throws //TODO define and consolidate exceptions      */
name|CoreValue
name|createValue
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

