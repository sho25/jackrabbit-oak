begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|shuffle
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
name|assertNotEquals
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
name|assertTrue
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
name|fail
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
name|HashSet
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
name|Set
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
name|ConcurrentHashMap
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
name|RDBDataSourceWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
specifier|public
class|class
name|BulkCreateOrUpdateTest
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|public
name|BulkCreateOrUpdateTest
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
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|DataSource
name|dataSource
init|=
name|dsf
operator|.
name|getRDBDataSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataSource
operator|instanceof
name|RDBDataSourceWrapper
condition|)
block|{
comment|// test drivers that do not return precise batch results
operator|(
operator|(
name|RDBDataSourceWrapper
operator|)
name|dataSource
operator|)
operator|.
name|setBatchResultPrecise
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|DataSource
name|dataSource
init|=
name|dsf
operator|.
name|getRDBDataSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataSource
operator|instanceof
name|RDBDataSourceWrapper
condition|)
block|{
operator|(
operator|(
name|RDBDataSourceWrapper
operator|)
name|dataSource
operator|)
operator|.
name|setBatchResultPrecise
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This tests create multiple items using createOrUpdate() method. The      * return value should be a list of null values.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateMultiple
parameter_list|()
block|{
specifier|final
name|int
name|amount
init|=
literal|100
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testCreateMultiple"
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
name|updates
operator|.
name|add
argument_list|(
name|up
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
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|amount
argument_list|,
name|docs
operator|.
name|size
argument_list|()
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|assertNull
argument_list|(
literal|"There shouldn't be a value for created doc"
argument_list|,
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"The node hasn't been created"
argument_list|,
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This method updates multiple items using createOrUpdate() method. The      * return value should be a list of items before the update.      */
annotation|@
name|Test
specifier|public
name|void
name|testUpdateMultiple
parameter_list|()
block|{
specifier|final
name|int
name|amount
init|=
literal|100
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testUpdateMultiple"
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
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
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|UpdateOp
name|up
init|=
name|updates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|copy
argument_list|()
decl_stmt|;
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|updates
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|up
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|amount
argument_list|,
name|docs
operator|.
name|size
argument_list|()
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|NodeDocument
name|oldDoc
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|oldDoc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|NodeDocument
name|newDoc
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The result list order is incorrect"
argument_list|,
name|updates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The old value is not correct"
argument_list|,
literal|100l
argument_list|,
name|oldDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The document hasn't been updated"
argument_list|,
literal|200l
argument_list|,
name|newDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This method creates or updates multiple items using createOrUpdate()      * method. New items have odd indexes and updates items have even indexes.      * The return value should be a list of old documents (for the updates) or      * nulls (for the inserts).      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrUpdateMultiple
parameter_list|()
block|{
name|int
name|amount
init|=
literal|100
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
comment|// create even items
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testCreateOrUpdateMultiple"
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
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
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
expr_stmt|;
name|updates
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// createOrUpdate all items
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testCreateOrUpdateMultiple"
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
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
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|amount
argument_list|,
name|docs
operator|.
name|size
argument_list|()
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testCreateOrUpdateMultiple"
operator|+
name|i
decl_stmt|;
name|NodeDocument
name|oldDoc
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NodeDocument
name|newDoc
init|=
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
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|assertNull
argument_list|(
literal|"The returned value should be null for created doc"
argument_list|,
name|oldDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
literal|"The returned doc shouldn't be null for updated doc"
argument_list|,
name|oldDoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The old value is not correct"
argument_list|,
literal|100l
argument_list|,
name|oldDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The result list order is incorrect"
argument_list|,
name|updates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|oldDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"The document hasn't been updated"
argument_list|,
literal|200l
argument_list|,
name|newDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run multiple batch updates concurrently. Each thread modifies only its own documents.      */
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentNoConflict
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|int
name|amountPerThread
init|=
literal|100
decl_stmt|;
name|int
name|threadCount
init|=
literal|10
decl_stmt|;
name|int
name|amount
init|=
name|amountPerThread
operator|*
name|threadCount
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
comment|// create even items
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentNoConflict"
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeDocument
argument_list|>
name|oldDocs
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|NodeDocument
argument_list|>
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|threadUpdates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amountPerThread
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|amountPerThread
condition|;
name|j
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentNoConflict"
operator|+
operator|(
name|j
operator|+
name|i
operator|*
name|amountPerThread
operator|)
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|200
operator|+
name|i
operator|+
name|j
argument_list|)
expr_stmt|;
name|threadUpdates
operator|.
name|add
argument_list|(
name|up
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
name|shuffle
argument_list|(
name|threadUpdates
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|NodeDocument
name|d
range|:
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|threadUpdates
argument_list|)
control|)
block|{
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|oldDocs
operator|.
name|put
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Thread hasn't finished in 10s"
argument_list|)
expr_stmt|;
block|}
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentNoConflict"
operator|+
name|i
decl_stmt|;
name|NodeDocument
name|oldDoc
init|=
name|oldDocs
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|NodeDocument
name|newDoc
init|=
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
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|assertNull
argument_list|(
literal|"The returned value should be null for created doc"
argument_list|,
name|oldDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
literal|"The returned doc shouldn't be null for updated doc"
argument_list|,
name|oldDoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The old value is not correct"
argument_list|,
literal|100l
argument_list|,
name|oldDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNotEquals
argument_list|(
literal|"The document hasn't been updated"
argument_list|,
literal|100l
argument_list|,
name|newDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run multiple batch updates concurrently. Each thread modifies the same set of documents.      */
annotation|@
name|Test
specifier|public
name|void
name|testConcurrentWithConflict
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|assumeTrue
argument_list|(
name|this
operator|.
name|dsf
operator|!=
name|DocumentStoreFixture
operator|.
name|RDB_DERBY
argument_list|)
expr_stmt|;
name|int
name|threadCount
init|=
literal|10
decl_stmt|;
name|int
name|amount
init|=
literal|500
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
comment|// create even items
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentNoConflict"
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
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
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|HashSet
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|threadUpdates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|amount
condition|;
name|j
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentWithConflict"
operator|+
name|j
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
name|up
operator|.
name|set
argument_list|(
literal|"prop"
argument_list|,
literal|200
operator|+
name|i
operator|*
name|amount
operator|+
name|j
argument_list|)
expr_stmt|;
name|threadUpdates
operator|.
name|add
argument_list|(
name|up
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
name|shuffle
argument_list|(
name|threadUpdates
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|threadUpdates
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Thread hasn't finished in 10s"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|exceptions
operator|.
name|size
argument_list|()
operator|+
literal|" out of "
operator|+
name|threadCount
operator|+
literal|" failed with exceptions, the first being: "
operator|+
name|exceptions
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|fail
argument_list|(
name|msg
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testConcurrentWithConflict"
operator|+
name|i
decl_stmt|;
name|NodeDocument
name|newDoc
init|=
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
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"The document hasn't been inserted"
argument_list|,
name|newDoc
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"The document hasn't been updated"
argument_list|,
literal|100l
argument_list|,
name|newDoc
operator|.
name|get
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This method adds a few updateOperations modifying the same document.      */
annotation|@
name|Test
specifier|public
name|void
name|testUpdateSameDocument
parameter_list|()
block|{
specifier|final
name|int
name|amount
init|=
literal|5
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updates
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testUpdateSameDocument"
decl_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
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
name|up
operator|.
name|set
argument_list|(
literal|"update_id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|up
operator|.
name|set
argument_list|(
literal|"prop_"
operator|+
name|i
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|up
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updates
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|amount
argument_list|,
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"The old value should be null for the first update"
argument_list|,
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|prevModCount
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|Long
name|modCount
init|=
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getModCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevModCount
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
name|modCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"_modCount, when present, must be increasing, but changed from "
operator|+
name|prevModCount
operator|+
literal|" to "
operator|+
name|modCount
argument_list|,
name|prevModCount
operator|.
name|longValue
argument_list|()
operator|<
name|modCount
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|prevModCount
operator|=
name|modCount
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The old value is not correct"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|,
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
literal|"update_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NodeDocument
name|newDoc
init|=
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
decl_stmt|;
if|if
condition|(
name|newDoc
operator|.
name|getModCount
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"The final mod count is not correct"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|,
name|newDoc
operator|.
name|getModCount
argument_list|()
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
name|amount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"The value is not correct"
argument_list|,
literal|100l
argument_list|,
name|newDoc
operator|.
name|get
argument_list|(
literal|"prop_"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBulkCreateOrUpdateIsNewFalse
parameter_list|()
block|{
name|bulkCreateOrUpdateIsNewFalse
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBulkCreateOrUpdateIsNewFalseMany
parameter_list|()
block|{
name|bulkCreateOrUpdateIsNewFalse
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|bulkCreateOrUpdateIsNewFalse
parameter_list|(
name|int
name|numUpdates
parameter_list|)
block|{
name|String
name|id1
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testBulkCreateOrUpdateIsNewFalse"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numUpdates
condition|;
name|i
operator|++
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|id1
operator|+
literal|"b"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|removeMe
operator|.
name|add
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|addAll
argument_list|(
name|ids
argument_list|)
expr_stmt|;
comment|// remove id1
name|super
operator|.
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|initial
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// insert other ids
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
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
name|assertNull
argument_list|(
name|super
operator|.
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|up
argument_list|)
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|super
operator|.
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|initial
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// bulk update
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ops
operator|.
name|add
argument_list|(
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|ops
operator|.
name|add
argument_list|(
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|result
init|=
name|super
operator|.
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|ops
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numUpdates
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// id1 result should be reported as null and not be created
name|assertNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
argument_list|)
expr_stmt|;
comment|// for other ids result should be reported with previous doc
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numUpdates
condition|;
name|i
operator|++
control|)
block|{
name|NodeDocument
name|prev
init|=
name|result
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|ids
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|prev
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|prev
operator|.
name|getModCount
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|initial
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getModCount
argument_list|()
argument_list|,
name|prev
operator|.
name|getModCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NodeDocument
name|updated
init|=
name|super
operator|.
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|updated
argument_list|)
expr_stmt|;
if|if
condition|(
name|prev
operator|.
name|getModCount
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assertNotEquals
argument_list|(
name|updated
operator|.
name|getModCount
argument_list|()
argument_list|,
name|prev
operator|.
name|getModCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

