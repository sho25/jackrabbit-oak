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
operator|.
name|mongo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|api
operator|.
name|CommitFailedException
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
name|AbstractMongoConnectionTest
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
name|DocumentNodeStore
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
name|MongoUtils
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
name|util
operator|.
name|Utils
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|spi
operator|.
name|commit
operator|.
name|EmptyHook
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
name|spi
operator|.
name|state
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|NodeDocument
operator|.
name|COLLISIONS
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
name|assertFalse
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

begin_comment
comment|/**  * Test for OAK-3344  */
end_comment

begin_class
specifier|public
class|class
name|CollisionMarkerTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|DocumentNodeStore
name|ns1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns2
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUpConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
name|newDocumentMK
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ns1
operator|=
name|mk
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|ns2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|conditionalCollisionUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// initialize second node store after background ops
comment|// on ns1. this makes sure ns2 sees all changes done so far
name|ns2
operator|=
name|newDocumentMK
argument_list|(
name|connectionFactory
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|,
literal|3
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|b1
operator|=
name|ns1
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|Revision
name|head
init|=
name|ns1
operator|.
name|getHeadRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|ns1
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b2
init|=
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|ns2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|String
name|rootId
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|NodeDocument
name|root
init|=
name|ns2
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|rootId
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"root document must not have a collision marker for a"
operator|+
literal|" committed revision"
argument_list|,
name|root
operator|.
name|getValueMap
argument_list|(
name|COLLISIONS
argument_list|)
operator|.
name|containsKey
argument_list|(
name|head
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|DocumentMK
name|newDocumentMK
parameter_list|(
name|DB
name|db
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
comment|// do not retry on conflicts
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|mk
return|;
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

