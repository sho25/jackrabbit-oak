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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Maps
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

begin_class
specifier|public
class|class
name|LastRevRecovery
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
specifier|public
name|LastRevRecovery
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
block|}
specifier|public
name|void
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
comment|//Set of parent path whose lastRev has been updated based on
comment|//last rev information obtained from suspects. Its possible
comment|//that lastRev for such parents present in DS has
comment|//higher value. So before persisting the changes for these
comment|//paths we need to ensure that there actual lastRev is lesser
comment|//than one being set via unsaved
name|Set
argument_list|<
name|String
argument_list|>
name|unverifiedParentPaths
init|=
name|Sets
operator|.
name|newHashSet
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
comment|//lastRev is consistent
if|if
condition|(
name|lostLastRev
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|//1. Update lastRev for this doc
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
comment|//2. Update lastRev for parent paths
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
name|unsaved
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|lostLastRev
argument_list|)
expr_stmt|;
name|unverifiedParentPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|//By now we have iterated over all suspects so remove entries for paths
comment|//whose lastRev have been determined on the basis of state obtained from
comment|//DS
name|Iterator
argument_list|<
name|String
argument_list|>
name|unverifiedParentPathsItr
init|=
name|unverifiedParentPaths
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|unverifiedParentPathsItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|unverifiedParentPath
init|=
name|unverifiedParentPathsItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|Revision
name|knownRevision
init|=
name|knownLastRevs
operator|.
name|get
argument_list|(
name|unverifiedParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|knownRevision
operator|!=
literal|null
condition|)
block|{
name|unverifiedParentPathsItr
operator|.
name|remove
argument_list|()
expr_stmt|;
name|unsaved
operator|.
name|put
argument_list|(
name|unverifiedParentPath
argument_list|,
name|knownRevision
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Now for the left over unverifiedParentPaths determine the lastRev
comment|//from DS and add them to unsaved. This ensures that we do not set lastRev
comment|//to a lower value
comment|//TODO For Mongo case we can fetch such documents more efficiently
comment|//via batch fetch
for|for
control|(
name|String
name|path
range|:
name|unverifiedParentPaths
control|)
block|{
name|NodeDocument
name|doc
init|=
name|getDocument
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|Revision
name|lastRev
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
name|unsaved
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|lastRev
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
comment|/**      * Determines the last revision value which needs to set for given clusterId      * on the passed document. If the last rev entries are consisted      *      * @param doc       NodeDocument where lastRev entries needs to be fixed      * @param clusterId clusterId for which lastRev has to be checked      * @return lastRev which needs to be updated.<tt>null</tt> if no      * updated is required i.e. lastRev entries are valid      */
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
comment|//Merge sort the revs for which changes have been made
comment|//to this doc
comment|//TODO Would looking into the Local map be sufficient
comment|//Probably yes as entries for a particular cluster node
comment|//are split by that cluster only
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
comment|//Look for latest valid revision> currentLastRev
comment|//if found then lastRev needs to be fixed
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
comment|//No valid revision found> currentLastRev
comment|//indicates that lastRev is valid for given clusterId
comment|//and no further checks are required
break|break;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|NodeDocument
name|getDocument
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
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
name|path
argument_list|)
argument_list|)
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

