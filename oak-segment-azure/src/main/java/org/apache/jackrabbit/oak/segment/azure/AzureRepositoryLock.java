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
name|AccessCondition
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
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|TimeUnit
import|;
end_import

begin_class
specifier|public
class|class
name|AzureRepositoryLock
implements|implements
name|RepositoryLock
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
name|AzureRepositoryLock
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|TIMEOUT_SEC
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.segment.azure.lock.timeout"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|INTERVAL
init|=
literal|60
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|shutdownHook
decl_stmt|;
specifier|private
specifier|final
name|CloudBlockBlob
name|blob
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|executor
decl_stmt|;
specifier|private
specifier|final
name|int
name|timeoutSec
decl_stmt|;
specifier|private
name|String
name|leaseId
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|doUpdate
decl_stmt|;
specifier|public
name|AzureRepositoryLock
parameter_list|(
name|CloudBlockBlob
name|blob
parameter_list|,
name|Runnable
name|shutdownHook
parameter_list|)
block|{
name|this
argument_list|(
name|blob
argument_list|,
name|shutdownHook
argument_list|,
name|TIMEOUT_SEC
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AzureRepositoryLock
parameter_list|(
name|CloudBlockBlob
name|blob
parameter_list|,
name|Runnable
name|shutdownHook
parameter_list|,
name|int
name|timeoutSec
parameter_list|)
block|{
name|this
operator|.
name|shutdownHook
operator|=
name|shutdownHook
expr_stmt|;
name|this
operator|.
name|blob
operator|=
name|blob
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
expr_stmt|;
name|this
operator|.
name|timeoutSec
operator|=
name|timeoutSec
expr_stmt|;
block|}
specifier|public
name|AzureRepositoryLock
name|lock
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Exception
name|ex
init|=
literal|null
decl_stmt|;
do|do
block|{
try|try
block|{
name|blob
operator|.
name|openOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|leaseId
operator|=
name|blob
operator|.
name|acquireLease
argument_list|(
name|INTERVAL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Acquired lease {}"
argument_list|,
name|leaseId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Can't acquire the lease. Retrying every 1s. Timeout is set to {}s."
argument_list|,
name|timeoutSec
argument_list|)
expr_stmt|;
block|}
name|ex
operator|=
name|e
expr_stmt|;
if|if
condition|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|/
literal|1000
operator|<
name|timeoutSec
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
do|while
condition|(
name|leaseId
operator|==
literal|null
condition|)
do|;
if|if
condition|(
name|leaseId
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't acquire the lease in {}s."
argument_list|,
name|timeoutSec
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
else|else
block|{
name|executor
operator|.
name|submit
argument_list|(
name|this
operator|::
name|refreshLease
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
specifier|private
name|void
name|refreshLease
parameter_list|()
block|{
name|doUpdate
operator|=
literal|true
expr_stmt|;
name|long
name|lastUpdate
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|doUpdate
condition|)
block|{
try|try
block|{
name|long
name|timeSinceLastUpdate
init|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastUpdate
operator|)
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|timeSinceLastUpdate
operator|>
name|INTERVAL
operator|/
literal|2
condition|)
block|{
name|blob
operator|.
name|renewLease
argument_list|(
name|AccessCondition
operator|.
name|generateLeaseCondition
argument_list|(
name|leaseId
argument_list|)
argument_list|)
expr_stmt|;
name|lastUpdate
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can't renew the lease"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|shutdownHook
operator|.
name|run
argument_list|()
expr_stmt|;
name|doUpdate
operator|=
literal|false
expr_stmt|;
return|return;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Interrupted the lease renewal loop"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unlock
parameter_list|()
throws|throws
name|IOException
block|{
name|doUpdate
operator|=
literal|false
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseLease
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|releaseLease
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|blob
operator|.
name|releaseLease
argument_list|(
name|AccessCondition
operator|.
name|generateLeaseCondition
argument_list|(
name|leaseId
argument_list|)
argument_list|)
expr_stmt|;
name|blob
operator|.
name|delete
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Released lease {}"
argument_list|,
name|leaseId
argument_list|)
expr_stmt|;
name|leaseId
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

