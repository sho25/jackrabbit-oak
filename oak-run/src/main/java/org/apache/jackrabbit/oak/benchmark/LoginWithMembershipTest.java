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
name|benchmark
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Measure performance of repository login with the test user being direct or  * inherited member of a specified number of groups.  */
end_comment

begin_class
specifier|public
class|class
name|LoginWithMembershipTest
extends|extends
name|AbstractLoginTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NUMBER_OF_GROUPS_DEFAULT
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|int
name|numberOfGroups
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|nestedGroups
decl_stmt|;
specifier|public
name|LoginWithMembershipTest
parameter_list|(
name|boolean
name|runWithToken
parameter_list|,
name|int
name|noIterations
parameter_list|,
name|int
name|numberOfGroups
parameter_list|,
name|boolean
name|nestedGroups
parameter_list|)
block|{
name|super
argument_list|(
name|USER
argument_list|,
name|runWithToken
argument_list|,
name|noIterations
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfGroups
operator|=
name|numberOfGroups
expr_stmt|;
name|this
operator|.
name|nestedGroups
operator|=
name|nestedGroups
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|(
name|repository
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|Session
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
try|try
block|{
name|UserManager
name|userManager
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|Authorizable
name|user
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|USER
argument_list|)
decl_stmt|;
comment|// make sure we have a least a single group the user is member of.
name|Group
name|gr
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|GROUP
argument_list|)
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|gr
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numberOfGroups
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|g
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|GROUP
operator|+
name|i
argument_list|)
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|nestedGroups
condition|)
block|{
name|g
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|g
operator|.
name|addMember
argument_list|(
name|gr
argument_list|)
expr_stmt|;
block|}
name|gr
operator|=
name|g
expr_stmt|;
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Session
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
name|Authorizable
name|authorizable
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|GROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
name|Node
name|n
init|=
name|s
operator|.
name|getNode
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|s
operator|.
name|logout
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
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Repository
name|repository
init|=
name|getRepository
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|repository
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

