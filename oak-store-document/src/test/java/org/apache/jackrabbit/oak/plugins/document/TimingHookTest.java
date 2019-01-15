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
name|AtomicLong
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
name|api
operator|.
name|CommitFailedException
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
name|commit
operator|.
name|CommitInfo
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
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
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|TimingHookTest
block|{
specifier|private
specifier|static
name|long
name|DELAY_MS
init|=
literal|10
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|commitTime
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|AtomicLong
name|processingTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|TimingHook
operator|.
name|wrap
argument_list|(
parameter_list|(
name|before
parameter_list|,
name|after
parameter_list|,
name|info
parameter_list|)
lambda|->
name|sleep
argument_list|()
argument_list|,
parameter_list|(
name|time
parameter_list|,
name|unit
parameter_list|)
lambda|->
name|processingTime
operator|.
name|set
argument_list|(
name|unit
operator|.
name|toMillis
argument_list|(
name|time
argument_list|)
argument_list|)
argument_list|)
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|EMPTY_NODE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processingTime
operator|.
name|get
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
name|DELAY_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|NodeState
name|sleep
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|DELAY_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
name|EMPTY_NODE
return|;
block|}
block|}
end_class

end_unit

