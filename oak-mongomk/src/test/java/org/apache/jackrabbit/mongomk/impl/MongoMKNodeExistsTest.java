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

begin_comment
comment|/**  * Tests for nodeExists.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKNodeExistsTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|String
name|revisionId
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|revisionId
operator|=
name|scenario
operator|.
name|deleteA
argument_list|()
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withoutRevisionId
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|scenario
operator|.
name|deleteA
argument_list|()
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withInvalidRevisionId
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
try|try
block|{
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|"123456789"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected: Invalid revision id exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|parentDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|scenario
operator|.
name|deleteA
argument_list|()
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|grandParentDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : { \"c\" : { \"d\" : {} } } }"
argument_list|,
literal|null
argument_list|,
literal|"Add /a/b/c/d"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"-\"b\""
argument_list|,
literal|null
argument_list|,
literal|"Remove /b"
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|existsInHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|"Add /a"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"b\" : {}"
argument_list|,
literal|null
argument_list|,
literal|"Add /a/b"
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The node a is not found in the head revision!"
argument_list|,
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|existsInOldRevNotInNewRev
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|String
name|rev1
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|rev2
init|=
name|scenario
operator|.
name|deleteA
argument_list|()
decl_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
name|rev1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
name|rev2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|siblingDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|scenario
operator|.
name|deleteB
argument_list|()
expr_stmt|;
name|boolean
name|exists
init|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
name|exists
operator|=
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

