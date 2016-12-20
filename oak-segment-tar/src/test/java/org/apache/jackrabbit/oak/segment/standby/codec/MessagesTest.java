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
name|codec
operator|.
name|Messages
operator|.
name|extractClientFrom
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
name|segment
operator|.
name|standby
operator|.
name|codec
operator|.
name|Messages
operator|.
name|extractMessageFrom
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|MessagesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|extractMessageFromPayloadWithoutMagic
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|extractMessageFrom
argument_list|(
literal|"wrong"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractMessageFromEmptyPayload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|extractMessageFrom
argument_list|(
literal|"Standby-CMD@"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractMessageFromPayloadWithClient
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"message"
argument_list|,
name|extractMessageFrom
argument_list|(
literal|"Standby-CMD@client:message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractMessageFromPayloadWithoutClient
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"message"
argument_list|,
name|extractMessageFrom
argument_list|(
literal|"Standby-CMD@:message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractClientFromPayloadWithoutMagic
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|extractClientFrom
argument_list|(
literal|"wrong"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractClientFromEmptyPayload
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|extractClientFrom
argument_list|(
literal|"Standby-CMD@"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractClientFromPayloadWithClient
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"client"
argument_list|,
name|extractClientFrom
argument_list|(
literal|"Standby-CMD@client:message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|extractClientFromPayloadWithoutClient
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|extractClientFrom
argument_list|(
literal|"Standby-CMD@:message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

