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
operator|.
name|rdb
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|AbstractRDBConnectionTest
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
name|DocumentMK
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
name|DocumentStore
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
name|Revision
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
name|Ignore
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

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"OAK-7101"
argument_list|)
specifier|public
class|class
name|RDBCacheConsistency2Test
extends|extends
name|AbstractRDBConnectionTest
block|{
specifier|private
specifier|static
specifier|final
name|long
name|CACHE_SIZE
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_NODES
init|=
literal|50
decl_stmt|;
specifier|private
name|DocumentStore
name|ds
decl_stmt|;
specifier|private
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|newBuilder
parameter_list|(
name|DataSource
name|db
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|prefix
init|=
literal|"T"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|RDBOptions
name|opt
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
name|prefix
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
name|CACHE_SIZE
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setRDBConnection
argument_list|(
name|dataSource
argument_list|,
name|opt
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|ds
operator|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
block|}
comment|/**      * Perform concurrent update and query operations and check if the document      * cache is consistent afterwards. See OAK-7101.      */
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
name|NodeDocument
operator|.
name|setLastRev
argument_list|(
name|op
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
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
name|updateDocuments
argument_list|()
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
name|getIfCached
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
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
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
operator|.
name|get
argument_list|()
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
name|counter
operator|.
name|incrementAndGet
argument_list|()
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
name|updateDocuments
parameter_list|()
block|{
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
name|Revision
name|r
init|=
name|newRevision
argument_list|()
decl_stmt|;
name|NodeDocument
operator|.
name|setLastRev
argument_list|(
name|op
argument_list|,
name|r
argument_list|)
expr_stmt|;
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
name|ops
operator|.
name|add
argument_list|(
name|op
operator|.
name|shallowCopy
argument_list|(
name|getIdFromPath
argument_list|(
literal|"/node-"
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|ops
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

