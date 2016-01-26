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
name|plugins
operator|.
name|document
operator|.
name|locks
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|AtomicLong
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
name|Condition
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|collect
operator|.
name|Iterables
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
name|Striped
import|;
end_import

begin_class
specifier|public
class|class
name|TreeNodeDocumentLocks
implements|implements
name|NodeDocumentLocks
block|{
comment|/**      * Locks to ensure cache consistency on reads, writes and invalidation.      */
specifier|private
specifier|final
name|Striped
argument_list|<
name|Lock
argument_list|>
name|locks
init|=
name|Striped
operator|.
name|lock
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
comment|/**      * ReadWriteLocks to synchronize cache access when child documents are      * requested from MongoDB and put into the cache. Accessing a single      * document in the cache will acquire a read (shared) lock for the parent      * key in addition to the lock (from {@link #locks}) for the individual      * document. Reading multiple sibling documents will acquire a write      * (exclusive) lock for the parent key. See OAK-1897.      */
specifier|private
specifier|final
name|Striped
argument_list|<
name|ReadWriteLock
argument_list|>
name|parentLocks
init|=
name|Striped
operator|.
name|readWriteLock
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
comment|/**      * Counts how many times {@link TreeLock}s were acquired.      */
specifier|private
specifier|volatile
name|AtomicLong
name|lockAcquisitionCounter
decl_stmt|;
comment|/**      * Acquires a lock for the given key. The returned tree lock will also hold      * a shared lock on the parent key.      *      * @param key a key.      * @return the acquired lock for the given key.      */
annotation|@
name|Override
specifier|public
name|TreeLock
name|acquire
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|lockAcquisitionCounter
operator|!=
literal|null
condition|)
block|{
name|lockAcquisitionCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|TreeLock
name|lock
init|=
name|TreeLock
operator|.
name|shared
argument_list|(
name|parentLocks
operator|.
name|get
argument_list|(
name|getParentId
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|,
name|locks
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|lock
return|;
block|}
comment|/**      * This implementation creates two sequences of locks (for the keys and for      * the their parents) using {@link #locks} and {@link #parentLocks}. Then      * both sequences are zipped into pairs (parentLock, lock) and passed to the      * {@link TreeLock#shared(ReadWriteLock, Lock)}. After that all tree locks      * are acquired.      *<p>      * Since we only acquire a parentLock.read, there's no danger of      * deadlock caused by interleaving locks from two different stripes by two      * threads. The only place where the parentLock.write is acquired is the      * {@link #acquireExclusive(String)} and that method doesn't acquire locks in bulk.      */
annotation|@
name|Override
specifier|public
name|Lock
name|acquire
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
block|{
if|if
condition|(
name|lockAcquisitionCounter
operator|!=
literal|null
condition|)
block|{
name|lockAcquisitionCounter
operator|.
name|addAndGet
argument_list|(
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Iterable
argument_list|<
name|String
argument_list|>
name|parentKeys
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|keys
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|keys
parameter_list|)
block|{
return|return
name|getParentId
argument_list|(
name|keys
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Lock
argument_list|>
name|lockIt
init|=
name|locks
operator|.
name|bulkGet
argument_list|(
name|keys
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ReadWriteLock
argument_list|>
name|parentLockIt
init|=
name|parentLocks
operator|.
name|bulkGet
argument_list|(
name|parentKeys
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Lock
argument_list|>
name|acquired
init|=
operator|new
name|ArrayList
argument_list|<
name|Lock
argument_list|>
argument_list|(
name|keys
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|lockIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|acquired
operator|.
name|add
argument_list|(
name|TreeLock
operator|.
name|shared
argument_list|(
name|parentLockIt
operator|.
name|next
argument_list|()
argument_list|,
name|lockIt
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Lock
name|lock
init|=
operator|new
name|BulkLock
argument_list|(
name|acquired
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|lock
return|;
block|}
comment|/**      * Acquires an exclusive lock on the given parent key. Use this method to      * block cache access for child keys of the given parent key.      *      * @param parentKey the parent key.      * @return the acquired lock for the given parent key.      */
specifier|public
name|TreeLock
name|acquireExclusive
parameter_list|(
name|String
name|parentKey
parameter_list|)
block|{
if|if
condition|(
name|lockAcquisitionCounter
operator|!=
literal|null
condition|)
block|{
name|lockAcquisitionCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|TreeLock
name|lock
init|=
name|TreeLock
operator|.
name|exclusive
argument_list|(
name|parentLocks
operator|.
name|get
argument_list|(
name|parentKey
argument_list|)
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|lock
return|;
block|}
comment|/**      * Returns the parent id for the given id. An empty String is returned if      * the given value is the id of the root document or the id for a long path.      *      * @param id an id for a document.      * @return the id of the parent document or the empty String.      */
annotation|@
name|Nonnull
specifier|private
specifier|static
name|String
name|getParentId
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
block|{
name|String
name|parentId
init|=
name|Utils
operator|.
name|getParentId
argument_list|(
name|checkNotNull
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
name|parentId
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|parentId
return|;
block|}
specifier|public
name|void
name|resetLockAcquisitionCount
parameter_list|()
block|{
name|lockAcquisitionCounter
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getLockAcquisitionCount
parameter_list|()
block|{
if|if
condition|(
name|lockAcquisitionCounter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The counter hasn't been initialized"
argument_list|)
throw|;
block|}
return|return
name|lockAcquisitionCounter
operator|.
name|get
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|TreeLock
implements|implements
name|Lock
block|{
specifier|private
specifier|final
name|Lock
name|parentLock
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
specifier|private
name|TreeLock
parameter_list|(
name|Lock
name|parentLock
parameter_list|,
name|Lock
name|lock
parameter_list|)
block|{
name|this
operator|.
name|parentLock
operator|=
name|parentLock
expr_stmt|;
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
specifier|private
specifier|static
name|TreeLock
name|shared
parameter_list|(
name|ReadWriteLock
name|parentLock
parameter_list|,
name|Lock
name|lock
parameter_list|)
block|{
return|return
operator|new
name|TreeLock
argument_list|(
name|parentLock
operator|.
name|readLock
argument_list|()
argument_list|,
name|lock
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|TreeLock
name|exclusive
parameter_list|(
name|ReadWriteLock
name|parentLock
parameter_list|)
block|{
return|return
operator|new
name|TreeLock
argument_list|(
name|parentLock
operator|.
name|writeLock
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lock
parameter_list|()
block|{
name|parentLock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unlock
parameter_list|()
block|{
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|parentLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|tryLock
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|tryLock
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Condition
name|newCondition
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

