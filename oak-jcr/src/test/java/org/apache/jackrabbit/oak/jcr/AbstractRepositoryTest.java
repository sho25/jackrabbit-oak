begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|commons
operator|.
name|CIHelper
operator|.
name|buildBotLinuxTrunk
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|Collection
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|JackrabbitRepository
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
name|commons
operator|.
name|FixturesHelper
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
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
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
name|query
operator|.
name|QueryEngineSettings
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
name|EveryonePrincipal
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
name|state
operator|.
name|NodeStore
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Abstract base class for repository tests providing methods for accessing  * the repository, a session and nodes and properties from that session.  *  * Users of this class must call clear to close the session associated with  * this instance and clean up the repository when done.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parallelized
operator|.
name|class
argument_list|)
annotation|@
name|Ignore
argument_list|(
literal|"This abstract base class does not have any tests"
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractRepositoryTest
block|{
specifier|protected
specifier|final
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Session
name|adminSession
decl_stmt|;
comment|/**      * The system property "nsfixtures" can be used to provide a      * whitespace-separated list of fixtures names for which the      * tests should be run (the default is to use all fixtures).      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Fixture
argument_list|>
name|FIXTURES
init|=
name|FixturesHelper
operator|.
name|getFixtures
argument_list|()
decl_stmt|;
specifier|protected
name|AbstractRepositoryTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_MK
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|NodeStoreFixture
operator|.
name|DOCUMENT_MK
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_NS
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|NodeStoreFixture
operator|.
name|DOCUMENT_NS
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|SEGMENT_MK
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|NodeStoreFixture
operator|.
name|SEGMENT_MK
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_RDB
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|NodeStoreFixture
operator|.
name|DOCUMENT_RDB
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|checkAssumptions
parameter_list|()
block|{
comment|// FIXME OAK-2379. Don't run the tests for now on the Linux BuildBot for DOCUMENT_RDB
name|assumeTrue
argument_list|(
operator|!
name|buildBotLinuxTrunk
argument_list|()
operator|||
name|fixture
operator|!=
name|NodeStoreFixture
operator|.
name|DOCUMENT_RDB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|logout
parameter_list|()
block|{
comment|// release session field
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
name|adminSession
operator|=
literal|null
expr_stmt|;
block|}
comment|// release repository field
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|repository
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|nodeStore
operator|!=
literal|null
condition|)
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Repository
name|getRepository
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|nodeStore
operator|=
name|createNodeStore
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
name|repository
operator|=
name|createRepository
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
return|return
name|repository
return|;
block|}
specifier|protected
name|NodeStore
name|createNodeStore
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|fixture
operator|.
name|createNodeStore
argument_list|()
return|;
block|}
specifier|protected
name|Repository
name|createRepository
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
return|return
name|initJcr
argument_list|(
operator|new
name|Jcr
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
return|;
block|}
specifier|protected
name|Jcr
name|initJcr
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
name|QueryEngineSettings
name|qs
init|=
operator|new
name|QueryEngineSettings
argument_list|()
decl_stmt|;
name|qs
operator|.
name|setFullTextComparisonWithoutIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|jcr
operator|.
name|withAsyncIndexing
argument_list|()
operator|.
name|with
argument_list|(
name|qs
argument_list|)
return|;
block|}
specifier|protected
name|Session
name|getAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|adminSession
operator|==
literal|null
condition|)
block|{
name|adminSession
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
block|}
return|return
name|adminSession
return|;
block|}
specifier|protected
name|Session
name|createAnonymousSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|admin
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|admin
argument_list|,
literal|"/"
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Session
name|createAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getAdminCredentials
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|SimpleCredentials
name|getAdminCredentials
parameter_list|()
block|{
return|return
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
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|Repository
parameter_list|>
name|R
name|dispose
parameter_list|(
name|R
name|repository
parameter_list|)
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

