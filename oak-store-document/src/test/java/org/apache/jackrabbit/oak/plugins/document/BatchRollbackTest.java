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
name|AtomicBoolean
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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|Rule
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
name|document
operator|.
name|Collection
operator|.
name|NODES
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
name|document
operator|.
name|TestUtils
operator|.
name|isFinalCommitRootUpdate
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
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|lessThan
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|BatchRollbackTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|TestStore
name|store
init|=
operator|new
name|TestStore
argument_list|()
decl_stmt|;
annotation|@
name|Ignore
argument_list|(
literal|"OAK-7984"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|batchRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ns
operator|.
name|setMaxBackOffMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// prepare some test nodes
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
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
name|builder
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// perform an merge that fails
name|builder
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|resetCounters
argument_list|()
expr_stmt|;
name|store
operator|.
name|failCommitOnce
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail with CommitFailedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertThat
argument_list|(
name|store
operator|.
name|getNumCreateOrUpdateCalls
argument_list|(
name|NODES
argument_list|)
argument_list|,
name|lessThan
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestStore
extends|extends
name|CountingDocumentStore
block|{
specifier|final
name|AtomicBoolean
name|failCommitOnce
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|TestStore
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
operator|&&
name|isFinalCommitRootUpdate
argument_list|(
name|update
argument_list|)
operator|&&
name|failCommitOnce
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"commit failed"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

