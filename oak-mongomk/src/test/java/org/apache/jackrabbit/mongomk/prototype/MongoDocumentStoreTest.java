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
name|mongomk
operator|.
name|prototype
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
name|concurrent
operator|.
name|ExecutorService
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
name|Executors
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
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
name|mongomk
operator|.
name|prototype
operator|.
name|DocumentStore
operator|.
name|Collection
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests the document store.  */
end_comment

begin_class
specifier|public
class|class
name|MongoDocumentStoreTest
block|{
comment|//    private static final boolean MONGO_DB = true;
comment|//    private static final int NODE_COUNT = 2000;
specifier|private
specifier|static
specifier|final
name|boolean
name|MONGO_DB
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
literal|10
decl_stmt|;
name|DocumentStore
name|openDocumentStore
parameter_list|()
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
return|return
operator|new
name|MongoDocumentStore
argument_list|(
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|MemoryDocumentStore
argument_list|()
return|;
block|}
name|void
name|dropCollections
parameter_list|()
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addGetAndRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|dropCollections
argument_list|()
expr_stmt|;
name|DocumentStore
name|docStore
init|=
name|openDocumentStore
argument_list|()
decl_stmt|;
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
literal|"/"
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|updateOp
operator|.
name|addMapEntry
argument_list|(
literal|"property1"
argument_list|,
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|increment
argument_list|(
literal|"property2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
literal|"property3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|docStore
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obj
init|=
name|docStore
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|property1
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|obj
operator|.
name|get
argument_list|(
literal|"property1"
argument_list|)
decl_stmt|;
name|String
name|value1
init|=
operator|(
name|String
operator|)
name|property1
operator|.
name|get
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|Long
name|value2
init|=
operator|(
name|Long
operator|)
name|obj
operator|.
name|get
argument_list|(
literal|"property2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|value2
argument_list|)
expr_stmt|;
name|String
name|value3
init|=
operator|(
name|String
operator|)
name|obj
operator|.
name|get
argument_list|(
literal|"property3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value3"
argument_list|,
name|value3
argument_list|)
expr_stmt|;
name|docStore
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|obj
operator|=
name|docStore
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|==
literal|null
argument_list|)
expr_stmt|;
name|dropCollections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|batchAdd
parameter_list|()
throws|throws
name|Exception
block|{
name|dropCollections
argument_list|()
expr_stmt|;
name|DocumentStore
name|docStore
init|=
name|openDocumentStore
argument_list|()
decl_stmt|;
name|int
name|nUpdates
init|=
literal|10
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
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
name|nUpdates
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
literal|"/node"
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
name|MongoDocumentStore
operator|.
name|KEY_PATH
argument_list|,
literal|"/node"
operator|+
name|i
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|addMapEntry
argument_list|(
literal|"property1"
argument_list|,
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|increment
argument_list|(
literal|"property2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
literal|"property3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|updateOps
operator|.
name|add
argument_list|(
name|updateOp
argument_list|)
expr_stmt|;
block|}
name|docStore
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updateOps
argument_list|)
expr_stmt|;
name|dropCollections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addLotsOfNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|nPrefix
init|=
operator|new
name|char
index|[]
block|{
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|,
literal|'G'
block|,
literal|'H'
block|,
literal|'I'
block|,
literal|'J'
block|,
literal|'K'
block|,
literal|'L'
block|,
literal|'M'
block|,
literal|'N'
block|,
literal|'O'
block|,
literal|'P'
block|,
literal|'Q'
block|,
literal|'R'
block|,
literal|'S'
block|,
literal|'T'
block|,
literal|'U'
block|,
literal|'V'
block|,
literal|'W'
block|,
literal|'X'
block|,
literal|'Y'
block|,
literal|'Z'
block|}
decl_stmt|;
name|int
name|nNodes
init|=
name|NODE_COUNT
decl_stmt|;
for|for
control|(
name|int
name|nThreads
init|=
literal|1
init|;
name|nThreads
operator|<
literal|32
condition|;
name|nThreads
operator|=
name|nThreads
operator|*
literal|2
control|)
block|{
name|DocumentStore
name|docStore
init|=
name|openDocumentStore
argument_list|()
decl_stmt|;
name|dropCollections
argument_list|()
expr_stmt|;
name|log
argument_list|(
literal|"Adding and updating "
operator|+
name|nNodes
operator|+
literal|" nodes in each "
operator|+
name|nThreads
operator|+
literal|" threads"
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|nThreads
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
name|nThreads
condition|;
name|j
operator|++
control|)
block|{
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|AddAndUpdateNodesTask
argument_list|(
name|docStore
argument_list|,
literal|"node"
operator|+
name|nPrefix
index|[
name|j
index|]
argument_list|,
name|nNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Done: "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|dropCollections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|batchInsert
parameter_list|()
throws|throws
name|Exception
block|{
name|doInsert
argument_list|(
name|NODE_COUNT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doInsert
argument_list|(
name|NODE_COUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doInsert
parameter_list|(
name|int
name|n
parameter_list|,
name|boolean
name|batch
parameter_list|)
throws|throws
name|Exception
block|{
name|dropCollections
argument_list|()
expr_stmt|;
name|DBCollection
name|collection
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"batchInsertTest"
argument_list|)
decl_stmt|;
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|index
operator|.
name|put
argument_list|(
literal|"_path"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DBObject
name|options
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"unique"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|collection
operator|.
name|ensureIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Inserting "
operator|+
name|n
operator|+
literal|" batch? "
operator|+
name|batch
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|batch
condition|)
block|{
name|DBObject
index|[]
name|arr
init|=
operator|new
name|BasicDBObject
index|[
name|n
index|]
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_path"
argument_list|,
literal|"/a"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|WriteResult
name|result
init|=
name|collection
operator|.
name|insert
argument_list|(
name|arr
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Error: "
operator|+
name|result
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|WriteResult
name|result
init|=
name|collection
operator|.
name|insert
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"_path"
argument_list|,
literal|"/a"
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|log
argument_list|(
literal|"Error: "
operator|+
name|result
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"Done: "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|dropCollections
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|NODE_COUNT
operator|<
literal|100
condition|)
block|{
return|return;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**      * Task to create / update nodes.      */
specifier|private
specifier|static
class|class
name|AddAndUpdateNodesTask
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|DocumentStore
name|docStore
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
specifier|final
name|int
name|nNodes
decl_stmt|;
specifier|public
name|AddAndUpdateNodesTask
parameter_list|(
name|DocumentStore
name|docStore
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|int
name|nNodes
parameter_list|)
block|{
name|this
operator|.
name|docStore
operator|=
name|docStore
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|nNodes
operator|=
name|nNodes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|addNodes
argument_list|()
expr_stmt|;
name|updateNodes
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addNodes
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nNodes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
literal|"/"
operator|+
name|nodeName
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|updateOp
operator|.
name|addMapEntry
argument_list|(
literal|"property1"
argument_list|,
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
literal|"property3"
argument_list|,
literal|"value3"
argument_list|)
expr_stmt|;
name|docStore
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|updateNodes
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nNodes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
literal|"/"
operator|+
name|nodeName
operator|+
name|i
decl_stmt|;
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|updateOp
operator|.
name|addMapEntry
argument_list|(
literal|"property1"
argument_list|,
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|updateOp
operator|.
name|set
argument_list|(
literal|"property4"
argument_list|,
literal|"value4"
argument_list|)
expr_stmt|;
name|docStore
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

