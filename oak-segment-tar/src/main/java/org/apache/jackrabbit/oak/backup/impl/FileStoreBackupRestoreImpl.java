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
name|backup
operator|.
name|impl
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
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|nanoTime
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|formatTime
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|done
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
name|commons
operator|.
name|jmx
operator|.
name|ManagementOperation
operator|.
name|newManagementOperation
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
name|Executor
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|jmx
operator|.
name|FileStoreBackupRestoreMBean
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
name|backup
operator|.
name|FileStoreBackup
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
name|backup
operator|.
name|FileStoreRestore
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
name|jmx
operator|.
name|ManagementOperation
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
name|Revisions
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
name|segment
operator|.
name|SegmentReader
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link FileStoreBackupRestoreMBean} based on a  * file.  */
end_comment

begin_class
specifier|public
class|class
name|FileStoreBackupRestoreImpl
implements|implements
name|FileStoreBackupRestoreMBean
block|{
specifier|private
specifier|static
specifier|final
name|String
name|BACKUP_OP_NAME
init|=
literal|"Backup"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RESTORE_OP_NAME
init|=
literal|"Restore"
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
name|ManagementOperation
argument_list|<
name|String
argument_list|>
name|backupOp
init|=
name|done
argument_list|(
name|BACKUP_OP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
name|ManagementOperation
argument_list|<
name|String
argument_list|>
name|restoreOp
init|=
name|done
argument_list|(
name|RESTORE_OP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FileStoreBackup
name|fileStoreBackup
decl_stmt|;
specifier|private
specifier|final
name|FileStoreRestore
name|fileStoreRestore
decl_stmt|;
comment|/**      * @param store    store to back up from or restore to      * @param file     file to back up to or restore from      * @param executor executor for running the back up or restore operation      */
specifier|public
name|FileStoreBackupRestoreImpl
parameter_list|(
annotation|@
name|Nonnull
name|SegmentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|Revisions
name|revisions
parameter_list|,
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|File
name|file
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
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
name|revisions
operator|=
name|checkNotNull
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
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
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileStoreBackup
operator|=
operator|new
name|FileStoreBackupImpl
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileStoreRestore
operator|=
operator|new
name|FileStoreRestoreImpl
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|CompositeData
name|startBackup
parameter_list|()
block|{
if|if
condition|(
name|backupOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|backupOp
operator|=
name|newManagementOperation
argument_list|(
literal|"Backup"
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|fileStoreBackup
operator|.
name|backup
argument_list|(
name|reader
argument_list|,
name|revisions
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
literal|"Backup completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|backupOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getBackupStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|CompositeData
name|getBackupStatus
parameter_list|()
block|{
return|return
name|backupOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|CompositeData
name|startRestore
parameter_list|()
block|{
if|if
condition|(
name|restoreOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|restoreOp
operator|=
name|newManagementOperation
argument_list|(
literal|"Restore"
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|fileStoreRestore
operator|.
name|restore
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
literal|"Restore completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|restoreOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getRestoreStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|CompositeData
name|getRestoreStatus
parameter_list|()
block|{
return|return
name|restoreOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|store
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
block|}
end_class

end_unit

