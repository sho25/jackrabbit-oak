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
name|jcr
operator|.
name|repository
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|NodeBuilder
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
name|Before
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|RepeatedRepositorySidegradeTest
extends|extends
name|RepeatedRepositoryUpgradeTest
block|{
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
operator|!
name|upgradeComplete
condition|)
block|{
specifier|final
name|File
name|sourceDir
init|=
operator|new
name|File
argument_list|(
name|getTestDirectory
argument_list|()
argument_list|,
literal|"jackrabbit2"
argument_list|)
decl_stmt|;
name|sourceDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileStore
name|fileStore
init|=
name|FileStore
operator|.
name|newFileStore
argument_list|(
name|sourceDir
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|SegmentNodeStore
name|segmentNodeStore
init|=
name|SegmentNodeStore
operator|.
name|newSegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|RepositoryImpl
name|repository
init|=
operator|(
name|RepositoryImpl
operator|)
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
name|segmentNodeStore
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
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
try|try
block|{
name|createSourceContent
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|NodeStore
name|target
init|=
name|getTargetNodeStore
argument_list|()
decl_stmt|;
name|doUpgradeRepository
argument_list|(
name|sourceDir
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fileStore
operator|=
name|FileStore
operator|.
name|newFileStore
argument_list|(
name|sourceDir
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|segmentNodeStore
operator|=
name|SegmentNodeStore
operator|.
name|newSegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|repository
operator|=
operator|(
name|RepositoryImpl
operator|)
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
name|segmentNodeStore
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
expr_stmt|;
try|try
block|{
name|modifySourceContent
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|doUpgradeRepository
argument_list|(
name|sourceDir
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
name|upgradeComplete
operator|=
literal|true
expr_stmt|;
block|}
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
name|FileStore
name|fileStore
init|=
name|FileStore
operator|.
name|newFileStore
argument_list|(
name|source
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|SegmentNodeStore
name|segmentNodeStore
init|=
name|SegmentNodeStore
operator|.
name|newSegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|RepositorySidegrade
name|repositoryUpgrade
init|=
operator|new
name|RepositorySidegrade
argument_list|(
name|segmentNodeStore
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|repositoryUpgrade
operator|.
name|copy
argument_list|(
operator|new
name|RepositoryInitializer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

