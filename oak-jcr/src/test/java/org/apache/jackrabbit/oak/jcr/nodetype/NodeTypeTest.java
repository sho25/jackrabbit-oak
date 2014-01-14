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
name|jcr
operator|.
name|nodetype
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
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
name|AbstractRepositoryTest
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
name|NodeStoreFixture
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
name|NodeTypeTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|NodeTypeTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a node to a node type that does not accept child nodes      * See OAK-479      */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalAddNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"q2"
argument_list|,
literal|"nt:query"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalAddNodeWithProps
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|n
operator|.
name|addNode
argument_list|(
literal|"q2"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeType
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|NodeTypeManager
name|manager
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|unregisterNodeType
argument_list|(
literal|"nt:query"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|expected
parameter_list|)
block|{
comment|// this type is referenced in content, so it can't be removed
block|}
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|unregisterNodeType
argument_list|(
literal|"nt:query"
argument_list|)
expr_stmt|;
comment|// no longer referenced in content, so removal should succeed
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|unexpected
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

