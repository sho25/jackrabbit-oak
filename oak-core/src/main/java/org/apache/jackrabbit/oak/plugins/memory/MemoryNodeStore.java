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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|state
operator|.
name|AbstractNodeStore
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
name|AbstractNodeStoreBranch
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
name|NodeStoreBranch
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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

begin_comment
comment|/**  * Basic in-memory node store implementation. Useful as a base class for  * more complex functionality.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeStore
extends|extends
name|AbstractNodeStore
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
name|EMPTY_NODE
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|checkpoints
init|=
name|newHashMap
argument_list|()
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
name|NodeStoreBranch
name|branch
parameter_list|()
block|{
return|return
operator|new
name|MemoryNodeStoreBranch
argument_list|(
name|this
argument_list|,
name|root
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return An instance of {@link ArrayBasedBlob}.      */
annotation|@
name|Override
specifier|public
name|ArrayBasedBlob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|inputStream
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
specifier|synchronized
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|lifetime
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|checkpoint
init|=
literal|"checkpoint"
operator|+
name|checkpoints
operator|.
name|size
argument_list|()
decl_stmt|;
name|checkpoints
operator|.
name|put
argument_list|(
name|checkpoint
argument_list|,
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|checkpoint
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
block|{
return|return
name|checkpoints
operator|.
name|get
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|MemoryNodeStoreBranch
extends|extends
name|AbstractNodeStoreBranch
block|{
comment|/** The underlying store to which this branch belongs */
specifier|private
specifier|final
name|MemoryNodeStore
name|store
decl_stmt|;
comment|/** Root state of the base revision of this branch */
specifier|private
specifier|final
name|NodeState
name|base
decl_stmt|;
comment|/** Root state of the head revision of this branch*/
specifier|private
specifier|volatile
name|NodeState
name|root
decl_stmt|;
specifier|public
name|MemoryNodeStoreBranch
parameter_list|(
name|MemoryNodeStore
name|store
parameter_list|,
name|NodeState
name|base
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
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
name|getHead
parameter_list|()
block|{
name|checkNotMerged
argument_list|()
expr_stmt|;
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
name|checkNotMerged
argument_list|()
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|newRoot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|merge
parameter_list|(
name|CommitHook
name|hook
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkNotMerged
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|store
operator|.
name|root
operator|.
name|compareAndSet
argument_list|(
name|base
argument_list|,
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|checkNotNull
argument_list|(
name|hook
argument_list|)
operator|.
name|processCommit
argument_list|(
name|base
argument_list|,
name|root
argument_list|)
argument_list|)
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
name|root
operator|=
literal|null
expr_stmt|;
comment|// Mark as merged
return|return
name|store
operator|.
name|getRoot
argument_list|()
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
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// ----------------------------------------------------< private>---
specifier|private
name|void
name|checkNotMerged
parameter_list|()
block|{
name|checkState
argument_list|(
name|root
operator|!=
literal|null
argument_list|,
literal|"Branch has already been merged"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

