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
name|backup
operator|.
name|impl
operator|.
name|FileStoreRestoreImpl
import|;
end_import

begin_comment
comment|/**  * Restore a backup of a segment store into an existing segment store.  */
end_comment

begin_class
specifier|public
class|class
name|Restore
block|{
comment|/**      * Create a builder for the {@link Restore} command.      *      * @return an instance of {@link Builder}.      */
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
comment|/**      * Collect options for the {@link Restore} command.      */
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
specifier|final
name|FileStoreRestore
name|fileStoreRestore
init|=
operator|new
name|FileStoreRestoreImpl
argument_list|()
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The source path of the restore. This parameter is mandatory.          *          * @param source the source path of the restore.          * @return this builder.          */
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
comment|/**          * The target of the restore. This parameter is mandatory.          *          * @param target the target of the restore.          * @return this builder.          */
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
comment|/**          * Create an executable version of the {@link Restore} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|Restore
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
name|Restore
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
name|FileStoreRestore
name|fileStoreRestore
decl_stmt|;
specifier|private
name|Restore
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
name|fileStoreRestore
operator|=
name|builder
operator|.
name|fileStoreRestore
expr_stmt|;
block|}
specifier|public
name|int
name|run
parameter_list|()
block|{
try|try
block|{
name|fileStoreRestore
operator|.
name|restore
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
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
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

