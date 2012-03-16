begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|large
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
name|mk
operator|.
name|MultiMkTestBase
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Test creating nodes and traversing.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|CreateNodesTraverseTest
extends|extends
name|MultiMkTestBase
block|{
comment|// -Xmx512m -Dmk.fastDb=true
specifier|public
name|CreateNodesTraverseTest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeCreator
name|c
init|=
operator|new
name|NodeCreator
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|c
operator|.
name|setTotalCount
argument_list|(
literal|200
argument_list|)
expr_stmt|;
comment|// 1 million node test
comment|// c.setLogToSystemOut(true);
comment|// c.setTotalCount(1000000);
comment|// 20 million node test
comment|// c.setTotalCount(20000000);
name|c
operator|.
name|create
argument_list|()
expr_stmt|;
name|c
operator|.
name|traverse
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

