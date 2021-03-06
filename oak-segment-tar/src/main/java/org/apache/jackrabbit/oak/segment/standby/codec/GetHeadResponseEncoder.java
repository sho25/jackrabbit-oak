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
name|MessageToByteEncoder
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

begin_comment
comment|/**  * Encodes a 'get head' response.  */
end_comment

begin_class
specifier|public
class|class
name|GetHeadResponseEncoder
extends|extends
name|MessageToByteEncoder
argument_list|<
name|GetHeadResponse
argument_list|>
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
name|GetHeadResponseEncoder
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|encode
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|GetHeadResponse
name|msg
parameter_list|,
name|ByteBuf
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sending head {} to client {}"
argument_list|,
name|msg
operator|.
name|getHeadRecordId
argument_list|()
argument_list|,
name|msg
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|body
init|=
name|msg
operator|.
name|getHeadRecordId
argument_list|()
operator|.
name|getBytes
argument_list|(
name|CharsetUtil
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|body
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_RECORD
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

