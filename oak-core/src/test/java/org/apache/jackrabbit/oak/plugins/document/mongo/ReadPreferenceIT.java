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
operator|.
name|mongo
package|;
end_package

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
name|Collection
operator|.
name|SETTINGS
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
name|mongo
operator|.
name|MongoDocumentStore
operator|.
name|DocumentReadPreference
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

begin_class
specifier|public
class|class
name|ReadPreferenceIT
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
name|MongoDocumentStore
name|mongoDS
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|long
name|replicationLag
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
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|replicationLag
operator|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|mk
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMaxReplicationLag
argument_list|(
name|replicationLag
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setLeaseCheck
argument_list|(
literal|false
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
name|mongoDS
operator|=
operator|(
name|MongoDocumentStore
operator|)
name|mk
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreferenceConversion
parameter_list|()
throws|throws
name|Exception
block|{
comment|//For cacheAge< replicationLag result should be primary
name|assertEquals
argument_list|(
name|DocumentReadPreference
operator|.
name|PRIMARY
argument_list|,
name|mongoDS
operator|.
name|getReadPreference
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocumentReadPreference
operator|.
name|PRIMARY
argument_list|,
name|mongoDS
operator|.
name|getReadPreference
argument_list|(
call|(
name|int
call|)
argument_list|(
name|replicationLag
operator|-
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//For Integer.MAX_VALUE it should be secondary as caller intends that value is stable
name|assertEquals
argument_list|(
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY
argument_list|,
name|mongoDS
operator|.
name|getReadPreference
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|//For all other cases depends on age
name|assertEquals
argument_list|(
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|,
name|mongoDS
operator|.
name|getReadPreference
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|,
name|mongoDS
operator|.
name|getReadPreference
argument_list|(
call|(
name|int
call|)
argument_list|(
name|replicationLag
operator|+
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMongoReadPreferencesDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PRIMARY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primaryPreferred
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_PRIMARY
argument_list|)
argument_list|)
expr_stmt|;
comment|//By default Mongo read preference is primary
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY
argument_list|)
argument_list|)
expr_stmt|;
comment|//Change the default and assert again
name|mongoDS
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getDB
argument_list|()
operator|.
name|setReadPreference
argument_list|(
name|ReadPreference
operator|.
name|secondary
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|secondary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY
argument_list|)
argument_list|)
expr_stmt|;
comment|//for case where parent age cannot be determined the preference should be primary
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|)
argument_list|)
expr_stmt|;
comment|//For collection other than NODES always primary
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|SETTINGS
argument_list|,
literal|"foo"
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMongoReadPreferencesWithAge
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Change the default
name|ReadPreference
name|testPref
init|=
name|ReadPreference
operator|.
name|secondary
argument_list|()
decl_stmt|;
name|mongoDS
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getDB
argument_list|()
operator|.
name|setReadPreference
argument_list|(
name|testPref
argument_list|)
expr_stmt|;
name|NodeStore
name|nodeStore
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|nodeStore
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
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|b1
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
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/x/y"
argument_list|)
decl_stmt|;
name|String
name|parentId
init|=
name|Utils
operator|.
name|getParentId
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|mongoDS
operator|.
name|invalidateCache
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|//For modifiedTime< replicationLag primary must be used
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
name|parentId
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|)
argument_list|)
expr_stmt|;
comment|//Going into future to make parent /x old enough
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
operator|+
name|replicationLag
argument_list|)
expr_stmt|;
name|mongoDS
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
comment|//For old modified nodes secondaries should be preferred
name|assertEquals
argument_list|(
name|testPref
argument_list|,
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
name|parentId
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadWriteMode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getConfiguredReadPreference
argument_list|(
name|NODES
argument_list|)
argument_list|)
expr_stmt|;
name|mongoDS
operator|.
name|setReadWriteMode
argument_list|(
literal|"readPreference=secondary&w=2&safe=true&j=true"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|secondary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getReadPreference
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mongoDS
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getWriteConcern
argument_list|()
operator|.
name|getW
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mongoDS
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getWriteConcern
argument_list|()
operator|.
name|getJ
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|secondary
argument_list|()
argument_list|,
name|mongoDS
operator|.
name|getConfiguredReadPreference
argument_list|(
name|NODES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

