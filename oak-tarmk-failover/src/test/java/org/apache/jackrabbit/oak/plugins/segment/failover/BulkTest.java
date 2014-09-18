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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|jmx
operator|.
name|FailoverStatusMBean
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
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
name|assertNotNull
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
name|assertTrue
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
name|BulkTest
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
name|test100Nodes
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|3000
argument_list|,
literal|3100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test1000Nodes
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|1000
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|53000
argument_list|,
literal|55000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test10000Nodes
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|10000
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|245000
argument_list|,
literal|246000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test100000Nodes
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|100000
argument_list|,
literal|9
argument_list|,
literal|9
argument_list|,
literal|2210000
argument_list|,
literal|2220000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test1MillionNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|1000000
argument_list|,
literal|87
argument_list|,
literal|87
argument_list|,
literal|22700000
argument_list|,
literal|22800000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test1MillionNodesUsingSSL
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|1000000
argument_list|,
literal|87
argument_list|,
literal|87
argument_list|,
literal|22700000
argument_list|,
literal|22800000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/*     @Test     public void test10MillionNodes() throws Exception {         test(10000000, 856, 856, 223000000, 224000000);     } */
comment|// private helper
specifier|private
name|void
name|test
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|minExpectedSegments
parameter_list|,
name|int
name|maxExpectedSegments
parameter_list|,
name|long
name|minExpectedBytes
parameter_list|,
name|long
name|maxExpectedBytes
parameter_list|)
throws|throws
name|Exception
block|{
name|test
argument_list|(
name|number
argument_list|,
name|minExpectedSegments
argument_list|,
name|maxExpectedSegments
argument_list|,
name|minExpectedBytes
argument_list|,
name|maxExpectedBytes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|test
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|minExpectedSegments
parameter_list|,
name|int
name|maxExpectedSegments
parameter_list|,
name|long
name|minExpectedBytes
parameter_list|,
name|long
name|maxExpectedBytes
parameter_list|,
name|boolean
name|useSSL
parameter_list|)
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
name|NodeBuilder
name|rootbuilder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|rootbuilder
operator|.
name|child
argument_list|(
literal|"store"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|number
operator|/
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|NodeBuilder
name|builder
init|=
name|b
operator|.
name|child
argument_list|(
literal|"Folder#"
operator|+
name|j
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|number
operator|<
literal|1000
condition|?
name|number
else|:
literal|1000
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"Test#"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"ts"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|store
operator|.
name|merge
argument_list|(
name|rootbuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|storeS
operator|.
name|flush
argument_list|()
expr_stmt|;
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
argument_list|,
name|useSSL
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
name|FailoverClient
operator|.
name|CLIENT_ID_PROPERTY_NAME
argument_list|,
literal|"Bar"
argument_list|)
expr_stmt|;
name|FailoverClient
name|cl
init|=
operator|new
name|FailoverClient
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|,
name|storeC
argument_list|,
name|useSSL
argument_list|)
decl_stmt|;
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
name|FailoverStatusMBean
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
name|cl
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
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|cl
operator|.
name|run
argument_list|()
expr_stmt|;
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
name|long
name|segments
init|=
operator|(
operator|(
name|Long
operator|)
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|connectionStatus
argument_list|,
literal|"TransferredSegments"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|bytes
init|=
operator|(
operator|(
name|Long
operator|)
name|jmxServer
operator|.
name|getAttribute
argument_list|(
name|connectionStatus
argument_list|,
literal|"TransferredSegmentBytes"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"did transfer "
operator|+
name|segments
operator|+
literal|" segments with "
operator|+
name|bytes
operator|+
literal|" bytes in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|/
literal|1000
operator|+
literal|" seconds."
argument_list|)
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
comment|//compare(segments, "segment", minExpectedSegments, maxExpectedSegments);
comment|//compare(bytes, "byte", minExpectedBytes, maxExpectedBytes);
block|}
finally|finally
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
name|cl
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|compare
parameter_list|(
name|long
name|current
parameter_list|,
name|String
name|unit
parameter_list|,
name|long
name|expectedMin
parameter_list|,
name|long
name|expectedMax
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"current number of "
operator|+
name|unit
operator|+
literal|"s ("
operator|+
name|current
operator|+
literal|") is less than minimum expected: "
operator|+
name|expectedMin
argument_list|,
name|current
operator|>=
name|expectedMin
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"current number of "
operator|+
name|unit
operator|+
literal|"s ("
operator|+
name|current
operator|+
literal|") is bigger than maximum expected: "
operator|+
name|expectedMax
argument_list|,
name|current
operator|<=
name|expectedMax
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

