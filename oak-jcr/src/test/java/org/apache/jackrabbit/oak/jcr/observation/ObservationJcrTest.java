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
name|jcr
operator|.
name|observation
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
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
name|core
operator|.
name|observation
operator|.
name|MixinTest
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
name|core
operator|.
name|observation
operator|.
name|MoveInPlaceTest
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
name|core
operator|.
name|observation
operator|.
name|ReorderTest
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
name|core
operator|.
name|observation
operator|.
name|ShareableNodesTest
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
name|jcr
operator|.
name|tck
operator|.
name|TCKBase
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
name|test
operator|.
name|ConcurrentTestSuite
import|;
end_import

begin_class
specifier|public
class|class
name|ObservationJcrTest
extends|extends
name|TCKBase
block|{
specifier|public
name|ObservationJcrTest
parameter_list|()
block|{
name|super
argument_list|(
literal|"Jackrabbit Observation tests"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|ObservationJcrTest
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addTests
parameter_list|()
block|{
name|TestSuite
name|tests
init|=
operator|new
name|ConcurrentTestSuite
argument_list|(
literal|"Jackrabbit Observation tests"
argument_list|)
decl_stmt|;
name|tests
operator|.
name|addTestSuite
argument_list|(
name|ReorderTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|tests
operator|.
name|addTestSuite
argument_list|(
name|MoveInPlaceTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|tests
operator|.
name|addTestSuite
argument_list|(
name|MixinTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|tests
operator|.
name|addTestSuite
argument_list|(
name|ShareableNodesTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|addTest
argument_list|(
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

