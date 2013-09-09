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
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
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
name|api
operator|.
name|CommitFailedException
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

begin_comment
comment|/**  * See OAK-993  */
end_comment

begin_class
specifier|public
class|class
name|ItemSaveTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|ItemSaveTest
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
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|root
decl_stmt|;
specifier|private
name|Node
name|foo
decl_stmt|;
specifier|private
name|Property
name|prop0
decl_stmt|;
specifier|private
name|Property
name|prop1
decl_stmt|;
specifier|private
name|Property
name|prop2
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|=
name|getAdminSession
argument_list|()
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
expr_stmt|;
name|foo
operator|=
name|root
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"child0"
argument_list|)
expr_stmt|;
name|prop0
operator|=
name|root
operator|.
name|setProperty
argument_list|(
literal|"p0"
argument_list|,
literal|"v0"
argument_list|)
expr_stmt|;
name|prop1
operator|=
name|foo
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"v1"
argument_list|)
expr_stmt|;
name|prop2
operator|=
name|foo
operator|.
name|setProperty
argument_list|(
literal|"p2"
argument_list|,
literal|"v2"
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
name|noChangesAtAll
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|foo
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveContainsAllChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|foo
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|foo
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveOnRoot
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|root
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|root
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveMissesNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|root
operator|.
name|addNode
argument_list|(
literal|"child1"
argument_list|)
expr_stmt|;
name|foo
operator|.
name|addNode
argument_list|(
literal|"child2"
argument_list|)
expr_stmt|;
name|foo
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected UnsupportedRepositoryOperationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveOnNewNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|foo
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|)
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected UnsupportedRepositoryOperationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveOnChangedProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// Property on root
name|prop0
operator|.
name|setValue
argument_list|(
literal|"changed"
argument_list|)
expr_stmt|;
name|prop0
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Property on child node
name|prop1
operator|.
name|setValue
argument_list|(
literal|"changed"
argument_list|)
expr_stmt|;
name|prop1
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveMissesProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|prop1
operator|.
name|setValue
argument_list|(
literal|"changed"
argument_list|)
expr_stmt|;
name|prop2
operator|.
name|setValue
argument_list|(
literal|"changed"
argument_list|)
expr_stmt|;
name|prop1
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected UnsupportedRepositoryOperationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveOnNewProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|foo
operator|.
name|setProperty
argument_list|(
literal|"p3"
argument_list|,
literal|"v3"
argument_list|)
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected UnsupportedRepositoryOperationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

