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

begin_class
specifier|public
class|class
name|AscendingClockTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testMillis
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|start
decl_stmt|,
name|last
decl_stmt|;
name|last
operator|=
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
expr_stmt|;
name|AscendingClock
name|c
init|=
operator|new
name|AscendingClock
argument_list|(
name|start
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|long
name|t
init|=
name|c
operator|.
name|time
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|t
operator|>
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|t
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testNanos
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|start
decl_stmt|,
name|last
decl_stmt|;
name|last
operator|=
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
expr_stmt|;
name|AscendingClock
name|c
init|=
operator|new
name|AscendingClock
argument_list|(
name|start
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|nanoTime
argument_list|()
operator|>
name|last
operator|*
literal|1000000
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|long
name|t
init|=
name|c
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|t
operator|>
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

