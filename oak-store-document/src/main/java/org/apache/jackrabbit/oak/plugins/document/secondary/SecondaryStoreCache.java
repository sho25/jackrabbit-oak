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
name|secondary
package|;
end_package

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
name|EvictingQueue
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
name|AbstractDocumentNodeState
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
name|DocumentNodeStateCache
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
name|NodeStateDiffer
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
name|Path
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
name|spi
operator|.
name|filter
operator|.
name|PathFilter
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|MeterStats
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
name|StatisticsProvider
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
name|StatsOptions
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

begin_class
specifier|public
class|class
name|SecondaryStoreCache
implements|implements
name|DocumentNodeStateCache
implements|,
name|SecondaryStoreRootObserver
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
specifier|static
specifier|final
name|AbstractDocumentNodeState
index|[]
name|EMPTY
init|=
operator|new
name|AbstractDocumentNodeState
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|PathFilter
name|pathFilter
decl_stmt|;
specifier|private
specifier|final
name|NodeStateDiffer
name|differ
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|unknownPaths
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|knownMissed
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|knownMissedOld
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|knownMissedNew
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|knownMissedInRange
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|headRevMatched
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|prevRevMatched
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxSize
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|EvictingQueue
argument_list|<
name|AbstractDocumentNodeState
argument_list|>
name|queue
decl_stmt|;
specifier|private
specifier|volatile
name|AbstractDocumentNodeState
index|[]
name|previousRoots
init|=
name|EMPTY
decl_stmt|;
specifier|public
name|SecondaryStoreCache
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|NodeStateDiffer
name|differ
parameter_list|,
name|PathFilter
name|pathFilter
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|differ
operator|=
name|differ
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|pathFilter
operator|=
name|pathFilter
expr_stmt|;
name|this
operator|.
name|unknownPaths
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_UNKNOWN"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|knownMissed
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_KNOWN_MISSED"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|knownMissedOld
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_KNOWN_MISSED_OLD"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|knownMissedNew
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_KNOWN_MISSED_NEW"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|knownMissedInRange
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_KNOWN_MISSED_IN_RANGE"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|headRevMatched
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_HEAD"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|prevRevMatched
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"DOCUMENT_CACHE_SEC_OLD"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|AbstractDocumentNodeState
name|getDocumentNodeState
parameter_list|(
name|Path
name|path
parameter_list|,
name|RevisionVector
name|rootRevision
parameter_list|,
name|RevisionVector
name|lastRev
parameter_list|)
block|{
comment|//TODO We might need skip the calls if they occur due to SecondaryStoreObserver
comment|//doing the diff or in the startup when we try to sync the state
name|String
name|p
init|=
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
name|PathFilter
operator|.
name|Result
name|result
init|=
name|pathFilter
operator|.
name|filter
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
name|PathFilter
operator|.
name|Result
operator|.
name|INCLUDE
condition|)
block|{
name|unknownPaths
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|DelegatingDocumentNodeState
operator|.
name|hasMetaProps
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AbstractDocumentNodeState
name|currentRoot
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|differ
argument_list|)
decl_stmt|;
comment|//If the root rev is< lastRev then secondary store is lagging and would
comment|//not have the matching result
if|if
condition|(
name|lastRev
operator|.
name|compareTo
argument_list|(
name|currentRoot
operator|.
name|getLastRevision
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AbstractDocumentNodeState
name|nodeState
init|=
name|findByMatchingLastRev
argument_list|(
name|currentRoot
argument_list|,
name|path
argument_list|,
name|lastRev
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeState
operator|!=
literal|null
condition|)
block|{
name|headRevMatched
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|nodeState
return|;
block|}
name|AbstractDocumentNodeState
name|matchingRoot
init|=
name|findMatchingRoot
argument_list|(
name|rootRevision
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingRoot
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|state
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|matchingRoot
argument_list|,
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|exists
argument_list|()
condition|)
block|{
name|AbstractDocumentNodeState
name|docState
init|=
name|asDocState
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|prevRevMatched
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|docState
return|;
block|}
block|}
name|knownMissed
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCached
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|pathFilter
operator|.
name|filter
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|==
name|PathFilter
operator|.
name|Result
operator|.
name|INCLUDE
return|;
block|}
annotation|@
name|Nullable
specifier|private
name|AbstractDocumentNodeState
name|findByMatchingLastRev
parameter_list|(
name|AbstractDocumentNodeState
name|root
parameter_list|,
name|Path
name|path
parameter_list|,
name|RevisionVector
name|lastRev
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|path
operator|.
name|elements
argument_list|()
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//requested lastRev is> current node lastRev then no need to check further
if|if
condition|(
name|lastRev
operator|.
name|compareTo
argument_list|(
name|asDocState
argument_list|(
name|state
argument_list|)
operator|.
name|getLastRevision
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
name|AbstractDocumentNodeState
name|docState
init|=
name|asDocState
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastRev
operator|.
name|equals
argument_list|(
name|docState
operator|.
name|getLastRevision
argument_list|()
argument_list|)
condition|)
block|{
name|headRevMatched
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|docState
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
specifier|private
name|AbstractDocumentNodeState
name|findMatchingRoot
parameter_list|(
name|RevisionVector
name|rr
parameter_list|)
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//Use a local variable as the array can get changed in process
name|AbstractDocumentNodeState
index|[]
name|roots
init|=
name|previousRoots
decl_stmt|;
name|AbstractDocumentNodeState
name|latest
init|=
name|roots
index|[
name|roots
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|AbstractDocumentNodeState
name|oldest
init|=
name|roots
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|rr
operator|.
name|compareTo
argument_list|(
name|latest
operator|.
name|getRootRevision
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
name|knownMissedNew
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|rr
operator|.
name|compareTo
argument_list|(
name|oldest
operator|.
name|getRootRevision
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|knownMissedOld
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|AbstractDocumentNodeState
name|result
init|=
name|findMatchingRoot
argument_list|(
name|roots
argument_list|,
name|rr
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|knownMissedInRange
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|AbstractDocumentNodeState
name|root
parameter_list|)
block|{
synchronized|synchronized
init|(
name|queue
init|)
block|{
comment|//TODO Possibly can be improved
name|queue
operator|.
name|add
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|previousRoots
operator|=
name|queue
operator|.
name|toArray
argument_list|(
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|previousRoots
operator|.
name|length
operator|==
literal|0
return|;
block|}
specifier|static
name|AbstractDocumentNodeState
name|findMatchingRoot
parameter_list|(
name|AbstractDocumentNodeState
index|[]
name|roots
parameter_list|,
name|RevisionVector
name|key
parameter_list|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|roots
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|//Perform a binary search as the array is sorted in ascending order
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|AbstractDocumentNodeState
name|midVal
init|=
name|roots
index|[
name|mid
index|]
decl_stmt|;
name|int
name|cmp
init|=
name|midVal
operator|.
name|getRootRevision
argument_list|()
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|midVal
return|;
comment|// key found
block|}
block|}
return|return
literal|null
return|;
comment|// key not found.
block|}
specifier|private
specifier|static
name|AbstractDocumentNodeState
name|asDocState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
return|return
operator|(
name|AbstractDocumentNodeState
operator|)
name|state
return|;
block|}
block|}
end_class

end_unit

