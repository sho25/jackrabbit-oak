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
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|Predicate
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
name|Iterables
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
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
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
name|getModifiedInSecs
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
name|getSelectedDocuments
import|;
end_import

begin_comment
comment|/**  * Utilities to retrieve _lastRev missing update candidates.  */
end_comment

begin_class
specifier|public
class|class
name|MissingLastRevSeeker
block|{
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|protected
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|public
name|MissingLastRevSeeker
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
comment|/**      * Gets the clusters which potentially need _lastRev recovery.      *      * @return the clusters      */
annotation|@
name|NotNull
specifier|public
name|Iterable
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|getAllClusters
parameter_list|()
block|{
return|return
name|ClusterNodeInfoDocument
operator|.
name|all
argument_list|(
name|store
argument_list|)
return|;
block|}
comment|/**      * Gets the cluster node info for the given cluster node id.      *      * @param clusterId the cluster id      * @return the cluster node info      */
annotation|@
name|Nullable
specifier|public
name|ClusterNodeInfoDocument
name|getClusterNodeInfo
parameter_list|(
specifier|final
name|int
name|clusterId
parameter_list|)
block|{
comment|// Fetch all documents.
return|return
name|store
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clusterId
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Get the candidates with modified time greater than or equal the specified      * {@code startTime} in milliseconds since the start of the epoch.      *      * @param startTime the start time in milliseconds.      * @return the candidates      */
annotation|@
name|NotNull
specifier|public
name|Iterable
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
comment|// Fetch all documents where lastmod>= startTime
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|nodes
init|=
name|getSelectedDocuments
argument_list|(
name|store
argument_list|,
name|MODIFIED_IN_SECS
argument_list|,
name|getModifiedInSecs
argument_list|(
name|startTime
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|nodes
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeDocument
name|input
parameter_list|)
block|{
name|Long
name|modified
init|=
operator|(
name|Long
operator|)
name|input
operator|.
name|get
argument_list|(
name|MODIFIED_IN_SECS
argument_list|)
decl_stmt|;
return|return
operator|(
name|modified
operator|!=
literal|null
operator|&&
operator|(
name|modified
operator|>=
name|getModifiedInSecs
argument_list|(
name|startTime
argument_list|)
operator|)
operator|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Acquire a recovery lock for the given cluster node info document. This      * method may break a lock when it determines the cluster node holding the      * recovery lock is no more active or its lease expired.      *       * @param clusterId      *            id of the cluster that is going to be recovered      * @param recoveredBy      *            id of cluster doing the recovery      * @return whether the lock has been acquired      */
specifier|public
name|boolean
name|acquireRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|recoveredBy
parameter_list|)
block|{
return|return
operator|new
name|RecoveryLock
argument_list|(
name|store
argument_list|,
name|clock
argument_list|,
name|clusterId
argument_list|)
operator|.
name|acquireRecoveryLock
argument_list|(
name|recoveredBy
argument_list|)
return|;
block|}
comment|/**      * Releases the recovery lock on the given {@code clusterId}. If      * {@code success} is {@code true}, the state of the cluster node entry      * is reset, otherwise it is left as is. That is, for a cluster node which      * requires recovery and the recovery process failed, the state will still      * be active, when this release method is called with {@code success} set      * to {@code false}.      *      * @param clusterId the id of the cluster node that was recovered.      * @param success whether recovery was successful.      */
specifier|public
name|void
name|releaseRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|boolean
name|success
parameter_list|)
block|{
operator|new
name|RecoveryLock
argument_list|(
name|store
argument_list|,
name|clock
argument_list|,
name|clusterId
argument_list|)
operator|.
name|releaseRecoveryLock
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeDocument
name|getRoot
parameter_list|()
block|{
return|return
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|Path
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if any of the cluster node info documents satisfies      * {@link ClusterNodeInfoDocument#isRecoveryNeeded(long)} where the passed      * timestamp is the current time.      *      * @return {@code true} if any of the cluster nodes need recovery,      *          {@code false} otherwise.      */
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|()
block|{
name|long
name|now
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|getAllClusters
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|anyMatch
argument_list|(
name|info
lambda|->
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|isRecoveryNeeded
argument_list|(
name|now
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Same as {@link ClusterNodeInfoDocument#isRecoveryNeeded(long)}.      *      * @deprecated use {@link ClusterNodeInfoDocument#isRecoveryNeeded(long)}      *          instead.      */
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|(
annotation|@
name|NotNull
name|ClusterNodeInfoDocument
name|nodeInfo
parameter_list|)
block|{
return|return
name|nodeInfo
operator|.
name|isRecoveryNeeded
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

