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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|TimeUnit
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|base
operator|.
name|Stopwatch
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
name|Iterators
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
name|sort
operator|.
name|StringSort
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
name|UpdateOp
operator|.
name|Condition
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
name|UpdateOp
operator|.
name|Key
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|StandardSystemProperty
operator|.
name|LINE_SEPARATOR
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
name|copyOf
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
name|Iterators
operator|.
name|partition
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
name|singletonMap
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
name|SplitDocType
operator|.
name|COMMIT_ROOT_ONLY
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
name|SplitDocType
operator|.
name|DEFAULT_LEAF
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
name|UpdateOp
operator|.
name|Condition
operator|.
name|newEqualsCondition
import|;
end_import

begin_class
specifier|public
class|class
name|VersionGarbageCollector
block|{
comment|//Kept less than MongoDocumentStore.IN_CLAUSE_BATCH_SIZE to avoid re-partitioning
specifier|private
specifier|static
specifier|final
name|int
name|DELETE_BATCH_SIZE
init|=
literal|450
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PROGRESS_BATCH_SIZE
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Key
name|KEY_MODIFIED
init|=
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|ds
decl_stmt|;
specifier|private
specifier|final
name|VersionGCSupport
name|versionStore
decl_stmt|;
specifier|private
name|int
name|overflowToDiskThreshold
init|=
literal|100000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VersionGarbageCollector
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Split document types which can be safely garbage collected      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|NodeDocument
operator|.
name|SplitDocType
argument_list|>
name|GC_TYPES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|DEFAULT_LEAF
argument_list|,
name|COMMIT_ROOT_ONLY
argument_list|)
decl_stmt|;
name|VersionGarbageCollector
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|,
name|VersionGCSupport
name|gcSupport
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|versionStore
operator|=
name|gcSupport
expr_stmt|;
name|this
operator|.
name|ds
operator|=
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
block|}
specifier|public
name|VersionGCStats
name|gc
parameter_list|(
name|long
name|maxRevisionAge
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|maxRevisionAgeInMillis
init|=
name|unit
operator|.
name|toMillis
argument_list|(
name|maxRevisionAge
argument_list|)
decl_stmt|;
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|VersionGCStats
name|stats
init|=
operator|new
name|VersionGCStats
argument_list|()
decl_stmt|;
specifier|final
name|long
name|oldestRevTimeStamp
init|=
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|maxRevisionAgeInMillis
decl_stmt|;
specifier|final
name|Revision
name|headRevision
init|=
name|nodeStore
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting revision garbage collection. Revisions older than [{}] will be "
operator|+
literal|"removed"
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|oldestRevTimeStamp
argument_list|)
argument_list|)
expr_stmt|;
comment|//Check for any registered checkpoint which prevent the GC from running
name|Revision
name|checkpoint
init|=
name|nodeStore
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkpoint
operator|!=
literal|null
operator|&&
name|checkpoint
operator|.
name|getTimestamp
argument_list|()
operator|<
name|oldestRevTimeStamp
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Ignoring revision garbage collection because a valid "
operator|+
literal|"checkpoint [{}] was found, which is older than [{}]."
argument_list|,
name|checkpoint
operator|.
name|toReadableString
argument_list|()
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|oldestRevTimeStamp
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|.
name|ignoredGCDueToCheckPoint
operator|=
literal|true
expr_stmt|;
return|return
name|stats
return|;
block|}
name|collectDeletedDocuments
argument_list|(
name|stats
argument_list|,
name|headRevision
argument_list|,
name|oldestRevTimeStamp
argument_list|)
expr_stmt|;
name|collectSplitDocuments
argument_list|(
name|stats
argument_list|,
name|oldestRevTimeStamp
argument_list|)
expr_stmt|;
name|sw
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Revision garbage collection finished in {}. {}"
argument_list|,
name|sw
argument_list|,
name|stats
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
specifier|public
name|void
name|setOverflowToDiskThreshold
parameter_list|(
name|int
name|overflowToDiskThreshold
parameter_list|)
block|{
name|this
operator|.
name|overflowToDiskThreshold
operator|=
name|overflowToDiskThreshold
expr_stmt|;
block|}
specifier|private
name|void
name|collectSplitDocuments
parameter_list|(
name|VersionGCStats
name|stats
parameter_list|,
name|long
name|oldestRevTimeStamp
parameter_list|)
block|{
name|versionStore
operator|.
name|deleteSplitDocuments
argument_list|(
name|GC_TYPES
argument_list|,
name|oldestRevTimeStamp
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|collectDeletedDocuments
parameter_list|(
name|VersionGCStats
name|stats
parameter_list|,
name|Revision
name|headRevision
parameter_list|,
name|long
name|oldestRevTimeStamp
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docsTraversed
init|=
literal|0
decl_stmt|;
name|DeletedDocsGC
name|gc
init|=
operator|new
name|DeletedDocsGC
argument_list|(
name|headRevision
argument_list|)
decl_stmt|;
try|try
block|{
name|stats
operator|.
name|collectDeletedDocs
operator|.
name|start
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|itr
init|=
name|versionStore
operator|.
name|getPossiblyDeletedDocs
argument_list|(
name|oldestRevTimeStamp
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|NodeDocument
name|doc
range|:
name|itr
control|)
block|{
comment|// Check if node is actually deleted at current revision
comment|// As node is not modified since oldestRevTimeStamp then
comment|// this node has not be revived again in past maxRevisionAge
comment|// So deleting it is safe
name|docsTraversed
operator|++
expr_stmt|;
if|if
condition|(
name|docsTraversed
operator|%
name|PROGRESS_BATCH_SIZE
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Iterated through {} documents so far. {} found to be deleted"
argument_list|,
name|docsTraversed
argument_list|,
name|gc
operator|.
name|getNumDocuments
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|gc
operator|.
name|possiblyDeleted
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|Utils
operator|.
name|closeIfCloseable
argument_list|(
name|itr
argument_list|)
expr_stmt|;
block|}
name|stats
operator|.
name|collectDeletedDocs
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|gc
operator|.
name|getNumDocuments
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|stats
operator|.
name|deleteDeletedDocs
operator|.
name|start
argument_list|()
expr_stmt|;
name|gc
operator|.
name|removeDocuments
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|invalidateDocChildrenCache
argument_list|()
expr_stmt|;
name|stats
operator|.
name|deleteDeletedDocs
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|gc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|VersionGCStats
block|{
name|boolean
name|ignoredGCDueToCheckPoint
decl_stmt|;
name|int
name|deletedDocGCCount
decl_stmt|;
name|int
name|splitDocGCCount
decl_stmt|;
name|int
name|intermediateSplitDocGCCount
decl_stmt|;
specifier|final
name|Stopwatch
name|collectDeletedDocs
init|=
name|Stopwatch
operator|.
name|createUnstarted
argument_list|()
decl_stmt|;
specifier|final
name|Stopwatch
name|deleteDeletedDocs
init|=
name|Stopwatch
operator|.
name|createUnstarted
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"VersionGCStats{"
operator|+
literal|"ignoredGCDueToCheckPoint="
operator|+
name|ignoredGCDueToCheckPoint
operator|+
literal|", deletedDocGCCount="
operator|+
name|deletedDocGCCount
operator|+
literal|", splitDocGCCount="
operator|+
name|splitDocGCCount
operator|+
literal|", intermediateSplitDocGCCount="
operator|+
name|intermediateSplitDocGCCount
operator|+
literal|", timeToCollectDeletedDocs="
operator|+
name|collectDeletedDocs
operator|+
literal|", timeTakenToDeleteDocs="
operator|+
name|deleteDeletedDocs
operator|+
literal|'}'
return|;
block|}
block|}
comment|/**      * A helper class to remove document for deleted nodes.      */
specifier|private
class|class
name|DeletedDocsGC
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|Revision
name|headRevision
decl_stmt|;
specifier|private
specifier|final
name|StringSort
name|docIdsToDelete
init|=
name|newStringSort
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StringSort
name|prevDocIdsToDelete
init|=
name|newStringSort
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|exclude
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|sorted
init|=
literal|false
decl_stmt|;
specifier|public
name|DeletedDocsGC
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|headRevision
parameter_list|)
block|{
name|this
operator|.
name|headRevision
operator|=
name|checkNotNull
argument_list|(
name|headRevision
argument_list|)
expr_stmt|;
block|}
comment|/**          * @return the number of documents gathers so far that have been          * identified as garbage via {@link #possiblyDeleted(NodeDocument)}.          * This number does not include the previous documents.          */
name|long
name|getNumDocuments
parameter_list|()
block|{
return|return
name|docIdsToDelete
operator|.
name|getSize
argument_list|()
return|;
block|}
comment|/**          * Informs the GC that the given document is possibly deleted. The          * implementation will check if the node still exists at the head          * revision passed to the constructor to this GC. The implementation          * will keep track of documents representing deleted nodes and remove          * them together with associated previous document          *          * @param doc the candidate document.          */
name|void
name|possiblyDeleted
parameter_list|(
name|NodeDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|nodeStore
argument_list|,
name|headRevision
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// construct an id that also contains
comment|// the _modified time of the document
name|String
name|id
init|=
name|doc
operator|.
name|getId
argument_list|()
operator|+
literal|"/"
operator|+
name|doc
operator|.
name|getModified
argument_list|()
decl_stmt|;
name|addDocument
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// Collect id of all previous docs also
for|for
control|(
name|NodeDocument
name|prevDoc
range|:
name|copyOf
argument_list|(
name|doc
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
control|)
block|{
name|addPreviousDocument
argument_list|(
name|prevDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**          * Removes the documents that have been identified as garbage. This          * also includes previous documents. This method will only remove          * documents that have not been modified since they were passed to          * {@link #possiblyDeleted(NodeDocument)}.          *          * @param stats to track the number of removed documents.          */
name|void
name|removeDocuments
parameter_list|(
name|VersionGCStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
name|stats
operator|.
name|deletedDocGCCount
operator|+=
name|removeDeletedDocuments
argument_list|()
expr_stmt|;
comment|// FIXME: this is incorrect because that method also removes intermediate docs
name|stats
operator|.
name|splitDocGCCount
operator|+=
name|removeDeletedPreviousDocuments
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|docIdsToDelete
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to close docIdsToDelete"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|prevDocIdsToDelete
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to close prevDocIdsToDelete"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------< internal>----------------------------
specifier|private
name|void
name|addDocument
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|docIdsToDelete
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|getNumPreviousDocuments
parameter_list|()
block|{
return|return
name|prevDocIdsToDelete
operator|.
name|getSize
argument_list|()
operator|-
name|exclude
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|void
name|addPreviousDocument
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|prevDocIdsToDelete
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|getDocIdsToDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureSorted
argument_list|()
expr_stmt|;
return|return
name|docIdsToDelete
operator|.
name|getIds
argument_list|()
return|;
block|}
specifier|private
name|void
name|concurrentModification
parameter_list|(
name|NodeDocument
name|doc
parameter_list|)
block|{
for|for
control|(
name|NodeDocument
name|prevDoc
range|:
name|copyOf
argument_list|(
name|doc
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
control|)
block|{
name|exclude
operator|.
name|add
argument_list|(
name|prevDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPrevDocIdsToDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureSorted
argument_list|()
expr_stmt|;
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|prevDocIdsToDelete
operator|.
name|getIds
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
operator|!
name|exclude
operator|.
name|contains
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|int
name|removeDeletedDocuments
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|docIdsToDelete
init|=
name|getDocIdsToDelete
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to delete [{}] documents"
argument_list|,
name|getNumDocuments
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|idListItr
init|=
name|partition
argument_list|(
name|docIdsToDelete
argument_list|,
name|DELETE_BATCH_SIZE
argument_list|)
decl_stmt|;
name|int
name|deletedCount
init|=
literal|0
decl_stmt|;
name|int
name|lastLoggedCount
init|=
literal|0
decl_stmt|;
name|int
name|recreatedCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|idListItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
argument_list|>
name|deletionBatch
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|idListItr
operator|.
name|next
argument_list|()
control|)
block|{
name|int
name|idx
init|=
name|s
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|long
name|modified
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|modified
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid _modified {} for {}"
argument_list|,
name|s
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|deletionBatch
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|singletonMap
argument_list|(
name|KEY_MODIFIED
argument_list|,
name|newEqualsCondition
argument_list|(
name|modified
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Performing batch deletion of documents with following ids. \n"
argument_list|)
decl_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|appendTo
argument_list|(
name|sb
argument_list|,
name|deletionBatch
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|nRemoved
init|=
name|ds
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|deletionBatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|nRemoved
operator|<
name|deletionBatch
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// some nodes were re-created while GC was running
comment|// find the document that still exist
for|for
control|(
name|String
name|id
range|:
name|deletionBatch
operator|.
name|keySet
argument_list|()
control|)
block|{
name|NodeDocument
name|d
init|=
name|ds
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|concurrentModification
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
name|recreatedCount
operator|+=
operator|(
name|deletionBatch
operator|.
name|size
argument_list|()
operator|-
name|nRemoved
operator|)
expr_stmt|;
block|}
name|deletedCount
operator|+=
name|nRemoved
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Deleted [{}] documents so far"
argument_list|,
name|deletedCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|deletedCount
operator|+
name|recreatedCount
operator|-
name|lastLoggedCount
operator|>=
name|PROGRESS_BATCH_SIZE
condition|)
block|{
name|lastLoggedCount
operator|=
name|deletedCount
operator|+
name|recreatedCount
expr_stmt|;
name|double
name|progress
init|=
name|lastLoggedCount
operator|*
literal|1.0
operator|/
name|getNumDocuments
argument_list|()
operator|*
literal|100
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Deleted %d (%1.2f%%) documents so far"
argument_list|,
name|deletedCount
argument_list|,
name|progress
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deletedCount
return|;
block|}
specifier|private
name|int
name|removeDeletedPreviousDocuments
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to delete [{}] previous documents"
argument_list|,
name|getNumPreviousDocuments
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|deletedCount
init|=
literal|0
decl_stmt|;
name|int
name|lastLoggedCount
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|idListItr
init|=
name|partition
argument_list|(
name|getPrevDocIdsToDelete
argument_list|()
argument_list|,
name|DELETE_BATCH_SIZE
argument_list|)
decl_stmt|;
while|while
condition|(
name|idListItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|deletionBatch
init|=
name|idListItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|deletedCount
operator|+=
name|deletionBatch
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Performing batch deletion of previous documents with following ids. \n"
argument_list|)
decl_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|appendTo
argument_list|(
name|sb
argument_list|,
name|deletionBatch
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|remove
argument_list|(
name|NODES
argument_list|,
name|deletionBatch
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Deleted [{}] previous documents so far"
argument_list|,
name|deletedCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|deletedCount
operator|-
name|lastLoggedCount
operator|>=
name|PROGRESS_BATCH_SIZE
condition|)
block|{
name|lastLoggedCount
operator|=
name|deletedCount
expr_stmt|;
name|double
name|progress
init|=
name|deletedCount
operator|*
literal|1.0
operator|/
operator|(
name|prevDocIdsToDelete
operator|.
name|getSize
argument_list|()
operator|-
name|exclude
operator|.
name|size
argument_list|()
operator|)
operator|*
literal|100
decl_stmt|;
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Deleted %d (%1.2f%%) previous documents so far"
argument_list|,
name|deletedCount
argument_list|,
name|progress
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deletedCount
return|;
block|}
specifier|private
name|void
name|ensureSorted
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|sorted
condition|)
block|{
name|docIdsToDelete
operator|.
name|sort
argument_list|()
expr_stmt|;
name|prevDocIdsToDelete
operator|.
name|sort
argument_list|()
expr_stmt|;
name|sorted
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|StringSort
name|newStringSort
parameter_list|()
block|{
return|return
operator|new
name|StringSort
argument_list|(
name|overflowToDiskThreshold
argument_list|,
name|NodeDocumentIdComparator
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

