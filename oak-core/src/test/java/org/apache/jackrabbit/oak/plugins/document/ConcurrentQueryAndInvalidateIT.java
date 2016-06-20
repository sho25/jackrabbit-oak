begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
operator|.
name|NODES
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
name|getIdFromPath
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
name|getKeyLowerLimit
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
name|getKeyUpperLimit
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeFalse
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
name|ConcurrentQueryAndInvalidateIT
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|public
name|ConcurrentQueryAndInvalidateIT
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
name|assumeTrue
argument_list|(
name|dsf
operator|.
name|hasSinglePersistence
argument_list|()
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|dsf
operator|instanceof
name|DocumentStoreFixture
operator|.
name|RDBFixture
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
specifier|final
name|int
name|NUM_NODES
init|=
literal|50
decl_stmt|;
specifier|private
specifier|volatile
name|long
name|counter
decl_stmt|;
specifier|private
name|DocumentStore
name|ds2
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setupSecondDocumentStore
parameter_list|()
block|{
name|ds2
operator|=
name|dsf
operator|.
name|createDocumentStore
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r
init|=
name|newRevision
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|ops
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/node-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
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
name|Document
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setLastRev
argument_list|(
name|op
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|ops
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|NODES
argument_list|,
name|ops
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|q
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// query docs on ds1
name|queryDocuments
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Thread
name|u
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// update docs on ds2
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|updateDocuments
argument_list|()
decl_stmt|;
comment|// invalidate docs on ds1
name|invalidateDocuments
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|q
operator|.
name|start
argument_list|()
expr_stmt|;
name|u
operator|.
name|start
argument_list|()
expr_stmt|;
name|q
operator|.
name|join
argument_list|()
expr_stmt|;
name|u
operator|.
name|join
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_NODES
condition|;
name|j
operator|++
control|)
block|{
name|NodeDocument
name|doc
init|=
name|ds
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/node-"
operator|+
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected revision timestamp for "
operator|+
name|doc
operator|.
name|getId
argument_list|()
argument_list|,
name|counter
argument_list|,
name|doc
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Revision
name|newRevision
parameter_list|()
block|{
return|return
operator|new
name|Revision
argument_list|(
operator|++
name|counter
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
return|;
block|}
specifier|private
name|void
name|queryDocuments
parameter_list|()
block|{
name|ds
operator|.
name|query
argument_list|(
name|NODES
argument_list|,
name|getKeyLowerLimit
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|getKeyUpperLimit
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|invalidateDocuments
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|ds
operator|.
name|invalidateCache
argument_list|(
name|ids
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|updateDocuments
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeDocument
operator|.
name|setLastRev
argument_list|(
name|op
argument_list|,
name|newRevision
argument_list|()
argument_list|)
expr_stmt|;
name|ds2
operator|.
name|update
argument_list|(
name|NODES
argument_list|,
name|ids
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|ids
return|;
block|}
block|}
end_class

end_unit

