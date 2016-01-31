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
name|standby
operator|.
name|client
operator|.
name|StandbyClient
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
name|jmx
operator|.
name|StandbyStatusMBean
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
name|server
operator|.
name|StandbyServer
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
name|Ignore
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
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|MBeanTest
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
name|setUpServerAndClient
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
name|closeServerAndClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testServerEmptyConfig
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StandbyServer
name|server
init|=
operator|new
name|StandbyServer
argument_list|(
name|this
operator|.
name|port
argument_list|,
name|this
operator|.
name|storeS
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|status
init|=
operator|new
name|ObjectName
argument_list|(
name|StandbyStatusMBean
operator|.
name|JMX_NAME
operator|+
literal|",id=*"
argument_list|)
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|instances
init|=
name|jmxServer
operator|.
name|queryNames
argument_list|(
name|status
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|instances
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ObjectName
argument_list|(
name|server
operator|.
name|getMBeanName
argument_list|()
argument_list|)
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"primary"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Mode"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|m
init|=
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|equals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STARTING
argument_list|)
operator|&&
operator|!
name|m
operator|.
name|equals
argument_list|(
literal|"channel unregistered"
argument_list|)
condition|)
name|fail
argument_list|(
literal|"unexpected Status "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STARTING
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"stop"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STOPPED
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"start"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_RUNNING
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
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
block|}
name|assertTrue
argument_list|(
operator|!
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClientEmptyConfigNoServer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StandbyClient
name|client
init|=
name|newStandbyClient
argument_list|(
name|storeC
argument_list|)
decl_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|status
init|=
operator|new
name|ObjectName
argument_list|(
name|StandbyStatusMBean
operator|.
name|JMX_NAME
operator|+
literal|",id=*"
argument_list|)
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|instances
init|=
name|jmxServer
operator|.
name|queryNames
argument_list|(
name|status
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|instances
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
literal|0
index|]
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ObjectName
argument_list|(
name|client
operator|.
name|getMBeanName
argument_list|()
argument_list|)
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|m
init|=
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Mode"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|startsWith
argument_list|(
literal|"client: "
argument_list|)
condition|)
name|fail
argument_list|(
literal|"unexpected mode "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"FailedRequests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-1"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"SecondsSinceLastSuccess"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_INITIALIZING
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"stop"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STOPPED
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"start"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STOPPED
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|!
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClientNoServer
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|StandbyClient
operator|.
name|CLIENT_ID_PROPERTY_NAME
argument_list|,
literal|"Foo"
argument_list|)
expr_stmt|;
specifier|final
name|StandbyClient
name|client
init|=
name|newStandbyClient
argument_list|(
name|storeC
argument_list|)
decl_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|status
init|=
operator|new
name|ObjectName
argument_list|(
name|client
operator|.
name|getMBeanName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"client: Foo"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"Mode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"FailedRequests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-1"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|status
argument_list|,
literal|"SecondsSinceLastSuccess"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"calcFailedRequests"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-1"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|status
argument_list|,
literal|"calcSecondsSinceLastSuccess"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|!
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2086"
argument_list|)
specifier|public
name|void
name|testClientAndServerEmptyConfig
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|StandbyServer
name|server
init|=
operator|new
name|StandbyServer
argument_list|(
name|port
argument_list|,
name|this
operator|.
name|storeS
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|StandbyClient
operator|.
name|CLIENT_ID_PROPERTY_NAME
argument_list|,
literal|"Bar"
argument_list|)
expr_stmt|;
specifier|final
name|StandbyClient
name|client
init|=
name|newStandbyClient
argument_list|(
name|storeC
argument_list|)
decl_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|MBeanServer
name|jmxServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|status
init|=
operator|new
name|ObjectName
argument_list|(
name|StandbyStatusMBean
operator|.
name|JMX_NAME
operator|+
literal|",id=*"
argument_list|)
decl_stmt|;
name|ObjectName
name|clientStatus
init|=
operator|new
name|ObjectName
argument_list|(
name|client
operator|.
name|getMBeanName
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectName
name|serverStatus
init|=
operator|new
name|ObjectName
argument_list|(
name|server
operator|.
name|getMBeanName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|instances
init|=
name|jmxServer
operator|.
name|queryNames
argument_list|(
name|status
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectName
name|connectionStatus
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectName
name|s
range|:
name|instances
control|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|clientStatus
argument_list|)
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|serverStatus
argument_list|)
condition|)
name|connectionStatus
operator|=
name|s
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|connectionStatus
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|clientStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|serverStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|connectionStatus
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|m
init|=
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Mode"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|startsWith
argument_list|(
literal|"client: "
argument_list|)
condition|)
name|fail
argument_list|(
literal|"unexpected mode "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"master"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Mode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"FailedRequests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"SecondsSinceLastSuccess"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"calcFailedRequests"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"calcSecondsSinceLastSuccess"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"FailedRequests"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"SecondsSinceLastSuccess"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"calcFailedRequests"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"calcSecondsSinceLastSuccess"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|2
argument_list|)
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|connectionStatus
argument_list|,
literal|"TransferredSegments"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|128
argument_list|)
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|connectionStatus
argument_list|,
literal|"TransferredSegmentBytes"
argument_list|)
argument_list|)
expr_stmt|;
comment|// stop the master
name|jmxServer
operator|.
name|invoke
argument_list|(
name|serverStatus
argument_list|,
literal|"stop"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Status"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|equals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STOPPED
argument_list|)
operator|&&
operator|!
name|m
operator|.
name|equals
argument_list|(
literal|"channel unregistered"
argument_list|)
condition|)
name|fail
argument_list|(
literal|"unexpected Status"
operator|+
name|m
argument_list|)
expr_stmt|;
comment|// restart the master
name|jmxServer
operator|.
name|invoke
argument_list|(
name|serverStatus
argument_list|,
literal|"start"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Status"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|equals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STARTING
argument_list|)
operator|&&
operator|!
name|m
operator|.
name|equals
argument_list|(
literal|"channel unregistered"
argument_list|)
condition|)
name|fail
argument_list|(
literal|"unexpected Status"
operator|+
name|m
argument_list|)
expr_stmt|;
comment|// stop the slave
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"stop"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|serverStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_STOPPED
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart the slave
name|jmxServer
operator|.
name|invoke
argument_list|(
name|clientStatus
argument_list|,
literal|"start"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandbyStatusMBean
operator|.
name|STATUS_RUNNING
argument_list|,
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|clientStatus
argument_list|,
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|!
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|clientStatus
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|jmxServer
operator|.
name|isRegistered
argument_list|(
name|serverStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

