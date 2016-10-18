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
name|memory
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
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|nio
operator|.
name|ByteBuffer
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
name|ConcurrentMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|segment
operator|.
name|CachingSegmentReader
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
name|Revisions
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
name|SegmentIdFactory
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
name|SegmentNotFoundException
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
name|SegmentReader
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
name|SegmentStore
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
name|SegmentTracker
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_comment
comment|/**  * A store used for in-memory operations.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryStore
implements|implements
name|SegmentStore
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentTracker
name|tracker
init|=
operator|new
name|SegmentTracker
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|MemoryStoreRevisions
name|revisions
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|segmentReader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentWriter
name|segmentWriter
decl_stmt|;
specifier|private
specifier|final
name|SegmentIdFactory
name|segmentIdFactory
init|=
operator|new
name|SegmentIdFactory
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
operator|new
name|SegmentId
argument_list|(
name|MemoryStore
operator|.
name|this
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|segments
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|public
name|MemoryStore
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|revisions
operator|=
operator|new
name|MemoryStoreRevisions
argument_list|()
expr_stmt|;
name|Supplier
argument_list|<
name|SegmentWriter
argument_list|>
name|getWriter
init|=
operator|new
name|Supplier
argument_list|<
name|SegmentWriter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SegmentWriter
name|get
parameter_list|()
block|{
return|return
name|getWriter
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|segmentReader
operator|=
operator|new
name|CachingSegmentReader
argument_list|(
name|getWriter
argument_list|,
literal|null
argument_list|,
literal|16
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentWriter
operator|=
name|segmentWriterBuilder
argument_list|(
literal|"sys"
argument_list|)
operator|.
name|withWriterPool
argument_list|()
operator|.
name|build
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|revisions
operator|.
name|bind
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|segmentWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentWriter
name|getWriter
parameter_list|()
block|{
return|return
name|segmentWriter
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SegmentReader
name|getReader
parameter_list|()
block|{
return|return
name|segmentReader
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Revisions
name|getRevisions
parameter_list|()
block|{
return|return
name|revisions
return|;
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
name|id
operator|.
name|sameStore
argument_list|(
name|this
argument_list|)
operator|||
name|segments
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|segments
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
return|return
name|segment
return|;
block|}
throw|throw
operator|new
name|SegmentNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
name|tracker
operator|.
name|newSegmentId
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|segmentIdFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentId
name|newBulkSegmentId
parameter_list|()
block|{
return|return
name|tracker
operator|.
name|newBulkSegmentId
argument_list|(
name|segmentIdFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|SegmentId
name|newDataSegmentId
parameter_list|()
block|{
return|return
name|tracker
operator|.
name|newDataSegmentId
argument_list|(
name|segmentIdFactory
argument_list|)
return|;
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
throws|throws
name|IOException
block|{
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|Segment
name|segment
init|=
operator|new
name|Segment
argument_list|(
name|this
argument_list|,
name|segmentReader
argument_list|,
name|id
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|segments
operator|.
name|putIfAbsent
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Segment override: "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|CheckForNull
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|segments
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|tracker
operator|.
name|getReferencedSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

