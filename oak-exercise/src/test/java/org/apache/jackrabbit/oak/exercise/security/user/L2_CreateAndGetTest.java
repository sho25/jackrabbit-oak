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
name|exercise
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: User Management  * =============================================================================  *  * Title: User Management Basics  * -----------------------------------------------------------------------------  *  * Goal:  * Make yourself familiar with the basic user management functionality as present  * in Jackrabbit API  *  * Exercises:  *  * - {@link #testCreateUser()}  *   Use this test to create a new user. Play with the parameters.  *   Question: What are valid values for the parameters?  *   Question: Which parameters can be 'null'?  *   Question: What's the effect if one/some parameters are 'null'?  *  * - {@link #testCreateGroup()}  *   Use to method to create a new group. Play with the parameters.  *   Question: What are valid values for the parameters?  *   Question: Which parameters can be 'null'?  *   Question: What's the effect if one/some parameters are 'null'?  *  * - {@link #testGetAuthorizable()}  *   Play around wit the various methods defined on {@link org.apache.jackrabbit.api.security.user.UserManager}  *   to retrieve an existing user or group.  *  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|L2_CreateAndGetTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|Group
name|testGroup
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
name|userManager
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
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
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
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
specifier|public
name|void
name|testCreateUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: use the following parameters (with suitable values) to create a new user.
comment|// EXERCISE: play with the values. what are valid values? which params can be null? what is the effect?
name|String
name|userID
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
name|Principal
name|principal
init|=
literal|null
decl_stmt|;
name|String
name|intermediatePath
init|=
literal|null
decl_stmt|;
comment|// EXERCISE: use both methods to create a new user. what's the effect?
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
name|password
argument_list|,
name|principal
argument_list|,
name|intermediatePath
argument_list|)
expr_stmt|;
comment|//testUser = userManager.createUser(userID password);
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: use the following parameters (with suitable values) to create a new group.
comment|// EXERCISE: play with the values. what are valid values? which params can be null? what is the effect?
name|String
name|groupID
init|=
literal|null
decl_stmt|;
name|Principal
name|principal
init|=
literal|null
decl_stmt|;
name|String
name|intermediatePath
init|=
literal|null
decl_stmt|;
comment|// EXERCISE: use both methods to create a new group. what's the effect?
name|testGroup
operator|=
name|userManager
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
name|principal
argument_list|,
name|intermediatePath
argument_list|)
expr_stmt|;
comment|//        testGroup = userManager.createGroup(groupID);
comment|//        testGroup = userManager.createGroup(principal);
comment|//        testGroup = userManager.createGroup(principal, intermediatePath);
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testGetAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"testUser"
argument_list|,
literal|null
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"testPrincipal"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testGroup
operator|=
name|userManager
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"testGroupPrincipal"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// EXERCISE: use all methods provided on UserManager interface to retrieve a given user/group.
comment|// - lookup by id
comment|// - lookup by path
comment|// - lookup by principal
comment|// - lookup by id + class
block|}
block|}
end_class

end_unit
