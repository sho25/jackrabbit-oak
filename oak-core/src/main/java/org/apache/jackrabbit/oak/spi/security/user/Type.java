begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|user
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
import|;
end_import

begin_comment
comment|/**  * The different authorizable types.  */
end_comment

begin_enum
specifier|public
enum|enum
name|Type
block|{
name|USER
parameter_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_USER
parameter_list|)
operator|,
constructor|GROUP(UserManager.SEARCH_TYPE_GROUP
block|)
enum|,
name|AUTHORIZABLE
argument_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
enum|;
end_enum

begin_decl_stmt
specifier|private
specifier|final
name|int
name|userType
decl_stmt|;
end_decl_stmt

begin_constructor
specifier|private
name|Type
parameter_list|(
name|int
name|userType
parameter_list|)
block|{
name|this
operator|.
name|userType
operator|=
name|userType
expr_stmt|;
block|}
end_constructor

unit|}
end_unit

