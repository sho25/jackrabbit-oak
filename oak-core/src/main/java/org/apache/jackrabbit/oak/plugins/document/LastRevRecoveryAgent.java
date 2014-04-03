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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|of
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
name|filter
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
name|mergeSorted
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

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
name|Lists
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
name|Maps
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
name|commons
operator|.
name|PathUtils
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
name|MongoDocumentStore
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
name|MongoMissingLastRevSeeker
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

begin_comment
comment|/**  * Utility class for recovering potential missing _lastRev updates of nodes due to crash of a node.  */
end_comment

begin_class
specifier|public
class|class
name|LastRevRecoveryAgent
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|MissingLastRevSeeker
name|missingLastRevUtil
decl_stmt|;
name|LastRevRecoveryAgent
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
if|if
condition|(
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
name|this
operator|.
name|missingLastRevUtil
operator|=
operator|new
name|MongoMissingLastRevSeeker
argument_list|(
operator|(
name|MongoDocumentStore
operator|)
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|missingLastRevUtil
operator|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Recover the correct _lastRev updates for potentially missing candidate nodes.      *       * @param clusterId the cluster id for which the _lastRev are to be recovered      * @return the int the number of restored nodes      */
specifier|public
name|int
name|recover
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|ClusterNodeInfoDocument
name|nodeInfo
init|=
name|missingLastRevUtil
operator|.
name|getClusterNodeInfo
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
comment|//TODO Currently leaseTime remains same per cluster node. If this
comment|//is made configurable then it should be read from DB entry
specifier|final
name|long
name|leaseTime
init|=
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_DURATION_MILLIS
decl_stmt|;
specifier|final
name|long
name|asyncDelay
init|=
name|nodeStore
operator|.
name|getAsyncDelay
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeInfo
operator|!=
literal|null
condition|)
block|{
name|long
name|leaseEnd
init|=
name|nodeInfo
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
comment|// Check if _lastRev recovery needed for this cluster node
comment|// state is Active&& recoveryLock not held by someone
if|if
condition|(
name|isRecoveryNeeded
argument_list|(
name|nodeInfo
argument_list|)
condition|)
block|{
comment|// retrieve the root document's _lastRev
name|NodeDocument
name|root
init|=
name|missingLastRevUtil
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|Revision
name|lastRev
init|=
name|root
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
comment|// start time is the _lastRev timestamp of this cluster node
specifier|final
name|long
name|startTime
decl_stmt|;
comment|//lastRev can be null if other cluster node did not got
comment|//chance to perform lastRev rollup even once
if|if
condition|(
name|lastRev
operator|!=
literal|null
condition|)
block|{
name|startTime
operator|=
name|lastRev
operator|.
name|getTimestamp
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startTime
operator|=
name|leaseEnd
operator|-
name|leaseTime
operator|-
name|asyncDelay
expr_stmt|;
block|}
comment|// Endtime is the leaseEnd + the asyncDelay
name|long
name|endTime
init|=
name|leaseEnd
operator|+
name|asyncDelay
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Recovering candidates modified in time range : [{},{}] for clusterId [{}]"
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|startTime
argument_list|)
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|endTime
argument_list|)
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
return|return
name|recoverCandidates
argument_list|(
name|clusterId
argument_list|,
name|startTime
argument_list|,
name|endTime
argument_list|)
return|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"No recovery needed for clusterId {}"
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/**      * Recover the correct _lastRev updates for the given candidate nodes.      *       * @param suspects the potential suspects      * @param clusterId the cluster id for which _lastRev recovery needed      * @return the int      */
specifier|public
name|int
name|recover
parameter_list|(
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|suspects
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|UnsavedModifications
name|unsaved
init|=
operator|new
name|UnsavedModifications
argument_list|()
decl_stmt|;
name|UnsavedModifications
name|unsavedParents
init|=
operator|new
name|UnsavedModifications
argument_list|()
decl_stmt|;
comment|//Map of known last rev of checked paths
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|knownLastRevs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
while|while
condition|(
name|suspects
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeDocument
name|doc
init|=
name|suspects
operator|.
name|next
argument_list|()
decl_stmt|;
name|Revision
name|currentLastRev
init|=
name|doc
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLastRev
operator|!=
literal|null
condition|)
block|{
name|knownLastRevs
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getPath
argument_list|()
argument_list|,
name|currentLastRev
argument_list|)
expr_stmt|;
block|}
name|Revision
name|lostLastRev
init|=
name|determineMissedLastRev
argument_list|(
name|doc
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
comment|//1. Update lastRev for this doc
if|if
condition|(
name|lostLastRev
operator|!=
literal|null
condition|)
block|{
name|unsaved
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getPath
argument_list|()
argument_list|,
name|lostLastRev
argument_list|)
expr_stmt|;
block|}
name|Revision
name|lastRevForParents
init|=
name|lostLastRev
operator|!=
literal|null
condition|?
name|lostLastRev
else|:
name|currentLastRev
decl_stmt|;
comment|//If both currentLastRev and lostLastRev are null it means
comment|//that no change is done by suspect cluster on this document
comment|//so nothing needs to be updated. Probably it was only changed by
comment|//other cluster nodes. If this node is parent of any child node which
comment|//has been modified by cluster then that node roll up would
comment|//add this node path to unsaved
comment|//2. Update lastRev for parent paths aka rollup
if|if
condition|(
name|lastRevForParents
operator|!=
literal|null
condition|)
block|{
name|String
name|path
init|=
name|doc
operator|.
name|getPath
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
break|break;
block|}
name|path
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|unsavedParents
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|lastRevForParents
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|String
name|parentPath
range|:
name|unsavedParents
operator|.
name|getPaths
argument_list|()
control|)
block|{
name|Revision
name|calcLastRev
init|=
name|unsavedParents
operator|.
name|get
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
name|Revision
name|knownLastRev
init|=
name|knownLastRevs
operator|.
name|get
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
comment|//Copy the calcLastRev of parent only if they have changed
comment|//In many case it might happen that parent have consistent lastRev
comment|//This check ensures that unnecessary updates are not made
if|if
condition|(
name|knownLastRev
operator|==
literal|null
operator|||
name|calcLastRev
operator|.
name|compareRevisionTime
argument_list|(
name|knownLastRev
argument_list|)
operator|>
literal|0
condition|)
block|{
name|unsaved
operator|.
name|put
argument_list|(
name|parentPath
argument_list|,
name|calcLastRev
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Note the size before persist as persist operation
comment|//would empty the internal state
name|int
name|size
init|=
name|unsaved
operator|.
name|getPaths
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Last revision for following documents would be updated {}"
argument_list|,
name|unsaved
operator|.
name|getPaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//UnsavedModifications is designed to be used in concurrent
comment|//access mode. For recovery case there is no concurrent access
comment|//involve so just pass a new lock instance
name|unsaved
operator|.
name|persist
argument_list|(
name|nodeStore
argument_list|,
operator|new
name|ReentrantLock
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Updated lastRev of [{}] documents while performing lastRev recovery for "
operator|+
literal|"cluster node [{}]"
argument_list|,
name|size
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/**      * Retrieves possible candidates which have been modifed in the time range and recovers the      * missing updates.      *       * @param clusterId the cluster id      * @param startTime the start time      * @param endTime the end time      * @return the int the number of restored nodes      */
specifier|private
name|int
name|recoverCandidates
parameter_list|(
specifier|final
name|int
name|clusterId
parameter_list|,
specifier|final
name|long
name|startTime
parameter_list|,
specifier|final
name|long
name|endTime
parameter_list|)
block|{
name|boolean
name|lockAcquired
init|=
name|missingLastRevUtil
operator|.
name|acquireRecoveryLock
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
comment|//TODO What if recovery is being performed for current clusterNode by some other node
comment|//should we halt the startup
if|if
condition|(
operator|!
name|lockAcquired
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Last revision recovery already being performed by some other node. "
operator|+
literal|"Would not attempt recovery"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|suspects
init|=
name|missingLastRevUtil
operator|.
name|getCandidates
argument_list|(
name|startTime
argument_list|,
name|endTime
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Performing Last Revision recovery for cluster {}"
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|recover
argument_list|(
name|suspects
operator|.
name|iterator
argument_list|()
argument_list|,
name|clusterId
argument_list|)
return|;
block|}
finally|finally
block|{
name|Utils
operator|.
name|closeIfCloseable
argument_list|(
name|suspects
argument_list|)
expr_stmt|;
comment|// Relinquish the lock on the recovery for the cluster on the clusterInfo
name|missingLastRevUtil
operator|.
name|releaseRecoveryLock
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Determines the last revision value which needs to set for given clusterId      * on the passed document. If the last rev entries are consisted      *       * @param doc NodeDocument where lastRev entries needs to be fixed      * @param clusterId clusterId for which lastRev has to be checked      * @return lastRev which needs to be updated.<tt>null</tt> if no      *         updated is required i.e. lastRev entries are valid      */
annotation|@
name|CheckForNull
specifier|private
name|Revision
name|determineMissedLastRev
parameter_list|(
name|NodeDocument
name|doc
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|Revision
name|currentLastRev
init|=
name|doc
operator|.
name|getLastRev
argument_list|()
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentLastRev
operator|==
literal|null
condition|)
block|{
name|currentLastRev
operator|=
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|clusterId
argument_list|)
expr_stmt|;
block|}
name|ClusterPredicate
name|cp
init|=
operator|new
name|ClusterPredicate
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
comment|// Merge sort the revs for which changes have been made
comment|// to this doc
comment|// localMap always keeps the most recent valid commit entry
comment|// per cluster node so looking into that should be sufficient
name|Iterable
argument_list|<
name|Revision
argument_list|>
name|revs
init|=
name|mergeSorted
argument_list|(
name|of
argument_list|(
name|filter
argument_list|(
name|doc
operator|.
name|getLocalCommitRoot
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|cp
argument_list|)
argument_list|,
name|filter
argument_list|(
name|doc
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|,
name|cp
argument_list|)
argument_list|)
argument_list|,
name|StableRevisionComparator
operator|.
name|REVERSE
argument_list|)
decl_stmt|;
comment|// Look for latest valid revision> currentLastRev
comment|// if found then lastRev needs to be fixed
for|for
control|(
name|Revision
name|rev
range|:
name|revs
control|)
block|{
if|if
condition|(
name|rev
operator|.
name|compareRevisionTime
argument_list|(
name|currentLastRev
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|isCommitted
argument_list|(
name|rev
argument_list|)
condition|)
block|{
return|return
name|rev
return|;
block|}
block|}
else|else
block|{
comment|// No valid revision found> currentLastRev
comment|// indicates that lastRev is valid for given clusterId
comment|// and no further checks are required
break|break;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Determines if any of the cluster node failed to renew its lease and      * did not properly shutdown. If any such cluster node is found then are potential      * candidates for last rev recovery      *      * @return true if last rev recovery needs to be performed for any of the cluster nodes      */
specifier|public
name|boolean
name|isRecoveryNeeded
parameter_list|()
block|{
return|return
name|missingLastRevUtil
operator|.
name|isRecoveryNeeded
argument_list|(
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|performRecoveryIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|isRecoveryNeeded
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|clusterIds
init|=
name|getRecoveryCandidateNodes
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting last revision recovery for following clusterId {}"
argument_list|,
name|clusterIds
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|clusterId
range|:
name|clusterIds
control|)
block|{
name|recover
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Gets the _lastRev recovery candidate cluster nodes.      *      * @return the recovery candidate nodes      */
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getRecoveryCandidateNodes
parameter_list|()
block|{
name|Iterable
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|clusters
init|=
name|missingLastRevUtil
operator|.
name|getAllClusters
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|candidateClusterNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ClusterNodeInfoDocument
name|nodeInfo
range|:
name|clusters
control|)
block|{
if|if
condition|(
name|isRecoveryNeeded
argument_list|(
name|nodeInfo
argument_list|)
condition|)
block|{
name|candidateClusterNodes
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|nodeInfo
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|candidateClusterNodes
return|;
block|}
specifier|private
name|boolean
name|isRecoveryNeeded
parameter_list|(
name|ClusterNodeInfoDocument
name|nodeInfo
parameter_list|)
block|{
if|if
condition|(
name|nodeInfo
operator|!=
literal|null
condition|)
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
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
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
specifier|private
specifier|static
class|class
name|ClusterPredicate
implements|implements
name|Predicate
argument_list|<
name|Revision
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|clusterId
decl_stmt|;
specifier|private
name|ClusterPredicate
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Revision
name|input
parameter_list|)
block|{
return|return
name|clusterId
operator|==
name|input
operator|.
name|getClusterId
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

