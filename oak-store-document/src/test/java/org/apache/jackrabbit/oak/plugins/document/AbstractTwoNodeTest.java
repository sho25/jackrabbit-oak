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
name|mongodb
operator|.
name|ReadPreference
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
name|MongoTestUtils
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
name|stats
operator|.
name|Clock
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
name|Ignore
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

begin_comment
comment|/**  * A base class for two node cluster tests with a virtual clock.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"This base test does not have tests"
argument_list|)
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AbstractTwoNodeTest
block|{
specifier|protected
specifier|final
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|protected
name|DocumentStore
name|store1
decl_stmt|;
specifier|protected
name|DocumentStore
name|store2
decl_stmt|;
specifier|protected
name|DocumentNodeStore
name|ds1
decl_stmt|;
specifier|protected
name|DocumentNodeStore
name|ds2
decl_stmt|;
specifier|protected
name|int
name|c1Id
decl_stmt|;
specifier|protected
name|int
name|c2Id
decl_stmt|;
specifier|protected
name|Clock
name|clock
decl_stmt|;
specifier|public
name|AbstractTwoNodeTest
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
comment|/**      * Can be overwritten by tests to customize / wrap the store.      *      * @param store the store to customize.      * @return the customized store.      */
specifier|protected
name|DocumentStore
name|customize
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
return|return
name|store
return|;
block|}
comment|//----------------------------------------< Set Up>
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
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
name|rdb
init|=
operator|new
name|DocumentStoreFixture
operator|.
name|RDBFixture
argument_list|(
literal|"RDB-H2(file)"
argument_list|,
literal|"jdbc:h2:file:./target/ds-test"
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|rdb
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
name|rdb
block|}
argument_list|)
expr_stmt|;
block|}
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
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|MongoUtils
operator|.
name|DB
argument_list|)
expr_stmt|;
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|store1
operator|=
name|fixture
operator|.
name|createDocumentStore
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fixture
operator|.
name|hasSinglePersistence
argument_list|()
condition|)
block|{
name|store2
operator|=
name|store1
expr_stmt|;
block|}
else|else
block|{
name|store2
operator|=
name|fixture
operator|.
name|createDocumentStore
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|ds1
operator|=
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
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|wrap
argument_list|(
name|customize
argument_list|(
name|store1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|c1Id
operator|=
name|ds1
operator|.
name|getClusterId
argument_list|()
expr_stmt|;
name|ds2
operator|=
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
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|wrap
argument_list|(
name|customize
argument_list|(
name|store2
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|c2Id
operator|=
name|ds2
operator|.
name|getClusterId
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
name|ds1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ds2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|store1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|store2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|fixture
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|DocumentStore
name|wrap
parameter_list|(
name|DocumentStore
name|ds
parameter_list|)
block|{
comment|// Enforce primary read preference because this test assumes causal
comment|// consistent reads across multiple document stores. Otherwise this
comment|// test fails on a replica set with secondary read preference
name|MongoTestUtils
operator|.
name|setReadPreference
argument_list|(
name|ds
argument_list|,
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocumentStoreTestWrapper
argument_list|(
name|ds
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|DocumentStoreTestWrapper
extends|extends
name|DocumentStoreWrapper
block|{
name|DocumentStoreTestWrapper
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

