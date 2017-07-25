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
name|commit
package|;
end_package

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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|Oak
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
name|spi
operator|.
name|security
operator|.
name|OpenSecurityProvider
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultThreeWayConflictHandlerOursTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|OUR_VALUE
init|=
literal|"our value"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|THEIR_VALUE
init|=
literal|"their value"
decl_stmt|;
specifier|private
name|Root
name|ourRoot
decl_stmt|;
specifier|private
name|Root
name|theirRoot
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|ContentSession
name|session
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|DefaultThreeWayConflictHandler
operator|.
name|OURS
argument_list|)
operator|.
name|createContentSession
argument_list|()
decl_stmt|;
comment|// Add test content
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
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
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|theirRoot
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|ourRoot
operator|=
literal|null
expr_stmt|;
name|theirRoot
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddExistingProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|q
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"q"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeDeletedProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeChangedProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteDeletedProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteChangedProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|PropertyState
name|p
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddExistingNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|n
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/n"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeDeletedNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|OUR_VALUE
argument_list|)
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|n
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OUR_VALUE
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteChangedNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|THEIR_VALUE
argument_list|)
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|n
init|=
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteDeletedNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|theirRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|theirRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ourRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|ourRoot
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

