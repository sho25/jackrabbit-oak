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
name|HashSet
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
name|ExecutionException
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
name|ExecutorService
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
name|Future
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
name|TimeUnit
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
name|core
operator|.
name|MicroKernelImpl
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
name|core
operator|.
name|Repository
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
name|InMemPersistence
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
name|store
operator|.
name|DefaultRevisionStore
operator|.
name|PutTokenImpl
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
name|store
operator|.
name|RevisionStore
operator|.
name|PutToken
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
name|spi
operator|.
name|blob
operator|.
name|MemoryBlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
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
name|Ignore
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests verifying the inner workings of<code>DefaultRevisionStore</code>.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultRevisionStoreTest
block|{
comment|/* avoid synthetic accessor */
name|DefaultRevisionStore
name|rs
decl_stmt|;
specifier|private
name|MicroKernelImpl
name|mk
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|rs
operator|=
operator|new
name|DefaultRevisionStore
argument_list|(
name|createPersistence
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Id
name|markCommits
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Keep head commit only
name|StoredCommit
name|commit
init|=
name|getHeadCommit
argument_list|()
decl_stmt|;
name|markCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
return|return
name|commit
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|rs
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|mk
operator|=
operator|new
name|MicroKernelImpl
argument_list|(
operator|new
name|Repository
argument_list|(
name|rs
argument_list|,
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|GCPersistence
name|createPersistence
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|InMemPersistence
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Verify revision history works with garbage collection.      *       * @throws Exception if an error occurs      */
annotation|@
name|Test
specifier|public
name|void
name|testRevisionHistory
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"c\":{ \"d\":{}  }}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/b"
argument_list|,
literal|"+\"e\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/c"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|headRevision
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|contents
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|headRevision
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rs
operator|.
name|gc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|headRevision
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|contents
argument_list|,
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|headRevision
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|history
init|=
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|parseJSONArray
argument_list|(
name|history
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify branch and merge works with garbage collection.      *       * @throws Exception if an error occurs      */
annotation|@
name|Test
specifier|public
name|void
name|testBranchMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\":{}, \"c\":{} }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|branchRevisionId
init|=
name|mk
operator|.
name|branch
argument_list|(
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"d\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|branchRevisionId
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"e\" : {}"
argument_list|,
name|branchRevisionId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rs
operator|.
name|gc
argument_list|()
expr_stmt|;
name|branchRevisionId
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
name|branchRevisionId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|merge
argument_list|(
name|branchRevisionId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|rs
operator|.
name|gc
argument_list|()
expr_stmt|;
name|String
name|history
init|=
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|parseJSONArray
argument_list|(
name|history
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify garbage collection can run concurrently with commits.      *       * @throws Exception if an error occurs      */
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testConcurrentGC
parameter_list|()
throws|throws
name|Exception
block|{
name|ScheduledExecutorService
name|gcExecutor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
name|rs
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : { \"c\" : { \"d\" : {} } } }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|"+\"e\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d/e"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|"-\"e\""
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|gcExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Verify garbage collection can run concurrently with branch& merge.      *       * @throws Exception if an error occurs      */
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testConcurrentMergeGC
parameter_list|()
throws|throws
name|Exception
block|{
name|ScheduledExecutorService
name|gcExecutor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
name|rs
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|20
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : { \"c\" : { \"d\" : {} } } }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|String
name|branchId
init|=
name|mk
operator|.
name|branch
argument_list|(
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
comment|/* add some data in even runs */
name|branchId
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|"+\"e\" : {}"
argument_list|,
name|branchId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|branchId
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d/e"
argument_list|,
literal|"+\"f\" : {}"
argument_list|,
name|branchId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* remove added data in odd runs */
name|branchId
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|"-\"e\""
argument_list|,
name|branchId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|mk
operator|.
name|merge
argument_list|(
name|branchId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|gcExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|putTokenImpl
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|Set
argument_list|<
name|PutToken
argument_list|>
name|tokens
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|PutToken
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|Future
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10000
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PutTokenImpl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|?
argument_list|>
name|result
range|:
name|results
control|)
block|{
name|result
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Parses the provided string into a {@code JSONArray}.      *      * @param json string to be parsed      * @return a {@code JSONArray}      * @throws {@code AssertionError} if the string cannot be parsed into a {@code JSONArray}      */
specifier|private
name|JSONArray
name|parseJSONArray
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|AssertionError
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
try|try
block|{
name|Object
name|obj
init|=
name|parser
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|instanceof
name|JSONArray
argument_list|)
expr_stmt|;
return|return
operator|(
name|JSONArray
operator|)
name|obj
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not a valid JSON array: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

