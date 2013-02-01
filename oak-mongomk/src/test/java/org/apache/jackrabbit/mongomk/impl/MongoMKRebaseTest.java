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
name|mongomk
operator|.
name|impl
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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
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

begin_comment
comment|/**  * Tests for {@code MicroKernel#rebase}  * FIXME: this is copied from MicroKernelImplTest. Factor out.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKRebaseTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|rebaseWithoutChanges
parameter_list|()
block|{
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|branch
argument_list|,
name|rebased
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fastForwardRebase
parameter_list|()
block|{
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/a\":{}"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|branch
argument_list|,
name|rebased
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseEmptyBranch
parameter_list|()
block|{
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"a\":{}}"
argument_list|,
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"a\":{}}"
argument_list|,
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|trunk
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
name|trunk
operator|.
name|equals
argument_list|(
name|rebased
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseAddNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x/b\":{}"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x/a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/a"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/b"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
name|rebased
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/a"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/b"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
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
name|rebaseRemoveNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"-\"/x/y\""
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x/a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/a"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/x"
argument_list|,
name|rebased
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/a"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
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
name|rebaseAddProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":42"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/q\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rebasedNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|trunkNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseRemoveProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":null"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/q\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rebasedNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|trunkNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseChangeProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":41"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/q\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":41"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rebasedNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|rebased
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"p\":41"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|trunkNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"q\":99"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseChangePropertyWithSameValue
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rebased
init|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rebasedNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rebasedNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|trunkNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseAddExistingNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x/a\":{}"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x/a\":{\"b\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/x/a/b"
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"addExistingNode\":{\":childNodeCount\":1,\"a\":{\":childNodeCount\":0}}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseAddExistingProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":42 ^\"/x/y/q\":42"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99 ^\"/x/y/q\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"addExistingProperty\":{\"q\":42,\"p\":42,\":childNodeCount\":0}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseChangeRemovedProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":null"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"changeDeletedProperty\":{\"p\":99,\":childNodeCount\":0}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseRemoveChangedProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":null"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"deleteChangedProperty\":{\"p\":42,\":childNodeCount\":0}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseChangedChangedProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":41"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":99"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":99"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"changeChangedProperty\":{\"p\":41,\":childNodeCount\":0}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseRemoveChangedNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"-\"/x/y\""
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":42"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"deleteChangedNode\":{\":childNodeCount\":1,\"y\":{\":childNodeCount\":0}}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseChangeRemovedNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/p\":42"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"-\"/x\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/x"
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"changeDeletedNode\":{\":childNodeCount\":1,\"x\":{\"p\":42,\""
operator|+
literal|":childNodeCount\":1,\"y\":{\":childNodeCount\":0}}}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseRemoveRemovedProperty
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{\"p\":42}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":null"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/y/p\":null"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/y/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"deleteDeletedProperty\":{\"p\":42,\":childNodeCount\":0}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebaseRemoveRemovedNode
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"-\"/x/y\""
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"-\"/x/y\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/x/y"
argument_list|,
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflict
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x/:conflict"
argument_list|,
name|branch
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\":childNodeCount\":1,\"deleteDeletedNode\":{\":childNodeCount\":1,\"y\":{\":childNodeCount\":0}}}"
argument_list|,
name|conflict
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mergeRebased
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/x\":{\"y\":{}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branch
init|=
name|mk
operator|.
name|branch
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|trunk
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/p\":42"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|branch
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"^\"/x/q\":43"
argument_list|,
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branch
operator|=
name|mk
operator|.
name|rebase
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|branchNode
operator|.
name|contains
argument_list|(
literal|"\"q\":43"
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|merge
argument_list|(
name|branch
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|trunkNode
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/x"
argument_list|,
name|branch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"p\":42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|trunkNode
operator|.
name|contains
argument_list|(
literal|"\"q\":43"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

