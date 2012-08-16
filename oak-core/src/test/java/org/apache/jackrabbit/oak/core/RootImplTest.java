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
name|core
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
name|List
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|CoreValue
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|RootImplTest
extends|extends
name|AbstractOakTest
block|{
annotation|@
name|Override
specifier|protected
name|NodeState
name|createInitialState
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|)
block|{
name|String
name|jsop
init|=
literal|"^\"a\":1 ^\"b\":2 ^\"c\":3 +\"x\":{\"xx\":{},\"xa\":\"value\"} +\"y\":{} +\"z\":{}"
decl_stmt|;
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|"test data"
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getTree
parameter_list|()
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|validPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|validPaths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|validPaths
operator|.
name|add
argument_list|(
literal|"/x"
argument_list|)
expr_stmt|;
name|validPaths
operator|.
name|add
argument_list|(
literal|"/x/xx"
argument_list|)
expr_stmt|;
name|validPaths
operator|.
name|add
argument_list|(
literal|"/y"
argument_list|)
expr_stmt|;
name|validPaths
operator|.
name|add
argument_list|(
literal|"/z"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|treePath
range|:
name|validPaths
control|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|treePath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|treePath
argument_list|,
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|invalidPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalidPaths
operator|.
name|add
argument_list|(
literal|"/any"
argument_list|)
expr_stmt|;
name|invalidPaths
operator|.
name|add
argument_list|(
literal|"/x/any"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|treePath
range|:
name|invalidPaths
control|)
block|{
name|assertNull
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|treePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|move
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|move
argument_list|(
literal|"/x"
argument_list|,
literal|"/y/xx"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|move2
parameter_list|()
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|r
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|x
init|=
name|r
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|r
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|x
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|move
argument_list|(
literal|"/x"
argument_list|,
literal|"/y/x"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"y"
argument_list|,
name|x
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**      * Regression test for OAK-208      */
specifier|public
name|void
name|removeMoved
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|r
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|r
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|r
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|root
operator|.
name|move
argument_list|(
literal|"/a"
argument_list|,
literal|"/b/c"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rename
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|move
argument_list|(
literal|"/x"
argument_list|,
literal|"/xx"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|copy
argument_list|(
literal|"/x"
argument_list|,
literal|"/y/xx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deepCopy
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"x1"
argument_list|)
expr_stmt|;
name|root
operator|.
name|copy
argument_list|(
literal|"/x"
argument_list|,
literal|"/y/xx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"x1"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"x1"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|x
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|Tree
name|xx
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|checkEqual
argument_list|(
name|x
argument_list|,
name|xx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rebase
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root1
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|RootImpl
name|root2
init|=
name|createRootImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|CoreValue
name|value
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"V1"
argument_list|)
decl_stmt|;
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"one"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"two"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"three"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|root2
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|root1
operator|.
name|rebase
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
operator|(
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|Tree
name|one
init|=
name|root2
operator|.
name|getTree
argument_list|(
literal|"/one"
argument_list|)
decl_stmt|;
name|one
operator|.
name|getChild
argument_list|(
literal|"two"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|one
operator|.
name|addChild
argument_list|(
literal|"four"
argument_list|)
expr_stmt|;
name|root2
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|root1
operator|.
name|rebase
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
operator|(
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkEqual
parameter_list|(
name|Tree
name|tree1
parameter_list|,
name|Tree
name|tree2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|tree1
operator|.
name|getChildrenCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tree1
operator|.
name|getPropertyCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property1
range|:
name|tree1
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|property1
argument_list|,
name|tree2
operator|.
name|getProperty
argument_list|(
name|property1
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child1
range|:
name|tree1
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|checkEqual
argument_list|(
name|child1
argument_list|,
name|tree2
operator|.
name|getChild
argument_list|(
name|child1
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

