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
name|index
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|io
operator|.
name|Files
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
name|Type
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
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|IndexRootDirectory
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|LocalIndexDir
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
name|NodeStore
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

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
operator|.
name|defaultCharset
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
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

begin_class
specifier|public
class|class
name|IndexCommandIT
extends|extends
name|AbstractIndexCommandTest
block|{
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|IndexCommand
operator|.
name|setDisableExitOnError
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dumpStatsAndInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//Close the repository so as all changes are flushed
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexCommand
name|command
init|=
operator|new
name|IndexCommand
argument_list|()
decl_stmt|;
name|File
name|outDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-index-temp-dir="
operator|+
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-index-out-dir="
operator|+
name|outDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-index-info"
block|,
literal|"-index-definitions"
block|,
name|fixture
operator|.
name|getDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|command
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|File
name|info
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_INFO_TXT
argument_list|)
decl_stmt|;
name|File
name|defns
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_DEFINITIONS_JSON
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|defns
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Files
operator|.
name|toString
argument_list|(
name|info
argument_list|,
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/oak:index/uuid"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Files
operator|.
name|toString
argument_list|(
name|info
argument_list|,
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|selectedIndexPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//Close the repository so as all changes are flushed
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexCommand
name|command
init|=
operator|new
name|IndexCommand
argument_list|()
decl_stmt|;
name|File
name|outDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-index-temp-dir="
operator|+
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-index-out-dir="
operator|+
name|outDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-index-paths=/oak:index/fooIndex"
block|,
literal|"-index-info"
block|,
literal|"-index-definitions"
block|,
name|fixture
operator|.
name|getDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|command
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|File
name|info
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_INFO_TXT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Files
operator|.
name|toString
argument_list|(
name|info
argument_list|,
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"/oak:index/uuid"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Files
operator|.
name|toString
argument_list|(
name|info
argument_list|,
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|consistencyCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//Close the repository so as all changes are flushed
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexCommand
name|command
init|=
operator|new
name|IndexCommand
argument_list|()
decl_stmt|;
name|File
name|outDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"--index-temp-dir="
operator|+
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--index-out-dir="
operator|+
name|outDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--index-consistency-check"
block|,
literal|"--"
block|,
comment|// -- indicates that options have ended and rest needs to be treated as non option
name|fixture
operator|.
name|getDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|command
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|File
name|report
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_CONSISTENCY_CHECK_TXT
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_INFO_TXT
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexCommand
operator|.
name|INDEX_DEFINITIONS_JSON
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|report
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Files
operator|.
name|toString
argument_list|(
name|report
argument_list|,
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dumpIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestData
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//Close the repository so as all changes are flushed
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexCommand
name|command
init|=
operator|new
name|IndexCommand
argument_list|()
decl_stmt|;
name|File
name|outDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"--index-temp-dir="
operator|+
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--index-out-dir="
operator|+
name|outDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--index-dump"
block|,
literal|"--"
block|,
comment|// -- indicates that options have ended and rest needs to be treated as non option
name|fixture
operator|.
name|getDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|command
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|File
name|dumpDir
init|=
operator|new
name|File
argument_list|(
name|outDir
argument_list|,
name|IndexDumper
operator|.
name|INDEX_DUMPS_DIR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dumpDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

