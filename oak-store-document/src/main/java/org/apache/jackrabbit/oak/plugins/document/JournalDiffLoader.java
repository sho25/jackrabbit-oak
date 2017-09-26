begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
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
name|TimeUnit
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
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
name|Stopwatch
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
name|cache
operator|.
name|CacheStats
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
name|util
operator|.
name|StringValue
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
name|checkArgument
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|byteCountToDisplaySize
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
name|JournalEntry
operator|.
name|asId
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
name|JournalEntry
operator|.
name|fillExternalChanges
import|;
end_import

begin_comment
comment|/**  * A DiffCache loader reading from journal entries.  */
end_comment

begin_class
class|class
name|JournalDiffLoader
implements|implements
name|DiffCache
operator|.
name|Loader
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
name|JournalDiffLoader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AbstractDocumentNodeState
name|base
decl_stmt|;
specifier|private
specifier|final
name|AbstractDocumentNodeState
name|node
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|ns
decl_stmt|;
specifier|private
name|Stats
name|stats
decl_stmt|;
name|JournalDiffLoader
parameter_list|(
annotation|@
name|Nonnull
name|AbstractDocumentNodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|AbstractDocumentNodeState
name|node
parameter_list|,
annotation|@
name|Nonnull
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|checkNotNull
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|ns
operator|=
name|checkNotNull
argument_list|(
name|ns
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|base
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"nodes must have matching paths: {} != {}"
argument_list|,
name|base
operator|.
name|getPath
argument_list|()
argument_list|,
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
block|{
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|RevisionVector
name|afterRev
init|=
name|node
operator|.
name|getRootRevision
argument_list|()
decl_stmt|;
name|RevisionVector
name|beforeRev
init|=
name|base
operator|.
name|getRootRevision
argument_list|()
decl_stmt|;
name|stats
operator|=
operator|new
name|Stats
argument_list|(
name|path
argument_list|,
name|beforeRev
argument_list|,
name|afterRev
argument_list|)
expr_stmt|;
name|StringSort
name|changes
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
try|try
block|{
name|readTrunkChanges
argument_list|(
name|path
argument_list|,
name|beforeRev
argument_list|,
name|afterRev
argument_list|,
name|changes
argument_list|)
expr_stmt|;
name|readBranchChanges
argument_list|(
name|path
argument_list|,
name|beforeRev
argument_list|,
name|changes
argument_list|)
expr_stmt|;
name|readBranchChanges
argument_list|(
name|path
argument_list|,
name|afterRev
argument_list|,
name|changes
argument_list|)
expr_stmt|;
name|changes
operator|.
name|sort
argument_list|()
expr_stmt|;
name|DiffCache
name|df
init|=
name|ns
operator|.
name|getDiffCache
argument_list|()
decl_stmt|;
name|WrappedDiffCache
name|wrappedCache
init|=
operator|new
name|WrappedDiffCache
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|df
argument_list|,
name|stats
argument_list|)
decl_stmt|;
name|JournalEntry
operator|.
name|applyTo
argument_list|(
name|changes
argument_list|,
name|wrappedCache
argument_list|,
name|path
argument_list|,
name|beforeRev
argument_list|,
name|afterRev
argument_list|)
expr_stmt|;
return|return
name|wrappedCache
operator|.
name|changes
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|DocumentStoreException
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|Utils
operator|.
name|closeIfCloseable
argument_list|(
name|changes
argument_list|)
expr_stmt|;
name|logStats
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readBranchChanges
parameter_list|(
name|String
name|path
parameter_list|,
name|RevisionVector
name|rv
parameter_list|,
name|StringSort
name|changes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|rv
operator|.
name|isBranch
argument_list|()
operator|||
name|ns
operator|.
name|isDisableBranches
argument_list|()
condition|)
block|{
return|return;
block|}
name|Branch
name|b
init|=
name|ns
operator|.
name|getBranches
argument_list|()
operator|.
name|getBranch
argument_list|(
name|rv
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|ns
operator|.
name|getBranches
argument_list|()
operator|.
name|isBranchBase
argument_list|(
name|rv
argument_list|)
condition|)
block|{
name|missingBranch
argument_list|(
name|rv
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
for|for
control|(
name|Revision
name|br
range|:
name|b
operator|.
name|getCommits
argument_list|()
control|)
block|{
name|Branch
operator|.
name|BranchCommit
name|bc
init|=
name|b
operator|.
name|getCommit
argument_list|(
name|br
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bc
operator|.
name|isRebase
argument_list|()
condition|)
block|{
name|JournalEntry
name|entry
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|JOURNAL
argument_list|,
name|asId
argument_list|(
name|br
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|addTo
argument_list|(
name|changes
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|stats
operator|.
name|numJournalEntries
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Missing journal entry for {}"
argument_list|,
name|asId
argument_list|(
name|br
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|readTrunkChanges
parameter_list|(
name|String
name|path
parameter_list|,
name|RevisionVector
name|beforeRev
parameter_list|,
name|RevisionVector
name|afterRev
parameter_list|,
name|StringSort
name|changes
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalEntry
name|localPending
init|=
name|ns
operator|.
name|getCurrentJournalEntry
argument_list|()
decl_stmt|;
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|NodeDocument
name|root
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|int
name|clusterId
init|=
name|ns
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Revision
argument_list|>
name|lastRevs
init|=
name|root
operator|.
name|getLastRev
argument_list|()
decl_stmt|;
name|Revision
name|localLastRev
decl_stmt|;
if|if
condition|(
name|clusterId
operator|==
literal|0
condition|)
block|{
comment|// read-only node store
name|localLastRev
operator|=
name|afterRev
operator|.
name|getRevision
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|localLastRev
operator|=
name|lastRevs
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localLastRev
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Root document does not have a "
operator|+
literal|"lastRev entry for local clusterId "
operator|+
name|clusterId
argument_list|)
throw|;
block|}
if|if
condition|(
name|ns
operator|.
name|isDisableBranches
argument_list|()
condition|)
block|{
name|beforeRev
operator|=
name|beforeRev
operator|.
name|asTrunkRevision
argument_list|()
expr_stmt|;
name|afterRev
operator|=
name|afterRev
operator|.
name|asTrunkRevision
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|beforeRev
operator|=
name|getBaseRevision
argument_list|(
name|beforeRev
argument_list|)
expr_stmt|;
name|afterRev
operator|=
name|getBaseRevision
argument_list|(
name|afterRev
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|beforeRev
operator|.
name|equals
argument_list|(
name|afterRev
argument_list|)
condition|)
block|{
return|return;
block|}
name|RevisionVector
name|max
init|=
name|beforeRev
operator|.
name|pmax
argument_list|(
name|afterRev
argument_list|)
decl_stmt|;
name|RevisionVector
name|min
init|=
name|beforeRev
operator|.
name|pmin
argument_list|(
name|afterRev
argument_list|)
decl_stmt|;
comment|// do we need to include changes from pending local changes?
if|if
condition|(
operator|!
name|max
operator|.
name|isRevisionNewer
argument_list|(
name|localLastRev
argument_list|)
operator|&&
operator|!
name|localLastRev
operator|.
name|equals
argument_list|(
name|max
operator|.
name|getRevision
argument_list|(
name|clusterId
argument_list|)
argument_list|)
condition|)
block|{
comment|// journal does not contain all local changes
name|localPending
operator|.
name|addTo
argument_list|(
name|changes
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|stats
operator|.
name|numJournalEntries
operator|++
expr_stmt|;
block|}
for|for
control|(
name|Revision
name|to
range|:
name|max
control|)
block|{
name|Revision
name|from
init|=
name|min
operator|.
name|getRevision
argument_list|(
name|to
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
comment|// there is no min revision with this clusterId
comment|// use revision with a timestamp of zero
name|from
operator|=
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StringSort
name|invalidateOnly
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
try|try
block|{
name|stats
operator|.
name|numJournalEntries
operator|+=
name|fillExternalChanges
argument_list|(
name|changes
argument_list|,
name|invalidateOnly
argument_list|,
name|path
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|ns
operator|.
name|getDocumentStore
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|invalidateOnly
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|RevisionVector
name|getBaseRevision
parameter_list|(
name|RevisionVector
name|rv
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rv
operator|.
name|isBranch
argument_list|()
condition|)
block|{
return|return
name|rv
return|;
block|}
name|Branch
name|b
init|=
name|ns
operator|.
name|getBranches
argument_list|()
operator|.
name|getBranch
argument_list|(
name|rv
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|rv
operator|=
name|b
operator|.
name|getBase
argument_list|(
name|rv
operator|.
name|getBranchRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ns
operator|.
name|getBranches
argument_list|()
operator|.
name|isBranchBase
argument_list|(
name|rv
argument_list|)
condition|)
block|{
name|rv
operator|=
name|rv
operator|.
name|asTrunkRevision
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingBranch
argument_list|(
name|rv
argument_list|)
expr_stmt|;
block|}
return|return
name|rv
return|;
block|}
specifier|private
specifier|static
name|void
name|missingBranch
parameter_list|(
name|RevisionVector
name|rv
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing branch for revision "
operator|+
name|rv
argument_list|)
throw|;
block|}
specifier|private
name|void
name|logStats
parameter_list|()
block|{
name|stats
operator|.
name|sw
operator|.
name|stop
argument_list|()
expr_stmt|;
name|long
name|timeInSec
init|=
name|stats
operator|.
name|sw
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeInSec
operator|>
literal|60
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|stats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timeInSec
operator|>
literal|10
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|stats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|stats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Stats
block|{
specifier|private
specifier|final
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|RevisionVector
name|from
decl_stmt|,
name|to
decl_stmt|;
specifier|private
name|long
name|numJournalEntries
decl_stmt|;
specifier|private
name|long
name|numDiffEntries
decl_stmt|;
specifier|private
name|long
name|keyMemory
decl_stmt|;
specifier|private
name|long
name|valueMemory
decl_stmt|;
name|Stats
parameter_list|(
name|String
name|path
parameter_list|,
name|RevisionVector
name|from
parameter_list|,
name|RevisionVector
name|to
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|msg
init|=
literal|"%d diffs for %s (%s/%s) loaded from %d journal entries in %s. "
operator|+
literal|"Keys: %s, values: %s, total: %s"
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
name|msg
argument_list|,
name|numDiffEntries
argument_list|,
name|path
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|numJournalEntries
argument_list|,
name|sw
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|keyMemory
argument_list|)
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|valueMemory
argument_list|)
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|keyMemory
operator|+
name|valueMemory
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|WrappedDiffCache
extends|extends
name|DiffCache
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|String
name|changes
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
name|DiffCache
name|cache
decl_stmt|;
specifier|private
name|Stats
name|stats
decl_stmt|;
name|WrappedDiffCache
parameter_list|(
name|String
name|path
parameter_list|,
name|DiffCache
name|cache
parameter_list|,
name|Stats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
block|}
annotation|@
name|CheckForNull
name|String
name|getChanges
parameter_list|()
block|{
return|return
name|changes
return|;
block|}
annotation|@
name|Override
name|String
name|getChanges
parameter_list|(
annotation|@
name|Nonnull
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|Nonnull
name|RevisionVector
name|to
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|Loader
name|loader
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
name|loader
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
name|Entry
name|newEntry
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|RevisionVector
name|from
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|RevisionVector
name|to
parameter_list|,
name|boolean
name|local
parameter_list|)
block|{
specifier|final
name|Entry
name|entry
init|=
name|cache
operator|.
name|newEntry
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|local
argument_list|)
decl_stmt|;
return|return
operator|new
name|Entry
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|append
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|String
name|changes
parameter_list|)
block|{
name|trackStats
argument_list|(
name|path
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|changes
argument_list|)
expr_stmt|;
name|entry
operator|.
name|append
argument_list|(
name|path
argument_list|,
name|changes
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|WrappedDiffCache
operator|.
name|this
operator|.
name|path
argument_list|)
condition|)
block|{
name|WrappedDiffCache
operator|.
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|done
parameter_list|()
block|{
return|return
name|entry
operator|.
name|done
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|private
name|void
name|trackStats
parameter_list|(
name|String
name|path
parameter_list|,
name|RevisionVector
name|from
parameter_list|,
name|RevisionVector
name|to
parameter_list|,
name|String
name|changes
parameter_list|)
block|{
name|stats
operator|.
name|numDiffEntries
operator|++
expr_stmt|;
name|stats
operator|.
name|keyMemory
operator|+=
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|stats
operator|.
name|keyMemory
operator|+=
name|from
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|stats
operator|.
name|keyMemory
operator|+=
name|to
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|stats
operator|.
name|valueMemory
operator|+=
operator|new
name|StringValue
argument_list|(
name|changes
argument_list|)
operator|.
name|getMemory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
name|Iterable
argument_list|<
name|CacheStats
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|cache
operator|.
name|getStats
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
