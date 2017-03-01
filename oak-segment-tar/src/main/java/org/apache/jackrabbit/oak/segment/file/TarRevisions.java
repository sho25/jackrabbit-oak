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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
operator|.
name|propagate
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
name|Throwables
operator|.
name|propagateIfInstanceOf
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Long
operator|.
name|MAX_VALUE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|DAYS
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
name|TimeUnit
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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

begin_comment
comment|/**  * This implementation of {@code Revisions} is backed by a  * {@link #JOURNAL_FILE_NAME journal} file where the current head is persisted  * by calling {@link #flush(Callable)}.  *<p>  * The {@link #setHead(Function, Option...)} method supports a timeout  * {@link Option}, which can be retrieved through factory methods of this class.  *<p>  * Instance of this class must be {@link #bind(SegmentStore, SegmentIdProvider, Supplier)} bound} to  * a {@code SegmentStore} otherwise its method throw {@code IllegalStateException}s.  */
end_comment

begin_class
specifier|public
class|class
name|TarRevisions
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
name|TarRevisions
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
comment|/**      * The lock protecting {@link #journalFile}.      */
specifier|private
specifier|final
name|Lock
name|journalFileLock
init|=
operator|new
name|ReentrantLock
argument_list|()
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
comment|/**      * The journal file. It is protected by {@link #journalFileLock}. It becomes      * {@code null} after it's closed.      */
specifier|private
name|RandomAccessFile
name|journalFile
decl_stmt|;
comment|/**      * The persisted head of the root journal, used to determine whether the      * latest {@link #head} value should be written to the disk.      */
annotation|@
name|Nonnull
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RecordId
argument_list|>
name|persistedHead
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|ReadWriteLock
name|rwLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
class|class
name|TimeOutOption
implements|implements
name|Option
block|{
specifier|private
specifier|final
name|long
name|time
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|TimeUnit
name|unit
decl_stmt|;
name|TimeOutOption
parameter_list|(
name|long
name|time
parameter_list|,
annotation|@
name|Nonnull
name|TimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|TimeOutOption
name|from
parameter_list|(
annotation|@
name|CheckForNull
name|Option
name|option
parameter_list|)
block|{
if|if
condition|(
name|option
operator|instanceof
name|TimeOutOption
condition|)
block|{
return|return
operator|(
name|TimeOutOption
operator|)
name|option
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid option "
operator|+
name|option
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Option to cause set head calls to be expedited. That is, cause them to skip the queue      * of any other callers waiting to complete that don't have this option specified.      */
specifier|public
specifier|static
specifier|final
name|Option
name|EXPEDITE_OPTION
init|=
operator|new
name|Option
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Expedite Option"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Timeout option approximating no time out ({@code Long.MAX_VALUE} days).      */
specifier|public
specifier|static
specifier|final
name|Option
name|INFINITY
init|=
operator|new
name|TimeOutOption
argument_list|(
name|MAX_VALUE
argument_list|,
name|DAYS
argument_list|)
decl_stmt|;
comment|/**      * Factory method for creating a timeout option.      */
specifier|public
specifier|static
name|Option
name|timeout
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|TimeOutOption
argument_list|(
name|time
argument_list|,
name|unit
argument_list|)
return|;
block|}
comment|/**      * Create a new instance placing the journal log file into the passed      * {@code directory}.      * @param directory     directory of the journal file      * @throws IOException      */
specifier|public
name|TarRevisions
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
literal|"rw"
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
name|this
operator|.
name|persistedHead
operator|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Bind this instance to a store.      * @param store              store to bind to      * @param idProvider         {@code SegmentIdProvider} of the {@code store}      * @param writeInitialNode   provider for the initial node in case the journal is empty.      * @throws IOException      */
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
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|RecordId
argument_list|>
name|writeInitialNode
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
name|head
operator|.
name|set
argument_list|(
name|writeInitialNode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|persistedHead
operator|.
name|set
argument_list|(
name|persistedId
argument_list|)
expr_stmt|;
name|head
operator|.
name|set
argument_list|(
name|persistedId
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Flush the id of the current head to the journal after a call to      * {@code persisted}. This method does nothing and returns immediately if      * called concurrently and a call is already in progress.      * @param persisted     call back for upstream dependencies to ensure      *                      the current head state is actually persisted before      *                      its id is written to the head state.      * @throws IOException      */
specifier|public
name|void
name|flush
parameter_list|(
annotation|@
name|Nonnull
name|Callable
argument_list|<
name|Void
argument_list|>
name|persisted
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
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|journalFileLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|journalFile
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|doFlush
argument_list|(
name|persisted
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalFileLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|doFlush
parameter_list|(
name|Callable
argument_list|<
name|Void
argument_list|>
name|persisted
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|RecordId
name|before
init|=
name|persistedHead
operator|.
name|get
argument_list|()
decl_stmt|;
name|RecordId
name|after
init|=
name|getHead
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|persisted
operator|.
name|call
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"TarMK journal update {} -> {}"
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|journalFile
operator|.
name|writeBytes
argument_list|(
name|after
operator|.
name|toString10
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
name|persistedHead
operator|.
name|set
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|propagateIfInstanceOf
argument_list|(
name|e
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|propagate
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * This implementation blocks if a concurrent call to      * {@link #setHead(Function, Option...)} is already in      * progress.      *      * @param options   zero or one expedite option for expediting this call      * @throws IllegalArgumentException  on any non recognised {@code option}.      * @see #EXPEDITE_OPTION      */
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
comment|// If the expedite option was specified we acquire the write lock instead of the read lock.
comment|// This will cause this thread to get the lock before all threads currently waiting to
comment|// enter the read lock. See also the class comment of ReadWriteLock.
name|Lock
name|lock
init|=
name|isExpedited
argument_list|(
name|options
argument_list|)
condition|?
name|rwLock
operator|.
name|writeLock
argument_list|()
else|:
name|rwLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
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
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This implementation blocks if a concurrent call is already in progress.      * @param newHead  function mapping an record id to the record id to which      *                 the current head id should be set. If it returns      *                 {@code null} the head remains unchanged and {@code setHead}      *                 returns {@code false}.       * @param options  zero or one timeout options specifying how long to block      * @throws InterruptedException      * @throws IllegalArgumentException  on any non recognised {@code option}.      * @see #timeout(long, TimeUnit)      * @see #INFINITY      */
annotation|@
name|Override
specifier|public
name|boolean
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
name|checkBound
argument_list|()
expr_stmt|;
name|TimeOutOption
name|timeout
init|=
name|getTimeout
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|tryLock
argument_list|(
name|timeout
operator|.
name|time
argument_list|,
name|timeout
operator|.
name|unit
argument_list|)
condition|)
block|{
try|try
block|{
name|RecordId
name|after
init|=
name|newHead
operator|.
name|apply
argument_list|(
name|getHead
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|after
operator|!=
literal|null
condition|)
block|{
name|head
operator|.
name|set
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isExpedited
parameter_list|(
name|Option
index|[]
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|options
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|options
index|[
literal|0
index|]
operator|==
name|EXPEDITE_OPTION
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Expected zero or one options, got "
operator|+
name|options
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|TimeOutOption
name|getTimeout
parameter_list|(
annotation|@
name|Nonnull
name|Option
index|[]
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|TimeOutOption
operator|.
name|from
argument_list|(
name|INFINITY
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|options
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|TimeOutOption
operator|.
name|from
argument_list|(
name|options
index|[
literal|0
index|]
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Expected zero or one options, got "
operator|+
name|options
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
comment|/**      * Close the underlying journal file.      * @throws IOException      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|journalFileLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|journalFile
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|journalFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|journalFile
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|journalFileLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

