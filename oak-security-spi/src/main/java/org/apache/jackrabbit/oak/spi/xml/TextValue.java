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
name|spi
operator|.
name|xml
package|;
end_package

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

begin_comment
comment|/**  * {@code TextValue} represents a serialized property value read  * from a System or Document View XML document.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TextValue
block|{
name|String
name|getString
parameter_list|()
function_decl|;
name|Value
name|getValue
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Dispose this value, i.e. free all bound resources. Once a value has      * been disposed, further method invocations will cause an IOException      * to be thrown.      */
name|void
name|dispose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

