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
name|Collections
import|;
end_import

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
name|List
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
name|AbstractOakTest
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|ContentRepository
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
name|api
operator|.
name|ContentSession
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
name|api
operator|.
name|Root
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
name|api
operator|.
name|Tree
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
name|core
operator|.
name|DefaultConflictHandler
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
name|UserConfig
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
name|UserConstants
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
name|UserProvider
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
name|action
operator|.
name|AuthorizableAction
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
name|Test
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * UserProviderImplTest...  *  * TODO: create tests with custom config that persists changes (currently fails since config used in UserValidator is different)  * TODO: add tests for setProtectedProperty (might still be refactored...)  */
end_comment

begin_class
specifier|public
class|class
name|UserProviderImplTest
extends|extends
name|AbstractOakTest
block|{
specifier|private
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|UserConfig
name|defaultConfig
decl_stmt|;
specifier|private
name|String
name|defaultUserPath
decl_stmt|;
specifier|private
name|String
name|defaultGroupPath
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|customOptions
decl_stmt|;
specifier|private
name|String
name|customUserPath
init|=
literal|"/home/users"
decl_stmt|;
specifier|private
name|String
name|customGroupPath
init|=
literal|"/home/groups"
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|cleanupPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|contentSession
operator|=
name|createAdminSession
argument_list|()
expr_stmt|;
name|root
operator|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
expr_stmt|;
name|defaultConfig
operator|=
operator|new
name|UserConfig
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|defaultUserPath
operator|=
name|defaultConfig
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_USER_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
name|defaultGroupPath
operator|=
name|defaultConfig
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
expr_stmt|;
name|customOptions
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|customOptions
operator|.
name|put
argument_list|(
name|UserConfig
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|customGroupPath
argument_list|)
expr_stmt|;
name|customOptions
operator|.
name|put
argument_list|(
name|UserConfig
operator|.
name|PARAM_USER_PATH
argument_list|,
name|customUserPath
argument_list|)
expr_stmt|;
name|cleanupPaths
operator|.
name|add
argument_list|(
name|defaultUserPath
argument_list|)
expr_stmt|;
name|cleanupPaths
operator|.
name|add
argument_list|(
name|defaultGroupPath
argument_list|)
expr_stmt|;
name|cleanupPaths
operator|.
name|add
argument_list|(
name|customUserPath
argument_list|)
expr_stmt|;
name|cleanupPaths
operator|.
name|add
argument_list|(
name|customGroupPath
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
for|for
control|(
name|String
name|path
range|:
name|cleanupPaths
control|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
return|return
name|createEmptyRepository
argument_list|()
return|;
block|}
specifier|private
name|UserProvider
name|createUserProvider
parameter_list|()
block|{
return|return
operator|new
name|UserProviderImpl
argument_list|(
name|contentSession
argument_list|,
name|root
argument_list|,
name|defaultConfig
argument_list|)
return|;
block|}
specifier|private
name|UserProvider
name|createUserProvider
parameter_list|(
name|int
name|defaultDepth
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|customOptions
argument_list|)
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
name|UserConfig
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|defaultDepth
argument_list|)
expr_stmt|;
return|return
operator|new
name|UserProviderImpl
argument_list|(
name|contentSession
argument_list|,
name|root
argument_list|,
operator|new
name|UserConfig
argument_list|(
literal|"admin"
argument_list|,
name|options
argument_list|,
name|Collections
operator|.
expr|<
name|AuthorizableAction
operator|>
name|emptySet
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
comment|// create test user
name|Tree
name|userTree
init|=
name|up
operator|.
name|createUser
argument_list|(
literal|"user1"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Text
operator|.
name|isDescendant
argument_list|(
name|defaultUserPath
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|level
init|=
name|defaultConfig
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_DEPTH
argument_list|)
operator|+
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultUserPath
argument_list|,
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|,
name|level
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure all users are created in a structure with default depth
name|userTree
operator|=
name|up
operator|.
name|createUser
argument_list|(
literal|"b"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultUserPath
operator|+
literal|"/b/bb/b"
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
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
name|m
operator|.
name|put
argument_list|(
literal|"bb"
argument_list|,
literal|"/b/bb/bb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bbb"
argument_list|,
literal|"/b/bb/bbb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bbbb"
argument_list|,
literal|"/b/bb/bbbb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bh"
argument_list|,
literal|"/b/bh/bh"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bHbh"
argument_list|,
literal|"/b/bH/bHbh"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"b_Hb"
argument_list|,
literal|"/b/b_/b_Hb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"basim"
argument_list|,
literal|"/b/ba/basim"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|uid
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|userTree
operator|=
name|up
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultUserPath
operator|+
name|m
operator|.
name|get
argument_list|(
name|uid
argument_list|)
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// create test user
name|Tree
name|userTree
init|=
name|up
operator|.
name|createUser
argument_list|(
literal|"nadine"
argument_list|,
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Text
operator|.
name|isDescendant
argument_list|(
name|customUserPath
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|userPath
init|=
name|customUserPath
operator|+
literal|"/a/b/c/nadine"
decl_stmt|;
name|assertEquals
argument_list|(
name|userPath
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Tree
name|groupTree
init|=
name|up
operator|.
name|createGroup
argument_list|(
literal|"group1"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|groupTree
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Text
operator|.
name|isDescendant
argument_list|(
name|defaultGroupPath
argument_list|,
name|groupTree
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|level
init|=
name|defaultConfig
operator|.
name|getConfigValue
argument_list|(
name|UserConfig
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_DEPTH
argument_list|)
operator|+
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultGroupPath
argument_list|,
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|groupTree
operator|.
name|getPath
argument_list|()
argument_list|,
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|// create test user
name|Tree
name|group
init|=
name|up
operator|.
name|createGroup
argument_list|(
literal|"authors"
argument_list|,
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Text
operator|.
name|isDescendant
argument_list|(
name|customGroupPath
argument_list|,
name|group
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|groupPath
init|=
name|customGroupPath
operator|+
literal|"/a/b/c/authors"
decl_stmt|;
name|assertEquals
argument_list|(
name|groupPath
argument_list|,
name|group
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateWithCustomDepth
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|userProvider
init|=
name|createUserProvider
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
literal|"b"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|customUserPath
operator|+
literal|"/b/bb/bbb/b"
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
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
name|m
operator|.
name|put
argument_list|(
literal|"bb"
argument_list|,
literal|"/b/bb/bbb/bb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bbb"
argument_list|,
literal|"/b/bb/bbb/bbb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bbbb"
argument_list|,
literal|"/b/bb/bbb/bbbb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bL"
argument_list|,
literal|"/b/bL/bLL/bL"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"bLbh"
argument_list|,
literal|"/b/bL/bLb/bLbh"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"b_Lb"
argument_list|,
literal|"/b/b_/b_L/b_Lb"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"basiL"
argument_list|,
literal|"/b/ba/bas/basiL"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|uid
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|userTree
operator|=
name|userProvider
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|customUserPath
operator|+
name|m
operator|.
name|get
argument_list|(
name|uid
argument_list|)
argument_list|,
name|userTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-270: UUID collisions are not yet detected upon commit."
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCreateWithCollision
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|userProvider
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Tree
name|userTree
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
literal|"AmaLia"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|colliding
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
name|colliding
operator|.
name|put
argument_list|(
literal|"AmaLia"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|colliding
operator|.
name|put
argument_list|(
literal|"AmaLia"
argument_list|,
literal|"s/ome/path"
argument_list|)
expr_stmt|;
name|colliding
operator|.
name|put
argument_list|(
literal|"amalia"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|colliding
operator|.
name|put
argument_list|(
literal|"Amalia"
argument_list|,
literal|"a/b/c"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|uid
range|:
name|colliding
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|Tree
name|c
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|colliding
operator|.
name|get
argument_list|(
name|uid
argument_list|)
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"userID collision must be detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
for|for
control|(
name|String
name|uid
range|:
name|colliding
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|Tree
name|c
init|=
name|userProvider
operator|.
name|createGroup
argument_list|(
name|uid
argument_list|,
name|colliding
operator|.
name|get
argument_list|(
name|uid
argument_list|)
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"userID collision must be detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIllegalChars
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|userProvider
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
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
name|m
operator|.
name|put
argument_list|(
literal|"z[x]"
argument_list|,
literal|"/z/"
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z["
argument_list|)
operator|+
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z[x]"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"z*x"
argument_list|,
literal|"/z/"
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z*"
argument_list|)
operator|+
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z*x"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"z/x"
argument_list|,
literal|"/z/"
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z/"
argument_list|)
operator|+
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"z/x"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"%\r|"
argument_list|,
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"%"
argument_list|)
operator|+
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"%\r"
argument_list|)
operator|+
literal|'/'
operator|+
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
literal|"%\r|"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|uid
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Tree
name|user
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultUserPath
operator|+
name|m
operator|.
name|get
argument_list|(
name|uid
argument_list|)
argument_list|,
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uid
argument_list|,
name|userProvider
operator|.
name|getAuthorizableId
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|ath
init|=
name|userProvider
operator|.
name|getAuthorizable
argument_list|(
name|uid
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Tree with id "
operator|+
name|uid
operator|+
literal|" must exist."
argument_list|,
name|ath
argument_list|)
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
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
literal|"hannah"
decl_stmt|;
name|String
name|groupID
init|=
literal|"cLevel"
decl_stmt|;
name|Tree
name|user
init|=
name|up
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|group
init|=
name|up
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|Tree
name|a
init|=
name|up
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|=
name|up
operator|.
name|getAuthorizable
argument_list|(
name|groupID
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
operator|.
name|getPath
argument_list|()
argument_list|,
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableWithType
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
literal|"thabit"
decl_stmt|;
name|Tree
name|user
init|=
name|up
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|Tree
name|a
init|=
name|up
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|up
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|up
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|groupID
init|=
literal|"hr"
decl_stmt|;
name|Tree
name|group
init|=
name|up
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|Tree
name|g
init|=
name|up
operator|.
name|getAuthorizable
argument_list|(
name|groupID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
name|a
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|up
operator|.
name|getAuthorizable
argument_list|(
name|groupID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|up
operator|.
name|getAuthorizable
argument_list|(
name|groupID
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableByPath
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Tree
name|user
init|=
name|up
operator|.
name|createUser
argument_list|(
literal|"shams"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|a
init|=
name|up
operator|.
name|getAuthorizableByPath
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|Tree
name|group
init|=
name|up
operator|.
name|createGroup
argument_list|(
literal|"devs"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|a
operator|=
name|up
operator|.
name|getAuthorizableByPath
argument_list|(
name|group
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|userProvider
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Tree
name|adminTree
init|=
name|userProvider
operator|.
name|createUser
argument_list|(
name|defaultConfig
operator|.
name|getAdminId
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|userProvider
operator|.
name|isAdminUser
argument_list|(
name|adminTree
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Tree
argument_list|>
name|others
init|=
operator|new
name|ArrayList
argument_list|<
name|Tree
argument_list|>
argument_list|()
decl_stmt|;
name|others
operator|.
name|add
argument_list|(
name|userProvider
operator|.
name|createUser
argument_list|(
literal|"laura"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|others
operator|.
name|add
argument_list|(
name|userProvider
operator|.
name|createGroup
argument_list|(
literal|"administrators"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|other
range|:
name|others
control|)
block|{
name|assertFalse
argument_list|(
name|userProvider
operator|.
name|isAdminUser
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableId
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
literal|"Amanda"
decl_stmt|;
name|Tree
name|user
init|=
name|up
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|userID
argument_list|,
name|up
operator|.
name|getAuthorizableId
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|groupID
init|=
literal|"visitors"
decl_stmt|;
name|Tree
name|group
init|=
name|up
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|groupID
argument_list|,
name|up
operator|.
name|getAuthorizableId
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveParentTree
parameter_list|()
throws|throws
name|Exception
block|{
name|UserProvider
name|up
init|=
name|createUserProvider
argument_list|()
decl_stmt|;
name|Tree
name|u1
init|=
name|up
operator|.
name|createUser
argument_list|(
literal|"b"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|Tree
name|u2
init|=
name|up
operator|.
name|createUser
argument_list|(
literal|"bb"
argument_list|,
literal|"bb"
argument_list|)
decl_stmt|;
name|Tree
name|folder
init|=
name|root
operator|.
name|getTree
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|u1
operator|.
name|getPath
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|folder
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|up
operator|.
name|getAuthorizable
argument_list|(
literal|"b"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Removing the top authorizable folder must remove all users contained."
argument_list|)
expr_stmt|;
name|u1
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|up
operator|.
name|getAuthorizable
argument_list|(
literal|"bb"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Removing the top authorizable folder must remove all users contained."
argument_list|)
expr_stmt|;
name|u2
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

