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
name|segment
operator|.
name|standby
operator|.
name|server
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
name|assertNotEquals
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
name|io
operator|.
name|InputStream
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|NullInputStream
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
name|commons
operator|.
name|junit
operator|.
name|TemporaryPort
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
name|segment
operator|.
name|standby
operator|.
name|client
operator|.
name|StandbyClientSync
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
name|test
operator|.
name|TemporaryBlobStore
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
name|test
operator|.
name|TemporaryFileStore
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
name|RuleChain
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
name|SlowServerIT
block|{
specifier|private
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
specifier|private
name|TemporaryBlobStore
name|serverBlobStore
init|=
operator|new
name|TemporaryBlobStore
argument_list|(
name|folder
argument_list|)
decl_stmt|;
specifier|private
name|TemporaryFileStore
name|serverFileStore
init|=
operator|new
name|TemporaryFileStore
argument_list|(
name|folder
argument_list|,
name|serverBlobStore
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|TemporaryBlobStore
name|clientBlobStore
init|=
operator|new
name|TemporaryBlobStore
argument_list|(
name|folder
argument_list|)
decl_stmt|;
specifier|private
name|TemporaryFileStore
name|clientFileStore
init|=
operator|new
name|TemporaryFileStore
argument_list|(
name|folder
argument_list|,
name|clientBlobStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|RuleChain
name|chain
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
name|folder
argument_list|)
operator|.
name|around
argument_list|(
name|serverBlobStore
argument_list|)
operator|.
name|around
argument_list|(
name|serverFileStore
argument_list|)
operator|.
name|around
argument_list|(
name|clientBlobStore
argument_list|)
operator|.
name|around
argument_list|(
name|clientFileStore
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryPort
name|serverPort
init|=
operator|new
name|TemporaryPort
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSyncFailingDueToTooShortTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|primary
init|=
name|serverFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|secondary
init|=
name|clientFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
comment|// Add a node on the primary that references a 5MB binary.
name|createTestContent
argument_list|(
name|primary
argument_list|)
expr_stmt|;
comment|// Configure a StandbyBlobReader that behaves like the default one, but
comment|// adds a 2s delay every time a binary is fetched from the Data Store.
name|StandbyBlobReader
name|blobReader
init|=
name|newDelayedBlobReader
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|DefaultStandbyBlobReader
argument_list|(
name|primary
operator|.
name|getBlobStore
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
comment|// The primary uses the delayed StandbyBlobReader and is configured
comment|// to transfer binaries in chunks of 1MB.
name|StandbyServerSync
name|server
init|=
name|StandbyServerSync
operator|.
name|builder
argument_list|()
operator|.
name|withPort
argument_list|(
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|withFileStore
argument_list|(
name|primary
argument_list|)
operator|.
name|withBlobChunkSize
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
operator|.
name|withStandbyBlobReader
argument_list|(
name|blobReader
argument_list|)
operator|.
name|build
argument_list|()
init|;
comment|// The client expects the server to respond withing 1s. When the
comment|// binary is requested, the delay on the server guarantees that the
comment|// timeout on the client will expire.
name|StandbyClientSync
name|client
operator|=
operator|new
name|StandbyClientSync
argument_list|(
literal|"localhost"
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
name|secondary
argument_list|,
literal|false
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
argument_list|)
init|)
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertNotEquals
argument_list|(
name|primary
operator|.
name|getHead
argument_list|()
argument_list|,
name|secondary
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
operator|.
name|getFailedRequests
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createTestContent
parameter_list|(
name|FileStore
name|fileStore
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|IOException
block|{
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|Blob
name|blob
init|=
name|store
operator|.
name|createBlob
argument_list|(
operator|new
name|NullInputStream
argument_list|(
literal|5
operator|*
literal|1024
operator|*
literal|102
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"data"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
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
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
name|StandbyBlobReader
name|newDelayedBlobReader
parameter_list|(
name|int
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|,
name|StandbyBlobReader
name|wrapped
parameter_list|)
block|{
return|return
operator|new
name|StandbyBlobReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|readBlob
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|timeUnit
operator|.
name|toMillis
argument_list|(
name|delay
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
return|return
name|wrapped
operator|.
name|readBlob
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getBlobLength
parameter_list|(
name|String
name|blobId
parameter_list|)
block|{
return|return
name|wrapped
operator|.
name|getBlobLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit
