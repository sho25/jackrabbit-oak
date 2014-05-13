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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|transform
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
operator|.
name|start
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
name|ClusterNodeInfo
operator|.
name|RecoverLockState
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
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCursor
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
name|ClusterNodeInfo
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
name|Collection
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
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|Document
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
name|MissingLastRevSeeker
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
name|util
operator|.
name|CloseableIterable
import|;
end_import

begin_comment
comment|/**  * Mongo specific version of MissingLastRevSeeker which uses mongo queries  * to fetch candidates which may have missed '_lastRev' updates.  *   * Uses a time range to find documents modified during that interval.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMissingLastRevSeeker
extends|extends
name|MissingLastRevSeeker
block|{
specifier|private
specifier|final
name|MongoDocumentStore
name|store
decl_stmt|;
specifier|public
name|MongoMissingLastRevSeeker
parameter_list|(
name|MongoDocumentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CloseableIterable
argument_list|<
name|NodeDocument
argument_list|>
name|getCandidates
parameter_list|(
specifier|final
name|long
name|startTime
parameter_list|,
specifier|final
name|long
name|endTime
parameter_list|)
block|{
name|DBObject
name|query
init|=
name|start
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|Commit
operator|.
name|getModifiedInSecs
argument_list|(
name|endTime
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|)
operator|.
name|greaterThanEquals
argument_list|(
name|Commit
operator|.
name|getModifiedInSecs
argument_list|(
name|startTime
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBObject
name|sortFields
init|=
operator|new
name|BasicDBObject
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|DBCursor
name|cursor
init|=
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|sort
argument_list|(
name|sortFields
argument_list|)
operator|.
name|setReadPreference
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|CloseableIterable
operator|.
name|wrap
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
name|store
operator|.
name|convertFromDBObject
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|cursor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acquireRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|QueryBuilder
name|query
init|=
name|start
argument_list|()
operator|.
name|and
argument_list|(
name|start
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
operator|.
name|is
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|clusterId
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|start
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_LOCK
argument_list|)
operator|.
name|notEquals
argument_list|(
name|RecoverLockState
operator|.
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|DBObject
name|returnFields
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|returnFields
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|BasicDBObject
name|setUpdates
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|setUpdates
operator|.
name|append
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_LOCK
argument_list|,
name|RecoverLockState
operator|.
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|BasicDBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|update
operator|.
name|append
argument_list|(
literal|"$set"
argument_list|,
name|setUpdates
argument_list|)
expr_stmt|;
name|DBObject
name|oldNode
init|=
name|getClusterNodeCollection
argument_list|()
operator|.
name|findAndModify
argument_list|(
name|query
operator|.
name|get
argument_list|()
argument_list|,
name|returnFields
argument_list|,
literal|null
comment|/*sort*/
argument_list|,
literal|false
comment|/*remove*/
argument_list|,
name|update
argument_list|,
literal|false
comment|/*returnNew*/
argument_list|,
literal|false
comment|/*upsert*/
argument_list|)
decl_stmt|;
return|return
name|oldNode
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
name|QueryBuilder
name|query
init|=
name|start
argument_list|(
name|ClusterNodeInfo
operator|.
name|STATE
argument_list|)
operator|.
name|is
argument_list|(
name|ClusterNodeInfo
operator|.
name|ClusterNodeState
operator|.
name|ACTIVE
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|)
operator|.
name|lessThan
argument_list|(
name|currentTime
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_LOCK
argument_list|)
operator|.
name|notEquals
argument_list|(
name|RecoverLockState
operator|.
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getClusterNodeCollection
argument_list|()
operator|.
name|findOne
argument_list|(
name|query
operator|.
name|get
argument_list|()
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|private
name|DBCollection
name|getNodeCollection
parameter_list|()
block|{
return|return
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
return|;
block|}
specifier|private
name|DBCollection
name|getClusterNodeCollection
parameter_list|()
block|{
return|return
name|store
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|)
return|;
block|}
block|}
end_class

end_unit

