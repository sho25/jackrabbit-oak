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
name|memory
operator|.
name|MemoryNodeStore
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
name|upgrade
operator|.
name|util
operator|.
name|VersionCopyTestUtils
operator|.
name|VersionCopySetup
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
name|upgrade
operator|.
name|version
operator|.
name|VersionCopyConfiguration
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
name|AfterClass
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
name|Property
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
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|Calendar
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
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
name|oak
operator|.
name|plugins
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|MIX_REP_VERSIONABLE_PATHS
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
name|oak
operator|.
name|upgrade
operator|.
name|util
operator|.
name|VersionCopyTestUtils
operator|.
name|createVersionableNode
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
name|oak
operator|.
name|upgrade
operator|.
name|util
operator|.
name|VersionCopyTestUtils
operator|.
name|isVersionable
import|;
end_import

begin_class
specifier|public
class|class
name|CopyVersionHistoryTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VERSIONABLES_OLD
init|=
literal|"/versionables/old"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSIONABLES_OLD_ORPHANED
init|=
literal|"/versionables/oldOrphaned"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSIONABLES_YOUNG
init|=
literal|"/versionables/young"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSIONABLES_YOUNG_ORPHANED
init|=
literal|"/versionables/youngOrphaned"
decl_stmt|;
specifier|protected
name|RepositoryImpl
name|repository
decl_stmt|;
specifier|private
specifier|static
name|Calendar
name|betweenHistories
decl_stmt|;
comment|/**      * Home directory of source repository.      */
specifier|private
specifier|static
name|File
name|source
decl_stmt|;
specifier|private
specifier|static
name|String
name|oldOrphanedHistory
decl_stmt|;
specifier|private
specifier|static
name|String
name|youngOrphanedHistory
decl_stmt|;
specifier|private
specifier|static
name|String
name|oldHistory
decl_stmt|;
specifier|private
specifier|static
name|String
name|youngHistory
decl_stmt|;
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
name|oldHistory
operator|=
name|createVersionableNode
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
expr_stmt|;
name|oldOrphanedHistory
operator|=
name|createVersionableNode
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD_ORPHANED
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|betweenHistories
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|youngOrphanedHistory
operator|=
name|createVersionableNode
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG_ORPHANED
argument_list|)
expr_stmt|;
name|youngHistory
operator|=
name|createVersionableNode
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
expr_stmt|;
name|session
operator|.
name|getNode
argument_list|(
name|VERSIONABLES_OLD_ORPHANED
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|getNode
argument_list|(
name|VERSIONABLES_YOUNG_ORPHANED
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
block|{
comment|// abuse this method to capture the source repo directory
name|CopyVersionHistoryTest
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
block|{
name|CopyVersionHistoryTest
operator|.
name|source
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyAllVersions
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
comment|// copying all versions is enabled by default
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertExisting
argument_list|(
name|session
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
name|assertHasVersionablePath
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|referencedSinceDate
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyVersions
argument_list|(
name|betweenHistories
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|oldOrphanedHistory
argument_list|)
expr_stmt|;
name|assertExisting
argument_list|(
name|session
argument_list|,
name|youngHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
name|assertHasVersionablePath
argument_list|(
name|session
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|referencedOlderThanOrphaned
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyOrphanedVersions
argument_list|(
name|betweenHistories
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldOrphanedHistory
argument_list|)
expr_stmt|;
name|assertExisting
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
name|assertHasVersionablePath
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyReferenced
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyOrphanedVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
name|assertExisting
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
name|assertHasVersionablePath
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyReferencedAfterDate
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyVersions
argument_list|(
name|betweenHistories
argument_list|)
expr_stmt|;
name|config
operator|.
name|setCopyOrphanedVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
name|assertExisting
argument_list|(
name|session
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
name|assertHasVersionablePath
argument_list|(
name|session
argument_list|,
name|youngHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyOrphaned
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyOrphanedAfterDate
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|config
operator|.
name|setCopyOrphanedVersions
argument_list|(
name|betweenHistories
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dontCopyVersionHistory
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Session
name|session
init|=
name|performCopy
argument_list|(
operator|new
name|VersionCopySetup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|(
name|VersionCopyConfiguration
name|config
parameter_list|)
block|{
name|config
operator|.
name|setCopyVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|config
operator|.
name|setCopyOrphanedVersions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_OLD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isVersionable
argument_list|(
name|session
argument_list|,
name|VERSIONABLES_YOUNG
argument_list|)
argument_list|)
expr_stmt|;
name|assertMissing
argument_list|(
name|session
argument_list|,
name|oldHistory
argument_list|,
name|youngHistory
argument_list|,
name|oldOrphanedHistory
argument_list|,
name|youngOrphanedHistory
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Session
name|performCopy
parameter_list|(
name|VersionCopySetup
name|setup
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
specifier|final
name|RepositoryConfig
name|sourceConfig
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
name|sourceContext
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|sourceConfig
argument_list|)
decl_stmt|;
specifier|final
name|NodeStore
name|targetNodeStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
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
name|sourceContext
argument_list|,
name|targetNodeStore
argument_list|)
decl_stmt|;
name|setup
operator|.
name|setup
argument_list|(
name|upgrade
operator|.
name|versionCopyConfiguration
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|setEarlyShutdown
argument_list|(
literal|true
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
name|sourceContext
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
name|targetNodeStore
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
return|return
name|repository
operator|.
name|login
argument_list|(
name|AbstractRepositoryUpgradeTest
operator|.
name|CREDENTIALS
argument_list|)
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|closeRepository
parameter_list|()
block|{
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertExisting
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|,
specifier|final
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
specifier|final
name|String
name|path
range|:
name|paths
control|)
block|{
specifier|final
name|String
name|relPath
init|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"node "
operator|+
name|path
operator|+
literal|" should exist"
argument_list|,
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|relPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertMissing
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|,
specifier|final
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
specifier|final
name|String
name|path
range|:
name|paths
control|)
block|{
specifier|final
name|String
name|relPath
init|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"node "
operator|+
name|path
operator|+
literal|" should not exist"
argument_list|,
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|relPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|assertHasVersionablePath
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|,
specifier|final
name|String
modifier|...
name|historyPaths
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|historyPath
range|:
name|historyPaths
control|)
block|{
specifier|final
name|String
name|workspaceName
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|versionHistory
init|=
name|session
operator|.
name|getNode
argument_list|(
name|historyPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|versionHistory
operator|.
name|isNodeType
argument_list|(
name|MIX_REP_VERSIONABLE_PATHS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|versionHistory
operator|.
name|hasProperty
argument_list|(
name|workspaceName
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Property
name|pathProperty
init|=
name|versionHistory
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|PATH
argument_list|,
name|pathProperty
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|VersionManager
name|vm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|historyPath
argument_list|,
name|vm
operator|.
name|getVersionHistory
argument_list|(
name|pathProperty
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

