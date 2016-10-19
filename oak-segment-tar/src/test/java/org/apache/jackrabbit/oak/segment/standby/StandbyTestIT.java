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
name|StandbyTestIT
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
name|serverFileStore
argument_list|)
operator|.
name|around
argument_list|(
name|clientFileStore
argument_list|)
decl_stmt|;
specifier|private
specifier|static
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
parameter_list|,
name|int
name|dataNodes
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
name|content
init|=
name|builder
operator|.
name|child
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|content
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
name|content
operator|.
name|setProperty
argument_list|(
literal|"testBlob"
argument_list|,
name|blob
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dataNodes
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|c
init|=
name|content
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|c
operator|.
name|setProperty
argument_list|(
literal|"p"
operator|+
name|i
argument_list|,
literal|"v"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/**      * OAK-2430      */
annotation|@
name|Test
specifier|public
name|void
name|testSyncLoop
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|blobSize
init|=
literal|25
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|dataNodes
init|=
literal|5000
decl_stmt|;
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
specifier|final
name|StandbyServerSync
name|serverSync
init|=
operator|new
name|StandbyServerSync
argument_list|(
name|getServerPort
argument_list|()
argument_list|,
name|primary
argument_list|)
decl_stmt|;
name|serverSync
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
argument_list|,
name|dataNodes
argument_list|)
decl_stmt|;
name|primary
operator|.
name|flush
argument_list|()
expr_stmt|;
name|StandbyClientSync
name|clientSync
init|=
name|newStandbyClientSync
argument_list|(
name|secondary
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
name|store
operator|.
name|release
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|clientSync
operator|.
name|cleanup
argument_list|()
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
operator|>
name|blobSize
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|serverSync
operator|.
name|close
argument_list|()
expr_stmt|;
name|clientSync
operator|.
name|close
argument_list|()
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
operator|>
name|blobSize
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
operator|>
name|blobSize
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

