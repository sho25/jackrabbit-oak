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
name|authorization
operator|.
name|permission
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
name|JcrConstants
import|;
end_import

begin_comment
comment|/**  * PermissionConstants... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|PermissionConstants
block|{
comment|/**      * @since OAK 1.0      */
name|String
name|NT_REP_PERMISSIONS
init|=
literal|"rep:Permissions"
decl_stmt|;
comment|/**      * @since OAK 1.0      */
name|String
name|NT_REP_PERMISSION_STORE
init|=
literal|"rep:PermissionStore"
decl_stmt|;
comment|/**      * @since OAK 1.0      */
name|String
name|REP_PERMISSION_STORE
init|=
literal|"rep:permissionStore"
decl_stmt|;
comment|/**      * @since OAK 1.0      */
name|String
name|PERMISSIONS_STORE_PATH
init|=
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|'/'
operator|+
name|REP_PERMISSION_STORE
decl_stmt|;
name|String
name|REP_ACCESS_CONTROLLED_PATH
init|=
literal|"rep:accessControlledPath"
decl_stmt|;
name|String
name|REP_PRIVILEGE_BITS
init|=
literal|"rep:privileges"
decl_stmt|;
name|String
name|REP_INDEX
init|=
literal|"rep:index"
decl_stmt|;
name|char
name|PREFIX_ALLOW
init|=
literal|'a'
decl_stmt|;
name|char
name|PREFIX_DENY
init|=
literal|'d'
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|PERMISSION_NODETYPE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NT_REP_PERMISSIONS
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

