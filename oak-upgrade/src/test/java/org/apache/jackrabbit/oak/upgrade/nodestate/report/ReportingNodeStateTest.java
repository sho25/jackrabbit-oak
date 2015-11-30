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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
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
name|api
operator|.
name|PropertyState
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
name|memory
operator|.
name|EmptyNodeState
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
name|ChildNodeEntry
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
name|NodeState
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
name|upgrade
operator|.
name|util
operator|.
name|NodeStateTestUtils
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|nodestate
operator|.
name|report
operator|.
name|AssertingPeriodicReporter
operator|.
name|hasReportedNode
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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
operator|.
name|AssertingPeriodicReporter
operator|.
name|hasReportedNodes
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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
operator|.
name|AssertingPeriodicReporter
operator|.
name|hasReportedProperty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
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
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|ReportingNodeStateTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|getChildNodeReportsNode
parameter_list|()
block|{
specifier|final
name|AssertingPeriodicReporter
name|reporter
init|=
operator|new
name|AssertingPeriodicReporter
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|nodeState
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|nodeState
operator|.
name|getChildNode
argument_list|(
literal|"a"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedNode
argument_list|(
literal|10
argument_list|,
literal|"/a10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedNode
argument_list|(
literal|20
argument_list|,
literal|"/a20"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChildNodeEntriesReportsNode
parameter_list|()
block|{
specifier|final
name|NodeBuilder
name|builder
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AssertingPeriodicReporter
name|reporter
init|=
operator|new
name|AssertingPeriodicReporter
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|nodeState
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|String
name|name
init|=
literal|"<none>"
decl_stmt|;
for|for
control|(
specifier|final
name|ChildNodeEntry
name|child
range|:
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
if|if
condition|(
operator|++
name|counter
operator|==
literal|10
condition|)
block|{
name|name
operator|=
name|child
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedNode
argument_list|(
literal|10
argument_list|,
literal|"/"
operator|+
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPropertyReportsProperty
parameter_list|()
block|{
specifier|final
name|NodeBuilder
name|builder
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
literal|"meaningOfLife"
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
literal|"42"
argument_list|)
expr_stmt|;
specifier|final
name|AssertingPeriodicReporter
name|reporter
init|=
operator|new
name|AssertingPeriodicReporter
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|nodeState
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// 7 accesses via 7 methods
name|nodeState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getBoolean
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getStrings
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeState
operator|.
name|getNames
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|not
argument_list|(
name|hasReportedProperty
argument_list|(
literal|0
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|1
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|2
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|3
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|4
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|5
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|6
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|7
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|not
argument_list|(
name|hasReportedProperty
argument_list|(
literal|8
argument_list|,
literal|"/meaningOfLife"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPropertiesReportsProperty
parameter_list|()
block|{
specifier|final
name|NodeBuilder
name|builder
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
literal|"a"
operator|+
name|i
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AssertingPeriodicReporter
name|reporter
init|=
operator|new
name|AssertingPeriodicReporter
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|nodeState
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|PropertyState
name|property
range|:
name|nodeState
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|++
name|counter
operator|==
literal|10
condition|)
block|{
break|break;
block|}
block|}
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedProperty
argument_list|(
literal|10
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compareAgainstBaseState
parameter_list|()
block|{
specifier|final
name|NodeBuilder
name|root
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"aa"
argument_list|)
expr_stmt|;
specifier|final
name|NodeState
name|before
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"ab"
argument_list|)
expr_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
specifier|final
name|AssertingPeriodicReporter
name|reporter
init|=
operator|new
name|AssertingPeriodicReporter
argument_list|(
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|after
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|root
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|NodeStateTestUtils
operator|.
name|expectDifference
argument_list|()
operator|.
name|childNodeAdded
argument_list|(
literal|"/a/ab"
argument_list|,
literal|"/b"
argument_list|)
operator|.
name|childNodeChanged
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|strict
argument_list|()
operator|.
name|verify
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
argument_list|,
name|hasReportedNodes
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/ab"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

