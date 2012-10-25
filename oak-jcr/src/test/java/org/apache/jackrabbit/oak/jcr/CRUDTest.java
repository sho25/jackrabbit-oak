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
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
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
name|Test
specifier|public
name|void
name|testCRUD
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|testRemoveBySetProperty
parameter_list|()
throws|throws
name|RepositoryException
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
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveBySetMVProperty
parameter_list|()
throws|throws
name|RepositoryException
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
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"def"
block|}
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMissingProperty
parameter_list|()
throws|throws
name|RepositoryException
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
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"missing"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"removing a missing property should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMissingMVProperty
parameter_list|()
throws|throws
name|RepositoryException
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
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"missing"
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"removing a missing property should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootPropertyPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Property
name|property
init|=
name|getAdminSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:primaryType"
argument_list|,
name|property
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

