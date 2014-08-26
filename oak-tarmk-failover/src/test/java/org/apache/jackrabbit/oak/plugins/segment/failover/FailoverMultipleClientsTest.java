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
package|;
end_package

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
name|SegmentNodeStore
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
name|SegmentTestUtils
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
name|failover
operator|.
name|client
operator|.
name|FailoverClient
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
name|failover
operator|.
name|server
operator|.
name|FailoverServer
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
name|state
operator|.
name|NodeStore
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
name|Assert
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
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

begin_class
specifier|public
class|class
name|FailoverMultipleClientsTest
extends|extends
name|TestBase
block|{
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpServerAndTwoClients
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|closeServerAndTwoClients
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleClients
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|storeS
argument_list|)
decl_stmt|;
specifier|final
name|FailoverServer
name|server
init|=
operator|new
name|FailoverServer
argument_list|(
name|port
argument_list|,
name|storeS
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|SegmentTestUtils
operator|.
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|)
expr_stmt|;
name|FailoverClient
name|cl1
init|=
operator|new
name|FailoverClient
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|,
name|storeC
argument_list|)
decl_stmt|;
name|FailoverClient
name|cl2
init|=
operator|new
name|FailoverClient
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|,
name|storeC2
argument_list|)
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
literal|"first client has invalid initial store!"
argument_list|,
name|storeS
operator|.
name|getHead
argument_list|()
operator|.
name|equals
argument_list|(
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"second client has invalid initial store!"
argument_list|,
name|storeS
operator|.
name|getHead
argument_list|()
operator|.
name|equals
argument_list|(
name|storeC2
operator|.
name|getHead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC2
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|cl1
operator|.
name|run
argument_list|()
expr_stmt|;
name|cl2
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|storeS
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|storeS
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC2
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|cl1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|SegmentTestUtils
operator|.
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|cl2
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|storeS
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC2
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"first client updated in stopped state!"
argument_list|,
name|storeS
operator|.
name|getHead
argument_list|()
operator|.
name|equals
argument_list|(
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cl1
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|storeS
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
name|cl1
operator|.
name|close
argument_list|()
expr_stmt|;
name|cl2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

