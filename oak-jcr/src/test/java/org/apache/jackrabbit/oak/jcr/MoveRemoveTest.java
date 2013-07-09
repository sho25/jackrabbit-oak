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
package|;
end_package

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
name|fail
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|MoveRemoveTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|MoveRemoveTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeExistingNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/new"
argument_list|,
name|n
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
name|removeNewNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/new"
argument_list|,
name|n
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
name|removeExistingNodeRefresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Node
name|n2
init|=
name|session2
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|n2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/new"
argument_list|,
name|n2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeExistingNodeParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/parent/new"
argument_list|,
name|n
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
name|removeNewNodeParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/parent/new"
argument_list|,
name|n
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
name|removeExistingNodeRefreshParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Node
name|n2
init|=
name|session2
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|n2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/parent/new"
argument_list|,
name|n2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|moveExistingNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/new"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/moved"
argument_list|,
name|n
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
name|moveNewNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/new"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/moved"
argument_list|,
name|n
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
name|moveExistingNodeRefresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Node
name|n2
init|=
name|session2
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/new"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|n2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/new"
argument_list|,
name|n2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|moveExistingParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|p
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/parent"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/moved"
argument_list|,
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/moved/new"
argument_list|,
name|n
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
name|moveNewNodeParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/parent"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/moved/new"
argument_list|,
name|n
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
name|moveExistingNodeRefreshParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Node
name|n2
init|=
name|session2
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/parent/new"
argument_list|)
decl_stmt|;
name|session
operator|.
name|move
argument_list|(
literal|"/parent"
argument_list|,
literal|"/moved"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|n2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{}
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/parent/new"
argument_list|,
name|n2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

