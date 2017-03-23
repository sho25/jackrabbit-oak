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
name|compaction
operator|.
name|SegmentGCOptions
operator|.
name|defaultGCOptions
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
name|io
operator|.
name|RandomAccessFile
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
name|JournalReader
import|;
end_import

begin_comment
comment|/**  * Perform an offline compaction of an existing segment store.  */
end_comment

begin_class
specifier|public
class|class
name|Compact
implements|implements
name|Runnable
block|{
comment|/**      * Create a builder for the {@link Compact} command.      *      * @return an instance of {@link Builder}.      */
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
comment|/**      * Collect options for the {@link Compact} command.      */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|path
decl_stmt|;
specifier|private
name|boolean
name|force
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The path to an existing segment store. This parameter is required.          *          * @param path the path to an existing segment store.          * @return this builder.          */
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Set whether or not to force compact concurrent commits on top of          * already compacted commits after the maximum number of retries has          * been reached. Force committing tries to exclusively write lock the          * node store.          *          * @param force {@code true} to force an exclusive commit of the          *              compacted state, {@code false} otherwise.          * @return this builder.          */
specifier|public
name|Builder
name|withForce
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link Compact} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|Runnable
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
operator|new
name|Compact
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
name|Compact
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
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
block|{
name|compact
argument_list|()
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
name|void
name|compact
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|store
operator|.
name|compact
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    -> cleaning up"
argument_list|)
expr_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|store
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|File
name|journal
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"journal.log"
argument_list|)
decl_stmt|;
name|String
name|head
decl_stmt|;
try|try
init|(
name|JournalReader
name|journalReader
init|=
operator|new
name|JournalReader
argument_list|(
name|journal
argument_list|)
init|)
block|{
name|head
operator|=
name|journalReader
operator|.
name|next
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|+
literal|" root "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
block|}
try|try
init|(
name|RandomAccessFile
name|journalFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|journal
argument_list|,
literal|"rw"
argument_list|)
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    -> writing new "
operator|+
name|journal
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|head
argument_list|)
expr_stmt|;
name|journalFile
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|journalFile
operator|.
name|writeBytes
argument_list|(
name|head
argument_list|)
expr_stmt|;
name|journalFile
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|FileStore
name|newFileStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|path
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
operator|.
name|withGCOptions
argument_list|(
name|defaultGCOptions
argument_list|()
operator|.
name|setOffline
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

