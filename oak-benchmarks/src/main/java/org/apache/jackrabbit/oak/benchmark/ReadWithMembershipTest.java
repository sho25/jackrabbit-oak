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
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
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
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|internal
operator|.
name|SecurityProviderBuilder
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
name|SecurityProvider
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
name|authorization
operator|.
name|AuthorizationConfiguration
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

begin_class
specifier|public
class|class
name|ReadWithMembershipTest
extends|extends
name|ReadDeepTreeTest
block|{
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|int
name|membershipSize
decl_stmt|;
specifier|private
specifier|final
name|int
name|numberOfAces
decl_stmt|;
specifier|protected
name|ReadWithMembershipTest
parameter_list|(
name|int
name|itemsToRead
parameter_list|,
name|boolean
name|doReport
parameter_list|,
name|int
name|membershipSize
parameter_list|,
name|int
name|numberOfAces
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|,
name|itemsToRead
argument_list|,
name|doReport
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|userId
operator|=
literal|"user-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
expr_stmt|;
name|this
operator|.
name|membershipSize
operator|=
name|membershipSize
expr_stmt|;
name|this
operator|.
name|numberOfAces
operator|=
name|numberOfAces
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createDeepTree
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|createDeepTree
argument_list|()
expr_stmt|;
name|UserManager
name|userManager
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|membershipSize
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
literal|"group"
operator|+
name|i
argument_list|)
decl_stmt|;
name|g
operator|.
name|addMember
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|setupACEs
argument_list|(
name|g
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setupACEs
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|size
init|=
name|allPaths
operator|.
name|size
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
name|numberOfAces
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|size
operator|*
name|Math
operator|.
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Item
name|item
init|=
name|adminSession
operator|.
name|getItem
argument_list|(
name|allPaths
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
operator|(
name|item
operator|.
name|isNode
argument_list|()
operator|)
condition|?
operator|(
name|Node
operator|)
name|item
else|:
name|item
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|getAccessControllablePath
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|path
argument_list|,
name|principal
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getImportFileName
parameter_list|()
block|{
return|return
literal|"deepTree_everyone.xml"
return|;
block|}
specifier|protected
name|Session
name|getTestSession
parameter_list|()
block|{
name|SimpleCredentials
name|sc
init|=
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
decl_stmt|;
return|return
name|login
argument_list|(
name|sc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
literal|"eagerCacheSize"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SecurityProvider
name|securityProvider
init|=
operator|new
name|SecurityProviderBuilder
argument_list|()
operator|.
name|with
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|params
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|with
argument_list|(
name|securityProvider
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Fixture "
operator|+
name|fixture
operator|+
literal|" not supported for this benchmark."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

