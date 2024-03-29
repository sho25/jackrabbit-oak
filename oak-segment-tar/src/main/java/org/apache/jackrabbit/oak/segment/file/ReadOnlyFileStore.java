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
name|DefaultSegmentWriterBuilder
operator|.
name|defaultSegmentWriterBuilder
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
name|Reclaimers
operator|.
name|newOldReclaimer
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
name|ArrayList
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
name|UUID
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
name|Callable
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|io
operator|.
name|Closer
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
name|util
operator|.
name|concurrent
operator|.
name|UncheckedExecutionException
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
name|RecordId
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
name|Segment
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
name|SegmentWriter
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
name|compaction
operator|.
name|SegmentGCOptions
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
comment|/**  * A read only {@link AbstractFileStore} implementation that supports going back  * to old revisions.  *<p>  * All write methods are no-ops.  */
end_comment

begin_class
specifier|public
class|class
name|ReadOnlyFileStore
extends|extends
name|AbstractFileStore
block|{
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
name|ReadOnlyFileStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TarFiles
name|tarFiles
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
specifier|final
name|int
name|gcRetainedGenerations
decl_stmt|;
specifier|private
name|ReadOnlyRevisions
name|revisions
decl_stmt|;
specifier|private
name|RecordId
name|currentHead
decl_stmt|;
name|ReadOnlyFileStore
parameter_list|(
name|FileStoreBuilder
name|builder
parameter_list|)
throws|throws
name|InvalidFileStoreVersionException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|newManifestChecker
argument_list|(
name|builder
operator|.
name|getPersistence
argument_list|()
argument_list|,
name|builder
operator|.
name|getStrictVersionCheck
argument_list|()
argument_list|)
operator|.
name|checkManifest
argument_list|()
expr_stmt|;
name|tarFiles
operator|=
name|TarFiles
operator|.
name|builder
argument_list|()
operator|.
name|withDirectory
argument_list|(
name|directory
argument_list|)
operator|.
name|withTarRecovery
argument_list|(
name|recovery
argument_list|)
operator|.
name|withIOMonitor
argument_list|(
name|ioMonitor
argument_list|)
operator|.
name|withRemoteStoreMonitor
argument_list|(
name|remoteStoreMonitor
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
name|memoryMapping
argument_list|)
operator|.
name|withReadOnly
argument_list|()
operator|.
name|withPersistence
argument_list|(
name|builder
operator|.
name|getPersistence
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|writer
operator|=
name|defaultSegmentWriterBuilder
argument_list|(
literal|"read-only"
argument_list|)
operator|.
name|withoutCache
argument_list|()
operator|.
name|build
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|gcRetainedGenerations
operator|=
name|builder
operator|.
name|getGcOptions
argument_list|()
operator|.
name|getRetainedGenerations
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"TarMK ReadOnly opened: {} (mmap={})"
argument_list|,
name|directory
argument_list|,
name|memoryMapping
argument_list|)
expr_stmt|;
block|}
name|ReadOnlyFileStore
name|bind
parameter_list|(
annotation|@
name|NotNull
name|ReadOnlyRevisions
name|revisions
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|revisions
operator|.
name|bind
argument_list|(
name|this
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
name|currentHead
operator|=
name|revisions
operator|.
name|getHead
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Go to the specified {@code revision}      *       * @param revision      */
specifier|public
name|void
name|setRevision
parameter_list|(
name|String
name|revision
parameter_list|)
block|{
name|RecordId
name|newHead
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|tracker
argument_list|,
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|revisions
operator|.
name|setHead
argument_list|(
name|currentHead
argument_list|,
name|newHead
argument_list|)
condition|)
block|{
name|currentHead
operator|=
name|newHead
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Read Only Store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
return|return
name|tarFiles
operator|.
name|containsSegment
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Segment
name|readSegment
parameter_list|(
specifier|final
name|SegmentId
name|id
parameter_list|)
block|{
try|try
block|{
return|return
name|segmentCache
operator|.
name|getSegment
argument_list|(
name|id
argument_list|,
operator|new
name|Callable
argument_list|<
name|Segment
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Segment
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|readSegmentUncached
argument_list|(
name|tarFiles
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|UncheckedExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|asSegmentNotFoundException
argument_list|(
name|e
argument_list|,
name|id
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|tarFiles
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
name|closeAndLogOnFail
argument_list|(
name|closer
argument_list|)
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// for any memory-mappings that are no longer used
name|log
operator|.
name|info
argument_list|(
literal|"TarMK closed: {}"
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|SegmentWriter
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarReaderIndex
parameter_list|()
block|{
return|return
name|tarFiles
operator|.
name|getIndices
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarGraph
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tarFiles
operator|.
name|getGraph
argument_list|(
name|fileName
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|SegmentId
argument_list|>
name|getSegmentIds
parameter_list|()
block|{
name|List
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|id
range|:
name|tarFiles
operator|.
name|getSegmentIds
argument_list|()
control|)
block|{
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|tracker
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
annotation|@
name|Override
specifier|public
name|ReadOnlyRevisions
name|getRevisions
parameter_list|()
block|{
return|return
name|revisions
return|;
block|}
specifier|public
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
block|{
return|return
name|tracker
operator|.
name|getReferencedSegmentIds
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectBlobReferences
parameter_list|(
name|Consumer
argument_list|<
name|String
argument_list|>
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|tarFiles
operator|.
name|collectBlobReferences
argument_list|(
name|collector
argument_list|,
name|newOldReclaimer
argument_list|(
name|SegmentGCOptions
operator|.
name|GCType
operator|.
name|FULL
argument_list|,
name|revisions
operator|.
name|getHead
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|,
name|gcRetainedGenerations
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

