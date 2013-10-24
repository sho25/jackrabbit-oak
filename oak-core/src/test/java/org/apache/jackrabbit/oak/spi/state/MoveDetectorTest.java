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
name|PropertyState
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
name|moved
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
name|FindSingleMove
name|findSingleMove
init|=
operator|new
name|FindSingleMove
argument_list|(
literal|"/test/x"
argument_list|,
literal|"/test/y/xx"
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|findSingleMove
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
name|assertTrue
argument_list|(
name|findSingleMove
operator|.
name|found
argument_list|()
argument_list|)
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
name|FindSingleMove
name|findSingleMove
init|=
operator|new
name|FindSingleMove
argument_list|(
literal|"/test/x"
argument_list|,
literal|"/test/z/xxx"
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|findSingleMove
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
name|assertTrue
argument_list|(
name|findSingleMove
operator|.
name|found
argument_list|()
argument_list|)
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
name|AssertNoMove
name|assertNoMove
init|=
operator|new
name|AssertNoMove
argument_list|()
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|assertNoMove
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
name|FindSingleMove
name|findSingleMove
init|=
operator|new
name|FindSingleMove
argument_list|(
literal|"/test/z"
argument_list|,
literal|"/test/y/z"
argument_list|)
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|findSingleMove
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
name|assertTrue
argument_list|(
name|findSingleMove
operator|.
name|found
argument_list|()
argument_list|)
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
name|AssertNoMove
name|assertNoMove
init|=
operator|new
name|AssertNoMove
argument_list|()
decl_stmt|;
name|MoveDetector
name|moveDetector
init|=
operator|new
name|MoveDetector
argument_list|(
name|assertNoMove
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
name|FindSingleMove
implements|implements
name|MoveValidator
block|{
specifier|private
specifier|final
name|String
name|sourcePath
decl_stmt|;
specifier|private
specifier|final
name|String
name|destPath
decl_stmt|;
specifier|private
name|boolean
name|found
decl_stmt|;
specifier|private
name|FindSingleMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|this
operator|.
name|sourcePath
operator|=
name|sourcePath
expr_stmt|;
name|this
operator|.
name|destPath
operator|=
name|destPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|,
name|NodeState
name|moved
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|found
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Test"
argument_list|,
literal|0
argument_list|,
literal|"There should only be a single move operation"
argument_list|)
throw|;
block|}
name|assertEquals
argument_list|(
name|this
operator|.
name|sourcePath
argument_list|,
name|sourcePath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|destPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|MoveValidator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
literal|null
return|;
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
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|MoveValidator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|found
parameter_list|()
block|{
return|return
name|found
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AssertNoMove
implements|implements
name|MoveValidator
block|{
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|,
name|NodeState
name|moved
parameter_list|)
throws|throws
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Test"
argument_list|,
literal|0
argument_list|,
literal|"There should be no move operation"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|MoveValidator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
literal|null
return|;
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
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|MoveValidator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

