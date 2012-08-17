begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|memory
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
name|AtomicReference
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
name|api
operator|.
name|CoreValueFactory
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
name|CommitEditor
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
name|EmptyEditor
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
name|NodeStore
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
name|NodeStoreBranch
import|;
end_import

begin_comment
comment|/**  * Basic in-memory node store implementation. Useful as a base class for  * more complex functionality.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeStore
implements|implements
name|NodeStore
block|{
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
name|root
init|=
operator|new
name|AtomicReference
argument_list|<
name|NodeState
argument_list|>
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|root
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|getBuilder
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|base
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStoreBranch
name|branch
parameter_list|(
name|CommitEditor
name|editor
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeStoreBranch
argument_list|(
name|root
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|MemoryValueFactory
operator|.
name|INSTANCE
return|;
block|}
specifier|private
class|class
name|MemoryNodeStoreBranch
implements|implements
name|NodeStoreBranch
block|{
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
specifier|private
specifier|volatile
name|NodeState
name|root
decl_stmt|;
specifier|public
name|MemoryNodeStoreBranch
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRoot
parameter_list|(
name|NodeState
name|newRoot
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|newRoot
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|()
throws|throws
name|CommitFailedException
block|{
while|while
condition|(
operator|!
name|MemoryNodeStore
operator|.
name|this
operator|.
name|root
operator|.
name|compareAndSet
argument_list|(
name|base
argument_list|,
name|root
argument_list|)
condition|)
block|{
comment|// TODO: rebase();
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copy
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

