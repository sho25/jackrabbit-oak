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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|PropertyType
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|IndexDefinitionBuilder
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
name|commons
operator|.
name|JcrUtils
operator|.
name|getOrCreateByPath
import|;
end_import

begin_class
specifier|public
class|class
name|AbstractIndexCommandTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
name|RepositoryFixture
name|fixture
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|cleaup
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fixture
operator|!=
literal|null
condition|)
block|{
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createTestData
parameter_list|(
name|boolean
name|asyncIndex
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
if|if
condition|(
name|fixture
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fixture
operator|=
operator|new
name|RepositoryFixture
argument_list|(
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexIndexDefinitions
argument_list|()
expr_stmt|;
name|createLuceneIndex
argument_list|(
name|asyncIndex
argument_list|)
expr_stmt|;
name|addTestContent
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|indexIndexDefinitions
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
comment|//By default Oak index definitions are not indexed
comment|//so add them to declaringNodeTypes
name|Session
name|session
init|=
name|fixture
operator|.
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|nodeType
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/oak:index/nodetype"
argument_list|)
decl_stmt|;
name|nodeType
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"oak:QueryIndexDefinition"
block|}
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addTestContent
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|Session
name|session
init|=
name|fixture
operator|.
name|getAdminSession
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|getOrCreateByPath
argument_list|(
literal|"/testNode/a"
operator|+
name|i
argument_list|,
literal|"oak:Unstructured"
argument_list|,
name|session
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createLuceneIndex
parameter_list|(
name|boolean
name|asyncIndex
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|IndexDefinitionBuilder
name|idxBuilder
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|asyncIndex
condition|)
block|{
name|idxBuilder
operator|.
name|noAsync
argument_list|()
expr_stmt|;
block|}
name|idxBuilder
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|fixture
operator|.
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|fooIndex
init|=
name|getOrCreateByPath
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"oak:QueryIndexDefinition"
argument_list|,
name|session
argument_list|)
decl_stmt|;
name|idxBuilder
operator|.
name|build
argument_list|(
name|fooIndex
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

