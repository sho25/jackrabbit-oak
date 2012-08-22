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
name|user
package|;
end_package

begin_comment
comment|/**  * UserConstants...  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserConstants
block|{
name|String
name|NT_REP_AUTHORIZABLE
init|=
literal|"rep:Authorizable"
decl_stmt|;
name|String
name|NT_REP_AUTHORIZABLE_FOLDER
init|=
literal|"rep:AuthorizableFolder"
decl_stmt|;
name|String
name|NT_REP_USER
init|=
literal|"rep:User"
decl_stmt|;
name|String
name|NT_REP_GROUP
init|=
literal|"rep:Group"
decl_stmt|;
name|String
name|NT_REP_MEMBERS
init|=
literal|"rep:Members"
decl_stmt|;
name|String
name|REP_PRINCIPAL_NAME
init|=
literal|"rep:principalName"
decl_stmt|;
name|String
name|REP_AUTHORIZABLE_ID
init|=
literal|"rep:authorizableId"
decl_stmt|;
name|String
name|REP_PASSWORD
init|=
literal|"rep:password"
decl_stmt|;
name|String
name|REP_DISABLED
init|=
literal|"rep:disabled"
decl_stmt|;
name|String
name|REP_MEMBERS
init|=
literal|"rep:members"
decl_stmt|;
name|String
name|REP_IMPERSONATORS
init|=
literal|"rep:impersonators"
decl_stmt|;
name|String
name|DEFAULT_USER_PATH
init|=
literal|"/rep:security/rep:authorizables/rep:users"
decl_stmt|;
name|String
name|DEFAULT_GROUP_PATH
init|=
literal|"/rep:security/rep:authorizables/rep:groups"
decl_stmt|;
name|int
name|DEFAULT_DEPTH
init|=
literal|2
decl_stmt|;
name|int
name|SEARCH_TYPE_USER
init|=
literal|1
decl_stmt|;
comment|/**      * Filter flag indicating that only<code>Group</code>s should be searched      * and returned.      */
name|int
name|SEARCH_TYPE_GROUP
init|=
literal|2
decl_stmt|;
comment|/**      * Filter flag indicating that all<code>Authorizable</code>s should be      * searched.      */
name|int
name|SEARCH_TYPE_AUTHORIZABLE
init|=
literal|3
decl_stmt|;
block|}
end_interface

end_unit

