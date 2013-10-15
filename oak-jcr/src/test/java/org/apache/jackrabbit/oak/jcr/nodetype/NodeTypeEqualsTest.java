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
name|nodetype
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
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
name|AbstractJCRTest
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
name|Test
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeEqualsTest
extends|extends
name|AbstractJCRTest
block|{
comment|/**      * Tests if 2 NodeType objects are equals if they refer to the same node type. OAK-1086.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testNodeTypeEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeType
name|nt
init|=
name|testRootNode
operator|.
name|getPrimaryNodeType
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Same NoteType could be equal"
argument_list|,
name|nt
argument_list|,
name|testRootNode
operator|.
name|getPrimaryNodeType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

