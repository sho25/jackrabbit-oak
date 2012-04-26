begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|store
package|;
end_package

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
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|ScheduledExecutorService
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
name|ThreadFactory
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
name|AtomicInteger
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
name|ReentrantReadWriteLock
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
name|mk
operator|.
name|model
operator|.
name|ChildNode
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
name|mk
operator|.
name|model
operator|.
name|ChildNodeEntriesMap
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
name|mk
operator|.
name|model
operator|.
name|Id
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
name|mk
operator|.
name|model
operator|.
name|MutableCommit
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
name|mk
operator|.
name|model
operator|.
name|MutableNode
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
name|mk
operator|.
name|model
operator|.
name|Node
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
name|mk
operator|.
name|model
operator|.
name|NodeDiffHandler
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
name|mk
operator|.
name|model
operator|.
name|NodeState
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
name|mk
operator|.
name|model
operator|.
name|NodeStateDiff
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
name|mk
operator|.
name|model
operator|.
name|StoredCommit
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
name|mk
operator|.
name|model
operator|.
name|StoredNode
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
name|mk
operator|.
name|persistence
operator|.
name|GCPersistence
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
name|mk
operator|.
name|persistence
operator|.
name|Persistence
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
name|mk
operator|.
name|util
operator|.
name|IOUtils
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
name|mk
operator|.
name|util
operator|.
name|SimpleLRUCache
import|;
end_import

begin_comment
comment|/**  * Default revision store implementation, passing calls to a {@code Persistence}  * and a {@code BlobStore}, respectively and providing caching.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultRevisionStore
extends|extends
name|AbstractRevisionStore
implements|implements
name|Closeable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SIZE
init|=
literal|"mk.cacheSize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE_SIZE
init|=
literal|10000
decl_stmt|;
specifier|private
name|boolean
name|initialized
decl_stmt|;
specifier|private
name|Id
name|head
decl_stmt|;
specifier|private
name|AtomicLong
name|commitCounter
decl_stmt|;
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|headLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Persistence
name|pm
decl_stmt|;
specifier|protected
specifier|final
name|GCPersistence
name|gcpm
decl_stmt|;
comment|/* avoid synthetic accessor */
name|int
name|initialCacheSize
decl_stmt|;
comment|/* avoid synthetic accessor */
name|Map
argument_list|<
name|Id
argument_list|,
name|Object
argument_list|>
name|cache
decl_stmt|;
comment|/**      * GC run state constants.      */
specifier|private
specifier|static
specifier|final
name|int
name|NOT_ACTIVE
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STARTING
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MARKING
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SWEEPING
init|=
literal|3
decl_stmt|;
comment|/**      * GC run state.      */
specifier|private
specifier|final
name|AtomicInteger
name|gcState
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**      * GC executor.      */
specifier|private
name|ScheduledExecutorService
name|gcExecutor
decl_stmt|;
comment|/**      * Active put tokens (Key: token, Value: null).      */
specifier|private
specifier|final
name|Map
argument_list|<
name|PutTokenImpl
argument_list|,
name|Object
argument_list|>
name|putTokens
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|PutTokenImpl
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Read-write lock for put tokens.      */
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|tokensLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
comment|/**      * Active branches (Key: branch root id, Value: branch head).      */
specifier|private
specifier|final
name|Map
argument_list|<
name|Id
argument_list|,
name|Id
argument_list|>
name|branches
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|Id
argument_list|,
name|Id
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|DefaultRevisionStore
parameter_list|(
name|Persistence
name|pm
parameter_list|)
block|{
name|this
operator|.
name|pm
operator|=
name|pm
expr_stmt|;
name|this
operator|.
name|gcpm
operator|=
operator|(
name|pm
operator|instanceof
name|GCPersistence
operator|)
condition|?
operator|(
name|GCPersistence
operator|)
name|pm
else|:
literal|null
expr_stmt|;
name|commitCounter
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already initialized"
argument_list|)
throw|;
block|}
name|initialCacheSize
operator|=
name|determineInitialCacheSize
argument_list|()
expr_stmt|;
name|cache
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|SimpleLRUCache
operator|.
expr|<
name|Id
argument_list|,
name|Object
operator|>
name|newInstance
argument_list|(
name|initialCacheSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure we've got a HEAD commit
name|head
operator|=
name|pm
operator|.
name|readHead
argument_list|()
expr_stmt|;
if|if
condition|(
name|head
operator|==
literal|null
operator|||
name|head
operator|.
name|getBytes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// assume virgin repository
name|byte
index|[]
name|rawHead
init|=
name|Id
operator|.
name|fromLong
argument_list|(
name|commitCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|head
operator|=
operator|new
name|Id
argument_list|(
name|rawHead
argument_list|)
expr_stmt|;
name|Id
name|rootNodeId
init|=
name|pm
operator|.
name|writeNode
argument_list|(
operator|new
name|MutableNode
argument_list|(
name|this
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|MutableCommit
name|initialCommit
init|=
operator|new
name|MutableCommit
argument_list|()
decl_stmt|;
name|initialCommit
operator|.
name|setCommitTS
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|initialCommit
operator|.
name|setRootNodeId
argument_list|(
name|rootNodeId
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeCommit
argument_list|(
name|head
argument_list|,
name|initialCommit
argument_list|)
expr_stmt|;
name|pm
operator|.
name|writeHead
argument_list|(
name|head
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commitCounter
operator|.
name|set
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|head
operator|.
name|toString
argument_list|()
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gcpm
operator|!=
literal|null
condition|)
block|{
name|gcExecutor
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
literal|"RevisionStore-GC"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|gcExecutor
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|cache
operator|.
name|size
argument_list|()
operator|>=
name|initialCacheSize
condition|)
block|{
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|60
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
if|if
condition|(
name|gcExecutor
operator|!=
literal|null
condition|)
block|{
name|gcExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|pm
argument_list|)
expr_stmt|;
name|initialized
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|void
name|verifyInitialized
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not initialized"
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|static
name|int
name|determineInitialCacheSize
parameter_list|()
block|{
name|String
name|val
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|CACHE_SIZE
argument_list|)
decl_stmt|;
return|return
operator|(
name|val
operator|!=
literal|null
operator|)
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
else|:
name|DEFAULT_CACHE_SIZE
return|;
block|}
comment|// --------------------------------------------------------< RevisionStore>
comment|/**      * Put token implementation.      */
specifier|static
class|class
name|PutTokenImpl
extends|extends
name|PutToken
block|{
specifier|private
specifier|static
name|int
name|idCounter
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|StoredNode
name|lastModifiedNode
decl_stmt|;
specifier|public
name|PutTokenImpl
parameter_list|()
block|{
name|this
operator|.
name|id
operator|=
operator|++
name|idCounter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|PutTokenImpl
condition|)
block|{
return|return
operator|(
operator|(
name|PutTokenImpl
operator|)
name|obj
operator|)
operator|.
name|id
operator|==
name|id
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
specifier|public
name|void
name|updateLastModifed
parameter_list|(
name|StoredNode
name|lastModifiedNode
parameter_list|)
block|{
name|this
operator|.
name|lastModifiedNode
operator|=
name|lastModifiedNode
expr_stmt|;
block|}
specifier|public
name|StoredNode
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModifiedNode
return|;
block|}
block|}
specifier|public
name|RevisionStore
operator|.
name|PutToken
name|createPutToken
parameter_list|()
block|{
return|return
operator|new
name|PutTokenImpl
argument_list|()
return|;
block|}
specifier|public
name|Id
name|putNode
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableNode
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|node
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
comment|/*          * Make sure that a GC cycle can not sweep this newly persisted node          * before we have updated our token          */
name|tokensLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Id
name|id
init|=
name|pm
operator|.
name|writeNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|StoredNode
name|snode
init|=
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
name|node
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|snode
argument_list|)
expr_stmt|;
name|PutTokenImpl
name|pti
init|=
operator|(
name|PutTokenImpl
operator|)
name|token
decl_stmt|;
name|pti
operator|.
name|updateLastModifed
argument_list|(
name|snode
argument_list|)
expr_stmt|;
name|putTokens
operator|.
name|put
argument_list|(
name|pti
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
finally|finally
block|{
name|tokensLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Id
name|putCNEMap
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|ChildNodeEntriesMap
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|map
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|map
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|Id
name|id
init|=
name|pm
operator|.
name|writeCNEMap
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|public
name|void
name|lockHead
parameter_list|()
block|{
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Id
name|putHeadCommit
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableCommit
name|commit
parameter_list|,
name|Id
name|branchRootId
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|isHeldByCurrentThread
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"putHeadCommit called without holding write lock."
argument_list|)
throw|;
block|}
name|Id
name|id
init|=
name|writeCommit
argument_list|(
name|token
argument_list|,
name|commit
argument_list|)
decl_stmt|;
name|setHeadCommitId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|putTokens
operator|.
name|remove
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|branchRootId
operator|!=
literal|null
condition|)
block|{
name|branches
operator|.
name|remove
argument_list|(
name|branchRootId
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
specifier|public
name|Id
name|putCommit
parameter_list|(
name|PutToken
name|token
parameter_list|,
name|MutableCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|Id
name|commitId
init|=
name|writeCommit
argument_list|(
name|token
argument_list|,
name|commit
argument_list|)
decl_stmt|;
name|putTokens
operator|.
name|remove
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Id
name|branchRootId
init|=
name|commit
operator|.
name|getBranchRootId
argument_list|()
decl_stmt|;
if|if
condition|(
name|branchRootId
operator|!=
literal|null
condition|)
block|{
name|branches
operator|.
name|put
argument_list|(
name|branchRootId
argument_list|,
name|commitId
argument_list|)
expr_stmt|;
block|}
return|return
name|commitId
return|;
block|}
specifier|public
name|void
name|unlockHead
parameter_list|()
block|{
name|headLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// -----------------------------------------------------< RevisionProvider>
specifier|public
name|StoredNode
name|getNode
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
return|;
block|}
name|node
operator|=
operator|new
name|StoredNode
argument_list|(
name|id
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|pm
operator|.
name|readNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|ChildNodeEntriesMap
name|getCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|ChildNodeEntriesMap
name|map
init|=
operator|(
name|ChildNodeEntriesMap
operator|)
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
return|return
name|map
return|;
block|}
name|map
operator|=
name|pm
operator|.
name|readCNEMap
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
specifier|public
name|StoredCommit
name|getCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|StoredCommit
name|commit
init|=
operator|(
name|StoredCommit
operator|)
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
return|return
name|commit
return|;
block|}
name|commit
operator|=
name|pm
operator|.
name|readCommit
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
expr_stmt|;
return|return
name|commit
return|;
block|}
specifier|public
name|StoredNode
name|getRootNode
parameter_list|(
name|Id
name|commitId
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|getNode
argument_list|(
name|getCommit
argument_list|(
name|commitId
argument_list|)
operator|.
name|getRootNodeId
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|StoredCommit
name|getHeadCommit
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getCommit
argument_list|(
name|getHeadCommitId
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Id
name|getHeadCommitId
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyInitialized
argument_list|()
expr_stmt|;
name|headLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|head
return|;
block|}
finally|finally
block|{
name|headLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// -------------------------------------------------------< implementation>
specifier|private
name|Id
name|writeCommit
parameter_list|(
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|,
name|MutableCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|PersistHook
name|callback
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commit
operator|instanceof
name|PersistHook
condition|)
block|{
name|callback
operator|=
operator|(
name|PersistHook
operator|)
name|commit
expr_stmt|;
name|callback
operator|.
name|prePersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|Id
name|id
init|=
name|commit
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|id
operator|=
name|Id
operator|.
name|fromLong
argument_list|(
name|commitCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|writeCommit
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
expr_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|postPersist
argument_list|(
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|StoredCommit
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|private
name|void
name|setHeadCommitId
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
block|{
comment|// non-synchronized since we're called from putHeadCommit
comment|// which requires a write lock
name|pm
operator|.
name|writeHead
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|head
operator|=
name|id
expr_stmt|;
name|long
name|counter
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|>
name|commitCounter
operator|.
name|get
argument_list|()
condition|)
block|{
name|commitCounter
operator|.
name|set
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------< overrides>
annotation|@
name|Override
specifier|public
name|void
name|compare
parameter_list|(
specifier|final
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|,
specifier|final
name|NodeStateDiff
name|diff
parameter_list|)
block|{
comment|// OAK-46: Efficient diffing of large child node lists
name|Node
name|beforeNode
init|=
operator|(
operator|(
name|StoredNodeAsState
operator|)
name|before
operator|)
operator|.
name|unwrap
argument_list|()
decl_stmt|;
name|Node
name|afterNode
init|=
operator|(
operator|(
name|StoredNodeAsState
operator|)
name|after
operator|)
operator|.
name|unwrap
argument_list|()
decl_stmt|;
name|beforeNode
operator|.
name|diff
argument_list|(
name|afterNode
argument_list|,
operator|new
name|NodeDiffHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|propAdded
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propChanged
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|oldValue
parameter_list|,
name|String
name|newValue
parameter_list|)
block|{
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|,
name|after
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propDeleted
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|ChildNode
name|added
parameter_list|)
block|{
name|String
name|name
init|=
name|added
operator|.
name|getName
argument_list|()
decl_stmt|;
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|ChildNode
name|deleted
parameter_list|)
block|{
name|String
name|name
init|=
name|deleted
operator|.
name|getName
argument_list|()
decl_stmt|;
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|ChildNode
name|changed
parameter_list|,
name|Id
name|newId
parameter_list|)
block|{
name|String
name|name
init|=
name|changed
operator|.
name|getName
argument_list|()
decl_stmt|;
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// -----------------------------------------------------------------------
comment|// GC
comment|/**      * Perform a garbage collection. If a garbage collection cycle is already      * running, this method returns immediately.      */
specifier|public
name|void
name|gc
parameter_list|()
block|{
if|if
condition|(
name|gcpm
operator|==
literal|null
operator|||
operator|!
name|gcState
operator|.
name|compareAndSet
argument_list|(
name|NOT_ACTIVE
argument_list|,
name|STARTING
argument_list|)
condition|)
block|{
comment|// already running
return|return;
block|}
try|try
block|{
name|markUncommittedNodes
argument_list|()
expr_stmt|;
name|Id
name|firstBranchRootId
init|=
name|markBranches
argument_list|()
decl_stmt|;
name|Id
name|firstCommitId
init|=
name|markCommits
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstBranchRootId
operator|!=
literal|null
operator|&&
name|firstBranchRootId
operator|.
name|compareTo
argument_list|(
name|firstCommitId
argument_list|)
operator|<
literal|0
condition|)
block|{
name|firstCommitId
operator|=
name|firstBranchRootId
expr_stmt|;
block|}
comment|/* repair dangling parent commit of first, preserved commit */
name|StoredCommit
name|commit
init|=
name|getCommit
argument_list|(
name|firstCommitId
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|.
name|getParentId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|MutableCommit
name|firstCommit
init|=
operator|new
name|MutableCommit
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|firstCommit
operator|.
name|setParentId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|gcpm
operator|.
name|replaceCommit
argument_list|(
name|firstCommit
operator|.
name|getId
argument_list|()
argument_list|,
name|firstCommit
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
comment|/* unable to perform GC */
name|gcState
operator|.
name|set
argument_list|(
name|NOT_ACTIVE
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return;
block|}
name|gcState
operator|.
name|set
argument_list|(
name|SWEEPING
argument_list|)
expr_stmt|;
try|try
block|{
name|gcpm
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|cache
operator|.
name|clear
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
finally|finally
block|{
name|gcState
operator|.
name|set
argument_list|(
name|NOT_ACTIVE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Mark nodes that have already been put but not committed yet.      *       * @throws Exception      *             if an error occurs      */
specifier|private
name|void
name|markUncommittedNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|tokensLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|gcpm
operator|.
name|start
argument_list|()
expr_stmt|;
name|gcState
operator|.
name|set
argument_list|(
name|MARKING
argument_list|)
expr_stmt|;
for|for
control|(
name|PutTokenImpl
name|token
range|:
name|putTokens
operator|.
name|keySet
argument_list|()
control|)
block|{
name|markNode
argument_list|(
name|token
operator|.
name|getLastModified
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|tokensLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Mark branches.      *       * @return first branch root id that needs to be preserved, or {@code null}      * @throws Exception      *             if an error occurs      */
specifier|private
name|Id
name|markBranches
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* Mark all branch commits */
for|for
control|(
name|Entry
argument_list|<
name|Id
argument_list|,
name|Id
argument_list|>
name|entry
range|:
name|branches
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Id
name|branchRootId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Id
name|branchHeadId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|branchHeadId
operator|.
name|equals
argument_list|(
name|branchRootId
argument_list|)
condition|)
block|{
name|StoredCommit
name|commit
init|=
name|getCommit
argument_list|(
name|branchHeadId
argument_list|)
decl_stmt|;
name|markCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|branchHeadId
operator|=
name|commit
operator|.
name|getParentId
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* Mark all master commits till the first branch root id */
if|if
condition|(
operator|!
name|branches
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Id
name|firstBranchRootId
init|=
name|branches
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|StoredCommit
name|commit
init|=
name|getHeadCommit
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|markCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
if|if
condition|(
name|commit
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|firstBranchRootId
argument_list|)
condition|)
block|{
break|break;
block|}
name|commit
operator|=
name|getCommit
argument_list|(
name|commit
operator|.
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|firstBranchRootId
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Mark all commits and nodes in a garbage collection cycle. Can be      * customized by subclasses. The default implementation preserves all      * commits that were created within 60 minutes of the current head commit.      *<p/>      * If this method throws an exception, the cycle will be stopped without      * sweeping.      *       * @return first commit id that will be preserved      * @throws Exception      *             if an error occurs      */
specifier|protected
name|Id
name|markCommits
parameter_list|()
throws|throws
name|Exception
block|{
name|StoredCommit
name|commit
init|=
name|getHeadCommit
argument_list|()
decl_stmt|;
name|long
name|tsLimit
init|=
name|commit
operator|.
name|getCommitTS
argument_list|()
operator|-
operator|(
literal|60
operator|*
literal|60
operator|*
literal|1000
operator|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|markCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|Id
name|id
init|=
name|commit
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|StoredCommit
name|parentCommit
init|=
name|getCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentCommit
operator|.
name|getCommitTS
argument_list|()
operator|<
name|tsLimit
condition|)
block|{
break|break;
block|}
name|commit
operator|=
name|parentCommit
expr_stmt|;
block|}
return|return
name|commit
operator|.
name|getId
argument_list|()
return|;
block|}
comment|/**      * Mark a commit. This marks all nodes belonging to this commit as well.      *       * @param commit commit      * @throws Exception if an error occurs      */
specifier|protected
name|void
name|markCommit
parameter_list|(
name|StoredCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|gcpm
operator|.
name|markCommit
argument_list|(
name|commit
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|markNode
argument_list|(
name|getNode
argument_list|(
name|commit
operator|.
name|getRootNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Mark a node. This marks all children as well.      *       * @param node node      * @throws Exception if an error occurs      */
specifier|private
name|void
name|markNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|gcpm
operator|.
name|markNode
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|iter
init|=
name|node
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ChildNode
name|c
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|markNode
argument_list|(
name|getNode
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

