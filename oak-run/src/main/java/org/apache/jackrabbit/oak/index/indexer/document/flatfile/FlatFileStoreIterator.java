begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
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
name|collect
operator|.
name|AbstractIterator
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|NodeStateEntry
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Iterators
operator|.
name|concat
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
name|Iterators
operator|.
name|singletonIterator
import|;
end_import

begin_class
class|class
name|FlatFileStoreIterator
extends|extends
name|AbstractIterator
argument_list|<
name|NodeStateEntry
argument_list|>
implements|implements
name|Iterator
argument_list|<
name|NodeStateEntry
argument_list|>
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|NodeStateEntry
argument_list|>
name|baseItr
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|NodeStateEntry
argument_list|>
name|buffer
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|NodeStateEntry
name|current
decl_stmt|;
specifier|private
specifier|final
name|int
name|checkChildLimit
decl_stmt|;
specifier|private
name|int
name|maxBufferSize
decl_stmt|;
specifier|public
name|FlatFileStoreIterator
parameter_list|(
name|Iterator
argument_list|<
name|NodeStateEntry
argument_list|>
name|baseItr
parameter_list|,
name|int
name|checkChildLimit
parameter_list|)
block|{
name|this
operator|.
name|baseItr
operator|=
name|baseItr
expr_stmt|;
name|this
operator|.
name|checkChildLimit
operator|=
name|checkChildLimit
expr_stmt|;
block|}
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStateEntry
name|computeNext
parameter_list|()
block|{
comment|//TODO Add some checks on expected ordering
name|current
operator|=
name|computeNextEntry
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Max buffer size in complete traversal is [{}]"
argument_list|,
name|maxBufferSize
argument_list|)
expr_stmt|;
return|return
name|endOfData
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|current
return|;
block|}
block|}
specifier|private
name|NodeStateEntry
name|computeNextEntry
parameter_list|()
block|{
name|maxBufferSize
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxBufferSize
argument_list|,
name|buffer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|buffer
operator|.
name|remove
argument_list|()
return|;
block|}
if|if
condition|(
name|baseItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|wrap
argument_list|(
name|baseItr
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|NodeStateEntry
name|wrap
parameter_list|(
name|NodeStateEntry
name|baseEntry
parameter_list|)
block|{
name|NodeState
name|state
init|=
operator|new
name|LazyChildrenNodeState
argument_list|(
name|baseEntry
operator|.
name|getNodeState
argument_list|()
argument_list|,
operator|new
name|ChildNodeStateProvider
argument_list|(
name|getEntries
argument_list|()
argument_list|,
name|baseEntry
operator|.
name|getPath
argument_list|()
argument_list|,
name|checkChildLimit
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeStateEntry
argument_list|(
name|state
argument_list|,
name|baseEntry
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Iterable
argument_list|<
name|NodeStateEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
parameter_list|()
lambda|->
name|concat
argument_list|(
name|singletonIterator
argument_list|(
name|current
argument_list|)
argument_list|,
name|queueIterator
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|NodeStateEntry
argument_list|>
name|queueIterator
parameter_list|()
block|{
name|ListIterator
argument_list|<
name|NodeStateEntry
argument_list|>
name|qitr
init|=
name|buffer
operator|.
name|listIterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractIterator
argument_list|<
name|NodeStateEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NodeStateEntry
name|computeNext
parameter_list|()
block|{
comment|//If queue is empty try to append by getting entry from base
if|if
condition|(
operator|!
name|qitr
operator|.
name|hasNext
argument_list|()
operator|&&
name|baseItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|qitr
operator|.
name|add
argument_list|(
name|wrap
argument_list|(
name|baseItr
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|qitr
operator|.
name|previous
argument_list|()
expr_stmt|;
comment|//Move back the itr
block|}
if|if
condition|(
name|qitr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|qitr
operator|.
name|next
argument_list|()
return|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit
