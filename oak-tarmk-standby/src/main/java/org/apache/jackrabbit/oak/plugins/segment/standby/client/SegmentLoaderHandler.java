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
name|plugins
operator|.
name|segment
operator|.
name|standby
operator|.
name|client
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
name|standby
operator|.
name|codec
operator|.
name|Messages
operator|.
name|newGetBlobReq
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
name|standby
operator|.
name|codec
operator|.
name|Messages
operator|.
name|newGetSegmentReq
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
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelInboundHandlerAdapter
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|concurrent
operator|.
name|EventExecutorGroup
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
name|SegmentNodeBuilder
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
name|plugins
operator|.
name|segment
operator|.
name|standby
operator|.
name|codec
operator|.
name|SegmentReply
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
name|standby
operator|.
name|store
operator|.
name|RemoteSegmentLoader
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
name|standby
operator|.
name|store
operator|.
name|StandbyStore
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
name|SegmentLoaderHandler
extends|extends
name|ChannelInboundHandlerAdapter
implements|implements
name|RemoteSegmentLoader
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
name|SegmentLoaderHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|StandbyStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|String
name|clientID
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|head
decl_stmt|;
specifier|private
specifier|final
name|EventExecutorGroup
name|loaderExecutor
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|running
decl_stmt|;
specifier|private
specifier|final
name|int
name|readTimeoutMs
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|autoClean
decl_stmt|;
specifier|private
name|ChannelHandlerContext
name|ctx
decl_stmt|;
specifier|final
name|BlockingQueue
argument_list|<
name|SegmentReply
argument_list|>
name|segment
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|SegmentReply
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SegmentLoaderHandler
parameter_list|(
specifier|final
name|StandbyStore
name|store
parameter_list|,
name|RecordId
name|head
parameter_list|,
name|EventExecutorGroup
name|loaderExecutor
parameter_list|,
name|String
name|clientID
parameter_list|,
name|AtomicBoolean
name|running
parameter_list|,
name|int
name|readTimeoutMs
parameter_list|,
name|boolean
name|autoClean
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
name|this
operator|.
name|loaderExecutor
operator|=
name|loaderExecutor
expr_stmt|;
name|this
operator|.
name|clientID
operator|=
name|clientID
expr_stmt|;
name|this
operator|.
name|running
operator|=
name|running
expr_stmt|;
name|this
operator|.
name|readTimeoutMs
operator|=
name|readTimeoutMs
expr_stmt|;
name|this
operator|.
name|autoClean
operator|=
name|autoClean
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|channelActive
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|initSync
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|userEventTriggered
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|evt
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|evt
operator|instanceof
name|SegmentReply
condition|)
block|{
name|segment
operator|.
name|offer
argument_list|(
operator|(
name|SegmentReply
operator|)
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initSync
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"new head id "
operator|+
name|head
argument_list|)
expr_stmt|;
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|preSyncSize
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|autoClean
condition|)
block|{
name|preSyncSize
operator|=
name|store
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|preSync
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|before
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|current
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|head
argument_list|)
decl_stmt|;
do|do
block|{
try|try
block|{
name|current
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|StandbyApplyDiff
argument_list|(
name|builder
argument_list|,
name|store
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
comment|// the segment is locally damaged or not present anymore
comment|// lets try to read this from the primary again
name|String
name|id
init|=
name|e
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|Segment
name|s
init|=
name|readSegment
argument_list|(
name|e
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"can't read locally corrupt segment "
operator|+
name|id
operator|+
literal|" from primary"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"did reread locally corrupt segment "
operator|+
name|id
operator|+
literal|" with size "
operator|+
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|persist
argument_list|(
name|s
operator|.
name|getSegmentId
argument_list|()
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
name|boolean
name|ok
init|=
name|store
operator|.
name|setHead
argument_list|(
name|before
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"updated head state successfully: {} in {}ms."
argument_list|,
name|ok
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoClean
operator|&&
name|preSyncSize
operator|>
literal|0
condition|)
block|{
name|long
name|postSyncSize
init|=
name|store
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// if size gain is over 25% call cleanup
if|if
condition|(
name|postSyncSize
operator|-
name|preSyncSize
operator|>
literal|0.25
operator|*
name|preSyncSize
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Store size increased from {} to {}, will run cleanup."
argument_list|,
name|humanReadableByteCount
argument_list|(
name|preSyncSize
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|postSyncSize
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|postSync
argument_list|()
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Segment
name|readSegment
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
block|{
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|newGetSegmentReq
argument_list|(
name|this
operator|.
name|clientID
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|getSegment
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|newGetBlobReq
argument_list|(
name|this
operator|.
name|clientID
argument_list|,
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|getBlob
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Throwable
name|cause
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception caught, closing channel."
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Segment
name|getSegment
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
block|{
return|return
name|getReply
argument_list|(
name|id
argument_list|,
name|SegmentReply
operator|.
name|SEGMENT
argument_list|)
operator|.
name|getSegment
argument_list|()
return|;
block|}
specifier|private
name|Blob
name|getBlob
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
block|{
return|return
name|getReply
argument_list|(
name|id
argument_list|,
name|SegmentReply
operator|.
name|BLOB
argument_list|)
operator|.
name|getBlob
argument_list|()
return|;
block|}
specifier|private
name|SegmentReply
name|getReply
parameter_list|(
specifier|final
name|String
name|id
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
name|SegmentReply
name|r
init|=
name|segment
operator|.
name|poll
argument_list|(
name|readTimeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"timeout waiting for {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return
name|SegmentReply
operator|.
name|empty
argument_list|()
return|;
block|}
if|if
condition|(
name|r
operator|.
name|getType
argument_list|()
operator|==
name|type
condition|)
block|{
switch|switch
condition|(
name|r
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|SegmentReply
operator|.
name|SEGMENT
case|:
if|if
condition|(
name|r
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|r
return|;
block|}
break|break;
case|case
name|SegmentReply
operator|.
name|BLOB
case|:
if|if
condition|(
name|r
operator|.
name|getBlob
argument_list|()
operator|.
name|getBlobId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|r
return|;
block|}
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|interrupted
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
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
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
operator|(
name|loaderExecutor
operator|!=
literal|null
operator|&&
operator|(
name|loaderExecutor
operator|.
name|isShuttingDown
argument_list|()
operator|||
name|loaderExecutor
operator|.
name|isShutdown
argument_list|()
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

