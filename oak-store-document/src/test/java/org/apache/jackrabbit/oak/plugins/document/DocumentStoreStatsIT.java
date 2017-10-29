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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|mongo
operator|.
name|MongoDocumentStore
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
name|rdb
operator|.
name|RDBDocumentStore
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
name|TestName
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
name|singletonList
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
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
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
name|NodeDocument
operator|.
name|getModifiedInSecs
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
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
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
name|anyLong
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
name|eq
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
name|verify
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Duplicates"
argument_list|)
specifier|public
class|class
name|DocumentStoreStatsIT
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|private
name|DocumentStoreStatsCollector
name|stats
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|public
name|DocumentStoreStatsIT
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
name|configureStatsCollector
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|checkSupportedStores
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|ds
operator|instanceof
name|MemoryDocumentStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|stats
argument_list|)
operator|.
name|doneCreate
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
name|singletonList
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|findCached_Uncached
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|stats
argument_list|)
operator|.
name|doneFindCached
argument_list|(
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|stats
argument_list|)
operator|.
name|doneFindUncached
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
name|id
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|query
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create ten documents
name|String
name|base
init|=
name|testName
operator|.
name|getMethodName
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|base
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|super
operator|.
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document with "
operator|+
name|id
operator|+
literal|" not created"
argument_list|,
name|success
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
name|query
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|base
argument_list|,
name|base
operator|+
literal|"A"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|stats
argument_list|)
operator|.
name|doneQuery
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
name|base
argument_list|)
argument_list|,
name|eq
argument_list|(
name|base
operator|+
literal|"A"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
comment|//indexedProperty
name|eq
argument_list|(
literal|5
argument_list|)
argument_list|,
comment|// resultSize
name|anyLong
argument_list|()
argument_list|,
comment|//lockTime
name|eq
argument_list|(
literal|false
argument_list|)
comment|//isSlaveOk
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|update
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toupdate
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|toupdate
operator|.
name|add
argument_list|(
name|id
operator|+
literal|"-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|toupdate
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|UpdateOp
name|up2
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|up2
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|update
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|toupdate
argument_list|,
name|up2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|stats
argument_list|)
operator|.
name|doneUpdate
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|findAndModify
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|DocumentStoreStatsCollector
name|coll
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|configureStatsCollector
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|up
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|up
operator|.
name|max
argument_list|(
literal|"_modified"
argument_list|,
literal|122L
argument_list|)
expr_stmt|;
name|ds
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|up
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|coll
argument_list|)
operator|.
name|doneFindAndModify
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
name|id
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|DocumentStoreStatsCollector
name|coll
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|configureStatsCollector
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|coll
argument_list|)
operator|.
name|doneRemove
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeMultiple
parameter_list|()
throws|throws
name|Exception
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|UpdateOp
name|up
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|singletonList
argument_list|(
name|up
argument_list|)
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
name|DocumentStoreStatsCollector
name|coll
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|configureStatsCollector
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|coll
argument_list|)
operator|.
name|doneRemove
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeConditional
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|modified
init|=
name|getModifiedInSecs
argument_list|(
name|r
operator|.
name|getTimestamp
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|ids
init|=
name|Maps
operator|.
name|newHashMap
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
name|ids
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|modified
argument_list|)
expr_stmt|;
name|UpdateOp
name|up
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
name|setModified
argument_list|(
name|up
argument_list|,
name|r
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
name|singletonList
argument_list|(
name|up
argument_list|)
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
name|DocumentStoreStatsCollector
name|coll
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|configureStatsCollector
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|coll
argument_list|)
operator|.
name|doneRemove
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeIndexProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r
init|=
operator|new
name|Revision
argument_list|(
literal|123456
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|mod
init|=
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|r
operator|.
name|getTimestamp
argument_list|()
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|up
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
name|setModified
argument_list|(
name|up
argument_list|,
name|r
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
name|singletonList
argument_list|(
name|up
argument_list|)
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
name|DocumentStoreStatsCollector
name|coll
init|=
name|mock
argument_list|(
name|DocumentStoreStatsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|configureStatsCollector
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|MODIFIED_IN_SECS
argument_list|,
name|mod
operator|-
literal|1
argument_list|,
name|mod
operator|+
literal|1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|coll
argument_list|)
operator|.
name|doneRemove
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|configureStatsCollector
parameter_list|(
name|DocumentStoreStatsCollector
name|stats
parameter_list|)
block|{
if|if
condition|(
name|ds
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
operator|(
operator|(
name|MongoDocumentStore
operator|)
name|ds
operator|)
operator|.
name|setStatsCollector
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ds
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|ds
operator|)
operator|.
name|setStatsCollector
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

