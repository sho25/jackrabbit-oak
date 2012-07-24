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
name|security
operator|.
name|authentication
operator|.
name|token
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_comment
comment|/**  * TokenProvider... TODO document, move to spi/api  */
end_comment

begin_interface
specifier|public
interface|interface
name|TokenProvider
block|{
comment|/**      * Default expiration time in ms for login tokens is 2 hours.      */
name|long
name|TOKEN_EXPIRATION
init|=
literal|2
operator|*
literal|3600
operator|*
literal|1000
decl_stmt|;
name|boolean
name|doCreateToken
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
function_decl|;
name|TokenInfo
name|createToken
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
function_decl|;
name|TokenInfo
name|getTokenInfo
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
name|boolean
name|removeToken
parameter_list|(
name|TokenInfo
name|tokenInfo
parameter_list|)
function_decl|;
name|boolean
name|resetTokenExpiration
parameter_list|(
name|TokenInfo
name|tokenInfo
parameter_list|,
name|long
name|loginTime
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

