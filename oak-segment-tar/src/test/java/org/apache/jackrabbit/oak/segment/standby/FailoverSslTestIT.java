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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|SegmentTestUtils
operator|.
name|addTestContent
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
name|segment
operator|.
name|test
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
name|FailoverSslTestIT
extends|extends
name|TestBase
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
name|TemporaryFileStore
name|serverFileStore
init|=
operator|new
name|TemporaryFileStore
argument_list|(
name|folder
argument_list|,
literal|false
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
literal|true
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
name|serverFileStore
argument_list|)
operator|.
name|around
argument_list|(
name|clientFileStore
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testFailoverSecure
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|storeS
init|=
name|serverFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|storeC
init|=
name|clientFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
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
name|storeS
argument_list|,
literal|true
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|storeC
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
literal|true
argument_list|)
init|;
init|)
block|{
name|assertTrue
argument_list|(
name|synchronizeAndCompareHead
argument_list|(
name|serverSync
argument_list|,
name|clientSync
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverSecureServerPlainClient
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|storeS
init|=
name|serverFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|storeC
init|=
name|clientFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
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
name|storeS
argument_list|,
literal|true
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|storeC
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|)
init|;
init|)
block|{
name|assertFalse
argument_list|(
name|synchronizeAndCompareHead
argument_list|(
name|serverSync
argument_list|,
name|clientSync
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverPlainServerSecureClient
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|storeS
init|=
name|serverFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|storeC
init|=
name|clientFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
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
name|storeS
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|storeC
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
literal|true
argument_list|)
init|;
init|)
block|{
name|assertFalse
argument_list|(
name|synchronizeAndCompareHead
argument_list|(
name|serverSync
argument_list|,
name|clientSync
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|synchronizeAndCompareHead
parameter_list|(
name|StandbyServerSync
name|serverSync
parameter_list|,
name|StandbyClientSync
name|clientSync
parameter_list|)
throws|throws
name|Exception
block|{
name|FileStore
name|storeS
init|=
name|serverFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|storeC
init|=
name|clientFileStore
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|storeS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|)
expr_stmt|;
name|storeS
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// this speeds up the test a little bit...
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
name|storeS
operator|.
name|getHead
argument_list|()
operator|.
name|equals
argument_list|(
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

