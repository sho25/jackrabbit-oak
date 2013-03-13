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
name|collect
operator|.
name|ImmutableMap
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
operator|.
name|nearest
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
operator|.
name|primary
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

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
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
specifier|final
name|WriteConcern
name|concern
init|=
name|WriteConcern
operator|.
name|SAFE
decl_stmt|;
comment|// TODO: MAJORITY?
specifier|private
specifier|final
name|DB
name|db
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|segments
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Journal
argument_list|>
name|journals
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
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
name|db
operator|=
name|checkNotNull
argument_list|(
name|db
argument_list|)
expr_stmt|;
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
name|cache
operator|=
name|cache
expr_stmt|;
name|journals
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
operator|new
name|MongoJournal
argument_list|(
name|this
argument_list|,
name|db
operator|.
name|getCollection
argument_list|(
literal|"journals"
argument_list|)
argument_list|,
name|concern
argument_list|,
name|EMPTY_NODE
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|synchronized
name|Journal
name|getJournal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Journal
name|journal
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
name|journal
operator|==
literal|null
condition|)
block|{
name|journal
operator|=
operator|new
name|MongoJournal
argument_list|(
name|this
argument_list|,
name|db
operator|.
name|getCollection
argument_list|(
literal|"journals"
argument_list|)
argument_list|,
name|concern
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|journals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|journal
argument_list|)
expr_stmt|;
block|}
return|return
name|journal
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
name|id
init|=
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
decl_stmt|;
name|DBObject
name|fields
init|=
operator|new
name|BasicDBObject
argument_list|(
name|of
argument_list|(
literal|"data"
argument_list|,
literal|1
argument_list|,
literal|"uuids"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DBObject
name|segment
init|=
name|segments
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
name|fields
argument_list|,
name|nearest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
name|segment
operator|=
name|segments
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
name|fields
argument_list|,
name|primary
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|,
name|concern
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

