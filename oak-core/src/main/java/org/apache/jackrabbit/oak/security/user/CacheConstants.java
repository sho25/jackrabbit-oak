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
name|user
package|;
end_package

begin_comment
comment|/**  * Constants for persisted user management related caches. Currently this only  * includes a basic cache for group principals names that is used to populate  * the set of {@link java.security.Principal}s as present on the  * {@link javax.security.auth.Subject} in the commit phase of the authentication.  */
end_comment

begin_interface
interface|interface
name|CacheConstants
block|{
name|String
name|NT_REP_CACHE
init|=
literal|"rep:Cache"
decl_stmt|;
name|String
name|REP_CACHE
init|=
literal|"rep:cache"
decl_stmt|;
name|String
name|REP_EXPIRATION
init|=
literal|"rep:expiration"
decl_stmt|;
name|String
name|REP_GROUP_PRINCIPAL_NAMES
init|=
literal|"rep:groupPrincipalNames"
decl_stmt|;
block|}
end_interface

end_unit

