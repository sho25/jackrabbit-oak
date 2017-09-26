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
package|;
end_package

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
name|AtomicBoolean
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|ClusterNodeInfoTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|TestStore
name|store
decl_stmt|;
specifier|private
name|FailureHandler
name|handler
init|=
operator|new
name|FailureHandler
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|TestStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLease
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|leaseEnd
init|=
name|info
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|leaseEnd
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|.
name|isLeaseFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFailBeforeUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|getFailBeforeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|leaseEnd
init|=
name|info
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
comment|// must succeed next time
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|leaseEnd
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|.
name|isLeaseFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// OAK-4770
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionAfter
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFailAfterUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|getFailAfterUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|leaseEnd
init|=
name|info
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
comment|// must succeed next time
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|leaseEnd
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|.
name|isLeaseFailure
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionBeforeWithDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFailBeforeUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// delay operations by half the lease time, this will
comment|// first delay the update and then delay the subsequent
comment|// find because of the exception on update. afterwards the
comment|// lease must be expired
name|store
operator|.
name|setDelayMillis
argument_list|(
name|info
operator|.
name|getLeaseTime
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|<
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionAfterWithDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|leaseEnd
init|=
name|info
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFailAfterUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// delay operations by half the lease time, this will
comment|// first delay the update and then delay the subsequent
comment|// find because of the exception on update. afterwards
comment|// the leaseEnd must reflect the updated value
name|store
operator|.
name|setDelayMillis
argument_list|(
name|info
operator|.
name|getLeaseTime
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|leaseEnd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionAfterFindFails
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|leaseEnd
init|=
name|info
operator|.
name|getLeaseEndTime
argument_list|()
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFailAfterUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|store
operator|.
name|setFailFind
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// delay operations by half the lease time, this will
comment|// first delay the update and then delay and fail the
comment|// subsequent find once.
name|store
operator|.
name|setDelayMillis
argument_list|(
name|info
operator|.
name|getLeaseTime
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|getFailFind
argument_list|()
argument_list|)
expr_stmt|;
comment|// must not reflect the updated value, because retries
comment|// to read the current cluster node info document stops
comment|// once lease expires
name|assertEquals
argument_list|(
name|leaseEnd
argument_list|,
name|info
operator|.
name|getLeaseEndTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseExceptionAfterFindSucceedsEventually
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|waitLeaseUpdateInterval
argument_list|()
expr_stmt|;
comment|// delay operations by a sixth of the lease time, this will
comment|// first delay the update and then delay and fail the
comment|// subsequent find calls. find retries should eventually
comment|// succeed within the lease time
name|store
operator|.
name|setDelayMillis
argument_list|(
name|info
operator|.
name|getLeaseTime
argument_list|()
operator|/
literal|6
argument_list|)
expr_stmt|;
name|store
operator|.
name|setFailAfterUpdate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|store
operator|.
name|setFailFind
argument_list|(
literal|3
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// the three retries must eventually succeed within the lease time
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|getFailFind
argument_list|()
argument_list|)
expr_stmt|;
comment|// must reflect the updated value
name|assertTrue
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|>
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseDelayed
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
name|recoverClusterNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|handler
operator|.
name|isLeaseFailure
argument_list|()
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"expected lease failure"
argument_list|)
expr_stmt|;
block|}
comment|// OAK-4779
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseWhileRecoveryRunning
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// wait until after lease end
name|clock
operator|.
name|waitUntil
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
comment|// simulate a started recovery
name|MissingLastRevSeeker
name|seeker
init|=
operator|new
name|MissingLastRevSeeker
argument_list|(
name|store
operator|.
name|getStore
argument_list|()
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
literal|1
argument_list|,
literal|42
argument_list|)
argument_list|)
expr_stmt|;
comment|// cluster node 1 must not be able to renew the lease now
try|try
block|{
comment|// must either return false
name|assertFalse
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// or throw an exception
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|renewLeaseTimedOutWithCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterNodeInfo
name|info
init|=
name|newClusterNodeInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// wait until after lease end
name|clock
operator|.
name|waitUntil
argument_list|(
name|info
operator|.
name|getLeaseEndTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
argument_list|)
expr_stmt|;
try|try
block|{
name|info
operator|.
name|performLeaseCheck
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"lease check must fail with exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// cluster node 1 must not be able to renew the lease now
try|try
block|{
comment|// must either return false
name|assertFalse
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
comment|// or throw an exception
block|}
block|}
specifier|private
name|void
name|recoverClusterNode
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
operator|.
name|getStore
argument_list|()
argument_list|)
comment|// use unwrapped store
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|42
argument_list|)
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
try|try
block|{
name|LastRevRecoveryAgent
name|recovery
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|ns
argument_list|)
decl_stmt|;
name|recovery
operator|.
name|recover
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|waitLeaseUpdateInterval
parameter_list|()
throws|throws
name|Exception
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|ClusterNodeInfo
operator|.
name|DEFAULT_LEASE_UPDATE_INTERVAL_MILLIS
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ClusterNodeInfo
name|newClusterNodeInfo
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|ClusterNodeInfo
name|info
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
name|info
operator|.
name|setLeaseFailureHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|renewLease
argument_list|()
argument_list|)
expr_stmt|;
comment|// perform initial lease renew
return|return
name|info
return|;
block|}
specifier|static
specifier|final
class|class
name|FailureHandler
implements|implements
name|LeaseFailureHandler
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|leaseFailure
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|handleLeaseFailure
parameter_list|()
block|{
name|leaseFailure
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLeaseFailure
parameter_list|()
block|{
return|return
name|leaseFailure
operator|.
name|get
argument_list|()
return|;
block|}
block|}
specifier|final
class|class
name|TestStore
extends|extends
name|DocumentStoreWrapper
block|{
specifier|private
specifier|final
name|AtomicInteger
name|failBeforeUpdate
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|failAfterUpdate
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|failFind
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|long
name|delayMillis
decl_stmt|;
name|TestStore
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocumentStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
name|maybeDelay
argument_list|()
expr_stmt|;
name|maybeThrow
argument_list|(
name|failBeforeUpdate
argument_list|,
literal|"update failed before"
argument_list|)
expr_stmt|;
name|T
name|doc
init|=
name|super
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
decl_stmt|;
name|maybeThrow
argument_list|(
name|failAfterUpdate
argument_list|,
literal|"update failed after"
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|maybeDelay
argument_list|()
expr_stmt|;
name|maybeThrow
argument_list|(
name|failFind
argument_list|,
literal|"find failed"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
specifier|private
name|void
name|maybeDelay
parameter_list|()
block|{
try|try
block|{
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|delayMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|maybeThrow
parameter_list|(
name|AtomicInteger
name|num
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|num
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|num
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|getFailBeforeUpdate
parameter_list|()
block|{
return|return
name|failBeforeUpdate
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setFailBeforeUpdate
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|failBeforeUpdate
operator|.
name|set
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getFailAfterUpdate
parameter_list|()
block|{
return|return
name|failAfterUpdate
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setFailAfterUpdate
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|failAfterUpdate
operator|.
name|set
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getDelayMillis
parameter_list|()
block|{
return|return
name|delayMillis
return|;
block|}
specifier|public
name|void
name|setDelayMillis
parameter_list|(
name|long
name|delayMillis
parameter_list|)
block|{
name|this
operator|.
name|delayMillis
operator|=
name|delayMillis
expr_stmt|;
block|}
specifier|public
name|int
name|getFailFind
parameter_list|()
block|{
return|return
name|failFind
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|void
name|setFailFind
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|failFind
operator|.
name|set
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
