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
comment|/**  * CoreValue... TODO: add description and javadoc for methods  */
end_comment

begin_interface
specifier|public
interface|interface
name|CoreValue
block|{
name|int
name|getType
parameter_list|()
function_decl|;
name|String
name|getString
parameter_list|()
function_decl|;
name|long
name|getLong
parameter_list|()
function_decl|;
name|double
name|getDouble
parameter_list|()
function_decl|;
name|boolean
name|getBoolean
parameter_list|()
function_decl|;
name|BigDecimal
name|getDecimal
parameter_list|()
function_decl|;
comment|/**      * Returns a new stream for this value object.      *      * @return a new stream.      */
name|InputStream
name|getNewStream
parameter_list|()
function_decl|;
name|long
name|length
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

