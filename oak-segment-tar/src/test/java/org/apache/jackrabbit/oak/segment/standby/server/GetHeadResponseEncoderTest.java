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
name|server
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
name|segment
operator|.
name|standby
operator|.
name|server
operator|.
name|ServerTestUtils
operator|.
name|mockRecordId
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|base
operator|.
name|Charsets
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
name|buffer
operator|.
name|Unpooled
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
name|embedded
operator|.
name|EmbeddedChannel
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
name|segment
operator|.
name|standby
operator|.
name|codec
operator|.
name|Messages
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|GetHeadResponseEncoderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|encodeResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|RecordId
name|recordId
init|=
name|mockRecordId
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|EmbeddedChannel
name|channel
init|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|GetHeadResponseEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeOutbound
argument_list|(
operator|new
name|GetHeadResponse
argument_list|(
literal|"clientId"
argument_list|,
name|recordId
argument_list|)
argument_list|)
expr_stmt|;
name|ByteBuf
name|buffer
init|=
operator|(
name|ByteBuf
operator|)
name|channel
operator|.
name|readOutbound
argument_list|()
decl_stmt|;
name|ByteBuf
name|expected
init|=
name|Unpooled
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|expected
operator|.
name|writeInt
argument_list|(
name|recordId
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|expected
operator|.
name|writeByte
argument_list|(
name|Messages
operator|.
name|HEADER_RECORD
argument_list|)
expr_stmt|;
name|expected
operator|.
name|writeBytes
argument_list|(
name|recordId
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

