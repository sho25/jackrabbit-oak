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
name|file
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|util
operator|.
name|UUID
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
name|Executors
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
name|ScheduledExecutorService
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
name|concurrent
operator|.
name|ExecutorCloser
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
name|stats
operator|.
name|DefaultStatisticsProvider
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
name|stats
operator|.
name|StatisticsProvider
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
name|FileStoreStatsTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|segmentFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|shutDown
parameter_list|()
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executor
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|initCall
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|store
init|=
name|mock
argument_list|(
name|FileStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|StatisticsProvider
name|statsProvider
init|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
decl_stmt|;
name|FileStoreStats
name|stats
init|=
operator|new
name|FileStoreStats
argument_list|(
name|statsProvider
argument_list|,
name|store
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|stats
operator|.
name|getApproximateSize
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|written
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1500
argument_list|,
name|stats
operator|.
name|getApproximateSize
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|reclaimed
argument_list|(
literal|250
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1250
argument_list|,
name|stats
operator|.
name|getApproximateSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|getTarFileCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tarWriterIntegration
parameter_list|()
throws|throws
name|Exception
block|{
name|StatisticsProvider
name|statsProvider
init|=
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
decl_stmt|;
name|FileStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|segmentFolder
operator|.
name|newFolder
argument_list|()
argument_list|)
operator|.
name|withStatisticsProvider
argument_list|(
name|statsProvider
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|FileStoreStats
name|stats
init|=
operator|new
name|FileStoreStats
argument_list|(
name|statsProvider
argument_list|,
name|store
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|long
name|initialSize
init|=
name|stats
operator|.
name|getApproximateSize
argument_list|()
decl_stmt|;
name|UUID
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|id
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
operator|&
operator|(
operator|-
literal|1
operator|>>>
literal|4
operator|)
decl_stmt|;
comment|// OAK-1672
name|byte
index|[]
name|data
init|=
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|File
name|file
init|=
name|segmentFolder
operator|.
name|newFile
argument_list|()
decl_stmt|;
try|try
init|(
name|TarWriter
name|writer
init|=
operator|new
name|TarWriter
argument_list|(
name|file
argument_list|,
name|stats
argument_list|)
init|)
block|{
name|writer
operator|.
name|writeEntry
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|stats
operator|.
name|getApproximateSize
argument_list|()
operator|-
name|initialSize
argument_list|,
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

