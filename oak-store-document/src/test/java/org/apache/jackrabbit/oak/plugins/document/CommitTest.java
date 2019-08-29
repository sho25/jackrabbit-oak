begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|plugins
operator|.
name|document
package|;
end_package

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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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

begin_class
specifier|public
class|class
name|CommitTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testModifiedTime
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
literal|10003
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
literal|12000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
literal|15006
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OAK-3733
annotation|@
name|Test
specifier|public
name|void
name|detectConflict
parameter_list|()
throws|throws
name|Exception
block|{
comment|// prepare node store
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|b
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|b
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// this commit should fail
name|Commit
name|c
init|=
name|ns
operator|.
name|newCommit
argument_list|(
name|changes
lambda|->
block|{
name|changes
operator|.
name|addNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo/baz"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"p"
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
try|try
block|{
name|c
operator|.
name|apply
argument_list|()
expr_stmt|;
name|ns
operator|.
name|done
argument_list|(
name|c
argument_list|,
literal|false
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConflictException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
finally|finally
block|{
name|ns
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|// try to re-create /foo
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|b
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
comment|// OAK-4321
annotation|@
name|Test
specifier|public
name|void
name|mergeExceptionMessage
parameter_list|()
throws|throws
name|Exception
block|{
comment|// prepare node store
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|b
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|b
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// this commit should fail
name|Commit
name|c
init|=
name|ns
operator|.
name|newCommit
argument_list|(
name|changes
lambda|->
block|{
name|changes
operator|.
name|addNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|apply
argument_list|()
expr_stmt|;
name|ns
operator|.
name|done
argument_list|(
name|c
argument_list|,
literal|false
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"commit must fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConflictException
name|e
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
literal|"Unexpected exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"older than base"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ns
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// OAK-4894
annotation|@
name|Test
specifier|public
name|void
name|branchCommitFails
parameter_list|()
throws|throws
name|Exception
block|{
comment|// prepare node store
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// this branch commit must fail with a DocumentStoreException
name|Commit
name|c
init|=
name|ns
operator|.
name|newCommit
argument_list|(
name|changes
lambda|->
block|{
name|changes
operator|.
name|removeNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|asBranchRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
try|try
block|{
name|c
operator|.
name|apply
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"commit must fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConflictException
name|e
parameter_list|)
block|{
comment|// expected
name|assertTrue
argument_list|(
literal|"Unexpected exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ns
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// OAK-8585
annotation|@
name|Test
specifier|public
name|void
name|alreadyDeletedMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|ns
operator|.
name|newCommit
argument_list|(
name|changes
lambda|->
block|{
name|changes
operator|.
name|removeNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|asBranchRevision
argument_list|(
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
try|try
block|{
name|c
operator|.
name|apply
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"commit must fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConflictException
name|e
parameter_list|)
block|{
comment|// expected
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"base revision"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"branch"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ns
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

