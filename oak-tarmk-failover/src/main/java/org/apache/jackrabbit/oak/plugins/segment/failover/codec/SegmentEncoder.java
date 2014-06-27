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
name|failover
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
name|buffer
operator|.
name|ByteBufOutputStream
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

begin_class
specifier|public
class|class
name|SegmentEncoder
extends|extends
name|MessageToByteEncoder
argument_list|<
name|Segment
argument_list|>
block|{
annotation|@
name|Override
specifier|protected
name|void
name|encode
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Segment
name|s
parameter_list|,
name|ByteBuf
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|SegmentId
name|id
init|=
name|s
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|size
argument_list|()
operator|+
literal|17
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_SEGMENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|ByteBufOutputStream
name|bout
init|=
operator|new
name|ByteBufOutputStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|s
operator|.
name|writeTo
argument_list|(
name|bout
argument_list|)
expr_stmt|;
name|bout
operator|.
name|flush
argument_list|()
expr_stmt|;
name|bout
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

