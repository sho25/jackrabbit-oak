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
name|concurrent
operator|.
name|TimeUnit
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
name|DBCollection
name|journals
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
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
decl_stmt|;
name|MongoJournal
parameter_list|(
name|DBCollection
name|journals
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|journals
operator|=
name|journals
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|head
operator|=
name|getHead
argument_list|()
expr_stmt|;
block|}
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
end_class

end_unit

