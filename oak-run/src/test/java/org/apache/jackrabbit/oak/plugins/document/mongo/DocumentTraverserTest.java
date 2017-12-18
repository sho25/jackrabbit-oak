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
name|document
operator|.
name|mongo
package|;
end_package

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
name|plugins
operator|.
name|document
operator|.
name|AbstractDocumentStoreTest
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
name|document
operator|.
name|Collection
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
name|document
operator|.
name|DocumentStoreFixture
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
name|document
operator|.
name|NodeDocument
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
name|document
operator|.
name|UpdateOp
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
name|document
operator|.
name|util
operator|.
name|CloseableIterable
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|document
operator|.
name|util
operator|.
name|Utils
operator|.
name|getPathFromId
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentTraverserTest
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|public
name|DocumentTraverserTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getAllDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|ds
operator|instanceof
name|MongoDocumentStore
argument_list|)
expr_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|asList
argument_list|(
name|newDocument
argument_list|(
literal|"/a/b"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newDocument
argument_list|(
literal|"/a/c"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newDocument
argument_list|(
literal|"/d"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
name|MongoDocumentTraverser
name|traverser
init|=
operator|new
name|MongoDocumentTraverser
argument_list|(
operator|(
name|MongoDocumentStore
operator|)
name|ds
argument_list|)
decl_stmt|;
name|traverser
operator|.
name|disableReadOnlyCheck
argument_list|()
expr_stmt|;
name|CloseableIterable
argument_list|<
name|NodeDocument
argument_list|>
name|itr
init|=
name|traverser
operator|.
name|getAllDocuments
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
lambda|->
name|getPathFromId
argument_list|(
name|id
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|StreamSupport
operator|.
name|stream
argument_list|(
name|itr
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|NodeDocument
operator|::
name|getPath
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|itr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ds
operator|.
name|getIfCached
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|ds
operator|.
name|getIfCached
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/a/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Excluded id should not be cached
name|assertNull
argument_list|(
name|ds
operator|.
name|getIfCached
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|UpdateOp
name|newDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|modified
parameter_list|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
name|modified
argument_list|)
expr_stmt|;
return|return
name|op
return|;
block|}
block|}
end_class

end_unit

