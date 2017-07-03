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
name|index
operator|.
name|lucene
operator|.
name|directory
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
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
name|IndexDefinition
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
name|OakDirectory
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
name|util
operator|.
name|IndexDefinitionBuilder
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
name|writer
operator|.
name|MultiplexersLucene
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
name|index
operator|.
name|lucene
operator|.
name|TestUtil
operator|.
name|createFile
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|IndexRootDirectory
operator|.
name|INDEX_METADATA_FILE_NAME
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexDumperTest
block|{
specifier|private
name|NodeState
name|rootState
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|idx
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
operator|.
name|build
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|directoryDump
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|long
name|size
init|=
literal|0
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data"
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|dir
argument_list|,
literal|"foo.txt"
argument_list|,
literal|"Test content"
argument_list|)
expr_stmt|;
name|size
operator|+=
name|DirectoryUtils
operator|.
name|dirSize
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|Directory
name|dir2
init|=
operator|new
name|OakDirectory
argument_list|(
name|idx
argument_list|,
literal|":data2"
operator|+
name|MultiplexersLucene
operator|.
name|INDEX_DIR_SUFFIX
argument_list|,
name|defn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|dir2
argument_list|,
literal|"foo.txt"
argument_list|,
literal|"Test content"
argument_list|)
expr_stmt|;
name|size
operator|+=
name|DirectoryUtils
operator|.
name|dirSize
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootState
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"fooIndex"
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|indexState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|File
name|out
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|LuceneIndexDumper
name|dumper
init|=
operator|new
name|LuceneIndexDumper
argument_list|(
name|indexState
argument_list|,
literal|"/fooIndex"
argument_list|,
name|out
argument_list|)
decl_stmt|;
name|dumper
operator|.
name|dump
argument_list|()
expr_stmt|;
name|File
name|indexDir
init|=
name|dumper
operator|.
name|getIndexDir
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|indexDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// 2 dir + 1 meta
name|assertEquals
argument_list|(
name|dumper
operator|.
name|getSize
argument_list|()
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|IndexMeta
name|meta
init|=
operator|new
name|IndexMeta
argument_list|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|,
name|INDEX_METADATA_FILE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|getFSNameFromJCRName
argument_list|(
literal|":data"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|getFSNameFromJCRName
argument_list|(
literal|":data2"
operator|+
name|MultiplexersLucene
operator|.
name|INDEX_DIR_SUFFIX
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

