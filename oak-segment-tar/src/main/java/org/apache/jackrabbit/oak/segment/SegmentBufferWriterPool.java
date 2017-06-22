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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|currentThread
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Supplier
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
name|util
operator|.
name|concurrent
operator|.
name|Monitor
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
name|util
operator|.
name|concurrent
operator|.
name|Monitor
operator|.
name|Guard
import|;
end_import

begin_comment
comment|/**  * This {@link WriteOperationHandler} uses a pool of {@link SegmentBufferWriter}s,  * which it passes to its {@link #execute(WriteOperation) execute} method.  *<p>  * Instances of this class are thread safe. See also the class comment of  * {@link DefaultSegmentWriter}.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentBufferWriterPool
implements|implements
name|WriteOperationHandler
block|{
comment|/**      * Monitor protecting the state of this pool. Neither of {@link #writers},      * {@link #borrowed} and {@link #disposed} must be modified without owning      * this monitor.      */
specifier|private
specifier|final
name|Monitor
name|poolMonitor
init|=
operator|new
name|Monitor
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/**      * Pool of current writers that are not in use      */
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|SegmentBufferWriter
argument_list|>
name|writers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**      * Writers that are currently in use      */
specifier|private
specifier|final
name|Set
argument_list|<
name|SegmentBufferWriter
argument_list|>
name|borrowed
init|=
name|newHashSet
argument_list|()
decl_stmt|;
comment|/**      * Retired writers that have not yet been flushed      */
specifier|private
specifier|final
name|Set
argument_list|<
name|SegmentBufferWriter
argument_list|>
name|disposed
init|=
name|newHashSet
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentIdProvider
name|idProvider
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Integer
argument_list|>
name|gcGeneration
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|String
name|wid
decl_stmt|;
specifier|private
name|short
name|writerId
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|SegmentBufferWriterPool
parameter_list|(
annotation|@
name|Nonnull
name|SegmentIdProvider
name|idProvider
parameter_list|,
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|String
name|wid
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|Integer
argument_list|>
name|gcGeneration
parameter_list|)
block|{
name|this
operator|.
name|idProvider
operator|=
name|checkNotNull
argument_list|(
name|idProvider
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
name|wid
operator|=
name|checkNotNull
argument_list|(
name|wid
argument_list|)
expr_stmt|;
name|this
operator|.
name|gcGeneration
operator|=
name|checkNotNull
argument_list|(
name|gcGeneration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
annotation|@
name|Nonnull
name|WriteOperation
name|writeOperation
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentBufferWriter
name|writer
init|=
name|borrowWriter
argument_list|(
name|currentThread
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|writeOperation
operator|.
name|execute
argument_list|(
name|writer
argument_list|)
return|;
block|}
finally|finally
block|{
name|returnWriter
argument_list|(
name|currentThread
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|(
annotation|@
name|Nonnull
name|SegmentStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SegmentBufferWriter
argument_list|>
name|toFlush
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SegmentBufferWriter
argument_list|>
name|toReturn
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|poolMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Collect all writers that are not currently in use and clear
comment|// the list so they won't get re-used anymore.
name|toFlush
operator|.
name|addAll
argument_list|(
name|writers
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|writers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Collect all borrowed writers, which we need to wait for.
comment|// Clear the list so they will get disposed once returned.
name|toReturn
operator|.
name|addAll
argument_list|(
name|borrowed
argument_list|)
expr_stmt|;
name|borrowed
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|poolMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
comment|// Wait for the return of the borrowed writers. This is the
comment|// case once all of them appear in the disposed set.
if|if
condition|(
name|safeEnterWhen
argument_list|(
name|poolMonitor
argument_list|,
name|allReturned
argument_list|(
name|toReturn
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
comment|// Collect all disposed writers and clear the list to mark them
comment|// as flushed.
name|toFlush
operator|.
name|addAll
argument_list|(
name|toReturn
argument_list|)
expr_stmt|;
name|disposed
operator|.
name|removeAll
argument_list|(
name|toReturn
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|poolMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Call flush from outside the pool monitor to avoid potential
comment|// deadlocks of that method calling SegmentStore.writeSegment
for|for
control|(
name|SegmentBufferWriter
name|writer
range|:
name|toFlush
control|)
block|{
name|writer
operator|.
name|flush
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Create a {@code Guard} that is satisfied if and only if {@link #disposed}      * contains all items in {@code toReturn}      */
annotation|@
name|Nonnull
specifier|private
name|Guard
name|allReturned
parameter_list|(
specifier|final
name|List
argument_list|<
name|SegmentBufferWriter
argument_list|>
name|toReturn
parameter_list|)
block|{
return|return
operator|new
name|Guard
argument_list|(
name|poolMonitor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisfied
parameter_list|()
block|{
return|return
name|disposed
operator|.
name|containsAll
argument_list|(
name|toReturn
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Same as {@code monitor.enterWhen(guard)} but copes with that pesky {@code      * InterruptedException} by catching it and setting this thread's      * interrupted flag.      */
specifier|private
specifier|static
name|boolean
name|safeEnterWhen
parameter_list|(
name|Monitor
name|monitor
parameter_list|,
name|Guard
name|guard
parameter_list|)
block|{
try|try
block|{
name|monitor
operator|.
name|enterWhen
argument_list|(
name|guard
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
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
literal|false
return|;
block|}
block|}
comment|/**      * Return a writer from the pool by its {@code key}. This method may return      * a fresh writer at any time. Callers need to return a writer before      * borrowing it again. Failing to do so leads to undefined behaviour.      */
specifier|private
name|SegmentBufferWriter
name|borrowWriter
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|poolMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
name|SegmentBufferWriter
name|writer
init|=
name|writers
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|new
name|SegmentBufferWriter
argument_list|(
name|idProvider
argument_list|,
name|reader
argument_list|,
name|getWriterId
argument_list|(
name|wid
argument_list|)
argument_list|,
name|gcGeneration
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|writer
operator|.
name|getGeneration
argument_list|()
operator|!=
name|gcGeneration
operator|.
name|get
argument_list|()
condition|)
block|{
name|disposed
operator|.
name|add
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SegmentBufferWriter
argument_list|(
name|idProvider
argument_list|,
name|reader
argument_list|,
name|getWriterId
argument_list|(
name|wid
argument_list|)
argument_list|,
name|gcGeneration
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|borrowed
operator|.
name|add
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
finally|finally
block|{
name|poolMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Return a writer to the pool using the {@code key} that was used to borrow      * it.      */
specifier|private
name|void
name|returnWriter
parameter_list|(
name|Object
name|key
parameter_list|,
name|SegmentBufferWriter
name|writer
parameter_list|)
block|{
name|poolMonitor
operator|.
name|enter
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|borrowed
operator|.
name|remove
argument_list|(
name|writer
argument_list|)
condition|)
block|{
name|checkState
argument_list|(
name|writers
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|writer
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Defer flush this writer as it was borrowed while flush() was called.
name|disposed
operator|.
name|add
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|poolMonitor
operator|.
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getWriterId
parameter_list|(
name|String
name|wid
parameter_list|)
block|{
if|if
condition|(
operator|++
name|writerId
operator|>
literal|9999
condition|)
block|{
name|writerId
operator|=
literal|0
expr_stmt|;
block|}
comment|// Manual padding seems to be fastest here
if|if
condition|(
name|writerId
operator|<
literal|10
condition|)
block|{
return|return
name|wid
operator|+
literal|".000"
operator|+
name|writerId
return|;
block|}
elseif|else
if|if
condition|(
name|writerId
operator|<
literal|100
condition|)
block|{
return|return
name|wid
operator|+
literal|".00"
operator|+
name|writerId
return|;
block|}
elseif|else
if|if
condition|(
name|writerId
operator|<
literal|1000
condition|)
block|{
return|return
name|wid
operator|+
literal|".0"
operator|+
name|writerId
return|;
block|}
else|else
block|{
return|return
name|wid
operator|+
literal|"."
operator|+
name|writerId
return|;
block|}
block|}
block|}
end_class

end_unit

