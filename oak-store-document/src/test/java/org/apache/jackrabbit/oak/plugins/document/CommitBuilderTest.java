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
name|NodeState
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
name|hamcrest
operator|.
name|core
operator|.
name|StringContains
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
name|assertNull
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
name|assertThat
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
name|CommitBuilderTest
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
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|ns
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|empty
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|builder
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CommitBuilder
operator|.
name|PSEUDO_COMMIT_REVISION
argument_list|,
name|builder
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"CommitBuilder.build() must fail when the builder "
operator|+
literal|"was created without a commit revision"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|Revision
name|r
init|=
name|ns
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|getModifiedPaths
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyWithBaseRevision
parameter_list|()
block|{
name|RevisionVector
name|baseRev
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
name|baseRev
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|baseRev
argument_list|,
name|builder
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|baseRev
argument_list|,
name|c
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|buildWithNullRevision
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|build
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNode
parameter_list|()
block|{
name|RevisionVector
name|baseRev
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
name|baseRev
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNode
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeTwice
parameter_list|()
block|{
name|RevisionVector
name|baseRev
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
name|baseRev
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNode
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
try|try
block|{
name|builder
operator|.
name|addNode
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Must fail with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"already added"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodePathNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|addNode
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeStateNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|addNode
argument_list|(
operator|(
name|DocumentNodeState
operator|)
literal|null
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeState
parameter_list|()
block|{
name|String
name|path
init|=
literal|"/foo"
decl_stmt|;
name|DocumentNodeState
name|foo
init|=
name|addNode
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNode
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateOp
name|up
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|UpdateOp
operator|.
name|Operation
name|op
init|=
name|up
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|UpdateOp
operator|.
name|Key
argument_list|(
literal|"_deleted"
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|branchCommit
parameter_list|()
block|{
name|RevisionVector
name|baseRev
init|=
name|ns
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|update
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
operator|.
name|asBranchRevision
argument_list|()
argument_list|)
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
name|baseRev
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNode
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|Revision
name|commitRev
init|=
name|ns
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|commitRev
argument_list|)
decl_stmt|;
name|UpdateOp
name|up
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|UpdateOp
operator|.
name|Operation
name|op
init|=
name|up
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|UpdateOp
operator|.
name|Key
argument_list|(
literal|"_bc"
argument_list|,
name|commitRev
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNode
parameter_list|()
block|{
name|DocumentNodeState
name|bar
init|=
name|addNode
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/bar"
decl_stmt|;
name|builder
operator|.
name|removeNode
argument_list|(
name|path
argument_list|,
name|bar
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateOp
name|up
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|UpdateOp
operator|.
name|Operation
name|op
init|=
name|up
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|UpdateOp
operator|.
name|Key
argument_list|(
literal|"_deleted"
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeTwice
parameter_list|()
block|{
name|DocumentNodeState
name|bar
init|=
name|addNode
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/bar"
decl_stmt|;
name|builder
operator|.
name|removeNode
argument_list|(
name|path
argument_list|,
name|bar
argument_list|)
expr_stmt|;
try|try
block|{
name|builder
operator|.
name|removeNode
argument_list|(
name|path
argument_list|,
name|bar
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Must throw DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodePathNull
parameter_list|()
block|{
name|DocumentNodeState
name|bar
init|=
name|addNode
argument_list|(
literal|"bar"
argument_list|)
decl_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|removeNode
argument_list|(
literal|null
argument_list|,
name|bar
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeStateNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|removeNode
argument_list|(
literal|"/bar"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateProperty
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|updateProperty
argument_list|(
literal|"/foo"
argument_list|,
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateOp
name|up
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|UpdateOp
operator|.
name|Operation
name|op
init|=
name|up
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|UpdateOp
operator|.
name|Key
argument_list|(
literal|"p"
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updatePropertyValueNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|updateProperty
argument_list|(
literal|"/foo"
argument_list|,
literal|"p"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|ns
operator|.
name|newRevision
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateOp
name|up
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|UpdateOp
operator|.
name|Operation
name|op
init|=
name|up
operator|.
name|getChanges
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|UpdateOp
operator|.
name|Key
argument_list|(
literal|"p"
argument_list|,
name|c
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updatePropertyPathNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|updateProperty
argument_list|(
literal|null
argument_list|,
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|updatePropertyPropertyNull
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|updateProperty
argument_list|(
literal|"/foo"
argument_list|,
literal|null
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|expectNPE
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
specifier|private
specifier|static
name|void
name|expectNPE
parameter_list|()
block|{
name|fail
argument_list|(
literal|"NullPointerException expected"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DocumentNodeState
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeBuilder
name|nb
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nb
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|ns
argument_list|,
name|nb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|child
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|DocumentNodeState
argument_list|)
expr_stmt|;
return|return
operator|(
name|DocumentNodeState
operator|)
name|child
return|;
block|}
block|}
end_class

end_unit

