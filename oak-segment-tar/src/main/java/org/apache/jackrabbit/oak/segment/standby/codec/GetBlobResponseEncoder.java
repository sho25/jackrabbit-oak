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
name|InputStream
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
name|ChannelOutboundHandlerAdapter
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
name|ChannelPromise
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
name|GetBlobResponseEncoder
extends|extends
name|ChannelOutboundHandlerAdapter
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
name|GetBlobResponseEncoder
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|blobChunkSize
decl_stmt|;
specifier|public
name|GetBlobResponseEncoder
parameter_list|(
specifier|final
name|int
name|blobChunkSize
parameter_list|)
block|{
name|this
operator|.
name|blobChunkSize
operator|=
name|blobChunkSize
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|msg
parameter_list|,
name|ChannelPromise
name|promise
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|msg
operator|instanceof
name|GetBlobResponse
condition|)
block|{
name|GetBlobResponse
name|response
init|=
operator|(
name|GetBlobResponse
operator|)
name|msg
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Sending blob {} to client {}"
argument_list|,
name|response
operator|.
name|getBlobId
argument_list|()
argument_list|,
name|response
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|clientId
init|=
name|response
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|blobId
init|=
name|response
operator|.
name|getBlobId
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|response
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|response
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|ChunkedBlobStream
argument_list|(
name|clientId
argument_list|,
name|blobId
argument_list|,
name|length
argument_list|,
name|in
argument_list|,
name|blobChunkSize
argument_list|)
argument_list|,
name|promise
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|write
argument_list|(
name|msg
argument_list|,
name|promise
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

