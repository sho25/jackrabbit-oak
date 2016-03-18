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
name|upgrade
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|assertEquals
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|RepositoryContext
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
name|RepositoryImpl
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
name|config
operator|.
name|RepositoryConfig
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
name|document
operator|.
name|DocumentNodeStore
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
name|document
operator|.
name|DocumentMK
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import

begin_class
specifier|public
class|class
name|SameNodeSiblingsTest
block|{
specifier|public
specifier|static
specifier|final
name|Credentials
name|CREDENTIALS
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|File
name|crx2RepoDir
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|createCrx2RepoDir
parameter_list|()
throws|throws
name|IOException
block|{
name|crx2RepoDir
operator|=
name|Files
operator|.
name|createTempDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|deleteCrx2RepoDir
parameter_list|()
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|crx2RepoDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|snsShouldBeRenamed
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|DocumentNodeStore
name|nodeStore
init|=
name|migrate
argument_list|(
operator|new
name|SourceDataCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|create
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|parent
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"something_else"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|parent
operator|.
name|setPrimaryType
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
comment|// change parent type to
comment|// something that doesn't
comment|// allow SNS
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeState
name|parent
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|children
init|=
name|newHashSet
argument_list|(
name|parent
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|of
argument_list|(
literal|"child"
argument_list|,
literal|"child_2_"
argument_list|,
literal|"child_3_"
argument_list|,
literal|"something_else"
argument_list|)
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nodeStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|snsShouldntBeRenamed
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|DocumentNodeStore
name|nodeStore
init|=
name|migrate
argument_list|(
operator|new
name|SourceDataCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|create
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|parent
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"something_else"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeState
name|parent
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|children
init|=
name|newHashSet
argument_list|(
name|parent
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|of
argument_list|(
literal|"child"
argument_list|,
literal|"child[2]"
argument_list|,
literal|"child[3]"
argument_list|,
literal|"something_else"
argument_list|)
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nodeStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|snsNewNameAlreadyExists
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|DocumentNodeStore
name|nodeStore
init|=
name|migrate
argument_list|(
operator|new
name|SourceDataCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|create
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|parent
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child_2_"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child_3_"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addNode
argument_list|(
literal|"child_3_2"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|parent
operator|.
name|setPrimaryType
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeState
name|parent
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"parent"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|children
init|=
name|newHashSet
argument_list|(
name|parent
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|of
argument_list|(
literal|"child"
argument_list|,
literal|"child_2_"
argument_list|,
literal|"child_3_"
argument_list|,
literal|"child_2_2"
argument_list|,
literal|"child_3_2"
argument_list|,
literal|"child_3_3"
argument_list|)
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|nodeStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|DocumentNodeStore
name|migrate
parameter_list|(
name|SourceDataCreator
name|sourceDataCreator
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|RepositoryConfig
name|config
init|=
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|crx2RepoDir
argument_list|)
decl_stmt|;
name|RepositoryImpl
name|repository
init|=
name|RepositoryImpl
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
decl_stmt|;
name|sourceDataCreator
operator|.
name|create
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|config
operator|=
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|crx2RepoDir
argument_list|)
expr_stmt|;
comment|// re-create the config
name|RepositoryContext
name|context
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|target
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
try|try
block|{
name|RepositoryUpgrade
name|upgrade
init|=
operator|new
name|RepositoryUpgrade
argument_list|(
name|context
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|upgrade
operator|.
name|copy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
name|target
return|;
block|}
specifier|private
specifier|static
interface|interface
name|SourceDataCreator
block|{
name|void
name|create
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
block|}
end_class

end_unit

