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
name|codec
package|;
end_package

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
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
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
name|handler
operator|.
name|codec
operator|.
name|LengthFieldBasedFrameDecoder
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
name|CharsetUtil
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
name|SegmentStore
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
annotation|@
name|Deprecated
specifier|public
class|class
name|RecordIdDecoder
extends|extends
name|LengthFieldBasedFrameDecoder
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
name|RecordIdDecoder
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
annotation|@
name|Deprecated
specifier|public
name|RecordIdDecoder
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
literal|64
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|protected
name|Object
name|decode
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ByteBuf
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteBuf
name|frame
init|=
operator|(
name|ByteBuf
operator|)
name|super
operator|.
name|decode
argument_list|(
name|ctx
argument_list|,
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|frame
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Received unexpected empty frame. Maybe you have enabled secure transmission on only one endpoint of the connection."
argument_list|)
throw|;
block|}
name|byte
name|type
init|=
name|frame
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|frame
operator|.
name|discardReadBytes
argument_list|()
expr_stmt|;
name|String
name|id
init|=
name|frame
operator|.
name|toString
argument_list|(
name|CharsetUtil
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"received type {} with id {}"
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return
name|RecordId
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|protected
name|ByteBuf
name|extractFrame
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ByteBuf
name|buffer
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|slice
argument_list|(
name|index
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
end_class

end_unit

