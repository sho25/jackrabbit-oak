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
name|List
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
name|TimeUnit
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
import|;
end_import

begin_class
specifier|public
class|class
name|MongoStore
implements|implements
name|SegmentStore
block|{
specifier|private
specifier|static
specifier|final
name|long
name|UPDATE_INTERVAL
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|segments
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|journals
decl_stmt|;
specifier|private
specifier|final
name|SegmentCache
name|cache
decl_stmt|;
specifier|public
name|MongoStore
parameter_list|(
name|DB
name|db
parameter_list|,
name|SegmentCache
name|cache
parameter_list|)
block|{
name|this
operator|.
name|segments
operator|=
name|db
operator|.
name|getCollection
argument_list|(
literal|"segments"
argument_list|)
expr_stmt|;
name|this
operator|.
name|journals
operator|=
name|db
operator|.
name|getCollection
argument_list|(
literal|"journals"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
if|if
condition|(
name|journals
operator|.
name|findOne
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
literal|"root"
argument_list|)
argument_list|)
operator|==
literal|null
condition|)
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
name|RecordId
name|id
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|journals
operator|.
name|insert
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"_id"
argument_list|,
literal|"root"
argument_list|,
literal|"head"
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|MongoStore
parameter_list|(
name|DB
name|db
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|this
argument_list|(
name|db
argument_list|,
operator|new
name|SegmentCache
argument_list|(
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MongoStore
parameter_list|(
name|Mongo
name|mongo
parameter_list|,
name|long
name|cacheSize
parameter_list|)
block|{
name|this
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|(
literal|"Oak"
argument_list|)
argument_list|,
name|cacheSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Journal
name|getJournal
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Journal
argument_list|()
block|{
specifier|private
name|long
name|nextUpdate
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
literal|2
operator|*
name|UPDATE_INTERVAL
decl_stmt|;
specifier|private
name|RecordId
name|head
init|=
name|getHead
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RecordId
name|getHead
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>=
name|nextUpdate
condition|)
block|{
name|DBObject
name|journal
init|=
name|journals
operator|.
name|findOne
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|head
operator|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|journal
operator|.
name|get
argument_list|(
literal|"head"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|nextUpdate
operator|=
name|now
operator|+
name|UPDATE_INTERVAL
expr_stmt|;
block|}
return|return
name|head
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setHead
parameter_list|(
name|RecordId
name|base
parameter_list|,
name|RecordId
name|head
parameter_list|)
block|{
name|DBObject
name|baseObject
init|=
operator|new
name|BasicDBObject
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|,
literal|"head"
argument_list|,
name|base
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DBObject
name|headObject
init|=
operator|new
name|BasicDBObject
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|,
literal|"head"
argument_list|,
name|head
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|journals
operator|.
name|findAndModify
argument_list|(
name|baseObject
argument_list|,
name|headObject
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
name|nextUpdate
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|UPDATE_INTERVAL
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|this
operator|.
name|head
argument_list|)
condition|)
block|{
comment|// force an update at next getHead() call
name|nextUpdate
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Segment
name|readSegment
parameter_list|(
specifier|final
name|UUID
name|segmentId
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getSegment
argument_list|(
name|segmentId
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
name|findSegment
argument_list|(
name|segmentId
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
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
name|cache
operator|.
name|addSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|insertSegment
argument_list|(
name|segment
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|segment
operator|.
name|getData
argument_list|()
argument_list|,
name|segment
operator|.
name|getUUIDs
argument_list|()
argument_list|)
expr_stmt|;
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
name|d
init|=
name|data
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|0
operator|||
name|length
operator|!=
name|data
operator|.
name|length
condition|)
block|{
name|d
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|d
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|insertSegment
argument_list|(
name|segmentId
argument_list|,
name|d
argument_list|,
operator|new
name|UUID
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Segment
name|findSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
block|{
name|DBObject
name|segment
init|=
name|segments
operator|.
name|findOne
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|segmentId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Segment "
operator|+
name|segmentId
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|segment
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|segment
operator|.
name|get
argument_list|(
literal|"uuids"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UUID
argument_list|>
name|uuids
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|object
range|:
name|list
control|)
block|{
name|uuids
operator|.
name|add
argument_list|(
name|UUID
operator|.
name|fromString
argument_list|(
name|object
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Segment
argument_list|(
name|this
argument_list|,
name|segmentId
argument_list|,
name|data
argument_list|,
name|uuids
argument_list|)
return|;
block|}
specifier|private
name|void
name|insertSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|UUID
index|[]
name|uuids
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|uuids
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|UUID
name|uuid
range|:
name|uuids
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|uuid
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BasicDBObject
name|segment
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|segment
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
name|segmentId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|segment
operator|.
name|put
argument_list|(
literal|"data"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|segment
operator|.
name|put
argument_list|(
literal|"uuids"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|segments
operator|.
name|insert
argument_list|(
name|segment
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
name|segments
operator|.
name|remove
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|segmentId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|removeSegment
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

