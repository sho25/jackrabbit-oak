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
name|com
operator|.
name|mongodb
operator|.
name|Mongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadConcern
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReplicaSetStatus
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
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
name|assertTrue
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|MongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|hasWriteConcern
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|MongoConnection
operator|.
name|hasWriteConcern
argument_list|(
literal|"mongodb://localhost:27017/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoConnection
operator|.
name|hasWriteConcern
argument_list|(
literal|"mongodb://localhost:27017/foo?w=1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|hasReadConcern
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|MongoConnection
operator|.
name|hasReadConcern
argument_list|(
literal|"mongodb://localhost:27017/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|MongoConnection
operator|.
name|hasReadConcern
argument_list|(
literal|"mongodb://localhost:27017/foo?readconcernlevel=majority"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sufficientWriteConcern
parameter_list|()
throws|throws
name|Exception
block|{
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|ACKNOWLEDGED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|JOURNALED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|MAJORITY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|FSYNC_SAFE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|FSYNCED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|JOURNAL_SAFE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|NORMAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|REPLICA_ACKNOWLEDGED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|REPLICAS_SAFE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|SAFE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernReplicaSet
argument_list|(
name|WriteConcern
operator|.
name|UNACKNOWLEDGED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|ACKNOWLEDGED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|JOURNALED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|MAJORITY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|FSYNC_SAFE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|FSYNCED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|JOURNAL_SAFE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|NORMAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|REPLICA_ACKNOWLEDGED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|REPLICAS_SAFE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|SAFE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientWriteConcernSingleNode
argument_list|(
name|WriteConcern
operator|.
name|UNACKNOWLEDGED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sufficientReadConcern
parameter_list|()
throws|throws
name|Exception
block|{
name|sufficientReadConcernReplicaSet
argument_list|(
name|ReadConcern
operator|.
name|DEFAULT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientReadConcernReplicaSet
argument_list|(
name|ReadConcern
operator|.
name|LOCAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sufficientReadConcernReplicaSet
argument_list|(
name|ReadConcern
operator|.
name|MAJORITY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientReadConcernSingleNode
argument_list|(
name|ReadConcern
operator|.
name|DEFAULT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientReadConcernSingleNode
argument_list|(
name|ReadConcern
operator|.
name|LOCAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sufficientReadConcernSingleNode
argument_list|(
name|ReadConcern
operator|.
name|MAJORITY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|socketKeepAlive
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|MongoClientOptions
operator|.
name|Builder
name|options
init|=
name|MongoConnection
operator|.
name|getDefaultBuilder
argument_list|()
decl_stmt|;
name|options
operator|.
name|socketKeepAlive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MongoConnection
name|c
init|=
operator|new
name|MongoConnection
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|,
name|options
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|getMongo
argument_list|()
operator|.
name|getMongoOptions
argument_list|()
operator|.
name|isSocketKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// default is without keep-alive
name|c
operator|=
operator|new
name|MongoConnection
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|)
expr_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|getMongo
argument_list|()
operator|.
name|getMongoOptions
argument_list|()
operator|.
name|isSocketKeepAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|sufficientWriteConcernReplicaSet
parameter_list|(
name|WriteConcern
name|w
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|sufficientWriteConcern
argument_list|(
name|w
argument_list|,
literal|true
argument_list|,
name|sufficient
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sufficientWriteConcernSingleNode
parameter_list|(
name|WriteConcern
name|w
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|sufficientWriteConcern
argument_list|(
name|w
argument_list|,
literal|false
argument_list|,
name|sufficient
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sufficientWriteConcern
parameter_list|(
name|WriteConcern
name|w
parameter_list|,
name|boolean
name|replicaSet
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|DB
name|db
init|=
name|mockDB
argument_list|(
name|ReadConcern
operator|.
name|DEFAULT
argument_list|,
name|w
argument_list|,
name|replicaSet
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sufficient
argument_list|,
name|MongoConnection
operator|.
name|hasSufficientWriteConcern
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sufficientReadConcernReplicaSet
parameter_list|(
name|ReadConcern
name|r
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|sufficientReadConcern
argument_list|(
name|r
argument_list|,
literal|true
argument_list|,
name|sufficient
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sufficientReadConcernSingleNode
parameter_list|(
name|ReadConcern
name|r
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|sufficientReadConcern
argument_list|(
name|r
argument_list|,
literal|false
argument_list|,
name|sufficient
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sufficientReadConcern
parameter_list|(
name|ReadConcern
name|r
parameter_list|,
name|boolean
name|replicaSet
parameter_list|,
name|boolean
name|sufficient
parameter_list|)
block|{
name|DB
name|db
init|=
name|mockDB
argument_list|(
name|r
argument_list|,
name|replicaSet
condition|?
name|WriteConcern
operator|.
name|MAJORITY
else|:
name|WriteConcern
operator|.
name|W1
argument_list|,
name|replicaSet
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sufficient
argument_list|,
name|MongoConnection
operator|.
name|hasSufficientReadConcern
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DB
name|mockDB
parameter_list|(
name|ReadConcern
name|r
parameter_list|,
name|WriteConcern
name|w
parameter_list|,
name|boolean
name|replicaSet
parameter_list|)
block|{
name|ReplicaSetStatus
name|status
decl_stmt|;
if|if
condition|(
name|replicaSet
condition|)
block|{
name|status
operator|=
name|mock
argument_list|(
name|ReplicaSetStatus
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|status
operator|=
literal|null
expr_stmt|;
block|}
name|DB
name|db
init|=
name|mock
argument_list|(
name|DB
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mongo
name|mongo
init|=
name|mock
argument_list|(
name|Mongo
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|db
operator|.
name|getMongo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mongo
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|db
operator|.
name|getWriteConcern
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|db
operator|.
name|getReadConcern
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mongo
operator|.
name|getReplicaSetStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|db
return|;
block|}
block|}
end_class

end_unit

