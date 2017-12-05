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
name|standby
operator|.
name|server
operator|.
name|StandbyServerSync
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
name|Ignore
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
name|ExternalPrivateStoreIT
extends|extends
name|DataStoreTestBase
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
name|Override
name|FileStore
name|getPrimary
parameter_list|()
block|{
return|return
name|serverFileStore
operator|.
name|fileStore
argument_list|()
return|;
block|}
annotation|@
name|Override
name|FileStore
name|getSecondary
parameter_list|()
block|{
return|return
name|clientFileStore
operator|.
name|fileStore
argument_list|()
return|;
block|}
annotation|@
name|Override
name|boolean
name|storesShouldBeDifferent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-7027"
argument_list|)
comment|// FIXME OAK-7027
specifier|public
name|void
name|testSyncFailingDueToTooShortTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|blobSize
init|=
literal|5
operator|*
name|MB
decl_stmt|;
name|FileStore
name|primary
init|=
name|getPrimary
argument_list|()
decl_stmt|;
name|FileStore
name|secondary
init|=
name|getSecondary
argument_list|()
decl_stmt|;
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|primary
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|,
name|blobSize
argument_list|)
expr_stmt|;
try|try
init|(
name|StandbyServerSync
name|serverSync
init|=
operator|new
name|StandbyServerSync
argument_list|(
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
name|primary
argument_list|,
literal|1
operator|*
name|MB
argument_list|)
init|;
name|StandbyClientSync
name|cl
operator|=
name|newStandbyClientSync
argument_list|(
name|secondary
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|60
argument_list|)
init|)
block|{
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
name|primary
operator|.
name|flush
argument_list|()
expr_stmt|;
name|cl
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
name|cl
operator|.
name|getFailedRequests
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

