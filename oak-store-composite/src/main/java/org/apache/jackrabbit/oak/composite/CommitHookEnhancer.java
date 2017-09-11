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
name|composite
package|;
end_package

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
name|CommitHook
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
name|ApplyDiff
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

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
name|Optional
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_class
class|class
name|CommitHookEnhancer
implements|implements
name|CommitHook
block|{
specifier|private
specifier|final
name|CompositionContext
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|CompositeNodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|CommitHook
name|hook
decl_stmt|;
specifier|private
name|Optional
argument_list|<
name|CompositeNodeBuilder
argument_list|>
name|updatedBuilder
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
name|CommitHookEnhancer
parameter_list|(
name|CommitHook
name|hook
parameter_list|,
name|CompositionContext
name|ctx
parameter_list|,
name|CompositeNodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|hook
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|beforeStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|MountedNodeStore
argument_list|,
name|NodeState
argument_list|>
name|afterStates
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|MountedNodeStore
name|mns
range|:
name|ctx
operator|.
name|getNonDefaultStores
argument_list|()
control|)
block|{
if|if
condition|(
name|mns
operator|.
name|getMount
argument_list|()
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|NodeState
name|root
init|=
name|mns
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|afterStates
operator|.
name|put
argument_list|(
name|mns
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|beforeStates
operator|.
name|put
argument_list|(
name|mns
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|afterStates
operator|.
name|put
argument_list|(
name|mns
argument_list|,
name|mns
operator|.
name|getNodeStore
argument_list|()
operator|.
name|rebase
argument_list|(
name|builder
operator|.
name|getNodeBuilder
argument_list|(
name|mns
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|beforeStates
operator|.
name|put
argument_list|(
name|mns
argument_list|,
name|builder
operator|.
name|getNodeBuilder
argument_list|(
name|mns
argument_list|)
operator|.
name|getBaseState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|afterStates
operator|.
name|put
argument_list|(
name|ctx
operator|.
name|getGlobalStore
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|beforeStates
operator|.
name|put
argument_list|(
name|ctx
operator|.
name|getGlobalStore
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|CompositeNodeState
name|compositeBefore
init|=
name|ctx
operator|.
name|createRootNodeState
argument_list|(
name|beforeStates
argument_list|)
decl_stmt|;
name|CompositeNodeState
name|compositeAfter
init|=
name|ctx
operator|.
name|createRootNodeState
argument_list|(
name|afterStates
argument_list|)
decl_stmt|;
name|NodeState
name|result
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|compositeBefore
argument_list|,
name|compositeAfter
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|updatedBuilder
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|toComposite
argument_list|(
name|result
argument_list|,
name|compositeBefore
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|instanceof
name|CompositeNodeState
condition|)
block|{
return|return
operator|(
operator|(
name|CompositeNodeState
operator|)
name|result
operator|)
operator|.
name|getNodeState
argument_list|(
name|ctx
operator|.
name|getGlobalStore
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The commit hook result should be a composite node state"
argument_list|)
throw|;
block|}
block|}
name|Optional
argument_list|<
name|CompositeNodeBuilder
argument_list|>
name|getUpdatedBuilder
parameter_list|()
block|{
return|return
name|updatedBuilder
return|;
block|}
specifier|private
name|CompositeNodeBuilder
name|toComposite
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|CompositeNodeState
name|compositeRoot
parameter_list|)
block|{
name|CompositeNodeBuilder
name|builder
init|=
name|compositeRoot
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nodeState
operator|.
name|compareAgainstBaseState
argument_list|(
name|compositeRoot
argument_list|,
operator|new
name|ApplyDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

