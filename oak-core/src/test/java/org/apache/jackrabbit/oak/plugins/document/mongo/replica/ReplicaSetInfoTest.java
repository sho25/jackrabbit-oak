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
operator|.
name|replica
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|transformValues
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
name|replica
operator|.
name|ReplicaSetInfo
operator|.
name|MemberState
operator|.
name|PRIMARY
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
name|replica
operator|.
name|ReplicaSetInfo
operator|.
name|MemberState
operator|.
name|RECOVERING
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
name|replica
operator|.
name|ReplicaSetInfo
operator|.
name|MemberState
operator|.
name|SECONDARY
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
name|assertTrue
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|RevisionVector
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
name|replica
operator|.
name|ReplicaSetInfo
operator|.
name|MemberState
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
name|bson
operator|.
name|BasicBSONObject
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
name|mockito
operator|.
name|Mockito
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
name|Function
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
name|DB
import|;
end_import

begin_class
specifier|public
class|class
name|ReplicaSetInfoTest
block|{
specifier|private
name|ReplicaSetInfo
name|replica
decl_stmt|;
specifier|private
name|ReplicationSetStatusMock
name|replicationSet
decl_stmt|;
specifier|private
name|Clock
operator|.
name|Virtual
name|clock
decl_stmt|;
specifier|private
name|Clock
operator|.
name|Virtual
name|mongoClock
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|resetEstimator
parameter_list|()
block|{
name|clock
operator|=
name|mongoClock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
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
name|when
argument_list|(
name|db
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"oak-db"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|db
operator|.
name|getSisterDB
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|replica
operator|=
operator|new
name|ReplicaSetInfo
argument_list|(
name|clock
argument_list|,
name|db
argument_list|,
literal|null
argument_list|,
literal|0l
argument_list|,
literal|0l
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BasicDBObject
name|getReplicaStatus
parameter_list|()
block|{
name|BasicDBObject
name|obj
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|obj
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
name|mongoClock
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|obj
operator|.
name|put
argument_list|(
literal|"members"
argument_list|,
name|replicationSet
operator|.
name|members
argument_list|)
expr_stmt|;
return|return
name|obj
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Timestamped
argument_list|<
name|RevisionVector
argument_list|>
argument_list|>
name|getRootRevisions
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|hosts
parameter_list|)
block|{
return|return
name|transformValues
argument_list|(
name|replicationSet
operator|.
name|memberRevisions
argument_list|,
operator|new
name|Function
argument_list|<
name|RevisionBuilder
argument_list|,
name|Timestamped
argument_list|<
name|RevisionVector
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Timestamped
argument_list|<
name|RevisionVector
argument_list|>
name|apply
parameter_list|(
name|RevisionBuilder
name|input
parameter_list|)
block|{
return|return
operator|new
name|Timestamped
argument_list|<
name|RevisionVector
argument_list|>
argument_list|(
name|input
operator|.
name|revs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|replica
operator|.
name|hiddenMembers
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMinimumRevision
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|20
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|20
argument_list|,
literal|18
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m2"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|20
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
expr_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|replica
operator|.
name|getMinimumRootRevisions
argument_list|()
operator|.
name|getRevision
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replica
operator|.
name|getMinimumRootRevisions
argument_list|()
operator|.
name|getRevision
argument_list|(
literal|1
argument_list|)
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|replica
operator|.
name|getMinimumRootRevisions
argument_list|()
operator|.
name|getRevision
argument_list|(
literal|2
argument_list|)
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMoreRecentThan
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|15
argument_list|,
literal|21
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|21
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m2"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|15
argument_list|,
literal|14
argument_list|,
literal|13
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m3"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|14
argument_list|,
literal|13
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|replica
operator|.
name|isMoreRecentThan
argument_list|(
name|lastRev
argument_list|(
literal|9
argument_list|,
literal|13
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|replica
operator|.
name|isMoreRecentThan
argument_list|(
name|lastRev
argument_list|(
literal|11
argument_list|,
literal|14
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnknownStateIsNotSafe
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|21
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|RECOVERING
argument_list|,
literal|"m2"
argument_list|)
expr_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|replica
operator|.
name|getMinimumRootRevisions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|replica
operator|.
name|isMoreRecentThan
argument_list|(
name|lastRev
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyIsNotSafe
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"m1"
argument_list|)
expr_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|replica
operator|.
name|getMinimumRootRevisions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|replica
operator|.
name|isMoreRecentThan
argument_list|(
name|lastRev
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOldestNotReplicated
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|5
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m2"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|2
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|replica
operator|.
name|secondariesSafeTimestamp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllSecondariesUpToDate
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m2"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|long
name|before
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|long
name|after
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertBetween
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|replica
operator|.
name|secondariesSafeTimestamp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllSecondariesUpToDateWithTimediff
parameter_list|()
block|{
name|addInstance
argument_list|(
name|PRIMARY
argument_list|,
literal|"mp"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m1"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|addInstance
argument_list|(
name|SECONDARY
argument_list|,
literal|"m2"
argument_list|)
operator|.
name|addRevisions
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|mongoClock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|mongoClock
operator|.
name|waitUntil
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|long
name|before
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|updateRevisions
argument_list|()
expr_stmt|;
name|long
name|after
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertBetween
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|replica
operator|.
name|secondariesSafeTimestamp
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RevisionBuilder
name|addInstance
parameter_list|(
name|MemberState
name|state
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|replicationSet
operator|==
literal|null
condition|)
block|{
name|replicationSet
operator|=
operator|new
name|ReplicationSetStatusMock
argument_list|()
expr_stmt|;
block|}
return|return
name|replicationSet
operator|.
name|addInstance
argument_list|(
name|state
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
name|void
name|updateRevisions
parameter_list|()
block|{
name|replica
operator|.
name|updateReplicaStatus
argument_list|()
expr_stmt|;
name|replicationSet
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
specifier|static
name|RevisionVector
name|lastRev
parameter_list|(
name|int
modifier|...
name|timestamps
parameter_list|)
block|{
return|return
operator|new
name|RevisionBuilder
argument_list|()
operator|.
name|addRevisions
argument_list|(
name|timestamps
argument_list|)
operator|.
name|revs
return|;
block|}
specifier|private
specifier|static
name|void
name|assertBetween
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|,
name|long
name|actual
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%d<= %d<= %d"
argument_list|,
name|from
argument_list|,
name|actual
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|from
operator|<=
name|actual
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|actual
operator|<=
name|to
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|ReplicationSetStatusMock
block|{
specifier|private
name|List
argument_list|<
name|BasicBSONObject
argument_list|>
name|members
init|=
operator|new
name|ArrayList
argument_list|<
name|BasicBSONObject
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|RevisionBuilder
argument_list|>
name|memberRevisions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RevisionBuilder
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|RevisionBuilder
name|addInstance
parameter_list|(
name|MemberState
name|state
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|BasicBSONObject
name|member
init|=
operator|new
name|BasicBSONObject
argument_list|()
decl_stmt|;
name|member
operator|.
name|put
argument_list|(
literal|"stateStr"
argument_list|,
name|state
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|member
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|members
operator|.
name|add
argument_list|(
name|member
argument_list|)
expr_stmt|;
name|RevisionBuilder
name|builder
init|=
operator|new
name|RevisionBuilder
argument_list|()
decl_stmt|;
name|memberRevisions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|RevisionBuilder
block|{
specifier|private
name|RevisionVector
name|revs
init|=
operator|new
name|RevisionVector
argument_list|()
decl_stmt|;
specifier|private
name|RevisionBuilder
name|addRevisions
parameter_list|(
name|int
modifier|...
name|timestamps
parameter_list|)
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
name|timestamps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addRevision
argument_list|(
name|timestamps
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|private
name|RevisionBuilder
name|addRevision
parameter_list|(
name|int
name|timestamp
parameter_list|,
name|int
name|counter
parameter_list|,
name|int
name|clusterId
parameter_list|,
name|boolean
name|branch
parameter_list|)
block|{
name|Revision
name|rev
init|=
operator|new
name|Revision
argument_list|(
name|timestamp
argument_list|,
name|counter
argument_list|,
name|clusterId
argument_list|,
name|branch
argument_list|)
decl_stmt|;
name|revs
operator|=
name|revs
operator|.
name|update
argument_list|(
name|rev
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

