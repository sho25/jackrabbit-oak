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
name|segment
operator|.
name|file
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
name|Sets
operator|.
name|newHashSet
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
name|segment
operator|.
name|SegmentId
operator|.
name|isDataSegmentId
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCStatus
operator|.
name|CLEANUP
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
name|segment
operator|.
name|file
operator|.
name|PrintableBytes
operator|.
name|newPrintableBytes
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
name|Collection
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|SegmentId
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|CleanupContext
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarFiles
import|;
end_import

begin_class
class|class
name|DefaultCleanupStrategy
implements|implements
name|CleanupStrategy
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintableStopwatch
name|watch
init|=
name|PrintableStopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|info
argument_list|(
literal|"cleanup started using reclaimer {}"
argument_list|,
name|context
operator|.
name|getReclaimer
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|updateStatus
argument_list|(
name|CLEANUP
operator|.
name|message
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getSegmentCache
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Suggest to the JVM that now would be a good time
comment|// to clear stale weak references in the SegmentTracker
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|TarFiles
operator|.
name|CleanupResult
name|cleanupResult
init|=
name|context
operator|.
name|getTarFiles
argument_list|()
operator|.
name|cleanup
argument_list|(
name|newCleanupContext
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|getReclaimer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cleanupResult
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|info
argument_list|(
literal|"cleanup interrupted"
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|getSegmentTracker
argument_list|()
operator|.
name|clearSegmentIdTables
argument_list|(
name|cleanupResult
operator|.
name|getReclaimedSegmentIds
argument_list|()
argument_list|,
name|context
operator|.
name|getSegmentEvictionReason
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|info
argument_list|(
literal|"cleanup marking files for deletion: {}"
argument_list|,
name|toFileNames
argument_list|(
name|cleanupResult
operator|.
name|getRemovableFiles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|finalSize
init|=
name|size
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|long
name|reclaimedSize
init|=
name|cleanupResult
operator|.
name|getReclaimedSize
argument_list|()
decl_stmt|;
name|context
operator|.
name|getFileStoreStats
argument_list|()
operator|.
name|reclaimed
argument_list|(
name|reclaimedSize
argument_list|)
expr_stmt|;
name|context
operator|.
name|getGCJournal
argument_list|()
operator|.
name|persist
argument_list|(
name|reclaimedSize
argument_list|,
name|finalSize
argument_list|,
name|getGcGeneration
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
operator|.
name|getCompactionMonitor
argument_list|()
operator|.
name|getCompactedNodes
argument_list|()
argument_list|,
name|context
operator|.
name|getCompactedRootId
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|cleaned
argument_list|(
name|reclaimedSize
argument_list|,
name|finalSize
argument_list|)
expr_stmt|;
name|context
operator|.
name|getGCListener
argument_list|()
operator|.
name|info
argument_list|(
literal|"cleanup completed in {}. Post cleanup size is {} and space reclaimed {}."
argument_list|,
name|watch
argument_list|,
name|newPrintableBytes
argument_list|(
name|finalSize
argument_list|)
argument_list|,
name|newPrintableBytes
argument_list|(
name|reclaimedSize
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cleanupResult
operator|.
name|getRemovableFiles
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|CleanupContext
name|newCleanupContext
parameter_list|(
name|Context
name|context
parameter_list|,
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|old
parameter_list|)
block|{
return|return
operator|new
name|CleanupContext
argument_list|()
block|{
specifier|private
name|boolean
name|isUnreferencedBulkSegment
parameter_list|(
name|UUID
name|id
parameter_list|,
name|boolean
name|referenced
parameter_list|)
block|{
return|return
operator|!
name|isDataSegmentId
argument_list|(
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
operator|&&
operator|!
name|referenced
return|;
block|}
specifier|private
name|boolean
name|isOldDataSegment
parameter_list|(
name|UUID
name|id
parameter_list|,
name|GCGeneration
name|generation
parameter_list|)
block|{
return|return
name|isDataSegmentId
argument_list|(
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
operator|&&
name|old
operator|.
name|apply
argument_list|(
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|UUID
argument_list|>
name|initialReferences
parameter_list|()
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|references
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentId
name|id
range|:
name|context
operator|.
name|getSegmentTracker
argument_list|()
operator|.
name|getReferencedSegmentIds
argument_list|()
control|)
block|{
if|if
condition|(
name|id
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
name|references
operator|.
name|add
argument_list|(
name|id
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|references
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldReclaim
parameter_list|(
name|UUID
name|id
parameter_list|,
name|GCGeneration
name|generation
parameter_list|,
name|boolean
name|referenced
parameter_list|)
block|{
return|return
name|isUnreferencedBulkSegment
argument_list|(
name|id
argument_list|,
name|referenced
argument_list|)
operator|||
name|isOldDataSegment
argument_list|(
name|id
argument_list|,
name|generation
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldFollow
parameter_list|(
name|UUID
name|from
parameter_list|,
name|UUID
name|to
parameter_list|)
block|{
return|return
operator|!
name|isDataSegmentId
argument_list|(
name|to
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|String
name|toFileNames
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
if|if
condition|(
name|files
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"none"
return|;
block|}
else|else
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|files
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|GCGeneration
name|getGcGeneration
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getRevisions
argument_list|()
operator|.
name|getHead
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|long
name|size
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getTarFiles
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

