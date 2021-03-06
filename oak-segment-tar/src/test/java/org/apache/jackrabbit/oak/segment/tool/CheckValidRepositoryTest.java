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
name|segment
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Lists
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
comment|/**  * Tests for {@link Check} assuming a consistent repository.  */
end_comment

begin_class
specifier|public
class|class
name|CheckValidRepositoryTest
extends|extends
name|CheckRepositoryTestBase
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulFullCheckWithBinaryTraversal
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"Checked 7 nodes and 21 properties"
argument_list|,
literal|"Path / is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulOnlyRootKidsCheckWithBinaryTraversalAndFilterPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/a"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/b"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/d"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/e"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/f"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"Checked 1 nodes and 1 properties"
argument_list|,
literal|"Checked 1 nodes and 2 properties"
argument_list|,
literal|"Checked 1 nodes and 3 properties"
argument_list|,
literal|"Path /a is consistent"
argument_list|,
literal|"Path /b is consistent"
argument_list|,
literal|"Path /c is consistent"
argument_list|,
literal|"Path /d is consistent"
argument_list|,
literal|"Path /e is consistent"
argument_list|,
literal|"Path /f is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulFullCheckWithoutBinaryTraversal
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"Checked 7 nodes and 15 properties"
argument_list|,
literal|"Path / is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulPartialCheckWithoutBinaryTraversal
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/a"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/b"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/d"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/e"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"Checked 1 nodes and 0 properties"
argument_list|,
literal|"Checked 1 nodes and 4 properties"
argument_list|,
literal|"Checked 1 nodes and 5 properties"
argument_list|,
literal|"Path /a is consistent"
argument_list|,
literal|"Path /b is consistent"
argument_list|,
literal|"Path /d is consistent"
argument_list|,
literal|"Path /e is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnsuccessfulPartialCheckWithoutBinaryTraversal
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/g"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"No good revision found"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Path /g not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnsuccessfulPartialCheckWithBinaryTraversal
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/a"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/f"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/g"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/d"
argument_list|)
expr_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/e"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Searched through 1 revisions and 0 checkpoints"
argument_list|,
literal|"Checked 1 nodes and 1 properties"
argument_list|,
literal|"Checked 1 nodes and 6 properties"
argument_list|,
literal|"Checked 1 nodes and 4 properties"
argument_list|,
literal|"Checked 1 nodes and 5 properties"
argument_list|,
literal|"Path /a is consistent"
argument_list|,
literal|"Path /f is consistent"
argument_list|,
literal|"Path /d is consistent"
argument_list|,
literal|"Path /e is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Path /g not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulCheckOfHeadAndCheckpointsWithoutFilterPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
name|checkpoints
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Checking checkpoints"
argument_list|,
literal|"Searched through 1 revisions and 2 checkpoints"
argument_list|,
literal|"Checked 7 nodes and 21 properties"
argument_list|,
literal|"Path / is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuccessfulCheckOfHeadAndCheckpointsWithFilterPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/f"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckHead
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
name|checkpoints
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking head"
argument_list|,
literal|"Checking checkpoints"
argument_list|,
literal|"Searched through 1 revisions and 2 checkpoints"
argument_list|,
literal|"Checked 1 nodes and 6 properties"
argument_list|,
literal|"Path /f is consistent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMissingCheckpointCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|strOut
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|strErr
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|outWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strOut
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|errWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|strErr
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|filterPaths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|checkpoints
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|checkpoints
operator|.
name|add
argument_list|(
literal|"bogus-checkpoint"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
literal|true
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
name|checkpoints
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
literal|true
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|outWriter
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|errWriter
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|outWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|errWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strOut
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checking checkpoints"
argument_list|,
literal|"Searched through 1 revisions and 1 checkpoints"
argument_list|,
literal|"No good revision found"
argument_list|)
argument_list|)
expr_stmt|;
name|assertExpectedOutput
argument_list|(
name|strErr
operator|.
name|toString
argument_list|()
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Checkpoint bogus-checkpoint not found in this revision!"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

