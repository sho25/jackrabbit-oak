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
operator|.
name|action
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|ImmutableMap
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Group
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|AbstractSecurityTest
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
name|security
operator|.
name|ConfigurationParameters
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|security
operator|.
name|user
operator|.
name|UserConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Testing the {@code AccessControlAction}  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlActionTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|AccessControlAction
operator|.
name|GROUP_PRIVILEGE_NAMES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|AccessControlAction
operator|.
name|USER_PRIVILEGE_NAMES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|userConfig
init|=
operator|new
name|ConfigurationParameters
argument_list|(
name|map
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConfigurationParameters
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|PARAM_USER_OPTIONS
argument_list|,
name|userConfig
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlActionForUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|User
name|u
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|uid
init|=
literal|"actionTestUser"
decl_stmt|;
name|u
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|uid
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertAcAction
argument_list|(
name|u
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|u
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessControlAction
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Group
name|gr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|gr
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
literal|"actionTestGroup"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertAcAction
argument_list|(
name|gr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
if|if
condition|(
name|gr
operator|!=
literal|null
condition|)
block|{
name|gr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertAcAction
parameter_list|(
name|Authorizable
name|a
parameter_list|,
name|String
name|expectedPrivName
parameter_list|)
throws|throws
name|Exception
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|AccessControlList
argument_list|)
expr_stmt|;
name|AccessControlList
name|acl
init|=
operator|(
name|AccessControlList
operator|)
name|policies
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Privilege
index|[]
block|{
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
operator|.
name|getPrivilege
argument_list|(
name|expectedPrivName
argument_list|)
block|}
argument_list|,
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
index|[
literal|0
index|]
operator|.
name|getPrivileges
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

