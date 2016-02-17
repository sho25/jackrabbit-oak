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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|AdditionalAnswers
operator|.
name|delegatesTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doAnswer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|Semaphore
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|junitx
operator|.
name|util
operator|.
name|PrivateAccessor
import|;
end_import

begin_comment
comment|/**  * Tests for the DocumentDiscoveryLiteService  */
end_comment

begin_class
specifier|public
class|class
name|DocumentDiscoveryLiteServiceTest
extends|extends
name|BaseDocumentDiscoveryLiteServiceTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testActivateDeactivate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// then test normal start with a DocumentNodeStore
name|DocumentMK
name|mk1
init|=
name|createMK
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DocumentDiscoveryLiteService
name|discoveryLite
init|=
operator|new
name|DocumentDiscoveryLiteService
argument_list|()
decl_stmt|;
name|PrivateAccessor
operator|.
name|setField
argument_list|(
name|discoveryLite
argument_list|,
literal|"nodeStore"
argument_list|,
name|mk1
operator|.
name|nodeStore
argument_list|)
expr_stmt|;
name|BundleContext
name|bc
init|=
name|mock
argument_list|(
name|BundleContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ComponentContext
name|c
init|=
name|mock
argument_list|(
name|ComponentContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|c
operator|.
name|getBundleContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|bc
argument_list|)
expr_stmt|;
name|discoveryLite
operator|.
name|activate
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|c
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|disableComponent
argument_list|(
name|DocumentDiscoveryLiteService
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|discoveryLite
operator|.
name|deactivate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneNode
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation
argument_list|,
literal|2000
argument_list|,
literal|"see myself as active"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCleanShutdown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s2
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation1
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation2
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|expectation1
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation2
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1
argument_list|,
literal|2000
argument_list|,
literal|"first should see both as active"
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation2
argument_list|,
literal|2000
argument_list|,
literal|"second should see both as active"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterShutdown
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterShutdown
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation1AfterShutdown
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterShutdown
argument_list|,
literal|2000
argument_list|,
literal|"first should only see itself after shutdown"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCrash
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s2
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation1
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation2
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|expectation1
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation2
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1
argument_list|,
literal|2000
argument_list|,
literal|"first should see both as active"
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation2
argument_list|,
literal|2000
argument_list|,
literal|"second should see both as active"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|crash
argument_list|()
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterShutdown
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterShutdown
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation1AfterShutdown
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterShutdown
argument_list|,
literal|2000
argument_list|,
literal|"first should only see itself after shutdown"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCrashAndLongduringRecovery
parameter_list|()
throws|throws
name|Throwable
block|{
name|doTestTwoNodesWithCrashAndLongduringDeactivation
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoNodesWithCrashAndLongduringRecoveryAndBacklog
parameter_list|()
throws|throws
name|Throwable
block|{
name|doTestTwoNodesWithCrashAndLongduringDeactivation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|void
name|doTestTwoNodesWithCrashAndLongduringDeactivation
parameter_list|(
name|boolean
name|withBacklog
parameter_list|)
throws|throws
name|Throwable
block|{
specifier|final
name|int
name|TEST_WAIT_TIMEOUT
init|=
literal|10000
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s1
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|SimplifiedInstance
name|s2
init|=
name|createInstance
argument_list|()
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation1
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
specifier|final
name|ViewExpectation
name|expectation2
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|expectation1
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation2
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should see both as active"
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation2
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"second should see both as active"
argument_list|)
expr_stmt|;
comment|// before crashing s2, make sure that s1's lastRevRecovery thread
comment|// doesn't run
name|s1
operator|.
name|stopLastRevThread
argument_list|()
expr_stmt|;
if|if
condition|(
name|withBacklog
condition|)
block|{
comment|// plus also stop s1's backgroundReadThread - in case we want to
comment|// test backlog handling
name|s1
operator|.
name|stopBgReadThread
argument_list|()
expr_stmt|;
comment|// and then, if we want to do backlog testing, then s2 should write
comment|// something
comment|// before it crashes, so here it comes:
name|s2
operator|.
name|addNode
argument_list|(
literal|"/foo/bar"
argument_list|)
expr_stmt|;
name|s2
operator|.
name|setProperty
argument_list|(
literal|"/foo/bar"
argument_list|,
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
comment|// then crash s2
name|s2
operator|.
name|crash
argument_list|()
expr_stmt|;
comment|// then wait 2 sec
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// at this stage, while s2 has crashed, we have stopped s1's
comment|// lastRevRecoveryThread, so we should still see both as active
name|logger
operator|.
name|info
argument_list|(
name|s1
operator|.
name|getClusterViewStr
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterCrashBeforeLastRevRecovery
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterCrashBeforeLastRevRecovery
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterCrashBeforeLastRevRecovery
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should still see both as active"
argument_list|)
expr_stmt|;
comment|// the next part is a bit tricky: we want to fine-control the
comment|// lastRevRecoveryThread's acquire/release locking.
comment|// the chosen way to do this is to make heavy use of mockito and two
comment|// semaphores:
comment|// when acquireRecoveryLock is called, that thread should wait for the
comment|// waitBeforeLocking semaphore to be released
specifier|final
name|MissingLastRevSeeker
name|missingLastRevUtil
init|=
operator|(
name|MissingLastRevSeeker
operator|)
name|PrivateAccessor
operator|.
name|getField
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getLastRevRecoveryAgent
argument_list|()
argument_list|,
literal|"missingLastRevUtil"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|missingLastRevUtil
argument_list|)
expr_stmt|;
name|MissingLastRevSeeker
name|mockedLongduringMissingLastRevUtil
init|=
name|mock
argument_list|(
name|MissingLastRevSeeker
operator|.
name|class
argument_list|,
name|delegatesTo
argument_list|(
name|missingLastRevUtil
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Semaphore
name|waitBeforeLocking
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"going to waitBeforeLocking"
argument_list|)
expr_stmt|;
name|waitBeforeLocking
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"done with waitBeforeLocking"
argument_list|)
expr_stmt|;
return|return
name|missingLastRevUtil
operator|.
name|acquireRecoveryLock
argument_list|(
operator|(
name|Integer
operator|)
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
argument_list|,
operator|(
name|Integer
operator|)
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|1
index|]
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|mockedLongduringMissingLastRevUtil
argument_list|)
operator|.
name|acquireRecoveryLock
argument_list|(
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|PrivateAccessor
operator|.
name|setField
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getLastRevRecoveryAgent
argument_list|()
argument_list|,
literal|"missingLastRevUtil"
argument_list|,
name|mockedLongduringMissingLastRevUtil
argument_list|)
expr_stmt|;
comment|// so let's start the lastRevThread again and wait for that
comment|// waitBeforeLocking semaphore to be hit
name|s1
operator|.
name|startLastRevThread
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
operator|new
name|Expectation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|fulfilled
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|waitBeforeLocking
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
return|return
literal|"no thread queued"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"lastRevRecoveryThread should acquire a lock"
argument_list|)
expr_stmt|;
comment|// at this stage the crashed s2 is still not in recovery mode, so let's
comment|// check:
name|logger
operator|.
name|info
argument_list|(
name|s1
operator|.
name|getClusterViewStr
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectation1AfterCrashBeforeLastRevRecoveryLocking
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterCrashBeforeLastRevRecoveryLocking
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterCrashBeforeLastRevRecoveryLocking
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should still see both as active"
argument_list|)
expr_stmt|;
comment|// one thing, before we let the waitBeforeLocking go, setup the release
comment|// semaphore/mock:
specifier|final
name|Semaphore
name|waitBeforeUnlocking
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Going to waitBeforeUnlocking"
argument_list|)
expr_stmt|;
name|waitBeforeUnlocking
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Done with waitBeforeUnlocking"
argument_list|)
expr_stmt|;
name|missingLastRevUtil
operator|.
name|releaseRecoveryLock
argument_list|(
operator|(
name|Integer
operator|)
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|mockedLongduringMissingLastRevUtil
argument_list|)
operator|.
name|releaseRecoveryLock
argument_list|(
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
comment|// let go (or tschaedere loh)
name|waitBeforeLocking
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// then, right after we let the waitBeforeLocking semaphore go, we
comment|// should see s2 in recovery mode
specifier|final
name|ViewExpectation
name|expectation1AfterCrashWhileLastRevRecoveryLocking
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectation1AfterCrashWhileLastRevRecoveryLocking
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectation1AfterCrashWhileLastRevRecoveryLocking
operator|.
name|setDeactivatingIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectation1AfterCrashWhileLastRevRecoveryLocking
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should still see s2 as recovering"
argument_list|)
expr_stmt|;
comment|// ok, meanwhile, the lastRevRecoveryAgent should have hit the ot
name|waitFor
argument_list|(
operator|new
name|Expectation
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|fulfilled
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|waitBeforeUnlocking
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
return|return
literal|"no thread queued"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"lastRevRecoveryThread should want to release a lock"
argument_list|)
expr_stmt|;
comment|// so then, we should still see the same state
name|waitFor
argument_list|(
name|expectation1AfterCrashWhileLastRevRecoveryLocking
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should still see s2 as recovering"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting 1,5sec"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting done"
argument_list|)
expr_stmt|;
comment|// first, lets check to see what the view looks like - should be
comment|// unchanged:
name|waitFor
argument_list|(
name|expectation1AfterCrashWhileLastRevRecoveryLocking
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should still see s2 as recovering"
argument_list|)
expr_stmt|;
comment|// let waitBeforeUnlocking go
name|logger
operator|.
name|info
argument_list|(
literal|"releasing waitBeforeUnlocking, state: "
operator|+
name|s1
operator|.
name|getClusterViewStr
argument_list|()
argument_list|)
expr_stmt|;
name|waitBeforeUnlocking
operator|.
name|release
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"released waitBeforeUnlocking"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|withBacklog
condition|)
block|{
specifier|final
name|ViewExpectation
name|expectationWithoutBacklog
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectationWithoutBacklog
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectationWithoutBacklog
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectationWithoutBacklog
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"finally we should see s2 as completely inactive"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// wait just 2 sec to see if the bgReadThread is really stopped
name|logger
operator|.
name|info
argument_list|(
literal|"sleeping 2 sec"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"sleeping 2 sec done, state: "
operator|+
name|s1
operator|.
name|getClusterViewStr
argument_list|()
argument_list|)
expr_stmt|;
comment|// when that's the case, check the view - it should now be in a
comment|// special 'final=false' mode
specifier|final
name|ViewExpectation
name|expectationBeforeBgRead
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectationBeforeBgRead
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectationBeforeBgRead
operator|.
name|setDeactivatingIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectationBeforeBgRead
operator|.
name|setFinal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectationBeforeBgRead
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"first should only see itself after shutdown"
argument_list|)
expr_stmt|;
comment|// ook, now we explicitly do a background read to get out of the
comment|// backlog situation
name|s1
operator|.
name|ns
operator|.
name|runBackgroundReadOperations
argument_list|()
expr_stmt|;
specifier|final
name|ViewExpectation
name|expectationAfterBgRead
init|=
operator|new
name|ViewExpectation
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|expectationAfterBgRead
operator|.
name|setActiveIds
argument_list|(
name|s1
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|expectationAfterBgRead
operator|.
name|setInactiveIds
argument_list|(
name|s2
operator|.
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|expectationAfterBgRead
argument_list|,
name|TEST_WAIT_TIMEOUT
argument_list|,
literal|"finally we should see s2 as completely inactive"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This test creates a large number of documentnodestores which it starts,      * runs, stops in a random fashion, always testing to make sure the      * clusterView is correct      */
annotation|@
name|Test
specifier|public
name|void
name|testSmallStartStopFiesta
parameter_list|()
throws|throws
name|Throwable
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"testSmallStartStopFiesta: start, seed="
operator|+
name|SEED
argument_list|)
expr_stmt|;
specifier|final
name|int
name|LOOP_CNT
init|=
literal|5
decl_stmt|;
comment|// with too many loops have also seen mongo
comment|// connections becoming starved thus test
comment|// failed
name|doStartStopFiesta
argument_list|(
name|LOOP_CNT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

