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
name|standby
operator|.
name|store
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
import|;
end_import

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
name|HashMap
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
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

begin_class
specifier|public
class|class
name|StandbyStore
implements|implements
name|SegmentStore
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
name|StandbyStore
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|StandbyStore
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
name|callId
operator|++
expr_stmt|;
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
name|persisted
init|=
operator|new
name|HashSet
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|SegmentId
argument_list|,
name|Segment
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|cacheOps
init|=
literal|0
decl_stmt|;
name|long
name|cacheWeight
init|=
literal|0
decl_stmt|;
name|long
name|maxWeight
init|=
literal|0
decl_stmt|;
name|long
name|maxKeys
init|=
literal|0
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
name|persisted
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
decl_stmt|;
name|boolean
name|logRefs
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|cache
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|s
operator|=
name|cache
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cacheWeight
operator|-=
name|s
operator|.
name|size
argument_list|()
expr_stmt|;
name|cacheOps
operator|++
expr_stmt|;
name|logRefs
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"transferring segment {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|s
operator|=
name|loader
operator|.
name|readSegment
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"processing segment {} with size {}"
argument_list|,
name|id
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|isDataSegmentId
argument_list|()
condition|)
block|{
name|boolean
name|hasPendingRefs
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
name|s
operator|.
name|getReferencedIds
argument_list|()
decl_stmt|;
if|if
condition|(
name|logRefs
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{} -> {}"
argument_list|,
name|id
argument_list|,
name|refs
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SegmentId
name|nr
range|:
name|refs
control|)
block|{
comment|// skip already persisted or self-ref
if|if
condition|(
name|persisted
operator|.
name|contains
argument_list|(
name|nr
argument_list|)
operator|||
name|id
operator|.
name|equals
argument_list|(
name|nr
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|hasPendingRefs
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|ids
operator|.
name|contains
argument_list|(
name|nr
argument_list|)
condition|)
block|{
if|if
condition|(
name|nr
operator|.
name|isBulkSegmentId
argument_list|()
condition|)
block|{
comment|// binaries first
name|ids
operator|.
name|addFirst
argument_list|(
name|nr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// data segments last
name|ids
operator|.
name|add
argument_list|(
name|nr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|hasPendingRefs
condition|)
block|{
name|persisted
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|id
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// persist it later, after the refs are in place
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// TODO there is a chance this might introduce
comment|// a OOME because of the position of the current
comment|// segment in the processing queue. putting it at
comment|// the end of the current queue means it will stay
comment|// in the cache until the pending queue of the
comment|// segment's references is processed.
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|cacheWeight
operator|+=
name|s
operator|.
name|size
argument_list|()
expr_stmt|;
name|cacheOps
operator|++
expr_stmt|;
name|maxWeight
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxWeight
argument_list|,
name|cacheWeight
argument_list|)
expr_stmt|;
name|maxKeys
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxKeys
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|persisted
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|id
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|removeAll
argument_list|(
name|persisted
argument_list|)
expr_stmt|;
name|err
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"could NOT read segment {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
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
name|persisted
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|cacheStats
operator|.
name|put
argument_list|(
name|callId
argument_list|,
literal|"W: "
operator|+
name|humanReadableByteCount
argument_list|(
name|maxWeight
argument_list|)
operator|+
literal|", Keys: "
operator|+
name|maxKeys
operator|+
literal|", Ops: "
operator|+
name|cacheOps
argument_list|)
expr_stmt|;
return|return
name|delegate
operator|.
name|readSegment
argument_list|(
name|sid
argument_list|)
return|;
block|}
specifier|public
name|void
name|persist
parameter_list|(
name|SegmentId
name|in
parameter_list|,
name|Segment
name|s
parameter_list|)
block|{
name|SegmentId
name|id
init|=
name|delegate
operator|.
name|getTracker
argument_list|()
operator|.
name|getSegmentId
argument_list|(
name|in
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|in
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"persisting segment {} with size {}"
argument_list|,
name|id
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
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
block|}
specifier|private
name|long
name|callId
init|=
literal|0
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|cacheStats
decl_stmt|;
specifier|public
name|void
name|preSync
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
name|this
operator|.
name|cacheStats
operator|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|postSync
parameter_list|()
block|{
name|loader
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
name|cacheStats
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"sync cache stats {}"
argument_list|,
name|cacheStats
argument_list|)
expr_stmt|;
block|}
name|cacheStats
operator|=
literal|null
expr_stmt|;
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
throws|throws
name|IOException
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
name|long
name|size
parameter_list|()
block|{
if|if
condition|(
name|delegate
operator|instanceof
name|FileStore
condition|)
block|{
return|return
operator|(
operator|(
name|FileStore
operator|)
name|delegate
operator|)
operator|.
name|size
argument_list|()
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|delegate
operator|instanceof
name|FileStore
condition|)
block|{
try|try
block|{
name|delegate
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
operator|.
name|dropCache
argument_list|()
expr_stmt|;
operator|(
operator|(
name|FileStore
operator|)
name|delegate
operator|)
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error running cleanup"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Delegate is not a FileStore, ignoring cleanup call"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

