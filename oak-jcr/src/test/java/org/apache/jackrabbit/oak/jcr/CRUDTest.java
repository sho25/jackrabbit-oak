begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|Test
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
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
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

begin_class
specifier|public
class|class
name|CRUDTest
extends|extends
name|AbstractRepositoryTest
block|{
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCRUD
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Repository
name|repository
init|=
name|getRepository
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create
name|Node
name|hello
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|hello
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Read
name|assertEquals
argument_list|(
literal|"hello world"
argument_list|,
name|session
operator|.
name|getProperty
argument_list|(
literal|"/hello/world"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update
name|session
operator|.
name|getNode
argument_list|(
literal|"/hello"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
literal|"Hello, World!"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello, World!"
argument_list|,
name|session
operator|.
name|getProperty
argument_list|(
literal|"/hello/world"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Delete
name|session
operator|.
name|getNode
argument_list|(
literal|"/hello"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|session
operator|.
name|propertyExists
argument_list|(
literal|"/hello/world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

