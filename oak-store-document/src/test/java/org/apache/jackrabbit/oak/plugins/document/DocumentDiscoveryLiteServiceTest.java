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
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|junitx
operator|.
name|util
operator|.
name|PrivateAccessor
import|;
end_import

begin_comment
comment|/**  * Tests for the DocumentDiscoveryLiteService  */
end_comment

begin_class
specifier|public
class|class
name|DocumentDiscoveryLiteServiceTest
extends|extends
name|BaseDocumentDiscoveryLiteServiceTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testActivateDeactivate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// then test normal start with a DocumentNodeStore
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentDiscoveryLiteService
name|discoveryLite
init|=
operator|new
name|DocumentDiscoveryLiteService
argument_list|()
decl_stmt|;
name|PrivateAccessor
operator|.
name|setField
argument_list|(
name|discoveryLite
argument_list|,
literal|"nodeStore"
argument_list|,
name|mk1
operator|.
name|nodeStore
argument_list|)
expr_stmt|;
name|BundleContext
name|bc
init|=
name|mock
argument_list|(
name|BundleContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ComponentContext
name|c
init|=
name|mock
argument_list|(
name|ComponentContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|c
operator|.
name|getBundleContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|bc
argument_list|)
expr_stmt|;
name|discoveryLite
operator|.
name|activate
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|c
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|disableComponent
argument_list|(
name|DocumentDiscoveryLiteService
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|discoveryLite
operator|.
name|deactivate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneNode
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation
argument_list|,
literal|2000
argument_list|,
literal|"see myself as active"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCleanShutdown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s2
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation1
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation2
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|expectation1
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation2
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1
argument_list|,
literal|2000
argument_list|,
literal|"first should see both as active"
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation2
argument_list|,
literal|2000
argument_list|,
literal|"second should see both as active"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterShutdown
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterShutdown
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation1AfterShutdown
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterShutdown
argument_list|,
literal|2000
argument_list|,
literal|"first should only see itself after shutdown"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCrash
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s2
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation1
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation2
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|expectation1
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation2
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1
argument_list|,
literal|2000
argument_list|,
literal|"first should see both as active"
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation2
argument_list|,
literal|2000
argument_list|,
literal|"second should see both as active"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|crash
argument_list|()
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterShutdown
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterShutdown
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation1AfterShutdown
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterShutdown
argument_list|,
literal|4000
argument_list|,
literal|"first should only see itself after shutdown"
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test creates a large number of documentnodestores which it starts,      * runs, stops in a random fashion, always testing to make sure the      * clusterView is correct      */
annotation|@
name|Test
specifier|public
name|void
name|testSmallStartStopFiesta
parameter_list|()
throws|throws
name|Throwable
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"testSmallStartStopFiesta: start, seed="
operator|+
name|SEED
argument_list|)
expr_stmt|;
specifier|final
name|int
name|LOOP_CNT
init|=
literal|5
decl_stmt|;
comment|// with too many loops have also seen mongo
comment|// connections becoming starved thus test
comment|// failed
name|doStartStopFiesta
argument_list|(
name|LOOP_CNT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

