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
name|AuthorizableExistsException
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
name|test
operator|.
name|NotExecutableException
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
comment|/**  * Tests for {@code Group} creation.  */
end_comment

begin_class
specifier|public
class|class
name|CreateGroupTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
name|List
argument_list|<
name|Authorizable
argument_list|>
name|createdGroups
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// remove all created groups again
for|for
control|(
name|Authorizable
name|createdGroup
range|:
name|createdGroups
control|)
block|{
try|try
block|{
name|createdGroup
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
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Group
name|createGroup
parameter_list|(
name|Principal
name|p
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Group
name|gr
init|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|gr
return|;
block|}
specifier|private
name|Group
name|createGroup
parameter_list|(
name|Principal
name|p
parameter_list|,
name|String
name|iPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Group
name|gr
init|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|p
argument_list|,
name|iPath
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|gr
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|Group
name|gr
init|=
name|createGroup
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|gr
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|gr
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"A new group must not have members."
argument_list|,
name|gr
operator|.
name|getMembers
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since OAK 1.0 In contrast to Jackrabbit core the intermediate path may      * not be an absolute path in OAK.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithAbsolutePath
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
try|try
block|{
name|Group
name|gr
init|=
name|createGroup
argument_list|(
name|p
argument_list|,
literal|"/any/path/to/the/new/group"
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConstraintViolationException expected."
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithAbsolutePath2
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|String
name|groupRoot
init|=
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
decl_stmt|;
name|String
name|path
init|=
name|groupRoot
operator|+
literal|"/any/path/to/the/new/group"
decl_stmt|;
name|Group
name|gr
init|=
name|createGroup
argument_list|(
name|p
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithRelativePath
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|Group
name|gr
init|=
name|createGroup
argument_list|(
name|p
argument_list|,
literal|"any/path"
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|gr
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gr
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
literal|"any/path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithNullPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Group
name|gr
init|=
name|createGroup
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A Group cannot be built from 'null' Principal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|Group
name|gr
init|=
name|createGroup
argument_list|(
literal|null
argument_list|,
literal|"/any/path/to/the/new/group"
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A Group cannot be built from 'null' Principal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDuplicateGroup
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|p
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|Group
name|gr
init|=
name|createGroup
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr
argument_list|)
expr_stmt|;
try|try
block|{
name|Group
name|gr2
init|=
name|createGroup
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|createdGroups
operator|.
name|add
argument_list|(
name|gr2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating 2 groups with the same Principal should throw AuthorizableExistsException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizableExistsException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
block|}
end_class

end_unit

