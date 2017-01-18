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
name|commons
operator|.
name|CIHelper
operator|.
name|jenkinsNodeLabel
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
name|Assume
operator|.
name|assumeFalse
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
name|FailoverIPRangeIT
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
name|testFailoverAllClients
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverLocalClient
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverLocalClientUseIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
literal|"::1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"::1"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverWrongClient
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.2"
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverWrongClientIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"::2"
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverLocalhost
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"localhost"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverValidIPRangeStart
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1-127.0.0.2"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverValidIPRangeEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.0-127.0.0.1"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverValidIPRange
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.0-127.0.0.2"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverInvalidRange
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.2-127.0.0.1"
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverCorrectList
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127-128"
block|,
literal|"126.0.0.1"
block|,
literal|"127.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverCorrectListIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"122-126"
block|,
literal|"::1"
block|,
literal|"126.0.0.1"
block|,
literal|"127.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverWrongList
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"126.0.0.1"
block|,
literal|"::2"
block|,
literal|"128.0.0.1-255.255.255.255"
block|,
literal|"128.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverCorrectListUseIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
literal|"::1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"127-128"
block|,
literal|"0:0:0:0:0:0:0:1"
block|,
literal|"126.0.0.1"
block|,
literal|"127.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverCorrectListIPv6UseIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
literal|"::1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"122-126"
block|,
literal|"::1"
block|,
literal|"126.0.0.1"
block|,
literal|"127.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailoverWrongListUseIPv6
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|jenkinsNodeLabel
argument_list|(
literal|"beam"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
name|noDualStackSupport
argument_list|)
expr_stmt|;
name|createTestWithConfig
argument_list|(
literal|"::1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"126.0.0.1"
block|,
literal|"::2"
block|,
literal|"128.0.0.1-255.255.255.255"
block|,
literal|"128.0.0.0-127.255.255.255"
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createTestWithConfig
parameter_list|(
name|String
index|[]
name|ipRanges
parameter_list|,
name|boolean
name|expectedToWork
parameter_list|)
throws|throws
name|Exception
block|{
name|createTestWithConfig
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|ipRanges
argument_list|,
name|expectedToWork
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createTestWithConfig
parameter_list|(
name|String
name|host
parameter_list|,
name|String
index|[]
name|ipRanges
parameter_list|,
name|boolean
name|expectedToWork
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
name|ipRanges
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
operator|new
name|StandbyClientSync
argument_list|(
name|host
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
name|storeC
argument_list|,
literal|false
argument_list|,
name|getClientTimeout
argument_list|()
argument_list|,
literal|false
argument_list|)
init|)
block|{
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
if|if
condition|(
name|expectedToWork
condition|)
block|{
name|assertEquals
argument_list|(
name|storeS
operator|.
name|getHead
argument_list|()
argument_list|,
name|storeC
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"stores are equal but shouldn't!"
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

