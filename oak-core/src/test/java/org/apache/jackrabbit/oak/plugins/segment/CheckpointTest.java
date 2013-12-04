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
name|segment
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|plugins
operator|.
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|EmptyHook
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
name|NodeBuilder
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
name|NodeStore
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
name|CheckpointTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testCheckpoint
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|SegmentNodeStore
name|store
init|=
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
decl_stmt|;
name|addTestNode
argument_list|(
name|store
argument_list|,
literal|"test-checkpoint"
argument_list|)
expr_stmt|;
name|verifyNS
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmTestNode
argument_list|(
name|store
argument_list|,
literal|"test-checkpoint"
argument_list|)
expr_stmt|;
name|verifyNS
argument_list|(
name|store
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// gc?
name|store
operator|.
name|retrieve
argument_list|(
name|SegmentIdFactory
operator|.
name|newDataSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|verifyNS
parameter_list|(
name|SegmentNodeStore
name|store
parameter_list|,
name|boolean
name|exists
parameter_list|)
block|{
name|String
name|cp
init|=
name|store
operator|.
name|checkpoint
argument_list|(
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Checkpoint must not be null"
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|NodeState
name|cpns
init|=
name|store
operator|.
name|retrieve
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cpns
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Node doesn't exist in checkpoint"
argument_list|,
name|cpns
operator|.
name|getChildNode
argument_list|(
literal|"test-checkpoint"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Node shouldn't exist in checkpoint"
argument_list|,
name|cpns
operator|.
name|getChildNode
argument_list|(
literal|"test-checkpoint"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addTestNode
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|rmTestNode
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

