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
name|io
operator|.
name|IOException
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
name|TimeUnit
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
comment|/**  * Test for OAK-2421.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|OrphanedBranchTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OrphanedBranchTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
decl_stmt|;
specifier|public
name|OrphanedBranchTest
parameter_list|(
name|DocumentStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|DocumentStoreFixture
operator|.
name|MemoryFixture
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|DocumentStoreFixture
name|mongo
init|=
operator|new
name|DocumentStoreFixture
operator|.
name|MongoFixture
argument_list|()
decl_stmt|;
if|if
condition|(
name|mongo
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|mongo
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|fixtures
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|fixture
operator|.
name|createDocumentStore
argument_list|()
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|gc
operator|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orphanedBranches
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numCreated
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|int
name|numBranches
init|=
name|store
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// perform changes until a branch is created
while|while
condition|(
name|store
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|numBranches
condition|)
block|{
name|child
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|count
operator|++
argument_list|)
expr_stmt|;
block|}
comment|// but do not merge!
name|numCreated
operator|++
expr_stmt|;
comment|// commit a change to get new head revision.
comment|// collisions will only be cleaned up if the head
comment|// revision is newer.
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|numCreated
argument_list|)
expr_stmt|;
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
name|numBranches
operator|=
name|store
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// after background ops we must not see more collisions
comment|// than active branches
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|store
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|collisions
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|COLLISIONS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"too many collisions: "
operator|+
name|collisions
operator|.
name|size
argument_list|()
argument_list|,
name|collisions
operator|.
name|size
argument_list|()
operator|<=
name|numBranches
argument_list|)
expr_stmt|;
comment|// split ops must remove orphaned changes
comment|// limit to check is number of branches considered active
comment|// plus NodeDocument.NUM_REVS_THRESHOLD
name|int
name|limit
init|=
name|numBranches
operator|+
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
decl_stmt|;
name|id
operator|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|doc
operator|=
name|store
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
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|map
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
literal|"prop"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"too many orphaned changes: "
operator|+
name|map
operator|.
name|size
argument_list|()
operator|+
literal|"> "
operator|+
name|limit
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|<=
name|limit
argument_list|)
expr_stmt|;
name|map
operator|=
name|doc
operator|.
name|getLocalCommitRoot
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"too many orphaned commit root entries: "
operator|+
name|map
operator|.
name|size
argument_list|()
operator|+
literal|"> "
operator|+
name|limit
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|<=
name|limit
argument_list|)
expr_stmt|;
comment|// run garbage collector once in a while for changes on /bar
if|if
condition|(
name|numCreated
operator|%
name|NodeDocument
operator|.
name|NUM_REVS_THRESHOLD
operator|==
literal|0
condition|)
block|{
name|gc
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"created {}, still considered active: {}"
argument_list|,
name|numCreated
argument_list|,
name|store
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create orphaned branches, until we were able to
comment|// collect 500 of them (or 100 for MongoDB and other fixtures)
name|int
name|collect
init|=
name|fixture
operator|instanceof
name|DocumentStoreFixture
operator|.
name|MemoryFixture
condition|?
literal|500
else|:
literal|100
decl_stmt|;
if|if
condition|(
name|numCreated
operator|-
name|store
operator|.
name|getBranches
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|collect
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

