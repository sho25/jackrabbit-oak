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
name|StorageException
import|;
end_import

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
name|CloudBlockBlob
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
name|persistence
operator|.
name|RepositoryLock
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|server
operator|.
name|ExportException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidKeyException
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
name|Semaphore
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|AzureRepositoryLockTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AzureRepositoryLockTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|StorageException
throws|,
name|InvalidKeyException
throws|,
name|URISyntaxException
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailingLock
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|StorageException
block|{
name|CloudBlockBlob
name|blob
init|=
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/repo.lock"
argument_list|)
decl_stmt|;
operator|new
name|AzureRepositoryLock
argument_list|(
name|blob
argument_list|,
parameter_list|()
lambda|->
block|{}
argument_list|,
literal|0
argument_list|)
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|AzureRepositoryLock
argument_list|(
name|blob
argument_list|,
parameter_list|()
lambda|->
block|{}
argument_list|,
literal|0
argument_list|)
operator|.
name|lock
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"The second lock should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// it's fine
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWaitingLock
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
throws|,
name|StorageException
throws|,
name|InterruptedException
block|{
name|CloudBlockBlob
name|blob
init|=
name|container
operator|.
name|getBlockBlobReference
argument_list|(
literal|"oak/repo.lock"
argument_list|)
decl_stmt|;
name|Semaphore
name|s
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|RepositoryLock
name|lock
init|=
operator|new
name|AzureRepositoryLock
argument_list|(
name|blob
argument_list|,
parameter_list|()
lambda|->
block|{}
argument_list|,
literal|0
argument_list|)
operator|.
name|lock
argument_list|()
decl_stmt|;
name|s
operator|.
name|release
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't lock or unlock the repo"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|s
operator|.
name|acquire
argument_list|()
expr_stmt|;
operator|new
name|AzureRepositoryLock
argument_list|(
name|blob
argument_list|,
parameter_list|()
lambda|->
block|{}
argument_list|,
literal|10
argument_list|)
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

