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
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
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
name|Set
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|NodeStateEntry
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
name|blob
operator|.
name|MemoryBlobStore
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
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticPseudoFunctionalStyleMethod"
argument_list|)
specifier|public
class|class
name|FlatFileStoreTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
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
name|Set
argument_list|<
name|String
argument_list|>
name|preferred
init|=
name|singleton
argument_list|(
literal|"jcr:content"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basicTest
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|createTestPaths
argument_list|()
decl_stmt|;
name|FlatFileNodeStoreBuilder
name|builder
init|=
operator|new
name|FlatFileNodeStoreBuilder
argument_list|(
name|TestUtils
operator|.
name|createEntries
argument_list|(
name|paths
argument_list|)
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|FlatFileStore
name|flatStore
init|=
name|builder
operator|.
name|withBlobStore
argument_list|(
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
operator|.
name|withPreferredPathElements
argument_list|(
name|preferred
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|entryPaths
init|=
name|StreamSupport
operator|.
name|stream
argument_list|(
name|flatStore
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|NodeStateEntry
operator|::
name|getPath
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
name|List
argument_list|<
name|String
argument_list|>
name|sortedPaths
init|=
name|TestUtils
operator|.
name|sortPaths
argument_list|(
name|paths
argument_list|,
name|preferred
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sortedPaths
argument_list|,
name|entryPaths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|createTestPaths
parameter_list|()
block|{
return|return
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|,
literal|"/c"
argument_list|,
literal|"/a/b w"
argument_list|,
literal|"/a/jcr:content"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/"
argument_list|,
literal|"/b/l"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

