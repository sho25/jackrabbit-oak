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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|AbstractIterator
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

begin_comment
comment|/**  * Utils to retrieve _lastRev missing update candidates.  */
end_comment

begin_class
specifier|public
class|class
name|MissingLastRevSeeker
block|{
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
specifier|public
name|MissingLastRevSeeker
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
comment|/**      * Gets the clusters which potentially need _lastRev recovery.      *      * @return the clusters      */
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
name|Collection
operator|.
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
operator|new
name|MissingLastRevCandidateIterable
argument_list|(
name|store
argument_list|,
name|NodeDocument
operator|.
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
name|NodeDocument
operator|.
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
name|NodeDocument
operator|.
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
comment|/**      * Query the document store for nodes newer than a specific timestamp; implement      * this as an Iterator that gets relatively small chunks from the store.      */
specifier|private
class|class
name|MissingLastRevCandidateIterable
implements|implements
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
block|{
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
specifier|public
name|MissingLastRevCandidateIterable
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|long
name|startTime
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
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|MissingLastRevCandidateIterator
argument_list|(
name|store
argument_list|,
name|startTime
argument_list|)
return|;
block|}
specifier|private
class|class
name|MissingLastRevCandidateIterator
extends|extends
name|AbstractIterator
argument_list|<
name|NodeDocument
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|CHUNKSIZE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
specifier|private
name|String
name|startIndex
init|=
name|NodeDocument
operator|.
name|MIN_ID_VALUE
decl_stmt|;
specifier|private
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|results
init|=
operator|new
name|LinkedList
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|MissingLastRevCandidateIterator
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|long
name|startTime
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
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
specifier|private
name|boolean
name|refill
parameter_list|()
block|{
if|if
condition|(
name|startIndex
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|found
init|=
name|store
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|startIndex
argument_list|,
name|NodeDocument
operator|.
name|MAX_ID_VALUE
argument_list|,
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
name|startTime
argument_list|,
name|CHUNKSIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|startIndex
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|results
operator|.
name|addAll
argument_list|(
name|found
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|NodeDocument
name|computeNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeDocument
name|result
init|=
name|results
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|startIndex
operator|=
name|result
operator|.
name|getId
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
block|{
comment|// need to refill
if|if
condition|(
name|refill
argument_list|()
condition|)
block|{
return|return
name|computeNext
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|boolean
name|acquireRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
comment|//This approach has a race condition where two different cluster nodes
comment|//can acquire the lock simultaneously.
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
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|set
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
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|releaseRecoveryLock
parameter_list|(
name|int
name|clusterId
parameter_list|)
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
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|set
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_LOCK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|ClusterNodeInfo
operator|.
name|STATE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
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
name|ROOT_PATH
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
for|for
control|(
name|ClusterNodeInfoDocument
name|nodeInfo
range|:
name|getAllClusters
argument_list|()
control|)
block|{
comment|// Check if _lastRev recovery needed for this cluster node
comment|// state is Active&& currentTime past the leaseEnd time&& recoveryLock not held by someone
if|if
condition|(
name|nodeInfo
operator|.
name|isActive
argument_list|()
operator|&&
name|currentTime
operator|>
name|nodeInfo
operator|.
name|getLeaseEndTime
argument_list|()
operator|&&
operator|!
name|nodeInfo
operator|.
name|isBeingRecovered
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

