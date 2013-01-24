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
name|mongomk
operator|.
name|impl
operator|.
name|action
package|;
end_package

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
name|Arrays
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|BaseMongoMicroKernelTest
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
name|api
operator|.
name|model
operator|.
name|Commit
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
name|api
operator|.
name|model
operator|.
name|Node
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
name|impl
operator|.
name|NodeAssert
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
name|impl
operator|.
name|command
operator|.
name|CommitCommand
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
name|impl
operator|.
name|model
operator|.
name|CommitBuilder
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
name|impl
operator|.
name|model
operator|.
name|MongoCommit
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
name|impl
operator|.
name|model
operator|.
name|MongoNode
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
name|util
operator|.
name|NodeBuilder
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
name|QueryBuilder
import|;
end_import

begin_class
specifier|public
class|class
name|FetchNodesActionTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|invalidFirstRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|revisionId1
init|=
name|addNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Long
name|revisionId2
init|=
name|addNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|Long
name|revisionId3
init|=
name|addNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|invalidateCommit
argument_list|(
name|revisionId1
argument_list|)
expr_stmt|;
name|updateBaseRevisionId
argument_list|(
name|revisionId2
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|createAndExecuteQuery
argument_list|(
name|revisionId3
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{\"/#%2$s\" : { \"b#%1$s\" : {}, \"c#%2$s\" : {} }}"
argument_list|,
name|revisionId2
argument_list|,
name|revisionId3
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidLastRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|addNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|Long
name|revisionId3
init|=
name|addNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|invalidateCommit
argument_list|(
name|revisionId3
argument_list|)
expr_stmt|;
try|try
block|{
name|createAndExecuteQuery
argument_list|(
name|revisionId3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected MicroKernelException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidMiddleRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|Long
name|revisionId1
init|=
name|addNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|Long
name|revisionId2
init|=
name|addNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|Long
name|revisionId3
init|=
name|addNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|invalidateCommit
argument_list|(
name|revisionId2
argument_list|)
expr_stmt|;
name|updateBaseRevisionId
argument_list|(
name|revisionId3
argument_list|,
name|revisionId1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|createAndExecuteQuery
argument_list|(
name|revisionId3
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{\"/#%2$s\" : { \"a#%1$s\" : {}, \"c#%2$s\" : {} }}"
argument_list|,
name|revisionId1
argument_list|,
name|revisionId3
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|samePrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|addNode
argument_list|(
literal|"a/b"
argument_list|)
expr_stmt|;
name|addNode
argument_list|(
literal|"a/bb"
argument_list|)
expr_stmt|;
name|long
name|rev
init|=
name|addNode
argument_list|(
literal|"a/b/c"
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
literal|0
decl_stmt|;
name|FetchNodesActionNew
name|action
init|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
name|depth
argument_list|,
name|rev
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodes
init|=
name|action
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|=
literal|1
expr_stmt|;
name|action
operator|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
name|depth
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|action
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
name|depth
operator|=
name|FetchNodesActionNew
operator|.
name|LIMITLESS_DEPTH
expr_stmt|;
name|action
operator|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
name|depth
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|action
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/b/c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|"/a/bb"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// FIXME - Revisit this test.
annotation|@
name|Test
specifier|public
name|void
name|fetchRootAndAllDepths
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|firstRevisionId
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|Long
name|secondRevisionId
init|=
name|scenario
operator|.
name|update_A_and_add_D_and_E
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|createAndExecuteQuery
argument_list|(
name|firstRevisionId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : {} }"
argument_list|,
name|firstRevisionId
argument_list|)
decl_stmt|;
name|Node
name|expected
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|secondRevisionId
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : {} }"
argument_list|,
name|firstRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|firstRevisionId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 } } }"
argument_list|,
name|firstRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|secondRevisionId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%2$s\" : { \"int\" : 1 , \"double\" : 0.123 } } }"
argument_list|,
name|firstRevisionId
argument_list|,
name|secondRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|firstRevisionId
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1, \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|firstRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|secondRevisionId
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%2$s\" : { \"int\" : 1 , \"double\" : 0.123 , \"b#%2$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true }, \"d#%2$s\" : { \"null\" : null } } } }"
argument_list|,
name|firstRevisionId
argument_list|,
name|secondRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|firstRevisionId
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 , \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|firstRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|secondRevisionId
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%2$s\" : { \"int\" : 1 , \"double\" : 0.123 , \"b#%2$s\" : { \"string\" : \"foo\", \"e#%2$s\" : { \"array\" : [ 123, null, 123.456, \"for:bar\", true ] } } , \"c#%1$s\" : { \"bool\" : true }, \"d#%2$s\" : { \"null\" : null } } } }"
argument_list|,
name|firstRevisionId
argument_list|,
name|secondRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Long
name|addNode
parameter_list|(
name|String
name|nodeName
parameter_list|)
throws|throws
name|Exception
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|nodeName
operator|+
literal|"\" : {}"
argument_list|,
literal|"Add /"
operator|+
name|nodeName
argument_list|)
decl_stmt|;
name|CommitCommand
name|command
init|=
operator|new
name|CommitCommand
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
name|commit
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fetchWithCertainPathsOneRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|revisionId
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|createAndExecuteQuery
argument_list|(
name|revisionId
argument_list|,
name|getPathSet
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|,
literal|"not_existing"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 , \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
name|Node
name|expected
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|revisionId
argument_list|,
name|getPathSet
argument_list|(
literal|"/a"
argument_list|,
literal|"not_existing"
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 } } }"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fetchWithCertainPathsTwoRevisions
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|getNodeStore
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|firstRevisionId
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|Long
name|secondRevisionId
init|=
name|scenario
operator|.
name|update_A_and_add_D_and_E
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|createAndExecuteQuery
argument_list|(
name|firstRevisionId
argument_list|,
name|getPathSet
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|,
literal|"/a/d"
argument_list|,
literal|"/a/b/e"
argument_list|,
literal|"not_existing"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 , \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|firstRevisionId
argument_list|)
decl_stmt|;
name|Node
name|expected
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
name|actuals
operator|=
name|createAndExecuteQuery
argument_list|(
name|secondRevisionId
argument_list|,
name|getPathSet
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/c"
argument_list|,
literal|"/a/d"
argument_list|,
literal|"/a/b/e"
argument_list|,
literal|"not_existing"
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%2$s\" : { \"int\" : 1 , \"double\" : 0.123 , \"b#%2$s\" : { \"string\" : \"foo\" , \"e#%2$s\" : { \"array\" : [ 123, null, 123.456, \"for:bar\", true ] } } , \"c#%1$s\" : { \"bool\" : true }, \"d#%2$s\" : { \"null\" : null } } } }"
argument_list|,
name|firstRevisionId
argument_list|,
name|secondRevisionId
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeAssert
operator|.
name|assertEquals
argument_list|(
name|expecteds
argument_list|,
name|actuals
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|Node
argument_list|>
name|createAndExecuteQuery
parameter_list|(
name|long
name|revisionId
parameter_list|)
block|{
return|return
name|createAndExecuteQuery
argument_list|(
name|revisionId
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|Node
argument_list|>
name|createAndExecuteQuery
parameter_list|(
name|long
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|createAndExecuteQuery
argument_list|(
name|revisionId
argument_list|,
name|paths
argument_list|,
name|depth
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|Node
argument_list|>
name|createAndExecuteQuery
parameter_list|(
name|long
name|revisionId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
return|return
name|createAndExecuteQuery
argument_list|(
name|revisionId
argument_list|,
name|paths
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|Node
argument_list|>
name|createAndExecuteQuery
parameter_list|(
name|long
name|revisionId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|FetchNodesActionNew
name|query
init|=
operator|new
name|FetchNodesActionNew
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
name|paths
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
return|return
name|toNode
argument_list|(
name|query
operator|.
name|execute
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getPathSet
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|invalidateCommit
parameter_list|(
name|Long
name|revisionId
parameter_list|)
block|{
name|DBCollection
name|commitCollection
init|=
name|getNodeStore
argument_list|()
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|DBObject
name|query
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|is
argument_list|(
name|revisionId
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|update
operator|.
name|put
argument_list|(
literal|"$set"
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
name|MongoCommit
operator|.
name|KEY_FAILED
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
argument_list|)
expr_stmt|;
name|commitCollection
operator|.
name|update
argument_list|(
name|query
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|updateBaseRevisionId
parameter_list|(
name|Long
name|revisionId2
parameter_list|,
name|Long
name|baseRevisionId
parameter_list|)
block|{
name|DBCollection
name|commitCollection
init|=
name|getNodeStore
argument_list|()
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|DBObject
name|query
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|is
argument_list|(
name|revisionId2
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"$set"
argument_list|,
operator|new
name|BasicDBObject
argument_list|(
name|MongoCommit
operator|.
name|KEY_BASE_REVISION_ID
argument_list|,
name|baseRevisionId
argument_list|)
argument_list|)
decl_stmt|;
name|commitCollection
operator|.
name|update
argument_list|(
name|query
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|Node
argument_list|>
name|toNode
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodeMongos
parameter_list|)
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|nodeMongos
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MongoNode
name|nodeMongo
range|:
name|nodeMongos
operator|.
name|values
argument_list|()
control|)
block|{
name|Node
name|node
init|=
name|MongoNode
operator|.
name|toNode
argument_list|(
name|nodeMongo
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
block|}
end_class

end_unit

