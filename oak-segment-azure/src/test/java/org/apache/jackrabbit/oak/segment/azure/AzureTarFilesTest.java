begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|blob
operator|.
name|CloudBlobContainer
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
name|spi
operator|.
name|monitor
operator|.
name|FileStoreMonitorAdapter
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
name|spi
operator|.
name|monitor
operator|.
name|IOMonitorAdapter
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
name|tar
operator|.
name|TarFiles
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
name|tar
operator|.
name|TarFilesTest
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
name|spi
operator|.
name|monitor
operator|.
name|RemoteStoreMonitorAdapter
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
name|ClassRule
import|;
end_import

begin_class
specifier|public
class|class
name|AzureTarFilesTest
extends|extends
name|TarFilesTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|AzuriteDockerRule
name|azurite
init|=
operator|new
name|AzuriteDockerRule
argument_list|()
decl_stmt|;
specifier|private
name|CloudBlobContainer
name|container
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|=
name|azurite
operator|.
name|getContainer
argument_list|(
literal|"oak-test"
argument_list|)
expr_stmt|;
name|tarFiles
operator|=
name|TarFiles
operator|.
name|builder
argument_list|()
operator|.
name|withDirectory
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
argument_list|)
operator|.
name|withTarRecovery
argument_list|(
parameter_list|(
name|id
parameter_list|,
name|data
parameter_list|,
name|recovery
parameter_list|)
lambda|->
block|{
comment|// Intentionally left blank
block|}
argument_list|)
operator|.
name|withIOMonitor
argument_list|(
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withFileStoreMonitor
argument_list|(
operator|new
name|FileStoreMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withRemoteStoreMonitor
argument_list|(
operator|new
name|RemoteStoreMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
name|MAX_FILE_SIZE
argument_list|)
operator|.
name|withPersistence
argument_list|(
operator|new
name|AzurePersistence
argument_list|(
name|container
operator|.
name|getDirectoryReference
argument_list|(
literal|"oak"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

