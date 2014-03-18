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
name|backup
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
name|Preconditions
operator|.
name|checkNotNull
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
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|Future
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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

begin_comment
comment|/**  * Default implementation of {@link FileStoreBackupRestoreMBean} based on a file.  */
end_comment

begin_class
specifier|public
class|class
name|FileStoreBackupRestore
implements|implements
name|FileStoreBackupRestoreMBean
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
name|FileStoreBackupRestore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
specifier|private
name|Future
argument_list|<
name|Long
argument_list|>
name|backupOp
decl_stmt|;
specifier|private
name|long
name|backupEndTime
decl_stmt|;
specifier|private
name|Future
argument_list|<
name|Long
argument_list|>
name|restoreOp
decl_stmt|;
specifier|private
name|long
name|restoreEndTime
decl_stmt|;
comment|/**      * @param store  store to back up from or restore to      * @param file   file to back up to or restore from      * @param executorService  executor service for running the back up or restore operation      *                         in the background.      */
specifier|public
name|FileStoreBackupRestore
parameter_list|(
annotation|@
name|Nonnull
name|NodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|File
name|file
parameter_list|,
annotation|@
name|Nonnull
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|checkNotNull
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|checkNotNull
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|startBackup
parameter_list|()
block|{
if|if
condition|(
name|backupOp
operator|!=
literal|null
operator|&&
operator|!
name|backupOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|"Backup already running"
return|;
block|}
else|else
block|{
name|backupOp
operator|=
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|FileStoreBackup
operator|.
name|backup
argument_list|(
name|store
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|getBackupStatus
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|getBackupStatus
parameter_list|()
block|{
if|if
condition|(
name|backupOp
operator|==
literal|null
condition|)
block|{
return|return
literal|"Backup not started"
return|;
block|}
elseif|else
if|if
condition|(
name|backupOp
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
return|return
literal|"Backup cancelled"
return|;
block|}
elseif|else
if|if
condition|(
name|backupOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
try|try
block|{
return|return
literal|"Backup completed in "
operator|+
name|formatTime
argument_list|(
name|backupOp
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
literal|"Backup status unknown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Backup failed"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|"Backup failed: "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
literal|"Backup running"
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|startRestore
parameter_list|()
block|{
if|if
condition|(
name|restoreOp
operator|!=
literal|null
operator|&&
operator|!
name|restoreOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|"Restore already running"
return|;
block|}
else|else
block|{
name|restoreOp
operator|=
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|FileStoreRestore
operator|.
name|restore
argument_list|(
name|file
argument_list|,
name|store
argument_list|)
expr_stmt|;
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|t0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|getRestoreStatus
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|String
name|getRestoreStatus
parameter_list|()
block|{
if|if
condition|(
name|restoreOp
operator|==
literal|null
condition|)
block|{
return|return
literal|"Restore not started"
return|;
block|}
elseif|else
if|if
condition|(
name|restoreOp
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
return|return
literal|"Restore cancelled"
return|;
block|}
elseif|else
if|if
condition|(
name|restoreOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
try|try
block|{
return|return
literal|"Restore completed in "
operator|+
name|formatTime
argument_list|(
name|restoreOp
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
literal|"Restore status unknown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Restore failed"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|"Restore failed: "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
literal|"Restore running"
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|formatTime
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
return|return
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|convert
argument_list|(
name|nanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|+
literal|" minutes"
return|;
block|}
block|}
end_class

end_unit

