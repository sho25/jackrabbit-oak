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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|collect
operator|.
name|Iterables
operator|.
name|transform
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
name|CLUSTER_NODES
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
name|ReadPreference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|FindIterable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|model
operator|.
name|Filters
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
name|conversions
operator|.
name|Bson
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
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|clock
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
annotation|@
name|Nonnull
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
parameter_list|)
block|{
name|Bson
name|query
init|=
name|Filters
operator|.
name|gte
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|startTime
argument_list|)
argument_list|)
decl_stmt|;
name|Bson
name|sortFields
init|=
operator|new
name|BasicDBObject
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FindIterable
argument_list|<
name|BasicDBObject
argument_list|>
name|cursor
init|=
name|getNodeCollection
argument_list|()
operator|.
name|withReadPreference
argument_list|(
name|ReadPreference
operator|.
name|primary
argument_list|()
argument_list|)
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
name|input
lambda|->
name|store
operator|.
name|convertFromDBObject
argument_list|(
name|NODES
argument_list|,
name|input
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|()
block|{
name|Bson
name|query
init|=
name|Filters
operator|.
name|and
argument_list|(
name|Filters
operator|.
name|eq
argument_list|(
name|ClusterNodeInfo
operator|.
name|STATE
argument_list|,
name|ClusterNodeInfo
operator|.
name|ClusterNodeState
operator|.
name|ACTIVE
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|Filters
operator|.
name|lt
argument_list|(
name|ClusterNodeInfo
operator|.
name|LEASE_END_KEY
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|getClusterNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|private
name|MongoCollection
argument_list|<
name|BasicDBObject
argument_list|>
name|getNodeCollection
parameter_list|()
block|{
return|return
name|store
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
return|;
block|}
specifier|private
name|MongoCollection
argument_list|<
name|BasicDBObject
argument_list|>
name|getClusterNodeCollection
parameter_list|()
block|{
return|return
name|store
operator|.
name|getDBCollection
argument_list|(
name|CLUSTER_NODES
argument_list|)
return|;
block|}
block|}
end_class

end_unit

