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
name|random
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|commons
operator|.
name|JcrUtils
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
name|Jcr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import static
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
name|AbstractRepositoryTest
operator|.
name|dispose
import|;
end_import

begin_comment
comment|/**  * Base class for randomized tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRandomizedTest
block|{
specifier|private
name|Repository
name|jackrabbitRepository
decl_stmt|;
specifier|private
name|Repository
name|oakRepository
decl_stmt|;
specifier|protected
name|String
name|userId
init|=
literal|"testuser"
decl_stmt|;
specifier|protected
name|String
index|[]
name|ids
init|=
operator|new
name|String
index|[]
block|{
name|userId
block|,
literal|"group1"
block|,
literal|"group2"
block|}
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|JackrabbitSession
argument_list|>
name|writeSessions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Session
argument_list|>
name|readSessions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|jackrabbitRepository
operator|=
name|JcrUtils
operator|.
name|getRepository
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"jackrabbit"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|oakRepository
operator|=
operator|new
name|Jcr
argument_list|()
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|writeSessions
operator|.
name|add
argument_list|(
operator|(
name|JackrabbitSession
operator|)
name|jackrabbitRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeSessions
operator|.
name|add
argument_list|(
operator|(
name|JackrabbitSession
operator|)
name|oakRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setupAuthorizables
argument_list|()
expr_stmt|;
name|setupContent
argument_list|()
expr_stmt|;
name|readSessions
operator|.
name|add
argument_list|(
name|jackrabbitRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|readSessions
operator|.
name|add
argument_list|(
name|oakRepository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|clearContent
argument_list|()
expr_stmt|;
name|clearAuthorizables
argument_list|()
expr_stmt|;
for|for
control|(
name|JackrabbitSession
name|s
range|:
name|writeSessions
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Session
name|s
range|:
name|readSessions
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
name|jackrabbitRepository
operator|=
name|dispose
argument_list|(
name|jackrabbitRepository
argument_list|)
expr_stmt|;
name|oakRepository
operator|=
name|dispose
argument_list|(
name|oakRepository
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Principal
name|getTestPrincipal
parameter_list|(
annotation|@
name|Nonnull
name|JackrabbitSession
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|session
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|)
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
specifier|protected
name|Principal
name|getPrincipal
parameter_list|(
annotation|@
name|Nonnull
name|JackrabbitSession
name|session
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|session
operator|.
name|getPrincipalManager
argument_list|()
operator|.
name|getPrincipal
argument_list|(
name|ids
index|[
name|index
index|]
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setupAuthorizables
parameter_list|()
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|JackrabbitSession
name|s
range|:
name|writeSessions
control|)
block|{
name|UserManager
name|userManager
init|=
name|s
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|User
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
name|userId
argument_list|,
name|userId
argument_list|)
decl_stmt|;
name|Group
name|group
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
literal|"group1"
argument_list|)
decl_stmt|;
name|group
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Group
name|group2
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
literal|"group2"
argument_list|)
decl_stmt|;
name|group2
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|clearAuthorizables
parameter_list|()
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|JackrabbitSession
name|s
range|:
name|writeSessions
control|)
block|{
name|UserManager
name|userManager
init|=
name|s
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|void
name|setupContent
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|void
name|clearContent
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

