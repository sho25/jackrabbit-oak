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
name|mongo
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
name|checkState
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
name|primaryPreferred
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
name|segment
operator|.
name|Journal
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
name|MergeDiff
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
name|BasicDBObjectBuilder
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
name|MongoException
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

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
import|;
end_import

begin_class
class|class
name|MongoJournal
implements|implements
name|Journal
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
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|journals
decl_stmt|;
specifier|private
specifier|final
name|WriteConcern
name|concern
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|DBObject
name|state
decl_stmt|;
specifier|private
name|long
name|stateLastUpdated
decl_stmt|;
name|MongoJournal
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|DBCollection
name|journals
parameter_list|,
name|WriteConcern
name|concern
parameter_list|,
name|NodeState
name|head
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|journals
operator|=
name|checkNotNull
argument_list|(
name|journals
argument_list|)
expr_stmt|;
name|this
operator|.
name|concern
operator|=
name|checkNotNull
argument_list|(
name|concern
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|"root"
expr_stmt|;
name|DBObject
name|id
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
literal|"root"
argument_list|)
decl_stmt|;
name|state
operator|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|SegmentWriter
name|writer
init|=
name|store
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|RecordId
name|headId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|head
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
name|state
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|of
argument_list|(
literal|"_id"
argument_list|,
literal|"root"
argument_list|,
literal|"head"
argument_list|,
name|headId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|journals
operator|.
name|insert
argument_list|(
name|state
argument_list|,
name|concern
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoException
operator|.
name|DuplicateKey
name|e
parameter_list|)
block|{
comment|// Someone else managed to concurrently create the journal,
comment|// so let's just re-read it from the database
name|state
operator|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|state
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|stateLastUpdated
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|MongoJournal
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|DBCollection
name|journals
parameter_list|,
name|WriteConcern
name|concern
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|journals
operator|=
name|checkNotNull
argument_list|(
name|journals
argument_list|)
expr_stmt|;
name|this
operator|.
name|concern
operator|=
name|checkNotNull
argument_list|(
name|concern
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
literal|"root"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|DBObject
name|id
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|state
operator|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|Journal
name|root
init|=
name|store
operator|.
name|getJournal
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|String
name|head
init|=
name|root
operator|.
name|getHead
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|state
operator|=
operator|new
name|BasicDBObject
argument_list|(
name|of
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|,
literal|"parent"
argument_list|,
literal|"root"
argument_list|,
literal|"base"
argument_list|,
name|head
argument_list|,
literal|"head"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|journals
operator|.
name|insert
argument_list|(
name|state
argument_list|,
name|concern
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoException
operator|.
name|DuplicateKey
name|e
parameter_list|)
block|{
comment|// Someone else managed to concurrently create the journal,
comment|// so let's just re-read it from the database
name|state
operator|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|state
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|stateLastUpdated
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
name|stateLastUpdated
operator|+
name|UPDATE_INTERVAL
operator|<
name|now
condition|)
block|{
name|DBObject
name|id
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|DBObject
name|freshState
init|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|nearest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|freshState
operator|==
literal|null
condition|)
block|{
name|freshState
operator|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|freshState
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|freshState
expr_stmt|;
name|stateLastUpdated
operator|=
name|now
expr_stmt|;
block|}
return|return
name|RecordId
operator|.
name|fromString
argument_list|(
name|state
operator|.
name|get
argument_list|(
literal|"head"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
if|if
condition|(
operator|!
name|base
operator|.
name|equals
argument_list|(
name|getHead
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BasicDBObjectBuilder
name|builder
init|=
name|BasicDBObjectBuilder
operator|.
name|start
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|containsField
argument_list|(
literal|"parent"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
literal|"parent"
argument_list|,
name|state
operator|.
name|get
argument_list|(
literal|"parent"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|containsField
argument_list|(
literal|"base"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
literal|"base"
argument_list|,
name|state
operator|.
name|get
argument_list|(
literal|"base"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
literal|"head"
argument_list|,
name|head
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DBObject
name|nextState
init|=
name|builder
operator|.
name|get
argument_list|()
decl_stmt|;
name|WriteResult
name|result
init|=
name|journals
operator|.
name|update
argument_list|(
name|state
argument_list|,
name|nextState
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|concern
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getN
argument_list|()
operator|==
literal|1
condition|)
block|{
name|state
operator|=
name|nextState
expr_stmt|;
name|stateLastUpdated
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// force refresh when next accessed
name|stateLastUpdated
operator|-=
name|UPDATE_INTERVAL
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|merge
parameter_list|()
block|{
name|DBObject
name|id
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|DBObject
name|state
init|=
name|journals
operator|.
name|findOne
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|primaryPreferred
argument_list|()
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|state
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|containsField
argument_list|(
literal|"parent"
argument_list|)
condition|)
block|{
name|RecordId
name|base
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|state
operator|.
name|get
argument_list|(
literal|"base"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|head
init|=
name|RecordId
operator|.
name|fromString
argument_list|(
name|state
operator|.
name|get
argument_list|(
literal|"head"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|head
argument_list|)
decl_stmt|;
name|Journal
name|parent
init|=
name|store
operator|.
name|getJournal
argument_list|(
name|state
operator|.
name|get
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentWriter
name|writer
init|=
name|store
operator|.
name|getWriter
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|parent
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
condition|)
block|{
name|RecordId
name|newBase
init|=
name|parent
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|newBase
argument_list|)
operator|.
name|builder
argument_list|()
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|MergeDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|RecordId
name|newHead
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
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
name|base
operator|=
name|newBase
expr_stmt|;
name|head
operator|=
name|newHead
expr_stmt|;
block|}
name|base
operator|=
name|head
expr_stmt|;
name|BasicDBObjectBuilder
name|builder
init|=
name|BasicDBObjectBuilder
operator|.
name|start
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"_id"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"parent"
argument_list|,
name|state
operator|.
name|get
argument_list|(
literal|"parent"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"base"
argument_list|,
name|base
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"head"
argument_list|,
name|head
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: concurrent updates?
name|journals
operator|.
name|update
argument_list|(
name|state
argument_list|,
name|builder
operator|.
name|get
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|concern
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

