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
name|hybrid
package|;
end_package

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
name|HashMultimap
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
name|Multimap
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneJournalPropertyBuilderTest
block|{
specifier|private
name|LuceneJournalPropertyBuilder
name|builder
init|=
operator|new
name|LuceneJournalPropertyBuilder
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|nullProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|addProperty
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|builder
operator|.
name|buildAsString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
operator|(
operator|(
name|IndexedPaths
operator|)
name|builder
operator|.
name|build
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nullOrEmptyJson
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|addProperty
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|LuceneJournalPropertyBuilder
name|builder2
init|=
operator|new
name|LuceneJournalPropertyBuilder
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|builder2
operator|.
name|addSerializedProperty
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|builder2
operator|.
name|addSerializedProperty
argument_list|(
name|builder
operator|.
name|buildAsString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
operator|(
operator|(
name|IndexedPaths
operator|)
name|builder2
operator|.
name|build
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addMulti
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneDocumentHolder
name|h1
init|=
name|createHolder
argument_list|()
decl_stmt|;
name|h1
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|h1
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProperty
argument_list|(
name|h1
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|h2
init|=
name|createHolder
argument_list|()
decl_stmt|;
name|h2
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/bar"
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProperty
argument_list|(
name|h2
argument_list|)
expr_stmt|;
name|IndexedPaths
name|indexedPaths
init|=
operator|(
name|IndexedPaths
operator|)
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|createdIndexPathMap
argument_list|(
name|indexedPaths
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/oak:index/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addMultiJson
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneDocumentHolder
name|h1
init|=
name|createHolder
argument_list|()
decl_stmt|;
name|h1
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|h1
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProperty
argument_list|(
name|h1
argument_list|)
expr_stmt|;
name|LuceneDocumentHolder
name|h2
init|=
name|createHolder
argument_list|()
decl_stmt|;
name|h2
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/bar"
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProperty
argument_list|(
name|h2
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|builder
operator|.
name|buildAsString
argument_list|()
decl_stmt|;
name|LuceneJournalPropertyBuilder
name|builder2
init|=
operator|new
name|LuceneJournalPropertyBuilder
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|builder2
operator|.
name|addSerializedProperty
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|IndexedPaths
name|indexedPaths
init|=
operator|(
name|IndexedPaths
operator|)
name|builder2
operator|.
name|build
argument_list|()
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|createdIndexPathMap
argument_list|(
name|indexedPaths
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/oak:index/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"/oak:index/foo"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|maxLimitReached
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxSize
init|=
literal|5
decl_stmt|;
name|builder
operator|=
operator|new
name|LuceneJournalPropertyBuilder
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxSize
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|LuceneDocumentHolder
name|h1
init|=
name|createHolder
argument_list|()
decl_stmt|;
name|h1
operator|.
name|add
argument_list|(
literal|true
argument_list|,
name|LuceneDoc
operator|.
name|forDelete
argument_list|(
literal|"/oak:index/foo"
argument_list|,
literal|"/a"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProperty
argument_list|(
name|h1
argument_list|)
expr_stmt|;
block|}
name|IndexedPaths
name|indexedPaths
init|=
operator|(
name|IndexedPaths
operator|)
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|maxSize
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|indexedPaths
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createdIndexPathMap
parameter_list|(
name|Iterable
argument_list|<
name|IndexedPathInfo
argument_list|>
name|itr
parameter_list|)
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexedPathInfo
name|i
range|:
name|itr
control|)
block|{
for|for
control|(
name|String
name|indexPath
range|:
name|i
operator|.
name|getIndexPaths
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|i
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|private
name|LuceneDocumentHolder
name|createHolder
parameter_list|()
block|{
name|IndexingQueue
name|queue
init|=
name|mock
argument_list|(
name|IndexingQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|queue
operator|.
name|addIfNotFullWithoutWait
argument_list|(
name|any
argument_list|(
name|LuceneDoc
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|LuceneDocumentHolder
argument_list|(
name|queue
argument_list|,
literal|100
argument_list|)
return|;
block|}
block|}
end_class

end_unit

