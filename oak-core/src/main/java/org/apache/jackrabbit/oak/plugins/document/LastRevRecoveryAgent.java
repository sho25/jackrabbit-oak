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
name|Maps
operator|.
name|filterKeys
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
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
name|JOURNAL
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
name|PROPERTY_OR_DELETED
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
name|Sets
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
name|MapFactory
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
specifier|public
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
name|long
name|leaseEnd
init|=
name|nodeInfo
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
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
name|log
operator|.
name|info
argument_list|(
literal|"Recovering candidates modified after: [{}] for clusterId [{}]"
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|startTime
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
comment|/**      * Recover the correct _lastRev updates for the given candidate nodes.      *      * @param suspects the potential suspects      * @param clusterId the cluster id for which _lastRev recovery needed      * @return the number of documents that required recovery.      */
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
return|return
name|recover
argument_list|(
name|suspects
argument_list|,
name|clusterId
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Recover the correct _lastRev updates for the given candidate nodes.      *       * @param suspects the potential suspects      * @param clusterId the cluster id for which _lastRev recovery needed      * @param dryRun if {@code true}, this method will only perform a check      *               but not apply the changes to the _lastRev fields.      * @return the number of documents that required recovery. This method      *          returns the number of the affected documents even if      *          {@code dryRun} is set true and no document was changed.      */
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
parameter_list|,
name|boolean
name|dryRun
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
name|MapFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|DocumentStore
name|docStore
init|=
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
specifier|final
name|JournalEntry
name|changes
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|docStore
argument_list|)
decl_stmt|;
name|long
name|count
init|=
literal|0
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
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Scanned {} suspects so far..."
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
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
comment|// 1. determine last committed modification on document
name|Revision
name|lastModifiedRev
init|=
name|determineLastModification
argument_list|(
name|doc
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|Revision
name|lastRevForParents
init|=
name|Utils
operator|.
name|max
argument_list|(
name|lastModifiedRev
argument_list|,
name|currentLastRev
argument_list|)
decl_stmt|;
comment|// remember the higher of the two revisions. this is the
comment|// most recent revision currently obtained from either a
comment|// _lastRev entry or an explicit modification on the document
if|if
condition|(
name|lastRevForParents
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
name|lastRevForParents
argument_list|)
expr_stmt|;
block|}
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
name|changes
operator|.
name|modified
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// track all changes
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
comment|// take the root's lastRev
specifier|final
name|Revision
name|lastRootRev
init|=
name|unsaved
operator|.
name|get
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
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
name|String
name|updates
init|=
name|unsaved
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|dryRun
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Dry run of lastRev recovery identified [{}] documents for "
operator|+
literal|"cluster node [{}]: {}"
argument_list|,
name|size
argument_list|,
name|clusterId
argument_list|,
name|updates
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//UnsavedModifications is designed to be used in concurrent
comment|//access mode. For recovery case there is no concurrent access
comment|//involve so just pass a new lock instance
comment|// the lock uses to do the persisting is a plain reentrant lock
comment|// thus it doesn't matter, where exactly the check is done
comment|// as to whether the recovered lastRev has already been
comment|// written to the journal.
name|unsaved
operator|.
name|persist
argument_list|(
name|nodeStore
argument_list|,
operator|new
name|UnsavedModifications
operator|.
name|Snapshot
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|acquiring
parameter_list|()
block|{
if|if
condition|(
name|lastRootRev
operator|==
literal|null
condition|)
block|{
comment|// this should never happen - when unsaved has no changes
comment|// that is reflected in the 'map' to be empty - in that
comment|// case 'persist()' quits early and never calls
comment|// acquiring() here.
comment|//
comment|// but even if it would occur - if we have no lastRootRev
comment|// then we cannot and probably don't have to persist anything
return|return;
block|}
specifier|final
name|String
name|id
init|=
name|JournalEntry
operator|.
name|asId
argument_list|(
name|lastRootRev
argument_list|)
decl_stmt|;
comment|// lastRootRev never null at this point
specifier|final
name|JournalEntry
name|existingEntry
init|=
name|docStore
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|JOURNAL
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingEntry
operator|!=
literal|null
condition|)
block|{
comment|// then the journal entry was already written - as can happen if
comment|// someone else (or the original instance itself) wrote the
comment|// journal entry, then died.
comment|// in this case, don't write it again.
comment|// hence: nothing to be done here. return.
return|return;
block|}
comment|// otherwise store a new journal entry now
name|docStore
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|singletonList
argument_list|(
name|changes
operator|.
name|asUpdateOp
argument_list|(
name|lastRootRev
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"cluster node [{}]: {}"
argument_list|,
name|size
argument_list|,
name|clusterId
argument_list|,
name|updates
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/**      * Retrieves possible candidates which have been modified after the given      * {@code startTime} and recovers the missing updates.      *       * @param clusterId the cluster id      * @param startTime the start time      * @return the int the number of restored nodes      */
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
comment|// Relinquish the lock on the recovery for the cluster on the
comment|// clusterInfo
comment|// TODO: in case recover throws a RuntimeException (or Error..) then
comment|// the recovery might have failed, yet the instance is marked
comment|// as 'recovered' (by setting the state to NONE).
comment|// is this really fine here? or should we not retry - or at least
comment|// log the throwable?
name|missingLastRevUtil
operator|.
name|releaseRecoveryLock
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|signalClusterStateChange
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Determines the last committed modification to the given document by      * a {@code clusterId}.      *       * @param doc a document.      * @param clusterId clusterId for which the last committed modification is      *                  looked up.      * @return the commit revision of the last modification by {@code clusterId}      *          to the given document.      */
annotation|@
name|CheckForNull
specifier|private
name|Revision
name|determineLastModification
parameter_list|(
name|NodeDocument
name|doc
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|ClusterPredicate
name|cp
init|=
operator|new
name|ClusterPredicate
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
name|Revision
name|lastModified
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|property
range|:
name|Sets
operator|.
name|filter
argument_list|(
name|doc
operator|.
name|keySet
argument_list|()
argument_list|,
name|PROPERTY_OR_DELETED
argument_list|)
control|)
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|valueMap
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
decl_stmt|;
comment|// collect committed changes of this cluster node
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|filterKeys
argument_list|(
name|valueMap
argument_list|,
name|cp
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Revision
name|rev
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
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
name|lastModified
operator|=
name|Utils
operator|.
name|max
argument_list|(
name|lastModified
argument_list|,
name|doc
operator|.
name|getCommitRevision
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|lastModified
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

