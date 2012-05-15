begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|concurrent
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Test concurrent access to nodes, the journal, and revision.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentTest
block|{
specifier|final
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Concurrent
operator|.
name|run
argument_list|(
literal|"MicroKernel"
argument_list|,
operator|new
name|Concurrent
operator|.
name|Task
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|id
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|String
name|newRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\""
operator|+
name|i
operator|+
literal|"\":{\"x\": "
operator|+
name|i
operator|+
literal|"}"
argument_list|,
name|rev
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|newRev
operator|.
name|equals
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getJournal
argument_list|(
name|rev
argument_list|,
name|newRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|start
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|newRev
argument_list|)
expr_stmt|;
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|newRev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|newRev
argument_list|)
argument_list|)
expr_stmt|;
name|rev
operator|=
name|newRev
expr_stmt|;
name|newRev
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\""
operator|+
name|i
operator|+
literal|"\""
argument_list|,
name|rev
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/"
operator|+
name|i
argument_list|,
name|newRev
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

