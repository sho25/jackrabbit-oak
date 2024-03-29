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
name|security
operator|.
name|authentication
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
import|;
end_import

begin_interface
specifier|public
interface|interface
name|TokenConstants
block|{
comment|/**      * Constant for the token attribute passed with valid simple credentials to      * trigger the generation of a new token.      */
name|String
name|TOKEN_ATTRIBUTE
init|=
literal|".token"
decl_stmt|;
name|String
name|TOKEN_ATTRIBUTE_EXPIRY
init|=
literal|"rep:token.exp"
decl_stmt|;
name|String
name|TOKEN_ATTRIBUTE_KEY
init|=
literal|"rep:token.key"
decl_stmt|;
name|String
name|TOKENS_NODE_NAME
init|=
literal|".tokens"
decl_stmt|;
name|String
name|TOKENS_NT_NAME
init|=
name|NodeTypeConstants
operator|.
name|NT_REP_UNSTRUCTURED
decl_stmt|;
name|String
name|TOKEN_NT_NAME
init|=
literal|"rep:Token"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|RESERVED_ATTRIBUTES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TOKEN_ATTRIBUTE
argument_list|,
name|TOKEN_ATTRIBUTE_EXPIRY
argument_list|,
name|TOKEN_ATTRIBUTE_KEY
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|TOKEN_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TOKEN_ATTRIBUTE_EXPIRY
argument_list|,
name|TOKEN_ATTRIBUTE_KEY
argument_list|)
decl_stmt|;
comment|/**      * Flag set on the TokenCredentials to skip refreshing the token expiration time      */
name|String
name|TOKEN_SKIP_REFRESH
init|=
literal|"tokenSkipRefresh"
decl_stmt|;
block|}
end_interface

end_unit

