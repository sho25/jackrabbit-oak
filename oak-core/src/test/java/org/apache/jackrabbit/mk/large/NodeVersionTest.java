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
name|mk
operator|.
name|large
package|;
end_package

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
name|assertFalse
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
name|mk
operator|.
name|MultiMkTestBase
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
name|mk
operator|.
name|simple
operator|.
name|NodeImpl
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
comment|/**  * Test moving nodes.  */
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
name|NodeVersionTest
extends|extends
name|MultiMkTestBase
block|{
specifier|private
name|String
name|head
decl_stmt|;
specifier|public
name|NodeVersionTest
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
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"nodeVersion\": false"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"nodeVersion\": null"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeVersion
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"nodeVersion\": true"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test1\": { \"id\": 1 }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test2\": { \"id\": 1 }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|NodeImpl
name|n
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|head
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|vra
init|=
name|n
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
name|String
name|v1a
init|=
name|n
operator|.
name|getNode
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
name|String
name|v2a
init|=
name|n
operator|.
name|getNode
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
comment|// changes the node version
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^ \"test2/id\": 2"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|n
operator|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|vrb
init|=
name|n
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
name|String
name|v1b
init|=
name|n
operator|.
name|getNode
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
name|String
name|v2b
init|=
name|n
operator|.
name|getNode
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getNodeVersion
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|vra
operator|.
name|equals
argument_list|(
name|vrb
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v1a
argument_list|,
name|v1b
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|v2a
operator|.
name|equals
argument_list|(
name|v2b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

