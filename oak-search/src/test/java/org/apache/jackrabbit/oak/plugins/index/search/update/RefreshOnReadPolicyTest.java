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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|update
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|RefreshOnReadPolicyTest
block|{
specifier|private
specifier|final
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RecordingRunnable
name|refreshCallback
init|=
operator|new
name|RecordingRunnable
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RefreshOnReadPolicy
name|policy
init|=
operator|new
name|RefreshOnReadPolicy
argument_list|(
name|clock
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|refreshDelta
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|noRefreshOnReadIfNotUpdated
parameter_list|()
throws|throws
name|Exception
block|{
name|policy
operator|.
name|refreshOnReadIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|refreshCallback
operator|.
name|assertNotInvokedAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|refreshOnFirstWrite
parameter_list|()
throws|throws
name|Exception
block|{
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
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|refreshCallback
operator|.
name|assertInvokedAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|refreshOnReadAfterWrite
parameter_list|()
throws|throws
name|Exception
block|{
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
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|refreshCallback
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//Call again without change in time
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
comment|//This time callback should not be invoked
name|refreshCallback
operator|.
name|assertNotInvokedAndReset
argument_list|()
expr_stmt|;
name|policy
operator|.
name|refreshOnReadIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
comment|//On read the callback should be invoked
name|refreshCallback
operator|.
name|assertInvokedAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|refreshOnWriteWithTimeElapsed
parameter_list|()
throws|throws
name|Exception
block|{
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
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|refreshCallback
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//Call again without change in time
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
comment|//This time callback should not be invoked
name|refreshCallback
operator|.
name|assertNotInvokedAndReset
argument_list|()
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|refreshDelta
argument_list|)
expr_stmt|;
name|policy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|refreshCallback
operator|.
name|assertInvokedAndReset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

