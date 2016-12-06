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
name|writer
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
name|IOException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|CachingFileDataStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
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
name|multiplex
operator|.
name|SimpleMountInfoProvider
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
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexReader
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
name|Before
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
name|newDoc
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|nodetype
operator|.
name|write
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
name|CoreMatchers
operator|.
name|instanceOf
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
name|contains
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
name|containsInAnyOrder
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

begin_class
specifier|public
class|class
name|MultiplexingIndexWriterTest
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
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mip
init|=
name|SimpleMountInfoProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"foo"
argument_list|,
literal|"/libs"
argument_list|,
literal|"/apps"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
name|Mount
name|fooMount
decl_stmt|;
specifier|private
name|Mount
name|defaultMount
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|initializeMounts
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultWriterWithNoMounts
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|writer
argument_list|,
name|instanceOf
argument_list|(
name|DefaultIndexWriter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|closeWithoutChange
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|getIndexDirNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writesInDefaultMount
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//1. Add entry in foo mount
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/libs/config"
argument_list|,
name|newDoc
argument_list|(
literal|"/libs/config"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|getIndexDirNodes
argument_list|()
decl_stmt|;
comment|//Only dirNode for mount foo should be present
name|assertThat
argument_list|(
name|names
argument_list|,
name|contains
argument_list|(
name|indexDirName
argument_list|(
name|fooMount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//2. Add entry in default mount
name|writer
operator|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content"
argument_list|,
name|newDoc
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|names
operator|=
name|getIndexDirNodes
argument_list|()
expr_stmt|;
comment|//Dir names for both mounts should be present
name|assertThat
argument_list|(
name|names
argument_list|,
name|containsInAnyOrder
argument_list|(
name|indexDirName
argument_list|(
name|fooMount
argument_list|)
argument_list|,
name|indexDirName
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writesInDefaultMountBlobStore
parameter_list|()
throws|throws
name|Exception
block|{
name|CachingFileDataStore
name|ds
init|=
name|DataStoreUtils
operator|.
name|createCachingFDS
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//1. Add entry in foo mount
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/libs/config"
argument_list|,
name|newDoc
argument_list|(
literal|"/libs/config"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|getIndexDirNodes
argument_list|()
decl_stmt|;
comment|//Only dirNode for mount foo should be present
name|assertThat
argument_list|(
name|names
argument_list|,
name|contains
argument_list|(
name|indexDirName
argument_list|(
name|fooMount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//2. Add entry in default mount
name|writer
operator|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content"
argument_list|,
name|newDoc
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|names
operator|=
name|getIndexDirNodes
argument_list|()
expr_stmt|;
comment|//Dir names for both mounts should be present
name|assertThat
argument_list|(
name|names
argument_list|,
name|containsInAnyOrder
argument_list|(
name|indexDirName
argument_list|(
name|fooMount
argument_list|)
argument_list|,
name|indexDirName
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deletes
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/libs/config"
argument_list|,
name|newDoc
argument_list|(
literal|"/libs/config"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/libs/install"
argument_list|,
name|newDoc
argument_list|(
literal|"/libs/install"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content"
argument_list|,
name|newDoc
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content/en"
argument_list|,
name|newDoc
argument_list|(
literal|"/content/en"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numDocs
argument_list|(
name|fooMount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numDocs
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
literal|"/libs/config"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numDocs
argument_list|(
name|fooMount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numDocs
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
literal|"/content"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numDocs
argument_list|(
name|fooMount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|numDocs
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteIncludingMount
parameter_list|()
throws|throws
name|Exception
block|{
name|mip
operator|=
name|SimpleMountInfoProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"foo"
argument_list|,
literal|"/content/remote"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|initializeMounts
argument_list|()
expr_stmt|;
name|LuceneIndexWriterFactory
name|factory
init|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|mip
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LuceneIndexWriter
name|writer
init|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content/remote/a"
argument_list|,
name|newDoc
argument_list|(
literal|"/content/remote/a"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/etc"
argument_list|,
name|newDoc
argument_list|(
literal|"/etc"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/content"
argument_list|,
name|newDoc
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numDocs
argument_list|(
name|fooMount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numDocs
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
name|factory
operator|.
name|newInstance
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
literal|"/content"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|numDocs
argument_list|(
name|fooMount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numDocs
argument_list|(
name|defaultMount
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initializeMounts
parameter_list|()
block|{
name|fooMount
operator|=
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|defaultMount
operator|=
name|mip
operator|.
name|getDefaultMount
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|numDocs
parameter_list|(
name|Mount
name|m
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|indexDirName
init|=
name|indexDirName
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|Directory
name|d
init|=
operator|new
name|OakDirectory
argument_list|(
name|builder
argument_list|,
name|indexDirName
argument_list|,
name|defn
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|numDocs
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getIndexDirNodes
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|builder
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|MultiplexersLucene
operator|.
name|isIndexDirName
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|names
return|;
block|}
specifier|private
name|String
name|indexDirName
parameter_list|(
name|Mount
name|m
parameter_list|)
block|{
return|return
name|MultiplexersLucene
operator|.
name|getIndexDirName
argument_list|(
name|m
argument_list|)
return|;
block|}
block|}
end_class

end_unit

