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
package|;
end_package

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
name|MemoryNodeState
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
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|journals
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
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
name|SegmentWriter
name|writer
init|=
operator|new
name|SegmentWriter
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|journals
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
name|writer
operator|.
name|writeNode
argument_list|(
name|root
argument_list|)
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
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RecordId
name|getJournalHead
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RecordId
name|head
init|=
name|journals
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|head
operator|!=
literal|null
condition|)
block|{
return|return
name|head
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Journal not found: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setJournalHead
parameter_list|(
name|String
name|name
parameter_list|,
name|RecordId
name|head
parameter_list|,
name|RecordId
name|base
parameter_list|)
block|{
return|return
name|journals
operator|.
name|replace
argument_list|(
name|name
argument_list|,
name|base
argument_list|,
name|head
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|UUID
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
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Segment not found: "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|createSegment
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
if|if
condition|(
name|segments
operator|.
name|put
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
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
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|createSegment
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
name|byte
index|[]
name|segment
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|segment
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|createSegment
argument_list|(
operator|new
name|Segment
argument_list|(
name|segmentId
argument_list|,
name|segment
argument_list|,
operator|new
name|UUID
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
block|{
if|if
condition|(
name|segments
operator|.
name|remove
argument_list|(
name|segmentId
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing segment: "
operator|+
name|segmentId
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

