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
name|stream
operator|.
name|Collectors
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
name|InitialContent
operator|.
name|INITIAL_CONTENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasItems
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|FSDirectoryFactoryTest
block|{
annotation|@
name|Rule
specifier|public
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
specifier|private
name|NodeState
name|root
init|=
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
name|Test
specifier|public
name|void
name|singleIndex
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
name|root
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
name|FSDirectoryFactory
name|factory
init|=
operator|new
name|FSDirectoryFactory
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|idx
argument_list|,
literal|":data"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexRootDirectory
name|idxDir
init|=
operator|new
name|IndexRootDirectory
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|indexes
init|=
name|idxDir
operator|.
name|getAllLocalIndexes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/fooIndex"
argument_list|,
name|indexes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getJcrPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|indexes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|dir
argument_list|,
literal|"data"
argument_list|)
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
name|multiIndexWithSimilarPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|defn1
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|root
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/content/a/en_us/oak:index/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexDefinition
name|defn2
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|root
argument_list|,
name|idx
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/content/b/en_us/oak:index/fooIndex"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FSDirectoryFactory
name|factory
init|=
operator|new
name|FSDirectoryFactory
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|factory
operator|.
name|newInstance
argument_list|(
name|defn1
argument_list|,
name|idx
argument_list|,
literal|":data"
argument_list|,
literal|false
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|factory
operator|.
name|newInstance
argument_list|(
name|defn2
argument_list|,
name|idx
argument_list|,
literal|":data"
argument_list|,
literal|false
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexRootDirectory
name|idxDir
init|=
operator|new
name|IndexRootDirectory
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|indexes
init|=
name|idxDir
operator|.
name|getAllLocalIndexes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|indexes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|idxPaths
init|=
name|indexes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|LocalIndexDir
operator|::
name|getJcrPath
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|idxPaths
argument_list|,
name|hasItems
argument_list|(
literal|"/content/a/en_us/oak:index/fooIndex"
argument_list|,
literal|"/content/b/en_us/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

