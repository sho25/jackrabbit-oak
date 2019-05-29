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
name|List
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
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
name|ImmutableList
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
name|commons
operator|.
name|PathUtils
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|assertFalse
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
name|assertNotNull
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
comment|/**  * Test user/group creation with intermediate path parameter.  */
end_comment

begin_class
specifier|public
class|class
name|IntermediatePathTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Authorizable
name|createAuthorizable
parameter_list|(
name|boolean
name|createGroup
parameter_list|,
annotation|@
name|Nullable
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|createGroup
condition|)
block|{
return|return
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
name|id
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|id
argument_list|)
argument_list|,
name|intermediatePath
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|id
argument_list|)
argument_list|,
name|intermediatePath
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserNullPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupNullPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserEmptyPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|.
name|equals
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupEmptyPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|.
name|equals
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserRelativePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|+
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupRelativePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserAbsolutePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|+
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|+
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupAbsolutePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/a/b/c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserRootPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
operator|.
name|equals
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupRootPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|.
name|equals
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUserWrongRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGroupWrongRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidAbsolutePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
literal|"testNode"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidPaths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|,
literal|1
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"/testNode"
argument_list|,
literal|"/nonExisting"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|absPath
range|:
name|invalidPaths
control|)
block|{
try|try
block|{
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
name|absPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid path "
operator|+
name|absPath
operator|+
literal|" outside of configured scope."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAbsolutePathsWithParentElements
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/a/../b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAbsolutePathsWithInvalidParentElements
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|invalidPath
init|=
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
operator|+
literal|"/../a"
decl_stmt|;
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|true
argument_list|,
name|invalidPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|,
literal|1
argument_list|)
operator|+
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid path "
operator|+
name|invalidPath
operator|+
literal|" outside of configured scope."
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
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRelativePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|,
literal|"testNode"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidPaths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|".."
argument_list|,
literal|"../.."
argument_list|,
literal|"../../.."
argument_list|,
literal|"../../../testNode"
argument_list|,
literal|"a/b/../../../c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|invalidPaths
control|)
block|{
try|try
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
name|relPath
argument_list|)
decl_stmt|;
comment|// NOTE: requires commit to detect the violation
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid path "
operator|+
name|relPath
operator|+
literal|" outside of configured scope."
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
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
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
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCurrentRelativePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Authorizable
name|authorizable
init|=
name|createAuthorizable
argument_list|(
literal|false
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

