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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|api
operator|.
name|JackrabbitSession
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
name|Oak
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
name|Jcr
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
name|segment
operator|.
name|SegmentNodeStore
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|Clock
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
name|BeforeClass
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRepositoryUpgradeTest
block|{
specifier|protected
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
specifier|static
name|Repository
name|targetRepository
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
comment|// ensure that we create a new repository for the next test
name|targetRepository
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
specifier|synchronized
name|void
name|upgradeRepository
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|targetRepository
operator|==
literal|null
condition|)
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"upgrade-"
operator|+
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTimeIncreasing
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"source"
argument_list|)
decl_stmt|;
name|source
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|InputStream
name|repoConfig
init|=
name|getRepositoryConfig
argument_list|()
decl_stmt|;
name|RepositoryConfig
name|config
decl_stmt|;
if|if
condition|(
name|repoConfig
operator|==
literal|null
condition|)
block|{
name|config
operator|=
name|RepositoryConfig
operator|.
name|install
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|OutputStream
name|out
init|=
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|source
argument_list|,
literal|"repository.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|repoConfig
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|repoConfig
operator|.
name|close
argument_list|()
expr_stmt|;
name|config
operator|=
name|RepositoryConfig
operator|.
name|create
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
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
name|createSourceContent
argument_list|(
name|repository
argument_list|)
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
name|NodeStore
name|target
init|=
operator|new
name|SegmentNodeStore
argument_list|()
decl_stmt|;
name|RepositoryUpgrade
operator|.
name|copy
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|targetRepository
operator|=
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
name|target
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|InputStream
name|getRepositoryConfig
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Repository
name|getTargetRepository
parameter_list|()
block|{
return|return
name|targetRepository
return|;
block|}
specifier|public
name|JackrabbitSession
name|createAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|(
name|JackrabbitSession
operator|)
name|getTargetRepository
argument_list|()
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|void
name|createSourceContent
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

