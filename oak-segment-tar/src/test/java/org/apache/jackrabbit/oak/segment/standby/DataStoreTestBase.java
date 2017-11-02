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
name|assertNotEquals
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
name|ByteArrayInputStream
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|commons
operator|.
name|CIHelper
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
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DataStoreTestBase
extends|extends
name|TestBase
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataStoreTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|GB
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|1024
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
name|TemporaryPort
name|proxyPort
init|=
operator|new
name|TemporaryPort
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|abstract
name|FileStore
name|getPrimary
parameter_list|()
function_decl|;
specifier|abstract
name|FileStore
name|getSecondary
parameter_list|()
function_decl|;
specifier|abstract
name|boolean
name|storesShouldBeDifferent
parameter_list|()
function_decl|;
specifier|private
name|InputStream
name|newRandomInputStream
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
specifier|final
name|int
name|seed
parameter_list|)
block|{
return|return
operator|new
name|InputStream
argument_list|()
block|{
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>=
name|size
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|count
operator|++
expr_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
return|;
block|}
block|}
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
specifier|private
name|void
name|addTestContentOnTheFly
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|child
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|seed
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
name|InputStream
name|randomInputStream
init|=
name|newRandomInputStream
argument_list|(
name|size
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|Blob
name|blob
init|=
name|store
operator|.
name|createBlob
argument_list|(
name|randomInputStream
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
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test begin: {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test end: {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResilientSync
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
comment|// run 1: unsuccessful
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
literal|4_000
argument_list|)
init|)
block|{
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// no persisted head on primary
comment|// sync shouldn't be successful, but shouldn't throw exception either,
comment|// timeout too low for TarMK flush thread to kick-in
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
block|}
comment|// run 2: successful
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
literal|4_000
argument_list|)
init|)
block|{
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// this time persisted head will be available on primary
comment|// waited at least 4s + 4s> 5s (TarMK flush thread run frequency)
name|cl
operator|.
name|run
argument_list|()
expr_stmt|;
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
name|assertTrue
argument_list|(
name|primary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
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
try|try
init|(
name|InputStream
name|blobInputStream
init|=
name|b
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|blobInputStream
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
name|assertTrue
argument_list|(
name|primary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
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
try|try
init|(
name|InputStream
name|blobInputStream
init|=
name|b
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|blobInputStream
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
comment|/*      * See OAK-5902.      */
annotation|@
name|Test
specifier|public
name|void
name|testSyncBigBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|CIHelper
operator|.
name|windows
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|blobSize
init|=
name|GB
decl_stmt|;
specifier|final
name|int
name|seed
init|=
literal|13
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
name|addTestContentOnTheFly
argument_list|(
name|store
argument_list|,
literal|"server"
argument_list|,
name|blobSize
argument_list|,
name|seed
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
literal|8
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
literal|2
operator|*
literal|60
operator|*
literal|1000
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
name|assertTrue
argument_list|(
name|primary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
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
try|try
init|(
name|InputStream
name|randomInputStream
init|=
name|newRandomInputStream
argument_list|(
name|blobSize
argument_list|,
name|seed
argument_list|)
init|;
name|InputStream
name|blobInputStream
operator|=
name|b
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|randomInputStream
argument_list|,
name|blobInputStream
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * See OAK-4969.      */
annotation|@
name|Test
specifier|public
name|void
name|testSyncUpdatedBinaryProperty
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
name|MB
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|secondary
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
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
argument_list|,
name|blobSize
argument_list|)
expr_stmt|;
name|primary
operator|.
name|flush
argument_list|()
expr_stmt|;
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
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
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
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
name|MB
argument_list|)
init|)
block|{
name|serverSync
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
init|(
name|NetworkErrorProxy
name|ignored
init|=
operator|new
name|NetworkErrorProxy
argument_list|(
name|proxyPort
operator|.
name|getPort
argument_list|()
argument_list|,
name|getServerHost
argument_list|()
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|,
name|flipPosition
argument_list|,
name|skipPosition
argument_list|,
name|skipBytes
argument_list|)
init|;
name|StandbyClientSync
name|clientSync
operator|=
name|newStandbyClientSync
argument_list|(
name|secondary
argument_list|,
name|proxyPort
operator|.
name|getPort
argument_list|()
argument_list|)
init|)
block|{
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storesShouldBeDifferent
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
literal|"stores are equal"
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
if|if
condition|(
name|intermediateChange
condition|)
block|{
name|blobSize
operator|=
literal|2
operator|*
name|MB
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
try|try
init|(
name|StandbyClientSync
name|clientSync
init|=
name|newStandbyClientSync
argument_list|(
name|secondary
argument_list|,
name|serverPort
operator|.
name|getPort
argument_list|()
argument_list|)
init|)
block|{
name|clientSync
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
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
name|assertTrue
argument_list|(
name|primary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|secondary
operator|.
name|getStats
argument_list|()
operator|.
name|getApproximateSize
argument_list|()
operator|<
name|MB
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
try|try
init|(
name|InputStream
name|blobInputStream
init|=
name|b
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|ByteStreams
operator|.
name|readFully
argument_list|(
name|blobInputStream
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
block|}
end_class

end_unit

