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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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
name|assertNotNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|data
operator|.
name|FileDataStore
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
name|api
operator|.
name|PropertyState
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
name|Type
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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

begin_class
specifier|public
class|class
name|DataStoreTestBase
extends|extends
name|TestBase
block|{
specifier|protected
name|boolean
name|storesCanBeEqual
init|=
literal|false
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpServerAndClient
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|FileStore
name|setupFileDataStore
parameter_list|(
name|File
name|d
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
literal|4092
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|DataStoreBlobStore
name|blobStore
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
decl_stmt|;
return|return
name|FileStore
operator|.
name|newFileStore
argument_list|(
name|d
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|1
argument_list|)
operator|.
name|withMemoryMapping
argument_list|(
literal|false
argument_list|)
operator|.
name|withNoCache
argument_list|()
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|create
argument_list|()
return|;
block|}
specifier|protected
name|byte
index|[]
name|addTestContent
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|child
parameter_list|,
name|int
name|size
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
name|builder
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"ts"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
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
name|Blob
name|blob
init|=
name|store
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"testBlob"
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
return|return
name|data
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSync
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|mb
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|blobSize
init|=
literal|5
operator|*
name|mb
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
operator|new
name|SegmentNodeStore
argument_list|(
name|primary
argument_list|)
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
name|primary
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|,
name|blobSize
argument_list|)
decl_stmt|;
name|primary
operator|.
name|flush
argument_list|()
expr_stmt|;
name|StandbyClient
name|cl
init|=
name|newStandbyClient
argument_list|(
name|secondary
argument_list|)
decl_stmt|;
name|cl
operator|.
name|run
argument_list|()
expr_stmt|;
try|try
block|{
name|assertEquals
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
block|}
name|assertTrue
argument_list|(
name|primary
operator|.
name|size
argument_list|()
operator|<
name|mb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|size
argument_list|()
operator|<
name|mb
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|secondary
operator|.
name|getHead
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"server"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"testBlob"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|BINARY
operator|.
name|tag
argument_list|()
argument_list|,
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobSize
argument_list|,
name|b
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|blobSize
index|]
decl_stmt|;
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|b
operator|.
name|getNewStream
argument_list|()
argument_list|,
name|testData
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|testData
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
literal|100
argument_list|,
literal|1
argument_list|,
operator|-
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
name|testProxySkippedBytesIntermediateChange
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|,
operator|-
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
name|testProxyFlippedStartByte
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
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
name|testProxyFlippedIntermediateByte2
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|150000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedIntermediateByteChange
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|150
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProxyFlippedIntermediateByteChange2
parameter_list|()
throws|throws
name|Exception
block|{
name|useProxy
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|150000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|useProxy
parameter_list|(
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
specifier|final
name|int
name|mb
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|int
name|blobSize
init|=
literal|5
operator|*
name|mb
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
operator|new
name|SegmentNodeStore
argument_list|(
name|primary
argument_list|)
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
name|primary
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|,
name|blobSize
argument_list|)
decl_stmt|;
name|primary
operator|.
name|flush
argument_list|()
expr_stmt|;
name|StandbyClient
name|cl
init|=
name|newStandbyClient
argument_list|(
name|secondary
argument_list|,
name|proxyPort
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
if|if
condition|(
operator|!
name|this
operator|.
name|storesCanBeEqual
condition|)
block|{
name|assertFalse
argument_list|(
literal|"stores are not expected to be equal"
argument_list|,
name|primary
operator|.
name|getHead
argument_list|()
operator|.
name|equals
argument_list|(
name|secondary
operator|.
name|getHead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|blobSize
operator|=
literal|2
operator|*
name|mb
expr_stmt|;
name|data
operator|=
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|,
name|blobSize
argument_list|)
expr_stmt|;
name|primary
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
name|assertTrue
argument_list|(
name|primary
operator|.
name|size
argument_list|()
operator|<
name|mb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|size
argument_list|()
operator|<
name|mb
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|secondary
operator|.
name|getHead
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"server"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"testBlob"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|BINARY
operator|.
name|tag
argument_list|()
argument_list|,
name|ps
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobSize
argument_list|,
name|b
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|blobSize
index|]
decl_stmt|;
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|b
operator|.
name|getNewStream
argument_list|()
argument_list|,
name|testData
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|testData
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

