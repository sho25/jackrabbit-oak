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
name|proxy
operator|.
name|NetworkErrorProxy
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
name|AfterClass
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
name|BrokenNetworkIT
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
name|clientFileStore1
init|=
operator|new
name|TemporaryFileStore
argument_list|(
name|folder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
name|TemporaryFileStore
name|clientFileStore2
init|=
operator|new
name|TemporaryFileStore
argument_list|(
name|folder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|NetworkErrorProxy
name|proxy
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
name|clientFileStore1
argument_list|)
operator|.
name|around
argument_list|(
name|clientFileStore2
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|proxy
operator|=
operator|new
name|NetworkErrorProxy
argument_list|(
name|getProxyPort
argument_list|()
argument_list|,
name|getServerHost
argument_list|()
argument_list|,
name|getServerPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxy
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxySSL
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxySkippedBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxySSLSkippedBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|,
literal|400
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxySkippedBytesIntermediateChange
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxySSLSkippedBytesIntermediateChange
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|,
literal|400
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedStartByte
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedStartByteSSL
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedIntermediateByte
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|150
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedIntermediateByteSSL
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|560
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedEndByte
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|220
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedEndByteSSL
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|575
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// private helper
specifier|private
name|void
name|useProxy
parameter_list|(
name|boolean
name|ssl
parameter_list|)
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
name|ssl
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|useProxy
parameter_list|(
name|boolean
name|ssl
parameter_list|,
name|int
name|skipPosition
parameter_list|,
name|int
name|skipBytes
parameter_list|,
name|boolean
name|intermediateChange
parameter_list|)
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
name|ssl
argument_list|,
name|skipPosition
argument_list|,
name|skipBytes
argument_list|,
operator|-
literal|1
argument_list|,
name|intermediateChange
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|useProxy
parameter_list|(
name|boolean
name|ssl
parameter_list|,
name|int
name|skipPosition
parameter_list|,
name|int
name|skipBytes
parameter_list|,
name|int
name|flipPosition
parameter_list|,
name|boolean
name|intermediateChange
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
name|clientFileStore1
operator|.
name|fileStore
argument_list|()
decl_stmt|;
name|FileStore
name|storeC2
init|=
name|clientFileStore2
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
try|try
init|(
name|StandbyServerSync
name|serverSync
init|=
operator|new
name|StandbyServerSync
argument_list|(
name|getServerPort
argument_list|()
argument_list|,
name|storeS
argument_list|,
name|ssl
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|storeC
argument_list|,
name|getProxyPort
argument_list|()
argument_list|,
name|ssl
argument_list|)
init|;
init|)
block|{
name|proxy
operator|.
name|skipBytes
argument_list|(
name|skipPosition
argument_list|,
name|skipBytes
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|flipByte
argument_list|(
name|flipPosition
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|connect
argument_list|()
expr_stmt|;
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|skipBytes
operator|>
literal|0
operator|||
name|flipPosition
operator|>=
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
literal|"stores are not expected to be equal"
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
name|assertEquals
argument_list|(
name|storeC2
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
name|proxy
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|intermediateChange
condition|)
block|{
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server2"
argument_list|)
expr_stmt|;
name|storeS
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

