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
name|upgrade
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
name|commons
operator|.
name|JcrUtils
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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

begin_class
specifier|public
class|class
name|IncludeExcludeUpgradeTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
annotation|@
name|Override
specifier|protected
name|void
name|createSourceContent
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/foo/de"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/foo/en"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/foo/fr"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/foo/it"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2015"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2014"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2013"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2012"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2011"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2010"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2010/12"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|JcrUtils
operator|.
name|getOrCreateByPath
argument_list|(
literal|"/content/assets/foo/2010/11"
argument_list|,
literal|"nt:folder"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doUpgradeRepository
parameter_list|(
name|File
name|source
parameter_list|,
name|NodeStore
name|target
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
specifier|final
name|RepositoryConfig
name|config
init|=
name|RepositoryConfig
operator|.
name|create
argument_list|(
name|source
argument_list|)
decl_stmt|;
specifier|final
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
try|try
block|{
specifier|final
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
name|setIncludes
argument_list|(
literal|"/content/foo/en"
argument_list|,
literal|"/content/assets/foo"
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|setExcludes
argument_list|(
literal|"/content/assets/foo/2013"
argument_list|,
literal|"/content/assets/foo/2012"
argument_list|,
literal|"/content/assets/foo/2011"
argument_list|,
literal|"/content/assets/foo/2010"
argument_list|)
expr_stmt|;
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldHaveIncludedPaths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertExisting
argument_list|(
literal|"/content/foo/en"
argument_list|,
literal|"/content/assets/foo/2015/02"
argument_list|,
literal|"/content/assets/foo/2015/01"
argument_list|,
literal|"/content/assets/foo/2014"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldLackPathsThatWereNotIncluded
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertMissing
argument_list|(
literal|"/content/foo/de"
argument_list|,
literal|"/content/foo/fr"
argument_list|,
literal|"/content/foo/it"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldLackExcludedPaths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertMissing
argument_list|(
literal|"/content/assets/foo/2013"
argument_list|,
literal|"/content/assets/foo/2012"
argument_list|,
literal|"/content/assets/foo/2011"
argument_list|,
literal|"/content/assets/foo/2010"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

