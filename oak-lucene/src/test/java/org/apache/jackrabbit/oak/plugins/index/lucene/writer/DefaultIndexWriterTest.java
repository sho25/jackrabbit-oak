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
name|directory
operator|.
name|DefaultDirectoryFactory
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
name|FSDirectoryFactory
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
name|document
operator|.
name|Document
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
name|ConcurrentMergeScheduler
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
name|IndexWriter
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
name|SerialMergeScheduler
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
name|FieldFactory
operator|.
name|newPathField
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
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|LuceneIndexConstants
operator|.
name|SUGGEST_DATA_CHILD_NAME
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
name|instanceOf
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
name|DefaultIndexWriterTest
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
annotation|@
name|Test
specifier|public
name|void
name|lazyInit
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DefaultIndexWriter
name|writer
init|=
operator|new
name|DefaultIndexWriter
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
literal|null
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
literal|false
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeInitializedUponReindex
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DefaultIndexWriter
name|writer
init|=
operator|new
name|DefaultIndexWriter
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
operator|new
name|DefaultDirectoryFactory
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexUpdated
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DefaultIndexWriter
name|writer
init|=
operator|new
name|DefaultIndexWriter
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
operator|new
name|DefaultDirectoryFactory
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
literal|"/a/b"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|close
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexWriterConfig_Scheduler_NonRemote
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DefaultIndexWriter
name|writer
init|=
operator|new
name|DefaultIndexWriter
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
operator|new
name|DefaultDirectoryFactory
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
name|writer
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|SerialMergeScheduler
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
name|indexWriterConfig_Scheduler_Remote
parameter_list|()
throws|throws
name|Exception
block|{
name|FSDirectoryFactory
name|fsdir
init|=
operator|new
name|FSDirectoryFactory
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
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
name|DefaultIndexWriter
name|writer
init|=
operator|new
name|DefaultIndexWriter
argument_list|(
name|defn
argument_list|,
name|builder
argument_list|,
name|fsdir
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
name|writer
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

