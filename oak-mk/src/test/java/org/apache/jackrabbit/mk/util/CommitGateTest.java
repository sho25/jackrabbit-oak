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
name|mk
operator|.
name|util
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  * Test the commit gate.  */
end_comment

begin_class
specifier|public
class|class
name|CommitGateTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|CommitGate
name|gate
init|=
operator|new
name|CommitGate
argument_list|()
decl_stmt|;
name|gate
operator|.
name|commit
argument_list|(
literal|"start"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|tick
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|spurious
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|waitMillis
init|=
literal|10
decl_stmt|;
name|int
name|threadCount
init|=
literal|10
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadCount
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|head
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|nh
decl_stmt|;
try|try
block|{
name|nh
operator|=
name|gate
operator|.
name|waitForCommit
argument_list|(
name|head
argument_list|,
name|waitMillis
argument_list|)
expr_stmt|;
if|if
condition|(
name|nh
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
name|spurious
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tick
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|head
operator|=
name|nh
expr_stmt|;
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"end"
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|waitMillis
operator|*
literal|10
argument_list|)
expr_stmt|;
comment|// assertTrue(threadCount< spurious.get());<- depends on timing
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tick
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|tick
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|spurious
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|commitCount
init|=
literal|100
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
name|commitCount
condition|;
name|i
operator|++
control|)
block|{
name|gate
operator|.
name|commit
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|gate
operator|.
name|commit
argument_list|(
literal|"end"
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|j
range|:
name|threads
control|)
block|{
name|j
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"ticks: "
operator|+
name|tick
operator|.
name|get
argument_list|()
operator|+
literal|" min: "
operator|+
name|threadCount
operator|*
name|commitCount
operator|+
literal|" spurious: "
operator|+
name|spurious
operator|.
name|get
argument_list|()
argument_list|,
name|tick
operator|.
name|get
argument_list|()
operator|>=
name|threadCount
operator|*
name|commitCount
operator|*
literal|0.2
operator|&&
name|tick
operator|.
name|get
argument_list|()
operator|<=
name|threadCount
operator|*
name|commitCount
operator|*
literal|1.2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

