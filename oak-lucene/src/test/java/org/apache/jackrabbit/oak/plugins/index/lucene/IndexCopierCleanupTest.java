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
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Closer
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
name|NodeState
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FilterDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|SimpleFSDirectory
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
name|Rule
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
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
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|CopyOnReadDirectory
operator|.
name|DELETE_MARGIN_MILLIS_NAME
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

begin_class
specifier|public
class|class
name|IndexCopierCleanupTest
block|{
specifier|private
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|maxFileSize
init|=
literal|7896
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SAFE_MARGIN_FOR_DELETION
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MARGIN_BUFFER_FOR_FS_GRANULARITY
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Clock
name|CLOCK
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|CLOCK
operator|.
name|waitUntil
argument_list|(
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignored
block|}
block|}
annotation|@
name|Rule
specifier|public
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
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|String
name|indexPath
init|=
literal|"/oak:index/test"
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|IndexDefinition
name|defn
init|=
literal|null
decl_stmt|;
specifier|private
name|CloseSafeRemoteRAMDirectory
name|remote
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|localFSDir
init|=
literal|null
decl_stmt|;
specifier|private
name|RAMIndexCopier
name|copier
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|DELETE_MARGIN_MILLIS_NAME
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|SAFE_MARGIN_FOR_DELETION
argument_list|)
argument_list|)
expr_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|defn
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|remote
operator|=
operator|new
name|CloseSafeRemoteRAMDirectory
argument_list|(
name|closer
argument_list|)
expr_stmt|;
name|localFSDir
operator|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
name|copier
operator|=
operator|new
name|RAMIndexCopier
argument_list|(
name|localFSDir
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// convince copier that local FS dir is ok (avoid integrity check doing the cleanup)
name|copier
operator|.
name|getCoRDir
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|DELETE_MARGIN_MILLIS_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicOperationSameNodeIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|cow
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|cow
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor1
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cow
operator|=
name|copier
operator|.
name|getCoWDir
argument_list|()
expr_stmt|;
name|cow
operator|.
name|deleteFile
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|cow
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor2
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cor1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//CoR1 saw "a" and everything else is newer. Nothing should get deleted
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|cow
operator|=
name|copier
operator|.
name|getCoWDir
argument_list|()
expr_stmt|;
name|cow
operator|.
name|deleteFile
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|cow
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor3
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cor2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|cor3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicOperationRemoteNodeIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|writeFile
argument_list|(
name|remote
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor1
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|remote
operator|.
name|deleteFile
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|remote
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor2
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cor1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//CoR1 saw "a" and everything else ("b" due to CoR2) is newer. Nothing should get deleted
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|remote
operator|.
name|deleteFile
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|remote
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor3
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cor2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|cor3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak7246Description
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Step 1
name|Directory
name|cow1
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow1
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow1
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|cow1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|remoteSnapshowCow1
init|=
name|remote
operator|.
name|snapshot
argument_list|()
decl_stmt|;
comment|// Step 2
name|Directory
name|cow2
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|cow2
operator|.
name|deleteFile
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|cow2
operator|.
name|deleteFile
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow2
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow2
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
comment|// Step 3
name|Directory
name|cor1
init|=
name|copier
operator|.
name|getCoRDir
argument_list|(
name|remoteSnapshowCow1
argument_list|)
decl_stmt|;
comment|// local listing
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
name|localFSDir
argument_list|)
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// reader listing
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|cor1
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Step 4
name|cow2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|remoteSnapshotCow2
init|=
name|remote
operator|.
name|snapshot
argument_list|()
decl_stmt|;
comment|// Step 5
name|Directory
name|cow3
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|cow3
operator|.
name|deleteFile
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|cow3
operator|.
name|deleteFile
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow3
argument_list|,
literal|"e"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|cow3
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
comment|// Step 6
name|Directory
name|cor2
init|=
name|copier
operator|.
name|getCoRDir
argument_list|(
name|remoteSnapshotCow2
argument_list|)
decl_stmt|;
comment|// local listing
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|,
literal|"e"
argument_list|,
literal|"f"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
name|localFSDir
argument_list|)
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// reader listing
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"c"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|cor2
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Step 7
name|cor1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// nothing should get deleted as CoR1 sees "a", "b" and everything else is newer
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|,
literal|"e"
argument_list|,
literal|"f"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
name|localFSDir
argument_list|)
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newlyWrittenFileMustNotBeDeletedDueToLateObservation
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|cow1
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow1
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|cow1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|snap1
init|=
name|remote
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|Directory
name|cow2
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow2
argument_list|,
literal|"fileX"
argument_list|)
expr_stmt|;
name|cow2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor1
init|=
name|copier
operator|.
name|getCoRDir
argument_list|(
name|snap1
argument_list|)
decl_stmt|;
name|cor1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"fileX"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newlyWrittenFileMustNotBeDeletedDueToLateClose
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|cow1
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow1
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|cow1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|cor1
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|Directory
name|cow2
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow2
argument_list|,
literal|"fileX"
argument_list|)
expr_stmt|;
name|cow2
operator|.
name|close
argument_list|()
expr_stmt|;
name|cor1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"fileX"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|failedWritesGetCleanedUp
parameter_list|()
throws|throws
name|Exception
block|{
name|CloseSafeRemoteRAMDirectory
name|oldRemote
init|=
name|remote
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|Directory
name|failedWriter
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|failedWriter
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|failedWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// actually, everything would've worked for 'failedWriter', but we restore 'remote' to old state
comment|// to fake failed remote update
name|remote
operator|=
name|oldRemote
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create some files that get sent to remote
name|Directory
name|cow
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|cow
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// reader that would invoke cleanup according to its view on close
name|Directory
name|cor
init|=
name|copier
operator|.
name|getCoRDir
argument_list|()
decl_stmt|;
name|cor
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|strayFilesGetRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|DelayCopyingSimpleFSDirectory
name|strayDir
init|=
operator|new
name|DelayCopyingSimpleFSDirectory
argument_list|(
name|localFSDir
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|strayDir
argument_list|,
literal|"oldestStray"
argument_list|)
expr_stmt|;
comment|// add "a" directly to remote
name|writeFile
argument_list|(
name|remote
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getCoRDir
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// "a" is added to remote and hence local FS gets when CoR is opened
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"oldestStray"
argument_list|)
argument_list|)
expr_stmt|;
comment|// "b" gets created locally by CoW
name|Directory
name|cow
init|=
name|copier
operator|.
name|getCoWDir
argument_list|()
decl_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|strayDir
argument_list|,
literal|"oldStray"
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getCoRDir
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// "oldStray" is newer than "b"
comment|// hence, doesn't get removed yet
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"oldStray"
argument_list|)
argument_list|)
expr_stmt|;
comment|// "c" gets created locally
name|cow
operator|=
name|copier
operator|.
name|getCoWDir
argument_list|()
expr_stmt|;
name|writeFile
argument_list|(
name|cow
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
comment|// "newStray" is newer than "c"
name|writeFile
argument_list|(
name|strayDir
argument_list|,
literal|"newStray"
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getCoRDir
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|existsLocally
argument_list|(
literal|"oldStray"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existsLocally
argument_list|(
literal|"newStray"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|marginIsRespected
parameter_list|()
throws|throws
name|Exception
block|{
name|writeFile
argument_list|(
name|remote
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|localFSDir
argument_list|,
literal|"beyond-margin"
argument_list|)
argument_list|,
literal|"beyond-margin-data"
argument_list|,
operator|(
name|Charset
operator|)
literal|null
argument_list|)
expr_stmt|;
name|DelayCopyingSimpleFSDirectory
operator|.
name|updateLastModified
argument_list|(
name|localFSDir
argument_list|,
literal|"beyond-margin"
argument_list|)
expr_stmt|;
comment|// Delay 1 more second to avoid FS time granularity
name|CLOCK
operator|.
name|waitUntil
argument_list|(
name|CLOCK
operator|.
name|getTime
argument_list|()
operator|+
name|SAFE_MARGIN_FOR_DELETION
operator|+
name|MARGIN_BUFFER_FOR_FS_GRANULARITY
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|localFSDir
argument_list|,
literal|"within-margin"
argument_list|)
argument_list|,
literal|"within-margin-data"
argument_list|,
operator|(
name|Charset
operator|)
literal|null
argument_list|)
expr_stmt|;
name|DelayCopyingSimpleFSDirectory
operator|.
name|updateLastModified
argument_list|(
name|localFSDir
argument_list|,
literal|"within-margin"
argument_list|)
expr_stmt|;
name|copier
operator|.
name|getCoRDir
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"within-margin"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
name|localFSDir
argument_list|)
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|existsLocally
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|localFSDir
argument_list|,
name|fileName
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
specifier|private
name|void
name|writeFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxFileSize
argument_list|)
operator|+
literal|1
operator|)
index|]
decl_stmt|;
name|rnd
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|IndexOutput
name|o
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|o
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|DelayCopyingSimpleFSDirectory
operator|.
name|updateLastModified
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|RAMIndexCopier
extends|extends
name|IndexCopier
block|{
specifier|final
name|File
name|baseFSDir
decl_stmt|;
name|RAMIndexCopier
parameter_list|(
name|File
name|baseFSDir
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|File
name|indexRootDir
parameter_list|,
name|boolean
name|prefetchEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|executor
argument_list|,
name|indexRootDir
argument_list|,
name|prefetchEnabled
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseFSDir
operator|=
name|baseFSDir
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Directory
name|createLocalDirForIndexReader
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DelayCopyingSimpleFSDirectory
argument_list|(
name|baseFSDir
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Directory
name|createLocalDirForIndexWriter
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DelayCopyingSimpleFSDirectory
argument_list|(
name|baseFSDir
argument_list|)
return|;
block|}
name|Directory
name|getCoRDir
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getCoRDir
argument_list|(
name|remote
operator|.
name|snapshot
argument_list|()
argument_list|)
return|;
block|}
name|Directory
name|getCoRDir
parameter_list|(
name|Directory
name|remoteSnapshot
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrapForRead
argument_list|(
name|indexPath
argument_list|,
name|defn
argument_list|,
name|remoteSnapshot
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|)
return|;
block|}
name|Directory
name|getCoWDir
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrapForWrite
argument_list|(
name|defn
argument_list|,
name|remote
argument_list|,
literal|false
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|DelayCopyingSimpleFSDirectory
extends|extends
name|SimpleFSDirectory
block|{
name|DelayCopyingSimpleFSDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|updateLastModified
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|DelayCopyingSimpleFSDirectory
name|d
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|DelayCopyingSimpleFSDirectory
condition|)
block|{
name|d
operator|=
operator|(
name|DelayCopyingSimpleFSDirectory
operator|)
name|dir
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|instanceof
name|FilterDirectory
condition|)
block|{
name|Directory
name|delegate
init|=
operator|(
operator|(
name|FilterDirectory
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
decl_stmt|;
if|if
condition|(
name|delegate
operator|instanceof
name|DelayCopyingSimpleFSDirectory
condition|)
block|{
name|d
operator|=
operator|(
name|DelayCopyingSimpleFSDirectory
operator|)
name|delegate
expr_stmt|;
block|}
block|}
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|d
operator|.
name|updateLastModified
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|updateLastModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|updateLastModified
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|CLOCK
operator|.
name|waitUntil
argument_list|(
name|CLOCK
operator|.
name|getTime
argument_list|()
operator|+
name|SAFE_MARGIN_FOR_DELETION
operator|+
name|MARGIN_BUFFER_FOR_FS_GRANULARITY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// ignored
block|}
block|}
specifier|static
name|void
name|updateLastModified
parameter_list|(
name|File
name|fsDirectory
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Update file timestamp manually to mimic last updated time updates without sleeping
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fsDirectory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|setLastModified
argument_list|(
name|CLOCK
operator|.
name|getTime
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to update last modified for "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|CloseSafeRemoteRAMDirectory
extends|extends
name|RAMDirectory
block|{
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
name|CloseSafeRemoteRAMDirectory
parameter_list|(
name|Closer
name|closer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|this
operator|::
name|close0
argument_list|)
expr_stmt|;
block|}
name|CloseSafeRemoteRAMDirectory
parameter_list|(
name|CloseSafeRemoteRAMDirectory
name|that
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|that
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|that
operator|.
name|closer
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|this
operator|::
name|close0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|to
operator|instanceof
name|DelayCopyingSimpleFSDirectory
condition|)
block|{
operator|(
operator|(
name|DelayCopyingSimpleFSDirectory
operator|)
name|to
operator|)
operator|.
name|updateLastModified
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
name|CloseSafeRemoteRAMDirectory
name|snapshot
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|CloseSafeRemoteRAMDirectory
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|private
name|void
name|close0
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

