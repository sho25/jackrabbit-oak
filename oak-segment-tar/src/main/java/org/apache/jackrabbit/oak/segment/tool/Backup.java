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
name|tool
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
name|tool
operator|.
name|Utils
operator|.
name|newBasicReadOnlyBlobStore
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
name|tool
operator|.
name|Utils
operator|.
name|openReadOnlyFileStore
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
name|impl
operator|.
name|FileStoreBackupImpl
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
name|InvalidFileStoreVersionException
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
name|ReadOnlyFileStore
import|;
end_import

begin_comment
comment|/**  * Perform a backup of a segment store into a specified folder.  */
end_comment

begin_class
specifier|public
class|class
name|Backup
implements|implements
name|Runnable
block|{
comment|/**      * Create a builder for the {@link Backup} command.      *      * @return an instance of {@link Builder}.      */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Collect options for the {@link Backup} command.      */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|source
decl_stmt|;
specifier|private
name|File
name|target
decl_stmt|;
specifier|private
name|boolean
name|fakeBlobStore
init|=
name|FileStoreBackupImpl
operator|.
name|USE_FAKE_BLOBSTORE
decl_stmt|;
specifier|private
specifier|final
name|FileStoreBackup
name|fileStoreBackup
init|=
operator|new
name|FileStoreBackupImpl
argument_list|()
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The source folder of the backup. This parameter is required. The path          * should point to a valid segment store.          *          * @param source the path of the source folder of the backup.          * @return this builder.          */
specifier|public
name|Builder
name|withSource
parameter_list|(
name|File
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|checkNotNull
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * The target folder of the backup. This parameter is required. The path          * should point to an existing segment store or to an empty folder. If          * the folder doesn't exist, it will be created.          *          * @param target the path of the target folder of the backup.          * @return this builder.          */
specifier|public
name|Builder
name|withTarget
parameter_list|(
name|File
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|checkNotNull
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Simulate the existence of a file-based blob store. This parameter is          * not required and defaults to {@code false}.          *          * @param fakeBlobStore {@code true} if a file-based blob store should          *                      be simulated, {@code false} otherwise.          * @return this builder.          */
specifier|public
name|Builder
name|withFakeBlobStore
parameter_list|(
name|boolean
name|fakeBlobStore
parameter_list|)
block|{
name|this
operator|.
name|fakeBlobStore
operator|=
name|fakeBlobStore
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link Backup} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|Runnable
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
operator|new
name|Backup
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|source
decl_stmt|;
specifier|private
specifier|final
name|File
name|target
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|fakeBlobStore
decl_stmt|;
specifier|private
specifier|final
name|FileStoreBackup
name|fileStoreBackup
decl_stmt|;
specifier|private
name|Backup
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|builder
operator|.
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|builder
operator|.
name|target
expr_stmt|;
name|this
operator|.
name|fakeBlobStore
operator|=
name|builder
operator|.
name|fakeBlobStore
expr_stmt|;
name|this
operator|.
name|fileStoreBackup
operator|=
name|builder
operator|.
name|fileStoreBackup
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|ReadOnlyFileStore
name|fs
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|fileStoreBackup
operator|.
name|backup
argument_list|(
name|fs
operator|.
name|getReader
argument_list|()
argument_list|,
name|fs
operator|.
name|getRevisions
argument_list|()
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|ReadOnlyFileStore
name|newFileStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
if|if
condition|(
name|fakeBlobStore
condition|)
block|{
return|return
name|openReadOnlyFileStore
argument_list|(
name|source
argument_list|,
name|newBasicReadOnlyBlobStore
argument_list|()
argument_list|)
return|;
block|}
return|return
name|openReadOnlyFileStore
argument_list|(
name|source
argument_list|)
return|;
block|}
block|}
end_class

end_unit

