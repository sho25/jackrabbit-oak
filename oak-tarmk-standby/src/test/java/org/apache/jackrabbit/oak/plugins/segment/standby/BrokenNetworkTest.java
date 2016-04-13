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
name|segment
operator|.
name|standby
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
name|plugins
operator|.
name|segment
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
name|standby
operator|.
name|client
operator|.
name|StandbyClient
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
name|standby
operator|.
name|server
operator|.
name|StandbyServer
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
name|Test
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

begin_class
specifier|public
class|class
name|BrokenNetworkTest
extends|extends
name|TestBase
block|{
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpServerAndTwoClients
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|closeServerAndTwoClients
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
name|NetworkErrorProxy
name|p
init|=
operator|new
name|NetworkErrorProxy
argument_list|(
name|proxyPort
argument_list|,
name|LOCALHOST
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|p
operator|.
name|skipBytes
argument_list|(
name|skipPosition
argument_list|,
name|skipBytes
argument_list|)
expr_stmt|;
name|p
operator|.
name|flipByte
argument_list|(
name|flipPosition
argument_list|)
expr_stmt|;
name|p
operator|.
name|run
argument_list|()
expr_stmt|;
name|NodeStore
name|store
init|=
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|storeS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|StandbyServer
name|server
init|=
operator|new
name|StandbyServer
argument_list|(
name|port
argument_list|,
name|storeS
argument_list|,
name|ssl
argument_list|)
decl_stmt|;
name|server
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
name|StandbyClient
name|cl
init|=
name|newStandbyClient
argument_list|(
name|storeC
argument_list|,
name|proxyPort
argument_list|,
name|ssl
argument_list|)
decl_stmt|;
name|cl
operator|.
name|run
argument_list|()
expr_stmt|;
try|try
block|{
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
name|p
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
name|cl
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
finally|finally
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
name|cl
operator|.
name|close
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

