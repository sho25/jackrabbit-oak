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
name|jcr
operator|.
name|security
operator|.
name|user
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|JackrabbitSession
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
name|jcr
operator|.
name|repository
operator|.
name|RepositoryImpl
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
name|test
operator|.
name|RepositoryStub
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests asserting that auto-refresh on session is properly propagated to the  * user management API.  */
end_comment

begin_class
specifier|public
class|class
name|RefreshTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
name|Session
name|adminSession
init|=
literal|null
decl_stmt|;
specifier|private
name|UserManager
name|adminUserManager
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|String
name|adminId
init|=
name|getHelper
argument_list|()
operator|.
name|getProperty
argument_list|(
name|RepositoryStub
operator|.
name|PROP_PREFIX
operator|+
literal|'.'
operator|+
name|RepositoryStub
operator|.
name|PROP_SUPERUSER_NAME
argument_list|)
decl_stmt|;
name|String
name|adminPw
init|=
name|getHelper
argument_list|()
operator|.
name|getProperty
argument_list|(
name|RepositoryStub
operator|.
name|PROP_PREFIX
operator|+
literal|'.'
operator|+
name|RepositoryStub
operator|.
name|PROP_SUPERUSER_PWD
argument_list|)
decl_stmt|;
name|SimpleCredentials
name|credentials
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|adminId
argument_list|,
name|adminPw
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|setAttribute
argument_list|(
name|RepositoryImpl
operator|.
name|REFRESH_INTERVAL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|adminSession
operator|=
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|adminUserManager
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
operator|.
name|getUserManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|adminSession
operator|!=
literal|null
condition|)
block|{
name|adminSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|uid
init|=
name|createUserId
argument_list|()
decl_stmt|;
name|user
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
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|adminUserManager
operator|.
name|getAuthorizable
argument_list|(
name|uid
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-1124"
argument_list|)
comment|// FIXME: OAK-1124
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizableGetProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|User
name|user
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|uid
init|=
name|createUserId
argument_list|()
decl_stmt|;
name|user
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
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Authorizable
name|a
init|=
name|adminUserManager
operator|.
name|getAuthorizable
argument_list|(
name|uid
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

