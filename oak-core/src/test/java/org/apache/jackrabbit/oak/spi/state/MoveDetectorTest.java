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
name|spi
operator|.
name|state
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
name|assertTrue
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Maps
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
name|memory
operator|.
name|EmptyNodeState
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
name|DefaultMoveValidator
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
name|EditorDiff
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|MoveDetectorTest
block|{
specifier|private
name|NodeState
name|root
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|test
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|test
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
operator|.
name|child
argument_list|(
literal|"zz"
argument_list|)
expr_stmt|;
name|root
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test whether we can detect a single move      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|simpleMove
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|moved1
init|=
name|move
argument_list|(
name|root
operator|.
name|builder
argument_list|()
argument_list|,
literal|"/test/x"
argument_list|,
literal|"/test/y/xx"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation1
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/test/x"
argument_list|,
literal|"/test/y/xx"
argument_list|)
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector1
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation1
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception1
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector1
argument_list|,
name|root
argument_list|,
name|moved1
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception1
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception1
throw|;
block|}
name|moveExpectation1
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
comment|// Test whether we can also detect the move back on top of the previous, persisted move
name|NodeState
name|moved2
init|=
name|move
argument_list|(
name|moved1
operator|.
name|builder
argument_list|()
argument_list|,
literal|"/test/y/xx"
argument_list|,
literal|"/test/x"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation2
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/test/y/xx"
argument_list|,
literal|"/test/x"
argument_list|)
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector2
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation2
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception2
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector2
argument_list|,
name|moved1
argument_list|,
name|moved2
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception2
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception2
throw|;
block|}
name|moveExpectation2
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
block|}
comment|/**      * Moving a moved node is reported as a single move from the original source      * to the final destination.      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|moveMoved
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/x"
argument_list|,
literal|"/test/y/xx"
argument_list|)
expr_stmt|;
name|NodeState
name|moved
init|=
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/y/xx"
argument_list|,
literal|"/test/z/xxx"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/test/x"
argument_list|,
literal|"/test/z/xxx"
argument_list|)
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector
argument_list|,
name|root
argument_list|,
name|moved
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|moveExpectation
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
block|}
comment|/**      * Moving a transiently added node doesn't generate a move event      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|moveAddedNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"added"
argument_list|)
expr_stmt|;
name|NodeState
name|moved
init|=
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/added"
argument_list|,
literal|"/test/y/added"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector
argument_list|,
name|root
argument_list|,
name|moved
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|moveExpectation
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
block|}
comment|/**      * Moving a node from a moved subtree doesn't generate a move event.      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|moveFromMovedSubtree
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/z"
argument_list|,
literal|"/test/y/z"
argument_list|)
expr_stmt|;
name|NodeState
name|moved
init|=
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/y/z/zz"
argument_list|,
literal|"/test/x/zz"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/test/z"
argument_list|,
literal|"/test/y/z"
argument_list|)
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector
argument_list|,
name|root
argument_list|,
name|moved
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|moveExpectation
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
block|}
comment|/**      * Moving a node forth and back again should not generate a move event.      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|moveForthAndBack
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/x"
argument_list|,
literal|"/test/y/xx"
argument_list|)
expr_stmt|;
name|NodeState
name|moved
init|=
name|move
argument_list|(
name|rootBuilder
argument_list|,
literal|"/test/y/xx"
argument_list|,
literal|"/test/x"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MoveExpectation
name|moveExpectation
init|=
operator|new
name|MoveExpectation
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|moveExpectation
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|moveDetector
argument_list|,
name|root
argument_list|,
name|moved
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|moveExpectation
operator|.
name|assertAllFound
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|NodeBuilder
name|move
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
block|{
name|NodeBuilder
name|sourceBuilder
init|=
name|getBuilder
argument_list|(
name|builder
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|NodeBuilder
name|destParentBuilder
init|=
name|getBuilder
argument_list|(
name|builder
argument_list|,
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|dest
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sourceBuilder
operator|.
name|moveTo
argument_list|(
name|destParentBuilder
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|dest
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getBuilder
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
class|class
name|MoveExpectation
extends|extends
name|DefaultMoveValidator
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moves
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|MoveExpectation
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moves
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|moves
operator|=
name|moves
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|MoveExpectation
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moves
parameter_list|)
block|{
name|this
argument_list|(
name|Maps
operator|.
name|newHashMap
argument_list|(
name|moves
argument_list|)
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|sourcePath
parameter_list|,
name|NodeState
name|moved
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|actualDestPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|String
name|expectedDestPath
init|=
name|moves
operator|.
name|remove
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected move. "
operator|+
literal|"Expected: "
operator|+
operator|(
name|expectedDestPath
operator|==
literal|null
condition|?
literal|"None"
else|:
literal|'>'
operator|+
name|sourcePath
operator|+
literal|':'
operator|+
name|expectedDestPath
operator|)
operator|+
literal|" Found:>"
operator|+
name|sourcePath
operator|+
literal|':'
operator|+
name|actualDestPath
argument_list|,
name|expectedDestPath
argument_list|,
name|actualDestPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MoveValidator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|new
name|MoveExpectation
argument_list|(
name|moves
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|assertAllFound
parameter_list|()
block|{
name|assertTrue
argument_list|(
literal|"Missing moves: "
operator|+
name|moves
argument_list|,
name|moves
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

