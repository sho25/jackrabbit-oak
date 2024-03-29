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
name|cow
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
name|Blob
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
name|memory
operator|.
name|MemoryNodeStore
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
name|commit
operator|.
name|Observable
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
name|Observer
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|UUID
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|Iterables
operator|.
name|addAll
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
name|Lists
operator|.
name|newArrayList
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
name|Lists
operator|.
name|newCopyOnWriteArrayList
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
name|newConcurrentMap
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
operator|.
name|stream
import|;
end_import

begin_class
specifier|public
class|class
name|BranchNodeStore
implements|implements
name|NodeStore
implements|,
name|Observable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|CHECKPOINT_LIFETIME
init|=
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toMillis
argument_list|(
literal|24
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|MemoryNodeStore
name|memoryNodeStore
decl_stmt|;
specifier|private
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|inheritedCheckpoints
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checkpointMapping
decl_stmt|;
specifier|public
name|BranchNodeStore
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|inheritedCheckpoints
operator|=
name|newArrayList
argument_list|(
name|nodeStore
operator|.
name|checkpoints
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|checkpointMapping
operator|=
name|newConcurrentMap
argument_list|()
expr_stmt|;
name|String
name|cp
init|=
name|nodeStore
operator|.
name|checkpoint
argument_list|(
name|CHECKPOINT_LIFETIME
argument_list|,
name|singletonMap
argument_list|(
literal|"type"
argument_list|,
literal|"copy-on-write"
argument_list|)
argument_list|)
decl_stmt|;
name|memoryNodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|(
name|nodeStore
operator|.
name|retrieve
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
for|for
control|(
name|String
name|cp
range|:
name|nodeStore
operator|.
name|checkpoints
argument_list|()
control|)
block|{
if|if
condition|(
literal|"copy-on-write"
operator|.
name|equals
argument_list|(
name|nodeStore
operator|.
name|checkpointInfo
argument_list|(
name|cp
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
condition|)
block|{
name|nodeStore
operator|.
name|release
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|memoryNodeStore
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|NotNull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|memoryNodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|commitHook
argument_list|,
name|info
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|rebase
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|memoryNodeStore
operator|.
name|rebase
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|reset
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
name|memoryNodeStore
operator|.
name|reset
argument_list|(
name|builder
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|memoryNodeStore
operator|.
name|createBlob
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|getBlob
parameter_list|(
annotation|@
name|NotNull
name|String
name|reference
parameter_list|)
block|{
return|return
name|memoryNodeStore
operator|.
name|getBlob
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|String
name|checkpoint
init|=
name|memoryNodeStore
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|properties
argument_list|)
decl_stmt|;
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|checkpointMapping
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
name|uuid
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|checkpoint
argument_list|(
name|lifetime
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|checkpoints
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|newArrayList
argument_list|(
name|inheritedCheckpoints
argument_list|)
decl_stmt|;
name|result
operator|.
name|retainAll
argument_list|(
name|newArrayList
argument_list|(
name|nodeStore
operator|.
name|checkpoints
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointMapping
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|memoryNodeStore
operator|.
name|listCheckpoints
argument_list|()
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|forEach
argument_list|(
name|result
operator|::
name|add
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|checkpointInfo
parameter_list|(
annotation|@
name|NotNull
name|String
name|checkpoint
parameter_list|)
block|{
if|if
condition|(
name|inheritedCheckpoints
operator|.
name|contains
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|nodeStore
operator|.
name|checkpointInfo
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|checkpointMapping
operator|.
name|containsKey
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|memoryNodeStore
operator|.
name|checkpointInfo
argument_list|(
name|checkpointMapping
operator|.
name|get
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|emptyMap
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|retrieve
parameter_list|(
annotation|@
name|NotNull
name|String
name|checkpoint
parameter_list|)
block|{
if|if
condition|(
name|inheritedCheckpoints
operator|.
name|contains
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|nodeStore
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|checkpointMapping
operator|.
name|containsKey
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|memoryNodeStore
operator|.
name|retrieve
argument_list|(
name|checkpointMapping
operator|.
name|get
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|release
parameter_list|(
annotation|@
name|NotNull
name|String
name|checkpoint
parameter_list|)
block|{
if|if
condition|(
name|inheritedCheckpoints
operator|.
name|contains
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|nodeStore
operator|.
name|release
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|checkpointMapping
operator|.
name|containsKey
argument_list|(
name|checkpoint
argument_list|)
condition|)
block|{
return|return
name|memoryNodeStore
operator|.
name|release
argument_list|(
name|checkpointMapping
operator|.
name|remove
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Closeable
name|addObserver
parameter_list|(
name|Observer
name|observer
parameter_list|)
block|{
return|return
name|memoryNodeStore
operator|.
name|addObserver
argument_list|(
name|observer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

