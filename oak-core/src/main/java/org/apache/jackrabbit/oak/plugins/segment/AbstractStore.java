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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|segment
operator|.
name|SegmentIdFactory
operator|.
name|isBulkSegmentId
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|cache
operator|.
name|CacheLIRS
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
name|cache
operator|.
name|Cache
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractStore
implements|implements
name|SegmentStore
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|protected
specifier|final
name|Cache
argument_list|<
name|UUID
argument_list|,
name|Segment
argument_list|>
name|segments
decl_stmt|;
comment|/**      * Identifiers of the segments that are currently being loaded.      */
specifier|private
specifier|final
name|Set
argument_list|<
name|UUID
argument_list|>
name|currentlyLoading
init|=
name|newHashSet
argument_list|()
decl_stmt|;
comment|/**      * Number of threads that are currently waiting for segments to be loaded.      * Used to avoid extra {@link #notifyAll()} calls when nobody is waiting.      */
specifier|private
name|int
name|currentlyWaiting
init|=
literal|0
decl_stmt|;
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
decl_stmt|;
specifier|protected
name|AbstractStore
parameter_list|(
name|int
name|cacheSizeMB
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|SegmentWriter
argument_list|(
name|this
argument_list|,
name|factory
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheSizeMB
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|segments
operator|=
name|CacheLIRS
operator|.
name|newBuilder
argument_list|()
operator|.
name|weigher
argument_list|(
name|Segment
operator|.
name|WEIGHER
argument_list|)
operator|.
name|maximumWeight
argument_list|(
name|cacheSizeMB
operator|*
name|MB
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|segments
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|Set
argument_list|<
name|UUID
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
block|{
return|return
name|factory
operator|.
name|getReferencedSegmentIds
argument_list|()
return|;
block|}
specifier|protected
name|Segment
name|createSegment
parameter_list|(
name|UUID
name|segmentId
parameter_list|,
name|ByteBuffer
name|data
parameter_list|)
block|{
return|return
operator|new
name|Segment
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|segmentId
argument_list|,
name|data
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|Segment
name|loadSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
function_decl|;
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
name|Segment
name|readSegment
parameter_list|(
name|UUID
name|id
parameter_list|)
block|{
if|if
condition|(
name|isBulkSegmentId
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|loadSegment
argument_list|(
name|id
argument_list|)
return|;
block|}
name|Segment
name|segment
init|=
name|getWriter
argument_list|()
operator|.
name|getCurrentSegment
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
if|if
condition|(
name|segments
operator|==
literal|null
condition|)
block|{
comment|// no in-memory cache, load the segment directly
return|return
name|loadSegment
argument_list|(
name|id
argument_list|)
return|;
block|}
synchronized|synchronized
init|(
name|segments
init|)
block|{
comment|// check if the segment is already cached
name|segment
operator|=
name|segments
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// ... or currently being loaded
while|while
condition|(
name|segment
operator|==
literal|null
operator|&&
name|currentlyLoading
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|currentlyWaiting
operator|++
expr_stmt|;
try|try
block|{
name|segments
operator|.
name|wait
argument_list|()
expr_stmt|;
comment|// for another thread to load the segment
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|currentlyWaiting
operator|--
expr_stmt|;
block|}
name|segment
operator|=
name|segments
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
comment|// found the segment in the cache
return|return
name|segment
return|;
block|}
comment|// not yet cached, so start let others know that we're loading it
name|currentlyLoading
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|segment
operator|=
name|loadSegment
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|segments
init|)
block|{
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
name|segments
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|segment
argument_list|)
expr_stmt|;
block|}
name|currentlyLoading
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentlyWaiting
operator|>
literal|0
condition|)
block|{
name|segments
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|segment
return|;
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
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|segments
init|)
block|{
while|while
condition|(
name|currentlyLoading
operator|.
name|contains
argument_list|(
name|segmentId
argument_list|)
condition|)
block|{
try|try
block|{
name|segments
operator|.
name|wait
argument_list|()
expr_stmt|;
comment|// for concurrent load to finish
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|segments
operator|.
name|invalidate
argument_list|(
name|segmentId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|segments
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|segments
init|)
block|{
while|while
condition|(
operator|!
name|currentlyLoading
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|segments
operator|.
name|wait
argument_list|()
expr_stmt|;
comment|// for concurrent loads to finish
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|segments
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInstance
parameter_list|(
name|Object
name|object
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Record
argument_list|>
name|type
parameter_list|)
block|{
assert|assert
name|Record
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
assert|;
return|return
name|type
operator|.
name|isInstance
argument_list|(
name|object
argument_list|)
operator|&&
operator|(
operator|(
name|Record
operator|)
name|object
operator|)
operator|.
name|getStore
argument_list|()
operator|==
name|this
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|ExternalBlob
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

