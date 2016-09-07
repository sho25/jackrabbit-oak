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
name|benchmark
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|io
operator|.
name|File
operator|.
name|createTempFile
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
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|stats
operator|.
name|DefaultStatisticsProvider
import|;
end_import

begin_class
specifier|public
class|class
name|BenchmarkBase
block|{
specifier|static
specifier|final
name|int
name|port
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"standby.server.port"
argument_list|,
literal|52800
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|LOCALHOST
init|=
literal|"127.0.0.1"
decl_stmt|;
specifier|static
specifier|final
name|int
name|timeout
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"standby.test.timeout"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|File
name|directoryS
decl_stmt|;
name|FileStore
name|storeS
decl_stmt|;
name|ScheduledExecutorService
name|executorS
decl_stmt|;
name|File
name|directoryC
decl_stmt|;
name|FileStore
name|storeC
decl_stmt|;
name|ScheduledExecutorService
name|executorC
decl_stmt|;
specifier|public
name|void
name|setUpServerAndClient
parameter_list|()
throws|throws
name|Exception
block|{
name|directoryS
operator|=
name|createTmpTargetDir
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-Server"
argument_list|)
expr_stmt|;
name|executorS
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|storeS
operator|=
name|setupPrimary
argument_list|(
name|directoryS
argument_list|,
name|executorS
argument_list|)
expr_stmt|;
comment|// client
name|directoryC
operator|=
name|createTmpTargetDir
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-Client"
argument_list|)
expr_stmt|;
name|executorC
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|storeC
operator|=
name|setupSecondary
argument_list|(
name|directoryC
argument_list|,
name|executorC
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|closeServerAndClient
parameter_list|()
block|{
name|storeS
operator|.
name|close
argument_list|()
expr_stmt|;
name|storeC
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directoryS
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directoryC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
if|if
condition|(
name|executorS
operator|!=
literal|null
condition|)
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executorS
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|executorC
operator|!=
literal|null
condition|)
block|{
operator|new
name|ExecutorCloser
argument_list|(
name|executorC
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|FileStore
name|newFileStore
parameter_list|(
name|File
name|directory
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|directory
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
name|withNodeDeduplicationCacheSize
argument_list|(
literal|0
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
literal|0
argument_list|)
operator|.
name|withStringCacheSize
argument_list|(
literal|0
argument_list|)
operator|.
name|withTemplateCacheSize
argument_list|(
literal|0
argument_list|)
operator|.
name|withStatisticsProvider
argument_list|(
operator|new
name|DefaultStatisticsProvider
argument_list|(
name|executor
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|protected
name|FileStore
name|setupPrimary
parameter_list|(
name|File
name|directory
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newFileStore
argument_list|(
name|directory
argument_list|,
name|executor
argument_list|)
return|;
block|}
specifier|protected
name|FileStore
name|setupSecondary
parameter_list|(
name|File
name|directory
parameter_list|,
name|ScheduledExecutorService
name|executor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newFileStore
argument_list|(
name|directory
argument_list|,
name|executor
argument_list|)
return|;
block|}
specifier|public
name|StandbyClient
name|newStandbyClient
parameter_list|(
name|FileStore
name|store
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newStandbyClient
argument_list|(
name|store
argument_list|,
name|port
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|StandbyClient
name|newStandbyClient
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newStandbyClient
argument_list|(
name|store
argument_list|,
name|port
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|StandbyClient
name|newStandbyClient
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|secure
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|StandbyClient
argument_list|(
name|LOCALHOST
argument_list|,
name|port
argument_list|,
name|store
argument_list|,
name|secure
argument_list|,
name|timeout
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|File
name|createTmpTargetDir
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|createTempFile
argument_list|(
name|name
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|f
operator|.
name|mkdir
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
end_class

end_unit

