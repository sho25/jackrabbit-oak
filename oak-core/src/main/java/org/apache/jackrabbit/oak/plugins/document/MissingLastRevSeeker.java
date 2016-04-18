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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
operator|.
name|RecoverLockState
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
name|ClusterNodeInfo
operator|.
name|ClusterNodeState
operator|.
name|ACTIVE
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
name|LEASE_END_KEY
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
name|REV_RECOVERY_BY
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
name|REV_RECOVERY_LOCK
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
operator|.
name|ACQUIRED
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
name|STATE
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
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MissingLastRevSeeker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|String
name|ROOT_PATH
init|=
literal|"/"
decl_stmt|;
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
specifier|private
specifier|final
name|Predicate
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|isRecoveryNeeded
init|=
operator|new
name|Predicate
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterNodeInfoDocument
name|nodeInfo
parameter_list|)
block|{
return|return
name|isRecoveryNeeded
argument_list|(
name|nodeInfo
argument_list|)
return|;
block|}
block|}
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
name|Nonnull
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
name|CheckForNull
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
comment|/**      * Get the candidates with modified time after the specified      * {@code startTime}.      *      * @param startTime the start time.      * @return the candidates      */
annotation|@
name|Nonnull
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
name|ClusterNodeInfoDocument
name|doc
init|=
name|getClusterNodeInfo
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
comment|// this is unexpected...
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isRecoveryNeeded
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|acquired
init|=
name|tryAcquireRecoveryLock
argument_list|(
name|doc
argument_list|,
name|recoveredBy
argument_list|)
decl_stmt|;
if|if
condition|(
name|acquired
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// either we already own the lock or were able to break the lock
return|return
name|doc
operator|.
name|isBeingRecoveredBy
argument_list|(
name|recoveredBy
argument_list|)
operator|||
name|tryBreakRecoveryLock
argument_list|(
name|doc
argument_list|,
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
try|try
block|{
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|clusterId
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
name|RecoverLockState
operator|.
name|NONE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_BY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|update
operator|.
name|set
argument_list|(
name|STATE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|ClusterNodeInfoDocument
name|old
init|=
name|store
operator|.
name|findAndUpdate
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"ClusterNodeInfo document for "
operator|+
name|clusterId
operator|+
literal|" missing."
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Released recovery lock for cluster id {} (recovery successful: {})"
argument_list|,
name|clusterId
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to release the recovery lock for clusterNodeId "
operator|+
name|clusterId
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|ex
operator|)
throw|;
block|}
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
name|ROOT_PATH
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|any
argument_list|(
name|getAllClusters
argument_list|()
argument_list|,
name|isRecoveryNeeded
argument_list|)
return|;
block|}
comment|/**      * Check if _lastRev recovery needed for this cluster node      * state is Active&& currentTime past the leaseEnd time      */
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|(
annotation|@
name|Nonnull
name|ClusterNodeInfoDocument
name|nodeInfo
parameter_list|)
block|{
return|return
name|nodeInfo
operator|.
name|isActive
argument_list|()
operator|&&
name|clock
operator|.
name|getTime
argument_list|()
operator|>
name|nodeInfo
operator|.
name|getLeaseEndTime
argument_list|()
return|;
block|}
comment|//-------------------------< internal>-------------------------------------
comment|/**      * Acquire a recovery lock for the given cluster node info document      *      * @param info      *            info document of the cluster that is going to be recovered      * @param recoveredBy      *            id of cluster doing the recovery ({@code 0} when unknown)      * @return whether the lock has been acquired      */
specifier|private
name|boolean
name|tryAcquireRecoveryLock
parameter_list|(
name|ClusterNodeInfoDocument
name|info
parameter_list|,
name|int
name|recoveredBy
parameter_list|)
block|{
name|int
name|clusterId
init|=
name|info
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
try|try
block|{
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|clusterId
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|equals
argument_list|(
name|STATE
argument_list|,
name|ACTIVE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|equals
argument_list|(
name|LEASE_END_KEY
argument_list|,
name|info
operator|.
name|getLeaseEndTime
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|notEquals
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoveredBy
operator|!=
literal|0
condition|)
block|{
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_BY
argument_list|,
name|recoveredBy
argument_list|)
expr_stmt|;
block|}
name|ClusterNodeInfoDocument
name|old
init|=
name|store
operator|.
name|findAndUpdate
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Acquired recovery lock for cluster id {}"
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
block|}
return|return
name|old
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to acquire the recovery lock for clusterNodeId "
operator|+
name|clusterId
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|ex
operator|)
throw|;
block|}
block|}
comment|/**      * Checks if the recovering cluster node is inactive and then tries to      * break the recovery lock.      *      * @param doc the cluster node info document of the cluster node to acquire      *            the recovery lock for.      * @param recoveredBy id of cluster doing the recovery.      * @return whether the lock has been acquired.      */
specifier|private
name|boolean
name|tryBreakRecoveryLock
parameter_list|(
name|ClusterNodeInfoDocument
name|doc
parameter_list|,
name|int
name|recoveredBy
parameter_list|)
block|{
name|Long
name|recoveryBy
init|=
name|doc
operator|.
name|getRecoveryBy
argument_list|()
decl_stmt|;
if|if
condition|(
name|recoveryBy
operator|==
literal|null
condition|)
block|{
comment|// cannot determine current lock owner
return|return
literal|false
return|;
block|}
name|ClusterNodeInfoDocument
name|recovering
init|=
name|getClusterNodeInfo
argument_list|(
name|recoveryBy
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|recovering
operator|==
literal|null
condition|)
block|{
comment|// cannot determine current lock owner
return|return
literal|false
return|;
block|}
if|if
condition|(
name|recovering
operator|.
name|isActive
argument_list|()
operator|&&
name|recovering
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|clock
operator|.
name|getTime
argument_list|()
condition|)
block|{
comment|// still active, cannot break lock
return|return
literal|false
return|;
block|}
comment|// try to break the lock
try|try
block|{
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|doc
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|equals
argument_list|(
name|STATE
argument_list|,
name|ACTIVE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|equals
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
name|ACQUIRED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|equals
argument_list|(
name|REV_RECOVERY_BY
argument_list|,
name|recoveryBy
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_BY
argument_list|,
name|recoveredBy
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|old
init|=
name|store
operator|.
name|findAndUpdate
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Acquired (broke) recovery lock for cluster id {}. "
operator|+
literal|"Previous lock owner: {}"
argument_list|,
name|doc
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|recoveryBy
argument_list|)
expr_stmt|;
block|}
return|return
name|old
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to break the recovery lock for clusterNodeId "
operator|+
name|doc
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|ex
operator|)
throw|;
block|}
block|}
block|}
end_class

end_unit

