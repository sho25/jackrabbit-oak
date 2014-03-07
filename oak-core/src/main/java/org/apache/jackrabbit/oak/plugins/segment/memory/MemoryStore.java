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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|ConcurrentMap
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Blob
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
name|plugins
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentNodeState
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
name|plugins
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
name|state
operator|.
name|NodeBuilder
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

begin_class
specifier|public
class|class
name|MemoryStore
implements|implements
name|SegmentStore
block|{
specifier|private
specifier|final
name|SegmentIdFactory
name|factory
init|=
operator|new
name|SegmentIdFactory
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|this
argument_list|,
name|factory
argument_list|)
decl_stmt|;
specifier|private
name|SegmentNodeState
name|head
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|UUID
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
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|SegmentWriter
name|writer
init|=
name|getWriter
argument_list|()
decl_stmt|;
name|this
operator|.
name|head
operator|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MemoryStore
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|SegmentNodeState
name|getHead
parameter_list|()
block|{
return|return
name|head
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|setHead
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|,
name|SegmentNodeState
name|head
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|head
operator|.
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
name|base
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|UUID
name|uuid
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|segments
operator|.
name|get
argument_list|(
name|uuid
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
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Segment not found: "
operator|+
name|uuid
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSegment
parameter_list|(
name|UUID
name|segmentId
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
name|factory
argument_list|,
name|segmentId
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
name|segmentId
argument_list|,
name|segment
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Segment override: "
operator|+
name|segmentId
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
block|{     }
annotation|@
name|Override
specifier|public
name|Blob
name|readBlob
parameter_list|(
name|String
name|reference
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

