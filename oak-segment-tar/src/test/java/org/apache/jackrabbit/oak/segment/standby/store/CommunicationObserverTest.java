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
name|store
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
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
name|assertNotNull
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
name|assertNotSame
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|CommunicationObserverTest
block|{
specifier|private
specifier|static
class|class
name|TestCommunicationObserver
extends|extends
name|CommunicationObserver
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|CommunicationPartnerMBean
argument_list|>
name|communicationPartners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|TestCommunicationObserver
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|maxClientMBeans
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|maxClientMBeans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|registerCommunicationPartner
parameter_list|(
name|CommunicationPartnerMBean
name|m
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|registerCommunicationPartner
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|communicationPartners
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|unregisterCommunicationPartner
parameter_list|(
name|CommunicationPartnerMBean
name|m
parameter_list|)
throws|throws
name|Exception
block|{
name|communicationPartners
operator|.
name|remove
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|super
operator|.
name|unregisterCommunicationPartner
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TestCommunicationObserver
name|observer
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|observer
operator|=
operator|new
name|TestCommunicationObserver
argument_list|(
literal|"test"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|observer
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|observer
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldExposeTheProvidedID
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|observer
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldRegisterObservablePartnerMBean
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldNotKeepManyObservablePartnerMBeans
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeClientName
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"client"
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeAddress
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposePort
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8080
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRemotePort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeLastRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"request"
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldUpdateLastRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"before"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"before"
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastRequest
argument_list|()
argument_list|)
expr_stmt|;
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"after"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"after"
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeLastSeen
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldUpdateLastSeen
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|Date
name|before
init|=
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeen
argument_list|()
decl_stmt|;
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|Date
name|after
init|=
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeen
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeLastSeenTimestamp
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeenTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeSentSegments
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendSegmentBytes
argument_list|(
literal|"client"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTransferredSegments
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeSentSegmentsSize
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendSegmentBytes
argument_list|(
literal|"client"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTransferredSegmentBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeSentBinaries
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendBinariesBytes
argument_list|(
literal|"client"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTransferredBinaries
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|observablePartnerMBeanShouldExposeSentBinariesSize
parameter_list|()
throws|throws
name|Exception
block|{
name|observer
operator|.
name|gotMessageFrom
argument_list|(
literal|"client"
argument_list|,
literal|"request"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|8080
argument_list|)
expr_stmt|;
name|observer
operator|.
name|didSendBinariesBytes
argument_list|(
literal|"client"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|observer
operator|.
name|communicationPartners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTransferredBinariesBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

