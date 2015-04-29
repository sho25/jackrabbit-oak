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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Random
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|jline
operator|.
name|internal
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|DescriptiveStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|SynchronizedDescriptiveStatistics
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
name|benchmark
operator|.
name|util
operator|.
name|Profiler
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
name|fixture
operator|.
name|RepositoryFixture
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
comment|/**  * Abstract base class for individual performance benchmarks.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractTest
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Benchmark
implements|implements
name|CSVResultGenerator
block|{
comment|/**      * A random string to guarantee concurrently running tests don't overwrite      * each others changes (for example in a cluster).      *<p>      * The probability of duplicates, for 50 concurrent processes, is less than      * 1 in 1 million.      */
specifier|static
specifier|final
name|String
name|TEST_ID
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|static
name|AtomicInteger
name|nodeNameCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**      * A node name that is guarantee to be unique within the current JVM.      */
specifier|static
name|String
name|nextNodeName
parameter_list|()
block|{
return|return
literal|"n"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|nodeNameCounter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Credentials
name|CREDENTIALS
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|WARMUP
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|Long
operator|.
name|getLong
argument_list|(
literal|"warmup"
argument_list|,
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|RUNTIME
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|Long
operator|.
name|getLong
argument_list|(
literal|"runtime"
argument_list|,
literal|60
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|PROFILE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"profile"
argument_list|)
decl_stmt|;
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
name|AbstractTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Credentials
name|credentials
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Session
argument_list|>
name|sessions
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|running
decl_stmt|;
specifier|private
name|Profiler
name|profiler
decl_stmt|;
specifier|private
name|PrintStream
name|out
decl_stmt|;
comment|/**      *<p>      * used to signal the {@link #runTest(int)} if stop running future test planned or not. If set      * to true, it will exit the loop not performing any more tests.      *</p>      *       *<p>      * useful when the running of the benchmark makes sense for as long as other processes didn't      * complete.      *</p>      *       *<p>      * Set this variable from within the benchmark itself by using {@link #issueHaltRequest(String)}      *</p>      *       *<p>      *<strong>it works only for concurrency level of 1 ({@code --concurrency 1} the      * default)</strong>      *</p>      */
specifier|private
name|boolean
name|haltRequested
decl_stmt|;
comment|/**      * If concurrency level is 1 ({@code --concurrency 1}, the default) it will issue a request to      * halt any future runs of a single benchmark. Useful when the benchmark makes sense only if run      * in conjunction of any other parallel operations.      *       * @param message an optional message that can be provided. It will logged at info level.      */
specifier|protected
name|void
name|issueHaltRequest
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|message
parameter_list|)
block|{
name|String
name|m
init|=
name|message
operator|==
literal|null
condition|?
literal|""
else|:
name|message
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"halt requested. {}"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|haltRequested
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      *<p>      * this method will be called during the {@link #tearDown()} before the {@link #afterSuite()}.      * Override it if you have background processes you wish to stop.      *</p>      *<p>      * For example in case of big imports, the suite could be keep running for as long as the import      * is running, even if the tests are actually no longer executed.      *</p>      */
specifier|protected
name|void
name|issueHaltChildThreads
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|setPrintStream
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
specifier|protected
specifier|static
name|int
name|getScale
parameter_list|(
name|int
name|def
parameter_list|)
block|{
name|int
name|scale
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"scale"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|scale
operator|==
literal|0
condition|)
block|{
name|scale
operator|=
name|def
expr_stmt|;
block|}
return|return
name|scale
return|;
block|}
comment|/**      * Prepares this performance benchmark.      *      * @param repository the repository to use      * @param credentials credentials of a user with write access      * @throws Exception if the benchmark can not be prepared      */
specifier|public
name|void
name|setUp
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|credentials
operator|=
name|credentials
expr_stmt|;
name|this
operator|.
name|sessions
operator|=
operator|new
name|LinkedList
argument_list|<
name|Session
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|threads
operator|=
operator|new
name|LinkedList
argument_list|<
name|Thread
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|running
operator|=
literal|true
expr_stmt|;
name|haltRequested
operator|=
literal|false
expr_stmt|;
name|beforeSuite
argument_list|()
expr_stmt|;
if|if
condition|(
name|PROFILE
condition|)
block|{
name|profiler
operator|=
operator|new
name|Profiler
argument_list|()
operator|.
name|startCollecting
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|Iterable
argument_list|<
name|RepositoryFixture
argument_list|>
name|fixtures
parameter_list|)
block|{
name|run
argument_list|(
name|fixtures
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|Iterable
argument_list|<
name|RepositoryFixture
argument_list|>
name|fixtures
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|concurrencyLevels
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"# %-26.26s       C     min     10%%     50%%     90%%     max       N%n"
argument_list|,
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|format
argument_list|(
literal|"# %-26.26s,      C,    min,    10%%,    50%%,    90%%,    max,      N%n"
argument_list|,
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RepositoryFixture
name|fixture
range|:
name|fixtures
control|)
block|{
try|try
block|{
name|Repository
index|[]
name|cluster
init|=
name|createRepository
argument_list|(
name|fixture
argument_list|)
decl_stmt|;
try|try
block|{
name|runTest
argument_list|(
name|fixture
argument_list|,
name|cluster
index|[
literal|0
index|]
argument_list|,
name|concurrencyLevels
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fixture
operator|.
name|tearDownCluster
argument_list|()
expr_stmt|;
block|}
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
block|}
specifier|private
name|void
name|runTest
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|,
name|Repository
name|repository
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|concurrencyLevels
parameter_list|)
throws|throws
name|Exception
block|{
name|setUp
argument_list|(
name|repository
argument_list|,
name|CREDENTIALS
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Run a few iterations to warm up the system
name|long
name|warmupEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|WARMUP
decl_stmt|;
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|warmupEnd
operator|&&
operator|!
name|stop
condition|)
block|{
if|if
condition|(
operator|!
name|stop
condition|)
block|{
comment|// we want to execute this at lease once. after that we consider the
comment|// `haltRequested` flag.
name|stop
operator|=
name|haltRequested
expr_stmt|;
block|}
name|execute
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|concurrencyLevels
operator|==
literal|null
operator|||
name|concurrencyLevels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|concurrencyLevels
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Integer
name|concurrency
range|:
name|concurrencyLevels
control|)
block|{
comment|// Run the test
name|DescriptiveStatistics
name|statistics
init|=
name|runTest
argument_list|(
name|concurrency
argument_list|)
decl_stmt|;
if|if
condition|(
name|statistics
operator|.
name|getN
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%-28.28s  %6d  %6.0f  %6.0f  %6.0f  %6.0f  %6.0f  %6d%n"
argument_list|,
name|fixture
operator|.
name|toString
argument_list|()
argument_list|,
name|concurrency
argument_list|,
name|statistics
operator|.
name|getMin
argument_list|()
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|10.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|50.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|90.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getMax
argument_list|()
argument_list|,
name|statistics
operator|.
name|getN
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|format
argument_list|(
literal|"%-28.28s, %6d, %6.0f, %6.0f, %6.0f, %6.0f, %6.0f, %6d%n"
argument_list|,
name|fixture
operator|.
name|toString
argument_list|()
argument_list|,
name|concurrency
argument_list|,
name|statistics
operator|.
name|getMin
argument_list|()
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|10.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|50.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|90.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getMax
argument_list|()
argument_list|,
name|statistics
operator|.
name|getN
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|Executor
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|SynchronizedDescriptiveStatistics
name|statistics
decl_stmt|;
specifier|private
name|boolean
name|running
init|=
literal|true
decl_stmt|;
specifier|private
name|Executor
parameter_list|(
name|String
name|name
parameter_list|,
name|SynchronizedDescriptiveStatistics
name|statistics
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|T
name|context
init|=
literal|null
decl_stmt|;
try|try
block|{
name|context
operator|=
name|prepareThreadExecutionContext
argument_list|()
expr_stmt|;
while|while
condition|(
name|running
condition|)
block|{
name|statistics
operator|.
name|addValue
argument_list|(
name|execute
argument_list|(
name|context
argument_list|)
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|disposeThreadExecutionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|DescriptiveStatistics
name|runTest
parameter_list|(
name|int
name|concurrencyLevel
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SynchronizedDescriptiveStatistics
name|statistics
init|=
operator|new
name|SynchronizedDescriptiveStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|concurrencyLevel
operator|==
literal|1
condition|)
block|{
comment|// Run test iterations, and capture the execution times
name|long
name|runtimeEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|RUNTIME
decl_stmt|;
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|runtimeEnd
operator|&&
operator|!
name|stop
condition|)
block|{
if|if
condition|(
operator|!
name|stop
condition|)
block|{
comment|// we want to execute this at lease once. after that we consider the
comment|// `haltRequested` flag.
name|stop
operator|=
name|haltRequested
expr_stmt|;
block|}
name|statistics
operator|.
name|addValue
argument_list|(
name|execute
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|List
argument_list|<
name|Executor
argument_list|>
name|threads
init|=
operator|new
name|LinkedList
argument_list|<
name|Executor
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|concurrencyLevel
condition|;
name|n
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Executor
argument_list|(
literal|"Background job "
operator|+
name|n
argument_list|,
name|statistics
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// start threads
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|//System.out.printf("Started %d threads%n", threads.size());
comment|// Run test iterations, and capture the execution times
name|long
name|runtimeEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|RUNTIME
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|runtimeEnd
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|runtimeEnd
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// stop threads
for|for
control|(
name|Executor
name|e
range|:
name|threads
control|)
block|{
name|e
operator|.
name|running
operator|=
literal|false
expr_stmt|;
block|}
comment|// wait for threads
for|for
control|(
name|Executor
name|e
range|:
name|threads
control|)
block|{
name|e
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|statistics
return|;
block|}
comment|/**      * Executes a single iteration of this test.      *      * @return number of milliseconds spent in this iteration      * @throws Exception if an error occurs      */
specifier|public
name|long
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|beforeTest
argument_list|()
expr_stmt|;
try|try
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// System.out.println("execute " + this);
name|runTest
argument_list|()
expr_stmt|;
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
return|;
block|}
finally|finally
block|{
name|afterTest
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|execute
parameter_list|(
name|T
name|executionContext
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|executionContext
operator|==
literal|null
condition|)
block|{
return|return
name|execute
argument_list|()
return|;
block|}
name|beforeTest
argument_list|(
name|executionContext
argument_list|)
expr_stmt|;
try|try
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// System.out.println("execute " + this);
name|runTest
argument_list|(
name|executionContext
argument_list|)
expr_stmt|;
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
return|;
block|}
finally|finally
block|{
name|afterTest
argument_list|(
name|executionContext
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Cleans up after this performance benchmark.      *      * @throws Exception if the benchmark can not be cleaned up      */
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|issueHaltChildThreads
argument_list|()
expr_stmt|;
name|this
operator|.
name|running
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|profiler
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|profiler
operator|.
name|stopCollecting
argument_list|()
operator|.
name|getTop
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|profiler
operator|=
literal|null
expr_stmt|;
block|}
name|afterSuite
argument_list|()
expr_stmt|;
for|for
control|(
name|Session
name|session
range|:
name|sessions
control|)
block|{
if|if
condition|(
name|session
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|threads
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|sessions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|credentials
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|repository
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Run before any iterations of this test get executed. Subclasses can      * override this method to set up static test content.      *      * @throws Exception if an error occurs      */
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
specifier|abstract
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Run after all iterations of this test have been executed. Subclasses can      * override this method to clean up static test content.      *      * @throws Exception if an error occurs      */
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Invoked before the thread starts. If the test later requires      * some thread local context e.g. JCR session per thread then sub      * classes can return a context instance. That instance would be      * passed as part of runTest call      *      * @return context instance to be used for runTest call for the      * current thread      */
specifier|protected
name|T
name|prepareThreadExecutionContext
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|disposeThreadExecutionContext
parameter_list|(
name|T
name|context
parameter_list|)
block|{      }
specifier|protected
name|void
name|afterTest
parameter_list|(
name|T
name|executionContext
parameter_list|)
block|{      }
specifier|protected
name|void
name|runTest
parameter_list|(
name|T
name|executionContext
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"If thread execution context is used then subclass must "
operator|+
literal|"override this method"
argument_list|)
throw|;
block|}
specifier|protected
name|void
name|beforeTest
parameter_list|(
name|T
name|executionContext
parameter_list|)
block|{      }
specifier|protected
name|void
name|failOnRepositoryVersions
parameter_list|(
name|String
modifier|...
name|versions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|repositoryVersion
init|=
name|repository
operator|.
name|getDescriptor
argument_list|(
name|Repository
operator|.
name|REP_VERSION_DESC
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|version
range|:
name|versions
control|)
block|{
if|if
condition|(
name|repositoryVersion
operator|.
name|startsWith
argument_list|(
name|version
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unable to run "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" on repository version "
operator|+
name|version
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
specifier|protected
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
name|credentials
return|;
block|}
comment|/**      * Returns a new reader session that will be automatically closed once      * all the iterations of this test have been executed.      *      * @return reader session      */
specifier|protected
name|Session
name|loginAnonymous
parameter_list|()
block|{
return|return
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns a new admin session that will be automatically closed once      * all the iterations of this test have been executed.      *      * @return admin session      */
specifier|protected
name|Session
name|loginAdministrative
parameter_list|()
block|{
return|return
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
return|;
block|}
comment|/**     * Returns a new session for the given user     * that will be automatically closed once     * all the iterations of this test have been executed.     *      * @param credentials the user credentials     * @return user session     */
specifier|protected
name|Session
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
try|try
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Logs out and removes the session from the internal pool.      * @param session the session to logout      */
specifier|protected
name|void
name|logout
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|.
name|remove
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns a new writer session that will be automatically closed once      * all the iterations of this test have been executed.      *      * @return writer session      */
specifier|protected
name|Session
name|loginWriter
parameter_list|()
block|{
try|try
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|sessions
init|)
block|{
name|sessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds a background thread that repeatedly executes the given job      * until all the iterations of this test have been executed.      *      * @param job background job      */
specifier|protected
name|void
name|addBackgroundJob
parameter_list|(
specifier|final
name|Runnable
name|job
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"Background job "
operator|+
name|job
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
name|job
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
comment|/**      * Customize the repository creation process by custom fixture handling      */
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|fixture
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

