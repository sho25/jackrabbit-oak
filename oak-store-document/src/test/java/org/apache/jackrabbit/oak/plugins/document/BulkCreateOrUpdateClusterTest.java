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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|junit
operator|.
name|LogCustomizer
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
import|;
end_import

begin_class
specifier|public
class|class
name|BulkCreateOrUpdateClusterTest
extends|extends
name|AbstractMultiDocumentStoreTest
block|{
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|BulkCreateOrUpdateClusterTest
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
name|ds1
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
name|DocumentStore
name|selectedDs
init|=
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
name|this
operator|.
name|ds1
else|:
name|this
operator|.
name|ds2
decl_stmt|;
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
try|try
block|{
for|for
control|(
name|NodeDocument
name|d
range|:
name|selectedDs
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
decl_stmt|;
comment|//avoid cache issues
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
name|newDoc
operator|=
name|ds1
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
name|newDoc
operator|=
name|ds2
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
name|ds1
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
name|DocumentStore
name|selectedDs
init|=
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
name|this
operator|.
name|ds1
else|:
name|this
operator|.
name|ds2
decl_stmt|;
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
name|selectedDs
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
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|t
operator|.
name|join
argument_list|(
literal|75000
argument_list|)
expr_stmt|;
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"join took "
operator|+
name|time
operator|+
literal|" ms"
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
name|ds1
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
name|newDoc
operator|=
name|ds2
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
annotation|@
name|Test
specifier|public
name|void
name|testSimpleConflictHandling
parameter_list|()
block|{
name|LogCustomizer
name|logCustomizer
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|RDBDocumentStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
operator|.
name|contains
argument_list|(
literal|"invalidating cache and retrying"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|logCustomizer
operator|.
name|starting
argument_list|()
expr_stmt|;
try|try
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
literal|".testSimpleConflictHandling1"
decl_stmt|;
name|String
name|id2
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testSimpleConflictHandling2"
decl_stmt|;
name|String
name|id3
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testSimpleConflictHandling3"
decl_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id3
argument_list|)
expr_stmt|;
block|{
name|UpdateOp
name|op1a
init|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op1a
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|UpdateOp
name|op2a
init|=
operator|new
name|UpdateOp
argument_list|(
name|id2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op2a
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|UpdateOp
name|op3a
init|=
operator|new
name|UpdateOp
argument_list|(
name|id3
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op3a
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|resulta
init|=
name|ds1
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op1a
argument_list|,
name|op2a
argument_list|,
name|op3a
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|resulta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|UpdateOp
name|op2b
init|=
operator|new
name|UpdateOp
argument_list|(
name|id2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|op2b
operator|.
name|increment
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|NodeDocument
name|prev2
init|=
name|ds2
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op2b
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prev2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|prev2
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|UpdateOp
name|op1c
init|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op1c
operator|.
name|increment
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|UpdateOp
name|op2c
init|=
operator|new
name|UpdateOp
argument_list|(
name|id2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op2c
operator|.
name|increment
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|UpdateOp
name|op3c
init|=
operator|new
name|UpdateOp
argument_list|(
name|id3
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op3c
operator|.
name|increment
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|resultc
init|=
name|ds1
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|op1c
argument_list|,
name|op2c
argument_list|,
name|op3c
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|resultc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeDocument
name|d
range|:
name|resultc
control|)
block|{
name|Long
name|fooval
init|=
operator|(
name|Long
operator|)
name|d
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|d
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|id2
argument_list|)
operator|)
condition|?
literal|2L
else|:
literal|1L
argument_list|,
name|fooval
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ds1
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
comment|// for RDB, verify that the cache invalidation was reached
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|logCustomizer
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|logCustomizer
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

