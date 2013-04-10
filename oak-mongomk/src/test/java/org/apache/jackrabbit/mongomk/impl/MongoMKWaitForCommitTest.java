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
name|mongomk
operator|.
name|impl
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
name|ScheduledFuture
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
name|api
operator|.
name|MicroKernel
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
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
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
name|mongomk
operator|.
name|prototype
operator|.
name|MongoMK
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
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * Tests for {@code MongoMicroKernel#waitForCommit(String, long)}  */
end_comment

begin_class
annotation|@
name|Ignore
specifier|public
class|class
name|MongoMKWaitForCommitTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
specifier|private
name|MicroKernel
name|mk2
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|DB
name|db
init|=
name|mongoConnection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|mk2
operator|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timeoutNonPositiveNoCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|headRev
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|<
literal|100
argument_list|)
expr_stmt|;
comment|// Basically no wait.
block|}
annotation|@
name|Test
specifier|public
name|void
name|timeoutNoCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|timeout
init|=
literal|500
decl_stmt|;
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
name|headRev
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|headRev
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|>=
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timeoutWithCommit1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|String
argument_list|>
name|future
init|=
name|scheduleCommit
argument_list|(
literal|1000
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|timeout
init|=
literal|500
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
name|headRev
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|headRev
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|headRev
operator|.
name|equals
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|>=
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|timeoutWithCommit2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|String
argument_list|>
name|future
init|=
name|scheduleCommit
argument_list|(
literal|500
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|timeout
init|=
literal|2000
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
name|headRev
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|headRev
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|headRev
operator|.
name|equals
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|<
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|branchIgnored
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|branchRev
init|=
name|mk
operator|.
name|branch
argument_list|(
name|headRev
argument_list|)
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|String
argument_list|>
name|future
init|=
name|scheduleCommit
argument_list|(
literal|500
argument_list|,
name|branchRev
argument_list|)
decl_stmt|;
name|int
name|timeout
init|=
literal|2000
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
name|headRev
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|headRev
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|headRev
operator|.
name|equals
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|>=
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nullOldHeadRevisionId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|headRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk2
operator|.
name|waitForCommit
argument_list|(
literal|null
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|long
name|after
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|headRev
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|headRev
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|<
literal|10
argument_list|)
expr_stmt|;
comment|// Basically no wait.
block|}
specifier|private
name|ScheduledFuture
argument_list|<
name|String
argument_list|>
name|scheduleCommit
parameter_list|(
name|long
name|delay
parameter_list|,
specifier|final
name|String
name|revisionId
parameter_list|)
block|{
name|ScheduledExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
name|ScheduledFuture
argument_list|<
name|String
argument_list|>
name|future
init|=
name|executorService
operator|.
name|schedule
argument_list|(
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}"
argument_list|,
name|revisionId
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|,
name|delay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
return|return
name|future
return|;
block|}
block|}
end_class

end_unit

