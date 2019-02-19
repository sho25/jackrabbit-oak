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
name|LeaseCheckMode
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
name|util
operator|.
name|MongoConnection
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
operator|.
name|getIdFromPath
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
name|DocumentMK
name|mk2
decl_stmt|;
specifier|private
name|Clock
name|clock
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUpConnection
parameter_list|()
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|setRevisionClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|setClusterNodeInfoClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mongoConnection
argument_list|)
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDBName
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setClientSessionDisabled
argument_list|(
literal|true
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongoConnection
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|mongoConnection
operator|.
name|getDBName
argument_list|()
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
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
comment|// use a separate connection for cluster node 2
name|MongoConnection
name|mongoConnection2
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|mk2
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|setClientSessionDisabled
argument_list|(
literal|true
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongoConnection2
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|mongoConnection2
operator|.
name|getDBName
argument_list|()
argument_list|)
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|open
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
comment|// reset readWrite mode before shutting down
name|mongoDS
operator|.
name|setReadWriteMode
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMongoReadPreferencesDefault
parameter_list|()
block|{
comment|// start with read preference set to primary
name|mongoDS
operator|.
name|setReadWriteMode
argument_list|(
name|rwMode
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|setReadWriteMode
argument_list|(
name|rwMode
argument_list|(
name|ReadPreference
operator|.
name|secondary
argument_list|()
argument_list|)
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
name|testReadWriteMode
parameter_list|()
block|{
name|mongoDS
operator|.
name|setReadWriteMode
argument_list|(
name|rwMode
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|Boolean
name|journal
init|=
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
name|getJournal
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|journal
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
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
annotation|@
name|Ignore
argument_list|(
literal|"OAK-8060"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|getMongoReadPreference
parameter_list|()
block|{
name|String
name|id
init|=
name|getIdFromPath
argument_list|(
literal|"/does/not/exist"
argument_list|)
decl_stmt|;
name|mongoDS
operator|.
name|setReadWriteMode
argument_list|(
name|rwMode
argument_list|(
name|ReadPreference
operator|.
name|secondaryPreferred
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mongoDS
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ReadPreference
name|readPref
init|=
name|mongoDS
operator|.
name|getMongoReadPreference
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|,
name|DocumentReadPreference
operator|.
name|PREFER_SECONDARY_IF_OLD_ENOUGH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|,
name|readPref
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|rwMode
parameter_list|(
name|ReadPreference
name|preference
parameter_list|)
block|{
return|return
literal|"readpreference="
operator|+
name|preference
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

