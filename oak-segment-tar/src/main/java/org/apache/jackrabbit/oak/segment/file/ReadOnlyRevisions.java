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
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

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
name|checkState
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
name|FileStoreUtil
operator|.
name|findPersistedRecordId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|RecordId
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
name|SegmentIdProvider
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
name|SegmentStore
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

begin_class
specifier|public
class|class
name|ReadOnlyRevisions
implements|implements
name|Revisions
implements|,
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ReadOnlyRevisions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOURNAL_FILE_NAME
init|=
literal|"journal.log"
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
name|head
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|RandomAccessFile
name|journalFile
decl_stmt|;
specifier|public
name|ReadOnlyRevisions
parameter_list|(
annotation|@
name|Nonnull
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|checkNotNull
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|this
operator|.
name|journalFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|JOURNAL_FILE_NAME
argument_list|)
argument_list|,
literal|"r"
argument_list|)
expr_stmt|;
name|this
operator|.
name|journalFile
operator|.
name|seek
argument_list|(
name|journalFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Bind this instance to a store.      *       * @param store store to bind to      * @param idProvider  {@code SegmentIdProvider} of the {@code store}      * @throws IOException      */
specifier|synchronized
name|void
name|bind
parameter_list|(
annotation|@
name|Nonnull
name|SegmentStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|SegmentIdProvider
name|idProvider
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|head
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|RecordId
name|persistedId
init|=
name|findPersistedRecordId
argument_list|(
name|store
argument_list|,
name|idProvider
argument_list|,
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|JOURNAL_FILE_NAME
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|persistedId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot start readonly store from empty journal"
argument_list|)
throw|;
block|}
name|head
operator|.
name|set
argument_list|(
name|persistedId
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkBound
parameter_list|()
block|{
name|checkState
argument_list|(
name|head
operator|.
name|get
argument_list|()
operator|!=
literal|null
argument_list|,
literal|"Revisions not bound to a store"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordId
name|getHead
parameter_list|()
block|{
name|checkBound
argument_list|()
expr_stmt|;
return|return
name|head
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setHead
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|head
parameter_list|,
annotation|@
name|Nonnull
name|Option
modifier|...
name|options
parameter_list|)
block|{
name|checkBound
argument_list|()
expr_stmt|;
name|RecordId
name|id
init|=
name|this
operator|.
name|head
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|id
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
operator|&&
name|this
operator|.
name|head
operator|.
name|compareAndSet
argument_list|(
name|id
argument_list|,
name|head
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RecordId
name|setHead
parameter_list|(
annotation|@
name|Nonnull
name|Function
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|newHead
parameter_list|,
annotation|@
name|Nonnull
name|Option
modifier|...
name|options
parameter_list|)
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ReadOnly Revisions"
argument_list|)
throw|;
block|}
comment|/**      * Close the underlying journal file.      *       * @throws IOException      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|journalFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

