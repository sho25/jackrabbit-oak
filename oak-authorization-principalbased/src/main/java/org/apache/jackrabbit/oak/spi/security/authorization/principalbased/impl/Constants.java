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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
package|;
end_package

begin_interface
interface|interface
name|Constants
block|{
comment|/**      * The name of the mixin type that defines the principal based access control policy node.      */
name|String
name|MIX_REP_PRINCIPAL_BASED_MIXIN
init|=
literal|"rep:PrincipalBasedMixin"
decl_stmt|;
comment|/**      * The primary node type name of the principal based access control policy node.      */
name|String
name|NT_REP_PRINCIPAL_POLICY
init|=
literal|"rep:PrincipalPolicy"
decl_stmt|;
comment|/**      * The primary node type name of the entries inside the principal based access control policy node.      */
name|String
name|NT_REP_PRINCIPAL_ENTRY
init|=
literal|"rep:PrincipalEntry"
decl_stmt|;
comment|/**      * The primary node type name of the restrictions node associated with entries inside the principal based access control policy node.      */
name|String
name|NT_REP_RESTRICTIONS
init|=
literal|"rep:Restrictions"
decl_stmt|;
comment|/**      * The name of the principal based access control policy node.      */
name|String
name|REP_PRINCIPAL_POLICY
init|=
literal|"rep:principalPolicy"
decl_stmt|;
comment|/**      * The name of the mandatory principal name property associated with the principal based access control policy.      */
name|String
name|REP_PRINCIPAL_NAME
init|=
literal|"rep:principalName"
decl_stmt|;
comment|/**      * The name of the mandatory path property of a given entry in a principal based access control policy.      * It will store an absolute path or empty string for the repository-level      */
name|String
name|REP_EFFECTIVE_PATH
init|=
literal|"rep:effectivePath"
decl_stmt|;
comment|/**      * The name of the mandatory principal property of a given entry in a principal based access control policy.      */
name|String
name|REP_PRIVILEGES
init|=
literal|"rep:privileges"
decl_stmt|;
comment|/**      * The name of the optional restriction node associated with a given entry in a principal based access control policy.      */
name|String
name|REP_RESTRICTIONS
init|=
literal|"rep:restrictions"
decl_stmt|;
comment|/**      * Value to be used for the {@code rep:effectivePath} property in case of repository level permissions (analog to passing      * null to {@code AccessControlManager.getEffectivePolicies(String)}.      */
name|String
name|REPOSITORY_PERMISSION_PATH
init|=
literal|""
decl_stmt|;
block|}
end_interface

end_unit

