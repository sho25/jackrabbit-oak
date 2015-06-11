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
name|remote
operator|.
name|http
operator|.
name|matcher
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_comment
comment|/**  * A predicate over an HTTP request. This predicate can be used to check if some  * preconditions on the request are met.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Matcher
block|{
comment|/**      * Check if the preconditions on the given request are met.      *      * @param request Request to check.      * @return {@code true} if the preconditions are met, {@code false}      * otherwise.      */
name|boolean
name|match
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

