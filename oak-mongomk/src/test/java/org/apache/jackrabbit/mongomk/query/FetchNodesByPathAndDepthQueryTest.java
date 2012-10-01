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
name|query
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
name|mongomk
operator|.
name|BaseMongoTest
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
name|command
operator|.
name|CommitCommandMongo
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
name|builder
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
name|builder
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
name|mongomk
operator|.
name|model
operator|.
name|CommitMongo
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
name|model
operator|.
name|NodeMongo
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
name|scenario
operator|.
name|SimpleNodeScenario
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
name|FetchNodesByPathAndDepthQueryTest
extends|extends
name|BaseMongoTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|fetchWithInvalidFirstRevision
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
name|FetchNodesByPathAndDepthQuery
name|query
init|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|revisionId3
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|query
operator|.
name|execute
argument_list|()
argument_list|)
decl_stmt|;
comment|//String json = String.format("{\"/#%1$s\" : { \"a#%2$s\" : {}, \"b#%3$s\" : {}, \"c#%1$s\" : {} }}", revisionId3, revisionId1, revisionId2);
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
name|Set
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
name|getDescendants
argument_list|(
literal|true
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
name|fetchWithInvalidLastRevision
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
name|revisionId3
argument_list|)
expr_stmt|;
name|FetchNodesByPathAndDepthQuery
name|query
init|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|revisionId3
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|query
operator|.
name|execute
argument_list|()
argument_list|)
decl_stmt|;
comment|//String json = String.format("{\"/#%1$s\" : { \"a#%2$s\" : {}, \"b#%3$s\" : {}, \"c#%1$s\" : {} }}", revisionId3, revisionId1, revisionId2);
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
literal|"{\"/#%2$s\" : { \"a#%1$s\" : {}, \"b#%2$s\" : {} }}"
argument_list|,
name|revisionId1
argument_list|,
name|revisionId2
argument_list|)
decl_stmt|;
name|Set
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
name|getDescendants
argument_list|(
literal|true
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
name|fetchWithInvalidMiddleRevision
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
name|FetchNodesByPathAndDepthQuery
name|query
init|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|revisionId3
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|query
operator|.
name|execute
argument_list|()
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
name|Set
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
name|getDescendants
argument_list|(
literal|true
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
name|simpleFetchRootAndAllDepths
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
name|mongoConnection
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
name|FetchNodesByPathAndDepthQuery
name|query
init|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|firstRevisionId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeMongo
argument_list|>
name|result
init|=
name|query
operator|.
name|execute
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|actuals
init|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|Node
name|expected
init|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : {} }"
argument_list|,
name|firstRevisionId
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Node
argument_list|>
name|expecteds
init|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|secondRevisionId
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : {} }"
argument_list|,
name|firstRevisionId
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|firstRevisionId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 } } }"
argument_list|,
name|firstRevisionId
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|secondRevisionId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
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
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|firstRevisionId
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1, \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|firstRevisionId
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|secondRevisionId
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
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
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|firstRevisionId
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"{ \"/#%1$s\" : { \"a#%1$s\" : { \"int\" : 1 , \"b#%1$s\" : { \"string\" : \"foo\" } , \"c#%1$s\" : { \"bool\" : true } } } }"
argument_list|,
name|firstRevisionId
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|query
operator|=
operator|new
name|FetchNodesByPathAndDepthQuery
argument_list|(
name|mongoConnection
argument_list|,
literal|"/"
argument_list|,
name|secondRevisionId
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|execute
argument_list|()
expr_stmt|;
name|actuals
operator|=
name|NodeMongo
operator|.
name|toNode
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|expected
operator|=
name|NodeBuilder
operator|.
name|build
argument_list|(
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
argument_list|)
expr_stmt|;
name|expecteds
operator|=
name|expected
operator|.
name|getDescendants
argument_list|(
literal|true
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
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
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
name|mongoConnection
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
name|CommitMongo
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
name|CommitMongo
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
name|mongoConnection
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
name|CommitMongo
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
name|CommitMongo
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
block|}
end_class

end_unit

