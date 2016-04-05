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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|alwaysFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test for OAK-3882  */
end_comment

begin_class
specifier|public
class|class
name|CollisionWithSplitTest
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
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
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
name|collisionAfterSplit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_NODES
init|=
literal|10
decl_stmt|;
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
name|b1
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
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
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|Revision
name|conflictRev
init|=
literal|null
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
name|setProperty
argument_list|(
literal|"p"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// assume a conflict is detected while writing on ns2
comment|// -> remember this revision
name|conflictRev
operator|=
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
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|conflictRev
argument_list|)
expr_stmt|;
comment|// run document split on ns1
name|DocumentStore
name|store
init|=
name|ns1
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|ops
init|=
name|SplitOperations
operator|.
name|forDocument
argument_list|(
name|doc
argument_list|,
name|ns1
argument_list|,
name|ns1
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
name|Predicates
operator|.
expr|<
name|String
operator|>
name|alwaysFalse
argument_list|()
argument_list|,
name|NUM_NODES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ops
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
operator|!
name|op
operator|.
name|isNew
argument_list|()
operator|||
operator|!
name|store
operator|.
name|create
argument_list|(
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
condition|)
block|{
name|store
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
comment|// attempt to set a property on a removed node
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/node-0"
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
literal|false
argument_list|)
decl_stmt|;
name|Revision
name|ourRev
init|=
name|ns2
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"p"
argument_list|,
name|ourRev
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setModified
argument_list|(
name|op
argument_list|,
name|ourRev
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setCommitRoot
argument_list|(
name|op
argument_list|,
name|ourRev
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ns2
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|findAndUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
comment|// now try to set a collision marker for the
comment|// committed revision on ns2
name|doc
operator|=
name|ns2
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getLocalCommitRoot
argument_list|()
operator|.
name|containsKey
argument_list|(
name|conflictRev
argument_list|)
argument_list|)
expr_stmt|;
name|Collision
name|c
init|=
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|conflictRev
argument_list|,
name|op
argument_list|,
name|ourRev
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Collision must match our revision ("
operator|+
name|ourRev
operator|+
literal|"). "
operator|+
literal|"The conflict revision "
operator|+
name|conflictRev
operator|+
literal|" is already committed."
argument_list|,
name|ourRev
argument_list|,
name|c
operator|.
name|mark
argument_list|(
name|ns2
operator|.
name|getDocumentStore
argument_list|()
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
return|return
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

