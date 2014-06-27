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
name|failover
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverStore
implements|implements
name|SegmentStore
block|{
specifier|private
specifier|final
name|SegmentTracker
name|tracker
init|=
operator|new
name|SegmentTracker
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|delegate
decl_stmt|;
specifier|private
name|RemoteSegmentLoader
name|loader
decl_stmt|;
specifier|public
name|FailoverStore
parameter_list|(
name|SegmentStore
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Override
specifier|public
name|SegmentNodeState
name|getHead
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getHead
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
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
return|return
name|delegate
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|head
argument_list|)
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
name|delegate
operator|.
name|containsSegment
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Segment
name|readSegment
parameter_list|(
name|SegmentId
name|sid
parameter_list|)
block|{
name|Deque
argument_list|<
name|SegmentId
argument_list|>
name|ids
init|=
operator|new
name|ArrayDeque
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|ids
operator|.
name|offer
argument_list|(
name|sid
argument_list|)
expr_stmt|;
name|int
name|err
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|SegmentId
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SegmentId
name|id
init|=
name|ids
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|seen
operator|.
name|contains
argument_list|(
name|id
argument_list|)
operator|&&
operator|!
name|delegate
operator|.
name|containsSegment
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|Segment
name|s
init|=
name|loader
operator|.
name|readSegment
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|s
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
name|ids
operator|.
name|addAll
argument_list|(
name|s
operator|.
name|getReferencedIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|s
operator|.
name|writeTo
argument_list|(
name|bout
argument_list|)
expr_stmt|;
name|writeSegment
argument_list|(
name|id
argument_list|,
name|bout
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to write remote segment "
operator|+
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|seen
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ids
operator|.
name|removeAll
argument_list|(
name|seen
argument_list|)
expr_stmt|;
name|err
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|loader
operator|.
name|isClosed
argument_list|()
operator|||
name|err
operator|==
literal|4
condition|)
block|{
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to load remote segment "
operator|+
name|id
argument_list|)
throw|;
block|}
name|err
operator|++
expr_stmt|;
name|ids
operator|.
name|addFirst
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|seen
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|delegate
operator|.
name|readSegment
argument_list|(
name|sid
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
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|delegate
operator|.
name|writeSegment
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|delegate
operator|.
name|readBlob
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getBlobStore
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|delegate
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setLoader
parameter_list|(
name|RemoteSegmentLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
block|}
end_class

end_unit

