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
name|Map
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
name|plugins
operator|.
name|document
operator|.
name|UpdateOp
operator|.
name|Key
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
name|UpdateOp
operator|.
name|Operation
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

begin_comment
comment|/**  * Test for OAK-3903  */
end_comment

begin_class
specifier|public
class|class
name|CommitRootUpdateTest
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
annotation|@
name|Test
specifier|public
name|void
name|exceptionOnUpdate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|throwAfterUpdate
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|MemoryDocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
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
name|T
name|doc
init|=
name|super
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|isFinalCommitRootUpdate
argument_list|(
name|update
argument_list|)
operator|&&
name|throwAfterUpdate
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
name|RuntimeException
argument_list|(
literal|"communication failure"
argument_list|)
throw|;
block|}
return|return
name|doc
return|;
block|}
specifier|private
name|boolean
name|isFinalCommitRootUpdate
parameter_list|(
name|UpdateOp
name|update
parameter_list|)
block|{
name|boolean
name|finalUpdate
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|op
range|:
name|update
operator|.
name|getChanges
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|op
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|NodeDocument
operator|.
name|isRevisionsEntry
argument_list|(
name|name
argument_list|)
operator|||
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|finalUpdate
operator|=
literal|false
expr_stmt|;
break|break;
block|}
return|return
name|finalUpdate
return|;
block|}
block|}
decl_stmt|;
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
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|throwAfterUpdate
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|Commit
name|c
init|=
name|ns
operator|.
name|newCommit
argument_list|(
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|addNode
argument_list|(
operator|new
name|DocumentNodeState
argument_list|(
name|ns
argument_list|,
literal|"/foo/node"
argument_list|,
name|c
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|addNode
argument_list|(
operator|new
name|DocumentNodeState
argument_list|(
name|ns
argument_list|,
literal|"/bar/node"
argument_list|,
name|c
operator|.
name|getBaseRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|apply
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|ns
operator|.
name|done
argument_list|(
name|c
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ns
operator|.
name|canceled
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeState
name|root
init|=
name|ns
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"node"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"node"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|throwAfterUpdate
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
block|{
return|return
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
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

