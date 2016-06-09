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
name|backup
package|;
end_package

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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|ByteArrayInputStream
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
name|Random
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
name|api
operator|.
name|Blob
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|security
operator|.
name|OpenSecurityProvider
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

begin_class
specifier|public
class|class
name|FileStoreBackupTest
block|{
specifier|private
name|File
name|src
decl_stmt|;
specifier|private
name|File
name|destination
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
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
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|src
operator|=
name|folder
operator|.
name|newFolder
argument_list|(
literal|"src"
argument_list|)
expr_stmt|;
name|destination
operator|=
name|folder
operator|.
name|newFolder
argument_list|(
literal|"dst"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackup
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|source
init|=
name|newFileStore
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|SegmentNodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|source
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|init
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|source
operator|.
name|flush
argument_list|()
expr_stmt|;
name|FileStoreBackup
operator|.
name|backup
argument_list|(
name|store
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|compare
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|addTestContent
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|source
operator|.
name|flush
argument_list|()
expr_stmt|;
name|FileStoreBackup
operator|.
name|backup
argument_list|(
name|store
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|compare
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|source
operator|.
name|compact
argument_list|()
expr_stmt|;
name|FileStoreBackup
operator|.
name|cleanup
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|FileStoreBackup
operator|.
name|backup
argument_list|(
name|store
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|compare
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRestore
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|source
init|=
name|newFileStore
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|SegmentNodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|source
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|init
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|source
operator|.
name|flush
argument_list|()
expr_stmt|;
name|FileStoreBackup
operator|.
name|backup
argument_list|(
name|store
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileStoreRestore
operator|.
name|restore
argument_list|(
name|destination
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|source
operator|=
name|newFileStore
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|compare
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addTestContent
parameter_list|(
name|NodeStore
name|store
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|IOException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|c
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"test-backup"
argument_list|)
operator|.
name|child
argument_list|(
literal|"binaries"
argument_list|)
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|.
name|setProperty
argument_list|(
literal|"binS"
operator|+
name|i
argument_list|,
name|createBlob
argument_list|(
name|store
argument_list|,
literal|6
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|.
name|setProperty
argument_list|(
literal|"binL"
operator|+
name|i
argument_list|,
name|createBlob
argument_list|(
name|store
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|child
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
comment|// make sure we don't backup the super-root
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
name|size
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
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|FileStore
name|newFileStore
parameter_list|(
name|File
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|fs
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withGCOptions
argument_list|(
name|SegmentGCOptions
operator|.
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|compare
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|backup
init|=
name|fileStoreBuilder
argument_list|(
name|destination
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|store
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
argument_list|,
name|backup
operator|.
name|getReader
argument_list|()
operator|.
name|readHeadState
argument_list|()
argument_list|)
expr_stmt|;
name|backup
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|init
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

